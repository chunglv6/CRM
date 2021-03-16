package com.viettel.etc.repositories;

import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;

import java.util.List;

public interface SaleOrderDetailRepository {
    List<SaleOrderDetailEntity> findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(
            Long servicePlanTypeId, String plateNumber, Long stationId, String effDate, String expDate);

    List<SaleOrderDetailEntity> findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(
            Long servicePlanTypeId, String plateNumber, Long stageId, String effDate, String expDate);
}
