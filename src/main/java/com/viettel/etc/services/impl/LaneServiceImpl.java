package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.etc.services.LaneService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

@Service
public class LaneServiceImpl implements LaneService {

    private static final Logger LOG = LoggerFactory.getLogger(BotServiceImpl.class);

    @Value("${ws.dmdc.lane}")
    private String laneUrl;

    /**
     * Lay danh sach lan ra,lan vao
     *
     * @param token
     * @return
     */
    @Override
    public List<LinkedHashMap<?, ?>> findAllLanes(String token) {
        String strResp = FnCommon.doGetRequest(laneUrl, null, token);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity;
        List<LinkedHashMap<?, ?>> result = null;
        try {
            responseEntity = jackson.readValue(strResp, ResponseEntity.class);
            LinkedHashMap<?, ?> data = (LinkedHashMap<?, ?>) responseEntity.getData();
            result = data != null ? (List<LinkedHashMap<?, ?>>) data.get("listData") : null;
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu lan ra , lan vao: ", e);
        }
        return result;
    }
}
