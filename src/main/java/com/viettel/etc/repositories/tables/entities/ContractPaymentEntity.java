package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Contract_payment
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "CONTRACT_PAYMENT")
public class ContractPaymentEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "CONTRACT_PAYMENT_SEQ")
    @SequenceGenerator(name = "CONTRACT_PAYMENT_SEQ", sequenceName = "CONTRACT_PAYMENT_SEQ", allocationSize = 1)
    @Column(name = "CONTRACT_PAYMENT_ID")
    Long contractPaymentId;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "METHOD_RECHARGE_ID")
    Long methodRechargeId;

    @Column(name = "ACCOUNT_NUMBER")
    String accountNumber;

    @Column(name = "ACCOUNT_OWNER")
    String accountOwner;

    @Column(name = "ACCOUNT_BANK_ID")
    Long accountBankId;

    @Column(name = "CREATE_USER")
    String createUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.COMMON_DATE_TIME_FORMAT)
    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    String status;

    @Column(name="TOKEN")
    String token;


    @Column(name="ORDER_ID")
    String orderId;

    @Column(name = "METHOD_RECHARGE_CODE")
    String methodRechargeCode;

    @Column(name = "TOPUP_AUTO")
    Long topupAuto;

    @Column(name = "TOPUP_AMOUNT")
    Long topupAmount;

    @Column(name = "DOCUMENT_TYPE_ID")
    Long documentTypeId;

    @Column(name = "DOCUMENT_TYPE_CODE")
    String documentTypeCode;

    @Column(name = "DOCUMENT_NO")
    String documentNo;

    @Column(name = "CHANNEL")
    Long channel;

    @Column(name = "LINK_PHONE")
    String linkPhone;

    public enum Status {
        NOT_ACTIVATED("0"),
        ACTIVATED("1");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
