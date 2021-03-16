package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResKeycloakDTO {
    Integer code;

    String message;

    String error;

    String access_token;
}
