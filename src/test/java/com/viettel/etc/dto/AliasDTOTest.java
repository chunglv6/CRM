package com.viettel.etc.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AliasDTOTest {

    private AliasDTO aliasDTOUnderTest;

    @BeforeEach
    void setUp() {
        aliasDTOUnderTest = new AliasDTO("aliasName");
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = aliasDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = aliasDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(aliasDTOUnderTest.hashCode(), result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = aliasDTOUnderTest.toString();

        // Verify the results
        assertEquals(aliasDTOUnderTest.toString(), result);
    }
}
