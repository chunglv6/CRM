package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleTransDetailDTO {
     String saleTransDateFrom;

     String saleTransDateTo;

     Long effect;

     Long saleTransId;

     Long stationId;

     Long stageId;

     String stage;

     String station;

     Long servicePlanTypeId;

     String servicePlanTypeName;

     Long price;

     Long status;

     String saleTransDate;

     String accountOwner;

     String effDate;

     String expDate;

     String autoRenew;

     Long efficiencyId;

     Long efficiency;

     Integer startrecord;

     Boolean resultsqlex;

     Integer pagesize;

     Boolean resultSqlEx;

     String plateNumber;

     Long chargeMethodId;

     String chargeMethodName;

     Long paymentMethodId;

     String saleTransCode;

     String epc;

     String booCode;

     String offerId;

     String offerLevel;

     Long saleTransDetailId;

     Long subscriptionTicketId;

     Long actTypeId;

     Long reasonId;

     Long stationType;

     Long stationInId;

     Long stationOutId;
}
