package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleSwapPlateNumberDTO  extends VehicleDTO {
    String newPlateNumber;

    Long actTypeId;

    Long reasonId;

    Long amount;

    List<Long> vehicleProfileIdDelete;
}
