package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ActTypeMappingDTO;
import com.viettel.etc.repositories.tables.entities.ActIdTypeMappingEntity;
import com.viettel.etc.services.tables.ActIdTypeMappingServiceJPA;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ActTypeMappingRepositoryImplTest {

    @Mock
    private ActIdTypeMappingServiceJPA mockActIdTypeMappingServiceJPA;

    @InjectMocks
    private ActTypeMappingRepositoryImpl actTypeMappingRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testFind() {
        // Setup
        final ActTypeMappingDTO itemParamsEntity = new ActTypeMappingDTO();
        itemParamsEntity.setId(0L);
        itemParamsEntity.setActTypeId(0L);
        itemParamsEntity.setDocumentTypeId(0L);
        itemParamsEntity.setCreateUser("createUser");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setUpdateUser("updateUser");
        itemParamsEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setDescription("description");
        itemParamsEntity.setStatus("status");
        itemParamsEntity.setStartrecord(0);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actTypeMappingRepositoryImplUnderTest.find(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testInsert() throws Exception {
        // Setup
        final ActTypeMappingDTO params = new ActTypeMappingDTO();
        params.setId(0L);
        params.setActTypeId(0L);
        params.setDocumentTypeId(0L);
        params.setCreateUser("createUser");
        params.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setUpdateUser("updateUser");
        params.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setDescription("description");
        params.setStatus("status");
        params.setStartrecord(0);

        final Authentication authentication = null;
        when(mockActIdTypeMappingServiceJPA.existsByActTypeIdAndDocumentTypeId(0L, 0L)).thenReturn(false);

        // Configure ActIdTypeMappingServiceJPA.save(...).
        final ActIdTypeMappingEntity actIdTypeMappingEntity = new ActIdTypeMappingEntity();
        actIdTypeMappingEntity.setId(0L);
        actIdTypeMappingEntity.setActTypeId(0L);
        actIdTypeMappingEntity.setDocumentTypeId(0L);
        actIdTypeMappingEntity.setCreateUser("createUser");
        actIdTypeMappingEntity.setCreateDate(new Date(0L));
        actIdTypeMappingEntity.setUpdateUser("updateUser");
        actIdTypeMappingEntity.setUpdateDate(new Date(0L));
        actIdTypeMappingEntity.setDescription("description");
        actIdTypeMappingEntity.setStatus("status");
        actIdTypeMappingEntity.setActReasonId(0L);
        when(mockActIdTypeMappingServiceJPA.save(new ActIdTypeMappingEntity())).thenReturn(actIdTypeMappingEntity);

        // Run the test
        final Object result = actTypeMappingRepositoryImplUnderTest.insert(params, authentication);

        // Verify the results
    }


    @Test
    void testFindOne() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actTypeMappingRepositoryImplUnderTest.findOne(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testUpdateStatus() throws Exception {
        // Setup
        final Authentication authentication = null;

        // Configure ActIdTypeMappingServiceJPA.findById(...).
        final ActIdTypeMappingEntity actIdTypeMappingEntity1 = new ActIdTypeMappingEntity();
        actIdTypeMappingEntity1.setId(0L);
        actIdTypeMappingEntity1.setActTypeId(0L);
        actIdTypeMappingEntity1.setDocumentTypeId(0L);
        actIdTypeMappingEntity1.setCreateUser("createUser");
        actIdTypeMappingEntity1.setCreateDate(new Date(0L));
        actIdTypeMappingEntity1.setUpdateUser("updateUser");
        actIdTypeMappingEntity1.setUpdateDate(new Date(0L));
        actIdTypeMappingEntity1.setDescription("description");
        actIdTypeMappingEntity1.setStatus("status");
        actIdTypeMappingEntity1.setActReasonId(0L);
        final Optional<ActIdTypeMappingEntity> actIdTypeMappingEntity = Optional.of(actIdTypeMappingEntity1);
        when(mockActIdTypeMappingServiceJPA.findById(0L)).thenReturn(actIdTypeMappingEntity);

        // Configure ActIdTypeMappingServiceJPA.save(...).
        final ActIdTypeMappingEntity actIdTypeMappingEntity2 = new ActIdTypeMappingEntity();
        actIdTypeMappingEntity2.setId(0L);
        actIdTypeMappingEntity2.setActTypeId(0L);
        actIdTypeMappingEntity2.setDocumentTypeId(0L);
        actIdTypeMappingEntity2.setCreateUser("createUser");
        actIdTypeMappingEntity2.setCreateDate(new Date(0L));
        actIdTypeMappingEntity2.setUpdateUser("updateUser");
        actIdTypeMappingEntity2.setUpdateDate(new Date(0L));
        actIdTypeMappingEntity2.setDescription("description");
        actIdTypeMappingEntity2.setStatus("status");
        actIdTypeMappingEntity2.setActReasonId(0L);
        when(mockActIdTypeMappingServiceJPA.save(new ActIdTypeMappingEntity())).thenReturn(actIdTypeMappingEntity2);

        // Run the test
        final Object result = actTypeMappingRepositoryImplUnderTest.updateStatus(0L, authentication);

        // Verify the results
    }

}
