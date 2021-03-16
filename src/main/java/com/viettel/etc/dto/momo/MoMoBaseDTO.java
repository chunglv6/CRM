package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoBaseDTO {
    @NonNull
    String partnerCode;
    @NonNull
    String partnerRefId;
    @NonNull
    Long amount;
    String momoTransId;
    public MoMoBaseDTO(@NonNull String partnerCode, @NonNull String partnerRefId, @NonNull Long amount) {
        this.partnerCode = partnerCode;
        this.partnerRefId = partnerRefId;
        this.amount = amount;
    }
}
