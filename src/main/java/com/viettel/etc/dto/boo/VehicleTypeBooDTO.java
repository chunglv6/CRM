package com.viettel.etc.dto.boo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeBooDTO {
    VehicleType data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleType {
        String name;
        String code;
        Long id;
    }
}
