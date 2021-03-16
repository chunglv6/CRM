package com.viettel.etc.dto.ocs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ETC360CheckInReqDTO {
    @NotBlank
    @JsonProperty(value = "EPC")
    String EPC;

    String requestId;

    String stationIn;

    String laneIn;

    String stationOut;

    String laneOut;

    String plate;

    @JsonProperty(value = "TID")
    String TID;

    @NotBlank
    @Pattern(regexp = "[02]")
    String movementType;

    String isFreeCharge;

    String hashValue;
}
