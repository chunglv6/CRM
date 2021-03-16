package com.viettel.etc.dto.im;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.xlibrary.core.entities.MessEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResStockEtagDTO {
    StockEtagDTO data;
    MessEntity mess;
}
