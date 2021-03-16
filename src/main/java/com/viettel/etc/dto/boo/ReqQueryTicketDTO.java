package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/***
 * Du lieu truyen vao tra cuu ve thang quy tu BOO1
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqQueryTicketDTO {
    String etag;
    String plate;
    Long subscription_ticket_id;
    Long station_in_id;
    Long station_out_id;
    Long request_datetime;
}
