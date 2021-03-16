package com.viettel.etc.controllers;
import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.services.ServiceFeeService;
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
import javax.validation.Valid;
import com.viettel.etc.controllers.ServiceFeeController;
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
class ServiceFeeControllerTest {

	private MockMvc mvc;
	@Mock
	private ServiceFeeService serviceFeeService;
	@InjectMocks 
	ServiceFeeController ServiceFeeController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(ServiceFeeController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void getServiceFee2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.getServiceFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getServiceFeeThrowException2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.getServiceFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDetailServiceFee2() throws Exception {
		Authentication authentication = null;
		Long serviceFeeId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.getDetailServiceFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+serviceFeeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDetailServiceFeeThrowException2() throws Exception {
		Authentication authentication = null;
		Long serviceFeeId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.getDetailServiceFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+serviceFeeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDetailServiceFeeByReason2() throws Exception {
		Authentication authentication = null;
		Long actReasonId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.getDetailServiceFeeByReason(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/act-reasons/"+actReasonId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDetailServiceFeeByReasonThrowException2() throws Exception {
		Authentication authentication = null;
		Long actReasonId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.getDetailServiceFeeByReason(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/act-reasons/"+actReasonId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void addServiceFee2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.addServiceFee(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void addServiceFeeThrowException2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		when(serviceFeeService.addServiceFee(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void deleteServiceFee2() throws Exception {
		Authentication authentication = null;
		Long serviceFeeId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.deleteServiceFee(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+serviceFeeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void deleteServiceFeeThrowException2() throws Exception {
		Authentication authentication = null;
		Long serviceFeeId = new Long(0);
		when(serviceFeeService.deleteServiceFee(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/"+serviceFeeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void approveServiceFee2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.approveServiceFee(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/approves")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void approveServiceFeeThrowException2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(serviceFeeService.approveServiceFee(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(Constants.REQUEST_MAPPING_V1 + "/service-charges"+"/approves")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}