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
public class DistrictDTO {
    Integer status;
    Boolean error;
    String message;
    List<District> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class District {
        @JsonProperty(value = "provinceId")
        Long PROVINCE_ID;

        @JsonProperty(value = "districtId")
        Long DISTRICT_ID;

        @JsonProperty(value = "districtValue")
        String DISTRICT_VALUE;

        @JsonProperty(value = "districtName")
        String DISTRICT_NAME;
    }
}
