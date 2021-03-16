package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.ReqOnlineEventSyncDTO;
import com.viettel.etc.dto.boo.ReqQueryTicketDTO;
import com.viettel.etc.dto.boo.ResOnlineEventDTO;
import com.viettel.etc.dto.im.StockEtagDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VehicleServiceImplTest {
    @Mock
    private VehicleRepositoryJPA vehicleRepositoryJPA;
    @InjectMocks
    private VehicleServiceImpl vehicleService;
    @Mock
    public ProductService productService;
    @Mock
    private OCSService ocsService;

    @Mock
    public ContractServiceJPA contractServiceJPA;

    @Mock
    private VehicleServiceJPA vehicleServiceJPA;

    @Mock
    private ActionAuditService actionAuditService;

    @Mock
    private ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Mock
    ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @Mock
    SaleTransRepositoryJPA saleTransRepositoryJPA;
    @Mock
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @InjectMocks
    private ActionAuditServiceImpl actionAuditServiceImpl;

    @Mock
    private ActionAuditRepositoryJPA actionAuditRepositoryJPA;

    @Mock
    private ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Mock
    private ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Mock
    private SaleTransService saleTransService;

    @Mock
    private SaleTransDetailService saleTransDetailService;

    @Mock
    private VehicleProfileRepositoryJPA vehicleProfileRepositoryJPA;

    @Mock
    private FileService fileService;

    @Mock
    private ContractProfileRepositoryJPA contractProfileRepositoryJPA;

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    CategoriesService categoriesService;

    @Mock
    VehicleTypeService vehicleTypeService;

    @Mock
    VehicleGroupService vehicleGroupService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    CriteriaQuery<VehicleEntity> query;
    @Mock
    CriteriaQuery<Long> countQuery;
    @Mock
    Root<VehicleEntity> root;
    @Mock
    TypedQuery<VehicleEntity> queryVehicle;
    @Mock
    Root<VehicleEntity> countRoot;
    @Mock
    Expression<Long> selection;

    @Mock
    JedisCacheService jedisCacheService;

    @Mock
    LuckyService luckyService;

    @Mock
    TypedQuery<Long> queryLong;


    ListVehicleAssignRfidDTO listVehicleAssignRfidDTO;
    Long custId = 1000l;
    Long contractId = 100l;


    RFIDDTO rFIDDTOTest;
    VehicleEntity vehicleEntityTest;
    List<VehicleEntity> lstVehicle;
    OCSResponse ocsResponse;
    ModifyVehicleDTO modifyVehicleDTO;
    Long vehicleId = 3000l;
    AddVehicleRequestDTO addVehicleRequestDTO;

    @Mock
    Boo1ServiceImpl boo1Service;
    @Mock
    private IMServiceImpl imService;

    @Mock
    ExceptionListRepositoryJPA exceptionListRepositoryJPA;

    @Mock
    ExceptionListServiceJPA exceptionListServiceJPA;

    @Mock
    SaleTransDetailServiceJPA saleTransDetailServiceJPA;


    @Value("${sms.user.register}")
    private String smsRegisterUser;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private SMSServiceImpl smsService;

    @Mock
    ContractRepositoryJPA contractRepositoryJPA;

    public void registerVehicleSetUp() {
        addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setPlateNumber("19N09055T");
        addVehicleRequestDTO.setOwner("ngocTM");
        addVehicleRequestDTO.setNetWeight(1000.00);
        addVehicleRequestDTO.setCargoWeight(1000.00);
        addVehicleRequestDTO.setSeatNumber(100l);
        addVehicleRequestDTO.setPlateTypeId(13l);
        addVehicleRequestDTO.setRfidSerial("710156");
        addVehicleRequestDTO.setVehicleTypeId(2l);
        addVehicleRequestDTO.setActTypeId(3l);
        addVehicleRequestDTO.setVehicleGroupId(1L);
        addVehicleRequestDTO.setVehicleGroupCode("1");
        List<VehicleProfileDTO> vehicleProfileDTOList = new ArrayList<>();
        VehicleProfileDTO vehicleProfileDTO = new VehicleProfileDTO();
        vehicleProfileDTO.setDocumentTypeId(1L);
        vehicleProfileDTO.setFileName("test.png");
        vehicleProfileDTO.setFileBase64("");
        vehicleProfileDTOList.add(vehicleProfileDTO);
        addVehicleRequestDTO.setVehicleProfileDTOs(vehicleProfileDTOList);

    }

    @Ignore
    public void registerVehicleTest() throws DataNotFoundException, IOException, XMLStreamException, JAXBException {
        List<ActReasonEntity> actReasonEntityList = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        actReasonEntityList.add(actReasonEntity);
        registerVehicleSetUp();
        ReflectionTestUtils.setField(vehicleService, "boo1Service", boo1Service);
        ReflectionTestUtils.setField(vehicleService, "actReasonRepositoryJPA", actReasonRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "imService", imService);
        given(actReasonRepositoryJPA.findAllByActTypeId(addVehicleRequestDTO.getActTypeId())).willReturn(actReasonEntityList);
        Map<String, Object> map = new HashMap<>();
        map.put("shop_id", 1L);
        map.put("warehouse_id", 1L);
        map.put("staff_id", 1L);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                return map;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "vehicleGroupService", vehicleGroupService);
        List<String> stringList = new ArrayList<>();
        stringList.add("1");
        stringList.add("4");
        given(vehicleGroupService.getVehicleGroupById(FnCommon.getStringToken(null), addVehicleRequestDTO)).willReturn(stringList);
        StockEtagDTO stockEtagDTO = new StockEtagDTO();
        stockEtagDTO.setEpc("");
        stockEtagDTO.setTid("");
        stockEtagDTO.setEtagTypeId(1L);
        given(imService.getSerialDetails(addVehicleRequestDTO.getRfidSerial(), null)).willReturn(stockEtagDTO);
        ReflectionTestUtils.setField(vehicleService, "vehiclesOfferId", "1");
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        given(vehicleRepositoryJPA.save(any())).willReturn(vehicleEntity);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        given(actionAuditService.updateLogToActionAudit(any())).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "fileService", fileService);
        ReflectionTestUtils.setField(vehicleService, "productService", productService);
        SaleBusinessMesDTO saleBusinessMesDTO = new SaleBusinessMesDTO();
        saleBusinessMesDTO.setSuccess("true");
        given(productService.callBCCSIM(null, addVehicleRequestDTO.getActTypeId(), vehicleEntity,
                addVehicleRequestDTO.getRfidSerial(), 1000L, Constants.CASE_UPDATE_VEHICLE.REGISTER_VEHICLE, "")).willReturn(saleBusinessMesDTO);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, addVehicleRequestDTO.getActTypeId())).willReturn(ocsResponse);
        ReflectionTestUtils.setField(vehicleService, "jedisCacheService",jedisCacheService);
        ReflectionTestUtils.setField(vehicleService, "smsService",smsService);
        ReflectionTestUtils.setField(vehicleService, "emailService",emailService);

        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractNo("1000000V");
        contractEntity.setContractId(1L);
        contractEntity.setNoticeEmail("ngoctranminh26@gmail.com");
        contractEntity.setNoticePhoneNumber("09777777777");
        given(contractRepositoryJPA.findByContractId(any())).willReturn(contractEntity);
        given(jedisCacheService.hget(contractEntity.getContractNo(), Constants.CONTRACT_KEY_GEN)).willReturn("AAAAA");
        ReflectionTestUtils.setField(vehicleService, "smsRegisterUser", smsRegisterUser);
        assertThrows(NullPointerException.class, () -> vehicleService.registerVehicle(addVehicleRequestDTO, null, custId, contractId, "1.1.1.1"));
    }

    public void modifyVehicleTestSetUp() {
        modifyVehicleDTO = new ModifyVehicleDTO();
        modifyVehicleDTO.setOwner("ngocTM");
        modifyVehicleDTO.setGrossWeight(1000.00);
        modifyVehicleDTO.setPullingWeight(1000.00);
        modifyVehicleDTO.setSeatNumber(100l);
        modifyVehicleDTO.setPlateType(13l);
        modifyVehicleDTO.setActTypeId(24l);
        modifyVehicleDTO.setReasonId(24l);
        modifyVehicleDTO.setPlateTypeCode("6");

    }

    @Test
    public void modifyVehicleTest1() {
        modifyVehicleTestSetUp();
        String expectedMessage = "crm.contract.not.exist";
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.empty());
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.modifyVehicle(modifyVehicleDTO, custId, contractId, vehicleId, null);
        });
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    public void modifyVehicleTest2() {
        modifyVehicleTestSetUp();
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setShopId(236666l);
        contractEntity.setShopName("JHFGJSDL");
        contractEntity.setContractNo("1q2e3r$T");
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
    }


    @Test
    public void modifyVehicleTest3() {
        modifyVehicleTestSetUp();
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setShopId(236666l);
        contractEntity.setShopName("JHFGJSDL");
        contractEntity.setContractNo("1q2e3r$T");
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        String expectedMessage = "crm.can.not.modify.vehicle";
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setStatus("1");
        vehicleEntity.setActiveStatus("5");
        vehicleEntity.setVehicleId(vehicleId);
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.of(vehicleEntity));
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.modifyVehicle(modifyVehicleDTO, custId, contractId, vehicleId, null);
        });
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void modifyVehicleTest4() {
        modifyVehicleTest2();
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setActiveStatus("5");
        vehicleEntity.setVehicleId(vehicleId);
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.of(vehicleEntity));
    }

    @Test
    public void modifyVehicleTest5() throws Exception {
        modifyVehicleTest4();
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(100l);
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip) {
                return actionAuditEntity;
            }
        };

        String ip = InetAddress.getLocalHost().getHostAddress();
        given(actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(null, modifyVehicleDTO.getReasonId(),
                modifyVehicleDTO.getActTypeId(), custId, contractId, vehicleId, ip))).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(vehicleService, "actionAuditService", actionAuditService);
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailServiceJPA", actionAuditDetailServiceJPA);
        ReflectionTestUtils.setField(vehicleService, "serviceFeeRepositoryJPA", serviceFeeRepositoryJPA);

    }

    @Test
    public void testLockRFIDCaseNullVehicle() throws Exception {
        //param
        Long vehicleId = 100L;
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        // mock
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.empty());
        // execute
        assertEquals(false, vehicleService.lockRFID(rfiddto, null));
    }

    @Test
    public void lockRFIDTest() throws Exception {
        //param
        final Long vehicleId = 1L;
        String ocsStatus = "2";
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ocsResponse.setDescription("Success");
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        RFIDDTO rfidDTO = new RFIDDTO();
        rfidDTO.setVehicleId(vehicleId);
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = UpdateVehicleRequestOCSDTO.builder()
                .epc(vehicleEntity.getEpc())
                .status(ocsStatus)
                .actTypeId(rfidDTO.getActTypeId())
                .build();
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(1L);
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, rfidDTO.getReasonId(), rfidDTO.getActTypeId(),
                vehicleEntity.getCustId(), vehicleEntity.getContractId(), vehicleEntity.getVehicleId(), ip);
        actionAuditEntity.setActionAuditId(1L);
        ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setActTypeId(rfidDTO.getActTypeId());
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setVehicleId(vehicleId);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(1L);
        List<ServiceFeeEntity> serviceFeeEntityList = new ArrayList<>();
        serviceFeeEntityList.add(serviceFeeEntity);
        // mock
        given(vehicleServiceJPA.findById(1L)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, null, true)).willReturn(ocsResponse);
        given(vehicleServiceJPA.save(vehicleEntity)).willReturn(vehicleEntity);
        given(contractServiceJPA.findById(1L)).willReturn(Optional.of(contractEntity));
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(actionAuditServiceImpl, "actionAuditRepositoryJPA", actionAuditRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "actionAuditService", actionAuditService);
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip) {
                return actionAuditEntity;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        given(serviceFeeRepositoryJPA.getByActTypeId(rfidDTO.getActTypeId())).willReturn(serviceFeeEntityList);
        ReflectionTestUtils.setField(vehicleService, "serviceFeeRepositoryJPA", serviceFeeRepositoryJPA);
        given(saleTransRepositoryJPA.save(saleTransEntity)).willReturn(saleTransEntity);
        ReflectionTestUtils.setField(vehicleService, "saleTransRepositoryJPA", saleTransRepositoryJPA);
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            public SaleTransEntity toSaleTrans(String saleTransCode, Long shopId, String shopName, Long amount,
                                               Long contractId, String contractNo,
                                               String createUser, Long custId, Authentication authentication) {
                return saleTransEntity;
            }
        };
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            public SaleTransDetailEntity toSaleTransDetail(long saleTransId, VehicleEntity vehicleEntity,
                                                           ServiceFeeEntity serviceFeeEntity, String userLogin) {
                return saleTransDetailEntity;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailRepositoryJPA", saleTransDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "exceptionListServiceJPA", exceptionListServiceJPA);
        List<SaleTransDetailEntity> saleTransDetailEntityList = new ArrayList<>();
        saleTransDetailEntity.setSaleTransDetailId(1L);
        saleTransDetailEntityList.add(saleTransDetailEntity);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailRepositoryJPA", saleTransDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "boo1Service", boo1Service);
        given(saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode("null", "RFID", "BOO1")).willReturn(saleTransDetailEntityList);
        // execute
        vehicleService.lockRFID(rfidDTO, null);
    }

    @Ignore
    public void testLockRFIDCaseServiceFeeIsNull() throws Exception {
        //param
        final Long vehicleId = 1L;
        String ocsStatus = "2";
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ocsResponse.setDescription("Success");
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        RFIDDTO rfidDTO = new RFIDDTO();
        rfidDTO.setVehicleId(vehicleId);
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = UpdateVehicleRequestOCSDTO.builder()
                .epc(vehicleEntity.getEpc())
                .status(ocsStatus)
                .actTypeId(rfidDTO.getActTypeId())
                .build();
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(1L);
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, rfidDTO.getReasonId(), rfidDTO.getActTypeId(),
                vehicleEntity.getCustId(), vehicleEntity.getContractId(), vehicleEntity.getVehicleId(), ip);
        actionAuditEntity.setActionAuditId(1L);
        ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setActTypeId(rfidDTO.getActTypeId());
        List<ServiceFeeEntity> serviceFeeEntityList = new ArrayList<>();
        // mock
        given(vehicleServiceJPA.findById(1L)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, null, true)).willReturn(ocsResponse);
        given(vehicleServiceJPA.save(vehicleEntity)).willReturn(vehicleEntity);
        given(contractServiceJPA.findById(1L)).willReturn(Optional.of(contractEntity));

        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(actionAuditServiceImpl, "actionAuditRepositoryJPA", actionAuditRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "actionAuditService", actionAuditService);
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip) {
                return actionAuditEntity;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        given(serviceFeeRepositoryJPA.getByActTypeId(rfidDTO.getActTypeId())).willReturn(serviceFeeEntityList);
        ReflectionTestUtils.setField(vehicleService, "serviceFeeRepositoryJPA", serviceFeeRepositoryJPA);
        // execute
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.lockRFID(rfidDTO, null);
        });
        // execute
        String expectedMessage = jedisCacheService.getMessageErrorByKey("crm.can.not.modify.vehicle");
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }


    @Test
    public void testLockRFIDCaseCallOCSFail() throws Exception {
        //param
        final Long vehicleId = 1L;
        String ocsStatus = "2";
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("1");
        ocsResponse.setDescription("Fail");
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        RFIDDTO rfidDTO = new RFIDDTO();
        rfidDTO.setVehicleId(vehicleId);
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = UpdateVehicleRequestOCSDTO.builder()
                .epc(vehicleEntity.getEpc())
                .status(ocsStatus)
                .actTypeId(rfidDTO.getActTypeId())
                .build();
        // mock
        given(vehicleServiceJPA.findById(1L)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, null, true)).willReturn(ocsResponse);
        // execute
        Exception exception = assertThrows(RuntimeException.class, () -> {
            vehicleService.lockRFID(rfidDTO, null);
        });
        // execute
        String expectedMessage = "Update status ocs fail";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }


    public void swapRFIDTestSetUp() {
//        MockedStatic mockedStatic = mockStatic(I18n.class);
//        mockedStatic.when(()->I18n.getText("abc")).thenReturn("123");
        ReflectionTestUtils.setField(vehicleService, "productService", productService);
        rFIDDTOTest = new RFIDDTO(1L, "", 1L, "", null, 1L, 1L, 0L, null, null, null);
    }

    @Test
    public void swapRFIDTest1() {
        swapRFIDTestSetUp();
        final Long vehicleId = 1L;
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.empty());
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.swapRFID(rFIDDTOTest, null);
        });

        String expectedMessage = "Vehicle not found";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, "crm.vehicle.not.found");
    }

    /**
     * Case pass qua if 1
     */
    @Test
    public void swapRFIDPassTest1() {
        swapRFIDTestSetUp();
        final Long vehicleId = 1L;
        vehicleEntityTest = new VehicleEntity();
        vehicleEntityTest.setVehicleId(vehicleId);
        vehicleEntityTest.setStatus("0");
        vehicleEntityTest.setContractId(1l);
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.of(vehicleEntityTest));
        Optional<VehicleEntity> optionalVehicleEntity = vehicleServiceJPA.findById(vehicleId);
        assertNotNull(optionalVehicleEntity);
    }

    @Test
    public void swapRFIDTest2() {
        swapRFIDPassTest1();
        ContractEntity contractEntity = new ContractEntity();
        given(contractServiceJPA.findById(1L)).willReturn(Optional.of(contractEntity));
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.swapRFID(rFIDDTOTest, null);
        });
        String expectedMessage = "Vehicle not active";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, "crm.vehicle.not.active");
    }

    @Test
    public void swapRFIDPassTest2() {
        swapRFIDPassTest1();
        vehicleEntityTest.setStatus("1");
        ContractEntity contractEntity = new ContractEntity();
        given(contractServiceJPA.findById(1L)).willReturn(Optional.of(contractEntity));
        ContractEntity contractEntityTest = contractServiceJPA.findById(1L).get();
        assertNotNull(contractEntityTest);
    }

    @Test
    public void swapRFIDTest3() {
        for (int i = 0; i <= 2; i++) {
            swapRFIDTest3_case("0");
            switch (i) {
                case 0:
                    swapRFIDTest3_case("0");
                    break;
                case 1:
                    swapRFIDTest3_case("2");
                    break;
                case 2:
                    swapRFIDTest3_case("5");
                    break;
            }
        }
    }

    public void swapRFIDTest3_case(String statusActive) {
        swapRFIDPassTest2();
        vehicleEntityTest.setActiveStatus(statusActive);
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.swapRFID(rFIDDTOTest, null);
        });
        String expectedMessage = "Vehicle not active";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, "crm.vehicle.not.active");
    }

    @Test
    public void swapRFIDTest4() {
        swapRFIDPassTest2();
        vehicleEntityTest.setActiveStatus("1");
        lstVehicle = new ArrayList<>();
        lstVehicle.add(vehicleEntityTest);
        given(vehicleServiceJPA.findBySerialRFID(rFIDDTOTest.getSerialRFID())).willReturn(lstVehicle);
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.swapRFID(rFIDDTOTest, null);
        });
        String expectedMessage = "Serial was attached by another vehicle";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, "crm.serial.was.attached.by.another.vehicle");
    }

    @Ignore
    public void swapRFIDPassTest4() throws Exception {
        swapRFIDPassTest2();
        vehicleEntityTest.setActiveStatus("1");
        lstVehicle = new ArrayList<>();
        given(vehicleServiceJPA.findBySerialRFID(rFIDDTOTest.getSerialRFID())).willReturn(lstVehicle);
        List<VehicleEntity> vehicles = vehicleServiceJPA.findBySerialRFID(rFIDDTOTest.getSerialRFID());
        assertEquals(0, vehicles.size());
        ReflectionTestUtils.setField(vehicleService, "imService", imService);
        SaleBusinessMesDTO saleBusinessMesDTO = new SaleBusinessMesDTO();
        saleBusinessMesDTO.setSuccess("true");
        RFIDDTO rfidDTO = new RFIDDTO();
        rfidDTO.setActTypeId(1L);
        rfidDTO.setSerialRFID("");

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setContractId(1L);
        vehicleEntity.setStatus("1");
        vehicleEntity.setActiveStatus("1");
        given(productService.callBCCSIM(null, rfidDTO.getActTypeId(), vehicleEntity, rfidDTO.getSerialRFID(), null, Constants.CASE_UPDATE_VEHICLE.SWAP_VEHICLE, "")).willReturn(saleBusinessMesDTO);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        given(ocsService.deleteVehicle(vehicleEntity.getEpc(), String.valueOf(vehicleEntity.getContractId()), null, (int) rfidDTO.getActTypeId())).willReturn(ocsResponse);
        StockEtagDTO stockEtagDTO = new StockEtagDTO();
        stockEtagDTO.setEpc("");
        stockEtagDTO.setTid("");
        stockEtagDTO.setEtagTypeId(1L);
        given(imService.getSerialDetails("", null)).willReturn(stockEtagDTO);
        OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.setResultCode("0");
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(1L);
        addVehicleRequestDTO.setStatus("1");
        addVehicleRequestDTO.setActiveStatus("1");
        addVehicleRequestDTO.setEpc("");
        addVehicleRequestDTO.setTid("");
        addVehicleRequestDTO.setRfidSerial("");
        addVehicleRequestDTO.setOfferExternalId("430");
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, 1)).willReturn(ocsResponse1);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        given(actionAuditService.updateLogToActionAudit(any())).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailServiceJPA", actionAuditDetailServiceJPA);
        List<SaleTransDetailEntity> saleTransDetailEntityList = new ArrayList<>();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(1L);
        saleTransDetailEntityList.add(saleTransDetailEntity);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailRepositoryJPA", saleTransDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "exceptionListServiceJPA", exceptionListServiceJPA);
        given(saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode("null", null, "BOO1")).willReturn(saleTransDetailEntityList);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailServiceJPA", saleTransDetailServiceJPA);
        ReflectionTestUtils.setField(vehicleService, "boo1Service", boo1Service);
        vehicleService.swapRFID(rFIDDTOTest, null);
    }

    @Test
    public void testActiveRFIDCaseNullVehicle() throws Exception {
        //param
        Long vehicleId = 100L;
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        ActiveRFIDResponseDTO.ActiveResponses activeResponses = new ActiveRFIDResponseDTO.ActiveResponses();
        activeResponses.setResult("FAIL");
        activeResponses.setVehicleId(vehicleId);
        activeResponses.setReason(null);
        activeResponses.setCode(2);
        // mock
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.empty());
        // execute
        assertEquals(activeResponses, vehicleService.activeRFIDOCS(rfiddto, null));
    }

    @Test
    public void testActiveRFIDCaseSuccess() throws Exception {
        //param
        Long vehicleId = 100L;
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        ActiveRFIDResponseDTO.ActiveResponses activeResponses = new ActiveRFIDResponseDTO.ActiveResponses();
        activeResponses.setResult("SUCCESS");
        activeResponses.setVehicleId(vehicleId);
        activeResponses.setCode(0);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ocsResponse.setDescription("Success");
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(vehicleEntity.getContractId());
        addVehicleRequestDTO.setEpc(vehicleEntity.getEpc());
        List<ActReasonEntity> actReasonEntityList = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        actReasonEntityList.add(actReasonEntity);
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, rfiddto.getReasonId(), rfiddto.getActTypeId(),
                vehicleEntity.getCustId(), vehicleEntity.getContractId(), vehicleEntity.getVehicleId(), ip);
        actionAuditEntity.setActionAuditId(1L);
        // mock
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, rfiddto.getActTypeId())).willReturn(ocsResponse);
        given(vehicleServiceJPA.save(vehicleEntity)).willReturn(vehicleEntity);
        given(actReasonRepositoryJPA.findAllByActTypeId(rfiddto.getActTypeId())).willReturn(actReasonEntityList);
        ReflectionTestUtils.setField(vehicleService, "actReasonRepositoryJPA", actReasonRepositoryJPA);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(actionAuditServiceImpl, "actionAuditRepositoryJPA", actionAuditRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "actionAuditService", actionAuditService);
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip) {
                return actionAuditEntity;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractNo("1000000V");
        contractEntity.setContractId(1L);
        contractEntity.setNoticeEmail("ngoctranminh26@gmail.com");
        contractEntity.setNoticePhoneNumber("09777777777");
        given(contractRepositoryJPA.findByContractId(any())).willReturn(contractEntity);
        given(jedisCacheService.hget(contractEntity.getContractNo(), Constants.CONTRACT_KEY_GEN)).willReturn("AAAAA");
        ReflectionTestUtils.setField(vehicleService, "smsRegisterUser", smsRegisterUser);
        // execute
        assertEquals(activeResponses, vehicleService.activeRFIDOCS(rfiddto, null));
    }

    @Test
    public void testActiveRFIDCaseCallOCSFail() throws Exception {
        //param
        Long vehicleId = 100L;
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        ActiveRFIDResponseDTO.ActiveResponses activeResponses = new ActiveRFIDResponseDTO.ActiveResponses();
        activeResponses.setResult("FAIL");
        activeResponses.setReason(null);
        activeResponses.setVehicleId(vehicleId);
        activeResponses.setCode(4);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(vehicleEntity.getContractId());
        addVehicleRequestDTO.setEpc(vehicleEntity.getEpc());
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, rfiddto.getActTypeId())).willReturn(ocsResponse);
        // execute
        assertEquals(activeResponses, vehicleService.activeRFIDOCS(rfiddto, null));
    }

    @Test
    public void testActiveRFIDCaseRFIDExist() throws Exception {
        //param
        Long vehicleId = 100L;
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        ActiveRFIDResponseDTO.ActiveResponses activeResponses = new ActiveRFIDResponseDTO.ActiveResponses();
        activeResponses.setResult("FAIL");
        activeResponses.setReason(null);
        activeResponses.setVehicleId(vehicleId);
        activeResponses.setCode(3);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ocsResponse.setDescription("WS_RFID_EXIST");
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(vehicleEntity.getContractId());
        addVehicleRequestDTO.setEpc(vehicleEntity.getEpc());
        // mock
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, rfiddto.getActTypeId())).willReturn(ocsResponse);
        // execute
        assertEquals(activeResponses, vehicleService.activeRFIDOCS(rfiddto, null));
    }


    @Test
    public void assignRFIDTest() throws Exception {
        //param
        List<AddVehicleRequestDTO> addVehicleRequestDTOS = new ArrayList<>();
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(contractId);
        addVehicleRequestDTO.setCustId(custId);
        addVehicleRequestDTO.setPlateNumber("19N109055");
        addVehicleRequestDTO.setActTypeId(1L);
        addVehicleRequestDTOS.add(addVehicleRequestDTO);
        listVehicleAssignRfidDTO = new ListVehicleAssignRfidDTO();
        listVehicleAssignRfidDTO.setStartRfidSerial("709111");
        listVehicleAssignRfidDTO.setAddVehicleRequestDTOS(addVehicleRequestDTOS);
        listVehicleAssignRfidDTO.setActTypeId(1L);
        ResultAssignRfidDTO resultAssignRfidDTO = new ResultAssignRfidDTO();
        List<AssignRfidDTO> list = new ArrayList<>();
        AssignRfidDTO assignRfidDTO = new AssignRfidDTO();
        assignRfidDTO.setRfidSerial("709111");
        assignRfidDTO.setDescriptions("FAIL");
        list.add(assignRfidDTO);
        resultAssignRfidDTO.setList(list);
        List<ActReasonEntity> actReasonEntityList = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        actReasonEntityList.add(actReasonEntity);
        ReflectionTestUtils.setField(vehicleService, "actReasonRepositoryJPA", actReasonRepositoryJPA);
        given(actReasonRepositoryJPA.findAllByActTypeId(addVehicleRequestDTO.getActTypeId())).willReturn(actReasonEntityList);
        actReasonEntityList.add(actReasonEntity);
        ReflectionTestUtils.setField(vehicleService, "boo1Service", boo1Service);
        ReflectionTestUtils.setField(vehicleService, "imService", imService);
        StockEtagDTO stockEtagDTO = new StockEtagDTO();
        stockEtagDTO.setEpc("");
        stockEtagDTO.setTid("");
        stockEtagDTO.setEtagTypeId(1L);
        given(imService.getSerialDetails(addVehicleRequestDTO.getRfidSerial(), null)).willReturn(stockEtagDTO);
        VehicleEntity vehicleEntity1 = new VehicleEntity();
        vehicleEntity1.setContractId(contractId);
        vehicleEntity1.setVehicleId(1L);
        vehicleEntity1.setActiveStatus("1");
        vehicleEntity1.setStatus("1");
        vehicleEntity1.setEpc("");
        vehicleEntity1.setTid("");
        vehicleEntity1.setRfidSerial("709111");
        vehicleEntity1.setRfidType("1");
        given(vehicleRepositoryJPA.findByVehicleIdAndStatusAndRfidSerial(addVehicleRequestDTO.getVehicleId(), "3", null)).willReturn(vehicleEntity1);
        given(vehicleRepositoryJPA.save(any())).willReturn(vehicleEntity1);
        ReflectionTestUtils.setField(vehicleService, "productService", productService);
        SaleBusinessMesDTO saleBusinessMesDTO = new SaleBusinessMesDTO();
        saleBusinessMesDTO.setSuccess("true");
        given(productService.callBCCSIM(null, 1L, vehicleEntity1,
                "709111", 1000L, Constants.CASE_UPDATE_VEHICLE.ASSIGN_VEHICLE, "")).willReturn(saleBusinessMesDTO);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        given(ocsService.createVehicleOCS(addVehicleRequestDTO, null, addVehicleRequestDTO.getActTypeId())).willReturn(ocsResponse);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        given(actionAuditService.updateLogToActionAudit(any())).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        //execute
        vehicleService.vehicleAssign(listVehicleAssignRfidDTO, null, custId, contractId);
    }


    @Test
    public void testDestroyRFIDCaseNullVehicle() throws Exception {
        //param
        Long vehicleId = 100L;
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        // mock
        given(vehicleServiceJPA.findById(vehicleId)).willReturn(Optional.empty());
        // execute
        assertEquals(false, vehicleService.destroyRFID(rfiddto, null));
    }
    @Test
    public void testDestroyRFIDCaseException() throws Exception {
        //param
        final Long vehicleId = 1L;
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("1");
        ocsResponse.setDescription("Call OCS fail");
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        // mock
        given(vehicleServiceJPA.findById(1L)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.deleteVehicle(vehicleEntity.getEpc(), String.valueOf(vehicleEntity.getContractId()), null, (int) rfiddto.getActTypeId())).willReturn(ocsResponse);
        Exception exception = assertThrows(EtcException.class, () -> {
            vehicleService.destroyRFID(rfiddto, null);
        });
        // execute
        String expectedMessage = "Destroy OCS fail: Call OCS fail";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void destroyRFIDTest() throws Exception {
        //param
        final Long vehicleId = 1L;
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ocsResponse.setDescription("Success");
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
        RFIDDTO rfiddto = new RFIDDTO();
        rfiddto.setVehicleId(vehicleId);
        rfiddto.setAmount(1000L);
        ActionAuditEntity auditEntity1 = new ActionAuditDTO().toEntity(null, 1L, 1L, 1L, 1L, 1L, "1234");
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(1000L);
        // mock
        given(vehicleServiceJPA.findById(1L)).willReturn(Optional.of(vehicleEntity));
        given(ocsService.deleteVehicle(vehicleEntity.getEpc(), String.valueOf(vehicleEntity.getContractId()), null, (int) rfiddto.getActTypeId())).willReturn(ocsResponse);
        ReflectionTestUtils.setField(actionAuditServiceImpl, "actionAuditRepositoryJPA", actionAuditRepositoryJPA);
        given(actionAuditService.updateLogToActionAudit(auditEntity1)).willReturn(auditEntity1);
        ReflectionTestUtils.setField(vehicleService, "actionAuditService", actionAuditService);
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip) {
                return auditEntity1;
            }
        };
        new MockUp<ActionAuditDetailDTO>() {
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId) {
                return actionAuditDetailEntity;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailServiceJPA", actionAuditDetailServiceJPA);
        given(saleTransService.addSaleTrans(rfiddto.getCustId(), vehicleEntity.getContractId(), null, null, rfiddto.getActTypeId())).willReturn(saleTransEntity);
        ReflectionTestUtils.setField(vehicleService, "saleTransService", saleTransService);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailService", saleTransDetailService);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailRepositoryJPA", saleTransDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailRepositoryJPA", saleTransDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "exceptionListServiceJPA", exceptionListServiceJPA);
        List<SaleTransDetailEntity> saleTransDetailEntityList = new ArrayList<>();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(1L);
        saleTransDetailEntityList.add(saleTransDetailEntity);
        ReflectionTestUtils.setField(vehicleService, "saleTransDetailRepositoryJPA", saleTransDetailRepositoryJPA);
        ReflectionTestUtils.setField(vehicleService, "boo1Service", boo1Service);
        given(saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode("null", "RFID", "BOO1")).willReturn(saleTransDetailEntityList);
        // execute
        Object object = vehicleService.destroyRFID(rfiddto, null);
    }

    @Test
    void testUpdateProfile() throws Exception {
        // Setup
        final java.sql.Date currDate = new java.sql.Date(0L);
        final org.springframework.security.core.Authentication authentication = null;
        final java.util.List<com.viettel.etc.dto.VehicleProfileDTO> dataParams = java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new java.sql.Date(0L), "approvedUser", new java.sql.Date(0L), "delUser", new java.sql.Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new java.sql.Date(0L)));

        // Configure ActionAuditService.updateLogToActionAudit(...).
        final com.viettel.etc.repositories.tables.entities.ActionAuditEntity actionAuditEntity1 = new com.viettel.etc.repositories.tables.entities.ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new java.sql.Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        when(actionAuditService.updateLogToActionAudit(new com.viettel.etc.repositories.tables.entities.ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new java.sql.Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(actionAuditEntity1);

        // Configure ContractServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.ContractEntity(0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L), "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        // Configure VehicleProfileRepositoryJPA.saveAll(...).
        final com.viettel.etc.repositories.tables.entities.VehicleProfileEntity vehicleProfileEntity = new com.viettel.etc.repositories.tables.entities.VehicleProfileEntity();
        vehicleProfileEntity.setVehicleProfileId(0L);
        vehicleProfileEntity.setContractId(0L);
        vehicleProfileEntity.setVehicleId(0L);
        vehicleProfileEntity.setDocumentTypeId(0L);
        vehicleProfileEntity.setFileName("fileName");
        vehicleProfileEntity.setFileSize("content".getBytes());
        vehicleProfileEntity.setFilePath("filePath");
        vehicleProfileEntity.setCreateUser("createUser");
        vehicleProfileEntity.setCreateDate(new java.sql.Date(0L));
        vehicleProfileEntity.setApprovedUser("approvedUser");
        final java.util.List<com.viettel.etc.repositories.tables.entities.VehicleProfileEntity> vehicleProfileEntityList = java.util.Arrays.asList(vehicleProfileEntity);
        when(vehicleProfileRepositoryJPA.saveAll(java.util.Arrays.asList(
                new com.viettel.etc.repositories.tables.entities.VehicleProfileEntity()))).thenReturn(vehicleProfileEntityList);
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
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication,
                                              Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId) throws UnknownHostException {
                return actionAuditEntity1;
            }
        };
        ReflectionTestUtils.setField(vehicleService, "fileService", fileService);
        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailServiceJPA", actionAuditDetailServiceJPA);
        vehicleService.updateProfile(currDate, authentication, 0L, 0L, 0L, 0L, dataParams);
    }


    @Ignore
    void testTransferVehicles() throws Exception {
        // Setup
        ContractProfileDTO contractProfileDTO = new com.viettel.etc.dto.ContractProfileDTO();
        contractProfileDTO.setContractId(0L);
        final VehicleTransferDTO vehicleTransferDTO = new VehicleTransferDTO(1L, java.util.Arrays.asList(0L),
                0L, 0L, 100L, 0L, java.util.Arrays.asList(contractProfileDTO));
        vehicleTransferDTO.setAmount(100L);
        Authentication authentication = null;
//
//        // Configure VehicleServiceJPA.findById(...).
        VehicleEntity vehicleEntity = new com.viettel.etc.repositories.tables.entities.VehicleEntity(0L, 0L, 2L,
                "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0,
                0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L,
                0L, 0L, 0L, "status", "activeStatus", "epc", "tid",
                "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode",
                new java.sql.Date(0L), new java.sql.Date(0L), 0L, 0L, "createUser", new java.sql.Date(0L),
                "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser",
                new java.sql.Date(0L), "owner", new java.sql.Date(0L), "appendixUsername", "note",
                "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        Optional<VehicleEntity> optionalVehicleEntity = Optional.of(vehicleEntity);
        when(vehicleServiceJPA.findById(0L)).thenReturn(optionalVehicleEntity);
//        Optional<VehicleEntity> vehicleEntityOptional = Optional.of(VehicleEntity);
//
        // Configure OCSService.transferVehicle(...).
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(ocsService.transferVehicle("epc", "tid", "2", "1", null, 0)).thenReturn(ocsResponse);
        // Configure ContractServiceJPA.findById(...).
        ContractEntity contractEntity = new ContractEntity(1L, 100L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName",
                "noticeStreet", "noticeAreaCode", "noticeEmail",
                "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L),
                "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L),
                "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        Optional<ContractEntity> optionalContractEntity = Optional.of(contractEntity);
        when(contractServiceJPA.findById(1L)).thenReturn(optionalContractEntity);
        // Run the test
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private VehicleEntity insertVehicle(VehicleEntity vehicleEntityOld, Long transferContractId) throws UnknownHostException {
                return vehicleEntity;
            }
        };

        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private void checkChargeTicket(Long vehicleId, Date currDate, Long contractId, Long transferContractId, String userLogin, Long newVehicleId) {
            }
        };

        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private void updateLogVehicleOld(Authentication authentication, VehicleTransferDTO vehicleTransferDTO, VehicleEntity vehicleEntityOld, VehicleEntity vehicleEntityUpdate, String ip)
                    throws JSONException, IllegalAccessException {
            }
        };

        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private void updateLogVehicleNew(Authentication authentication, VehicleEntity vehicleEntityInsert, String ip) throws JSONException, IllegalAccessException {
            }
        };

        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private void updateContractProfile(Authentication authentication, Date currDate, String userLogin, VehicleTransferDTO vehicleTransferDTO, String ip) throws Exception, EtcException {

            }

        };

        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private void updateContractProfile(Authentication authentication, Date currDate, String userLogin, VehicleTransferDTO vehicleTransferDTO, String ip) throws Exception, EtcException {
            }
        };
        vehicleService.transferVehicles(vehicleTransferDTO, authentication);
        // Verify the results
    }

    @Ignore
    void testExportVehiclesAssigned() throws Exception {
        // Setup
        Authentication authentication = null;

        // Configure VehicleRepository.exportVehiclesAssigned(...).
        ExportVehiclesAssignedDTO exportVehiclesAssignedDTO = new ExportVehiclesAssignedDTO();
        exportVehiclesAssignedDTO.setPlateNumber("plateNumber");
        exportVehiclesAssignedDTO.setCustId(0L);
        exportVehiclesAssignedDTO.setNameType("nameType");
        exportVehiclesAssignedDTO.setNetWeight(0L);
        exportVehiclesAssignedDTO.setCargoWeight(0L);
        exportVehiclesAssignedDTO.setNameGroup("nameGroup");
        exportVehiclesAssignedDTO.setGrossWeight(0L);
        exportVehiclesAssignedDTO.setPullingWeight(0L);
        exportVehiclesAssignedDTO.setSeatNumber(0L);
        exportVehiclesAssignedDTO.setEngineNumber("engineNumber");
        final java.util.List<com.viettel.etc.dto.ExportVehiclesAssignedDTO> exportVehiclesAssignedDTOS = java.util.Arrays.asList(exportVehiclesAssignedDTO);
        when(vehicleRepository.exportVehiclesAssigned(0L)).thenReturn(exportVehiclesAssignedDTOS);

        // Configure CategoriesService.findCategoriesCustomer(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity.setListData(java.util.Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(categoriesService.findCategoriesCustomer("token")).thenReturn(resultSelectEntity);

        // Run the test
        List<Long> vehicleTypeIdList = new ArrayList<>();

        vehicleTypeIdList.add(1L);
        List<VehicleTypeDTO.VehicleType> dataVehicleTypes = new ArrayList<>();
        when(vehicleTypeService.findVehicleTypeByListId(FnCommon.getStringToken(authentication), vehicleTypeIdList)).thenReturn(dataVehicleTypes);
        ReflectionTestUtils.setField(vehicleService, "vehicleTypeService", vehicleTypeService);
        ReflectionTestUtils.setField(vehicleService, "vehicleGroupService", vehicleGroupService);
        vehicleService.exportVehiclesAssigned(0L, authentication);
    }

    @Test
    void testFindProfileByVehicle() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleProfilePaginationDTO requestModel = new com.viettel.etc.dto.VehicleProfilePaginationDTO(0, 0, false, "documentTypeName", new java.sql.Date(0L), new java.sql.Date(0L), 0L, java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfilePaginationDTO.ProfileDTO("fileName", 0L)));

        // Configure VehicleRepository.findProfileByContract(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity.setListData(java.util.Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(vehicleRepository.findProfileByContract(new VehicleProfilePaginationDTO(0, 0,
                false, "documentTypeName", new java.sql.Date(0L), new java.sql.Date(0L),
                0L, java.util.Arrays.asList(new VehicleProfilePaginationDTO.ProfileDTO("fileName", 0L))))).
                thenReturn(resultSelectEntity);
        // Run the test
        ReflectionTestUtils.setField(vehicleService, "vehicleRepository", vehicleRepository);
        when(vehicleRepository.findProfileByContract(requestModel)).thenReturn(resultSelectEntity);
        List<VehicleProfilePaginationDTO> list = new ArrayList<>();
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private List<VehicleProfilePaginationDTO> addListProfileToResult(List<VehicleProfilePaginationDTO> vehicleProfileList) {
                return list;
            }
        };
        vehicleService.findProfileByVehicle(requestModel);
        // Verify the results
    }

    @Test
    void testTransferVehicleOnContract() throws Exception {
        // Setup
        final com.viettel.etc.dto.ContractProfileDTO contractProfileDTO = new com.viettel.etc.dto.ContractProfileDTO();
        contractProfileDTO.setContractProfileId(0L);
        contractProfileDTO.setCustId(0L);
        contractProfileDTO.setContractId(0L);
        contractProfileDTO.setDocumentTypeId(0L);
        contractProfileDTO.setFileName("fileName");
        contractProfileDTO.setFileSize("fileSize");
        contractProfileDTO.setFilePath("filePath");
        contractProfileDTO.setCreateUser("createUser");
        contractProfileDTO.setCreateDate(new java.sql.Date(0L));
        contractProfileDTO.setApprovedUser("approvedUser");
        final VehicleTransferDTO vehicleTransferDTO = new VehicleTransferDTO(0L, java.util.Arrays.asList(0L), 0L, 0L, 0L, 0L, java.util.Arrays.asList(contractProfileDTO));
        final Authentication authentication = null;

        // Configure VehicleServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.VehicleEntity> vehicleEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new java.sql.Date(0L), new java.sql.Date(0L), 0L, 0L, "createUser", new java.sql.Date(0L), "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "owner", new java.sql.Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"));
        when(vehicleServiceJPA.findById(0L)).thenReturn(vehicleEntity);

        // Configure VehicleServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.VehicleEntity vehicleEntity1 = new com.viettel.etc.repositories.tables.entities.VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new java.sql.Date(0L), new java.sql.Date(0L), 0L, 0L, "createUser", new java.sql.Date(0L), "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "owner", new java.sql.Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(vehicleServiceJPA.save(new com.viettel.etc.repositories.tables.entities.VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new java.sql.Date(0L), new java.sql.Date(0L), 0L, 0L, "createUser", new java.sql.Date(0L), "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "owner", new java.sql.Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"))).thenReturn(vehicleEntity1);

        // Configure ActionAuditService.updateLogToActionAudit(...).
        final com.viettel.etc.repositories.tables.entities.ActionAuditEntity actionAuditEntity = new com.viettel.etc.repositories.tables.entities.ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new java.sql.Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        when(actionAuditService.updateLogToActionAudit(new com.viettel.etc.repositories.tables.entities.ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new java.sql.Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(actionAuditEntity);

        // Configure ContractServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.ContractEntity(0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L), "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        // Configure OCSService.deleteVehicle(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(ocsService.deleteVehicle("RFID", "contractId", null, 0)).thenReturn(ocsResponse);

        // Configure OCSService.createVehicleOCS(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse1 = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(ocsService.createVehicleOCS(new com.viettel.etc.dto.AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new java.util.GregorianCalendar(2019, java.util.Calendar.JANUARY, 1).getTime(), new java.util.GregorianCalendar(2019, java.util.Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new java.util.GregorianCalendar(2019, java.util.Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new java.util.GregorianCalendar(2019, java.util.Calendar.JANUARY, 1).getTime(), "addfilesUser", new java.util.GregorianCalendar(2019, java.util.Calendar.JANUARY, 1).getTime(), "owner", new java.util.GregorianCalendar(2019, java.util.Calendar.JANUARY, 1).getTime(), "appendixUsername", java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new java.sql.Date(0L), "approvedUser", new java.sql.Date(0L), "delUser", new java.sql.Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new java.sql.Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"), null, 0L)).thenReturn(ocsResponse1);
        String ip = InetAddress.getLocalHost().getHostAddress();
        Long vehicleId = 0L;
        given(actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, vehicleTransferDTO.getReasonId(), vehicleTransferDTO.getActTypeId(),
                vehicleEntity1.getCustId(), vehicleEntity1.getContractId(), vehicleId, ip))).willReturn(actionAuditEntity);
        new MockUp<ActionAuditDTO>() {
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip) {
                return actionAuditEntity;
            }
        };

        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean checkOcsCode(OCSResponse response) {
                return true;
            }
        };

        ReflectionTestUtils.setField(vehicleService, "actionAuditDetailServiceJPA", actionAuditDetailServiceJPA);
        vehicleService.transferVehicleOnContract(vehicleTransferDTO, authentication);

        // Verify the resultsb
    }

    @Test
    void testFindVehicleByContract() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleByContractDTO requestModel = new com.viettel.etc.dto.VehicleByContractDTO();
        requestModel.setNameType("nameType");
        requestModel.setNameGroup("nameGroup");
        requestModel.setSalesType(0);
        requestModel.setColor("color");
        requestModel.setMark("mark");
        requestModel.setBrand("brand");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");
        requestModel.setSearchType("searchType");
        requestModel.setTypeScreen("typeScreen");
        requestModel.setVehicleTypeId(1L);
        final org.springframework.security.core.Authentication authentication = null;

        // Configure VehicleRepository.findVehicleByContract(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        List<VehicleByContractDTO> vehicleByContractDTOList = new ArrayList<>();
        vehicleByContractDTOList.add(requestModel);
        resultSelectEntity.setListData(vehicleByContractDTOList);
        resultSelectEntity.setCount("count");

        // Configure CategoriesService.findCategoriesCustomer(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity1 = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        List<Map<String, String>> listResultDTO = new ArrayList<>();
        Map<String, String> maps = new HashMap<>();
        maps.put("test", "test");
        listResultDTO.add(maps);
        resultSelectEntity1.setListData(listResultDTO);
        resultSelectEntity1.setCount("count");

        // Run the test
        given(vehicleRepository.findVehicleByContract(requestModel)).willReturn(resultSelectEntity);
        given(categoriesService.findCategoriesCustomer(FnCommon.getStringToken(authentication))).willReturn(resultSelectEntity1);
        ReflectionTestUtils.setField(vehicleService, "categoriesService", categoriesService);
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private String buildCategoryKeyByCode(String tableName, String id) {
                return "VEHICLE_TYPE_1";
            }
        };
        vehicleService.findVehicleByContract(requestModel, authentication);

        // Verify the results
    }

    @Ignore
    void testFindVehicleById() throws Exception {
        // Setup
        // Setup
        final com.viettel.etc.dto.VehicleByContractDTO requestModel = new com.viettel.etc.dto.VehicleByContractDTO();
        requestModel.setNameType("nameType");
        requestModel.setNameGroup("nameGroup");
        requestModel.setSalesType(0);
        requestModel.setColor("color");
        requestModel.setMark("mark");
        requestModel.setBrand("brand");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");
        requestModel.setSearchType("searchType");
        requestModel.setTypeScreen("typeScreen");
        requestModel.setVehicleTypeId(1L);
        requestModel.setVehicleColourId(1L);
        final org.springframework.security.core.Authentication authentication = null;

        // Configure VehicleRepository.findVehicleById(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        List<VehicleByContractDTO> list = new ArrayList<>();
        list.add(requestModel);
        resultSelectEntity.setListData(list);
        resultSelectEntity.setCount("count");
        when(vehicleRepository.findVehicleById(0L)).thenReturn(resultSelectEntity);

        // Configure CategoriesService.findCategoriesCustomer(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity1 = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity1.setListData(java.util.Arrays.asList("value"));
        resultSelectEntity1.setCount("count");
        when(categoriesService.findCategoriesCustomer("token")).thenReturn(resultSelectEntity1);
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private String buildCategoryKeyByCode(String tableName, String id) {
                return "VEHICLE_COLOUR_1";
            }
        };
        // Run the test
        vehicleService.findVehicleById(0L, authentication);
        // Verify the results
    }


    @Test
    void testSearchVehicle() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleDTO vehicleDTO = new com.viettel.etc.dto.VehicleDTO();
        vehicleDTO.setPagesize(100);
        vehicleDTO.setStartrecord(1);
        vehicleDTO.setVehicleGroupId(0L);
        final org.springframework.security.core.Authentication authentication = null;
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(VehicleEntity.class)).thenReturn(query);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(query.from(VehicleEntity.class)).thenReturn(root);

        // Configure ContractServiceJPA.findByAccountUser(...).
        final java.util.List<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntities =
                java.util.Arrays.asList(new com.viettel.etc.repositories.tables.entities.ContractEntity
                        (0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L),
                                "description", "status", "emailNotification", "smsNotification",
                                "pushNotification", "billCycle", "payCharge",
                                0L, "accountUser", "noticeName",
                                "noticeAreaName", "noticeStreet",
                                "noticeAreaCode", "noticeEmail", "noticePhoneNumber",
                                "profileStatus", "approvedUser", new java.sql.Date(0L),
                                "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L),
                                "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        List<VehicleDTO> resultList = new ArrayList<>();
        resultList.add(vehicleDTO);
        List<VehicleEntity> listResult = new ArrayList<>();
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(vehicleId);
        vehicleEntity.setEpc("RFID");
        vehicleEntity.setContractId(1L);
        listResult.add(vehicleEntity);
        when(entityManager.createQuery(query)).thenReturn(queryVehicle);
        when(queryVehicle.setFirstResult(vehicleDTO.getStartrecord())).thenReturn(queryVehicle);
        when(queryVehicle.setMaxResults(vehicleDTO.getPagesize())).thenReturn(queryVehicle);
        when(queryVehicle.getResultList()).thenReturn(listResult);
        when(contractServiceJPA.findByAccountUser(FnCommon.getUserLogin(authentication))).thenReturn(contractEntities);
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private <T> T getDataOfOtherModule(String url, Authentication authentication, Class<T> responseType) throws JsonProcessingException {
                ObjectMapper mapper = new ObjectMapper();
//                String json = "{\"mess\":1196,\"code\":1666,\"email\":\"joseluis.delacruz@gmail.com\"}";
                String json = "{\n" +
                        "\"mess\" : {\"code\" : 12}\n" +
                        "}";
                JsonNode node = mapper.readTree(json);
                return (T) node;
            }
        };
        when(countQuery.from(VehicleEntity.class)).thenReturn(countRoot);
        when(criteriaBuilder.count(countRoot)).thenReturn(selection);
        when(countQuery.select(criteriaBuilder.count(countRoot))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery.select(criteriaBuilder.count(countRoot)))).thenReturn(queryLong);
        // Run the test
        vehicleService.searchVehicle(vehicleDTO, authentication);

        // Verify the results
    }

    @Test
    void testFindVehicleAssignedRFID() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleByContractDTO requestModel = new com.viettel.etc.dto.VehicleByContractDTO();
        requestModel.setNameType("nameType");
        requestModel.setNameGroup("nameGroup");
        requestModel.setSalesType(0);
        requestModel.setColor("color");
        requestModel.setMark("mark");
        requestModel.setBrand("brand");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");
        requestModel.setSearchType("searchType");
        requestModel.setTypeScreen("typeScreen");
        List<VehicleByContractDTO> list = new ArrayList<>();
        list.add(requestModel);

        final org.springframework.security.core.Authentication authentication = null;

        // Configure VehicleRepository.findVehicleAssignedRFID(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity.setListData(list);
        resultSelectEntity.setCount("count");
        given(vehicleRepository.findVehicleAssignedRFID(requestModel)).willReturn(resultSelectEntity);

        // Configure CategoriesService.findCategoriesCustomer(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity1 = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        List<Map<String, String>> listResultDTO = new ArrayList<>();
        Map<String, String> maps = new HashMap<>();
        maps.put("test", "test");
        listResultDTO.add(maps);
        resultSelectEntity1.setListData(listResultDTO);
        resultSelectEntity1.setCount("count");
        when(categoriesService.findCategoriesCustomer(FnCommon.getStringToken(authentication))).thenReturn(resultSelectEntity1);
        new MockUp<VehicleServiceImpl>() {
            @mockit.Mock
            private String buildCategoryKeyByCode(String tableName, String id) {
                return "VEHICLE_TYPE_1";
            }
        };
        // Run the test
        vehicleService.findVehicleAssignedRFID(requestModel, authentication);

        // Verify the results
    }

    @Test
    void testDownloadVehiclesTemplate() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;

        // Configure CategoriesService.findCategoriesCustomer(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        List<LinkedHashMap<?, ?>> vehicleTypes = new ArrayList<>();
        LinkedHashMap<String, String> lhm
                = new LinkedHashMap<String, String>();
        lhm.put("one", "ngoctm");
        vehicleTypes.add(lhm);
        resultSelectEntity.setListData(vehicleTypes);
        when(categoriesService.findCategoriesCustomer(FnCommon.getStringToken(authentication))).thenReturn(resultSelectEntity);
        ReflectionTestUtils.setField(vehicleService, "vehicleTypeService", vehicleTypeService);
        // Run the test
        ReflectionTestUtils.setField(vehicleService, "tableNameCategories", "PLATE_TYPE,VEHICLE_BRAND,VEHICLE_MARK,VEHICLE_COLOUR");
        vehicleService.downloadVehiclesTemplate(authentication);
    }

    @Test
    void testDeleteProfile() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.UpdateVehicleProfileDTO updateVehicleProfileDTO = new com.viettel.etc.dto.UpdateVehicleProfileDTO();
        updateVehicleProfileDTO.setVehicleProfiles(java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new java.sql.Date(0L), "approvedUser", new java.sql.Date(0L), "delUser", new java.sql.Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new java.sql.Date(0L))));
        updateVehicleProfileDTO.setContractId(0L);
        updateVehicleProfileDTO.setCustId(0L);
        updateVehicleProfileDTO.setReasonId(0L);
        updateVehicleProfileDTO.setActTypeId(0L);

        // Configure VehicleProfileRepositoryJPA.findById(...).
        final com.viettel.etc.repositories.tables.entities.VehicleProfileEntity vehicleProfileEntity1 = new com.viettel.etc.repositories.tables.entities.VehicleProfileEntity();
        vehicleProfileEntity1.setVehicleProfileId(0L);
        vehicleProfileEntity1.setContractId(0L);
        vehicleProfileEntity1.setVehicleId(0L);
        vehicleProfileEntity1.setDocumentTypeId(0L);
        vehicleProfileEntity1.setFileName("fileName");
        vehicleProfileEntity1.setFileSize("content".getBytes());
        vehicleProfileEntity1.setFilePath("filePath");
        vehicleProfileEntity1.setCreateUser("createUser");
        vehicleProfileEntity1.setCreateDate(new java.sql.Date(0L));
        vehicleProfileEntity1.setApprovedUser("approvedUser");
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.VehicleProfileEntity> vehicleProfileEntity = java.util.Optional.of(vehicleProfileEntity1);
        when(vehicleProfileRepositoryJPA.findById(0L)).thenReturn(vehicleProfileEntity);

        // Run the test
        ReflectionTestUtils.setField(vehicleService, "fileService", fileService);
        vehicleService.deleteProfile(authentication, 0L, 0L, updateVehicleProfileDTO);
        // Verify the result
    }

    @Test
    void testFindProfileByVehicleId() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleProfileDTO requestModel = new com.viettel.etc.dto.VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new java.sql.Date(0L), "approvedUser", new java.sql.Date(0L), "delUser", new java.sql.Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new java.sql.Date(0L));
        // Configure VehicleRepository.findProfileByVehicleId(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity.setListData(java.util.Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(vehicleRepository.findProfileByVehicleId(new com.viettel.etc.dto.VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new java.sql.Date(0L), "approvedUser", new java.sql.Date(0L), "delUser", new java.sql.Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new java.sql.Date(0L)))).thenReturn(resultSelectEntity);
        // Run the test
        vehicleService.findProfileByVehicleId(requestModel);
        // Verify the results
    }

    @Test
    void testGetVehiclesByPlateNumber() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleSearchDTO itemParamsEntity = new com.viettel.etc.dto.VehicleSearchDTO();
        itemParamsEntity.setPlateNumber("plateNumber");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setIsPortalBot(false);

        // Configure VehicleRepository.getVehiclesByPlateNumberForPortalBot(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity.setListData(java.util.Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(vehicleRepository.getVehiclesByPlateNumberForPortalBot(new com.viettel.etc.dto.VehicleSearchDTO())).thenReturn(resultSelectEntity);

        // Configure VehicleRepository.getVehiclesByPlateNumber(...).
        final com.viettel.etc.xlibrary.core.entities.ResultSelectEntity resultSelectEntity1 = new com.viettel.etc.xlibrary.core.entities.ResultSelectEntity();
        resultSelectEntity1.setListData(java.util.Arrays.asList("value"));
        resultSelectEntity1.setCount("count");
        when(vehicleRepository.getVehiclesByPlateNumber(new com.viettel.etc.dto.VehicleSearchDTO())).thenReturn(resultSelectEntity1);
        // Run the test
        vehicleService.getVehiclesByPlateNumber(itemParamsEntity);
        // Verify the results
    }

    @Test
    void testDownloadProfileByContract() throws Exception {
        // Setup
        final com.viettel.etc.dto.VehicleProfilePaginationDTO expectedResult = new com.viettel.etc.dto.VehicleProfilePaginationDTO(0, 0, false, "documentTypeName", new java.sql.Date(0L), new java.sql.Date(0L), 0L, java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfilePaginationDTO.ProfileDTO("fileName", 0L)));

        // Configure VehicleRepository.downloadProfileByContract(...).
        final java.util.List<com.viettel.etc.dto.VehicleProfilePaginationDTO> vehicleProfilePaginationDTOS = java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfilePaginationDTO(0, 0, false, "documentTypeName", new java.sql.Date(0L), new java.sql.Date(0L), 0L, java.util.Arrays.asList(new com.viettel.etc.dto.VehicleProfilePaginationDTO.ProfileDTO("fileName", 0L))));
        when(vehicleRepository.downloadProfileByContract(0L)).thenReturn(vehicleProfilePaginationDTOS);

        // Run the test
        vehicleService.downloadProfileByContract(0L);
    }

}
