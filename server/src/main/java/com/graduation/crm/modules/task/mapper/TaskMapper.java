package com.graduation.crm.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.entity.Task;
import com.graduation.crm.modules.task.vo.TaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    IPage<TaskVO> selectTaskPage(IPage<TaskVO> page, @Param("query") TaskQueryDTO query);
}

