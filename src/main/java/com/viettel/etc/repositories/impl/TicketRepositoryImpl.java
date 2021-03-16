package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.viettel.etc.utils.Constants.*;

/**
 * Autogen class: Lop thao tac mua ve thang quy
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@Service
public class TicketRepositoryImpl extends CommonDataBaseRepository implements TicketRepository {
    /**
     * Lay phi dang ky mua ve thang, quy
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public List<ServicePlanDTO> getFee(ServicePlanDTO itemParamsEntity) {


        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        //create sql query
        sql.append("select SERVICE_PLAN_ID                             as servicePlanId, \n");
        sql.append("FEE                                                as fee,                  -- Gia ve\n");
        sql.append("VEHICLE_GROUP_ID                                   as vehicleGroupId,       -- Loai PT thu phi\n");
        sql.append("SERVICE_PLAN_TYPE_ID                               as servicePlanTypeId,    -- Loai ve  \n");
        sql.append("CHARGE_METHOD_ID                                   as chargeMethodId,        -- Phuong thuc thu phi\n");
        sql.append("AUTO_RENEW                                         as autoRenew,             -- Cho phep gia han\n");
        sql.append("OCS_CODE                                           as ocsCode,            -- Ma ocs \n");
        sql.append("SCOPE                                              as scope            -- Pham vi ap dung  \n");
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

        //Lan ra
        if (itemParamsEntity.getLaneOut() != null) {
            sql.append("and LANE_OUT = :laneOut \n");
            hmapParams.put("laneOut", itemParamsEntity.getLaneOut());
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
        if (itemParamsEntity.getVehicleGroupId() != null) {
            sql.append("and VEHICLE_GROUP_ID = :vehicleGroupId \n");
            hmapParams.put("vehicleGroupId", itemParamsEntity.getVehicleGroupId());
        }

        //them dieu kien tim kiem ngay hieu luc
        if (itemParamsEntity.getCreateDateFrom() != null) {
            sql.append("and ((to_date(:effDate,'dd/MM/yyyy hh24:mi:ss') between START_DATE and END_DATE) \n");
            sql.append("or(START_DATE <= to_date(:effDate,'dd/MM/yyyy hh24:mi:ss') and END_DATE Is null)) \n");
            hmapParams.put("effDate", itemParamsEntity.getCreateDateFrom());
            hmapParams.put("effDate", itemParamsEntity.getCreateDateFrom());
        }

        sql.append("order by FEE \n");

        Integer start = null;
        Integer pageSize = null;

        //execute query
        List<ServicePlanDTO> listData = (List<ServicePlanDTO>) getListData(sql, hmapParams, start, pageSize, ServicePlanDTO.class);

        return listData;
    }

    /**
     * Check trung mua ve thang, quy
     *
     * @return
     */
    @Override
    public ServicePlanVehicleDuplicateDTO checkExistsTicket(ServicePlanDTO param, boolean isCallFromBoo1) {

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select ");
        if(isCallFromBoo1) {
            sql.append(" LISTAGG(ORG_PLATE_NUMBER, ',')  ");
        }else{
            sql.append(" LISTAGG(PLATE_NUMBER, ',')  ");
        }
        sql.append(" WITHIN GROUP (order by STAGE_ID) as plateNumber \n");
        sql.append("from SALE_TRANS_DETAIL \n");
        sql.append("where 1=1 \n");
        sql.append(" and status = 2  \n");
        if (param.getPlateNumber() != null) {
            if(isCallFromBoo1){
                sql.append("and ORG_PLATE_NUMBER = :plateNumber \n");
            }else{
                sql.append("and PLATE_NUMBER = :plateNumber \n");
            }
            hmapParams.put("plateNumber", param.getPlateNumber());
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
}
