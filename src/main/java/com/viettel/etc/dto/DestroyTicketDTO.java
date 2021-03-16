package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestroyTicketDTO {
    Long saleTransDetailId;

    String plateNumber;

    Long stationId;

    Long stageId;

    String ticketType;

    String epc;

    String booCode;

    String offerLevel;

    String offerId;

    Long actTypeId;

    Long subscriptionTicketId;

    Long reasonId;

    Long vehicleId;

    Long custId;

    Long destroyTicketType;

    Long amount;

    String stationType;

    Long stationInId;

    Long stationOutId;

    String startDate;

    String endDate;

    String contractNo;
}
