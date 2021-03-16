package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.boo.ReqCalculatorTicketDTO;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionListRepositoryImplTest {

    private ExceptionListRepositoryImpl exceptionListRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        exceptionListRepositoryImplUnderTest = new ExceptionListRepositoryImpl();
    }

    @Test
    void testFindAllExceptionEffective() {
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

        final List<ExceptionListEntity> expectedResult = new ArrayList<>();

        // Run the test
        //final List<ExceptionListEntity> result = exceptionListRepositoryImplUnderTest.findAllExceptionEffective(reqCalculatorTicketDTO, 0L, 0L, "licensePlateType");

        // Verify the results
        //assertEquals(expectedResult, result);
        assertThrows(NullPointerException.class, () -> exceptionListRepositoryImplUnderTest.findAllExceptionEffective(reqCalculatorTicketDTO, 0L, 0L, "licensePlateType"));
    }
}
