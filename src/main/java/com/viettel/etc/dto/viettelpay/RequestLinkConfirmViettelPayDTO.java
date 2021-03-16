package com.viettel.etc.dto.viettelpay;

import com.viettel.etc.repositories.tables.entities.ContractPaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLinkConfirmViettelPayDTO extends RequestLinkViettelPayDTO{
    @NotNull
    String otp;
    @NotNull
    String originalOrderId;
    @NotNull
    String cardDocumentName;
    @NotNull
    @NotEmpty
    String cardFileBase64;
    @NotNull
    String documentLinkInitName;
    @NotNull
    @NotEmpty
    String documentLinkInitFileBase64;
    @NotNull
    String accountNumber;
    @NotNull
    String accountOwner;
    @NotNull
    String documentNo;
    @NotNull
    String documentTypeCode;
    @NotNull
    Long documentTypeId;
    @NotNull
    String topupAuto;
    @NotNull
    String bankCode;
    String topupAmount;
    String transContent;
    String moreInfo;
    String contractAddress;
    String contractEmail;
    String contractGender;
    String contractDob;
    String contractIdNo;
    String contractIdType;
    String contractIdIssueDate;
    String contractIdIssuePlace;
    String contractTel;
    String contractObjetType;
    String staffCode;
    String staffName;
    String shopCode;
    String shopName;
    String shopAddress;

    public ContractPaymentEntity toContractPaymentEntity(){
        ContractPaymentEntity contractPaymentEntity = new ContractPaymentEntity();
        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        contractPaymentEntity.setAccountNumber(getAccountNumber());
        contractPaymentEntity.setAccountOwner(getAccountOwner());
        contractPaymentEntity.setDocumentNo(getDocumentNo());
        contractPaymentEntity.setDocumentTypeCode(getDocumentTypeCode());
        contractPaymentEntity.setDocumentTypeId(getDocumentTypeId());
        contractPaymentEntity.setCreateDate(new Date(System.currentTimeMillis()));
        return contractPaymentEntity;
    }

    public ContractPaymentEntity toContractPaymentEntityUpdate(ContractPaymentEntity contractPaymentEntity){
        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        contractPaymentEntity.setAccountOwner(getAccountOwner());
        contractPaymentEntity.setDocumentNo(getDocumentNo());
        contractPaymentEntity.setDocumentTypeCode(getDocumentTypeCode());
        contractPaymentEntity.setDocumentTypeId(getDocumentTypeId());
        contractPaymentEntity.setAccountNumber(getAccountNumber());
        return contractPaymentEntity;
    }
}
