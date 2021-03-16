package com.viettel.etc.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionsHistoryVehicleDTOTest {

    private TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTOUnderTest;

    @BeforeEach
    void setUp() {
        transactionsHistoryVehicleDTOUnderTest = new TransactionsHistoryVehicleDTO(new TransactionsHistoryVehicleDTO.TransactionsHistoryList(), new TransactionsHistoryVehicleDTO.StatusTransactionsHistoryVehicleDTO());
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = transactionsHistoryVehicleDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = transactionsHistoryVehicleDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(transactionsHistoryVehicleDTOUnderTest.hashCode(), result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = transactionsHistoryVehicleDTOUnderTest.toString();

        // Verify the results
        assertEquals(transactionsHistoryVehicleDTOUnderTest.toString(), result);
    }
}
