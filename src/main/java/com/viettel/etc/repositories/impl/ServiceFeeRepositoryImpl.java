package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.repositories.ServiceFeeRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Autogen class: Lop thao tac phi
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@Repository
public class ServiceFeeRepositoryImpl extends CommonDataBaseRepository implements ServiceFeeRepository {
    /**
     * Lay danh sach phi
     *
     * @param params
     * @return
     */
    @Override
    public ResultSelectEntity getServiceFee(ServiceFeeDTO params) {
        StringBuilder sql = new StringBuilder();
        sql.append("select  \n")
            .append("sf.SERVICE_FEE_ID              as          id,                 -- Id \n")
            .append("sf.SERVICE_FEE_NAME            as          serviceFeeName,     -- Name \n")
            .append("sf.SERVICE_FEE_CODE            as          serviceFeeCode,     -- Code \n")
            .append("sf.ACT_TYPE_ID                 as          actionTypeId,       -- Loai tac dong \n")
            .append("sf.ACT_REASON_ID               as          actReasonId,        -- Ly do tac dong \n")
            .append("sf.FEE                         as          fee,                -- Muc phi \n")
            .append("sf.START_DATE                  as          startDate,          -- Ngay hieu luc \n")
            .append("sf.END_DATE                    as          endDate,            -- Ngay het hieu luc \n")
            .append("sf.STATUS                      as          status,             -- Trang thai \n")
            .append("sf.CREATE_DATE                 as          createDate,         -- Ngay tao \n")
            .append("sf.CREATE_USER                 as          createUser,         -- Nguoi tao \n")
            .append("sf.APPROVED_DATE               as          approvedDate,       -- Ngay tao \n")
            .append("sf.APPROVED_USER               as          approvedUser,       -- Nguoi tao \n")
            .append("sf.APPROVED_STATUS             as          approvedStatus,     -- Phe duet \n")
            .append("at.NAME                        as          actTypeName,        --Ten loai tac dong \n")
            .append("at.CODE                        as          actTypeCode,        --Ma loai tac dong \n")
            .append("ar.NAME                        as          actReasonName,      --Ten ly do tac dong \n")
            .append("ar.CODE                        as          actReasonCode       --Ma ly do tac dong \n")
            .append("from SERVICE_FEE sf \n")
            .append("INNER JOIN ACT_TYPE at ON sf.ACT_TYPE_ID = at.ACT_TYPE_ID \n")
            .append("INNER JOIN ACT_REASON ar ON sf.ACT_REASON_ID = ar.ACT_REASON_ID \n")
            .append("where 1=1 \n");

        HashMap<String, Object> hmapParams = new HashMap<>();

        //Tim kiem theo khoang ngay hieu luc
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);

        if (params.getStartDate() != null) {
            sql.append("and sf.START_DATE >= to_date(:startDate, :format ) \n");
            hmapParams.put("startDate", formatter.format(params.getStartDate()));
            hmapParams.put("format", Constants.ORACLE_DATE_FORMAT);
        }
        if (params.getEndDate() != null) {
            sql.append("and sf.END_DATE <= to_date(:endDate, :format ) \n");
            hmapParams.put("endDate", formatter.format(params.getEndDate()));
            hmapParams.put("format", Constants.ORACLE_DATE_FORMAT);
        }

        //Tim kiem theo loai tac dong
        if (params.getActionTypeId() != null) {
            sql.append("and sf.ACT_TYPE_ID = :actTypeId \n");
            hmapParams.put("actTypeId", params.getActionTypeId());
        }

        //Tim kiem theo ly do tac dong
        if (params.getActReasonId() != null) {
            sql.append("and sf.ACT_REASON_ID = :actReasonId \n");
            hmapParams.put("actReasonId", params.getActReasonId());
        }

        //Tim kiem theo muc phi
        if (params.getFee() != null) {
            sql.append("and sf.FEE = :fee \n");
            hmapParams.put("fee", params.getFee());
        }

        //Tim kiem theo trang thai
        if (params.getStatus() != null) {
            sql.append("and sf.STATUS = :status \n");
            hmapParams.put("status", params.getStatus());
        }

        //Tim kiem theo ma
        if (params.getServiceFeeCode() != null) {
            sql.append("and sf.SERVICE_FEE_CODE like :serviceFeeCode \n");
            hmapParams.put("serviceFeeCode", "%" + params.getServiceFeeCode() + "%");
        }

        sql.append("order by sf.CREATE_DATE \n");

        Integer start = null;

        if (params.getStartrecord() != null) {
            start = params.getStartrecord();
        }

        Integer pageSize = null;

        if (params.getPagesize() != null) {
            pageSize = params.getPagesize();
        }

        return getListDataAndCount(sql, hmapParams, start, pageSize, ServiceFeeDTO.class);
    }
}

