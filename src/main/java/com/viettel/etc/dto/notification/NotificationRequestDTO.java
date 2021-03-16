package com.viettel.etc.dto.notification;

import lombok.Data;

@Data
public class NotificationRequestDTO {
    String target;
    String title;
    String body;
}
