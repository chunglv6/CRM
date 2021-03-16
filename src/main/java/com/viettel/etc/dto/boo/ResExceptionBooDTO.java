package com.viettel.etc.dto.boo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResExceptionBooDTO {
    Long timestamp;
    String error_code;
    String error;
    String message;

    public ResExceptionBooDTO(Long timestamp, String message) {
        this.timestamp = timestamp;
        this.error_code = "500";
        this.error = message;
        this.message = message;
    }

    public ResExceptionBooDTO(Long timestamp, String error_code, String error, String message) {
        this.timestamp = timestamp;
        this.error_code = error_code;
        this.error = error;
        this.message = message;
    }
}
