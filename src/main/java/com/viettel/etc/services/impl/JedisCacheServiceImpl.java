package com.viettel.etc.services.impl;

import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JedisCacheServiceImpl implements JedisCacheService {
    private static final Logger LOGGER = Logger.getLogger(FnCommon.class);

    @Qualifier("redisTemplate")
    @Autowired
    RedisTemplate redisTemplateCluster;

    @Override
    public void hset(String key, String field, String value) {
        redisTemplateCluster.opsForHash().put(key, field, value);
    }

    @Override
    public Object hget(String key, String field) {
        return redisTemplateCluster.opsForHash().get(key, field);
    }

    @Override
    public String getMessageErrorByKey(String key) {
        try {
            Object getRedis = hget(key, Constants.MESSAGE_ERROR_VI);
            return getRedis != null ? getRedis.toString() : key;
        } catch (Exception e) {
            LOGGER.error(e);
            return key;
        }
    }

    @Override
    public int getCodeErrorByKey(String key) {
        try {
            return Integer.parseInt(hget(key, Constants.MESSAGE_CODE).toString());
        } catch (Exception e) {
            LOGGER.error(e);
            return 0;
        }
    }

    @Override
    public String replaceMessWithValues(String mess, Map<String, String> parameter) {
        String result = getMessageErrorByKey(mess);
        for (Map.Entry<String, String> entry : parameter.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public Object hdelete(String key, String field) {
        return redisTemplateCluster.opsForHash().delete(key, field);
    }


}
