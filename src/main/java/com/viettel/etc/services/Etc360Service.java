package com.viettel.etc.services;

import com.viettel.etc.dto.ocs.ETC360CheckInReqDTO;
import com.viettel.etc.dto.ocs.ETC360GetChargeReqDTO;
import org.springframework.security.core.Authentication;

public interface Etc360Service {
    Object querySession(String plateNumber, Authentication authentication);

    Object getCharge(ETC360GetChargeReqDTO dataParams, Authentication authentication);

    Object checkIn(ETC360CheckInReqDTO dataParams, Authentication authentication);

    Object searchPlateNumber(String plateNumber, String plateType, Authentication authentication);
}
