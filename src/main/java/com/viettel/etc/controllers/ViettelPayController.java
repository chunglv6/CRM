package com.viettel.etc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.viettel.etc.dto.viettelpay.*;
import com.viettel.etc.services.ViettelPayService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1 + "/viettelpay")
public class ViettelPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViettelPayController.class);

    @Autowired
    ViettelPayService viettelPayService;

    /**
     * Tim kiem hop dong bang (contractId va contractNo) hoac (plateType va plateNumber)
     *
     * @return ResponseEntity
     */
    @GetMapping("/contract/info")
    public ResponseEntity<?> findContractByContractOrPlate(RequestBaseViettelDTO data, BindingResult bindingResult, @AuthenticationPrincipal Authentication authentication
    ) {
        if (!bindingResult.getFieldErrors().isEmpty()) {
            String messageErrors = FnCommon.handleParamsBindingException(bindingResult.getFieldErrors());
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseContract(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, messageErrors), null));
        }
        if (FnCommon.isNullOrEmpty(data.getOrderId())) {
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseContract(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, "OrderId bat buoc khac null"), null));
        }
        return ResponseEntity.ok().body(viettelPayService.findByPlateOrContract(data, authentication));
    }

    /**
     * Cap nhat contract payment khi lien ket viettelpay
     *
     * @param authentication Ma token
     * @return ResponseEntity
     */
    @GetMapping("/register-confirm")
    public ResponseEntity<?> confirmResultRegisterViettelPay(RequestConfirmRegisterDTO data,
                                                             @AuthenticationPrincipal Authentication authentication, BindingResult bindingResult) throws IllegalAccessException {
        String validateMess = FnCommon.hasFieldNull(data);
        if (!FnCommon.isNullOrEmpty(validateMess)) {
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, validateMess)));
        }
        return ResponseEntity.ok().body(viettelPayService.updateContractPaymentWhenRegister(data, authentication));
    }

    /**
     * Cap nhat contract payment khi huy lien ket viettelpay
     *
     * @return ResponseEntity
     */
    @GetMapping("/unregister-confirm")
    public ResponseEntity<?> confirmResultUnRegisterViettelPay(RequestContractPaymentDTO data, BindingResult bindingResult, @AuthenticationPrincipal Authentication authentication) throws IllegalAccessException {

        String validateMess = FnCommon.hasFieldNull(data);
        if (!FnCommon.isNullOrEmpty(validateMess)) {
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, validateMess)));
        }
        return ResponseEntity.ok().body(viettelPayService.updateContractPaymentWhenUnRegister(data, authentication));
    }

    /**
     * Tra ket qua thay doi nguon tien
     *
     * @param authentication Ma token
     * @return ResponseEntity
     */
    @GetMapping("/change-source")
    public ResponseEntity<?> changeMoneySourceViettelPay(RequestConfirmChangeMoneySourceDTO data,
                                                         @AuthenticationPrincipal Authentication authentication, BindingResult bindingResult) throws IllegalAccessException {
        String validateMess = FnCommon.hasFieldNull(data);
        if (!FnCommon.isNullOrEmpty(validateMess)) {
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, validateMess)));
        }
        return ResponseEntity.ok().body(viettelPayService.changeMoneySourceViettelPay(data, authentication));
    }


    /**
     * khoi tao lien ket tai khoan viettel pay
     *
     * @param authentication: thong tin nguoi dung
     * @param requestBody
     * @return
     */
    @PostMapping("/link-init")
    public ResponseEntity<?> linkInitViettelPay(@Valid @RequestBody RequestLinkInitViettelPayDTO requestBody, @AuthenticationPrincipal Authentication authentication) throws DataNotFoundException, IOException {
        Object responseModel = viettelPayService.linkInitViettelPay(requestBody, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * xac nhan khoi tao lien ket tai khoan viettel pay
     *
     * @param authentication: thong tin nguoi dung
     * @param requestData
     * @return
     */
    @PostMapping("/link-confirm")
    public ResponseEntity<?> linkViettelPayConfirm(@Valid @RequestBody RequestLinkConfirmViettelPayDTO requestData, @AuthenticationPrincipal Authentication authentication) throws DataNotFoundException, IOException {
        Object response = viettelPayService.linkConfirmViettelPay(requestData, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * khoi tao huy lien ket tai khoan viettel pay
     *
     * @param authentication: thong tin nguoi dung
     * @param requestData
     * @return
     */
    @PostMapping("/cancel-init")
    public ResponseEntity<?> linkViettelPayCancalInit(@Valid @RequestBody RequestCancelInitViettelPayDTO requestData, @AuthenticationPrincipal Authentication authentication) throws DataNotFoundException, IOException {
        Object response = viettelPayService.linkCancelInitViettelPay(requestData, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * xac nhan khoi tao huy lien ket tai khoan viettel pay
     *
     * @param authentication: thong tin nguoi dung
     * @param requestData
     * @return
     */
    @PostMapping("/cancel-confirm")
    public ResponseEntity<?> linkViettelPayCancalConfirm(@Valid @RequestBody RequestCancelConfirmViettelPayDTO requestData, @AuthenticationPrincipal Authentication authentication) throws DataNotFoundException, IOException {
        Object response = viettelPayService.linkCancelConfirmViettelPay(requestData, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * danh sach nguon tien thanh toan tai khoan viettel pay
     *
     * @param authentication: thong tin nguoi dung
     * @param msisdn
     * @param actTypeId
     * @return
     */
    @GetMapping("/source/query")
    public ResponseEntity<?> getSourceViettelPay(@RequestParam("msisdn") String msisdn, @RequestParam("actTypeId") Long actTypeId, @AuthenticationPrincipal Authentication authentication) throws DataNotFoundException, IOException {
        Object respone = viettelPayService.getSourcesViettelPay(msisdn, actTypeId, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(respone), HttpStatus.OK);
    }

    /***
     * Thong tin mua ve thang quy va gia han qua luong sdk cua viettelpay
     * @param billingCode ma giao dich ETC truyen sang ViettelPay qua luong SDK
     * @return
     */
    @GetMapping("/check-info/ticket/purchase/extended")
    public ResponseEntity<?> getInfoTicketPurchaseAndExtendedViaSDK(@RequestParam("billingCode") String billingCode, @AuthenticationPrincipal Authentication authentication) throws JsonProcessingException {
        Object response = viettelPayService.getInfoTicketPurchaseAndExtendedViaSDK(authentication, billingCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Tra ket qua tu dong gia han ve thang quy
     *
     * @return
     */
    @PostMapping("/result/ticket-purchase/init/auto-renew")
    public ResponseEntity<?> confirmResultTicketPurchaseAutoRenew(@Valid @RequestBody RequestRenewTicketPricesDTO data
            , @AuthenticationPrincipal Authentication authentication) throws Exception {
        String validateMess = FnCommon.hasExceptionField(data);
        if (!FnCommon.isNullOrEmpty(validateMess)) {
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, validateMess)));
        }
        return ResponseEntity.ok().body(viettelPayService.confirmResultTicketPurchaseAutoRenew(data, authentication));
    }

    /**
     * Tra ket qua huy tu dong gia han ve thang quy
     *
     * @return
     */
    @PostMapping("/result/ticket-purchase/cancel/auto-renew")
    public ResponseEntity<?> confirmResultCancelTicketPurchaseAutoRenew
    (@Valid @RequestBody RequestRenewTicketPricesDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        String validateMess = FnCommon.hasExceptionField(data);
        if (!FnCommon.isNullOrEmpty(validateMess)) {
            return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                    new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, validateMess)));
        }

        return ResponseEntity.ok().body(viettelPayService.confirmResultCancelTicketPurchaseAutoRenew(data, authentication));
    }


    /***
     * Kiem tra thong tin Dang ky gia han qua SDK (Luong rieng) cua viettelpay
     * @param billingCode ma giao dich ETC truyen sang ViettelPay qua luong SDK
     * @return
     */
    @GetMapping("/check-info/subscriptions/extended/private-stream")
    public ResponseEntity<?> getInfoSubscriptionsExtendedViaSDKPrivateStream(@RequestParam("billingCode") String billingCode, @AuthenticationPrincipal Authentication authentication) throws JsonProcessingException {
        Object response = viettelPayService.getInfoSubscriptionsExtendedViaSDKPrivateStream(authentication, billingCode);
        return ResponseEntity.ok(response);
    }


    /***
     * Tra ket qua verify sale order cua CRM cho VT PAY
     * @param viettelPayRequestDTO
     * @param authentication
     * @return
     * @throws EtcException
     */
//    @PostMapping(value = "/sale-order/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/result/ticket-purchase/add-money/verify-data", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifySaleOrderOld(@Valid @ModelAttribute ViettelPayRequestDTO viettelPayRequestDTO, @AuthenticationPrincipal Authentication authentication) throws EtcException {
        LOGGER.info("VTPAY verify SALE ORDER tren CRM backend req={}", viettelPayRequestDTO.toString());
        Object response = viettelPayService.verifySaleOrder(viettelPayRequestDTO, authentication);
        return ResponseEntity.ok().body(response);
    }


    /***
     * API VTPAY trả kết quả nộp tiền vào tài khoản giao thông cho backend CRM
     * @param data
     * @param authentication
     * @return
     * @throws Exception
     */
//    @PostMapping(value = "/sale-order/result", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/result/ticket-purchase/add-money/response-result", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resultSaleOrderFromVTPAYOld(@Valid @ModelAttribute ViettelPayRequestDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        LOGGER.info("VTPAY tra ket qua SALE ORDER cho CRM backend req={}", data.toString());
        Object response = viettelPayService.resultSaleOrder(data, authentication);
        return ResponseEntity.ok().body(response);
    }

    /***
     * Tra ket qua verify sale order cua CRM cho VT PAY - Chuyen DOAMIN
     * @param viettelPayRequestDTO
     * @param authentication
     * @return
     * @throws EtcException
     */
    @PostMapping(value = "/sale-order/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifySaleOrder(@Valid @ModelAttribute ViettelPayRequestDTO viettelPayRequestDTO, @AuthenticationPrincipal Authentication authentication) throws EtcException {
        LOGGER.info("VTPAY verify SALE ORDER tren CRM backend req={}", viettelPayRequestDTO.toString());
        Object response = viettelPayService.verifySaleOrder(viettelPayRequestDTO, authentication);
        return ResponseEntity.ok().body(response);
    }


    /***
     * API VTPAY trả kết quả nộp tiền vào tài khoản giao thông cho backend CRM - Chuyen DOAMIN
     * @param data
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/sale-order/result", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resultSaleOrderFromVTPAY(@Valid @ModelAttribute ViettelPayRequestDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        LOGGER.info("VTPAY tra ket qua SALE ORDER cho CRM backend req={}", data.toString());
        Object response = viettelPayService.resultSaleOrder(data, authentication);
        return ResponseEntity.ok().body(response);
    }


    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException ex) {
        String messageErrors = FnCommon.handleParamsBindingException(ex.getFieldErrors());
        return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, messageErrors)));
    }

    /**
     * Xu ly du lieu dau vao khong dung dinh dang
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<?> handleBindException(InvalidFormatException ex) {
        String mess = "";
        for (JsonMappingException.Reference path : ex.getPath()) {
            mess = path.getFieldName() + ": Khong dung dinh dang";
        }
        return ResponseEntity.ok().body(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, mess)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleErrorNotReadAbleException(HttpMessageNotReadableException exception) {
        return ResponseEntity.ok(new ResponseViettelPayDTO.ResponseRegisterConfirm(null,
                new StatusDTO().responseMessage(StatusDTO.StatusCode.FAIL, "Đã xảy ra lỗi trong quá trình xử lý ")));
    }
}
