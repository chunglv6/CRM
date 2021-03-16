package com.viettel.etc.dto.boo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Du lieu phan hoi khi tra cuu ve thang quy cua BOO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResChargeTicketDTO {
    Long subscription_ticket_id;
    Long price_amount;
    String status;
    String etag;
    Long vehicle_type;
    String register_vehicle_type;
    Long seat;
    Double weight_goods;
    Double weight_all;
    String station_type;
    String ticket_type;
    String start_date;
    String end_date;
    String plate;
    Long station_in_id;
    Long station_out_id;
    Long request_id;
    Long response_datetime;
}
