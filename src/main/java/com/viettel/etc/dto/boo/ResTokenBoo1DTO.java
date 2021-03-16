package com.viettel.etc.dto.boo;

import lombok.Data;

@Data
public class ResTokenBoo1DTO {
    String access_token;
    String token_type;
    String refresh_token;
    Long expires_in;
    String scope;
    String username;
    String boo;
    String jti;
}
