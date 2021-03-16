package com.viettel.etc.controllers;

import com.viettel.etc.dto.DocumentDTO;
import com.viettel.etc.services.DocumentService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autogen class: Lop lay danh sach ve chung tu
 *
 * @author ToolGen
 * @date Fri Jul 03 10:13:49 ICT 2020
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    /**
     * Lay danh sach ve cac loai chung tu theo loai tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "/document-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDocumentType(@AuthenticationPrincipal Authentication authentication,
                                                  DocumentDTO dataParams) {

        Object resultObj = documentService.getDocumentType(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Lay danh loai giay to theo loai khach hang
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "/document-types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDocumentByCustTypeId(@AuthenticationPrincipal Authentication authentication,
                                                          DocumentDTO dataParams, @PathVariable("id") Long custTypeId) {

        Object resultObj = documentService.getDocumentByCustTypeId(dataParams, custTypeId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }


    /**
     * Lay danh sach ve cac loai chung tu theo loai tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = Constants.MOBILE + "/document-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDocumentTypes(@AuthenticationPrincipal Authentication authentication,
                                                   DocumentDTO dataParams) {

        Object resultObj = documentService.getDocumentTypes(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
