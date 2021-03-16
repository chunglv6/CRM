package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Service_plan
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SERVICE_PLAN")
public class ServicePlanEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "SERVICE_PLAN_SEQ")
    @SequenceGenerator(name = "SERVICE_PLAN_SEQ", sequenceName = "SERVICE_PLAN_SEQ", allocationSize = 1)
    @Column(name = "SERVICE_PLAN_ID")
    @NotNull
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

    @Column(name = "LANE_IN")
    Long laneIn;

    @Column(name = "LANE_OUT")
    Long laneOut;

    @Column(name = "STATUS")
    Long status;

    @Column(name = "DESTROY_USER")
    String destroyUser;

    @Column(name = "DESTROY_DATE")
    Date destroyDate;

    @Column(name = "APPROVER_USER")
    String approverUser;

    @Column(name = "APPROVER_DATE")
    Date approverDate;

    @Column(name = "NOTE")
    String note;

    @Column(name = "DESTROY_NOTE")
    String destroyNote;

    @Column(name = "APPROVED_STATUS")
    Long approveStatus;

    @Column(name = "DOC_REFER")
    String docRefer;

    @Column(name = "DOC_FILE_PATH")
    String docFilePath;

    @Column(name = "OFFER_TYPE")
    Long offerType;

    @Column(name = "PROMOTION_ID")
    Long promotionId;

    public enum Status {
        DELETED(0L),
        PENDING(1L),
        APPROVAL(2L),
        REJECT(3L),
        CANCEL(4L);
        public final Long value;

        Status(Long value) {
            this.value = value;
        }
    }

    public enum Scope {
        STATION(3L),
        STAGE(4L);
        public final Long value;

        Scope(Long value) {
            this.value = value;
        }
    }
}
