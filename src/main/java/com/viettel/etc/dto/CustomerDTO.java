package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.validates.EmailFormat;
import com.viettel.etc.utils.validates.PhoneNumberFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    Long custId;
    @NotNull
    Long custTypeId;
    @NotNull
    String documentNumber;
    @NotNull
    Long documentTypeId;
    String taxCode;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date dateOfIssue;
    String dateIssue;
    @NotNull
    String placeOfIssue;
    String custName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date birthDate;
    String dateOfBirth;
    String gender;
    @NotNull
    String areaName;
    String street;
    @NotNull
    String areaCode;
    @EmailFormat
    String email;
    @NotNull
    @PhoneNumberFormat
    String phoneNumber;
    String fax;
    String website;
    String repIdentityNumber;
    Long repIdentityTypeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date repDateOfIssue;
    String repDateIssue;
    String repPlaceOfIssue;
    String repName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date repBirthDate;
    String repGender;
    String repAreaName;
    String repStreet;
    String repAreaCode;
    @EmailFormat
    String repEmail;
    String repPhoneNumber;
    String authIdentityNumber;
    Long authIdentityTypeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date authDateOfIssue;
    String authPlaceOfIssue;
    String authName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date authBirthDate;
    String authGender;
    String authAreaName;
    String authStreet;
    String authAreaCode;
    @EmailFormat
    String authEmail;
    String authPhoneNumber;
    String status;
    String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;
    @NotNull
    Long actTypeId;
    Long reasonId;
    Long price;
    Long amount;

    public CustomerEntity toRegisterCustomerEnterprise() {
        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setDocumentTypeId(getDocumentTypeId());

        customerEntity.setCustTypeId(getCustTypeId());
        customerEntity.setPhoneNumber(getPhoneNumber());
        customerEntity.setCustName(getCustName());
        customerEntity.setTaxCode(getTaxCode());
        customerEntity.setBirthDate(getBirthDate());
        customerEntity.setDocumentNumber(getDocumentNumber());
        customerEntity.setGender(getGender());
        customerEntity.setDateOfIssue(getDateOfIssue());
        customerEntity.setPlaceOfIssue(getPlaceOfIssue());

        customerEntity.setAreaName(getAreaName());
        customerEntity.setAreaCode(getAreaCode());
        customerEntity.setStreet(getStreet());
        customerEntity.setEmail(getEmail());

        customerEntity.setRepIdentityNumber(getRepIdentityNumber());
        customerEntity.setRepIdentityTypeId(getRepIdentityTypeId());
        customerEntity.setRepDateOfIssue(getRepDateOfIssue());
        customerEntity.setRepPlaceOfIssue(getRepPlaceOfIssue());
        customerEntity.setRepName(getRepName());
        customerEntity.setRepBirthDate(getRepBirthDate());
        customerEntity.setRepGender(getRepGender());
        customerEntity.setRepAreaName(getRepAreaName());
        customerEntity.setRepStreet(getRepStreet());
        customerEntity.setRepAreaCode(getRepAreaCode());
        customerEntity.setRepEmail(getRepEmail());
        customerEntity.setRepPhoneNumber(getRepPhoneNumber());

        customerEntity.setAuthIdentityNumber(getAuthIdentityNumber());
        customerEntity.setAuthIdentityTypeId(getAuthIdentityTypeId());
        customerEntity.setAuthDateOfIssue(getAuthDateOfIssue());
        customerEntity.setAuthPlaceOfIssue(getAuthPlaceOfIssue());
        customerEntity.setAuthName(getAuthName());
        customerEntity.setAuthBirthDate(getAuthBirthDate());
        customerEntity.setAuthGender(getAuthGender());
        customerEntity.setAuthAreaName(getAuthAreaName());
        customerEntity.setAuthStreet(getAuthStreet());
        customerEntity.setAuthAreaCode(getAuthAreaCode());
        customerEntity.setAuthEmail(getAuthEmail());
        customerEntity.setAuthPhoneNumber(getAuthPhoneNumber());

        return customerEntity;
    }

    public CustomerEntity toAddCustomerEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustTypeId(getCustTypeId());
        customer.setCustId(getCustId());
        customer.setPhoneNumber(getPhoneNumber());
        customer.setCustName(getCustName());

        if (getBirthDate() != null) {
            customer.setBirthDate(getBirthDate());
        } else {
            java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
            customer.setBirthDate(currDate);
        }

        customer.setGender(getGender());
        customer.setDocumentTypeId(getDocumentTypeId());
        customer.setDocumentNumber(getDocumentNumber());
        customer.setDateOfIssue(getDateOfIssue());
        customer.setPlaceOfIssue(getPlaceOfIssue());
        customer.setAreaCode(getAreaCode());
        customer.setStreet(getStreet());
        customer.setAreaName(getAreaName());
        customer.setEmail(getEmail());
        return customer;
    }

    public void updateValueCustomer(CustomerEntity customer) {
        customer.setCustTypeId(custTypeId);
        customer.setCustName(custName);
        customer.setBirthDate(birthDate);
        customer.setGender(gender);
        customer.setDateOfIssue(dateOfIssue);
        customer.setPlaceOfIssue(placeOfIssue);
        customer.setAreaCode(areaCode);
        customer.setStreet(street);
        customer.setAreaName(areaName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setDocumentNumber(documentNumber);
        customer.setDocumentTypeId(documentTypeId);
    }

    public void updateValueCustomerEnterprise(CustomerEntity customer) {
        customer.setCustTypeId(custTypeId);
        customer.setCustName(custName);
        customer.setBirthDate(birthDate);
        customer.setGender(gender);
        customer.setDateOfIssue(dateOfIssue);
        customer.setPlaceOfIssue(placeOfIssue);
        customer.setAreaCode(areaCode);
        customer.setStreet(street);
        customer.setAreaName(areaName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setRepName(repName);
        customer.setRepBirthDate(repBirthDate);
        customer.setRepGender(repGender);
        customer.setRepDateOfIssue(repDateOfIssue);
        customer.setRepPlaceOfIssue(repPlaceOfIssue);
        customer.setRepPlaceOfIssue(repPlaceOfIssue);
        customer.setRepAreaCode(repAreaCode);
        customer.setRepStreet(repStreet);
        customer.setRepAreaName(repAreaName);
        customer.setRepPhoneNumber(repPhoneNumber);
        customer.setRepEmail(repEmail);
        customer.setAuthName(authName);
        customer.setAuthBirthDate(authBirthDate);
        customer.setAuthGender(authGender);
        customer.setAuthDateOfIssue(authDateOfIssue);
        customer.setAuthPlaceOfIssue(authPlaceOfIssue);
        customer.setAuthPlaceOfIssue(authPlaceOfIssue);
        customer.setAuthAreaCode(authAreaCode);
        customer.setAuthStreet(authStreet);
        customer.setAuthAreaName(authAreaName);
        customer.setAuthPhoneNumber(authPhoneNumber);
        customer.setAuthEmail(authEmail);
        customer.setAuthIdentityNumber(authIdentityNumber);
        customer.setAuthIdentityTypeId(authIdentityTypeId);
        customer.setDocumentTypeId(documentTypeId);
        customer.setDocumentNumber(documentNumber);
        customer.setTaxCode(taxCode);
    }


    String userIdTest;


    String statusId;


    Integer startrecord;


    Integer pagesize;


    Boolean resultSqlEx;

}
