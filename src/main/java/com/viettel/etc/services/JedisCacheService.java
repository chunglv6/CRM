package com.viettel.etc.services;

import java.util.Map;

public interface JedisCacheService {
    void hset(String key, String field, String value);

    Object hget(String key, String field);

    String getMessageErrorByKey(String key);

    int getCodeErrorByKey(String key);

    String replaceMessWithValues(String mess, Map<String, String> parameter);

    Object hdelete(String key, String field);
}
