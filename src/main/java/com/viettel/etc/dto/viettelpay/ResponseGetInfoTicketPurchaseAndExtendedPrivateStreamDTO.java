package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO {
    String orderId;
    Long contractId;
    String msisdn;
    String token;
    String billingCode;

    List<TicketOrder> tickes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TicketOrder {
        String ticketNo;
        String ticketType;
        String plateNumber;
        String plateTypeId;
        String plateType;
        Long amount;
        Long stationCode;
        Long stationCodeOut;
        Long stationId;
        Long stageId;
        String stationName;
        String stationNameOut;
        String startDate;
        String endDate;
        String epc;
    }

}
