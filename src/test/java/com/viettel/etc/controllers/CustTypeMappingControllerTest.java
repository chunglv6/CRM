package com.viettel.etc.controllers;import com.viettel.etc.dto.CustTypeMappingDTO;import com.viettel.etc.services.CustTypeMappingService;import com.viettel.etc.utils.Constants;import com.viettel.etc.utils.exceptions.EtcException;import com.viettel.etc.xlibrary.core.constants.FunctionCommon;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.HttpStatus;import org.springframework.http.MediaType;import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.web.bind.annotation.*;import javax.validation.Valid;import com.viettel.etc.controllers.CustTypeMappingController;
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
class CustTypeMappingControllerTest {

	private MockMvc mvc;
	@Mock
	private CustTypeMappingService custTypeMappingService;
	@InjectMocks 	CustTypeMappingController CustTypeMappingController;	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(CustTypeMappingController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void find2() throws Exception {
		Authentication authentication = null;
		CustTypeMappingDTO dataParams = new CustTypeMappingDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(custTypeMappingService.find(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/cust-type-mapping"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findThrowException2() throws Exception {
		Authentication authentication = null;
		CustTypeMappingDTO dataParams = new CustTypeMappingDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(custTypeMappingService.find(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/cust-type-mapping"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findOne1() throws Exception {
		Long id = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(custTypeMappingService.findOne(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/cust-type-mapping"+"/"+id+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findOneThrowException1() throws Exception {
		Long id = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(custTypeMappingService.findOne(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/cust-type-mapping"+"/"+id+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}