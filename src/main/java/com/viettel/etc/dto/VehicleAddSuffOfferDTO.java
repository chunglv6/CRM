package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransDetailEntity;
import com.viettel.etc.utils.Constants;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VehicleAddSuffOfferDTO {
    // DTO to map SaleTransDetail

    private Long serviceFeeId;

    private Long contractId;

    private String serviceFeeName;

    private Long vehicleId;

    private String plateNumber;

    private String epc;

    private String tid;

    private String rfidSerial;

    private Long vehiclesGroupId;

    private Long servicePlanId;

    private String servicePlanName;

    private Long servicePlanTypeId;

    private String ocsCode;

    private String scope;

    private String offerLevel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private java.util.Date effDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private java.util.Date expDate;

    private String autoRenew;

    private Long stationId;

    private Long stageId;
    @NotNull
    private Long price;
    @NotNull
    private Long quantity;

    private Long discountId;

    private Long amount;

    private Long promotionId;

    private Long promotionAmount;

    private String description;

    private String offerId;

    private String stationLevel;

    private String laneId;

    private String isOCSCharged;

    private String chargedVehicleType;

    Long laneOut;

    Long chargeMethodId;

    private String booCode;

    private Long stationType;

    private Long vehicleTypeId;

    private Double netWeight;

    private Double cargoWeight;

    private Long seatNumber;

    private String stationOrStageName;

    private String isGenCdr;


    public SaleTransDetailEntity toAddSaleTransDetailEntity(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, java.sql.Date currDate, String userLogin, Long price, long quantity, long saleTransId) {
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setServiceFeeName(serviceFeeName);
        saleTransDetailEntity.setVehicleId(vehicleId);
        saleTransDetailEntity.setPlateNumber(plateNumber);
        // Lay tu BCCS IM
        saleTransDetailEntity.setServiceFeeId(serviceFeeId);
        saleTransDetailEntity.setEpc(vehicleAddSuffOfferDTO.getEpc());
        saleTransDetailEntity.setTid(vehicleAddSuffOfferDTO.getTid());
        saleTransDetailEntity.setRfidSerial(vehicleAddSuffOfferDTO.getRfidSerial());
        saleTransDetailEntity.setVehicleGroupId(vehiclesGroupId);
        saleTransDetailEntity.setServicePlanId(servicePlanId);
        saleTransDetailEntity.setServicePlanName(servicePlanName);
        saleTransDetailEntity.setServicePlanTypeId(servicePlanTypeId);
        saleTransDetailEntity.setOcsCode(ocsCode);
        saleTransDetailEntity.setOfferLevel(Constants.OFFER_LEVEL_DEFAULT.toString());
        saleTransDetailEntity.setStageId(stageId);
        saleTransDetailEntity.setPrice(price);
        saleTransDetailEntity.setDiscountId(discountId);
        saleTransDetailEntity.setAmount(amount);
        saleTransDetailEntity.setPromotionId(promotionId);
        saleTransDetailEntity.setPromotionAmount(promotionAmount);
        saleTransDetailEntity.setDescription(description);
        saleTransDetailEntity.setSaleTransId(saleTransId);
        saleTransDetailEntity.setCreateDate(currDate);
        saleTransDetailEntity.setCreateUser(userLogin);
        if (vehicleAddSuffOfferDTO.getEffDate() != null) {
            saleTransDetailEntity.setEffDate(new java.sql.Date(vehicleAddSuffOfferDTO.getEffDate().getTime()));
        }
        if (vehicleAddSuffOfferDTO.getExpDate() != null) {
            saleTransDetailEntity.setExpDate(new java.sql.Date(vehicleAddSuffOfferDTO.getExpDate().getTime()));
        }
        saleTransDetailEntity.setSaleTransDate(currDate);
        saleTransDetailEntity.setPrice(price);
        saleTransDetailEntity.setQuantity(quantity);
        saleTransDetailEntity.setCreateUser(userLogin);
        saleTransDetailEntity.setCreateDate(currDate);
        saleTransDetailEntity.setSaleTransId(saleTransId);
        saleTransDetailEntity.setStationId(stationId);


        return saleTransDetailEntity;
    }


    public VehicleAddSuffOfferDTO toEntityChangeSupOffer(SaleOrderDetailEntity saleOrderDetailEntity) {
        VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSuffOfferDTO.setEpc(saleOrderDetailEntity.getEpc());
        vehicleAddSuffOfferDTO.setEffDate(saleOrderDetailEntity.getEffDate());
        vehicleAddSuffOfferDTO.setExpDate(saleOrderDetailEntity.getExpDate());
        vehicleAddSuffOfferDTO.setOfferId(saleOrderDetailEntity.getOcsCode());
        vehicleAddSuffOfferDTO.setOfferLevel(saleOrderDetailEntity.getOfferLevel());
        return vehicleAddSuffOfferDTO;
    }
}
