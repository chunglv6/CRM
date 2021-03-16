package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.CustTypeMappingDTO;
import com.viettel.etc.repositories.tables.entities.CustIdTypeMappingEntity;
import com.viettel.etc.services.tables.CustIdTypeMappingServiceJPA;
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

class CustTypeMappingRepositoryImplTest {

    @Mock
    private CustIdTypeMappingServiceJPA mockCustIdTypeMappingServiceJPA;

    @InjectMocks
    private CustTypeMappingRepositoryImpl custTypeMappingRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testFind() {
        // Setup
        final CustTypeMappingDTO itemParamsEntity = new CustTypeMappingDTO();
        itemParamsEntity.setId(0L);
        itemParamsEntity.setCustTypeId(0L);
        itemParamsEntity.setDocumentTypeId(0L);
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setDescription("description");
        itemParamsEntity.setStatus("status");
        itemParamsEntity.setCreateUser("createUser");
        itemParamsEntity.setUpdateUser("updateUser");
        itemParamsEntity.setStartrecord(0);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = custTypeMappingRepositoryImplUnderTest.find(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testInsert() throws Exception {
        // Setup
        final CustTypeMappingDTO params = new CustTypeMappingDTO();
        params.setId(0L);
        params.setCustTypeId(0L);
        params.setDocumentTypeId(0L);
        params.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setUpdateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        params.setDescription("description");
        params.setStatus("status");
        params.setCreateUser("createUser");
        params.setUpdateUser("updateUser");
        params.setStartrecord(0);

        final Authentication authentication = null;
        when(mockCustIdTypeMappingServiceJPA.existsByCustTypeIdAndDocumentTypeId(0L, 0L)).thenReturn(false);

        // Configure CustIdTypeMappingServiceJPA.save(...).
        final CustIdTypeMappingEntity custIdTypeMappingEntity = new CustIdTypeMappingEntity();
        custIdTypeMappingEntity.setId(0L);
        custIdTypeMappingEntity.setCustTypeId(0L);
        custIdTypeMappingEntity.setDocumentTypeId(0L);
        custIdTypeMappingEntity.setCreateDate(new Date(0L));
        custIdTypeMappingEntity.setUpdateDate(new Date(0L));
        custIdTypeMappingEntity.setDescription("description");
        custIdTypeMappingEntity.setStatus("status");
        custIdTypeMappingEntity.setCreateUser("createUser");
        custIdTypeMappingEntity.setUpdateUser("updateUser");
        when(mockCustIdTypeMappingServiceJPA.save(new CustIdTypeMappingEntity())).thenReturn(custIdTypeMappingEntity);

        // Run the test
        final Object result = custTypeMappingRepositoryImplUnderTest.insert(params, authentication);

        // Verify the results
    }

    @Test
    void testFindOne() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = custTypeMappingRepositoryImplUnderTest.findOne(0L);
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

        // Configure CustIdTypeMappingServiceJPA.findById(...).
        final CustIdTypeMappingEntity custIdTypeMappingEntity1 = new CustIdTypeMappingEntity();
        custIdTypeMappingEntity1.setId(0L);
        custIdTypeMappingEntity1.setCustTypeId(0L);
        custIdTypeMappingEntity1.setDocumentTypeId(0L);
        custIdTypeMappingEntity1.setCreateDate(new Date(0L));
        custIdTypeMappingEntity1.setUpdateDate(new Date(0L));
        custIdTypeMappingEntity1.setDescription("description");
        custIdTypeMappingEntity1.setStatus("status");
        custIdTypeMappingEntity1.setCreateUser("createUser");
        custIdTypeMappingEntity1.setUpdateUser("updateUser");
        final Optional<CustIdTypeMappingEntity> custIdTypeMappingEntity = Optional.of(custIdTypeMappingEntity1);
        when(mockCustIdTypeMappingServiceJPA.findById(0L)).thenReturn(custIdTypeMappingEntity);

        // Configure CustIdTypeMappingServiceJPA.save(...).
        final CustIdTypeMappingEntity custIdTypeMappingEntity2 = new CustIdTypeMappingEntity();
        custIdTypeMappingEntity2.setId(0L);
        custIdTypeMappingEntity2.setCustTypeId(0L);
        custIdTypeMappingEntity2.setDocumentTypeId(0L);
        custIdTypeMappingEntity2.setCreateDate(new Date(0L));
        custIdTypeMappingEntity2.setUpdateDate(new Date(0L));
        custIdTypeMappingEntity2.setDescription("description");
        custIdTypeMappingEntity2.setStatus("status");
        custIdTypeMappingEntity2.setCreateUser("createUser");
        custIdTypeMappingEntity2.setUpdateUser("updateUser");
        when(mockCustIdTypeMappingServiceJPA.save(new CustIdTypeMappingEntity())).thenReturn(custIdTypeMappingEntity2);

        // Run the test
        final Object result = custTypeMappingRepositoryImplUnderTest.updateStatus(0L, authentication);

        // Verify the results
    }

}
