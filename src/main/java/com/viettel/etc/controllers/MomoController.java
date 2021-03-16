package com.viettel.etc.controllers;

import com.viettel.etc.dto.momo.*;
import com.viettel.etc.services.MoMoService;
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
public class MomoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MomoController.class);

    @Autowired
    MoMoService moMoService;

    /***
     * Tim kiem hop dong bang (contractId va contractNo) hoac (plateType va plateNumber)
     * @param data
     * @param authentication
     * @return
     */
    @GetMapping("/momo-app/contract-info")
    public ResponseEntity<?> findContractByContractOrPlate(MoMoGetContractRequestDTO data, @AuthenticationPrincipal Authentication authentication) {
        Object result = moMoService.findByPlateOrContract(data, authentication);
        return ResponseEntity.ok().body(result);
    }

    /***
     * Xử lý yêu cầu nạp tiền cho hợp đồng giao thông số từ ví MoMo
     * MoMo gọi API BackEnd CRM yêu cầu nạp tiền trên OCS cho hợp đồng giao thông số
     * @param data
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/momo-app/add-balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleRequestAddMoneyFromMoMoApp(@RequestBody MoMoAppRequestPaymentDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        MoMoAppResponsePaymentDTO resultObj = moMoService.handleRequestAddMoneyFromMoMoApp(data, authentication);
        return new ResponseEntity<>(resultObj, HttpStatus.OK);

    }

    /***
     * Xac nhan ket qua treo tiền thành công ben phia server MoMo gui sang
     * @param data
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/momo/pay/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> responseNotifyFromMoMo(@Valid @RequestBody MoMoNotifyRequestDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        MoMoNotifyResponseDTO resultObj = moMoService.responseNotifyFromMoMo(data, authentication);
        return new ResponseEntity<>(resultObj, HttpStatus.OK);

    }


    /***
     * Api verify SALE ORDER cho MOMO tu mobile-app-chupt tao SALE ORDER
     * @param data
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/mobile-app-chupt/sale-order/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyDataFromMomo (@Valid @RequestBody MoMoNotifyRequestDTO data, @AuthenticationPrincipal Authentication authentication) throws Exception {
        LOGGER.info("verifyDataFromMomo verify SALE ORDER tren CRM backend req={}", data.toString());
        Object result = moMoService.verifyMoMoRequestData(data, authentication);
        return ResponseEntity.ok().body(result);
    }


    /***
     * Api verify SALE ORDER cho MOMO tu portal-chupt tao SALE ORDER
     * @param data
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/portal-chupt/sale-order/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyDataFromMomoWeb (@Valid MoMoWebNotifyRequestDTO data) throws Exception {
        LOGGER.info("verifyDataFromMomoWeb verify SALE ORDER tren CRM backend req={}", data.toString());
        Object result = moMoService.verifyMoMoRequestDataWeb(data);
        return ResponseEntity.ok().body(result);
    }
}
