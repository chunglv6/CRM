package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.utils.Constants;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractDTO {

    Long contractId;

    Long custId;

    String contractNo;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date signDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date effDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date expDate;

    String description;

    String status;

    @NotNull
    @NotEmpty
    String emailNotification;

    @NotNull
    @NotEmpty
    String smsNotification;

    @NotNull
    @NotEmpty
    String pushNotification;

    @NotNull
    @NotEmpty
    String billCycle;

    @NotNull
    @NotEmpty
    String payCharge;

    String accountUser;

    @NotNull
    @NotEmpty
    String noticeName;

    String noticeAreaName;

    @NotNull
    @NotEmpty
    String noticeStreet;

    @NotNull
    @NotEmpty
    String noticeAreaCode;

    String noticeEmail;

    @NotNull
    @NotEmpty
    String noticePhoneNumber;

    String profileStatus;

    String approvedUser;

    Date approvedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date addfilesDate;

    @NotNull
    @NotEmpty
    String signName;

    String smsRenew;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date smsDateSign;

    String createUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;

    @NotNull
    Long actTypeId;

    @Valid
    List<ContractProfileDTO> contractProfileDTOs;

    Long balance;


    Long reasonId;

    Long price;

    Long amount;

    public ContractEntity toAddContractEntity() {
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setSignDate(getSignDate());
        contractEntity.setSignName(getSignName());
        contractEntity.setEffDate(getEffDate());
        contractEntity.setExpDate(getExpDate());
        contractEntity.setDescription(getDescription());
        contractEntity.setEmailNotification(getEmailNotification());
        contractEntity.setSmsNotification(getSmsNotification());
        contractEntity.setPushNotification(getPushNotification());
        contractEntity.setBillCycle(getBillCycle());
        contractEntity.setPayCharge(getPayCharge());
        contractEntity.setAccountUser(getAccountUser());
        contractEntity.setNoticeName(getNoticeName());
        contractEntity.setNoticeStreet(getNoticeStreet());
        contractEntity.setNoticeEmail(getNoticeEmail());
        contractEntity.setNoticePhoneNumber(getNoticePhoneNumber());
        contractEntity.setNoticeAreaCode(getNoticeAreaCode());
        contractEntity.setNoticeAreaName(getNoticeAreaName());
        contractEntity.setSmsRenew(getSmsRenew());
        contractEntity.setSmsDateSign(getSmsDateSign());

        return contractEntity;
    }

    public void setDataToEntityOnEditContract(ContractEntity contractEntity) {
        contractEntity.setSignName(getSignName());
        contractEntity.setEffDate(getEffDate());
        contractEntity.setExpDate(getExpDate());
        contractEntity.setEmailNotification(getEmailNotification());
        contractEntity.setSmsNotification(getSmsNotification());
        contractEntity.setPushNotification(getPushNotification());
        contractEntity.setBillCycle(getBillCycle());
        contractEntity.setPayCharge(getPayCharge());
        contractEntity.setNoticeName(getNoticeName());
        contractEntity.setNoticeAreaCode(getNoticeAreaCode());
        contractEntity.setNoticeAreaName(getNoticeAreaName());
        contractEntity.setNoticeStreet(getNoticeStreet());
        contractEntity.setNoticeEmail(getNoticeEmail());
        contractEntity.setNoticePhoneNumber(getNoticePhoneNumber());
        contractEntity.setSmsRenew(getSmsRenew());
        contractEntity.setSmsDateSign(getSmsDateSign());
    }

    public ContractDTO fromEntity(ContractEntity contractEntity, String ocsResponse) {
        OCSContractInfo ocsContractInfo = new Gson().fromJson(ocsResponse, OCSContractInfo.class);
        ContractDTO contractDTO = new Gson().fromJson(new Gson().toJson(contractEntity), ContractDTO.class);
        contractDTO.setBalance(ocsContractInfo.balance);

        return contractDTO;
    }

    static class OCSContractInfo {
        Long balance;
    }
}
