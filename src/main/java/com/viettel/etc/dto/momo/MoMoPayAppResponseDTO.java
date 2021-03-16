package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoPayAppResponseDTO {
    Integer status;
    String message;
    Long amount;
    String transid;
    String signature;
}
