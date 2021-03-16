package com.viettel.etc.controllers;

import com.viettel.etc.dto.boo.*;
import com.viettel.etc.dto.keycloak.ReqLoginDTO;
import com.viettel.etc.dto.keycloak.ResLoginDTO;
import com.viettel.etc.services.BooService;
import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.services.KeycloakService;
import com.viettel.etc.utils.exceptions.BooException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class BOOController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BOOController.class);

    @Autowired
    BooService booService;

    @Autowired
    JedisCacheService jedisCacheService;

    @Autowired
    private KeycloakService keycloakService;

    /**
     * Tra cuu du lieu ve
     *
     * @param reqQueryTicketDTO DU lieu ve
     * @return
     */
    @GetMapping("/boo/subscription")
    public ResponseEntity<?> queryTicket(@AuthenticationPrincipal Authentication authentication, ReqQueryTicketDTO reqQueryTicketDTO) {
        LOGGER.info("BOO1 call queryTicket req=" + reqQueryTicketDTO.toString());
        Object result = booService.queryTicket(authentication, reqQueryTicketDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Tinh gia ve
     *
     * @param reqCalculatorTicketDTO Du lieu ve
     * @return
     */
    @GetMapping("/boo/subscription/check")
    public ResponseEntity<?> calculatorTicket(@AuthenticationPrincipal Authentication authentication, ReqCalculatorTicketDTO reqCalculatorTicketDTO) {
        LOGGER.info("BOO1 call calculatorTicket req=" + reqCalculatorTicketDTO.toString());
        Object result = booService.calculatorTicket(authentication, reqCalculatorTicketDTO, true);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Mua ve
     *
     * @param reqChargeTicketDTO
     * @return
     */
    @PostMapping("/boo/subscription")
    public ResponseEntity<?> chargeTicket(@AuthenticationPrincipal Authentication authentication, @RequestBody ReqChargeTicketDTO reqChargeTicketDTO) throws Exception {
        LOGGER.info("BOO1 call chargeTicket req=" + reqChargeTicketDTO.toString());
        Object result = booService.chargeTicket(authentication, reqChargeTicketDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Huy ve
     *
     * @param reqCancelTicketDTO
     * @return
     */
    @PostMapping(value = "/boo/subscription/cancel")
    public ResponseEntity<?> cancelTicket(@AuthenticationPrincipal Authentication authentication, @RequestBody ReqCancelTicketDTO reqCancelTicketDTO) {
        LOGGER.info("BOO1 call cancelTicket req=" + reqCancelTicketDTO.toString());
        Object result = booService.cancelTicket(authentication, reqCancelTicketDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Ket qua huy ve
     *
     * @param reqCancelResultDTO
     * @return
     */
    @PostMapping("/boo/subscription/cancel-result")
    public ResponseEntity<?> cancelTicketResult(@AuthenticationPrincipal Authentication authentication, @RequestBody ReqCancelResultDTO reqCancelResultDTO) throws Exception {
        LOGGER.info("BOO1 call cancelTicketResult req=" + reqCancelResultDTO.toString());
        Object result = booService.cancelResult(authentication, reqCancelResultDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Tra cuu thong tin
     *
     * @param reqActivationCheckDTO
     * @return
     */
    @PostMapping("/boo/activation/check")
    public ResponseEntity<?> activationCheck(@AuthenticationPrincipal Authentication authentication, @RequestBody ReqActivationCheckDTO reqActivationCheckDTO) {
        LOGGER.info("BOO1 call activationCheck req=" + reqActivationCheckDTO.toString());
        Object result = booService.checkActivation(authentication, reqActivationCheckDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Dong bo du lieu online khi them moi xe ngoai le
     *
     * @param requestOnlineEventRegBooDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/boo/online-event/reg")
    public ResponseEntity<?> onlineEventReg(@RequestBody ReqOnlineEventRegDTO requestOnlineEventRegBooDTO, @AuthenticationPrincipal Authentication authentication) throws Exception {
        LOGGER.info("BOO1 call onlineEventReg req=" + requestOnlineEventRegBooDTO.toString());
        return ResponseEntity.ok().body(booService.onlineEventReg(requestOnlineEventRegBooDTO, authentication));
    }

    /**
     * Dong bo du lieu online khi thay doi du lieu cua xe ngoai le
     *
     * @param reqOnlineEventSyncDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/boo/online-event/sync")
    public ResponseEntity<?> onlineEventSync(@RequestBody ReqOnlineEventSyncDTO reqOnlineEventSyncDTO,
                                             @AuthenticationPrincipal Authentication authentication) throws Exception {
        LOGGER.info("BOO1 call onlineEventSync req=" + reqOnlineEventSyncDTO.toString());
        return ResponseEntity.ok().body(booService.onlineEventSync(reqOnlineEventSyncDTO, authentication));
    }

    /**
     * Lay danh muc mapping boo
     *
     * @param authentication
     * @return
     * @throws Exception
     */
    @GetMapping("/boo/mapping")
    public ResponseEntity<?> getListCategoryMappingBoo(@AuthenticationPrincipal Authentication authentication, ReqMappingDTO reqMappingDTO) {
        LOGGER.info("BOO1 call getListCategoryMappingBoo req=" + reqMappingDTO.toString());
        return ResponseEntity.ok().body(booService.getListCategoryMappingBoo(authentication, reqMappingDTO));
    }

    /**
     * Dang nhap
     *
     * @param reqLoginDTO params client
     * @return
     */
    @PostMapping(value = "/boo/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@Valid @ModelAttribute ReqLoginDTO reqLoginDTO) {
        LOGGER.info("BOO1 call login req=" + reqLoginDTO.toString());
        ResLoginDTO resultObj = keycloakService.loginBoo(reqLoginDTO);
        return new ResponseEntity<>(resultObj, HttpStatus.valueOf(resultObj.getCode()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleErrorNotReadAbleException(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResExceptionBooDTO(System.currentTimeMillis(), "crm.boo.internal.server"));
    }

    @ExceptionHandler(BooException.class)
    public ResponseEntity<?> handleBooException(BooException exception) {
        return ResponseEntity.badRequest().body(new ResExceptionBooDTO(exception.getTimestamp(), String.valueOf(jedisCacheService.getCodeErrorByKey(exception.getError())), exception.getError(), jedisCacheService.getMessageErrorByKey(exception.getMessage())));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResExceptionBooDTO(System.currentTimeMillis(), "crm.boo.internal.server"));
    }
}
