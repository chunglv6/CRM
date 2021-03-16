package com.viettel.etc.controllers;

import com.viettel.etc.dto.OtpDTO;
import com.viettel.etc.services.OtpService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.viettel.etc.controllers.OTPController;
import org.hamcrest.MatcherAssert;
import com.viettel.etc.utils.ErrorApp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.viettel.etc.utils.exceptions.EtcException;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@ExtendWith(MockitoExtension.class)
class OTPControllerTest {

    private MockMvc mvc;
    @Mock
    OtpService otpService;
    @InjectMocks
    OTPController OTPController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(OTPController).build();
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Ignore
    void requestOTP1() throws Exception {
        OtpDTO dataParams = new OtpDTO();
        Authentication authentication = null;
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(otpService.requestOTP(Mockito.any(), authentication)).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(Constants.REQUEST_MAPPING_V1 + "/request-otp")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void requestOTPThrowException1() throws Exception {
        OtpDTO dataParams = new OtpDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        Authentication authentication = null;
        when(otpService.requestOTP(Mockito.any(), authentication)).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(Constants.REQUEST_MAPPING_V1 + "/request-otp")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

}
