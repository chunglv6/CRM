package com.viettel.etc.controllers;
import com.viettel.etc.dto.boo.*;
import com.viettel.etc.dto.keycloak.ReqLoginDTO;
import com.viettel.etc.dto.keycloak.ResLoginDTO;
import com.viettel.etc.services.BooService;
import com.viettel.etc.services.ServiceRegistryService;
import com.viettel.etc.utils.exceptions.BooException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.BOOController;
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
class BOOControllerTest {

	private MockMvc mvc;
	@Mock
	BooService booService;
	@Mock
	private ServiceRegistryService serviceRegistryService;
	@InjectMocks 
	BOOController BOOController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(BOOController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void queryTicket2() throws Exception {
		Authentication authentication = null;
		ReqQueryTicketDTO reqQueryTicketDTO = new ReqQueryTicketDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(booService.queryTicket(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/boo/subscription")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void queryTicketThrowException2() throws Exception {
		Authentication authentication = null;
		ReqQueryTicketDTO reqQueryTicketDTO = new ReqQueryTicketDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(booService.queryTicket(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/boo/subscription")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void calculatorTicket2() throws Exception {
		Authentication authentication = null;
		ReqCalculatorTicketDTO reqCalculatorTicketDTO = new ReqCalculatorTicketDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(booService.calculatorTicket(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/boo/subscription/check")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void calculatorTicketThrowException2() throws Exception {
		Authentication authentication = null;
		ReqCalculatorTicketDTO reqCalculatorTicketDTO = new ReqCalculatorTicketDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(booService.calculatorTicket(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/boo/subscription/check")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void chargeTicket2() throws Exception {
		Authentication authentication = null;
		ReqChargeTicketDTO reqChargeTicketDTO = new ReqChargeTicketDTO();
		ResChargeTicketDTO resultData1 = null;
		when(booService.chargeTicket(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/subscription")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqChargeTicketDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void chargeTicketThrowException2() throws Exception {
		Authentication authentication = null;
		ReqChargeTicketDTO reqChargeTicketDTO = new ReqChargeTicketDTO();
		when(booService.chargeTicket(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/subscription")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqChargeTicketDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}

	@Test
	void cancelTicket2() throws Exception {
		Authentication authentication = null;
		ReqCancelTicketDTO reqCancelTicketDTO = new ReqCancelTicketDTO();
		ResCancelTicketDTO resultData1 = null;
		when(booService.cancelTicket(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/subscription/cancel")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqCancelTicketDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void cancelTicketThrowException2() throws Exception {
		Authentication authentication = null;
		ReqCancelTicketDTO reqCancelTicketDTO = new ReqCancelTicketDTO();
		ResCancelTicketDTO resultData1 = null;
		when(booService.cancelTicket(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/subscription/cancel")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqCancelTicketDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void cancelTicketResult2() throws Exception {
		Authentication authentication = null;
		ReqCancelResultDTO reqCancelResultDTO = new ReqCancelResultDTO();
		ResCancelResultDTO resultData1 = null;
		when(booService.cancelResult(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/subscription/cancel-result")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqCancelResultDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void cancelTicketResultThrowException2() throws Exception {
		Authentication authentication = null;
		ReqCancelResultDTO reqCancelResultDTO = new ReqCancelResultDTO();
		ResCancelResultDTO resultData1 = null;
		when(booService.cancelResult(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/subscription/cancel-result")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqCancelResultDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void activationCheck2() throws Exception {
		Authentication authentication = null;
		ReqActivationCheckDTO reqActivationCheckDTO = new ReqActivationCheckDTO();
		ResActivationCheckDTO resultData1 = null;
		when(booService.checkActivation(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/activation/check")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqActivationCheckDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void activationCheckThrowException2() throws Exception {
		Authentication authentication = null;
		ReqActivationCheckDTO reqActivationCheckDTO = new ReqActivationCheckDTO();
		ResActivationCheckDTO resultData1 = null;
		when(booService.checkActivation(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/activation/check")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqActivationCheckDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void onlineEventReg2() throws Exception {
		ReqOnlineEventRegDTO requestOnlineEventRegBooDTO = new ReqOnlineEventRegDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/online-event/reg")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(requestOnlineEventRegBooDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void onlineEventRegThrowException2() throws Exception {
		ReqOnlineEventRegDTO requestOnlineEventRegBooDTO = new ReqOnlineEventRegDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/online-event/reg")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(requestOnlineEventRegBooDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void onlineEventSync2() throws Exception {
		ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/online-event/sync")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqOnlineEventSyncDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void onlineEventSyncThrowException2() throws Exception {
		ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/boo/online-event/sync")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(reqOnlineEventSyncDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getListCategoryMappingBoo2() throws Exception {
		Authentication authentication = null;
		ReqMappingDTO reqMappingDTO = new ReqMappingDTO();
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/boo/mapping")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getListCategoryMappingBooThrowException2() throws Exception {
		Authentication authentication = null;
		ReqMappingDTO reqMappingDTO = new ReqMappingDTO();
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/boo/mapping")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}