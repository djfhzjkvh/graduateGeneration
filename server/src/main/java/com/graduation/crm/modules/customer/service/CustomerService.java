package com.graduation.crm.modules.customer.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.customer.dto.CustomerAssignDTO;
import com.graduation.crm.modules.customer.dto.CustomerAssignLogQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerExcelImportConfirmDTO;
import com.graduation.crm.modules.customer.dto.CustomerExcelImportPreviewDTO;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerStatusUpdateDTO;
import com.graduation.crm.modules.customer.dto.CustomerUpdateDTO;
import com.graduation.crm.modules.customer.vo.CustomerAssignLogVO;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportPreviewVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportResultVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import com.graduation.crm.modules.customer.vo.CustomerProfileVO;

public interface CustomerService {

    PageResult<CustomerListVO> page(CustomerQueryDTO queryDTO);

    CustomerDetailVO detail(Long id);

    CustomerProfileVO profile(Long id);

    Long create(CustomerCreateDTO dto);

    void update(Long id, CustomerUpdateDTO dto);

    void delete(Long id);

    void assign(Long id, CustomerAssignDTO dto);

    void updateStatus(Long id, CustomerStatusUpdateDTO dto);

    PageResult<CustomerAssignLogVO> assignLogPage(CustomerAssignLogQueryDTO queryDTO);

    PageResult<CustomerAssignLogVO> assignLogsByCustomer(Long customerId, CustomerAssignLogQueryDTO queryDTO);

    CustomerExcelImportPreviewVO previewExcelImport(CustomerExcelImportPreviewDTO dto);

    CustomerExcelImportResultVO confirmExcelImport(CustomerExcelImportConfirmDTO dto);
}
