package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.*;
import com.viettel.etc.services.VehicleGroupService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class VehicleGroupServiceImpl implements VehicleGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleGroupServiceImpl.class);
    @Value("${ws.dmdc.vehicle-groups}")
    private String vehicleGroupUrl;

    @Value("${ws.dmdc.vehicle.group.id}")
    private String vehicleGroupId;

    @Value("${crm.ws.time-out}")
    private String timeOut;

    @Override
    public List<VehicleGroupDTO.VehicleGroup> findVehicleGroupByListId(String token, List<Long> vehicleGroupIdList) {
        Map<String, String> params = new HashMap<>();
        String listId = Joiner.on(",").join(vehicleGroupIdList);
        params.put("listId", listId);
        List<VehicleGroupDTO.VehicleGroup> result = new ArrayList<>();
        String strResp = FnCommon.doGetRequest(vehicleGroupUrl, params, token);
        VehicleGroupDTO vehicleGroupDTO = new Gson().fromJson(strResp, VehicleGroupDTO.class);
        if (vehicleGroupDTO.getData() != null) {
            result = vehicleGroupDTO.getData();
        }
        return result;
    }

    @Override
    public List<String> getVehicleGroupById(String token, AddVehicleRequestDTO addVehicleRequestDTO) {
        Map<String, String> params = new HashMap<>();
        if (!Objects.isNull(addVehicleRequestDTO.getVehicleTypeId())) {
            params.put("vehicleTypeId", addVehicleRequestDTO.getVehicleTypeId().toString());
        }
        if (!Objects.isNull(addVehicleRequestDTO.getSeatNumber())) {
            params.put("seatNumber", addVehicleRequestDTO.getSeatNumber().toString());
        }
        if (!Objects.isNull(addVehicleRequestDTO.getCargoWeight())) {
            params.put("cargoWeight", addVehicleRequestDTO.getCargoWeight().toString());
        }
        if (!Objects.isNull(addVehicleRequestDTO.getNetWeight())) {
            params.put("netWeight", addVehicleRequestDTO.getNetWeight().toString());
        }
        if (!Objects.isNull(addVehicleRequestDTO.getPullingWeight())) {
            params.put("pullingWeight", addVehicleRequestDTO.getPullingWeight().toString());
        }
        if (!Objects.isNull(addVehicleRequestDTO.getStationId())) {
            params.put("stationId", addVehicleRequestDTO.getStationId().toString());
        }
        String strResp = FnCommon.doGetRequest(vehicleGroupId, params, token);
        List<String> list = new ArrayList<>();
        VehicleGroupResponse vehicleGroupResponse = new Gson().fromJson(strResp, VehicleGroupResponse.class);
        if (vehicleGroupResponse != null && vehicleGroupResponse.getData() != null) {
            list.add(vehicleGroupResponse.getData().getVehicleGroupId().toString());
            list.add(vehicleGroupResponse.getData().getVehicleGroupCode());
        }
        return list;
    }

    @Override
    public List<VehicleGroupIdResponseDTO.VehicleGroup> findVehicleGroupByListObject(String token, VehicleGroupIdDTO vehicleGroupIdDTO) throws IOException {
        List<VehicleGroupIdResponseDTO.VehicleGroup> result = new ArrayList<>();
        String strResp = doPostRequest(vehicleGroupId, vehicleGroupIdDTO, token);
        VehicleGroupIdResponseDTO vehicleGroupDTO = new Gson().fromJson(strResp, VehicleGroupIdResponseDTO.class);
        if (vehicleGroupDTO.getData() != null) {
            result = vehicleGroupDTO.getData();
        }
        return result;
    }

    String doPostRequest(String url, VehicleGroupIdDTO vehicleGroupIdDTO, String token) throws IOException {
        String strRes = null;
        Request request = null;
        OkHttpClient client = new OkHttpClient();
        //check step == config
        client.setConnectTimeout(Long.parseLong(timeOut), TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(vehicleGroupIdDTO));
        request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .url(httpBuilder.build())
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        strRes = response.body().string();
        return strRes.replace("null", "\"\"");
    }

    /**
     * Lay tat ca du lieu vehicle group
     *
     * @param token Ma xac thuc
     * @return Danh sach du lieu
     */
    @Override
    public List<LinkedHashMap<?, ?>> findAllVehicleGroup(String token) {
        String strResp = FnCommon.doGetRequest(vehicleGroupUrl, null, token);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity;
        List<LinkedHashMap<?, ?>> result = null;
        try {
            responseEntity = jackson.readValue(strResp, ResponseEntity.class);
            LinkedHashMap<?, ?> data = (LinkedHashMap<?, ?>) responseEntity.getData();
            result = (List<LinkedHashMap<?, ?>>) data.get("listData");
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu doan: ", e);
        }
        return result;
    }
}
