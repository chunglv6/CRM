package com.viettel.etc.services.impl;

import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.VehiclesExceptionDTO;
import com.viettel.etc.repositories.VehiclesExceptionRepository;
import com.viettel.etc.repositories.tables.entities.AttachmentFileEntity;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.services.tables.ExceptionListServiceJPA;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exports.PdfHtmlTemplateExporter;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
public class VehiclesExceptionServiceImplTest {
    @Mock
    private VehiclesExceptionRepository vehiclesExceptionRepository;
    @Mock
    private ExceptionListServiceJPA exceptionListServiceJPA;
    @Mock
    private PdfHtmlTemplateExporter parseThymeleafTemplate;
    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private VehicleServiceJPA vehicleServiceJPA;
    @Mock
    private FileService fileService;

    @InjectMocks
    VehiclesExceptionServiceImpl service;

    private AttachmentFileDTO attachmentFileDTOTest;
    private VehiclesExceptionDTO dto;
    Map<String, Object> map;

    @Mock
    JedisCacheService jedisCacheService;

    @BeforeEach
    public void setUp() {
        map = new HashMap<>();
        map.put("partner_code", null);
        map.put("partner_type", "bot");

        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                return map;
            }
        };

        new MockUp<IOUtils>() {
            @mockit.Mock
            public byte[] toByteArray(InputStream input) {
                return "\u00ea\u003a\u0069\u0010\u00a2\u00d8\u0008\u0000\u002b\u0030\u0030".getBytes();
            }
        };

    }

    @Test
    public void downloadAttachmentFileTest() {
        attachmentFileDTOTest = new AttachmentFileDTO();
        given(service.downloadAttachmentFile(Mockito.anyLong())).willReturn(attachmentFileDTOTest);
        assertEquals(attachmentFileDTOTest, service.downloadAttachmentFile(Mockito.anyLong()));
    }

    @Test
    public void getAttachmentFilesTest() {
        List<AttachmentFileEntity> entityList = new ArrayList<>();
        given(vehiclesExceptionRepository.getAttachmentFiles(1L, 1L)).willReturn(entityList);
        assertEquals(entityList, service.getAttachmentFiles(1L, 1L));
    }

    @Test
    public void getDataVehiclesExceptionTest() {
        service.getDataVehiclesException(dto, null);

        dto = new VehiclesExceptionDTO();
        service.getDataVehiclesException(dto, null);

        dto.setModule("C");
        service.getDataVehiclesException(dto, null);

        dto.setModule(Constants.MODULE_BOT);
        service.getDataVehiclesException(dto, null);

        map.put("partner_type", "1");
        map.put("partner_code", "bot912");
        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                return map;
            }
        };
        service.getDataVehiclesException(dto, null);

        dto.setExceptionGroup(3L);
        service.getDataVehiclesException(dto, null);

        dto.setExceptionGroup(1L);
        dto.setModule(Constants.MODULE_MOT);
        service.getDataVehiclesException(dto, null);

        //region check maxlength
        StringBuilder str = new StringBuilder("aaaaaaaaaa");
        for (int i = 0; i <= 10; i++) {
            str.append(str);
        }
        dto.setPlateNumber(str.toString());
        dto.setModule(Constants.MODULE_CRM);
        service.getDataVehiclesException(dto, null);

        dto.setPlateNumber("plateNumber");
        dto.setEpc(str.toString());
        service.getDataVehiclesException(dto, null);
        //endregion

        //region check date valid
        dto.setPlateNumber("plateNumber");
        dto.setEpc("epc");
        dto.setCreateDateFrom("222222");
        service.getDataVehiclesException(dto, null);

        dto.setCreateDateFrom("2/2/2020");
        dto.setCreateDateTo("222");
        service.getDataVehiclesException(dto, null);

        dto.setCreateDateTo("2/1/2020");
        dto.setEffDateFrom("222");
        service.getDataVehiclesException(dto, null);

        dto.setEffDateFrom("2/2/2020");
        dto.setEffDateTo("222");
        service.getDataVehiclesException(dto, null);

        dto.setEffDateTo("2/1/2020");
        dto.setApprovalDateFrom("222");
        service.getDataVehiclesException(dto, null);

        dto.setApprovalDateFrom("2/2/2020");
        dto.setApprovalDateTo("222");
        service.getDataVehiclesException(dto, null);

        dto.setApprovalDateTo("2/1/2020");
        dto.setCancelDateFrom("222");
        service.getDataVehiclesException(dto, null);

        dto.setCancelDateFrom("2/2/2020");
        dto.setCancelDateTo("222");
        service.getDataVehiclesException(dto, null);

        dto.setCancelDateTo("2/1/2020");
        dto.setExpDateFrom("222");
        service.getDataVehiclesException(dto, null);

        dto.setExpDateFrom("2/2/2020");
        dto.setExpDateTo("222");
        service.getDataVehiclesException(dto, null);

        dto.setExpDateTo("2/1/2020");
        service.getDataVehiclesException(dto, null);
        //endregion

        //region check date after
        dto.setCreateDateTo("2/2/2020");
        service.getDataVehiclesException(dto, null);
        dto.setEffDateTo("2/2/2020");
        service.getDataVehiclesException(dto, null);
        dto.setExpDateTo("2/2/2020");
        service.getDataVehiclesException(dto, null);
        dto.setApprovalDateTo("2/2/2020");
        service.getDataVehiclesException(dto, null);
        dto.setCancelDateTo("2/2/2020");
        service.getDataVehiclesException(dto, null);
        //endregion
    }

    @Test
    public void viewDetailTest() {
        dto = new VehiclesExceptionDTO();
        dto.setVehicleId(null);
        VehicleEntity vehicleEntity = new VehicleEntity();

        given(vehiclesExceptionRepository.viewDetail(1L)).willReturn(dto);
        given(vehicleServiceJPA.existsById(1L)).willReturn(true);
        given(vehicleServiceJPA.getOne(1L)).willReturn(vehicleEntity);
        service.viewDetail(1L);
    }

    @Test
    public void deleteVehiclesExceptionTest() {
        given(exceptionListServiceJPA.findByExceptionListId(1L)).willReturn(null);
        service.deleteVehiclesException(null, 1L);

        ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
        exceptionListEntity.setStatus(2L);
        given(exceptionListServiceJPA.findByExceptionListId(1L)).willReturn(exceptionListEntity);
        service.deleteVehiclesException(null, 1L);

        exceptionListEntity.setStatus(1L);
        given(exceptionListServiceJPA.findByExceptionListId(1L)).willReturn(exceptionListEntity);
        given(vehiclesExceptionRepository.deleteExceptionList(1L)).willReturn(true);
        service.deleteVehiclesException(null, 1L);
    }

    @Test
    public void exportVehiclesExceptionExcelTest() {
        ReflectionTestUtils.setField(service, "jedisCacheService", jedisCacheService);

        service.exportVehiclesExceptionExcel(dto, null);

        dto = new VehiclesExceptionDTO();
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setModule("C");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setModule(Constants.MODULE_BOT);
        service.exportVehiclesExceptionExcel(dto, null);

        map.put("partner_type", "1");
        map.put("partner_code", "bot912");
        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                return map;
            }
        };
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setExceptionGroup(3L);
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setExceptionGroup(1L);
        dto.setModule(Constants.MODULE_MOT);
        service.exportVehiclesExceptionExcel(dto, null);

        //region check maxlength
        StringBuilder str = new StringBuilder("aaaaaaaaaa");
        for (int i = 0; i <= 10; i++) {
            str.append(str);
        }
        dto.setPlateNumber(str.toString());
        dto.setModule(Constants.MODULE_CRM);
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setPlateNumber("plateNumber");
        dto.setEpc(str.toString());
        service.exportVehiclesExceptionExcel(dto, null);
        //endregion

        //region check date valid
        dto.setPlateNumber("plateNumber");
        dto.setEpc("epc");
        dto.setCreateDateFrom("222222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setCreateDateFrom("2/2/2020");
        dto.setCreateDateTo("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setCreateDateTo("2/1/2020");
        dto.setEffDateFrom("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setEffDateFrom("2/2/2020");
        dto.setEffDateTo("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setEffDateTo("2/1/2020");
        dto.setApprovalDateFrom("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setApprovalDateFrom("2/2/2020");
        dto.setApprovalDateTo("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setApprovalDateTo("2/1/2020");
        dto.setCancelDateFrom("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setCancelDateFrom("2/2/2020");
        dto.setCancelDateTo("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setCancelDateTo("2/1/2020");
        dto.setExpDateFrom("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setExpDateFrom("2/2/2020");
        dto.setExpDateTo("222");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setExpDateTo("2/1/2020");
        service.exportVehiclesExceptionExcel(dto, null);
        //endregion

        //region check date after
        dto.setCreateDateTo("2/2/2020");
        service.exportVehiclesExceptionExcel(dto, null);
        dto.setEffDateTo("2/2/2020");
        service.exportVehiclesExceptionExcel(dto, null);
        dto.setExpDateTo("2/2/2020");
        service.exportVehiclesExceptionExcel(dto, null);
        dto.setApprovalDateTo("2/2/2020");
        service.exportVehiclesExceptionExcel(dto, null);
        dto.setCancelDateTo("2/2/2020");
        service.exportVehiclesExceptionExcel(dto, null);
        //endregion

        dto.setModule(Constants.MODULE_MOT);
        dto.setExceptionGroup(1L);
        dto.setExceptionType("1");
        service.exportVehiclesExceptionExcel(dto, null);

        dto.setExceptionType("2");
        service.exportVehiclesExceptionExcel(dto, null);
    }

    @Ignore
    public void exportVehiclesExceptionPDFTest() throws Exception {
        service.exportVehiclesExceptionPDF(dto, null);

        dto = new VehiclesExceptionDTO();
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setModule("C");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setModule(Constants.MODULE_BOT);
        service.exportVehiclesExceptionPDF(dto, null);

        map.put("partner_type", "1");
        map.put("partner_code", "bot912");
        new MockUp<FnCommon>() {
            @mockit.Mock
            public Map<String, Object> getAttribute(Authentication authentication) {
                return map;
            }
        };
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setExceptionGroup(3L);
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setExceptionGroup(1L);
        dto.setModule(Constants.MODULE_MOT);
        service.exportVehiclesExceptionPDF(dto, null);

        //region check maxlength
        StringBuilder str = new StringBuilder("aaaaaaaaaa");
        for (int i = 0; i <= 10; i++) {
            str.append(str);
        }
        dto.setPlateNumber(str.toString());
        dto.setModule(Constants.MODULE_CRM);
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setPlateNumber("plateNumber");
        dto.setEpc(str.toString());
        service.exportVehiclesExceptionPDF(dto, null);
        //endregion

        //region check date valid
        dto.setPlateNumber("plateNumber");
        dto.setEpc("epc");
        dto.setCreateDateFrom("222222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setCreateDateFrom("2/2/2020");
        dto.setCreateDateTo("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setCreateDateTo("2/1/2020");
        dto.setEffDateFrom("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setEffDateFrom("2/2/2020");
        dto.setEffDateTo("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setEffDateTo("2/1/2020");
        dto.setApprovalDateFrom("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setApprovalDateFrom("2/2/2020");
        dto.setApprovalDateTo("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setApprovalDateTo("2/1/2020");
        dto.setCancelDateFrom("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setCancelDateFrom("2/2/2020");
        dto.setCancelDateTo("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setCancelDateTo("2/1/2020");
        dto.setExpDateFrom("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setExpDateFrom("2/2/2020");
        dto.setExpDateTo("222");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setExpDateTo("2/1/2020");
        service.exportVehiclesExceptionPDF(dto, null);
        //endregion

        //region check date after
        dto.setCreateDateTo("2/2/2020");
        service.exportVehiclesExceptionPDF(dto, null);
        dto.setEffDateTo("2/2/2020");
        service.exportVehiclesExceptionPDF(dto, null);
        dto.setExpDateTo("2/2/2020");
        service.exportVehiclesExceptionPDF(dto, null);
        dto.setApprovalDateTo("2/2/2020");
        service.exportVehiclesExceptionPDF(dto, null);

        dto.setCancelDateTo("2/2/2020");
        dto.setModule(Constants.MODULE_MOT);
        dto.setExceptionGroup(1L);
        dto.setExceptionType("2");
        given(parseThymeleafTemplate.parseThymeleafTemplate(null, null)).willReturn("html");
        given(parseThymeleafTemplate.generatePdfFromHtml(Mockito.anyString(), Mockito.anyString())).willReturn("C:\\Users\\TaiTV\\Desktop\\backup\\newFile.txt");
//        File myFile = Mockito.mock(File.class);
//        given(new File(Mockito.anyString())).willReturn(null);
        service.exportVehiclesExceptionPDF(dto, null);
        //endregion


        dto.setExceptionType("1");
        service.exportVehiclesExceptionPDF(dto, null);
    }

    @Test
    public void postUpdateStatusTest() throws IOException, JSONException {
        dto = new VehiclesExceptionDTO();
        service.postUpdateStatus(null, dto);

        List<Long> ids = Collections.singletonList(1L);
        dto.setExceptionListIds(ids);
        service.postUpdateStatus(null, dto);
    }

    @Test
    public void addVehiclesExceptionSingleTest() {
        dto = new VehiclesExceptionDTO();
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setModule("A");
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setExceptionGroup(Constants.EXCEPTION_GROUP.EXCEPTION);
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setPlateNumber("B38536");
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setEpc("Mockito");
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setExceptionType("1");
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setStatus(1L);
        service.addVehiclesExceptionSingle(null, dto, null);

        Date date = new Date(2020, 11, 04);
        Date date2 = new Date(2020, 11, 02);
        dto.setEffDate(date);
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setExpDate(date2);
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setExpDate(date);
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setLicensePlateType(1L);
        service.addVehiclesExceptionSingle(null, dto, null);

        dto.setDocumentName("aaa");
        service.addVehiclesExceptionSingle(null, dto, null);

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("a", "aaaa".getBytes());
        files.add(multipartFile);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setModule(Constants.MODULE_CRM);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionVehicleType(1L);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionType(Constants.EXCEPTION_TYPE.TICKET);
        dto.setExceptionVehicleType(1L);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionTicketType(1L);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionType("55");
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionGroup(Constants.EXCEPTION_GROUP.PRIORITIZE);
        dto.setExceptionType(Constants.EXCEPTION_TYPE.WHITELIST);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setWhiteListType(1L);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionType(Constants.EXCEPTION_TYPE.BLACKLIST);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setBlackListType(2L);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionType("55");
        service.addVehiclesExceptionSingle(null, dto, files);

        VehiclesExceptionDTO.stage stage = new VehiclesExceptionDTO.stage();
        List<VehiclesExceptionDTO.stage> stageList = new ArrayList<>();
        stageList.add(stage);
        dto.setStages(stageList);
        service.addVehiclesExceptionSingle(null, dto, files);

        stage.setStageId(1L);
        stageList = new ArrayList<>();
        stageList.add(stage);
        dto.setStages(stageList);
        service.addVehiclesExceptionSingle(null, dto, files);

        stage.setStageName("Stage");
        stageList = new ArrayList<>();
        stageList.add(stage);
        dto.setStages(stageList);

        VehiclesExceptionDTO.station station = new VehiclesExceptionDTO.station();
        List<VehiclesExceptionDTO.station> stationList = new ArrayList<>();
        stationList.add(station);
        dto.setStations(stationList);
        service.addVehiclesExceptionSingle(null, dto, files);

        station.setStationId(1L);
        stationList = new ArrayList<>();
        stationList.add(station);
        dto.setStations(stationList);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionGroup(3L);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionType(Constants.EXCEPTION_TYPE.TICKET);
        service.addVehiclesExceptionSingle(null, dto, files);
    }

    @Test
    public void addVehiclesExceptionSingleTest2() {
        //region success param
        dto = new VehiclesExceptionDTO();
        dto.setModule("A");
        dto.setExceptionGroup(1L);
        dto.setPlateNumber("B38536");
        dto.setEpc("Mockito");
        dto.setExceptionType("1");
        dto.setStatus(1L);
        Date date = new Date(2020, 11, 04);
        dto.setEffDate(date);
        dto.setExpDate(date);
        dto.setLicensePlateType(1L);
        dto.setDocumentName("aaa");
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("a", "aaaa".getBytes());
        files.add(multipartFile);
        dto.setModule(Constants.MODULE_CRM);
        dto.setExceptionVehicleType(1L);
        dto.setExceptionTicketType(1L);
        dto.setExceptionGroup(Constants.EXCEPTION_GROUP.PRIORITIZE);
        dto.setWhiteListType(1L);
        dto.setExceptionType(Constants.EXCEPTION_TYPE.BLACKLIST);
        dto.setBlackListType(2L);

        VehiclesExceptionDTO.stage stage = new VehiclesExceptionDTO.stage();
        List<VehiclesExceptionDTO.stage> stageList = new ArrayList<>();
        stage.setStageId(1L);
        stage.setStageName("Stage");
        stageList.add(stage);
        dto.setStages(stageList);

        VehiclesExceptionDTO.station station = new VehiclesExceptionDTO.station();
        List<VehiclesExceptionDTO.station> stationList = new ArrayList<>();
        station.setStationId(1L);
        station.setStationName("station");
        stationList.add(station);
        dto.setStations(stationList);
        //endregion

        Long idLimit = 1000000000000000000L;
        dto.setExceptionListId(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionListId(1L);
        dto.setStationId(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setStationId(1L);
        dto.setStageId(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setStageId(1L);
        dto.setVehicleTypeId(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setVehicleTypeId(1L);
        dto.setExceptionTicketType(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionTicketType(1L);
        dto.setLicensePlateType(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setLicensePlateType(1L);
        dto.setExceptionVehicleType(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setExceptionVehicleType(1L);
        dto.setRouterId(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setRouterId(1L);
        dto.setWhiteListType(idLimit);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setWhiteListType(1L);
        dto.setExceptionListIds(Collections.singletonList(idLimit));
        service.addVehiclesExceptionSingle(null, dto, files);

        StringBuilder strLimit = new StringBuilder("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        for (int i = 0; i < 5; i++) {
            strLimit.append(strLimit);
        }
        dto.setExceptionListIds(Collections.singletonList(1L));
        dto.setPlateNumber(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setPlateNumber("a");
        dto.setEpc(strLimit.toString());
        strLimit.toString().length();
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setEpc("a");
        dto.setTid(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setTid("a");
        dto.setRfidSerial(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setRfidSerial("a");
        dto.setDescription(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setDescription("a");
        dto.setCustomerName(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setCustomerName("a");
        dto.setContractId(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setContractId("a");
        dto.setCreateUser(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setCreateUser("a");
        dto.setApprovedUser(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setApprovedUser("a");
        dto.setProcessUser(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setProcessUser("a");
        dto.setCancelUser(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setCancelUser("a");
        dto.setStageName(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setStageName("a");
        dto.setStationName(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setStationName("a");
        dto.setRouterName(strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setRouterName("a");
        dto.setProcessCommnents(strLimit.toString() + strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setProcessCommnents("a");
        dto.setApproveCommnents(strLimit.toString() + strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setApproveCommnents("a");
        dto.setCancelCommnents(strLimit.toString() + strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setCancelCommnents("a");
        dto.setDocumentName(strLimit.toString() + strLimit.toString());
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setDocumentName("a");
        stage.setStageId(idLimit);
        stage.setStageName(strLimit.toString());
        stageList.add(stage);
        dto.setStages(stageList);
        service.addVehiclesExceptionSingle(null, dto, files);

        stageList = new ArrayList<>();
        stage.setStageId(1L);
        stageList.add(stage);
        dto.setStages(stageList);
        service.addVehiclesExceptionSingle(null, dto, files);

        dto.setStages(null);
        station.setStationId(idLimit);
        station.setStationName(strLimit.toString());
        stationList.add(station);
        dto.setStations(stationList);
        service.addVehiclesExceptionSingle(null, dto, files);

        stationList = new ArrayList<>();
        station.setStationId(1L);
        stationList.add(station);
        dto.setStations(stationList);
        service.addVehiclesExceptionSingle(null, dto, files);

    }

    @Test
    public void addVehiclesExceptionSingleTest3() {
        //region success param
        Authentication authentication = Mockito.spy(Authentication.class);
        dto = new VehiclesExceptionDTO();
        dto.setModule("A");
        dto.setExceptionGroup(1L);
        dto.setPlateNumber("B38536");
        dto.setEpc("Mockito");
        dto.setExceptionType("1");
        dto.setStatus(1L);
        Date date = new Date(2020, 11, 04);
        dto.setEffDate(date);
        dto.setExpDate(date);
        dto.setLicensePlateType(1L);
        dto.setDocumentName("aaa");
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("a", "aaaa".getBytes());
        files.add(multipartFile);
        dto.setModule(Constants.MODULE_CRM);
        dto.setExceptionVehicleType(1L);
        dto.setExceptionTicketType(1L);
        dto.setExceptionGroup(Constants.EXCEPTION_GROUP.PRIORITIZE);
        dto.setWhiteListType(1L);
        dto.setExceptionType(Constants.EXCEPTION_TYPE.BLACKLIST);
        dto.setBlackListType(2L);

        VehiclesExceptionDTO.stage stage = new VehiclesExceptionDTO.stage();
        List<VehiclesExceptionDTO.stage> stageList = new ArrayList<>();
        stage.setStageId(1L);
        stage.setStageName("Stage");
        stageList.add(stage);
        //endregion

        //region test case
        stage.setStageId(2L);
        stage.setStageName("Stage2");
        stageList.add(stage);
        dto.setStages(stageList);

        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(resultSelectEntity);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        ServicePlanEntity servicePlanEntity = new ServicePlanEntity();
        servicePlanEntity.setOcsCode("aaa");
        List<ServicePlanEntity> servicePlanEntityList = new ArrayList<>();
        servicePlanEntityList.add(servicePlanEntity);
        resultSelectEntity.setListData(servicePlanEntityList);
        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(resultSelectEntity);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        dto.setStages(null);
        VehiclesExceptionDTO.station station = new VehiclesExceptionDTO.station();
        List<VehiclesExceptionDTO.station> stationList = new ArrayList<>();
        station.setStationId(1L);
        station.setStationName("station");
        stationList.add(station);
        station.setStationId(2L);
        station.setStationName("station2");
        stationList.add(station);
        dto.setStations(stationList);
        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(new ResultSelectEntity());
        service.addVehiclesExceptionSingle(authentication, dto, files);

        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(resultSelectEntity);
        given(vehiclesExceptionRepository.getCountExceptionList(any(ExceptionListEntity.class))).willReturn(1);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        given(vehiclesExceptionRepository.getCountExceptionList(any(ExceptionListEntity.class))).willReturn(0);
        List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        ExceptionListEntity entity = ExceptionListEntity.builder().exceptionListId(1L).build();
        exceptionListEntities.add(entity);
        given(vehiclesExceptionRepository.saveAllExceptionList(any())).willReturn(exceptionListEntities);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        stationList = new ArrayList<>();
        stationList.add(station);
        dto.setStations(stationList);
        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(new ResultSelectEntity());
        service.addVehiclesExceptionSingle(authentication, dto, files);

        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(resultSelectEntity);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        stageList = new ArrayList<>();
        stageList.add(stage);
        dto.setStations(null);
        dto.setStages(stageList);
        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(new ResultSelectEntity());
        service.addVehiclesExceptionSingle(authentication, dto, files);

        given(vehiclesExceptionRepository.getOCSCode(any(ServicePlanEntity.class), anyString())).willReturn(resultSelectEntity);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        stationList = new ArrayList<>();
        station = new VehiclesExceptionDTO.station();
        stationList.add(station);
        dto.setStations(stationList);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        stationList = new ArrayList<>();
        station = new VehiclesExceptionDTO.station();
        station.setStationId(1L);
        stationList.add(station);
        dto.setStations(stationList);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        stageList = new ArrayList<>();
        stage = new VehiclesExceptionDTO.stage();
        stageList.add(stage);
        dto.setStages(stageList);
        service.addVehiclesExceptionSingle(authentication, dto, files);

        stageList = new ArrayList<>();
        stage = new VehiclesExceptionDTO.stage();
        stage.setStageId(1L);
        stageList.add(stage);
        dto.setStages(stageList);
        service.addVehiclesExceptionSingle(authentication, dto, files);
        //endregion
    }

    @Test
    public void updateVehiclesExceptionTest() {
        //region success param
        Authentication authentication = Mockito.spy(Authentication.class);
        dto = new VehiclesExceptionDTO();
        dto.setModule("A");
        dto.setExceptionGroup(1L);
        dto.setPlateNumber("B38536");
        dto.setEpc("Mockito");
        dto.setExceptionType("1");
        dto.setStatus(1L);
        Date date = new Date(2020, 11, 04);
        dto.setEffDate(date);
        dto.setExpDate(date);
        dto.setLicensePlateType(1L);
        dto.setDocumentName("aaa");
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("a", "aaaa".getBytes());
        files.add(multipartFile);
        dto.setModule(Constants.MODULE_CRM);
        dto.setExceptionVehicleType(1L);
        dto.setExceptionTicketType(1L);
        dto.setExceptionGroup(Constants.EXCEPTION_GROUP.PRIORITIZE);
        dto.setWhiteListType(1L);
        dto.setExceptionType(Constants.EXCEPTION_TYPE.BLACKLIST);
        dto.setBlackListType(2L);

        VehiclesExceptionDTO.stage stage = new VehiclesExceptionDTO.stage();
        List<VehiclesExceptionDTO.stage> stageList = new ArrayList<>();
        stage.setStageId(1L);
        stage.setStageName("Stage");
        stageList.add(stage);
        //endregion

        service.updateVehiclesException(authentication, dto, files);
        dto.setStageId(1L);
        dto.setStationId(1L);
        service.updateVehiclesException(authentication, dto, files);

        dto.setStageName("hi");
        dto.setStationName("hi");
        service.updateVehiclesException(authentication, dto, files);

        dto.setExceptionListId(1L);
        dto.setAttachmentFileIds(Collections.singletonList(1L));
        given(vehiclesExceptionRepository.getCountDatAttachmentFileAfterDelete(dto.getAttachmentFileIds(), 1L)).willReturn(0);
        service.updateVehiclesException(authentication, dto, null);

        given(vehiclesExceptionRepository.getCountDatAttachmentFileAfterDelete(dto.getAttachmentFileIds(), 1L)).willReturn(1);
        given(vehiclesExceptionRepository.getCountExceptionList(any())).willReturn(1);
        service.updateVehiclesException(authentication, dto, files);

        given(vehiclesExceptionRepository.getCountExceptionList(any())).willReturn(0);
        ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
        exceptionListEntity.setExceptionListId(1L);
        given(vehiclesExceptionRepository.saveExceptionList(any())).willReturn(exceptionListEntity);
        service.updateVehiclesException(authentication, dto, files);
    }

    @Test
    public void postCreateVehiclesExceptionFileTest() {
        Authentication authentication = Mockito.spy(Authentication.class);
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile excelFile = new MockMultipartFile("a", "aaaa".getBytes());
        files.add(excelFile);
        dto = new VehiclesExceptionDTO();

        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        dto.setModule("A");
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        dto.setExceptionGroup(1L);
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        dto.setStatus(1L);
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        dto.setDocumentName("aaa");
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, null);

        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        dto.setStationId(1L);
        dto.setStationName("hi");
        dto.setStageId(1L);
        dto.setStageName("hi");
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        excelFile = new MockMultipartFile("a.xlsx",".xlsx", "application/vnd.ms-excel",  "aaaa".getBytes());
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);

        XSSFWorkbook mockWorkbook = mock(XSSFWorkbook.class);
        service.postCreateVehiclesExceptionFile(authentication, dto, excelFile, files);


    }

//    @Test
//    public void Test() {
//        service.updateVehiclesException(authentication, dto, files);
//
//    }

}
