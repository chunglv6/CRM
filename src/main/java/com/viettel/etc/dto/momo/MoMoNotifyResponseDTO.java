package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoNotifyResponseDTO {
    Integer status;
    String message;
    String partnerRefId;
    String momoTransId;
    Long amount;
    String signature;
}
