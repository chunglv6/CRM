package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvinceDTO {
    Integer status;
    Boolean error;
    String message;
    List<Province> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Province {
        @JsonProperty(value = "provinceId")
        Long PROVINCE_ID;

        @JsonProperty(value = "provinceCode")
        String PROVINCE_CODE;

        @JsonProperty(value = "provinceName")
        String PROVINCE_NAME;
    }
}
