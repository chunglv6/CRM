package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.repositories.tables.entities.SaleTransEntity;
import com.viettel.etc.utils.Constants;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AddSupOfferRequestDTO {
    // DTO to map saleTrans
    // Chua co cach sinh
    private String saleTransCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date saleTransDate;

    private String invoiceUsed;

    private Long shopId;

    private String shopName;

    private Long staffId;

    private String staffName;

    private Long paymentMethodId;

    private String accountNumber;

    private String accountOwner;

    private Long accountBankId;

    private Long amount;

    private Long quantity;

    private Long discount;

    private Long promotion;

    private Long amountTax;

    private Long amountNotTax;

    private Long vat;

    private Long tax;

    private Long contractId;

    private String contractNo;

    private String description;

    private String destroyUser;

    private Date destroyDate;

    List<VehicleAddSuffOfferDTO> list;

    private Long actTypeId;

    private boolean acountDirect;

    private boolean acountETC;

    private boolean acountLinked;

    public SaleTransEntity toAddSaleTranEntity(String userLogin, java.sql.Date currDate, long custId, long contractId, long shopId, String shopName) {
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransCode(saleTransCode);
        if (saleTransDate != null) {
            saleTransEntity.setSaleTransDate(new java.sql.Date(saleTransDate.getTime()));
        }
        saleTransEntity.setInvoiceUsed(invoiceUsed);

        saleTransEntity.setShopId(shopId);

        saleTransEntity.setShopName(shopName);

        saleTransEntity.setStaffName(userLogin);

        saleTransEntity.setPaymentMethodId(paymentMethodId);

        saleTransEntity.setAccountNumber(accountNumber);

        saleTransEntity.setAccountOwner(accountOwner);

        saleTransEntity.setAccountBankId(accountBankId);

        saleTransEntity.setAmount(amount);

        saleTransEntity.setDiscount(discount);

        saleTransEntity.setQuantity(quantity);

        saleTransEntity.setPromotion(promotion);

        saleTransEntity.setAmountTax(amountTax);

        saleTransEntity.setAmountNotTax(amountNotTax);

        saleTransEntity.setVat(vat);

        saleTransEntity.setTax(tax);

        saleTransEntity.setContractId(contractId);

        saleTransEntity.setContractNo(contractNo);

        saleTransEntity.setDescription(description);

        saleTransEntity.setDestroyUser(destroyUser);
        if (destroyDate != null) {
            saleTransEntity.setDestroyDate(new java.sql.Date(destroyDate.getTime()));
        }
        saleTransEntity.setCreateDate(currDate);
        saleTransEntity.setStaffName(userLogin);
        saleTransEntity.setCreateUser(userLogin);
        saleTransEntity.setCustId(custId);
        saleTransEntity.setPaymentMethodId(paymentMethodId);
        saleTransEntity.setStaffId(staffId);


        return saleTransEntity;
    }

}
