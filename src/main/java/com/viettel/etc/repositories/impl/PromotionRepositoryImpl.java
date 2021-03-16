package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.repositories.PromotionRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Autogen class Repository Impl: Lop quan ly chuong trinh khuyen mai/ chiet khau
 *
 * @author ToolGen
 * @date Fri Sep 04 15:40:35 ICT 2020
 */
@Repository
public class PromotionRepositoryImpl extends CommonDataBaseRepository implements PromotionRepository {

    /**
     * Lay danh sach chuong trinh khuyen mai/ chiet khau
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getPromotions(PromotionDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("select  \n")
                .append("PROMOTION_ID                as          Id,                -- Id \n")
                .append("PROMOTION_LEVEL             as          promotionLevel,    -- Loai chuong trinh \n")
                .append("PROMOTION_CODE              as          promotionCode,     -- Ma chuong trinh \n")
                .append("PROMOTION_NAME              as          promotionName,     -- Ten chuong trinh \n")
                .append("PROMOTION_AMOUNT            as          promotionAmount,   -- Muc KM/CK \n")
                .append("EFF_DATE                    as          effDate,           -- Ngay hieu luc \n")
                .append("EXP_DATE                    as          expDate,           -- Ngay het hieu luc \n")
                .append("STATUS                      as          status,            -- Trang thai \n")
                .append("CREATE_DATE                 as          createDate,        -- Ngay tao \n")
                .append("APPROVED_STATUS             as          approvedStatus     -- Phe duet \n")
                .append("from PROMOTION \n")
                .append("where 1=1 \n");

        HashMap<String, Object> hmapParams = new HashMap<>();

        //Tim kiem theo Loai chuong trinh
        if (itemParamsEntity.getPromotionLevel() != null) {
            sql.append("and PROMOTION_LEVEL = :promotionLevel \n");
            hmapParams.put("promotionLevel", itemParamsEntity.getPromotionLevel());
        }

        //Tim kiem theo Ma chuong trinh
        if (itemParamsEntity.getPromotionCode() != null) {
            sql.append("and PROMOTION_CODE = :promotionCode \n");
            hmapParams.put("promotionCode", itemParamsEntity.getPromotionCode());
        }

        //Tim kiem theo Ten chuong trinh
        if (itemParamsEntity.getPromotionName() != null) {
            sql.append("and LOWER(PROMOTION_NAME) like :promotionName \n");
            hmapParams.put("promotionName", "%" + itemParamsEntity.getPromotionName().toLowerCase().trim() + "%");
        }

        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getStationIds()) && !FnCommon.isNullOrEmpty(itemParamsEntity.getStageIds())) {
            sql.append("and (STATION_ID IN (:stationIds) OR STAGE_ID IN (:stageIds)) \n");
            hmapParams.put("stationIds", itemParamsEntity.getStationIds());
            hmapParams.put("stageIds", itemParamsEntity.getStageIds());
        } else if (!FnCommon.isNullOrEmpty(itemParamsEntity.getStationIds()) && FnCommon.isNullOrEmpty(itemParamsEntity.getStageIds())) {
            sql.append("and STATION_ID IN (:stationIds) \n");
            hmapParams.put("stationIds", itemParamsEntity.getStationIds());
        } else if (FnCommon.isNullOrEmpty(itemParamsEntity.getStationIds()) && !FnCommon.isNullOrEmpty(itemParamsEntity.getStageIds())) {
            sql.append("and STAGE_ID IN (:stageIds) \n");
            hmapParams.put("stageIds", itemParamsEntity.getStageIds());
        }

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);

        //Tim kiem theo khoang ngay hieu luc
        if (itemParamsEntity.getEffDate() != null) {
            sql.append("and EFF_DATE >= to_date(:effDate, :format ) \n");
            hmapParams.put("effDate", formatter.format(itemParamsEntity.getEffDate()));
            hmapParams.put("format", Constants.ORACLE_DATE_FORMAT);
        }
        if (itemParamsEntity.getExpDate() != null) {
            sql.append("and EXP_DATE <= to_date(:expDate, :format ) \n");
            hmapParams.put("expDate", formatter.format(itemParamsEntity.getExpDate()));
            hmapParams.put("format", Constants.ORACLE_DATE_FORMAT);
        }

        /**
         Trạng thái:
         0: Không hoạt động
         1: Hoạt động
         2: Hủy hiệu lực
         **/

        //Tim kiem theo trang thai
        if (itemParamsEntity.getStatus() != null) {
            sql.append("and STATUS = :status \n");
            hmapParams.put("status", itemParamsEntity.getStatus());
        }

        //Lay data khi tich hieu luc
        if (itemParamsEntity.getIsEffective() != null && itemParamsEntity.getIsExpire() == null) {
            if (itemParamsEntity.getIsEffective()) {
                sql.append("and (STATUS = 1 and (SYSDATE between EFF_DATE and EXP_DATE)) \n");
            }
        }

        //Lay data khi tich het hieu luc
        if (itemParamsEntity.getIsExpire() != null && itemParamsEntity.getIsEffective() == null) {
            if (itemParamsEntity.getIsExpire()) {
                sql.append("and (STATUS = 1 and (SYSDATE not between EFF_DATE and EXP_DATE)) \n");
            }
        }

        //Lay data khi tich ca hieu luc va het hieu luc
        if (itemParamsEntity.getIsExpire() != null && itemParamsEntity.getIsEffective() != null) {
            sql.append("and STATUS = 1 \n");
        }

        sql.append("order by NLSSORT(upper(PROMOTION_NAME), 'nls_sort=Vietnamese') \n");

        Integer start = null;

        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pageSize = null;

        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        ResultSelectEntity resultData = getListDataAndCount(sql, hmapParams, start, pageSize, PromotionDTO.class);

        return resultData;
    }


    /**
     * Lay du lieu doi tuong huong khuyen mai/ chiet khau
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getPromotionAssignDetail(PromotionAssignDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("select \n")
        .append("p.PROMOTION_ASSIGN_ID               as          promotionAssignId,          -- id \n")
        .append("p.PROMOTION_ID                      as          promotionId,                -- promotionId \n")
        .append("p.ASSIGN_LEVEL                      as          assignLevel,                -- Muc doi tuong \n")
        .append("p.CUST_ID                           as          custId,                     -- khach hang \n")
        .append("cu.CUST_NAME                        as          custName,                   -- ten khach hang \n")
        .append("p.CONTRACT_ID                       as          contractId,                 -- hop dong \n")
        .append("co.CONTRACT_NO                      as          contractNo,                 -- ma hop dong \n")
        .append("p.VEHICLE_ID                        as          vehicleId,                  -- phuong tien \n")
        .append("p.PLATE_NUMBER                      as          plateNumber,                -- bien so xe \n")
        .append("p.CREATE_USER                       as          createUser,                 -- nguoi tao  \n")
        .append("p.CREATE_DATE                       as          createDate,                 -- Ngay tao \n")
        .append("p.EPC                               as          EPC,                        -- EPC \n")
        .append("p.EFF_DATE                          as          effDate,                    -- ngay hieu luc\n")
        .append("p.EXP_DATE                          as          expDate                     -- ngay het hieu luc\n")
        .append("from PROMOTION_ASSIGN p \n")
        .append("INNER JOIN CUSTOMER cu ON p.CUST_ID = cu.CUST_ID \n")
        .append("LEFT JOIN CONTRACT co ON p.CONTRACT_ID = co.CONTRACT_ID \n")
        .append("where 1=1 \n");
        HashMap<String, Object> hmapParams = new HashMap<>();

        if (itemParamsEntity.getPromotionId() != null) {
            sql.append("and PROMOTION_ID = :promotionId \n");
            hmapParams.put("promotionId", itemParamsEntity.getPromotionId());
        }
        Integer start = null;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, PromotionAssignDTO.class);
    }
}