package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Sale_order_detail
 *
 * @author ToolGen
 * @date Thu Oct 22 17:42:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDER_DETAIL")
public class SaleOrderDetailEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "SALE_ORDER_DETAIL_SEQ")
    @SequenceGenerator(name = "SALE_ORDER_DETAIL_SEQ", sequenceName = "SALE_ORDER_DETAIL_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "SALE_ORDER_DETAIL_ID")
    Long saleOrderDetailId;

    @Column(name = "SALE_ORDER_ID")
    Long saleOrderId;

    @Column(name = "SALE_ORDER_DATE")
    Date saleOrderDate;

    @Column(name = "SERVICE_FEE_ID")
    Long serviceFeeId;

    @Column(name = "SERVICE_FEE_NAME")
    String serviceFeeName;

    @Column(name = "VEHICLE_ID")
    Long vehicleId;

    @Column(name = "PLATE_NUMBER")
    String plateNumber;

    @Column(name = "EPC")
    String epc;

    @Column(name = "TID")
    String tid;

    @Column(name = "RFID_SERIAL")
    String rfidSerial;

    @Column(name = "VEHICLE_GROUP_ID")
    Long vehicleGroupId;

    @Column(name = "SERVICE_PLAN_ID")
    Long servicePlanId;

    @Column(name = "SERVICE_PLAN_NAME")
    Long servicePlanName;

    @Column(name = "SERVICE_PLAN_TYPE_ID")
    Long servicePlanTypeId;

    @Column(name = "OCS_CODE")
    String ocsCode;

    @Column(name = "SCOPE")
    String scope;

    @Column(name = "OFFER_LEVEL")
    String offerLevel;

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;

    @Column(name = "AUTO_RENEW")
    String autoRenew;

    @Column(name = "AUTO_RENEW_VTP")
    String autoRenewVtp;

    @Column(name = "STATION_ID")
    Long stationId;

    @Column(name = "STAGE_ID")
    Long stageId;

    @Column(name = "PRICE")
    Long price;

    @Column(name = "QUANTITY")
    Long quantity;

    @Column(name = "DISCOUNT_ID")
    Long discountId;

    @Column(name = "AMOUNT")
    Long amount;

    @Column(name = "PROMOTION_ID")
    Long promotionId;

    @Column(name = "PROMOTION_AMOUNT")
    Long promotionAmount;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "BOO_CODE")
    String booCode;

}