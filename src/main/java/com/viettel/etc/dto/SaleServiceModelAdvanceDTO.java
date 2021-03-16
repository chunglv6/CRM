package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaleServiceModelAdvanceDTO {
    private List<PackageOfferDTO> listSaleServiceDetail;
    private ProdPackProductOfferTypeDTO saleServiceModel;
}
