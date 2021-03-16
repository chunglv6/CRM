package com.viettel.etc.controllers;import com.viettel.etc.dto.SaleTransDetailDTO;import com.viettel.etc.dto.SaleTransDetailVehicleOwnerAppDTO;import com.viettel.etc.services.SaleTransDetailService;import com.viettel.etc.utils.Constants;import com.viettel.etc.utils.FnCommon;import com.viettel.etc.xlibrary.core.constants.FunctionCommon;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.HttpStatus;import org.springframework.http.MediaType;import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.web.bind.annotation.PathVariable;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RequestMethod;import org.springframework.web.bind.annotation.RestController;import javax.servlet.http.HttpServletResponse;import com.viettel.etc.controllers.SaleTransDetailController;
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
class SaleTransDetailControllerTest {

	private MockMvc mvc;
	@Mock
	private SaleTransDetailService saleTransDetailService;
	@InjectMocks 	SaleTransDetailController SaleTransDetailController;	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(SaleTransDetailController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void searchTicketPurchaseHistories3() throws Exception {
		Authentication authentication = null;
		Long vehicleId = new Long(0);
		SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransDetailService.searchTicketPurchaseHistories(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/vehicles/"+vehicleId+"/ticket-purchase-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchTicketPurchaseHistoriesThrowException3() throws Exception {
		Authentication authentication = null;
		Long vehicleId = new Long(0);
		SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransDetailService.searchTicketPurchaseHistories(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/vehicles/"+vehicleId+"/ticket-purchase-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void vehicleOwnerAppTicketPurchaseHistories2() throws Exception {
		Authentication authentication = null;
		SaleTransDetailVehicleOwnerAppDTO saleTransDetailDTO = new SaleTransDetailVehicleOwnerAppDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransDetailService.vehicleOwnerAppTicketPurchaseHistories(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+Constants.MOBILE + "/ticket-purchase-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void vehicleOwnerAppTicketPurchaseHistoriesThrowException2() throws Exception {
		Authentication authentication = null;
		SaleTransDetailVehicleOwnerAppDTO saleTransDetailDTO = new SaleTransDetailVehicleOwnerAppDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(saleTransDetailService.vehicleOwnerAppTicketPurchaseHistories(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+Constants.MOBILE + "/ticket-purchase-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}