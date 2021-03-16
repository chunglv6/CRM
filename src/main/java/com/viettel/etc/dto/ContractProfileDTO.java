package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ContractProfileDTO {
    Long contractProfileId;
    Long custId;
    Long contractId;
    @NotNull
    Long documentTypeId;
    @NotNull
    @NotEmpty
    String fileName;
    String fileSize;
    String filePath;
    String createUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;
    String approvedUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date approvedDate;
    String delUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date delDate;
    String description;
    String status;
    @NotNull
    @NotEmpty
    String fileBase64;
    Integer startrecord;
    Boolean resultsqlex;
    Integer pagesize;
    Boolean resultSqlEx;
    String documentTypeName;
    Long type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date scanDay;
    Long actTypeId;
    Long reasonId;

    List<ProfileDTO> profileDTOList = new ArrayList<>();

    public void addProfile(String fileName, Long contractProfileId){
        profileDTOList.add(new ProfileDTO(fileName, contractProfileId));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    public class ProfileDTO {
        String fileName;
        Long contractProfileId;
    }
}
