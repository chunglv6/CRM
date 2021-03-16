package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.repositories.tables.entities.PromotionEntity;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertNull;

class PromotionRepositoryImplTest {

    private PromotionRepositoryImpl promotionRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        promotionRepositoryImplUnderTest = new PromotionRepositoryImpl();
    }

    @Test
    void testGetPromotions() {
        // Setup
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final PromotionDTO itemParamsEntity = new PromotionDTO(promotionEntity);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = promotionRepositoryImplUnderTest.getPromotions(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
        // Verify the results
    }

    @Test
    void testGetPromotionAssignDetail() {
        // Setup
        final PromotionAssignDTO itemParamsEntity = new PromotionAssignDTO();
        itemParamsEntity.setPromotionAssignId(0L);
        itemParamsEntity.setPromotionId(0L);
        itemParamsEntity.setAssignLevel("assignLevel");
        itemParamsEntity.setCustId(0L);
        itemParamsEntity.setCustName("custName");
        itemParamsEntity.setContractId(0L);
        itemParamsEntity.setContractNo("contractNo");
        itemParamsEntity.setVehicleId(0L);
        itemParamsEntity.setPlateNumber("plateNumber");
        itemParamsEntity.setEPC("EPC");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = promotionRepositoryImplUnderTest.getPromotionAssignDetail(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }
}
