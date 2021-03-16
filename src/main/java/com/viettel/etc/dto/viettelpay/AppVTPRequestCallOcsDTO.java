package com.viettel.etc.dto.viettelpay;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppVTPRequestCallOcsDTO {
    String cmd;
    String merchant_code;
    String order_id;
    String version;
    String check_sum;
}
