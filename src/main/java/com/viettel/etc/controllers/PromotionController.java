package com.viettel.etc.controllers;

import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.services.PromotionService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * Autogen class: Lop quan ly chuong trinh khuyen mai/ chiet khau
 *
 * @author ToolGen
 * @date Fri Sep 04 15:40:32 ICT 2020
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1 + "/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;

    /**
     * Lay danh sach chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPromotions(@AuthenticationPrincipal Authentication authentication,
                                                PromotionDTO dataParams) {

        Object resultObj = promotionService.getPromotions(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xem chi tiet chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param promotionId     Id ctrinh khuyen mai
     * @return
     */
    @GetMapping(value = "/{promotionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPromotionDetail(@AuthenticationPrincipal Authentication authentication,
                                                     @PathVariable Long promotionId) {

        Object resultObj = promotionService.getDetailPromotion(promotionId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Them chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addPromotion(@AuthenticationPrincipal Authentication authentication,
                                               @Valid @RequestPart(value = "dataParams") PromotionDTO dataParams,
                                               @Valid @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        Object resultObj = promotionService.addPromotion(dataParams, files, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Sua chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PutMapping(value = "/{promotionId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> editPromotion(@AuthenticationPrincipal Authentication authentication,
                                                @PathVariable Long promotionId,
                                                @Valid @RequestPart(value = "dataParams") PromotionDTO dataParams,
                                                @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        Object resultObj = promotionService.editPromotion(dataParams, authentication, promotionId, files);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xoa chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @DeleteMapping(value = "/{promotionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deletePromotion(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable Long promotionId) throws Exception {

        Object resultObj = promotionService.deletePromotion(authentication, promotionId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xoa file dinh kem
     *
     * @return
     */
    @DeleteMapping(value = "/attachments/{attachmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAttachment(@PathVariable Long attachmentId) throws Exception {
        promotionService.deleteAttachment(attachmentId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(""), HttpStatus.OK);
    }

    /**
     * Tai file dinh kem
     *
     * @return
     */
    @PostMapping(value = "/attachments/{attachmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void downloadAttachment(@PathVariable Long attachmentId,
                                   HttpServletResponse response) throws Exception {
        promotionService.downloadAttachment(attachmentId, response);
    }

    /**
     * Phe duet chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @PutMapping(value = "/approves", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> approvePromotion(@AuthenticationPrincipal Authentication authentication,
                                                   @RequestBody PromotionDTO params) {

        Object resultObj = promotionService.approvePromotion(params.getListId(), authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * export danh sach lich su tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @param response        response
     * @return
     */
    @RequestMapping(value = "/exports", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportPromotion(@AuthenticationPrincipal Authentication authentication, @RequestBody PromotionDTO dataParams, HttpServletResponse response)
            throws Exception {

        dataParams.setStartrecord(null);
        dataParams.setPagesize(null);
        String fileName = promotionService.exportPromotion(dataParams);
        FnCommon.responseFile(response, fileName);
    }


    /**
     * Lay du lieu doi tuong huong khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "/assigns", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPromotionAssigns(@AuthenticationPrincipal Authentication authentication,
                                                      PromotionAssignDTO dataParams) {
        Object resultObj = promotionService.getPromotionAssigns(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Them chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PostMapping(value = "/assigns", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addPromotionAssign(@AuthenticationPrincipal Authentication authentication,
                                                     @RequestBody @Valid PromotionAssignDTO dataParams) {

        Object resultObj = promotionService.addPromotionAssign(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xoa doi tuong huong chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @DeleteMapping(value = "/assigns/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deletePromotionAssign(@AuthenticationPrincipal Authentication authentication,
                                                        @PathVariable Long id) {
        Object resultObj = promotionService.deletePromotionAssign(authentication, id);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
