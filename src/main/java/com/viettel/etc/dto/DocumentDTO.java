package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;

import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Lop lay danh sach ve chung tu
 * 
 * @author ToolGen
 * @date Fri Jul 03 10:13:49 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DocumentDTO {

    Long id;

    String code;

    String val;

    String name;

    String custTypeId;

    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

    String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;
}