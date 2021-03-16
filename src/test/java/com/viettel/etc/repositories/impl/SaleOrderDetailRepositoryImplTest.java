package com.viettel.etc.repositories.impl;

import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SaleOrderDetailRepositoryImplTest {

    private SaleOrderDetailRepositoryImpl saleOrderDetailRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        saleOrderDetailRepositoryImplUnderTest = new SaleOrderDetailRepositoryImpl();
    }

    @Test
    void testFindByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate() {
        // Setup
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> expectedResult = Arrays.asList(saleOrderDetailEntity);

        // Run the test
        final List<SaleOrderDetailEntity> result = saleOrderDetailRepositoryImplUnderTest.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate");

        // Verify the results
        assertNull(result);
    }

    @Test
    void testFindAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate() {
        // Setup
        final SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
        saleOrderDetailEntity.setSaleOrderDetailId(0L);
        saleOrderDetailEntity.setSaleOrderId(0L);
        saleOrderDetailEntity.setSaleOrderDate(new Date(0L));
        saleOrderDetailEntity.setServiceFeeId(0L);
        saleOrderDetailEntity.setServiceFeeName("serviceFeeName");
        saleOrderDetailEntity.setVehicleId(0L);
        saleOrderDetailEntity.setPlateNumber("plateNumber");
        saleOrderDetailEntity.setEpc("epc");
        saleOrderDetailEntity.setTid("tid");
        saleOrderDetailEntity.setRfidSerial("rfidSerial");
        final List<SaleOrderDetailEntity> expectedResult = Arrays.asList(saleOrderDetailEntity);

        // Run the test
        final List<SaleOrderDetailEntity> result = saleOrderDetailRepositoryImplUnderTest.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(0L, "plateNumber", 0L, "effDate", "expDate");

        // Verify the results
        assertNull(result);
    }
}
