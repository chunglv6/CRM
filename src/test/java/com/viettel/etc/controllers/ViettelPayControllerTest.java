package com.viettel.etc.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.viettel.etc.dto.viettelpay.*;
import com.viettel.etc.services.ViettelPayService;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;
import java.security.SignatureException;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.ViettelPayController;
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
class ViettelPayControllerTest {

	private MockMvc mvc;
	@Mock
	ViettelPayService viettelPayService;
	@InjectMocks 
	ViettelPayController ViettelPayController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(ViettelPayController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void findContractByContractOrPlate3() throws Exception {
		RequestBaseViettelDTO data = new RequestBaseViettelDTO();
		BindingResult bindingResult = null;
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/contract/info")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findContractByContractOrPlateThrowException3() throws Exception {
		RequestBaseViettelDTO data = new RequestBaseViettelDTO();
		BindingResult bindingResult = null;
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/contract/info")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultRegisterViettelPay3() throws Exception {
		RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
		Authentication authentication = null;
		BindingResult bindingResult = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/register-confirm")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultRegisterViettelPayThrowException3() throws Exception {
		RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
		Authentication authentication = null;
		BindingResult bindingResult = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/register-confirm")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultUnRegisterViettelPay3() throws Exception {
		RequestContractPaymentDTO data = new RequestContractPaymentDTO();
		BindingResult bindingResult = null;
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/unregister-confirm")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultUnRegisterViettelPayThrowException3() throws Exception {
		RequestContractPaymentDTO data = new RequestContractPaymentDTO();
		BindingResult bindingResult = null;
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/unregister-confirm")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void changeMoneySourceViettelPay3() throws Exception {
		RequestConfirmChangeMoneySourceDTO data = new RequestConfirmChangeMoneySourceDTO();
		Authentication authentication = null;
		BindingResult bindingResult = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/change-source")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void changeMoneySourceViettelPayThrowException3() throws Exception {
		RequestConfirmChangeMoneySourceDTO data = new RequestConfirmChangeMoneySourceDTO();
		Authentication authentication = null;
		BindingResult bindingResult = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/change-source")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void getInfoTicketPurchaseAndExtendedViaSDK2() throws Exception {
		String billingCode = new String("test");
		Authentication authentication = null;
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(viettelPayService.getInfoTicketPurchaseAndExtendedViaSDK(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/check-info/ticket/purchase/extended")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void getInfoTicketPurchaseAndExtendedViaSDKThrowException2() throws Exception {
		String billingCode = new String("test");
		Authentication authentication = null;
		when(viettelPayService.getInfoTicketPurchaseAndExtendedViaSDK(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1 + "/viettelpay"+"/check-info/ticket/purchase/extended")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void confirmResultTicketPurchaseAutoRenew2() throws Exception {
		RequestRenewTicketPricesDTO data = new RequestRenewTicketPricesDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/init/auto-renew")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultTicketPurchaseAutoRenewThrowException2() throws Exception {
		RequestRenewTicketPricesDTO data = new RequestRenewTicketPricesDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/init/auto-renew")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultCancelTicketPurchaseAutoRenew2() throws Exception {
		RequestRenewTicketPricesDTO data = new RequestRenewTicketPricesDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/cancel/auto-renew")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void confirmResultCancelTicketPurchaseAutoRenewThrowException2() throws Exception {
		RequestRenewTicketPricesDTO data = new RequestRenewTicketPricesDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/cancel/auto-renew")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void responseDataAddMoneyToAccount2() throws Exception {
		ViettelPayRequestDTO data = new ViettelPayRequestDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/add-money/response-result")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void responseDataAddMoneyToAccountThrowException2() throws Exception {
		ViettelPayRequestDTO data = new ViettelPayRequestDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/add-money/response-result")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void verifyDataFromViettelPay2() throws Exception {
		ViettelPayRequestDTO data = new ViettelPayRequestDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/add-money/verify-data")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void verifyDataFromViettelPayThrowException2() throws Exception {
		ViettelPayRequestDTO data = new ViettelPayRequestDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1 + "/viettelpay"+"/result/ticket-purchase/add-money/verify-data")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(data))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}
