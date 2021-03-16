package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Thong tin giao dich
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleTransDTO {

    Long id;

    String code;

    String name;

    String saleTransDateFrom;

    String saleTransDateTo;

    Long saleTransId;

    Long saleTransType;

    String serviceFeeName;

    String createUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date saleTransDate;

    Long amount;

    String saleTransContent;

    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

    Long serviceFeeId;

    String serviceFeeCode;

    String saleTransCode;
}