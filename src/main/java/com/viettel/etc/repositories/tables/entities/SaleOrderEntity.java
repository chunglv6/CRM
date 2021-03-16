package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Sale_order
 *
 * @author ToolGen
 * @date Thu Oct 22 17:41:46 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDER")
public class SaleOrderEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "SALE_ORDER_SEQ")
    @SequenceGenerator(name = "SALE_ORDER_SEQ", sequenceName = "SALE_ORDER_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "SALE_ORDER_ID")
    Long saleOrderId;

    @Column(name = "SALE_ORDER_DATE")
    Date saleOrderDate;

    @Column(name = "SALE_ORDER_TYPE")
    String saleOrderType;

    @Column(name = "SALE_ORDER_SOURCE")
    String saleOrderSource;

    @Column(name = "STATUS")
    String status;

    @Column(name = "METHOD_RECHARGE_ID")
    Long methodRechargeId;

    @Column(name = "PAYMENT_METHOD_ID")
    Long paymentMethodId;

    @Column(name = "AMOUNT")
    Long amount;

    @Column(name = "QUANTITY")
    Long quantity;

    @Column(name = "DISCOUNT")
    Long discount;

    @Column(name = "PROMOTION")
    Long promotion;

    @Column(name = "AMOUNT_TAX")
    Long amountTax;

    @Column(name = "AMOUNT_NOT_TAX")
    Long amountNotTax;

    @Column(name = "VAT")
    Long vat;

    @Column(name = "TAX")
    Long tax;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "CONTRACT_NO")
    String contractNo;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "DESTROY_USER")
    String destroyUser;

    @Column(name = "DESTROY_DATE")
    Date destroyDate;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "SOURCE_CONTRACT_ID")
    Long sourceContractId;

    @Column(name = "SOURCE_CONTRACT_NO")
    String sourceContractNo;

    @Column(name = "PAY_GATE_STATUS")
    Long payGateStatus;

    @Column(name = "PAY_GATE_ERROR_CODE")
    String payGateErrorCode;

    @Column(name = "VERIFY_STATUS")
    Long verifyStatus;

    @Column(name = "OCS_STATUS")
    Long ocsStatus;

    public enum SaleOrderType {
        ADD_MONEY("1"),
        CHARGE_TICKET("2");

        public final String value;

        SaleOrderType(String value) {
            this.value = value;
        }
    }

    public enum SaleOrderSource {
        VTP("VTP"),
        MOMO("MoMo");

        public final String value;

        SaleOrderSource(String value) {
            this.value = value;
        }
    }

    public enum Status {
        NEW_ORDER("1"),
        SUCCESS_PAYMENT("2"),
        CANCELED("3"),
        OCS_FAILED("4"); // call OCS failed

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }

    public enum PaymentStatus {
        SUCCESS(1L),
        FAILED(0L);

        public final Long value;

        PaymentStatus(Long value) {
            this.value = value;
        }
    }

    public enum VerifyStatus {
        SUCCESS(1L),
        FAILED(0L);

        public final Long value;

        VerifyStatus(Long value) {
            this.value = value;
        }
    }

    public enum OcsStatus {
        SUCCESS(1L),
        FAILED(0L);

        public final Long value;

        OcsStatus(Long value) {
            this.value = value;
        }
    }

    public enum PaymentCode {
        SUCCESS("00");

        public final String value;

        PaymentCode(String value) {
            this.value = value;
        }
    }

}
