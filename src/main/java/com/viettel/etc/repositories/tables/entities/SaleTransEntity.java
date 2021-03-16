package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Sale_trans
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SALE_TRANS")
public class SaleTransEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "SALE_TRANS_SEQ")
    @SequenceGenerator(name = "SALE_TRANS_SEQ", sequenceName = "SALE_TRANS_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "SALE_TRANS_ID")
    Long saleTransId;

    @Column(name = "SALE_TRANS_CODE")
    String saleTransCode;

    @Column(name = "SALE_TRANS_DATE")
    Date saleTransDate;

    @Column(name = "SALE_TRANS_TYPE")
    String saleTransType;

    @Column(name = "STATUS")
    String status;

    @Column(name = "INVOICE_USED")
    String invoiceUsed;

    @Column(name = "INVOICE_CREATE_DATE")
    Date invoiceCreateDate;

    @Column(name = "SHOP_ID")
    Long shopId;

    @Column(name = "SHOP_NAME")
    String shopName;

    @Column(name = "STAFF_ID")
    Long staffId;

    @Column(name = "STAFF_NAME")
    String staffName;

    @Column(name = "PAYMENT_METHOD_ID")
    Long paymentMethodId;

    @Column(name = "ACCOUNT_NUMBER")
    String accountNumber;

    @Column(name = "ACCOUNT_OWNER")
    String accountOwner;

    @Column(name = "ACCOUNT_BANK_ID")
    Long accountBankId;

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

    @Column(name = "APPROVER_USER")
    String approverUser;

    @Column(name = "APPROVER_DATE")
    Date approverDate;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "CUST_ID")
    Long custId;

    public enum Status {
        UN_PAID("1"),
        PAID_NOT_INVOICED("2"),
        BILLED("3"),
        CANCEL("4"),
        NOT_SUCCESS("5");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }

    public enum SaleTransType {
        PACKAGE("1"),
        MONTHLY_QUARTERLY_TICKET("2");

        public final String value;

        SaleTransType(String value) {
            this.value = value;
        }
    }

    @Column(name = "METHOD_RECHARGE_ID")
    Long methodRechargeId;

    public enum PaymentMethodId {
        ETC("0"),
        CURRENCY("1"),
        ACCOUNT_LINKED("2");

        public final String value;

        PaymentMethodId(String value) {
            this.value = value;
        }
    }

}
