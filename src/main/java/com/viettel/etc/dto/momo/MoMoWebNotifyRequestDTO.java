package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoWebNotifyRequestDTO {
    @NonNull
    String partnerCode;
    @NonNull
    String accessKey;
    @NonNull
    String requestId;
    @NonNull
    Long amount;
    @NonNull
    String orderId;
    @NonNull
    String orderInfo;
    @NonNull
    String orderType;
    @NonNull
    String transId;
    @NonNull
    int errorCode;
    @NonNull
    String message;
    @NonNull
    String localMessage;
    @NonNull
    String payType;
    @NonNull
    String responseTime;
    @NonNull
    String extraData;
    @NonNull
    String signature;
}
