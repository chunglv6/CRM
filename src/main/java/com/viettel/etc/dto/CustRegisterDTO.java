package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustRegisterDTO {

    Long custRegId;

    Long accountId;

    String accountUser;

    String areaCode;

    String areaName;

    String authAreaCode;

    String authAreaName;

    Date authBirthDate;

    Date authDateOfIssue;

    String authEmail;

    String authGender;

    String authIentityNumber;

    Long authIdentityTypeId;

    String authName;

    String authPhoneNumber;

    String authPlaceOfIssue;

    String authStreet;

    Date birthDate;

    String custName;

    Long custTypeId;

    Date dateOfIssue;

    String documentNumber;

    Long documentTypeId;

    String email;

    String fax;

    String gender;

    Long numVehicle;

    String phoneNumber;

    String placeOfIssue;

    Date regDate;

    String regStatus;

    String repAreaCode;

    String repAreaName;

    Date repBirthDate;

    Date repDateOfIssue;

    String repEmail;

    String repGender;

    String repIdentityNumber;

    Long repIdentityTypeId;

    String repName;

    String repPhoneNumber;

    String repPlaceOfIssue;

    String repStreet;

    String street;

    String taxCode;

    String website;

    Integer startrecord;

    Integer pagesize;

}
