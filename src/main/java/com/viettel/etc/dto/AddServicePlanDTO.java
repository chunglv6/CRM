package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.utils.Constants;
import lombok.Data;

import java.sql.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AddServicePlanDTO {
    String servicePlanCode;
    Long servicePlanTypeID;
    Long scope;
    Long stationId;
    Long stageId;
    Long laneIn;
    Long laneOut;
    Long vehicleGroupId;
    Long fee;
    String ocsCode;
    Long routeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date endDate;

    Long autoRenew;
    Long useDay;
    String servicePlanTypeName;
    String scopeName;
    String stationName;
    String stageName;
    String laneInName;
    String laneOutName;
    String vehicleGroupName;
    Long stationType;
    Long chargeMethodId;
    Long stationInId;
    Long stationOutId;

    public ServicePlanEntity convertToServicePlanEntity(String customer) {
        ServicePlanEntity servicePlanEntity = new ServicePlanEntity();
        servicePlanEntity.setStatus(ServicePlanEntity.Status.PENDING.value);
        servicePlanEntity.setServicePlanCode(servicePlanCode);
        servicePlanEntity.setServicePlanTypeId(servicePlanTypeID);
        servicePlanEntity.setServicePlanName(servicePlanTypeName);
        servicePlanEntity.setScope(scope);
        servicePlanEntity.setStationId(stationId);
        servicePlanEntity.setStageId(stageId);
        servicePlanEntity.setLaneIn(laneIn);
        servicePlanEntity.setLaneOut(laneOut);
        servicePlanEntity.setVehicleGroupId(vehicleGroupId);
        servicePlanEntity.setFee(fee);
        servicePlanEntity.setStatus(1L);
        servicePlanEntity.setOcsCode(ocsCode);
        servicePlanEntity.setStartDate(startDate);
        servicePlanEntity.setEndDate(endDate);
        servicePlanEntity.setAutoRenew(autoRenew);
        servicePlanEntity.setUseDay(useDay);
        servicePlanEntity.setCreateUser(customer);
        servicePlanEntity.setChargeMethodId(chargeMethodId);
        servicePlanEntity.setStationType(stationType);
        servicePlanEntity.setCreateDate(new Date(new java.util.Date().getTime()));
        servicePlanEntity.setOfferlevel(2L);
        servicePlanEntity.setStationInId(stationInId);
        servicePlanEntity.setStationOutId(stationOutId);
        servicePlanEntity.setRouteId(routeId);
        return servicePlanEntity;

    }

}
