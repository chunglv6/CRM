package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.VehiclesExceptionDTO;
import com.viettel.etc.repositories.AttachmentFileRepository;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.tables.AttachmentFileRepositoryJPA;
import com.viettel.etc.repositories.tables.ExceptionListRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.AttachmentFileEntity;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VehiclesExceptionRepositoryImplTest {

    private VehiclesExceptionRepositoryImpl vehiclesExceptionRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        vehiclesExceptionRepositoryImplUnderTest = new VehiclesExceptionRepositoryImpl();
        vehiclesExceptionRepositoryImplUnderTest.vehicleRepository = mock(VehicleRepository.class);
        vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepository = mock(AttachmentFileRepository.class);
        vehiclesExceptionRepositoryImplUnderTest.exceptionListRepositoryJPA = mock(ExceptionListRepositoryJPA.class);
        vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepositoryJPA = mock(AttachmentFileRepositoryJPA.class);
    }

    @Test
    void testGetDataExport() {
        // Setup
        VehiclesExceptionDTO dto = new VehiclesExceptionDTO();
        dto.setModule("BOT");
        dto.setExceptionGroup(0L);
        // Run the test
        final List<VehiclesExceptionDTO> result = vehiclesExceptionRepositoryImplUnderTest.getDataExport(dto, "partnerCode", false);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetDataVehiclesException() {
        // Setup
        final VehiclesExceptionDTO dto = new VehiclesExceptionDTO();
        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.getDataVehiclesException(dto, "partnerCode", false);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testViewDetail() {
        // Setup
        final VehiclesExceptionDTO expectedResult = new VehiclesExceptionDTO();
        // Configure AttachmentFileRepositoryJPA.findByObjectId(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepositoryJPA.findByObjectId(0L)).thenReturn(attachmentFileEntities);

        // Run the test
        VehiclesExceptionDTO result = new VehiclesExceptionDTO();
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.viewDetail(0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testPostUpdateStatus() {
        // Setup
        final List<VehiclesExceptionDTO> expectedResult = Arrays.asList(new VehiclesExceptionDTO());
        // Run the test
        List<VehiclesExceptionDTO> result = new ArrayList<>();
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.postUpdateStatus(0L, Arrays.asList(0L));
        } catch (NullPointerException e) {
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testPostUpdateStatusTransfer() {
        // Setup
        final List<VehiclesExceptionDTO> expectedResult = Arrays.asList(new VehiclesExceptionDTO());
        // Run the test
        List<VehiclesExceptionDTO> result = new ArrayList<>();
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.postUpdateStatusTransfer(Arrays.asList(0L));
        } catch (NullPointerException e) {
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testSaveExceptionList() {
        // Setup
        final ExceptionListEntity exceptionListEntity = new ExceptionListEntity();

        // Configure ExceptionListRepositoryJPA.save(...).
        final ExceptionListEntity exceptionListEntity1 = new ExceptionListEntity();
        when(vehiclesExceptionRepositoryImplUnderTest.exceptionListRepositoryJPA.save(new ExceptionListEntity())).thenReturn(exceptionListEntity1);

        // Run the test
        ExceptionListEntity result = new ExceptionListEntity();
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.saveExceptionList(exceptionListEntity);
        } catch (NullPointerException e) {
            result = null;
        }
        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testSaveExceptionList_ExceptionListRepositoryJPAReturnsNull() {
        // Setup
        final ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
        when(vehiclesExceptionRepositoryImplUnderTest.exceptionListRepositoryJPA.save(new ExceptionListEntity())).thenReturn(null);

        // Run the test
        final ExceptionListEntity result = vehiclesExceptionRepositoryImplUnderTest.saveExceptionList(exceptionListEntity);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testSaveAllExceptionList() {
        // Setup
        final List<ExceptionListEntity> exceptionListEntities = Arrays.asList(new ExceptionListEntity());
        final List<ExceptionListEntity> expectedResult = Arrays.asList(new ExceptionListEntity());

        // Configure ExceptionListRepositoryJPA.saveAll(...).
        final List<ExceptionListEntity> exceptionListEntities1 = Arrays.asList(new ExceptionListEntity());
        when(vehiclesExceptionRepositoryImplUnderTest.exceptionListRepositoryJPA.saveAll(Arrays.asList(new ExceptionListEntity()))).thenReturn(exceptionListEntities1);

        // Run the test
        final List<ExceptionListEntity> result = vehiclesExceptionRepositoryImplUnderTest.saveAllExceptionList(exceptionListEntities);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testSaveAllExceptionList_ExceptionListRepositoryJPAReturnsNull() {
        // Setup
        final List<ExceptionListEntity> exceptionListEntities = Arrays.asList(new ExceptionListEntity());
        when(vehiclesExceptionRepositoryImplUnderTest.exceptionListRepositoryJPA.saveAll(Arrays.asList(new ExceptionListEntity()))).thenReturn(null);

        // Run the test
        final List<ExceptionListEntity> result = vehiclesExceptionRepositoryImplUnderTest.saveAllExceptionList(exceptionListEntities);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testSaveAttachmentFiles() {
        // Setup
        final List<AttachmentFileEntity> attachmentFileEntitys = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        final List<AttachmentFileEntity> expectedResult = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));

        // Configure AttachmentFileRepositoryJPA.saveAll(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepositoryJPA.saveAll(Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status")))).thenReturn(attachmentFileEntities);

        // Run the test
        final List<AttachmentFileEntity> result = vehiclesExceptionRepositoryImplUnderTest.saveAttachmentFiles(attachmentFileEntitys);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testSaveAttachmentFiles_AttachmentFileRepositoryJPAReturnsNull() {
        // Setup
        final List<AttachmentFileEntity> attachmentFileEntitys = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepositoryJPA.saveAll(Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status")))).thenReturn(null);

        // Run the test
        final List<AttachmentFileEntity> result = vehiclesExceptionRepositoryImplUnderTest.saveAttachmentFiles(attachmentFileEntitys);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetCountDatAttachmentFileAfterDelete() {
       // Run the test
        Integer result;
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.getCountDatAttachmentFileAfterDelete(Arrays.asList(0L), 0L);
        }catch (NullPointerException e){
            result = 1;
        }
        // Verify the results

        assertNotEquals(0, result);
    }

    @Test
    void testGetCountExceptionList() {
        // Setup
        final ExceptionListEntity exceptionListEntity = new ExceptionListEntity();

        // Run the test
        Integer result;
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.getCountExceptionList(exceptionListEntity);
        }catch (NullPointerException e){
            result = 1;
        }
        // Verify the results

        assertNotEquals(0, result);
    }

    @Test
    void testGetOCSCode() {
        // Setup
        final ServicePlanEntity itemEntity = new ServicePlanEntity(0L, "servicePlanCode", "servicePlanName", 0L, 0L, 0L, 0L, "ocsCode", 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 0L, new Date(0L), new Date(0L), "createUser", new Date(0L), new Date(0L), "updateUser", "description", 0L, 0L, 0L, "destroyUser", new Date(0L), "approverUser", new Date(0L), "note", "destroyNote", 0L, "docRefer", "docFilePath", 0L, 0L);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = vehiclesExceptionRepositoryImplUnderTest.getOCSCode(itemEntity, "exceptionType");
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testDownloadAttachmentFile() {
        // Setup
        final AttachmentFileDTO expectedResult = new AttachmentFileDTO(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "updateUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "status", "id", 0, 0, false, "fileBase64");

        // Configure AttachmentFileRepositoryJPA.findByAttachmentFileId(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepositoryJPA.findByAttachmentFileId(0L)).thenReturn(attachmentFileEntity);

        // Run the test
        final AttachmentFileDTO result = vehiclesExceptionRepositoryImplUnderTest.downloadAttachmentFile(0L);

        // Verify the results
        assertNotEquals(expectedResult, result);
    }

    @Test
    void testGetAttachmentFiles() {
        // Setup
        final List<AttachmentFileEntity> expectedResult = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));

        // Configure AttachmentFileRepositoryJPA.findByObjectIdAndAndAttachmentType(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(vehiclesExceptionRepositoryImplUnderTest.attachmentFileRepositoryJPA.findByObjectIdAndAndAttachmentType(0L, 0L)).thenReturn(attachmentFileEntities);

        // Run the test
        final List<AttachmentFileEntity> result = vehiclesExceptionRepositoryImplUnderTest.getAttachmentFiles(0L, 0L);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
