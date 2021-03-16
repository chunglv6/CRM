package com.viettel.etc.controllers;

import com.viettel.etc.dto.ApproveVehicleExceptionDTO;
import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.ExceptionListReqDTO;
import com.viettel.etc.dto.VehiclesExceptionDTO;
import com.viettel.etc.repositories.tables.entities.AttachmentFileEntity;
import com.viettel.etc.services.VehiclesExceptionService;
import com.viettel.etc.services.impl.FileServiceImpl;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Autogen class: Phuong tien ngoai le, uu tien, cam
 *
 * @author DongNV
 * @since Mon Jul 20 18:48:46 ICT 2020
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1 + "/vehicles-exception")
public class VehiclesExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehiclesExceptionController.class);

    @Autowired
    private VehiclesExceptionService vehiclesExceptionService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private FileServiceImpl fileService;

    /**
     * API xuat file Excel
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PostMapping(value = "/export-excel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportVehiclesExceptionExcel(@AuthenticationPrincipal Authentication authentication,
                                                          @RequestBody VehiclesExceptionDTO dataParams) throws UnsupportedEncodingException {
        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.exportVehiclesExceptionExcel(dataParams, authentication);
        ErrorApp errorApp = (ErrorApp) objectMap.get("error");
        Object resultObj = objectMap.get("data");
        if (resultObj == null) {
            resultObj = new byte[0];
        }

        ByteArrayResource byteArrayResource = new ByteArrayResource((byte[]) resultObj);
        HttpHeaders headers = new HttpHeaders();
        List<String> customHeaders = new ArrayList<>();
        customHeaders.add("Content-Disposition");
        customHeaders.add("Content-Response");
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Content-Disposition", "attachment;filename=" + FnCommon.createFileName("DanhSachPhuongTienNgoaiLe", new Date()) + ".xlsx");
        headers.set("Content-Response", URLEncoder.encode(errorApp.toStringJson(), "UTF-8").replace("+", "%20"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(byteArrayResource);
    }

    /**
     * API xuat file PDF
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PostMapping(value = "/export-pdf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> exportVehiclesExceptionPDF(@AuthenticationPrincipal Authentication authentication,
                                                             @RequestBody VehiclesExceptionDTO dataParams) throws UnsupportedEncodingException {
        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.exportVehiclesExceptionPDF(dataParams, authentication);
        ErrorApp errorApp = (ErrorApp) objectMap.get("error");
        Object resultObj = objectMap.get("data");
        if (resultObj == null) {
            resultObj = new byte[0];
        }

        ByteArrayResource byteArrayResource = new ByteArrayResource((byte[]) resultObj);
        HttpHeaders headers = new HttpHeaders();
        List<String> customHeaders = new ArrayList<>();
        customHeaders.add("Content-Disposition");
        customHeaders.add("Content-Response");
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Content-Disposition", "attachment;filename=" + FnCommon.createFileName("DanhSachPhuongTienNgoaiLe", new Date()) + ".pdf");
        headers.set("Content-Response", URLEncoder.encode(errorApp.toStringJson(), "UTF-8").replace("+", "%20"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(byteArrayResource);
    }


    /**
     * API tim kiem phuong tien ngoai le/ uu tien & cam
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDataVehiclesException(@AuthenticationPrincipal Authentication authentication,
                                                           VehiclesExceptionDTO dataParams) {
        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.getDataVehiclesException(dataParams, authentication);
        ErrorApp errorApp = (ErrorApp) objectMap.get("error");
        return new ResponseEntity<>(FnCommon.responseToClient(errorApp, objectMap.get("data")), HttpStatus.OK);
    }

    /**
     * Tim kiem phuong tien ngoai le/ uu tien & cam
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "/results", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getVehiclesException(@AuthenticationPrincipal Authentication authentication,
                                                       VehiclesExceptionDTO dataParams) {

        Object resultObj = vehiclesExceptionService.getVehiclesException(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * API lay thong tin chi tiet xe ngoai le
     *
     * @param authentication: thong tin nguoi dung
     * @param exceptionListId params client
     * @return ResponseEntity<Object>
     */
    @GetMapping(value = "/{exceptionListId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewDetail(@AuthenticationPrincipal Authentication authentication,
                                             @PathVariable Long exceptionListId) {
        Object resultObj = vehiclesExceptionService.viewDetail(exceptionListId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Update status (Trinh duyet xe ngoai le)
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams:     params client
     * @return
     */
    @PutMapping(value = "/pre-approval", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postUpdateStatus(@AuthenticationPrincipal Authentication authentication,
                                                   @Valid @RequestBody VehiclesExceptionDTO dataParams) throws IOException, JSONException {

        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.postUpdateStatus(authentication, dataParams);
        return new ResponseEntity<>(FnCommon.responseToClient((ErrorApp) objectMap.get("error"), objectMap.get("data")), HttpStatus.OK);
    }

    /**
     * duyet xe ngoai le
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams:     params client
     * @return
     */
    @PutMapping(value = "/approval", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postApproval(@AuthenticationPrincipal Authentication authentication,
                                               @RequestBody VehiclesExceptionDTO dataParams) throws Exception {

        ApproveVehicleExceptionDTO dataResult = vehiclesExceptionService.postApproval(dataParams, authentication);
        return new ResponseEntity<>( dataResult, HttpStatus.OK);
    }

    /**
     * Them moi phuong tien ( ngoai le/uu tien/ cam)
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams:     params client
     * @return
     */
    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addExceptionList(@AuthenticationPrincipal Authentication authentication,
                                                   @Valid @RequestBody ExceptionListReqDTO dataParams) {

        vehiclesExceptionService.addExceptionList(authentication, dataParams);
        return new ResponseEntity<>(FnCommon.responseToClient(ErrorApp.SUCCESS), HttpStatus.OK);
    }

    /**
     * Them moi phuong tien ( ngoai le/uu tien/ cam) tu file
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExceptionList(@AuthenticationPrincipal Authentication authentication,
                                                 @RequestPart(name = "file") MultipartFile excelFile) throws IOException {
        return vehiclesExceptionService.importExceptionList(authentication, excelFile);
    }

    /**
     * download file template
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @PostMapping(value = "/import-template", produces = MediaType.APPLICATION_JSON_VALUE)
    public void importTemplateExceptionList(@AuthenticationPrincipal Authentication authentication,
                                            HttpServletResponse httpServletResponse) throws IOException {
        ByteArrayResource result = vehiclesExceptionService.importTemplateExceptionList(authentication);
        FnCommon.responseFile(httpServletResponse, result.getByteArray(), "template_ngoaile_uutien_vipham.xlsx");
    }

    /**
     * Them moi phuong tien ( ngoai le/uu tien/ cam)
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams:     params client
     * @return
     */
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addVehiclesExceptionSingle(@AuthenticationPrincipal Authentication authentication,
                                                             @Valid @RequestPart(value = "dataParams") VehiclesExceptionDTO dataParams,
                                                             @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.addVehiclesExceptionSingle(authentication, dataParams, files);
        return new ResponseEntity<>(FnCommon.responseToClient((ErrorApp) objectMap.get("error"), objectMap.get("data")), HttpStatus.OK);
    }


    /**
     * Them moi phuong tien ( ngoai le/uu tien/ cam) bang file excel
     *
     * @param authentication: thong tin nguoi dung
     * @param excelFile:      params client
     * @param files:          params client
     * @return
     */
    @PostMapping(value = "/import-vehicles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> postCreateVehiclesExceptionFile(@AuthenticationPrincipal Authentication authentication,
                                                                  @Valid @RequestPart(value = "dataParams") VehiclesExceptionDTO dataParams,
                                                                  @RequestPart(name = "excelFile") MultipartFile excelFile,
                                                                  @RequestPart(name = "files") List<MultipartFile> files) throws UnsupportedEncodingException {

        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.postCreateVehiclesExceptionFile(authentication, dataParams, excelFile, files);

        ErrorApp errorApp = (ErrorApp) objectMap.get("error");
        Object resultObj = objectMap.get("data");
        Object resultCount = objectMap.get("count");

        ByteArrayResource byteArrayResource = new ByteArrayResource(resultObj != null ? (byte[]) resultObj : new byte[0]);
        String count = resultCount != null ? resultCount.toString() : "0/0";

        String fileName = excelFile.getOriginalFilename();
        if (fileName.endsWith(".xlsx")) {
            fileName = fileName.substring(0, fileName.length() - 5);
            fileName = FnCommon.replaceFileName(fileName) + "-result.xlsx";
        } else if (fileName.endsWith(".xls")) {
            fileName = fileName.substring(0, fileName.length() - 4);
            fileName = FnCommon.replaceFileName(fileName) + "-result.xls";
        }

        List<String> customHeaders = new ArrayList<>();
        customHeaders.add("Content-Disposition");
        customHeaders.add("Content-Response");
        customHeaders.add("Content-Count");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Content-Disposition", "attachment;filename=" + fileName);
        headers.set("Content-Response", URLEncoder.encode(errorApp.toStringJson(), "UTF-8").replace("+", "%20"));
        headers.set("Content-Count", count);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);
    }

    /**
     * Xoa phuong tien ( ngoai le/uu tien/ cam) bang file excel
     *
     * @param authentication: thong tin nguoi dung
     * @param id:             params client
     * @return
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteVehiclesException(@AuthenticationPrincipal Authentication authentication,
                                                          @PathVariable() Long id) {

        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.deleteVehiclesException(authentication, id);
        return new ResponseEntity<>(FnCommon.responseToClient((ErrorApp) objectMap.get("error"), objectMap.get("data")), HttpStatus.OK);
    }


    /**
     * Sua doi phuong tien ( ngoai le/uu tien/ cam)
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams:     params client
     * @return
     */
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateVehiclesException(@AuthenticationPrincipal Authentication authentication,
                                                          @Valid @RequestPart(value = "dataParams") VehiclesExceptionDTO dataParams,
                                                          @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Map<String, Object> objectMap = (Map<String, Object>) vehiclesExceptionService.updateVehiclesException(authentication, dataParams, files);
        return new ResponseEntity<>(FnCommon.responseToClient((ErrorApp) objectMap.get("error"), objectMap.get("data")), HttpStatus.OK);
    }

    /**
     * Sua doi phuong tien ( ngoai le/uu tien/ cam)
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams:     params client
     * @return
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateException(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable("id") Long id,
                                                  @RequestBody VehiclesExceptionDTO dataParams) throws Exception {

        Object result = vehiclesExceptionService.updateException(authentication, dataParams, id);
        return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
    }

    /**
     * Tai file dinh kem ( ngoai le/uu tien/ cam)
     *
     * @param authentication:   thong tin nguoi dung
     * @param attachmentFileId: params client
     * @return ResponseEntity<Object>
     */
    @PostMapping(value = "/download-attachment-file/{attachmentFileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> downloadAttachmentFile(@AuthenticationPrincipal Authentication authentication,
                                                    @PathVariable() Long attachmentFileId) {
        String error = "";
        AttachmentFileDTO fileDTO = vehiclesExceptionService.downloadAttachmentFile(attachmentFileId);
        if (FnCommon.isNullObject(fileDTO)) {
            error = "crm.vehicles.exception.attachmentFile.not.found";
        }

        Path path = Paths.get(fileDTO.getDocumentPath());
        MediaType mediaType = FnCommon.getMediaTypeForFileName(this.servletContext, path.getFileName().toString());

        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            LOGGER.error("Has ERROR", e);
            error = "crm.vehicles.exception.attachmentFile.not.found";
        }
        ByteArrayResource resource = new ByteArrayResource(data);

        List<String> customHeaders = new ArrayList<>();
        customHeaders.add("Content-Disposition");
        customHeaders.add("Content-Error");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Access-Control-Expose-Headers", "Content-Disposition");
        headers.set("Content-Disposition", "attachment;filename=" + path.getFileName().toString());
        headers.set("Content-Error", error);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(resource);
    }

    /**
     * Tai files dinh kem
     *
     * @param authentication:  thong tin nguoi dung
     * @param exceptionListId: params client
     * @return ResponseEntity<Object>
     */
    @PostMapping(value = "/download-attachment-files/{exceptionListId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> downloadAttachmentFiles(@AuthenticationPrincipal Authentication authentication,
                                                     @PathVariable Long exceptionListId) {
        String error = "";
        List<AttachmentFileEntity> fileList = vehiclesExceptionService.getAttachmentFiles(exceptionListId, Constants.ATTACHMENT_EXCEPTION);
        if (fileList.size() == 0) {
            error = "crm.vehicles.exception.attachmentFile.not.found";
        }
        Map<String, byte[]> listFile = new HashMap<>();
        for (AttachmentFileEntity fileEntity : fileList) {
            listFile.put(fileEntity.getDocumentName(), fileService.getFile(fileEntity.getDocumentPath()));
        }
        String zipName = "ExceptionList" + exceptionListId + "[" + new Date().getTime() + "]" + ".zip";
        MediaType mediaType = FnCommon.getMediaTypeForFileName(this.servletContext, zipName);
        byte[] data = FnCommon.zipFiles(listFile);
        ByteArrayResource resource = new ByteArrayResource(data);
        List<String> customHeaders = new ArrayList<>();
        customHeaders.add("Content-Disposition");
        customHeaders.add("Content-Error");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Access-Control-Expose-Headers", "Content-Disposition");
        headers.set("Content-Disposition", "attachment;filename=" + zipName);
        headers.set("Content-Type", "application/zip");
        headers.set("Content-Error", error);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(resource);
    }
}
