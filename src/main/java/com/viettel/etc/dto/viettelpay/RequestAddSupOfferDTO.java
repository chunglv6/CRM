package com.viettel.etc.dto.viettelpay;

import lombok.Data;

import java.util.List;

@Data
public class RequestAddSupOfferDTO {
    String contractNo;
    Long desContractId;
    Long amount;
    Long quantity;
    String returnUrl;
    Long actTypeId;
    boolean accountETC;
    List<VehicleAddSupOfferViettelPayDTO> list;
}
