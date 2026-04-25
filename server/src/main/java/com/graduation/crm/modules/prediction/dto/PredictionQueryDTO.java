package com.graduation.crm.modules.prediction.dto;

import lombok.Data;

@Data
public class PredictionQueryDTO {
    private Long advisorId;
    private Long managerId;
    private String dateStart;
    private String dateEnd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
