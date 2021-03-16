package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResAddSupOfferDTO {
    Long billingCode;
    String vtpCheckSum;
}
