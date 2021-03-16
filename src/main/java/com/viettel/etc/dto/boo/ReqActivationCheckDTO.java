package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Du lieu yeu cau kich hoat moi etag giua 2 BOO
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqActivationCheckDTO {
    String plate;
    Long request_id;
    Long request_datetime;
}
