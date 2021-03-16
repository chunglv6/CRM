package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Autogen class DTO: Tra cuu thong tin phuong tien
 *
 * @author ToolGen
 * @date Thu Jun 25 08:26:59 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class VehicleByContractDTO extends VehicleDTO {

    String nameType;

    String nameGroup;

    Integer salesType;

    String color;

    String mark;

    String brand;

    String startDate;

    String endDate;

    String searchType;

    String typeScreen;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date appendixDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date addfilesDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;

    Boolean resultSqlEx;

    Long fee;

    Long servicePlanId;
    
    String custName;

    String plateTypeName;

    Long vehicleImportType;

    String plateTypeCode;

    String plateNumber;

    String EPC;

    String promotionCode;

}
