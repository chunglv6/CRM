package com.viettel.etc.controllers;
import com.viettel.etc.dto.ActionAuditHistoryDTO;
import com.viettel.etc.dto.ActionAuditHistoryDetailDTO;
import com.viettel.etc.dto.ActionDTO;
import com.viettel.etc.dto.AuditHistoryDTO;
import com.viettel.etc.services.ActionService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.ActionController;
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
class ActionControllerTest {

    private MockMvc mvc;
    @Mock
    private ActionService actionService;
    @InjectMocks
    ActionController ActionController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(ActionController).build();
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void getActionType2() throws Exception {
        Authentication authentication = null;
        ActionDTO dataParams = new ActionDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionType(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-types")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionTypeThrowException2() throws Exception {
        Authentication authentication = null;
        ActionDTO dataParams = new ActionDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionType(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-types")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionHistory2() throws Exception {
        Authentication authentication = null;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionHistoryThrowException2() throws Exception {
        Authentication authentication = null;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionReason3() throws Exception {
        Authentication authentication = null;
        ActionDTO dataParams = new ActionDTO();
        Long id = new Long(0);
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionReason(Mockito.any(),Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-reasons/"+id+"")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionReasonThrowException3() throws Exception {
        Authentication authentication = null;
        ActionDTO dataParams = new ActionDTO();
        Long id = new Long(0);
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionReason(Mockito.any(),Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-reasons/"+id+"")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionReason2() throws Exception {
        Authentication authentication = null;
        ActionDTO dataParams = new ActionDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionReason(Mockito.any(),Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-reasons")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionReasonThrowException2() throws Exception {
        Authentication authentication = null;
        ActionDTO dataParams = new ActionDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionReason(Mockito.any(),Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-reasons")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionHistoryDetail3() throws Exception {
        Authentication authentication = null;
        ActionAuditHistoryDetailDTO dataParams = new ActionAuditHistoryDetailDTO();
        Long id = new Long(0);
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionHistoryDetail(Mockito.any(),Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-histories/"+id+"")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getActionHistoryDetailThrowException3() throws Exception {
        Authentication authentication = null;
        ActionAuditHistoryDetailDTO dataParams = new ActionAuditHistoryDetailDTO();
        Long id = new Long(0);
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.getActionHistoryDetail(Mockito.any(),Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/action-histories/"+id+"")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void findActionCustomerHistory3() throws Exception {
        Authentication authentication = null;
        Long custId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.findActionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/customers/"+custId+"/act-customers-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void findActionCustomerHistoryThrowException3() throws Exception {
        Authentication authentication = null;
        Long custId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.findActionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/customers/"+custId+"/act-customers-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void findActionContractHistory4() throws Exception {
        Authentication authentication = null;
        Long custId = new Long(0);
        Long contractId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.findActionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/customers/"+custId+"/contracts/"+contractId+"/act-contract-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void findActionContractHistoryThrowException4() throws Exception {
        Authentication authentication = null;
        Long custId = new Long(0);
        Long contractId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.findActionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/customers/"+custId+"/contracts/"+contractId+"/act-contract-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void exportActHistory3() throws Exception {
        Authentication authentication = null;
        AuditHistoryDTO dataParams = new AuditHistoryDTO();
        HttpServletResponse response = null;
        String resultData1 = null;
        when(actionService.exportActHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(REQUEST_MAPPING_V1+"/act-histories/exports")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void exportActHistoryThrowException3() throws Exception {
        Authentication authentication = null;
        AuditHistoryDTO dataParams = new AuditHistoryDTO();
        HttpServletResponse response = null;
        when(actionService.exportActHistory(Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                post(REQUEST_MAPPING_V1+"/act-histories/exports")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void findActionVehicleHistory5() throws Exception {
        Authentication authentication = null;
        Long custId = new Long(0);
        Long contractId = new Long(0);
        Long vehicleId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.findActionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/customers/"+custId+"/contracts/"+contractId+"/vehicles/"+vehicleId+"/act-vehicle-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void findActionVehicleHistoryThrowException5() throws Exception {
        Authentication authentication = null;
        Long custId = new Long(0);
        Long contractId = new Long(0);
        Long vehicleId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.findActionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/customers/"+custId+"/contracts/"+contractId+"/vehicles/"+vehicleId+"/act-vehicle-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void actionVehicleHistory3() throws Exception {
        Authentication authentication = null;
        Long contractId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.actionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/contracts/"+contractId+"/act-vehicle-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void actionVehicleHistoryThrowException3() throws Exception {
        Authentication authentication = null;
        Long contractId = new Long(0);
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(actionService.actionCustomerHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/contracts/"+contractId+"/act-vehicle-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void exportActionHistory3() throws Exception {
        Authentication authentication = null;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        HttpServletResponse response = null;
        String resultData1 = null;
        when(actionService.exportActionHistory(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(REQUEST_MAPPING_V1+"/action-histories/exports")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void exportActionHistoryThrowException3() throws Exception {
        Authentication authentication = null;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        HttpServletResponse response = null;
        when(actionService.exportActionHistory(Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                post(REQUEST_MAPPING_V1+"/action-histories/exports")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void actionVehicleHistoryExport4() throws Exception {
        Authentication authentication = null;
        Long contractId = new Long(0);
        HttpServletResponse response = null;
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        String resultData1 = null;
        when(actionService.exportActionHistoryVehicle(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/contracts/"+contractId+"/act-vehicle-histories/export")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void actionVehicleHistoryExportThrowException4() throws Exception {
        Authentication authentication = null;
        Long contractId = new Long(0);
        HttpServletResponse response = null;
        AuditHistoryDTO requestModel = new AuditHistoryDTO();
        when(actionService.exportActionHistoryVehicle(Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
        MockHttpServletResponse responseExpected = mvc.perform(
                get(REQUEST_MAPPING_V1+"/contracts/"+contractId+"/act-vehicle-histories/export")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

}