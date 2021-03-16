package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PUBLIC)
public class RequestConfirmRegisterDTO extends RequestContractPaymentDTO {
    String accountOwner;
    String bankCode;
}
