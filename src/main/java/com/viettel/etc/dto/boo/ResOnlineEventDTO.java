package com.viettel.etc.dto.boo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Du lieu phan hoi khi dong bo du lieu online giua hai BOO
 */
@Data
@NoArgsConstructor
public class ResOnlineEventDTO {
    Long request_id;
    Long process_datetime;
    Long response_datetime;
    String response_code;

    public ResOnlineEventDTO(Long process_datetime, Long response_datetime,
                             Long request_id, String response_code) {
        this.process_datetime = process_datetime;
        this.response_datetime = response_datetime;
        this.request_id = request_id;
        this.response_code = response_code;
    }
}
