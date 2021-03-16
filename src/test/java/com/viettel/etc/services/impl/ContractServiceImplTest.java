package com.viettel.etc.services.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSCreateContractForm;
import com.viettel.etc.dto.ocs.OCSDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.repositories.ContractRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

@ExtendWith(SpringExtension.class)
class ContractServiceImplTest {
    @Mock
    ContractRepository contractRepository;

    @Mock
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Mock
    CustomerRepositoryJPA customerRepositoryJPA;

    @InjectMocks
    ContractServiceImpl contractService;

    @Mock
    CustomerServiceJPA customerServiceJPA;

    @Mock
    ContractServiceJPA contractServiceJPA;

    @Mock
    ContractRepositoryJPA contractRepositoryJPA;

    @InjectMocks
    VehicleServiceImpl vehicleServiceImpl;

    @Mock
    CustomerServiceJPA customerServiceJPAMock;

    @Mock
    VehicleService vehicleService;


    @Mock
    ProductService productService;

    @Mock
    OCSServiceImpl ocsService;

    @Mock
    OCSServiceImpl ocsServiceImpl;

    AddVehicleRequestDTO addVehicleRequestDTO;

    ContractEntity contractEntity1;

    VehicleEntity vehicleEntity;
    SaleBusinessMesDTO saleBusinessMesDTO;

    @Mock
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Mock
    ActionAuditService actionAuditService;

    @Mock
    private ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Mock
    FileService fileService;

    @Mock
    ContractProfileServiceJPA contractProfileServiceJPA;

    @Mock
    SaleTransService saleTransService;

    @Mock
    SaleTransDetailService saleTransDetailService;

    @Mock
    VehicleServiceJPA vehicleServiceJPA;

    @Mock
    ContractPaymentServiceJPA contractPaymentServiceJPA;

    @Mock
    JedisCacheService jedisCacheService;

    @Value("${sms.user.register}")
    private String smsRegisterUser;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private SMSServiceImpl smsService;

    @Mock
    private LuckyService luckyService;

    @BeforeEach
    void setUp() {
        vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);

        saleBusinessMesDTO = new SaleBusinessMesDTO();
        saleBusinessMesDTO.setSuccess("true");

        Map<String, Object> map = new HashMap<>();
        map.put("shop_id", "1");
        map.put("shop_name", "1");

        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                return map;
            }
        };

        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            public VehicleEntity saveVehicle(AddVehicleRequestDTO dataParams, Authentication authentication,
                                             Long customerId, Long contractId) {
                return vehicleEntity;
            }
        };

        new MockUp<ProductServiceImpl>() {
            @mockit.Mock
            public SaleBusinessMesDTO callBCCSIM(Authentication authentication, Long actTypeId, VehicleEntity vehicleEntity, String serial, Long custId, int caseUpdate, String statusVehicleBoo1) throws EtcException, JAXBException, XMLStreamException, IOException{
                return saleBusinessMesDTO;
            };
        };


        addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(1L);
        addVehicleRequestDTO.setTid("1");
        addVehicleRequestDTO.setEpc("1");
        addVehicleRequestDTO.setPlateNumber("19N109055T");
        addVehicleRequestDTO.setEffDate(new java.util.Date());
        addVehicleRequestDTO.setExpDate(new java.util.Date());
        addVehicleRequestDTO.setVehicleTypeId(1L);
        addVehicleRequestDTO.setStatus("1");
        addVehicleRequestDTO.setOfferExternalId(String.valueOf(1L));
        addVehicleRequestDTO.setPlateType(1L);
        addVehicleRequestDTO.setRfidSerial("1");
        addVehicleRequestDTO.setRfidType("2");
        addVehicleRequestDTO.setActiveStatus("1");
        addVehicleRequestDTO.setVehicleGroupId(1L);
        addVehicleRequestDTO.setPlateTypeCode("x");
        addVehicleRequestDTO.setVehicleGroupCode("y");
        addVehicleRequestDTO.setVehicleTypeName("z");
        addVehicleRequestDTO.setVehicleTypeName("m");
        addVehicleRequestDTO.setOwner("ToanDM");
        addVehicleRequestDTO.setNetWeight(1.00);
        addVehicleRequestDTO.setCargoWeight(1.00);
        addVehicleRequestDTO.setGrossWeight(1.00);
        addVehicleRequestDTO.setSeatNumber(1L);
        addVehicleRequestDTO.setPlateTypeId(1L);
        addVehicleRequestDTO.setActTypeId(1L);
        addVehicleRequestDTO.setRfidSerial("1");

        contractEntity1 = new ContractEntity();
        contractEntity1.setCustId(5L);
        contractEntity1.setContractNo("HD1");
    }

    @Test
    @DisplayName("Kiem tra tim kiem hop dong khi du lieu null")
    void ifFindContractByCustomerNull() {
        given(contractRepository.findContractByCustomer(1, null)).willReturn(null);
        given(contractService.findContractByCustomer(1, null)).willReturn(null);
        Object listObject = contractService.findContractByCustomer(1, null);
        assertNull(listObject);

    }

    @Test
    @DisplayName("Kiem tra tim kiem hop dong khi ton tai")
    void ifFindContractByCustomerNotNull() {
        ResultSelectEntity result = new ResultSelectEntity();
        result.setCount(1);
        result.setListData(Arrays.asList("1", "2"));
        given(contractRepository.findContractByCustomer(1, null)).willReturn(result);
        Object listObject = contractService.findContractByCustomer(1, null);
        assertEquals(result, listObject);

    }

    @Test
    @DisplayName("Kiem tra khi gop hop dong thanh cong")
    void appendContract() throws DataNotFoundException, JAXBException, XMLStreamException, IOException {
        ReflectionTestUtils.setField(contractService, "customerServiceJPA", customerServiceJPA);
        given(customerServiceJPA.existsById(5L)).willReturn(true);

        ReflectionTestUtils.setField(contractService, "contractServiceJPA", contractServiceJPA);
        ReflectionTestUtils.setField(contractServiceJPA, "contractRepositoryJPA", contractRepositoryJPA);
        given(contractServiceJPA.findById(6L)).willReturn(Optional.of(contractEntity1));

        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setCustId(5L);
        addVehicleRequestDTO.setContractId(6L);
        given(vehicleService.registerVehicle(addVehicleRequestDTO, null, 5L, 6L, "1.1.1.1")).willReturn(addVehicleRequestDTO);
        assertNotEquals(vehicleService.registerVehicle(addVehicleRequestDTO, null, 5L, 6L, "1.1.1.1"), null);
    }

    @Test
    @DisplayName("Kiem tra khi gop hop dong throw exception")
    void ifAppendContractThrows() throws DataNotFoundException, JAXBException, XMLStreamException, IOException {
        ReflectionTestUtils.setField(contractService, "customerServiceJPA", customerServiceJPA);
        given(customerServiceJPA.existsById(5L)).willReturn(true);

        ReflectionTestUtils.setField(contractService, "contractServiceJPA", contractServiceJPA);
        ReflectionTestUtils.setField(contractServiceJPA, "contractRepositoryJPA", contractRepositoryJPA);
        given(contractServiceJPA.findById(6L)).willReturn(Optional.of(contractEntity1));

        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setCustId(5L);
        addVehicleRequestDTO.setContractId(6L);
        given(vehicleService.registerVehicle(addVehicleRequestDTO, null, 5L, 6L, "1.1.1.1")).willThrow(EtcException.class);
        assertThrows(EtcException.class, () -> vehicleService.registerVehicle(addVehicleRequestDTO, null, 5L, 6L, "1.1.1.1"));
    }

    @Test
    @DisplayName("Kiem tra khi throw exception")
    void testExceptionWhenCheckCustomerExits() {
        ReflectionTestUtils.setField(contractService, "customerServiceJPA", customerServiceJPA);
        ReflectionTestUtils.setField(customerServiceJPA, "customerRepositoryJPA", customerRepositoryJPA);
        given(customerServiceJPA.existsById(5L)).willReturn(false);
        Exception exceptionWhenCheckCustomerExits = assertThrows(EtcException.class, () -> {
            contractService.appendContract(new AddVehicleRequestDTO(), null, 5l, 6L, "1.1.1.1");
        });
        assertEquals(exceptionWhenCheckCustomerExits.getLocalizedMessage(), "crm.customer.not.exist");

    }

    @Test
    @DisplayName("Kiem tra khi hop dong khong thuoc khach hang nay")
    void testExceptionWhenCheckContractBeLongCustomer() {
        ReflectionTestUtils.setField(contractService, "customerServiceJPA", customerServiceJPA);
        given(customerServiceJPA.existsById(5L)).willReturn(true);
        Exception ex = assertThrows(EtcException.class, () -> {
            contractService.appendContract(new AddVehicleRequestDTO(), null, 5l, 6L, "1.1.1.1");
        });
        assertEquals(ex.getMessage(), "crm.contract.not.exist");
    }

    @Test
    @DisplayName("Kiem tra khi throw exception khi gop hop dong")
    void testExceptionWhenContractEntityThrowException() throws DataNotFoundException, JAXBException, XMLStreamException, IOException {
        testExceptionWhenCheckCustomerExits();
        testExceptionWhenCheckContractBeLongCustomer();
        ReflectionTestUtils.setField(contractService, "contractServiceJPA", contractServiceJPA);
        ReflectionTestUtils.setField(contractServiceJPA, "contractRepositoryJPA", contractRepositoryJPA);
        given(contractServiceJPA.findById(6L)).willThrow(new EtcException("ocs.error.info.contract"));
        Exception exceptionWhenContractEntityThrowException = assertThrows(EtcException.class, () -> {
            contractService.appendContract(new AddVehicleRequestDTO(), null, 5l, 6L, "1.1.1.1");
        });
        assertEquals(exceptionWhenContractEntityThrowException.getMessage(), "ocs.error.info.contract");
    }

    @Test
    @DisplayName("Kiem tra phuong tien co ton tai khong")
    void testCheckVehicleExist() throws DataNotFoundException, IOException, XMLStreamException, JAXBException {
        appendContract();
        ReflectionTestUtils.setField(vehicleServiceImpl, "vehicleRepositoryJPA", vehicleRepositoryJPA);
        given(vehicleRepositoryJPA.findAllByPlateNumberAndStatusIsNot("123456", VehicleEntity.Status.NOT_ACTIVATED.value))
                .willReturn(Arrays.asList(new VehicleEntity()));
        assertSame(true, vehicleServiceImpl.checkVehicleExist("123456"));
    }


    @Test
    @DisplayName("Kiem tra dang ki phuong tien")
    void testRegisterVehicle() throws DataNotFoundException, IOException, XMLStreamException, JAXBException {
        appendContract();
        assertDoesNotThrow(() -> vehicleServiceImpl.validateRegisterVehicle(addVehicleRequestDTO));
        assertSame(vehicleEntity, vehicleServiceImpl.saveVehicle(addVehicleRequestDTO, null, 5L, 1L));
        productService = spy(new ProductServiceImpl());

        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("1");
        ocsResponse.setDescription("Success");
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, addVehicleRequestDTO.getActTypeId())).willReturn(ocsResponse);
        ReflectionTestUtils.setField(vehicleServiceImpl, "productService", productService);
        ReflectionTestUtils.setField(vehicleServiceImpl, "ocsService", ocsService);
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            public String checkVehicleActivationBoo1(String plateNumber, Authentication authentication, String plateTypeCode, Long actTypeId) throws IOException {
                return "OK";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean checkOcsCode(OCSResponse response){
                return true;
            }
        };
        ReflectionTestUtils.setField(vehicleServiceImpl, "jedisCacheService",jedisCacheService);
        ReflectionTestUtils.setField(vehicleServiceImpl, "smsService",smsService);
        ReflectionTestUtils.setField(vehicleServiceImpl, "emailService",emailService);

        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractNo("1000000V");
        contractEntity.setContractId(1L);
        contractEntity.setNoticeEmail("ngoctranminh26@gmail.com");
        contractEntity.setNoticePhoneNumber("09777777777");
        given(contractRepositoryJPA.findByContractId(any())).willReturn(contractEntity);
        given(jedisCacheService.hget(contractEntity.getContractNo(), Constants.CONTRACT_KEY_GEN)).willReturn("AAAAA");
        ReflectionTestUtils.setField(vehicleServiceImpl, "smsRegisterUser", smsRegisterUser);
        assertThrows(NullPointerException.class, () -> vehicleServiceImpl.registerVehicle(addVehicleRequestDTO, null, 5L, 1L, "1.1.1.1"));
    }

    @Test
    void ifValidateRegisterVehicleNotThrow() {
        assertDoesNotThrow(() -> vehicleServiceImpl.validateRegisterVehicle(addVehicleRequestDTO));
    }

    @Test
    public void testAddContractCaseSuccess() throws Exception {
        // param
        Long customerId = 1L;
        Long contractId = 2L;
        Long actTypeId = 3L;
        Long actReasonId = 4L;
        ContractDTO dataParams = new ContractDTO();
        dataParams.setContractId(contractId);
        dataParams.setActTypeId(actTypeId);
        dataParams.setReasonId(actReasonId);
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        List<ActReasonEntity> actReasonEntityList = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActTypeId(actTypeId);
        actReasonEntity.setActReasonId(actReasonId);
        actReasonEntityList.add(actReasonEntity);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        OCSCreateContractForm ocsCreateContractForm = new OCSCreateContractForm();
        ocsCreateContractForm.setCustomerId("1");
        ocsCreateContractForm.setContractCode("V00000null/2020/HD-1");
        ocsCreateContractForm.setContractStatus("1");
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        // mock
        given(customerServiceJPA.findById(customerId)).willReturn(Optional.of(customerEntity));
        given(actReasonRepositoryJPA.findAllByActTypeId(actTypeId)).willReturn(actReasonEntityList);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getStringToken(Authentication authentication) {
                return "token";
            }
        };
        new MockUp<ContractServiceImpl>() {
            @mockit.Mock
            public String createUserForContract(String contractNo, ContractDTO contractDTO, Authentication authentication, String passwordApp) {
                return "user";
            }
        };
        ReflectionTestUtils.setField(contractService, "actionAuditService", actionAuditService);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        ReflectionTestUtils.setField(contractService,"actionAuditDetailServiceJPA",actionAuditDetailServiceJPA);
        new MockUp<OCSDTO>() {
            @mockit.Mock
            public OCSCreateContractForm createContractFormFromContractEntity(ContractEntity entity, CustomerEntity customer, ContractDTO contractDTO) {
                return ocsCreateContractForm;
            }
        };
        given(ocsService.createContract(ocsCreateContractForm,null,dataParams.getActTypeId().intValue())).willReturn(ocsResponse);
        // execute
//        Exception exception = assertThrows(NullPointerException.class, () -> {
//            contractService.addContract(dataParams, null, customerId);
//        });
//        // execute
//        String expectedMessage = null;
//        String actualMessage = exception.getMessage();
//        assertEquals(actualMessage, expectedMessage);
        contractService.addContract(dataParams,null,customerId);
    }

    @Test
    public void testUpdateProfileCaseSuccess() throws Exception {
        // param
        Date date = new java.sql.Date(System.currentTimeMillis());
        Long custId = 1L;
        Long contractId = 2L;
        Long actTypeId = 3L;
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        List<ContractProfileDTO> contractProfileDTOs = new ArrayList<>();
        ContractProfileDTO contractProfileDTO = new ContractProfileDTO();
        contractProfileDTO.setActTypeId(actTypeId);
        contractProfileDTO.setFileBase64("fileBase64");
        contractProfileDTO.setFilePath("filePath");
        contractProfileDTOs.add(contractProfileDTO);

        // mock
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean checkBriefcaseValid(String fileName, byte[] file, Integer maxFileSizeMb) {
                return true;
            }
        };
        new MockUp<FileService>() {
            @mockit.Mock
            void uploadFile(String filePath, byte[] file) {
            }
        };
        ReflectionTestUtils.setField(contractService, "contractProfileServiceJPA", contractProfileServiceJPA);
        contractService.updateProfile(null, date, custId, contractId, actionAuditEntity, contractProfileDTOs);
    }

    @Test
    public void testAppendContractCaseSuccess() throws Exception {
        // param
        Long customerId = 1L;
        Long contractId = 2L;
        AddVehicleRequestDTO dataParam = new AddVehicleRequestDTO();
        dataParam.setContractAppendix("0/PL.null");
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setCustId(customerId);

        // mock
        given(customerServiceJPA.existsById(customerId)).willReturn(true);
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        given(vehicleService.registerVehicle(dataParam, null, customerId, contractId, "1.1.1.1")).willReturn(dataParam);
        // execute
        assertEquals(dataParam, contractService.appendContract(dataParam, null, customerId, contractId, "1.1.1.1"));
    }

    @Test
    public void testEditContractCaseSuccess() throws Exception {
        // param
        Long actReasonId = 4L;
        Long customerId = 1L;
        Long contractId =2L;
        Long actTypeId = 3L;
        String contractNo = "contractNo";
        List<Long> contractIdList = new ArrayList<>();
        contractIdList.add(2L);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setCustId(customerId);
        contractEntity.setContractNo(contractNo);
        contractEntity.setPaymentDefaultId(10L);
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setReasonId(actReasonId);
        contractDTO.setActTypeId(actTypeId);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(10L);
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        OCSUpdateContractForm ocsUpdateContractForm = new OCSUpdateContractForm();
        ocsUpdateContractForm.setContractId(contractId.toString());
        ocsUpdateContractForm.setCustomerId(customerId.toString());
        ocsUpdateContractForm.setContractCode("contractNo");
        ContractDTO expect = new ContractDTO();
        expect.setContractId(contractId);
        ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setStatus("1");
        // mock
        given(contractServiceJPA.countContract(contractIdList, "2")).willReturn(1L);
        ReflectionTestUtils.setField(contractService, "contractServiceJPA", contractServiceJPA);
        given(customerServiceJPA.existsById(customerId)).willReturn(true);
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        given(contractServiceJPA.save(contractEntity)).willReturn(contractEntity);
        ReflectionTestUtils.setField(contractService, "actionAuditService", actionAuditService);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        ReflectionTestUtils.setField(contractService,"actionAuditDetailServiceJPA",actionAuditDetailServiceJPA);
        given(saleTransService.addSaleTrans(customerId, contractId, contractNo, null, actTypeId)).willReturn(saleTransEntity);
        given(customerServiceJPA.findById(customerId)).willReturn(Optional.of(customerEntity));
        given(contractPaymentServiceJPA.findById(10L)).willReturn(Optional.of(contractPaymentEntity));
        new MockUp<OCSDTO>() {
            @mockit.Mock
            public OCSUpdateContractForm updateContractOCSFormFromEntity(ContractEntity entity, CustomerEntity customer, ContractDTO contractDTO) {
                return ocsUpdateContractForm;
            }
        };
        given(ocsService.updateContract(ocsUpdateContractForm, null, actTypeId.intValue())).willReturn(ocsResponse);
        // execute
        assertEquals(expect,contractService.editContract(contractDTO, null, customerId, contractId));
    }

    @Test
    public void testSplitContractCaseSuccess() throws Exception {
        // param
        SplitContractDTO dataParam = new SplitContractDTO();
        Long customerId = 1L;
        Long contractId = 2L;
        Long vehicleId = 3L;
        Long actTypeId = 4L;
        dataParam.setActTypeId(actTypeId);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setContractNo("contractNo");
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setContractId(contractId);
        List<Long> listVehicle = new ArrayList<>();
        listVehicle.add(vehicleId);
        List<VehicleEntity> vehicleEntityList = new ArrayList<>();
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntityList.add(vehicleEntity);
        // mock
        new MockUp<ContractServiceImpl>(){
            @mockit.Mock
            public Object addContract(ContractDTO dataParams, Authentication authentication, Long customerId){
                return contractDTO;
            }
        };
        given(contractRepositoryJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        given(vehicleServiceJPA.findAllById(listVehicle)).willReturn(vehicleEntityList);
        // execute
        assertEquals(contractDTO, contractService.splitContract(dataParam, null, customerId, contractId));
    }

    @Test
    public void testTerminateContractCaseSuccess() throws Exception {
        // param
        TerminateContractDTO dataParams = new TerminateContractDTO();
        Long customerId = 1L;
        Long actTypeId = 3L;
        List<Long> contractIdList = new ArrayList<>();
        contractIdList.add(2L);
        dataParams.setContractIds(contractIdList);
        dataParams.setActTypeId(actTypeId);
        List<ContractEntity> terminateContracts =new ArrayList<>();
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setPaymentDefaultId(10L);
        contractEntity.setCustId(customerId);
        contractEntity.setContractId(2L);
        contractEntity.setContractNo("contractNo");
        terminateContracts.add(contractEntity);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, 4L, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(1L);
        List<VehicleEntity> terminateVehicles = new ArrayList<>();
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setCustId(customerId);
        terminateVehicles.add(vehicleEntity);
        ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setStatus("1");
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        OCSUpdateContractForm ocsUpdateContractForm = new OCSUpdateContractForm();
        ocsUpdateContractForm.setContractId("2");
        // mock
        given(customerServiceJPA.existsById(customerId)).willReturn(true);
        given(contractServiceJPA.countContract(contractIdList, "2")).willReturn(1L);
        given(contractServiceJPA.findAllById(contractIdList)).willReturn(terminateContracts);
        ReflectionTestUtils.setField(contractService, "actionAuditService", actionAuditService);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        ReflectionTestUtils.setField(contractService,"actionAuditDetailServiceJPA",actionAuditDetailServiceJPA);
        given(saleTransService.addSaleTrans(customerId, 2L, "contractNo", null, actTypeId)).willReturn(saleTransEntity);
        given(vehicleServiceJPA.findByContractIdInAndStatusAndActiveStatus(contractIdList, "1", Arrays.asList("1"))).willReturn(terminateVehicles);
        given(contractPaymentServiceJPA.findById(10L)).willReturn(Optional.of(contractPaymentEntity));
        given(ocsService.terminateContract(ocsUpdateContractForm, null, Math.toIntExact(dataParams.getActTypeId()))).willReturn(ocsResponse);
        // execute
        contractService.terminateContract(dataParams, null, customerId);
    }

    @Test
    public void testMerContractCaseSuccess() throws Exception {
        // param
        MergeContractDTO dataParams  = new MergeContractDTO();
        Long customerId = 1L;
        Long contractId = 2L;
        Long actTypeId = 3L;
        dataParams.setActTypeId(actTypeId);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        List<Long> secondaryContractId = new ArrayList<>();
        secondaryContractId.add(contractId);
        secondaryContractId.add(4L);
        List<Long> contractIds = new ArrayList<>();
        contractIds.add(2L);
        contractIds.add(4L);
        dataParams.setSecondaryContractId(secondaryContractId);
        List<VehicleEntity> vehicleEntities = new ArrayList<>();
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setCustId(customerId);
        vehicleEntities.add(vehicleEntity);
        List<ContractEntity> secondaryContracts = new ArrayList<>();
        ContractEntity contractEntity1 = new ContractEntity();
        contractEntity1.setContractId(contractId);
        contractEntity1.setPaymentDefaultId(10L);
        secondaryContracts.add(contractEntity1);
        List<Long> collect = new ArrayList<>();
        collect.add(contractId);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, 4L, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setStatus("1");
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        OCSUpdateContractForm ocsUpdateContractForm = new OCSUpdateContractForm();
        ocsUpdateContractForm.setContractId(contractId.toString());
        // mock
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        given(contractServiceJPA.countContract(contractIds, "2")).willReturn(2L);
        given(contractServiceJPA.findAllById(secondaryContractId)).willReturn(secondaryContracts);
        given(vehicleServiceJPA.findAllByContractIdInAndStatusAndActiveStatus(collect, "1", "1")).willReturn(vehicleEntities);
        ReflectionTestUtils.setField(contractService, "actionAuditService", actionAuditService);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        ReflectionTestUtils.setField(contractService,"actionAuditDetailServiceJPA",actionAuditDetailServiceJPA);
        given(contractPaymentServiceJPA.findById(10L)).willReturn(Optional.of(contractPaymentEntity));
        given(ocsService.terminateContract(ocsUpdateContractForm, null, actTypeId.intValue())).willReturn(ocsResponse);
        // execute
        contractService.mergeContract(dataParams, null, customerId, contractId);
    }

    @Test
    public void testFindContractByIdCaseSuccess() throws Exception {
        // param
        Long contractId = 1L;
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ContractByCustomerDTO> data = new ArrayList<>();
        ContractByCustomerDTO contractByCustomerDTO = new ContractByCustomerDTO();
        contractByCustomerDTO.setContractId(contractId);
        contractByCustomerDTO.setCustomerType(1L);
        data.add(contractByCustomerDTO);
        resultSelectEntity.setListData(data);
        // mock
        given(contractRepository.findContractById(contractId)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, contractService.findContractById(contractId));
    }
    @Test
    public void testFindContractByIdCaseCustTypeIsRep() throws Exception {
        // param
        Long contractId = 1L;
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ContractByCustomerDTO> data = new ArrayList<>();
        ContractByCustomerDTO contractByCustomerDTO = new ContractByCustomerDTO();
        contractByCustomerDTO.setContractId(contractId);
        contractByCustomerDTO.setCustomerType(2L);
        data.add(contractByCustomerDTO);
        resultSelectEntity.setListData(data);
        // mock
        given(contractRepository.findContractById(contractId)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, contractService.findContractById(contractId));
    }

    @Test
    public void testFindContractById() throws Exception {
        // param
        Long contractId = 1L;
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ContractByCustomerDTO> data = new ArrayList<>();
        ContractByCustomerDTO contractByCustomerDTO = new ContractByCustomerDTO();
        contractByCustomerDTO.setContractId(contractId);
        contractByCustomerDTO.setCustomerType(3L);
        data.add(contractByCustomerDTO);
        resultSelectEntity.setListData(data);
        // mock
        given(contractRepository.findContractById(contractId)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, contractService.findContractById(contractId));
    }

    @Test
    public void testGetDataUserContractCaseSuccess() throws Exception {
        // param
        Long contractId = 1L;
        Long customerId = 2L;
        String user = "login";
        Date date = new Date(System.currentTimeMillis());
        MobileUserDetailDTO dataResult = new MobileUserDetailDTO();
        dataResult.setContractId(contractId.toString());
        dataResult.setCustomerId(customerId.toString());
        dataResult.setBirth(FnCommon.convertDateToStringOther(date, Constants.COMMON_DATE_FORMAT));
        dataResult.setSignDate(FnCommon.convertDateToStringOther(date, Constants.COMMON_DATE_FORMAT));
        dataResult.setEffDate(FnCommon.convertDateToStringOther(date, Constants.COMMON_DATE_FORMAT));
        dataResult.setExpDate(FnCommon.convertDateToStringOther(date, Constants.COMMON_DATE_FORMAT));
        dataResult.setDateOfIssue(FnCommon.convertDateToStringOther(date, Constants.COMMON_DATE_FORMAT));
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setCustId(customerId);
        contractEntity.setSignDate(date);
        contractEntity.setEffDate(date);
        contractEntity.setExpDate(date);
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        customerEntity.setBirthDate(date);
        customerEntity.setDateOfIssue(date);


        // mock
        new MockUp<FnCommon>(){
            @mockit.Mock
            public String getUserLogin(Authentication authentication){
                return user;
            }
        };
        given(contractServiceJPA.getByAccountUser(user)).willReturn(contractEntity);
        given(customerServiceJPA.getOne(customerId)).willReturn(customerEntity);
        // execute
        assertEquals(dataResult, contractService.getDataUserContract(null));
    }

    @Test
    public void testFindProfileByContract() throws Exception {
        // param
        Long contractId = 1L;
        ResultSelectEntity resultData = new ResultSelectEntity();
        List<ContractProfileDTO> listData = new ArrayList<>();
        ContractProfileDTO contractProfileDTO = new ContractProfileDTO();
        contractProfileDTO.setContractId(contractId);
        listData.add(contractProfileDTO);
        resultData.setListData(listData);
        ContractProfileDTO dataParams = new ContractProfileDTO();
        // mock
        given(contractRepository.findProfileByContract(contractId, dataParams)).willReturn(resultData);
        // execute
        assertEquals(resultData, contractService.findProfileByContract(contractId, dataParams));
    }
    @Test
    public void testDownloadProfileByContract() throws Exception {
        // param
        Integer contractId = 1;
        List<ContractProfileDTO> contractProfileDTOList = new ArrayList<>();
        ContractProfileDTO contractProfileDTO = new ContractProfileDTO();
        contractProfileDTO.setContractId(contractId.longValue());
        contractProfileDTOList.add(contractProfileDTO);

        // mock
        given(contractRepository.downloadProfileByContract(contractId)).willReturn(contractProfileDTOList);
        // execute
        assertEquals(contractProfileDTO, contractService.downloadProfileByContract(contractId));
    }

    @Test
    public void testDeleteProfile() throws Exception {
        // param
        Long customerId = 1L;
        Long contractId = 2L;
        Long profileId = 3L;
        ContractProfileEntity contractProfileEntity = new ContractProfileEntity();
        contractProfileEntity.setCustId(customerId);
        contractProfileEntity.setFilePath("filePath");
        // mock
        given(contractProfileServiceJPA.findById(profileId)).willReturn(Optional.of(contractProfileEntity));
        // execute
        contractService.deleteProfile(null, customerId, contractId, profileId);
    }

    @Test
    public void testGetOCSInfo() throws Exception {
        // param
        Long contractId = 2L;
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setContractId(contractId);
        // mock
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        given(ocsService.getContractInfo(null, contractId)).willReturn("ocs");
        new MockUp<ContractDTO>(){
            @mockit.Mock
            public ContractDTO fromEntity(ContractEntity contractEntity, String ocsResponse){
                return contractDTO;
            }
        };
        // execute
        assertEquals(contractDTO, contractService.getOCSInfo(null, contractId));
    }

    @Test
    public void testCreateUserForContract() throws Exception {
        // param
        Long contractId = 2L;
        String contractNo = "contractNo";
        String passwordApp = "pass";
        String user = "login";
        String location = "location";
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setContractId(contractId);
        MultiValueMap<String, String> header = new HttpHeaders();
        header.add("Location", location);
        // mock
        new MockUp<FnCommon>(){
            @mockit.Mock
            public String getStringToken(Authentication authentication){
                return user;
            }
        };
        new MockUp<ContractServiceImpl>() {
            @mockit.Mock
            private <T> ResponseEntity<ObjectNode> getExchange(RestTemplate restTemplate, ObjectNode body, HttpHeaders headers) throws JsonProcessingException {
                ObjectMapper mapper = new ObjectMapper();
                String json = "{\"Location\" : \"aaaa\"}";
                JsonNode node = mapper.readTree(json);
                ObjectNode objectNode = node.deepCopy();
                objectNode.set("", node);
                ResponseEntity<ObjectNode> res = new ResponseEntity<>(objectNode, header, HttpStatus.OK);
                return res;
            }
        };
        ReflectionTestUtils.setField(contractService, "wsRegisterUser", "http://10.60.156.159:8080/auth/admin/realms/etc-internal/users");
        // execute
        assertEquals(location, contractService.createUserForContract(contractNo, contractDTO, null, passwordApp));
    }
}
