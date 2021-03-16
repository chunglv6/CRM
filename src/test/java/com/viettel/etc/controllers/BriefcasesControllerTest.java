package com.viettel.etc.controllers;
import com.viettel.etc.dto.AcceptBriefcasesDTO;
import com.viettel.etc.dto.AcceptBriefcasesVehicleDTO;
import com.viettel.etc.dto.AdditionalBriefcasesDTO;
import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.services.BriefcasesService;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;
import java.net.UnknownHostException;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.BriefcasesController;
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
class BriefcasesControllerTest {

	private MockMvc mvc;
	@Mock
	BriefcasesService briefcasesService;
	@InjectMocks 
	BriefcasesController BriefcasesController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(BriefcasesController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Ignore
	void searchBriefcases2() throws Exception {
		Authentication authentication = null;
		SearchBriefcasesDTO searchBriefcasesDTO = new SearchBriefcasesDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(briefcasesService.searchBriefcases(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/briefcases")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void searchBriefcasesThrowException2() throws Exception {
		Authentication authentication = null;
		SearchBriefcasesDTO searchBriefcasesDTO = new SearchBriefcasesDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(briefcasesService.searchBriefcases(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/briefcases")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void approvalContract2() throws Exception {
		Authentication authentication = null;
		AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
		Mockito.doNothing().when(briefcasesService).approvalContract(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/briefcases/approval-contract-profiles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(acceptBriefcasesDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void approvalContractThrowException2() throws Exception {
		Authentication authentication = null;
		AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
		Mockito.doThrow(new EtcException(ErrorApp.DATA_EMPTY)).when(briefcasesService).approvalContract(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/briefcases/approval-contract-profiles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(acceptBriefcasesDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void rejectContract2() throws Exception {
		Authentication authentication = null;
		AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
		Mockito.doNothing().when(briefcasesService).rejectContract(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/briefcases/reject-contract-profiles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(acceptBriefcasesDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void rejectContractThrowException2() throws Exception {
		Authentication authentication = null;
		AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
		Mockito.doNothing().when(briefcasesService).rejectContract(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/briefcases/reject-contract-profiles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(acceptBriefcasesDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void approvalVehicle2() throws Exception {
		Authentication authentication = null;
		AcceptBriefcasesVehicleDTO acceptBriefcasesDTO = new AcceptBriefcasesVehicleDTO();
		Mockito.doNothing().when(briefcasesService).approvalVehicle(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/briefcases/approval-vehicles-profiles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(acceptBriefcasesDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void approvalVehicleThrowException2() throws Exception {
		Authentication authentication = null;
		AcceptBriefcasesVehicleDTO acceptBriefcasesDTO = new AcceptBriefcasesVehicleDTO();
		Mockito.doNothing().when(briefcasesService).approvalVehicle(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/briefcases/approval-vehicles-profiles")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(acceptBriefcasesDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}