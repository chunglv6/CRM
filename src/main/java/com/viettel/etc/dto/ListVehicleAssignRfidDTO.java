package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListVehicleAssignRfidDTO {
    List<AddVehicleRequestDTO> addVehicleRequestDTOS;
    String startRfidSerial;
    Long actTypeId;

}
