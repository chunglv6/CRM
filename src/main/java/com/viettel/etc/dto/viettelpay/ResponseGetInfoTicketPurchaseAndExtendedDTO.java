package com.viettel.etc.dto.viettelpay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGetInfoTicketPurchaseAndExtendedDTO {
    String orderId;
    Long contractId;
    String msisdn;
    String token;
    Long amount;
    String billingCode;

    List<TicketOrder> tickes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TicketOrder{
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
