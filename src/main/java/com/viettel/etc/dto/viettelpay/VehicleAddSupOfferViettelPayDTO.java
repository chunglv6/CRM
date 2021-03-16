package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.Data;

import java.sql.Date;

@Data
public class VehicleAddSupOfferViettelPayDTO {
    String plateNumber;
    Long vehicleId;
    Long vehiclesGroupId;
    Long quantity;
    Long price;
    Long stationId;
    Long stageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date effDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date expDate;
    Long laneOut;
    Long chargeMethodId;
    Long servicePlanTypeId;
    String autoRenew;
    String booCode;
    Long stationType;
    Long vehicleTypeId;
    Double netWeight;
    Double cargoWeight;
    Long seatNumber;
    String epc;
}
