package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfirmOrderResDTO {
    String message;

    Integer errorCode;

    DataRes data;

    public static class DataRes {
        String order_number;

        String message;
    }
}
