package com.viettel.etc.dto.momo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class MoMoPayWebRequestDTO {
    @NonNull
    String partnerCode;
    @NonNull
    String accessKey;
    @NonNull
    String requestId;
    @NonNull
    String amount;
    @NonNull
    String orderId;
    @NonNull
    String orderInfo;
    @NonNull
    String returnUrl;
    @NonNull
    String notifyUrl;
    @NonNull
    String requestType;
    @NonNull
    String signature;

    String extraData;

}
