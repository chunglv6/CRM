package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Thong tin yeu cau reset mat khau
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqResetPassDTO {
    @NotNull
    String phone;

    @NotNull
    String user;

    @NotNull
    String value;

    @NotNull
    String otp;

}
