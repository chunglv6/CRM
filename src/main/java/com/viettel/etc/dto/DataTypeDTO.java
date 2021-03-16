package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTypeDTO {
    List<DataType> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataType {
        Long id;
        String code;
        String val;
        String is_default;
    }
}
