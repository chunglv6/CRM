package com.viettel.etc.dto;

import lombok.Data;

@Data
public class AddSupOfferRoamingDTO {
    String EPC;
    String eff_Date;
    String exp_Date;
    String partyCode;
    String agentId;
    String agentName;
    String staffId;
    String staffName;
    String subscriptionTicketId;
    String requestId;
    String requestDateTime;
    String responseId;
    String responseDateTime;
    String ticketType;
    String discountId;
    String charge;
    String stationIn;
    String stationOut;
    String laneIn;
    String laneOut;
    Long actTypeId;
}
