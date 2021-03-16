package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettel.etc.dto.im.ResStockEtagDTO;
import com.viettel.etc.dto.im.ResStockTransEtagDTO;
import com.viettel.etc.dto.im.StockEtagDTO;
import com.viettel.etc.dto.im.StockTransEtagDTO;
import com.viettel.etc.repositories.tables.VehicleRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.services.IMService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;

import static com.viettel.etc.utils.Constants.CASE_UPDATE_VEHICLE;

@Service
public class IMServiceImpl implements IMService {

    @Value("${ws.im-vdtc.verify.serial.url}")
    private String wsVerifySerial;

    @Value("${ws.im-vdtc.update.serial.url}")
    private String wsUpdateSerial;

    @Value("${ws.im-vdtc.get.serial.url}")
    private String wsSerialDetails;

    @Autowired
    private VehicleRepositoryJPA vehicleRepositoryJPA;

    /**
     * Verify serial
     *
     * @param stockTransEtagDTO
     * @param authentication
     * @return
     * @throws IOException
     */
    @Override
    public StockTransEtagDTO verifySerial(StockTransEtagDTO stockTransEtagDTO, Authentication authentication, VehicleEntity vehicleEntity, int caseUpdate){

        String response = FnCommon.doPostRequest(wsVerifySerial, FnCommon.getStringToken(authentication), stockTransEtagDTO);
        if (FnCommon.isNullOrEmpty(response)) {
            returnStatusVehicle(vehicleEntity, caseUpdate);
            stockTransEtagDTO.setStatus(Constants.IM_VDTC_STATUS.FAILED);
            updateSerial(stockTransEtagDTO, authentication);
            throw new EtcException("im.vdtc.call.ws.fail");
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ResStockTransEtagDTO>() {
        }.getType();
        ResStockTransEtagDTO res = gson.fromJson(response, type);
        if(res.getMess().getCode() != ErrorApp.SUCCESS.getCode()){
            returnStatusVehicle(vehicleEntity, caseUpdate);
            stockTransEtagDTO.setStatus(Constants.IM_VDTC_STATUS.FAILED);
            updateSerial(stockTransEtagDTO, authentication);
            throw new EtcException(res.getMess().getDescription());
        }
        return res.getData();
    }

    /***
     * Cap nhat lai trang thai cua xe neu ma goi IM VDTC loi
     * @param vehicleEntity
     * @param caseUpdate
     */
    private void returnStatusVehicle(VehicleEntity vehicleEntity, int caseUpdate){
        if(caseUpdate == CASE_UPDATE_VEHICLE.REGISTER_VEHICLE){
            vehicleRepositoryJPA.delete(vehicleEntity);
        }else if(caseUpdate == CASE_UPDATE_VEHICLE.ASSIGN_VEHICLE){
            vehicleEntity.setStatus(VehicleEntity.Status.SIMILAR.value);
            vehicleEntity.setActiveStatus(null);
            vehicleEntity.setRfidSerial(null);
            vehicleEntity.setTid(null);
            vehicleEntity.setEpc(null);
            vehicleEntity.setRfidType(null);
            vehicleRepositoryJPA.save(vehicleEntity);
        }
    }

    /**
     * Update serial
     *
     * @param stockTransEtagDTO
     * @param authentication
     * @return
     * @throws IOException
     */
    @Override
    public StockTransEtagDTO updateSerial(StockTransEtagDTO stockTransEtagDTO, Authentication authentication)  {

        String response = FnCommon.doPostRequest(wsUpdateSerial, FnCommon.getStringToken(authentication), stockTransEtagDTO);
        if (FnCommon.isNullOrEmpty(response)) {
            throw new EtcException("im.vdtc.call.ws.fail");
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ResStockTransEtagDTO>() {
        }.getType();
        ResStockTransEtagDTO res = gson.fromJson(response, type);
        if(res.getMess().getCode() != ErrorApp.SUCCESS.getCode()){
            throw new EtcException(res.getMess().getDescription());
        }
        return res.getData();
    }

    /****
     * Get detail serial from im-vdtc 29112020
     * @param serial
     * @param authentication
     * @return
     * @throws IOException
     */
    @Override
    public StockEtagDTO getSerialDetails(String serial, Authentication authentication){
        if(FnCommon.isNullOrEmpty(serial)){
            throw new EtcException("crm.validate.im-vdtc.serial.invalid");
        }
        String url = wsSerialDetails.replace("{serial}", serial);
        String response = FnCommon.doGetRequest(url, null, FnCommon.getStringToken(authentication));
        if (FnCommon.isNullOrEmpty(response)) {
            throw  new EtcException("im.vdtc.call.ws.fail");
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ResStockEtagDTO>() {
        }.getType();
        ResStockEtagDTO res = gson.fromJson(response, type);
        if(res.getMess().getCode() != ErrorApp.SUCCESS.getCode()){
            throw new EtcException(res.getMess().getDescription());
        }
        return res.getData();
    }
}
