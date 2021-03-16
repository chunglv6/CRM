package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Service_plan_his
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SERVICE_PLAN_HIS")
public class ServicePlanHisEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "SERVICE_PLAN_HIS_SEQ")
    @SequenceGenerator(name = "SERVICE_PLAN_HIS_SEQ", sequenceName = "SERVICE_PLAN_HIS_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "SERVICE_PLAN_HIS_ID")
    Long servicePlanHisId;

    @Column(name = "SERVICE_PLAN_ID")
    Long servicePlanId;

    @Column(name = "SERVICE_PLAN_CODE")
    String servicePlanCode;

    @Column(name = "SERVICE_PLAN_NAME")
    String servicePlanName;

    @Column(name = "SERVICE_PLAN_TYPE_ID")
    Long servicePlanTypeId;

    @Column(name = "VEHICLE_PRIORITIZE_ID")
    Long vehiclePrioritizeId;

    @Column(name = "SERVICE_TYPE")
    Long serviceType;

    @Column(name = "OFFERLEVEL")
    Long offerlevel;

    @Column(name = "OCS_CODE")
    String ocsCode;

    @Column(name = "VEHICLE_GROUP_ID")
    Long vehicleGroupId;

    @Column(name = "ROUTE_ID")
    Long routeId;

    @Column(name = "STATION_TYPE")
    Long stationType;

    @Column(name = "STATION_ID")
    Long stationId;

    @Column(name = "STAGE_ID")
    Long stageId;

    @Column(name = "STATION_IN_ID")
    Long stationInId;

    @Column(name = "STATION_OUT_ID")
    Long stationOutId;

    @Column(name = "CHARGE_METHOD_ID")
    Long chargeMethodId;

    @Column(name = "AUTO_RENEW")
    Long autoRenew;

    @Column(name = "IS_OCS_CHARGED")
    Long isOcsCharged;

    @Column(name = "FEE")
    Long fee;

    @Column(name = "USE_DAY")
    Long useDay;

    @Column(name = "SCOPE")
    Long scope;

    @Column(name = "START_DATE")
    Date startDate;

    @Column(name = "END_DATE")
    Date endDate;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "DESCRIPTION")
    String description;
}
