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
public class WardsDTO {
    Integer status;
    Boolean error;
    String message;
    List<Wards> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Wards {
        @JsonProperty(value = "districtId")
        Long DISTRICT_ID;

        @JsonProperty(value = "wardsId")
        Long WARDS_ID;

        @JsonProperty(value = "wardsName")
        String WARDS_NAME;
    }
}
