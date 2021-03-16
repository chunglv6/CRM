package com.viettel.etc.dto;

import lombok.Data;

@Data
public class AssignRfidDTO {
    private Long vehicleId;
    private String rfidSerial;
    private String descriptions;
}
