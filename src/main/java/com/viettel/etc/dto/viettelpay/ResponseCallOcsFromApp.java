package com.viettel.etc.dto.viettelpay;

import com.viettel.etc.dto.ResponseChargeTicketCRM;
import lombok.Data;

@Data
public class ResponseCallOcsFromApp extends ResponseChargeTicketCRM {
    Long amountSuccess;
    Long amountFailed;
}
