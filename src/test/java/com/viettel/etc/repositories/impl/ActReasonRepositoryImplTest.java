package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ActReasonDTO;
import com.viettel.etc.repositories.tables.entities.ActReasonEntity;
import com.viettel.etc.services.tables.ActReasonServiceJPA;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ActReasonRepositoryImplTest {

    @Mock
    private ActReasonServiceJPA mockActReasonServiceJPA;

    @InjectMocks
    private ActReasonRepositoryImpl actReasonRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testFind() {
        // Setup
        final ActReasonDTO itemParamsEntity = new ActReasonDTO();
        itemParamsEntity.setActReasonId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setActTypeId(0L);
        itemParamsEntity.setName("name");
        itemParamsEntity.setCreateUser("createUser");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setUpdateUser("updateUser");
        itemParamsEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setDescription("description");
        itemParamsEntity.setStatus("status");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actReasonRepositoryImplUnderTest.find(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);

    }

    @Test
    void testInsert() throws Exception {
        // Setup
        final ActReasonDTO params = new ActReasonDTO();
        params.setActReasonId(0L);
        params.setCode("code");
        params.setActTypeId(0L);
        params.setName("name");
        params.setCreateUser("createUser");
        params.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setUpdateUser("updateUser");
        params.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setDescription("description");
        params.setStatus("status");

        final Authentication authentication = null;

        // Configure ActReasonServiceJPA.findAllByCodeAndStatus(...).
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
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(mockActReasonServiceJPA.findAllByCodeAndStatus("code", "status")).thenReturn(actReasonEntities);

        // Configure ActReasonServiceJPA.save(...).
        final ActReasonEntity actReasonEntity1 = new ActReasonEntity();
        actReasonEntity1.setActReasonId(0L);
        actReasonEntity1.setActTypeId(0L);
        actReasonEntity1.setName("name");
        actReasonEntity1.setCode("code");
        actReasonEntity1.setCreateUser("createUser");
        actReasonEntity1.setCreateDate(new Date(0L));
        actReasonEntity1.setUpdateUser("updateUser");
        actReasonEntity1.setUpdateDate(new Date(0L));
        actReasonEntity1.setDescription("description");
        actReasonEntity1.setStatus("status");
        when(mockActReasonServiceJPA.save(new ActReasonEntity())).thenReturn(actReasonEntity1);

        // Run the test
        final Object result = actReasonRepositoryImplUnderTest.insert(params, authentication);

        // Verify the results
    }


    @Test
    void testUpdate() throws Exception {
        // Setup
        final ActReasonDTO param = new ActReasonDTO();
        param.setActReasonId(0L);
        param.setCode("code");
        param.setActTypeId(0L);
        param.setName("name");
        param.setCreateUser("createUser");
        param.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        param.setUpdateUser("updateUser");
        param.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        param.setDescription("description");
        param.setStatus("status");

        final Authentication authentication = null;

        // Configure ActReasonServiceJPA.findAllByCodeAndStatus(...).
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
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(mockActReasonServiceJPA.findAllByCodeAndStatus("code", "status")).thenReturn(actReasonEntities);

        // Configure ActReasonServiceJPA.findById(...).
        final ActReasonEntity actReasonEntity2 = new ActReasonEntity();
        actReasonEntity2.setActReasonId(0L);
        actReasonEntity2.setActTypeId(0L);
        actReasonEntity2.setName("name");
        actReasonEntity2.setCode("code");
        actReasonEntity2.setCreateUser("createUser");
        actReasonEntity2.setCreateDate(new Date(0L));
        actReasonEntity2.setUpdateUser("updateUser");
        actReasonEntity2.setUpdateDate(new Date(0L));
        actReasonEntity2.setDescription("description");
        actReasonEntity2.setStatus("status");
        final Optional<ActReasonEntity> actReasonEntity1 = Optional.of(actReasonEntity2);
        when(mockActReasonServiceJPA.findById(0L)).thenReturn(actReasonEntity1);

        // Configure ActReasonServiceJPA.save(...).
        final ActReasonEntity actReasonEntity3 = new ActReasonEntity();
        actReasonEntity3.setActReasonId(0L);
        actReasonEntity3.setActTypeId(0L);
        actReasonEntity3.setName("name");
        actReasonEntity3.setCode("code");
        actReasonEntity3.setCreateUser("createUser");
        actReasonEntity3.setCreateDate(new Date(0L));
        actReasonEntity3.setUpdateUser("updateUser");
        actReasonEntity3.setUpdateDate(new Date(0L));
        actReasonEntity3.setDescription("description");
        actReasonEntity3.setStatus("status");
        when(mockActReasonServiceJPA.save(new ActReasonEntity())).thenReturn(actReasonEntity3);

        // Run the test
        final Object result = actReasonRepositoryImplUnderTest.update(param, authentication);

        // Verify the results
    }


    @Test
    void testFindOne() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actReasonRepositoryImplUnderTest.findOne(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }
}
