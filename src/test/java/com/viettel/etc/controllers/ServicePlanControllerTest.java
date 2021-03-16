package com.viettel.etc.controllers;
import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.services.AttachmentFileService;
import com.viettel.etc.services.ImportServicePlanService;
import com.viettel.etc.services.ServicePlanService;
import com.viettel.etc.services.ServicePlanTypeService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.ServicePlanController;
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
class ServicePlanControllerTest {

	private MockMvc mvc;
	@Mock
	private ServicePlanService servicePlanService;
	@Mock
	private ServicePlanTypeService servicePlanTypeService;
	@Mock
	private AttachmentFileService attachmentFileService;
	@Mock
	private ImportServicePlanService importServicePlanService;
	@InjectMocks 
	ServicePlanController ServicePlanController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(ServicePlanController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void deleteTicketPrices2() throws Exception {
		Authentication authentication = null;
		Long servicePlanId = new Long(0);
		Mockito.doNothing().when(servicePlanService).deleteTicketPrices(Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(REQUEST_MAPPING_V1+"/ticket-prices/"+servicePlanId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void deleteTicketPricesThrowException2() throws Exception {
		Authentication authentication = null;
		Long servicePlanId = new Long(0);
		Mockito.doThrow(new EtcException("common.validate.data.empty")).when(servicePlanService).deleteTicketPrices(Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(REQUEST_MAPPING_V1+"/ticket-prices/"+servicePlanId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchServicePlan2() throws Exception {
		Authentication authentication = null;
		PriceListDTO dataParams = new PriceListDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.searchServicePlan(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/service-plans")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void searchServicePlanThrowException2() throws Exception {
		Authentication authentication = null;
		PriceListDTO dataParams = new PriceListDTO();
		when(servicePlanService.searchServicePlan(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/service-plans")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchTicketPrices2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		ResultSelectEntity resultData1 = null;
		when(servicePlanService.searchTicketPrices(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchTicketPricesThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		ResultSelectEntity resultData1 = null;
		when(servicePlanService.searchTicketPrices(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void approvalTicketPrices2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
		List resultData1 = null;
		when(servicePlanService.approvalTicketPrices(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/ticket-prices/approval")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(servicePlanDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void approvalTicketPricesThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
		when(servicePlanService.approvalTicketPrices(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/ticket-prices/approval")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(servicePlanDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

//	@Test
//	void getFee2() throws Exception {
//		Authentication authentication = null;
//		ServicePlanDTO dataParams = new ServicePlanDTO();
//		ResultSelectEntity resultData1 = new ResultSelectEntity();
//		when(servicePlanService.getFee(Mockito.any(), null)).thenReturn(resultData1);
//		MockHttpServletResponse responseExpected = mvc.perform(
//			get(REQUEST_MAPPING_V1+"/service-plans/fee")
//			.accept(MediaType.APPLICATION_JSON)
//			.contentType(MediaType.APPLICATION_JSON))
//			.andReturn().getResponse();
//
//		// assert result
//		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
//	}

//	@Test
//	void getFeeThrowException2() throws Exception {
//		Authentication authentication = null;
//		ServicePlanDTO dataParams = new ServicePlanDTO();
//		ResultSelectEntity resultData1 = new ResultSelectEntity();
//		when(servicePlanService.getFee(Mockito.any(), null)).thenReturn(resultData1);
//		MockHttpServletResponse responseExpected = mvc.perform(
//			get(REQUEST_MAPPING_V1+"/service-plans/fee")
//			.accept(MediaType.APPLICATION_JSON)
//			.contentType(MediaType.APPLICATION_JSON))
//			.andReturn().getResponse();
//
//		// assert result
//		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
//	}

	@Test
	void getFeeMobile2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.getFeeMobile(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/service-plans/fee")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getFeeMobileThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.getFeeMobile(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/service-plans/fee")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getTicketType1() throws Exception {
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices/type")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getTicketTypeThrowException1() throws Exception {
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices/type")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void rejectTicketPrices2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
		List resultData1 = null;
		when(servicePlanService.rejectTicketPrices(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/ticket-prices/reject")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(servicePlanDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void rejectTicketPricesThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
		List resultData1 = null;
		when(servicePlanService.rejectTicketPrices(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/ticket-prices/reject")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(servicePlanDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void cancelTicketPrices2() throws Exception {
		Authentication authentication = null;
		Long servicePlanId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.cancelTicketPrice(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/ticket-prices/"+servicePlanId+"/cancel")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void cancelTicketPricesThrowException2() throws Exception {
		Authentication authentication = null;
		Long servicePlanId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.cancelTicketPrice(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+"/ticket-prices/"+servicePlanId+"/cancel")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchMinFeeTicketPrices2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.getMinFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices/min-fee")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchMinFeeTicketPricesThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.getMinFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices/min-fee")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportServicePlan2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		String resultData1 = null;
		when(servicePlanService.exportServicePlan(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/ticket-prices/exports")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportServicePlanThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		when(servicePlanService.exportServicePlan(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/ticket-prices/exports")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void downloadServicePlanTemplate2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		String resultData1 = null;
		when(servicePlanService.downloadServicePlanTemplate(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/ticket-prices/download-template")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void downloadServicePlanTemplateThrowException2() throws Exception {
		Authentication authentication = null;
		ServicePlanDTO dataParams = new ServicePlanDTO();
		when(servicePlanService.downloadServicePlanTemplate(Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/ticket-prices/download-template")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findTicketPricesById2() throws Exception {
		Authentication authentication = null;
		Long servicePlanId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.findTicketPricesById(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices/"+servicePlanId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findTicketPricesByIdThrowException2() throws Exception {
		Authentication authentication = null;
		Long servicePlanId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(servicePlanService.findTicketPricesById(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ticket-prices/"+servicePlanId+"")
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
		AttachmentFileDTO resultData1 = null;
		when(attachmentFileService.getFileByAttachId(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/ticket-prices/profiles/"+profileId+"/download")
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
		AttachmentFileDTO resultData1 = null;
		when(attachmentFileService.getFileByAttachId(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/ticket-prices/profiles/"+profileId+"/download")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	void getChangeFee2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ServiceFeeDTO resultData1 = null;
		when(servicePlanService.getChangeFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/fees")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getChangeFeeThrowException2() throws Exception {
		Authentication authentication = null;
		ServiceFeeDTO dataParams = new ServiceFeeDTO();
		ServiceFeeDTO resultData1 = null;
		when(servicePlanService.getChangeFee(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/fees")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}
