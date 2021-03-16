package com.viettel.etc.dto.ocs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ETC360GetChargeReqDTO {

    @NotBlank
    @JsonProperty(value = "EPC")
    String EPC;

    String stationIn;

    String stationOut;

    @NotBlank
    @Pattern(regexp = "[02]")
    String movementType;

    String eventTimeStamp;

    String vehicleGroup;
}
