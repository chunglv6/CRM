package com.viettel.etc.dto.momo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class MoMoAppRequestPaymentDTO {
    String requestId;
    String contractNo;
    String contractId;
    Long amount;
    String requestTime;
    String msisdn;
    String extraData;
}
