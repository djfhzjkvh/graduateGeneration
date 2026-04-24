package com.graduation.crm.modules.competitor.service;

import com.graduation.crm.modules.competitor.dto.CompetitorCompareDTO;
import com.graduation.crm.modules.competitor.vo.CompetitorAiResponseVO;
import com.graduation.crm.modules.competitor.vo.CompetitorCompareVO;
import com.graduation.crm.modules.competitor.vo.CustomerCompetitorFocusVO;

import java.util.List;

public interface CompetitorCompareService {

    CompetitorCompareVO compare(CompetitorCompareDTO dto);

    CompetitorAiResponseVO aiResponse(CompetitorCompareDTO dto);

    List<CustomerCompetitorFocusVO> history(Long customerId);
}

