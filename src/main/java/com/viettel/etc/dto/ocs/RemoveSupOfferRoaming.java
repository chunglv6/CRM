package com.viettel.etc.dto.ocs;

import lombok.Data;

@Data
public class RemoveSupOfferRoaming {

    String EPC;
    String agentId;
    String agentName;
    String staffId;
    String staffName;
    String subscriptionTicketId;
    String requestId;
    String requestDateTime;
    String responseId;
    String responseDateTime;
    String isRefund;
    Long actTypeId;
}
