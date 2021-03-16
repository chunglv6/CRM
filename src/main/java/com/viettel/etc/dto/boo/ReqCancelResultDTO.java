package com.viettel.etc.dto.boo;

import lombok.Data;

/***
 * Du lieu truyen vao sau khi BOT confirm
 */
@Data
public class ReqCancelResultDTO {
    Long request_id;
    Long ref_trans_id;
    Long subscription_ticket_id;
    Long process_datetime;
    Long response_datetime;
    String status;
    public Long BOT_confirm;
}
