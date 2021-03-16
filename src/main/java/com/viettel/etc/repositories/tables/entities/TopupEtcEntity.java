package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Topup_etc
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TOPUP_ETC")
public class TopupEtcEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "TOPUP_ETC_SEQ")
    @SequenceGenerator(name = "TOPUP_ETC_SEQ", sequenceName = "TOPUP_ETC_SEQ", allocationSize = 1)
    @Column(name = "TOPUP_ETC_ID")
    Long topupEtcId;

    @Column(name = "TOPUP_CODE")
    String topupCode;

    @Column(name = "TOPUP_DATE")
    Date topupDate;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "AMOUNT")
    Long amount;

    @Column(name = "BALANCE_BEFORE")
    Long balanceBefore;

    @Column(name = "BALANCE_AFTER")
    Long balanceAfter;

    @Column(name = "TOPUP_TYPE")
    Long topupType;

    @Column(name = "SHOP_ID")
    Long shopId;

    @Column(name = "SHOP_NAME")
    String shopName;

    @Column(name = "STAFF_CODE")
    String staffCode;

    @Column(name = "STAFF_NAME")
    String staffName;

    @Column(name = "STATUS")
    String status;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "TOPUP_PAYER")
    String topupPayer;

    @Column(name = "VOLUME_NO")
    String volumeNo;

    @Column(name = "NO")
    Long no;

    @Column(name = "DISCOUNT")
    Long discount;

    @Column(name = "TOPUP_CHANNEL")
    Long topupChannel;

    @Column(name = "REPONSE_TIME")
    Long responseTime;

    @Column(name = "PARTNER_TRANSID")
    String partnerTransId;

    @Column(name = "FEE")
    Long fee;

    @Column(name = "MSISDN")
    String msisdn;

    @Lob
    @Column(name = "SIGNATURE")
    byte[] signature;

    @Column(name = "SOURCE_CONTRACT_ID")
    Long sourceContractId;

    @Column(name = "SOURCE_CONTRACT_NO")
    String sourceContractNo;

    @Column(name = "SALE_ORDER_ID")
    Long saleOrderId;

    @Column(name = "SALE_ORDER_DATE")
    Date saleOrderDate;

    @Column(name = "TRANS_ID")
    String transId;

    public enum TopupType {
        TOPUP_CASH(0),
        TOPUP_MOMO(1),
        TOPUP_VIETTELPAY(2),
        TOPUP_BANK(3);

        public final long value;

        TopupType(long value) {
            this.value = value;
        }
    }

    public enum TopupChannel {
        VTP_CHANNEL(1),
        MOMO_CHANNEL(2);

        public final long value;

        TopupChannel(long value) {
            this.value = value;
        }
    }

    public enum Status {
        WAITING("0"),
        SUCCESS("1"),
        ERROR("2");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }


//    @Column(name = "REPONSE_TIME")
//    Long reponse_time;
}