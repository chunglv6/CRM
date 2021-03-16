package com.viettel.etc.services;

import com.viettel.etc.dto.im.StockEtagDTO;
import com.viettel.etc.dto.im.StockTransEtagDTO;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface IMService {
    StockTransEtagDTO verifySerial(StockTransEtagDTO  stockTransEtagDTO, Authentication authentication, VehicleEntity vehicleEntity, int caseUpdate) throws IOException;

    StockTransEtagDTO updateSerial(StockTransEtagDTO  stockTransEtagDTO, Authentication authentication) throws IOException;

    StockEtagDTO getSerialDetails(String serial, Authentication authentication) throws IOException;
}
