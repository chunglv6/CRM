package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.boo.*;
import com.viettel.etc.repositories.BooRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Repository
public class BooRepositoryImpl extends CommonDataBaseRepository implements BooRepository {
    @Override
    public DataBooDTO findByPlateNumberAndEpc(String plateNumber, String epc) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        DataBooDTO result = null;
        sql.append("select ");
        sql.append("ve.vehicle_id as vehicleId,ve.vehicle_type_code as vehicleType,ve.plate_type_code as plateType,");
        sql.append("ve.tid as tId,ve.RFID_SERIAL as rfIdSerial,");
        sql.append(" cu.cust_id as custId,cu.cust_name as custName,");
        sql.append("con.contract_id as contractId,con.contract_no as contractNo ");
        sql.append("from vehicle ve inner join customer cu ");
        sql.append("on ve.cust_id = cu.cust_id ");
        sql.append(" inner join contract con on ve.contract_id = con.contract_id where ve.plate_number = ? and ve.epc = ?");
        arrParams.add(plateNumber);
        arrParams.add(epc);
        Object data = getFirstData(sql, arrParams, DataBooDTO.class);
        if (data != null) {
            result = new DataBooDTO().convertToDataBooDTO(data);
        }
        return result;
    }


    /***
     * Thuc hien truy van tra cuu lich su mua ve
     * @param queryTicketBooDTO
     * @return
     */
    @Override
    public ResultSelectEntity queryTicketBoo(ReqQueryTicketDTO queryTicketBooDTO) {
        StringBuilder sb = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sb.append(" select * from (\n");
        sb.append(" SELECT \n");
        sb.append(" std.subscription_ticket_id AS subscription_ticket_id,    -- Mã vé tháng / quý \n");
        sb.append(" sp.fee                AS price_amount,              -- Giá tiền         \n");
        sb.append(" CASE         \n");
        sb.append(" WHEN to_date(sysdate, 'dd/mm/yyyy') BETWEEN to_date(std.eff_date, 'dd/mm/yyyy') AND to_date(std.exp_date, 'dd/mm/yyyy') THEN 1 ELSE 0 END         \n");
        sb.append(" AS status,                                          -- 1: hiệu lực; 0: hết hiệu lực\n");
        sb.append(" CASE \n");
        sb.append(" WHEN sp.station_type = 1 THEN 'O' ELSE 'C'\n");
        sb.append(" END AS  station_type,                              -- Trạm kín: C, Trạm mở: O          \n");
        sb.append(" CASE \n");
        sb.append(" WHEN sp.station_type = 1 THEN sp.station_id \n");
        sb.append(" ELSE sp.station_in_id END AS station_in_id,       -- Mã trạm vào (trường hợp trạm kín), mã trạm (trạm mở)\n");
        sb.append(" CASE \n");
        sb.append(" WHEN sp.station_type = 1 THEN sp.station_id \n");
        sb.append(" ELSE sp.station_out_id END AS station_out_id,       -- Mã trạm ra (trường hợp trạm kín), mã trạm (trạm mở). Trường hợp trạm mở station_in_id = station_out_id\n");
        sb.append(" \n");
        sb.append(" CASE sp.service_plan_type_id \n");
        sb.append(" WHEN 1  THEN 'L' \n");
        sb.append(" WHEN 2  THEN 'W' \n");
        sb.append(" WHEN 3  THEN 'B' \n");
        sb.append(" WHEN 4  THEN 'T' \n");
        sb.append(" WHEN 5  THEN 'Q' \n");
        sb.append(" WHEN 6  THEN 'N' \n");
        sb.append(" WHEN 7  THEN 'E' \n");
        sb.append(" END                 as ticket_type,             -- Loại vé: L,T,Q,N\n");
        sb.append(" to_char(std.eff_date, 'yyyymmdd') as start_date,  -- Ngày bắt đầu hiệu lực vé tháng quý, format: YYYYMMDD\n");
        sb.append(" to_char(std.exp_date, 'yyyymmdd') as end_date,    -- Ngày kết thúc hiệu lực vé tháng quý, format: YYYYMMDD\n");
        sb.append(" std.ORG_PLATE_NUMBER as plate,                          -- Biển số\n");
        sb.append(" v.epc as etag,                                      -- etag\n");
        sb.append(" v.vehicle_group_id as vehicle_type,                  -- Loại xe tính phí, chưa chắc đã khớp với loại xe truyền vào\n");
        sb.append(" v.vehicle_type_id as register_vehicle_type,        -- Loại xe đăng kiểm\n");
        sb.append(" v.seat_number as seat,                              -- Số ghế\n");
        sb.append(" v.cargo_weight as weight_goods,                     -- cân nặng hàng hóa\n");
        sb.append(" v.gross_weight as weight_all                        -- cân nặng toàn bộ\n");
        sb.append(" FROM \n");
        sb.append(" service_plan       sp, \n");
        sb.append(" (select s.* from sale_trans s where s.sale_trans_type = 2)  st, \n");
        sb.append(" sale_trans_detail  std, \n");
        sb.append(" vehicle v \n");
        sb.append(" WHERE \n");
        sb.append(" std.sale_trans_id  = st.sale_trans_id \n");
        sb.append(" and \n");
        sb.append(" std.vehicle_id = v.vehicle_id \n");
        sb.append(" and \n");
        sb.append(" sp.service_plan_id = std.service_plan_id\n");
        sb.append(" ) where 1= 1\n");

        Integer start = 0;
        Integer pageSize = null;
        if (queryTicketBooDTO != null) {

            if (queryTicketBooDTO.getSubscription_ticket_id() != null) {
                sb.append(" AND ");
                sb.append(" subscription_ticket_id = ? \n");
                arrParams.add(queryTicketBooDTO.getSubscription_ticket_id());
            }
            if (!FnCommon.isNullOrEmpty(queryTicketBooDTO.getEtag())) {
                sb.append(" AND ");
                sb.append(" etag = ?");
                sb.append("\n");
                arrParams.add(queryTicketBooDTO.getEtag());
            }
            if (!FnCommon.isNullOrEmpty(queryTicketBooDTO.getPlate())) {
                sb.append(" AND ");
                sb.append(" plate = ? \n");
                arrParams.add(queryTicketBooDTO.getPlate());
            }
            if (queryTicketBooDTO.getStation_in_id() != null) {
                sb.append(" AND ");
                sb.append(" station_in_id = ? \n");
                arrParams.add(queryTicketBooDTO.getStation_in_id());
            }
            if (queryTicketBooDTO.getStation_out_id() != null) {
                sb.append(" AND ");
                sb.append(" station_out_id = ? \n");
                arrParams.add(queryTicketBooDTO.getStation_out_id());
            }
        }

        return getListDataAndCount(sb, arrParams, start, pageSize, ResQueryTicketDTO.ListTicket.class);
    }

    /***
     * Tra cuu gia ve (lien thong boo)
     * @param reqCalculatorTicketDTO
     * @return
     */
    @Override
    public ResultSelectEntity getFeeBoo(ReqCalculatorTicketDTO reqCalculatorTicketDTO, List<String> vehicleGroupId, Long stageId, Long methodChargeId, Long excVehicleId, Long excTicketId,Long vehicleGroupIdOld, boolean isCallFromBOO) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append(" SELECT \n");
        sql.append(" sp.fee                     AS price_amount,                    -- Gia ve \n");
        sql.append(" sp.vehicle_group_id        AS vehicle_type                     -- Loai PT thu phi \n");
        if (!isCallFromBOO) {
            sql.append(", sp.SERVICE_PLAN_ID         AS service_plan_id,                 -- Id bang gia cuoc \n");
            sql.append(" sp.SERVICE_PLAN_TYPE_ID    AS service_plan_type_id,             -- Ma danh muc, loai goi dich vu \n");
            sql.append(" sp.CHARGE_METHOD_ID        AS charge_method_id,                 -- Ma phuong thuc tinh phi \n");
            sql.append(" sp.OCS_CODE                AS ocs_code,                         -- Ma ocs \n");
            sql.append(" sp.AUTO_RENEW              AS auto_renew,                      -- Tu dong gia han \n");
            sql.append(" sp.SCOPE                   AS scope,                            -- Pham vi ap dung \n");
            sql.append(" sp.stage_id                AS stage_id                            -- Doan \n");
        }
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
        if (reqCalculatorTicketDTO.getVehicle_type() != null && FnCommon.isNullOrEmpty(vehicleGroupId)) {
            sql.append(" AND sp.vehicle_group_id IN ( \n");
            sql.append(reqCalculatorTicketDTO.getVehicle_type());
            sql.append(")\n");
        }

        if (reqCalculatorTicketDTO.getStation_in_id() != null && stageId == null) {
            sql.append(" AND sp.station_id =:station_id\n");
            hmapParams.put("station_id", reqCalculatorTicketDTO.getStation_in_id());
        }
        if (stageId != null) {
            sql.append(" AND sp.STAGE_ID =:stage_id\n");
            hmapParams.put("stage_id", stageId);
        }
        if (methodChargeId != null) {
            sql.append("and sp.CHARGE_METHOD_ID = :chargeMethodId \n");
            hmapParams.put("chargeMethodId", methodChargeId);
        }
        //them dieu kien tim kiem ngay hieu luc
        if (reqCalculatorTicketDTO.getStart_date() != null) {
            sql.append("and ((to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "') between START_DATE and END_DATE) \n");
            sql.append("or(START_DATE <= to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "') and END_DATE Is null)) \n");
            hmapParams.put("effDate", reqCalculatorTicketDTO.getStart_date());
            hmapParams.put("effDate", reqCalculatorTicketDTO.getStart_date());
        }

        if (reqCalculatorTicketDTO.getTicket_type() != null) {
            sql.append(" AND sp.service_plan_type_id =:service_plan_type_id");
            hmapParams.put("service_plan_type_id", FnCommon.convertTicketType(reqCalculatorTicketDTO.getTicket_type()));
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
            sql.append(" sp.fee                     AS price_amount,                    -- Gia ve \n");
            sql.append(" sp.vehicle_group_id        AS vehicle_type                     -- Loai PT thu phi \n");
            if (!isCallFromBOO) {
                sql.append(", sp.SERVICE_PLAN_ID         AS service_plan_id,                 -- Id bang gia cuoc \n");
                sql.append(" sp.SERVICE_PLAN_TYPE_ID    AS service_plan_type_id,             -- Ma danh muc, loai goi dich vu \n");
                sql.append(" sp.CHARGE_METHOD_ID        AS charge_method_id,                 -- Ma phuong thuc tinh phi \n");
                sql.append(" sp.OCS_CODE                AS ocs_code,                         -- Ma ocs \n");
                sql.append(" sp.AUTO_RENEW              AS auto_renew,                      -- Tu dong gia han \n");
                sql.append(" sp.SCOPE                   AS scope,                            -- Pham vi ap dung \n");
                sql.append(" sp.stage_id                AS stage_id                            -- Doan \n");
            }
            sql.append(" FROM \n");
            sql.append(" service_plan sp \n");
            sql.append(" WHERE \n");
            sql.append(" 1 = 1 \n");
            sql.append(" AND sp.STATUS = 2 \n");


            if (vehicleGroupIdOld != null) {
                sql.append(" AND sp.vehicle_group_id IN (:groupIds)\n");
                hmapParams.put("groupIds", vehicleGroupIdOld);
            }

            if (reqCalculatorTicketDTO.getStation_in_id() != null && stageId == null) {
                sql.append(" AND sp.station_id =:station_id\n");
                hmapParams.put("station_id", reqCalculatorTicketDTO.getStation_in_id());
            }
            if (stageId != null) {
                sql.append(" AND sp.STAGE_ID =:stage_id\n");
                hmapParams.put("stage_id", stageId);
            }
            if (methodChargeId != null) {
                sql.append("and sp.CHARGE_METHOD_ID = :chargeMethodId \n");
                hmapParams.put("chargeMethodId", methodChargeId);
            }
            //them dieu kien tim kiem ngay hieu luc
            if (reqCalculatorTicketDTO.getStart_date() != null) {
                sql.append("and ((to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "') between START_DATE and END_DATE) \n");
                sql.append("or(START_DATE <= to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "') and END_DATE Is null)) \n");
                hmapParams.put("effDate", reqCalculatorTicketDTO.getStart_date());
                hmapParams.put("effDate", reqCalculatorTicketDTO.getStart_date());
            }

            if (reqCalculatorTicketDTO.getTicket_type() != null) {
                sql.append(" AND sp.service_plan_type_id =:service_plan_type_id");
                hmapParams.put("service_plan_type_id", FnCommon.convertTicketType(reqCalculatorTicketDTO.getTicket_type()));
            }
            if (excTicketId != null) {
                sql.append(" and sp.promotion_id =:promotionId ");
                hmapParams.put("promotionId", excTicketId);
            }
            sql.append(")");
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, ResCalculatorTicketDTO.class);
    }

    /***
     * lay danh muc mapping Boo
     * @param categoryMappingBooDTO
     * @return
     */
    public ResultSelectEntity getListCategoryMappingBoo(ReqMappingDTO categoryMappingBooDTO) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select \n");
        sql.append(" bm.code                        AS code,                  -- CODE \n");
        sql.append(" bm.description                 AS description,          -- mo ta \n");
        sql.append(" bm.name                        AS name,                 -- ten danh muc \n ");
        sql.append(" bm.type                        AS type,                  -- loai \n");
        sql.append(" bm.value                       AS value                 -- gia tri \n");
        sql.append(" FROM \n");
        sql.append(" boo_list_mapping bm \n");
        sql.append(" WHERE 1 = 1 \n");
        if (!FnCommon.isNullObject(categoryMappingBooDTO.getCode())) {
            sql.append(" AND bm.code = :code");
            hmapParams.put("code", categoryMappingBooDTO.getCode().trim());
        }
        if (!FnCommon.isNullObject(categoryMappingBooDTO.getType())) {
            sql.append(" AND bm.type = :type");
            hmapParams.put("type", categoryMappingBooDTO.getType().trim());
        }
        return getListDataAndCount(sql, hmapParams, 0, null, ReqMappingDTO.class);
    }
}
