package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopupAccountDTO {
    Long topupAccountConfigId;

    Long topupAmount;

    @NotBlank
    String accountUser;

    @NotBlank
    String accountUserId;

    String accountFullname;

    String partyCode;

    String partyName;

    String email;

    String phoneNumber;

    String dentityNumber;

    @NotNull
    Long amount;

    Long balance;

    String status;

    String description;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date topupDate;

    Integer startrecord;
    Integer pagesize;

    public String getAccountUser() {
        return StringUtils.upperCase(StringUtils.trim(accountUser));
    }

    public String getAccountUserId() {
        return StringUtils.trim(accountUserId);
    }

    public String getAccountFullname() {
        return StringUtils.trim(accountFullname);
    }

    public String getPartyCode() {
        return StringUtils.trim(partyCode);
    }

    public String getPartyName() {
        return StringUtils.trim(partyName);
    }

    public String getEmail() {
        return StringUtils.trim(email);
    }

    public String getPhoneNumber() {
        return StringUtils.trim(phoneNumber);
    }

    public String getDentityNumber() {
        return StringUtils.trim(dentityNumber);
    }

    public String getDescription() {
        return StringUtils.trim(description);
    }
}
