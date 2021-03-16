package com.viettel.etc.controllers;

import com.viettel.etc.dto.AliasDTO;
import com.viettel.etc.dto.TopupAccountDTO;
import com.viettel.etc.dto.keycloak.*;
import com.viettel.etc.services.KeycloakService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class KeycloakController {

    @Autowired
    KeycloakService keycloakService;

    @PostMapping(value = "/keycloak/import-account", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> importAccount(@RequestParam MultipartFile fileImport, @AuthenticationPrincipal Authentication authentication, @RequestPart String group) throws IOException {
        return keycloakService.createUserKeycloak(authentication, fileImport, group);
    }

    /***
     * Them account vao danh sach tram truong
     * @return
     */
    @GetMapping(value = "/topup-ctv/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getMembers(TopupAccountDTO params,
                                             @AuthenticationPrincipal Authentication authentication) {
        Object result = keycloakService.findCTVAccount(params, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /**
     * Lay toan bo danh sach user kem theo group
     */
    @GetMapping(value = "/keycloak/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllUserKeycloak(@AuthenticationPrincipal Authentication authentication, ReqUserKeycloakDTO reqUserKeycloakDTO) throws IOException {
        Object resultObj = keycloakService.getUser(reqUserKeycloakDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Dang nhap
     *
     * @param dataParams params client
     * @return
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@Valid @ModelAttribute ReqLoginDTO dataParams) {
        ResLoginDTO resultObj = keycloakService.login(dataParams);
        return new ResponseEntity<>((resultObj), HttpStatus.valueOf(resultObj.getCode()));
    }

    /**
     * Dang xuat
     *
     * @param dataParams params client
     * @return
     */
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(@Valid @ModelAttribute ReqLogoutDTO dataParams) throws IOException {
        ResLoginDTO resultObj = keycloakService.logout(dataParams);
        if (resultObj == null) {
            return new ResponseEntity<>(FnCommon.responseToClient(ErrorApp.SUCCESS), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultObj, HttpStatus.valueOf(resultObj.getCode()));
        }
    }

    /**
     * Doi ten dang nhap
     *
     * @return
     */
    @PostMapping(value = "/alias", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> alias(@AuthenticationPrincipal Authentication authentication,
                                        @Valid @RequestBody AliasDTO body) {
        keycloakService.alias(body.getAliasName(), authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(ErrorApp.SUCCESS), HttpStatus.OK);
    }

    /**
     * Khoa tai khoan khach hang
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @PutMapping(value = "/lock/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> lockUser(HttpServletRequest request,
                                           @AuthenticationPrincipal Authentication authentication,
                                           @PathVariable("userId") String userId,
                                           @Valid @RequestBody ReqChangePassUserKeycloakDTO dataParams) throws IOException {
        Object resultObj = keycloakService.lockUser(userId, dataParams, authentication, request.getRemoteAddr());
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Reset pass tai khoan khach hang
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @PutMapping(value = "/reset/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> resetPassUser(@AuthenticationPrincipal Authentication authentication,
                                                @PathVariable("userId") String userId) throws IOException {
        Object resultObj = keycloakService.resetPassUser(userId, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Reset pass tai khoan khach hang
     *
     * @param dataParams params client
     * @return
     */
    @PutMapping(value = Constants.MOBILE + "/reset/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> resetPassUserCPT(@Valid @RequestBody ReqResetPassDTO dataParams) throws IOException {
        Object resultObj = keycloakService.resetPassUserCPT(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Doi pass tai khoan khach hang
     *
     * @param dataParams params client
     * @return
     */
    @PutMapping(value = Constants.MOBILE + "/change/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changePassUser(@AuthenticationPrincipal Authentication authentication,
                                                 @Valid @RequestBody ReqChangePassDTO dataParams) throws IOException {
        Object resultObj = keycloakService.changePassUser(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
