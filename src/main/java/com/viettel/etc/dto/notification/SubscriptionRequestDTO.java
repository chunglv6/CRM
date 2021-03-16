package com.viettel.etc.dto.notification;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionRequestDTO {
    String target;
    String title;
    String body;
    List<String> tokens;
    String topicName;
}
