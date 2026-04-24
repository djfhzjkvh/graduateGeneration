package com.graduation.crm.modules.dashboard.mapper;

import com.graduation.crm.modules.dashboard.vo.AdvisorRankVO;
import com.graduation.crm.modules.dashboard.vo.DailyStatVO;
import com.graduation.crm.modules.dashboard.vo.SimpleCustomerVO;
import com.graduation.crm.modules.dashboard.vo.SimpleTaskVO;
import com.graduation.crm.modules.dashboard.vo.TaskStatusStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardMapper {

    Integer countTodayTasks(@Param("ownerId") Long ownerId);

    Integer countOverdueTasks(@Param("ownerId") Long ownerId);

    Integer countHighIntentCustomers(@Param("advisorId") Long advisorId, @Param("managerId") Long managerId);

    Integer countNewCustomers(@Param("advisorId") Long advisorId, @Param("managerId") Long managerId);

    Integer countCustomers(@Param("advisorId") Long advisorId, @Param("managerId") Long managerId, @Param("status") String status);

    Integer countPendingTasks(@Param("ownerId") Long ownerId, @Param("managerId") Long managerId);

    List<SimpleTaskVO> selectTodayTasks(@Param("ownerId") Long ownerId, @Param("limit") Integer limit);

    List<SimpleCustomerVO> selectHighIntentCustomers(@Param("advisorId") Long advisorId, @Param("managerId") Long managerId, @Param("limit") Integer limit);

    List<AdvisorRankVO> selectAdvisorRank(@Param("managerId") Long managerId);

    List<DailyStatVO> selectDailyStats(@Param("limit") Integer limit);

    List<TaskStatusStatVO> selectTaskStatusStats();
}

