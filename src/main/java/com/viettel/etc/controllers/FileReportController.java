package com.viettel.etc.controllers;

import com.viettel.etc.dto.FileReportDTO;
import com.viettel.etc.dto.VehicleDTO;
import com.viettel.etc.services.DocxService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class FileReportController {

    @Autowired
    DocxService docxService;

    @PostMapping(value = "/report/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReportFiles(@AuthenticationPrincipal Authentication authentication,
                                               @RequestBody VehicleDTO params) {
        String fileName = docxService.createFileTemplate(params);
        Map<String, String> map = new HashMap<>();
        map.put("fileName", fileName);
        return new ResponseEntity<>(FnCommon.responseToClient(map), HttpStatus.OK);
    }

    @PostMapping(value = "/report/unsigned", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReportFiles(@AuthenticationPrincipal Authentication authentication,
                                            @RequestBody FileReportDTO params) throws IOException {
        ByteArrayResource file = docxService.getFileTemplate(params);
        FileReportDTO result = new FileReportDTO();
        if (file == null) {
            throw new EtcException(ErrorApp.ERR_DATA);
        } else {
            String encodedString = Base64.getEncoder().encodeToString(file.getByteArray());
            result.setFileName(params.getFileName() + ".pdf");
            result.setBase64Data(encodedString);
            return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/report/signed", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getSignedReportFiles(@AuthenticationPrincipal Authentication authentication,
                                                  FileReportDTO params,
                                                  @RequestPart(value = "file", required = false) MultipartFile photo) throws IOException {

        ByteArrayResource file = docxService.getFileSigned(photo.getBytes(), params);
        FileReportDTO result = new FileReportDTO();
        if (file == null) {
            throw new EtcException(ErrorApp.ERR_DATA);
        } else {
            String fileName = params.getFileName();
            String encodedString = Base64.getEncoder().encodeToString(file.getByteArray());
            result.setFileName(fileName + "_signed.pdf");
            result.setBase64Data(encodedString);
            return new ResponseEntity<>(FnCommon.responseToClient(result), HttpStatus.OK);
        }
    }
}
