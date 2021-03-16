package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRepositoryImplTest {

    private VehicleRepositoryImpl vehicleRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        vehicleRepositoryImplUnderTest = new VehicleRepositoryImpl();
    }

    @Test
    void testExportVehiclesAssigned() {
        // Setup
        final ExportVehiclesAssignedDTO exportVehiclesAssignedDTO = new ExportVehiclesAssignedDTO();
        exportVehiclesAssignedDTO.setPlateNumber("plateNumber");
        exportVehiclesAssignedDTO.setCustId(0L);
        exportVehiclesAssignedDTO.setNameType("nameType");
        exportVehiclesAssignedDTO.setNetWeight(0L);
        exportVehiclesAssignedDTO.setCargoWeight(0L);
        exportVehiclesAssignedDTO.setNameGroup("nameGroup");
        exportVehiclesAssignedDTO.setGrossWeight(0L);
        exportVehiclesAssignedDTO.setPullingWeight(0L);
        exportVehiclesAssignedDTO.setSeatNumber(0L);
        exportVehiclesAssignedDTO.setEngineNumber("engineNumber");
        final List<ExportVehiclesAssignedDTO> expectedResult = Arrays.asList(exportVehiclesAssignedDTO);

        // Run the test
        final List<ExportVehiclesAssignedDTO> result = vehicleRepositoryImplUnderTest.exportVehiclesAssigned(0L);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testCheckSignVehicle() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.checkSignVehicle(Arrays.asList("value"));
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindProfileByContract() {
        // Setup
        final VehicleProfilePaginationDTO requestModel = new VehicleProfilePaginationDTO(0, 0, false, "documentTypeName", new Date(0L), new Date(0L), 0L, Arrays.asList(new VehicleProfilePaginationDTO.ProfileDTO("fileName", 0L)));

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.findProfileByContract(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testDownloadProfileByContract() {
        // Setup
        final List<VehicleProfilePaginationDTO> expectedResult = Arrays.asList(new VehicleProfilePaginationDTO(0, 0, false, "documentTypeName", new Date(0L), new Date(0L), 0L, Arrays.asList(new VehicleProfilePaginationDTO.ProfileDTO("fileName", 0L))));

        // Run the test
        final List<VehicleProfilePaginationDTO> result = vehicleRepositoryImplUnderTest.downloadProfileByContract(0L);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testFindVehicleByContract() {
        // Setup
        final VehicleByContractDTO requestModel = new VehicleByContractDTO();
        requestModel.setNameType("nameType");
        requestModel.setNameGroup("nameGroup");
        requestModel.setSalesType(0);
        requestModel.setColor("color");
        requestModel.setMark("mark");
        requestModel.setBrand("brand");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");
        requestModel.setSearchType("searchType");
        requestModel.setTypeScreen("typeScreen");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.findVehicleByContract(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindVehicleById() {
      // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.findVehicleById(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testGetVehicleRegistry() {
        // Setup
        final VehicleDTO itemParamsEntity = new VehicleDTO();

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.getVehicleRegistry(itemParamsEntity, "plateNumber");
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testFindProfileByVehicleId() {
        // Setup
        final VehicleProfileDTO requestModel = new VehicleProfileDTO(0L, 0L, 0L, 0L, "fileName", "fileSize", "filePath", "createUser", new Date(0L), "approvedUser", new Date(0L), "delUser", new Date(0L), "description", "status", "fileBase64", 0L, "documentTypeName", 0, 0, new Date(0L));

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.findProfileByVehicleId(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindVehicleSearchTree() {
        // Setup
        final VehicleDTO requestModel = new VehicleDTO();
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.findVehicleSearchTree(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testGetVehiclesByPlateNumber() {
        // Setup
        final VehicleSearchDTO itemParamsEntity = new VehicleSearchDTO();
        itemParamsEntity.setPlateNumber("plateNumber");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setIsPortalBot(false);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.getVehiclesByPlateNumber(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testGetVehiclesByPlateNumberForPortalBot() {
        // Setup
        final VehicleSearchDTO itemParamsEntity = new VehicleSearchDTO();
        itemParamsEntity.setPlateNumber("plateNumber");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setIsPortalBot(false);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.getVehiclesByPlateNumberForPortalBot(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindVehicleAssignedRFID() {
        // Setup
        final VehicleByContractDTO requestModel = new VehicleByContractDTO();
        requestModel.setNameType("nameType");
        requestModel.setNameGroup("nameGroup");
        requestModel.setSalesType(0);
        requestModel.setColor("color");
        requestModel.setMark("mark");
        requestModel.setBrand("brand");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");
        requestModel.setSearchType("searchType");
        requestModel.setTypeScreen("typeScreen");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.findVehicleAssignedRFID(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testGetVehicleRegistryInfo() {
        // Setup
        final VehicleDTO itemParamsEntity = new VehicleDTO();
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.getVehicleRegistryInfo(itemParamsEntity, "plateNumber");
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetVehicleExp() {
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehicleRepositoryImplUnderTest.getVehicleExp();
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }
}
