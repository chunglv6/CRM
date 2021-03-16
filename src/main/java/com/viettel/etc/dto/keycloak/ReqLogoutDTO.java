package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqLogoutDTO {
    @NotNull
    String client_id;

    @NotNull
    String refresh_token;

    String client_secret;

    @Override
    public String toString() {
        String s = "client_id=" + client_id + "&" +
                "refresh_token=" + refresh_token;
        if (client_secret != null) {
            s += "&client_secret=" + client_secret;
        }
        return s;
    }
}
