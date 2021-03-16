package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleTransDetailVehicleOwnerAppDTO extends SaleTransDetailDTO {
     String plateNumber;
     String vehicleGroupName;
     Long vehicleGroupId;
     String methodCharge;
     String createUser;
}
