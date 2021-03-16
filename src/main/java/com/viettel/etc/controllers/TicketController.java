package com.viettel.etc.controllers;

import com.viettel.etc.dto.*;
import com.viettel.etc.services.TicketService;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: Lop thao tac mua ve thang quy
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class TicketController {

    @Autowired
    TicketService ticketService;

    /**
     * buy ticket
     *
     * @param authentication:    thong tin nguoi dung
     * @param addSupOfferRequest
     * @return
     */
    @RequestMapping(value = "/customers/{customerId}/contracts/{contractId}/charge-ticket-boo", method = RequestMethod.POST)
    public ResponseEntity<Object> chargeTicketNew(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable Long customerId,
                                                  @PathVariable Long contractId,
                                                  @RequestBody AddSupOfferRequestDTO addSupOfferRequest) throws Exception {
        ResponseChargeTicketCRM responseChargeTicketCRM = ticketService.chargeTicketNew(addSupOfferRequest, authentication, customerId, contractId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseChargeTicketCRM), HttpStatus.OK);
    }

    /**
     * buy ticket ngoài hợp đồng web cpt
     *
     * @param authentication:        thong tin nguoi dung
     * @param listAddSupOfferRequest
     * @return chargeTicketNewOutContractId
     */
    @RequestMapping(value = "/customers/{customerId}/contracts/{contractId}/charge-ticket-boo-cpt", method = RequestMethod.POST)
    public ResponseEntity<Object> chargeTicketNewOutContractId(@AuthenticationPrincipal Authentication authentication,
                                                               @PathVariable Long customerId,
                                                               @PathVariable Long contractId,
                                                               @RequestBody ListAddSupOfferRequestDTO listAddSupOfferRequest) throws Exception {
        ResponseChargeTicketCRM responseChargeTicketCRM = ticketService.chargeTicketNewOutContractId(listAddSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseChargeTicketCRM), HttpStatus.OK);
    }

    /**
     * Tra cuu thong ve bi huy ben boo1
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @RequestMapping(value = "/ticket/del-boo1/{contractId}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchTicketTypeDelBoo1(@AuthenticationPrincipal Authentication authentication,
                                                          @PathVariable Long contractId,
                                                          SaleTransDelBoo1DTO saleTransDelBoo1DTO) throws Exception {
        Object resultObj = ticketService.searchTicketDelBoo1(contractId, saleTransDelBoo1DTO);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Xac nhan huy ve thang quy
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @RequestMapping(value = "/ticket/confirm/destroy/{saleTransDetailId}", method = RequestMethod.POST)
    public ResponseEntity<Object> confirmDestroyTicket(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable Long saleTransDetailId,
                                                       @RequestBody SaleTransDelBoo1DTO saleTransDelBoo1DTO) throws Exception {
        Object resultObj = ticketService.confirmDestroyTicket(saleTransDetailId, saleTransDelBoo1DTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Huy ve co hoan tien
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @RequestMapping(value = "/ticket/destroy/refund/{subscriptionTicketId}", method = RequestMethod.POST)
    public ResponseEntity<Object> destroyTicketRefund(@AuthenticationPrincipal Authentication authentication,
                                                      @PathVariable Long subscriptionTicketId,
                                                      @RequestBody SaleTransDelBoo1DTO saleTransDelBoo1DTO) throws Exception {
        Object resultObj = ticketService.destroyTicketRefund(subscriptionTicketId, saleTransDelBoo1DTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Huy ve khong hoan tien
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @RequestMapping(value = "/ticket/destroy/not-refund/{saleTransDetailId}", method = RequestMethod.POST)
    public ResponseEntity<Object> destroyTicketNotRefund(@AuthenticationPrincipal Authentication authentication,
                                                         @PathVariable Long saleTransDetailId,
                                                         @RequestBody SaleTransDetailDTO saleTransDetailDTO) throws Exception {
        Object resultObj = ticketService.destroyTicketNotRefund(saleTransDetailId, saleTransDetailDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Huy tu dong gia han
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @RequestMapping(value = "/ticket/cancel/autoRenew/{saleTransDetailId}", method = RequestMethod.POST)
    public ResponseEntity<Object> cancelAutoRenew(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable Long saleTransDetailId,
                                                  @RequestBody SaleTransDetailDTO saleTransDetailDTO) throws Exception {
        Object resultObj = ticketService.cancelAutoRenew(saleTransDetailId, saleTransDetailDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Tu dong gia han
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @RequestMapping(value = "/ticket/auto-renew/{saleTransDetailId}", method = RequestMethod.POST)
    public ResponseEntity<Object> registerAutoRenew(@AuthenticationPrincipal Authentication authentication,
                                                    @PathVariable Long saleTransDetailId,
                                                    @RequestBody SaleTransDetailDTO saleTransDetailDTO) throws Exception {
        Object resultObj = ticketService.registerAutoRenew(saleTransDetailId, saleTransDetailDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
