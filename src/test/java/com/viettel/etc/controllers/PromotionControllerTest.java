package com.viettel.etc.controllers;
import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.services.PromotionService;
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
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import com.viettel.etc.controllers.PromotionController;
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
class PromotionControllerTest {

	private MockMvc mvc;
	@Mock
	private PromotionService promotionService;
	@InjectMocks 
	PromotionController PromotionController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(PromotionController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void getPromotions2() throws Exception {
		Authentication authentication = null;
		PromotionDTO dataParams = new PromotionDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.getPromotions(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getPromotionsThrowException2() throws Exception {
		Authentication authentication = null;
		PromotionDTO dataParams = new PromotionDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.getPromotions(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/"+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getPromotionDetail2() throws Exception {
		Authentication authentication = null;
		Long promotionId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.getDetailPromotion(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/"+promotionId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getPromotionDetailThrowException2() throws Exception {
		Authentication authentication = null;
		Long promotionId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.getDetailPromotion(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/"+promotionId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void deletePromotion2() throws Exception {
		Authentication authentication = null;
		Long promotionId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.deletePromotion(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/"+promotionId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void deletePromotionThrowException2() throws Exception {
		Authentication authentication = null;
		Long promotionId = new Long(0);
		when(promotionService.deletePromotion(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/"+promotionId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void deleteAttachment1() throws Exception {
		Long attachmentId = new Long(0);
		Mockito.doNothing().when(promotionService).deleteAttachment(Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/attachments/"+attachmentId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void deleteAttachmentThrowException1() throws Exception {
		Long attachmentId = new Long(0);
		Mockito.doNothing().when(promotionService).deleteAttachment(Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			delete(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/attachments/"+attachmentId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void approvePromotion2() throws Exception {
		Authentication authentication = null;
		PromotionDTO params = new PromotionDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.approvePromotion(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/approves")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(params))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void approvePromotionThrowException2() throws Exception {
		Authentication authentication = null;
		PromotionDTO params = new PromotionDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.approvePromotion(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			put(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/approves")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(params))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportPromotion3() throws Exception {
		Authentication authentication = null;
		PromotionDTO dataParams = new PromotionDTO();
		HttpServletResponse response = null;
		String resultData1 = null;
		when(promotionService.exportPromotion(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/exports")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void exportPromotionThrowException3() throws Exception {
		Authentication authentication = null;
		PromotionDTO dataParams = new PromotionDTO();
		HttpServletResponse response = null;
		when(promotionService.exportPromotion(Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/exports")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getPromotionAssigns2() throws Exception {
		Authentication authentication = null;
		PromotionAssignDTO dataParams = new PromotionAssignDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.getPromotionAssigns(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/assigns")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getPromotionAssignsThrowException2() throws Exception {
		Authentication authentication = null;
		PromotionAssignDTO dataParams = new PromotionAssignDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.getPromotionAssigns(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/assigns")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void addPromotionAssign2() throws Exception {
		Authentication authentication = null;
		PromotionAssignDTO dataParams = new PromotionAssignDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.addPromotionAssign(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/assigns")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void addPromotionAssignThrowException2() throws Exception {
		Authentication authentication = null;
		PromotionAssignDTO dataParams = new PromotionAssignDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(promotionService.addPromotionAssign(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1 + "/promotions"+"/assigns")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

}