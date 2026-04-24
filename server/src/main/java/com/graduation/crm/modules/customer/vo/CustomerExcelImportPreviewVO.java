package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.util.List;

/**
 * Excel 客户导入预览结果。
 */
@Data
public class CustomerExcelImportPreviewVO {

    private Long sourceFileId;
    private Integer totalCount;
    private Integer validCount;
    private Integer invalidCount;
    private List<CustomerExcelImportRowVO> rows;
}
