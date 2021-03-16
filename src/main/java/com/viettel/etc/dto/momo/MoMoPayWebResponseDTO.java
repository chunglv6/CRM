package com.viettel.etc.dto.momo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class MoMoPayWebResponseDTO {
    @NonNull
    String requestId;
    @NonNull
    String orderId;
    @NonNull
    int errorCode;
    @NonNull
    String message;
    @NonNull
    String localMessage;
    @NonNull
    String requestType;
    @NonNull
    String payUrl;
    @NonNull
    String signature;

    String qrCodeUrl;

    String deeplink;

    String deeplinkWebInApp;

}
