package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.DocumentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentRepositoryImplTest {

    private DocumentRepositoryImpl documentRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        documentRepositoryImplUnderTest = new DocumentRepositoryImpl();
    }

    @Test
    void testGetDocumentType() {
        // Setup
        final DocumentDTO itemParamsEntity = new DocumentDTO();
        itemParamsEntity.setId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setVal("val");
        itemParamsEntity.setName("name");
        itemParamsEntity.setCustTypeId("custTypeId");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setResultSqlEx(false);
        itemParamsEntity.setType("type");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());

        final DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(0L);
        documentDTO.setCode("code");
        documentDTO.setVal("val");
        documentDTO.setName("name");
        documentDTO.setCustTypeId("custTypeId");
        documentDTO.setStartrecord(0);
        documentDTO.setPagesize(0);
        documentDTO.setResultSqlEx(false);
        documentDTO.setType("type");
        documentDTO.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<DocumentDTO> expectedResult = Arrays.asList(documentDTO);

        // Run the test
        final List<DocumentDTO> result = documentRepositoryImplUnderTest.getDocumentType(itemParamsEntity);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetDocumentByCustTypeId() {
        // Setup
        final DocumentDTO itemParamsEntity = new DocumentDTO();
        itemParamsEntity.setId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setVal("val");
        itemParamsEntity.setName("name");
        itemParamsEntity.setCustTypeId("custTypeId");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setResultSqlEx(false);
        itemParamsEntity.setType("type");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());

        final DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(0L);
        documentDTO.setCode("code");
        documentDTO.setVal("val");
        documentDTO.setName("name");
        documentDTO.setCustTypeId("custTypeId");
        documentDTO.setStartrecord(0);
        documentDTO.setPagesize(0);
        documentDTO.setResultSqlEx(false);
        documentDTO.setType("type");
        documentDTO.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<DocumentDTO> expectedResult = Arrays.asList(documentDTO);

        // Run the test
        final List<DocumentDTO> result = documentRepositoryImplUnderTest.getDocumentByCustTypeId(itemParamsEntity, 0L);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetDocumentTypes() {
        // Setup
        final DocumentDTO itemParamsEntity = new DocumentDTO();
        itemParamsEntity.setId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setVal("val");
        itemParamsEntity.setName("name");
        itemParamsEntity.setCustTypeId("custTypeId");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setResultSqlEx(false);
        itemParamsEntity.setType("type");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());

        final DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(0L);
        documentDTO.setCode("code");
        documentDTO.setVal("val");
        documentDTO.setName("name");
        documentDTO.setCustTypeId("custTypeId");
        documentDTO.setStartrecord(0);
        documentDTO.setPagesize(0);
        documentDTO.setResultSqlEx(false);
        documentDTO.setType("type");
        documentDTO.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<DocumentDTO> expectedResult = Arrays.asList(documentDTO);

        // Run the test
        final List<DocumentDTO> result = documentRepositoryImplUnderTest.getDocumentTypes(itemParamsEntity);

        // Verify the results
        assertNull(result);
    }
}
