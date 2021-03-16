package com.viettel.etc.services;

import com.viettel.etc.dto.notification.NotificationRequestDTO;
import com.viettel.etc.dto.notification.SubscriptionRequestDTO;

public interface NotificationService {

    String sendPnsToDevice(NotificationRequestDTO notificationRequestDTO);

    void subscribeToTopic(SubscriptionRequestDTO subscriptionRequestDTO);

    void unsubscribeFromTopic(SubscriptionRequestDTO subscriptionRequestDTO);

    String sendPnsToTopic(SubscriptionRequestDTO subscriptionRequestDTO);

    String sendPnsToCondition(NotificationRequestDTO requestDTO);
}
