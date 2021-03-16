package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.viettel.etc.dto.VehicleTypeDTO;
import com.viettel.etc.dto.boo.VehicleTypeBooDTO;
import com.viettel.etc.services.VehicleTypeService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VehicleTypeServiceImpl implements VehicleTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(StationServiceImpl.class);

    @Value("${ws.dmdc.vehicle-types}")
    String vehicleTypeUrl;

    @Value("${ws.dmdc.vehicle.type.code}")
    String vehicleTypeByCodeUrl;

    @Value("${ws.dmdc.vehicle-types.id}")
    String urlVehicleTypeById;

    @Override
    public List<LinkedHashMap<?, ?>> findAllVehicleType(String token) {
        String strResp = FnCommon.doGetRequest(vehicleTypeUrl, null, token);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity = null;
        List<LinkedHashMap<?, ?>> result = null;
        try {
            responseEntity = jackson.readValue(strResp, ResponseEntity.class);
            LinkedHashMap<?, ?> data = (LinkedHashMap<?, ?>) responseEntity.getData();
            result = (List<LinkedHashMap<?, ?>>) data.get("listData");
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu loai phuong tien: ", e);
        }
        return result;
    }

    @Override
    public List<VehicleTypeDTO.VehicleType> findVehicleTypeByListId(String token, List<Long> vehicleTypeIdList) {
        Map<String, String> params = new HashMap<>();
        String listId = Joiner.on(",").join(vehicleTypeIdList);
        params.put("listId", listId);
        List<VehicleTypeDTO.VehicleType> result = new ArrayList<>();
        String strResp = FnCommon.doGetRequest(vehicleTypeUrl, params, token);
        VehicleTypeDTO vehicleTypeDTO = new Gson().fromJson(strResp, VehicleTypeDTO.class);
        if (vehicleTypeDTO.getData() != null) {
            result = vehicleTypeDTO.getData();
        }
        return result;
    }

    /**
     * Tim kiem theo ten
     *
     * @param token
     * @param name
     * @return
     */
    @Override
    public List<LinkedHashMap<?, ?>> findByName(String token, String name) {
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity;
        List<LinkedHashMap<?, ?>> result = null;
        try {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            String strResp = FnCommon.doGetRequest(vehicleTypeUrl, params, token);
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
    public LinkedHashMap<?, ?> findById(String token, String id) {
        String vehicleUrl = urlVehicleTypeById.replace("{id}", id);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity;
        LinkedHashMap<?, ?> result = null;
        try {
            String strResp = FnCommon.doGetRequest(vehicleUrl, null, token);
            if (!FnCommon.isNullOrEmpty(strResp)) {
                responseEntity = jackson.readValue(strResp, ResponseEntity.class);
                result = (LinkedHashMap<?, ?>) responseEntity.getData();
            }
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu doan: ", e);
        }
        return result;
    }

    @Override
    public VehicleTypeBooDTO findByCode(String token, String code) {
        String vehicleUrl = vehicleTypeByCodeUrl.replace("{code}", code);
        VehicleTypeBooDTO vehicleType = null;
        try {
            String strResp = FnCommon.doGetRequest(vehicleUrl, null, token);
            if (!FnCommon.isNullOrEmpty(strResp)) {
                vehicleType = new Gson().fromJson(strResp, VehicleTypeBooDTO.class);
            }
        } catch (Exception e) {
            LOG.error("Loi lay du lieu doan: ", e);
        }
        return vehicleType;
    }


}
