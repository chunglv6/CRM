package com.viettel.etc.dto.viettelpay;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class RequestContractPaymentDTO {
    String orderId;
    String requestTime;
    String msisdn;
    String token;
    Long contractId;
    Long channel;
    /**
     * Loai giay to cua khach hang
     * ex: Chung minh nhan dan
     */
//    String idType;
    /**
     * So giay to cua khach hang
     */
//    String idCard;
}


