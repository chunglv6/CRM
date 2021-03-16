package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Du lieu dong bo online BOO
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqOnlineEventRegDTO {
    String plate;
    String etag;
    Long request_id;
    Long request_datetime;
    Long station_in_id;
    Long station_out_id;
    String type;
    String vehicle_type;
    String exception_vehicle_type;
    Long price_turn;
    Long price_monthly;
    Long price_quarterly;
    String start_date;
    String end_date;
    Long status;
    String action_type;
}
