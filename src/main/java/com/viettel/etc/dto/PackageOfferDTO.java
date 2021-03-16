package com.viettel.etc.dto;

import lombok.Data;

@Data
public class PackageOfferDTO {
    private Long price;
    private Long vat;
    private Long productOfferingId;
    private Long productOfferPriceId;
    private Long productOfferTypeId;
    private Long newOrSold;
    private Long prodPackOfferId;
}
