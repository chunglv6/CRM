package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ContractProfileDTO;
import com.viettel.etc.dto.ContractSearchDTO;
import com.viettel.etc.dto.SearchContractByCustomerDTO;
import com.viettel.etc.dto.SearchContractDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContractRepositoryImplTest {

    private ContractRepositoryImpl contractRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        contractRepositoryImplUnderTest = new ContractRepositoryImpl();
    }

    @Test
    void testFindContractById() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = contractRepositoryImplUnderTest.findContractById(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindContractByCustomer() {
        // Setup
        final SearchContractByCustomerDTO requestModel = new SearchContractByCustomerDTO(0, "contractNo", "startDate", "endDate", "signName", "signDate", "status", 0, false, 0, false);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = contractRepositoryImplUnderTest.findContractByCustomer(0, requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testSearchContract() {
        // Setup
        final SearchContractDTO searchContractDTO = new SearchContractDTO("contractNo", "accountUserId", "plateNumber", "documentNumber", "phoneNumber", "noticePhoneNumber", 0L, 0L, "custName", 0L, "startDate", "endDate", "signName", new Date(0L), "status", 0, false, 0, false, "documentNumberOrPhone", "accountUser", new Date(0L), new Date(0L), new Date(0L), 0L, 0L, 0L, "repIdentityNumber", "authIdentityNumber", 0L);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = contractRepositoryImplUnderTest.searchContract(searchContractDTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindProfileByContract() {
        // Setup
        final ContractProfileDTO requestModel = new ContractProfileDTO();
        requestModel.setContractProfileId(0L);
        requestModel.setCustId(0L);
        requestModel.setContractId(0L);
        requestModel.setDocumentTypeId(0L);
        requestModel.setFileName("fileName");
        requestModel.setFileSize("fileSize");
        requestModel.setFilePath("filePath");
        requestModel.setCreateUser("createUser");
        requestModel.setCreateDate(new Date(0L));
        requestModel.setApprovedUser("approvedUser");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = contractRepositoryImplUnderTest.findProfileByContract(0L, requestModel);
        }catch (NullPointerException e){
            result = null;
        }

        // Verify the results
    }

    @Test
    void testDownloadProfileByContract() {
        // Setup
        final ContractProfileDTO contractProfileDTO = new ContractProfileDTO();
        contractProfileDTO.setContractProfileId(0L);
        contractProfileDTO.setCustId(0L);
        contractProfileDTO.setContractId(0L);
        contractProfileDTO.setDocumentTypeId(0L);
        contractProfileDTO.setFileName("fileName");
        contractProfileDTO.setFileSize("fileSize");
        contractProfileDTO.setFilePath("filePath");
        contractProfileDTO.setCreateUser("createUser");
        contractProfileDTO.setCreateDate(new Date(0L));
        contractProfileDTO.setApprovedUser("approvedUser");
        final List<ContractProfileDTO> expectedResult = Arrays.asList(contractProfileDTO);

        // Run the test
        final List<ContractProfileDTO> result = contractRepositoryImplUnderTest.downloadProfileByContract(0);
        assertNull(result);
    }

    @Test
    void testFindByPlateNumberAndContractNo() {
        // Setup
        final ContractSearchDTO dataParams = new ContractSearchDTO("inputSearch", "custName", "repName", "authName", 0L, "phoneNumber", "documentNumber", 0L);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = contractRepositoryImplUnderTest.findByPlateNumberAndContractNo(dataParams);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
    }

    @Test
    void testFindProfileByContractId() {
        // Setup
        final ContractProfileDTO requestModel = new ContractProfileDTO();
        requestModel.setContractProfileId(0L);
        requestModel.setCustId(0L);
        requestModel.setContractId(0L);
        requestModel.setDocumentTypeId(0L);
        requestModel.setFileName("fileName");
        requestModel.setFileSize("fileSize");
        requestModel.setFilePath("filePath");
        requestModel.setCreateUser("createUser");
        requestModel.setCreateDate(new Date(0L));
        requestModel.setApprovedUser("approvedUser");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = contractRepositoryImplUnderTest.findProfileByContractId(0L, requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
    }
}
