package com.graduation.crm.modules.heat.service;

import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.follow.entity.FollowRecord;

import java.time.LocalDateTime;

public interface HeatSignalService {
    void analyzeFollowSignals(Customer customer, FollowRecord record, LocalDateTime previousFollowTime);
}
