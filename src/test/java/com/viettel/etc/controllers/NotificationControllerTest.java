package com.viettel.etc.controllers;import com.viettel.etc.dto.notification.NotificationRequestDTO;import com.viettel.etc.dto.notification.SubscriptionRequestDTO;import com.viettel.etc.services.NotificationService;import com.viettel.etc.utils.Constants;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.HttpStatus;import org.springframework.http.MediaType;import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.PostMapping;import org.springframework.web.bind.annotation.RequestBody;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;import com.viettel.etc.controllers.NotificationController;
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
class NotificationControllerTest {

	private MockMvc mvc;
	@Mock
	NotificationService notificationService;
	@InjectMocks 	NotificationController NotificationController;	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(NotificationController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void sendPnsToDevice1() throws Exception {
		NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1+"/token")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(notificationRequestDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void sendPnsToDeviceThrowException1() throws Exception {
		NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1+"/token")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(notificationRequestDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void subscribeToTopic1() throws Exception {
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		Mockito.doNothing().when(notificationService).subscribeToTopic(Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1+"/subscribe")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(subscriptionRequestDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void subscribeToTopicThrowException1() throws Exception {
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		Mockito.doNothing().when(notificationService).subscribeToTopic(Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1+"/subscribe")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(subscriptionRequestDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void unsubscribeFromTopic1() throws Exception {
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		Mockito.doNothing().when(notificationService).unsubscribeFromTopic(Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1+"/unsubscribe")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void unsubscribeFromTopicThrowException1() throws Exception {
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		Mockito.doNothing().when(notificationService).unsubscribeFromTopic(Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			post(Constants.REQUEST_MAPPING_V1+"/unsubscribe")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}