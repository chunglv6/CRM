package com.viettel.etc.controllers;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.services.ServiceFeeService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Autogen class: Lop thao tac phi
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1 + "/service-charges")
public class ServiceFeeController {

    @Autowired
    private ServiceFeeService serviceFeeService;

    /**
     * Lay danh sach muc phi dich vu
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getServiceFee(@AuthenticationPrincipal Authentication authentication,
                                                ServiceFeeDTO dataParams) {
        Object resultObj = serviceFeeService.getServiceFee(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xem chi tiet muc phi dich vu
     *
     * @param authentication: thong tin nguoi dung
     * @param serviceFeeId    id muc phi dich vu
     * @return
     */
    @GetMapping(value = "/{serviceFeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDetailServiceFee(@AuthenticationPrincipal Authentication authentication,
                                                      @PathVariable Long serviceFeeId) {

        Object resultObj = serviceFeeService.getDetailServiceFee(serviceFeeId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xem chi tiet muc phi dich vu
     *
     * @param authentication: thong tin nguoi dung
     * @param actReasonId     id muc phi dich vu
     * @return
     */
    @GetMapping(value = "/act-reasons/{actReasonId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDetailServiceFeeByReason(@AuthenticationPrincipal Authentication authentication,
                                                              @PathVariable Long actReasonId) {

        Object resultObj = serviceFeeService.getDetailServiceFeeByReason(actReasonId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Them chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addServiceFee(@AuthenticationPrincipal Authentication authentication,
                                                @RequestBody @Valid ServiceFeeDTO dataParams) throws Exception {

        Object resultObj = serviceFeeService.addServiceFee(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Sua chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PutMapping(value = "/{serviceFeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> editServiceFee(@AuthenticationPrincipal Authentication authentication,
                                                 @PathVariable Long serviceFeeId,
                                                 @RequestBody @Valid ServiceFeeDTO dataParams) throws Exception {

        Object resultObj = serviceFeeService.editServiceFee(dataParams, authentication, serviceFeeId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xoa chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @DeleteMapping(value = "/{serviceFeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteServiceFee(@AuthenticationPrincipal Authentication authentication,
                                                   @PathVariable Long serviceFeeId) throws Exception {

        Object resultObj = serviceFeeService.deleteServiceFee(authentication, serviceFeeId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Phe duet chuong trinh khuyen mai/ chiet khau
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @PutMapping(value = "/approves", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> approveServiceFee(@AuthenticationPrincipal Authentication authentication,
                                                    @RequestBody ServiceFeeDTO dataParams) {

        Object resultObj = serviceFeeService.approveServiceFee(dataParams.getListId(), authentication);
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
    public void exportServiceFee(@AuthenticationPrincipal Authentication authentication, @RequestBody ServiceFeeDTO dataParams, HttpServletResponse response)
            throws Exception {
        String fileName = serviceFeeService.exportServiceFee(dataParams);
        FnCommon.responseFile(response, fileName);
    }
}
