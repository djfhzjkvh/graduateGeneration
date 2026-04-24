package com.graduation.crm.modules.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Excel 客户导入确认参数。
 */
@Data
public class CustomerExcelImportConfirmDTO {

    @NotNull(message = "源文件不能为空")
    private Long sourceFileId;

    private Long createdBy;
}
