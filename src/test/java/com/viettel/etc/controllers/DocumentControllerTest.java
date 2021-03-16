package com.viettel.etc.controllers;import com.viettel.etc.dto.DocumentDTO;import com.viettel.etc.services.DocumentService;import com.viettel.etc.utils.Constants;import com.viettel.etc.xlibrary.core.constants.FunctionCommon;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.HttpStatus;import org.springframework.http.MediaType;import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.web.bind.annotation.GetMapping;import org.springframework.web.bind.annotation.PathVariable;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;import com.viettel.etc.controllers.DocumentController;
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
class DocumentControllerTest {

	private MockMvc mvc;
	@Mock
	private DocumentService documentService;
	@InjectMocks 	DocumentController DocumentController;	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(DocumentController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void getDocumentType2() throws Exception {
		Authentication authentication = null;
		DocumentDTO dataParams = new DocumentDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(documentService.getDocumentType(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/document-types")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDocumentTypeThrowException2() throws Exception {
		Authentication authentication = null;
		DocumentDTO dataParams = new DocumentDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(documentService.getDocumentType(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/document-types")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDocumentByCustTypeId3() throws Exception {
		Authentication authentication = null;
		DocumentDTO dataParams = new DocumentDTO();
		Long custTypeId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(documentService.getDocumentByCustTypeId(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/document-types/"+custTypeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDocumentByCustTypeIdThrowException3() throws Exception {
		Authentication authentication = null;
		DocumentDTO dataParams = new DocumentDTO();
		Long custTypeId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(documentService.getDocumentByCustTypeId(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(Constants.REQUEST_MAPPING_V1+"/document-types/"+custTypeId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}