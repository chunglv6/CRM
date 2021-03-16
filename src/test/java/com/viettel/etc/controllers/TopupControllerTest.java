package com.viettel.etc.controllers;
import com.viettel.etc.dto.TopupDTO;
import com.viettel.etc.dto.TopupExportDTO;
import com.viettel.etc.repositories.tables.entities.TopupEtcEntity;
import com.viettel.etc.services.TopupService;
import com.viettel.etc.services.tables.TopupEtcServiceJPA;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.TopupController;
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
class TopupControllerTest {

	private MockMvc mvc;
	@Mock
	TopupEtcServiceJPA topupEtcServiceJPA;
	@Mock
	TopupService topupService;
	@InjectMocks 
	TopupController TopupController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(TopupController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Ignore
	void getTopupHistory2() throws Exception {
		Authentication authentication = null;
		Long contractId = new Long(0);
		List resultData1 = null;
		when(topupEtcServiceJPA.findAllByContractIdDesc(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/topup-etc/cash-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void getTopupHistoryThrowException2() throws Exception {
		Authentication authentication = null;
		Long contractId = new Long(0);
		List resultData1 = null;
		when(topupEtcServiceJPA.findAllByContractIdDesc(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/topup-etc/cash-histories")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

}