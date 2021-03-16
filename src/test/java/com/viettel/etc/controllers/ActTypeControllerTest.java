package com.viettel.etc.controllers;
import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.services.ActTypeService;
import com.viettel.etc.utils.Constants;
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
import com.viettel.etc.controllers.ActTypeController;
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
class ActTypeControllerTest {

	private MockMvc mvc;
	@Mock
	private ActTypeService actTypeService;
	@InjectMocks 
	ActTypeController ActTypeController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(ActTypeController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void find2() throws Exception {
		Authentication authentication = null;
		ActTypeDTO dataParams = new ActTypeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(actTypeService.find(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/act-type"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findThrowException2() throws Exception {
		Authentication authentication = null;
		ActTypeDTO dataParams = new ActTypeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(actTypeService.find(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/act-type"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void insert2() throws Exception {
		Authentication authentication = null;
		ActTypeDTO dataParams = new ActTypeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(actTypeService.insert(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/act-type"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void insertThrowException2() throws Exception {
		Authentication authentication = null;
		ActTypeDTO dataParams = new ActTypeDTO();
		when(actTypeService.insert(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/act-type"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void update3() throws Exception {
		Authentication authentication = null;
		ActTypeDTO dataParams = new ActTypeDTO();
		Long actTypeId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(actTypeService.update(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(Constants.REQUEST_MAPPING_V1 + "/act-type"+"/"+actTypeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void updateThrowException3() throws Exception {
		Authentication authentication = null;
		ActTypeDTO dataParams = new ActTypeDTO();
		Long actTypeId = new Long(0);
		when(actTypeService.update(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			put(Constants.REQUEST_MAPPING_V1 + "/act-type"+"/"+actTypeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}