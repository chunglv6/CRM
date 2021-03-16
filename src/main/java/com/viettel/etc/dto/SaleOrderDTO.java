package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleOrderDTO {
    Long saleOrderId;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_TIME_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date saleOrderDate;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date saleOrderBefore;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date saleOrderAfter;

    String saleOrderType;

    String saleOrderSource;

    String status;

    Long methodRechargeId;

    Long paymentMethodId;

    Long amount;

    Long quantity;

    Long discount;

    Long promotion;

    Long amountTax;

    Long amountNotTax;

    Long vat;

    Long tax;

    Long custId;

    Long contractId;

    String contractNo;

    String description;

    String destroyUser;

    Date destroyDate;

    String createUser;

    Date createDate;

    Long sourceContractId;

    String sourceContractNo;

    Long payGateStatus;

    String payGateErrorCode;

    Long verifyStatus;

    Long ocsStatus;

    Integer startrecord;

    Integer pagesize;
}
