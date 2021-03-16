package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionAuditHistoryDTO extends ActionAuditDTO {

    private Long actionAuditId;

    private Long custId;

    private String custName;

    private String actObject;

    private String actionTypeName;

    private String actionReasonName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    private Date createDate;

    private Integer startrecord;

    private Integer pagesize;

    private String documentNumber;

    private String taxCode;

    private String phoneNumber;

    private String contractNo;

    private String plateNumber;

    private String tableName;

    private String rfidSerial;

    private String startDate;

    private String endDate;

}
