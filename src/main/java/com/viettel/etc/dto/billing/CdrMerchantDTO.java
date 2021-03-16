package com.viettel.etc.dto.billing;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CdrMerchantDTO {
    Long transType;
    String transTypeName;
    String chargeMethod;
    String ticketId;
    String extTransId;
    Long contractId;
    Long phoneNo;
    Long price;
    Long balanceBefore;
    Long balanceAfter;
    Long status;
    Long cdrType;
    String requestTimestamp;
    String responseTimestamp;
    String source;
    Long callingMethod;
    String shopId;
    String accountNo;
    String partyCode;
    String ctvUsername;
}
