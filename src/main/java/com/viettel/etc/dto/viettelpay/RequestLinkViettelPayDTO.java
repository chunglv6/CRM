package com.viettel.etc.dto.viettelpay;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestLinkViettelPayDTO {
    @NotNull
    String orderId;
    @NotNull
    Long contractId;
    @NotNull
    String msisdn;
    @NotNull
    Long actionTypeId;
}
