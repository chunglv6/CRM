package com.viettel.etc.services.impl;

import com.squareup.okhttp.Request;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSCreateContractForm;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.utils.FnCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class OCSServiceImplTest {

    @Mock
    private WsAuditRepositoryJPA mockWsAuditRepositoryJPA;

    private OCSServiceImpl ocsServiceImplUnderTest;

    private WsAuditEntity wsAuditEntity;

    @BeforeEach
    void setUp() {
        initMocks(this);
        ocsServiceImplUnderTest = new OCSServiceImpl(mockWsAuditRepositoryJPA);
        wsAuditEntity = new WsAuditEntity();
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
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "toandm";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getClientId(Authentication authentication) {
                return "X";
            }
        };
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            public String buildLogRequest(Request request, String body) {
                return "Build log";
            }
        };
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            public void updateWriteLogFail(WsAuditEntity wsAuditEntity) {
                System.out.println("Run update log");
            }
        };


        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) {
                Map<Object, Object> result = new HashMap<>();
                result.put("resultCode", "0");
                result.put("description", "Test");
                return result.toString();
            }
        };

    }


    @Test
    void testWriteLog() {
        // Setup
        final Authentication authentication = null;
        final WsAuditEntity expectedResult = new WsAuditEntity();
        expectedResult.setWsCallType("wsCallType");
        expectedResult.setActTypeId(0L);
        expectedResult.setRequestTime(new Date(System.currentTimeMillis()));
        expectedResult.setActionUserName("system");
        expectedResult.setWsUri("url");
        expectedResult.setSourceAppId("crm");
        expectedResult.setIpPc("ip");
        expectedResult.setDestinationAppId("OCS");
        expectedResult.setStatus("status");
        expectedResult.setFinishTime(0L);

        expectedResult.setMsgReponse("resp".getBytes());
        expectedResult.setMsgRequest("req".getBytes());
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(expectedResult);
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            public WsAuditEntity writeLog(String req, String resp, String url, String ip, long timeCallOCS, long actTypeId,
                                          Authentication authentication, String wsCallType, String status){
                return expectedResult;
            }
        };
        // Run the test
        final WsAuditEntity result = ocsServiceImplUnderTest.writeLog("req", "resp", "url", "ip", 0L, 0L, authentication, "wsCallType", "status");
        // Verify the results

        assertEquals(expectedResult,result);
    }

    @Test
    void testCreateVehicleOCS() {
        // Setup
        final AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi");
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);
        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.createVehicleOCS(addVehicleRequestDTO, authentication, 0L);
        assertEquals(result.getResultCode(), "0");

        // Verify the results
    }

    @Ignore
    void testThrowExceptionWhenCreateVehicleOCS() {
        // Setup
        final AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi");
        final Authentication authentication = null;
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) throws Exception {
                throw new Exception();
            }
        };
        // Run the test
        assertThrows(Exception.class, () -> ocsServiceImplUnderTest.createVehicleOCS(addVehicleRequestDTO, authentication, 0L));
    }

    @Ignore
    void testModifyVehicleOCS() {
        // Setup
//        final UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO(0L, 0L, "epc", "status", 0L, "plateNumber", "effDate");
        final UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO();
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.modifyVehicleOCS(updateVehicleRequestOCSDTO, authentication, true);
        assertEquals(result.getResultCode(), "0");
        // Verify the results
    }

    @Test
    void testCreateContract() {
        // Setup
        final OCSCreateContractForm form = new OCSCreateContractForm("contractId", "contractCode", "customerId", "customerName", "msisdn", "emailAddress", "birthDay", "identify", "effDate", "expDate", "customerType", "contractType", "customerStatus", "contractStatus", "langid", "chargeType", "chargeMethod", "accountNumber", "accountOwner");
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.createContract(form, authentication, 0);
        assertEquals(result.getResultCode(), "0");
        // Verify the results
    }

    @Test
    void testUpdateContract() {
        // Setup
        final OCSUpdateContractForm form = new OCSUpdateContractForm();
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.updateContract(form, authentication, 0);
        assertEquals(result.getResultCode(), "0");
        // Verify the results
    }

    @Test
    void testTerminateContract() {
        // Setup
        final OCSUpdateContractForm form = new OCSUpdateContractForm();
        final Authentication authentication = null;
        // Configure WsAuditRepositoryJPA.save(...).
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);
        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.terminateContract(form, authentication, 0);
        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }

    @Test
    void testDeleteVehicle() {
        // Setup
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);
        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.deleteVehicle("EPC", "contractId", authentication, 0);
        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }

    @Test
    void testCharge() throws Exception {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequestDTO = new AddSupOfferRequestDTO();
        addSupOfferRequestDTO.setSaleTransCode("saleTransCode");
        addSupOfferRequestDTO.setSaleTransDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        addSupOfferRequestDTO.setInvoiceUsed("invoiceUsed");
        addSupOfferRequestDTO.setShopId(0L);
        addSupOfferRequestDTO.setShopName("shopName");
        addSupOfferRequestDTO.setStaffId(0L);
        addSupOfferRequestDTO.setStaffName("staffName");
        addSupOfferRequestDTO.setPaymentMethodId(0L);
        addSupOfferRequestDTO.setAccountNumber("accountNumber");
        addSupOfferRequestDTO.setAccountOwner("accountOwner");
        addSupOfferRequestDTO.setAmount(0L);
        addSupOfferRequestDTO.setActTypeId(0L);

        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.charge(addSupOfferRequestDTO, authentication, 0L, "partyCode");

        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }


    @Ignore
    void testCharge_ThrowsException() {
        // Setup
        final AddSupOfferRequestDTO addSupOfferRequestDTO = new AddSupOfferRequestDTO();
        addSupOfferRequestDTO.setSaleTransCode("saleTransCode");
        addSupOfferRequestDTO.setSaleTransDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        addSupOfferRequestDTO.setInvoiceUsed("invoiceUsed");
        addSupOfferRequestDTO.setShopId(0L);
        addSupOfferRequestDTO.setShopName("shopName");
        addSupOfferRequestDTO.setStaffId(0L);
        addSupOfferRequestDTO.setStaffName("staffName");
        addSupOfferRequestDTO.setPaymentMethodId(0L);
        addSupOfferRequestDTO.setAccountNumber("accountNumber");
        addSupOfferRequestDTO.setAccountOwner("accountOwner");
        addSupOfferRequestDTO.setAmount(0L);
        addSupOfferRequestDTO.setActTypeId(0L);

        final Authentication authentication = null;


        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) throws Exception {
                throw new Exception();
            }
        };
        // Run the test
        assertThrows(Exception.class, () -> ocsServiceImplUnderTest.charge(addSupOfferRequestDTO, authentication, 0L, "partyCode"));
    }

    @Test
    void testAddSupOffer() throws Exception {
        // Setup
        final VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSuffOfferDTO.setServiceFeeId(0L);
        vehicleAddSuffOfferDTO.setServiceFeeName("serviceFeeName");
        vehicleAddSuffOfferDTO.setVehicleId(0L);
        vehicleAddSuffOfferDTO.setPlateNumber("plateNumber");
        vehicleAddSuffOfferDTO.setEpc("epc");
        vehicleAddSuffOfferDTO.setTid("tid");
        vehicleAddSuffOfferDTO.setRfidSerial("rfidSerial");
        vehicleAddSuffOfferDTO.setVehiclesGroupId(0L);
        vehicleAddSuffOfferDTO.setServicePlanId(0L);
        vehicleAddSuffOfferDTO.setServicePlanName("servicePlanName");
        vehicleAddSuffOfferDTO.setOfferId("0");
        vehicleAddSuffOfferDTO.setOfferLevel("0");
        vehicleAddSuffOfferDTO.setStationLevel("0");
        vehicleAddSuffOfferDTO.setStationId(0L);
        vehicleAddSuffOfferDTO.setLaneId("0");
        vehicleAddSuffOfferDTO.setIsOCSCharged("0");
        vehicleAddSuffOfferDTO.setChargedVehicleType("0");
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.addSupOffer(vehicleAddSuffOfferDTO, authentication, 0L, 0L, 1L, "staffName", "partyCode");

        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }

    @Test
    void testAddSupOffer_ThrowsException() {
        // Setup
        final VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSuffOfferDTO.setServiceFeeId(0L);
        vehicleAddSuffOfferDTO.setServiceFeeName("serviceFeeName");
        vehicleAddSuffOfferDTO.setVehicleId(0L);
        vehicleAddSuffOfferDTO.setPlateNumber("plateNumber");
        vehicleAddSuffOfferDTO.setEpc("epc");
        vehicleAddSuffOfferDTO.setTid("tid");
        vehicleAddSuffOfferDTO.setRfidSerial("rfidSerial");
        vehicleAddSuffOfferDTO.setVehiclesGroupId(0L);
        vehicleAddSuffOfferDTO.setServicePlanId(0L);
        vehicleAddSuffOfferDTO.setServicePlanName("servicePlanName");
        vehicleAddSuffOfferDTO.setOfferId("0");
        vehicleAddSuffOfferDTO.setOfferLevel("0");
        vehicleAddSuffOfferDTO.setStationLevel("0");
        vehicleAddSuffOfferDTO.setStationId(0L);
        vehicleAddSuffOfferDTO.setLaneId("0");
        vehicleAddSuffOfferDTO.setIsOCSCharged("0");
        vehicleAddSuffOfferDTO.setChargedVehicleType("0");
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) throws Exception {
                throw new Exception();
            }
        };
        // Run the test
        assertThrows(Exception.class, () -> ocsServiceImplUnderTest.addSupOffer(vehicleAddSuffOfferDTO, authentication, 0L, 0L, 0L, "staffName", "partyCode"));
    }

    @Test
    void testGetContractInfo() {
        // Setup
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final String result = ocsServiceImplUnderTest.getContractInfo(authentication, 0L);

        // Verify the results
        assertEquals("{resultCode=0, description=Test}", result);
    }


    @Test
    void testDeleteSupOffer() {
        // Setup
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.deleteSupOffer(authentication, 0L, 0L, "epc", "offerId", "offerLevel");

        // Verify the results

        assertEquals(result.getResultCode(), "0");
    }

    @Test
    void testAddBalance() {
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

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.addBalance(authentication, 0L, 0L, 0L);

        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }

    @Ignore
    void testThrowAddBalance() {
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
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) throws Exception {
                throw new Exception();
            }
        };

        // Verify the results
        assertThrows(Exception.class, () -> ocsServiceImplUnderTest.addBalance(authentication, 0L, 0L, 0L));
    }

    @Test
    void testTransferVehicle() {
        // Setup
        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);
        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.transferVehicle("EPC", "TID", "contractId", "newContractId", authentication, 0);

        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }

    @Test
    void testChangeSupOffer() throws Exception {
        // Setup
        final VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSuffOfferDTO.setServiceFeeId(0L);
        vehicleAddSuffOfferDTO.setServiceFeeName("serviceFeeName");
        vehicleAddSuffOfferDTO.setVehicleId(0L);
        vehicleAddSuffOfferDTO.setPlateNumber("plateNumber");
        vehicleAddSuffOfferDTO.setEpc("epc");
        vehicleAddSuffOfferDTO.setTid("tid");
        vehicleAddSuffOfferDTO.setRfidSerial("rfidSerial");
        vehicleAddSuffOfferDTO.setVehiclesGroupId(0L);
        vehicleAddSuffOfferDTO.setServicePlanId(0L);
        vehicleAddSuffOfferDTO.setServicePlanName("servicePlanName");
        vehicleAddSuffOfferDTO.setOfferId("0");
        vehicleAddSuffOfferDTO.setOfferLevel("0");
        vehicleAddSuffOfferDTO.setStationLevel("0");
        vehicleAddSuffOfferDTO.setStationId(0L);
        vehicleAddSuffOfferDTO.setLaneId("0");
        vehicleAddSuffOfferDTO.setIsOCSCharged("0");
        vehicleAddSuffOfferDTO.setChargedVehicleType("0");

        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final OCSResponse result = ocsServiceImplUnderTest.changeSupOffer(vehicleAddSuffOfferDTO, authentication, 0L, 0L, "staffName", "isRecurring");

        // Verify the results
        assertEquals(result.getResultCode(), "0");
    }

    @Ignore
    void testChangeSupOffer_ThrowsException() {
        // Setup
        final VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSuffOfferDTO.setServiceFeeId(0L);
        vehicleAddSuffOfferDTO.setServiceFeeName("serviceFeeName");
        vehicleAddSuffOfferDTO.setVehicleId(0L);
        vehicleAddSuffOfferDTO.setPlateNumber("plateNumber");
        vehicleAddSuffOfferDTO.setEpc("epc");
        vehicleAddSuffOfferDTO.setTid("tid");
        vehicleAddSuffOfferDTO.setRfidSerial("rfidSerial");
        vehicleAddSuffOfferDTO.setVehiclesGroupId(0L);
        vehicleAddSuffOfferDTO.setServicePlanId(0L);
        vehicleAddSuffOfferDTO.setServicePlanName("servicePlanName");
        vehicleAddSuffOfferDTO.setOfferId("0");
        vehicleAddSuffOfferDTO.setOfferLevel("0");
        vehicleAddSuffOfferDTO.setStationLevel("0");
        vehicleAddSuffOfferDTO.setStationId(0L);
        vehicleAddSuffOfferDTO.setLaneId("0");
        vehicleAddSuffOfferDTO.setIsOCSCharged("0");
        vehicleAddSuffOfferDTO.setChargedVehicleType("0");

        final Authentication authentication = null;
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) throws Exception {
                throw new Exception();
            }
        };
        // Run the test
        assertThrows(Exception.class, () -> ocsServiceImplUnderTest.changeSupOffer(vehicleAddSuffOfferDTO, authentication, 0L, 0L, "staffName", "isRecurring"));
    }

    @Test
    void testQueryVehicleOcs() {
        // Setup
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final LinkedHashMap<?, ?> result = ocsServiceImplUnderTest.queryVehicleOcs("epc", authentication);

        // Verify the results
        assertEquals(result.get("resultCode"), 0.0);
    }

    @Test
    void testQueryContractOcs() {
        // Setup
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final LinkedHashMap<?, ?> result = ocsServiceImplUnderTest.queryContractOcs("contractId", authentication);

        // Verify the results
        assertEquals(result.get("resultCode"), 0.0);
    }

    @Test
    void testDoPostRequest() {
        // Setup
        final Map<String, String> params = new HashMap<>();
        final Authentication authentication = null;

        // Configure WsAuditRepositoryJPA.save(...).
        when(mockWsAuditRepositoryJPA.save(new WsAuditEntity())).thenReturn(wsAuditEntity);

        // Run the test
        final String result = ocsServiceImplUnderTest.doPostRequest("url", params, "json", 0, authentication, 0L);

        // Verify the results
        assertEquals("{resultCode=0, description=Test}", result);
    }

}
