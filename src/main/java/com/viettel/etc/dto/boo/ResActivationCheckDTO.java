package com.viettel.etc.dto.boo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Du lieu phan hoi kich hoat moi etag giua 2 BOO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResActivationCheckDTO {
    Long request_id;
    String response_code;
    Long process_datetime;
    Long response_datetime;
    String etag;
    String plate;
    String status;
    Long vehicle_type;
    String register_vehicle_type;
    Long seat;
    Double weight_goods;
    Double weight_all;
}
