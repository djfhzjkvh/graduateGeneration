package com.graduation.crm.modules.report.dto;

import lombok.Data;

@Data
public class ReportQueryDTO {

    private String dateStart;
    private String dateEnd;
    private Long deptId;
}

