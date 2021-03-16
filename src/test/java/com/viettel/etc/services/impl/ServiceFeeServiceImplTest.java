package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.repositories.ServiceFeeRepository;
import com.viettel.etc.repositories.tables.entities.ActReasonEntity;
import com.viettel.etc.repositories.tables.entities.ServiceFeeEntity;
import com.viettel.etc.services.tables.ActReasonServiceJPA;
import com.viettel.etc.services.tables.ServiceFeeServiceJPA;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceFeeServiceImplTest {

    private ServiceFeeServiceImpl serviceFeeServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        serviceFeeServiceImplUnderTest = new ServiceFeeServiceImpl();
        serviceFeeServiceImplUnderTest.serviceFeeServiceJPA = mock(ServiceFeeServiceJPA.class);
        serviceFeeServiceImplUnderTest.serviceFeeRepository = mock(ServiceFeeRepository.class);
        serviceFeeServiceImplUnderTest.actReasonServiceJPA = mock(ActReasonServiceJPA.class);
    }

    @Test
    void testGetServiceFee() {
        // Setup
        final ServiceFeeDTO params = new ServiceFeeDTO(0L, "serviceFeeName", "serviceFeeCode", 0L, 0L, "actTypeName", "actTypeCode", 0L, "actReasonName", "actReasonCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "createUser", 0, false, "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "docRefer", "description", "listId", 0, 0);

        ServiceFeeDTO serviceFeeDTO = new ServiceFeeDTO();
        serviceFeeDTO.setStartDate(new Date(System.currentTimeMillis()));
        serviceFeeDTO.setStatus(1);
        // Configure ServiceFeeRepository.getServiceFee(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList(serviceFeeDTO));
        resultSelectEntity.setCount("count");
        when(serviceFeeServiceImplUnderTest.serviceFeeRepository.getServiceFee(any())).thenReturn(resultSelectEntity);

        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.getServiceFee(params);

        // Verify the results
    }

    @Test
    void testGetDetailServiceFee() {
        // Setup
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.existsById(0L)).thenReturn(false);

        // Configure ServiceFeeServiceJPA.getOne(...).
        final ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setServiceFeeId(0L);
        serviceFeeEntity.setFee(0L);
        serviceFeeEntity.setActTypeId(0L);
        serviceFeeEntity.setActReasonId(0L);
        serviceFeeEntity.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setCreateUser("createUser");
        serviceFeeEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateUser("updateUser");
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getOne(0L)).thenReturn(serviceFeeEntity);

        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.getDetailServiceFee(0L);

        // Verify the results
    }

    @Test
    void testGetDetailServiceFeeByReason() {
        // Setup

        // Configure ServiceFeeServiceJPA.getByActReasonIdAndStartDateBefore(...).
        final ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setServiceFeeId(0L);
        serviceFeeEntity.setFee(0L);
        serviceFeeEntity.setActTypeId(0L);
        serviceFeeEntity.setActReasonId(0L);
        serviceFeeEntity.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setCreateUser("createUser");
        serviceFeeEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateUser("updateUser");
        final List<ServiceFeeEntity> serviceFeeEntities = Arrays.asList(serviceFeeEntity);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByActReasonIdAndStartDateBefore(0L, new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime())).thenReturn(serviceFeeEntities);

        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.getDetailServiceFeeByReason(0L);

        // Verify the results
    }

    @Test
    void testAddServiceFee() {
        // Setup
        ServiceFeeDTO params = new ServiceFeeDTO(0L, "serviceFeeName", "serviceFeeCode", 0L, 0L, "actTypeName", "actTypeCode", 0L, "actReasonName", "actReasonCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2121, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2121, Calendar.JANUARY, 1).getTime(), "createUser", 0, false, "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "docRefer", "description", "listId", 0, 0);
        params.setStartDate(Date.valueOf("2121-01-01"));
        final Authentication authentication = null;

        // Configure ServiceFeeServiceJPA.getByActReasonIdAndEndDateIsNull(...).
        final ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setServiceFeeId(0L);
        serviceFeeEntity.setFee(0L);
        serviceFeeEntity.setActTypeId(0L);
        serviceFeeEntity.setActReasonId(0L);
        serviceFeeEntity.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setCreateUser("createUser");
        serviceFeeEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateUser("updateUser");
        final List<ServiceFeeEntity> serviceFeeEntities = Arrays.asList(serviceFeeEntity);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByActReasonIdAndEndDateIsNull(0L)).thenReturn(serviceFeeEntities);

        // Configure ServiceFeeServiceJPA.getByActReasonIdAndEndDateAfter(...).
        final ServiceFeeEntity serviceFeeEntity1 = new ServiceFeeEntity();
        serviceFeeEntity1.setServiceFeeId(0L);
        serviceFeeEntity1.setFee(0L);
        serviceFeeEntity1.setActTypeId(0L);
        serviceFeeEntity1.setActReasonId(0L);
        serviceFeeEntity1.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setCreateUser("createUser");
        serviceFeeEntity1.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setUpdateUser("updateUser");
        final List<ServiceFeeEntity> serviceFeeEntities1 = Arrays.asList(serviceFeeEntity1);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByActReasonIdAndEndDateAfter(0L, new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime())).thenReturn(serviceFeeEntities1);

        // Configure ServiceFeeServiceJPA.getByActReasonIdAndEndDateBetween(...).
        final ServiceFeeEntity serviceFeeEntity2 = new ServiceFeeEntity();
        serviceFeeEntity2.setServiceFeeId(0L);
        serviceFeeEntity2.setFee(0L);
        serviceFeeEntity2.setActTypeId(0L);
        serviceFeeEntity2.setActReasonId(0L);
        serviceFeeEntity2.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setCreateUser("createUser");
        serviceFeeEntity2.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setUpdateUser("updateUser");
        final List<ServiceFeeEntity> serviceFeeEntities2 = Arrays.asList(serviceFeeEntity2);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByActReasonIdAndEndDateBetween(0L, new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime())).thenReturn(serviceFeeEntities2);

        // Configure ServiceFeeServiceJPA.getByActReasonIdAndStartDateBetween(...).
        final ServiceFeeEntity serviceFeeEntity3 = new ServiceFeeEntity();
        serviceFeeEntity3.setServiceFeeId(0L);
        serviceFeeEntity3.setFee(0L);
        serviceFeeEntity3.setActTypeId(0L);
        serviceFeeEntity3.setActReasonId(0L);
        serviceFeeEntity3.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity3.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity3.setCreateUser("createUser");
        serviceFeeEntity3.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity3.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity3.setUpdateUser("updateUser");
        final List<ServiceFeeEntity> serviceFeeEntities3 = Arrays.asList(serviceFeeEntity3);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByActReasonIdAndStartDateBetween(0L, new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime())).thenReturn(serviceFeeEntities3);

        when(serviceFeeServiceImplUnderTest.actReasonServiceJPA.existsById(0L)).thenReturn(false);

        // Configure ActReasonServiceJPA.getOne(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        when(serviceFeeServiceImplUnderTest.actReasonServiceJPA.getOne(0L)).thenReturn(actReasonEntity);

        // Configure ServiceFeeServiceJPA.save(...).
        final ServiceFeeEntity serviceFeeEntity4 = new ServiceFeeEntity();
        serviceFeeEntity4.setServiceFeeId(0L);
        serviceFeeEntity4.setFee(0L);
        serviceFeeEntity4.setActTypeId(0L);
        serviceFeeEntity4.setActReasonId(0L);
        serviceFeeEntity4.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity4.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity4.setCreateUser("createUser");
        serviceFeeEntity4.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity4.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity4.setUpdateUser("updateUser");
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.save(new ServiceFeeEntity())).thenReturn(serviceFeeEntity4);

        new MockUp<ServiceFeeDTO>() {
            @mockit.Mock
            public ServiceFeeDTO toResponse(ServiceFeeEntity entity) {
                ServiceFeeDTO serviceFeeDTO = new ServiceFeeDTO();
                serviceFeeDTO.setStatus(1);
                return serviceFeeDTO;
            }
        };
        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.addServiceFee(params, authentication);

        assertNotNull(result);
        // Verify the results
//        assertThrows(EtcException.class, () ->serviceFeeServiceImplUnderTest.addServiceFee(params, authentication));
    }

    @Test
    void testEditServiceFee() {
        // Setup
        ServiceFeeDTO params = new ServiceFeeDTO(0L, "serviceFeeName", "serviceFeeCode", 0L, 0L, "actTypeName", "actTypeCode", 0L, "actReasonName", "actReasonCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "createUser", 0, false, "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "docRefer", "description", "listId", 0, 0);
        params.setEndDate(Date.valueOf("2121-01-01"));
        final Authentication authentication = null;
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.existsById(0L)).thenReturn(true);

        // Configure ServiceFeeServiceJPA.getOne(...).
        final ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setServiceFeeId(0L);
        serviceFeeEntity.setFee(0L);
        serviceFeeEntity.setActTypeId(0L);
        serviceFeeEntity.setActReasonId(0L);
        serviceFeeEntity.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setCreateUser("createUser");
        serviceFeeEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateUser("updateUser");
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getOne(0L)).thenReturn(serviceFeeEntity);

        // Configure ServiceFeeServiceJPA.getByActReasonIdAndStartDateBetween(...).
        final ServiceFeeEntity serviceFeeEntity1 = new ServiceFeeEntity();
        serviceFeeEntity1.setServiceFeeId(0L);
        serviceFeeEntity1.setFee(0L);
        serviceFeeEntity1.setActTypeId(0L);
        serviceFeeEntity1.setActReasonId(0L);
        serviceFeeEntity1.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setCreateUser("createUser");
        serviceFeeEntity1.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setUpdateUser("updateUser");
        final List<ServiceFeeEntity> serviceFeeEntities = Arrays.asList(serviceFeeEntity1);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByActReasonIdAndStartDateBetween(0L, new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime())).thenReturn(serviceFeeEntities);

        when(serviceFeeServiceImplUnderTest.actReasonServiceJPA.existsById(0L)).thenReturn(false);

        // Configure ActReasonServiceJPA.getOne(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        when(serviceFeeServiceImplUnderTest.actReasonServiceJPA.getOne(0L)).thenReturn(actReasonEntity);

        // Configure ServiceFeeServiceJPA.save(...).
        final ServiceFeeEntity serviceFeeEntity2 = new ServiceFeeEntity();
        serviceFeeEntity2.setServiceFeeId(0L);
        serviceFeeEntity2.setFee(0L);
        serviceFeeEntity2.setActTypeId(0L);
        serviceFeeEntity2.setActReasonId(0L);
        serviceFeeEntity2.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setCreateUser("createUser");
        serviceFeeEntity2.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity2.setUpdateUser("updateUser");
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.save(new ServiceFeeEntity())).thenReturn(serviceFeeEntity2);

        new MockUp<ServiceFeeDTO>() {
            @mockit.Mock
            public ServiceFeeDTO toResponse(ServiceFeeEntity entity) {
                ServiceFeeDTO serviceFeeDTO = new ServiceFeeDTO();
                serviceFeeDTO.setStatus(1);
                return serviceFeeDTO;
            }
        };
        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.editServiceFee(params, authentication, 0L);

        // Verify the results
    }

    @Ignore
    void testDeleteServiceFee() {
        // Setup
        final Authentication authentication = null;
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.existsById(0L)).thenReturn(false);

        // Configure ServiceFeeServiceJPA.getOne(...).
        final ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setServiceFeeId(0L);
        serviceFeeEntity.setFee(0L);
        serviceFeeEntity.setActTypeId(0L);
        serviceFeeEntity.setActReasonId(0L);
        serviceFeeEntity.setStartDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setEndDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setCreateUser("createUser");
        serviceFeeEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateUser("updateUser");
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getOne(0L)).thenReturn(serviceFeeEntity);

        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.deleteServiceFee(authentication, 0L);

        // Verify the results
        verify(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA).deleteById(0L);
    }

    @Test
    void testApproveServiceFee() {
        // Setup
        final Authentication authentication = null;
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.existsById(any())).thenReturn(true);

        // Configure ServiceFeeServiceJPA.getByIdIn(...).
        final ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setServiceFeeId(0L);
        serviceFeeEntity.setFee(0L);
        serviceFeeEntity.setActTypeId(0L);
        serviceFeeEntity.setActReasonId(0L);
        serviceFeeEntity.setStartDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setEndDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setCreateUser("createUser");
        serviceFeeEntity.setCreateDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity.setUpdateUser("updateUser");
        serviceFeeEntity.setStatus(1);
        final List<ServiceFeeEntity> serviceFeeEntities = Arrays.asList(serviceFeeEntity);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.getByIdIn(anyList())).thenReturn(serviceFeeEntities);

        // Configure ServiceFeeServiceJPA.saveAll(...).
        final ServiceFeeEntity serviceFeeEntity1 = new ServiceFeeEntity();
        serviceFeeEntity1.setServiceFeeId(0L);
        serviceFeeEntity1.setFee(0L);
        serviceFeeEntity1.setActTypeId(0L);
        serviceFeeEntity1.setActReasonId(0L);
        serviceFeeEntity1.setStartDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setEndDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setCreateUser("createUser");
        serviceFeeEntity1.setCreateDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setUpdateDate(new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime());
        serviceFeeEntity1.setUpdateUser("updateUser");
        serviceFeeEntity1.setStatus(1);
        final List<ServiceFeeEntity> serviceFeeEntities1 = Arrays.asList(serviceFeeEntity1);
        when(serviceFeeServiceImplUnderTest.serviceFeeServiceJPA.saveAll(Arrays.asList(new ServiceFeeEntity()))).thenReturn(serviceFeeEntities1);

        // Run the test
        final Object result = serviceFeeServiceImplUnderTest.approveServiceFee("1,2,3", authentication);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testExportServiceFee() {
        // Setup
        final ServiceFeeDTO params = new ServiceFeeDTO(0L, "serviceFeeName", "serviceFeeCode", 0L, 0L, "actTypeName", "actTypeCode", 0L, "actReasonName", "actReasonCode", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "createUser", 0, false, "approvedUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), 0L, 0L, "docRefer", "description", "listId", 0, 0);

        // Configure ServiceFeeRepository.getServiceFee(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList(params));
        resultSelectEntity.setCount("count");
        when(serviceFeeServiceImplUnderTest.serviceFeeRepository.getServiceFee(any())).thenReturn(resultSelectEntity);

        // Run the test
        final String result = serviceFeeServiceImplUnderTest.exportServiceFee(params);

        // Verify the results
    }
}
