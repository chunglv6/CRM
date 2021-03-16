package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ExportVehiclesAssignedDTO;
import com.viettel.etc.repositories.SaleOrderDetailRepository;
import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderDetailRepositoryImpl extends CommonDataBaseRepository implements SaleOrderDetailRepository {
    @Override
    public List<SaleOrderDetailEntity> findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(
            Long servicePlanTypeId, String plateNumber, Long stationId, String effDate, String expDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("Select  SALE_ORDER_DETAIL_ID as  saleOrderDetailId,SALE_ORDER_ID as saleOrderId,");
        sql.append("CREATE_DATE as createDate, CREATE_USER as createUser, DESCRIPTION as description,");
        sql.append("PROMOTION_AMOUNT as promotionAmount, PROMOTION_ID as promotionId,");
        sql.append("AMOUNT as amount,DISCOUNT_ID as discountId, QUANTITY as quantity, PRICE as price,");
        sql.append("STAGE_ID as stageId ,STATION_ID as stationId, AUTO_RENEW_VTP as autoRenewVtp,");
        sql.append("AUTO_RENEW as autoRenew, EXP_DATE as expDate, EFF_DATE as effDate, OFFER_LEVEL as offerLevel,");
        sql.append("SCOPE as scope, OCS_CODE as ocsCode, SERVICE_PLAN_TYPE_ID as servicePlanTypeId,");
        sql.append("SERVICE_PLAN_NAME as servicePlanName, SERVICE_PLAN_ID as servicePlanId, VEHICLE_GROUP_ID as vehicleGroupId,");
        sql.append("RFID_SERIAL as rfidSerial , TID as tid, EPC as epc, PLATE_NUMBER as plateNumber,");
        sql.append("VEHICLE_ID as vehicleId, SERVICE_FEE_NAME as serviceFeeName, SERVICE_FEE_ID as serviceFeeId, SALE_ORDER_DATE as saleOrderDate ");
        sql.append("From SALE_ORDER_DETAIL where service_plan_type_id = ? and plate_number =? and ");
        sql.append("station_id = ? and eff_date=to_timestamp(?,'yyyyMMddHH24MISS') and exp_date=to_timestamp(?,'yyyyMMddHH24MISS')");
        List<Object> arrParams = new ArrayList<>();
        arrParams.add(servicePlanTypeId);
        arrParams.add(plateNumber);
        arrParams.add(stationId);
        arrParams.add(effDate);
        arrParams.add(expDate);
        return (List<SaleOrderDetailEntity>) getListData(sql, arrParams, 0, null, SaleOrderDetailEntity.class);
    }

    @Override
    public List<SaleOrderDetailEntity> findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(
            Long servicePlanTypeId, String plateNumber, Long stageId, String effDate, String expDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("Select  SALE_ORDER_DETAIL_ID as  saleOrderDetailId,SALE_ORDER_ID as saleOrderId,");
        sql.append("CREATE_DATE as createDate, CREATE_USER as createUser, DESCRIPTION as description,");
        sql.append("PROMOTION_AMOUNT as promotionAmount, PROMOTION_ID as promotionId,");
        sql.append("AMOUNT as amount,DISCOUNT_ID as discountId, QUANTITY as quantity, PRICE as price,");
        sql.append("STAGE_ID as stageId ,STATION_ID as stationId, AUTO_RENEW_VTP as autoRenewVtp,");
        sql.append("AUTO_RENEW as autoRenew, EXP_DATE as expDate, EFF_DATE as effDate, OFFER_LEVEL as offerLevel,");
        sql.append("SCOPE as scope, OCS_CODE as ocsCode, SERVICE_PLAN_TYPE_ID as servicePlanTypeId,");
        sql.append("SERVICE_PLAN_NAME as servicePlanName, SERVICE_PLAN_ID as servicePlanId, VEHICLE_GROUP_ID as vehicleGroupId,");
        sql.append("RFID_SERIAL as rfidSerial , TID as tid, EPC as epc, PLATE_NUMBER as plateNumber,");
        sql.append("VEHICLE_ID as vehicleId, SERVICE_FEE_NAME as serviceFeeName, SERVICE_FEE_ID as serviceFeeId, SALE_ORDER_DATE as saleOrderDate ");
        sql.append("From SALE_ORDER_DETAIL where service_plan_type_id = ? and plate_number =? and ");
        sql.append("stage_id = ? and eff_date=to_timestamp(?,'yyyyMMddHH24MISS') and exp_date=to_timestamp(?,'yyyyMMddHH24MISS')");
        List<Object> arrParams = new ArrayList<>();
        arrParams.add(servicePlanTypeId);
        arrParams.add(plateNumber);
        arrParams.add(stageId);
        arrParams.add(effDate);
        arrParams.add(expDate);
        return (List<SaleOrderDetailEntity>) getListData(sql, arrParams, 0, null, SaleOrderDetailEntity.class);

    }
}
