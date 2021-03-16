package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.viettel.etc.dto.StageDTO;
import com.viettel.etc.services.StageService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StageServiceImpl implements StageService {
    private static final Logger LOG = LoggerFactory.getLogger(StageServiceImpl.class);

    @Value("${ws.dmdc.stages}")
    private String stageUrl;

    /**
     * Lay thong tin stages
     *
     * @param token
     * @param stageIdList
     * @return
     */
    @Override
    public List<StageDTO.Stage> findStagesByListId(String token, List<Long> stageIdList) {
        Map<String, String> params = new HashMap<>();
        String listId = Joiner.on(",").join(stageIdList);
        params.put("listId", listId);
        List<StageDTO.Stage> result = new ArrayList<>();
        String strResp = FnCommon.doGetRequest(stageUrl, params, token);
        StageDTO stageDTO = new Gson().fromJson(strResp, StageDTO.class);
        if (stageDTO.getData() != null) {
            result = stageDTO.getData();
        }
        return result;
    }

    /**
     * Ham lay du lieu doan
     *
     * @param token
     * @return
     */
    @Override
    public List<LinkedHashMap<?, ?>> findAllStages(String token) {
        return findAllStagesBOO2(token, null);
    }

    @Override
    public List<LinkedHashMap<?, ?>> findAllStagesBOO2(String token, Map<String, String> params) {
        String strResp = FnCommon.doGetRequest(stageUrl, params, token);
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

    /**
     * Lay du lieu stage qua stationInId va stationOutId
     *
     * @param stationInId
     * @param stationOutId
     * @return
     */
    @Override
    public LinkedHashMap<?, ?> findByStationInAndStationOut(Long stationInId, Long stationOutId, String token) {
        String url = stageUrl + "/stationIn/" + stationInId + "/stationOut/" + stationOutId;
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity;
        LinkedHashMap<?, ?> result = null;
        try {
            String strResp = FnCommon.doGetRequest(url, null, token);
            if (!FnCommon.isNullOrEmpty(strResp)) {
                responseEntity = jackson.readValue(strResp, ResponseEntity.class);
                result = (LinkedHashMap<?, ?>) responseEntity.getData();
            }
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu doan: ", e);
        }
        return result;
    }

}
