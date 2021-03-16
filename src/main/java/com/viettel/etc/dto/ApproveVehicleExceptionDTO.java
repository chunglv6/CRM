package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApproveVehicleExceptionDTO {

    DataList data;
    Mess mess;

    @Data
    @NoArgsConstructor
    public static class DataList {
        List<Exception> listSuccess = new ArrayList<>();
        List<Exception> listError = new ArrayList<>();
        List<Exception> listWarning = new ArrayList<>();
        String messSuccess;
        String messError;
        String messWarning;
    }

    @Data
    @NoArgsConstructor
    public static class Mess {
        Integer code = 200;
        String description;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Exception {
        Long exceptionId;
        String ocsWarning;
        String boo1Warning;
        String error;
    }
}
