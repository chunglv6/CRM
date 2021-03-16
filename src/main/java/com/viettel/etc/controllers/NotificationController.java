package com.viettel.etc.controllers;

import com.viettel.etc.dto.notification.NotificationRequestDTO;
import com.viettel.etc.dto.notification.SubscriptionRequestDTO;
import com.viettel.etc.services.NotificationService;
import com.viettel.etc.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendPnsToDevice(@RequestBody NotificationRequestDTO notificationRequestDTO) {
        return new ResponseEntity<>(notificationService.sendPnsToDevice(notificationRequestDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    public void subscribeToTopic(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        notificationService.subscribeToTopic(subscriptionRequestDTO);
    }

    @PostMapping(value = "/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    public void unsubscribeFromTopic(SubscriptionRequestDTO subscriptionRequestDTO) {
        notificationService.unsubscribeFromTopic(subscriptionRequestDTO);
    }

    @PostMapping(value = "/topic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendPnsToTopic(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        return new ResponseEntity<>(notificationService.sendPnsToTopic(subscriptionRequestDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/condition", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendPnsToCondition(@RequestBody NotificationRequestDTO requestDTO) {
        return new ResponseEntity<>(notificationService.sendPnsToCondition(requestDTO), HttpStatus.OK);
    }
}
