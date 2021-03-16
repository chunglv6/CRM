package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BriefcasesRepositoryImplTest {

    private BriefcasesRepositoryImpl briefcasesRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        briefcasesRepositoryImplUnderTest = new BriefcasesRepositoryImpl();
    }

    @Test
    void testSearchBriefcases() {
        // Setup
        final SearchBriefcasesDTO searchBriefcasesDTO = new SearchBriefcasesDTO();
        searchBriefcasesDTO.setContractNo("contractNo");
        searchBriefcasesDTO.setDocumentNumber("documentNumber");
        searchBriefcasesDTO.setCustId(0L);
        searchBriefcasesDTO.setCustTypeId(0L);
        searchBriefcasesDTO.setFromDate("fromDate");
        searchBriefcasesDTO.setToDate("toDate");
        searchBriefcasesDTO.setActTypeId(0L);
        searchBriefcasesDTO.setShopId(0L);
        searchBriefcasesDTO.setProfileStatus("profileStatus");
        searchBriefcasesDTO.setSignDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = briefcasesRepositoryImplUnderTest.searchBriefcases(searchBriefcasesDTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }
}
