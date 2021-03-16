package com.viettel.etc.dto;

import lombok.Data;

@Data
public class ModifyVehicleDTO {
    private Long seatNumber;
    private Double grossWeight;
    private Double cargoWeight;
    private Double netWeight;
    private Long vehicleGroupId;
    private Long vehicleTypeId;
    private Double pullingWeight;
    private String chassicNumber;
    private String engineNumber;
    private Long vehicleMarkId;
    private Long vehicleBrandId;
    private Long vehicleColourId;
    private Long plateType;
    private String approvedUser;
    private String owner;
    private Long actTypeId;
    private Long reasonId;
    private String vehicleTypeCode;
    private String vehicleTypeName;
    private String vehicleGroupCode;
    private String plateTypeCode;
}
