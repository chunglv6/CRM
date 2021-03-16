package com.viettel.etc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.etc.dto.ServiceRegisterDTO;
import com.viettel.etc.dto.keycloak.*;
import com.viettel.etc.dto.viettelpost.BillResponseDTO;
import com.viettel.etc.services.KeycloakService;
import com.viettel.etc.services.ServiceRegistryService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
class CustomerRegisterControllerTest {

    private MockMvc mvc;
    @Mock
    private ServiceRegistryService serviceRegistryService;
    @InjectMocks
    CustomerRegisterController CustomerRegisterController;
    @Mock
    private KeycloakService keycloakService;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(CustomerRegisterController).build();
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Ignore
    void register1() throws Exception {
        ServiceRegisterDTO dataParams = new ServiceRegisterDTO();
        BillResponseDTO resultData1 = new BillResponseDTO();
        when(serviceRegistryService.register(Mockito.any(), Mockito.anyString())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(Constants.REQUEST_MAPPING_V1 + Constants.MOBILE + "/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void registerThrowException1() throws Exception {
        ServiceRegisterDTO dataParams = new ServiceRegisterDTO();
        BillResponseDTO resultData1 = new BillResponseDTO();
        when(serviceRegistryService.register(Mockito.any(), Mockito.anyString())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(Constants.REQUEST_MAPPING_V1 + Constants.MOBILE + "/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void login1() throws Exception {
        ReqLoginDTO dataParams = new ReqLoginDTO();
        ResLoginDTO resultData1 = null;
        when(keycloakService.login(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(Constants.REQUEST_MAPPING_V1 + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
    }

    @Ignore
    void loginThrowException1() throws Exception {
        ReqLoginDTO dataParams = new ReqLoginDTO();
        ResLoginDTO resultData1 = null;
        when(keycloakService.login(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(Constants.REQUEST_MAPPING_V1 + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
    }

    @Ignore
    void lockUser4() throws Exception {
        String userId = "test";
        ReqChangePassUserKeycloakDTO dataParams = new ReqChangePassUserKeycloakDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(keycloakService.lockUser(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + "/lock/users/" + userId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void lockUserThrowException4() throws Exception {
        String userId = "test";
        ReqChangePassUserKeycloakDTO dataParams = new ReqChangePassUserKeycloakDTO();
        when(keycloakService.lockUser(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + "/lock/users/" + userId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void resetPassUser2() throws Exception {
        String userId = "test";
        ResKeycloakDTO resKeycloakDTO = new ResKeycloakDTO();
        when(keycloakService.resetPassUser(Mockito.any(), Mockito.any())).thenReturn(resKeycloakDTO);
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + "/reset/users/" + userId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void resetPassUserThrowException2() throws Exception {
        Authentication authentication = null;
        String userId = "test";
        when(keycloakService.resetPassUser(Mockito.any(), Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + "/reset/users/" + userId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void resetPassUserCPT1() throws Exception {
        ReqResetPassDTO dataParams = new ReqResetPassDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(keycloakService.resetPassUserCPT(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + Constants.MOBILE + "/reset/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void resetPassUserCPTThrowException1() throws Exception {
        ReqResetPassDTO dataParams = new ReqResetPassDTO();
        when(keycloakService.resetPassUserCPT(Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + Constants.MOBILE + "/reset/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void changePassUserCPT2() throws Exception {
        Authentication authentication = null;
        ReqChangePassDTO dataParams = new ReqChangePassDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(keycloakService.changePassUser(Mockito.any(), Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + Constants.MOBILE + "/change/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Ignore
    void changePassUserCPTThrowException2() throws Exception {
        Authentication authentication = null;
        ReqChangePassDTO dataParams = new ReqChangePassDTO();
        when(keycloakService.changePassUser(Mockito.any(), Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + Constants.MOBILE + "/change/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
    }

}
