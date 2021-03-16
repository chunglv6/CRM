package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/***
 * Du lieu truyen vao huy ve thang quy tu BOO1
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqCancelTicketDTO {
    Long subscription_ticket_id;
    String station_type;
    Long station_in_id;
    Long station_out_id;
    String ticket_type;
    String start_date;
    String end_date;
    String plate;
    String etag;
    Long request_id;
    Long request_datetime;
    Long request_type;
}
