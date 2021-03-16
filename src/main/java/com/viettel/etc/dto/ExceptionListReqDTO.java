package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionListReqDTO {

    @NotBlank
    String exceptionType;

    @NotBlank
    @Pattern(regexp = "[0-9A-Z]+")
    String plateNumber;

    String plateType;

    String epc;

    String tid;

    String rfidSerial;

    String vehicleType;

    String exceptionTicketType;

    String exceptionVehicleType;

    Long registerVehicleType;

    Long groupVehicle;

    String description;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date effDate;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date expDate;

    String customerName;

    String contractNo;

    String contractId;

    Long customerId;

    Long whiteListType;

    Long blackListType;

    String ocsCode;

    Long vehicleId;

    Long promotionId;

    Long priceTurn;

    Long priceMonthly;

    Long priceQuarterly;

    String orgPlateNumber;

    @NotEmpty
    List<StationStage> stationStages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StationStage {
        Long stationId;
        String stationName;
        Long stagesId;
        String stageName;
    }
}
