package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class AcceptBriefcasesDTO {
    private String signName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date birthDay;
    private String gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date authDateOfIssue;
    private String authPlaceOfIssue;
    private Long custId;
    List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList;
    private boolean accept;
    private Long contractId;
    private String reason;
    Long actTypeId;
}
