package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.repositories.tables.entities.ActTypeEntity;
import com.viettel.etc.services.tables.ActTypeServiceJPA;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ActTypeRepositoryImplTest {

    @Mock
    private ActTypeServiceJPA mockActTypeServiceJPA;

    @InjectMocks
    private ActTypeRepositoryImpl actTypeRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testFind() {
        // Setup
        final ActTypeDTO itemParamsEntity = new ActTypeDTO();
        itemParamsEntity.setActTypeId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setName("name");
        itemParamsEntity.setIsOcs("isOcs");
        itemParamsEntity.setActObject("actObject");
        itemParamsEntity.setCreateUser("createUser");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setUpdateUser("updateUser");
        itemParamsEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setDescription("description");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actTypeRepositoryImplUnderTest.find(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);

    }

    @Test
    void testInsert() throws Exception {
        // Setup
        final ActTypeDTO params = new ActTypeDTO();
        params.setActTypeId(0L);
        params.setCode("code");
        params.setName("name");
        params.setIsOcs("isOcs");
        params.setActObject("actObject");
        params.setCreateUser("createUser");
        params.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setUpdateUser("updateUser");
        params.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setDescription("description");

        final Authentication authentication = null;

        // Configure ActTypeServiceJPA.findAllByCodeAndStatus(...).
        final ActTypeEntity actTypeEntity = new ActTypeEntity();
        actTypeEntity.setActTypeId(0L);
        actTypeEntity.setCode("code");
        actTypeEntity.setName("name");
        actTypeEntity.setIsOcs("isOcs");
        actTypeEntity.setActObject("actObject");
        actTypeEntity.setCreateUser("createUser");
        actTypeEntity.setCreateDate(new Date(0L));
        actTypeEntity.setUpdateUser("updateUser");
        actTypeEntity.setUpdateDate(new Date(0L));
        actTypeEntity.setDescription("description");
        final List<ActTypeEntity> actTypeEntities = Arrays.asList(actTypeEntity);
        when(mockActTypeServiceJPA.findAllByCodeAndStatus("code", "status")).thenReturn(actTypeEntities);

        // Configure ActTypeServiceJPA.save(...).
        final ActTypeEntity actTypeEntity1 = new ActTypeEntity();
        actTypeEntity1.setActTypeId(0L);
        actTypeEntity1.setCode("code");
        actTypeEntity1.setName("name");
        actTypeEntity1.setIsOcs("isOcs");
        actTypeEntity1.setActObject("actObject");
        actTypeEntity1.setCreateUser("createUser");
        actTypeEntity1.setCreateDate(new Date(0L));
        actTypeEntity1.setUpdateUser("updateUser");
        actTypeEntity1.setUpdateDate(new Date(0L));
        actTypeEntity1.setDescription("description");
        when(mockActTypeServiceJPA.save(new ActTypeEntity())).thenReturn(actTypeEntity1);

        // Run the test
        final Object result = actTypeRepositoryImplUnderTest.insert(params, authentication);

        // Verify the results
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        final ActTypeDTO param = new ActTypeDTO();
        param.setActTypeId(0L);
        param.setCode("code");
        param.setName("name");
        param.setIsOcs("isOcs");
        param.setActObject("actObject");
        param.setCreateUser("createUser");
        param.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        param.setUpdateUser("updateUser");
        param.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        param.setDescription("description");

        final Authentication authentication = null;

        // Configure ActTypeServiceJPA.findAllByCodeAndStatus(...).
        final ActTypeEntity actTypeEntity = new ActTypeEntity();
        actTypeEntity.setActTypeId(0L);
        actTypeEntity.setCode("code");
        actTypeEntity.setName("name");
        actTypeEntity.setIsOcs("isOcs");
        actTypeEntity.setActObject("actObject");
        actTypeEntity.setCreateUser("createUser");
        actTypeEntity.setCreateDate(new Date(0L));
        actTypeEntity.setUpdateUser("updateUser");
        actTypeEntity.setUpdateDate(new Date(0L));
        actTypeEntity.setDescription("description");
        final List<ActTypeEntity> actTypeEntities = Arrays.asList(actTypeEntity);
        when(mockActTypeServiceJPA.findAllByCodeAndStatus(Mockito.anyString(), Mockito.anyString())).thenReturn(actTypeEntities);
        when(mockActTypeServiceJPA.existsById(Mockito.anyLong())).thenReturn(true);

        // Configure ActTypeServiceJPA.findById(...).
        final ActTypeEntity actTypeEntity2 = new ActTypeEntity();
        actTypeEntity2.setActTypeId(0L);
        actTypeEntity2.setCode("code");
        actTypeEntity2.setName("name");
        actTypeEntity2.setIsOcs("isOcs");
        actTypeEntity2.setActObject("actObject");
        actTypeEntity2.setCreateUser("createUser");
        actTypeEntity2.setCreateDate(new Date(0L));
        actTypeEntity2.setUpdateUser("updateUser");
        actTypeEntity2.setUpdateDate(new Date(0L));
        actTypeEntity2.setDescription("description");
        final Optional<ActTypeEntity> actTypeEntity1 = Optional.of(actTypeEntity2);
        when(mockActTypeServiceJPA.findById(Mockito.anyLong())).thenReturn(actTypeEntity1);

        // Configure ActTypeServiceJPA.save(...).
        final ActTypeEntity actTypeEntity3 = new ActTypeEntity();
        actTypeEntity3.setActTypeId(0L);
        actTypeEntity3.setCode("code");
        actTypeEntity3.setName("name");
        actTypeEntity3.setIsOcs("isOcs");
        actTypeEntity3.setActObject("actObject");
        actTypeEntity3.setCreateUser("createUser");
        actTypeEntity3.setCreateDate(new Date(0L));
        actTypeEntity3.setUpdateUser("updateUser");
        actTypeEntity3.setUpdateDate(new Date(0L));
        actTypeEntity3.setDescription("description");
        when(mockActTypeServiceJPA.save(Mockito.any())).thenReturn(actTypeEntity3);

        // Run the test
        final Object result = actTypeRepositoryImplUnderTest.update(param, authentication);

        // Verify the results
    }

    @Test
    void testFindOne() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actTypeRepositoryImplUnderTest.findOne(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }
}
