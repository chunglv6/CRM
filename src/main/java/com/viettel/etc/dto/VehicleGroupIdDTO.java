package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class VehicleGroupIdDTO {
    List<AddVehicleRequestDTO> vehicle;
}
