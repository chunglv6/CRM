package com.viettel.etc.dto.dmdc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehicleGroupDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String is_active;
}