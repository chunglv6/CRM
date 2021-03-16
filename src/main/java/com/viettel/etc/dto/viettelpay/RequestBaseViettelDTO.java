package com.viettel.etc.dto.viettelpay;

import lombok.Data;
import lombok.NonNull;

@Data
public class RequestBaseViettelDTO {
    private String orderId;
    Long contractId;
    String searchType;
    String plateType;
    String plateNumber;
    String contractNo;
}
