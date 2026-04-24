package com.graduation.crm.modules.customer.service.impl;

import com.graduation.crm.modules.customer.service.CustomerExcelTemplateService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * 客户 Excel 模板服务实现。
 */
@Service
public class CustomerExcelTemplateServiceImpl implements CustomerExcelTemplateService {

    private static final String[] HEADERS = {
            "客户姓名", "手机号", "性别", "来源", "意向等级", "预算下限", "预算上限",
            "区域", "户型", "购房目的", "备注", "顾问ID", "经理ID", "部门ID"
    };

    @Override
    public XSSFWorkbook buildImportTemplate() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("客户导入模板");
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(HEADERS[i]);
            sheet.setColumnWidth(i, 18 * 256);
        }

        // 示例行用于演示模板填写格式，导入时可删除。
        Row sample = sheet.createRow(1);
        sample.createCell(0).setCellValue("张三");
        sample.createCell(1).setCellValue("13800000000");
        sample.createCell(2).setCellValue("男");
        sample.createCell(3).setCellValue("WECHAT");
        sample.createCell(4).setCellValue("HIGH");
        sample.createCell(5).setCellValue("1000000");
        sample.createCell(6).setCellValue("1500000");
        sample.createCell(7).setCellValue("南开区");
        sample.createCell(8).setCellValue("三室两厅");
        sample.createCell(9).setCellValue("自住");
        sample.createCell(10).setCellValue("关注学区和地铁");
        sample.createCell(11).setCellValue("4");
        sample.createCell(12).setCellValue("2");
        sample.createCell(13).setCellValue("1");
        return workbook;
    }
}
