package com.viettel.etc.dto;

import lombok.Data;

@Data
public class TopupCashResponseDTO {
    String amsSaleTransId;
    String responseCode;
    String saleTransCode;
    String transactionId;
    String description;
    String errorCode;
    String keyMsg;
    boolean success;
}
