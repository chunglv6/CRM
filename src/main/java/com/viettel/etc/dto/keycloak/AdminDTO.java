package com.viettel.etc.dto.keycloak;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@Log4j
public class AdminDTO {
    String password;

    String client_id;

    String client_secret;

    String grant_type;

    String username;

    public AdminDTO(String username, String password, String clientId, String secret) {
        this.username = username;
        this.password = password;
        this.client_id = clientId;
        this.client_secret = secret;
        this.grant_type = "password";
    }
    @Override
    public String toString() {
        String s = null;
        try {
            s =  "grant_type=" + grant_type + "&" +
                    "client_id=" + client_id + "&" +
                    "client_secret=" + client_secret + "&" +
                    "username=" + username + "&" +
                    "password=" + URLEncoder.encode(password, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error(e);
        }
        return s;
    }
}
