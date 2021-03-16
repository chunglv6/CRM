package com.viettel.etc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.exports.ConfigFileExport;
import com.viettel.etc.utils.exports.ConfigHeaderExport;
import com.viettel.etc.utils.exports.ConfigSubheaderExport;
import com.viettel.etc.utils.exports.ConfigSubtitleExport;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.MessEntity;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Phuong thuc chung cho toan bo project
 */
@Log4j
public class FnCommon extends FunctionCommon {

    private static final Logger LOGGER = Logger.getLogger(FnCommon.class);
    private static final String formatDate = "yyyy/MM/dd HH:mm:ss";

    /**
     * Convert class to json string
     *
     * @param object
     * @return
     */
    public static String toStringJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("FnCommon toStringJson error: ", e);
            throw new EtcException("common.error.convert.json");
        }
    }

    /**
     * Trim string
     *
     * @param s string
     * @return
     */
    public static String trim(String s) {
        return s == null ? null : s.trim();
    }

    public static boolean equal(Object o1, Object o2) {
        if (isNullObject(o1)) {
            return isNullObject(o2);
        } else {
            return o1.equals(o2);
        }
    }

    /**
     * Convert date date to string date
     *
     * @param date
     * @param isFullDateTime:true: full date time, false: date sort
     * @return
     */
    public static String convertDateToString(Date date, Boolean isFullDateTime) {
        String strDate;
        if (date == null) {
            return "";
        }
        if (isFullDateTime) {
            strDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
        } else {
            strDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
        }
        return strDate;
    }

    /**
     * Go bo dau tieng viet
     *
     * @param s
     * @return
     */
    public static String removeAccent(String s) {
        if (s == null) {
            return "";
        }
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace("đ", "d").replace("Đ", "D");
    }

    /**
     * tra du lieu ve client
     *
     * @param errorApp
     * @param itemObject
     * @return
     */
    public static Object responseToClient(ErrorApp errorApp, Object itemObject) {
        ResponseEntity responseEntity = new ResponseEntity();
        if (itemObject != null) {
            responseEntity.setData(itemObject);
        }
        MessEntity itemEntity = new MessEntity();
        itemEntity.setCode(errorApp.getCode());
        itemEntity.setDescription(errorApp.getDescription());
        responseEntity.setMess(itemEntity);
        return responseEntity;
    }

    public static Object responseToClient(int code, String description) {
        ResponseEntity responseEntity = new ResponseEntity();
        MessEntity itemEntity = new MessEntity();
        itemEntity.setCode(code);
        itemEntity.setDescription(description);
        responseEntity.setMess(itemEntity);
        return responseEntity;
    }

    /**
     * tra ve client ma loi va du lieu theo ma loi
     *
     * @param etcException
     * @param itemObject
     * @return
     */
    public static Object responseToClient(EtcException etcException, Object itemObject) {
        return responseToClient(etcException.getErrorApp(), itemObject);
    }

    /**
     * tra ve client thong bao ma loi
     *
     * @param etcException
     * @return
     */
    public static Object responseToClient(EtcException etcException) {
        return responseToClient(etcException.getErrorApp(), null);
    }

    /**
     * lay thong tin nguoi dung
     *
     * @param authentication
     * @return
     */
    public static String getUserLogin(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            return principal.getKeycloakSecurityContext().getToken().getPreferredUsername().toUpperCase();
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }

    /**
     * lay chuoi string token
     *
     * @param authentication
     * @return
     */
    public static String getStringToken(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            return principal.getKeycloakSecurityContext().getTokenString();
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }

    public static String getIdUserLogin(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            return principal.getKeycloakSecurityContext().getToken().getSubject();
        } catch (Exception e) {
            LOGGER.error("Loi! getIdUserLogin: ", e);
            return null;
        }
    }

    /**
     * kiem tra truong hop value == null return ""
     *
     * @param obj
     * @return
     */
    public static String retNull(Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * kiem tra truong hop value == null return 0l
     *
     * @param obj
     * @return
     */
    public static Long retlong(Object obj) {
        return obj == null ? 0L : Long.parseLong(obj.toString());
    }

    /**
     * kiem tra truong hop value == null return null
     *
     * @param obj
     * @return
     */
    public static Long checkLong(Object obj) {
        return obj == null ? null : Long.valueOf(obj.toString());
    }

    /**
     * Convert String => list String by : ","
     *
     * @param stringArr
     * @return
     */
    public static List<String> convertStringToList(String stringArr) {
        if (stringArr != null && !"".equals(stringArr)) {
            return Arrays.asList(stringArr.split(","));
        }
        return new ArrayList<>();
    }

    public static boolean isNullOrEmpty(String toTest) {

        return toTest == null || toTest.isEmpty();
    }

    public static boolean isNullOrBlank(String toTest) {
        return StringUtils.isBlank(toTest);
    }

    public static boolean isNullOrBlank(Cell toTest) {
        return toTest == null || toTest.getCellType().equals(CellType.BLANK);
    }

    /**
     * kiem tra ngay truyen vao param 1 co lon hon ngay cua param 2 hay khong
     *
     * @param effDate
     * @param expDate
     * @return
     */
    public static void checkDateIsAfter(Date effDate, Date expDate) throws EtcException {
        if (Objects.isNull(expDate)) {
            return;
        }
        if (Objects.isNull(effDate) || effDate.getTime() >= expDate.getTime()) {
            throw new EtcException("common.invalid.expire-date");
        }
    }

    /**
     * Su dung: download file, tra ve MediaType
     *
     * @param servletContext
     * @param fileName
     * @return
     */
    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
        String mineType = servletContext.getMimeType(fileName);
        try {
            return MediaType.parseMediaType(mineType);
        } catch (Exception e) {
            LOGGER.error("Has ERROR ", e);
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static boolean checkFileExtensionValid(String fileName, String... fileExtensions) {
        Objects.requireNonNull(fileName);
        for (String fileExtension : fileExtensions) {
            if (fileName.toLowerCase().endsWith(fileExtension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkBriefcaseValid(String fileName, byte[] file, Integer maxFileSizeMb) {
        if (Objects.isNull(maxFileSizeMb)) {
            maxFileSizeMb = 15;
        }
        Objects.requireNonNull(file);
        long fileSizeMb = file.length / (1024 * 1024);
        return checkFileExtensionValid(fileName, ".JPG", ".PNG", ".TIFF", ".BMP", ".PDF", ".JPEG") && fileSizeMb <= maxFileSizeMb;
    }

    public static void setOkHtppClient(OkHttpClient client) {
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
    }

    /**
     * Gui request den server categories
     *
     * @param url
     * @param params
     * @param token
     * @return
     */
    public static String doGetRequest(String url, Map<String, String> params, String token) {
        String strRes = null;
        OkHttpClient client = new OkHttpClient();
        try {
            setOkHtppClient(client);
            HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();

            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    httpBuider.addQueryParameter(param.getKey(), param.getValue());
                }
            }
            Request request = null;
            if (token != null) {
                request = new Request.Builder()
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .url(httpBuider.build())
                        .get()
                        .build();
            } else {
                request = new Request.Builder()
                        .url(httpBuider.build())
                        .get()
                        .build();
            }
            Response response = client.newCall(request).execute();
            strRes = response.body().string();

            return strRes.replace("null", "\"\"");
        } catch (Exception e) {
            LOGGER.error("Has error", e);
        }
        return strRes;
    }

    /**
     * Gui request den server keycloak
     *
     * @param url
     * @param token
     * @param requestBody
     * @return
     */
    public static Response doPutRequest(String url, String token, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient();
        try {
            setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            Request request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .url(httpBuilder.build())
                    .put(requestBody)
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            LOGGER.error("Has error", e);
        }
        return null;
    }

    public static String doPostRequest(String url, String token, Object obj) {
        OkHttpClient client = new OkHttpClient();
        try {
            setOkHtppClient(client);
            HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
            RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(obj));
            Request request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .url(httpBuider.build())
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            LOGGER.error("Has error", e);
        }
        return null;
    }

    /**
     * Tao va set style cho column cua excel
     *
     * @param wb
     */
    public static CellStyle createAndSetStyleCell(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.GREEN.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return cellStyle;
    }

    /**
     * Convert date date to string date
     *
     * @param date
     * @return
     */
    public static String convertDateToString(java.util.Date date) {
        String strDate;
        if (date == null) {
            return "";
        }
        strDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        return strDate;
    }

    /**
     * Lấy thông tin attribute của user trong token
     *
     * @param authentication
     * @return
     */
    public static Map<String, Object> getAttribute(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            AccessToken token = principal.getKeycloakSecurityContext().getToken();
            return token.getOtherClaims();
        } catch (Exception e) {
            LOGGER.error("Loi! getAttribute: ", e);
            return null;
        }
    }

    /**
     * lay role nguoi dung
     *
     * @param authentication
     * @return
     */
    public static Set<String> getRoleId(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            Set<String> roleId = principal.getKeycloakSecurityContext().getToken().getResourceAccess().get(FnCommon.getClientId(authentication)).getRoles();
            return roleId;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }

    public static Set<String> getRoleIdCrm(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            Set<String> roleId = principal.getKeycloakSecurityContext().getToken().getResourceAccess().get("crm").getRoles();
            return roleId;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }

    /**
     * Validate ten file
     *
     * @param fileName
     */
    public static boolean validateFileName(String fileName) {
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");

    }

    /**
     * Validate tieu de
     *
     * @param sheet
     */
    public static boolean validateHeaderCell(Sheet sheet, int indexRow, int cellNumber, String headerString) {
        boolean isAccepted = false;
        if (sheet != null) {
            Row row = sheet.getRow(indexRow);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cellNumber; i++) {
                builder.append(row.getCell(i).getStringCellValue().trim());
            }
            if (headerString.equals(builder.toString())) {
                isAccepted = true;
            }
        }
        return isAccepted;
    }

    /**
     * Set style for header
     */

    public static void setFontHeaderCell(Workbook wb, CellStyle cellStyle) {
        Font fontHeader = wb.createFont();
        fontHeader.setBold(true);
        fontHeader.setCharSet(FontCharset.VIETNAMESE.getNativeId());
        cellStyle.setFont(fontHeader);
    }

    //region kiem tra obj co null khong

    /**
     * Lay gia tri String cua cell
     *
     * @param cell
     */
    public static String getStringValue(Cell cell) {
        try {
            if (cell == null) {
                return "";
            } else if (CellType.BLANK == cell.getCellType()) {
                return "";
            } else if (CellType.BOOLEAN == cell.getCellType()) {
                return cell.getBooleanCellValue() + "";
            } else if (CellType.ERROR == cell.getCellType()) {
                return null;
            } else if (CellType.FORMULA == cell.getCellType()) {
                return cell.getCellFormula();
            } else if (CellType.NUMERIC == cell.getCellType()) {
                return String.format("%.0f", cell.getNumericCellValue());
            } else if (CellType.STRING == cell.getCellType()) {
                return cell.getStringCellValue();
            }
        } catch (Exception e) {
            LOGGER.error("Error when cast value to String", e);
        }
        return "";
    }

    /**
     * kiem tra obj co null khong
     *
     * @param obj
     * @return
     */
    public static boolean isNullObject(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return isNullOrEmpty(obj.toString());
        }
        return false;
    }

    /**
     * kiem tra null hoac rong
     *
     * @param s
     * @return
     */
    public static boolean isNullOrEmpty(CharSequence s) {
        int strLengt;
        if (s == null || (strLengt = s.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLengt; i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * kiem tra null hoac rong
     *
     * @param collection
     * @return
     */
    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * kiem tra null hoac rong
     *
     * @param objects
     * @return
     */
    public static boolean isNullOrEmpty(final Object[] objects) {
        return objects == null || objects.length == 0;
    }
    //endregion

    /**
     * kiem tra null hoac rong
     *
     * @param map
     * @return
     */
    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    //region xuat excel
    public static byte[] exportExcel(List<ConfigFileExport> config) {
        try {
            Workbook workbook = new XSSFWorkbook();

            //region Declare style
            Font xSSFFont = workbook.createFont();
            xSSFFont.setFontName(HSSFFont.FONT_ARIAL);
            xSSFFont.setFontHeightInPoints((short) 20);
            xSSFFont.setBold(true);
            xSSFFont.setColor(IndexedColors.BLACK.index);

            CellStyle cellStyleTitle = workbook.createCellStyle();
            cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
            cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleTitle.setFillForegroundColor(IndexedColors.WHITE.index);
            cellStyleTitle.setFont(xSSFFont);

            Font xSSFFontHeader = workbook.createFont();
            xSSFFontHeader.setFontName(HSSFFont.FONT_ARIAL);
            xSSFFontHeader.setFontHeightInPoints((short) 10);
            xSSFFontHeader.setColor(IndexedColors.BLACK.index);
            xSSFFontHeader.setBold(true);

            Font subTitleFont = workbook.createFont();
            subTitleFont.setFontName(HSSFFont.FONT_ARIAL);
            subTitleFont.setFontHeightInPoints((short) 10);
            subTitleFont.setColor(IndexedColors.BLACK.index);

            Font subHeaderFont = workbook.createFont();
            subHeaderFont.setFontName(HSSFFont.FONT_ARIAL);
            subHeaderFont.setFontHeightInPoints((short) 10);
            subHeaderFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFont = workbook.createFont();
            rowDataFont.setFontName(HSSFFont.FONT_ARIAL);
            rowDataFont.setFontHeightInPoints((short) 10);
            rowDataFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFontBold = workbook.createFont();
            rowDataFontBold.setFontName(HSSFFont.FONT_ARIAL);
            rowDataFontBold.setFontHeightInPoints((short) 10);
            rowDataFontBold.setColor(IndexedColors.BLACK.index);
            rowDataFontBold.setBold(true);

            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
            cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleHeader.setBorderLeft(BorderStyle.THIN);
            cellStyleHeader.setBorderBottom(BorderStyle.THIN);
            cellStyleHeader.setBorderRight(BorderStyle.THIN);
            cellStyleHeader.setBorderTop(BorderStyle.THIN);
            cellStyleHeader.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
            cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleHeader.setWrapText(true);
            cellStyleHeader.setFont(xSSFFontHeader);

            CellStyle cellStyleLeft = workbook.createCellStyle();
            cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeft.setBorderLeft(BorderStyle.THIN);
            cellStyleLeft.setBorderBottom(BorderStyle.THIN);
            cellStyleLeft.setBorderRight(BorderStyle.THIN);
            cellStyleLeft.setBorderTop(BorderStyle.THIN);
            cellStyleLeft.setWrapText(true);
            cellStyleLeft.setFont(rowDataFont);

            CellStyle cellStyleLeftBold = workbook.createCellStyle();
            cellStyleLeftBold.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeftBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeftBold.setBorderLeft(BorderStyle.THIN);
            cellStyleLeftBold.setBorderBottom(BorderStyle.THIN);
            cellStyleLeftBold.setBorderRight(BorderStyle.THIN);
            cellStyleLeftBold.setBorderTop(BorderStyle.THIN);
            cellStyleLeftBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleLeftBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleLeftBold.setWrapText(true);
            cellStyleLeftBold.setFont(rowDataFontBold);

            CellStyle cellStyleRight = workbook.createCellStyle();
            cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRight.setBorderLeft(BorderStyle.THIN);
            cellStyleRight.setBorderBottom(BorderStyle.THIN);
            cellStyleRight.setBorderRight(BorderStyle.THIN);
            cellStyleRight.setBorderTop(BorderStyle.THIN);
            cellStyleRight.setWrapText(true);
            cellStyleRight.setFont(rowDataFont);

            CellStyle cellStyleRightBold = workbook.createCellStyle();
            cellStyleRightBold.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRightBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRightBold.setBorderLeft(BorderStyle.THIN);
            cellStyleRightBold.setBorderBottom(BorderStyle.THIN);
            cellStyleRightBold.setBorderRight(BorderStyle.THIN);
            cellStyleRightBold.setBorderTop(BorderStyle.THIN);
            cellStyleRightBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleRightBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleRightBold.setWrapText(true);
            cellStyleRightBold.setFont(rowDataFontBold);

            CellStyle cellStyleCenter = workbook.createCellStyle();
            cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenter.setBorderLeft(BorderStyle.THIN);
            cellStyleCenter.setBorderBottom(BorderStyle.THIN);
            cellStyleCenter.setBorderRight(BorderStyle.THIN);
            cellStyleCenter.setBorderTop(BorderStyle.THIN);
            cellStyleCenter.setWrapText(true);
            cellStyleCenter.setFont(rowDataFont);

            CellStyle cellStyleCenterBold = workbook.createCellStyle();
            cellStyleCenterBold.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenterBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenterBold.setBorderLeft(BorderStyle.THIN);
            cellStyleCenterBold.setBorderBottom(BorderStyle.THIN);
            cellStyleCenterBold.setBorderRight(BorderStyle.THIN);
            cellStyleCenterBold.setBorderTop(BorderStyle.THIN);
            cellStyleCenterBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleCenterBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleCenterBold.setWrapText(true);
            cellStyleCenterBold.setFont(rowDataFontBold);
            //endregion

            for (ConfigFileExport item : config) {
                Sheet sheet = workbook.createSheet(item.getSheetName());

                //region Build title
                // Title
                Row rowMainTitle = sheet.createRow(item.getCellTitleIndex());
                Cell mainCellTitle = rowMainTitle.createCell(0);
                mainCellTitle.setCellValue(item.getTitle() == null ? "" : item.getTitle());
                mainCellTitle.setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(
                        new CellRangeAddress(item.getCellTitleIndex(), item.getCellTitleIndex(), 0,
                                item.getMergeTitleEndIndex()));
                // Sub title
                if (item.getSuptitle() != null && !item.getSuptitle().isEmpty()) {
                    for (ConfigSubtitleExport suptitle : item.getSuptitle()) {
                        int indexSubTitle = suptitle.getIndexRow();
                        Row rowSubTitle = sheet.createRow(indexSubTitle);
                        Cell cellTitle = rowSubTitle.createCell(suptitle.getIndexCell());
                        cellTitle.setCellValue(suptitle.getSubtitleName());
                        CellStyle cellStyleSubTitle = workbook.createCellStyle();
                        if ("CENTER".equals(suptitle.getAlign())) {
                            cellStyleSubTitle.setAlignment(HorizontalAlignment.CENTER);
                        }
                        if ("LEFT".equals(suptitle.getAlign())) {
                            cellStyleSubTitle.setAlignment(HorizontalAlignment.LEFT);
                        }
                        if ("RIGHT".equals(suptitle.getAlign())) {
                            cellStyleSubTitle.setAlignment(HorizontalAlignment.RIGHT);
                        }
                        cellStyleSubTitle.setVerticalAlignment(VerticalAlignment.CENTER);
                        cellStyleSubTitle.setFont(subTitleFont);
                        cellTitle.setCellStyle(cellStyleSubTitle);
                        sheet.addMergedRegion(
                                new CellRangeAddress(indexSubTitle, indexSubTitle, suptitle.getIndexCell(),
                                        suptitle.getMergeTitleEndIndex()));
                    }
                }
                //endregion

                //region Build header
                int totalRowSupheader = 0;
                if (item.isCreatHeader()) {
                    int index = -1;
                    Cell cellHeader;
                    Row rowHeader = sheet.createRow(item.getStartRow());
                    rowHeader.setHeight((short) 500);
                    // Header
                    for (ConfigHeaderExport header : item.getHeader()) {
                        cellHeader = rowHeader.createCell(index + 1);
                        cellHeader.setCellValue(header.getHeaderName());
                        cellHeader.setCellStyle(cellStyleHeader);
                        sheet.setColumnWidth(index + 1, header.getWidth());
                        index++;
                    }
                    // Sub header
                    if (item.getSupheader() != null && !item.getSupheader().isEmpty()) {
                        Map<Integer, Integer> rowMap = new HashMap<>();
                        for (ConfigSubheaderExport supheader : item.getSupheader()) {
                            int indexSubHeader = supheader.getIndexRow();
                            rowMap.put(indexSubHeader, indexSubHeader);
                            Row rowSubHeader = sheet.getRow(indexSubHeader);
                            CellStyle cellStyleSubHeader = workbook.createCellStyle();
                            cellStyleSubHeader.setVerticalAlignment(VerticalAlignment.CENTER);
                            cellStyleSubHeader.setFillForegroundColor(IndexedColors.AQUA.index);
                            cellStyleSubHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cellStyleSubHeader.setFont(subHeaderFont);
                            if (rowSubHeader == null) {
                                rowSubHeader = sheet.createRow(indexSubHeader);
                                for (int i = 0; i < item.getHeader().size(); i++) {
                                    rowSubHeader.createCell(i).setCellStyle(cellStyleSubHeader);
                                }
                            }
                            Cell cellSubHeader = rowSubHeader.createCell(supheader.getIndexCell());
                            cellSubHeader.setCellValue(supheader.getSubheaderName());
                            if ("CENTER".equals(supheader.getAlign())) {
                                cellStyleSubHeader.setAlignment(HorizontalAlignment.CENTER);
                            }
                            if ("LEFT".equals(supheader.getAlign())) {
                                cellStyleSubHeader.setAlignment(HorizontalAlignment.LEFT);
                            }
                            if ("RIGHT".equals(supheader.getAlign())) {
                                cellStyleSubHeader.setAlignment(HorizontalAlignment.RIGHT);
                            }
                            cellSubHeader.setCellStyle(cellStyleSubHeader);
                            if (supheader.getMergeHeaderEndIndex() != 0) {
                                sheet.addMergedRegion(
                                        new CellRangeAddress(indexSubHeader, indexSubHeader, supheader.getIndexCell(),
                                                supheader.getMergeHeaderEndIndex()));
                            }
                        }
                        totalRowSupheader = rowMap.size();
                    }
                }
                //endregion

                //region Fill data
                if (item.getLstData() != null && !item.getLstData().isEmpty()) {
                    //init mapColumn
                    Object firstRow = item.getLstData().get(0);
                    Map<String, Field> mapField = new HashMap<>();
                    for (ConfigHeaderExport header : item.getHeader()) {
                        for (Field f : firstRow.getClass().getDeclaredFields()) {
                            f.setAccessible(true);
                            if (f.getName().equals(header.getFieldName())) {
                                mapField.put(header.getFieldName(), f);
                            }
                        }
                        if (firstRow.getClass().getSuperclass() != null) {
                            for (Field f : firstRow.getClass()
                                    .getSuperclass().getDeclaredFields()) {
                                f.setAccessible(true);
                                if (f.getName().equals(header.getFieldName())) {
                                    mapField.put(header.getFieldName(), f);
                                }
                            }
                        }
                    }
                    //fillData
                    Row row;
                    List lstData = item.getLstData();
                    List<ConfigHeaderExport> lstHeader = item.getHeader();
                    int startRow = item.getStartRow();
                    Boolean isRowBold;
                    for (int i = 0; i < lstData.size(); i++) {
                        row = sheet.createRow(i + startRow + 1 + totalRowSupheader);
                        row.setHeight((short) 250);
                        Cell cell;
                        int j = 0;
                        isRowBold = false;
                        for (int e = 0; e < lstHeader.size(); e++) {
                            ConfigHeaderExport head = lstHeader.get(e);
                            String header = head.getFieldName();
                            String align = head.getAlign();
                            Object obj = lstData.get(i);
                            Field f = mapField.get(header);
                            String value = "";
                            if (f != null) {
                                Object tempValue = f.get(obj);
                                if (tempValue instanceof java.util.Date) {
                                    if ("DATE".equals(head.getStyleFormat())) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        value = tempValue == null ? "" : simpleDateFormat.format((java.util.Date) tempValue);
                                    } else if ("DATETIME".equals(head.getStyleFormat())) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        value = tempValue == null ? "" : simpleDateFormat.format((java.util.Date) tempValue);
                                    } else {
                                        value = tempValue == null ? "" : tempValue.toString();
                                    }
                                } else {
                                    value = tempValue == null ? "" : tempValue.toString();
                                }
                            }
                            cell = row.createCell(j);
                            if ("NUMBER".equals(head.getStyleFormat())) {
                                if (!"".equals(value.trim())) {
                                    cell.setCellValue(Double.valueOf(value));
                                } else {
                                    cell.setCellValue(value);
                                }
                                if (e == 0 && item.isAutoGenNo()) {
                                    cell.setCellValue(i + 1);
                                }
                            } else if ("CURRENCY".equals(head.getStyleFormat())) {
                                if (!"".equals(value.trim())) {
                                    cell.setCellValue(formatVNDCurrency(new BigDecimal(value)));
                                } else {
                                    cell.setCellValue(value);
                                }
                            } else {
                                if (value.length() > 32767) {
                                    cell.setCellValue(value.substring(0, 32766));
                                } else {
                                    cell.setCellValue(value);
                                }
                                if (e == 0 && "".equals(value.trim())) {
                                    isRowBold = true;
                                }
                            }
                            if ("CENTER".equals(align)) {
                                if (isRowBold) {
                                    cell.setCellStyle(cellStyleCenterBold);
                                } else {
                                    cell.setCellStyle(cellStyleCenter);
                                }
                            }
                            if ("LEFT".equals(align)) {
                                if (isRowBold) {
                                    cell.setCellStyle(cellStyleLeftBold);
                                } else {
                                    cell.setCellStyle(cellStyleLeft);
                                }
                            }
                            if ("RIGHT".equals(align)) {
                                if (isRowBold) {
                                    cell.setCellStyle(cellStyleRightBold);
                                } else {
                                    cell.setCellStyle(cellStyleRight);
                                }
                            }
                            j++;
                        }
                    }
                }
                //endregion
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            return bytes;
        } catch (Exception e) {
            LOGGER.error("Loi! exportExcel: ", e);
            return null;
        }
    }
    //endregion

    //region Dinh dang tien te

    /**
     * Create file name
     *
     * @param originalFilename
     * @param date
     * @return
     */
    public static String createFileName(String originalFilename, java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return originalFilename + "_" + calendar.get(Calendar.HOUR_OF_DAY) + ""
                + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND);
    }

    /**
     * Dinh dang tien te
     *
     * @param currency
     * @return
     */
    public static String formatVNDCurrency(BigDecimal currency) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        if (currency == null) {
            currency = new BigDecimal(0);
        }
        return formatter.format(currency);
    }

    /**
     * Dinh dang tien te
     *
     * @param currency
     * @return
     */
    public static String formatVNDCurrency(Double currency) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        if (currency == null) {
            currency = new Double(0);
        }
        return formatter.format(currency);
    }
    //endregion

    /**
     * Dinh dang tien te
     *
     * @param currency
     * @return
     */
    public static String formatVNDCurrency(Long currency) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        if (currency == null) {
            currency = new Long(0);
        }
        return formatter.format(currency);
    }

    //region kiem tra date
    public static boolean isDate(String date, String pattern) {
        if (isNullOrEmpty(date)) {
            return false;
        }

        try {
            DateFormat df = new SimpleDateFormat(pattern);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    //endregion

    public static boolean isAfterDay(String dateStr1, String dateStr2, String pattern) {
        String patternDefault = isNullOrEmpty(pattern) ? Constants.COMMON_DATE_FORMAT : pattern;
        DateFormat df = new SimpleDateFormat(patternDefault);
        if (isNullOrEmpty(dateStr1) || isNullOrEmpty(dateStr2)) {
            return false;
        }
        try {
            java.util.Date date1 = df.parse(dateStr1);
            java.util.Date date2 = df.parse(dateStr2);
            return date1.after(date2);
        } catch (ParseException e) {
            return false;
        }
    }
    //endregion

    //region check max lenght
    public static boolean isMaxLengthTooLarge(Long value, int maxlength) {
        return Math.log10(value) > maxlength;
    }

    /**
     * lay thong tin nguoi dung
     *
     * @param authentication
     * @return
     */
    public static String getClientId(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            String clientId = principal.getKeycloakSecurityContext().getToken().getIssuedFor();
            return clientId;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }

    /**
     * Kiem tra parameter truyen vao dang object co truong nao null hay khong
     *
     * @param data
     * @return
     * @throws IllegalAccessException
     */
    public static String hasFieldNull(Object data) throws IllegalAccessException {
        StringBuilder result = new StringBuilder();
        for (Field f : data.getClass().getFields()) {
            f.setAccessible(true);
            if (f.get(data) == null)
                result.append(f.getName()).append(" bat buoc khac null;");
        }
        return result.toString();
    }

    /**
     * Kiem tra params truyen vao phuong thuc get bang model co hop le hay khong
     *
     * @param fieldErrors Danh sach cac truong khong dung
     * @return Chuoi loi
     */

    public static String handleParamsBindingException(List<FieldError> fieldErrors) {
        StringBuilder result = new StringBuilder();
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            for (FieldError fieldError : fieldErrors) {
                result.append("Truong ").append(fieldError.getField()).append(" khong dung kieu du lieu;");
            }
        }
        return result.toString();
    }

    /**
     * Convert date date to string date
     *
     * @param date
     * @param isFullDateTime:true: full date time, false: date sort
     * @return
     */
    public static String convertDateToString(Date date, Boolean isFullDateTime, String prefix) {
        String strDate;
        if (date == null) {
            return "";
        }
        if (isFullDateTime) {
            strDate = new SimpleDateFormat("dd" + prefix + "MM" + prefix + "yyyy HH:mm:ss").format(date);
        } else {
            strDate = new SimpleDateFormat("dd" + prefix + "MM" + prefix + "yyyy").format(date);
        }
        return strDate;
    }

    /**
     * Them ngay thang cho ten file
     *
     * @param fileName ten file goc
     * @return Ten file moi
     */
    public static String replaceFileName(String fileName) {
        if (!FnCommon.isNullOrEmpty(fileName)) {
            long date = new Date(System.currentTimeMillis()).getTime();
            return fileName + "_" + date;
        }
        return "";
    }

    /**
     * Convert date date to string date
     *
     * @param date
     * @return
     */
    public static String convertDate(java.util.Date date) {
        String strDate;
        if (date == null) {
            return "";
        }
        strDate = new SimpleDateFormat("YYYYddMMHHmmss").format(date);
        return strDate;
    }

    /**
     * Lay gia tri id cua danh muc
     *
     * @param source
     * @return
     */
    public static String getIdFromString(String source) {
        String result = "";
        if (!FnCommon.isNullOrEmpty(source)) {
            String[] arrResult = source.split("-");
            if (arrResult.length > 1) {
                result = arrResult[arrResult.length - 1];
            }
        }
        return result;
    }

    /**
     * Lay gia tri date dinh dang yyyyMMddHHmmssSSS[000000-999999]
     *
     * @param date
     * @return
     */
    public static String formatDateTime(java.util.Date date) {
        SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        if (date == null) {
            return "";
        } else {
            return DATE_TIME_FORMAT.format(date).concat(getRandomNumberString());
        }
    }

    /**
     * Lay random [000000-999999]
     * It will generate 6 digit random Number from 0 to 999999
     * and convert any number sequence into 6 character.
     *
     * @param
     * @return
     */
    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    public static String convertDateToStringOther(java.util.Date date, String format) {
        String strDate;
        if (date == null) {
            return "";
        }
        strDate = new SimpleDateFormat(format).format(date);
        return strDate;
    }

    /**
     * Convert string date to java.sql.date
     *
     * @param strDate Chuoi string date
     * @param format  Dinh dang mong muon
     * @return
     */
    public static Date convertStringToDate(String strDate, String format) {
        if (!FnCommon.isNullOrEmpty(strDate)) {
            SimpleDateFormat sdFormat = new SimpleDateFormat(format);
            sdFormat.setLenient(false);
            try {
                java.util.Date date = sdFormat.parse(strDate);
                return new Date(date.getTime());
            } catch (ParseException e) {
                LOGGER.error("Convert string to date fail", e);
                return null;
            }
        }
        return null;
    }

    /**
     * So sanh ngay khong co gio phut giay
     *
     * @param date dau vao
     * @return true or false
     */
    public static boolean compareDate(java.util.Date date) {
        java.util.Date dateNow = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return date.getTime() == calendar.getTimeInMillis() || date.getTime() > calendar.getTimeInMillis();
    }

    /**
     * Format dinh dang ngay thang OCS
     *
     * @param date
     * @return
     */
    public static String formatOCSDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        return sdf.format(date);
    }

    public static byte[] zipFiles(Map<String, byte[]> listFile) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (Map.Entry<String, byte[]> file : listFile.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(file.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(file.getValue());
            }
        } catch (IOException ex) {
            log.info(ex);
        }
        return fos.toByteArray();
    }

    /**
     * Kiem tra parameter truyen vao dang object co truong nao null hay khong va truong do phai khac truong ngoai le
     *
     * @param data
     * @return
     * @throws IllegalAccessException
     */
    public static String hasExceptionField(Object data) throws IllegalAccessException {
        StringBuilder result = new StringBuilder();
        for (Field f : data.getClass().getFields()) {
            f.setAccessible(true);
            if (Objects.isNull(f.get(data)) && !Constants.VIETTEL_PAY_EXCEPTION_FIELD.EXCEPTION_FIELD.contains(f.getName()))
                result.append(f.getName()).append(" bat buoc khac null;");
        }
        return result.toString();
    }

    /**
     * Lam tron gia tri kieu double
     *
     * @param value
     * @return
     */
    public static Double round(Double value) {
        DecimalFormat newFormat = new DecimalFormat("#.##");
        return Double.valueOf(newFormat.format(value));
    }

    /**
     * validate do lon cua gia tri
     *
     * @param value
     * @param maxlength
     * @return
     */
    public static boolean validateMaxlengthDouble(Double value, int maxlength) {
        return Math.log10(value) > maxlength;
    }

    /**
     * Validate so dong trong file excel
     *
     * @param row
     * @return
     */
    public static boolean validateMaxRow(int row) {
        return (row > 1000);
    }

    /**
     * Kiem tra do dai chuoi
     *
     * @param string
     * @param maxlength
     * @return
     */
    public static boolean validateMaxlengthString(String string, int maxlength) {
        return (string.length() > maxlength);
    }

    /***
     * Format plate BOO1
     */

    public static String formatPlateBOO1(String plate) {
        if (!isNullOrEmpty(plate)) {
            Pattern p = Pattern.compile("(?<=\\D)[\\d]\\w+");
            Matcher m = p.matcher(plate);
            String plateCheck = "";

            if (m.find()) {
                plateCheck = m.group();
            }
            if (plateCheck.length() == 4 && StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                return plate;
            } else if (plateCheck.length() >= 5) {
                if (plate.endsWith("T") || plate.endsWith("X") || plate.endsWith("V")) {
                    return plate.substring(0, plate.length() - 1);
                }
            }
        }
        return plate;
    }

    public static String formatStringBOO2ToBOO1(String dateInput) throws ParseException {
        SimpleDateFormat formatBOO1 = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT_BOO);
        SimpleDateFormat formatBOO2 = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
        if (!FnCommon.isNullOrEmpty(dateInput)) {
            return formatBOO1.format(formatBOO2.parse(dateInput));
        }
        return "";
    }

    /***
     * Format plate BOO1
     * T Trang
     * X xanh
     * V vang
     */

    public static String getPlateTypeBOO1(String plate) {
        if (!isNullOrEmpty(plate)) {
            Pattern p = Pattern.compile("(?<=\\D)[\\d]\\w+");
            Matcher m = p.matcher(plate);
            String plateCheck = "";
            if (m.find()) {
                plateCheck = m.group();
            }
            if (plateCheck.length() == 4 && StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                return "1";
            } else if (plateCheck.length() > 5) {
                String plateType = plate.substring(plate.length() - 1);
                switch (plateType) {
                    case "T":
                        return "1";
                    case "X":
                        return "2";
                    case "V":
                        return "6";
                }
            }
        }
        return "1";
    }

    /***
     * Validate start-date end-date mua ve thang quy BOO1
     */
    public static boolean validateDateChargeTicket(long methodCharge, String ticketType, String startDate, String
            endDate, String formatDate) {
        Date start = FnCommon.convertStringToDate(startDate, formatDate);
        Date end = FnCommon.convertStringToDate(endDate, formatDate);
        if (start == null || end == null) {
            return false;
        }

        if (methodCharge == 1L) {
            // tinh thuong
            if (getDayOfMonth(start) != 1) {
                // khong phai ngay dau thang
                return false;
            } else {
                int diffMonth = monthsBetween(start, end);
                if (diffMonth == 0 && "T".equals(ticketType)) {
                    return getDayOfMonth(end) == getMaxDayOfMonth(end);
                } else if ("Q".equals(ticketType)) {
                    return checkQuarter(start, end, startDate, endDate);
                }
            }
        } else if (methodCharge == 2L) {
            // tinh block
            long diff = end.getTime() - start.getTime();
            long numberOfDay = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return (numberOfDay == 29 && "T".equals(ticketType)) || (numberOfDay == 89 && "Q".equals(ticketType));
        }
        return false;
    }


    public static boolean checkQuarter(Date start, Date end, String startDate, String endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(end);

        if (cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR) && end.getTime() > System.currentTimeMillis()) {
            String s1 = startDate.substring(4);
            String s2 = endDate.substring(4);
            return "0101".equals(s1) && "0331".equals(s2) || "0401".equals(s1) && "0630".equals(s2) || "0701".equals(s1) && "0930".equals(s2) || "1001".equals(s1) && "1231".equals(s2);
        }
        return false;
    }

    /***
     * Get number of day by date
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /***
     * Get max of day by month
     * @param date
     * @return
     */
    public static int getMaxDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /***
     * diff month between date
     * @param s1
     * @param s2
     * @return
     */
    private static int monthsBetween(Date s1, Date s2) {
        Calendar d1 = Calendar.getInstance();
        d1.setTime(s1);
        Calendar d2 = Calendar.getInstance();
        d2.setTime(s2);
        return (d2.get(Calendar.YEAR) - d1.get(Calendar.YEAR)) * 12 + d2.get(Calendar.MONTH) - d1.get(Calendar.MONTH);
    }

    /***
     * Convert ticketType BOO1 to BOO2
     * @param ticketType
     * @return
     */
    public static String convertTicketType(String ticketType) {
        switch (ticketType) {
            case "L":
                return "1";
            case "T":
                return "4";
            case "Q":
                return "5";
            case "N":
                return "6";
            default:
                return "";
        }
    }

    /***
     * Convert ticketType BOO1 to BOO2
     * @param servicePlanTypeId
     * @return
     */
    public static String convertToTicketType(String servicePlanTypeId) {
        switch (servicePlanTypeId) {
            case "1":
                return "L";
            case "4":
                return "T";
            case "5":
                return "Q";
            case "6":
                return "N";
            default:
                return "";
        }
    }

    /**
     * Kiem tra ocs code
     *
     * @param response du lieu phan hoi
     * @return boolean
     */
    public static boolean checkOcsCode(OCSResponse response) {
        return response != null && response.getResultCode() != null && "0".equals(response.getResultCode());
    }

    public static String convertStationType(Long stationType) {
        if (stationType == 1) {
            return "O";
        }
        if (stationType == 0) {
            return "C";
        }
        return "";
    }

    /***
     * Validate end of plateNumber
     */

    public static boolean validatePlateContainsTVX(String plate) {
        Pattern p = Pattern.compile("(?<=\\D)[\\d]\\w+");
        Matcher m = p.matcher(plate);
        String plateCheck = "";
        boolean result = false;
        if (m.find()) {
            plateCheck = m.group();
        }
        if (!FnCommon.isNullOrEmpty(plateCheck)) {
            boolean isSuffix = (plate.endsWith("T") || plate.endsWith("X") || plate.endsWith("V"));
            if (plateCheck.length() == 4 && StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                result = true;
            } else if (plateCheck.length() == 5 && !StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                return true;
            } else if (plateCheck.length() == 5 && StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                /***
                 * Đối với biển số dãy số có 5 chữ số nếu là biển trắng thêm chữ T, biển Xanh thêm (X) vào cuối
                 * trừ các biển có 02 chữ cái sau mã tỉnh ví dụ: LD, NN, LB, KT, NG
                 */
                p = Pattern.compile("(?<=\\d)\\D{2}");
                m = p.matcher(plate);
                if (m.find()) {
                    plateCheck = m.group();
                }
                if (plateCheck.length() == 2) {
                    return !isSuffix;
                } else {
                    return false;
                }

            } else if (plateCheck.length() > 5) {
                /***
                 * Đối với biển số dãy số có 5 chữ số nếu là biển trắng thêm chữ T, biển Xanh thêm (X) vào cuối
                 * trừ các biển có 02 chữ cái sau mã tỉnh ví dụ: LD, NN, LB, KT, NG
                 */
                p = Pattern.compile("(?<=\\d)\\D{2}");
                m = p.matcher(plate);
                if (m.find()) {
                    plateCheck = m.group();
                }
                if (plateCheck.length() == 2) {
                    return false;
                } else {
                    return isSuffix;
                }
            }
        }
        return result;
    }

    /***
     * Check bien so co phai 5so 2chu khong
     * @param plate
     * @return
     */
    public static boolean checkPlate5Number2Char(String plate) {
        Pattern p = Pattern.compile("(?<=\\d)\\D{2}");
        Matcher m = p.matcher(plate);
        String plateCheck = "";
        if (m.find()) {
            plateCheck = m.group();
        }

        return plateCheck.length() == 2;
    }

    /***
     * Validate end of plateNumber
     */

    public static String getPlateNumberBoo1(String plate, String plateType) {
        Pattern p = Pattern.compile("(?<=\\D)[\\d]\\w+");
        Matcher m = p.matcher(plate);
        String plateCheck = "";
        if (m.find()) {
            plateCheck = m.group();
        }
        if (!FnCommon.isNullOrEmpty(plateCheck)) {
            if (plateCheck.length() == 4 && StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                return plate;
            } else if (plateCheck.length() == 5 && !StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                // neu 4 so va so cuoi la chu thi cat di
                return plate.substring(0, plate.length() - 1);
            } else if (plateCheck.length() >= 5 && StringUtils.isNumeric(plate.substring(plate.length() - 1))) {
                /***
                 * Đối với biển số dãy số có 5 chữ số nếu là biển trắng thêm chữ T, biển Xanh thêm (X) vào cuối
                 * trừ các biển có 02 chữ cái sau mã tỉnh ví dụ: LD, NN, LB, KT, NG
                 */
                p = Pattern.compile("(?<=\\d)\\D{2}");
                m = p.matcher(plate);
                if (m.find()) {
                    plateCheck = m.group();
                }
                if (plateCheck.length() == 2) {
                    return plate;
                } else {
                    return plate + plateType;
                }

            }
        }
        return plate + plateType;
    }

    /***
     * Gen check_sum using HMAC_SHA1_ALGORITHM
     * @param data
     * @param key
     * @return
     */
    public static String calculateRFC2104HMAC(String data, String key) {

        String result = "";
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), Constants.HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = new String(Base64.encodeBase64(rawHmac));
        } catch (Exception e) {
            log.error("Failed to generate HMAC : ", e);
        }
        return result;
    }

    public static void responseFile(HttpServletResponse response, String fileName) throws IOException {
        File fileToDownload = new File(System.getProperty("user.dir") + File.separator + fileName);
        try (InputStream inputStream = new FileInputStream(fileToDownload)) {
            response.setContentType("application/force-download");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    public static void responseFile(HttpServletResponse response, byte[] file, String fileName) throws IOException {
        response.setContentType("application/force-download");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.getOutputStream().write(file);
        response.flushBuffer();
    }


    public static String getPlateTypeCodeFromPlateNumber(String plateNumber) {
        if (!isNullOrEmpty(plateNumber)) {
            if (plateNumber.endsWith("T")) {
                return "1";
            } else if (plateNumber.endsWith("X")) {
                return "2";
            } else if (plateNumber.endsWith("V")) {
                return "6";
            }
        }
        return "1";
    }

    public static String mappingPlateTypeBOO2ToBOO1(String plateTypeCode) {
        if (!isNullOrEmpty(plateTypeCode)) {
            switch (plateTypeCode) {
                case "1":
                    return "T";
                case "2":
                    return "X";
                case "6":
                    return "V";
            }
        }
        return "";
    }

    public static String mappingPlateTypeBOO1ToBOO2(String plateType) {
        if (!isNullOrEmpty(plateType)) {
            switch (plateType) {
                case "T":
                    return "1";
                case "X":
                    return "2";
                case "V":
                    return "6";
            }
        }
        return null;
    }

    /**
     * Mapping du lieu loai phuong tien ngoai le
     *
     * @param inputType
     * @return
     */
    public static String mappingExceptionListType(String inputType) {
        switch (inputType) {
            case "LX":
            case "MGX":
                return Constants.EXCEPTION_LIST_TYPE.LX;
            case "GV":
            case "MGV":
                return Constants.EXCEPTION_LIST_TYPE.GV;
            case "WL":
                return Constants.EXCEPTION_LIST_TYPE.WL;
            case "BL":
                return Constants.EXCEPTION_LIST_TYPE.BL;
            default:
                return null;
        }
    }

    /**
     * @param strDate
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     * @throws ParseException
     */
    public static java.util.Date setTimeOfDate(String strDate, int hours, int minutes, int seconds) {
        if (!FnCommon.isNullOrEmpty(strDate)) {
            try {
                java.util.Date timeOfDate = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT).parse(strDate);
                timeOfDate.setHours(hours);
                timeOfDate.setMinutes(minutes);
                timeOfDate.setSeconds(seconds);
                return timeOfDate;
            } catch (ParseException e) {
                LOGGER.error("Convert string to date fail", e);
                return null;
            }
        }
        return null;

    }

    public static java.util.Date setTimeOfDate(java.util.Date date, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public static String urlDecodeString(String input) {
        try {
            return URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("urlDecode ", e);
        }
        return input;
    }

    public static String urlEncodeString(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("urlDecode ", e);
        }
        return input;
    }

    public static String urlDecodeCheckSumString(String input) {
        try {
            return URLDecoder.decode(input, StandardCharsets.UTF_8.name()).replaceAll(" ", "+");
        } catch (UnsupportedEncodingException e) {
            log.error("urlDecode ", e);
        }
        return input;
    }

    /***
     * validate stringDate by format dd/MM/yyyy HH:mm:ss
     * @param strDate
     * @return
     */
    public static boolean validateCommonFormatDate(String strDate) {
        String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(strDate).matches();
    }

    /**
     * Lay khoang cach giua 2 ngay
     *
     * @param sysDate
     * @param effDate
     * @return
     */
    public static long diffDays(Long sysDate, Long effDate) {
        Long i = Math.abs(sysDate - effDate);
        return TimeUnit.DAYS.convert(i, TimeUnit.MILLISECONDS);
    }

    /**
     * Cong them so ngay config
     *
     * @param date
     * @param days
     * @return
     */
    public static java.util.Date addDays(java.util.Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new java.util.Date(c.getTimeInMillis());
    }

    public static java.util.Date round(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static java.util.Date addYears(java.util.Date date, int years) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, years);
        return new java.util.Date(c.getTimeInMillis());
    }

    public static java.sql.Date addYearsSql(java.sql.Date date, int years) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, years);
        return new Date(c.getTimeInMillis());
    }


    /**
     * Tra ve file ket qua
     *
     * @param data du lieu noi dung file dang byte[]
     */
    public static org.springframework.http.ResponseEntity<?> returnFileExcel(byte[] data, String fileName) {
        return returnFileExcel(data, fileName, null, null);
    }

    public static org.springframework.http.ResponseEntity<?> returnFileExcel(byte[] data, String fileName, Long dataFailed, Long dataSuccess) {
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
        if (dataFailed != null && dataSuccess != null) {
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + "; Failed-lines=" + dataFailed.toString() + "; Success-lines=" + dataSuccess.toString());
        } else {
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        }
        header.add("Access-Control-Expose-Headers", "Content-Disposition");
        return org.springframework.http.ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public static boolean rowIsEmpty(Row row) {
        if (row == null || row.getLastCellNum() <= 0) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public static String md5Encoder(@NotNull String inputString) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(inputString.getBytes());
            return bytesToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }
}
