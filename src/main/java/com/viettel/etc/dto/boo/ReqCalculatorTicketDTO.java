package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/***
 * Du lieu truyen vao tinh gia ve thang quy tu BOO1
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqCalculatorTicketDTO {
    String station_type;
    Long station_in_id;
    Long station_out_id;
    String ticket_type;
    String start_date;
    String end_date;
    String plate;
    String etag;
    Long vehicle_type;
    String register_vehicle_type;
    Long seat;
    Double weight_goods;
    Double weight_all;
    Long request_datetime;
}
