package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TicketRepositoryImplTest {

    private TicketRepositoryImpl ticketRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        ticketRepositoryImplUnderTest = new TicketRepositoryImpl();
    }

    @Test
    void testGetFee() {
        // Setup
        final ServicePlanDTO itemParamsEntity = new ServicePlanDTO();
        itemParamsEntity.setServicePlanId(0L);
        itemParamsEntity.setServicePlanCode("servicePlanCode");
        itemParamsEntity.setServicePlanName("servicePlanName");
        itemParamsEntity.setServicePlanTypeId(0L);
        itemParamsEntity.setOcsCode("ocsCode");
        itemParamsEntity.setVehicleGroupId(0L);
        itemParamsEntity.setRouteId(0L);
        itemParamsEntity.setStationType(0L);
        itemParamsEntity.setDescription("description");
        itemParamsEntity.setStationId(0L);

        final ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setServicePlanId(0L);
        servicePlanDTO.setServicePlanCode("servicePlanCode");
        servicePlanDTO.setServicePlanName("servicePlanName");
        servicePlanDTO.setServicePlanTypeId(0L);
        servicePlanDTO.setOcsCode("ocsCode");
        servicePlanDTO.setVehicleGroupId(0L);
        servicePlanDTO.setRouteId(0L);
        servicePlanDTO.setStationType(0L);
        servicePlanDTO.setDescription("description");
        servicePlanDTO.setStationId(0L);
        final List<ServicePlanDTO> expectedResult = Arrays.asList(servicePlanDTO);

        // Run the test
        final List<ServicePlanDTO> result = ticketRepositoryImplUnderTest.getFee(itemParamsEntity);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testCheckExistsTicket() {
        // Setup
        final ServicePlanDTO param = new ServicePlanDTO();
        param.setServicePlanId(0L);
        param.setServicePlanCode("servicePlanCode");
        param.setServicePlanName("servicePlanName");
        param.setServicePlanTypeId(0L);
        param.setOcsCode("ocsCode");
        param.setVehicleGroupId(0L);
        param.setRouteId(0L);
        param.setStationType(0L);
        param.setDescription("description");
        param.setStationId(0L);

        final ServicePlanVehicleDuplicateDTO expectedResult = new ServicePlanVehicleDuplicateDTO();
        expectedResult.setPlateNumber("plateNumber");

        // Run the test
        final ServicePlanVehicleDuplicateDTO result = ticketRepositoryImplUnderTest.checkExistsTicket(param, false);

        // Verify the results
        assertNull(result);
    }
}
