package com.viettel.etc.dto.boo;

import lombok.Data;

/**
 * Du lieu phan hoi khi Boo goi huy ve thang quy
 */
@Data
public class ResCancelTicketDTO {
    Long request_id;
    Long subscription_ticket_id;
    Long process_datetime;
    Long response_datetime;
    String status;
    Long request_type;
}
