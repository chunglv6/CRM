package com.viettel.etc.dto.keycloak;

import lombok.Data;

@Data
public class ReqUserKeycloakDTO {
    int pageIndex;
    int pageSize;
    String keyword;
}
