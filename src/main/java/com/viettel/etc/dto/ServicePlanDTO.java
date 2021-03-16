package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

/**
 * Autogen class DTO: Lop thao tac danh sach ca nhan
 * 
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ServicePlanDTO {

    Long servicePlanId;
    @NotNull
    String servicePlanCode;

    String servicePlanName;
    @NotNull
    Long servicePlanTypeId;

    @NotNull
    String ocsCode;

    @NotNull
    Long vehicleGroupId;

    Long routeId;

    Long stationType;

    String description;

    Long stationId;

    Long stageId;

    Long stationInId;

    Long stationOutId;

    Long chargeMethodId;

    @NotNull
    Long autoRenew;

    @NotNull
    Long fee;

    Long useDay;

    @NotNull
    Long scope;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date endDate;

    String createUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN)
    Date createDate;

    Long laneIn;

    Long laneOut;

    Long status;

    Integer startrecord;

    Integer pagesize;

    String createDateFrom;

    String createDateTo;

    String startDateFrom;

    String endDateTo;

    String approveDateFrom;

    String approveDateTo;

    Long effId;

    String effDate;

    String stationTypeId;

    List<Long> servicePlanIdList;

    String stationIdList;

    String vehicleGroupIdArr;

    String stageIdList;

    String plateNumber;

    List<BotRevenueShareDTO> botRevenueShareList;

    List<AttachmentFileDTO> attachmentFileDTOS;

    String note;

    Long vehicleTypeId;

    Double netWeight;

    Double cargoWeight;

    Long seatNumber;

    String epc;

    Long promotionId;

    String booCode;

    String expDate;

    Long vehicleId;

    String reasons;

    String stationOrStageName;

    public void updateValueTicketPrices(ServicePlanEntity servicePlanEntity){
        servicePlanEntity.setStatus(ServicePlanEntity.Status.PENDING.value);
        servicePlanEntity.setScope(scope);
        servicePlanEntity.setVehicleGroupId(vehicleGroupId);
        servicePlanEntity.setStageId(stageId);
        servicePlanEntity.setStationId(stationId);
        servicePlanEntity.setLaneIn(laneIn);
        servicePlanEntity.setLaneOut(laneOut);
        servicePlanEntity.setFee(fee);
        servicePlanEntity.setOcsCode(ocsCode);
        servicePlanEntity.setStartDate(startDate);
        servicePlanEntity.setEndDate(endDate);
        servicePlanEntity.setDescription(description);
        servicePlanEntity.setAutoRenew(autoRenew);
        servicePlanEntity.setUseDay(useDay);
        servicePlanEntity.setServicePlanTypeId(servicePlanTypeId);
        servicePlanEntity.setServicePlanCode(servicePlanCode);
        servicePlanEntity.setStationType(stationType);
        servicePlanEntity.setChargeMethodId(chargeMethodId);
        servicePlanEntity.setRouteId(routeId);
        servicePlanEntity.setStationInId(stationInId);
        servicePlanEntity.setStationOutId(stationOutId);
    }
}
