package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanFeeDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;
import com.viettel.etc.repositories.ServicePlanRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.viettel.etc.utils.Constants.*;

/**
 * Autogen class Repository Impl: Lop thao tac danh sach ca nhan
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:20 ICT 2020
 */
@Repository
public class ServicePlanRepositoryImpl extends CommonDataBaseRepository implements ServicePlanRepository {

    /**
     * Tim kiem gia ve bang cuoc
     *
     * @param servicePlanDTO
     * @return
     */
    @Override
    public ResultSelectEntity searchTicketPrices(ServicePlanDTO servicePlanDTO) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select \n");
        sql.append("    SERVICE_PLAN_TYPE_ID                as servicePlanTypeId,           -- Loai ve\n");
        sql.append("    SCOPE                               as scope,                       -- Pham vi ap dung\n");
        sql.append("    STATION_ID                          as stationId,                   -- Ma Tram\n");
        sql.append("    STAGE_ID                            as stageId,                      -- Ma doan\n");
        sql.append("    LANE_IN                             as laneIn,                      -- Lan vao\n");
        sql.append("    LANE_OUT                            as laneOut,                     -- Lan ra\n");
        sql.append("    VEHICLE_GROUP_ID                    as vehicleGroupId,              -- Loai PT thu phi\n");
        sql.append("    FEE                                 as fee,                         -- Gia ve\n");
        sql.append("    OCS_CODE                            as ocsCode,                     -- Ma OCS\n");
        sql.append("    CONCAT(to_char(START_DATE, 'dd/mm/yyyy'),CONCAT('-' ,to_char(END_DATE, 'dd/mm/yyyy')))      as effDate,       -- Ngay hieu luc\n");
        sql.append("    CREATE_DATE                         as createDate,                  -- Ngay tao\n");
        sql.append("    CREATE_USER                         as createUser,                  -- Nguoi tao\n");
        sql.append("    STATUS                              as status,                      -- Trang thai\n");
        sql.append("    SERVICE_PLAN_ID                     as servicePlanId                \n");
        sql.append(" from SERVICE_PLAN WHERE STATUS != 0 \n");
        if (servicePlanDTO.getServicePlanTypeId() != null) {
            sql.append(" and SERVICE_PLAN_TYPE_ID = ?\n");
            arrParams.add(servicePlanDTO.getServicePlanTypeId());
        }
        if (servicePlanDTO.getScope() != null) {
            sql.append(" and SCOPE = ?\n");
            arrParams.add(servicePlanDTO.getScope());
        }
        if (servicePlanDTO.getVehicleGroupId() != null) {
            sql.append(" and VEHICLE_GROUP_ID = ?\n");
            arrParams.add(servicePlanDTO.getVehicleGroupId());
        }
        if (servicePlanDTO.getRouteId() != null) {
            sql.append(" and ROUTE_ID = ?\n");
            arrParams.add(servicePlanDTO.getRouteId());
        }
        if (servicePlanDTO.getStageId() != null) {
            sql.append(" and STAGE_ID = ?\n");
            arrParams.add(servicePlanDTO.getStageId());
        }
        if (servicePlanDTO.getStationId() != null) {
            sql.append(" and STATION_ID = ?\n");
            arrParams.add(servicePlanDTO.getStationId());
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getCreateDateFrom())) {
            sql.append(" and to_date(? , 'dd/MM/yyyy') <= CREATE_DATE\n");
            arrParams.add(servicePlanDTO.getCreateDateFrom());
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getCreateDateTo())) {
            sql.append(" and to_date(? , 'dd/MM/yyyy HH24:mi:ss') >= CREATE_DATE\n");
            arrParams.add(servicePlanDTO.getCreateDateTo() + Constants.TIME_ONE_DAY);
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getApproveDateFrom())) {
            sql.append(" and to_date(? , 'dd/MM/yyyy') <= APPROVER_DATE\n");
            arrParams.add(servicePlanDTO.getApproveDateFrom());
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getApproveDateTo())) {
            sql.append(" and to_date(? , 'dd/MM/yyyy HH24:mi:ss') >= APPROVER_DATE\n");
            arrParams.add(servicePlanDTO.getApproveDateTo() + Constants.TIME_ONE_DAY);
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getStartDateFrom())) {
            sql.append(" and (to_date( ? , 'dd/mm/yyyy') < END_DATE  + 1 or END_DATE is null)\n");
            arrParams.add(servicePlanDTO.getStartDateFrom());
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getEndDateTo())) {
            sql.append(" and (to_date( ? ,'dd/mm/yyyy') >= START_DATE)\n");
            arrParams.add(servicePlanDTO.getEndDateTo());
        }
        if (servicePlanDTO.getStatus() != null) {
            sql.append(" and STATUS =?\n");
            arrParams.add(servicePlanDTO.getStatus());
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getOcsCode())) {
            sql.append(" and OCS_CODE =?\n");
            arrParams.add(servicePlanDTO.getOcsCode());
        }
        if (servicePlanDTO.getEffId() != null && servicePlanDTO.getEffId() == 1) {
            sql.append(" and STATUS =2 and to_date(sysdate, 'dd/mm/yyyy') BETWEEN TO_DATE(START_DATE, 'dd/mm/yyyy') AND TO_DATE(END_DATE, 'dd/mm/yyyy')\n");
        }
        if (servicePlanDTO.getEffId() != null && servicePlanDTO.getEffId() == 2) {
            sql.append(" and STATUS =2 and to_date(sysdate, 'dd/mm/yyyy') NOT BETWEEN TO_DATE(START_DATE, 'dd/mm/yyyy') AND TO_DATE(END_DATE, 'dd/mm/yyyy')\n");
        }
        sql.append(" ORDER BY STATUS, UPDATE_DATE,CREATE_DATE DESC");
        Integer start = 0;
        if (servicePlanDTO.getStartrecord() != null) {
            start = servicePlanDTO.getStartrecord();
        }
        Integer pageSize = null;
        if (servicePlanDTO.getPagesize() != null) {
            pageSize = servicePlanDTO.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, ServicePlanDTO.class);
    }


    /**
     * Lay phi dang ky mua ve thang, quy
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ServicePlanFeeDTO getFee(ServicePlanDTO itemParamsEntity) {

        ServicePlanFeeDTO result = new ServicePlanFeeDTO();

        if (itemParamsEntity.getPlateNumber() != null) {
            result.setServicePlanVehicleDuplicate(checkExistsTicket(itemParamsEntity).getPlateNumber());
        } else {
            result.setServicePlanVehicleDuplicate(null);
        }

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        //create sql query
        sql.append("select SERVICE_PLAN_ID                             as servicePlanId, \n");
        sql.append("FEE                                                as fee,                  -- Gia ve\n");
        sql.append("VEHICLE_GROUP_ID                                   as vehicleGroupId,       -- Loai PT thu phi\n");
        sql.append("SERVICE_PLAN_TYPE_ID                               as servicePlanTypeId,    -- Loai ve  \n");
        sql.append("CHARGE_METHOD_ID                                   as chargeMethodId,       -- Phuong thuc thu phi\n");
        sql.append("OCS_CODE                                           as ocsCode,              -- Ma OCS\n");
        sql.append("AUTO_RENEW                                         as autoRenew             -- Cho phep gia han\n");
        sql.append("from SERVICE_PLAN  \n");
        sql.append("where 1=1 \n");

        //Doan
        if (itemParamsEntity.getStageId() != null) {
            sql.append("and STAGE_ID = :stageId \n");
            hmapParams.put("stageId", itemParamsEntity.getStageId());
        }

        //Tram
        if (itemParamsEntity.getStationId() != null) {
            sql.append("and STATION_ID = :stationId \n");
            hmapParams.put("stationId", itemParamsEntity.getStationId());
        }

        //Lan vao
        if (itemParamsEntity.getLaneIn() != null) {
            sql.append("and LANE_IN = :laneIn \n");
            hmapParams.put("laneIn", itemParamsEntity.getLaneIn());
        }

        //Lan ra
        if (itemParamsEntity.getLaneOut() != null) {
            sql.append("and LANE_OUT = :laneOut \n");
            hmapParams.put("laneOut", itemParamsEntity.getLaneOut());
        } else {
            sql.append("and LANE_OUT is NULL \n");
        }

        //Cach tinh
        if (itemParamsEntity.getChargeMethodId() != null) {
            sql.append("and CHARGE_METHOD_ID = :chargeMethodId \n");
            hmapParams.put("chargeMethodId", itemParamsEntity.getChargeMethodId());
        }

        //Loai ve
        if (itemParamsEntity.getServicePlanTypeId() != null) {
            sql.append("and SERVICE_PLAN_TYPE_ID = :servicePlanTypeId \n");
            hmapParams.put("servicePlanTypeId", itemParamsEntity.getServicePlanTypeId());
        }

        //Ma nhom phuong tien thu phi
        if (itemParamsEntity.getVehicleGroupIdArr() != null) {
            sql.append("and VEHICLE_GROUP_ID in");
            sql.append("(");
            sql.append(itemParamsEntity.getVehicleGroupIdArr());
            sql.append(") \n");
        }

        //Cho phep gia han
        if (itemParamsEntity.getAutoRenew() != null) {
            sql.append("and AUTO_RENEW = :autoRenew \n");
            hmapParams.put("autoRenew", itemParamsEntity.getAutoRenew());
        }

        //them dieu kien tim kiem ngay hieu luc
        if (itemParamsEntity.getCreateDateFrom() != null) {
            sql.append("and ((to_date(:effDate,'" + COMMON_DATE_FORMAT + "') between START_DATE and END_DATE) \n");
            sql.append("or(START_DATE <= to_date(:effDate,'" + COMMON_DATE_FORMAT + "') and END_DATE Is null)) \n");
            hmapParams.put("effDate", itemParamsEntity.getCreateDateFrom());
            hmapParams.put("effDate", itemParamsEntity.getCreateDateFrom());
        }

        //them dieu kien tim kiem theo trang thai
        if (itemParamsEntity.getStatus() != null) {
            sql.append("and STATUS = :status \n");
            hmapParams.put("status", itemParamsEntity.getStatus());
        }

        sql.append("order by FEE \n");

        Integer start = null;
        Integer pageSize = null;

        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }

        //execute query
        List<ServicePlanDTO> listData = (List<ServicePlanDTO>) getListData(sql, hmapParams, start, pageSize, ServicePlanDTO.class);
        result.setListServicePlan(listData);
        //return
        return result;
    }

    /**
     * Lay phi dang ky mua ve thang, quy
     *
     * @param dataParam: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getFeeMobile(ServicePlanDTO dataParam) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        //create sql query
        sql.append("select SERVICE_PLAN_ID                             as servicePlanId, \n");
        sql.append("FEE                                                as fee,                  -- Gia ve\n");
        sql.append("VEHICLE_GROUP_ID                                   as vehicleGroupId,       -- Loai PT thu phi\n");
        sql.append("SERVICE_PLAN_TYPE_ID                               as servicePlanTypeId,    -- Loai ve  \n");
        sql.append("CHARGE_METHOD_ID                                   as chargeMethodId        --Phuong thuc thu phi\n");
        sql.append("from SERVICE_PLAN  \n");
        sql.append("where 1=1 \n");

        //Doan
        if (dataParam.getStageId() != null) {
            sql.append("and STAGE_ID = :stageId \n");
            hmapParams.put("stageId", dataParam.getStageId());
        }

        //Tram
        if (dataParam.getStationId() != null) {
            sql.append("and STATION_ID = :stationId \n");
            hmapParams.put("stationId", dataParam.getStationId());
            sql.append("and STATION_TYPE = 1 ");
        }

        //Ma nhom phuong tien thu phi
        if (dataParam.getVehicleGroupIdArr() != null) {
            sql.append("and VEHICLE_GROUP_ID in");
            sql.append("(").append(dataParam.getVehicleGroupIdArr()).append(") ");
        }

        if (dataParam.getServicePlanTypeId() != null) {
            sql.append("and SERVICE_PLAN_TYPE_ID = :servicePlanTypeId");
            hmapParams.put("servicePlanTypeId", dataParam.getServicePlanTypeId());
        }

        //Cho phep gia han
        if (dataParam.getAutoRenew() != null) {
            sql.append("and AUTO_RENEW = :autoRenew \n");
            hmapParams.put("autoRenew", dataParam.getAutoRenew());
        }

        Integer start = null;
        Integer pageSize = null;

        if (dataParam.getStartrecord() != null) {
            start = dataParam.getStartrecord();
        }

        if (dataParam.getPagesize() != null) {
            pageSize = dataParam.getPagesize();
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, ServicePlanDTO.class);
    }

    /**
     * Tim kiem gia ve nho nhat theo station id
     *
     * @param servicePlanDTO
     * @return
     */
    @Override
    public ResultSelectEntity getMinFee(ServicePlanDTO servicePlanDTO) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select \n");
        sql.append("    MIN(FEE)                                as fee,                             -- Gia ve nho nhat\n");
        sql.append("    STATION_ID                              as stationId,                       -- Ma doan\n");
        sql.append("    STAGE_ID                                as stageId                          -- Ma tram\n");
        sql.append(" FROM SERVICE_PLAN ");
        sql.append(" where ((sysdate between START_DATE AND END_DATE) OR (sysdate > START_DATE AND END_DATE IS NULL))");
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getStationIdList())) {
            sql.append(" and STATION_ID IN  (").append(servicePlanDTO.getStationIdList()).append(") GROUP BY STATION_ID,STAGE_ID \n");
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getStageIdList())) {
            sql.append(" and STAGE_ID IN  (").append(servicePlanDTO.getStageIdList()).append(") GROUP BY STAGE_ID,STATION_ID \n");
        }
        Integer start = 0;
        if (servicePlanDTO.getStartrecord() != null) {
            start = servicePlanDTO.getStartrecord();
        }
        Integer pageSize = null;
        if (servicePlanDTO.getPagesize() != null) {
            pageSize = servicePlanDTO.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, ServicePlanDTO.class);
    }

    /**
     * Tim kiem gia ve theo loai PT, tram, doan, lan vao, lan ra
     *
     * @param dataParams
     * @return
     */
    @Override
    public ResultSelectEntity findTicketPricesExits(ServicePlanDTO dataParams) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select  \n");
        sql.append(" START_DATE             as startDate                -- Ngay bat dau hieu luc \n");
        sql.append(" ,END_DATE              as endDate                  -- Ngay het hieu luc \n");
        sql.append(" ,SERVICE_PLAN_ID       as servicePlanId            -- Ngay het hieu luc \n");
        sql.append(" FROM SERVICE_PLAN where 1=1 ");
        if (dataParams.getScope() != null) {
            sql.append(" AND SCOPE = ? \n");
            arrParams.add(dataParams.getScope());
        }
        if (dataParams.getVehicleGroupId() != null) {
            sql.append(" AND VEHICLE_GROUP_ID = ? \n");
            arrParams.add(dataParams.getVehicleGroupId());
        }
        if (dataParams.getStationId() != null) {
            sql.append(" AND STATION_ID = ? \n");
            arrParams.add(dataParams.getStationId());
        }
        if (dataParams.getStageId() != null) {
            sql.append(" AND STAGE_ID = ? \n");
            arrParams.add(dataParams.getStageId());
        }
        if (dataParams.getLaneOut() != null) {
            sql.append(" AND LANE_OUT = ? \n");
            arrParams.add(dataParams.getLaneOut());
        }
        if (dataParams.getLaneIn() != null) {
            sql.append(" AND LANE_IN = ? \n");
            arrParams.add(dataParams.getLaneIn());
        }

        Integer start = 0;
        if (dataParams.getStartrecord() != null) {
            start = dataParams.getStartrecord();
        }
        Integer pageSize = null;
        if (dataParams.getPagesize() != null) {
            pageSize = dataParams.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, ServicePlanDTO.class);
    }

    /***
     * Get thong tin trung the
     * @param param
     * @return
     */
    private ServicePlanVehicleDuplicateDTO checkExistsTicket(ServicePlanDTO param) {

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select LISTAGG(PLATE_NUMBER, ',') WITHIN GROUP (order by STAGE_ID) as plateNumber \n");
        sql.append("from SALE_TRANS_DETAIL \n");
        sql.append("where 1=1 \n");

        if (param.getPlateNumber() != null) {
            sql.append("and PLATE_NUMBER in");
            sql.append("(");
            sql.append(param.getPlateNumber());
            sql.append(") \n");
        }

        if (param.getStationId() != null) {
            sql.append("and STATION_ID = :stationId \n");
            hmapParams.put("stationId", param.getStationId());
        }

        if (param.getStageId() != null) {
            sql.append("and STAGE_ID = :stageId \n");
            hmapParams.put("stageId", param.getStageId());
        }

        if (param.getLaneOut() != null) {
            sql.append("and LANE_OUT = :laneOut \n");
            hmapParams.put("laneOut", param.getLaneOut());
        }

        if (param.getCreateDateFrom() != null && param.getCreateDateTo() != null) {
            sql.append("and( to_date(:startDate,'" + COMMON_DATE_FORMAT + "') between EFF_DATE and EXP_DATE \n");
            hmapParams.put("startDate", param.getCreateDateFrom());

            sql.append("or EFF_DATE between to_date(:startDate,'" + COMMON_DATE_FORMAT + "') and to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "')) \n");
            hmapParams.put("startDate", param.getCreateDateFrom());
            hmapParams.put("endDate", param.getCreateDateTo() + TIME_ONE_DAY);
        }

        return (ServicePlanVehicleDuplicateDTO) getFirstData(sql, hmapParams, ServicePlanVehicleDuplicateDTO.class);
    }

    /**
     * Tim kiem gia ve da ton tai ngay hieu luc
     *
     * @param servicePlanDTO
     * @return
     */
    @Override
    public ResultSelectEntity findTicketPriceExitsEffDate(ServicePlanDTO servicePlanDTO) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select  \n");
        sql.append(" START_DATE             as startDate                -- Ngay bat dau hieu luc \n");
        sql.append(" ,END_DATE              as endDate                  -- Ngay het hieu luc \n");
        sql.append(" ,SERVICE_PLAN_ID       as servicePlanId            -- Id \n");
        sql.append(" FROM SERVICE_PLAN where 1=1 ");
        if (servicePlanDTO.getVehicleGroupId() != null) {
            sql.append(" AND VEHICLE_GROUP_ID = ? \n");
            arrParams.add(servicePlanDTO.getVehicleGroupId());
        }
        if (servicePlanDTO.getStationId() != null && servicePlanDTO.getLaneOut() != null) {
            sql.append(" AND STATION_ID = ? and LANE_OUT = ?\n");
            arrParams.add(servicePlanDTO.getStationId());
            arrParams.add(servicePlanDTO.getLaneOut());
        }
        if (servicePlanDTO.getStationId() != null && servicePlanDTO.getLaneOut() == null) {
            sql.append(" AND STATION_ID = ? and LANE_OUT is null\n");
            arrParams.add(servicePlanDTO.getStationId());
        }
        if (servicePlanDTO.getStageId() != null && servicePlanDTO.getLaneOut() != null) {
            sql.append(" AND STAGE_ID = ? and LANE_OUT = ?\n");
            arrParams.add(servicePlanDTO.getStageId());
            arrParams.add(servicePlanDTO.getLaneOut());
        }
        if (servicePlanDTO.getStageId() != null && servicePlanDTO.getLaneOut() == null) {
            sql.append(" AND STAGE_ID = ? and LANE_OUT is null\n");
            arrParams.add(servicePlanDTO.getStageId());
        }
        if (servicePlanDTO.getAutoRenew() != null) {
            sql.append(" AND AUTO_RENEW = ? \n");
            arrParams.add(servicePlanDTO.getAutoRenew());
        }
        if (servicePlanDTO.getServicePlanTypeId() != null) {
            sql.append(" AND SERVICE_PLAN_TYPE_ID = ? \n");
            arrParams.add(servicePlanDTO.getServicePlanTypeId());
        }
        if (servicePlanDTO.getStartDate() != null) {
            sql.append(" AND (to_date(? , 'dd/MM/yyyy') < END_DATE +1 or END_DATE is null)\n");
            arrParams.add(FnCommon.convertDateToString(servicePlanDTO.getStartDate(), false, Constants.SLASH));
        }
        if (servicePlanDTO.getEndDate() != null) {
            sql.append(" AND (to_date(? , 'dd/MM/yyyy') >= START_DATE)\n");
            arrParams.add(FnCommon.convertDateToString(servicePlanDTO.getEndDate(), false, Constants.SLASH));
        }
        sql.append(" and status = 2 \n");

        Integer start = 0;
        if (servicePlanDTO.getStartrecord() != null) {
            start = servicePlanDTO.getStartrecord();
        }
        Integer pageSize = null;
        if (servicePlanDTO.getPagesize() != null) {
            pageSize = servicePlanDTO.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, ServicePlanDTO.class);
    }

    /**
     * Check ve da ton tai hay chua
     *
     * @param param
     * @return
     */
    @Override
    public ServicePlanVehicleDuplicateDTO checkExistsTicketNew(ServicePlanDTO param) {

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select PLATE_NUMBER as plateNumber \n");
        sql.append("from SALE_TRANS_DETAIL \n");
        sql.append("where 1=1 \n");
        sql.append(" and status = 2  \n");
        if (param.getPlateNumber() != null) {
            sql.append("and PLATE_NUMBER  = :PLATE_NUMBER ");
            hmapParams.put("PLATE_NUMBER", param.getPlateNumber());
        }

        if (param.getStationId() != null) {
            sql.append("and STATION_ID = :stationId \n");
            hmapParams.put("stationId", param.getStationId());
        }

        if (param.getStageId() != null) {
            sql.append("and STAGE_ID = :stageId \n");
            hmapParams.put("stageId", param.getStageId());
        }

        if (param.getLaneOut() != null) {
            sql.append("and LANE_OUT = :laneOut \n");
            hmapParams.put("laneOut", param.getLaneOut());
        }

        if (param.getEffDate() != null && param.getExpDate() != null) {
            sql.append("and to_date(:startDate,'" + COMMON_DATE_FORMAT + "') <= TRUNC(EXP_DATE)  \n");
            hmapParams.put("startDate", param.getEffDate());

            sql.append("and to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "')  >= EFF_DATE ");
            hmapParams.put("endDate", param.getExpDate());
        }
        return (ServicePlanVehicleDuplicateDTO) getFirstData(sql, hmapParams, ServicePlanVehicleDuplicateDTO.class);
    }

    /***
     * Tra cuu gia ve (lien thong boo)
     * @param servicePlanDTO
     * @return
     */
    @Override
    public ResultSelectEntity getFeeNew(ServicePlanDTO servicePlanDTO, Long excVehicleId, Long excTicketId, List<String> vehicleGroupId, Long vehicleGroupIdOld) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append(" SELECT \n");
        sql.append(" sp.fee                     AS fee,                    -- Gia ve \n");
        sql.append(" sp.vehicle_group_id        AS vehicleGroupId                     -- Loai PT thu phi \n");
        sql.append(", sp.SERVICE_PLAN_ID         AS servicePlanId,                 -- Id bang gia cuoc \n");
        sql.append(" sp.SERVICE_PLAN_TYPE_ID    AS servicePlanTypeId,             -- Ma danh muc, loai goi dich vu \n");
        sql.append(" sp.CHARGE_METHOD_ID        AS chargeMethodId,                 -- Ma phuong thuc tinh phi \n");
        sql.append(" sp.OCS_CODE                AS ocsCode,                         -- Ma ocs \n");
        sql.append(" sp.AUTO_RENEW              AS autoRenew,                      -- Tu dong gia han \n");
        sql.append(" sp.SCOPE                   AS scope,                            -- Pham vi ap dung \n");
        sql.append(" sp.stage_id                AS stageId                            -- Doan \n");
        sql.append(" FROM \n");
        sql.append(" service_plan sp \n");
        sql.append(" WHERE \n");
        sql.append(" 1 = 1 \n");
        sql.append(" AND sp.STATUS = 2 \n");

        Integer start = 0;
        Integer pageSize = null;

        if (!FnCommon.isNullOrEmpty(vehicleGroupId)) {
            sql.append(" AND sp.vehicle_group_id IN (:groupIds)\n");
            hmapParams.put("groupIds", vehicleGroupId.get(0));
        }
        if (servicePlanDTO.getVehicleGroupId() != null && FnCommon.isNullOrEmpty(vehicleGroupId)) {
            sql.append(" AND sp.vehicle_group_id IN ( \n");
            sql.append(servicePlanDTO.getVehicleGroupId());
            sql.append(")\n");
        }
        if (servicePlanDTO.getStationId() != null && servicePlanDTO.getStageId() == null) {
            sql.append(" AND sp.station_id =:station_id\n");
            hmapParams.put("station_id", servicePlanDTO.getStationId());
        }
        if (servicePlanDTO.getStageId() != null) {
            sql.append(" AND sp.STAGE_ID =:stage_id\n");
            hmapParams.put("stage_id", servicePlanDTO.getStageId());
        }
        if (servicePlanDTO.getChargeMethodId() != null) {
            sql.append("and sp.CHARGE_METHOD_ID = :chargeMethodId \n");
            hmapParams.put("chargeMethodId", servicePlanDTO.getChargeMethodId());
        }
        //them dieu kien tim kiem ngay hieu luc
        if (servicePlanDTO.getEffDate() != null) {
            sql.append("and ((to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT + "') between START_DATE and END_DATE) \n");
            sql.append("or(START_DATE <= to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT + "') and END_DATE Is null)) \n");
            hmapParams.put("effDate", servicePlanDTO.getEffDate());
            hmapParams.put("effDate", servicePlanDTO.getEffDate());
        }

        if (servicePlanDTO.getServicePlanTypeId() != null) {
            sql.append(" AND sp.service_plan_type_id =:service_plan_type_id");
            hmapParams.put("service_plan_type_id", servicePlanDTO.getServicePlanTypeId());
        }

        if (excVehicleId != null) {
            sql.append(" and sp.promotion_id =:excVehicleId ");
            hmapParams.put("excVehicleId", excVehicleId);
        }
        if (excTicketId != null && excVehicleId == null) {
            sql.append(" and sp.promotion_id =:promotionId ");
            hmapParams.put("promotionId", excTicketId);
        }
        if (excTicketId == null && excVehicleId == null) {
            sql.append(" and sp.promotion_id is null ");
        }
        if (excTicketId != null && excVehicleId != null) {
            sql.append(" UNION ALL (");
            sql.append(" SELECT \n");
            sql.append(" sp.fee                     AS fee,                    -- Gia ve \n");
            sql.append(" sp.vehicle_group_id        AS vehicleGroupId                     -- Loai PT thu phi \n");
            sql.append(", sp.SERVICE_PLAN_ID         AS servicePlanId,                 -- Id bang gia cuoc \n");
            sql.append(" sp.SERVICE_PLAN_TYPE_ID    AS servicePlanTypeId,             -- Ma danh muc, loai goi dich vu \n");
            sql.append(" sp.CHARGE_METHOD_ID        AS chargeMethodId,                 -- Ma phuong thuc tinh phi \n");
            sql.append(" sp.OCS_CODE                AS ocsCode,                         -- Ma ocs \n");
            sql.append(" sp.AUTO_RENEW              AS autoRenew,                      -- Tu dong gia han \n");
            sql.append(" sp.SCOPE                   AS scope,                            -- Pham vi ap dung \n");
            sql.append(" sp.stage_id                AS stageId                            -- Doan \n");
            sql.append(" FROM \n");
            sql.append(" service_plan sp \n");
            sql.append(" WHERE \n");
            sql.append(" 1 = 1 \n");
            sql.append(" AND sp.STATUS = 2 \n");


            if (vehicleGroupIdOld != null) {
                sql.append(" AND sp.vehicle_group_id IN (:groupIds)\n");
                hmapParams.put("groupIds", vehicleGroupIdOld);
            }

            if (servicePlanDTO.getStationId() != null && servicePlanDTO.getStageId() == null) {
                sql.append(" AND sp.station_id =:station_id\n");
                hmapParams.put("station_id", servicePlanDTO.getStationId());
            }
            if (servicePlanDTO.getStageId() != null) {
                sql.append(" AND sp.STAGE_ID =:stage_id\n");
                hmapParams.put("stage_id", servicePlanDTO.getStageId());
            }
            if (servicePlanDTO.getChargeMethodId() != null) {
                sql.append("and sp.CHARGE_METHOD_ID = :chargeMethodId \n");
                hmapParams.put("chargeMethodId", servicePlanDTO.getChargeMethodId());
            }
            //them dieu kien tim kiem ngay hieu luc
            if (servicePlanDTO.getEffDate() != null) {
                sql.append("and ((to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT + "') between START_DATE and END_DATE) \n");
                sql.append("or(START_DATE <= to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT + "') and END_DATE Is null)) \n");
                hmapParams.put("effDate", servicePlanDTO.getEffDate());
                hmapParams.put("effDate", servicePlanDTO.getEffDate());
            }

            if (servicePlanDTO.getServicePlanTypeId() != null) {
                sql.append(" AND sp.service_plan_type_id =:service_plan_type_id");
                hmapParams.put("service_plan_type_id", servicePlanDTO.getServicePlanTypeId());
            }
            sql.append(" and sp.promotion_id =:promotionId ");
            hmapParams.put("promotionId", excTicketId);
            sql.append(")");
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, ServicePlanDTO.class);
    }

    @Override
    public ServicePlanDTO getServicePlan(Long stationId, Long stageId, Long servicePlanTypeId, Long vehicleGroupId, String effDate, String expDate, String offerId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select  \n");
        sql.append(" SERVICE_PLAN_ID                as servicePlanId,                -- ma gia ve \n");
        sql.append(" SERVICE_PLAN_NAME              as servicePlanName,                -- ten gia ve \n");
        sql.append(" SERVICE_PLAN_TYPE_ID           as servicePlanTypeId,                -- \n");
        sql.append(" OCS_CODE                       as ocsCode,                -- \n");
        sql.append(" SCOPE                          as scope,                -- \n");
        sql.append(" FEE                            as fee,                -- \n");
        sql.append(" PROMOTION_ID                   as promotionId                -- \n");
        sql.append(" FROM SERVICE_PLAN where STATUS = 2 and OCS_CODE = ?");
        arrParams.add(offerId);
        if (servicePlanTypeId == null) {
            sql.append(" AND PROMOTION_ID is not null \n");
        } else {
            sql.append(" AND PROMOTION_ID is null \n");
        }
        if (stationId != null && stationId > 0) {
            sql.append(" AND STATION_ID = ? \n");
            arrParams.add(stationId);
        }
        if (stageId != null && stageId > 0) {
            sql.append(" AND STAGE_ID = ? \n");
            arrParams.add(stageId);
        }

        if (servicePlanTypeId != null) {
            sql.append(" AND SERVICE_PLAN_TYPE_ID = ? \n");
            arrParams.add(servicePlanTypeId);
        }
        if (vehicleGroupId != null) {
            sql.append(" AND VEHICLE_GROUP_ID = ? \n");
            arrParams.add(vehicleGroupId);
        }

        if(!FnCommon.isNullOrEmpty(effDate)){
            sql.append(" AND START_DATE < to_date(?, 'dd/mm/yyyy hh24:mi:ss') \n");
            arrParams.add(effDate);
        }

        if(!FnCommon.isNullOrEmpty(expDate)){
            sql.append("AND (END_DATE is null OR END_DATE > to_date(?, 'dd/mm/yyyy hh24:mi:ss')) \n");
            arrParams.add(expDate);
        }
        ResultSelectEntity result = getListDataAndCount(sql, arrParams, null, null, ServicePlanDTO.class);
        if (result != null && result.getListData().size() > 0) {
            return (ServicePlanDTO) result.getListData().get(0);
        } else {
            return null;
        }
    }
}
