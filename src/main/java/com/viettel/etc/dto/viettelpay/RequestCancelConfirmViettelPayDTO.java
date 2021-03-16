package com.viettel.etc.dto.viettelpay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCancelConfirmViettelPayDTO extends RequestLinkViettelPayDTO{
    @NotNull
    String otp;
    @NotNull
    String originalOrderId;
    @NotNull
    String token;
}
