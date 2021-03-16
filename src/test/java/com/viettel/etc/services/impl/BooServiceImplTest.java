package com.viettel.etc.services.impl;

import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.repositories.BooRepository;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.ExceptionListServiceJPA;
import com.viettel.etc.services.tables.SaleTransDetailServiceJPA;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BooServiceImplTest {

    private BooServiceImpl booServiceImplUnderTest;


    @BeforeEach
    void setUp() {
        booServiceImplUnderTest = new BooServiceImpl();
        booServiceImplUnderTest.exceptionListServiceJPA = mock(ExceptionListServiceJPA.class);
        booServiceImplUnderTest.booRepository = mock(BooRepository.class);
        booServiceImplUnderTest.saleTransRepositoryJPA = mock(SaleTransRepositoryJPA.class);
        booServiceImplUnderTest.saleTransDetailRepositoryJPA = mock(SaleTransDetailRepositoryJPA.class);
        booServiceImplUnderTest.vehicleRepositoryJPA = mock(VehicleRepositoryJPA.class);
        booServiceImplUnderTest.vehicleServiceJPA = mock(VehicleServiceJPA.class);
        booServiceImplUnderTest.contractServiceJPA = mock(ContractServiceJPA.class);
        booServiceImplUnderTest.vehicleTypeService = mock(VehicleTypeService.class);
        booServiceImplUnderTest.vehicleGroupService = mock(VehicleGroupService.class);
        booServiceImplUnderTest.vehicleService = mock(VehicleService.class);
        booServiceImplUnderTest.saleTransDetailServiceJPA = mock(SaleTransDetailServiceJPA.class);
        booServiceImplUnderTest.ocsService = mock(OCSService.class);
        booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA = mock(SaleTransDelBoo1RepositoryJPA.class);
        booServiceImplUnderTest.EtExceptionListRepositoryJPA = mock(ExceptionListRepositoryJPA.class);
        booServiceImplUnderTest.ticketRepository = mock(TicketRepository.class);
        booServiceImplUnderTest.actionAuditService = mock(ActionAuditService.class);
        booServiceImplUnderTest.actionAuditDetailService = mock(ActionAuditDetailService.class);
        booServiceImplUnderTest.actionAuditDetailRepositoryJPA = mock(ActionAuditDetailRepositoryJPA.class);
        booServiceImplUnderTest.actReasonRepositoryJPA = mock(ActReasonRepositoryJPA.class);
        booServiceImplUnderTest.categoryMappingRepositoryJPA = mock(CategoryMappingRepositoryJPA.class);
        booServiceImplUnderTest.vehicleTypeService = mock(VehicleTypeService.class);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getStringToken(Authentication authentication) {
                return "toandm";
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public boolean checkOcsCode(OCSResponse response) {
                return true;
            }
        };

        booServiceImplUnderTest.stationService = mock(StationService.class);

        ReflectionTestUtils.setField(booServiceImplUnderTest, "stationsUrl", "http://10.60.156.159:8989/api/v1/stations/{stationId}");
        ReflectionTestUtils.setField(booServiceImplUnderTest, "stagesBooUrl", "http://10.60.156.159:8989/api/v1/stations/{stationId}");
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("30N12345T");
        saleTransDetailEntity.setEpc("1");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleTransDetailEntity> saleTransDetailEntities = Arrays.asList(saleTransDetailEntity);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.
                findOrgPlateTypeNumberAndEpcAndBooFlow("30N12345T",
                        "1", 3L)).thenReturn(saleTransDetailEntities);
    }

    @Test
    void testQueryTicket() {
        // Setup
        final Authentication authentication = null;
        final ReqQueryTicketDTO reqQueryTicketDTO = new ReqQueryTicketDTO();
        reqQueryTicketDTO.setEtag("etag");
        reqQueryTicketDTO.setPlate("plate");
        reqQueryTicketDTO.setSubscription_ticket_id(0L);
        reqQueryTicketDTO.setStation_in_id(0L);
        reqQueryTicketDTO.setStation_out_id(0L);
        reqQueryTicketDTO.setRequest_datetime(0L);

        // Configure BooRepository.queryTicketBoo(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(booServiceImplUnderTest.booRepository.queryTicketBoo(reqQueryTicketDTO)).thenReturn(resultSelectEntity);

        // Run the test
        final ResQueryTicketDTO result = (ResQueryTicketDTO) booServiceImplUnderTest.queryTicket(authentication, reqQueryTicketDTO);

        // Verify the results
        assertEquals(result.getData().size(), 1);
    }

    @Test
    void testCalculatorTicket() {
        // Setup
        final Authentication authentication = null;
        final ReqCalculatorTicketDTO reqCalculatorTicketDTO = new ReqCalculatorTicketDTO();
        reqCalculatorTicketDTO.setStation_type("O");
        reqCalculatorTicketDTO.setStation_in_id(5018L);

        reqCalculatorTicketDTO.setStation_out_id(0L);
        reqCalculatorTicketDTO.setTicket_type("ticket_type");
        reqCalculatorTicketDTO.setStart_date("20201111");
        reqCalculatorTicketDTO.setEnd_date("20201130");
        reqCalculatorTicketDTO.setPlate("30N1234");
        reqCalculatorTicketDTO.setEtag("1");
        reqCalculatorTicketDTO.setVehicle_type(0L);
        reqCalculatorTicketDTO.setRegister_vehicle_type("0");
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean isNullOrEmpty(String test) {
                return false;
            }
        };
        when(booServiceImplUnderTest.vehicleGroupService.getVehicleGroupById("token",
                new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix",
                        "30N12345T", 0L, 0L, 0L, 0.0,
                        0.0, 0.0, 0.0, "chassicNumber",
                        "engineNumber", 0L, 0L, 0L, 0L,
                        "status", "activeStatus", "1", "tid", "rfidSerial",
                        "reservedMemory", "rfidType", 0L, "offerCode",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        0L, 0L, "createUser", "contractNo",
                        "type", "offerExternalId", "vehicleType", 0L,
                        "vehicleColor", "plateColor", "vehicleMark", "plate",
                        0L, "vehicleBrand",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "profileStatus", "approvedUser",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "addfilesUser",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "appendixUsername",
                        Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L,
                                0L, "fileName", "fileSize", "filePath",
                                "createUser", new Date(0L), "approvedUser", new Date(0L),
                                "delUser", new Date(0L), "description", "status",
                                "fileBase64", 0L, "documentTypeName",
                                0, 0, new Date(0L))), 0L, 0,
                        "vehicleGroupCode", "plateTypeCode",
                        "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"))).thenReturn(Arrays.asList("value"));

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                Map<Object, Object> station = new HashMap<>();
                station.put("id", 0L);
                station.put("method_charge_id", 1L);
                station.put("name", "0");
                Map<Object, Object> result = new HashMap<>();
                result.put("data", station.toString());
                System.out.println(result.toString());
                return result.toString();
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            private Map<String, String> checkVehicleCouldChargeTicket(ReqCalculatorTicketDTO requestCalculatorTicket, StageBooDTO.Stage stage, StationBooDTO.Station station, String plateType) {
                Map<String, String> mapException = new HashMap<>();
                mapException.put("PROMOTION_ID", "0");
                mapException.put("EXCEPTION_VEHICLE_TYPE", "0");
                mapException.put("EXCEPTION_VEHICLE_TYPE_PROMOTION_ID", "0");
                mapException.put("EXCEPTION_TICKET_TYPE_PROMOTION_ID", "0");
                return mapException;
            }
        };
        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(exceptionListEntities);


        // Configure BooRepository.getFeeBoo(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        ResCalculatorTicketDTO resCalculatorTicketDTO = new ResCalculatorTicketDTO();
        resCalculatorTicketDTO.setAuto_renew(1L);
        resCalculatorTicketDTO.setCharge_method_id(1L);
        resCalculatorTicketDTO.setOcs_code("0");
        resCalculatorTicketDTO.setPrice_amount(0L);
        resCalculatorTicketDTO.setProcess_datetime(1L);
        resCalculatorTicketDTO.setScope(0L);
        resCalculatorTicketDTO.setService_plan_id(1L);
        resCalculatorTicketDTO.setStage_id(0L);
        resCalculatorTicketDTO.setVehicle_type(0L);
        resCalculatorTicketDTO.setService_plan_type_id(0L);
        resCalculatorTicketDTO.setPrice_amount(10L);
        resultSelectEntity.setListData(Arrays.asList(resCalculatorTicketDTO));
        resultSelectEntity.setCount("10");


        when(booServiceImplUnderTest.booRepository.getFeeBoo(reqCalculatorTicketDTO, null, null, 1L, 0L, 0L, 0L, false)).thenReturn(resultSelectEntity);

        // Run the test
        final ResCalculatorTicketDTO result = (ResCalculatorTicketDTO) booServiceImplUnderTest.calculatorTicket(authentication, reqCalculatorTicketDTO, false);
        Assertions.assertThat(result).isEqualToComparingFieldByField(resCalculatorTicketDTO);
        // Verify the results
//        Assertions.assertThat(result).isEqualToComparingFieldByField(expected);
    }

    @Test
    void testCalculatorTicketWithCaseC() {
        // Setup
        final Authentication authentication = null;
        final ReqCalculatorTicketDTO reqCalculatorTicketDTO = new ReqCalculatorTicketDTO();
        reqCalculatorTicketDTO.setStation_type("C");
        reqCalculatorTicketDTO.setStation_in_id(5018L);
        reqCalculatorTicketDTO.setStation_out_id(0L);
        reqCalculatorTicketDTO.setTicket_type("ticket_type");
        reqCalculatorTicketDTO.setStart_date("20201111");
        reqCalculatorTicketDTO.setEnd_date("20201130");
        reqCalculatorTicketDTO.setPlate("30N1234");
        reqCalculatorTicketDTO.setEtag("1");
        reqCalculatorTicketDTO.setVehicle_type(0L);
        reqCalculatorTicketDTO.setRegister_vehicle_type("0");

        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean isNullOrEmpty(String test) {
                return false;
            }
        };
        when(booServiceImplUnderTest.vehicleGroupService.getVehicleGroupById("token",
                new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix",
                        "30N12345T", 0L, 0L, 0L, 0.0,
                        0.0, 0.0, 0.0, "chassicNumber",
                        "engineNumber", 0L, 0L, 0L, 0L,
                        "status", "activeStatus", "1", "tid", "rfidSerial",
                        "reservedMemory", "rfidType", 0L, "offerCode",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        0L, 0L, "createUser", "contractNo",
                        "type", "offerExternalId", "vehicleType", 0L,
                        "vehicleColor", "plateColor", "vehicleMark", "plate",
                        0L, "vehicleBrand",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "profileStatus", "approvedUser",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "addfilesUser",
                        new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                        "appendixUsername",
                        Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L,
                                0L, "fileName", "fileSize", "filePath",
                                "createUser", new Date(0L), "approvedUser", new Date(0L),
                                "delUser", new Date(0L), "description", "status",
                                "fileBase64", 0L, "documentTypeName",
                                0, 0, new Date(0L))), 0L, 0,
                        "vehicleGroupCode", "plateTypeCode",
                        "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"))).thenReturn(Arrays.asList("value"));

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                Map<Object, Object> station = new HashMap<>();
                station.put("id", 0L);
                station.put("method_charge_id", 1L);
                station.put("name", "0");
                Map<Object, Object> result = new HashMap<>();
                result.put("data", station.toString());
                System.out.println(result.toString());
                return result.toString();
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            private Map<String, String> checkVehicleCouldChargeTicket(ReqCalculatorTicketDTO requestCalculatorTicket, StageBooDTO.Stage stage, StationBooDTO.Station station, String plateType) {
                Map<String, String> mapException = new HashMap<>();
                mapException.put("PROMOTION_ID", "0");
                mapException.put("EXCEPTION_VEHICLE_TYPE", "0");
                mapException.put("EXCEPTION_VEHICLE_TYPE_PROMOTION_ID", "0");
                mapException.put("EXCEPTION_TICKET_TYPE_PROMOTION_ID", "0");
                return mapException;
            }
        };
        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(exceptionListEntities);


        // Configure BooRepository.getFeeBoo(...).

        final ResCalculatorTicketDTO resCalculatorTicketDTO = new ResCalculatorTicketDTO();
        resCalculatorTicketDTO.setAuto_renew(1L);
        resCalculatorTicketDTO.setCharge_method_id(1L);
        resCalculatorTicketDTO.setOcs_code("0");
        resCalculatorTicketDTO.setPrice_amount(0L);
        resCalculatorTicketDTO.setProcess_datetime(1L);
        resCalculatorTicketDTO.setScope(0L);
        resCalculatorTicketDTO.setService_plan_id(1L);
        resCalculatorTicketDTO.setStage_id(0L);
        resCalculatorTicketDTO.setVehicle_type(0L);
        resCalculatorTicketDTO.setService_plan_type_id(0L);
        resCalculatorTicketDTO.setPrice_amount(10L);

        final ResultSelectEntity result = new ResultSelectEntity();
        result.setListData(Arrays.asList(resCalculatorTicketDTO));
        result.setCount("10");

        when(booServiceImplUnderTest.booRepository.getFeeBoo(reqCalculatorTicketDTO, null, 0L, 1L, 0L, 0L, 0L, false)).thenReturn(result);


        // Run the test
        final ResCalculatorTicketDTO result1 = (ResCalculatorTicketDTO) booServiceImplUnderTest.calculatorTicket(authentication, reqCalculatorTicketDTO, false);
        Assertions.assertThat(result1).isEqualToComparingFieldByField(resCalculatorTicketDTO);
        // Verify the results
//        Assertions.assertThat(result).isEqualToComparingFieldByField(expected);
    }

    @Test
    void testChargeTicket() throws Exception {
        // Setup
        final Authentication authentication = null;
        final ReqChargeTicketDTO reqChargeTicketDTO = new ReqChargeTicketDTO();
        reqChargeTicketDTO.setStation_type("O");
        reqChargeTicketDTO.setStation_in_id(5018L);
        reqChargeTicketDTO.setStation_out_id(0L);
        reqChargeTicketDTO.setTicket_type("ticket_type");
        reqChargeTicketDTO.setStart_date("20201110");
        reqChargeTicketDTO.setEnd_date("20201130");
        reqChargeTicketDTO.setPlate("30N12345T");
        reqChargeTicketDTO.setEtag("1");
        reqChargeTicketDTO.setVehicle_type(1L);
        reqChargeTicketDTO.setRegister_vehicle_type("1");

        final ResCalculatorTicketDTO resCalculatorTicketDTO = new ResCalculatorTicketDTO();
        resCalculatorTicketDTO.setAuto_renew(1L);
        resCalculatorTicketDTO.setCharge_method_id(1L);
        resCalculatorTicketDTO.setOcs_code("0");
        resCalculatorTicketDTO.setPrice_amount(0L);
        resCalculatorTicketDTO.setProcess_datetime(1L);
        resCalculatorTicketDTO.setScope(0L);
        resCalculatorTicketDTO.setService_plan_id(1L);
        resCalculatorTicketDTO.setStage_id(0L);
        resCalculatorTicketDTO.setVehicle_type(0L);
        resCalculatorTicketDTO.setService_plan_type_id(0L);
        resCalculatorTicketDTO.setPrice_amount(10L);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getPlateTypeBOO1(String plate) {
                return "T";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String formatPlateBOO1(String plate) {
                return "30N12345";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                Map<String, String> station = new HashMap<>();
                station.put("name", "0");
                station.put("id", "1");
                station.put("method_charge_id", "1");
                Map stationDTO = new HashMap();
                stationDTO.put("data", station);
                return stationDTO.toString();
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public Object calculatorTicket(Authentication authentication, ReqCalculatorTicketDTO reqCalculatorTicketDTO, boolean isCallFromBoo) {
                return resCalculatorTicketDTO;
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean validateDateChargeTicket(long methodCharge, String ticketType, String startDate, String
                    endDate, String formatDate) {
                return true;
            }
        };
        new MockUp<OCSServiceImpl>() {
            @mockit.Mock
            String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) {
                Map<Object, Object> result = new HashMap<>();
                result.put("resultCode", "0");
                result.put("description", "Success");
                return result.toString();
            }
        };


        final ResChargeTicketDTO expectedResult = new ResChargeTicketDTO(0L, 10L, "1", "1", 0L, "1", null, null, null, "0", "ticket_type", "20201110", "20201130", "30N12345T", 5018L, 0L, null, 1605004211861L);
        when(booServiceImplUnderTest.vehicleGroupService.getVehicleGroupById("token", new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "30N12345T", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "0", "1", "1", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 23L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"))).thenReturn(Arrays.asList("value"));


        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber(null);
        SimpleDateFormat formatEtc = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
        ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setPlateNumber("30N12345T");
        servicePlanDTO.setStationId(5018L);
        servicePlanDTO.setCreateDateFrom("10/11/2020");
        servicePlanDTO.setCreateDateTo("30/11/2020");
        servicePlanDTO.setStageId(0L);
        when(booServiceImplUnderTest.ticketRepository.checkExistsTicket(servicePlanDTO, true)).thenReturn(servicePlanVehicleDuplicateDTO);

        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(0L);
        addVehicleRequestDTO.setPlateType(1L);
        addVehicleRequestDTO.setActTypeId(23L);

        final OCSResponse resultObj = new OCSResponse();
        resultObj.setResultCode("0");
        resultObj.setDescription("Success");
        when(booServiceImplUnderTest.ocsService.createVehicleOCS(addVehicleRequestDTO, authentication, 23L)).thenReturn(resultObj);
        VehicleEntity save = new VehicleEntity();
        save.setVehicleId(1L);
        save.setCustId(Long.parseLong("1"));
        save.setContractId(Long.parseLong("1"));
        save.setPlateNumber("30N12345T");
        save.setVehicleTypeId(1L);
        save.setVehicleGroupId(0L);
        save.setSeatNumber(null);
        save.setCargoWeight(null);
        save.setStatus(VehicleEntity.Status.ACTIVATED.value);
        save.setCreateUser(null);
        save.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        save.setProfileStatus(VehicleEntity.ProfilesStatus.APPROVED.value);
        save.setVehicleId(null);
        save.setEffDate(new java.sql.Date(System.currentTimeMillis()));
        save.setRfidSerial("0");
        save.setEpc("1");
        save.setPlateTypeCode("T");


        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public VehicleEntity saveVehicle(Authentication authentication, ReqChargeTicketDTO reqChargeTicketDTO,
                                             Long vehicleGroupId, String rfidSerial, String plateTypeCode, String plate) {

                return save;
            }
        };
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
//        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(1L);
        saleTransDetailEntity.setServicePlanId(1L);
        saleTransDetailEntity.setSaleTransDate(new Date(System.currentTimeMillis()));
//        saleTransDetailEntity.setServiceFeeId(0L);
//        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
//        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("30N12345T");
        saleTransDetailEntity.setEpc("1");
//        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("0");
        saleTransDetailEntity.setVehicleGroupId(0L);
        saleTransDetailEntity.setServicePlanTypeId(0L);
        saleTransDetailEntity.setOcsCode("0");
        saleTransDetailEntity.setScope("0");
        saleTransDetailEntity.setOfferLevel("2");
        java.util.Date effDate = FnCommon.convertStringToDate("2020-11-10", "yyyy-MM-dd");
        saleTransDetailEntity.setEffDate(new Date(effDate.getTime()));
        java.util.Date expDate = FnCommon.convertStringToDate("2020-11-30", "yyyy-MM-dd");
        saleTransDetailEntity.setExpDate(new Date(expDate.getTime()));
        saleTransDetailEntity.setStationId(5018L);
        saleTransDetailEntity.setStageId(0L);
        saleTransDetailEntity.setPrice(10L);
        saleTransDetailEntity.setQuantity(1L);
        saleTransDetailEntity.setCreateDate(new Date(System.currentTimeMillis()));
        saleTransDetailEntity.setStatus(2L);
        saleTransDetailEntity.setOrgPlateNumber("30N12345T");
        saleTransDetailEntity.setBooCode("BOO2");
        saleTransDetailEntity.setSubscriptionTicketId(0L);
        saleTransDetailEntity.setBooFlow(3L);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.save(saleTransDetailEntity)).thenReturn(new SaleTransDetailEntity());
        SaleTransDetailEntity slt = booServiceImplUnderTest.saleTransDetailRepositoryJPA.save(saleTransDetailEntity);

        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            private ActionAuditEntity writeLogNew(Authentication authentication, long reasonId, long actTypeId, long contractId, long customerId, long id, Object entity, String tableName) {
                return new ActionAuditEntity();
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            private Object addSaleTransDetail(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO,
                                              Authentication authentication, long saleTransId,
                                              String scope, String ocsCode, Long stageId, String orgPlateNumber,
                                              Long subscriptionTicketId, Long reasonId, Long actTypeId) {
                return new Object();
            }
        };
        // Run the test
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            private Long addOfferToOCS(Authentication authentication, ResCalculatorTicketDTO resCalculatorTicket, ReqChargeTicketDTO reqChargeTicket, VehicleEntity vehicleEntity) {
                return 1L;
            }
        };
        final ResChargeTicketDTO result = booServiceImplUnderTest.chargeTicket(authentication, reqChargeTicketDTO);
        verify(booServiceImplUnderTest.saleTransDetailRepositoryJPA, times(1)).save(saleTransDetailEntity);
        // Verify the results
        assertEquals(expectedResult.getStatus(), result.getStatus());
    }

    @Test
    void testChargeTicket_OCSServiceAddSupOfferThrowsException() throws Exception {
        // Setup
        final Authentication authentication = null;
        final ReqChargeTicketDTO reqChargeTicketDTO = new ReqChargeTicketDTO();
        reqChargeTicketDTO.setStation_type("station_type");
        reqChargeTicketDTO.setStation_in_id(0L);
        reqChargeTicketDTO.setStation_out_id(0L);
        reqChargeTicketDTO.setTicket_type("ticket_type");
        reqChargeTicketDTO.setStart_date("start_date");
        reqChargeTicketDTO.setEnd_date("end_date");
        reqChargeTicketDTO.setPlate("plate");
        reqChargeTicketDTO.setEtag("etag");
        reqChargeTicketDTO.setVehicle_type(0L);
        reqChargeTicketDTO.setRegister_vehicle_type("register_vehicle_type");

        when(booServiceImplUnderTest.vehicleGroupService.getVehicleGroupById("token", new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"))).thenReturn(Arrays.asList("value"));

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(exceptionListEntities);

        // Configure BooRepository.getFeeBoo(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(booServiceImplUnderTest.booRepository.getFeeBoo(new ReqCalculatorTicketDTO(), Arrays.asList("value"), 0L, 0L, null, 0L, 0L, false)).thenReturn(resultSelectEntity);

        // Configure TicketRepository.checkExistsTicket(...).
        final ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("plateNumber");
        when(booServiceImplUnderTest.ticketRepository.checkExistsTicket(new ServicePlanDTO(), false)).thenReturn(servicePlanVehicleDuplicateDTO);

        // Configure VehicleRepositoryJPA.findByContractIdAndPlateNumberAndPlateTypeCode(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(booServiceImplUnderTest.vehicleRepositoryJPA.findByContractIdAndPlateNumberAndPlateTypeCode(0L, "plateNumber", "plateTypeCode")).thenReturn(vehicleEntity);

        when(booServiceImplUnderTest.vehicleRepositoryJPA.getNextValSequenceSerial()).thenReturn(0L);

        // Configure OCSService.createVehicleOCS(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.createVehicleOCS(new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"), null, 0L)).thenReturn(ocsResponse);

        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(booServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(0L)).thenReturn(actReasonEntities);

        // Configure VehicleRepositoryJPA.save(...).
        final VehicleEntity vehicleEntity1 = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(booServiceImplUnderTest.vehicleRepositoryJPA.save(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"))).thenReturn(vehicleEntity1);

        // Configure ActionAuditService.updateLogToActionAudit(...).
        final ActionAuditEntity actionAuditEntity = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        when(booServiceImplUnderTest.actionAuditService.updateLogToActionAudit(new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(actionAuditEntity);

        // Configure ActionAuditDetailRepositoryJPA.save(...).
        final ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(0L);
        actionAuditDetailEntity.setActionAuditId(0L);
        actionAuditDetailEntity.setCreateDate(new Date(0L));
        actionAuditDetailEntity.setTableName("tableName");
        actionAuditDetailEntity.setPkId(0L);
        actionAuditDetailEntity.setColunmName("colunmName");
        actionAuditDetailEntity.setOldValue("oldValue");
        actionAuditDetailEntity.setNewValue("newValue");
        when(booServiceImplUnderTest.actionAuditDetailRepositoryJPA.save(new ActionAuditDetailEntity())).thenReturn(actionAuditDetailEntity);

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
        when(booServiceImplUnderTest.saleTransRepositoryJPA.save(new SaleTransEntity())).thenReturn(saleTransEntity);

        when(booServiceImplUnderTest.ocsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenThrow(Exception.class);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
//        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(1L);
        saleTransDetailEntity.setSaleTransDate(new Date(System.currentTimeMillis()));
//        saleTransDetailEntity.setServiceFeeId(0L);
//        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
//        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("30N12345T");
        saleTransDetailEntity.setEpc("1");
//        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("0");
        saleTransDetailEntity.setVehicleGroupId(0L);
        saleTransDetailEntity.setServicePlanTypeId(0L);
        saleTransDetailEntity.setOcsCode("0");
        saleTransDetailEntity.setScope("0");
        saleTransDetailEntity.setOfferLevel("2");
        java.util.Date effDate = FnCommon.convertStringToDate("2020-11-10", "yyyy-MM-dd");
        saleTransDetailEntity.setEffDate(new Date(effDate.getTime()));
        java.util.Date expDate = FnCommon.convertStringToDate("2020-11-30", "yyyy-MM-dd");
        saleTransDetailEntity.setExpDate(new Date(expDate.getTime()));
        saleTransDetailEntity.setStationId(5018L);
        saleTransDetailEntity.setStageId(0L);
        saleTransDetailEntity.setPrice(10L);
        saleTransDetailEntity.setQuantity(1L);
        saleTransDetailEntity.setCreateDate(new Date(System.currentTimeMillis()));
        saleTransDetailEntity.setStatus(2L);
        saleTransDetailEntity.setOrgPlateNumber("30N12345T");
        saleTransDetailEntity.setBooCode("BOO2");
        saleTransDetailEntity.setSubscriptionTicketId(0L);
        saleTransDetailEntity.setBooFlow(3L);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.save(saleTransDetailEntity)).thenReturn(saleTransDetailEntity);

        // Run the test
        assertThrows(Exception.class, () -> booServiceImplUnderTest.chargeTicket(authentication, reqChargeTicketDTO));
    }

    @Test
    void testCancelTicket() {
        // Setup
        final Authentication authentication = null;
        final ReqCancelTicketDTO reqCancelTicketDTO = new ReqCancelTicketDTO();
        reqCancelTicketDTO.setSubscription_ticket_id(0L);
        reqCancelTicketDTO.setStation_type("station_type");
        reqCancelTicketDTO.setStation_in_id(0L);
        reqCancelTicketDTO.setStation_out_id(0L);
        reqCancelTicketDTO.setTicket_type("ticket_type");
        reqCancelTicketDTO.setStart_date("start_date");
        reqCancelTicketDTO.setEnd_date("end_date");
        reqCancelTicketDTO.setPlate("plate");
        reqCancelTicketDTO.setEtag("etag");
        reqCancelTicketDTO.setRequest_id(0L);
        reqCancelTicketDTO.setRequest_type(0L);


        final ResCancelTicketDTO expectedResult = new ResCancelTicketDTO();
        expectedResult.setRequest_id(0L);
        expectedResult.setSubscription_ticket_id(0L);
        expectedResult.setProcess_datetime(0L);
        expectedResult.setResponse_datetime(0L);
        expectedResult.setStatus("REJECT");
        expectedResult.setRequest_type(0L);

        // Configure SaleTransDetailRepositoryJPA.findBySubscriptionTicketId(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("plateNumber");
        saleTransDetailEntity.setOcsCode("0");
        saleTransDetailEntity.setEpc("1");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");
        saleTransDetailEntity.setEffDate(new Date(0L));
        saleTransDetailEntity.setExpDate(new Date(System.currentTimeMillis() + 100000));
        saleTransDetailEntity.setOfferLevel("0");
        saleTransDetailEntity.setStationId(0L);

        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findBySubscriptionTicketId(0L)).thenReturn(saleTransDetailEntity);
        // Configure SaleTransDelBoo1RepositoryJPA.findById(...).
        final SaleTransDelBoo1Entity saleTransDelBoo1Entity1 = new SaleTransDelBoo1Entity();
        saleTransDelBoo1Entity1.setSaleTransDelBoo1Id(0L);
        saleTransDelBoo1Entity1.setSubscriptionTicketId(0L);
        saleTransDelBoo1Entity1.setStationType(0L);
        saleTransDelBoo1Entity1.setStationInId(0L);
        saleTransDelBoo1Entity1.setStationOutId(0L);
        saleTransDelBoo1Entity1.setStageId(0L);
        saleTransDelBoo1Entity1.setServicePlanTypeId(0L);
        saleTransDelBoo1Entity1.setEffDate(new Date(0L));
        saleTransDelBoo1Entity1.setExpDate(new Date(2L));
        saleTransDelBoo1Entity1.setPlateNumber("plateNumber");

        final Optional<SaleTransDelBoo1Entity> saleTransDelBoo1Entity = Optional.of(saleTransDelBoo1Entity1);
        when(booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA.findById(0L)).thenReturn(saleTransDelBoo1Entity);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("Ok");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.deleteSupOffer(authentication, 2L, 1L, "1", "0", "0")).thenReturn(ocsResponse);


        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(booServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(0L)).thenReturn(actReasonEntities);

        // Configure SaleTransDetailRepositoryJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity1 = new SaleTransDetailEntity();
        saleTransDetailEntity1.setSaleTransDetailId(0L);
        saleTransDetailEntity1.setSaleTransId(0L);
        saleTransDetailEntity1.setSaleTransDate(new Date(0L));
        saleTransDetailEntity1.setServiceFeeId(0L);
        saleTransDetailEntity1.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity1.setVehicleId(0L);
        saleTransDetailEntity1.setPlateNumber("plateNumber");
        saleTransDetailEntity1.setEpc("epc");
        saleTransDetailEntity1.setTid("tid");
        saleTransDetailEntity1.setRfidSerial("rfidSerial");
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity1);

        // Configure SaleTransDelBoo1RepositoryJPA.save(...).
        final SaleTransDelBoo1Entity saleTransDelBoo1Entity2 = new SaleTransDelBoo1Entity();
        saleTransDelBoo1Entity2.setSaleTransDelBoo1Id(0L);
        saleTransDelBoo1Entity2.setSubscriptionTicketId(0L);
        saleTransDelBoo1Entity2.setStationType(0L);
        saleTransDelBoo1Entity2.setStationInId(0L);
        saleTransDelBoo1Entity2.setStationOutId(0L);
        saleTransDelBoo1Entity2.setStageId(0L);
        saleTransDelBoo1Entity2.setServicePlanTypeId(0L);
        saleTransDelBoo1Entity2.setEffDate(new Date(0L));
        saleTransDelBoo1Entity2.setExpDate(new Date(0L));
        saleTransDelBoo1Entity2.setPlateNumber("plateNumber");

        when(booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA.save(new SaleTransDelBoo1Entity())).thenReturn(saleTransDelBoo1Entity2);
        when(booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA.findById(0L)).thenReturn(Optional.empty());
        final VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setContractId(1L);
        when(booServiceImplUnderTest.vehicleRepositoryJPA.findByVehicleId(saleTransDetailEntity.getVehicleId())).thenReturn(vehicleEntity);
        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = new TransactionsHistoryVehicleDTO();
        TransactionsHistoryVehicleDTO.TransactionsHistoryList data = new TransactionsHistoryVehicleDTO.TransactionsHistoryList();
        transactionsHistoryVehicleDTO.setData(data);
        List<TransactionsHistoryVehicleDTO.TransactionsHistory> listData = new ArrayList<>();
        data.setListData(listData);
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
             TransactionsHistoryVehicleDTO getTransactionsVehicle(Authentication authentication, String plate, String ticketType,
                                                                         VehicleEntity vehicleEntity, SaleTransDetailEntity saleTransDetailEntity, java.util.Date effDate, java.util.Date expDate){
                return transactionsHistoryVehicleDTO;
            };
        };
        // Run the test
        final ResCancelTicketDTO result = booServiceImplUnderTest.cancelTicket(authentication, reqCancelTicketDTO);

        // Verify the results
        assertEquals(expectedResult.getStatus(), result.getStatus());
    }

    @Test
    void testOnlineEventReg() {
        // Setup
        final ReqOnlineEventRegDTO reqOnlineEventRegDTO = new ReqOnlineEventRegDTO();
        reqOnlineEventRegDTO.setPlate("30N13579T");
        reqOnlineEventRegDTO.setEtag("etag");
        reqOnlineEventRegDTO.setRequest_id(0L);
        reqOnlineEventRegDTO.setRequest_datetime(0L);
        reqOnlineEventRegDTO.setStation_in_id(0L);
        reqOnlineEventRegDTO.setStation_out_id(0L);
        reqOnlineEventRegDTO.setType("WL");
        reqOnlineEventRegDTO.setVehicle_type("register_vehicle_type");
        reqOnlineEventRegDTO.setException_vehicle_type("exception_vehicle_type");
        reqOnlineEventRegDTO.setPrice_turn(0L);
        reqOnlineEventRegDTO.setAction_type("I");
        reqOnlineEventRegDTO.setStart_date("20201110 00:00:00");
        reqOnlineEventRegDTO.setEnd_date("21001230 00:00:00");
        new MockUp<FnCommon>() {
            @mockit.Mock
            Date convertStringToDate(String strDate, String format) throws ParseException {
                SimpleDateFormat sdFormat = new SimpleDateFormat(format);
                sdFormat.setLenient(false);
                java.util.Date date = sdFormat.parse(strDate);
                return new Date(date.getTime());
            }
        };


        final Authentication authentication = null;
        final ResOnlineEventDTO expectedResult = new ResOnlineEventDTO(0L, 0L, 0L, "0");

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpcAndOrgPlateNumber(...).
        final List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findByPlateNumberAndEpcAndOrgPlateNumber(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(exceptionListEntities);

        // Configure BooRepository.findByPlateNumberAndEpc(...).
        final DataBooDTO dataBooDTO = new DataBooDTO();
        dataBooDTO.setVehicleId(0L);
        dataBooDTO.setVehicleType("vehicleType");
        dataBooDTO.setVehicleTypeId(0L);
        dataBooDTO.setCustId(0L);
        dataBooDTO.setCustName("custName");
        dataBooDTO.setContractId(0L);
        dataBooDTO.setContractNo("contractNo");
        dataBooDTO.setPlateType("1");
        dataBooDTO.setTId("tId");
        dataBooDTO.setRfIdSerial("rfIdSerial");
        when(booServiceImplUnderTest.booRepository.findByPlateNumberAndEpc(Mockito.anyString(), Mockito.anyString())).thenReturn(dataBooDTO);

        // Configure ExceptionListServiceJPA.save(...).
        final ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
        when(booServiceImplUnderTest.exceptionListServiceJPA.save(Mockito.any())).thenReturn(exceptionListEntity);

        // Configure ExceptionListServiceJPA.saveAll(...).
        final List<ExceptionListEntity> exceptionListEntities1 = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.saveAll(Mockito.anyList())).thenReturn(exceptionListEntities1);
        LinkedTreeMap stations = new LinkedTreeMap();
        stations.put("booCode", "BOO1");
        when(booServiceImplUnderTest.stationService.findById(Mockito.anyString(), Mockito.anyLong())).thenReturn(stations);
        // Run the test
        final ResOnlineEventDTO result = booServiceImplUnderTest.onlineEventReg(reqOnlineEventRegDTO, null);

        // Verify the results
        assertEquals(expectedResult.getResponse_code(), result.getResponse_code());
    }

    @Test
    void testOnlineEventSyncDBS() throws Exception {
        // Setup
        final ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
        reqOnlineEventSyncDTO.setPlate_old("30N12345T");
        reqOnlineEventSyncDTO.setTicket_type_old("ticket_type_old");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        reqOnlineEventSyncDTO.setSeat_old(0L);
        reqOnlineEventSyncDTO.setWeight_goods_old(0.0);
        reqOnlineEventSyncDTO.setWeight_all_old(0.0);
        reqOnlineEventSyncDTO.setEtag_old("1");
        reqOnlineEventSyncDTO.setPlate_new("30N12345X");
        reqOnlineEventSyncDTO.setEtag_new("2");
        reqOnlineEventSyncDTO.setTicket_type_new("ticket_type_new");
        reqOnlineEventSyncDTO.setReason("DBS");
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean validatePlateContainsTVX(String plate) {
                return true;
            }
        };


        final Authentication authentication = null;
        final ResOnlineEventDTO expectedResult = new ResOnlineEventDTO(0L, 0L, 0L, "response_code");

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntity = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(exceptionListEntity);

        // Configure SaleTransDetailServiceJPA.findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("30N12345T");
        saleTransDetailEntity.setEpc("1");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");

        final List<SaleTransDetailEntity> saleTransDetailEntities = Arrays.asList(saleTransDetailEntity);
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.
                findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual("30N12345T",
                        "1", 0L, new Date(0L), new Date(0L))).thenReturn(saleTransDetailEntities);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.
                findOrgPlateTypeNumberAndEpcAndBooFlow("30N12345T",
                        "1", 3L)).thenReturn(saleTransDetailEntities);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(0L);
        when(booServiceImplUnderTest.vehicleRepositoryJPA.findById(0L)).thenReturn(Optional.of(vehicleEntity));

        when(booServiceImplUnderTest.saleTransDetailServiceJPA.saveAll(saleTransDetailEntities)).thenReturn(saleTransDetailEntities);

        // Run the test
        final ResOnlineEventDTO result = booServiceImplUnderTest.onlineEventSync(reqOnlineEventSyncDTO, authentication);

        // Verify the results
        assertEquals(result.getResponse_code(), "0");
    }
    @Test
    void testOnlineEventSyncOpenAndCloseEpc() throws Exception {
        // Setup
        final ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
        reqOnlineEventSyncDTO.setPlate_old("30N12345T");
        reqOnlineEventSyncDTO.setTicket_type_old("ticket_type_old");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        reqOnlineEventSyncDTO.setSeat_old(0L);
        reqOnlineEventSyncDTO.setWeight_goods_old(0.0);
        reqOnlineEventSyncDTO.setWeight_all_old(0.0);
        reqOnlineEventSyncDTO.setEtag_old("1");
        reqOnlineEventSyncDTO.setPlate_new("30N12345X");
        reqOnlineEventSyncDTO.setEtag_new("2");
        reqOnlineEventSyncDTO.setTicket_type_new("ticket_type_new");
        reqOnlineEventSyncDTO.setReason("DME");
        reqOnlineEventSyncDTO.setEtag_status_new("1");
        reqOnlineEventSyncDTO.setEtag_status_old("2");
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean validatePlateContainsTVX(String plate) {
                return true;
            }
        };
        final Authentication authentication = null;
        final ResOnlineEventDTO expectedResult = new ResOnlineEventDTO(0L, 0L, 0L, "response_code");

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntity = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("30N12345", "1", 0L)).thenReturn(exceptionListEntity);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String formatPlateBOO1(String plate) {
                return "30N12345";
            }
        };

        // Configure SaleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus(...).
        final SaleTransDetailEntity saleTransDetailEntity2 = new SaleTransDetailEntity();
        saleTransDetailEntity2.setSaleTransDetailId(0L);
        saleTransDetailEntity2.setSaleTransId(0L);
        saleTransDetailEntity2.setSaleTransDate(new Date(0L));
        saleTransDetailEntity2.setServiceFeeId(0L);
        saleTransDetailEntity2.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity2.setVehicleId(0L);
        saleTransDetailEntity2.setPlateNumber("30N12345T");
        saleTransDetailEntity2.setEpc("1");
        saleTransDetailEntity2.setTid("tid");
        saleTransDetailEntity2.setRfidSerial("rfidSerial");

        final List<SaleTransDetailEntity> saleTransDetailEntities2 = Arrays.asList(saleTransDetailEntity2);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus("30N12345T", "1", 0L)).thenReturn(saleTransDetailEntities2);
        final Optional<VehicleEntity> vehicleEntity2 = Optional.of(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "30N12345T", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"));
        when(booServiceImplUnderTest.vehicleServiceJPA.findByPlateNumberAndActiveStatusNot("30N12345T", "2")).thenReturn(vehicleEntity2);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(0L);
        when(booServiceImplUnderTest.vehicleRepositoryJPA.findById(0L)).thenReturn(Optional.of(vehicleEntity));
        when(booServiceImplUnderTest.vehicleServiceJPA.findByPlateNumberAndActiveStatusNot("30N12345", "2")).thenReturn(vehicleEntity2);
        // Run the test
        final ResOnlineEventDTO result = booServiceImplUnderTest.onlineEventSync(reqOnlineEventSyncDTO, authentication);

        // Verify the results
        assertEquals(result.getResponse_code(), "0");
    }

    @Test
    void testOnlineEventSyncChangeOtherInfo() throws Exception {
        // Setup
        final ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
        reqOnlineEventSyncDTO.setPlate_old("30N12345T");
        reqOnlineEventSyncDTO.setTicket_type_old("ticket_type_old");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        reqOnlineEventSyncDTO.setSeat_old(0L);
        reqOnlineEventSyncDTO.setWeight_goods_old(0.0);
        reqOnlineEventSyncDTO.setWeight_all_old(0.0);
        reqOnlineEventSyncDTO.setEtag_old("1");
        reqOnlineEventSyncDTO.setPlate_new("30N12345X");
        reqOnlineEventSyncDTO.setEtag_new("2");
        reqOnlineEventSyncDTO.setTicket_type_new("ticket_type_new");
        reqOnlineEventSyncDTO.setReason("TKH");
        reqOnlineEventSyncDTO.setEtag_status_new("1");
        reqOnlineEventSyncDTO.setEtag_status_old("2");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_new("1");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("0");
        reqOnlineEventSyncDTO.setSeat_new(1L);
        reqOnlineEventSyncDTO.setSeat_old(2L);
        reqOnlineEventSyncDTO.setWeight_all_new(10.0);
        reqOnlineEventSyncDTO.setWeight_all_old(20.0);
        reqOnlineEventSyncDTO.setWeight_goods_new(5.0);
        reqOnlineEventSyncDTO.setWeight_goods_old(10.0);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean validatePlateContainsTVX(String plate) {
                return true;
            }
        };
        final Authentication authentication = null;
        final ResOnlineEventDTO expectedResult = new ResOnlineEventDTO(0L, 0L, 0L, "response_code");

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntity = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("30N12345", "1", 0L)).thenReturn(exceptionListEntity);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String formatPlateBOO1(String plate) {
                return "30N12345";
            }
        };

        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public boolean handleCallOcsWhenChangeInfoVehicle(VehicleEntity vehicleEntity, List<String> vehicleGroupIds,
                                                              ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO,
                                                              Authentication authentication, List<SaleTransDetailEntity> saleTransDetailEntities) {
                return true;
            }
        };

        // Configure SaleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus(...).
        final SaleTransDetailEntity saleTransDetailEntity2 = new SaleTransDetailEntity();
        saleTransDetailEntity2.setSaleTransDetailId(0L);
        saleTransDetailEntity2.setSaleTransId(0L);
        saleTransDetailEntity2.setSaleTransDate(new Date(0L));
        saleTransDetailEntity2.setServiceFeeId(0L);
        saleTransDetailEntity2.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity2.setVehicleId(0L);
        saleTransDetailEntity2.setPlateNumber("30N12345T");
        saleTransDetailEntity2.setEpc("1");
        saleTransDetailEntity2.setTid("tid");
        saleTransDetailEntity2.setRfidSerial("rfidSerial");

        final List<SaleTransDetailEntity> saleTransDetailEntities2 = Arrays.asList(saleTransDetailEntity2);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus("30N12345T", "1", 0L)).thenReturn(saleTransDetailEntities2);
        final Optional<VehicleEntity> vehicleEntity2 = Optional.of(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "30N12345T", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"));
        when(booServiceImplUnderTest.vehicleServiceJPA.findByPlateNumberAndActiveStatusNot("30N12345", "2")).thenReturn(vehicleEntity2);
        LinkedHashMap data = new LinkedHashMap<>();
        data.put("id", "1");
        data.put("mappingType", "1");
        final VehicleEntity vehicleEntityOld = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "30N12345T", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(booServiceImplUnderTest.vehicleTypeService.findById("toandm", "0")).thenReturn(data);
        when(booServiceImplUnderTest.vehicleServiceJPA.findByEpcAndPlateNumberAndActiveStatusAndStatusAndContractId(
                "1", "30N12345", "1", "1", 1L
        )).thenReturn(Arrays.asList(vehicleEntityOld));
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public List<String> findVehicleGroup(String token, AddVehicleRequestDTO addVehicleRequestDTO,
                                                 ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO) {
                return Arrays.asList("1");
            }
        };

        // Run the test
        final ResOnlineEventDTO result = booServiceImplUnderTest.onlineEventSync(reqOnlineEventSyncDTO, authentication);

        // Verify the results
        assertEquals(result.getResponse_code(), "0");
    }

    @Ignore
    void testOnlineEventSyncChangeInfoRegister() throws Exception {
        // Setup
        final ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
        reqOnlineEventSyncDTO.setPlate_old("30N12345T");
        reqOnlineEventSyncDTO.setTicket_type_old("ticket_type_old");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        reqOnlineEventSyncDTO.setSeat_old(0L);
        reqOnlineEventSyncDTO.setWeight_goods_old(0.0);
        reqOnlineEventSyncDTO.setWeight_all_old(0.0);
        reqOnlineEventSyncDTO.setEtag_old("1");
        reqOnlineEventSyncDTO.setPlate_new("30N12345X");
        reqOnlineEventSyncDTO.setEtag_new("2");
        reqOnlineEventSyncDTO.setTicket_type_new("ticket_type_new");
        reqOnlineEventSyncDTO.setReason("TDK");
        reqOnlineEventSyncDTO.setEtag_status_new("1");
        reqOnlineEventSyncDTO.setEtag_status_old("2");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_new("1");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("0");
        reqOnlineEventSyncDTO.setSeat_new(1L);
        reqOnlineEventSyncDTO.setSeat_old(2L);
        reqOnlineEventSyncDTO.setWeight_all_new(10.0);
        reqOnlineEventSyncDTO.setWeight_all_old(20.0);
        reqOnlineEventSyncDTO.setWeight_goods_new(5.0);
        reqOnlineEventSyncDTO.setWeight_goods_old(10.0);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public boolean validatePlateContainsTVX(String plate) {
                return true;
            }
        };
        final Authentication authentication = null;
        final ResOnlineEventDTO expectedResult = new ResOnlineEventDTO(0L, 0L, 0L, "response_code");

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntity = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("30N12345", "1", 0L)).thenReturn(exceptionListEntity);

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String formatPlateBOO1(String plate) {
                return "30N12345";
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public List<String> findVehicleGroup(String token, AddVehicleRequestDTO addVehicleRequestDTO,
                                                 ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO, String mappingType) {
                return Arrays.asList("0");
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public List<String> findVehicleGroup(String token, AddVehicleRequestDTO addVehicleRequestDTO,
                                                 ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO, String mappingType) {
                return Arrays.asList("0");
            }
        };
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            public boolean handleCallOcsWhenChangeInfoVehicle(VehicleEntity vehicleEntity, List<String> vehicleGroupIds,
                                                              ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO,
                                                              Authentication authentication) {
                return true;
            }
        };

        // Configure SaleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransDetailId(0L);
        saleTransDetailEntity.setSaleTransId(0L);
        saleTransDetailEntity.setSaleTransDate(new Date(0L));
        saleTransDetailEntity.setServiceFeeId(0L);
        saleTransDetailEntity.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity.setVehicleId(0L);
        saleTransDetailEntity.setPlateNumber("30N12345T");
        saleTransDetailEntity.setEpc("1");
        saleTransDetailEntity.setTid("tid");
        saleTransDetailEntity.setRfidSerial("rfidSerial");

        final List<SaleTransDetailEntity> saleTransDetailEntities = Arrays.asList(saleTransDetailEntity);
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.
                findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual("30N12345",
                        "1", 0L, new Date(0L), new Date(0L))).thenReturn(saleTransDetailEntities);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus("30N12345", "1", 0L)).thenReturn(saleTransDetailEntities);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus("30N12345T", "1", 0L)).thenReturn(saleTransDetailEntities);
        SaleTransEntity saleTransEntityToOptional = new SaleTransEntity();
        saleTransEntityToOptional.setContractId(0L);
        Optional<SaleTransEntity> saleTransEntity = Optional.of(saleTransEntityToOptional);
        when(booServiceImplUnderTest.saleTransRepositoryJPA.findById(0L)).thenReturn(saleTransEntity);
        final Optional<VehicleEntity> vehicleEntity2 = Optional.of(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "30N12345T", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"));
        when(booServiceImplUnderTest.vehicleServiceJPA.findByPlateNumberAndActiveStatusNot("30N12345T", "2")).thenReturn(vehicleEntity2);
        LinkedHashMap data = new LinkedHashMap<>();
        data.put("id", "1");
        data.put("mappingType", "1");
        when(booServiceImplUnderTest.vehicleTypeService.findById("toandm", "0")).thenReturn(data);


        // Run the test
        final ResOnlineEventDTO result = booServiceImplUnderTest.onlineEventSync(reqOnlineEventSyncDTO, authentication);

        // Verify the results
        assertEquals(result.getResponse_code(), "0");
    }

    @Test
    void testOnlineEventSync_OCSServiceAddSupOfferThrowsException() throws Exception {
        // Setup
        final ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
        reqOnlineEventSyncDTO.setPlate_old("plate_old");
        reqOnlineEventSyncDTO.setTicket_type_old("ticket_type_old");
        reqOnlineEventSyncDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        reqOnlineEventSyncDTO.setSeat_old(0L);
        reqOnlineEventSyncDTO.setWeight_goods_old(0.0);
        reqOnlineEventSyncDTO.setWeight_all_old(0.0);
        reqOnlineEventSyncDTO.setEtag_old("etag_old");
        reqOnlineEventSyncDTO.setPlate_new("plate_new");
        reqOnlineEventSyncDTO.setEtag_new("etag_new");
        reqOnlineEventSyncDTO.setTicket_type_new("ticket_type_new");

        final Authentication authentication = null;

        // Configure ExceptionListServiceJPA.findByPlateNumberAndEpc(...).
        final List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        when(booServiceImplUnderTest.exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(exceptionListEntities);

        // Configure SaleTransDetailServiceJPA.findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual(...).
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
        final List<SaleTransDetailEntity> saleTransDetailEntities = Arrays.asList(saleTransDetailEntity);
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual("plateNumber", "epc", 0L, new Date(0L), new Date(0L))).thenReturn(saleTransDetailEntities);

        // Configure SaleTransDetailServiceJPA.saveAll(...).
        final SaleTransDetailEntity saleTransDetailEntity1 = new SaleTransDetailEntity();
        saleTransDetailEntity1.setSaleTransDetailId(0L);
        saleTransDetailEntity1.setSaleTransId(0L);
        saleTransDetailEntity1.setSaleTransDate(new Date(0L));
        saleTransDetailEntity1.setServiceFeeId(0L);
        saleTransDetailEntity1.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity1.setVehicleId(0L);
        saleTransDetailEntity1.setPlateNumber("plateNumber");
        saleTransDetailEntity1.setEpc("epc");
        saleTransDetailEntity1.setTid("tid");
        saleTransDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleTransDetailEntity> saleTransDetailEntities1 = Arrays.asList(saleTransDetailEntity1);
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.saveAll(Arrays.asList(new SaleTransDetailEntity()))).thenReturn(saleTransDetailEntities1);

        // Configure OCSService.modifyVehicleOCS(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
//        when(booServiceImplUnderTest.ocsService.modifyVehicleOCS(new UpdateVehicleRequestOCSDTO(0L, 0L, "epc", "status", 0L, "plateNumber", "effDate",""), null, false)).thenReturn(ocsResponse);

        // Configure VehicleServiceJPA.findByPlateNumber(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        // Configure ContractServiceJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(booServiceImplUnderTest.contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse1);

        // Configure OCSService.deleteVehicle(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.deleteVehicle("RFID", "contractId", null, 0)).thenReturn(ocsResponse2);

        // Configure VehicleServiceJPA.save(...).
        final VehicleEntity vehicleEntity1 = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(booServiceImplUnderTest.vehicleServiceJPA.save(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"))).thenReturn(vehicleEntity1);

        // Configure OCSService.createVehicleOCS(...).
        final OCSResponse ocsResponse3 = new OCSResponse();
        ocsResponse3.resultCode("resultCode");
        ocsResponse3.setResultCode("resultCode");
        ocsResponse3.description("description");
        ocsResponse3.setDescription("description");
        ocsResponse3.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.createVehicleOCS(new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"), null, 0L)).thenReturn(ocsResponse3);

        // Configure SaleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus(...).
        final SaleTransDetailEntity saleTransDetailEntity2 = new SaleTransDetailEntity();
        saleTransDetailEntity2.setSaleTransDetailId(0L);
        saleTransDetailEntity2.setSaleTransId(0L);
        saleTransDetailEntity2.setSaleTransDate(new Date(0L));
        saleTransDetailEntity2.setServiceFeeId(0L);
        saleTransDetailEntity2.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity2.setVehicleId(0L);
        saleTransDetailEntity2.setPlateNumber("plateNumber");
        saleTransDetailEntity2.setEpc("epc");
        saleTransDetailEntity2.setTid("tid");
        saleTransDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleTransDetailEntity> saleTransDetailEntities2 = Arrays.asList(saleTransDetailEntity2);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(saleTransDetailEntities2);

        // Configure SaleTransRepositoryJPA.findById(...).
        final SaleTransEntity saleTransEntity1 = new SaleTransEntity();
        saleTransEntity1.setSaleTransId(0L);
        saleTransEntity1.setSaleTransCode("saleTransCode");
        saleTransEntity1.setSaleTransDate(new Date(0L));
        saleTransEntity1.setSaleTransType("saleTransType");
        saleTransEntity1.setStatus("status");
        saleTransEntity1.setInvoiceUsed("invoiceUsed");
        saleTransEntity1.setInvoiceCreateDate(new Date(0L));
        saleTransEntity1.setShopId(0L);
        saleTransEntity1.setShopName("shopName");
        saleTransEntity1.setStaffId(0L);
        final Optional<SaleTransEntity> saleTransEntity = Optional.of(saleTransEntity1);
        when(booServiceImplUnderTest.saleTransRepositoryJPA.findById(0L)).thenReturn(saleTransEntity);

        when(booServiceImplUnderTest.ocsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenThrow(Exception.class);

        // Configure VehicleServiceJPA.findByPlateNumberAndActiveStatusNot(...).
        final Optional<VehicleEntity> vehicleEntity2 = Optional.of(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"));
        when(booServiceImplUnderTest.vehicleServiceJPA.findByPlateNumberAndActiveStatusNot("plateNumber", "activeStatus")).thenReturn(vehicleEntity2);

        doReturn(new LinkedHashMap<>()).when(booServiceImplUnderTest.vehicleTypeService).findById("token", "id");
        when(booServiceImplUnderTest.vehicleGroupService.getVehicleGroupById("token", new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"))).thenReturn(Arrays.asList("value"));

        // Run the test
        assertThrows(Exception.class, () -> booServiceImplUnderTest.onlineEventSync(reqOnlineEventSyncDTO, authentication));
    }

    @Test
    void testCheckActivation() {
        // Setup
        final Authentication authentication = null;
        final ReqActivationCheckDTO reqActivationCheckDTO = new ReqActivationCheckDTO();
        reqActivationCheckDTO.setPlate("30N12345T");
        reqActivationCheckDTO.setRequest_id(0L);
        reqActivationCheckDTO.setRequest_datetime(0L);

        final ResActivationCheckDTO expectedResult = new ResActivationCheckDTO();
        expectedResult.setRequest_id(0L);
        expectedResult.setResponse_code("0");
        expectedResult.setProcess_datetime(0L);
        expectedResult.setResponse_datetime(0L);
        expectedResult.setEtag("1");
        expectedResult.setPlate("30N12345T");
        expectedResult.setStatus("NAN");

        // Configure VehicleRepositoryJPA.findAllByPlateNumber(...).
        final List<VehicleEntity> vehicleEntities = Arrays.asList(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "30N12345", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "1", "0", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"));
        when(booServiceImplUnderTest.vehicleRepositoryJPA.findAllByPlateNumber("30N12345")).thenReturn(vehicleEntities);

        // Run the test
        final ResActivationCheckDTO result = booServiceImplUnderTest.checkActivation(authentication, reqActivationCheckDTO);

        // Verify the results
        assertEquals(expectedResult.getResponse_code(), result.getResponse_code());
    }

    @Test
    void testValidateServicePlan() {
        // Setup
        final ReqOnlineEventRegDTO requestOnlineEventRegBooDTO = new ReqOnlineEventRegDTO();
        requestOnlineEventRegBooDTO.setPlate("plate");
        requestOnlineEventRegBooDTO.setEtag("etag");
        requestOnlineEventRegBooDTO.setRequest_id(0L);
        requestOnlineEventRegBooDTO.setRequest_datetime(0L);
        requestOnlineEventRegBooDTO.setStation_in_id(0L);
        requestOnlineEventRegBooDTO.setStation_out_id(0L);
        requestOnlineEventRegBooDTO.setType("type");
        requestOnlineEventRegBooDTO.setVehicle_type("register_vehicle_type");
        requestOnlineEventRegBooDTO.setException_vehicle_type("exception_vehicle_type");
        requestOnlineEventRegBooDTO.setPrice_turn(0L);
        requestOnlineEventRegBooDTO.setPrice_monthly(0L);
        requestOnlineEventRegBooDTO.setPrice_quarterly(0L);

        // Run the test
        booServiceImplUnderTest.validateServicePlan(requestOnlineEventRegBooDTO);

        // Verify the results
    }

    @Test
    void testValidateVehicleType() {
        // Setup
        final ReqOnlineEventRegDTO requestOnlineEventRegBooDTO = new ReqOnlineEventRegDTO();
        requestOnlineEventRegBooDTO.setPlate("plate");
        requestOnlineEventRegBooDTO.setEtag("etag");
        requestOnlineEventRegBooDTO.setRequest_id(0L);
        requestOnlineEventRegBooDTO.setRequest_datetime(0L);
        requestOnlineEventRegBooDTO.setStation_in_id(0L);
        requestOnlineEventRegBooDTO.setStation_out_id(0L);
        requestOnlineEventRegBooDTO.setType("type");
        requestOnlineEventRegBooDTO.setVehicle_type("register_vehicle_type");
        requestOnlineEventRegBooDTO.setException_vehicle_type("exception_vehicle_type");
        requestOnlineEventRegBooDTO.setPrice_turn(0L);

        // Run the test
        booServiceImplUnderTest.validateVehicleType(requestOnlineEventRegBooDTO);

        // Verify the results
    }

    @Test
    void testChangeEpc_OCSServiceAddSupOfferThrowsException() throws Exception {
        // Setup
        final ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO = new ReqOnlineEventSyncDTO();
        requestOnlineEventSyncBooDTO.setPlate_old("plate_old");
        requestOnlineEventSyncBooDTO.setTicket_type_old("ticket_type_old");
        requestOnlineEventSyncBooDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        requestOnlineEventSyncBooDTO.setSeat_old(0L);
        requestOnlineEventSyncBooDTO.setWeight_goods_old(0.0);
        requestOnlineEventSyncBooDTO.setWeight_all_old(0.0);
        requestOnlineEventSyncBooDTO.setEtag_old("etag_old");
        requestOnlineEventSyncBooDTO.setPlate_new("plate_new");
        requestOnlineEventSyncBooDTO.setEtag_new("etag_new");
        requestOnlineEventSyncBooDTO.setTicket_type_new("ticket_type_new");

        final Authentication authentication = null;

        // Configure SaleTransDetailServiceJPA.findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual(...).
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
        final List<SaleTransDetailEntity> saleTransDetailEntities = Arrays.asList(saleTransDetailEntity);
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.findByPlateNumberAndEpcAndStatusNotAndEffDateLessThanEqualAndExpDateGreaterThanEqual("plateNumber", "epc", 0L, new Date(0L), new Date(0L))).thenReturn(saleTransDetailEntities);

        // Configure VehicleServiceJPA.findByPlateNumber(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        // Configure ContractServiceJPA.findById(...).
        final Optional<ContractEntity> contractEntity = Optional.of(new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(booServiceImplUnderTest.contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.deleteSupOffer(null, 0L, 0L, "epc", "offerId", "offerLevel")).thenReturn(ocsResponse);

        // Configure OCSService.deleteVehicle(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.deleteVehicle("RFID", "contractId", null, 0)).thenReturn(ocsResponse1);

        // Configure VehicleServiceJPA.save(...).
        final VehicleEntity vehicleEntity1 = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(booServiceImplUnderTest.vehicleServiceJPA.save(new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId"))).thenReturn(vehicleEntity1);

        // Configure OCSService.createVehicleOCS(...).
        final OCSResponse ocsResponse2 = new OCSResponse();
        ocsResponse2.resultCode("resultCode");
        ocsResponse2.setResultCode("resultCode");
        ocsResponse2.description("description");
        ocsResponse2.setDescription("description");
        ocsResponse2.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.createVehicleOCS(new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"), null, 0L)).thenReturn(ocsResponse2);

        // Configure SaleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus(...).
        final SaleTransDetailEntity saleTransDetailEntity1 = new SaleTransDetailEntity();
        saleTransDetailEntity1.setSaleTransDetailId(0L);
        saleTransDetailEntity1.setSaleTransId(0L);
        saleTransDetailEntity1.setSaleTransDate(new Date(0L));
        saleTransDetailEntity1.setServiceFeeId(0L);
        saleTransDetailEntity1.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity1.setVehicleId(0L);
        saleTransDetailEntity1.setPlateNumber("plateNumber");
        saleTransDetailEntity1.setEpc("epc");
        saleTransDetailEntity1.setTid("tid");
        saleTransDetailEntity1.setRfidSerial("rfidSerial");
        final List<SaleTransDetailEntity> saleTransDetailEntities1 = Arrays.asList(saleTransDetailEntity1);
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findByPlateNumberAndEpcAndStatus("plateNumber", "epc", 0L)).thenReturn(saleTransDetailEntities1);

        // Configure SaleTransRepositoryJPA.findById(...).
        final SaleTransEntity saleTransEntity1 = new SaleTransEntity();
        saleTransEntity1.setSaleTransId(0L);
        saleTransEntity1.setSaleTransCode("saleTransCode");
        saleTransEntity1.setSaleTransDate(new Date(0L));
        saleTransEntity1.setSaleTransType("saleTransType");
        saleTransEntity1.setStatus("status");
        saleTransEntity1.setInvoiceUsed("invoiceUsed");
        saleTransEntity1.setInvoiceCreateDate(new Date(0L));
        saleTransEntity1.setShopId(0L);
        saleTransEntity1.setShopName("shopName");
        saleTransEntity1.setStaffId(0L);
        final Optional<SaleTransEntity> saleTransEntity = Optional.of(saleTransEntity1);
        when(booServiceImplUnderTest.saleTransRepositoryJPA.findById(0L)).thenReturn(saleTransEntity);

        when(booServiceImplUnderTest.ocsService.addSupOffer(new VehicleAddSuffOfferDTO(), null, 0L, 0L, 0L, "staffName", "partyCode")).thenThrow(Exception.class);

        // Configure SaleTransDetailServiceJPA.saveAll(...).
        final SaleTransDetailEntity saleTransDetailEntity2 = new SaleTransDetailEntity();
        saleTransDetailEntity2.setSaleTransDetailId(0L);
        saleTransDetailEntity2.setSaleTransId(0L);
        saleTransDetailEntity2.setSaleTransDate(new Date(0L));
        saleTransDetailEntity2.setServiceFeeId(0L);
        saleTransDetailEntity2.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity2.setVehicleId(0L);
        saleTransDetailEntity2.setPlateNumber("plateNumber");
        saleTransDetailEntity2.setEpc("epc");
        saleTransDetailEntity2.setTid("tid");
        saleTransDetailEntity2.setRfidSerial("rfidSerial");
        final List<SaleTransDetailEntity> saleTransDetailEntities2 = Arrays.asList(saleTransDetailEntity2);
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.saveAll(Arrays.asList(new SaleTransDetailEntity()))).thenReturn(saleTransDetailEntities2);

        // Run the test
        assertThrows(Exception.class, () -> booServiceImplUnderTest.changeEpc(requestOnlineEventSyncBooDTO, authentication, Arrays.asList(new ExceptionListEntity()), Arrays.asList(new SaleTransDetailEntity())));
    }

    @Test
    void testMapActiveStatus() {
        // Setup
        final ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO = new ReqOnlineEventSyncDTO();
        requestOnlineEventSyncBooDTO.setPlate_old("plate_old");
        requestOnlineEventSyncBooDTO.setTicket_type_old("ticket_type_old");
        requestOnlineEventSyncBooDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        requestOnlineEventSyncBooDTO.setSeat_old(0L);
        requestOnlineEventSyncBooDTO.setWeight_goods_old(0.0);
        requestOnlineEventSyncBooDTO.setWeight_all_old(0.0);
        requestOnlineEventSyncBooDTO.setEtag_old("etag_old");
        requestOnlineEventSyncBooDTO.setPlate_new("plate_new");
        requestOnlineEventSyncBooDTO.setEtag_new("etag_new");
        requestOnlineEventSyncBooDTO.setTicket_type_new("ticket_type_new");

        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");

        // Run the test
        booServiceImplUnderTest.mapActiveStatus(requestOnlineEventSyncBooDTO, vehicleEntity);

        // Verify the results
    }

    @Test
    void testFindVehicleGroup() {
        // Setup
        final AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi");
        final ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO = new ReqOnlineEventSyncDTO();
        requestOnlineEventSyncBooDTO.setPlate_old("plate_old");
        requestOnlineEventSyncBooDTO.setTicket_type_old("ticket_type_old");
        requestOnlineEventSyncBooDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        requestOnlineEventSyncBooDTO.setSeat_old(0L);
        requestOnlineEventSyncBooDTO.setWeight_goods_old(0.0);
        requestOnlineEventSyncBooDTO.setWeight_all_old(0.0);
        requestOnlineEventSyncBooDTO.setEtag_old("etag_old");
        requestOnlineEventSyncBooDTO.setPlate_new("plate_new");
        requestOnlineEventSyncBooDTO.setEtag_new("etag_new");
        requestOnlineEventSyncBooDTO.setTicket_type_new("ticket_type_new");

        when(booServiceImplUnderTest.vehicleGroupService.getVehicleGroupById("token", new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi"))).thenReturn(Arrays.asList("value"));

        // Run the test
        final List<String> result = booServiceImplUnderTest.findVehicleGroup("token", addVehicleRequestDTO, requestOnlineEventSyncBooDTO);

        // Verify the results
        assertEquals(Arrays.asList("value"), result);
    }

    @Test
    void testSetValueFollowVehicleMappingType() {
        // Setup
        final AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "createUser", "contractNo", "type", "offerExternalId", "vehicleType", 0L, "vehicleColor", "plateColor", "vehicleMark", "plate", 0L, "vehicleBrand", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "profileStatus", "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "addfilesUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "owner", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "appendixUsername", Arrays.asList(new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L))), 0L, 0, "vehicleGroupCode", "plateTypeCode", "vehicleTypeCode", "vehicleTypeName", 0L,"promotionId", "fileNameGiayDeNghi");
        final ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO = new ReqOnlineEventSyncDTO();
        requestOnlineEventSyncBooDTO.setPlate_old("plate_old");
        requestOnlineEventSyncBooDTO.setTicket_type_old("ticket_type_old");
        requestOnlineEventSyncBooDTO.setRegister_vehicle_type_old("register_vehicle_type_old");
        requestOnlineEventSyncBooDTO.setSeat_old(0L);
        requestOnlineEventSyncBooDTO.setWeight_goods_old(0.0);
        requestOnlineEventSyncBooDTO.setWeight_all_old(0.0);
        requestOnlineEventSyncBooDTO.setEtag_old("etag_old");
        requestOnlineEventSyncBooDTO.setPlate_new("plate_new");
        requestOnlineEventSyncBooDTO.setEtag_new("etag_new");
        requestOnlineEventSyncBooDTO.setTicket_type_new("ticket_type_new");

        // Run the test
        booServiceImplUnderTest.setValueFollowVehicleMappingType(addVehicleRequestDTO, requestOnlineEventSyncBooDTO);

        // Verify the results
    }

    @Test
    void testCancelResult() throws Exception {
        // Setup
        final Authentication authentication = null;
        final ReqCancelResultDTO reqCancelResultDTO = new ReqCancelResultDTO();
        reqCancelResultDTO.setRequest_id(0L);
        reqCancelResultDTO.setRef_trans_id(0L);
        reqCancelResultDTO.setSubscription_ticket_id(0L);
        reqCancelResultDTO.setProcess_datetime(0L);
        reqCancelResultDTO.setResponse_datetime(0L);
        reqCancelResultDTO.setStatus("SUCCESS");
        reqCancelResultDTO.setBOT_confirm(1L);

        final ResCancelResultDTO expectedResult = new ResCancelResultDTO();
        expectedResult.setRequest_id(0L);
        expectedResult.setProcess_datetime(0L);
        expectedResult.setResponse_datetime(0L);

        // Configure SaleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(...).
        final SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
        saleTransDelBoo1Entity.setSaleTransDelBoo1Id(0L);
        saleTransDelBoo1Entity.setSubscriptionTicketId(0L);
        saleTransDelBoo1Entity.setStationType(0L);
        saleTransDelBoo1Entity.setStationInId(0L);
        saleTransDelBoo1Entity.setStationOutId(0L);
        saleTransDelBoo1Entity.setStageId(0L);
        saleTransDelBoo1Entity.setServicePlanTypeId(0L);
        saleTransDelBoo1Entity.setEffDate(new Date(0L));
        saleTransDelBoo1Entity.setExpDate(new Date(0L));
        saleTransDelBoo1Entity.setPlateNumber("plateNumber");
        when(booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(0L)).thenReturn(saleTransDelBoo1Entity);

        // Configure SaleTransDetailRepositoryJPA.findBySubscriptionTicketId(...).
        final SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setOcsCode("0");
        saleTransDetailEntity.setOfferLevel("0");
        saleTransDetailEntity.setEpc("0");
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
        when(booServiceImplUnderTest.saleTransDetailRepositoryJPA.findBySubscriptionTicketId(0L)).thenReturn(saleTransDetailEntity);


        // Configure OCSService.deleteSupOffer(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("SUCCESS");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.deleteSupOffer(authentication, 1L, 0L, "0", "0", "0")).thenReturn(ocsResponse);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse1 = new OCSResponse();
        ocsResponse1.resultCode("0");
        ocsResponse1.setResultCode("0");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(booServiceImplUnderTest.ocsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse1);

        // Configure SaleTransDelBoo1RepositoryJPA.save(...).
        final SaleTransDelBoo1Entity saleTransDelBoo1Entity1 = new SaleTransDelBoo1Entity();
        saleTransDelBoo1Entity1.setContractId(0L);
        saleTransDelBoo1Entity1.setOcsCode("0");
        saleTransDelBoo1Entity1.setOfferLevel("0");
        saleTransDelBoo1Entity1.setEpc("0");
        saleTransDelBoo1Entity1.setSaleTransDelBoo1Id(0L);
        saleTransDelBoo1Entity1.setSubscriptionTicketId(0L);
        saleTransDelBoo1Entity1.setStationType(0L);
        saleTransDelBoo1Entity1.setStationInId(0L);
        saleTransDelBoo1Entity1.setStationOutId(0L);
        saleTransDelBoo1Entity1.setStageId(0L);
        saleTransDelBoo1Entity1.setServicePlanTypeId(0L);
        saleTransDelBoo1Entity1.setEffDate(new Date(0L));
        saleTransDelBoo1Entity1.setExpDate(new Date(0L));
        saleTransDelBoo1Entity1.setPlateNumber("plateNumber");
        when(booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(0L)).thenReturn(saleTransDelBoo1Entity1);
        when(booServiceImplUnderTest.saleTransDelBoo1RepositoryJPA.save(new SaleTransDelBoo1Entity())).thenReturn(saleTransDelBoo1Entity1);

        // Configure SaleTransDetailServiceJPA.save(...).
        final SaleTransDetailEntity saleTransDetailEntity1 = new SaleTransDetailEntity();
        saleTransDetailEntity1.setSaleTransDetailId(0L);
        saleTransDetailEntity1.setSaleTransId(0L);
        saleTransDetailEntity1.setSaleTransDate(new Date(0L));
        saleTransDetailEntity1.setServiceFeeId(0L);
        saleTransDetailEntity1.setServiceFeeName("serviceFeeName");
        saleTransDetailEntity1.setVehicleId(0L);
        saleTransDetailEntity1.setPlateNumber("plateNumber");
        saleTransDetailEntity1.setEpc("epc");
        saleTransDetailEntity1.setTid("tid");
        saleTransDetailEntity1.setRfidSerial("rfidSerial");
        when(booServiceImplUnderTest.saleTransDetailServiceJPA.save(new SaleTransDetailEntity())).thenReturn(saleTransDetailEntity1);

        // Run the test
        final ResCancelResultDTO result = booServiceImplUnderTest.cancelResult(authentication, reqCancelResultDTO);

        // Verify the results
        assertEquals(expectedResult.getRequest_id(), result.getRequest_id());
    }

    @Ignore
    void testSaveVehicle() {
        // Setup
        final Authentication authentication = null;
        final ReqChargeTicketDTO reqChargeTicketDTO = new ReqChargeTicketDTO();
        reqChargeTicketDTO.setStation_type("station_type");
        reqChargeTicketDTO.setStation_in_id(0L);
        reqChargeTicketDTO.setStation_out_id(0L);
        reqChargeTicketDTO.setTicket_type("ticket_type");
        reqChargeTicketDTO.setStart_date("start_date");
        reqChargeTicketDTO.setEnd_date("end_date");
        reqChargeTicketDTO.setPlate("plate");
        reqChargeTicketDTO.setEtag("etag");
        reqChargeTicketDTO.setVehicle_type(0L);
        reqChargeTicketDTO.setRegister_vehicle_type("register_vehicle_type");
        reqChargeTicketDTO.setSeat(0L);

        final VehicleEntity expectedResult = new VehicleEntity(0L, 1L, 1L,
                null, "plate", 0L, 0L,
                0L, null, null, null, null,
                null, null, null, null,
                null, null, "1", null, "etag", null,
                "rfidSerial", null, null, null,
                null, new java.sql.Date(System.currentTimeMillis()), null, null, null, null,
                Date.valueOf("2020-11-11"), "2", null, null,
                null, null, null, null,
                null, null, null,
                null, null, "plateTypeCode","promotionId");


        // Configure VehicleRepositoryJPA.save(...).
//         final VehicleEntity save =
        final VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setCustId(1L);
        vehicleEntity.setContractId(1L);
        vehicleEntity.setPlateNumber("plate");
        vehicleEntity.setVehicleTypeId(0L);
        vehicleEntity.setVehicleGroupId(0L);
        vehicleEntity.setSeatNumber(reqChargeTicketDTO.getSeat());
        vehicleEntity.setCargoWeight(reqChargeTicketDTO.getWeight_goods());
        vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
        vehicleEntity.setCreateUser(null);
        vehicleEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleEntity.setProfileStatus(VehicleEntity.ProfilesStatus.APPROVED.value);
        vehicleEntity.setVehicleId(reqChargeTicketDTO.getVehicle_type());
        vehicleEntity.setEffDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleEntity.setRfidSerial("rfidSerial");
        vehicleEntity.setEpc(reqChargeTicketDTO.getEtag());
        vehicleEntity.setPlateTypeCode("plateTypeCode");

        when(booServiceImplUnderTest.vehicleRepositoryJPA.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
        new MockUp<BooServiceImpl>() {
            @mockit.Mock
            private ActionAuditEntity writeLogNew(Authentication authentication, long reasonId, long actTypeId, long contractId, long customerId, long id, Object entity, String tableName) {
                return new ActionAuditEntity();
            }
        };


        // Run the test
        final VehicleEntity result = booServiceImplUnderTest.saveVehicle(authentication, reqChargeTicketDTO, 0L, "0", "plateTypeCode", "plate");

        // Verify the results
        assertEquals(expectedResult.getVehicleId(), result.getVehicleId());
    }

}
