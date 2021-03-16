package com.viettel.etc.dto.im;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockTransEtagDTO {
    @NotNull
    Long staffId;

    @NotNull
    Long warehouseId;

    @NotNull
    String serial;

    String epc;

    String tid;

    String status;

    String etagTypeId;
}
