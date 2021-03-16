package com.viettel.etc.services.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.viettel.etc.dto.notification.NotificationRequestDTO;
import com.viettel.etc.dto.notification.SubscriptionRequestDTO;
import com.viettel.etc.services.NotificationService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@Log4j
public class NotificationServiceImpl implements NotificationService {

    @Value("${app.firebase-config}")
    private String firebaseConfig;

    private FirebaseApp firebaseApp;

    @PostConstruct
    private void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfig).getInputStream())).build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Create FirebaseApp Error", e);
        }
    }

    @Override
    public String sendPnsToDevice(NotificationRequestDTO requestDTO) {
        Message message = Message.builder()
                .setToken(requestDTO.getTarget())
                .setNotification(Notification.builder().setTitle(requestDTO.getTitle()).setBody(requestDTO.getBody()).build())
                .putData("content", requestDTO.getTitle())
                .putData("body", requestDTO.getBody())
                .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }

        return response;
    }

    @Override
    public void subscribeToTopic(SubscriptionRequestDTO subscriptionRequestDTO) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).subscribeToTopic(subscriptionRequestDTO.getTokens(),
                    subscriptionRequestDTO.getTopicName());
        } catch (FirebaseMessagingException e) {
            log.error("Firebase subscribe to topic fail", e);
        }
    }

    @Override
    public void unsubscribeFromTopic(SubscriptionRequestDTO subscriptionRequestDTO) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).unsubscribeFromTopic(subscriptionRequestDTO.getTokens(),
                    subscriptionRequestDTO.getTopicName());
        } catch (FirebaseMessagingException e) {
            log.error("Firebase unsubscribe from topic fail", e);
        }
    }

    @Override
    public String sendPnsToTopic(SubscriptionRequestDTO subscriptionRequestDTO) {
        Message message = Message.builder()
                .setTopic(subscriptionRequestDTO.getTarget())
                .setNotification(Notification.builder().setTitle(subscriptionRequestDTO.getTitle()).setBody(subscriptionRequestDTO.getBody()).build())
                .putData("content", subscriptionRequestDTO.getTitle())
                .putData("body", subscriptionRequestDTO.getBody())
                .build();

        String response = null;
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }

        return response;
    }

    @Override
    public String sendPnsToCondition(NotificationRequestDTO requestDTO) {
        Message message = Message.builder()
                .setCondition(requestDTO.getTarget())
                .setNotification(Notification.builder().setTitle(requestDTO.getTitle()).setBody(requestDTO.getBody()).build())
                .putData("content", requestDTO.getTitle())
                .putData("body", requestDTO.getBody())
                .build();
        String response = null;
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }
        return response;
    }
}
