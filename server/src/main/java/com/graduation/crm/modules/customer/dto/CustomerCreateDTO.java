package com.graduation.crm.modules.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerCreateDTO {

    @NotBlank(message = "客户姓名不能为空")
    private String customerName;
    private String mobile;
    private String gender;
    private Integer age;
    private String source;
    private Long advisorId;
    private Long managerId;
    private Long deptId;
    private String intentLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String region;
    private String houseType;
    private String purpose;
    private String remark;
    private Long createdBy;
    private List<Long> tagIds;
}
