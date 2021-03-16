package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResLoginDTO {
    String access_token;

    Long expires_in;

    Long refresh_expires_in;

    String refresh_token;

    String token_type;

    String session_state;

    String scope;

    String error;

    String error_description;

    @JsonIgnore
    Integer code;

    Long isETC = 1L;
}
