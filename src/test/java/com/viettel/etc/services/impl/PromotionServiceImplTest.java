package com.viettel.etc.services.impl;

import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.repositories.PromotionRepository;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PromotionServiceImplTest {
    @Mock
    private PromotionServiceJPA mockPromotionServiceJPA;
    @Mock
    private PromotionRepository mockPromotionRepository;
    @Mock
    private PromotionAssignServiceJPA mockPromotionAssignServiceJPA;
    @Mock
    private VehicleServiceJPA mockVehicleServiceJPA;
    @Mock
    private ContractServiceJPA mockContractServiceJPA;
    @Mock
    private AttachmentFileServiceJPA mockAttachmentFileServiceJPA;
    @Mock
    private FileService mockFileService;

    @InjectMocks
    private PromotionServiceImpl promotionServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPromotions() {
        // Setup
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final PromotionDTO params = new PromotionDTO(promotionEntity);
        params.setIsActive(true);
        params.setStartrecord(1);
        params.setPagesize(5);
        List<PromotionDTO> lst = new ArrayList<>();
        lst.add(params);
        // Configure PromotionRepository.getPromotions(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(lst);
        resultSelectEntity.setCount("count");
        when(mockPromotionRepository.getPromotions(params)).thenReturn(resultSelectEntity);

        // Run the test
        final Object result = promotionServiceImplUnderTest.getPromotions(params);
        assertNotNull(result);
    }

    @Test
    void testGetDetailPromotion() {
        // Setup
        when(mockPromotionServiceJPA.existsById(1L)).thenReturn(true);

        // Configure PromotionServiceJPA.getOne(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(1L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.getOne(1L)).thenReturn(promotionEntity);

        // Configure AttachmentFileServiceJPA.getByAttachmentTypeAndObjectIdAndStatus(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new java.sql.Date(0L), "updateUser", new Date(0L), "status"));
        when(mockAttachmentFileServiceJPA.getByAttachmentTypeAndObjectIdAndStatus(4L, 1L, "1")).thenReturn(attachmentFileEntities);

        // Run the test
        final Object result = promotionServiceImplUnderTest.getDetailPromotion(1L);
        assertNotNull(result);
        // Verify the results
    }

    @Test
    void testAddpromotionThrowns1() throws IOException {
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final PromotionDTO params = new PromotionDTO(promotionEntity);
        final List<MultipartFile> files = Arrays.asList();
        final Authentication authentication = null;

        assertThrows(EtcException.class,
                () -> promotionServiceImplUnderTest.addPromotion(params, files, authentication),
                "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddpromotionThrowns2() throws IOException {
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setEffDate(new GregorianCalendar(2042, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2040, Calendar.JANUARY, 1).getTime());
        final PromotionDTO params = new PromotionDTO(promotionEntity);
        final List<MultipartFile> files = Arrays.asList();
        final Authentication authentication = null;

        assertThrows(EtcException.class,
                () -> promotionServiceImplUnderTest.addPromotion(params, files, authentication),
                "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddPromotion() throws IOException {
        // Setup
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2089, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(null);
        final PromotionDTO params = new PromotionDTO(promotionEntity);
        final List<MultipartFile> files = Arrays.asList();
        final Authentication authentication = null;

        // Configure PromotionServiceJPA.save(...).
        final PromotionEntity promotionEntity1 = new PromotionEntity();
        promotionEntity1.setPromotionId(0L);
        promotionEntity1.setPromotionCode("promotionCode");
        promotionEntity1.setPromotionName("promotionName");
        promotionEntity1.setPromotionContent("promotionContent");
        promotionEntity1.setPromotionLevel("promotionLevel");
        promotionEntity1.setPromotionType("promotionType");
        promotionEntity1.setPromotionAmount(0.0);
        promotionEntity1.setStatus("status");
        promotionEntity1.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity1.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.save(any(PromotionEntity.class))).thenReturn(promotionEntity1);

        // Configure AttachmentFileServiceJPA.saveAll(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(mockAttachmentFileServiceJPA.saveAll(Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status")))).thenReturn(attachmentFileEntities);

        // Run the test
        final Object result = promotionServiceImplUnderTest.addPromotion(params, files, authentication);
        assertNotNull(result);

    }

    @Test
    void testEditPromotionThrowns() {
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final PromotionDTO itemParamsEntity = new PromotionDTO(promotionEntity);
        final Authentication authentication = null;
        final List<MultipartFile> files = Arrays.asList();
        assertThrows(EtcException.class,
                () -> promotionServiceImplUnderTest.editPromotion(itemParamsEntity, authentication, 0L, files),
                "Da co loi bat thuong xay ra");
    }

    @Test
    void testEditPromotion() throws IOException {
        // Setup
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2091, Calendar.JANUARY, 1).getTime());
        final PromotionDTO itemParamsEntity = new PromotionDTO(promotionEntity);
        final Authentication authentication = null;
        final List<MultipartFile> files = Arrays.asList();

        // Configure PromotionServiceJPA.findById(...).
        final PromotionEntity promotionEntity2 = new PromotionEntity();
        promotionEntity2.setPromotionId(0L);
        promotionEntity2.setPromotionCode("promotionCode");
        promotionEntity2.setPromotionName("promotionName");
        promotionEntity2.setPromotionContent("promotionContent");
        promotionEntity2.setPromotionLevel("promotionLevel");
        promotionEntity2.setPromotionType("promotionType");
        promotionEntity2.setPromotionAmount(0.0);
        promotionEntity2.setStatus("status");
        promotionEntity2.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity2.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final Optional<PromotionEntity> promotionEntity1 = Optional.of(promotionEntity2);
        when(mockPromotionServiceJPA.findById(0L)).thenReturn(promotionEntity1);

        // Configure PromotionServiceJPA.save(...).
        final PromotionEntity promotionEntity3 = new PromotionEntity();
        promotionEntity3.setPromotionId(0L);
        promotionEntity3.setPromotionCode("promotionCode");
        promotionEntity3.setPromotionName("promotionName");
        promotionEntity3.setPromotionContent("promotionContent");
        promotionEntity3.setPromotionLevel("promotionLevel");
        promotionEntity3.setPromotionType("promotionType");
        promotionEntity3.setPromotionAmount(0.0);
        promotionEntity3.setStatus("status");
        promotionEntity3.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity3.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.save(any(PromotionEntity.class))).thenReturn(promotionEntity3);

        // Configure AttachmentFileServiceJPA.saveAll(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(mockAttachmentFileServiceJPA.saveAll(Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status")))).thenReturn(attachmentFileEntities);

        // Run the test
        final Object result = promotionServiceImplUnderTest.editPromotion(itemParamsEntity, authentication, 0L, files);
        assertNotNull(result);
    }

    @Test
    void testEditPromotionNull() throws IOException {
        // Setup
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2091, Calendar.JANUARY, 1).getTime());
        final PromotionDTO itemParamsEntity = new PromotionDTO(promotionEntity);
        final Authentication authentication = null;
        final List<MultipartFile> files = Arrays.asList();
        when(mockPromotionServiceJPA.findById(0L)).thenReturn(Optional.empty());

        // Configure PromotionServiceJPA.save(...).
        final PromotionEntity promotionEntity3 = new PromotionEntity();
        promotionEntity3.setPromotionId(0L);
        promotionEntity3.setPromotionCode("promotionCode");
        promotionEntity3.setPromotionName("promotionName");
        promotionEntity3.setPromotionContent("promotionContent");
        promotionEntity3.setPromotionLevel("promotionLevel");
        promotionEntity3.setPromotionType("promotionType");
        promotionEntity3.setPromotionAmount(0.0);
        promotionEntity3.setStatus("status");
        promotionEntity3.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity3.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.save(any(PromotionEntity.class))).thenReturn(promotionEntity3);

        // Configure AttachmentFileServiceJPA.saveAll(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(mockAttachmentFileServiceJPA.saveAll(Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status")))).thenReturn(attachmentFileEntities);

        // Run the test
        final Object result = promotionServiceImplUnderTest.editPromotion(itemParamsEntity, authentication, 0L, files);
        assertNull(result);
    }

    @Test
    void testAddAttachmentFile() throws Exception {
        // Setup
        final List<MultipartFile> files = Arrays.asList();
        // Configure AttachmentFileServiceJPA.saveAll(...).
        final List<AttachmentFileEntity> attachmentFileEntities = Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status"));
        when(mockAttachmentFileServiceJPA.saveAll(Arrays.asList(new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status")))).thenReturn(attachmentFileEntities);
        // Run the test
        promotionServiceImplUnderTest.addAttachmentFile(files, "userLogin", 0L);
    }

    @Test
    void testDeletePromotion() {
        // Setup
        final Authentication authentication = null;

        // Configure PromotionServiceJPA.findById(...).
        final PromotionEntity promotionEntity1 = new PromotionEntity();
        promotionEntity1.setPromotionId(0L);
        promotionEntity1.setPromotionCode("promotionCode");
        promotionEntity1.setPromotionName("promotionName");
        promotionEntity1.setPromotionContent("promotionContent");
        promotionEntity1.setPromotionLevel("promotionLevel");
        promotionEntity1.setPromotionType("promotionType");
        promotionEntity1.setPromotionAmount(0.0);
        promotionEntity1.setStatus("0");
        promotionEntity1.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity1.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final Optional<PromotionEntity> promotionEntity = Optional.of(promotionEntity1);
        when(mockPromotionServiceJPA.findById(0L)).thenReturn(promotionEntity);

        // Run the test
        final Object result = promotionServiceImplUnderTest.deletePromotion(authentication, 0L);

        // Verify the results
        assertEquals(result, "");
        verify(mockPromotionServiceJPA).deleteById(0L);
    }

    @Test
    void testDeletePromotionNull() {
        // Setup
        final Authentication authentication = null;
        when(mockPromotionServiceJPA.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final Object result = promotionServiceImplUnderTest.deletePromotion(authentication, 0L);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testDeleteAttachment() {
        // Setup
        when(mockAttachmentFileServiceJPA.existsById(0L)).thenReturn(true);

        // Configure AttachmentFileServiceJPA.getOne(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 4L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileServiceJPA.getOne(0L)).thenReturn(attachmentFileEntity);

        // Run the test
        promotionServiceImplUnderTest.deleteAttachment(0L);

        verify(mockAttachmentFileServiceJPA).deleteById(0L);
    }

    @Test
    void testDeleteAttachmentException() {
        // Setup
        when(mockAttachmentFileServiceJPA.existsById(0L)).thenReturn(false);
        assertThrows(EtcException.class,
                () -> promotionServiceImplUnderTest.deleteAttachment(0L),
                "Da co loi bat thuong xay ra");
    }

    @Test
    void testDeleteAttachmentException1() {
        when(mockAttachmentFileServiceJPA.existsById(0L)).thenReturn(true);

        // Configure AttachmentFileServiceJPA.getOne(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileServiceJPA.getOne(0L)).thenReturn(attachmentFileEntity);
        assertThrows(EtcException.class,
                () -> promotionServiceImplUnderTest.deleteAttachment(0L),
                "Da co loi bat thuong xay ra");
    }

    @Test
    void testApprovePromotion() {
        // Setup
        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getByIdIn(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("0");
        promotionEntity.setEffDate(new GregorianCalendar(2091, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionEntity> promotionEntities = Arrays.asList(promotionEntity);
        when(mockPromotionServiceJPA.getByIdIn(Arrays.asList(0L))).thenReturn(promotionEntities);

        // Configure PromotionServiceJPA.saveAll(...).
        final PromotionEntity promotionEntity1 = new PromotionEntity();
        promotionEntity1.setPromotionId(0L);
        promotionEntity1.setPromotionCode("promotionCode");
        promotionEntity1.setPromotionName("promotionName");
        promotionEntity1.setPromotionContent("promotionContent");
        promotionEntity1.setPromotionLevel("promotionLevel");
        promotionEntity1.setPromotionType("promotionType");
        promotionEntity1.setPromotionAmount(0.0);
        promotionEntity1.setStatus("0");
        promotionEntity1.setEffDate(new GregorianCalendar(2091, Calendar.JANUARY, 1).getTime());
        promotionEntity1.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionEntity> promotionEntities1 = Arrays.asList(promotionEntity1);
        when(mockPromotionServiceJPA.saveAll(Arrays.asList(new PromotionEntity()))).thenReturn(promotionEntities1);
        // Run the test
        final Object result = promotionServiceImplUnderTest.approvePromotion("0", authentication);
        assertNotNull(result);
    }

    @Test
    void testApprovePromotionNull() {
        // Setup
        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(false);

        final Object result = promotionServiceImplUnderTest.approvePromotion("0", authentication);
        assertNull(result);
    }

    @Test
    void testApprovePromotionThrowns() {
        // Setup
        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getByIdIn(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("0");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionEntity> promotionEntities = Arrays.asList(promotionEntity);
        when(mockPromotionServiceJPA.getByIdIn(Arrays.asList(0L))).thenReturn(promotionEntities);
        assertThrows(EtcException.class, () -> promotionServiceImplUnderTest.approvePromotion("0", authentication), "đa co loi bat thuong xay ra");
    }

    @Test
    void testExportPromotion() {
        // Setup
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("1");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final PromotionDTO itemParamsEntity = new PromotionDTO(promotionEntity);

        // Configure PromotionRepository.getPromotions(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList(itemParamsEntity));
        resultSelectEntity.setCount("count");
        when(mockPromotionRepository.getPromotions(new PromotionDTO(new PromotionEntity()))).thenReturn(resultSelectEntity);
        new MockUp<PromotionServiceImpl>() {
            @mockit.Mock
            public Object getPromotions(PromotionDTO params) {
                return resultSelectEntity;
            }
        };
        // Run the test
        final String result = promotionServiceImplUnderTest.exportPromotion(itemParamsEntity);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetPromotionAssigns() {
        // Setup
        final PromotionAssignDTO itemParamsEntity = new PromotionAssignDTO();
        itemParamsEntity.setPromotionAssignId(0L);
        itemParamsEntity.setPromotionId(0L);
        itemParamsEntity.setAssignLevel("assignLevel");
        itemParamsEntity.setCustId(0L);
        itemParamsEntity.setCustName("custName");
        itemParamsEntity.setContractId(0L);
        itemParamsEntity.setContractNo("contractNo");
        itemParamsEntity.setVehicleId(0L);
        itemParamsEntity.setPlateNumber("plateNumber");
        itemParamsEntity.setEPC("EPC");

        // Configure PromotionRepository.getPromotionAssignDetail(...).
        final ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setListData(Arrays.asList("value"));
        resultSelectEntity.setCount("count");
        when(mockPromotionRepository.getPromotionAssignDetail(itemParamsEntity)).thenReturn(resultSelectEntity);

        // Run the test
        final Object result = promotionServiceImplUnderTest.getPromotionAssigns(itemParamsEntity);
        assertNotNull(result);
    }

    @Test
    void testAddPromotionAssignThrows() {
        // Setup
        final PromotionAssignDTO params = new PromotionAssignDTO();
        params.setPromotionAssignId(0L);
        params.setPromotionId(0L);
        params.setAssignLevel("assignLevel");
        params.setCustId(0L);
        params.setCustName("custName");
        params.setContractId(0L);
        params.setContractNo("contractNo");
        params.setVehicleId(0L);
        params.setPlateNumber("plateNumber");
        params.setEPC("EPC");

        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(false);
        assertThrows(EtcException.class, () -> promotionServiceImplUnderTest.addPromotionAssign(params, authentication), "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddPromotionAssignThrowns1() {
        // Setup
        final PromotionAssignDTO params = new PromotionAssignDTO();
        params.setPromotionAssignId(0L);
        params.setPromotionId(0L);
        params.setAssignLevel("assignLevel");
        params.setCustId(0L);
        params.setCustName("custName");
        params.setContractId(0L);
        params.setContractNo("contractNo");
        params.setVehicleId(0L);
        params.setPlateNumber("plateNumber");
        params.setEPC("EPC");

        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getOne(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("3");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.getOne(0L)).thenReturn(promotionEntity);
        assertThrows(EtcException.class, () -> promotionServiceImplUnderTest.addPromotionAssign(params, authentication), "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddPromotionAssignThrowns2() {
        // Setup
        final PromotionAssignDTO params = new PromotionAssignDTO();
        params.setPromotionAssignId(0L);
        params.setPromotionId(0L);
        params.setAssignLevel("2");
        params.setCustName("custName");
        params.setContractNo("contractNo");
        params.setVehicleId(0L);
        params.setPlateNumber("plateNumber");
        params.setEPC("EPC");

        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getOne(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.getOne(0L)).thenReturn(promotionEntity);

        // Configure VehicleServiceJPA.getOne(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleServiceJPA.getOne(0L)).thenReturn(vehicleEntity);

        // Configure PromotionAssignServiceJPA.getByCustLvl(...).
        final PromotionAssignEntity promotionAssignEntity = new PromotionAssignEntity();
        promotionAssignEntity.setPromotionAssignId(0L);
        promotionAssignEntity.setPromotionId(0L);
        promotionAssignEntity.setAssignLevel("assignLevel");
        promotionAssignEntity.setCustId(0L);
        promotionAssignEntity.setContractId(0L);
        promotionAssignEntity.setVehicleId(0L);
        promotionAssignEntity.setPlateNumber("plateNumber");
        promotionAssignEntity.setEPC("EPC");
        promotionAssignEntity.setCreateUser("createUser");
        promotionAssignEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities = Arrays.asList(promotionAssignEntity);
        when(mockPromotionAssignServiceJPA.getByCustLvl(0L, 0L)).thenReturn(promotionAssignEntities);
        assertThrows(EtcException.class, () -> promotionServiceImplUnderTest.addPromotionAssign(params, authentication), "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddPromotionAssignThrowns3() {
        // Setup
        final PromotionAssignDTO params = new PromotionAssignDTO();
        params.setPromotionAssignId(0L);
        params.setPromotionId(0L);
        params.setAssignLevel("2");
        params.setCustName("custName");
        params.setContractNo("contractNo");
        params.setVehicleId(0L);
        params.setPlateNumber("plateNumber");
        params.setEPC("EPC");

        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getOne(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.getOne(0L)).thenReturn(promotionEntity);

        // Configure VehicleServiceJPA.getOne(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleServiceJPA.getOne(0L)).thenReturn(vehicleEntity);

        // Configure PromotionAssignServiceJPA.getByCustLvl(...).
        final PromotionAssignEntity promotionAssignEntity = new PromotionAssignEntity();
        promotionAssignEntity.setPromotionAssignId(0L);
        promotionAssignEntity.setPromotionId(0L);
        promotionAssignEntity.setAssignLevel("assignLevel");
        promotionAssignEntity.setCustId(0L);
        promotionAssignEntity.setContractId(0L);
        promotionAssignEntity.setVehicleId(0L);
        promotionAssignEntity.setPlateNumber("plateNumber");
        promotionAssignEntity.setEPC("EPC");
        promotionAssignEntity.setCreateUser("createUser");
        promotionAssignEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities = Arrays.asList(promotionAssignEntity);
        when(mockPromotionAssignServiceJPA.getByCustLvl(0L, 0L)).thenReturn(new ArrayList<>());
        // Configure PromotionAssignServiceJPA.getByContractLvl(...).
        final PromotionAssignEntity promotionAssignEntity1 = new PromotionAssignEntity();
        promotionAssignEntity1.setPromotionAssignId(0L);
        promotionAssignEntity1.setPromotionId(0L);
        promotionAssignEntity1.setAssignLevel("assignLevel");
        promotionAssignEntity1.setCustId(0L);
        promotionAssignEntity1.setContractId(0L);
        promotionAssignEntity1.setVehicleId(0L);
        promotionAssignEntity1.setPlateNumber("plateNumber");
        promotionAssignEntity1.setEPC("EPC");
        promotionAssignEntity1.setCreateUser("createUser");
        promotionAssignEntity1.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities1 = Arrays.asList(promotionAssignEntity1);
        when(mockPromotionAssignServiceJPA.getByContractLvl(0L, 0L)).thenReturn(promotionAssignEntities1);
        assertThrows(EtcException.class, () -> promotionServiceImplUnderTest.addPromotionAssign(params, authentication), "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddPromotionAssignThrowns4(){
        // Setup
        final PromotionAssignDTO params = new PromotionAssignDTO();
        params.setPromotionAssignId(0L);
        params.setPromotionId(0L);
        params.setAssignLevel("2");
        params.setCustName("custName");
        params.setContractNo("contractNo");
        params.setVehicleId(0L);
        params.setPlateNumber("plateNumber");
        params.setEPC("EPC");

        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getOne(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.getOne(0L)).thenReturn(promotionEntity);

        // Configure VehicleServiceJPA.getOne(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleServiceJPA.getOne(0L)).thenReturn(vehicleEntity);

        // Configure PromotionAssignServiceJPA.getByCustLvl(...).
        final PromotionAssignEntity promotionAssignEntity = new PromotionAssignEntity();
        promotionAssignEntity.setPromotionAssignId(0L);
        promotionAssignEntity.setPromotionId(0L);
        promotionAssignEntity.setAssignLevel("assignLevel");
        promotionAssignEntity.setCustId(0L);
        promotionAssignEntity.setContractId(0L);
        promotionAssignEntity.setVehicleId(0L);
        promotionAssignEntity.setPlateNumber("plateNumber");
        promotionAssignEntity.setEPC("EPC");
        promotionAssignEntity.setCreateUser("createUser");
        promotionAssignEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities = Arrays.asList(promotionAssignEntity);
        when(mockPromotionAssignServiceJPA.getByCustLvl(0L, 0L)).thenReturn(new ArrayList<>());
        // Configure PromotionAssignServiceJPA.getByContractLvl(...).
        final PromotionAssignEntity promotionAssignEntity1 = new PromotionAssignEntity();
        promotionAssignEntity1.setPromotionAssignId(0L);
        promotionAssignEntity1.setPromotionId(0L);
        promotionAssignEntity1.setAssignLevel("assignLevel");
        promotionAssignEntity1.setCustId(0L);
        promotionAssignEntity1.setContractId(0L);
        promotionAssignEntity1.setVehicleId(0L);
        promotionAssignEntity1.setPlateNumber("plateNumber");
        promotionAssignEntity1.setEPC("EPC");
        promotionAssignEntity1.setCreateUser("createUser");
        promotionAssignEntity1.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionAssignServiceJPA.getByContractLvl(0L, 0L)).thenReturn(new ArrayList<>());
        // Configure PromotionAssignServiceJPA.getByVehicle(...).
        final PromotionAssignEntity promotionAssignEntity2 = new PromotionAssignEntity();
        promotionAssignEntity2.setPromotionAssignId(0L);
        promotionAssignEntity2.setPromotionId(0L);
        promotionAssignEntity2.setAssignLevel("assignLevel");
        promotionAssignEntity2.setCustId(0L);
        promotionAssignEntity2.setContractId(0L);
        promotionAssignEntity2.setVehicleId(0L);
        promotionAssignEntity2.setPlateNumber("plateNumber");
        promotionAssignEntity2.setEPC("EPC");
        promotionAssignEntity2.setCreateUser("createUser");
        promotionAssignEntity2.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities2 = Arrays.asList(promotionAssignEntity2);
        when(mockPromotionAssignServiceJPA.getByVehicle(0L, 0L)).thenReturn(promotionAssignEntities2);
        assertThrows(EtcException.class, () -> promotionServiceImplUnderTest.addPromotionAssign(params, authentication), "Da co loi bat thuong xay ra");
    }

    @Test
    void testAddPromotionAssign() {
        // Setup
        final PromotionAssignDTO params = new PromotionAssignDTO();
        params.setPromotionAssignId(0L);
        params.setPromotionId(0L);
        params.setAssignLevel("2");
        params.setCustName("custName");
        params.setContractNo("contractNo");
        params.setVehicleId(0L);
        params.setPlateNumber("plateNumber");
        params.setEPC("EPC");

        final Authentication authentication = null;
        when(mockPromotionServiceJPA.existsById(0L)).thenReturn(true);

        // Configure PromotionServiceJPA.getOne(...).
        final PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setPromotionId(0L);
        promotionEntity.setPromotionCode("promotionCode");
        promotionEntity.setPromotionName("promotionName");
        promotionEntity.setPromotionContent("promotionContent");
        promotionEntity.setPromotionLevel("promotionLevel");
        promotionEntity.setPromotionType("promotionType");
        promotionEntity.setPromotionAmount(0.0);
        promotionEntity.setStatus("status");
        promotionEntity.setEffDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        promotionEntity.setExpDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionServiceJPA.getOne(0L)).thenReturn(promotionEntity);

        // Configure VehicleServiceJPA.getOne(...).
        final VehicleEntity vehicleEntity = new VehicleEntity(0L, 0L, 0L, "contractAppendix", "plateNumber", 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, "chassicNumber", "engineNumber", 0L, 0L, 0L, 0L, "status", "activeStatus", "epc", "tid", "rfidSerial", "reservedMemory", "rfidType", 0L, "offerCode", new Date(0L), new Date(0L), 0L, 0L, "createUser", new Date(0L), "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "owner", new Date(0L), "appendixUsername", "note", "vehicleTypeName", "vehicleTypeCode", "vehicleGroupCode", "plateTypeCode","promotionId");
        when(mockVehicleServiceJPA.getOne(0L)).thenReturn(vehicleEntity);

        // Configure PromotionAssignServiceJPA.getByCustLvl(...).
        final PromotionAssignEntity promotionAssignEntity = new PromotionAssignEntity();
        promotionAssignEntity.setPromotionAssignId(0L);
        promotionAssignEntity.setPromotionId(0L);
        promotionAssignEntity.setAssignLevel("assignLevel");
        promotionAssignEntity.setCustId(0L);
        promotionAssignEntity.setContractId(0L);
        promotionAssignEntity.setVehicleId(0L);
        promotionAssignEntity.setPlateNumber("plateNumber");
        promotionAssignEntity.setEPC("EPC");
        promotionAssignEntity.setCreateUser("createUser");
        promotionAssignEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionAssignServiceJPA.getByCustLvl(0L, 0L)).thenReturn(new ArrayList<>());

        // Configure PromotionAssignServiceJPA.getByContractLvl(...).
        final PromotionAssignEntity promotionAssignEntity1 = new PromotionAssignEntity();
        promotionAssignEntity1.setPromotionAssignId(0L);
        promotionAssignEntity1.setPromotionId(0L);
        promotionAssignEntity1.setAssignLevel("assignLevel");
        promotionAssignEntity1.setCustId(0L);
        promotionAssignEntity1.setContractId(0L);
        promotionAssignEntity1.setVehicleId(0L);
        promotionAssignEntity1.setPlateNumber("plateNumber");
        promotionAssignEntity1.setEPC("EPC");
        promotionAssignEntity1.setCreateUser("createUser");
        promotionAssignEntity1.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionAssignServiceJPA.getByContractLvl(0L, 0L)).thenReturn(new ArrayList<>());

        // Configure PromotionAssignServiceJPA.getByVehicle(...).
        final PromotionAssignEntity promotionAssignEntity2 = new PromotionAssignEntity();
        promotionAssignEntity2.setPromotionAssignId(0L);
        promotionAssignEntity2.setPromotionId(0L);
        promotionAssignEntity2.setAssignLevel("assignLevel");
        promotionAssignEntity2.setCustId(0L);
        promotionAssignEntity2.setContractId(0L);
        promotionAssignEntity2.setVehicleId(0L);
        promotionAssignEntity2.setPlateNumber("plateNumber");
        promotionAssignEntity2.setEPC("EPC");
        promotionAssignEntity2.setCreateUser("createUser");
        promotionAssignEntity2.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities2 = Arrays.asList(promotionAssignEntity2);
        when(mockPromotionAssignServiceJPA.getByVehicle(0L, 0L)).thenReturn(new ArrayList<>());

        // Configure ContractServiceJPA.getOne(...).
        final ContractEntity contractEntity = new ContractEntity(0L, 0L, "contractNo", new Date(0L), new Date(0L), new Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new Date(0L), "addfilesUser", new Date(0L), "signName", "smsRenew", new Date(0L), "createUser", new Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber");
        when(mockContractServiceJPA.getOne(0L)).thenReturn(contractEntity);

        // Configure PromotionAssignServiceJPA.getByContract(...).
        final PromotionAssignEntity promotionAssignEntity3 = new PromotionAssignEntity();
        promotionAssignEntity3.setPromotionAssignId(0L);
        promotionAssignEntity3.setPromotionId(0L);
        promotionAssignEntity3.setAssignLevel("assignLevel");
        promotionAssignEntity3.setCustId(0L);
        promotionAssignEntity3.setContractId(0L);
        promotionAssignEntity3.setVehicleId(0L);
        promotionAssignEntity3.setPlateNumber("plateNumber");
        promotionAssignEntity3.setEPC("EPC");
        promotionAssignEntity3.setCreateUser("createUser");
        promotionAssignEntity3.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities3 = Arrays.asList(promotionAssignEntity3);
        when(mockPromotionAssignServiceJPA.getByContract(0L, 0L)).thenReturn(promotionAssignEntities3);

        // Configure PromotionAssignServiceJPA.getByCust(...).
        final PromotionAssignEntity promotionAssignEntity4 = new PromotionAssignEntity();
        promotionAssignEntity4.setPromotionAssignId(0L);
        promotionAssignEntity4.setPromotionId(0L);
        promotionAssignEntity4.setAssignLevel("assignLevel");
        promotionAssignEntity4.setCustId(0L);
        promotionAssignEntity4.setContractId(0L);
        promotionAssignEntity4.setVehicleId(0L);
        promotionAssignEntity4.setPlateNumber("plateNumber");
        promotionAssignEntity4.setEPC("EPC");
        promotionAssignEntity4.setCreateUser("createUser");
        promotionAssignEntity4.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final List<PromotionAssignEntity> promotionAssignEntities4 = Arrays.asList(promotionAssignEntity4);
        when(mockPromotionAssignServiceJPA.getByCust(0L, 0L)).thenReturn(promotionAssignEntities4);

        // Configure PromotionAssignServiceJPA.save(...).
        final PromotionAssignEntity promotionAssignEntity5 = new PromotionAssignEntity();
        promotionAssignEntity5.setPromotionAssignId(0L);
        promotionAssignEntity5.setPromotionId(0L);
        promotionAssignEntity5.setAssignLevel("assignLevel");
        promotionAssignEntity5.setCustId(0L);
        promotionAssignEntity5.setContractId(0L);
        promotionAssignEntity5.setVehicleId(0L);
        promotionAssignEntity5.setPlateNumber("plateNumber");
        promotionAssignEntity5.setEPC("EPC");
        promotionAssignEntity5.setCreateUser("createUser");
        promotionAssignEntity5.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockPromotionAssignServiceJPA.save(new PromotionAssignEntity())).thenReturn(promotionAssignEntity5);

        // Run the test
        final Object result = promotionServiceImplUnderTest.addPromotionAssign(params, authentication);
        assertNull(result);
    }

    @Test
    void testDeletePromotionAssign() {
        // Setup
        final Authentication authentication = null;

        // Configure PromotionAssignServiceJPA.findById(...).
        final PromotionAssignEntity promotionAssignEntity1 = new PromotionAssignEntity();
        promotionAssignEntity1.setPromotionAssignId(0L);
        promotionAssignEntity1.setPromotionId(0L);
        promotionAssignEntity1.setAssignLevel("assignLevel");
        promotionAssignEntity1.setCustId(0L);
        promotionAssignEntity1.setContractId(0L);
        promotionAssignEntity1.setVehicleId(0L);
        promotionAssignEntity1.setPlateNumber("plateNumber");
        promotionAssignEntity1.setEPC("EPC");
        promotionAssignEntity1.setCreateUser("createUser");
        promotionAssignEntity1.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final Optional<PromotionAssignEntity> promotionAssignEntity = Optional.of(promotionAssignEntity1);
        when(mockPromotionAssignServiceJPA.findById(0L)).thenReturn(promotionAssignEntity);

        // Run the test
        final Object result = promotionServiceImplUnderTest.deletePromotionAssign(authentication, 0L);

        assertEquals(result, "");
        // Verify the results
        verify(mockPromotionAssignServiceJPA).deleteById(0L);
    }

    @Test
    void testDeletePromotionAssignNull(){
        // Setup
        final Authentication authentication = null;
        when(mockPromotionAssignServiceJPA.findById(0L)).thenReturn(Optional.empty());
        final Object result = promotionServiceImplUnderTest.deletePromotionAssign(authentication, 0L);
        assertNull(result);
    }

    @Test
    void testDownloadAttachment() throws Exception {
        // Setup
        final HttpServletResponse response = new MockHttpServletResponse();

        // Configure AttachmentFileServiceJPA.getOne(...).
        final AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new Date(0L), "updateUser", new Date(0L), "status");
        when(mockAttachmentFileServiceJPA.getOne(0L)).thenReturn(attachmentFileEntity);

        when(mockFileService.getFile("filePath")).thenReturn("content".getBytes());
        new MockUp<FnCommon>(){
            @mockit.Mock
            public void responseFile(HttpServletResponse response, byte[] file, String fileName) throws IOException {
            }
        };
        // Run the test
        promotionServiceImplUnderTest.downloadAttachment(0L, response);
    }
}
