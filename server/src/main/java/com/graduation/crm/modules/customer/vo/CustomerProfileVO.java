package com.graduation.crm.modules.customer.vo;

import com.graduation.crm.modules.competitor.vo.CustomerCompetitorFocusVO;
import com.graduation.crm.modules.follow.vo.FollowRecordVO;
import com.graduation.crm.modules.heat.vo.HeatDetailVO;
import com.graduation.crm.modules.note.vo.NoteVO;
import com.graduation.crm.modules.task.vo.TaskVO;
import lombok.Data;

import java.util.List;

/**
 * 客户画像聚合返回对象。
 */
@Data
public class CustomerProfileVO {

    private CustomerDetailVO customer;
    private List<FollowRecordVO> recentFollows;
    private List<TaskVO> recentTasks;
    private List<NoteVO> recentNotes;
    private List<CustomerCompetitorFocusVO> competitorFocuses;
    private List<CustomerAssignLogVO> assignLogs;
    private HeatDetailVO heat;
}
