package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanFeeDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicePlanRepositoryImplTest {

    private ServicePlanRepositoryImpl servicePlanRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        servicePlanRepositoryImplUnderTest = new ServicePlanRepositoryImpl();
        new MockUp<CommonDataBaseRepository>() {
            @mockit.Mock
            public ResultSelectEntity getListDataAndCount(StringBuilder queryString, List<Object> arrParams, Integer startPage, Integer pageLoad, Class<?> classOfT) {
                return new ResultSelectEntity();
            }
        };
    }

    @Test
    void testSearchTicketPrices() {
        // Setup
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
        servicePlanDTO.setScope(1L);
        servicePlanDTO.setStageId(1L);
        servicePlanDTO.setCreateDateFrom("11/11/2020");
        servicePlanDTO.setCreateDateTo("11/11/2020");
        servicePlanDTO.setApproveDateFrom("11/11/2020");
        servicePlanDTO.setApproveDateTo("11/11/2020");
        servicePlanDTO.setStartDateFrom("11/11/2020");
        servicePlanDTO.setEndDateTo("11/11/2020");
        servicePlanDTO.setStatus(1L);
        servicePlanDTO.setEffId(1L);
        servicePlanDTO.setStartrecord(1);
        servicePlanDTO.setPagesize(5);

        // Run the test
        ResultSelectEntity result = servicePlanRepositoryImplUnderTest.searchTicketPrices(servicePlanDTO);
        assertNotNull(result);

        // Verify the results
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
        itemParamsEntity.setPlateNumber("a");
        itemParamsEntity.setStageId(1L);
        itemParamsEntity.setLaneIn(1L);
        itemParamsEntity.setLaneOut(10L);
        itemParamsEntity.setChargeMethodId(1L);
        itemParamsEntity.setVehicleGroupIdArr("getVehicleGroupIdArr");
        itemParamsEntity.setAutoRenew(1L);
        itemParamsEntity.setCreateDateFrom("11/11/2020");
        itemParamsEntity.setStatus(1l);
        itemParamsEntity.setStartrecord(1);
        itemParamsEntity.setPagesize(1);
        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("12345");

        new MockUp<ServicePlanRepositoryImpl>() {
            @mockit.Mock
            public ServicePlanVehicleDuplicateDTO checkExistsTicket(ServicePlanDTO param) {
                return servicePlanVehicleDuplicateDTO;
            }
        };

        final ServicePlanFeeDTO expectedResult = new ServicePlanFeeDTO();
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
        expectedResult.setListServicePlan(Arrays.asList(servicePlanDTO));
        expectedResult.setServicePlanVehicleDuplicate("servicePlanVehicleDuplicate");

        // Run the test
        final ServicePlanFeeDTO result = servicePlanRepositoryImplUnderTest.getFee(itemParamsEntity);

        assertNotNull(result);
    }

    @Test
    void testGetFeeMobile() {
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
        servicePlanDTO.setScope(1L);
        servicePlanDTO.setStageId(1L);
        servicePlanDTO.setCreateDateFrom("11/11/2020");
        servicePlanDTO.setCreateDateTo("11/11/2020");
        servicePlanDTO.setApproveDateFrom("11/11/2020");
        servicePlanDTO.setApproveDateTo("11/11/2020");
        servicePlanDTO.setStartDateFrom("11/11/2020");
        servicePlanDTO.setEndDateTo("11/11/2020");
        servicePlanDTO.setStatus(1L);
        servicePlanDTO.setEffId(1L);
        servicePlanDTO.setStartrecord(1);
        servicePlanDTO.setPagesize(5);
    }

    @Test
    void testGetMinFee() {
        // Setup
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
        servicePlanDTO.setStationIdList("avc");
        servicePlanDTO.setStageIdList("avc");
        servicePlanDTO.setStartrecord(1);
        servicePlanDTO.setPagesize(1);

        // Run the test
        final ResultSelectEntity result = servicePlanRepositoryImplUnderTest.getMinFee(servicePlanDTO);
        assertNotNull(result);

        // Verify the results
    }

    @Test
    void testFindTicketPricesExits() {
        // Setup
        final ServicePlanDTO dataParams = new ServicePlanDTO();
        dataParams.setServicePlanId(0L);
        dataParams.setServicePlanCode("servicePlanCode");
        dataParams.setServicePlanName("servicePlanName");
        dataParams.setServicePlanTypeId(0L);
        dataParams.setOcsCode("ocsCode");
        dataParams.setVehicleGroupId(0L);
        dataParams.setRouteId(0L);
        dataParams.setStationType(0L);
        dataParams.setDescription("description");
        dataParams.setStationId(0L);
        dataParams.setScope(0L);
        dataParams.setStageId(0L);
        dataParams.setLaneOut(0L);
        dataParams.setLaneIn(0L);
        dataParams.setStartrecord(1);
        dataParams.setPagesize(1);

        // Run the test
        final ResultSelectEntity result = servicePlanRepositoryImplUnderTest.findTicketPricesExits(dataParams);
        assertNotNull(result);
    }

    @Test
    void testFindTicketPriceExitsEffDate() {
        // Setup
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
        servicePlanDTO.setLaneOut(0L);
        servicePlanDTO.setStageId(0L);
        servicePlanDTO.setAutoRenew(0L);
        servicePlanDTO.setPagesize(1);
        servicePlanDTO.setStartrecord(1);
        servicePlanDTO.setStartDate(new java.sql.Date(System.currentTimeMillis()));
        servicePlanDTO.setEndDate(new java.sql.Date(System.currentTimeMillis()));

        // Run the test
        final ResultSelectEntity result = servicePlanRepositoryImplUnderTest.findTicketPriceExitsEffDate(servicePlanDTO);
        assertNotNull(result);
    }

    @Test
    void testCheckExistsTicketNew() {
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
        param.setPlateNumber("0L");
        param.setStageId(0L);
        param.setLaneOut(0L);
        param.setCreateDateFrom("0L");
        param.setCreateDateTo("0L");

        final ServicePlanVehicleDuplicateDTO expectedResult = new ServicePlanVehicleDuplicateDTO();
        expectedResult.setPlateNumber("plateNumber");

        // Run the test
        final ServicePlanVehicleDuplicateDTO result = servicePlanRepositoryImplUnderTest.checkExistsTicketNew(param);
        assertNull(result);
    }

    @Test
    void testGetFeeNew() {
        // Setup
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
        servicePlanDTO.setStageId(0L);
        servicePlanDTO.setChargeMethodId(0L);
        servicePlanDTO.setEffDate("0L");

        new MockUp<CommonDataBaseRepository>() {
            @mockit.Mock
            public ResultSelectEntity getListDataAndCount(StringBuilder queryString, HashMap<String, Object> hmapParams, Integer startPage, Integer pageLoad, Class<?> classOfT) {
                return new ResultSelectEntity();
            }
        };
        // Run the test
        final ResultSelectEntity result = servicePlanRepositoryImplUnderTest.getFeeNew(servicePlanDTO, null,0L,new ArrayList<>(), 0L);
        assertNotNull(result);
    }
}
