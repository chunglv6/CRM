package com.viettel.etc.dto.viettelpay;

import lombok.Data;

@Data
public class ViettelPayRequestDTO {
    String billcode;
    String cust_msisdn;
    String error_code;
    String merchant_code;
    String order_id;
    Long payment_status;
    Long trans_amount;
    String vt_transaction_id;
    String check_sum;
}
