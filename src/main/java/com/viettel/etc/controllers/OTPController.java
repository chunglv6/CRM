package com.viettel.etc.controllers;

import com.viettel.etc.dto.OtpDTO;
import com.viettel.etc.services.OtpService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


/**
 * Autogen class: Lop gui ma OTP
 *
 * @author ToolGen
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class OTPController {

    @Autowired
    OtpService otpService;

    /**
     * Yeu cau ma OTP
     *
     * @param dataParams params client
     * @return
     */
    @GetMapping(value = "/request-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> requestOTP(@AuthenticationPrincipal Authentication authentication, OtpDTO dataParams) {
        Object resultObj = otpService.requestOTP(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Yeu cau ma OTP cho hop dong
     *
     * @param dataParams params client
     * @return
     */
    @GetMapping(value = "/request-otp/contract", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> requestOtpContract(@AuthenticationPrincipal Authentication authentication, OtpDTO dataParams) {
        Object resultObj = otpService.requestOtpContract(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Yeu cau ma OTP cho hop dong
     *
     * @param dataParams params client
     * @return
     */
    @PostMapping(value = "/request-otp/contract", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> otpContract(@AuthenticationPrincipal Authentication authentication, OtpDTO dataParams) {
        Object resultObj = otpService.otpContract(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
