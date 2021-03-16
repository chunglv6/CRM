package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoNotifyRequestDTO extends MoMoBaseDTO {
    String accessKey;
    String partnerTransId;
    String transType;
    Integer status;
    String message;
    Long responseTime;
    String storeId;
    String signature;
}
