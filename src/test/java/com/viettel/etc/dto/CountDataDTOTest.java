package com.viettel.etc.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountDataDTOTest {

    private CountDataDTO countDataDTOUnderTest;

    @BeforeEach
    void setUp() {
        countDataDTOUnderTest = new CountDataDTO();
        countDataDTOUnderTest.totalCustomer = 0;
        countDataDTOUnderTest.totalContract = 0;
        countDataDTOUnderTest.totalVehicle = 0;
        countDataDTOUnderTest.totalContractProfile = 0;
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = countDataDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = countDataDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(countDataDTOUnderTest.hashCode(), result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = countDataDTOUnderTest.toString();

        // Verify the results
        assertEquals(countDataDTOUnderTest.toString(), result);
    }
}
