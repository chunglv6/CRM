package com.viettel.etc.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChargeSupOfferDTOTest {

    private ChargeSupOfferDTO chargeSupOfferDTOUnderTest;

    @BeforeEach
    void setUp() {
        chargeSupOfferDTOUnderTest = new ChargeSupOfferDTO();
        chargeSupOfferDTOUnderTest.contractId = "contractId";
        chargeSupOfferDTOUnderTest.epc = "epc";
        chargeSupOfferDTOUnderTest.offerId = "offerId";
        chargeSupOfferDTOUnderTest.exp_Date = "exp_Date";
        chargeSupOfferDTOUnderTest.offerLevel = "offerLevel";
        chargeSupOfferDTOUnderTest.isRecurring = "isRecurring";
        chargeSupOfferDTOUnderTest.isRecurringExtBalance = "isRecurringExtBalance";
        chargeSupOfferDTOUnderTest.agentId = "agentId";
        chargeSupOfferDTOUnderTest.agentName = "agentName";
        chargeSupOfferDTOUnderTest.staffId = "staffId";
        chargeSupOfferDTOUnderTest.staffName = "staffName";
        chargeSupOfferDTOUnderTest.subscriptionTicketId = "subscriptionTicketId";
        chargeSupOfferDTOUnderTest.requestId = "requestId";
        chargeSupOfferDTOUnderTest.requestDateTime = "requestDateTime";
        chargeSupOfferDTOUnderTest.responseId = "responseId";
        chargeSupOfferDTOUnderTest.responseDateTime = "responseDateTime";
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = chargeSupOfferDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = chargeSupOfferDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(chargeSupOfferDTOUnderTest.hashCode(), result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = chargeSupOfferDTOUnderTest.toString();

        // Verify the results
        assertEquals(chargeSupOfferDTOUnderTest.toString(), result);
    }
}
