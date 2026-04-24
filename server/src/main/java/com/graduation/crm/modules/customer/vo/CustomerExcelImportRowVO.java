package com.graduation.crm.modules.customer.vo;

import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import lombok.Data;

import java.util.List;

/**
 * Excel 客户导入单行预览结果。
 */
@Data
public class CustomerExcelImportRowVO {

    private Integer rowIndex;
    private Boolean valid;
    private List<String> errors;
    private CustomerCreateDTO customer;
}
