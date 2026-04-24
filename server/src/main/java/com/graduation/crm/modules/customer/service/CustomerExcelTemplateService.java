package com.graduation.crm.modules.customer.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 客户 Excel 模板服务。
 */
public interface CustomerExcelTemplateService {

    XSSFWorkbook buildImportTemplate();
}
