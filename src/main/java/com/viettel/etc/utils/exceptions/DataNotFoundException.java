package com.viettel.etc.utils.exceptions;

import java.util.function.Supplier;

public class DataNotFoundException extends Exception implements Supplier<DataNotFoundException> {
    Integer code;

    public DataNotFoundException(String message) {
        super(message);
        this.code = 400;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public DataNotFoundException get() {
        return this;
    }
}
