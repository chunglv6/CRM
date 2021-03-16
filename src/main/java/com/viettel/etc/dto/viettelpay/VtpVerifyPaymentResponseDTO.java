package com.viettel.etc.dto.viettelpay;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VtpVerifyPaymentResponseDTO {
    String merchant_code;
    String order_id;
    String error_code;
    String vt_transaction_id;
    String payment_status;
    String version;
    String check_sum;
}

