package com.viettel.etc.repositories.tables.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Sale_trans_detail
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SALE_TRANS_DETAIL")
public class SaleTransDetailEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "SALE_TRANS_DETAIL_SEQ")
    @SequenceGenerator(name = "SALE_TRANS_DETAIL_SEQ", sequenceName = "SALE_TRANS_DETAIL_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "SALE_TRANS_DETAIL_ID")
    Long saleTransDetailId;

    @Column(name = "SALE_TRANS_ID")
    Long saleTransId;

    @Column(name = "SALE_TRANS_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date saleTransDate;

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
    String servicePlanName;

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

    @Column(name = "STATUS")
    Long status;

    @Column(name = "ORG_PLATE_NUMBER")
    String orgPlateNumber;

    @Column(name = "BOO_CODE")
    String booCode;

    public enum Status {
        UN_PAID(1L),
        PAID_NOT_INVOICED(2L),
        BILLED(3L),
        CANCEL(4L);

        public final Long value;

        Status(Long value) {
            this.value = value;
        }
    }


    @Column(name = "LANE_IN")
    Long lane_in;


    @Column(name = "LANE_OUT")
    Long lane_out;


    @Column(name = "AUTO_RENEW_VTP")
    Long auto_renew_vtp;


    @Column(name = "DESTROY_USER")
    String destroy_user;


    @Column(name = "DESTROY_DATE")
    Date destroy_date;


    @Column(name = "ACT_TYPE_ID")
    Long act_type_id;


    @Column(name = "ACT_REASON_ID")
    Long act_reason_id;


    @Column(name = "REFUND_TYPE")
    Long refund_type;

    @Column(name = "RESPONSE_DATETIME")
    Long responseDateTime;

    @Column(name = "SUBSCRIPTION_TICKET_ID")
    Long subscriptionTicketId;

    @Column(name = "BOO_FLOW")
    Long booFlow;
}
