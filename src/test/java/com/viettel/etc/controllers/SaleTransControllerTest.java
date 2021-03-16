package com.viettel.etc.controllers;
import com.viettel.etc.dto.SaleTransDTO;
import com.viettel.etc.dto.SaleTransVehicleOwnerAppDTO;
import com.viettel.etc.services.SaleTransService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.viettel.etc.controllers.SaleTransController;
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
class SaleTransControllerTest {

	private MockMvc mvc;
	@Mock
	private SaleTransService saleTransService;
	@InjectMocks 
	SaleTransController SaleTransController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(SaleTransController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void findOtherTransactionHistories3() throws Exception {
		Authentication authentication = null;
		Long contractId = new Long(0);
		SaleTransDTO saleTransDTO = new SaleTransDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransService.findOtherTransactionHistories(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/contracts/"+contractId+"/other-transaction-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findOtherTransactionHistoriesThrowException3() throws Exception {
		Authentication authentication = null;
		Long contractId = new Long(0);
		SaleTransDTO saleTransDTO = new SaleTransDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransService.findOtherTransactionHistories(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/contracts/"+contractId+"/other-transaction-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportOtherTransactionHistories4() throws Exception {
		Authentication authentication = null;
		Long contractId = new Long(0);
		HttpServletResponse response = null;
		SaleTransDTO saleTransDTO = new SaleTransDTO();
		String resultData1 = null;
		when(saleTransService.exportOtherTransactionHistories(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/contracts/"+contractId+"/other-transaction-histories/export")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportOtherTransactionHistoriesThrowException4() throws Exception {
		Authentication authentication = null;
		Long contractId = new Long(0);
		HttpServletResponse response = null;
		SaleTransDTO saleTransDTO = new SaleTransDTO();
		String resultData1 = null;
		when(saleTransService.exportOtherTransactionHistories(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/contracts/"+contractId+"/other-transaction-histories/export")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getServiceFees2() throws Exception {
		Authentication authentication = null;
		SaleTransDTO dataParams = new SaleTransDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransService.getServiceFees(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/services-fees")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getServiceFeesThrowException2() throws Exception {
		Authentication authentication = null;
		SaleTransDTO dataParams = new SaleTransDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransService.getServiceFees(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/services-fees")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}