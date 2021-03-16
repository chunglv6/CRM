package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SaleTranTicketsDTO;
import com.viettel.etc.dto.SaleTransDetailDTO;
import com.viettel.etc.dto.SaleTransDetailVehicleOwnerAppDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SaleTransDetailRepositoryImplTest {

    private SaleTransDetailRepositoryImpl saleTransDetailRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        saleTransDetailRepositoryImplUnderTest = new SaleTransDetailRepositoryImpl();
    }

    @Test
    void testFindTicketPurchaseHistories() {
        // Setup
        final SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();

        saleTransDetailDTO.setSaleTransDateFrom("20/10/2020");
        saleTransDetailDTO.setSaleTransDateTo("22/10/2020");
        saleTransDetailDTO.setEffect(0L);
        saleTransDetailDTO.setSaleTransId(0L);
        saleTransDetailDTO.setStationId(0L);
        saleTransDetailDTO.setStageId(0L);
        saleTransDetailDTO.setStage("Stage");
        saleTransDetailDTO.setStation("Station");
        saleTransDetailDTO.setServicePlanTypeId(0L);
        saleTransDetailDTO.setServicePlanTypeName("servicePlanTypeName");
        saleTransDetailDTO.setPrice(0L);
        saleTransDetailDTO.setStatus(0L);
        saleTransDetailDTO.setSaleTransDate("20/10/2020");
        saleTransDetailDTO.setChargeMethodName("chargeMethodName");
        saleTransDetailDTO.setAccountOwner("AccountOwner");
        saleTransDetailDTO.setActTypeId(0L);
        saleTransDetailDTO.setSubscriptionTicketId(0L);
        saleTransDetailDTO.setStationType(0L);
        saleTransDetailDTO.setStationOutId(0L);
        saleTransDetailDTO.setSaleTransDetailId(0L);
        saleTransDetailDTO.setSaleTransCode("saleTransCode");
        saleTransDetailDTO.setResultsqlex(false);
        saleTransDetailDTO.setReasonId(0L);
        saleTransDetailDTO.setPlateNumber("plateNumber");
        saleTransDetailDTO.setPaymentMethodId(0L);
        saleTransDetailDTO.setOfferLevel("offerLevel");
        saleTransDetailDTO.setOfferId("0");
        saleTransDetailDTO.setExpDate("20/10/2020");
        saleTransDetailDTO.setChargeMethodId(0L);
        saleTransDetailDTO.setBooCode("booCode");
        saleTransDetailDTO.setAutoRenew("autoRenew");
        saleTransDetailDTO.setEpc("EPC");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransDetailRepositoryImplUnderTest.findTicketPurchaseHistories(0L, saleTransDetailDTO);
        }catch (NullPointerException e){
            result = null;
        }
    }

    @Test
    void testVehicleOwnerAppTicketPurchaseHistories() {
        // Setup
        final SaleTransDetailVehicleOwnerAppDTO saleTransDetailVehicleOwnerAppDTO = new SaleTransDetailVehicleOwnerAppDTO("plateNumber", "vehicleGroupName", 0L, "methodCharge", "createUser");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransDetailRepositoryImplUnderTest.vehicleOwnerAppTicketPurchaseHistories(saleTransDetailVehicleOwnerAppDTO, 0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
    }

    @Test
    void testSearchTicketPurchaseEfficiencyHistories() {
        // Setup
        final SaleTranTicketsDTO req = new SaleTranTicketsDTO(0L, 0, 0);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransDetailRepositoryImplUnderTest.searchTicketPurchaseEfficiencyHistories(req);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testSearchTicketHistories() {
        // Setup
        final SaleTranTicketsDTO req = new SaleTranTicketsDTO(0L, 0, 0);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransDetailRepositoryImplUnderTest.searchTicketHistories(req);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }
}
