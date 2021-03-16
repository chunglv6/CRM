package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Class mo ta trang thai response cho viettelpay
 */
@Data
@NoArgsConstructor
public class StatusDTO {

    String code;
    String message;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss")
    Date responseTime;

    public StatusDTO(String code, String message, String description, Date responseTime) {
        this.message = message;
        this.code = code;
        this.description = description;
        this.responseTime = responseTime;
    }

    public StatusDTO responseMessage(StatusCode statusCode, String description) {
        return new StatusDTO(statusCode.statusCode, statusCode.message, description, new Date());
    }

    public enum StatusCode {
        NOT_EXIST("204", "common.validate.data.empty"),
        SUCCESS("200", CoreErrorApp.SUCCESS.getDescription()),
        INVALID("201", "viettelpay.paramter.input.invalid"),
        FAIL("212", "crm.internal.server");
        private final String statusCode;
        private final String message;

        StatusCode(String statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }
    }

}
