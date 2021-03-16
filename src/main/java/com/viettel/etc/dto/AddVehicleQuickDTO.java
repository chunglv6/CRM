package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddVehicleQuickDTO {
    String phoneNumber;
    String plateNumber;
    String serial;
    Long vehicleTypeId;
    Long vehicleGroupId;
    Long seatNumber;
    Double grossWeight;
    Double netWeight;
    Double cargoWeight;
    Double pullingWeight;
    String chassicNumber;
    String engineNumber;
    String fileNameGiayDeNghi;
    String owner;
    String address;
    String vehicleTypeCode;
    String vehicleGroupCode;
    Long amount;
}
