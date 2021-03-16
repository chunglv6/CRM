package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopupDTO {
    @NotNull
    String address;
    @NotNull
    String customerName;
    @NotNull
    String stockModelCode;
    @NotNull
    Long price;
    @NotNull
    Long vat;
    @NotNull
    Long quantity;
    @NotNull
    String transType;
    @NotNull
    String saleTransType;
    @NotNull
    Long contractId;
    @NotNull
    Long balanceBefore;
}
