package com.viettel.etc.dto;

import com.viettel.etc.dto.viettelpay.RequestRenewTicketPricesDTO;
import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Date;

@Data
public class SaleOrderDetailDTO {
    Long saleOrderDetailId;
    Long saleOrderId;
    Date saleOrderDate;
    Long serviceFeeId;
    String serviceFeeName;
    Long vehicleId;
    String plateNumber;
    String epc;
    String tid;
    String rfidSerial;
    Long vehicleGroupId;
    Long servicePlanId;
    Long servicePlanName;
    Long servicePlanTypeId;
    String ocsCode;
    String scope;
    String offerLevel;
    Date effDate;
    Date expDate;
    String autoRenew;
    String autoRenewVtp;
    Long stationId;
    Long stageId;
    Long price;
    Long quantity;
    Long discountId;
    Long amount;
    Long promotionId;
    Long promotionAmount;
    String description;
    String createUser;
    Date createDate;
}
