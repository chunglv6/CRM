package com.viettel.etc.controllers;

import com.viettel.etc.repositories.tables.entities.ContractPaymentEntity;
import com.viettel.etc.services.ContractPaymentService;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: Lop thao tac hop dong co lien ket tai khoan thanh toan
 *
 * @author ToolGen
 * @date Fri Jun 28 14:52:58 ICT 2020
 */
@RestController
@RequestMapping(REQUEST_MAPPING_V1)
public class ContractPaymentController {

    @Autowired
    ContractPaymentService contractPaymentService;

    /**
     * Lay danh sach thanh toan hop dong theo contractId
     *
     * @param authentication
     * @param contractId
     * @return
     * @throws DataNotFoundException
     */
    @GetMapping("/contract-payments/{contractId}")
    public ResponseEntity<?> getContractPaymentByContractId(@AuthenticationPrincipal Authentication authentication, @PathVariable("contractId") Long contractId) throws DataNotFoundException {
        ContractPaymentEntity response = contractPaymentService.findByContractIdAndStatus(contractId, ContractPaymentEntity.Status.ACTIVATED.value);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }
}
