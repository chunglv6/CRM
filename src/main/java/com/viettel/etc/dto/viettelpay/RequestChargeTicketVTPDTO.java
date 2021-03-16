package com.viettel.etc.dto.viettelpay;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RequestChargeTicketVTPDTO {
    String orderId;
    String billingCode;
    Date requestTime;
    String token;
    String contractId;
    String msisdn;
    String bankCodeAfter;
    Long amount;
    String code;
    String message;
    List<Ticket> listTicket;

    @Data
    public static class Ticket {
        Long ticket_type;
        String plateNumber;
        Long amount;
        Long stationInID;
        Long stationOutID;
        Long effDate;
        Long expDate;
    }
}
