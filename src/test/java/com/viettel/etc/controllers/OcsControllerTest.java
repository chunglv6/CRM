package com.viettel.etc.controllers;
import com.viettel.etc.services.OCSService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.OcsController;
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
class OcsControllerTest {

	private MockMvc mvc;
	@Mock
	OCSService ocsService;
	@InjectMocks 
	OcsController OcsController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(OcsController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Ignore
	void queryVehicle2() throws Exception {
		String epc = new String("test");
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ocs/queryVehicle")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void queryVehicleThrowException2() throws Exception {
		String epc = new String("test");
		Authentication authentication = null;
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/ocs/queryVehicle")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

}
