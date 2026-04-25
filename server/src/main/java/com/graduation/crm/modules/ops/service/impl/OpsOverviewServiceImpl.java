package com.graduation.crm.modules.ops.service.impl;

import com.graduation.crm.modules.ops.service.OpsOverviewService;
import com.graduation.crm.modules.ops.vo.OpsRiskTaskGenerateVO;
import org.springframework.stereotype.Service;

@Service
public class OpsOverviewServiceImpl implements OpsOverviewService {

    @Override
    public OpsRiskTaskGenerateVO generateRiskTasks() {
        OpsRiskTaskGenerateVO vo = new OpsRiskTaskGenerateVO();
        vo.setRiskCount(0);
        vo.setCreatedTaskCount(0);
        vo.setSkippedRiskCount(0);
        return vo;
    }
}
