package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;

import javax.validation.constraints.NotNull;
import com.viettel.etc.utils.validates.PhoneNumberFormat;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSuperAppDTO {
    private  String EMPTY_STRING = " ";

    Long custId;
    Long contractId;
    String contractNo;
    String custName;
    String registerNo;
    @NotNull
    @PhoneNumberFormat
    String phoneNumber;
    @NotNull
    String documentNumber;
    @NotNull
    String password;
    Long documentTypeId;
    Long custTypeId;
    Long actTypeId;
    Long reasonId;


    public CustomerEntity toAddCustomerEntity() {
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        CustomerEntity customer = new CustomerEntity();
        customer.setCustTypeId(getCustTypeId()); // 1: Ca nhan trong nuoc
        customer.setCustId(getCustId());
        customer.setPhoneNumber(getPhoneNumber());
        customer.setCustName(getCustName());
        customer.setBirthDate(currDate);
        customer.setDocumentTypeId(getDocumentTypeId()); // Mac dinh la CMND
        customer.setDocumentNumber(getDocumentNumber());
        customer.setDateOfIssue(currDate);
        customer.setPlaceOfIssue(EMPTY_STRING);
        customer.setAreaCode(EMPTY_STRING);
        customer.setStreet(EMPTY_STRING);
        customer.setAreaName(EMPTY_STRING);
        customer.setCreateDate(currDate);
        customer.setCreateUser("ADMIN_SUPERAPP");
        return customer;
    }

    public ContractEntity toAddContractEntity() {
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setSignDate(currDate);
        contractEntity.setSignName(EMPTY_STRING);
        contractEntity.setEffDate(currDate);
        contractEntity.setExpDate(currDate);
        contractEntity.setEmailNotification(EMPTY_STRING);
        contractEntity.setSmsNotification(EMPTY_STRING);
        contractEntity.setPushNotification(EMPTY_STRING);
        contractEntity.setBillCycle(EMPTY_STRING);
        contractEntity.setPayCharge(EMPTY_STRING);
        contractEntity.setAccountAlias(getPhoneNumber());
        contractEntity.setNoticeName(EMPTY_STRING);
        contractEntity.setNoticeStreet(EMPTY_STRING);
        contractEntity.setNoticeEmail(EMPTY_STRING);
        contractEntity.setNoticePhoneNumber(getPhoneNumber());
        contractEntity.setNoticeAreaCode(EMPTY_STRING);
        contractEntity.setNoticeAreaName(EMPTY_STRING);
        contractEntity.setSmsRenew(EMPTY_STRING);
        contractEntity.setSmsDateSign(currDate);
        contractEntity.setIsEtc(0L);
        contractEntity.setCreateDate(currDate);
        contractEntity.setCreateUser("ADMIN_SUPERAPP");
        contractEntity.setIsLock(0L);
        contractEntity.setStatus(ContractEntity.Status.NOT_ACTIVATED.value);
        contractEntity.setProfileStatus(ContractEntity.ProfilesStatus.NOT_RECEIVED.value);

        return contractEntity;
    }
}
