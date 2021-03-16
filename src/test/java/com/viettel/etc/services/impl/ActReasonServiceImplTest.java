package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActReasonDTO;
import com.viettel.etc.repositories.ActReasonRepository;
import com.viettel.etc.services.tables.ActReasonServiceJPA;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ActReasonServiceImplTest {

    @Mock
    private ActReasonRepository mockActReasonRepository;

    @InjectMocks
    private ActReasonServiceImpl actReasonServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        // Configure ActReasonRepository.find(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(mockActReasonRepository.find(new ActReasonDTO())).thenReturn(resultSelectEntity);

        // Run the test
        final Object result = actReasonServiceImplUnderTest.find(itemParamsEntity);

        // Verify the results
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
        when(mockActReasonRepository.insert(new ActReasonDTO(), null)).thenReturn("result");

        // Run the test
        final Object result = actReasonServiceImplUnderTest.insert(params, authentication);

        // Verify the results
    }

    @Test
    void testInsert_ActReasonRepositoryThrowsException() throws Exception {
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
        when(mockActReasonRepository.insert(any(), any())).thenThrow(Exception.class);

        // Run the test
        assertThrows(Exception.class, () -> actReasonServiceImplUnderTest.insert(params, authentication));
    }

    @Test
    void testUpdate() throws Exception {
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
        when(mockActReasonRepository.update(new ActReasonDTO(), null)).thenReturn("result");

        // Run the test
        final Object result = actReasonServiceImplUnderTest.update(params, authentication);

        // Verify the results
    }

    @Test
    void testUpdate_ActReasonRepositoryThrowsException() throws Exception {
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
        when(mockActReasonRepository.update(any(), any())).thenThrow(Exception.class);

        // Run the test
        assertThrows(Exception.class, () -> actReasonServiceImplUnderTest.update(params, authentication));
    }

    @Test
    void testFindOne() {
        // Setup

        // Configure ActReasonRepository.findOne(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(mockActReasonRepository.findOne(0L)).thenReturn(resultSelectEntity);

        // Run the test
        final Object result = actReasonServiceImplUnderTest.findOne(0L);

        // Verify the results
    }
}
