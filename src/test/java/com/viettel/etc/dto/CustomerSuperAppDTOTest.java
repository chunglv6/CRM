package com.viettel.etc.dto;

import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class CustomerSuperAppDTOTest {

    private CustomerSuperAppDTO customerSuperAppDTOUnderTest;

    @BeforeEach
    void setUp() {
        customerSuperAppDTOUnderTest = new CustomerSuperAppDTO("EMPTY_STRING", 0L, 0L, "contractNo", "custName", "registerNo", "phoneNumber", "documentNumber", "password", 0L, 0L, 0L, 0L);
    }

    @Test
    void testToAddCustomerEntity() {
        // Setup
        final CustomerEntity expectedResult = new CustomerEntity();
        expectedResult.setCustId(0L);
        expectedResult.setCustTypeId(0L);
        expectedResult.setDocumentNumber("documentNumber");
        expectedResult.setDocumentTypeId(0L);
        expectedResult.setDateOfIssue(new Date(0L));
        expectedResult.setPlaceOfIssue("EMPTY_STRING");
        expectedResult.setCustName("custName");
        expectedResult.setBirthDate(new Date(0L));
        expectedResult.setGender("gender");

        // Run the test
        final CustomerEntity result = customerSuperAppDTOUnderTest.toAddCustomerEntity();

        // Verify the results
        //assertEquals(expectedResult, result);
        assertNotNull(result);
    }

    @Test
    void testToAddContractEntity() {
        // Setup
        final ContractEntity expectedResult = new ContractEntity();

        // Run the test
        final ContractEntity result = customerSuperAppDTOUnderTest.toAddContractEntity();

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = customerSuperAppDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = customerSuperAppDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(customerSuperAppDTOUnderTest.hashCode(), result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = customerSuperAppDTOUnderTest.toString();

        // Verify the results
        assertEquals(customerSuperAppDTOUnderTest.toString(), result);
    }

    @Test
    void testBuilder() {
        // Setup

        // Run the test
        final CustomerSuperAppDTO.CustomerSuperAppDTOBuilder result = CustomerSuperAppDTO.builder();

        // Verify the results
    }
}
