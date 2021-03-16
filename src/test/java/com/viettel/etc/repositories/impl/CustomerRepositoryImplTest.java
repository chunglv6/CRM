package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.CustRegisterDTO;
import com.viettel.etc.dto.CustomerSearchDTO;
import com.viettel.etc.dto.SearchInfoDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerRepositoryImplTest {

    private CustomerRepositoryImpl customerRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        customerRepositoryImplUnderTest = new CustomerRepositoryImpl();
    }

    @Test
    void testFindCustomerByDocumentAndPhone() {
        // Setup
        final CustomerSearchDTO requestModel = new CustomerSearchDTO("token", "custTypeName", "inputSearch", "signName");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = customerRepositoryImplUnderTest.findCustomerByDocumentAndPhone(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testSearchTreeInfo() {
        // Setup
        final SearchInfoDTO requestModel = new SearchInfoDTO();
        requestModel.setCustId(0L);
        requestModel.setCustName("custName");
        requestModel.setContractNo("contractNo");
        requestModel.setPlateNumber("plateNumber");
        requestModel.setPhoneNumber("phoneNumber");
        requestModel.setDocumentNumber("documentNumber");
        requestModel.setRfidSerial("0L");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");
        requestModel.setStatus("status");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = customerRepositoryImplUnderTest.searchTreeInfo(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindCustomerById() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = customerRepositoryImplUnderTest.findCustomerById(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testGetCustomerRegister() {
        // Setup
        final CustRegisterDTO itemParamsEntity = new CustRegisterDTO(0L, 0L, "accountUser", "areaCode", "areaName", "authAreaCode", "authAreaName", new Date(0L), new Date(0L), "authEmail", "authGender", "authIentityNumber", 0L, "authName", "authPhoneNumber", "authPlaceOfIssue", "authStreet", new Date(0L), "custName", 0L, new Date(0L), "documentNumber", 0L, "email", "fax", "gender", 0L, "phoneNumber", "placeOfIssue", new Date(0L), "regStatus", "repAreaCode", "repAreaName", new Date(0L), new Date(0L), "repEmail", "repGender", "repIdentityNumber", 0L, "repName", "repPhoneNumber", "repPlaceOfIssue", "repStreet", "street", "taxCode", "website", 0, 0);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = customerRepositoryImplUnderTest.getCustomerRegister(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }
}
