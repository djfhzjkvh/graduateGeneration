package com.graduation.crm.modules.prediction.service;

import com.graduation.crm.modules.prediction.dto.PredictionQueryDTO;
import com.graduation.crm.modules.prediction.vo.PredictionOverviewVO;

public interface PredictionService {
    PredictionOverviewVO overview(PredictionQueryDTO queryDTO);
}
