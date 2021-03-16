package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoConfirmResponseDTO {
    Integer status;
    String message;
    String signature;
    MoMoBaseDTO data;
}
