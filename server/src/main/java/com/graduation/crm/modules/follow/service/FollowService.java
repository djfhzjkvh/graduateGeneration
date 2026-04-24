package com.graduation.crm.modules.follow.service;

import com.graduation.crm.modules.follow.dto.FollowCreateDTO;
import com.graduation.crm.modules.follow.vo.FollowRecordVO;

import java.util.List;

public interface FollowService {

    void create(FollowCreateDTO dto);

    List<FollowRecordVO> listByCustomerId(Long customerId);
}

