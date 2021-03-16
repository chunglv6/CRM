package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;

import com.viettel.etc.repositories.tables.entities.ActTypeEntity;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Loai tac dong
 * 
 * @author ToolGen
 * @date Fri Sep 04 09:41:42 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ActTypeDTO {

    Long actTypeId;

    String code;

    String name;

    String isOcs;

    String actObject;

    String createUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;

    String updateUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date updateDate;

    String description;

    String status;

    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

}