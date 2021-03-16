package com.viettel.etc.controllers;

import com.viettel.etc.dto.ocs.ETC360CheckInReqDTO;
import com.viettel.etc.dto.ocs.ETC360GetChargeReqDTO;
import com.viettel.etc.services.Etc360Service;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class Etc360Controller {

    @Autowired
    Etc360Service etc360Service;

    @GetMapping(value = "/etc360/query-session", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> querySession(@AuthenticationPrincipal Authentication authentication,
                                               @NotBlank String plateNumber) {

        Object result = etc360Service.querySession(plateNumber, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
    }

    @PostMapping(value = "/etc360/get-charge", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCharge(@AuthenticationPrincipal Authentication authentication,
                                            @Valid @RequestBody ETC360GetChargeReqDTO dataParams) {

        Object result = etc360Service.getCharge(dataParams, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
    }

    @PostMapping(value = "/etc360/check-in", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkIn(@AuthenticationPrincipal Authentication authentication,
                                          @Valid @RequestBody ETC360CheckInReqDTO dataParams) {

        Object result = etc360Service.checkIn(dataParams, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
    }

    @GetMapping(value = "/etc360/vehicle/{plateNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchPlateNumber(@AuthenticationPrincipal Authentication authentication,
                                                    @Param("plateType") String plateType,
                                                    @Pattern(regexp = "[A-Z0-9]+") @PathVariable String plateNumber) {

        Object result = etc360Service.searchPlateNumber(plateNumber, plateType, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
    }
}
