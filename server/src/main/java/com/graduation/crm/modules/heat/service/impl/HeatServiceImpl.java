package com.graduation.crm.modules.heat.service.impl;

import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.heat.entity.CustomerHeatLog;
import com.graduation.crm.modules.heat.mapper.HeatMapper;
import com.graduation.crm.modules.heat.service.HeatService;
import com.graduation.crm.modules.heat.vo.HeatDetailVO;
import com.graduation.crm.modules.heat.vo.HighIntentCustomerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeatServiceImpl implements HeatService {

    private final HeatMapper heatMapper;
    private final CustomerMapper customerMapper;

    @Override
    public HeatDetailVO detail(Long customerId) {
        HeatDetailVO detail = heatMapper.selectLatestHeat(customerId);
        if (detail == null) {
            return calculate(customerId);
        }
        enrichLevelAndSuggestion(detail);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeatDetailVO calculate(Long customerId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null || Integer.valueOf(1).equals(customer.getDeleted())) {
            throw new BusinessException("客户不存在");
        }

        ScoreParts parts = calculateScoreParts(customer);
        int totalScore = parts.totalScore();
        String reason = buildReason(customer, parts);

        CustomerHeatLog log = new CustomerHeatLog();
        log.setCustomerId(customerId);
        log.setScore(totalScore);
        log.setScoreDate(LocalDate.now());
        log.setReplySpeedScore(parts.followFrequencyScore);
        log.setAskDepthScore(parts.keywordScore);
        log.setBargainScore(parts.budgetScore);
        log.setVisitScore(parts.visitScore);
        log.setSentimentScore(parts.sentimentScore);
        log.setTotalReason(reason);
        // 同一客户同一天只保留一条热度记录，重复计算时刷新当天结果。
        heatMapper.upsertHeatLog(log);

        // 热度分冗余到客户主表，方便客户列表、看板和高意向池快速排序。
        customer.setHeatScore(totalScore);
        customerMapper.updateById(customer);

        HeatDetailVO vo = new HeatDetailVO();
        vo.setCustomerId(customer.getId());
        vo.setCustomerName(customer.getCustomerName());
        vo.setScore(totalScore);
        vo.setScoreDate(LocalDate.now());
        vo.setReplySpeedScore(parts.followFrequencyScore);
        vo.setAskDepthScore(parts.keywordScore);
        vo.setBargainScore(parts.budgetScore);
        vo.setVisitScore(parts.visitScore);
        vo.setSentimentScore(parts.sentimentScore);
        vo.setReason(reason);
        enrichLevelAndSuggestion(vo);
        return vo;
    }

    @Override
    public Integer batchCalculate() {
        List<Customer> customers = customerMapper.selectList(null);
        int count = 0;
        for (Customer customer : customers) {
            if (!Integer.valueOf(1).equals(customer.getDeleted())) {
                calculate(customer.getId());
                count++;
            }
        }
        return count;
    }

    @Override
    public List<HighIntentCustomerVO> highIntent(Long advisorId, Long managerId) {
        return heatMapper.selectHighIntentCustomers(advisorId, managerId);
    }

    private ScoreParts calculateScoreParts(Customer customer) {
        Integer recentFollows = safeInt(heatMapper.countRecentFollows(customer.getId(), 7));
        Integer keywordHits = safeInt(heatMapper.countFollowKeywordHits(customer.getId()));
        Integer visitOrDeal = safeInt(heatMapper.countVisitOrDealFollows(customer.getId()));
        BigDecimal avgSentiment = heatMapper.avgSentimentScore(customer.getId());

        BigDecimal followFrequencyScore = BigDecimal.valueOf(Math.min(20, recentFollows * 5L));
        BigDecimal keywordScore = BigDecimal.valueOf(Math.min(20, keywordHits * 4L));
        BigDecimal budgetScore = calculateBudgetScore(customer);
        BigDecimal visitScore = BigDecimal.valueOf(Math.min(20, visitOrDeal * 20L));
        BigDecimal sentimentScore = avgSentiment == null ? BigDecimal.TEN : avgSentiment.multiply(BigDecimal.valueOf(0.2));

        return new ScoreParts(followFrequencyScore, keywordScore, budgetScore, visitScore, sentimentScore);
    }

    private BigDecimal calculateBudgetScore(Customer customer) {
        if (customer.getBudgetMax() == null) {
            return BigDecimal.valueOf(8);
        }
        // 预算越明确越容易进入有效转化；当前阶段没有楼盘总价匹配，先按预算完整度计分。
        if (customer.getBudgetMin() != null && customer.getBudgetMax() != null) {
            return BigDecimal.valueOf(18);
        }
        return BigDecimal.valueOf(14);
    }

    private String buildReason(Customer customer, ScoreParts parts) {
        StringBuilder reason = new StringBuilder();
        reason.append("客户当前热度为").append(parts.totalScore()).append("分。");
        if (parts.followFrequencyScore.compareTo(BigDecimal.valueOf(10)) >= 0) {
            reason.append("近期跟进较频繁，");
        }
        if (parts.keywordScore.compareTo(BigDecimal.valueOf(8)) >= 0) {
            reason.append("沟通中出现预算、看房、优惠等意向关键词，");
        }
        if (parts.visitScore.compareTo(BigDecimal.ZERO) > 0) {
            reason.append("已有到访或成交相关行为，");
        }
        if ("DEAL".equals(customer.getStatus())) {
            reason.append("客户已成交，可进入转介绍维护。");
        } else if ("LOST".equals(customer.getStatus())) {
            reason.append("客户已流失，建议降低跟进优先级。");
        } else {
            reason.append("建议继续保持节奏并推动下一步到访或方案确认。");
        }
        return reason.toString();
    }

    private void enrichLevelAndSuggestion(HeatDetailVO vo) {
        if (vo.getScore() == null) {
            vo.setScore(0);
        }
        if (vo.getScore() >= 80) {
            vo.setLevel("HIGH");
            vo.setSuggestion("建议优先跟进，尽快确认到访时间、优惠方案或经理协助。");
        } else if (vo.getScore() >= 50) {
            vo.setLevel("MEDIUM");
            vo.setSuggestion("建议持续培育，补充户型、价格和贷款信息。");
        } else {
            vo.setLevel("LOW");
            vo.setSuggestion("建议降低优先级，定期触达并观察后续反馈。");
        }
    }

    private Integer safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static class ScoreParts {
        private final BigDecimal followFrequencyScore;
        private final BigDecimal keywordScore;
        private final BigDecimal budgetScore;
        private final BigDecimal visitScore;
        private final BigDecimal sentimentScore;

        private ScoreParts(BigDecimal followFrequencyScore, BigDecimal keywordScore, BigDecimal budgetScore,
                           BigDecimal visitScore, BigDecimal sentimentScore) {
            this.followFrequencyScore = followFrequencyScore;
            this.keywordScore = keywordScore;
            this.budgetScore = budgetScore;
            this.visitScore = visitScore;
            this.sentimentScore = sentimentScore;
        }

        private int totalScore() {
            return followFrequencyScore
                    .add(keywordScore)
                    .add(budgetScore)
                    .add(visitScore)
                    .add(sentimentScore)
                    .min(BigDecimal.valueOf(100))
                    .intValue();
        }
    }
}
