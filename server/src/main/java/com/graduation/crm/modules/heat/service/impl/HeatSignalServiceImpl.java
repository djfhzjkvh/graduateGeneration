package com.graduation.crm.modules.heat.service.impl;

import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.follow.entity.FollowRecord;
import com.graduation.crm.modules.heat.entity.CustomerHeatSignal;
import com.graduation.crm.modules.heat.mapper.CustomerHeatSignalMapper;
import com.graduation.crm.modules.heat.service.HeatSignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeatSignalServiceImpl implements HeatSignalService {

    private final CustomerHeatSignalMapper customerHeatSignalMapper;

    @Override
    public void analyzeFollowSignals(Customer customer, FollowRecord record, LocalDateTime previousFollowTime) {
        if (customer == null || record == null) {
            return;
        }
        String content = firstNonBlank(record.getContent(), record.getSummary());
        saveSignal(customer.getId(), record.getId(), "REPLY_SPEED", scoreReplySpeed(previousFollowTime),
                "Follow frequency signal", content);
        saveSignal(customer.getId(), record.getId(), "ASK_DEPTH", scoreKeyword(content),
                "Follow content keyword signal", content);
        if ("VISITED".equals(record.getFollowResult()) || "DEAL".equals(record.getFollowResult())) {
            saveSignal(customer.getId(), record.getId(), "VISIT_INTENT", BigDecimal.valueOf(20),
                    "Visit or deal follow result", content);
        }
        log.debug("Heat signals analyzed, customerId={}, followId={}", customer.getId(), record.getId());
    }

    private BigDecimal scoreReplySpeed(LocalDateTime previousFollowTime) {
        if (previousFollowTime == null) {
            return BigDecimal.valueOf(6);
        }
        long hours = Math.max(1, Duration.between(previousFollowTime, LocalDateTime.now()).toHours());
        return hours <= 24 ? BigDecimal.valueOf(14) : BigDecimal.valueOf(8);
    }

    private BigDecimal scoreKeyword(String content) {
        if (content == null || content.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        String text = content.toLowerCase();
        int hits = 0;
        for (String keyword : new String[]{"budget", "price", "discount", "visit", "loan", "预算", "价格", "优惠", "看房", "贷款"}) {
            if (text.contains(keyword)) {
                hits++;
            }
        }
        return BigDecimal.valueOf(Math.min(20, hits * 4L));
    }

    private void saveSignal(Long customerId, Long followId, String signalType, BigDecimal scoreDelta,
                            String reason, String rawText) {
        CustomerHeatSignal signal = new CustomerHeatSignal();
        signal.setCustomerId(customerId);
        signal.setFollowId(followId);
        signal.setSignalType(signalType);
        signal.setScoreDelta(scoreDelta);
        signal.setReason(reason);
        signal.setRawText(rawText);
        signal.setSignalTime(LocalDateTime.now());
        signal.setCreatedAt(LocalDateTime.now());
        customerHeatSignalMapper.insert(signal);
    }

    private String firstNonBlank(String first, String second) {
        return first != null && !first.trim().isEmpty() ? first : second;
    }
}
