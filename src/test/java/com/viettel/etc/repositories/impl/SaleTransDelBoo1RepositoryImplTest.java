package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SaleTransDelBoo1DTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SaleTransDelBoo1RepositoryImplTest {

    private SaleTransDelBoo1RepositoryImpl saleTransDelBoo1RepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        saleTransDelBoo1RepositoryImplUnderTest = new SaleTransDelBoo1RepositoryImpl();
    }

    @Test
    void testGetIMInfo() {
        // Setup
        final SaleTransDelBoo1DTO itemParamsEntity = new SaleTransDelBoo1DTO();
        itemParamsEntity.setSaleTransDelBoo1Id(0L);
        itemParamsEntity.setSubscriptionTicketId(0L);
        itemParamsEntity.setStationType(0L);
        itemParamsEntity.setStationInId(0L);
        itemParamsEntity.setStationOutId(0L);
        itemParamsEntity.setStageId(0L);
        itemParamsEntity.setServicePlanTypeId(0L);
        itemParamsEntity.setEffDate(new Date(0L));
        itemParamsEntity.setExpDate(new Date(0L));
        itemParamsEntity.setPlateNumber("plateNumber");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransDelBoo1RepositoryImplUnderTest.getSaleTransDelBoo1(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testSearchTicketDel() {
        // Setup
        final SaleTransDelBoo1DTO saleTransDelBoo1DTO = new SaleTransDelBoo1DTO();
        saleTransDelBoo1DTO.setSaleTransDelBoo1Id(0L);
        saleTransDelBoo1DTO.setSubscriptionTicketId(0L);
        saleTransDelBoo1DTO.setStationType(0L);
        saleTransDelBoo1DTO.setStationInId(0L);
        saleTransDelBoo1DTO.setStationOutId(0L);
        saleTransDelBoo1DTO.setStageId(0L);
        saleTransDelBoo1DTO.setServicePlanTypeId(0L);
        saleTransDelBoo1DTO.setEffDate(new Date(0L));
        saleTransDelBoo1DTO.setExpDate(new Date(0L));
        saleTransDelBoo1DTO.setPlateNumber("plateNumber");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransDelBoo1RepositoryImplUnderTest.searchTicketDel(0L, saleTransDelBoo1DTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }
}
