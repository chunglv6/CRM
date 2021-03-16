package com.viettel.etc.controllers;
import com.viettel.etc.dto.*;
import com.viettel.etc.services.ContractService;
import com.viettel.etc.services.FileService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import com.viettel.etc.xlibrary.core.entities.UserSystemEntity;
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
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Date;
import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;
import com.viettel.etc.controllers.ContractController;
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
class ContractControllerTest {

	private MockMvc mvc;
	@Mock
	private ContractService contractService;
	@Mock
	private FileService fileService;
	@InjectMocks 
	ContractController ContractController;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(ContractController).build();
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void getDataContract2() throws Exception {
		Authentication authentication = null;
		SearchContractDTO searchContractDTO = new SearchContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.searchContract(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDataContractThrowException2() throws Exception {
		Authentication authentication = null;
		SearchContractDTO searchContractDTO = new SearchContractDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.searchContract(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findContractById2() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findContractById(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findContractByIdThrowException2() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findContractById(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDataUserContract1() throws Exception {
		Authentication authentication = null;
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.getDataUserContract(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/contracts")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void getDataUserContractThrowException1() throws Exception {
		Authentication authentication = null;
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.getDataUserContract(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+Constants.MOBILE + "/contracts")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findContractByCustomer3() throws Exception {
		Authentication authentication = null;
		Integer custId = new Integer(0);
		SearchContractByCustomerDTO requestModel = new SearchContractByCustomerDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findContractByCustomer(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/"+custId+"/contracts")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findContractByCustomerThrowException3() throws Exception {
		Authentication authentication = null;
		Integer custId = new Integer(0);
		SearchContractByCustomerDTO requestModel = new SearchContractByCustomerDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findContractByCustomer(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/"+custId+"/contracts")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByContractAll3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		ContractProfileDTO requestModel = new ContractProfileDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findProfileByContract(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/profiles/all")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByContractAllThrowException3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		ContractProfileDTO requestModel = new ContractProfileDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findProfileByContract(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/profiles/all")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByContract3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		ContractProfileDTO requestModel = new ContractProfileDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findProfileByContractId(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/profiles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Test
	void findProfileByContractThrowException3() throws Exception {
		Authentication authenEntity = null;
		Long contractId = new Long(0);
		ContractProfileDTO requestModel = new ContractProfileDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findProfileByContractId(Mockito.any(),Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/customers/contracts/"+contractId+"/profiles")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.OK.value()));
	}

	@Ignore
	void downloadProfileByContract2() throws Exception {
		UserSystemEntity authenEntity = new UserSystemEntity();
		Integer profileId = new Integer(0);
		ContractProfileDTO resultData1 = null;
		when(contractService.downloadProfileByContract(Mockito.any())).thenReturn(resultData1);
		byte[] resultData2 = null;
		when(fileService.getFile(Mockito.any())).thenReturn(resultData2);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customers/contracts/profiles/"+profileId+"/download")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Ignore
	void downloadProfileByContractThrowException2() throws Exception {
		UserSystemEntity authenEntity = new UserSystemEntity();
		Integer profileId = new Integer(0);
		ContractProfileDTO resultData1 = null;
		when(contractService.downloadProfileByContract(Mockito.any())).thenReturn(resultData1);
		byte[] resultData2 = null;
		when(fileService.getFile(Mockito.any())).thenReturn(resultData2);
		MockHttpServletResponse responseExpected = mvc.perform(
			post(REQUEST_MAPPING_V1+"/customers/contracts/profiles/"+profileId+"/download")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Ignore
	void findByPlateNumberAndContractNo2() throws Exception {
		Authentication authentication = null;
		ContractSearchDTO dataParams = new ContractSearchDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findByPlateNumberAndContractNo(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/contracts-info")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NO_CONTENT.value()));
	}

	@Ignore
	void findByPlateNumberAndContractNoThrowException2() throws Exception {
		Authentication authentication = null;
		ContractSearchDTO dataParams = new ContractSearchDTO();
		ResultSelectEntity resultData1 = new ResultSelectEntity();
		when(contractService.findByPlateNumberAndContractNo(Mockito.any())).thenReturn(resultData1);
		MockHttpServletResponse responseExpected = mvc.perform(
			get(REQUEST_MAPPING_V1+"/contracts-info")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();

		// assert result
		assertThat(responseExpected.getStatus(), Matchers.equalTo(HttpStatus.NO_CONTENT.value()));
	}

}