package com.viettel.etc.dto.ocs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ETC360ResponseDTO {
    Integer resultCode;

    String description;

    @JsonProperty(value = "EPC")
    String epc;

    Long stationIn;

    Long laneIn;

    Long stationOut;

    Long laneOut;

    Integer movementType;

    Long ticketId;

    Long ticketType;

    Long price;

    Long originalPrice;

    String plateNumber;
    
    Integer plateType;

    Long vehicleType;

    String effDate;

    String expDate;
}
