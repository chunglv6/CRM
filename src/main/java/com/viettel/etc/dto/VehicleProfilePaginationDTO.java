package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleProfilePaginationDTO extends  VehicleProfileDTO{
    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

    String documentTypeName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date receptionDay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date scanDay;

    Long actTypeId;

    List<VehicleProfilePaginationDTO.ProfileDTO> profileDTOList = new ArrayList<>();

    public void addProfile(String fileName, Long vehicleProfileId){
        profileDTOList.add(new VehicleProfilePaginationDTO.ProfileDTO(fileName, vehicleProfileId));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProfileDTO {
        String fileName;
        Long contractProfileId;
    }
}
