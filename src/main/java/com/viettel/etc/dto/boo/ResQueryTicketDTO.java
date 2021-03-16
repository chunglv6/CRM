package com.viettel.etc.dto.boo;

import lombok.Data;

import java.util.List;

/**
 * Du lieu phan hoi khi tra cuu ve thang quy cua BOO
 */
@Data
public class ResQueryTicketDTO {
    List<ListTicket> data;
    Long process_datetime;
    Long response_datetime;

    @Data
    public static class ListTicket {
        Long subscription_ticket_id;
        Long price_amount;
        String status;
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
    }
}
