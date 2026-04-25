package com.graduation.crm.modules.notification.service;

import com.graduation.crm.modules.notification.dto.NotificationSendDTO;

public interface NotificationService {
    Long sendToUser(NotificationSendDTO dto);

    Long sendToChannel(String channelCode, NotificationSendDTO dto);

    int sendToManagers(String title, String content, String bizType, Long bizId);

    int retryFailed(Integer maxRetryCount, Integer batchLimit);
}
