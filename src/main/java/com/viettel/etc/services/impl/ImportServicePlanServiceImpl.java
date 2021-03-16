package com.viettel.etc.services.impl;

import com.viettel.etc.dto.AddServicePlanDTO;
import com.viettel.etc.dto.StageDTO;
import com.viettel.etc.dto.StationDTO;
import com.viettel.etc.repositories.tables.entities.BotRevenueShareEntity;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.repositories.tables.entities.ServicePlanTypeEntity;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.BotRevenueShareServiceJPA;
import com.viettel.etc.services.tables.ServicePlanServiceJPA;
import com.viettel.etc.services.tables.ServicePlanTypeServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportServicePlanServiceImpl implements ImportServicePlanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesServiceImpl.class);

    @Autowired
    private BotService botService;

    @Autowired
    private StageService stageService;

    @Autowired
    private StationService stationService;

    @Autowired
    private ServicePlanTypeServiceJPA servicePlanTypeServiceJPA;

    @Autowired
    private ServicePlanServiceJPA servicePlanServiceJPA;

    @Autowired
    private BotRevenueShareServiceJPA botRevenueShareServiceJPA;

    @Autowired
    JedisCacheService jedisCacheService;

    /**
     * Import du lieu tu file excel
     *
     * @param fileImport     file import
     * @param authentication Ma xac thuc
     * @return ResponseEntity
     * @throws IOException  loi doc ghi file
     * @throws EtcException loi phan hoi
     */
    @Override
    public ResponseEntity<?> importServicePlan(MultipartFile fileImport, Authentication authentication) throws EtcException, IOException {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        boolean isExcel = FnCommon.validateFileName(fileName);
        StringBuilder hasError = new StringBuilder();
        String token = FnCommon.getStringToken(authentication);
        String customer = FnCommon.getUserLogin(authentication);
        List<AddServicePlanDTO> listAddServicePlanDTO = new ArrayList<>();
        List<String> listError = new ArrayList<>();
        if (!isExcel) {
            throw new EtcException("common.validate.err.data.excel");
        }
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            List<String> lstServicePlanCode = new ArrayList<>();
            Map<String, Long> countServicePlanCode = new HashMap<>();
            boolean validateHeaderCell = FnCommon.validateHeaderCell(sheet, 2, 12, headerContent());
            if (validateHeaderCell) {
                if (checkLimitRow(sheet, lstServicePlanCode)) {
                    if (!lstServicePlanCode.isEmpty()) {
                        countServicePlanCode = lstServicePlanCode.stream().distinct().collect(Collectors.toMap(a -> a, a -> 0L));
                    }
                    Map<String, List<Map<String, String>>> revenueShare = handleRevenueShareExcel(workbook);
                    CellStyle cellHeaderStyle = FnCommon.createAndSetStyleCell(workbook);
                    CellStyle cellContentStyle = FnCommon.createAndSetStyleCell(workbook);
                    FnCommon.setFontHeaderCell(workbook, cellHeaderStyle);
                    addHeaderContent(sheet, 3, 19, 20, cellHeaderStyle);
                    List<LinkedHashMap<?, ?>> bots = botService.findAllBots(token);
                    List<LinkedHashMap<?, ?>> stages = stageService.findAllStages(token);
                    List<LinkedHashMap<?, ?>> stations = stationService.findAllStations(token);
                    List<ServicePlanTypeEntity> servicePlanTypes = servicePlanTypeServiceJPA.findAll();
                    handleRowContent(sheet, listAddServicePlanDTO, listError, cellContentStyle, revenueShare, token, bots,
                            stations, stages, servicePlanTypes, countServicePlanCode, hasError);

                    sheet.autoSizeColumn(19);
                    sheet.autoSizeColumn(20);
                    if (FnCommon.isNullOrEmpty(hasError.toString())) {
                        if (!listAddServicePlanDTO.isEmpty() && FnCommon.isNullOrEmpty(hasError.toString())) {
                            List<ServicePlanEntity> data = convertToListServicePlanEntity(listAddServicePlanDTO, customer);
                            saveServicePlanAndRevenueShare(data, revenueShare, bots);
                        }
                    }
                }
                org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                bytes = os.toByteArray();
            } else {
                return new ResponseEntity<>(FnCommon.responseToClient(new EtcException("common.validate.err.data.excel")), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
        }
        return returnFileExcel(bytes, fileName, hasError);
    }


    /**
     * Kiem tra so ban ghi khong lon hon 1000 ban ghi
     *
     * @param sheet Sheet du lieu
     * @return false or true
     */
    public boolean checkLimitRow(Sheet sheet, List<String> lstServicePlanCode) {
        int count = 0;
        if (sheet != null) {
            for (int i = 4; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (hasEmptyOrNullRow(row, 1, 12)) {
                    if (count > 1000) {
                        return false;
                    }
                    if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(1)))) {
                        lstServicePlanCode.add(FnCommon.getStringValue(row.getCell(1)));
                    }
                    count++;
                }
            }
        }
        return true;
    }

    /**
     * check content row has empty or null
     *
     * @param row      Du lieu ma row can kiem tra
     * @param fromCell Cot bat dau thuc hien
     * @param toCell   Cot Ket thuc thuc hien
     * @return Chuoi kiem tra
     */
    private boolean hasEmptyOrNullRow(Row row, int fromCell, int toCell) {
        if (row != null) {
            for (int i = fromCell; i <= toCell; i++) {
                if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(i)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Import du lieu tu file excel
     * <p>
     * Xu ly du lieu doanh thu chia se trong file excel
     *
     * @param workbook File excel
     * @return
     */
    public Map<String, List<Map<String, String>>> handleRevenueShareExcel(Workbook workbook) {
        List<Map<String, String>> result = new ArrayList();
        Sheet sheet = workbook.getSheetAt(0);
        int lastRow = sheet.getLastRowNum();
        for (int i = 4; i < lastRow; i++) {
            Map<String, String> element = new HashMap<>();
            Row row = sheet.getRow(i);
            Cell cellServicePlan = row.getCell(13);
            Cell cellBot = row.getCell(14);
            Cell cellRevenueShare = row.getCell(15);
            if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(cellServicePlan)) &&
                    !FnCommon.isNullOrEmpty(FnCommon.getStringValue(cellBot))
            ) {
                element.put("servicePlanCode", FnCommon.getStringValue(cellServicePlan).trim());
                element.put("botName", FnCommon.getStringValue(cellBot));
                String botId = FnCommon.getIdFromString(FnCommon.getStringValue(cellBot).trim());
                element.put("botId", botId);
                if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(cellRevenueShare))) {
                    element.put("revenueShare", FnCommon.getStringValue(cellRevenueShare).trim());
                } else {
                    element.put("revenueShare", "");
                }
                result.add(element);
            }
        }
        return result.stream().collect(Collectors.groupingBy(value -> value.get("servicePlanCode")));
    }

    /**
     * Noi dung phan header
     *
     * @return Chuoi string header cua sheet
     */
    private String headerContent() {
        StringBuilder headerContent = new StringBuilder();
        headerContent.append("STT").append("servicePlanCode").append("servicePlanTypeID").append("stationId")
                .append("stageId").append("vehicleGroupId").append("fee").append("ocsCode")
                .append("startDate").append("endDate").append("autoRenew").append("useDay");
        return headerContent.toString();

    }

    /**
     * Check header
     *
     * @param sheet             sheet lua chon
     * @param indexHeaderRow    chi muc hang
     * @param indexCheckInvalid chi muc cot kiem tra du lieu hop le
     * @param indexReason       chi muc cot ghi ly do
     * @param headerStyle       set style
     */
    private void addHeaderContent(Sheet sheet, int indexHeaderRow, int indexCheckInvalid, int indexReason, CellStyle headerStyle) {
        if (sheet.getLastRowNum() >= 4) {
            Cell cell16 = sheet.getRow(indexHeaderRow).createCell(indexCheckInvalid);
            cell16.setCellStyle(headerStyle);
            cell16.setCellValue("Kiểm tra hợp lệ");
            Cell cell17 = sheet.getRow(indexHeaderRow).createCell(indexReason);
            cell17.setCellStyle(headerStyle);
            cell17.setCellValue("Lý do");
        }
    }

    /**
     * Import du lieu tu file excel
     * <p>
     * Xu ly noi dung file
     *
     * @param sheet        sheet lua chon
     * @param list         danh sach du lieu bang cuoc gia ve import
     * @param listError    danh sach loi tra ra
     * @param cellStyle    style
     * @param revenueShare danh sach doanh thu chia se
     */
    private void handleRowContent(Sheet sheet, List<AddServicePlanDTO> list, List<String> listError, CellStyle cellStyle,
                                  Map<String, List<Map<String, String>>> revenueShare,
                                  String token, List<LinkedHashMap<?, ?>> bots, List<LinkedHashMap<?, ?>> stations,
                                  List<LinkedHashMap<?, ?>> stages, List<ServicePlanTypeEntity> servicePlanTypeEntityList,
                                  Map<String, Long> countServicePlanCode, StringBuilder hasError) {
        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            StringBuilder result = new StringBuilder();
            AddServicePlanDTO servicePlanDTO = new AddServicePlanDTO();
            Row row = sheet.getRow(i);
            if (!hasEmptyOrNullRow(row, 0, 12)) {
                continue;
            }
            validateInputExcel(servicePlanDTO, row, result, listError, revenueShare, token, bots, stations, stages,
                    servicePlanTypeEntityList, countServicePlanCode);
            if (!"".equals(result.toString())) {
                hasError.append("error");
                listError.add(result.toString());
                Cell cellCheck = row.createCell(19);
                cellCheck.setCellStyle(cellStyle);
                cellCheck.setCellValue(jedisCacheService.getMessageErrorByKey("validate.excel.data.invalid"));
                Cell cellReason = row.createCell(20);
                cellReason.setCellStyle(cellStyle);
                cellReason.setCellValue(result.toString());
            } else {
                Cell cell = row.createCell(19);
                cell.setCellValue(jedisCacheService.getMessageErrorByKey("validate.excel.data.valid"));
                cell.setCellStyle(cellStyle);
                list.add(servicePlanDTO);
            }
        }
    }

    /**
     * Kiem tra du lieu dau vao cua file excel
     *
     * @param servicePlanDTO Du lieu bang cuoc gia ve can kiem tra
     * @param row            hang thuc hien
     * @param result         Chuoi kiem tra ket qua loi
     * @param listError      danh sach loi
     * @param revenueShare   doanh thu chia se
     */
    private void validateInputExcel(AddServicePlanDTO servicePlanDTO, Row row, StringBuilder result, List<String> listError,
                                    Map<String, List<Map<String, String>>> revenueShare, String token, List<LinkedHashMap<?, ?>> bots,
                                    List<LinkedHashMap<?, ?>> stations, List<LinkedHashMap<?, ?>> stages,
                                    List<ServicePlanTypeEntity> servicePlanTypeEntityList, Map<String, Long> countServicePlanCode
    ) {
        validateServicePlanCode(row, servicePlanDTO, countServicePlanCode, result, listError);
        validateServicePlanTypeId(row, servicePlanDTO, servicePlanTypeEntityList, result);
        validateStationAndStage(row, servicePlanDTO, result, token, revenueShare);
        validateVehicleGroupId(row, servicePlanDTO, result);
        validateFee(row, servicePlanDTO, result, revenueShare);
        validateOcsCode(row, servicePlanDTO, result);
        validateStartDate(row, servicePlanDTO, result);
        validateEndDate(row, servicePlanDTO, result);
        validateAutoRenew(row, servicePlanDTO, result);
    }

    /**
     * Xu ly du lieu doan
     *
     * @param type           Loai kiem tra. 1. Tram; 2. Doan
     * @param value          Gia tri cua tram hoac doan
     * @param botKey         Key tim kiem trong map
     * @param token          Xac thuc
     * @param revenueShare   Doanh thu chia se
     * @param result         Chuoi kiem tra loi
     * @param servicePlanDTO Entity bang cuoc gia ve
     */
    public void handleStationOrStage(int type, String value, String botKey, String token, Map<String, List<Map<String, String>>> revenueShare, StringBuilder result, AddServicePlanDTO servicePlanDTO) {
        String stationId = null;
        String stageId = null;
        String id = FnCommon.getIdFromString(value);
        if (id.length() > 10) {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.stage_station.max_length")).append(",");
        }
        if (!revenueShare.containsKey(botKey)) {
            return;
        }
        if (type == 1) {
            stationId = id;
            if (!FnCommon.isNullOrEmpty(stationId)) {
                servicePlanDTO.setStationId(Long.parseLong(stationId));
                servicePlanDTO.setStationName(value);
                servicePlanDTO.setScope(3L);
                List<StationDTO.Station> stations = stationService.findStationsByListId(token, Arrays.asList(servicePlanDTO.getStationId()));
                if (stations != null && !stations.isEmpty()) {
                    servicePlanDTO.setChargeMethodId(stations.get(0).getMethod_charge_id());
                    servicePlanDTO.setRouteId(stations.get(0).getRoute_id());
                }


            }

        } else {
            stageId = id;
            if (!FnCommon.isNullOrEmpty(stageId)) {
                servicePlanDTO.setStageId(Long.parseLong(stageId));
                servicePlanDTO.setStageName(value);
                servicePlanDTO.setScope(4L);
                List<StageDTO.Stage> stages = stageService.findStagesByListId(token, Arrays.asList(servicePlanDTO.getStageId()));
                if (stages != null && !stages.isEmpty()) {
                    servicePlanDTO.setStationInId(stages.get(0).getStationIn().getId());
                    servicePlanDTO.setStationOutId(stages.get(0).getStationOut().getId());
                    servicePlanDTO.setChargeMethodId(stages.get(0).getMethod_charge_id());
                    servicePlanDTO.setRouteId(stages.get(0).getRoute_id());
                }
            }
        }
        if (!existsBotInStageOrStation(token, stationId, stageId, botKey, revenueShare)) {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.exists_bot")).append(",");
        }
    }

    /**
     * Kiem tra bot co ton tai o tram hoac doan nao khong
     *
     * @param token     Ma xac thuc
     * @param stationId Ma tram
     * @param stageId   Ma doan
     * @return
     */
    private boolean existsBotInStageOrStation(String token, String stationId, String stageId, String botKey,
                                              Map<String, List<Map<String, String>>> revenueShare) {
        boolean result = false;
        com.viettel.etc.xlibrary.core.entities.ResponseEntity responseEntity;
        StringBuilder botIds = new StringBuilder();
        if (revenueShare.containsKey(botKey)) {
            List<Map<String, String>> bots = revenueShare.get(botKey);
            if (bots != null && !bots.isEmpty()) {
                botIds.append(bots.get(0).get("botId"));
                for (int i = 1; i < bots.size(); i++) {
                    botIds.append(",").append(bots.get(i).get("botId"));
                }
                responseEntity = botService.existsBotInStageOrStation(token, botIds.toString(), stationId, stageId);
                if (responseEntity != null && responseEntity.getData() != null && (Boolean) responseEntity.getData()) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * So sanh ngay khong co gio phut giay
     *
     * @param date dau vao
     * @return true or false
     */

    public boolean compareDate(Date date) {
        Date dateNow = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (date.getTime() == calendar.getTimeInMillis() || date.getTime() > calendar.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    /**
     * Kiem tra gia ve co bang voi tong doanh thi chia se
     *
     * @param servicePlanCode Ma code bang cuoc gia ve
     * @param fee             gia ve
     * @param lstRevenueShare Danh sach doanh thu chia se
     * @return true hoac false kiem tra gia ve tong co bang gia ve trong file excel khong
     */
    public boolean checkFee(String servicePlanCode, String fee, Map<String, List<Map<String, String>>> lstRevenueShare, StringBuilder errorString) {
        boolean result = false;
        Long parseFee = Long.parseLong(fee);
        List<Map<String, String>> revenueShares = lstRevenueShare.get(servicePlanCode);
        Long tmpFee = 0L;
        if (revenueShares != null && !revenueShares.isEmpty()) {
            for (Map<String, String> revenueShare : revenueShares) {
                if (!FnCommon.isNullOrEmpty(revenueShare.get("revenueShare"))) {
                    Long feeInRevenueShare = hasNumber(revenueShare.get("revenueShare"));
                    if (feeInRevenueShare != null) {
                        if (feeInRevenueShare.compareTo(0L) < 0) {
                            errorString.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee.minus")).append(",");
                        } else {
                            tmpFee += feeInRevenueShare;
                        }
                    } else {
                        errorString.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee.format")).append(",");
                    }
                } else {
                    errorString.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee")).append(",");
                }
            }
        }
        if (parseFee.equals(tmpFee)) {
            result = true;
        }
        return result;
    }

    /**
     * Kiem tra da ton tai bang cuoc, gia ve nay chua
     *
     * @param servicePlanCode Ma code bang cuoc gia ve
     * @return true neu ton tai ban ghi hoac false neu khong ton tai ban ghi
     */
    @Override
    public List<ServicePlanEntity> findByServicePlanCodeAndStatus(String servicePlanCode, Long status) {
        return servicePlanServiceJPA.findByServicePlanCodeAndStatus(servicePlanCode, status);
    }

    /**
     * Kiem tra ma loai ve co dung khong
     *
     * @param inputValue Gia tri ma loai ve dau vao
     * @param elements   Danh sach loai gia ve
     * @return true or false
     */
    public boolean existsInServicePlanTypes(Long inputValue, List<ServicePlanTypeEntity> elements) {
        for (ServicePlanTypeEntity element : elements) {
            if (inputValue.equals(element.getServicePlanTypeId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tra ve file mau
     *
     * @param data du lieu noi dung file dang byte[]
     */
    public ResponseEntity<?> returnFileExcel(byte[] data, String fileName, StringBuilder hasError) throws UnsupportedEncodingException {
        if (fileName.endsWith(".xlsx")) {
            fileName = fileName.substring(0, fileName.length() - 5);
            fileName = FnCommon.replaceFileName(fileName) + "-result.xlsx";
        }
        if (fileName.endsWith(".xls")) {
            fileName = fileName.substring(0, fileName.length() - 4);
            fileName = FnCommon.replaceFileName(fileName) + "-result.xls";
        }
        ByteArrayResource resource = new ByteArrayResource(data);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Access-Control-Expose-Headers", "Content-Disposition");
        if (!FnCommon.isNullOrEmpty(hasError.toString())) {
            return ResponseEntity.badRequest().headers(header).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        }
        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Convert thanh danh sach ServicePlanEntity
     *
     * @param lstAddServicePlanDTO Danh sach bang cuoc gia ve import
     * @param customer             Ten nguoi dung
     * @return List<ServicePlanEntity>
     */
    private List<ServicePlanEntity> convertToListServicePlanEntity(List<AddServicePlanDTO> lstAddServicePlanDTO, String customer) {
        List<ServicePlanEntity> lstServicePlanEntities = new ArrayList<>();
        if (lstAddServicePlanDTO != null && !lstAddServicePlanDTO.isEmpty()) {
            lstAddServicePlanDTO = lstAddServicePlanDTO.stream().filter(distinctByKey(data -> data.getServicePlanTypeID())).collect(Collectors.toList());
        }
        for (AddServicePlanDTO addServicePlanDto : lstAddServicePlanDTO) {
            ServicePlanEntity servicePlanEntity = addServicePlanDto.convertToServicePlanEntity(customer);
            servicePlanEntity.setStatus(ServicePlanEntity.Status.PENDING.value);
            lstServicePlanEntities.add(servicePlanEntity);
        }
        return lstServicePlanEntities;
    }

    public <T> java.util.function.Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Import du lieu tu file excel
     * Luu doanh thu chia se
     *
     * @param data         Danh sach bang cuoc gia ve
     * @param revenueShare Doanh thu chia se
     * @param bots         danh sach bot
     */
    public void saveServicePlanAndRevenueShare(List<ServicePlanEntity> data, Map<String, List<Map<String, String>>> revenueShare, List<LinkedHashMap<?, ?>> bots) {
        for (ServicePlanEntity servicePlanEntity : data) {
            ServicePlanEntity result = servicePlanServiceJPA.save(servicePlanEntity);
            saveBotRevenueShare(result, bots, revenueShare);
        }
    }

    /**
     * Import du lieu tu file excel
     * <p>
     * Luu doanh thi cua cac bot
     *
     * @param servicePlanEntity Du lieu bang cuoc gia ve
     * @param bots              danh sach bot
     * @param revenueShares     gia tri doanh thu chia se
     */
    public void saveBotRevenueShare(ServicePlanEntity servicePlanEntity, List<LinkedHashMap<?, ?>> bots, Map<String, List<Map<String, String>>> revenueShares) {
        List<Map<String, String>> revenueShare = revenueShares.get(servicePlanEntity.getServicePlanCode());
        revenueShare.forEach(data -> {
            BotRevenueShareEntity botRevenueShareEntity = new BotRevenueShareEntity();
            for (LinkedHashMap<?, ?> bot : bots) {
                if (bot.get("name").equals(data.get("botName"))) {
                    botRevenueShareEntity.setBotId(Long.parseLong(bot.get("id").toString()));
                    break;
                }
            }
            botRevenueShareEntity.setBotName(data.get("botName"));
            botRevenueShareEntity.setBotRevenue(Double.parseDouble(data.get("revenueShare")));
            botRevenueShareEntity.setServicePlanId(servicePlanEntity.getServicePlanId());
            botRevenueShareServiceJPA.save(botRevenueShareEntity);
        });
    }

    /**
     * Kiem tra du lieu truong servicePlanCode(Ma gia ve)
     *
     * @param row                  Hang can kiem tra du lieu
     * @param servicePlanDTO       Entity
     * @param countServicePlanCode Map kiem tra trung ma gia ve
     * @param result               Chuoi thong bao loi
     * @param listError            Danh sach loi
     */
    public void validateServicePlanCode(Row row, AddServicePlanDTO servicePlanDTO, Map<String, Long> countServicePlanCode,
                                        StringBuilder result, List<String> listError) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(1)))) {
            String servicePlanCode = FnCommon.getStringValue(row.getCell(1)).trim();
            if (servicePlanCode.length() > 20) {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.code.max_length")).append(",");
            }
            servicePlanDTO.setServicePlanCode(servicePlanCode);
            List<ServicePlanEntity> check = findByServicePlanCodeAndStatus(servicePlanDTO.getServicePlanCode(), Constants.SERVICE_PLAN_STATUS.DELETED);
            if (check.isEmpty() && countServicePlanCode.get(servicePlanCode) != null && countServicePlanCode.get(servicePlanCode) == 0) {
                Long count = countServicePlanCode.get(servicePlanCode) + 1;
                countServicePlanCode.put(servicePlanCode, count);
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.exists")).append(",");
                listError.add(result.toString());
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.code")).append(",");
        }
    }

    /**
     * Kiem tra du lieu truong servicePlanTypeId
     *
     * @param row                       Hang can kiem tra du lieu
     * @param servicePlanDTO            Entity
     * @param servicePlanTypeEntityList Danh sach servicePlanType
     * @param result                    Chuoi ket qua loi
     */
    public void validateServicePlanTypeId(Row row, AddServicePlanDTO servicePlanDTO,
                                          List<ServicePlanTypeEntity> servicePlanTypeEntityList,
                                          StringBuilder result
    ) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(2)))) {
            String servicePlanTypeName = FnCommon.getStringValue((row.getCell(2))).trim();
            if (!FnCommon.isNullOrEmpty(FnCommon.getIdFromString(servicePlanTypeName))) {
                servicePlanDTO.setServicePlanTypeName(servicePlanTypeName);
                Long servicePlanTypeId = hasNumber(FnCommon.getIdFromString(servicePlanTypeName).trim());
                if (String.valueOf(servicePlanTypeId).length() > 18) {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.type.id.max_length")).append(",");
                }
                if (servicePlanTypeId != null) {
                    if (!existsInServicePlanTypes(servicePlanTypeId, servicePlanTypeEntityList)) {
                        result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.type.id")).append(",");
                    } else {
                        servicePlanDTO.setServicePlanTypeID(Long.parseLong(FnCommon.getIdFromString(servicePlanTypeName)));
                    }
                } else {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.type.has_number")).append(",");
                }
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.type.id")).append(",");
            }

        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.type")).append(",");
        }
    }

    /**
     * Kiem tra tram va doan
     *
     * @param row            Hanh thuc hien
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua
     * @param token          Ma xac thuc
     * @param revenueShare   Doanh thu chia se
     */
    public void validateStationAndStage(Row row, AddServicePlanDTO servicePlanDTO,
                                        StringBuilder result, String token, Map<String, List<Map<String, String>>> revenueShare) {
        if (FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(3)))
                && FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(4)))) {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.stage_station.empty")).append(",");
        } else if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(3)))
                && FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(4)))) {
            String station = FnCommon.getStringValue((row.getCell(3))).trim();
            String botKey = FnCommon.getStringValue(row.getCell(1)).trim();
            servicePlanDTO.setStationType(1L);
            handleStationOrStage(1, station, botKey, token, revenueShare, result, servicePlanDTO);
        } else if (FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(3)))
                && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(4)))) {
            String stage = FnCommon.getStringValue((row.getCell(4))).trim();
            String botKey = FnCommon.getStringValue(row.getCell(1)).trim();
            servicePlanDTO.setStationType(0L);
            handleStationOrStage(2, stage, botKey, token, revenueShare, result, servicePlanDTO);
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.stage_station")).append(",");
        }
    }

    /**
     * Kiem tra loai phuong tien tinh phi
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     */
    public void validateVehicleGroupId(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(5)))) {
            String vehicleGroupName = FnCommon.getStringValue((row.getCell(5))).trim();
            if (!FnCommon.isNullOrEmpty(FnCommon.getIdFromString(vehicleGroupName))) {
                Long vehicleGroupId = Long.parseLong(FnCommon.getIdFromString(vehicleGroupName));
                if (String.valueOf(vehicleGroupId).length() > 10) {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.vehicle_group_id.max_length")).append(",");
                }
                servicePlanDTO.setVehicleGroupName(vehicleGroupName);
                servicePlanDTO.setVehicleGroupId(vehicleGroupId);
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.vehicle_group.id")).append(",");
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.vehicle_group_id")).append(",");
        }
    }

    /**
     * Kiem tra gia ve
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     * @param revenueShare   Doanh thu chia se
     */
    public void validateFee(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result, Map<String, List<Map<String, String>>> revenueShare) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(6)))) {
            Long fee = hasNumber(FnCommon.getStringValue(row.getCell(6)).trim());
            if (fee != null) {
                if (String.valueOf(fee).length() > 18) {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee.max_length")).append(",");
                }
                if (revenueShare.containsKey(FnCommon.getStringValue(row.getCell(1)).trim())) {
                    if (checkFee(FnCommon.getStringValue(row.getCell(1)).trim(), FnCommon.getStringValue(row.getCell(6)).trim(), revenueShare, result)) {
                        servicePlanDTO.setFee(fee);
                    } else {
                        result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee.compare")).append(",");
                    }
                } else {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.revenue_share.fee")).append(",");
                }
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee.format")).append(",");
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee")).append(",");
        }
    }

    /**
     * Kiem tra ocs_code
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     */
    public void validateOcsCode(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(7)))) {
            String ocsCode = FnCommon.getStringValue(row.getCell(7)).trim();
            if (ocsCode.length() <= 50) {
                servicePlanDTO.setOcsCode(ocsCode);
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.ocs_code.length")).append(",");
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.ocs_code")).append(",");
        }
    }

    /**
     * Kiem tra ngay hieu luc
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     */
    public void validateStartDate(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(8)))) {
            java.sql.Date startDate = FnCommon.convertStringToDate(FnCommon.getStringValue(row.getCell(8)).trim(), "dd/MM/yyyy");
            if (startDate != null) {
                servicePlanDTO.setStartDate(startDate);
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.date.format")).append(",");
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.start_date")).append(",");
        }
    }

    /**
     * Kiem tra ngay het hieu luc
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     */
    public void validateEndDate(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(9)))) {
            java.sql.Date endDate = FnCommon.convertStringToDate(FnCommon.getStringValue(row.getCell(9)).trim(), "dd/MM/yyyy");
            Date startDate = !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(8)).trim())?FnCommon.convertStringToDate(FnCommon.getStringValue(row.getCell(8)).trim(), "dd/MM/yyyy"):null;
            if (endDate == null || startDate==null) {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.end_date.format")).append(",");
            } else {
                if (endDate.compareTo(startDate) >= 0) {
                    servicePlanDTO.setEndDate(endDate);
                } else {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.end_date.compare.start_date")).append(",");
                }
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.end_date")).append(",");
        }
    }

    /**
     * Kiem tra gia han va ngay gia han
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     */
    public void validateAutoRenew(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(10)))
        ) {
            String autoRenew = Objects.requireNonNull(FnCommon.getStringValue(row.getCell(10))).trim();
            if ("Có".equals(autoRenew)) {
                servicePlanDTO.setAutoRenew(1L);
                validateUseDay(row, servicePlanDTO, result);
            } else {
                servicePlanDTO.setAutoRenew(0L);
            }
        } else {
            servicePlanDTO.setAutoRenew(0L);
        }
    }

    /**
     * Kiem tra chu ki gia han
     *
     * @param row            Hang thuc hien kiem tra du lieu
     * @param servicePlanDTO Entity
     * @param result         Chuoi ket qua loi
     */
    public void validateUseDay(Row row, AddServicePlanDTO servicePlanDTO, StringBuilder result) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(11)))) {
            Long useDay = hasNumber(Objects.requireNonNull(FnCommon.getStringValue(row.getCell(11))).trim());
            if (useDay != null) {
                if (String.valueOf(useDay).length() <= 10) {
                    servicePlanDTO.setUseDay(useDay);
                } else {
                    result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.use_day.max_length")).append(",");
                }
            } else {
                result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.use_day.format"));
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("validate.service.plan.use_day"));
        }
    }

    /**
     * Kiem tra ki tu nhap vao co phai la so hay khong
     *
     * @param inputValue Du lieu dau vao
     * @return Du lieu kieu Long
     */
    public Long hasNumber(String inputValue) {
        try {
            if (inputValue.matches("^[0-9]*$")) {
                return Long.parseLong(inputValue);
            }
            return null;
        } catch (NumberFormatException ex) {
            LOGGER.error("Du lieu dau vao khong phai kieu so ", ex);
            return null;
        }
    }
}
