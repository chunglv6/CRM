package com.viettel.etc.repositories.tables.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Contract
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CONTRACT")
public class ContractEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "CONTRACT_SEQ")
    @SequenceGenerator(name = "CONTRACT_SEQ", sequenceName = "CONTRACT_SEQ", allocationSize = 1)
    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_NO")
    String contractNo;

    @Column(name = "SIGN_DATE")
    Date signDate;

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    String status;

    @Column(name = "EMAIL_NOTIFICATION")
    String emailNotification;

    @Column(name = "SMS_NOTIFICATION")
    String smsNotification;

    @Column(name = "PUSH_NOTIFICATION")
    String pushNotification;

    @Column(name = "BILL_CYCLE")
    String billCycle;

    @Column(name = "PAY_CHARGE")
    String payCharge;

    @Column(name = "PAYMENT_DEFAULT_ID")
    Long paymentDefaultId;

    @Column(name = "ACCOUNT_USER")
    String accountUser;

    @Column(name = "NOTICE_NAME")
    String noticeName;

    @Column(name = "NOTICE_AREA_NAME")
    String noticeAreaName;

    @Column(name = "NOTICE_STREET")
    String noticeStreet;

    @Column(name = "NOTICE_AREA_CODE")
    String noticeAreaCode;

    @Column(name = "NOTICE_EMAIL")
    String noticeEmail;

    @Column(name = "NOTICE_PHONE_NUMBER")
    String noticePhoneNumber;

    @Column(name = "PROFILE_STATUS")
    String profileStatus;

    @Column(name = "APPROVED_USER")
    String approvedUser;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "ADDFILES_USER")
    String addfilesUser;

    @Column(name = "ADDFILES_DATE")
    Date addfilesDate;

    @Column(name = "SIGN_NAME")
    String signName;

    @Column(name = "SMS_RENEW")
    String smsRenew;

    @Column(name = "SMS_DATE_SIGN")
    Date smsDateSign;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "SHOP_ID")
    Long shopId;

    @Column(name = "SHOP_NAME")
    String shopName;

    @Column(name = "ACCOUNT_USER_ID")
    String accountUserId;

    @Column(name = "NOTE")
    String note;

    @Column(name = "IS_LOCK")
    Long isLock;

    @Column(name = "ACCOUNT_ALIAS")
    String accountAlias;

    @Column(name = "ORDER_NUMBER")
    String orderNumber;

    @Column(name = "IS_ADDITIONAL", columnDefinition = "NUMBER DEFAULT 0")
    Long isAdditional = 0L;

    @Column(name = "IS_ETC", columnDefinition = "NUMBER DEFAULT 1")
    Long isEtc = 1L;

    public enum Status {
        NOT_ACTIVATED("1"),
        ACTIVATED("2"),
        CANCEL("3"),
        TERMINATE("4");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }

    public enum ProfilesStatus {
        NOT_RECEIVED("1"),
        APPROVED("2"),
        REJECT("3"),
        ADDITIONAL_RECORDS("4");

        public final String value;

        ProfilesStatus(String value) {
            this.value = value;
        }
    }

    /**
    *
    * Dam bao cac ham dung hien tai chay dung khi them field isEtc
    */
    public ContractEntity(Long contractId, Long custId, String contractNo, Date signDate, Date effDate, Date expDate, String description, String status, String emailNotification, String smsNotification, String pushNotification, String billCycle, String payCharge, Long paymentDefaultId, String accountUser, String noticeName, String noticeAreaName, String noticeStreet, String noticeAreaCode, String noticeEmail, String noticePhoneNumber, String profileStatus, String approvedUser, Date approvedDate, String addfilesUser, Date addfilesDate, String signName, String smsRenew, Date smsDateSign, String createUser, Date createDate, Long shopId, String shopName, String accountUserId, String note, Long isLock, String accountAlias, String orderNumber) {
        this.contractId = contractId;
        this.custId = custId;
        this.contractNo = contractNo;
        this.signDate = signDate;
        this.effDate = effDate;
        this.expDate = expDate;
        this.description = description;
        this.status = status;
        this.emailNotification = emailNotification;
        this.smsNotification = smsNotification;
        this.pushNotification = pushNotification;
        this.billCycle = billCycle;
        this.payCharge = payCharge;
        this.paymentDefaultId = paymentDefaultId;
        this.accountUser = accountUser;
        this.noticeName = noticeName;
        this.noticeAreaName = noticeAreaName;
        this.noticeStreet = noticeStreet;
        this.noticeAreaCode = noticeAreaCode;
        this.noticeEmail = noticeEmail;
        this.noticePhoneNumber = noticePhoneNumber;
        this.profileStatus = profileStatus;
        this.approvedUser = approvedUser;
        this.approvedDate = approvedDate;
        this.addfilesUser = addfilesUser;
        this.addfilesDate = addfilesDate;
        this.signName = signName;
        this.smsRenew = smsRenew;
        this.smsDateSign = smsDateSign;
        this.createUser = createUser;
        this.createDate = createDate;
        this.shopId = shopId;
        this.shopName = shopName;
        this.accountUserId = accountUserId;
        this.note = note;
        this.isLock = isLock;
        this.accountAlias = accountAlias;
        this.orderNumber = orderNumber;
    }
}
