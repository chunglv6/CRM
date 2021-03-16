package com.viettel.etc.dto;

import lombok.Data;

@Data
public class ChargeSupOfferDTO {
    String contractId;
    String epc;
    String offerId;
    String exp_Date;
    String offerLevel;
    String isRecurring;
    String isRecurringExtBalance;
    String agentId;
    String agentName;
    String staffId;
    String staffName;
    String subscriptionTicketId;
    String requestId;
    String requestDateTime;
    String responseId;
    String responseDateTime;
}
