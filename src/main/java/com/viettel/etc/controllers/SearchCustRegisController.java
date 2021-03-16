package com.viettel.etc.controllers;


import com.viettel.etc.dto.SearchCustRegisDTO;
import com.viettel.etc.services.SearchCustRegisService;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
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
 * Autogen class: Lay thong tin khach hang da dang ki
 * 
 * @author ToolGen
 * @date Mon Feb 01 15:43:13 ICT 2021
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class SearchCustRegisController {
    @Autowired 
    private SearchCustRegisService searchCustRegisService;
    

    /**
     * Lay du lieu doanh thu cua bot
     * 
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return 
     */
    @GetMapping(value = "/cust-registers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchCustRegstered(@AuthenticationPrincipal Authentication authentication, SearchCustRegisDTO dataParams) {
        Object resultObj = searchCustRegisService.searchCustRegstered(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }


    /**
     * export danh sach khach hang da dang ki
     *
     * @param authentication: thong tin nguoi dung
     * @param response        response
     * @param params      params client
     * @return
     */
    @PostMapping(value = "/cust-registers/exports", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportCustRegistered(@AuthenticationPrincipal Authentication authentication,@RequestBody SearchCustRegisDTO params, HttpServletResponse response) throws Exception {
        String fileName = searchCustRegisService.exportCustRegistered(params);
        FnCommon.responseFile(response, fileName);
    }
}