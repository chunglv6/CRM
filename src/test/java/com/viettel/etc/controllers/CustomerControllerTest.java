package com.viettel.etc.controllers;
import com.google.zxing.WriterException;
import com.viettel.etc.dto.*;
import com.viettel.etc.services.ContractService;
import com.viettel.etc.services.CustomerService;
import com.viettel.etc.services.QrCodeService;
import com.viettel.etc.utils.*;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.CustomerController;
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
class CustomerControllerTest {

	private MockMvc mvc;
	@Mock
	CustomerService customerService;
	@Mock
	ContractService contractService;
	@Mock
	QrCodeService qrCodeService;
	@Mock
	VehicleService vehicleService;
	@InjectMocks 
	CustomerController CustomerController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(CustomerController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Ignore
	void addCustomerEnterprise2() throws Exception {
		Authentication authentication = null;
		CustomerDTO dataParams = new CustomerDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.addCustomerEnterprise(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customer-enterprise")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void addCustomerEnterpriseThrowException2() throws Exception {
		Authentication authentication = null;
		CustomerDTO dataParams = new CustomerDTO();
		when(customerService.addCustomerEnterprise(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customer-enterprise")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(dataParams))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void saveCustomer2() throws Exception {
		Authentication authentication = null;
		CustomerDTO customer = new CustomerDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.addCustomer(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customer-personal")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(customer))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Ignore
	void saveCustomerThrowException2() throws Exception {
		Authentication authentication = null;
		CustomerDTO customer = new CustomerDTO();
		when(customerService.addCustomer(Mockito.any(),Mockito.any())).thenThrow(new EtcException(ErrorApp.DATA_EMPTY));
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customer-personal")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(customer))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void updateUserData2() throws Exception {
		Authentication authentication = null;
		UsersDTO usersDTO = new UsersDTO();
//		Mockito.doNothing().when(customerService).updateCustomer(Mockito.any(),Mockito.any(),Mockito.any());
//		Mockito.doNothing().when(customerService).updateCustomer(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+Constants.MOBILE + "/customers")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(usersDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void updateUserDataThrowException2() throws Exception {
		Authentication authentication = null;
		UsersDTO usersDTO = new UsersDTO();
//		Mockito.doThrow(new EtcException(ErrorApp.DATA_EMPTY)).when(customerService).updateCustomer(Mockito.any(),Mockito.any(),Mockito.any());
//		Mockito.doThrow(new EtcException(ErrorApp.DATA_EMPTY)).when(customerService).updateCustomer(Mockito.any(),Mockito.any(),Mockito.any());
		MockHttpServletResponse responseExpected = mvc.perform(
			put(REQUEST_MAPPING_V1+Constants.MOBILE + "/customers")
			.accept(MediaType.APPLICATION_JSON)
			.content((new ObjectMapper()).writeValueAsString(usersDTO))
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void findCustomerByDocumentAndPhone2() throws Exception {
		Authentication authentication = null;
		CustomerSearchDTO dataParams = new CustomerSearchDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.findCustomerByDocumentAndPhone(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customer-info")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NO_CONTENT.value()));
	}

	@Ignore
	void findCustomerByDocumentAndPhoneThrowException2() throws Exception {
		Authentication authentication = null;
		CustomerSearchDTO dataParams = new CustomerSearchDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.findCustomerByDocumentAndPhone(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customer-info")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	void searchTreeInfo2() throws Exception {
		Authentication authentication = null;
		SearchInfoDTO dataParams = new SearchInfoDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.searchTreeInfo(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void searchTreeInfoThrowException2() throws Exception {
		Authentication authentication = null;
		SearchInfoDTO dataParams = new SearchInfoDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.searchTreeInfo(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getCustomerById2() throws Exception {
		Authentication authenEntity = null;
		Long custId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.findCustomerById(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/"+custId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getCustomerByIdThrowException2() throws Exception {
		Authentication authenEntity = null;
		Long custId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(customerService.findCustomerById(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/"+custId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

}