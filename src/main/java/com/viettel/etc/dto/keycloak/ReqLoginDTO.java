package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.FnCommon;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Log4j
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqLoginDTO {
    @NotNull
    String password;

    @NotNull
    String client_id;

    String grant_type;

    @NotNull
    String username;

    String client_secret;

    @Override
    public String toString() {
        String s = null;
        try {
            s = "grant_type=" + grant_type + "&" +
                    "client_id=" + client_id + "&" +
                    "username=" + username + "&" +
                    "password=" + URLEncoder.encode(password, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error(e);
        }
        if (!FnCommon.isNullOrEmpty(client_secret)) {
            s += "&client_secret=" + client_secret;
        }
        return s;
    }
}
