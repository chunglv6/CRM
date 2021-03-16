package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.StationDTO;
import com.viettel.etc.services.StationService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StationServiceImpl implements StationService {
    private static final Logger LOG = LoggerFactory.getLogger(StationServiceImpl.class);

    @Value("${ws.dmdc.stations}")
    private String stationsUrl;

    @Value("${ws.dmdc.station.detail}")
    private String stationDetail;

    /**
     * Lay thong tin stations
     *
     * @param token         Chuoi token xac thuc
     * @param stationIdList Danh sach id tram
     * @return Danh sach tram tim kiem
     */
    @Override
    public List<StationDTO.Station> findStationsByListId(String token, List<Long> stationIdList) {
        Map<String, String> params = new HashMap<>();
        String listId = Joiner.on(",").join(stationIdList);
        params.put("listId", listId);
        List<StationDTO.Station> result = new ArrayList<>();
        String strResp = FnCommon.doGetRequest(stationsUrl, params, token);
        StationDTO stationDTO = new Gson().fromJson(strResp, StationDTO.class);
        if (stationDTO.getData() != null) {
            result = stationDTO.getData();
        }
        return result;
    }

    /**
     * Ham lay du lieu tram
     *
     * @param token Chuoi token xac thuc
     * @return Danh sach tram tim kiem
     */
    @Override
    public List<LinkedHashMap<?, ?>> findAllStations(String token) {
        return findAllStationsBOO2(token, null);
    }

    @Override
    public List<LinkedHashMap<?, ?>> findAllStationsBOO2(String token, Map<String, String> params) {
        String strResp = FnCommon.doGetRequest(stationsUrl, params, token);
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
            LOG.error("Loi lay du lieu tram: ", e);
        }
        return result;
    }

    @Override
    public LinkedTreeMap<?, ?> findById(String token, Long id) {
        LinkedTreeMap<?, ?> result = new LinkedTreeMap<>();
        String url = stationDetail.replace("{id}", String.valueOf(id));
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(id));
            String strResp = FnCommon.doGetRequest(url, params, token);
            LinkedHashMap<?, ?> stationDTO = new Gson().fromJson(strResp, LinkedHashMap.class);
            if (stationDTO != null && stationDTO.get("data") != null) {
                result = (LinkedTreeMap) stationDTO.get("data");
            }
        } catch (Exception ex) {
            LOG.error("Loi lay du lieu tram: ", ex);
            result = null;
        }
        return result;
    }

}
