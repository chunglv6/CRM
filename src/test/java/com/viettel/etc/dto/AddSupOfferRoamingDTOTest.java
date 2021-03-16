package com.viettel.etc.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddSupOfferRoamingDTOTest {

    private AddSupOfferRoamingDTO addSupOfferRoamingDTOUnderTest;

    @BeforeEach
    void setUp() {
        addSupOfferRoamingDTOUnderTest = new AddSupOfferRoamingDTO();
        addSupOfferRoamingDTOUnderTest.EPC = "EPC";
        addSupOfferRoamingDTOUnderTest.eff_Date = "eff_Date";
        addSupOfferRoamingDTOUnderTest.exp_Date = "exp_Date";
        addSupOfferRoamingDTOUnderTest.partyCode = "partyCode";
        addSupOfferRoamingDTOUnderTest.agentId = "agentId";
        addSupOfferRoamingDTOUnderTest.agentName = "agentName";
        addSupOfferRoamingDTOUnderTest.staffId = "staffId";
        addSupOfferRoamingDTOUnderTest.staffName = "staffName";
        addSupOfferRoamingDTOUnderTest.subscriptionTicketId = "subscriptionTicketId";
        addSupOfferRoamingDTOUnderTest.requestId = "requestId";
        addSupOfferRoamingDTOUnderTest.requestDateTime = "requestDateTime";
        addSupOfferRoamingDTOUnderTest.responseId = "responseId";
        addSupOfferRoamingDTOUnderTest.responseDateTime = "responseDateTime";
        addSupOfferRoamingDTOUnderTest.ticketType = "ticketType";
        addSupOfferRoamingDTOUnderTest.discountId = "discountId";
        addSupOfferRoamingDTOUnderTest.charge = "charge";
        addSupOfferRoamingDTOUnderTest.stationIn = "stationIn";
        addSupOfferRoamingDTOUnderTest.stationOut = "stationOut";
        addSupOfferRoamingDTOUnderTest.laneIn = "laneIn";
        addSupOfferRoamingDTOUnderTest.laneOut = "laneOut";
        addSupOfferRoamingDTOUnderTest.actTypeId = 0L;
    }

    @Test
    void testEquals() {
        // Setup

        // Run the test
        final boolean result = addSupOfferRoamingDTOUnderTest.equals("o");

        // Verify the results
        assertTrue(!result);
    }

    @Test
    void testHashCode() {
        // Setup

        // Run the test
        final int result = addSupOfferRoamingDTOUnderTest.hashCode();

        // Verify the results
        assertEquals(result, result);
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = addSupOfferRoamingDTOUnderTest.toString();

        // Verify the results
        assertEquals(addSupOfferRoamingDTOUnderTest.toString(), result);
    }
}
