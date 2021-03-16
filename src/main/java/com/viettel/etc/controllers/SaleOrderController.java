package com.viettel.etc.controllers;

import com.viettel.etc.dto.SaleOrderDTO;
import com.viettel.etc.services.SaleOrderService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1)
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    /**
     * Tra cuu lich su giao dich nap tien
     *
     * @param authentication:         thong tin nguoi dung
     * @param params:                 params client
     * @return
     */
    @GetMapping(value = "/sale-orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSaleOrder(@AuthenticationPrincipal Authentication authentication,
                                               SaleOrderDTO params) {
        Object resultObj = saleOrderService.getSaleOrder(params);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
