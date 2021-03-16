package com.viettel.etc.dto;

import lombok.Data;


@Data
public class ProductPackageFeeDTO {
    private Long price;
    private Long pricePolicyId;
    private Long vat;
    private String code;
}
