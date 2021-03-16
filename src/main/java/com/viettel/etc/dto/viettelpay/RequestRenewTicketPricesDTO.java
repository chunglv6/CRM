package com.viettel.etc.dto.viettelpay;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


/**
 * Du lieu tu dong gia han gia ve
 */

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class RequestRenewTicketPricesDTO {
    String orderId;
    String requestTime;
    String msisdn;
    String token;
    String code;
    String message;
    List<AutoRenewVtpDTO> autoRenew_VTP;
}
