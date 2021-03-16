package com.viettel.etc.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ClusterRedisConfig {

    @Value("${spring.redis.cluster.nodes}")
    String nodes;

    String password;

    @Value("${spring.redis.cluster.max-redirects}")
    int maxRedirects;
}
