package com.graduation.crm.modules.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeptVO {

    private Long id;
    private String deptName;
    private Long parentId;
    private Long leaderId;
    private String leaderName;
    private Integer status;
    private List<DeptVO> children = new ArrayList<>();
}

