package com.viettel.etc.utils.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.function.Supplier;

public class BooException extends RuntimeException implements Supplier<BooException> {
    Long timestamp;
    String error_code;
    String error;
    @JsonIgnore
    Integer httpCode;

    @Override
    public BooException get() {
        return this;
    }

    public BooException(String error, String message, Integer httpCode) {
        super(message);
        this.error = error;
        this.timestamp = Instant.now().getEpochSecond();
        this.httpCode = httpCode;
    }

    public BooException(String message) {
        super(message);
        this.error = message;
        this.timestamp = Instant.now().getEpochSecond();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getError_code() {
        return error_code;
    }

    public String getError() {
        return error;
    }

    public Integer getHttpCode() {
        return httpCode;
    }
}
