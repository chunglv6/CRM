package com.viettel.etc.controllers;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.tables.entities.TopupEtcEntity;
import com.viettel.etc.services.TopupService;
import com.viettel.etc.services.tables.TopupEtcServiceJPA;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: Lop ve tac dong nop tien mat vao tai khoan ETC
 *
 * @author ToolGen
 * @date Mon Otc 26 11:19:51 ICT 2020
 */
@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class TopupController {

    @Autowired
    TopupEtcServiceJPA topupEtcServiceJPA;

    @Autowired
    TopupService topupService;

    /**
     * Lay danh sach lich su nap tien theo contractId
     *
     * @param authentication
     * @param contractId
     * @return
     */
    @GetMapping("/topup-etc/cash-histories")
    public ResponseEntity<?> getTopupHistory(@AuthenticationPrincipal Authentication authentication, @RequestParam("contractId") Long contractId) {
        List<TopupEtcEntity> topupEtcEntities = topupEtcServiceJPA.findAllByContractIdDesc(contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(topupEtcEntities), HttpStatus.OK);
    }

    /***
     * thuc hien nop tien mat vao tai khoan etc
     * @param authentication
     * @param request
     * @return
     */
    @PostMapping("/topup-etc/cashs")
    public ResponseEntity<?> executeTopupToEtcAction(@AuthenticationPrincipal Authentication authentication, @RequestBody TopupDTO request) throws Exception {
        Long result = topupService.executeTopupCashToEtc(authentication, request);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Export bill
     * @param authentication
     * @param topup
     * @return
     */
    @PostMapping("/topup-etc/cash/export")
    public ResponseEntity<?> exportTopupCashBill(@AuthenticationPrincipal Authentication authentication, @RequestBody TopupExportDTO topup) throws UnsupportedEncodingException {
        Map<String, Object> result = (Map<String, Object>) topupService.exportTopupCashBill(authentication, topup);
        Object resultObj = result.get("data");
        ErrorApp errorApp = (ErrorApp) result.get("error");
        if (resultObj == null) {
            resultObj = new byte[0];
        }
        ByteArrayResource byteArrayResource = new ByteArrayResource((byte[]) resultObj);
        HttpHeaders headers = new HttpHeaders();
        List<String> customHeaders = new ArrayList<>();
        customHeaders.add("Content-Disposition");
        customHeaders.add("Content-Response");
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Content-Disposition", "attachment;filename=" + FnCommon.createFileName("history_topup", new Date()) + ".pdf");
        headers.set("Content-Response", URLEncoder.encode(errorApp.toStringJson(), "UTF-8").replace("+", "%20"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(byteArrayResource);
    }

    /***
     * Nap tien cho hop dong
     * @return
     */
    @PostMapping(value = "/topup-ctv/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transfer(@Valid @RequestBody TopupTransferDTO params,
                                           @AuthenticationPrincipal Authentication authentication) {
        Object result = topupService.transferMoney(authentication, params);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Nap tien cho tram truong
     * @return
     */
    @PostMapping(value = "/topup-ctv/add-balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addBalance(@RequestBody TopupCtvDTO params,
                                             @AuthenticationPrincipal Authentication authentication) {
        TopupCtvResDTO result = topupService.addBalance(authentication, params);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Danh sach tram truong
     * @return
     */
    @GetMapping(value = "/topup-ctv/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUsers(TopupAccountDTO params,
                                           @AuthenticationPrincipal Authentication authentication) {
        Object result = topupService.findTopupAccount(params);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Them account vao danh sach tram truong
     * @return
     */
    @PostMapping(value = "/topup-ctv/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addUser(@Valid @RequestBody TopupAccountDTO params,
                                          @AuthenticationPrincipal Authentication authentication) {
        Object result = topupService.addAccount(params, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Sua account tram truong
     * @return
     */
    @PutMapping(value = "/topup-ctv/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@Valid @RequestBody TopupAccountDTO params,
                                             @AuthenticationPrincipal Authentication authentication) {
        Object result = topupService.updateAccount(params, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Lay account vao danh sach tram truong
     * @return
     */
    @GetMapping(value = "/topup-ctv/users/{accountUser}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> detailUserCtv(@PathVariable @NotBlank String accountUser,
                                                @AuthenticationPrincipal Authentication authentication) {
        Object result = topupService.detailCTVAccount(accountUser.toUpperCase(), authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     * Xoa account vao danh sach tram truong
     * @return
     */
    @DeleteMapping(value = "/topup-ctv/users/{accountUser}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUserCtv(@PathVariable @NotBlank String accountUser,
                                                @AuthenticationPrincipal Authentication authentication) {
        Object result = topupService.deleteCTVAccount(accountUser.trim());
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }
}
