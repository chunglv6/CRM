package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanFeeDTO;
import com.viettel.etc.repositories.ServicePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ServicePlanServiceImplTest {

    @InjectMocks
    ServicePlanServiceImpl servicePlanService;

    @Mock
    ServicePlanRepository servicePlanRepository;

    ServicePlanDTO itemParamsEntity;
    
    @BeforeEach
    void setUp() {
        itemParamsEntity = new ServicePlanDTO();
    }

//    @Test
//    void getFee_Test1() throws IOException {
//
//        //mock
//        given(servicePlanRepository.getFee(itemParamsEntity)).willReturn(null);
//
//        //goi den ham service de gia lap
//        Object data;
//        data = servicePlanService.getFee(itemParamsEntity, null);
//
//        assertNull(data);
//    }

//    @Test
//    void getFee_Test2() throws IOException {
//
//        itemParamsEntity.setVehicleGroupIdArr("2,3");
//        itemParamsEntity.setServicePlanTypeId((long)4);
//        itemParamsEntity.setChargeMethodId((long)2);
//        itemParamsEntity.setPlateNumber("'33H1123321','33H13333333'");
//        itemParamsEntity.setStageId((long)21);
//        itemParamsEntity.setAutoRenew((long)1);
//        itemParamsEntity.setCreateDateFrom("04/08/2020");
//        itemParamsEntity.setCreateDateTo("03/09/2020");
//
//        ServicePlanFeeDTO servicePlanFeeDTOItem = new ServicePlanFeeDTO();
//        List<ServicePlanDTO> servicePlanDTOList = new ArrayList<>();
//        ServicePlanDTO servicePlanDTOItem = new ServicePlanDTO();
//
//        servicePlanDTOItem.setServicePlanId((long) 256);
//        servicePlanDTOItem.setServicePlanTypeId((long) 4);
//        servicePlanDTOItem.setVehicleGroupId((long) 3);
//        servicePlanDTOItem.setChargeMethodId((long) 2);
//        servicePlanDTOItem.setAutoRenew((long) 1);
//        servicePlanDTOItem.setFee((long) 100000);
//        servicePlanDTOList.add(servicePlanDTOItem);
//
//
//        servicePlanFeeDTOItem.setListServicePlan(servicePlanDTOList);
//        servicePlanFeeDTOItem.setServicePlanVehicleDuplicate("33H1123321,33H13333333");
//
//        //mock
//        given(servicePlanRepository.getFee(itemParamsEntity)).willReturn(servicePlanFeeDTOItem);
//
//        Object data;
//        //goi den ham service de gia lap
//        data = servicePlanService.getFee(itemParamsEntity, null);
//
//        ServicePlanFeeDTO dataResult = new ServicePlanFeeDTO();
//        List<ServicePlanDTO> servicePlanDTOListResult = new ArrayList<>();
//        ServicePlanDTO servicePlanDTOItemResult = new ServicePlanDTO();
//
//        servicePlanDTOItemResult.setServicePlanId((long) 256);
//        servicePlanDTOItemResult.setServicePlanTypeId((long) 4);
//        servicePlanDTOItemResult.setVehicleGroupId((long) 3);
//        servicePlanDTOItemResult.setChargeMethodId((long) 2);
//        servicePlanDTOItemResult.setAutoRenew((long) 1);
//        servicePlanDTOItemResult.setFee((long) 100000);
//
//        servicePlanDTOListResult.add(servicePlanDTOItemResult);
//
//        dataResult.setListServicePlan(servicePlanDTOListResult);
//        dataResult.setServicePlanVehicleDuplicate("33H1123321,33H13333333");
//
//        System.err.println(dataResult);
//        assertEquals(dataResult,data);
//    }
}