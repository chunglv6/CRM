package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.viettel.etc.dto.VehicleDTO;
import com.viettel.etc.dto.boo.ReqActivationCheckDTO;
import com.viettel.etc.dto.boo.ResActivationCheckDTO;
import com.viettel.etc.dto.ocs.ETC360CheckInReqDTO;
import com.viettel.etc.dto.ocs.ETC360GetChargeReqDTO;
import com.viettel.etc.dto.ocs.ETC360ResponseDTO;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.services.Etc360Service;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Etc360ServiceImpl implements Etc360Service {
    @Autowired
    OCSServiceImpl ocsService;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    Boo1ServiceImpl boo1Service;

    @Value("${ws.ocs.query-session}")
    String querySessionUrl;

    @Value("${ws.ocs.get-charge}")
    String getChargeUrl;

    @Value("${ws.ocs.check-in}")
    String checkInUrl;

    @Override
    public Object querySession(String plateNumber, Authentication authentication) {
        Map<String, String> params = new HashMap<>();
        params.put("plateNumber", plateNumber);
        String stringRes = ocsService.doPostRequest(querySessionUrl, params, "", 1, authentication, 1);
        return new Gson().fromJson(stringRes, HashMap.class);
    }

    @Override
    public Object getCharge(ETC360GetChargeReqDTO dataParams, Authentication authentication) {
        Map<String, String> params = new HashMap<>();
        params.put("EPC", dataParams.getEPC());
        if (Constants.OCS_STATION.equals(dataParams.getMovementType())) {
            params.put("stationIn", dataParams.getStationIn());
            params.put("laneIn", "0");
        }
        if (Constants.OCS_STAGE.equals(dataParams.getMovementType())) {
            params.put("stationIn", dataParams.getStationIn());
            params.put("laneIn", "0");
            params.put("stationOut", dataParams.getStationOut());
            params.put("laneOut", "0");
        }
        params.put("movementType", dataParams.getMovementType());
        params.put("eventTimeStamp", dataParams.getEventTimeStamp());
        if (dataParams.getVehicleGroup() != null) {
            params.put("chargedVehicleType", dataParams.getVehicleGroup());
        }
        String stringRes = ocsService.doPostRequest(getChargeUrl, params, "", 1, authentication, 1);
        return new Gson().fromJson(stringRes, ETC360ResponseDTO.class);
    }

    @Override
    public Object checkIn(ETC360CheckInReqDTO dataParams, Authentication authentication) {
        Map<String, String> params = new HashMap<>();
        params.put("EPC", dataParams.getEPC());
        if (Constants.OCS_STATION.equals(dataParams.getMovementType())) {
            params.put("stationIn", dataParams.getStationIn());
            params.put("laneIn", "0");
        } else if (Constants.OCS_STAGE.equals(dataParams.getMovementType())) {
            params.put("stationIn", dataParams.getStationIn());
            params.put("laneIn", "0");
            params.put("stationOut", dataParams.getStationOut());
            params.put("laneOut", "0");
        }
        if (dataParams.getPlate() != null) {
            params.put("plate", dataParams.getPlate());
        }
        if (dataParams.getTID() != null) {
            params.put("TID", dataParams.getTID());
        }
        params.put("movementType", dataParams.getMovementType());
        params.put("isFreeCharge", "0");
        if (dataParams.getHashValue() != null) {
            params.put("hashValue", dataParams.getHashValue());
        }
        String stringRes = ocsService.doPostRequest(checkInUrl, params, "", 1, authentication, 1);
        return new Gson().fromJson(stringRes, ETC360ResponseDTO.class);
    }

    @Override
    public Object searchPlateNumber(String plateNumber, String plateType, Authentication authentication) {
        VehicleEntity vehicle = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(plateNumber, FnCommon.getPlateTypeCodeFromPlateNumber(plateType));
        VehicleDTO vehicleDTO = new VehicleDTO();
        if (vehicle == null) {
            ReqActivationCheckDTO req = new ReqActivationCheckDTO();
            req.setPlate(FnCommon.getPlateNumberBoo1(plateNumber, plateType));
            req.setRequest_id(System.currentTimeMillis());
            req.setRequest_datetime(System.currentTimeMillis());
            ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
            if (res != null && Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                vehicleDTO.setEpc(res.getEtag());
                vehicleDTO.setPlateNumber(res.getPlate());
                vehicleDTO.setVehicleGroupId(res.getVehicle_type());
                vehicleDTO.setVehicleTypeId(Long.valueOf(res.getRegister_vehicle_type()));
                return vehicleDTO;
            }
            return null;
        }
        vehicleDTO.setEpc(vehicle.getEpc());
        vehicleDTO.setRfidSerial(vehicle.getRfidSerial());
        vehicleDTO.setCargoWeight(vehicle.getCargoWeight());
        vehicleDTO.setNetWeight(vehicle.getNetWeight());
        vehicleDTO.setVehicleId(vehicle.getVehicleId());
        vehicleDTO.setSeatNumber(vehicle.getSeatNumber());
        vehicleDTO.setPlateNumber(vehicle.getPlateNumber());
        vehicleDTO.setVehicleGroupId(vehicle.getVehicleGroupId());
        vehicleDTO.setVehicleTypeId(vehicle.getVehicleTypeId());
        vehicleDTO.setCustId(vehicle.getCustId());
        vehicleDTO.setContractId(vehicle.getContractId());
        return vehicleDTO;
    }
}
