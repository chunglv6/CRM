package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateVehicleProfileDTO {
    @Valid
    List<VehicleProfileDTO> vehicleProfiles;
    @NotNull
    Long contractId;
    @NotNull
    Long custId;

    Long reasonId;

    @NotNull
    Long actTypeId;
}
