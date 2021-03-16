package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseGetInfoRegisterDTO {
    private Integer code;
    private String plateNumber;
    private String vehicleTypeId;
    private String chassicNumber;
    private String seatNumber;
    private String cargoWeight;
    private String netWeight;
    private String pullingWeight;
    private String grossWeight;
    private String owner;
    private String address;
    private String descriptions;
    Long vehicleGroupId;
    String vehicleGroupCode;
}
