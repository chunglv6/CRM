package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchContractDTO {

    private String contractNo;

    private String accountUserId;

    private String plateNumber;

    private String documentNumber;

    private String phoneNumber;

    String noticePhoneNumber;

    private Long contractId;

    private Long custId;

    private String custName;

    private Long custTypeId;

    private String startDate;

    private String endDate;

    private String signName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date signDate;

    private String status;

    Integer startrecord;

    Boolean resultsqlex;

    Integer pagesize;

    Boolean resultSqlEx;

    String documentNumberOrPhone;

    String accountUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date repBirthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date authBirthDate;

    Long documentTypeId;

    Long repIdentityTypeId;

    Long authIdentityTypeId;

    String repIdentityNumber;

    String authIdentityNumber;

    Long isLock;
}
