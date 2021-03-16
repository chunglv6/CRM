package com.viettel.etc.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.dto.im.StockEtagDTO;
import com.viettel.etc.dto.viettelpost.ConfirmOrderDTO;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Autogen class DTO: Dau noi phuong tien sang OCS
 *
 * @author ToolGen
 * @date Thu Jun 11 08:35:38 ICT 2020
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddVehicleRequestDTO extends ConfirmOrderDTO {
    Long vehicleId;
    Long custId;
    Long contractId;
    String contractAppendix;
    String plateNumber;
    Long vehicleTypeId;
    Long vehicleGroupId;
    Long seatNumber;
    Double grossWeight;
    Double netWeight;
    Double cargoWeight;
    Double pullingWeight;
    String chassicNumber;
    String engineNumber;
    Long vehicleMarkId;
    Long vehicleBrandId;
    Long vehicleColourId;
    Long plateType;
    String status;
    String activeStatus;
    String epc;
    String tid;
    String rfidSerial;
    String reservedMemory;
    String rfidType;
    Long offerId;
    String offerCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date effDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date expDate;
    Long deptId;
    Long shopId;
    String createUser;
    String contractNo;
    @NotNull
    String type;
    @NotNull
    String offerExternalId;
    String vehicleType;
    Long actTypeId;
    String vehicleColor;
    String plateColor;
    String vehicleMark;
    String plate;
    Long plateTypeId;
    String vehicleBrand;
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
    Long reasonId;
    Integer id;
    String vehicleGroupCode;
    String plateTypeCode;
    String vehicleTypeCode;
    String vehicleTypeName;
    Long stationId;
    String promotionCode;
    String fileNameGiayDeNghi;

    public VehicleEntity toAddVehicleEntity(Long customerId, Long contractId, String userLogin, StockEtagDTO stockEtagDTO) {
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setContractId(contractId);
        vehicleEntity.setCustId(customerId);
        vehicleEntity.setContractAppendix(getContractAppendix());
        vehicleEntity.setPlateNumber(plateNumber.toUpperCase());
        vehicleEntity.setVehicleTypeId(vehicleTypeId);
        vehicleEntity.setVehicleGroupId(vehicleGroupId);
        vehicleEntity.setSeatNumber(seatNumber);
        vehicleEntity.setNetWeight(netWeight);
        vehicleEntity.setGrossWeight(grossWeight);
        vehicleEntity.setCargoWeight(cargoWeight);
        vehicleEntity.setPullingWeight(pullingWeight);
        vehicleEntity.setChassicNumber(chassicNumber);
        vehicleEntity.setEngineNumber(engineNumber);
        vehicleEntity.setVehicleMarkId(vehicleMarkId);
        vehicleEntity.setVehicleBrandId(vehicleBrandId);
        vehicleEntity.setVehicleColourId(vehicleColourId);
        vehicleEntity.setPlateType(plateTypeId);
        vehicleEntity.setStatus(status);
        vehicleEntity.setActiveStatus(activeStatus);
        if (stockEtagDTO != null) {
            vehicleEntity.setEpc(stockEtagDTO.getEpc());
            vehicleEntity.setTid(stockEtagDTO.getTid());
        }
        vehicleEntity.setRfidSerial(rfidSerial);
        vehicleEntity.setReservedMemory(reservedMemory);
        vehicleEntity.setRfidType(rfidType);
        vehicleEntity.setOfferId(offerId);
        vehicleEntity.setOfferCode(offerCode);
        vehicleEntity.setEffDate(new java.sql.Date(System.currentTimeMillis()));
        Date exfDate = FnCommon.addYears(new Date(), 50);
        vehicleEntity.setExpDate(new java.sql.Date(exfDate.getTime()));
        vehicleEntity.setDeptId(deptId);
        vehicleEntity.setShopId(shopId);
        vehicleEntity.setCreateUser(userLogin);
        vehicleEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleEntity.setProfileStatus(profileStatus);
        vehicleEntity.setApprovedUser(approvedUser);
        vehicleEntity.setApprovedDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleEntity.setAddfilesUser(addfilesUser);
        if (getAddfilesDate() != null) {
            vehicleEntity.setAddfilesDate(new java.sql.Date(getAddfilesDate().getTime()));
        }
        vehicleEntity.setOwner(owner);
        if (getAddfilesDate() != null) {
            vehicleEntity.setAppendixDate(new java.sql.Date(getAddfilesDate().getTime()));
        }
        vehicleEntity.setAppendixUsername(appendixUsername);
        if (appendixDate != null) {
            vehicleEntity.setAppendixDate(new java.sql.Date(appendixDate.getTime()));
        }
        vehicleEntity.setActiveStatus(activeStatus);
        vehicleEntity.setProfileStatus(profileStatus);
        vehicleEntity.setVehicleGroupCode(vehicleGroupCode);
        vehicleEntity.setPlateTypeCode(plateTypeCode);
        vehicleEntity.setVehicleTypeName(vehicleTypeName);
        vehicleEntity.setVehicleTypeCode(vehicleTypeCode);
        vehicleEntity.setPromotionCode(promotionCode);
        return vehicleEntity;
    }


    public AddVehicleRequestDTO entityToAddVehicleRequestDTO(VehicleEntity vehicleEntity, Long contractId) {
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(contractId);
        addVehicleRequestDTO.setTid(vehicleEntity.getTid());
        addVehicleRequestDTO.setEpc(vehicleEntity.getEpc());
        addVehicleRequestDTO.setPlateNumber(vehicleEntity.getPlateNumber());
        addVehicleRequestDTO.setEffDate(vehicleEntity.getEffDate());
        addVehicleRequestDTO.setExpDate(vehicleEntity.getExpDate());
        addVehicleRequestDTO.setVehicleTypeId(vehicleEntity.getVehicleTypeId());
        addVehicleRequestDTO.setStatus(vehicleEntity.getStatus());
        addVehicleRequestDTO.setOfferExternalId("430");
        addVehicleRequestDTO.setPlateType(vehicleEntity.getPlateType());
        addVehicleRequestDTO.setRfidSerial(vehicleEntity.getRfidSerial());
        addVehicleRequestDTO.setRfidType(vehicleEntity.getRfidType());
        addVehicleRequestDTO.setActiveStatus(vehicleEntity.getActiveStatus());
        addVehicleRequestDTO.setVehicleGroupId(vehicleEntity.getVehicleGroupId());
        addVehicleRequestDTO.setPlateTypeCode(vehicleEntity.getPlateTypeCode());
        addVehicleRequestDTO.setVehicleGroupCode(vehicleEntity.getVehicleTypeCode());
        addVehicleRequestDTO.setVehicleTypeName(vehicleEntity.getVehicleTypeName());
        addVehicleRequestDTO.setVehicleTypeName(vehicleEntity.getPlateTypeCode());
        addVehicleRequestDTO.setSeatNumber(vehicleEntity.getSeatNumber());
        if(vehicleEntity.getCargoWeight() != null) {
            addVehicleRequestDTO.setCargoWeight(vehicleEntity.getCargoWeight());
        }
        return addVehicleRequestDTO;
    }
}
