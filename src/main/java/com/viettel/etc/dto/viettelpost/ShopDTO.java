package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopDTO {
    Integer status;
    Boolean error;
    String message;
    List<Shop> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Shop {
        Long groupaddressId;
        Long cusId;
        String name;
        String phone;
        String address;
        Long provinceId;
        Long districtId;
        Long wardsId;
    }
}
