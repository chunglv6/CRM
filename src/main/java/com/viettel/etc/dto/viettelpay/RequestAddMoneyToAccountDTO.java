package com.viettel.etc.dto.viettelpay;

import lombok.Data;

@Data
public class RequestAddMoneyToAccountDTO extends RequestContractPaymentDTO {
    String requestId;
    Long amount;
    String orderInfo;
    long saleOrderDate;
    String errorCode;
    String message;
    String extraData;
    String status;
}
