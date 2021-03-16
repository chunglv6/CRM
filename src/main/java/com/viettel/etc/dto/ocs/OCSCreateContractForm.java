package com.viettel.etc.dto.ocs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class OCSCreateContractForm {
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
}
