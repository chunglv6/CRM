package com.viettel.etc.controllers;

import com.viettel.etc.dto.AddSupOfferRequestDTO;
import com.viettel.etc.dto.momo.MoMoRawDataDTO;
import com.viettel.etc.dto.viettelpay.RequestAddSupOfferDTO;
import com.viettel.etc.services.MoMoService;
import com.viettel.etc.services.ViettelPayService;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class MobileAppController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileAppController.class);

    @Autowired
    ViettelPayService viettelPayService;

    @Autowired
    MoMoService moMoService;

    /***
     * Tao sale order cho MOBILE APP khi nap tien vao tai khoan giao thong qua VIETTEL PAY
     * Co the nap tien cho tai khoan giao thong khac
     *
     * @param authentication
     * @param customerId
     * @param contractId
     * @param addSupOfferRequest
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/viettelpay/customers/{customerId}/contracts/{contractId}/sale-order", method = RequestMethod.POST)
    @RequestMapping(value = "/viettelpay/customers/{customerId}/contracts/{contractId}/add-money-order", method = RequestMethod.POST)
    public ResponseEntity<Object> createSaleOrderOld(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable Long customerId,
                                                  @PathVariable Long contractId,
                                                  @RequestBody RequestAddSupOfferDTO addSupOfferRequest) throws EtcException {
        LOGGER.info("MOBILE APP call CREATE SALE ORDER customerId={},contractId={},req={}", customerId, contractId, addSupOfferRequest.toString());
        Object response = viettelPayService.createSaleOrder(addSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }


    /***
     * Tao sale order cho MOBILE APP khi nap tien vao tai khoan giao thong qua VIETTEL PAY - Chuyen DOMAIN
     * Co the nap tien cho tai khoan giao thong khac
     *
     * @param authentication
     * @param customerId
     * @param contractId
     * @param addSupOfferRequest
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/viettelpay/customers/{customerId}/contracts/{contractId}/sale-order", method = RequestMethod.POST)
    public ResponseEntity<Object> createSaleOrder(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable Long customerId,
                                                  @PathVariable Long contractId,
                                                  @RequestBody RequestAddSupOfferDTO addSupOfferRequest) throws EtcException {
        LOGGER.info("MOBILE APP call CREATE SALE ORDER customerId={},contractId={},req={}", customerId, contractId, addSupOfferRequest.toString());
        Object response = viettelPayService.createSaleOrder(addSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }


    /**
     * Tạo dữ liệu đơn hàng nộp tiền hộ cho hợp đồng khác qua cổng thanh toán
     *
     * @param authentication:    thong tin nguoi dung
     * @param addSupOfferRequest
     * @return
     */
    @RequestMapping(value = "/viettelpay/customers/{customerId}/contracts/{contractId}/sale-order-other", method = RequestMethod.POST)
    public ResponseEntity<Object> createSaleOrderOther(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable Long customerId,
                                                       @PathVariable Long contractId,
                                                       @RequestBody RequestAddSupOfferDTO addSupOfferRequest) {
        Object response = viettelPayService.createSaleOrder(addSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }


    /**
     * buy ticket
     *
     * @param authentication:    thong tin nguoi dung
     * @param addSupOfferRequest
     * @return
     */
    @RequestMapping(value = "/viettelpay/customers/{customerId}/contracts/{contractId}/charge-ticket", method = RequestMethod.POST)
    public ResponseEntity<Object> confirmChargeTicket(@AuthenticationPrincipal Authentication authentication,
                                                      @PathVariable Long customerId,
                                                      @PathVariable Long contractId,
                                                      @RequestBody AddSupOfferRequestDTO addSupOfferRequest) throws Exception {
        Object response = viettelPayService.confirmChargeTicket(addSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }


    /**
     * App ChuPT goi server CRM sau do CRM backend goi backend MOMO
     * Gui yeu cau treo tien toi server Momo
     * Response sale order khi app chu phuong tien nap tien
     *
     * @param
     * @return ResponseEntity
     */
    @PostMapping(value = "/momo/pay/app", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> requestPayment(@Valid @RequestBody MoMoRawDataDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        Object resultObj = moMoService.requestMoMoAppPayment(data, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);

    }


    /**
     * Tao SALE ORDER tu APP CHU PT goi sang backend CRM de tao ORDER nap tien tu MOMO
     *
     * @param
     * @return ResponseEntity
     */
    @PostMapping(value = "/mobile-app-chupt/customers/{customerId}/contracts/{contractId}/sale-order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> mobileAppChuPTCreateSaleOrder(@AuthenticationPrincipal Authentication authentication,
                                                                @PathVariable Long customerId,
                                                                @PathVariable Long contractId,
                                                                @RequestBody RequestAddSupOfferDTO addSupOfferRequest) {
        Object response = moMoService.mobileAppChuPTCreateSaleOrder(addSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }


    /**
     * Tao SALE ORDER tu Portal CHU PT goi sang backend CRM de tao ORDER nap tien tu MOMO
     *
     * @param
     * @return ResponseEntity
     */
    @PostMapping(value = "/portal-chupt/customers/{customerId}/contracts/{contractId}/sale-order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> portalChuPTCreateSaleOrder(@AuthenticationPrincipal Authentication authentication,
                                                             @PathVariable Long customerId,
                                                             @PathVariable Long contractId,
                                                             @RequestBody RequestAddSupOfferDTO addSupOfferRequest) throws Exception {
        Object response = moMoService.portalChuPTCreateSaleOrder(addSupOfferRequest, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }
}
