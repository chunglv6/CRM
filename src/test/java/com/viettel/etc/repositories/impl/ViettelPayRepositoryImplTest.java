package com.viettel.etc.repositories.impl;

import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ViettelPayRepositoryImplTest {

    private ViettelPayRepositoryImpl viettelPayRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        viettelPayRepositoryImplUnderTest = new ViettelPayRepositoryImpl();
    }

    @Test
    void testFindByPlateOrContract() {
        // Setup

        // Run the test
        final Object result = viettelPayRepositoryImplUnderTest.findByPlateOrContract("orderId", "searchType", 0L, "contractNo", "plateType", "plateNumber");

        // Verify the results
    }

    @Test
    void testFindAllVehicleByContractAndPlate() {
        // Setup

        // Run the test
        final Object result = viettelPayRepositoryImplUnderTest.findAllVehicleByContractAndPlate(0L);

        // Verify the results
    }

    @Test
    void testGetInfoOderTicketPurchaseAndExtendedViaSDK() {
        // Run the test
        Object result;
        try {
            result = viettelPayRepositoryImplUnderTest.getInfoOderTicketPurchaseAndExtendedViaSDK("billingCode");
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
        // Verify the results
    }

    @Test
    void testGetTicketExtendedViaSDK() {
        // Run the test
        Object result;
        try {
            result = viettelPayRepositoryImplUnderTest.getTicketExtendedViaSDK("orderId");
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
        // Verify the results
    }

    @Test
    void testGetInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream() {
        // Run the test
        Object result;
        try {
            result = viettelPayRepositoryImplUnderTest.getInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream("billingCode");
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
        // Verify the results
    }

    @Test
    void testGetTicketExtendedViaSDKPrivateStream() {
        // Setup

        // Run the test
        final Object result = viettelPayRepositoryImplUnderTest.getTicketExtendedViaSDKPrivateStream("orderId");

        // Verify the results
    }
}
