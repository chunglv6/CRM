package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Du lieu dong bo online BOO
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqOnlineEventSyncDTO {
    String plate_old;
    String ticket_type_old;
    String register_vehicle_type_old;
    Long seat_old;
    Double weight_goods_old;
    Double weight_all_old;
    String etag_old;
    String plate_new;
    String etag_new;
    String ticket_type_new;
    String register_vehicle_type_new;
    Long seat_new;
    Double weight_goods_new;
    Double weight_all_new;
    Long request_id;
    Long request_datetime;
    String etag_status_old;
    String etag_status_new;
    String reason;
}
