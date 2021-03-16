package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.ReqActivationCheckDTO;
import com.viettel.etc.dto.boo.ReqExceptionListDTO;
import com.viettel.etc.dto.boo.ResActivationCheckDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.repositories.VehiclesExceptionRepository;
import com.viettel.etc.repositories.tables.VehicleRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.ExceptionListServiceJPA;
import com.viettel.etc.services.tables.ServicePlanServiceJPA;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.exports.ConfigFileExport;
import com.viettel.etc.utils.exports.ConfigHeaderExport;
import com.viettel.etc.utils.exports.PdfHtmlTemplateExporter;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Autogen class: Phuong tien ngoai le, uu tien, cam
 *
 * @author ToolGen
 * @date Mon Jul 20 18:48:49 ICT 2020
 */
@Log4j
@Service
public class VehiclesExceptionServiceImpl implements VehiclesExceptionService {

    @Autowired
    VehiclesExceptionRepository vehiclesExceptionRepository;

    @Autowired
    ExceptionListServiceJPA exceptionListServiceJPA;

    @Autowired
    PdfHtmlTemplateExporter parseThymeleafTemplate;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    FileService fileService;

    @Autowired
    Boo1ServiceImpl boo1Service;

    @Autowired
    ServicePlanServiceJPA servicePlanServiceJPA;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    OCSService ocsService;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    private VehicleGroupService vehicleGroupService;

    @Autowired
    JedisCacheService jedisCacheService;

    @Autowired
    private StageService stageService;

    @Autowired
    private StationService stationService;

    @Autowired
    private PromotionService promotionService;

    @Value("${ws.boo1.online-event.reg}")
    String onlineEventUrl;

    @Value("${ws.dmdc.stage.get}")
    private String stageUrl;

    @Value("${ws.dmdc.stations.boo}")
    private String stationUrl;

    @Value("${ws.ocs.vehicle-offer-id}")
    String vehicleOfferId;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VehiclesExceptionServiceImpl.class);

    private static final String BLACK_LIST = "BL";
    private static final String WHILE_LIST = "WL";
    private static final String TICKET_TYPE = "GV";
    private static final String BOO1 = "BOO1";
    private static final String BOO2 = "BOO2";

    private final int ID = 0;
    private final int NAME = 1;

    private final int PLATE_NUMBER = 0;
    private final int PLATE_TYPE = 1;
    private final int EXCEPTION_LIST_TYPE = 2;//loai ngoai le
    private final int EFF_DATE = 3;
    private final int EXP_DATE = 4;
    private final int PROMOTION = 5;
    private final int STATION = 6;
    private final int STAGES = 7;
//    private final int EXCEPTION_VEHICLE_TYPE = 8;//loai xe mien giam

    private final int RESULT = 8;
    private final int DESC = 9;

    /**
     * API xuat file Excel
     *
     * @param itemParamsEntity params client
     * @param authentication   params client
     * @return Object
     */
    @Override
    public Object exportVehiclesExceptionExcel(VehiclesExceptionDTO itemParamsEntity, Authentication authentication) {
        Map<String, Object> objectMap = new HashMap<>();
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        String partnerType = FnCommon.isNullObject(map.get("partner_type")) ? null : map.get("partner_type").toString();
        String partnerCode = FnCommon.isNullObject(map.get("partner_code")) ? null : map.get("partner_code").toString();
        boolean isAdmin = (FnCommon.isNullOrEmpty(partnerType) || !"bot".equalsIgnoreCase(partnerType));

        ErrorApp errorApp = validateExport(itemParamsEntity, partnerCode, isAdmin);
        if (errorApp.getCode() != 1) {
            objectMap.put("error", errorApp);
            return objectMap;
        }

        List<VehiclesExceptionDTO> dataExport = vehiclesExceptionRepository.getDataExport(itemParamsEntity, partnerCode, isAdmin);

        //region Header
        List<ConfigHeaderExport> headerExports = new ArrayList<>();
        ConfigHeaderExport columnSheet;
        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("common.label.stt"), "stt", "RIGHT", "NUMBER", 2000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stageStation"), "stationStageName", "LEFT", "STRING", 6000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plateNumber"), "plateNumber", "LEFT", "STRING", 4000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plateType"), "plateName", "LEFT", "STRING", 4000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.epc"), "rfidSerial", "LEFT", "STRING", 4000);
        headerExports.add(columnSheet);

        int mergeTitleEndIndex;

        if (itemParamsEntity.getModule().equalsIgnoreCase(Constants.MODULE_MOT) && itemParamsEntity.getExceptionGroup() == 1) {
            String title;
            mergeTitleEndIndex = 12;
            if ("1".equals(itemParamsEntity.getExceptionType())) {
                title = jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.vehicleType");
            } else {
                title = jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ticketType");
            }
            columnSheet = new ConfigHeaderExport(title, "content", "LEFT", "STRING", 5000);
            headerExports.add(columnSheet);
        } else {
            columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.vehicleType"), "vehicleTypeName", "LEFT", "STRING", 4000);
            headerExports.add(columnSheet);

            if (itemParamsEntity.getExceptionGroup() == 1) {
                mergeTitleEndIndex = 14;
                columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.type.1"), "exceptionName", "LEFT", "STRING", 4000);
                headerExports.add(columnSheet);
                columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.content"), "content", "LEFT", "STRING", 5000);
                headerExports.add(columnSheet);
            } else {
                mergeTitleEndIndex = 13;
                columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.type.2"), "exceptionName", "LEFT", "STRING", 4000);
                headerExports.add(columnSheet);
            }
        }

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.note"), "description", "LEFT", "STRING", 4000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("common.label.status"), "statusName", "LEFT", "STRING", 4000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDate"), "effTime", "CENTER", "STRING", 8000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("common.label.createDate"), "createDate", "CENTER", "DATE", 4000);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("common.label.createUser"), "createUser", "LEFT", "STRING", 3500);
        headerExports.add(columnSheet);

        columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("common.label.approveDate"), "approvedDate", "CENTER", "DATE", 4000);
        headerExports.add(columnSheet);

        if (!itemParamsEntity.getModule().equalsIgnoreCase(Constants.MODULE_CRM)) {
            columnSheet = new ConfigHeaderExport(jedisCacheService.getMessageErrorByKey("common.label.cancelDate"), "cancelDate", "CENTER", "DATE", 4000);
            headerExports.add(columnSheet);
        }
        //endregion

        // Config infor
        String sheetName = "Sheet 1";
        String title = itemParamsEntity.getExceptionGroup() == 1 ?
                "crm.vehicles.exception.export.title.1" : "crm.vehicles.exception.export.title.2";

        ConfigFileExport configFileExport = new ConfigFileExport(
                dataExport, sheetName, jedisCacheService.getMessageErrorByKey(title),
                null, 2,
                0, mergeTitleEndIndex,
                true, headerExports, null,
                true
        );

        List<ConfigFileExport> fileExports = new ArrayList<>();
        fileExports.add(configFileExport);
        objectMap.put("data", FnCommon.exportExcel(fileExports));
        objectMap.put("error", ErrorApp.SUCCESS);
        return objectMap;
    }

    /**
     * API xuat file PDF
     *
     * @param itemParamsEntity params client
     * @param authentication   params client
     * @return Object
     */
    @Override
    public Object exportVehiclesExceptionPDF(VehiclesExceptionDTO itemParamsEntity, Authentication authentication) {
        /*
        ==========================================================
        itemParamsEntity: params nguoi dung truyen len
        ==========================================================
        */
        Map<String, Object> objectMap = new HashMap<>();
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        String partnerType = FnCommon.isNullObject(map.get("partner_type")) ? null : map.get("partner_type").toString();
        String partnerCode = FnCommon.isNullObject(map.get("partner_code")) ? null : map.get("partner_code").toString();
        boolean isAdmin = (FnCommon.isNullOrEmpty(partnerType) || !"bot".equalsIgnoreCase(partnerType));
        ErrorApp errorApp = validateExport(itemParamsEntity, partnerCode, isAdmin);
        if (errorApp.getCode() != 1) {
            objectMap.put("error", errorApp);
            return objectMap;
        }

        List<VehiclesExceptionDTO> dataExport = vehiclesExceptionRepository.getDataExport(itemParamsEntity, partnerCode, isAdmin);

//        File file = new File(exportRootPath);
//        if (!file.exists()) {
//            file.mkdirs();
//        }

        String outputPath = System.getProperty("user.dir") + File.separator + "export.pdf";
        String title = itemParamsEntity.getExceptionGroup() == 1 ? "Danh sách xe ngoại lệ" : "Danh sách xe ưu tiên & cấm";
        if (itemParamsEntity.getModule().equalsIgnoreCase(Constants.MODULE_MOT)) {
            if ("1".equalsIgnoreCase(itemParamsEntity.getExceptionType())) {
                title = "DANH SÁCH NGOẠI LỆ THEO LOẠI XE";
            }
            if ("2".equalsIgnoreCase(itemParamsEntity.getExceptionType())) {
                title = "DANH SÁCH NGOẠI LỆ THEO GIÁ VÉ";
            }
        }
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("items", dataExport);
        dataMap.put("title", title);
        dataMap.put("exceptionGroup", itemParamsEntity.getExceptionGroup());

        String temp = "template/vehicleException_pdf_template";
        String html = parseThymeleafTemplate.parseThymeleafTemplate(temp, dataMap);
        try {
            String filePath = parseThymeleafTemplate.generatePdfFromHtml(html, outputPath);
            byte[] bytes = IOUtils.toByteArray(new FileInputStream(new File(filePath)));
            objectMap.put("data", bytes);
            objectMap.put("error", ErrorApp.SUCCESS);
            return objectMap;
        } catch (Exception e) {
            Logger.getLogger(VehiclesExceptionServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        objectMap.put("error", ErrorApp.ERR_DATA);

        return objectMap;
    }

    public ErrorApp validateExport(VehiclesExceptionDTO item, String partnerCode, boolean isAdmin) {
        ErrorApp errorApp = ErrorApp.ERR_DATA;
        if (item == null) {
            return errorApp;
        }

        if (FnCommon.isNullOrEmpty(item.getModule())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.empty"));
            return errorApp;
        }

        if (!item.getModule().equalsIgnoreCase(Constants.MODULE_CRM)
                && !item.getModule().equalsIgnoreCase(Constants.MODULE_BOT)
                && !item.getModule().equalsIgnoreCase(Constants.MODULE_MOT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.invalid"));
            return errorApp;
        }

        if (item.getModule().equalsIgnoreCase(Constants.MODULE_BOT) && FnCommon.isNullOrEmpty(partnerCode) && !isAdmin) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.partnerCode.empty"));
            return errorApp;
        }

        if (FnCommon.isNullObject(item.getExceptionGroup())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.empty"));
            return errorApp;
        }

        if (item.getExceptionGroup() != 1L && item.getExceptionGroup() != 2L) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.invalid"));
            return errorApp;
        }

        if (item.getModule().equalsIgnoreCase(Constants.MODULE_MOT)
                && item.getExceptionGroup() == 1L
                && FnCommon.isNullOrEmpty(item.getExceptionType())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.mot.exceptionType.empty"));
            return errorApp;
        }

        //region check maxlength
        if (!FnCommon.isNullOrEmpty(item.getPlateNumber()) && item.getPlateNumber().length() > 100) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plateNumber.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEpc()) && item.getEpc().length() > 50) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.epc.length"));
            return errorApp;
        }
        //endregion

        //region check date valid
        if (!FnCommon.isNullOrEmpty(item.getCreateDateFrom())
                && !FnCommon.isDate(item.getCreateDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCreateDateTo())
                && !FnCommon.isDate(item.getCreateDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEffDateFrom())
                && !FnCommon.isDate(item.getEffDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEffDateTo())
                && !FnCommon.isDate(item.getEffDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovalDateFrom())
                && !FnCommon.isDate(item.getApprovalDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvalDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovalDateTo())
                && !FnCommon.isDate(item.getApprovalDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvalDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelDateFrom())
                && !FnCommon.isDate(item.getCancelDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.cancelDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelDateTo())
                && !FnCommon.isDate(item.getCancelDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.cancelDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getExpDateFrom())
                && !FnCommon.isDate(item.getExpDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.expDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getExpDateTo())
                && !FnCommon.isDate(item.getExpDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.expDateTo.invalid"));
            return errorApp;
        }
        //endregion

        //region check date after
        if (!FnCommon.isNullOrEmpty(item.getCreateDateTo())
                && !FnCommon.isNullOrEmpty(item.getCreateDateFrom())
                && FnCommon.isAfterDay(item.getCreateDateFrom(), item.getCreateDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEffDateFrom())
                && !FnCommon.isNullOrEmpty(item.getEffDateTo())
                && FnCommon.isAfterDay(item.getEffDateFrom(), item.getEffDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getExpDateFrom())
                && !FnCommon.isNullOrEmpty(item.getExpDateTo())
                && FnCommon.isAfterDay(item.getExpDateFrom(), item.getExpDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.expDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovalDateFrom())
                && !FnCommon.isNullOrEmpty(item.getApprovalDateTo())
                && FnCommon.isAfterDay(item.getApprovalDateFrom(), item.getApprovalDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvalDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelDateFrom())
                && !FnCommon.isNullOrEmpty(item.getCancelDateTo())
                && FnCommon.isAfterDay(item.getCancelDateFrom(), item.getCancelDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.cancelDate.after"));
            return errorApp;
        }
        //endregion

        return ErrorApp.SUCCESS;
    }

    /**
     * API tim kiem phuong tien ngoai le/ uu tien & cam
     *
     * @param itemParamsEntity params client
     * @return Object
     */
    @Override
    public Object getDataVehiclesException(VehiclesExceptionDTO itemParamsEntity, Authentication authentication) {
        /*
        ==========================================================
        itemParamsEntity: params nguoi dung truyen len
        ==========================================================
        */
        Map<String, Object> objectMap = new HashMap<>();
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        String partnerType = FnCommon.isNullObject(map.get("partner_type")) ? null : map.get("partner_type").toString();
        String partnerCode = FnCommon.isNullObject(map.get("partner_code")) ? null : map.get("partner_code").toString();
        boolean isAdmin = (FnCommon.isNullOrEmpty(partnerType) || !"bot".equalsIgnoreCase(partnerType));
        ErrorApp errorApp = validateSearch(itemParamsEntity, partnerCode, isAdmin);
        if (errorApp.getCode() != 1) {
            objectMap.put("error", errorApp);
            return objectMap;
        }

        ResultSelectEntity dataResult = vehiclesExceptionRepository.getDataVehiclesException(itemParamsEntity, partnerCode, isAdmin);
        objectMap.put("data", dataResult);
        objectMap.put("error", ErrorApp.SUCCESS);
        return objectMap;
    }

    /**
     * API tim kiem phuong tien ngoai le/ uu tien & cam
     *
     * @param authentication:   thong tin nguoi dung
     * @param params:           params client
     * @return
     */
    @Override
    public Object getVehiclesException(VehiclesExceptionDTO params, Authentication authentication) {
        return vehiclesExceptionRepository.getVehiclesException(params);
    }

    public ErrorApp validateSearch(VehiclesExceptionDTO item, String partnerCode, boolean isAdmin) {
        ErrorApp errorApp = ErrorApp.ERR_DATA;
        if (item == null) {
            return errorApp;
        }

        if (FnCommon.isNullOrEmpty(item.getModule())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.empty"));
            return errorApp;
        }

        if (!item.getModule().equalsIgnoreCase(Constants.MODULE_CRM)
                && !item.getModule().equalsIgnoreCase(Constants.MODULE_BOT)
                && !item.getModule().equalsIgnoreCase(Constants.MODULE_MOT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.invalid"));
            return errorApp;
        }

        if (item.getModule().equalsIgnoreCase(Constants.MODULE_BOT) && FnCommon.isNullOrEmpty(partnerCode) && !isAdmin) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.partnerCode.empty"));
            return errorApp;
        }

        if (FnCommon.isNullObject(item.getExceptionGroup())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.empty"));
            return errorApp;
        }

        if (item.getExceptionGroup() != 1L && item.getExceptionGroup() != 2L) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.invalid"));
            return errorApp;
        }

        if (item.getModule().equalsIgnoreCase(Constants.MODULE_MOT)
                && item.getExceptionGroup() == 1L
                && FnCommon.isNullOrEmpty(item.getExceptionType())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.mot.exceptionType.empty"));
            return errorApp;
        }

        //region check maxlength
        if (!FnCommon.isNullOrEmpty(item.getPlateNumber()) && item.getPlateNumber().length() > 100) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plateNumber.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEpc()) && item.getEpc().length() > 50) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.epc.length"));
            return errorApp;
        }
        //endregion

        //region check date valid
        if (!FnCommon.isNullOrEmpty(item.getCreateDateFrom())
                && !FnCommon.isDate(item.getCreateDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCreateDateTo())
                && !FnCommon.isDate(item.getCreateDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEffDateFrom())
                && !FnCommon.isDate(item.getEffDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEffDateTo())
                && !FnCommon.isDate(item.getEffDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovalDateFrom())
                && !FnCommon.isDate(item.getApprovalDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvalDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovalDateTo())
                && !FnCommon.isDate(item.getApprovalDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvalDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelDateFrom())
                && !FnCommon.isDate(item.getCancelDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.cancelDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelDateTo())
                && !FnCommon.isDate(item.getCancelDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.cancelDateTo.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getExpDateFrom())
                && !FnCommon.isDate(item.getExpDateFrom(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.expDateFrom.invalid"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getExpDateTo())
                && !FnCommon.isDate(item.getExpDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.expDateTo.invalid"));
            return errorApp;
        }
        //endregion

        //region check date after
        if (!FnCommon.isNullOrEmpty(item.getCreateDateTo())
                && !FnCommon.isNullOrEmpty(item.getCreateDateFrom())
                && FnCommon.isAfterDay(item.getCreateDateFrom(), item.getCreateDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEffDateFrom())
                && !FnCommon.isNullOrEmpty(item.getEffDateTo())
                && FnCommon.isAfterDay(item.getEffDateFrom(), item.getEffDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.effDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getExpDateFrom())
                && !FnCommon.isNullOrEmpty(item.getExpDateTo())
                && FnCommon.isAfterDay(item.getExpDateFrom(), item.getExpDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.expDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovalDateFrom())
                && !FnCommon.isNullOrEmpty(item.getApprovalDateTo())
                && FnCommon.isAfterDay(item.getApprovalDateFrom(), item.getApprovalDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvalDate.after"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelDateFrom())
                && !FnCommon.isNullOrEmpty(item.getCancelDateTo())
                && FnCommon.isAfterDay(item.getCancelDateFrom(), item.getCancelDateTo(), Constants.COMMON_DATE_FORMAT)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.cancelDate.after"));
            return errorApp;
        }
        //endregion

        return ErrorApp.SUCCESS;
    }

    /**
     * API lay thong tin chi tiet xe ngoai le
     *
     * @param exceptionListId params client
     * @return Object
     */
    @Override
    public Object viewDetail(Long exceptionListId) {
        VehiclesExceptionDTO result = vehiclesExceptionRepository.viewDetail(exceptionListId);
        if (result.getVehicleId() != null && vehicleServiceJPA.existsById(result.getVehicleId())) {
            result.setVehicleGroupId(vehicleServiceJPA.getOne(result.getVehicleId()).getVehicleGroupId());
        }
        return result;
    }

    /**
     * Trinh duyet xe uu tien/cam/ngoai (Thuc hien update status in DB Exception_List theo Exception_List_Id)
     *
     * @param authentication   params client
     * @param itemParamsEntity params client
     * @return Object
     */
    @Override
    public Object postUpdateStatus(Authentication authentication, VehiclesExceptionDTO itemParamsEntity) throws IOException, JSONException {
        Map<String, Object> objectMap = new HashMap<>();

        ErrorApp errorApp = ErrorApp.ERR_DATA;
        objectMap.put("error", ErrorApp.SUCCESS);
        if (itemParamsEntity.getStatus() == null) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.status.empty"));
            objectMap.put("error", errorApp);
        } else if (itemParamsEntity.getExceptionListIds() == null || itemParamsEntity.getExceptionListIds().size() <= 0) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exception_list_id.empty"));
            objectMap.put("error", errorApp);
        } else {
            vehiclesExceptionRepository.postUpdateStatus(itemParamsEntity.getStatus(), itemParamsEntity.getExceptionListIds());
        }
        return objectMap;
    }

    @Override
    public ApproveVehicleExceptionDTO postApproval(VehiclesExceptionDTO params, Authentication authentication) throws Exception {
        if (params.getExceptionListIds() == null || params.getExceptionListIds().size() <= 0) {
            throw new EtcException("crm.vehicles.exception.exception_list_id.empty");
        }
        boolean isWarning = false;
        boolean doTransferBoo1 = false;
        ApproveVehicleExceptionDTO responseToClient = new ApproveVehicleExceptionDTO();
        ApproveVehicleExceptionDTO.Mess mess = new ApproveVehicleExceptionDTO.Mess();
        ApproveVehicleExceptionDTO.DataList data = new ApproveVehicleExceptionDTO.DataList();
        List<ApproveVehicleExceptionDTO.Exception> listSuccess = new LinkedList<>();
        List<ApproveVehicleExceptionDTO.Exception> listError = new LinkedList<>();
        List<ApproveVehicleExceptionDTO.Exception> listWarning = new LinkedList<>();
        StringBuilder messSuccess = new StringBuilder();
        StringBuilder messError = new StringBuilder();
        StringBuilder messWarning = new StringBuilder();
        params.setStatus(Constants.EXCEPTION_APPROVED);
        List<Long> listIdStatus = params.getExceptionListIds();
        List<Long> listIdSync = new LinkedList<>();
        List<Long> listIdRemove = new LinkedList<>();
        String ocs;
        for (Long exceptionId : listIdStatus) {
            ApproveVehicleExceptionDTO.Exception exceptionResponse = new ApproveVehicleExceptionDTO.Exception();
            exceptionResponse.setExceptionId(exceptionId);
            ExceptionListEntity exceptionListEntity = exceptionListServiceJPA.getOne(exceptionId);
            if (exceptionListEntity == null) {
                listIdRemove.add(exceptionId);
                exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.id"));
                listError.add(exceptionResponse);
                messError.append(exceptionId).append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.id")).append("\n");
                continue;
            }
            if (!Constants.EXCEPTION_NEW.equals(exceptionListEntity.getStatus())) {
                listIdRemove.add(exceptionId);
                exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.status"));
                listError.add(exceptionResponse);
                messError.append(exceptionListEntity.getPlateNumber()).append(" ").append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.status")).append("\n");
                continue;
            }
            Long stationIn = null;
            Long stationOut = null;
            boolean isBoo2 = false;
            Long servicePlanType = null;
            Long vehicleGroupId = null;

            if (exceptionListEntity.getLicensePlateType() != null) {
                exceptionListEntity.setPlateTypeCode(exceptionListEntity.getLicensePlateType().toString());
            }
            String plateType = FnCommon.mappingPlateTypeBOO2ToBOO1(String.valueOf(exceptionListEntity.getLicensePlateType()));
            exceptionListEntity.setOrgPlateNumber(exceptionListEntity.getPlateNumber() + plateType);
            ReqActivationCheckDTO req = new ReqActivationCheckDTO();
            req.setPlate(FnCommon.getPlateNumberBoo1(exceptionListEntity.getPlateNumber(), plateType));
            req.setRequest_id(System.currentTimeMillis());
            req.setRequest_datetime(System.currentTimeMillis());
            ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
            switch (exceptionListEntity.getExceptionType()) {
                case Constants.EXCEPTION_VEHICLE:
                    servicePlanType = Constants.SERVICE_PLAN_TYPE_TURN;
                    vehicleGroupId = Long.valueOf(exceptionListEntity.getExceptionVehicleType());
                    break;
                case Constants.EXCEPTION_TICKET:
                    servicePlanType = Constants.SERVICE_PLAN_TYPE_TURN;
                    if (Constants.RACH_MIEU_STATION.equals(exceptionListEntity.getStationId())) {
                        if (exceptionListEntity.getVehicleTypeId() != null && (exceptionListEntity.getVehicleTypeId().equals(1L) || exceptionListEntity.getVehicleTypeId().equals(3L))) {
                            AddVehicleRequestDTO dataParams = new AddVehicleRequestDTO();
                            if (vehicleServiceJPA.isBoo2ActVehicle(exceptionListEntity.getPlateNumber(), String.valueOf(exceptionListEntity.getLicensePlateType()))) {  // xe boo2 lay trong bang vehicle
                                VehicleEntity vehicleEntity = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(exceptionListEntity.getPlateNumber(), String.valueOf(exceptionListEntity.getLicensePlateType()));
                                dataParams.setVehicleTypeId(vehicleEntity.getVehicleTypeId());
                                dataParams.setCargoWeight(vehicleEntity.getCargoWeight());
                                dataParams.setNetWeight(vehicleEntity.getNetWeight());
                                dataParams.setGrossWeight(vehicleEntity.getGrossWeight());
                                dataParams.setPullingWeight(vehicleEntity.getPullingWeight());
                                dataParams.setSeatNumber(vehicleEntity.getSeatNumber());
                                dataParams.setStationId(Constants.RACH_MIEU_STATION);
                            } else if (res != null && Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                                // xe boo1 lay thong tin tu ban tin 300
                                dataParams.setVehicleTypeId(Long.parseLong(res.getRegister_vehicle_type()));
                                dataParams.setSeatNumber(res.getSeat());
                                dataParams.setCargoWeight(res.getWeight_goods());
                                dataParams.setGrossWeight(res.getWeight_all());
                                dataParams.setStationId(Constants.RACH_MIEU_STATION);
                            } else {
                                listIdRemove.add(exceptionId);
                                exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.vehicle"));
                                listError.add(exceptionResponse);
                                messError.append(exceptionListEntity.getPlateNumber()).append(" ").append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.vehicle")).append("\n");
                                continue;
                            }
                            List<String> stringList = vehicleGroupService.getVehicleGroupById(FnCommon.getStringToken(authentication), dataParams);
                            if (stringList != null && stringList.size() == 2) {
                                vehicleGroupId = Long.parseLong(stringList.get(0));
                            }
                        }
                    }
                    if (vehicleGroupId == null) {
                        vehicleGroupId = exceptionListEntity.getVehicleTypeId();
                    }
                    ServicePlanEntity servicePlan;
                    if (exceptionListEntity.getStageId() != null) {
                        servicePlan = servicePlanServiceJPA.getByPromotionAndStageAndGroupAndType(
                                exceptionListEntity.getPromotionId(), exceptionListEntity.getStageId(),
                                vehicleGroupId, Constants.SERVICE_PLAN_TYPE_TURN);
                        if (servicePlan != null) {
                            exceptionListEntity.setPriceTurn(servicePlan.getFee());
                        }
                        servicePlan = servicePlanServiceJPA.getByPromotionAndStageAndGroupAndType(
                                exceptionListEntity.getPromotionId(), exceptionListEntity.getStageId(),
                                vehicleGroupId, Constants.SERVICE_PLAN_TYPE_MONTH);
                        if (servicePlan != null) {
                            exceptionListEntity.setPriceMonthly(servicePlan.getFee());
                        }
                        servicePlan = servicePlanServiceJPA.getByPromotionAndStageAndGroupAndType(
                                exceptionListEntity.getPromotionId(), exceptionListEntity.getStageId(),
                                vehicleGroupId, Constants.SERVICE_PLAN_TYPE_QUART);
                    } else {
                        servicePlan = servicePlanServiceJPA.getByPromotionAndStationAndGroupAndType(
                                exceptionListEntity.getPromotionId(), exceptionListEntity.getStationId(),
                                vehicleGroupId, Constants.SERVICE_PLAN_TYPE_TURN);
                        if (servicePlan != null) {
                            exceptionListEntity.setPriceTurn(servicePlan.getFee());
                        }
                        servicePlan = servicePlanServiceJPA.getByPromotionAndStationAndGroupAndType(
                                exceptionListEntity.getPromotionId(), exceptionListEntity.getStationId(),
                                vehicleGroupId, Constants.SERVICE_PLAN_TYPE_MONTH);
                        if (servicePlan != null) {
                            exceptionListEntity.setPriceMonthly(servicePlan.getFee());
                        }
                        servicePlan = servicePlanServiceJPA.getByPromotionAndStationAndGroupAndType(
                                exceptionListEntity.getPromotionId(), exceptionListEntity.getStationId(),
                                vehicleGroupId, Constants.SERVICE_PLAN_TYPE_QUART);
                    }
                    if (servicePlan != null) {
                        exceptionListEntity.setPriceQuarterly(servicePlan.getFee());
                    }
                    break;
                case Constants.EXCEPTION_PRIORITY:
                    servicePlanType = Constants.SERVICE_PLAN_TYPE_PRIORITY;
                    vehicleGroupId = null;
                    break;
                case Constants.EXCEPTION_BAN:
                    servicePlanType = Constants.SERVICE_PLAN_TYPE_BAN;
                    vehicleGroupId = null;
            }
            if (exceptionListEntity.getStageId() != null) {
                String requestUrl = stageUrl.replace("{stageId}", exceptionListEntity.getStageId().toString());
                String stageResponse = FnCommon.doGetRequest(requestUrl, null, FnCommon.getStringToken(authentication));
                if (stageResponse != null) {
                    JSONObject stageRes = new JSONObject(stageResponse);
                    JSONObject stageData = stageRes.getJSONObject("data");
                    JSONObject in = stageData.getJSONObject("stationIn");
                    JSONObject out = stageData.getJSONObject("stationOut");
                    if (Constants.BOO2.equals(stageData.getString("booCode"))) {
                        isBoo2 = true;
                    }
                    stationIn = in.getLong("id");
                    stationOut = out.getLong("id");
                } else {
                    exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.cannot.connect.categories"));
                    listError.add(exceptionResponse);
                    messError.append(exceptionListEntity.getPlateNumber()).append(" ").append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.cannot.connect.categories")).append("\n");
                    continue;
                }
                ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.getByPromotionAndStageAndGroupAndType(exceptionListEntity.getPromotionId(),
                        exceptionListEntity.getStageId(), vehicleGroupId, servicePlanType);
                if (servicePlanEntity == null) {
                    listIdRemove.add(exceptionId);
                    exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.service-plan.not.exists"));
                    listError.add(exceptionResponse);
                    messError.append(exceptionListEntity.getPlateNumber()).append(" ").append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.service-plan.not.exists")).append("\n");
                    continue;
                }
                ocs = servicePlanEntity.getOcsCode();
            } else {
                String requestUrl = stationUrl.replace("{stationId}", exceptionListEntity.getStationId().toString());
                String stationResponse = FnCommon.doGetRequest(requestUrl, null, FnCommon.getStringToken(authentication));
                if (stationResponse != null) {
                    JSONObject stationRes = new JSONObject(stationResponse);
                    JSONObject stationData = stationRes.getJSONObject("data");
                    if (Constants.BOO2.equals(stationData.getString("booCode"))) {
                        isBoo2 = true;
                    }
                } else {
                    exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.cannot.connect.categories"));
                    listError.add(exceptionResponse);
                    messError.append(exceptionListEntity.getPlateNumber()).append(" ").append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.cannot.connect.categories")).append("\n");
                    continue;
                }
                ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.getByPromotionAndStationAndGroupAndType(exceptionListEntity.getPromotionId(),
                        exceptionListEntity.getStationId(), vehicleGroupId, servicePlanType);
                if (servicePlanEntity == null) {
                    listIdRemove.add(exceptionId);
                    exceptionResponse.setError(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.service-plan.not.exists"));
                    listError.add(exceptionResponse);
                    messError.append(exceptionListEntity.getPlateNumber()).append(" ").append(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.service-plan.not.exists")).append("\n");
                    continue;
                }
                ocs = servicePlanEntity.getOcsCode();
            }
            exceptionListEntity.setOcsCode(ocs);
            // dong bo khi xe thuoc boo1 tram boo2
            if (isBoo2) {
                //check phuong tien thuoc boo1
                if (res != null) {
                    // xe thuoc boo1
                    if (Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                        exceptionListEntity.setContractId(Constants.BOO_INFO.CONTRACT_ID.toString());
                        // them xe tren ocs
                        if (!Objects.equals("Success", ocsService.queryVehicleOcs(res.getEtag(), authentication).get("description"))) {
                            AddVehicleRequestDTO vehicleRequest = new AddVehicleRequestDTO();
                            vehicleRequest.setContractId(Constants.BOO_INFO.CONTRACT_ID);
                            vehicleRequest.setEpc(res.getEtag());
                            vehicleRequest.setPlateNumber(exceptionListEntity.getPlateNumber());
                            vehicleRequest.setVehicleGroupId(res.getVehicle_type());
                            vehicleRequest.setEffDate(exceptionListEntity.getEffDate());
                            vehicleRequest.setExpDate(exceptionListEntity.getExpDate());
                            if (exceptionListEntity.getLicensePlateType() != null) {
                                vehicleRequest.setPlateTypeCode(exceptionListEntity.getLicensePlateType().toString());
                            }
                            vehicleRequest.setTid(res.getEtag());
                            vehicleRequest.setRfidType(String.valueOf(Constants.RFIDStatus.ACTIVE.code));
                            vehicleRequest.setOfferExternalId(vehicleOfferId);
                            exceptionListEntity.setTid(res.getEtag());
                            vehicleRequest.setRfidSerial(vehicleRepositoryJPA.getNextValSequenceSerial().toString());
                            vehicleRequest.setStatus(VehicleEntity.Status.ACTIVATED.value);
                            if(res.getWeight_goods() != null) {
                                vehicleRequest.setCargoWeight(res.getWeight_goods());
                            }
                            vehicleRequest.setSeatNumber(res.getSeat());
                            OCSResponse ocsResponse = ocsService.createVehicleOCS(vehicleRequest, authentication, Constants.ACT_TYPE.BOO1_SYNC);
                            if (!"0".equals(ocsResponse.getResultCode())) {
                                exceptionResponse.setOcsWarning(ocsResponse.getDescription());
                                isWarning = true;
                            } else {
                                //them xe vao hop dong boo1 neu chua ton tai
                                if (!vehicleServiceJPA.isAddedContractBoo1(exceptionListEntity.getPlateNumber(), String.valueOf(exceptionListEntity.getLicensePlateType()))) {
                                    VehicleEntity vehicleEntity = new VehicleEntity();
                                    vehicleEntity.setCustId(Constants.BOO_INFO.CUST_ID);
                                    vehicleEntity.setContractId(Constants.BOO_INFO.CONTRACT_ID);
                                    vehicleEntity.setPlateNumber(exceptionListEntity.getPlateNumber());
                                    vehicleEntity.setVehicleGroupId(res.getVehicle_type());
                                    vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
                                    vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
                                    if (exceptionListEntity.getLicensePlateType() != null) {
                                        vehicleEntity.setPlateTypeCode(exceptionListEntity.getLicensePlateType().toString());
                                    }
                                    vehicleEntity.setEpc(res.getEtag());
                                    vehicleEntity.setTid(res.getEtag());
                                    vehicleEntity.setRfidSerial(vehicleRepositoryJPA.getNextValSequenceSerial().toString());
                                    vehicleEntity.setRfidType(String.valueOf(Constants.RFIDStatus.ACTIVE.code));
                                    vehicleEntity.setCreateDate(new Date(System.currentTimeMillis()));
                                    vehicleEntity.setEffDate(new Date(System.currentTimeMillis()));
                                    vehicleEntity.setCreateUser(FnCommon.getUserLogin(authentication));
                                    vehicleEntity.setSeatNumber(res.getSeat());
                                    if(res.getWeight_goods() != null) {
                                        vehicleEntity.setCargoWeight(res.getWeight_goods());
                                    }
                                    if (res.getWeight_all() != null) {
                                        vehicleEntity.setGrossWeight(res.getWeight_all());
                                    }
                                    vehicleEntity.setVehicleTypeId(Long.parseLong(res.getRegister_vehicle_type()));
                                    vehicleEntity.setProfileStatus(Constants.BOO_INFO.PROFILE_STATUS);
                                    vehicleServiceJPA.save(vehicleEntity);
                                }
                            }
                        }
                        exceptionListEntity.setEpc(res.getEtag());
                        doTransferBoo1 = true;
                    } else {
                        List<VehicleEntity> vehicleList = vehicleServiceJPA.getByPlateNumber(exceptionListEntity.getPlateNumber(), String.valueOf(exceptionListEntity.getLicensePlateType()));
                        if (vehicleList != null && !vehicleList.isEmpty()) {
                            VehicleEntity vehicleEntity = vehicleList.get(0);
                            exceptionListEntity.setContractId(vehicleEntity.getContractId().toString());
                            exceptionListEntity.setCustomerId(vehicleEntity.getCustId());
                        }
                    }
                } else {
                    exceptionResponse.setBoo1Warning(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.cannot.connect.boo1"));
                    isWarning = true;
                }
            }
            exceptionListEntity.setApprovedDate(new Date(System.currentTimeMillis()));
            exceptionListEntity.setApprovedUser(FnCommon.getUserLogin(authentication));
            if (exceptionResponse.getOcsWarning() == null) {
                VehicleAddSuffOfferDTO suffOffer = new VehicleAddSuffOfferDTO();
                suffOffer.setEpc(exceptionListEntity.getEpc());
                suffOffer.setEffDate(exceptionListEntity.getEffDate());
                if (exceptionListEntity.getExpDate() == null) {
                    suffOffer.setExpDate(FnCommon.addYears(exceptionListEntity.getEffDate(), 30));
                    exceptionListEntity.setExpDate(FnCommon.addYearsSql(exceptionListEntity.getEffDate(), 30));
                } else {
                    suffOffer.setExpDate(exceptionListEntity.getExpDate());
                }
                suffOffer.setExpDate(FnCommon.setTimeOfDate(suffOffer.getExpDate(), 23, 59, 59));
                suffOffer.setOfferId(ocs);
                suffOffer.setOfferLevel(Constants.OFFER_LEVEL_DEFAULT.toString());
                suffOffer.setAutoRenew("0");    //ve luot khong gia han tu dong
                OCSResponse ocsResponse = ocsService.addSupOffer(suffOffer, authentication, Constants.ACT_TYPE.BOO1_SYNC, Long.valueOf(exceptionListEntity.getContractId()), null, null, null);
                if (!FnCommon.checkOcsCode(ocsResponse)) {
                    //Neu trung offer da add truoc do tu tool import thi changeoffer
                    if ("WS_TICKET_OVERLAP".equals(ocsResponse.getDescription())) {
                        ocsService.changeSupOffer(suffOffer, authentication, Long.valueOf(exceptionListEntity.getContractId()), null, null, null);
                    } else {
                        exceptionResponse.setOcsWarning(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.add.sub-offer.ocs"));
                        listIdRemove.add(exceptionId);
                        isWarning = true;
                    }
                } else if (doTransferBoo1 && exceptionResponse.getOcsWarning() == null) {
                    //dong bo boo1
                    String body = new ReqExceptionListDTO().getStringReq(exceptionListEntity, "I", stationIn, stationOut, plateType);
                    String response = boo1Service.doPostBoo1Request(onlineEventUrl,body, 0, body, Constants.ACT_TYPE.BOO1_SYNC);
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.has("error")) {
                            listIdSync.add(exceptionId);
                        } else {
                            LOG.info("postApproval error: " + jsonObject.getString("error"));
                            exceptionResponse.setBoo1Warning(jsonObject.getString("error"));
                            exceptionListEntity.setIsTransferredBoo1(0L);
                            isWarning = true;
                        }
                    } else {
                        exceptionResponse.setBoo1Warning(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.cannot.connect.boo1"));
                        isWarning = true;
                    }
                }
            }
            if (exceptionResponse.getOcsWarning() == null) {
                exceptionListServiceJPA.save(exceptionListEntity);
            } else {
                listIdRemove.add(exceptionId);
            }
            if (isWarning) {
                listWarning.add(exceptionResponse);
                messWarning.append(exceptionListEntity.getPlateNumber()).append(" ");
                if (exceptionResponse.getOcsWarning() != null) {
                    messWarning.append(exceptionResponse.getOcsWarning()).append("\n");
                }
                if (exceptionResponse.getBoo1Warning() != null) {
                    messWarning.append(exceptionResponse.getBoo1Warning()).append("\n");
                }
            } else {
                listSuccess.add(exceptionResponse);
                messSuccess.append(exceptionListEntity.getPlateNumber()).append(",");
            }
        }
        if(messSuccess.length() > 0) {
            data.setMessSuccess(jedisCacheService.getMessageErrorByKey("crm.exception-list.approval.success").replace("{1}", messSuccess.toString().substring(0, messSuccess.length() - 1)));
        }

        data.setListSuccess(listSuccess);
        data.setListError(listError);
        data.setListWarning(listWarning);
        data.setMessError(messError.toString());
        data.setMessWarning(messWarning.toString());
        mess.setCode(ErrorApp.SUCCESS.getCode());
        mess.setDescription(ErrorApp.SUCCESS.getDescription());
        responseToClient.setData(data);
        responseToClient.setMess(mess);
        listIdStatus.removeAll(listIdRemove);
        vehiclesExceptionRepository.postUpdateStatus(params.getStatus(), listIdStatus);
        vehiclesExceptionRepository.postUpdateStatusTransfer(listIdSync);
        return responseToClient;
    }

    /**
     * Them moi xe ngoai le/uu tien/cam don le
     *
     * @param authentication   params client
     * @param itemParamsEntity params client
     * @param files            params client
     * @return Object
     */
    @Override
    public Object addVehiclesExceptionSingle(Authentication authentication, VehiclesExceptionDTO itemParamsEntity, List<MultipartFile> files) {
        Map<String, Object> objectMap = new HashMap<>();
        // Validate input
        ErrorApp errorValid = validateAdd(itemParamsEntity, files, false);
        if (errorValid.getCode() != ErrorApp.SUCCESS.getCode()) {
            objectMap.put("error", errorValid);
            return objectMap;
        }

        List<ExceptionListEntity> exceptionListEntities = new ArrayList<>();
        String userLogin = FnCommon.getUserLogin(authentication);
        Date currDate = new Date(System.currentTimeMillis());

        itemParamsEntity.setTid("TID");// Chua chot
        itemParamsEntity.setCreateDate(currDate);
        itemParamsEntity.setCreateUser(userLogin);

        int countStage = FnCommon.isNullOrEmpty(itemParamsEntity.getStages()) ? 0 : itemParamsEntity.getStages().size();
        int countStation = FnCommon.isNullOrEmpty(itemParamsEntity.getStations()) ? 0 : itemParamsEntity.getStations().size();

        // Truong hop input doan/tram co 1 gia tri (countStage + countStation == 1): thi se lay ma OCS dua vao ca laneInId va laneOutId
        // Neu lay ma OCS theo lan khong co gia tri se lay ma OCS theo doan.
        // Truong hop multi doan/tram thi laneInId va laneOutId coi la null.
        // Khi co bao nhieu OCS se insert bay nhieu ban ghi vao ExceptionList.
        // Khong lay duoc ma OCS se bao loi.

        if (countStage + countStation == 1) {
            ResultSelectEntity results = vehiclesExceptionRepository.getOCSCode(ServicePlanEntity.builder()
                    .laneIn(itemParamsEntity.getLaneInId())
                    .laneOut(itemParamsEntity.getLaneOutId())
                    .stageId(countStage > 0 ? itemParamsEntity.getStages().get(0).getStageId() : null)
                    .stationId(countStation > 0 ? itemParamsEntity.getStations().get(0).getStationId() : null)
                    .build(), itemParamsEntity.getExceptionType());

            // Neu getOCSCode ko co du lieu tiep tuc lay OCS code theo tram/doan
            if (FnCommon.isNullObject(results.getListData()) || results.getListData().size() < 1) {
                results = vehiclesExceptionRepository.getOCSCode(ServicePlanEntity.builder()
                        .stageId(countStage > 0 ? itemParamsEntity.getStages().get(0).getStageId() : null)
                        .stationId(countStation > 0 ? itemParamsEntity.getStations().get(0).getStationId() : null)
                        .build(), itemParamsEntity.getExceptionType());
            }

            if (FnCommon.isNullObject(results.getListData()) || results.getListData().size() < 1) {
                ErrorApp errorApp = ErrorApp.DATA_EMPTY;
                errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ocs.empty"));
                objectMap.put("error", errorApp);
                return objectMap;
            } else {
                for (ServicePlanEntity entity : (List<ServicePlanEntity>) results.getListData()) {
                    ExceptionListEntity exceptionEntity = (ExceptionListEntity) FnCommon.convertObjectToObject(itemParamsEntity, ExceptionListEntity.class);
                    exceptionEntity.setOcsCode(entity.getOcsCode());
                    exceptionEntity.setStageId(countStage > 0 ? itemParamsEntity.getStages().get(0).getStageId() : null);
                    exceptionEntity.setStageName(countStage > 0 ? itemParamsEntity.getStages().get(0).getStageName() : null);
                    exceptionEntity.setStationId(countStation > 0 ? itemParamsEntity.getStations().get(0).getStationId() : null);
                    exceptionEntity.setStationName(countStation > 0 ? itemParamsEntity.getStations().get(0).getStationName() : null);
                    exceptionListEntities.add(exceptionEntity);
                }
            }
        } else {
            itemParamsEntity.setLaneInId(null);
            itemParamsEntity.setLaneOutId(null);
            itemParamsEntity.setLaneInName(null);
            itemParamsEntity.setLaneOutName(null);

            if (countStage > 0) {   // Lay thong tin doan
                for (VehiclesExceptionDTO.stage stage : itemParamsEntity.getStages()) {
                    ResultSelectEntity results = vehiclesExceptionRepository.getOCSCode(
                            ServicePlanEntity.builder()
                                    .stageId(stage.getStageId())
                                    .build(), itemParamsEntity.getExceptionType());

                    if (FnCommon.isNullObject(results.getListData()) || results.getListData().size() < 1) {
                        ErrorApp errorApp = ErrorApp.DATA_EMPTY;
                        errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ocs.empty"));
                        objectMap.put("error", errorApp);
                        return objectMap;
                    } else {
                        for (ServicePlanEntity entity : (List<ServicePlanEntity>) results.getListData()) {
                            ExceptionListEntity exceptionEntity = (ExceptionListEntity) FnCommon.convertObjectToObject(itemParamsEntity, ExceptionListEntity.class);
                            exceptionEntity.setOcsCode(entity.getOcsCode());
                            exceptionEntity.setStageId(stage.getStageId());
                            exceptionEntity.setStageName(stage.getStageName());
                            exceptionListEntities.add(exceptionEntity);
                        }
                    }
                }
            }

            if (countStation > 0) { // Lay thong tin tram
                for (VehiclesExceptionDTO.station station : itemParamsEntity.getStations()) {
                    ResultSelectEntity results = vehiclesExceptionRepository.getOCSCode(
                            ServicePlanEntity.builder()
                                    .stationId(station.getStationId())
                                    .build(), itemParamsEntity.getExceptionType());

                    if (FnCommon.isNullObject(results.getListData()) || results.getListData().size() < 1) {
                        ErrorApp errorApp = ErrorApp.DATA_EMPTY;
                        errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ocs.empty"));
                        objectMap.put("error", errorApp);
                        return objectMap;
                    } else {
                        for (ServicePlanEntity entity : (List<ServicePlanEntity>) results.getListData()) {
                            ExceptionListEntity exceptionEntity = (ExceptionListEntity) FnCommon.convertObjectToObject(itemParamsEntity, ExceptionListEntity.class);
                            exceptionEntity.setOcsCode(entity.getOcsCode());
                            exceptionEntity.setStationId(station.getStationId());
                            exceptionEntity.setStationName(station.getStationName());
                            exceptionListEntities.add(exceptionEntity);
                        }
                    }
                }
            }
        }

        // CHECK DUPLICATE DATA
        for (ExceptionListEntity item : exceptionListEntities) {
            Integer count = vehiclesExceptionRepository.getCountExceptionList(item);
            if (0 < count) {
                ErrorApp errorApp = ErrorApp.ERR_DATA;
                errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.duplicate.data"));
                objectMap.put("error", errorApp);
                return objectMap;
            }
        }

        // INSERT EXCEPTION LIST
        exceptionListEntities = vehiclesExceptionRepository.saveAllExceptionList(exceptionListEntities);

        List<AttachmentFileEntity> attachmentFileEntitys = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String filePath = String.format("/%s/%s", "vehicles-exceptions", UUID.randomUUID().toString() + "-" + file.getOriginalFilename());
                fileService.uploadFile(filePath, file.getBytes());
                for (ExceptionListEntity entity : exceptionListEntities) {
                    AttachmentFileEntity attachmentFileEntity = AttachmentFileEntity.builder()
                            .documentName(itemParamsEntity.getDocumentName())
                            .documentPath(filePath)
                            .objectId(entity.getExceptionListId())
                            .createUser(userLogin)
                            .createDate(currDate)
                            .attachmentType(Constants.ATTACHMENT_TYPE.EXCEPTION)        // 2. File đính kèm của nghiệp vụ quản lý Ngoại lê/cấm/ưu tiên.
                            .status(Constants.ATTACHMENT_FILE_STATUS.ACTIVE)            // 1. Hoat dong
                            .build();
                    attachmentFileEntitys.add(attachmentFileEntity);
                }
            } catch (IOException ioEx) {
                log.error("Upload File :" + ioEx);
            }
        }
        if (attachmentFileEntitys.size() > 0)       // insert data vao DB ATTACHMENT_FILE
            vehiclesExceptionRepository.saveAttachmentFiles(attachmentFileEntitys);

        objectMap.put("error", ErrorApp.SUCCESS);
        return objectMap;
    }

    @Override
    public void addExceptionList(Authentication authentication, ExceptionListReqDTO params) {
        Long vehicleGroupId = null;
        Long contractId = null;
        Long vehicleTypeId = null;
        String etag = null;
        VehicleEntity vehicleEntity = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(params.getPlateNumber(), params.getPlateType());
        if (params.getExpDate() != null && params.getEffDate().getTime() >= params.getExpDate().getTime()) {
            throw new EtcException("crm.eff.date.after.exp.date");
        }
        if (vehicleEntity == null || !vehicleServiceJPA.isBoo2ActVehicle(params.getPlateNumber(), params.getPlateType())) {
            String plateType = FnCommon.mappingPlateTypeBOO2ToBOO1(params.getPlateType());
            ReqActivationCheckDTO req = new ReqActivationCheckDTO();
            req.setPlate(FnCommon.getPlateNumberBoo1(params.getPlateNumber(), plateType));
            req.setRequest_id(System.currentTimeMillis());
            req.setRequest_datetime(System.currentTimeMillis());
            ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
            if (res == null) {
                throw new EtcException("crm.vehicle.boo1.connect.fail");
            } else if (Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                vehicleGroupId = res.getVehicle_type();
                contractId = Constants.BOO_INFO.CONTRACT_ID;
                vehicleTypeId = Long.valueOf(res.getRegister_vehicle_type());
                etag = res.getEtag();
            }
        } else {
            vehicleGroupId = vehicleEntity.getVehicleGroupId();
            contractId = vehicleEntity.getContractId();
            vehicleTypeId = vehicleEntity.getVehicleTypeId();
            etag = vehicleEntity.getEpc();
        }
        if (etag == null) {
            throw new EtcException("crm.vehicle.not.found");
        }

        Long licensePlateType;
        if (params.getPlateType() != null && !params.getPlateType().isEmpty()) {
            licensePlateType = Long.valueOf(params.getPlateType());
        } else {
            licensePlateType = 1L;
        }
        List<ExceptionListEntity> exceptionListEntityList = new LinkedList<>();
        for (ExceptionListReqDTO.StationStage stationStage : params.getStationStages()) {
            List<ExceptionListEntity> exceptionList = exceptionListServiceJPA.exceptionCheck(params.getExceptionType(),
                    params.getPlateNumber(), licensePlateType, stationStage.getStationId(), stationStage.getStagesId(),
                    params.getPromotionId());

            for (ExceptionListEntity exceptionCheck : exceptionList) {
                if (exceptionCheck.getExpDate() != null) {
                    if (params.getEffDate().after(exceptionCheck.getExpDate())) {
                        continue;
                    } else if (params.getExpDate() != null && params.getExpDate().before(exceptionCheck.getEffDate())) {
                        continue;
                    }
                } else {
                    if (params.getExpDate() != null && params.getExpDate().before(exceptionCheck.getEffDate())) {
                        continue;
                    }
                }
                String name;
                if (stationStage.getStationId() != null) {
                    name = stationStage.getStationName();
                } else {
                    name = stationStage.getStageName();
                }
                throw new EtcException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        String.format("Trùng khoảng ngày hiệu lực tại %s", name));
            }
            ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
            exceptionListEntity.setPlateNumber(params.getPlateNumber());
            exceptionListEntity.setEpc(etag);
            exceptionListEntity.setEffDate(params.getEffDate());
            exceptionListEntity.setExpDate(params.getExpDate());
            exceptionListEntity.setTid(params.getTid());
            exceptionListEntity.setLicensePlateType(licensePlateType);
            exceptionListEntity.setRfidSerial(params.getRfidSerial());
            exceptionListEntity.setRegisterVehicleType(vehicleTypeId);
            if (stationStage.getStationId() != null) {
                exceptionListEntity.setStationId(stationStage.getStationId());
                exceptionListEntity.setStationName(stationStage.getStationName());
            } else {
                exceptionListEntity.setStageId(stationStage.getStagesId());
                exceptionListEntity.setStageName(stationStage.getStageName());
            }
            exceptionListEntity.setCustomerId(params.getCustomerId());
            exceptionListEntity.setCustomerName(params.getCustomerName());
            if (contractId != null) {
                exceptionListEntity.setContractId(String.valueOf(contractId));
            }
            exceptionListEntity.setContractNo(params.getContractNo());
            exceptionListEntity.setStatus(Constants.EXCEPTION_NEW);
            exceptionListEntity.setCreateUser(FnCommon.getUserLogin(authentication));
            exceptionListEntity.setExceptionType(params.getExceptionType());
            exceptionListEntity.setPromotionId(params.getPromotionId());
            exceptionListEntity.setRegisterVehicleType(params.getRegisterVehicleType());
            exceptionListEntity.setDescription(params.getDescription());
            switch (params.getExceptionType()) {
                case Constants.EXCEPTION_VEHICLE:
                    exceptionListEntity.setExceptionVehicleType(params.getExceptionVehicleType());
                    exceptionListEntity.setVehicleTypeId(vehicleGroupId);
                    break;
                case Constants.EXCEPTION_TICKET:
                    exceptionListEntity.setVehicleTypeId(vehicleGroupId);
                    break;
                case Constants.EXCEPTION_PRIORITY:
                    exceptionListEntity.setWhiteListType(params.getWhiteListType());
                    break;
                case Constants.EXCEPTION_BAN:
                    exceptionListEntity.setBlackListType(params.getBlackListType());
            }
            exceptionListEntityList.add(exceptionListEntity);
        }
        exceptionListServiceJPA.saveAll(exceptionListEntityList);
    }

    @Override
    public ResponseEntity<?> importExceptionList(Authentication authentication, MultipartFile file) throws IOException {
        byte[] byteArr = file.getBytes();
        String fileName = "result.xlsx";
        byte[] bytes;
        long failedLines = 0;
        long success = 0;

        List<LinkedHashMap<?, ?>> stages = stageService.findAllStages(FnCommon.getStringToken(authentication));
        List<LinkedHashMap<?, ?>> stations = stationService.findAllStations(FnCommon.getStringToken(authentication));
        HashMap<Long, String> stationList = mappingStationStages(stations);
        HashMap<Long, String> stageList = mappingStationStages(stages);

        HashMap<Long, Object> promotionList = new HashMap<>();
        PromotionDTO promotionParams = new PromotionDTO();
        promotionParams.setIsActive(true);
        promotionParams.setPromotionLevel(Constants.PROMOTION_NL);
        ResultSelectEntity dataResult = (ResultSelectEntity) promotionService.getPromotions(promotionParams);
        List<PromotionDTO> promotions = (List<PromotionDTO>) dataResult.getListData();
        for (PromotionDTO promotionDTO : promotions) {
            promotionList.put(promotionDTO.getId(), null);
        }

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getRow(0) != null && sheet.getRow(0).getLastCellNum() != 10) {
                throw new EtcException("crm.vehicles.exception.excel");
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (!FnCommon.rowIsEmpty(row)) {
                    String failCell = validateRow(row);
                    if (failCell != null) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed",failCell + " không đúng định dạng");
                        failedLines++;
                        continue;
                    }
                    if (FnCommon.isNullOrBlank(row.getCell(PLATE_NUMBER)) || FnCommon.isNullOrBlank(row.getCell(PLATE_NUMBER).getStringCellValue())) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Biển số xe không được để trống");
                        failedLines++;
                        continue;
                    }
                    if (FnCommon.isNullOrBlank(row.getCell(EFF_DATE)) || FnCommon.isNullOrBlank(row.getCell(EXP_DATE)) ||
                            FnCommon.isNullOrBlank(row.getCell(EFF_DATE).getStringCellValue()) || FnCommon.isNullOrBlank(row.getCell(EXP_DATE).getStringCellValue())) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Ngày hiệu lực và ngày hết hiệu lực không được để trống ");
                        failedLines++;
                        continue;
                    }
                    if (FnCommon.isNullOrBlank(row.getCell(EXCEPTION_LIST_TYPE)) || FnCommon.isNullOrBlank(row.getCell(EXCEPTION_LIST_TYPE).getStringCellValue())) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Loại ngoại lệ không được để trống");
                        failedLines++;
                        continue;
                    }
                    if (FnCommon.isNullOrBlank(row.getCell(STATION)) && FnCommon.isNullOrBlank(row.getCell(STAGES))) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Trạm hoặc đoạn không được để trống");
                        failedLines++;
                        continue;
                    }
                    if (!FnCommon.isNullOrBlank(row.getCell(STATION)) && !FnCommon.isNullOrBlank(row.getCell(STAGES))) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Chỉ được nhập trạm hoặc đoạn");
                        failedLines++;
                        continue;
                    }

                    Long vehicleType = 0L, vehicleGroup, promotion = null,
                            customerId = Constants.BOO_INFO.CUST_ID, contractId = Constants.BOO_INFO.CONTRACT_ID, stationId, stagesId;
                    Long plateTypeCode = null;
                    String plateType = "";
                    java.util.Date effDate, expDate;
                    String plateNumber = row.getCell(PLATE_NUMBER).getStringCellValue().replaceAll("[\\-\\.]", "").trim();

                    if (!FnCommon.isNullOrBlank(row.getCell(PLATE_TYPE)) && !FnCommon.isNullOrBlank(row.getCell(PLATE_TYPE).getStringCellValue())) {
                        plateType = row.getCell(PLATE_TYPE).getStringCellValue().trim();
                        plateType = plateType.toUpperCase();
                        if (!"T".equals(plateType) && !"X".equals(plateType) && !"V".equals(plateType)) {
                            resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Loại biển số phải là T,X,V");
                            failedLines++;
                            continue;
                        }
                    }
                    if (row.getCell(EFF_DATE).getCellType() != CellType.STRING) {
                        if (HSSFDateUtil.isCellDateFormatted(row.getCell(EFF_DATE))) {
                            effDate = row.getCell(EFF_DATE).getDateCellValue();
                        } else {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Giá trị ngày hiệu lực không hợp lệ");
                            failedLines ++;
                            continue;
                        }
                    } else {
                        try {
                            effDate = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT).parse(row.getCell(EFF_DATE).getStringCellValue().trim());
                        } catch (Exception e) {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Giá trị ngày hiệu lực không hợp lệ");
                            log.error(e);
                            failedLines ++;
                            continue;
                        }
                    }
                    if (row.getCell(EXP_DATE).getCellType() != CellType.STRING) {
                        if (HSSFDateUtil.isCellDateFormatted(row.getCell(EXP_DATE))) {
                            expDate = row.getCell(EXP_DATE).getDateCellValue();
                        } else {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Giá trị ngày hết hiệu lực không hợp lệ");
                            failedLines ++;
                            continue;
                        }
                    } else {
                        try {
                            expDate = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT).parse(row.getCell(EXP_DATE).getStringCellValue().trim());
                        } catch (Exception e) {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Giá trị ngày hết hiệu lực không hợp lệ");
                            log.error(e);
                            failedLines ++;
                            continue;
                        }
                    }
                    if (effDate.getTime() >= expDate.getTime()) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Ngày hết hiệu lực phải lớn hơn ngày hiệu lực");
                        failedLines++;
                        continue;
                    }

                    String exceptionListType = row.getCell(EXCEPTION_LIST_TYPE).getStringCellValue().trim();
                    exceptionListType = exceptionListType.toUpperCase();
                    if (!"GV".equals(exceptionListType) && !"BL".equals(exceptionListType) && !"WL".equals(exceptionListType)) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Loại ngoại lệ phải là GV,BL,WL");
                        failedLines++;
                        continue;
                    }
                    String etag;
                    if (!FnCommon.isNullOrBlank(row.getCell(PROMOTION))) {
                        promotion = (long) row.getCell(PROMOTION).getNumericCellValue();
                        if (!promotionList.containsKey(promotion)) {
                            resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Chương trình khuyến mãi không tồn tại");
                            failedLines++;
                            continue;
                        }
                    }
                    if (!FnCommon.isNullOrBlank(row.getCell(STATION))) {
                        stationId = (long) row.getCell(STATION).getNumericCellValue();
                        if (!stationList.containsKey(stationId)) {
                            resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Trạm không tồn tại");
                            failedLines++;
                            continue;
                        }
                        stagesId = null;
                    } else {
                        stagesId = (long) row.getCell(STAGES).getNumericCellValue();
                        if (!stageList.containsKey(stagesId)) {
                            resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Đoạn không tồn tại");
                            failedLines++;
                            continue;
                        }
                        stationId = null;
                    }
                    VehicleEntity vehicle = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(plateNumber, String.valueOf(plateTypeCode));

                    if (vehicle == null) {
                        // Neu xe cua BOO1 goi BOO1 lay thong tin etag
                        ResActivationCheckDTO res;
                        //Neu co loai bien so check boo1
                        if (!FnCommon.isNullOrBlank(plateType)) {
                            String boo1PlateNumber = FnCommon.getPlateNumberBoo1(plateNumber, plateType);
                            res = getBoo1Vehicle(boo1PlateNumber, authentication);
                            if (res == null) {
                                resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Không tìm thấy xe bên BOO1 và BOO2");
                                failedLines ++;
                                continue;
                            } else {
                                plateTypeCode = Long.valueOf(FnCommon.getPlateTypeBOO1(plateType));
                            }
                        } else {
                            // neu khong co thu tung loai bien
                            res = getBoo1Vehicle(plateNumber, authentication);
                            if (res == null) {
                                res = getBoo1Vehicle(plateNumber + "T", authentication);
                                if (res == null) {
                                    res = getBoo1Vehicle(plateNumber + "X", authentication);
                                    if (res == null) {
                                        res = getBoo1Vehicle(plateNumber + "V", authentication);
                                        if (res == null) {
                                            resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Không tìm thấy xe bên BOO1 và BOO2");
                                            failedLines ++;
                                            continue;
                                        } else {
                                            plateTypeCode = 6L;
                                        }
                                    } else {
                                        plateTypeCode = 2L;
                                    }
                                } else {
                                    plateTypeCode = 1L;
                                }
                            }
                        }
                        etag = res.getEtag();
                        if (!FnCommon.isNullOrBlank(res.getRegister_vehicle_type())) {
                            vehicleType = Long.parseLong(res.getRegister_vehicle_type());
                        }
                        vehicleGroup = res.getVehicle_type();

                    } else {
                        etag = vehicle.getEpc();
                        vehicleType = vehicle.getVehicleTypeId();
                        vehicleGroup = vehicle.getVehicleGroupId();
                        customerId = vehicle.getCustId();
                        contractId = vehicle.getContractId();
                        if (!FnCommon.isNullOrBlank(plateType)) {
                            plateTypeCode = Long.valueOf(FnCommon.mappingPlateTypeBOO1ToBOO2(plateType));
                        } else if (vehicle.getPlateTypeCode() != null) {
                            plateTypeCode = Long.valueOf(vehicle.getPlateTypeCode());
                        }
                    }

                    //insert vao exception list
                    if (plateTypeCode == null) {
                        plateTypeCode = 1L;
                    }
                    String exceptionType = null;
                    ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
                    exceptionListEntity.setPlateNumber(plateNumber);
                    exceptionListEntity.setEpc(etag);
                    exceptionListEntity.setEffDate(new Date(effDate.getTime()));
                    exceptionListEntity.setExpDate(new Date(expDate.getTime()));
                    exceptionListEntity.setTid(etag);
                    exceptionListEntity.setLicensePlateType(plateTypeCode);
                    if (stationId != null) {
                        exceptionListEntity.setStationId(stationId);
                        exceptionListEntity.setStationName(stationList.get(stationId));
                    } else {
                        exceptionListEntity.setStageId(stagesId);
                        exceptionListEntity.setStageName(stageList.get(stagesId));
                    }
                    exceptionListEntity.setCustomerId(customerId);
                    if (contractId != null) {
                        exceptionListEntity.setContractId(String.valueOf(contractId));
                        if (!contractId.equals(1L)) {
                            exceptionListEntity.setContractNo(contractServiceJPA.getOne(contractId).getContractNo());
                        }
                    }
                    if (vehicle != null) {
                        exceptionListEntity.setCustomerName(vehicle.getOwner());
                    }
                    exceptionListEntity.setStatus(Constants.EXCEPTION_NEW);
                    exceptionListEntity.setCreateUser(FnCommon.getUserLogin(authentication));
                    exceptionListEntity.setRegisterVehicleType(vehicleType);
                    exceptionListEntity.setDescription("Import tu file");
                    switch (exceptionListType) {
                        case TICKET_TYPE:
                            if (promotion == null) {
                                resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Chương trình khuyến mãi không được để trống với ngoại lệ giá vé");
                                failedLines ++;
                                continue;
                            }
                            exceptionListEntity.setPromotionId(promotion);
                            exceptionListEntity.setVehicleTypeId(vehicleGroup);
                            exceptionType = Constants.EXCEPTION_TICKET;
                            break;
                        case WHILE_LIST:
                            exceptionListEntity.setVehicleTypeId(null);
                            exceptionListEntity.setPromotionId(null);
                            exceptionType = Constants.EXCEPTION_PRIORITY;
                            break;
                        case BLACK_LIST:
                            exceptionListEntity.setVehicleTypeId(null);
                            exceptionListEntity.setPromotionId(null);
                            exceptionType = Constants.EXCEPTION_BAN;
                    }
                    exceptionListEntity.setExceptionType(exceptionType);

                    // check trung
                    List<ExceptionListEntity> exceptionList = exceptionListServiceJPA.exceptionCheck(exceptionType,
                            plateNumber, plateTypeCode, stationId, stagesId, promotion);

                    boolean dateNotValid = false;
                    for (ExceptionListEntity exceptionCheck : exceptionList) {
                        if (exceptionCheck.getExpDate() != null) {
                            if (effDate.after(exceptionCheck.getExpDate())) {
                                continue;
                            } else if (expDate.before(exceptionCheck.getEffDate())) {
                                continue;
                            }
                        } else {
                            if (expDate.before(exceptionCheck.getEffDate())) {
                                continue;
                            }
                        }
                        dateNotValid = true;
                    }
                    if (dateNotValid) {
                        resultImport(row.createCell(RESULT), row.createCell(DESC), "Failed", "Trùng khoảng ngày hiệu lực");
                        failedLines ++;
                        continue;
                    }
                    exceptionListServiceJPA.save(exceptionListEntity);
                    success++;
                    resultImport(row.createCell(RESULT), row.createCell(DESC), "Success", "Thành công");
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new org.apache.commons.io.output.ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            log.error(e);
            if (e instanceof EtcException) {
                throw e;
            }
            throw new EtcException("crm.file.not.is.format.excel");
        }
        return FnCommon.returnFileExcel(bytes, fileName, failedLines, success);
    }

    private HashMap<Long, String> mappingStationStages(List<LinkedHashMap<?,?>> list) {
        HashMap<Long, String> result = new HashMap<>();
        for (LinkedHashMap<?,?> item : list) {
            result.put(new Long(item.get("id").toString()), item.get("name").toString());
        }
        return result;
    }

    private String validateRow(Row row) {
        if (!FnCommon.isNullOrBlank(row.getCell(PLATE_NUMBER)) && row.getCell(PLATE_NUMBER).getCellType() != CellType.STRING) {
            return "biển số xe";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(PLATE_TYPE)) && row.getCell(PLATE_TYPE).getCellType() != CellType.STRING) {
            return "loại biển số";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(EXCEPTION_LIST_TYPE)) && row.getCell(EXCEPTION_LIST_TYPE).getCellType() != CellType.STRING) {
            return "loại ngoại lệ";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(EFF_DATE)) && row.getCell(EFF_DATE).getCellType() != CellType.STRING && row.getCell(EFF_DATE).getCellType() != CellType.NUMERIC) {
            return "ngày hiệu lực";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(EXP_DATE)) && row.getCell(EXP_DATE).getCellType() != CellType.STRING && row.getCell(EXP_DATE).getCellType() != CellType.NUMERIC) {
            return "ngày hết hiệu lực";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(PROMOTION)) && row.getCell(PROMOTION).getCellType() != CellType.NUMERIC) {
            return "khuyến mãi";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(STATION)) && row.getCell(STATION).getCellType() != CellType.NUMERIC) {
            return "trạm";
        }
        if (!FnCommon.isNullOrBlank(row.getCell(STAGES)) && row.getCell(STAGES).getCellType() != CellType.NUMERIC) {
            return "đoạn";
        }
        return null;
    }

    private void resultImport(Cell resultCell, Cell descCell, String result, String desc) {
        if(resultCell != null && descCell != null) {
            resultCell.setCellType(CellType.STRING);
            resultCell.setCellValue(result);
            descCell.setCellType(CellType.STRING);
            descCell.setCellValue(desc);
        }
    }

    public ByteArrayResource importTemplateExceptionList(Authentication authentication) {
        String pathTemplate = "template" + File.separator + "template_ngoaile_uutien_vipham.xlsx";
        try(InputStream in = getClass().getClassLoader().getResourceAsStream(pathTemplate);
            Workbook workbook = WorkbookFactory.create(in)) {

            // get data categories
            Map<String, String> params = new HashMap<>();
            params.put("booCode", "BOO2");
            List<LinkedHashMap<?, ?>> stages = stageService.findAllStagesBOO2(FnCommon.getStringToken(authentication), params);
            List<LinkedHashMap<?, ?>> stations = stationService.findAllStationsBOO2(FnCommon.getStringToken(authentication), params);
            PromotionDTO promotionParams = new PromotionDTO();
            promotionParams.setIsActive(true);
            promotionParams.setPromotionLevel(Constants.PROMOTION_NL);
            ResultSelectEntity dataResult = (ResultSelectEntity) promotionService.getPromotions(promotionParams);
            List<PromotionDTO> promotions = (List<PromotionDTO>) dataResult.getListData();

            //danh muc chuong trinh khuyen mai
            Sheet sheetPromotion = workbook.getSheetAt(1);
            for (int i = 0; i < promotions.size(); i++) {
                Row row = sheetPromotion.createRow(i+1);
                Cell cellId = row.createCell(ID, CellType.NUMERIC);
                cellId.setCellValue(promotions.get(i).getId());
                Cell cellName = row.createCell(NAME, CellType.STRING);
                cellName.setCellValue(promotions.get(i).getPromotionName());
            }

            //danh muc tram
            sheetPromotion = workbook.getSheetAt(2);
            for (int i = 0; i < stations.size(); i++) {
                LinkedHashMap<?, ?> station = stations.get(i);
                Row row = sheetPromotion.createRow(i+1);
                Cell cellId = row.createCell(ID, CellType.NUMERIC);
                cellId.setCellValue(Long.parseLong(station.get("id").toString()));
                Cell cellName = row.createCell(NAME, CellType.STRING);
                cellName.setCellValue(station.get("name").toString());
            }

            //danh muc doan
            sheetPromotion = workbook.getSheetAt(3);
            for (int i = 0; i < stages.size(); i++) {
                LinkedHashMap<?, ?> stage = stages.get(i);
                Row row = sheetPromotion.createRow(i+1);
                Cell cellId = row.createCell(ID, CellType.NUMERIC);
                cellId.setCellValue(Long.parseLong(stage.get("id").toString()));
                Cell cellName = row.createCell(NAME, CellType.STRING);
                cellName.setCellValue(stage.get("name").toString());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Validate emptu Input API tao moi va update phuong tien ngoai le, uu tien, cam
     *
     * @param itemParamsEntity params client
     * @param files            params client
     * @param isUpdate         Check api update
     * @return ErrorApp
     */
    private ErrorApp validateAdd(VehiclesExceptionDTO itemParamsEntity, List<MultipartFile> files, boolean isUpdate) {
        ErrorApp errorApp = ErrorApp.ERR_DATA;

        if (FnCommon.isNullOrEmpty(itemParamsEntity.getModule()))                                                       // Check module
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.empty"));
        else if (FnCommon.isNullObject(itemParamsEntity.getExceptionGroup()))                                           // check exceptionGroup
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.empty"));
        else if (FnCommon.isNullOrEmpty(itemParamsEntity.getPlateNumber()))                                             // Check plateNumber
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plate.number.empty"));
        else if (FnCommon.isNullOrEmpty(itemParamsEntity.getEpc()))                                                     // Check epc
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.epc.empty"));
        else if (FnCommon.isNullOrEmpty(itemParamsEntity.getExceptionType()))                                           // Check exceptionType
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exception.type.empty"));
        else if (FnCommon.isNullObject(itemParamsEntity.getStatus()))                                                   // Check status
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.status.empty"));
        else if (FnCommon.isNullObject(itemParamsEntity.getEffDate()))                                                  // Check effDate
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.eff.date.empty"));
        else if (!FnCommon.isNullObject(itemParamsEntity.getExpDate())                                                  // Check ex
                && itemParamsEntity.getExpDate().before(itemParamsEntity.getEffDate()))
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exp.date.err"));
        else if (FnCommon.isNullObject(itemParamsEntity.getLicensePlateType()))                                         // Check loai bien so
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plate.type.empty"));
        else if (FnCommon.isNullOrEmpty(itemParamsEntity.getDocumentName()))                                            // Check documentName
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.documentName.empty"));
        else if (!isUpdate && (files == null || files.size() <= 0))                                                     // Check file dinh kem
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.document.files.empty"));
        else if (isUpdate                                                                                               // Check Id tram doan truong hop sua xe ngoai le, uu tien, cam
                && FnCommon.isNullObject(itemParamsEntity.getStageId())
                && FnCommon.isNullObject(itemParamsEntity.getStationId()))
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.stationId.empty"));
        else if (isUpdate                                                                                               // Check Name tram doan truong hop sua xe ngoai le, uu tien, cam
                && FnCommon.isNullObject(itemParamsEntity.getStageName())
                && FnCommon.isNullObject(itemParamsEntity.getStationName()))
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.stationName.empty"));
        else if (isUpdate && FnCommon.isNullObject(itemParamsEntity.getExceptionListId()))
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exception_list_id.empty"));
        else {
            if (itemParamsEntity.getModule().equalsIgnoreCase(Constants.MODULE_CRM)
                    || itemParamsEntity.getModule().equalsIgnoreCase(Constants.MODULE_BOT)
                    || itemParamsEntity.getModule().equalsIgnoreCase(Constants.MODULE_MOT)) {
                if (itemParamsEntity.getExceptionGroup().equals(Constants.EXCEPTION_GROUP.EXCEPTION)) {                          // Nhom Ngoai Le
                    if (itemParamsEntity.getExceptionType().equalsIgnoreCase(Constants.EXCEPTION_TYPE.VEHICLE)) {           // Ngoai le loai xe
                        if (FnCommon.isNullObject(itemParamsEntity.getExceptionVehicleType()))
                            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionVehicleType.empty"));
                        else
                            errorApp = ErrorApp.SUCCESS;
                    } else if (itemParamsEntity.getExceptionType().equalsIgnoreCase(Constants.EXCEPTION_TYPE.TICKET)) {     // Ngoai le loai gia ve
                        if (FnCommon.isNullObject(itemParamsEntity.getExceptionTicketType()))
                            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionTicketType.empty"));
                        else
                            errorApp = ErrorApp.SUCCESS;
                    } else
                        errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionType"));
                } else if (itemParamsEntity.getExceptionGroup().equals(Constants.EXCEPTION_GROUP.PRIORITIZE)) {                  // Nhom uu tien / cam
                    if (itemParamsEntity.getExceptionType().equalsIgnoreCase(Constants.EXCEPTION_TYPE.WHITELIST)) {         // Uu tien
                        if (FnCommon.isNullObject(itemParamsEntity.getWhiteListType()))
                            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.whiteType.empty"));
                        else
                            errorApp = ErrorApp.SUCCESS;
                    } else if (itemParamsEntity.getExceptionType().equalsIgnoreCase(Constants.EXCEPTION_TYPE.BLACKLIST)) {   // Cam
                        if (FnCommon.isNullObject(itemParamsEntity.getBlackListType()))
                            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.blackType.empty"));
                        else
                            errorApp = ErrorApp.SUCCESS;
                    } else
                        errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionType"));
                } else
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.invalid"));
            } else
                errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.invalid"));
        }

        // validate Empty list doan tram doi voi truong hop them moi phuong tien uu tien ngoai le cam
        if (!isUpdate && errorApp.getCode() == ErrorApp.SUCCESS.getCode()) {
            errorApp = validateEmptyStageAndStation(itemParamsEntity);
        }

        // Validate max length
        if (errorApp.getCode() == ErrorApp.SUCCESS.getCode()) {
            errorApp = checkMaxLength(itemParamsEntity);
        }

        return errorApp;
    }

    /**
     * Them moi xe (ngoai le/uu tien/cam) bang file
     *
     * @param authentication   params client
     * @param itemParamsEntity params client
     * @param excelFile        params client
     * @param files            params client
     * @return Object
     */
    @Override
    public Object postCreateVehiclesExceptionFile(Authentication authentication, VehiclesExceptionDTO itemParamsEntity, MultipartFile excelFile, List<MultipartFile> files) {
        Map<String, Object> objectMap = new HashMap<>();

        // Validate input
        ErrorApp errorEmpty = validateEmptyCreateVehiclesExceptionFile(itemParamsEntity, files);
        if (errorEmpty.getCode() != ErrorApp.SUCCESS.getCode()) {
            objectMap.put("error", errorEmpty);
            return objectMap;
        } else if (!FnCommon.validateFileName(excelFile.getOriginalFilename())) {
            objectMap.put("error", ErrorApp.ERR_DATA_EXCEL_FORMAT);
            return objectMap;
        }

        List<ExceptionListEntity> exceptionListEntitiesExcel = new ArrayList<>();
        String userLogin = FnCommon.getUserLogin(authentication);
        java.util.Date today = new java.util.Date();
        boolean isReadSuccess = true;
        byte[] bytes = new byte[0];
        int total = 0;

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile.getInputStream());
            List<Integer> sheetIndexs = new ArrayList<>();
            if (itemParamsEntity.getExceptionGroup() == 1L) {
                sheetIndexs.add(Constants.EXCEL_SHEET.EXCEPTION_VEHICLE);
                sheetIndexs.add(Constants.EXCEL_SHEET.EXCEPTION_TICKET);
            } else {
                sheetIndexs.add(Constants.EXCEL_SHEET.WHITELIST);
                sheetIndexs.add(Constants.EXCEL_SHEET.BLACKLIST);
            }
            for (Integer index : sheetIndexs) {
                List<Integer> errorRow = new ArrayList<>();
                XSSFSheet worksheet = workbook.getSheetAt(index);
                String headerExceptionType = worksheet.getRow(1).getCell(2) == null ? jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.exceptionType") : worksheet.getRow(1).getCell(2).toString();
                for (int i = 0; i <= worksheet.getLastRowNum(); i++) {
                    XSSFRow row = worksheet.getRow(i);
                    if (i == 1) {
                        row.getCell(0).getCellStyle();
                        Cell cell6 = row.createCell(6);
                        cell6.setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.description"));
                        cell6.setCellStyle(row.getCell(0).getCellStyle());
                        worksheet.autoSizeColumn(6);
                        worksheet.setColumnWidth(6, 10000);

                        Cell cell7 = row.createCell(7);
                        cell7.setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.column"));
                        cell7.setCellStyle(row.getCell(0).getCellStyle());
                    }
                    if (i >= 2 && row != null) {   // Doc tu row thu 3
                        total++;
                        if (FnCommon.isNullObject(row.getCell(1))
                                || row.getCell(1).toString().isEmpty()) {
                            row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plate.number.empty"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (FnCommon.isNullObject(row.getCell(2))
                                || row.getCell(2).toString().isEmpty()) {
                            row.createCell(6).setCellValue(headerExceptionType + " " + jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.data.empty"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (FnCommon.isNullObject(row.getCell(3))
                                || row.getCell(3).toString().isEmpty()) {
                            row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.eff.date.empty"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (!(2 == row.getCell(2).toString().split("\\|").length)) {
                            row.createCell(6).setCellValue(headerExceptionType + " " + jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.format"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (!FnCommon.isDate(row.getCell(3).toString(), Constants.EXCEL_DATE_FORMAT)) {
                            row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.eff.date"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (!FnCommon.isNullObject(row.getCell(4)) && !FnCommon.isDate(row.getCell(4).toString(), Constants.EXCEL_DATE_FORMAT)) {
                            row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.exp.date"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (row.getCell(3).getDateCellValue().compareTo(new java.util.Date(today.getYear(), today.getMonth(), today.getDate())) < 0) {
                            row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.eff.date.err"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else if (!FnCommon.isNullObject(row.getCell(4)) && row.getCell(4).getDateCellValue().compareTo(row.getCell(3).getDateCellValue()) < 0) {
                            row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exp.date.err"));
                            row.createCell(7).setCellValue(i + 1);
                            errorRow.add(i);
                        } else {
                            // Lay thong tin theo bien so xe
                            List<VehiclesExceptionDTO> listResult = (List<VehiclesExceptionDTO>) vehiclesExceptionRepository
                                    .getVehiclesByPlateNumber(VehiclesExceptionDTO.builder().plateNumber(row.getCell(1).toString()).build())
                                    .getListData();

                            if (listResult.size() < 1) {
                                row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.plateNumber"));
                                row.createCell(7).setCellValue(i + 1);
                                errorRow.add(i);
                            } else if (FnCommon.isNullOrEmpty(listResult.get(0).getEpc())) {
                                row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.epc"));
                                row.createCell(7).setCellValue(i + 1);
                                errorRow.add(i);
                            } else if (!FnCommon.isNullObject(row.getCell(5))
                                    && !row.getCell(5).toString().isEmpty()
                                    && row.getCell(5).toString().length() > 255) {
                                row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.note.length"));
                                row.createCell(7).setCellValue(i + 1);
                                errorRow.add(i);
                            } else {

                                String[] exceptionTypes = row.getCell(2).toString().split("\\|");

                                ExceptionListEntity exceptionListEntity = ExceptionListEntity.builder()
                                        .plateNumber(listResult.get(0).getPlateNumber())
                                        .contractId(listResult.get(0).getContractId())
                                        .contractNo(listResult.get(0).getContractNo())
                                        .customerId(listResult.get(0).getCustomerId())
                                        .customerName(listResult.get(0).getCustomerName())
                                        .vehicleTypeId(listResult.get(0).getVehicleTypeId())
                                        .licensePlateType(listResult.get(0).getLicensePlateType())
                                        .epc(listResult.get(0).getEpc())
                                        .rfidSerial(listResult.get(0).getRfidSerial())
                                        .vehicleId(listResult.get(0).getVehicleId())
                                        .effDate(new java.sql.Date(row.getCell(3).getDateCellValue().getTime()))
                                        .expDate(FnCommon.isNullObject(row.getCell(4)) ? null : new java.sql.Date(row.getCell(4).getDateCellValue().getTime()))
                                        .description(FnCommon.isNullObject(row.getCell(5)) ? null : row.getCell(5).toString())
                                        .tid("TID")     // TID Chua chot
                                        .status(itemParamsEntity.getStatus())
                                        .createDate(new Date(System.currentTimeMillis()))
                                        .createUser(userLogin)
                                        .stageId(itemParamsEntity.getStageId())
                                        .stageName(itemParamsEntity.getStageName())
                                        .stationId(itemParamsEntity.getStationId())
                                        .stationName(itemParamsEntity.getStationName())
                                        .laneInId(itemParamsEntity.getLaneInId())
                                        .laneOutId(itemParamsEntity.getLaneOutId())
                                        .laneInName(itemParamsEntity.getLaneInName())
                                        .laneOutName(itemParamsEntity.getLaneOutName())
                                        .exceptionVehicleType(index == Constants.EXCEL_SHEET.EXCEPTION_VEHICLE ? exceptionTypes[0] : null)
                                        .exceptionTicketType(index == Constants.EXCEL_SHEET.EXCEPTION_TICKET ? exceptionTypes[0] : null)
                                        .whiteListType(index == Constants.EXCEL_SHEET.WHITELIST ? Long.parseLong(exceptionTypes[0]) : null)
                                        .blackListType(index == Constants.EXCEL_SHEET.BLACKLIST ? Long.parseLong(exceptionTypes[0]) : null)
                                        .build();

                                if (index == Constants.EXCEL_SHEET.EXCEPTION_VEHICLE) {
                                    exceptionListEntity.setExceptionType(Constants.EXCEPTION_TYPE.VEHICLE);
                                } else if (index == Constants.EXCEL_SHEET.EXCEPTION_TICKET) {
                                    exceptionListEntity.setExceptionType(Constants.EXCEPTION_TYPE.TICKET);
                                } else if (index == Constants.EXCEL_SHEET.WHITELIST) {
                                    exceptionListEntity.setExceptionType(Constants.EXCEPTION_TYPE.WHITELIST);
                                } else {
                                    exceptionListEntity.setExceptionType(Constants.EXCEPTION_TYPE.BLACKLIST);
                                }

                                if (0 < vehiclesExceptionRepository.getCountExceptionList(exceptionListEntity)) {
                                    row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.duplicate.data"));
                                    row.createCell(7).setCellValue(i + 1);
                                    errorRow.add(i);
                                } else {
                                    ResultSelectEntity results = vehiclesExceptionRepository.getOCSCode(ServicePlanEntity.builder()
                                            .vehicleGroupId(exceptionListEntity.getVehicleTypeId())
                                            .laneIn(exceptionListEntity.getLaneInId())
                                            .laneOut(exceptionListEntity.getLaneOutId())
                                            .stageId(exceptionListEntity.getStageId())
                                            .stationId(exceptionListEntity.getStationId())
                                            .build(), exceptionListEntity.getExceptionType());

                                    // Neu getOCSCode ko co du lieu tiep tuc lay OCS code theo tram/doan
                                    if (FnCommon.isNullObject(results.getListData()) || results.getListData().size() < 1) {
                                        results = vehiclesExceptionRepository.getOCSCode(ServicePlanEntity.builder()
                                                .vehicleGroupId(exceptionListEntity.getVehicleTypeId())
                                                .stageId(exceptionListEntity.getStageId())
                                                .stationId(exceptionListEntity.getStationId())
                                                .build(), exceptionListEntity.getExceptionType());
                                    }

                                    if (FnCommon.isNullObject(results.getListData()) || results.getListData().size() < 1) {
                                        row.createCell(6).setCellValue(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ocs.empty"));
                                        row.createCell(7).setCellValue(i + 1);
                                        errorRow.add(i);
                                    } else {
                                        for (ServicePlanEntity entity : (List<ServicePlanEntity>) results.getListData()) {
                                            ExceptionListEntity newEntity = (ExceptionListEntity) FnCommon.convertObjectToObject(exceptionListEntity, ExceptionListEntity.class);
                                            newEntity.setOcsCode(entity.getOcsCode());
                                            exceptionListEntitiesExcel.add(newEntity);
                                        }
                                        worksheet.removeRow(row);
                                    }
                                }
                            }
                        }
                    }
                }

                // Thuc hien xoa row data thanh cong trong file excel thong bao loi
                if (errorRow.size() > 0) {
                    isReadSuccess = false;
                    int startIndex = 2;
                    Integer removeRow = 0;
                    for (Integer errorIndex : errorRow) {
                        if (errorIndex + removeRow != startIndex) {
                            worksheet.shiftRows(errorIndex + removeRow, worksheet.getLastRowNum(), startIndex - (errorIndex + removeRow)); //shifts rows between row 5 (index 4) and last row one row down
                            removeRow += startIndex - (errorIndex + removeRow);
                            if (worksheet instanceof XSSFSheet) {
                                // correcting bug that shiftRows does not adjusting references of the cells
                                // if row is shifted down, then reference in the cells remain r="A3", r="B3", ...
                                // they must be adjusted to the new row thoug: r="A4", r="B4", ...
                                // apache poi 3.17 has done this properly but had have other bugs in shiftRows.
                                for (int r = worksheet.getFirstRowNum(); r < worksheet.getLastRowNum() + 1; r++) {
                                    XSSFRow row = worksheet.getRow(r);
                                    if (row != null) {
                                        long rRef = row.getCTRow().getR();
                                        for (Cell cell : row) {
                                            String cRef = ((XSSFCell) cell).getCTCell().getR();
                                            ((XSSFCell) cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
                                        }
                                    }
                                }   // end correcting bug
                            }
                        }
                        startIndex++;
                    }
                }
            }

            // Ghi loi du lieu ra file
            if (!isReadSuccess) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                bytes = outputStream.toByteArray();
                outputStream.flush();
                outputStream.close();
            }

            workbook.close();

        } catch (Exception e) {
            log.error("error", e);
            ErrorApp errorApp = ErrorApp.ERR_DATA;
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel"));
            objectMap.put("error", errorApp);
            return objectMap;
        }

        if (0 == total) {
            ErrorApp errorApp = ErrorApp.ERR_DATA;
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.excel.empty"));
            objectMap.put("error", errorApp);
            return objectMap;
        }

        // insert data vao DB EXCEPTION_LIST
        if (exceptionListEntitiesExcel.size() > 0) {
            exceptionListEntitiesExcel = vehiclesExceptionRepository.saveAllExceptionList(exceptionListEntitiesExcel);

            List<AttachmentFileEntity> attachmentFileEntitys = new ArrayList<>();
            for (MultipartFile file : files) {
                try { // Luu file
                    String filePath = String.format("/%s/%s", "vehicles-exceptions", UUID.randomUUID().toString() + "-" + file.getOriginalFilename());
                    fileService.uploadFile(filePath, file.getBytes());
                    for (ExceptionListEntity exceptionListEntity : exceptionListEntitiesExcel) {
                        AttachmentFileEntity attachmentFileEntity = AttachmentFileEntity.builder()
                                .documentName(itemParamsEntity.getDocumentName())
                                .documentPath(filePath)
                                .objectId(exceptionListEntity.getExceptionListId())
                                .createUser(userLogin)
                                .createDate(new Date(System.currentTimeMillis()))
                                .attachmentType(Constants.ATTACHMENT_TYPE.EXCEPTION)    // 2. File đính kèm của nghiệp vụ quản lý Ngoại lê/cấm/ưu tiên.
                                .status(Constants.ATTACHMENT_FILE_STATUS.ACTIVE)        // 1. Hoat dong
                                .build();

                        attachmentFileEntitys.add(attachmentFileEntity);
                    }
                } catch (IOException ioEx) {
                    log.error("Upload File :" + ioEx);
                }
            }
            if (attachmentFileEntitys.size() > 0) // insert data vao DB ATTACHMENT_FILE
                vehiclesExceptionRepository.saveAttachmentFiles(attachmentFileEntitys);
        }

        objectMap.put("count", exceptionListEntitiesExcel.size() + "/" + total);
        objectMap.put("data", bytes);
        if (!isReadSuccess) {
            objectMap.put("error", ErrorApp.ERR_DATA_EXCEL_FILE);
        } else {
            objectMap.put("error", ErrorApp.SUCCESS);
        }
        return objectMap;
    }

    /**
     * Validate Input API tao moi phuong tien ngoai le, uu tien, cam bang file excel
     *
     * @param itemParamsEntity params client
     * @param files            params client
     * @return ErrorApp
     */
    private ErrorApp validateEmptyCreateVehiclesExceptionFile(VehiclesExceptionDTO itemParamsEntity, List<MultipartFile> files) {
        ErrorApp errorApp = ErrorApp.ERR_DATA;

        if (FnCommon.isNullOrEmpty(itemParamsEntity.getModule())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.module.empty"));
            return errorApp;
        } else if (FnCommon.isNullObject(itemParamsEntity.getExceptionGroup())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionGroup.empty"));
            return errorApp;
        } else if (FnCommon.isNullObject(itemParamsEntity.getStatus())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.status.empty"));
            return errorApp;
        } else if (FnCommon.isNullOrEmpty(itemParamsEntity.getDocumentName())) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.documentName.empty"));
            return errorApp;
        } else if (files == null || files.size() <= 0) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.document.files.empty"));
            return errorApp;
        } else if (!((!FnCommon.isNullObject(itemParamsEntity.getStageId()) && !FnCommon.isNullOrEmpty(itemParamsEntity.getStageName()))
                || (!FnCommon.isNullObject(itemParamsEntity.getStationId()) && !FnCommon.isNullOrEmpty(itemParamsEntity.getStationName())))) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.station.empty"));
            return errorApp;
        }

        // Validate max length
        return checkMaxLength(itemParamsEntity);
    }

    /**
     * Validate empty cho tram va doan
     *
     * @param itemParamsEntity params client
     * @return ErrorApp
     */
    private ErrorApp validateEmptyStageAndStation(VehiclesExceptionDTO itemParamsEntity) {
        ErrorApp errorApp = ErrorApp.ERR_DATA;

        if ((itemParamsEntity.getStages() == null || itemParamsEntity.getStages().size() == 0)
                && (itemParamsEntity.getStations() == null || itemParamsEntity.getStations().size() == 0)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.station.empty"));
            return errorApp;
        }

        // Validate danh sach doan
        if (!FnCommon.isNullObject(itemParamsEntity.getStages())
                && itemParamsEntity.getStages().size() > 0) {
            for (VehiclesExceptionDTO.stage stage : itemParamsEntity.getStages()) {
                if (FnCommon.isNullObject(stage.getStageId())) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.stationId.empty"));
                    return errorApp;
                } else if (FnCommon.isNullOrEmpty(stage.getStageName())) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.stationName.empty"));
                    return errorApp;
                }
            }
        }
        // Validate danh sach tram
        if (!FnCommon.isNullObject(itemParamsEntity.getStations())
                && itemParamsEntity.getStations().size() > 0) {
            for (VehiclesExceptionDTO.station station : itemParamsEntity.getStations()) {
                if (FnCommon.isNullObject(station.getStationId())) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.stationId.empty"));
                    return errorApp;
                } else if (FnCommon.isNullOrEmpty(station.getStationName())) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stage.stationName.empty"));
                    return errorApp;
                }
            }
        }
        return ErrorApp.SUCCESS;
    }

    /**
     * Xóa xe (ngoai le/uu tien/cam)
     *
     * @param authentication params client
     * @param id             params client
     * @return Object
     */
    @Override
    public Object deleteVehiclesException(Authentication authentication, Long id) {
        Map<String, Object> objectMap = new HashMap<>();
        ErrorApp errorApp = ErrorApp.ERR_DATA;

        ExceptionListEntity exceptionListEntity = exceptionListServiceJPA.findByExceptionListId(id);

        if (FnCommon.isNullObject(exceptionListEntity)) {
            log.warn("ExceptionList not found, Exception_List_Id: " + id);
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.find"));
            objectMap.put("error", errorApp);
            return objectMap;
        }

        if (exceptionListEntity.getStatus() != 1L) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.delete"));
            objectMap.put("error", errorApp);
            return objectMap;
        }

        Boolean isDelete = vehiclesExceptionRepository.deleteExceptionList(id);
        if (isDelete) {
            vehiclesExceptionRepository.deleteAttachmentFiles(id);
        }

        objectMap.put("error", ErrorApp.SUCCESS);
        return objectMap;
    }

    /**
     * sua thong tin xe (ngoai le/uu tien/cam)
     *
     * @param authentication   params client
     * @param itemParamsEntity params client
     * @param files            params client
     * @return Object
     */
    @Override
    public Object updateVehiclesException(Authentication authentication, VehiclesExceptionDTO itemParamsEntity, List<MultipartFile> files) {
        Map<String, Object> objectMap = new HashMap<>();
        ErrorApp errorValid = validateAdd(itemParamsEntity, files, true);
        if (errorValid.getCode() != ErrorApp.SUCCESS.getCode()) {
            objectMap.put("error", errorValid);
            return objectMap;
        }

        // CHECK ATTACHMENT_FILE (file dinh kem sau thi xoa het ma khong co add them file dinh kem se bao loi)
        if (null != itemParamsEntity.getAttachmentFileIds()) {
            Integer count = vehiclesExceptionRepository.getCountDatAttachmentFileAfterDelete(itemParamsEntity.getAttachmentFileIds(), itemParamsEntity.getExceptionListId());
            if (0 >= count && FnCommon.isNullObject(files)) {
                ErrorApp errorApp = ErrorApp.ERR_DATA;
                errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.document.files.empty"));
                objectMap.put("error", errorApp);
                return objectMap;
            }
        }

        ExceptionListEntity exceptionListEntity = (ExceptionListEntity) FnCommon.convertObjectToObject(itemParamsEntity, ExceptionListEntity.class);
        exceptionListEntity.setTid("TID");      // Chua chot

        //CHECK DUPLICATE DATA
        Integer count = vehiclesExceptionRepository.getCountExceptionList(exceptionListEntity);
        if (0 < count) {
            ErrorApp errorApp = ErrorApp.ERR_DATA;
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.duplicate.data"));
            objectMap.put("error", errorApp);
            return objectMap;
        }

        // UPDATE EXCEPTION_LIST
        exceptionListEntity = vehiclesExceptionRepository.saveExceptionList(exceptionListEntity);

        // UPDATE DOCUMENT_NAME in ATTACHMENT_FILE
        vehiclesExceptionRepository.UpdateDocumentNameByExceptionListId(itemParamsEntity);

        // DELETE OLD ATTACHMENT_FILE
        if (itemParamsEntity.getAttachmentFileIds() != null) {
            vehiclesExceptionRepository.deleteAttachmentFileByIds(itemParamsEntity.getAttachmentFileIds());
        }

        // INSERT NEW ATTACHMENT_FILE
        if (files != null) {
            List<AttachmentFileEntity> attachmentFileEntitys = new ArrayList<>();
            String userLogin = FnCommon.getUserLogin(authentication);
            Date currDate = new Date(System.currentTimeMillis());

            for (MultipartFile file : files) {
                try {
                    String filePath = String.format("/%s/%s", "vehicles-exceptions", UUID.randomUUID().toString() + "-" + file.getOriginalFilename());
                    fileService.uploadFile(filePath, file.getBytes());

                    AttachmentFileEntity attachmentFileEntity = AttachmentFileEntity.builder()
                            .documentName(itemParamsEntity.getDocumentName())
                            .documentPath(filePath)
                            .objectId(exceptionListEntity.getExceptionListId())
                            .createUser(userLogin)
                            .createDate(currDate)
                            .attachmentType(Constants.ATTACHMENT_TYPE.EXCEPTION)    // 2. File đính kèm của nghiệp vụ quản lý Ngoại lê/cấm/ưu tiên.
                            .status(Constants.ATTACHMENT_FILE_STATUS.ACTIVE)        // 1. Hoat dong
                            .build();

                    attachmentFileEntitys.add(attachmentFileEntity);
                } catch (IOException ioEx) {
                    log.error("Upload File :" + ioEx);
                }
            }

            if (attachmentFileEntitys.size() > 0) {
                vehiclesExceptionRepository.saveAttachmentFiles(attachmentFileEntitys);
            }
        }
        objectMap.put("error", ErrorApp.SUCCESS);
        return objectMap;
    }

    @Override
    public Object updateException(Authentication authentication, VehiclesExceptionDTO dataParams, Long exceptionId) throws Exception {
        ExceptionListEntity exceptionListEntity = exceptionListServiceJPA.getOne(exceptionId);
        if (exceptionListEntity == null) {
            return null;
        }
        Long contractId = null;
        if (exceptionListEntity.getContractId() != null) {
            contractId = Long.valueOf(exceptionListEntity.getContractId());
        }
        long now = System.currentTimeMillis();
        Long stationIn;
        Long stationOut;
        String plateTypeCode = FnCommon.mappingPlateTypeBOO2ToBOO1(String.valueOf(exceptionListEntity.getLicensePlateType()));
        if (Constants.EXCEPTION_APPROVED.equals(exceptionListEntity.getStatus())) {
            if (dataParams.getExpDate() != null && dataParams.getEffDate().getTime() >= dataParams.getExpDate().getTime()) {
                throw new EtcException("crm.eff.date.after.exp.date");
            }
            if (dataParams.getExpDate() != null && dataParams.getExpDate().getTime() <= now) {
                throw new EtcException("crm.exp.date.is.past");
            }
            // check trung
            List<ExceptionListEntity> exceptionList = exceptionListServiceJPA.exceptionCheck(exceptionListEntity.getExceptionType(),
                    exceptionListEntity.getPlateNumber(), exceptionListEntity.getLicensePlateType(), exceptionListEntity.getStationId(),
                    exceptionListEntity.getStageId(), exceptionListEntity.getPromotionId());

            for (ExceptionListEntity exceptionCheck : exceptionList) {
                if (exceptionId.equals(exceptionCheck.getExceptionListId())) {
                    continue;
                }
                if (exceptionCheck.getExpDate() != null) {
                    if (exceptionListEntity.getEffDate().after(exceptionCheck.getExpDate())) {
                        continue;
                    } else if (exceptionListEntity.getExpDate().before(exceptionCheck.getEffDate())) {
                        continue;
                    }
                } else {
                    if (exceptionListEntity.getExpDate().before(exceptionCheck.getEffDate())) {
                        continue;
                    }
                }
                throw new EtcException("crm.date.overlap");
            }
            exceptionListEntity.setExpDate(dataParams.getExpDate());
            VehicleAddSuffOfferDTO suffOffer = new VehicleAddSuffOfferDTO();
            suffOffer.setEpc(exceptionListEntity.getEpc());
            suffOffer.setEffDate(exceptionListEntity.getEffDate());
            if (exceptionListEntity.getExpDate() == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(exceptionListEntity.getEffDate());
                calendar.add(Calendar.YEAR, 30);
                suffOffer.setExpDate(calendar.getTime());
            } else {
                suffOffer.setExpDate(exceptionListEntity.getExpDate());
            }
            suffOffer.setOfferId(exceptionListEntity.getOcsCode());
            suffOffer.setOfferLevel(Constants.OFFER_LEVEL_DEFAULT.toString());
            suffOffer.setAutoRenew("0");
            OCSResponse ocsResponse = ocsService.changeSupOffer(suffOffer, authentication, contractId, 1L, null, null);
            if (!FnCommon.checkOcsCode(ocsResponse)) {
                throw new EtcException("ocs.communicate.error");
            }
            if (Constants.BOO_INFO.CONTRACT_ID.equals(contractId)) {
                if (exceptionListEntity.getStageId() != null) {
                    String requestUrl = stageUrl.replace("{stageId}", exceptionListEntity.getStageId().toString());
                    String stageResponse = FnCommon.doGetRequest(requestUrl, null, FnCommon.getStringToken(authentication));
                    if (stageResponse != null) {
                        JSONObject stageRes = new JSONObject(stageResponse);
                        JSONObject stageData = stageRes.getJSONObject("data");
                        JSONObject in = stageData.getJSONObject("stationIn");
                        JSONObject out = stageData.getJSONObject("stationOut");
                        stationIn = in.getLong("id");
                        stationOut = out.getLong("id");
                        String body = new ReqExceptionListDTO().getStringReq(exceptionListEntity, "U", stationIn, stationOut, plateTypeCode);
                        //dong bo boo1
                        boo1Service.doPostBoo1Request(onlineEventUrl, body,0, body, Constants.ACT_TYPE.BOO1_SYNC);
                    }
                }
            }
            exceptionListServiceJPA.save(exceptionListEntity);
            return dataParams;
        } else if (Constants.EXCEPTION_NEW.equals(exceptionListEntity.getStatus())) {
            Long vehicleGroupId = null;
            if (dataParams.getExpDate() != null && dataParams.getEffDate().getTime() >= dataParams.getExpDate().getTime()) {
                throw new EtcException("crm.eff.date.after.exp.date");
            }
            VehicleEntity vehicleEntity = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(dataParams.getPlateNumber(), String.valueOf(dataParams.getLicensePlateType()));
            if (vehicleEntity == null) {
                ReqActivationCheckDTO req = new ReqActivationCheckDTO();
                req.setPlate(FnCommon.getPlateNumberBoo1(dataParams.getPlateNumber(), FnCommon.mappingPlateTypeBOO2ToBOO1(plateTypeCode)));
                req.setRequest_id(System.currentTimeMillis());
                req.setRequest_datetime(System.currentTimeMillis());
                ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
                if (res != null && Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                    vehicleGroupId = res.getVehicle_type();
                }
            } else {
                vehicleGroupId = vehicleEntity.getVehicleGroupId();
            }
            exceptionListEntity.setPlateNumber(dataParams.getPlateNumber());
            exceptionListEntity.setEpc(dataParams.getEpc());
            exceptionListEntity.setEffDate(dataParams.getEffDate());
            exceptionListEntity.setExpDate(dataParams.getExpDate());
            exceptionListEntity.setTid(dataParams.getTid());
            exceptionListEntity.setLicensePlateType(dataParams.getLicensePlateType());
            exceptionListEntity.setStationId(dataParams.getStationId());
            exceptionListEntity.setStationName(dataParams.getStationName());
            exceptionListEntity.setStageId(dataParams.getStageId());
            exceptionListEntity.setStageName(dataParams.getStageName());
            exceptionListEntity.setCustomerId(dataParams.getCustomerId());
            exceptionListEntity.setCustomerName(dataParams.getCustomerName());
            exceptionListEntity.setContractId(dataParams.getContractId());
            exceptionListEntity.setContractNo(dataParams.getContractNo());
            exceptionListEntity.setCreateUser(FnCommon.getUserLogin(authentication));
            exceptionListEntity.setExceptionType(dataParams.getExceptionType());
            exceptionListEntity.setPromotionId(dataParams.getPromotionId());
            exceptionListEntity.setRegisterVehicleType(dataParams.getRegisterVehicleType());
            exceptionListEntity.setDescription(dataParams.getDescription());
            switch (dataParams.getExceptionType()) {
                case Constants.EXCEPTION_VEHICLE:
                    exceptionListEntity.setExceptionVehicleType(dataParams.getExceptionVehicleType().toString());
                    exceptionListEntity.setVehicleTypeId(vehicleGroupId);
                    break;
                case Constants.EXCEPTION_TICKET:
                    exceptionListEntity.setVehicleTypeId(vehicleGroupId);
                    break;
                case Constants.EXCEPTION_PRIORITY:
                    exceptionListEntity.setWhiteListType(dataParams.getWhiteListType());
                    break;
                case Constants.EXCEPTION_BAN:
                    exceptionListEntity.setBlackListType(dataParams.getBlackListType());
            }

            // check trung
            List<ExceptionListEntity> exceptionList = exceptionListServiceJPA.exceptionCheck(exceptionListEntity.getExceptionType(),
                    exceptionListEntity.getPlateNumber(), exceptionListEntity.getLicensePlateType(), exceptionListEntity.getStationId(),
                    exceptionListEntity.getStageId(), exceptionListEntity.getPromotionId());

            for (ExceptionListEntity exceptionCheck : exceptionList) {
                if (exceptionId.equals(exceptionCheck.getExceptionListId())) {
                    continue;
                }
                if (exceptionCheck.getExpDate() != null) {
                    if (exceptionListEntity.getEffDate().after(exceptionCheck.getExpDate())) {
                        continue;
                    } else if (exceptionListEntity.getExpDate().before(exceptionCheck.getEffDate())) {
                        continue;
                    }
                } else {
                    if (exceptionListEntity.getExpDate().before(exceptionCheck.getEffDate())) {
                        continue;
                    }
                }
                throw new EtcException("crm.date.overlap");
            }

            exceptionListServiceJPA.save(exceptionListEntity);
            return dataParams;
        } else {
            return null;
        }
    }

    @Override
    public AttachmentFileDTO downloadAttachmentFile(Long attachmentFileId) {
        return vehiclesExceptionRepository.downloadAttachmentFile(attachmentFileId);
    }

    @Override
    public List<AttachmentFileEntity> getAttachmentFiles(Long objectId, Long attachmentType) {
        return vehiclesExceptionRepository.getAttachmentFiles(objectId, attachmentType);
    }

    /**
     * Check max lengeth input
     *
     * @param item params client
     * @return Object
     */
    private ErrorApp checkMaxLength(VehiclesExceptionDTO item) {
        ErrorApp errorApp = ErrorApp.ERR_DATA;

        if (!FnCommon.isNullObject(item.getExceptionListId())
                && FnCommon.isMaxLengthTooLarge(item.getExceptionListId(), 10)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionListId.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getStationId())
                && FnCommon.isMaxLengthTooLarge(item.getStationId(), 10)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stationId.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getStageId())
                && FnCommon.isMaxLengthTooLarge(item.getStageId(), 10)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stageId.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getVehicleTypeId())
                && FnCommon.isMaxLengthTooLarge(item.getVehicleTypeId(), 10)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.vehicleTypeId.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getExceptionTicketType())
                && FnCommon.isMaxLengthTooLarge(item.getExceptionTicketType(), 1)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionTicketType.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getLicensePlateType())
                && FnCommon.isMaxLengthTooLarge(item.getLicensePlateType(), 1)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.licensePlateType.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getExceptionVehicleType())
                && FnCommon.isMaxLengthTooLarge(item.getExceptionVehicleType(), 10)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionVehicleType.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getRouterId())
                && FnCommon.isMaxLengthTooLarge(item.getRouterId(), 10)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.routerId.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getWhiteListType())
                && FnCommon.isMaxLengthTooLarge(item.getWhiteListType(), 2)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.whiteListType.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getBlackListType())
                && FnCommon.isMaxLengthTooLarge(item.getBlackListType(), 2)) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.blackListType.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getExceptionListIds())) {
            List<Long> listInvalid = item.getExceptionListIds().stream()
                    .filter(id -> FnCommon.isMaxLengthTooLarge(id, 10))
                    .collect(Collectors.toList());
            if (!FnCommon.isNullOrEmpty(listInvalid)) {
                errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.exceptionListId.length"));
                return errorApp;
            }
        }

        if (!FnCommon.isNullOrEmpty(item.getPlateNumber()) && item.getPlateNumber().length() > 100) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.plateNumber.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getEpc()) && item.getEpc().length() > 50) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.epc.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getTid()) && item.getTid().length() > 50) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.tid.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getRfidSerial()) && item.getRfidSerial().length() > 50) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.rfidSerial.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getDescription()) && item.getDescription().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.description.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCustomerName()) && item.getCustomerName().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.customerName.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getContractNo()) && item.getContractNo().length() > 100) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.contractNo.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getContractId()) && item.getContractId().length() > 10) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.contractId.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCreateUser()) && item.getCreateUser().length() > 50) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.createUser.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApprovedUser()) && item.getApprovedUser().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.approvedUser.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getProcessUser()) && item.getProcessUser().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.processUser.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelUser()) && item.getCancelUser().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.CancelUser.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getStageName()) && item.getStageName().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stageName.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getStationName()) && item.getStationName().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stationName.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getRouterName()) && item.getRouterName().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.RouterName.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getProcessCommnents()) && item.getProcessCommnents().length() > 1000) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ProcessCommnents.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getApproveCommnents()) && item.getApproveCommnents().length() > 1000) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.ApproveCommnents.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getCancelCommnents()) && item.getCancelCommnents().length() > 1000) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.CancelCommnents.length"));
            return errorApp;
        }

        if (!FnCommon.isNullOrEmpty(item.getDocumentName()) && item.getDocumentName().length() > 255) {
            errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.epc.length"));
            return errorApp;
        }

        if (!FnCommon.isNullObject(item.getStages())) {
            for (VehiclesExceptionDTO.stage stage : item.getStages()) {
                if (!FnCommon.isNullObject(stage.getStageId()) && FnCommon.isMaxLengthTooLarge(stage.getStageId(), 10)) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stageId.length"));
                    return errorApp;
                }
                if (!FnCommon.isNullObject(stage.getStageName()) && stage.getStageName().length() > 255) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stageName.length"));
                    return errorApp;
                }
            }
        }

        if (!FnCommon.isNullObject(item.getStations())) {
            for (VehiclesExceptionDTO.station station : item.getStations()) {
                if (!FnCommon.isNullObject(station.getStationId()) && FnCommon.isMaxLengthTooLarge(station.getStationId(), 10)) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stationId.length"));
                    return errorApp;
                }
                if (!FnCommon.isNullObject(station.getStationName()) && station.getStationName().length() > 255) {
                    errorApp.setDescription(jedisCacheService.getMessageErrorByKey("crm.vehicles.exception.stationName.length"));
                    return errorApp;
                }
            }
        }

        return ErrorApp.SUCCESS;
    }

    private ResActivationCheckDTO getBoo1Vehicle(String plateNumber, Authentication authentication) {
        ReqActivationCheckDTO req = new ReqActivationCheckDTO();
        req.setPlate(plateNumber);
        req.setRequest_id(System.currentTimeMillis());
        req.setRequest_datetime(System.currentTimeMillis());
        ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
        if (res != null && Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
            return res;
        } else {
            return null;
        }
    }
}
