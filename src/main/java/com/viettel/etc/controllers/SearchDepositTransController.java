package com.viettel.etc.controllers;

import com.viettel.etc.dto.SearchCustRegisDTO;
import com.viettel.etc.dto.SearchDepositTransDTO;
import com.viettel.etc.services.SearchDepositTransService;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.UserSystemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.viettel.etc.utils.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.servlet.http.HttpServletResponse;

/**
 * Autogen class: Lop thao tac xem thong tin giao dich nap tien
 * 
 * @author ToolGen
 * @date Mon Feb 08 09:01:07 ICT 2021
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class SearchDepositTransController {
    @Autowired 
    private SearchDepositTransService searchDepositTransService;
    

    /**
     * Lay du lieu giao dich nap tien
     * 
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return 
     */
    @GetMapping(value = "/topup-etcs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDepositInfo(@AuthenticationPrincipal Authentication authentication, 
                                              SearchDepositTransDTO dataParams) {
        /*
        ==========================================================
        authenEntity: user info and role
        dataParams: danh sach bien client co the truyen len
        ==========================================================
        */
        Object resultObj = searchDepositTransService.getDepositInfo(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * export danh sach giao dich nap tien
     *
     * @param authentication: thong tin nguoi dung
     * @param response        response
     * @param params      params client
     * @return
     */
    @PostMapping(value = "/topup-etcs/exports", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportDepositTrans(@AuthenticationPrincipal Authentication authentication, @RequestBody SearchDepositTransDTO params, HttpServletResponse response) throws Exception {
        String fileName = searchDepositTransService.exportDepositTrans(params);
        FnCommon.responseFile(response, fileName);
    }
}