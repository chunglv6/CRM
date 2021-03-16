package com.viettel.etc;

import com.viettel.etc.repositories.tables.entities.MessageErrorEntity;
import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.services.MessageErrorService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class ServiceApplication implements CommandLineRunner {
    @Autowired
    MessageErrorService messageErrorService;

    @Autowired
    JedisCacheService jedisCluster;

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        List<MessageErrorEntity> messageErrorEntities = messageErrorService.getAll();
        if (!FnCommon.isNullObject(messageErrorEntities)) {
            messageErrorEntities.forEach(message -> {
                jedisCluster.hset(message.getMessage(), Constants.MESSAGE_CODE, String.valueOf(message.getCode()));
                if (!FnCommon.isNullOrEmpty(message.getMessageVi())) {
                    jedisCluster.hset(message.getMessage(), Constants.MESSAGE_ERROR_VI, message.getMessageVi());
                }
                if (!FnCommon.isNullOrEmpty(message.getMessageEn())) {
                    jedisCluster.hset(message.getMessage(), Constants.MESSAGE_ERROR_EN, message.getMessageEn());
                }
            });
        }
    }
}
