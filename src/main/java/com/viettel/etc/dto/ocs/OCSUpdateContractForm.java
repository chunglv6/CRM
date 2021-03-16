package com.viettel.etc.dto.ocs;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Pattern;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OCSUpdateContractForm {
    String contractId;
    String contractCode;
    String customerId;
    String customerName;
    String msisdn;
    String emailAddress;
    String birthDay;
    String identify;
    String effDate;
    String expDate;
    String customerType;
    String contractType;
    String customerStatus;
    String contractStatus;
    String langid;
    String chargeType;
    String chargeMethod;
    String accountNumber;
    String accountOwner;
    String token;
    @Pattern(regexp = "[01]")
    String autoTopUpExt;
    String topUpBal;

    public enum ChargeMethod {
        NONE("0"),
        MOMO("1"),
        VIETTELPAY("2"),
        BANK("3");

        public final String value;

        ChargeMethod(String value) {
            this.value = value;
        }
    }
}
