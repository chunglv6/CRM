package com.viettel.etc.dto.viettelpay;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Data
public class RequestConfirmChangeMoneySourceDTO extends RequestContractPaymentDTO {
    String accountOwner;
    String bankCodeAfter;
}