package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@Log4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    String password;

    String client_id;

    String grant_type;

    String username;

    String client_secret;

    public UserDTO(String password, String client_id, String username, String client_secret) {
        this.password = password;
        this.client_id = client_id;
        this.username = username;
        this.grant_type = "password";
        this.client_secret = client_secret;
    }

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
        if (client_secret != null) {
            return s + "&client_secret=" + client_secret;
        } else {
            return s;
        }
    }
}
