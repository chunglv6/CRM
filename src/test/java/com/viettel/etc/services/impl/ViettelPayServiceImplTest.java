package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.dto.viettelpay.*;
import com.viettel.etc.repositories.SaleOrderDetailRepository;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.repositories.ViettelPayRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.SaleOrderServiceJPA;
import com.viettel.etc.services.tables.SaleTransDetailServiceJPA;
import com.viettel.etc.services.tables.SaleTransServiceJPA;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.security.SignatureException;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ViettelPayServiceImplTest {

    String responseConfirmSuccess;
    String responseConfirmFail;
    String responseCancelConfirmSuccess;
    String responseCancelConfirmFail;
    String dataLinkInitSuccess;
    String dataLinkInitSuccessNoData;
    String dataDoubleTransaction;
    String responseCancelInitSuccess;
    String responseCancelInitFail;
    String responseGetSourceSuccess;
    String responseGetSourceFail;
    @Mock
    private ViettelPayRepository mockVtPayRepository;
    @Mock
    private ContractPaymentService mockContractPaymentService;
    @Mock
    private OCSService mockOcsService;
    @Mock
    private CategoriesService mockCategoriesService;
    @Mock
    private AttachmentFileRepositoryJPA mockAttachmentFileRepository;
    @Mock
    private ContractPaymentRepositoryJPA mockContractPaymentRepositoryJPA;
    @Mock
    private ContractRepositoryJPA mockContractRepositoryJPA;
    @Mock
    private CustomerRepositoryJPA mockCustomerRepositoryJPA;
    @Mock
    private WsAuditRepositoryJPA mockWsAuditRepositoryJPA;
    @Mock
    private SaleTransDetailServiceJPA mockSaleTransDetailServiceJPA;
    @Mock
    private SaleTransServiceJPA mockSaleTransServiceJPA;
    @Mock
    private StageService mockStageService;
    @Mock
    private SaleOrderRepositoryJPA mockSaleOrderRepositoryJPA;
    @Mock
    private SaleOrderServiceJPA mockSaleOrderServiceJPA;
    @Mock
    private SaleOrderDetailRepositoryJPA mockSaleOrderDetailRepositoryJPA;
    @Mock
    private VehicleRepositoryJPA mockVehicleRepositoryJPA;
    @Mock
    private SaleTransRepositoryJPA mockSaleTransRepositoryJPA;
    @Mock
    private SaleTransDetailRepositoryJPA mockSaleTransDetailRepositoryJPA;
    @Mock
    private TicketRepository mockTicketRepository;
    @Mock
    private SaleOrderDetailRepository mockSaleOrderDetailRepository;
    @Mock
    private FileService mockFileService;
    @Mock
    private TicketService mockTicketService;
    @InjectMocks
    private ViettelPayServiceImpl viettelPayServiceImplUnderTest;
    @Mock
    private ServicePlanService mockServicePlanService;
    @Mock
    private JedisCacheService jedisCacheService;
    @Mock
    private LuckyService luckyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        responseCancelInitSuccess = "{\"status\":\n" +
                "{\"code\":\"00\",\n" +
                "\"message\":\"Thành công\",\n" +
                "\"responseTime\":\"2020-08-21T17:10:53.634Z\",\n" +
                "\"requestId\":\"2ab420b38d474317bc8cb648cd245322\"},\n" +
                "\"data\":\n" +
                "{\n" +
                "\"otp\":\"918921\",\n" +
                "\"orderId\":\"20200821171050821011703\",\n" +
                "\"contractId\":\"4353\",\n" +
                "\"msisdn\":\"84985931848\"\n" +
                "}\n" +
                "}";
        responseCancelInitFail = "{\"status\":\n" +
                "{\"code\":\"00111\",\n" +
                "\"message\":\"That bai\",\n" +
                "\"responseTime\":\"2020-08-21T17:10:53.634Z\",\n" +
                "\"requestId\":\"2ab420b38d474317bc8cb648cd245322\"},\n" +
                "\"data\":\n" +
                "{\n" +
                "\"otp\":\"918921\",\n" +
                "\"orderId\":\"20200821171050821011703\",\n" +
                "\"contractId\":\"4353\",\n" +
                "\"msisdn\":\"84985931848\"\n" +
                "}\n" +
                "}";
        responseConfirmFail = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"0011\",\n" +
                "        \"message\": \"That Bai\",\n" +
                "        \"responseTime\": \"2020-08-25T15:49:06.389Z\",\n" +
                "        \"requestId\": \"f29baf3708014b248f62c7ff0810a850\"\n" +
                "    },\n" +
                "    \"data\": {}\n" +
                "}";
        dataLinkInitSuccess = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"00\",\n" +
                "        \"message\": \"Thành công\",\n" +
                "        \"responseTime\": \"2020-08-25T15:49:06.389Z\",\n" +
                "        \"requestId\": \"f29baf3708014b248f62c7ff0810a850\"\n" +
                "    },\n" +
                "    \"data\": {\n" +
                "        \"orderId\": \"209001071314137891199\",\n" +
                "        \"msisdn\": \"84985931848\",\n" +
                "        \"bankCode\": \"MB\",\n" +
                "        \"contractId\": \"12345\"\n" +
                "    }\n" +
                "}";
        dataLinkInitSuccessNoData = "";

        responseConfirmSuccess = "{\"status\":\n" +
                "{\"code\":\"00\",\"message\":\"Thành công\",\n" +
                "\"responseTime\":\"2020-08-21T16:40:39.346Z\",\n" +
                "\"requestId\":\"562533ebfb41451eb638deb0955b3876\"},\n" +
                "\"data\":\n" +
                "{\n" +
                "\"token\":\"01EG868FN2QMGJXKACCM14ZV3Y\",\n" +
                "\"originalOrderId\":\"20200821163855028271122\",\n" +
                "\"bankCode\":\"MB\",\n" +
                "\"orderId\":\"20200821164035803669207\",\n" +
                "\"contractId\":\"4353\",\n" +
                "\"msisdn\":\"84985931848\"}\n" +
                "}";

        responseCancelConfirmSuccess = "{\"status\":\n" +
                "{\"code\":\"00\",\"message\":\"Thành công\",\"responseTime\":\"2020-08-21T17:12:24.269Z\",\n" +
                "\"requestId\":\"31aea0c687b04321b94316c78d1b22a9\"},\n" +
                "\"data\":{\n" +
                "\"originalOrderId\":\"20200821171050821011703\",\n" +
                "\"orderId\":\"20200821171222178174827\",\n" +
                "\"contractId\":\"4353\",\n" +
                "\"msisdn\":\"84985931848\"}\n" +
                "}";
        responseCancelConfirmFail = "{\"status\":\n" +
                "{\"code\":\"001\",\"message\":\"Thành công\",\"responseTime\":\"2020-08-21T17:12:24.269Z\",\n" +
                "\"requestId\":\"31aea0c687b04321b94316c78d1b22a9\"},\n" +
                "\"data\":{\n" +
                "\"originalOrderId\":\"20200821171050821011703\",\n" +
                "\"orderId\":\"20200821171222178174827\",\n" +
                "\"contractId\":\"4353\",\n" +
                "\"msisdn\":\"84985931848\"}\n" +
                "}";
        dataDoubleTransaction = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"EL001\",\n" +
                "        \"message\": \"Giao dich trung lap\",\n" +
                "        \"responseTime\": \"2020-08-25T15:49:06.389Z\",\n" +
                "        \"requestId\": \"f29baf3708014b248f62c7ff0810a850\"\n" +
                "    },\n" +
                "    \"data\": {}\n" +
                "}";
        responseGetSourceSuccess = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"00\",\n" +
                "        \"message\": \"Giao dich Thanh cong\",\n" +
                "        \"responseTime\": \"2020-08-25T15:49:06.389Z\",\n" +
                "        \"requestId\": \"f29baf3708014b248f62c7ff0810a850\"\n" +
                "    },\n" +
                "    \"data\": {" +
                "\"msisdn\":\"12345454\",\n" +
                "\"orderId\":\"20200821171222178174827\"\n" +
                "}\n" +
                "}";
        responseGetSourceFail = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"001\",\n" +
                "        \"message\": \"Giao dich Thanh cong\",\n" +
                "        \"responseTime\": \"2020-08-25T15:49:06.389Z\",\n" +
                "        \"requestId\": \"f29baf3708014b248f62c7ff0810a850\"\n" +
                "    },\n" +
                "    \"data\": {" +
                "\"msisdn\":\"12345454\",\n" +
                "\"orderId\":\"20200821171222178174827\"\n" +
                "}\n" +
                "}";
    }

    @Test
    void testFindByPlateOrContract() {
        // Setup
        final RequestBaseViettelDTO data = new RequestBaseViettelDTO();
        data.setOrderId("orderId");
        data.setContractId(0L);
        data.setSearchType("2");
        data.setPlateType("plateType");
        data.setPlateNumber("plateNumber");
        data.setContractNo("12345");

        final Authentication authentication = null;
        DataDTO dataDTO = new DataDTO();
        dataDTO.setContractId(0L);
        dataDTO.setContractNo("12345");
        when(mockVtPayRepository.findByPlateOrContract(any(), any(), any(), any(), any(), any())).thenReturn(Arrays.asList(dataDTO));

        // Configure ContractPaymentService.findByContractIdAndStatus(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractIdAndStatus(0L, "status")).thenReturn(contractPaymentEntity);

        when(mockVtPayRepository.findAllVehicleByContractAndPlate(any())).thenReturn("result");

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.findByPlateOrContract(data, authentication);

        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenRegister() {
        // Setup
        final RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCode("bankCode");

        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenRegister(new RequestConfirmRegisterDTO(), null)).thenReturn(false);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(0L)).thenReturn(contractEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenRegister_ContractRepositoryJPAReturnsNull() {
        // Setup
        final RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCode("bankCode");

        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenRegister(any(), any())).thenReturn(true);
        when(mockContractRepositoryJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenRegister_CustomerRepositoryJPAReturnsNull() {
        // Setup
        final RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCode("bankCode");

        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenRegister(any(), any())).thenReturn(true);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenRegister_CustomerRepositoryJPAReturnsPresent() {
        // Setup
        final RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCode("bankCode");

        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenRegister(any(), any())).thenReturn(true);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(any(OCSUpdateContractForm.class), any(), anyInt())).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenRegister_CustomerRepositoryJPAReturnsPresent_Ocs_ThrowsException() {
        // Setup
        final RequestConfirmRegisterDTO data = new RequestConfirmRegisterDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCode("bankCode");
        data.setToken("asadsdasdasd");

        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenRegister(any(), any())).thenReturn(true);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("01");
        ocsResponse.setResultCode("01");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(any(OCSUpdateContractForm.class), any(), anyInt())).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenRegister(data, authentication);

        // Verify the results
    }

    @Ignore
    void testUpdateContractPaymentWhenUnRegister() {
        // Setup
        final RequestContractPaymentDTO data = new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L);
        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenUnRegister(new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L))).thenReturn(true);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(any(OCSUpdateContractForm.class), any(), anyInt())).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenUnRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenUnRegister_ThrowsEtcException() {
        // Setup
        final RequestContractPaymentDTO data = new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L);
        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenUnRegister(new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L))).thenReturn(true);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("01");
        ocsResponse.setResultCode("01");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(any(OCSUpdateContractForm.class), any(), anyInt())).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenUnRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenUnRegister_ContractRepositoryJPAReturnsNull() {
        // Setup
        final RequestContractPaymentDTO data = new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L);
        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenUnRegister(new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L))).thenReturn(false);
        when(mockContractRepositoryJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenUnRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testUpdateContractPaymentWhenUnRegister_CustomerRepositoryJPAReturnsNull() {
        // Setup
        final RequestContractPaymentDTO data = new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L);
        final Authentication authentication = null;
        when(mockContractPaymentService.updateContractPaymentWhenUnRegister(new RequestContractPaymentDTO("orderId", "requestTime", "msisdn", "token", 0L, 0L))).thenReturn(false);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(0L)).thenReturn(contractEntity);

        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.updateContractPaymentWhenUnRegister(data, authentication);

        // Verify the results
    }

    @Test
    void testChangeMoneySourceViettelPay() {
        // Setup
        final RequestConfirmChangeMoneySourceDTO data = new RequestConfirmChangeMoneySourceDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCodeAfter("bankCodeAfter");

        final Authentication authentication = null;
        when(mockContractPaymentService.changeMoneySourceViettelPay(new RequestConfirmChangeMoneySourceDTO(), null)).thenReturn(false);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.changeMoneySourceViettelPay(data, authentication);

        // Verify the results
    }

    @Test
    void testChangeMoneySourceViettelPay_CaseTrue() {
        // Setup
        final RequestConfirmChangeMoneySourceDTO data = new RequestConfirmChangeMoneySourceDTO();
        data.setAccountOwner("accountOwner");
        data.setBankCodeAfter("bankCodeAfter");

        final Authentication authentication = null;
        when(mockContractPaymentService.changeMoneySourceViettelPay(any(), any())).thenReturn(true);

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.changeMoneySourceViettelPay(data, authentication);

        // Verify the results
    }

    @Test
    void testLinkInitViettelPay() throws Exception {
        // Setup
        final RequestLinkInitViettelPayDTO data = new RequestLinkInitViettelPayDTO("contractNo", "contractFullName", "bankCode", "idNo", "idType");
        final Authentication authentication = null;

        // Configure ContractPaymentRepositoryJPA.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentRepositoryJPA.findByContractId(0L)).thenReturn(null);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return dataLinkInitSuccess;
            }
        };
        String responseLinkInit = viettelPayServiceImplUnderTest.doRequestViettelPay("POST_REQUEST", "linkConfirmViettelPayUrl", FnCommon.toStringJson("data"), null, null, 13L, 1);
        ResponseViettelPayDTO.ResponseLinkInitDTO responseLink = new Gson().fromJson(responseLinkInit, ResponseViettelPayDTO.ResponseLinkInitDTO.class);
        // Run the test

        assertNotNull(responseLink.getData());
        // Verify the results
    }

    @Test
    void testLinkInitViettelPayHasErrorOccurred() throws Exception {
        // Setup
        final RequestLinkInitViettelPayDTO data = new RequestLinkInitViettelPayDTO("contractNo", "contractFullName", "bankCode", "idNo", "idType");
        final Authentication authentication = null;
        data.setActionTypeId(13L);

        // Configure ContractPaymentRepositoryJPA.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentRepositoryJPA.findByContractId(0L)).thenReturn(null);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return dataLinkInitSuccessNoData;
            }
        };
        // Run the test

        assertThrows(DataNotFoundException.class, () -> viettelPayServiceImplUnderTest.linkInitViettelPay(data, authentication));
        // Verify the results
    }

    @Ignore
    void testLinkInitViettelPayReturnData() throws Exception {
        // Setup
        final RequestLinkInitViettelPayDTO data = new RequestLinkInitViettelPayDTO("contractNo", "contractFullName", "bankCode", "idNo", "idType");
        data.setActionTypeId(13L);
        final Authentication authentication = null;

        // Configure ContractPaymentRepositoryJPA.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentRepositoryJPA.findByContractId(0L)).thenReturn(null);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return dataLinkInitSuccess;
            }
        };
        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkInitViettelPay(data, authentication);
        // Verify the results
        final ResponseViettelPayDTO.DataRegisterDTO dataRegisterDTO = new ResponseViettelPayDTO.DataRegisterDTO();

        dataRegisterDTO.setOrderId("209001071314137891199");
        dataRegisterDTO.setMsisdn("84985931848");
        dataRegisterDTO.setBankCode("MB");
        dataRegisterDTO.setContractId(12345L);

        assertEquals(dataRegisterDTO, result);
    }

    @Ignore
    void testLinkInitViettelPaySuccess() throws IOException, DataNotFoundException {
        // Setup
        final RequestLinkInitViettelPayDTO data = new RequestLinkInitViettelPayDTO("contractNo", "contractFullName", "bankCode", "idNo", "idType");
        final Authentication authentication = null;

        // Configure ContractPaymentRepositoryJPA.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentRepositoryJPA.findByContractId(0L)).thenReturn(null);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return dataLinkInitSuccess;
            }
        };
        String responseLinkInit = viettelPayServiceImplUnderTest.doRequestViettelPay("GET", "URL_INIT", "DATA", null, null, 13L, 1);
        ResponseViettelPayDTO.ResponseLinkInitDTO responseLink = new Gson().fromJson(responseLinkInit, ResponseViettelPayDTO.ResponseLinkInitDTO.class);
        assertEquals("00", responseLink.getStatus().getCode());
    }

    @Test
    void testLinkInitViettelPayDoubleTransaction() {
        // Setup
        final RequestLinkInitViettelPayDTO data = new RequestLinkInitViettelPayDTO("contractNo",
                "contractFullName", "bankCode", "idNo", "idType");
        data.setActionTypeId(13L);
        final Authentication authentication = null;

        // Configure ContractPaymentRepositoryJPA.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentRepositoryJPA.findByContractId(0L)).thenReturn(null);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return dataDoubleTransaction;
            }
        };
        String responseLinkInit = viettelPayServiceImplUnderTest.doRequestViettelPay("POST", null, data.toString(), null, null, 13L, 1);
        ResponseViettelPayDTO.ResponseLinkInitDTO responseLink = new Gson().fromJson(responseLinkInit, ResponseViettelPayDTO.ResponseLinkInitDTO.class);
        assertThrows(DataNotFoundException.class,
                () -> viettelPayServiceImplUnderTest.linkInitViettelPay(data, authentication)
                , ""
        );
    }

    @Test
    void testLinkInitViettelPay_ThrowsContractPaymentExist() {
        // Setup
        final RequestLinkInitViettelPayDTO data = new RequestLinkInitViettelPayDTO("contractNo", "contractFullName", "bankCode", "idNo", "idType");
        final Authentication authentication = null;
        // Configure ContractPaymentRepositoryJPA.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        when(mockContractPaymentRepositoryJPA.findByContractId(any())).thenReturn(contractPaymentEntity);

        assertThrows(EtcException.class, () ->
                        viettelPayServiceImplUnderTest.linkInitViettelPay(data, authentication),
                "Hợp đồng này đã được liên kết với một tài khoản ViettelPay.");
    }

    @Ignore
    void testLinkConfirmViettelPayDocumentLinkInitFileBase64() {
        // Setup
        final RequestLinkConfirmViettelPayDTO data = new RequestLinkConfirmViettelPayDTO("otp", "originalOrderId", "cardDocumentName", "cardFileBase64", "documentLinkInitName", "documentLinkInitFileBase64", "accountNumber", "accountOwner", "documentNo", "documentTypeCode", 0L, "topupAuto", "bankCode", "topupAmount", "transContent", "moreInfo", "contractAddress", "contractEmail", "contractGender", "contractDob", "contractIdNo", "contractIdType", "contractIdIssueDate", "contractIdIssuePlace", "contractTel", "contractObjetType", "staffCode", "staffName", "shopCode", "shopName", "shopAddress");
        final Authentication authentication = null;
        data.setDocumentLinkInitFileBase64("..");
        assertThrows(EtcException.class,
                () -> viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, null),
                "file chứng minh thư không đúng định dạng base64");
    }

    @Test
    void testLinkConfirmViettelPayCardFileBase64() {
        // Setup
        final RequestLinkConfirmViettelPayDTO data = new RequestLinkConfirmViettelPayDTO("otp", "originalOrderId", "cardDocumentName", "cardFileBase64", "documentLinkInitName", "documentLinkInitFileBase64", "accountNumber", "accountOwner", "documentNo", "documentTypeCode", 0L, "topupAuto", "bankCode", "topupAmount", "transContent", "moreInfo", "contractAddress", "contractEmail", "contractGender", "contractDob", "contractIdNo", "contractIdType", "contractIdIssueDate", "contractIdIssuePlace", "contractTel", "contractObjetType", "staffCode", "staffName", "shopCode", "shopName", "shopAddress");
        final Authentication authentication = null;
        data.setCardFileBase64("..");
        assertThrows(EtcException.class,
                () -> viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, null),
                "file đồng ý liên kết không đúng định dạng base64");
    }

    @Test
    void testLinkConfirmViettelPay() throws Exception {
        // Setup
        final RequestLinkConfirmViettelPayDTO data = new RequestLinkConfirmViettelPayDTO("otp", "originalOrderId", "cardDocumentName", "cardFileBase64", "documentLinkInitName", "documentLinkInitFileBase64", "accountNumber", "accountOwner", "documentNo", "documentTypeCode", 0L, "topupAuto", "bankCode", "topupAmount", "transContent", "moreInfo", "contractAddress", "contractEmail", "contractGender", "contractDob", "contractIdNo", "contractIdType", "contractIdIssueDate", "contractIdIssuePlace", "contractTel", "contractObjetType", "staffCode", "staffName", "shopCode", "shopName", "shopAddress");
        final Authentication authentication = null;
        data.setActionTypeId(13L);
        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.NOT_ACTIVATED.value);
        when(mockContractPaymentService.findByContractId(any())).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode(any(), any(), any())).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        when(mockContractPaymentRepositoryJPA.save(any())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure AttachmentFileRepositoryJPA.save(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileRepository.save(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"))).thenReturn(attachmentFileEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void updateContractOnOCS(Long customerId, String contractId, String msisdn, String token, String value, ContractEntity contractEntity,
                                             int actTypeId, Authentication authentication) {
            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseConfirmSuccess;
            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public void saveAttachmentFile(RequestLinkConfirmViettelPayDTO data, String userLogin, Long objectId) throws EtcException, IOException {

            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, authentication);

        // Verify the results
        ResponseViettelPayDTO.DataLinkConfirmDTO dataLinkConfirmDTO = new ResponseViettelPayDTO.DataLinkConfirmDTO();
        dataLinkConfirmDTO.setOrderId("20200821164035803669207");
        dataLinkConfirmDTO.setToken("01EG868FN2QMGJXKACCM14ZV3Y");
        dataLinkConfirmDTO.setBankCode("MB");
        dataLinkConfirmDTO.setContractId("4353");
        dataLinkConfirmDTO.setMsisdn("84985931848");
        dataLinkConfirmDTO.setOriginalOrderId("20200821163855028271122");
        assertEquals(dataLinkConfirmDTO, result);
    }

    @Test
    void testLinkConfirmViettelPay_ThrowsException() throws Exception {
        // Setup
        final RequestLinkConfirmViettelPayDTO data = new RequestLinkConfirmViettelPayDTO("otp", "originalOrderId", "cardDocumentName", "cardFileBase64", "documentLinkInitName", "documentLinkInitFileBase64", "accountNumber", "accountOwner", "documentNo", "documentTypeCode", 0L, "topupAuto", "bankCode", "topupAmount", "transContent", "moreInfo", "contractAddress", "contractEmail", "contractGender", "contractDob", "contractIdNo", "contractIdType", "contractIdIssueDate", "contractIdIssuePlace", "contractTel", "contractObjetType", "staffCode", "staffName", "shopCode", "shopName", "shopAddress");
        final Authentication authentication = null;
        data.setActionTypeId(13L);
        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(any())).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode(any(), any(), any())).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        when(mockContractPaymentRepositoryJPA.save(any())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure AttachmentFileRepositoryJPA.save(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileRepository.save(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"))).thenReturn(attachmentFileEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void updateContractOnOCS(Long customerId, String contractId, String msisdn, String token, String value, ContractEntity contractEntity,
                                             int actTypeId, Authentication authentication) {
            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseConfirmSuccess;
            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public void saveAttachmentFile(RequestLinkConfirmViettelPayDTO data, String userLogin, Long objectId) throws EtcException, IOException {

            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, authentication);

        // Verify the results
//        assertThrows(EtcException.class, () ->viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, authentication));
    }

    @Test
    void testLinkConfirmViettelPayCallToCancelInit() throws Exception {
        // Setup
        final RequestLinkConfirmViettelPayDTO data = new RequestLinkConfirmViettelPayDTO("otp", "originalOrderId", "cardDocumentName", "cardFileBase64", "documentLinkInitName", "documentLinkInitFileBase64", "accountNumber", "accountOwner", "documentNo", "documentTypeCode", 0L, "topupAuto", "bankCode", "topupAmount", "transContent", "moreInfo", "contractAddress", "contractEmail", "contractGender", "contractDob", "contractIdNo", "contractIdType", "contractIdIssueDate", "contractIdIssuePlace", "contractTel", "contractObjetType", "staffCode", "staffName", "shopCode", "shopName", "shopAddress");
        final Authentication authentication = null;
        data.setActionTypeId(13L);
        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(0L)).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode(any(), any(), any())).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        when(mockContractPaymentRepositoryJPA.save(any())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure AttachmentFileRepositoryJPA.save(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileRepository.save(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"))).thenReturn(attachmentFileEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseConfirmSuccess;
            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public void saveAttachmentFile(RequestLinkConfirmViettelPayDTO data, String userLogin, Long objectId) throws EtcException, IOException {

            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void executelinkCancelViettelPay(ResponseViettelPayDTO.ResponseLinkConfirmDTO responseLinkConfirm, Long contractId, Authentication authentication) throws IOException, DataNotFoundException {
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, authentication);

        // Verify the results
        ResponseViettelPayDTO.DataLinkConfirmDTO dataLinkConfirmDTO = new ResponseViettelPayDTO.DataLinkConfirmDTO();
        dataLinkConfirmDTO.setOrderId("20200821164035803669207");
        dataLinkConfirmDTO.setToken("01EG868FN2QMGJXKACCM14ZV3Y");
        dataLinkConfirmDTO.setBankCode("MB");
        dataLinkConfirmDTO.setContractId("4353");
        dataLinkConfirmDTO.setMsisdn("84985931848");
        dataLinkConfirmDTO.setOriginalOrderId("20200821163855028271122");
        assertEquals(dataLinkConfirmDTO, result);
    }

    @Test
    void testLinkConfirmViettelPayError() throws Exception {
        // Setup
        final RequestLinkConfirmViettelPayDTO data = new RequestLinkConfirmViettelPayDTO("otp", "originalOrderId", "cardDocumentName", "cardFileBase64", "documentLinkInitName", "documentLinkInitFileBase64", "accountNumber", "accountOwner", "documentNo", "documentTypeCode", 0L, "topupAuto", "bankCode", "topupAmount", "transContent", "moreInfo", "contractAddress", "contractEmail", "contractGender", "contractDob", "contractIdNo", "contractIdType", "contractIdIssueDate", "contractIdIssuePlace", "contractTel", "contractObjetType", "staffCode", "staffName", "shopCode", "shopName", "shopAddress");
        final Authentication authentication = null;
        data.setActionTypeId(13L);
        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(0L)).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode(any(), any(), any())).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        when(mockContractPaymentRepositoryJPA.save(any())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure AttachmentFileRepositoryJPA.save(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileRepository.save(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"))).thenReturn(attachmentFileEntity);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(any())).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseConfirmFail;
            }
        };

        // Run the test
//        final Object result = viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, authentication);

        // Verify the results

        assertThrows(DataNotFoundException.class, () ->
                        viettelPayServiceImplUnderTest.linkConfirmViettelPay(data, authentication)
                , "");
    }

    @Test
    void testLinkCancelInitViettelPay() throws Exception {
        // Setup
        final RequestCancelInitViettelPayDTO data = new RequestCancelInitViettelPayDTO("token");
        data.setActionTypeId(13L);
        final Authentication authentication = null;

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(any())).thenReturn(contractPaymentEntity);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseCancelInitSuccess;
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkCancelInitViettelPay(data, authentication);
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testLinkCancelInitViettelPay_ThrowsDataNotFoundException() {
        // Setup
        final RequestCancelInitViettelPayDTO data = new RequestCancelInitViettelPayDTO("token");
        data.setActionTypeId(13L);
        final Authentication authentication = null;

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(any())).thenReturn(contractPaymentEntity);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseCancelInitFail;
            }
        };
        // Run the test
        assertThrows(DataNotFoundException.class, () -> viettelPayServiceImplUnderTest.linkCancelInitViettelPay(data, authentication));
    }

    @Test
    void testLinkCancelInitViettelPay_ThrowsEtcException() {
        // Setup
        final RequestCancelInitViettelPayDTO data = new RequestCancelInitViettelPayDTO("token");
        final Authentication authentication = null;

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(0L)).thenReturn(contractPaymentEntity);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        assertThrows(EtcException.class, () -> viettelPayServiceImplUnderTest.linkCancelInitViettelPay(data, authentication));
    }

    @Test
    void testLinkCancelConfirmViettelPay() throws Exception {
        // Setup
        final RequestCancelConfirmViettelPayDTO data = new RequestCancelConfirmViettelPayDTO("otp", "originalOrderId", "token");
        final Authentication authentication = null;
        data.setActionTypeId(13L);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        when(mockContractPaymentService.findByContractId(any())).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode(anyString(), anyString(), anyString())).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        contractPaymentEntity1.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        when(mockContractPaymentRepositoryJPA.save(any())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseCancelConfirmSuccess;
            }
        };
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void updateContractOnOCS(Long customerId, String contractId, String msisdn, String token, String value, ContractEntity contractEntity,
                                             int actTypeId, Authentication authentication) {
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkCancelConfirmViettelPay(data, authentication);
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testLinkCancelConfirmViettelPay_ThrowsDataNotFoundException() {
        // Setup
        final RequestCancelConfirmViettelPayDTO data = new RequestCancelConfirmViettelPayDTO("otp", "originalOrderId", "token");
        final Authentication authentication = null;
        data.setActionTypeId(13L);

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(0L)).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(0L)).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode("token", "tableName", "code")).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        when(mockContractPaymentRepositoryJPA.save(new ContractPaymentEntity())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseCancelConfirmFail;
            }
        };
        // Run the test
        assertThrows(DataNotFoundException.class, () -> viettelPayServiceImplUnderTest.linkCancelConfirmViettelPay(data, authentication));
    }

    @Test
    void testLinkCancelConfirmViettelPay_saveOrUpdateContractPayment() throws Exception {
        // Setup
        final RequestCancelConfirmViettelPayDTO data = new RequestCancelConfirmViettelPayDTO("otp", "originalOrderId", "token");
        final Authentication authentication = null;
        data.setActionTypeId(13L);
        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(any())).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        when(mockContractPaymentService.findByContractId(any())).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode(any(), any(), any())).thenReturn(categoryData);

        when(mockContractPaymentRepositoryJPA.save(any())).thenReturn(contractPaymentEntity);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        // Configure CustomerRepositoryJPA.findById(...).
        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setCustId(0L);
        customerEntity1.setCustTypeId(0L);
        customerEntity1.setDocumentNumber("documentNumber");
        customerEntity1.setDocumentTypeId(0L);
        customerEntity1.setTaxCode("taxCode");
        customerEntity1.setDateOfIssue(new Date(0L));
        customerEntity1.setPlaceOfIssue("placeOfIssue");
        customerEntity1.setCustName("custName");
        customerEntity1.setBirthDate(new Date(0L));
        customerEntity1.setGender("gender");
        final Optional<CustomerEntity> customerEntity = Optional.of(customerEntity1);
        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(customerEntity);

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseCancelConfirmSuccess;
            }
        };
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void updateContractOnOCS(Long customerId, String contractId, String msisdn, String token, String value, ContractEntity contractEntity,
                                             int actTypeId, Authentication authentication) {
            }
        };
        // Run the test
        final Object result = viettelPayServiceImplUnderTest.linkCancelConfirmViettelPay(data, authentication);

        // Verify the results
    }

    @Test
    void testLinkCancelConfirmViettelPay_CustomerRepositoryJPAReturnsNull() throws Exception {
        // Setup
        final RequestCancelConfirmViettelPayDTO data = new RequestCancelConfirmViettelPayDTO("otp", "originalOrderId", "token");
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure ContractRepositoryJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(mockContractRepositoryJPA.findById(0L)).thenReturn(contractEntity);

        // Configure ContractPaymentService.findByContractId(...).
        final ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setContractPaymentId(0L);
        contractPaymentEntity.setCustId(0L);
        contractPaymentEntity.setContractId(0L);
        contractPaymentEntity.setMethodRechargeId(0L);
        contractPaymentEntity.setAccountNumber("accountNumber");
        contractPaymentEntity.setAccountOwner("accountOwner");
        contractPaymentEntity.setAccountBankId(0L);
        contractPaymentEntity.setCreateUser("createUser");
        contractPaymentEntity.setCreateDate(new Date(0L));
        contractPaymentEntity.setDescription("description");
        when(mockContractPaymentService.findByContractId(0L)).thenReturn(contractPaymentEntity);

        // Configure CategoriesService.findCategoriesByTableNameAndCode(...).
        final CategoryDTO.CategoryData categoryData = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));
        when(mockCategoriesService.findCategoriesByTableNameAndCode("token", "tableName", "code")).thenReturn(categoryData);

        // Configure ContractPaymentRepositoryJPA.save(...).
        final ContractPaymentEntity contractPaymentEntity1 = new ContractPaymentEntity();
        contractPaymentEntity1.setContractPaymentId(0L);
        contractPaymentEntity1.setCustId(0L);
        contractPaymentEntity1.setContractId(0L);
        contractPaymentEntity1.setMethodRechargeId(0L);
        contractPaymentEntity1.setAccountNumber("accountNumber");
        contractPaymentEntity1.setAccountOwner("accountOwner");
        contractPaymentEntity1.setAccountBankId(0L);
        contractPaymentEntity1.setCreateUser("createUser");
        contractPaymentEntity1.setCreateDate(new Date(0L));
        contractPaymentEntity1.setDescription("description");
        when(mockContractPaymentRepositoryJPA.save(new ContractPaymentEntity())).thenReturn(contractPaymentEntity1);

        // Configure ContractRepositoryJPA.save(...).
        final ContractEntity contractEntity1 = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.save(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"))).thenReturn(contractEntity1);

        when(mockCustomerRepositoryJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure OCSService.updateContract(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.updateContract(new OCSUpdateContractForm(), null, 0)).thenReturn(ocsResponse);
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseCancelConfirmSuccess;
            }
        };
        String response = viettelPayServiceImplUnderTest.doRequestViettelPay("POST_REQUEST", "linkCancelConfirmViettelPayUrl", FnCommon.toStringJson("data"), null, null, 1L, 1);
        ResponseViettelPayDTO.ResponseLinkConfirmDTO responseLinkCancelConfirmDTO = new Gson().fromJson(response, ResponseViettelPayDTO.ResponseLinkConfirmDTO.class);
        // Run the test
//        final Object result = viettelPayServiceImplUnderTest.linkCancelConfirmViettelPay(data, authentication);

        // Verify the results
    }

    @Ignore
    void testGetSourcesViettelPay() throws Exception {
        // Setup
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseGetSourceSuccess;
            }
        };
        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getSourcesViettelPay("msisdn", 0L, authentication);
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testGetSourcesViettelPay_ThrowsDataNotFoundException() {
        // Setup
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
                return responseGetSourceFail;
            }
        };
        // Run the test
        assertThrows(DataNotFoundException.class, () -> viettelPayServiceImplUnderTest.getSourcesViettelPay("msisdn", 0L, authentication));
    }

    @Test
    void testGetInfoTicketPurchaseAndExtendedViaSDK_NullInfoTicketPurchase() throws JsonProcessingException {
        // Setup
        final Authentication authentication = null;
        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDK("billingCode")).thenReturn(null);

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes("token")).thenReturn(dataTypes);

        when(mockVtPayRepository.getTicketExtendedViaSDK("orderId")).thenReturn("result");

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getInfoTicketPurchaseAndExtendedViaSDK(authentication, "billingCode");
        assertNotNull(result);
    }

    @Ignore
    void testGetInfoTicketPurchaseAndExtendedViaSDK() throws Exception {
        // Setup
        final Authentication authentication = null;
        ResponseGetInfoTicketPurchaseAndExtendedDTO response = new ResponseGetInfoTicketPurchaseAndExtendedDTO();
        response.setAmount(1L);
        response.setOrderId("12345");
        response.setBillingCode("12345");
        response.setContractId(1L);
        response.setMsisdn("123456789");
        response.setToken("acbdv12345");
        //response.setTickes(Arrays.asList(new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder()));
        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDK("billingCode")).thenReturn(null);

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes(any())).thenReturn(dataTypes);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private List<DataTypeDTO.DataType> getPlateTypes(String token) {
                return Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
            }
        };

        ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder ticketOrderResult = new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder(
                "1112",
                "11",
                "12",
                "1",
                "12313",
                1L,
                1L,
                1L,
                1L,
                1L,
                "abcd",
                "amcd",
                "startDate",
                "endDate",
                "epc"
        );
        when(mockVtPayRepository.getTicketExtendedViaSDK(any())).thenReturn(Arrays.asList(ticketOrderResult));

        StageBooDTO.Stage stage = new StageBooDTO.Stage("stageName", 1L, 1L, 1L, 1L);
        StageBooDTO stageBooDTO = new StageBooDTO(stage);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StageBooDTO getStageById(String token, String stageId) {
                return stageBooDTO;
            }
        };

        StationBooDTO.Station station = new StationBooDTO.Station("name", 1L, 1L);
        StationBooDTO stationBooDTO = new StationBooDTO(station);
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StationBooDTO getStationById(String token, String stationId) {
                return stationBooDTO;
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getInfoTicketPurchaseAndExtendedViaSDK(authentication, "billingCode");
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testGetInfoTicketPurchaseAndExtendedViaSDK_StageIdNull() throws Exception {
        // Setup
        final Authentication authentication = null;
        ResponseGetInfoTicketPurchaseAndExtendedDTO response = new ResponseGetInfoTicketPurchaseAndExtendedDTO();
        response.setAmount(1L);
        response.setOrderId("12345");
        response.setBillingCode("12345");
        response.setContractId(1L);
        response.setMsisdn("123456789");
        response.setToken("acbdv12345");
        //response.setTickes(Arrays.asList(new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder()));
        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDK("billingCode")).thenReturn(response);

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes(any())).thenReturn(dataTypes);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private List<DataTypeDTO.DataType> getPlateTypes(String token) {
                return Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
            }
        };

        ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder ticketOrderResult = new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder(
                "1112",
                "11",
                "12",
                "1",
                "12313",
                1L,
                1L,
                1L,
                1L,
                null,
                "abcd",
                "amcd",
                "startDate",
                "endDate",
                "epc"
        );
        when(mockVtPayRepository.getTicketExtendedViaSDK(any())).thenReturn(Arrays.asList(ticketOrderResult));

        StageBooDTO.Stage stage = new StageBooDTO.Stage("stageName", 1L, 1L, 1L, 1L);
        StageBooDTO stageBooDTO = new StageBooDTO(stage);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StageBooDTO getStageById(String token, String stageId) {
                return stageBooDTO;
            }
        };

        StationBooDTO.Station station = new StationBooDTO.Station("name", 1L, 1L);
        StationBooDTO stationBooDTO = new StationBooDTO(station);
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StationBooDTO getStationById(String token, String stationId) {
                return stationBooDTO;
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getInfoTicketPurchaseAndExtendedViaSDK(authentication, "billingCode");
        assertNotNull(result);
        // Verify the results
    }

    @Ignore
    void testGetInfoTicketPurchaseAndExtendedViaSDK_StageIdNotNull() throws Exception {
        // Setup
        final Authentication authentication = null;
        ResponseGetInfoTicketPurchaseAndExtendedDTO response = new ResponseGetInfoTicketPurchaseAndExtendedDTO();
        response.setAmount(1L);
        response.setOrderId("12345");
        response.setBillingCode("12345");
        response.setContractId(1L);
        response.setMsisdn("123456789");
        response.setToken("acbdv12345");
        //response.setTickes(Arrays.asList(new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder()));
        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDK("billingCode")).thenReturn(response);

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes(any())).thenReturn(dataTypes);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private List<DataTypeDTO.DataType> getPlateTypes(String token) {
                return Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
            }
        };

        ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder ticketOrderResult = new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder(
                "1112",
                "11",
                "12",
                "1",
                "12313",
                1L,
                1L,
                1L,
                1L,
                1L,
                "abcd",
                "amcd",
                "startDate",
                "endDate",
                "epc"
        );
        when(mockVtPayRepository.getTicketExtendedViaSDK(any())).thenReturn(Arrays.asList(ticketOrderResult));

        StageBooDTO.Stage stage = new StageBooDTO.Stage("stageName", 1L, 1L, 1L, 1L);
        StageBooDTO stageBooDTO = new StageBooDTO(stage);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StageBooDTO getStageById(String token, String stageId) {
                return stageBooDTO;
            }
        };

        StationBooDTO.Station station = new StationBooDTO.Station("name", 1L, 1L);
        StationBooDTO stationBooDTO = new StationBooDTO(station);
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StationBooDTO getStationById(String token, String stationId) {
                return stationBooDTO;
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getInfoTicketPurchaseAndExtendedViaSDK(authentication, "billingCode");
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testGetInfoTicketPurchaseAndExtendedViaSDK_TicketNull() throws Exception {
        // Setup
        final Authentication authentication = null;
        ResponseGetInfoTicketPurchaseAndExtendedDTO response = new ResponseGetInfoTicketPurchaseAndExtendedDTO();
        response.setAmount(1L);
        response.setOrderId("12345");
        response.setBillingCode("12345");
        response.setContractId(1L);
        response.setMsisdn("123456789");
        response.setToken("acbdv12345");
        //response.setTickes(Arrays.asList(new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder()));
        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDK("billingCode")).thenReturn(response);

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes(any())).thenReturn(dataTypes);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private List<DataTypeDTO.DataType> getPlateTypes(String token) {
                return Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
            }
        };

        when(mockVtPayRepository.getTicketExtendedViaSDK("orderId")).thenReturn("result");

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getInfoTicketPurchaseAndExtendedViaSDK(authentication, "billingCode");
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testWriteLog() {
        // Setup
        final Authentication authentication = null;
        final WsAuditEntity expectedResult = new WsAuditEntity();
        expectedResult.setWsAuditId(0L);
        expectedResult.setWsCallType("wsCallType");
        expectedResult.setActTypeId(0L);
        expectedResult.setRequestTime(new Date(0L));
        expectedResult.setActionUserName("actionUserName");
        expectedResult.setWsUri("wsUri");
        expectedResult.setSourceAppId("sourceAppId");
        expectedResult.setIpPc("ipPc");
        expectedResult.setDestinationAppId("destinationAppId");
        expectedResult.setStatus("status");

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(any())).thenReturn(wsAuditEntity);

        // Run the test
        final WsAuditEntity result = viettelPayServiceImplUnderTest.writeLog("req", "resp", "url", "ip", 0L, 0L, authentication, "wsCallType", "status");

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testWriteLog_AuthenticationNotNull() {
        // Setup
        final Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "authen";
            }
        };
        final WsAuditEntity expectedResult = new WsAuditEntity();
        expectedResult.setWsAuditId(0L);
        expectedResult.setWsCallType("wsCallType");
        expectedResult.setActTypeId(0L);
        expectedResult.setRequestTime(new Date(0L));
        expectedResult.setActionUserName("actionUserName");
        expectedResult.setWsUri("wsUri");
        expectedResult.setSourceAppId("sourceAppId");
        expectedResult.setIpPc("ipPc");
        expectedResult.setDestinationAppId("destinationAppId");
        expectedResult.setStatus("status");

        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(null);

        // Run the test
        final WsAuditEntity result = viettelPayServiceImplUnderTest.writeLog("req", "resp", "url", "ip", 0L, 0L, authentication, "wsCallType", "status");

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testDoRequestViettelPay() {
        // Setup
        final Map<String, String> params = new HashMap<>();
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        final WsAuditEntity wsAuditEntity = new WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final String result = viettelPayServiceImplUnderTest.doRequestViettelPay("requestMethod", "url", "jsonData", params, authentication, 0L, 1);

        // Verify the results
        assertEquals(null, result);
    }

    @Test
    void testDoRequestViettelPay_WsAuditRepositoryJPAReturnsNull() {
        // Setup
        final Map<String, String> params = new HashMap<>();
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(null);

        // Run the test
        final String result = viettelPayServiceImplUnderTest.doRequestViettelPay("requestMethod", "url", "jsonData", params, authentication, 0L, 1);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testConfirmResultTicketPurchaseAutoRenew() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.confirmResultTicketPurchaseAutoRenew(requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testConfirmResultTicketPurchaseAutoRenew_SaleOrderDetailRepositoryJPAReturnsNull() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(null);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.confirmResultTicketPurchaseAutoRenew(requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testConfirmResultTicketPurchaseAutoRenew_SaleOrderServiceJPAReturnsAbsent() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.confirmResultTicketPurchaseAutoRenew(requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testConfirmResultTicketPurchaseAutoRenew_OCSServiceThrowsException() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenThrow(Exception.class);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.confirmResultTicketPurchaseAutoRenew(requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testConfirmResultCancelTicketPurchaseAutoRenew() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("id", "1234");
        doReturn(linkedHashMap).when(mockStageService).findByStationInAndStationOut(any(), any(), any());

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.confirmResultCancelTicketPurchaseAutoRenew(requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testConfirmResultCancelTicketPurchaseAutoRenew_SaleOrderDetailRepositoryJPAReturnsNull() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("id", "1234");
        doReturn(linkedHashMap).when(mockStageService).findByStationInAndStationOut(any(), any(), any());

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(null);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public List<SaleOrderDetailEntity> getSaleOrderDetail(AutoRenewVtpDTO autoRenewVtpDTO, Authentication authentication) {
                return saleOrderDetailEntities1;
            }
        };
        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.confirmResultCancelTicketPurchaseAutoRenew(requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testHandleAutoRenew() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            public List<SaleOrderDetailEntity> getSaleOrderDetail(AutoRenewVtpDTO autoRenewVtpDTO, Authentication authentication) {
                return saleOrderDetailEntities2;
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.handleAutoRenew("type", requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testHandleAutoRenew_SaleOrderDetailRepositoryJPAReturnsNull() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(null);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.handleAutoRenew("type", requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testHandleAutoRenew_SaleOrderServiceJPAReturnsAbsent() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.handleAutoRenew("type", requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testHandleAutoRenew_OCSServiceThrowsException() throws Exception {
        // Setup
        final RequestRenewTicketPricesDTO requestRenewTicketPricesDTO = new RequestRenewTicketPricesDTO();
        requestRenewTicketPricesDTO.setOrderId("orderId");
        requestRenewTicketPricesDTO.setRequestTime("requestTime");
        requestRenewTicketPricesDTO.setMsisdn("msisdn");
        requestRenewTicketPricesDTO.setToken("token");
        requestRenewTicketPricesDTO.setCode("code");
        requestRenewTicketPricesDTO.setMessage("message");
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");
        requestRenewTicketPricesDTO.setAutoRenew_VTP(Arrays.asList(autoRenewVtpDTO));

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm expectedResult = new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO("orderId", "msisdn", 0L), new StatusDTO("code", "message", "description", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()));

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        doReturn(new LinkedHashMap<>()).when(mockStageService).findByStationInAndStationOut(0L, 0L, "token");

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities1);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities2 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities2);

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenThrow(Exception.class);

        // Run the test
        final ResponseViettelPayDTO.ResponseUnRegisterConfirm result = viettelPayServiceImplUnderTest.handleAutoRenew("type", requestRenewTicketPricesDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testChargeTicketVTP() throws Exception {
        // Setup
        final Authentication authentication = null;
        final RequestChargeTicketVTPDTO requestChargeTicketVTPDTO = new RequestChargeTicketVTPDTO();
        requestChargeTicketVTPDTO.setOrderId("123");
        requestChargeTicketVTPDTO.setBillingCode("123");
        requestChargeTicketVTPDTO.setRequestTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        requestChargeTicketVTPDTO.setToken("token");
        requestChargeTicketVTPDTO.setContractId("12");
        requestChargeTicketVTPDTO.setMsisdn("123456");
        requestChargeTicketVTPDTO.setBankCodeAfter("1223");
        requestChargeTicketVTPDTO.setAmount(0L);
        requestChargeTicketVTPDTO.setCode("12");
        requestChargeTicketVTPDTO.setMessage("message");

        final ResponseChargeTicketDTO expectedResult = new ResponseChargeTicketDTO(new ResponseChargeTicketDTO.ResponseStatus("code", "message", "description", "responseTime"), new ResponseChargeTicketDTO.ResponseData("orderId", 0L, "billingCode", "msisdn"));

        // Configure SaleOrderRepositoryJPA.findBySaleOrderId(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        saleOrderEntity.setContractId(0L);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderId(any())).thenReturn(saleOrderEntity);

        // Configure SaleTransRepositoryJPA.save(...).
        final SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(0L);
        saleTransEntity.setSaleTransCode("saleTransCode");
        saleTransEntity.setSaleTransDate(new Date(0L));
        saleTransEntity.setSaleTransType("saleTransType");
        saleTransEntity.setStatus("status");
        saleTransEntity.setInvoiceUsed("invoiceUsed");
        saleTransEntity.setInvoiceCreateDate(new Date(0L));
        saleTransEntity.setShopId(0L);
        saleTransEntity.setShopName("shopName");
        saleTransEntity.setStaffId(0L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure OCSService.charge(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.charge(new AddSupOfferRequestDTO(), null, 0L, "partyCode")).thenReturn(ocsResponse);

        // Configure SaleOrderDetailRepositoryJPA.findAllBySaleOrderId(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("112321");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(saleOrderDetailEntities);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber12");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, VehicleEntity.Status.ACTIVATED.value, "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        // Configure OCSService.addSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenReturn(ocsResponse1);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse2);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse3 = new OCSResponse();
        ocsResponse3.resultCode("resultCode");
        ocsResponse3.setResultCode("resultCode");
        ocsResponse3.description("description");
        ocsResponse3.setDescription("description");
        ocsResponse3.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse3);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("plateNumber123");
        saleTransDetailEntity.setEpc("epc");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        when(mockSaleTransDetailRepositoryJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity1);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void chargeOCS(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, long contractId, String partyCode) throws Exception {
            }
        };
        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("0");
        ocsResponseaddSupOffer.setResultCode("0");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };
        // Run the test
        final ResponseChargeTicketDTO result = viettelPayServiceImplUnderTest.chargeTicketVTP(authentication, requestChargeTicketVTPDTO);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testChargeTicketVTP_OcsNull_ThrowsEtcException() throws Exception {
        // Setup
        final Authentication authentication = null;
        final RequestChargeTicketVTPDTO requestChargeTicketVTPDTO = new RequestChargeTicketVTPDTO();
        requestChargeTicketVTPDTO.setOrderId("123");
        requestChargeTicketVTPDTO.setBillingCode("123");
        requestChargeTicketVTPDTO.setRequestTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        requestChargeTicketVTPDTO.setToken("token");
        requestChargeTicketVTPDTO.setContractId("12");
        requestChargeTicketVTPDTO.setMsisdn("123456");
        requestChargeTicketVTPDTO.setBankCodeAfter("1223");
        requestChargeTicketVTPDTO.setAmount(0L);
        requestChargeTicketVTPDTO.setCode("12");
        requestChargeTicketVTPDTO.setMessage("message");

        final ResponseChargeTicketDTO expectedResult = new ResponseChargeTicketDTO(new ResponseChargeTicketDTO.ResponseStatus("code", "message", "description", "responseTime"), new ResponseChargeTicketDTO.ResponseData("orderId", 0L, "billingCode", "msisdn"));

        // Configure SaleOrderRepositoryJPA.findBySaleOrderId(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        saleOrderEntity.setContractId(0L);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderId(any())).thenReturn(saleOrderEntity);

        // Configure SaleTransRepositoryJPA.save(...).
        final SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(0L);
        saleTransEntity.setSaleTransCode("saleTransCode");
        saleTransEntity.setSaleTransDate(new Date(0L));
        saleTransEntity.setSaleTransType("saleTransType");
        saleTransEntity.setStatus("status");
        saleTransEntity.setInvoiceUsed("invoiceUsed");
        saleTransEntity.setInvoiceCreateDate(new Date(0L));
        saleTransEntity.setShopId(0L);
        saleTransEntity.setShopName("shopName");
        saleTransEntity.setStaffId(0L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure OCSService.charge(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.charge(new AddSupOfferRequestDTO(), null, 0L, "partyCode")).thenReturn(ocsResponse);

        // Configure SaleOrderDetailRepositoryJPA.findAllBySaleOrderId(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("112321");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(saleOrderDetailEntities);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber12");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, VehicleEntity.Status.ACTIVATED.value, "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        // Configure OCSService.addSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenReturn(ocsResponse1);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse2);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse3 = new OCSResponse();
        ocsResponse3.resultCode("resultCode");
        ocsResponse3.setResultCode("resultCode");
        ocsResponse3.description("description");
        ocsResponse3.setDescription("description");
        ocsResponse3.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse3);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("plateNumber123");
        saleTransDetailEntity.setEpc("epc");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        when(mockSaleTransDetailRepositoryJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity1);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void chargeOCS(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, long contractId, String partyCode) throws Exception {
            }
        };
        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("01");
        ocsResponseaddSupOffer.setResultCode("01");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };
        // Run the test
        final ResponseChargeTicketDTO result = viettelPayServiceImplUnderTest.chargeTicketVTP(authentication, requestChargeTicketVTPDTO);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testChargeTicketVTP_ThrowsStatusActiveValueLine862() throws Exception {
        // Setup
        final Authentication authentication = null;
        final RequestChargeTicketVTPDTO requestChargeTicketVTPDTO = new RequestChargeTicketVTPDTO();
        requestChargeTicketVTPDTO.setOrderId("123");
        requestChargeTicketVTPDTO.setBillingCode("123");
        requestChargeTicketVTPDTO.setRequestTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        requestChargeTicketVTPDTO.setToken("token");
        requestChargeTicketVTPDTO.setContractId("12");
        requestChargeTicketVTPDTO.setMsisdn("123456");
        requestChargeTicketVTPDTO.setBankCodeAfter("1223");
        requestChargeTicketVTPDTO.setAmount(0L);
        requestChargeTicketVTPDTO.setCode("12");
        requestChargeTicketVTPDTO.setMessage("message");

        final ResponseChargeTicketDTO expectedResult = new ResponseChargeTicketDTO(new ResponseChargeTicketDTO.ResponseStatus("code", "message", "description", "responseTime"), new ResponseChargeTicketDTO.ResponseData("orderId", 0L, "billingCode", "msisdn"));

        // Configure SaleOrderRepositoryJPA.findBySaleOrderId(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        saleOrderEntity.setContractId(0L);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderId(any())).thenReturn(saleOrderEntity);

        // Configure SaleTransRepositoryJPA.save(...).
        final SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(0L);
        saleTransEntity.setSaleTransCode("saleTransCode");
        saleTransEntity.setSaleTransDate(new Date(0L));
        saleTransEntity.setSaleTransType("saleTransType");
        saleTransEntity.setStatus("status");
        saleTransEntity.setInvoiceUsed("invoiceUsed");
        saleTransEntity.setInvoiceCreateDate(new Date(0L));
        saleTransEntity.setShopId(0L);
        saleTransEntity.setShopName("shopName");
        saleTransEntity.setStaffId(0L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure OCSService.charge(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.charge(new AddSupOfferRequestDTO(), null, 0L, "partyCode")).thenReturn(ocsResponse);

        // Configure SaleOrderDetailRepositoryJPA.findAllBySaleOrderId(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("112321");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(saleOrderDetailEntities);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber12");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure OCSService.addSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenReturn(ocsResponse1);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse2);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse3 = new OCSResponse();
        ocsResponse3.resultCode("resultCode");
        ocsResponse3.setResultCode("resultCode");
        ocsResponse3.description("description");
        ocsResponse3.setDescription("description");
        ocsResponse3.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse3);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("plateNumber123");
        saleTransDetailEntity.setEpc("epc");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        when(mockSaleTransDetailRepositoryJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity1);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void chargeOCS(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, long contractId, String partyCode) throws Exception {
            }
        };
        // Run the test
        // Verify the results
        final Object result = viettelPayServiceImplUnderTest.chargeTicketVTP(authentication, requestChargeTicketVTPDTO);
        assertNotNull(result);
    }

    @Test
    void testChargeTicketVTP_ThrowsPlateNumberDuplicate() throws Exception {
        // Setup
        final Authentication authentication = null;
        final RequestChargeTicketVTPDTO requestChargeTicketVTPDTO = new RequestChargeTicketVTPDTO();
        requestChargeTicketVTPDTO.setOrderId("123");
        requestChargeTicketVTPDTO.setBillingCode("123");
        requestChargeTicketVTPDTO.setRequestTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        requestChargeTicketVTPDTO.setToken("token");
        requestChargeTicketVTPDTO.setContractId("12");
        requestChargeTicketVTPDTO.setMsisdn("123456");
        requestChargeTicketVTPDTO.setBankCodeAfter("1223");
        requestChargeTicketVTPDTO.setAmount(0L);
        requestChargeTicketVTPDTO.setCode("12");
        requestChargeTicketVTPDTO.setMessage("message");

        final ResponseChargeTicketDTO expectedResult = new ResponseChargeTicketDTO(new ResponseChargeTicketDTO.ResponseStatus("code", "message", "description", "responseTime"), new ResponseChargeTicketDTO.ResponseData("orderId", 0L, "billingCode", "msisdn"));

        // Configure SaleOrderRepositoryJPA.findBySaleOrderId(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        saleOrderEntity.setContractId(0L);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderId(any())).thenReturn(saleOrderEntity);

        // Configure SaleTransRepositoryJPA.save(...).
        final SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(0L);
        saleTransEntity.setSaleTransCode("saleTransCode");
        saleTransEntity.setSaleTransDate(new Date(0L));
        saleTransEntity.setSaleTransType("saleTransType");
        saleTransEntity.setStatus("status");
        saleTransEntity.setInvoiceUsed("invoiceUsed");
        saleTransEntity.setInvoiceCreateDate(new Date(0L));
        saleTransEntity.setShopId(0L);
        saleTransEntity.setShopName("shopName");
        saleTransEntity.setStaffId(0L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure OCSService.charge(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.charge(new AddSupOfferRequestDTO(), null, 0L, "partyCode")).thenReturn(ocsResponse);

        // Configure SaleOrderDetailRepositoryJPA.findAllBySaleOrderId(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(saleOrderDetailEntities);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure OCSService.addSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenReturn(ocsResponse1);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse2);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse3 = new OCSResponse();
        ocsResponse3.resultCode("resultCode");
        ocsResponse3.setResultCode("resultCode");
        ocsResponse3.description("description");
        ocsResponse3.setDescription("description");
        ocsResponse3.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse3);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("plateNumber");
        saleTransDetailEntity.setEpc("epc");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        when(mockSaleTransDetailRepositoryJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity1);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private void chargeOCS(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, long contractId, String partyCode) throws Exception {
            }
        };
        // Run the test
        // Verify the results
        final Object result = viettelPayServiceImplUnderTest.chargeTicketVTP(authentication, requestChargeTicketVTPDTO);
        assertNotNull(result);
    }

    @Ignore
    void testChargeTicketVTP_ThrowsEtcException() throws Exception {
        // Setup
        final Authentication authentication = null;
        final RequestChargeTicketVTPDTO requestChargeTicketVTPDTO = new RequestChargeTicketVTPDTO();
        requestChargeTicketVTPDTO.setOrderId("123");
        requestChargeTicketVTPDTO.setBillingCode("123");
        requestChargeTicketVTPDTO.setRequestTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        requestChargeTicketVTPDTO.setToken("token");
        requestChargeTicketVTPDTO.setContractId("12");
        requestChargeTicketVTPDTO.setMsisdn("123456");
        requestChargeTicketVTPDTO.setBankCodeAfter("1223");
        requestChargeTicketVTPDTO.setAmount(0L);
        requestChargeTicketVTPDTO.setCode("12");
        requestChargeTicketVTPDTO.setMessage("message");

        final ResponseChargeTicketDTO expectedResult = new ResponseChargeTicketDTO(new ResponseChargeTicketDTO.ResponseStatus("code", "message", "description", "responseTime"), new ResponseChargeTicketDTO.ResponseData("orderId", 0L, "billingCode", "msisdn"));

        // Configure SaleOrderRepositoryJPA.findBySaleOrderId(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("2");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderId(any())).thenReturn(saleOrderEntity);

        // Configure SaleTransRepositoryJPA.save(...).
        final SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(0L);
        saleTransEntity.setSaleTransCode("saleTransCode");
        saleTransEntity.setSaleTransDate(new Date(0L));
        saleTransEntity.setSaleTransType("saleTransType");
        saleTransEntity.setStatus("status");
        saleTransEntity.setInvoiceUsed("invoiceUsed");
        saleTransEntity.setInvoiceCreateDate(new Date(0L));
        saleTransEntity.setShopId(0L);
        saleTransEntity.setShopName("shopName");
        saleTransEntity.setStaffId(0L);
        when(mockSaleTransRepositoryJPA.save(new SaleTransEntity())).thenReturn(saleTransEntity);

        // Configure OCSService.charge(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.charge(new AddSupOfferRequestDTO(), null, 0L, "partyCode")).thenReturn(ocsResponse);

        // Configure SaleOrderDetailRepositoryJPA.findAllBySaleOrderId(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(0L)).thenReturn(saleOrderDetailEntities);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure OCSService.addSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenReturn(ocsResponse1);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse2);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse3 = new OCSResponse();
        ocsResponse3.resultCode("resultCode");
        ocsResponse3.setResultCode("resultCode");
        ocsResponse3.description("description");
        ocsResponse3.setDescription("description");
        ocsResponse3.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse3);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("plateNumber");
        saleTransDetailEntity.setEpc("epc");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        when(mockSaleTransDetailRepositoryJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity1);

        // Run the test
        // Verify the results
        final Object result = viettelPayServiceImplUnderTest.chargeTicketVTP(authentication, requestChargeTicketVTPDTO);
        assertNotNull(result);
    }

    @Test
    void testConfirmChargeTicket() throws IOException {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        addSupOfferRequest.setActTypeId(2L);
        addSupOfferRequest.setAcountETC(true);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        servicePlanDTO.setScope(1L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(any())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);
        final ContractEntity contractEntity = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.findByContractId(0L)).thenReturn(contractEntity);
        testAddSaleOrder();
        /*new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private SaleOrderEntity addSaleOrder(AddSupOfferRequestDTO addSupOfferRequest, String userLogin, Date date, Long customerId, Long contractId) {
                return saleOrderEntity;
            }
        }; */

        final  ServicePlanFeeDTO ticketInfo = new ServicePlanFeeDTO();
        ticketInfo.setListServicePlan(servicePlanDTOS);
//        ticketInfo.setServicePlanVehicleDuplicate("duplicate");
        final RequestGetFeeChargeTicketDTO requestGetFeeChargeTicket = new RequestGetFeeChargeTicketDTO();
        requestGetFeeChargeTicket.setServicePlanDTOList(servicePlanDTOS);
        when(mockServicePlanService.getFeeNew(any(), any())).thenReturn(ticketInfo);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private long getTotalCharge(List<ServicePlanDTO> list) {
                return 0L;
            }
        };

        // Run the test
        final ResAddSupOfferDTO result = viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testConfirmChargeTicket_ThrowsPriceExist() throws IOException {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        addSupOfferRequest.setActTypeId(2L);
        addSupOfferRequest.setAcountETC(true);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        servicePlanDTO.setScope(1L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(any())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);
        final ContractEntity contractEntity = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.findByContractId(0L)).thenReturn(contractEntity);
        testAddSaleOrder();
        /*new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private SaleOrderEntity addSaleOrder(AddSupOfferRequestDTO addSupOfferRequest, String userLogin, Date date, Long customerId, Long contractId) {
                return saleOrderEntity;
            }
        }; */

        final  ServicePlanFeeDTO ticketInfo = new ServicePlanFeeDTO();
        ticketInfo.setListServicePlan(servicePlanDTOS);
        ticketInfo.setServicePlanVehicleDuplicate("duplicate");
        final RequestGetFeeChargeTicketDTO requestGetFeeChargeTicket = new RequestGetFeeChargeTicketDTO();
        requestGetFeeChargeTicket.setServicePlanDTOList(servicePlanDTOS);
        when(mockServicePlanService.getFeeNew(any(), any())).thenReturn(ticketInfo);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private long getTotalCharge(List<ServicePlanDTO> list) {
                return 0L;
            }
        };

        // Run the test
//        final ResAddSupOfferDTO result = viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L);

        // Verify the results
//        assertEquals(expectedResult, result);
        assertThrows(EtcException.class, () -> viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L));
    }

    @Test
    void testConfirmChargeTicket_ThrowsPriceIsWrong() throws IOException {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        addSupOfferRequest.setActTypeId(2L);
        addSupOfferRequest.setAcountETC(true);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        servicePlanDTO.setScope(1L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(any())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);
        final ContractEntity contractEntity = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractRepositoryJPA.findByContractId(0L)).thenReturn(contractEntity);
        testAddSaleOrder();
        /*new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private SaleOrderEntity addSaleOrder(AddSupOfferRequestDTO addSupOfferRequest, String userLogin, Date date, Long customerId, Long contractId) {
                return saleOrderEntity;
            }
        }; */

        final  ServicePlanFeeDTO ticketInfo = new ServicePlanFeeDTO();
        ticketInfo.setListServicePlan(servicePlanDTOS);
//        ticketInfo.setServicePlanVehicleDuplicate("duplicate");
        final RequestGetFeeChargeTicketDTO requestGetFeeChargeTicket = new RequestGetFeeChargeTicketDTO();
        requestGetFeeChargeTicket.setServicePlanDTOList(servicePlanDTOS);
        when(mockServicePlanService.getFeeNew(any(), any())).thenReturn(ticketInfo);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private long getTotalCharge(List<ServicePlanDTO> list) {
                return 1L;
            }
        };

        // Run the test
//        final ResAddSupOfferDTO result = viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L);

        // Verify the results
//        assertEquals(expectedResult, result);
        when(jedisCacheService.getMessageErrorByKey("crm.total.price.is.wrong")).thenReturn("crm.total.price.is.wrong");
        assertThrows(EtcException.class, () -> viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L));
    }

    @Test
    void testConfirmChargeTicket_AddSaleOrder() {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        servicePlanDTO.setScope(1L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(any())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "CRM";
            }
        };

        // Run the test
        // Verify the results
        assertThrows(Exception.class, () -> viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L));
    }

    @Test
    void testConfirmChargeTicket_PlateNumberContains() {
        // Setup
        AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        vehicleAddSupOfferViettelPayDTO.setAutoRenew("1");
        vehicleAddSupOfferViettelPayDTO.setServicePlanTypeId(1L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(any())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private SaleOrderEntity addSaleOrder(AddSupOfferRequestDTO addSupOfferRequest, String userLogin, Date date, Long customerId, Long contractId) {
                return saleOrderEntity;
            }
        };

        // Run the test
        assertThrows(EtcException.class, () -> viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L));
    }

    @Test
    void testConfirmChargeTicket_ThrowsEtcException() {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(new ServicePlanDTO())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private SaleOrderEntity addSaleOrder(AddSupOfferRequestDTO addSupOfferRequest, String userLogin, Date date, Long customerId, Long contractId) {
                return saleOrderEntity;
            }
        };

        // Run the test
        // Verify the results
        assertThrows(EtcException.class, () -> viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L));
    }

    @Test
    void testConfirmChargeTicket_SaleOrderRepositoryJPAReturnsNull() {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        final VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        final ResAddSupOfferDTO expectedResult = new ResAddSupOfferDTO();
        expectedResult.setBillingCode(0L);

        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(mockSaleOrderRepositoryJPA.save(any())).thenReturn(saleOrderEntity);

        // Configure VehicleRepositoryJPA.findByVehicleId(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleRepositoryJPA.findByVehicleId(0L)).thenReturn(vehicleEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(mockTicketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure TicketRepository.getFee(...).
        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        final List<ServicePlanDTO> servicePlanDTOS = Arrays.asList(servicePlanDTO);
        when(mockTicketRepository.getFee(new ServicePlanDTO())).thenReturn(servicePlanDTOS);

        // Configure SaleOrderDetailRepositoryJPA.saveAll(...).
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity);
        when(mockSaleOrderDetailRepositoryJPA.saveAll(Arrays.asList(new SaleOrderDetailEntity()))).thenReturn(saleOrderDetailEntities);

        // Run the test
        // Verify the results
        assertThrows(EtcException.class, () -> viettelPayServiceImplUnderTest.confirmChargeTicket(addSupOfferRequest, authentication, 0L, 0L));
    }

//    @Test
//    void testCreateAddMoneyOrder() throws SignatureException {
//        // Setup
//        final RequestAddSupOfferDTO addSupOfferRequest = new RequestAddSupOfferDTO();
//        addSupOfferRequest.setContractNo("contractNo");
//        addSupOfferRequest.setAmount(0L);
//        addSupOfferRequest.setQuantity(0L);
//        final VehicleAddSupOfferViettelPayDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSupOfferViettelPayDTO();
//        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
//        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
//        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
//        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
//        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
//        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
//        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
//        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
//        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
//        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
//        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));
//
//        final Authentication authentication = null;
//
//        // Configure SaleOrderRepositoryJPA.save(...).
//        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
//        saleOrderEntity.setSaleOrderId(0L);
//        saleOrderEntity.setSaleOrderDate(new Date(0L));
//        saleOrderEntity.setSaleOrderType("saleOrderType");
//        saleOrderEntity.setSaleOrderSource("saleOrderSource");
//        saleOrderEntity.setStatus("status");
//        saleOrderEntity.setMethodRechargeId(0L);
//        saleOrderEntity.setPaymentMethodId(0L);
//        saleOrderEntity.setAmount(0L);
//        saleOrderEntity.setQuantity(0L);
//        saleOrderEntity.setDiscount(0L);
//        when(mockSaleOrderRepositoryJPA.save(any())).thenReturn(saleOrderEntity);
//
//        // Run the test
//        final Object result = viettelPayServiceImplUnderTest.createSaleOrder(addSupOfferRequest, authentication, 0L, 0L);
//
//        // Verify the results
//    }

    @Test
    void testResponseAddMoney() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("1");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);
        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testResponseAddMoney_SaleOrderType2() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("2");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(0L);
        saleTransEntity.setContractId(1L);
        saleTransEntity.setSaleTransId(1L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(any())).thenReturn(saleOrderEntity2);

        SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setVehicleId(1L);
        saleOrderDetailEntity.setPlateNumber("12345");
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(Arrays.asList(saleOrderDetailEntity));

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setStatus("1");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("12345");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("0");
        ocsResponseaddSupOffer.setResultCode("0");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Ignore
    void testResponseAddMoney_SaleOrderType2_If1() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("2");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(0L);
        saleTransEntity.setContractId(1L);
        saleTransEntity.setSaleTransId(1L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(any())).thenReturn(saleOrderEntity2);

        SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setVehicleId(1L);
        saleOrderDetailEntity.setPlateNumber("123456");
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(Arrays.asList(saleOrderDetailEntity));

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setStatus("2");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("12345");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("0");
        ocsResponseaddSupOffer.setResultCode("0");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Ignore
    void testResponseAddMoney_SaleOrderType2_If2() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("2");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(0L);
        saleTransEntity.setContractId(1L);
        saleTransEntity.setSaleTransId(1L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(any())).thenReturn(saleOrderEntity2);

        SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setVehicleId(1L);
        saleOrderDetailEntity.setPlateNumber("123456");
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(Arrays.asList(saleOrderDetailEntity));

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setStatus("1");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("12345");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("0");
        ocsResponseaddSupOffer.setResultCode("0");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testResponseAddMoney_SaleOrderType2_If2_ocsResponse_null() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("2");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(0L);
        saleTransEntity.setContractId(1L);
        saleTransEntity.setSaleTransId(1L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(any())).thenReturn(saleOrderEntity2);

        SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setVehicleId(1L);
        saleOrderDetailEntity.setPlateNumber("123456");
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(Arrays.asList(saleOrderDetailEntity));

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setStatus("1");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("12345");
        when(mockTicketRepository.checkExistsTicket(any(), anyBoolean())).thenReturn(servicePlanVehicleDuplicateDTO);

        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("01");
        ocsResponseaddSupOffer.setResultCode("01");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testResponseAddMoney_SaleOrderType2_AddSubOfferFail() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("2");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(0L);
        saleTransEntity.setContractId(1L);
        saleTransEntity.setSaleTransId(1L);
        when(mockSaleTransRepositoryJPA.save(any())).thenReturn(saleTransEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(any())).thenReturn(saleOrderEntity2);

        SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setVehicleId(1L);
        when(mockSaleOrderDetailRepositoryJPA.findAllBySaleOrderId(any())).thenReturn(Arrays.asList(saleOrderDetailEntity));

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setStatus("1");
        when(mockVehicleRepositoryJPA.findByVehicleId(any())).thenReturn(vehicleEntity);

        final OCSResponse ocsResponseaddSupOffer = new OCSResponse();
        ocsResponseaddSupOffer.resultCode("01");
        ocsResponseaddSupOffer.setResultCode("01");
        ocsResponseaddSupOffer.description("description");
        ocsResponseaddSupOffer.setDescription("description");
        ocsResponseaddSupOffer.setSubscriptionTicketId("subscriptionTicketId");
        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
                return ocsResponseaddSupOffer;
            }
        };

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testResponseAddMoney_OcsFail() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderTest = new SaleOrderEntity();
        saleOrderTest.setSaleOrderId(0L);
        saleOrderTest.setSaleOrderDate(new Date(0L));
        saleOrderTest.setSaleOrderType("1");
        saleOrderTest.setSaleOrderSource("1");
        saleOrderTest.setStatus("2");
        saleOrderTest.setMethodRechargeId(0L);
        saleOrderTest.setPaymentMethodId(0L);
        saleOrderTest.setAmount(0L);
        saleOrderTest.setQuantity(0L);
        saleOrderTest.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrder = Optional.of(saleOrderTest);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrder);
        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("01");
        ocsResponse.setResultCode("01");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

//    @Test
//    void testResponseAddMoney_ThrowsException() {
//        // Setup
//        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
//        viettelPayRequestDTO.setBillcode("billcode");
//        viettelPayRequestDTO.setCust_msisdn("cust_msisdn");
//        viettelPayRequestDTO.setError_code("error_code");
//        viettelPayRequestDTO.setMerchant_code("merchant_code");
//        viettelPayRequestDTO.setOrder_id("order_id");
//        viettelPayRequestDTO.setPayment_status(0L);
//        viettelPayRequestDTO.setTrans_amount(0L);
//        viettelPayRequestDTO.setVt_transaction_id("vt_transaction_id");
//        viettelPayRequestDTO.setCheck_sum("check_sum");
//
//        final Authentication authentication = null;
//
//        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
//        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
//        saleOrderEntity1.setSaleOrderId(0L);
//        saleOrderEntity1.setSaleOrderDate(new Date(0L));
//        saleOrderEntity1.setSaleOrderType("saleOrderType");
//        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
//        saleOrderEntity1.setStatus("status");
//        saleOrderEntity1.setMethodRechargeId(0L);
//        saleOrderEntity1.setPaymentMethodId(0L);
//        saleOrderEntity1.setAmount(0L);
//        saleOrderEntity1.setQuantity(0L);
//        saleOrderEntity1.setDiscount(0L);
//        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
//        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);
//
//        // Configure OCSService.addBalance(...).
//        final OCSResponse ocsResponse = new OCSResponse();
//        ocsResponse.resultCode("resultCode");
//        ocsResponse.setResultCode("resultCode");
//        ocsResponse.description("description");
//        ocsResponse.setDescription("description");
//        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
//        when(mockOcsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse);
//
//        // Configure SaleOrderServiceJPA.save(...).
//        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
//        saleOrderEntity2.setSaleOrderId(0L);
//        saleOrderEntity2.setSaleOrderDate(new Date(0L));
//        saleOrderEntity2.setSaleOrderType("saleOrderType");
//        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
//        saleOrderEntity2.setStatus("status");
//        saleOrderEntity2.setMethodRechargeId(0L);
//        saleOrderEntity2.setPaymentMethodId(0L);
//        saleOrderEntity2.setAmount(0L);
//        saleOrderEntity2.setQuantity(0L);
//        saleOrderEntity2.setDiscount(0L);
//        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);
//
//        // Run the test
//        assertThrows(Exception.class, () -> viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication));
//    }

    @Test
    void testResponseAddMoney_SaleOrderRepositoryJPAReturnsAbsent() throws Exception {
        // Setup
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(any(), any(), any(), any())).thenReturn(saleOrderEntity);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

    }

    @Test
    void testResponseAddMoney_SaleOrderRepositoryJPAReturnsAbsent_OcsNUll() throws Exception {
        // Setup
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(any(), any(), any(), any())).thenReturn(saleOrderEntity);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenReturn(null);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

    }

    @Ignore
    void testResponseAddMoney_SaleOrderRepositoryJPAReturnsAbsent_OcsException() throws Exception {
        // Setup
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("1234");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("00");
        viettelPayRequestDTO.setMerchant_code("12421");
        viettelPayRequestDTO.setOrder_id("1234");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("1");
        viettelPayRequestDTO.setCheck_sum("asczxvx345345");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.ResponseAddMoneyResultData expectedResult = new ResponseViettelPayDTO.ResponseAddMoneyResultData("errorCode", "merchantCode", "orderId", "returnUrl", "returnBillCode", "extraInfo", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(any(), any(), any(), any())).thenReturn(saleOrderEntity);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addBalance(any(), any(), any(), any())).thenThrow(new EtcException("ascas"));

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "zxnasjdhkjsqw123";
            }
        };

        // Run the test
        final ResponseViettelPayDTO.ResponseAddMoneyResultData result = viettelPayServiceImplUnderTest.resultSaleOrder(viettelPayRequestDTO, authentication);

    }

    @Test
    void testVerifyViettelPayRequestData() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("12345");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("1111");
        viettelPayRequestDTO.setMerchant_code("11111");
        viettelPayRequestDTO.setOrder_id("12345");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("111122222");
        viettelPayRequestDTO.setCheck_sum("aasczxczxc");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.VerifyViettelPayData expectedResult = new ResponseViettelPayDTO.VerifyViettelPayData("billCode", "merchantCode", "orderId", "transAmount", "errorCode", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "asdzxcq243423";
            }
        };
        // Run the test
        final ResponseViettelPayDTO.VerifyViettelPayData result = viettelPayServiceImplUnderTest.verifySaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testVerifyViettelPayRequestData_NotEmpty() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("12345");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("1111");
        viettelPayRequestDTO.setMerchant_code("11111");
        viettelPayRequestDTO.setOrder_id("12345");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("111122222");
        viettelPayRequestDTO.setCheck_sum("aasczxczxc");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.VerifyViettelPayData expectedResult = new ResponseViettelPayDTO.VerifyViettelPayData("billCode", "merchantCode", "orderId", "transAmount", "errorCode", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrderEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "asdzxcq243423";
            }
        };
        // Run the test
        final ResponseViettelPayDTO.VerifyViettelPayData result = viettelPayServiceImplUnderTest.verifySaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testVerifyViettelPayRequestData_SaleOrderRepositoryJPAReturnsAbsent() throws Exception {
        // Setup
        final ViettelPayRequestDTO viettelPayRequestDTO = new ViettelPayRequestDTO();
        viettelPayRequestDTO.setBillcode("12345");
        viettelPayRequestDTO.setCust_msisdn("1234567");
        viettelPayRequestDTO.setError_code("1111");
        viettelPayRequestDTO.setMerchant_code("11111");
        viettelPayRequestDTO.setOrder_id("12345");
        viettelPayRequestDTO.setPayment_status(0L);
        viettelPayRequestDTO.setTrans_amount(0L);
        viettelPayRequestDTO.setVt_transaction_id("111122222");
        viettelPayRequestDTO.setCheck_sum("aasczxczxc");

        final Authentication authentication = null;
        final ResponseViettelPayDTO.VerifyViettelPayData expectedResult = new ResponseViettelPayDTO.VerifyViettelPayData("billCode", "merchantCode", "orderId", "transAmount", "errorCode", "checkSum");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(0L, "saleOrderType", "saleOrderSource", "status")).thenReturn(saleOrderEntity);

        when(mockSaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(any(), any())).thenReturn(saleOrderEntity);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(mockSaleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String calculateRFC2104HMAC(String data, String key)
                    throws SignatureException {
                return "aasczxczxc";
            }
        };
        // Run the test
        final ResponseViettelPayDTO.VerifyViettelPayData result = viettelPayServiceImplUnderTest.verifySaleOrder(viettelPayRequestDTO, authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testGetInfoSubscriptionsExtendedViaSDKPrivateStream_ThrowsJsonProcessingException() {
        // Setup
        final Authentication authentication = null;
        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream("billingCode")).thenReturn("result");

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes("token")).thenReturn(dataTypes);

        when(mockVtPayRepository.getTicketExtendedViaSDKPrivateStream("orderId")).thenReturn("result");

        // Run the test
        assertThrows(JsonSyntaxException.class, () -> viettelPayServiceImplUnderTest.getInfoSubscriptionsExtendedViaSDKPrivateStream(authentication, "billingCode"));
    }

    @Test
    void testGetInfoSubscriptionsExtendedViaSDKPrivateStream() throws JsonProcessingException {
        // Setup
        final Authentication authentication = null;

        ResponseGetInfoTicketPurchaseAndExtendedDTO response = new ResponseGetInfoTicketPurchaseAndExtendedDTO();
        response.setAmount(1L);
        response.setOrderId("12345");
        response.setBillingCode("12345");
        response.setContractId(1L);
        response.setMsisdn("123456789");
        response.setToken("acbdv12345");

        when(mockVtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream(any())).thenReturn(response);

        // Configure TicketService.getTicketTypes(...).
        final List<DataTypeDTO.DataType> dataTypes = Arrays.asList(new DataTypeDTO.DataType(0L, "code", "val", "is_default"));
        when(mockTicketService.getTicketTypes(any())).thenReturn(dataTypes);
        String res = "{data:[{\n" +
                "    \"id\": \"1\",\n" +
                "    \"code\": \"abc123\",\n" +
                "    \"val\": \"TOP\",\n" +
                "    \"is_default\": 1\n" +
                "}]}";
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                return res;
            }
        };
        ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder ticketOrder = new ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder(
                "1234",
                "1",
                "12345",
                "123456",
                "1111",
                1L,
                1L,
                1L,
                1L,
                1L,
                "name",
                "stationNameOut",
                "startDate",
                "endDate",
                "epc"
        );

        when(mockVtPayRepository.getTicketExtendedViaSDKPrivateStream(any())).thenReturn(Arrays.asList(ticketOrder));

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StageBooDTO getStageById(String token, String stageId) {
                return new StageBooDTO(new StageBooDTO.Stage("name", 0L, 0L, 0L, 0L));
            }
        };

        new MockUp<ViettelPayServiceImpl>() {
            @mockit.Mock
            private StationBooDTO getStationById(String token, String stationId) {
                return new StationBooDTO(new StationBooDTO.Station("name", 0L, 0L));
            }
        };

        // Run the test
        final Object result = viettelPayServiceImplUnderTest.getInfoSubscriptionsExtendedViaSDKPrivateStream(authentication, "billingCode");
    }

    @Test
    void testGetSaleOrderDetail() {
        // Setup
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(0L);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");

        final Authentication authentication = null;
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> expectedResult = Arrays.asList(saleOrderDetailEntity);

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("id", "1234");
        doReturn(linkedHashMap).when(mockStageService).findByStationInAndStationOut(any(), any(), any());

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(any(), any(), any(), any(), any())).thenReturn(saleOrderDetailEntities1);

        // Run the test
        final List<SaleOrderDetailEntity> result1 = viettelPayServiceImplUnderTest.getSaleOrderDetail(autoRenewVtpDTO, authentication);

        // Verify the results
        assertNotNull(result1);
    }

    @Test
    void testGetSaleOrderDetail_NotNull() {
        // Setup
        final AutoRenewVtpDTO autoRenewVtpDTO = new AutoRenewVtpDTO();
        autoRenewVtpDTO.setTicket_type(0L);
        autoRenewVtpDTO.setPlateNumber("plateNumber");
        autoRenewVtpDTO.setAmount(0L);
        autoRenewVtpDTO.setStationInId(0L);
        autoRenewVtpDTO.setStationOutId(null);
        autoRenewVtpDTO.setEffDate("effDate");
        autoRenewVtpDTO.setExpDate("expDate");

        final Authentication authentication = null;
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> expectedResult = Arrays.asList(saleOrderDetailEntity);

        // Configure SaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity1 = new SaleOrderDetailEntity();
        saleOrderDetailEntity1.setSaleOrderDetailId(0L);
        saleOrderDetailEntity1.setSaleOrderId(0L);
        saleOrderDetailEntity1.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity1.setServiceFeeId(0L);
        saleOrderDetailEntity1.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity1.setVehicleId(0L);
        saleOrderDetailEntity1.setPlateNumber("plateNumber");
        saleOrderDetailEntity1.setEpc("epc");
        saleOrderDetailEntity1.setTid("tid");
        saleOrderDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities = Arrays.asList(saleOrderDetailEntity1);
        when(mockSaleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate")).thenReturn(saleOrderDetailEntities);

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("id", "1234");
        doReturn(linkedHashMap).when(mockStageService).findByStationInAndStationOut(any(), any(), any());

        // Configure SaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(...).
        final SaleOrderDetailEntity saleOrderDetailEntity2 = new SaleOrderDetailEntity();
        saleOrderDetailEntity2.setSaleOrderDetailId(0L);
        saleOrderDetailEntity2.setSaleOrderId(0L);
        saleOrderDetailEntity2.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity2.setServiceFeeId(0L);
        saleOrderDetailEntity2.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity2.setVehicleId(0L);
        saleOrderDetailEntity2.setPlateNumber("plateNumber");
        saleOrderDetailEntity2.setEpc("epc");
        saleOrderDetailEntity2.setTid("tid");
        saleOrderDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> saleOrderDetailEntities1 = Arrays.asList(saleOrderDetailEntity2);
        when(mockSaleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(any(), any(), any(), any(), any())).thenReturn(saleOrderDetailEntities1);

        // Run the test
        final List<SaleOrderDetailEntity> result1 = viettelPayServiceImplUnderTest.getSaleOrderDetail(autoRenewVtpDTO, authentication);

        // Verify the results
        assertNotNull(result1);
    }

    @Test
    void testHandleOcsAutoRenewVtp() throws Exception {
        // Setup
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> result = Arrays.asList(saleOrderDetailEntity);
        final Authentication authentication = null;

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        saleOrderEntity1.setContractId(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(any())).thenReturn(saleOrderEntity);

        // Configure OCSService.changeSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.changeSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, "staffName", "isRecurring")).thenReturn(ocsResponse);

        // Run the test
        final Long result1 = viettelPayServiceImplUnderTest.handleOcsAutoRenewVtp(result, authentication, "isCancel");

        // Verify the results
        assertEquals(0L, result1);
    }

    @Test
    void testHandleOcsAutoRenewVtp_OCSServiceThrowsException() throws Exception {
        // Setup
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> result = Arrays.asList(saleOrderDetailEntity);
        final Authentication authentication = null;

        // Configure SaleOrderServiceJPA.findById(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        saleOrderEntity1.setContractId(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(mockSaleOrderServiceJPA.findById(0L)).thenReturn(saleOrderEntity);

        when(mockOcsService.changeSupOffer(any(), any(), any(), any(), any(), any())).thenThrow(Exception.class);

        // Run the test
        final Long result1 = viettelPayServiceImplUnderTest.handleOcsAutoRenewVtp(result, authentication, "isCancel");

        // Verify the results
        assertEquals(0L, result1);
    }

    @Test
    void testAddSaleOrder() {
        AddSupOfferRequestDTO requestAddSupOfferDTO = new AddSupOfferRequestDTO();
        requestAddSupOfferDTO.setAmount(1L);
        requestAddSupOfferDTO.setContractNo("123");
        requestAddSupOfferDTO.setQuantity(1L);

        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        saleOrderEntity.setContractId(0L);
        when(mockSaleOrderRepositoryJPA.save(any())).thenReturn(saleOrderEntity);

        Object result = viettelPayServiceImplUnderTest.addSaleOrder(requestAddSupOfferDTO, "userLogin", null, 1L, 1L);
        assertNotNull(result);
    }

    @Ignore
    void addSupOfferOCS() throws Exception {
        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                Map<String, Object> map = new HashMap<>();
                map.put("staff_id", 1L);
                return map;
            }
        };

        // Configure OCSService.addSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(mockOcsService.addSupOffer(any(), any(), anyLong(), anyLong(), anyLong(), anyString(), anyString())).thenReturn(ocsResponse1);
        Object result = viettelPayServiceImplUnderTest.addSupOfferOCS(any(), any(), anyLong(), anyLong(), anyString(), anyString());
        assertNotNull(result);
    }

}
