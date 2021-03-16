package com.viettel.etc.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoRawDataDTO extends MoMoBaseDTO {
    String customerNumber; // Số điện thoại khách hàng MoMo
    String appData; // Token nhận được từ app MoMo
}
