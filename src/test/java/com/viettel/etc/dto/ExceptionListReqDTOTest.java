package com.viettel.etc.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Date;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

class ExceptionListReqDTOTest {

    @Mock
    private Date mockEffDate;
    @Mock
    private Date mockExpDate;

    private ExceptionListReqDTO exceptionListReqDTOUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        exceptionListReqDTOUnderTest = new ExceptionListReqDTO();
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = exceptionListReqDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = exceptionListReqDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(exceptionListReqDTOUnderTest.hashCode(), result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = exceptionListReqDTOUnderTest.toString();

        // Verify the results
        assertEquals(exceptionListReqDTOUnderTest.toString(), result);
    }

    @Test
    void testBuilder() {
        // Setup

        // Run the test
        final ExceptionListReqDTO.ExceptionListReqDTOBuilder result = ExceptionListReqDTO.builder();

        // Verify the results
    }
}
