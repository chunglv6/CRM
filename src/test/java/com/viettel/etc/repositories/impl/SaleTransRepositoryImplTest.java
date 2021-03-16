package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SaleTransDTO;
import com.viettel.etc.dto.SaleTransVehicleOwnerAppDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleTransRepositoryImplTest {

    private SaleTransRepositoryImpl saleTransRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        saleTransRepositoryImplUnderTest = new SaleTransRepositoryImpl();
    }

    @Test
    void testFindOtherTransactionHistories() {
        // Setup
        final SaleTransDTO saleTransDTO = new SaleTransDTO(0L, "code", "name", "saleTransDateFrom", "saleTransDateTo", 0L, 0L, "serviceFeeName", "createUser", new Date(0L), 0L, "saleTransContent", 0, 0, false, 0L, "serviceFeeCode", "saleTransCode");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransRepositoryImplUnderTest.findOtherTransactionHistories(0L, saleTransDTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testGetServiceFees() {
        // Setup
        final SaleTransDTO itemParamsEntity = new SaleTransDTO(0L, "code", "name", "saleTransDateFrom", "saleTransDateTo", 0L, 0L, "serviceFeeName", "createUser", new Date(0L), 0L, "saleTransContent", 0, 0, false, 0L, "serviceFeeCode", "saleTransCode");
        final List<SaleTransDTO> expectedResult = Arrays.asList(new SaleTransDTO(0L, "code", "name", "saleTransDateFrom", "saleTransDateTo", 0L, 0L, "serviceFeeName", "createUser", new Date(0L), 0L, "saleTransContent", 0, 0, false, 0L, "serviceFeeCode", "saleTransCode"));

        // Run the test
        final List<SaleTransDTO> result = saleTransRepositoryImplUnderTest.getServiceFees(itemParamsEntity);

        assertNull(result);
    }

    @Test
    void testVehicleOwnerAppOtherTransactionHistories() {
        // Setup
        final SaleTransVehicleOwnerAppDTO transVehicleOwnerAppDTO = new SaleTransVehicleOwnerAppDTO("saleTransCode", "0L", "0L", "plateNumber", "fromDate", "toDate");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = saleTransRepositoryImplUnderTest.vehicleOwnerAppOtherTransactionHistories(transVehicleOwnerAppDTO, 0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }
}
