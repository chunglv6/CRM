package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;

import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Lop ve tac dong
 * 
 * @author ToolGen
 * @date Mon Jun 29 11:19:51 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ActionDTO {
    Long reasonId;

    String id;

    Long actTypeId;

    String code;

    String name;

    String isOcs;

    String actObject;

    String createUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;

    String updateUser;

    Date updateDate;

    String description;

    String status;

    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

    String createDateFrom;

    String createDateTo;

    String actionUserName;

    String custName;

    Boolean getAll;

    Long actionAuditId;
}