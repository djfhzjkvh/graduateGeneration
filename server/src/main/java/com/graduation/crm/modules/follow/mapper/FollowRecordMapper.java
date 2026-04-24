package com.graduation.crm.modules.follow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.follow.entity.FollowRecord;
import com.graduation.crm.modules.follow.vo.FollowRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowRecordMapper extends BaseMapper<FollowRecord> {

    List<FollowRecordVO> selectByCustomerId(@Param("customerId") Long customerId);
}

