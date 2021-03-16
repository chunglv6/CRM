package com.viettel.etc.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viettel.etc.dto.TopupAccountDTO;
import com.viettel.etc.dto.keycloak.ResUserKeycloakDTO;
import com.viettel.etc.dto.keycloak.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface KeycloakService {
    ResponseEntity<?> createUserKeycloak(Authentication authentication, MultipartFile fileImport,String group) throws IOException;

    Object findCTVAccount(TopupAccountDTO params, Authentication authentication);

    List<ResUserKeycloakDTO.GroupUserKeycloak> getUserGroups(String userId, Authentication authentication);

    Object getUser(ReqUserKeycloakDTO reqUserKeycloakDTO, Authentication authentication) throws IOException;

    ResLoginDTO login(ReqLoginDTO itemParamsEntity);

    ResLoginDTO logout(ReqLogoutDTO params) throws IOException;

    void alias(String aliasName, Authentication authentication);

    ResLoginDTO loginBoo(ReqLoginDTO params);

    Object lockUser(String userId, ReqChangePassUserKeycloakDTO params, Authentication authentication, String ip) throws IOException;

    Object resetPassUser(String userId, Authentication authentication) throws IOException;

    Object resetPassUserCPT(ReqResetPassDTO params) throws IOException;

    Object changePassUser(ReqChangePassDTO params, Authentication authentication) throws IOException;

    String getAdminToken();

    String getUserToken(String wsLogin, String pass, String clientSecret, Authentication authentication);
}
