package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.util.List;

/**
 * Excel 客户导入确认结果。
 */
@Data
public class CustomerExcelImportResultVO {

    private Integer successCount;
    private Integer failedCount;
    private List<CustomerExcelImportRowVO> failedRows;
}
