package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoConfirmRequestDTO {
    @NonNull
    String partnerCode;
    @NotNull
    String partnerRefId;
    @NonNull
    String requestType;
    @NotNull
    String momoTransId;
    @NotNull
    String signature;
    @NonNull
    String requestId;
    String customerNumber;
    String description;
}
