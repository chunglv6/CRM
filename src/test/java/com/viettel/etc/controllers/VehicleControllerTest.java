package com.viettel.etc.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.viettel.etc.dto.*;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.VehicleController;
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
class VehicleControllerTest {

	private MockMvc mvc;
	@Mock
	private VehicleService vehicleService;
	@Mock
	private FileService fileService;
	@InjectMocks 
	VehicleController VehicleController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(VehicleController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void vehicle4() throws Exception {
		Authentication authentication = null;
		Long customerId = new Long(0);
		Long contractId = new Long(0);
		ListVehicleAssignRfidDTO assignRfidDTO = new ListVehicleAssignRfidDTO();
		ResultAssignRfidDTO resultData1 = null;
		when(vehicleService.vehicleAssign(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customers/"+customerId+"/contracts/"+contractId+"/vehicles/assign-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(assignRfidDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void vehicleThrowException4() throws Exception {
		Authentication authentication = null;
		Long customerId = new Long(0);
		Long contractId = new Long(0);
		ListVehicleAssignRfidDTO assignRfidDTO = new ListVehicleAssignRfidDTO();
		when(vehicleService.vehicleAssign(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customers/"+customerId+"/contracts/"+contractId+"/vehicles/assign-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(assignRfidDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportVehiclesAssigned2() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		String resultData1 = null;
		when(vehicleService.exportVehiclesAssigned(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/contracts/"+contractId+"/vehicles-assigned-export")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportVehiclesAssignedThrowException2() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		when(vehicleService.exportVehiclesAssigned(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/contracts/"+contractId+"/vehicles-assigned-export")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void vehicleRegistryInfo2() throws Exception {
		Authentication authenEntity = null;
		VehicleInforRegistryDTO data = new VehicleInforRegistryDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.vehicleRegistryInfo(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/vehicles-registry-info")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void vehicleRegistryInfoThrowException2() throws Exception {
		Authentication authenEntity = null;
		VehicleInforRegistryDTO data = new VehicleInforRegistryDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.vehicleRegistryInfo(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/vehicles-registry-info")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByVehicleAll3() throws Exception {
		Authentication authentication = null;
		Long vehicleId = new Long(0);
		VehicleProfilePaginationDTO requestModel = new VehicleProfilePaginationDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findProfileByVehicle(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/"+vehicleId+"/profiles/all")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByVehicleAllThrowException3() throws Exception {
		Authentication authentication = null;
		Long vehicleId = new Long(0);
		VehicleProfilePaginationDTO requestModel = new VehicleProfilePaginationDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findProfileByVehicle(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/"+vehicleId+"/profiles/all")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByVehicle3() throws Exception {
		Authentication authentication = null;
		Long vehicleId = new Long(0);
		VehicleProfileDTO requestModel = new VehicleProfileDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findProfileByVehicleId(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/"+vehicleId+"/profiles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByVehicleThrowException3() throws Exception {
		Authentication authentication = null;
		Long vehicleId = new Long(0);
		VehicleProfileDTO requestModel = new VehicleProfileDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findProfileByVehicleId(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/"+vehicleId+"/profiles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void downloadProfileByContract2() throws Exception {
		Authentication authenEntity = null;
		Long profileId = new Long(0);
		VehicleProfilePaginationDTO resultData1 = null;
		when(vehicleService.downloadProfileByContract(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/profiles/"+profileId+"/download")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	void downloadProfileByContractThrowException2() throws Exception {
		Authentication authenEntity = null;
		Long profileId = new Long(0);
		VehicleProfilePaginationDTO resultData1 = null;
		when(vehicleService.downloadProfileByContract(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/profiles/"+profileId+"/download")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	void findVehicleAssignRFID3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		VehicleByContractDTO requestModel = new VehicleByContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleByContract(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/vehicles/assigned-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleAssignRFIDThrowException3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		VehicleByContractDTO requestModel = new VehicleByContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleByContract(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/vehicles/assigned-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleNotAssignRFID3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		VehicleByContractDTO requestModel = new VehicleByContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleByContract(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/vehicles/not-assign-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleNotAssignRFIDThrowException3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		VehicleByContractDTO requestModel = new VehicleByContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleByContract(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/vehicles/not-assign-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleById2() throws Exception {
		Authentication authenEntity = null;
		Long vehicleId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleById(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/"+vehicleId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleByIdThrowException2() throws Exception {
		Authentication authenEntity = null;
		Long vehicleId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleById(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/vehicles/"+vehicleId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchVehicle2() throws Exception {
		Authentication authentication = null;
		VehicleDTO vehicleDTO = new VehicleDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.searchVehicle(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/customers/contracts/vehicles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void searchVehicleThrowException2() throws Exception {
		Authentication authentication = null;
		VehicleDTO vehicleDTO = new VehicleDTO();
		when(vehicleService.searchVehicle(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/customers/contracts/vehicles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void transferVehicle2() throws Exception {
		Authentication authentication = null;
		VehicleTransferDTO vehicleTransferDTO = new VehicleTransferDTO();
		Mockito.doNothing().when(vehicleService).transferVehicles(Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/transfer-vehicles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(vehicleTransferDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void transferVehicleThrowException2() throws Exception {
		Authentication authentication = null;
		VehicleTransferDTO vehicleTransferDTO = new VehicleTransferDTO();
		Mockito.doThrow(new EtcException(ErrorApp.DATA_EMPTY)).when(vehicleService).transferVehicles(Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/transfer-vehicles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(vehicleTransferDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void downloadVehiclesTemplate2() throws Exception {
		HttpServletResponse response = null;
		Authentication authentication = null;
		String resultData1 = null;
		when(vehicleService.downloadVehiclesTemplate(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/vehicles/download-template")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void downloadVehiclesTemplateThrowException2() throws Exception {
		HttpServletResponse response = null;
		Authentication authentication = null;
		String resultData1 = null;
		when(vehicleService.downloadVehiclesTemplate(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/vehicles/download-template")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getVehicleRegistry3() throws Exception {
		Authentication authentication = null;
		VehicleDTO dataParams = new VehicleDTO();
		String plateNumber = new String("test");
		ResponseGetInfoRegisterDTO resultData1 = new ResponseGetInfoRegisterDTO();
		resultData1.setCode(1);
		when(vehicleService.getVehicleRegistry(Mockito.any(), Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/vehicles/registry-info/"+plateNumber+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getVehicleRegistryThrowException3() throws Exception {
		Authentication authentication = null;
		VehicleDTO dataParams = new VehicleDTO();
		String plateNumber = new String("test");
		ResponseGetInfoRegisterDTO resultData1 = new ResponseGetInfoRegisterDTO();
		resultData1.setCode(1);
		when(vehicleService.getVehicleRegistry(Mockito.any(), Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/vehicles/registry-info/"+plateNumber+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getVehiclesByPlateNumber2() throws Exception {
		Authentication authentication = null;
		VehicleSearchDTO dataParams = new VehicleSearchDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.getVehiclesByPlateNumber(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/vehicles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getVehiclesByPlateNumberThrowException2() throws Exception {
		Authentication authentication = null;
		VehicleSearchDTO dataParams = new VehicleSearchDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.getVehiclesByPlateNumber(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/vehicles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void vehiclesImportTemplateExcept2() throws Exception {
		HttpServletResponse response = null;
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/vehicles/template-except-vehicle")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void vehiclesImportTemplateExceptThrowException2() throws Exception {
		HttpServletResponse response = null;
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/vehicles/template-except-vehicle")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleAssignedRFID3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		VehicleByContractDTO requestModel = new VehicleByContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleAssignedRFID(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/vehicles/find-assigned-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findVehicleAssignedRFIDThrowException3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		VehicleByContractDTO requestModel = new VehicleByContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.findVehicleAssignedRFID(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/vehicles/find-assigned-rfid")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getVehicleRegistryInfo3() throws Exception {
		Authentication authentication = null;
		VehicleDTO dataParams = new VehicleDTO();
		String plateNumber = new String("test");
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.getVehicleRegistryInfo(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/vehicles/vehicle-registry-info/"+plateNumber+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getVehicleRegistryInfoThrowException3() throws Exception {
		Authentication authentication = null;
		VehicleDTO dataParams = new VehicleDTO();
		String plateNumber = new String("test");
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(vehicleService.getVehicleRegistryInfo(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/vehicles/vehicle-registry-info/"+plateNumber+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}
