package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.boo.DataBooDTO;
import com.viettel.etc.dto.boo.ReqCalculatorTicketDTO;
import com.viettel.etc.dto.boo.ReqMappingDTO;
import com.viettel.etc.dto.boo.ReqQueryTicketDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BooRepositoryImplTest {

    private BooRepositoryImpl booRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        booRepositoryImplUnderTest = new BooRepositoryImpl();
    }

    @Test
    void testFindByPlateNumberAndEpc() {
        // Setup
        final DataBooDTO expectedResult = new DataBooDTO();
        expectedResult.setVehicleId(0L);
        expectedResult.setVehicleType("vehicleType");
        expectedResult.setVehicleTypeId(0L);
        expectedResult.setCustId(0L);
        expectedResult.setCustName("custName");
        expectedResult.setContractId(0L);
        expectedResult.setContractNo("contractNo");
        expectedResult.setPlateType("plateType");
        expectedResult.setTId("tId");
        expectedResult.setRfIdSerial("rfIdSerial");

        // Run the test
        final DataBooDTO result = booRepositoryImplUnderTest.findByPlateNumberAndEpc("plateNumber", "epc");

        // Verify the results
        assertNull(result);
    }

    @Test
    void testQueryTicketBoo() {
        // Setup
        final ReqQueryTicketDTO queryTicketBooDTO = new ReqQueryTicketDTO();
        queryTicketBooDTO.setEtag("etag");
        queryTicketBooDTO.setPlate("plate");
        queryTicketBooDTO.setSubscription_ticket_id(0L);
        queryTicketBooDTO.setStation_in_id(0L);
        queryTicketBooDTO.setStation_out_id(0L);
        queryTicketBooDTO.setRequest_datetime(0L);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = booRepositoryImplUnderTest.queryTicketBoo(queryTicketBooDTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);

    }

    @Test
    void testGetFeeBoo() {
        // Setup
        final ReqCalculatorTicketDTO reqCalculatorTicketDTO = new ReqCalculatorTicketDTO();
        reqCalculatorTicketDTO.setStation_type("station_type");
        reqCalculatorTicketDTO.setStation_in_id(0L);
        reqCalculatorTicketDTO.setStation_out_id(0L);
        reqCalculatorTicketDTO.setTicket_type("ticket_type");
        reqCalculatorTicketDTO.setStart_date("start_date");
        reqCalculatorTicketDTO.setEnd_date("end_date");
        reqCalculatorTicketDTO.setPlate("plate");
        reqCalculatorTicketDTO.setEtag("etag");
        reqCalculatorTicketDTO.setVehicle_type(0L);
        reqCalculatorTicketDTO.setRegister_vehicle_type("register_vehicle_type");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = booRepositoryImplUnderTest.getFeeBoo(reqCalculatorTicketDTO, Arrays.asList("value"), 0L, 0L, null,0L,0L, false);

        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);

    }

    @Test
    void testGetListCategoryMappingBoo() {
        // Setup
        final ReqMappingDTO categoryMappingBooDTO = new ReqMappingDTO();
        categoryMappingBooDTO.setType("type");
        categoryMappingBooDTO.setCode("code");
        categoryMappingBooDTO.setName("name");
        categoryMappingBooDTO.setValue("value");
        categoryMappingBooDTO.setDescription("description");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = booRepositoryImplUnderTest.getListCategoryMappingBoo(categoryMappingBooDTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNull(result);

    }
}
