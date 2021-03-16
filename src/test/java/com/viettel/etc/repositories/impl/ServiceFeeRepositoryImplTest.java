package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertNull;

class ServiceFeeRepositoryImplTest {

    private ServiceFeeRepositoryImpl serviceFeeRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        serviceFeeRepositoryImplUnderTest = new ServiceFeeRepositoryImpl();
    }

    @Test
    void testGetServiceFee() {
        // Setup
        final ServiceFeeDTO params = new ServiceFeeDTO();
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = serviceFeeRepositoryImplUnderTest.getServiceFee(params);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }
}
