package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Thong tin yeu cau thay doi mat khau
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqChangePassDTO {
    @NotNull
    String value;

    @NotNull
    String newValue;

    String clientSecret;
}
