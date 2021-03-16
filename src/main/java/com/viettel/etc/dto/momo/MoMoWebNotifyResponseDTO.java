package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoWebNotifyResponseDTO {
    @NonNull
    String partnerCode;
    @NonNull
    String accessKey;
    @NonNull
    String requestId;
    @NonNull
    String orderId;
    @NonNull
    int errorCode;
    @NonNull
    String message;
    @NonNull
    String responseTime;

    String extraData;
    @NonNull
    String signature;
}
