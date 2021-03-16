package com.viettel.etc.controllers;

import com.viettel.etc.dto.mobile.MobileAutoVerUpdateDTO;
import com.viettel.etc.dto.mobile.MobileInsertLogDTO;
import com.viettel.etc.dto.mobile.ReqDataCheckVersionMobile;
import com.viettel.etc.dto.mobile.ReqDownloadVersionNew;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.MobileAutoUpdateVerService;
import com.viettel.etc.services.MobileInsertLogService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
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

import java.io.IOException;

@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class VersionMobileController {

    @Autowired
    private MobileAutoUpdateVerService mobileAutoUpdateVerService;

    @Autowired
    private MobileInsertLogService mobileInsertLogService;

    @Autowired
    private FileService fileService;

    /***
     * Ghi log cac phien ban thay doi
     * @param mobileInsertLogDTO
     * @param authentication
     * @return
     */
    @PostMapping(Constants.MOBILE + "/insert-log-mobile")
    public ResponseEntity<?> saveInstallLog(@RequestBody MobileInsertLogDTO mobileInsertLogDTO, @AuthenticationPrincipal Authentication authentication) {
        return new ResponseEntity<>(FunctionCommon.responseToClient(mobileInsertLogService.saveMobileLog(mobileInsertLogDTO, authentication)), HttpStatus.OK);
    }

    /***
     * Tao version moi
     * @param jsonString
     * @param authentication
     * @return
     */
    @PostMapping(value = Constants.MOBILE + "/auto-update-ver-mobile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> autoUpdateVerMobile(@RequestParam MultipartFile fileImport, @RequestParam String jsonString, @AuthenticationPrincipal Authentication authentication) throws IOException {
        return new ResponseEntity<>(FunctionCommon.responseToClient(mobileAutoUpdateVerService.createVersionMobile(fileImport, jsonString, authentication)), HttpStatus.OK);
    }

    /***
     * Tim kiem cac version theo thiet bi
     * @param mobileAutoVerUpdateDTO
     * @param authentication
     * @return
     */
    @PostMapping(Constants.MOBILE + "/search-ver-mobile")
    public ResponseEntity<?> searchVerMobile(@RequestBody MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO, @AuthenticationPrincipal Authentication authentication) {
        return new ResponseEntity<>(FunctionCommon.responseToClient(mobileAutoUpdateVerService.searchVerMobile(mobileAutoVerUpdateDTO, authentication)), HttpStatus.OK);
    }

    /***
     * Lay phien ban moi nhat
     * @param reqDataCheckVersionMobile
     * @return
     */
    @GetMapping(Constants.MOBILE + "/get-version-current")
    public ResponseEntity<?> getVersionCurrent(ReqDataCheckVersionMobile reqDataCheckVersionMobile) {
        return new ResponseEntity<>(FunctionCommon.responseToClient(mobileAutoUpdateVerService.getVersionCurrent(reqDataCheckVersionMobile)), HttpStatus.OK);
    }

    /***
     * downdload file apk theo version moi nhat android
     * @param reqDownloadVersionNew
     * @return
     */
    @GetMapping(Constants.MOBILE + "/download-version-mobile-new")
    public ResponseEntity<?> downloadVersionMobileNew(ReqDownloadVersionNew reqDownloadVersionNew){
        byte[] data = fileService.getFile(reqDownloadVersionNew.getVersionName() +"-"+reqDownloadVersionNew.getUrl());
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + reqDownloadVersionNew.getUrl())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .contentLength(data.length)
                .body(resource);
    }
}
