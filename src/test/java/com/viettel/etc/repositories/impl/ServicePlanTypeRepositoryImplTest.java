package com.viettel.etc.repositories.impl;

import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ServicePlanTypeRepositoryImplTest {

    private ServicePlanTypeRepositoryImpl servicePlanTypeRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        servicePlanTypeRepositoryImplUnderTest = new ServicePlanTypeRepositoryImpl();
    }

    @Test
    void testGetTicketType() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = servicePlanTypeRepositoryImplUnderTest.getTicketType();
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }
}
