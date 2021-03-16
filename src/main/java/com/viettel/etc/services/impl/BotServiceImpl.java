package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.etc.services.BotService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BotServiceImpl implements BotService {
    private static final Logger LOG = LoggerFactory.getLogger(BotServiceImpl.class);

    @Value("${ws.dmdc.bot}")
    private String botUrl;

    /**
     * Ham lay du lieu bots
     *
     * @param token
     * @return
     */
    @Override
    public List<LinkedHashMap<?, ?>> findAllBots(String token) {
        String strResp = FnCommon.doGetRequest(botUrl, null, token);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity;
        List<LinkedHashMap<?, ?>> result = null;
        try {
            if (!FnCommon.isNullOrEmpty(strResp)) {
                responseEntity = jackson.readValue(strResp, ResponseEntity.class);
                LinkedHashMap<?, ?> data = (LinkedHashMap<?, ?>) responseEntity.getData();
                result = data != null ? (List<LinkedHashMap<?, ?>>) data.get("listData") : null;
            }
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu doan: ", e);
        }
        return result;
    }

    @Override
    public ResponseEntity existsBotInStageOrStation(String token, String botIds, String stationId, String stageId) {
        Map<String, String> params = new HashMap<>();
        params.put("botIds", botIds);
        params.put("stationId", stationId);
        params.put("stageId", stageId);
        String strResp = FnCommon.doGetRequest(botUrl + "/contain/check", params, token);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity = null;
        try {
            responseEntity = jackson.readValue(strResp, ResponseEntity.class);

        } catch (JsonProcessingException e) {
            LOG.error("Kiem tra du lieu bots ton tai trong tram hoac doan loi: ", e);
        }
        return responseEntity;
    }

}
