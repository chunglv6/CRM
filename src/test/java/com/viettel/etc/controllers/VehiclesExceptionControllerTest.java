package com.viettel.etc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.etc.dto.ApproveVehicleExceptionDTO;
import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.VehiclesExceptionDTO;
import com.viettel.etc.services.VehiclesExceptionService;
import com.viettel.etc.services.impl.FileServiceImpl;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hamcrest.Matchers;
import org.json.JSONObject;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
class VehiclesExceptionControllerTest {

    private MockMvc mvc;
    @Mock
    private VehiclesExceptionService vehiclesExceptionService;
    @Mock
    private ServletContext servletContext;
    @Mock
    private FileServiceImpl fileService;
    @InjectMocks
    VehiclesExceptionController VehiclesExceptionController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(VehiclesExceptionController).build();
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void viewDetail2() throws Exception {
        Authentication authentication = null;
        Long exceptionListId = new Long(0);
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(vehiclesExceptionService.viewDetail(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception" + "/" + exceptionListId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void viewDetailThrowException2() throws Exception {
        Authentication authentication = null;
        Long exceptionListId = new Long(0);
        ResultSelectEntity resultData1 = new ResultSelectEntity();
        when(vehiclesExceptionService.viewDetail(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception" + "/" + exceptionListId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void downloadAttachmentFile2() throws Exception {
        Authentication authentication = null;
        Long attachmentFileId = new Long(0);
        AttachmentFileDTO resultData1 = null;
        when(vehiclesExceptionService.downloadAttachmentFile(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception" + "/download-attachment-file/" + attachmentFileId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Ignore
    void downloadAttachmentFileThrowException2() throws Exception {
        Authentication authentication = null;
        Long attachmentFileId = new Long(0);
        AttachmentFileDTO resultData1 = null;
        when(vehiclesExceptionService.downloadAttachmentFile(Mockito.any())).thenReturn(resultData1);
        MockHttpServletResponse responseExpected = mvc.perform(
                post(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception" + "/download-attachment-file/" + attachmentFileId + "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // assert result
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void getDataVehiclesException() throws Exception {
        VehiclesExceptionDTO dataParams = new VehiclesExceptionDTO();
        Authentication authentication = null;
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("error", ErrorApp.SUCCESS);
        when(vehiclesExceptionService.getDataVehiclesException(dataParams, authentication)).thenReturn(objectMap);
        MockHttpServletResponse responseExpected = mvc.perform(
                get(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void postUpdateStatus() throws Exception {
        VehiclesExceptionDTO dataParams = new VehiclesExceptionDTO();
        dataParams.setExceptionListId(1L);
        dataParams.setStatus(Constants.EXCEPTION_NEW);
        Authentication authentication = null;
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("error", ErrorApp.SUCCESS);
        when(vehiclesExceptionService.postUpdateStatus(authentication, dataParams)).thenReturn(objectMap);
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception/pre-approval")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void postApproval() throws Exception {
        VehiclesExceptionDTO dataParams = new VehiclesExceptionDTO();
        Authentication authentication = null;
        ApproveVehicleExceptionDTO jsonObject = new ApproveVehicleExceptionDTO();
        ApproveVehicleExceptionDTO.Mess mess = new ApproveVehicleExceptionDTO.Mess();
        mess.setCode(200);
        mess.setDescription("Thành công");
        jsonObject.setMess(mess);
        when(vehiclesExceptionService.postApproval(Mockito.any(), Mockito.any())).thenReturn(jsonObject);
        MockHttpServletResponse responseExpected = mvc.perform(
                put(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception/approval")
                        .accept(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(dataParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }

    @Test
    void deleteVehiclesException() throws Exception {
        Authentication authentication = null;
        Long id = 1L;
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("error", ErrorApp.SUCCESS);
        when(vehiclesExceptionService.deleteVehiclesException(authentication, id)).thenReturn(objectMap);
        MockHttpServletResponse responseExpected = mvc.perform(
                delete(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
    }
}
