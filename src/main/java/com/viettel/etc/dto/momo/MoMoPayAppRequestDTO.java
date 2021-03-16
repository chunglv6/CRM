package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoPayAppRequestDTO {
    String partnerCode;
    String partnerRefId;
    String customerNumber;
    String appData;
    String hash;
    Double version;
    Integer payType;
    String description;
}
