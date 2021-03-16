package com.viettel.etc.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
public class VehicleGroupIdResponseDTO {
    List<VehicleGroupIdResponseDTO.VehicleGroup> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VehicleGroup{
        String name;
        Integer id;
        Long vehicleGroupId;
        String vehicleGroupCode;
    }
}
