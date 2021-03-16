package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.ActionRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;
import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT_24H;

/**
 * Autogen class Repository Impl: Lop ve tac dong
 *
 * @author ToolGen
 * @date Mon Jun 29 11:19:53 ICT 2020
 */
@Repository
public class ActionRepositoryImpl extends CommonDataBaseRepository implements ActionRepository {

    /**
     * lay danh sach loai tac dong
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public List<ActionDTO> getActionType(ActionDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select ACT_TYPE_ID as actTypeId, CODE, NAME, STATUS from ACT_TYPE where 1 = 1 \n");

        if (itemParamsEntity.getGetAll() == null || !itemParamsEntity.getGetAll()) {
            sql.append(" AND STATUS = 1 \n");
        }

        if (itemParamsEntity.getActObject() != null) {
            //lay loai tac dong theo doi tuong tac dong
            sql.append("and ACT_OBJECT = :act_object \n");
            hmapParams.put("act_object", itemParamsEntity.getActObject());
        }

        //sapxep
        sql.append(" order by NLSSORT(upper(NAME), 'nls_sort=Vietnamese') \n");

        Integer start = null;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pageSize = null;
        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }

        return (List<ActionDTO>) getListData(sql, hmapParams, start, pageSize, ActionDTO.class);
    }

    /**
     * lay danh sach lich su tac dong
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getActionHistory(ActionAuditHistoryDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select distinct \n");
        sql.append("AA.ACTION_AUDIT_ID          as actionAuditId,       --ma tac dong \n");
        sql.append("AA.CUST_ID                  as custId,              --ma khach hang \n");
        sql.append("C.CUST_NAME                 as custName,            --ten khach hang  \n");
        sql.append("AT.ACT_OBJECT               as actObject,           --doi tuong tac dong \n");
        sql.append("AT.NAME                     as actionTypeName,      --loai tac dong \n");
        sql.append("AR.NAME                     as actionReasonName,    --ly do tac dong \n");
        sql.append("AA.ACTION_USER_FULL_NAME    as actionUserFullName,  --Ten nguoi tac dong \n");
        sql.append("AA.CREATE_DATE              as createDate,          --Ngay Tac Dong \n");
        sql.append("AA.STATUS                   as status               --Trang Thai \n");
        sql.append("from ACTION_AUDIT AA --collect table \n");
        sql.append("left join CUSTOMER C on  AA.CUST_ID = C.CUST_ID \n");
        sql.append("join ACT_REASON AR on AA.ACT_REASON_ID = AR.ACT_REASON_ID \n");
        sql.append("join ACT_TYPE AT on  AA.ACT_TYPE_ID = AT.ACT_TYPE_ID \n");
        sql.append("left join CONTRACT CT on AA.CONTRACT_ID = CT.CONTRACT_ID \n");
        sql.append("left join VEHICLE V on  AA.VEHICLE_ID = V.VEHICLE_ID \n");
        sql.append("join ACTION_AUDIT_DETAIL AAD on AA.ACTION_AUDIT_ID = AAD.ACTION_AUDIT_ID \n");
        sql.append("where 1 = 1 \n");

        //them dieu kien tim kiem theo doi tuong tac dong
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getActObject())) {
            sql.append("and AT.ACT_OBJECT = :actObject \n");
            hmapParams.put("actObject", itemParamsEntity.getActObject());
        }

        //them dieu kien tim kiem theo loai tac dong
        if (itemParamsEntity.getActTypeId() != null) {
            sql.append("and AT.ACT_TYPE_ID = :actTypeId \n");
            hmapParams.put("actTypeId", itemParamsEntity.getActTypeId());
        }

        //them dieu kien tim kiem theo ten khach hang
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getCustName())) {
            sql.append("and UPPER(C.CUST_NAME) like :custName \n");
            hmapParams.put("custName", "%" + itemParamsEntity.getCustName().toUpperCase() + "%");
        }

        //them dieu kien tim kiem theo CMND va MST
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getDocumentNumber())) {
            sql.append("and C.DOCUMENT_NUMBER = :documentNumber \n");
            sql.append("or C.TAX_CODE = :documentNumber \n");
            hmapParams.put("documentNumber", itemParamsEntity.getDocumentNumber());
        }

        //them dieu kien tim kiem theo sdt
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getPhoneNumber())) {
            sql.append("and C.PHONE_NUMBER = :phoneNumber \n");
            hmapParams.put("phoneNumber", itemParamsEntity.getPhoneNumber());
        }

        //them dieu kien tim kiem theo so hop dong
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getContractNo())) {
            sql.append("and UPPER(CT.CONTRACT_NO) like :contractNo \n");
            hmapParams.put("contractNo", "%" + itemParamsEntity.getContractNo().toUpperCase() + "%");
        }

        //them dieu kien tim kiem theo bien so xe
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getPlateNumber())) {
            sql.append("and UPPER(V.PLATE_NUMBER) like :plateNumber \n");
            hmapParams.put("plateNumber", "%" + itemParamsEntity.getPlateNumber().toUpperCase() + "%");
        }

        //them dieu kien tim kiem theo so serial RFID
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getRfidSerial())) {
            sql.append("and UPPER(V.RFID_SERIAL) like :rfidSerial \n");
            hmapParams.put("rfidSerial", "%" + itemParamsEntity.getRfidSerial().toUpperCase() + "%");
        }

        //them dieu kien tim kiem theo nhan vien tac dong
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getActionUserFullName())) {
            sql.append("and UPPER(AA.ACTION_USER_FULL_NAME) like :actionUserFullName \n");
            hmapParams.put("actionUserFullName", "%" + itemParamsEntity.getActionUserFullName().toUpperCase() + "%");
        }

        //them dieu kien tim kiem theo ten bang
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getTableName())) {
            sql.append("and UPPER(AAD.TABLE_NAME) like :tableName \n");
            hmapParams.put("tableName", "%" + itemParamsEntity.getTableName().toUpperCase() + "%");
        }

        //Tu ngay
        if (itemParamsEntity.getStartDate() != null && itemParamsEntity.getEndDate() == null) {
            sql.append("and AA.CREATE_DATE >= to_date(:startDate,'" + COMMON_DATE_FORMAT + "') \n");
            hmapParams.put("startDate", itemParamsEntity.getStartDate());
        }

        //Den ngay
        if (itemParamsEntity.getStartDate() == null && itemParamsEntity.getEndDate() != null) {
            sql.append("and AA.CREATE_DATE <= to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "') \n");
            hmapParams.put("endDate", itemParamsEntity.getEndDate() + Constants.TIME_ONE_DAY);
        }

        //Khoang ngay
        if (itemParamsEntity.getStartDate() != null && itemParamsEntity.getEndDate() != null) {
            sql.append("and AA.CREATE_DATE between to_date(:startDate,'" + COMMON_DATE_FORMAT + "') and to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "') \n");
            hmapParams.put("startDate", itemParamsEntity.getStartDate());
            hmapParams.put("endDate", itemParamsEntity.getEndDate() + Constants.TIME_ONE_DAY);
        }

        sql.append("order by AA.CREATE_DATE desc");

        Integer start = null;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pageSize = null;
        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }

        return getListDataAndCount(sql, hmapParams, start, pageSize, ActionAuditHistoryDTO.class);
    }

    /**
     * lay danh sach ly do
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public List<ActionDTO> getActionReason(ActionDTO itemParamsEntity, Long id) {

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select ACT_REASON_ID as id, NAME, STATUS from ACT_REASON where ACT_TYPE_ID = :id  AND STATUS = :status \n");
        hmapParams.put("id", id);
        hmapParams.put("status", Constants.ACT_REASON_ACTIVE);
        sql.append(" order by NLSSORT(upper(NAME), 'nls_sort=Vietnamese')");
        Integer start = null;
        if (itemParamsEntity != null && itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if (itemParamsEntity != null && itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        return (List<ActionDTO>) getListData(sql, hmapParams, start, pageSize, ActionDTO.class);
    }

    /**
     * lay danh sach lich su tac dong chi tiet
     *
     * @param itemParamsEntity: params client truyen len
     * @param id:               action audit id
     * @return
     */
    @Override
    public ResultSelectEntity getActionHistoryDetail(ActionAuditHistoryDetailDTO itemParamsEntity, Long id) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        //Create SQL query
        sql.append("select CREATE_DATE          as  createDate,         ---Thoi gian thay doi \n");
        sql.append("TABLE_NAME                  as  tableName,          ---Ten bang thay doi \n");
        sql.append("COLUNM_NAME                 as  columnName,         ---Ten cot thay doi \n");
        sql.append("OLD_VALUE                   as  oldValue,           ---Gia tri cu \n");
        sql.append("NEW_VALUE                   as  newValue            ---Gia tri moi \n");
        sql.append("from ACTION_AUDIT_DETAIL where 1=1 \n");

        //add conditions
        if (id != null) {
            sql.append("and ACTION_AUDIT_ID = :id");
            hmapParams.put("id", id);
        }

        //add paging
        Integer start = null;
        if (itemParamsEntity != null && itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pageSize = null;
        if (itemParamsEntity != null && itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }

        return getListDataAndCount(sql, hmapParams, start, pageSize, ActionAuditHistoryDetailDTO.class);
    }

    /**
     * Lich su tac dong KH
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findActionCustomerHistory(AuditHistoryDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("    select\n");
        sql.append("        au.ACTION_USER_NAME     as actionUserName,              -- Nguoi tac dong\n");
        sql.append("        at.NAME                 as actionType,                  -- Loai tac dong\n");
        sql.append("        to_char(au.CREATE_DATE, 'dd/mm/yyyy HH24:mi:ss') as actionDate,   -- Ngay tac dong\n");
        sql.append("        cus.CUST_NAME           as userName,                    -- Nguoi yeu cau tac dong\n");
        sql.append("        ar.NAME                 as reason                      -- Ly do\n");
        sql.append("    from \n");
        sql.append(" ACTION_AUDIT au, ACTION_AUDIT_DETAIL aud, ACT_TYPE at, ACT_REASON ar, CUSTOMER cus \n");
        sql.append(" where au.ACTION_AUDIT_ID = aud.ACTION_AUDIT_ID \n");
        sql.append(" and au.ACT_TYPE_ID = at.ACT_TYPE_ID \n");
        sql.append(" and au.ACT_REASON_ID = ar.ACT_REASON_ID \n");
        sql.append(" and au.CUST_ID = cus.CUST_ID \n");
        if (requestModel.getScreenType() == null) {
            sql.append(" and au.cust_id = ? and at.ACT_OBJECT in (1,2) \n");
            arrParams.add(requestModel.getCustId());
        } else {
            // lich su tac dong cua khach hang
            if (requestModel.getScreenType().equals(Constants.CUSTOMER_TYPE_SCREEN)) {
                sql.append(" and au.cust_id = ? and at.ACT_OBJECT = 1\n");
                arrParams.add(requestModel.getCustId());
            }
            // lich su tac dong cua hop dong
            if (requestModel.getScreenType().equals(Constants.CONTRACT_TYPE_SCREEN)) {
                sql.append(" and au.contract_id = ? and at.ACT_OBJECT = 2\n");
                arrParams.add(requestModel.getContractId());
            }
            // lich su tac dong cua phuong tien
            if (requestModel.getScreenType().equals(Constants.VEHICLE_TYPE_SCREEN)) {
                sql.append(" and au.vehicle_id = ? and at.ACT_OBJECT = 3\n");
                arrParams.add(requestModel.getVehicleId());
            }
        }

        //Them param startDate, endDate
        if (requestModel.getStartDate() != null && !"".equals(requestModel.getStartDate())) {
            sql.append(" and aud.CREATE_DATE >= to_date(?,'dd/mm/yyyy')");
            arrParams.add(requestModel.getStartDate());
        }
        if (requestModel.getEndDate() != null && !"".equals(requestModel.getEndDate())) {
            sql.append(" and aud.CREATE_DATE <= to_date(?,'dd/mm/yyyy HH24:mi:ss')");
            arrParams.add(requestModel.getEndDate() + Constants.TIME_ONE_DAY);
        }
        sql.append(" order by au.CREATE_DATE DESC");
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, AuditHistoryDTO.class);
    }

    /**
     * Lich su tac dong KH
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity actionCustomerHistory(AuditHistoryDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        String plateNumber = requestModel.getPlateNumber() == null ? "" : requestModel.getPlateNumber();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select\n")
        .append(" au.ACTION_USER_NAME     as actionUserName,                         -- Nguoi tac dong\n")
        .append(" at.NAME                 as actionType,                             -- Loai tac dong\n")
        .append(" to_char(au.CREATE_DATE, 'dd/mm/yyyy HH24:mi:ss') as actionDate,    -- Ngay tac dong\n")
        .append(" cus.CUST_NAME           as userName,                               -- Nguoi yeu cau tac dong\n")
        .append(" ar.NAME                 as reason,                                 -- Ly do\n")
        .append(" aud.COLUNM_NAME         as columnName,                             -- Truong tac dong\n")
        .append(" veh.plate_number        as plateNumber                             -- Bien so xe \n")
        .append(" from \n")
        .append(" ACTION_AUDIT au, ACTION_AUDIT_DETAIL aud, ACT_TYPE at, ACT_REASON ar, CUSTOMER cus, VEHICLE veh \n")
        .append(" where au.ACTION_AUDIT_ID = aud.ACTION_AUDIT_ID \n")
        .append(" and au.ACT_TYPE_ID = at.ACT_TYPE_ID \n")
        .append(" and au.ACT_REASON_ID = ar.ACT_REASON_ID \n")
        .append(" and au.contract_id = veh.contract_id")
        .append(" and au.vehicle_id = veh.vehicle_id")
        .append(" and au.CUST_ID = cus.CUST_ID \n");
        // theo hop dong
        sql.append(" and au.contract_id = ?\n");
        arrParams.add(requestModel.getContractId());
        // theo phuong tien
        sql.append(" and at.ACT_OBJECT = 3 and veh.plate_number like ?\n");
        arrParams.add("%" + plateNumber + "%");

        //Them param startDate, endDate
        if (requestModel.getStartDate() != null && !"".equals(requestModel.getStartDate())) {
            sql.append(" and aud.CREATE_DATE >= to_date(?,'dd/mm/yyyy')");
            arrParams.add(requestModel.getStartDate());
        }
        if (requestModel.getEndDate() != null && !"".equals(requestModel.getEndDate())) {
            sql.append(" and aud.CREATE_DATE <= to_date(?,'dd/mm/yyyy HH24:mi:ss')");
            arrParams.add(requestModel.getEndDate() + Constants.TIME_ONE_DAY);
        }
        sql.append(" order by au.CREATE_DATE DESC");
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, AuditHistoryDTO.class);
    }

    /**
     * Lay danh sach lich su the RFID
     *
     * @param vehicleId
     * @param actionDTO
     * @return
     */
    @Override
    public ResultSelectEntity findRfidVehicleHistories(Long vehicleId, ActionDTO actionDTO) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select ");
        sql.append(" au.CREATE_DATE             as createDate,                          -- Ngay tac dong\n");
        sql.append(" au.ACTION_USER_NAME        as actionUserName,                      -- Nguoi tac dong\n");
        sql.append(" cu.CUST_NAME               as custName,                            -- Nguoi yeu cau\n");
        sql.append(" au.ACT_REASON_ID           as actReasonId,                         -- Ma ly do\n");
        sql.append(" at.NAME                    as name,                                -- Ten loai tac dong\n");
        sql.append(" au.ACTION_AUDIT_ID         as actionAuditId                        -- Ma action audit\n");
        sql.append(" from \n");
        sql.append(" (select CREATE_DATE,ACTION_USER_NAME,ACT_REASON_ID, ACTION_AUDIT_ID,CUST_ID,  ACT_TYPE_ID, VEHICLE_ID from  ACTION_AUDIT) au,\n");
        sql.append(" (select ACT_TYPE_ID, NAME, ACT_OBJECT from ACT_TYPE) at,\n");
        sql.append(" (select CUST_ID,CUST_NAME from  CUSTOMER) cu");
        sql.append(" where au.ACT_TYPE_ID = at.ACT_TYPE_ID (+)\n");
        sql.append(" and au.CUST_ID = cu.CUST_ID (+)\n");
        // act_object  rfid is 4
        sql.append(" and at.ACT_OBJECT =4 \n");
        if (!FnCommon.isNullOrEmpty(vehicleId.toString())) {
            sql.append(" and au.VEHICLE_ID = ? \n");
            arrParams.add(vehicleId);
        }
        if (!FnCommon.isNullOrEmpty(actionDTO.getCreateDateFrom())) {
            sql.append(" and au.CREATE_DATE >= TO_DATE (?, 'dd/mm/yyyy')\n");
            arrParams.add(actionDTO.getCreateDateFrom());
        }
        if (!FnCommon.isNullOrEmpty(actionDTO.getCreateDateTo())) {
            sql.append(" and au.CREATE_DATE <= TO_DATE (?, 'dd/mm/yyyy')\n");
            arrParams.add(actionDTO.getCreateDateTo());
        }
        Integer start = null;
        if (actionDTO.getStartrecord() != null) {
            start = actionDTO.getStartrecord();
        }
        Integer pageSize = null;
        if (actionDTO.getPagesize() != null) {
            pageSize = actionDTO.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, ActionDTO.class);
    }

    /**
     * Lay tong so ban ghi khach hang, contract, vehicle, contract profile
     * @return
     */
    @Override
    public Object countData() {
        StringBuilder sql = new StringBuilder();
        sql.append("select totalCustomer,totalContract,totalVehicle, totalContractProfile");
        sql.append(" from");
        sql.append(" (SELECT count(1) totalCustomer from customer),");
        sql.append(" (SELECT count(1) totalContract from contract),");
        sql.append(" (SELECT count(1) totalVehicle from vehicle),");
        sql.append(" (SELECT count(1) totalContractProfile from contract_profile)");
        return getFirstData(sql, new HashMap<String, Object>(), CountDataDTO.class);
    }
}
