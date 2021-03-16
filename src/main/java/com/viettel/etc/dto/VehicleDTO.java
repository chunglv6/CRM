package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleDTO {
    Long vehicleId;
    Long custId;
    Long contractId;
    String contractAppendix;
    @NotNull
    String plateNumber;
    @NotNull
    Long vehicleTypeId;
    @NotNull
    Long vehicleGroupId;
    String vehicleGroupName;
    String vehicleGroupDescription;
    @NotNull
    Long seatNumber;
    @NotNull
    Double netWeight;
    Double grossWeight;
    @NotNull
    Double cargoWeight;
    Double pullingWeight;
    String chassicNumber;
    String engineNumber;
    Long vehicleMarkId;
    Long vehicleBrandId;
    Long vehicleColourId;
    @NotNull
    Long plateType;
    String status;
    String activeStatus;
    List<String> statuses;
    List<String> activeStatuses;
    String activeStatusIsNot;
    String epc;
    String tid;
    String rfidSerial;
    String reservedMemory;
    String rfidType;
    Long offerId;
    String offerCode;
    Date effDate;
    Date expDate;
    Long deptId;
    Long shopId;
    String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;
    String profileStatus;
    String approvedUser;
    Date approvedDate;
    String addfilesUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date addfilesDate;
    String owner;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date appendixDate;
    String appendixUsername;
    List<VehicleProfileDTO> vehicleProfileDTOs;
    String vehicleTypeName;
    String vehicleTypeCode;
    Boolean inContract;

    @JsonIgnore
    Integer startrecord = 0;

    @JsonIgnore
    Integer pagesize = 10;

    Boolean resultSqlEx;

    String contractNo;

    String plateTypeCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date regisExpDate;

    String filePath;

    String fileName;

    public VehicleDTO fromEntityToSearchVehicle(VehicleEntity vehicleEntity) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVehicleId(vehicleEntity.getVehicleId());
        vehicleDTO.setVehicleGroupId(vehicleEntity.getVehicleGroupId());
        vehicleDTO.setCargoWeight(vehicleEntity.getCargoWeight());
        vehicleDTO.setPlateNumber(vehicleEntity.getPlateNumber());
        vehicleDTO.setSeatNumber(vehicleEntity.getSeatNumber());
        vehicleDTO.setEpc(vehicleEntity.getEpc());
        vehicleDTO.setStatus(vehicleEntity.getStatus());
        vehicleDTO.setActiveStatus(vehicleEntity.getActiveStatus());
        vehicleDTO.setRfidSerial(vehicleEntity.getRfidSerial());
        vehicleDTO.setVehicleTypeId(vehicleEntity.getVehicleTypeId());
        vehicleDTO.setVehicleTypeCode(vehicleEntity.getVehicleTypeCode());
        vehicleDTO.setVehicleTypeName(vehicleEntity.getVehicleTypeName());
        vehicleDTO.setNetWeight(vehicleEntity.getNetWeight());
        vehicleDTO.setPlateTypeCode(vehicleEntity.getPlateTypeCode());
        vehicleDTO.setContractId(vehicleEntity.getContractId());
        return vehicleDTO;
    }

}
