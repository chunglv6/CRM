package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.viettelpay.*;
import com.viettel.etc.repositories.ViettelPayRepository;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ViettelPayRepositoryImpl extends CommonDataBaseRepository implements ViettelPayRepository {
    /**
     * Tim kiem du lieu bang (contractId va contractNo) hoac ( plateNumber va plateType)
     *
     * @param orderId    Ma dinh danh request viettelpay gui ve
     * @param searchType Loai tim kiem: 1. Tim kiem theo hop dong; 2. Tim kiem theo loai phuong tien
     * @return Du lieu phan hoi cho client
     */
    @Override
    public Object findByPlateOrContract(String orderId, String searchType, Long contractId, String contractNo, String plateType, String plateNumber) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select\n");
        sql.append("c.contract_id                   as contractId,\n");
        sql.append("c.contract_no                   as contractNo,\n");
        sql.append("cu.cust_name                    as fullName,\n");
        sql.append("cu.cust_type_id                 as custType,\n");
        sql.append("doc.code                        as idType,\n");
        sql.append("cu.document_number              as idCard,\n");
        sql.append("cu.phone_number                 as contactTel,\n");
        sql.append("concat(cu.street,cu.area_name)  as address,\n");
        sql.append("c.status                        as contractStatus,\n");
        sql.append("c.PAYMENT_DEFAULT_ID            as paymentLinking,\n");
        sql.append("c.SIGN_DATE                     as signDate\n");

        if (!FnCommon.isNullOrEmpty(searchType) && "2".equals(searchType)) {
            sql.append("from CONTRACT c inner join  CUSTOMER cu on cu.cust_id = c.cust_id\n");
            sql.append(" inner join document_type doc on doc.document_type_id = cu.document_type_id\n");
            sql.append("where  c.status = 2 and ");
            sql.append(" c.contract_no = ?");
            arrParams.add(contractNo);
        }
        if (!FnCommon.isNullOrEmpty(searchType) && "3".equals(searchType)) {
            sql.append("from CONTRACT c inner join  CUSTOMER cu on cu.cust_id = c.cust_id\n");
            sql.append(" inner join document_type doc on doc.document_type_id = cu.document_type_id\n");
            sql.append("where  c.status = 2 and ");
            sql.append(" c.contract_id = ?");
            arrParams.add(contractId);
        }
        if (!FnCommon.isNullOrEmpty(searchType) && "1".equals(searchType)) {
            sql.append("from CONTRACT c inner join  CUSTOMER cu on cu.cust_id = c.cust_id\n");
            sql.append(" inner join document_type doc on doc.document_type_id = cu.document_type_id\n");
            sql.append("inner join vehicle vh on vh.contract_id = c.contract_id\n");
            sql.append("where vh.plate_type_code = ? and vh.plate_number= ? and vh.status=1 and vh.active_status <>5");
            arrParams.add(plateType);
            arrParams.add(plateNumber);
        }
        return getFirstData(sql, arrParams, DataDTO.class);
    }

    /**
     * Tim kiem tat ca phuong tien con hoat dong va chua chuyen chu quyen
     *
     * @param contractId Ma dinh danh cua hop dong
     * @return Object
     */
    @Override
    public Object findAllVehicleByContractAndPlate(Long contractId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select ve.plate_number as plateNumber,ve.plate_type as plateType \n");
        sql.append("from vehicle ve inner join contract co on co.contract_id = ve.contract_id\n");
        sql.append("where ve.status=1 and co.status = 2 and ve.active_status != 5\n");
        if (contractId != null) {
            sql.append("and ve.contract_id = ?");
            arrParams.add(contractId);
        }
        return getListData(sql, arrParams, 0, null, DataDTO.Plate.class);
    }

    /***
     * Lay thong tin giao dich mua ve
     * @param billingCode
     * @return
     */
    @Override
    public Object getInfoOderTicketPurchaseAndExtendedViaSDK(String billingCode) {
        StringBuilder sql = new StringBuilder();
        StatusDTO statusDTO = null;
        List<Object> arrParams = new ArrayList<>();
        sql.append("SELECT \n");
        sql.append(" so.sale_order_id               as orderId,     -- ma giao dich\n");
        sql.append(" so.contract_id                 as contractId,  -- ma hop dong\n");
        sql.append(" so.amount                      as amount,      -- so tien thanh toan (so tien gio hang) \n");
        sql.append(" so.sale_order_id               as billingCode, -- ma don hang \n");
        sql.append(" cp.account_number              as msisdn,      -- so dien thoai viettelpay \n");
        sql.append(" cp.token                       as token        -- ma token lien ket viettelpay \n");
        sql.append(" FROM \n");
        sql.append(" sale_order so,contract_payment cp \n");
        sql.append(" WHERE \n");
        sql.append(" so.contract_id=cp.contract_id(+) \n");
        sql.append(" AND \n");
        sql.append(" so.sale_order_id = ?");
        arrParams.add(billingCode);
        List<Object> listResult = (List<Object>) getListData(sql, arrParams,0,null, ResponseGetInfoTicketPurchaseAndExtendedDTO.class);
        if (listResult.size()==0) {
            return null;
        }
        return listResult.get(0);
    }

    /***
     * Lay thong tin ve mua/gia han qua sdk
     * @param orderId
     * @return
     */
    @Override
    public Object getTicketExtendedViaSDK(String orderId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("SELECT \n");
        sql.append(" sod.sale_order_detail_id           as ticketNo,        -- ma ve \n");
        sql.append(" sod.service_plan_type_id           as ticketType,      -- ma loai ve \n");
        sql.append(" sod.plate_number                   as plateNumber,     -- bien so xe dang ky gia han \n");
        sql.append(" (select v.plate_type from vehicle v where v.vehicle_id = sod.vehicle_id) as plateTypeId, -- ma loai bien so \n");
        sql.append(" sod.amount                         as amount,          -- so tien gia han ve \n");
        sql.append(" sod.stage_id                       as stageId,         --  \n");
        sql.append(" sod.station_id                     as stationId, \n");
        sql.append(" sod.epc                     as epc,        --so the Etag \n");
        sql.append(" to_char(sod.eff_date, 'yyyy/MM/dd HH24:mi:ss')                   as startDate,       -- thoi gian bat dau hieu luc ve \n");
        sql.append(" to_char(sod.exp_date, 'yyyy/MM/dd HH24:mi:ss')               as endDate \n");
        sql.append(" FROM \n");
        sql.append(" sale_order_detail sod \n");
        sql.append(" WHERE 1=1 \n");
        sql.append(" AND sod.sale_order_id = ? \n");
        sql.append(" AND sod.auto_renew_vtp = '1' ");
        arrParams.add(orderId);
        return getListData(sql, arrParams, 0, null, ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder.class);
    }

    /***
     * Lay thong tin giao dich mua ve
     * @param billingCode
     * @return
     */
    @Override
    public Object getInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream(String billingCode) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("SELECT \n");
        sql.append(" so.sale_order_id               as orderId,     -- ma giao dich\n");
        sql.append(" so.contract_id                 as contractId,  -- ma hop dong\n");
        sql.append(" so.sale_order_id               as billingCode, -- ma don hang \n");
        sql.append(" cp.account_number              as msisdn,      -- so dien thoai viettelpay \n");
        sql.append(" cp.token                       as token        -- ma token lien ket viettelpay \n");
        sql.append(" FROM \n");
        sql.append(" sale_order so,contract_payment cp \n");
        sql.append(" WHERE \n");
        sql.append(" so.contract_id=cp.contract_id(+) \n");
        sql.append(" AND \n");
        sql.append(" so.sale_order_id = ?");
        arrParams.add(billingCode);
        List<Object> listResult = (List<Object>) getListData(sql, arrParams,0,null, ResponseGetInfoTicketPurchaseAndExtendedDTO.class);
        if (listResult.size()==0) {
            return null;
        }
        return listResult.get(0);
    }

    /***
     * Lay thong tin ve mua/gia han qua sdk
     * @param orderId
     * @return
     */
    @Override
    public Object getTicketExtendedViaSDKPrivateStream(String orderId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("SELECT \n");
        sql.append(" sod.sale_order_detail_id           as ticketNo,        -- ma ve \n");
        sql.append(" sod.service_plan_type_id           as ticketType,      -- ma loai ve \n");
        sql.append(" sod.plate_number                   as plateNumber,     -- bien so xe dang ky gia han \n");
        sql.append(" (select v.plate_type from vehicle v where v.vehicle_id = sod.vehicle_id) as plateTypeId, -- ma loai bien so \n");
        sql.append(" sod.amount                         as amount,          -- so tien gia han ve \n");
        sql.append(" sod.stage_id                       as stageId,         --  \n");
        sql.append(" sod.station_id                     as stationId, \n");
        sql.append(" sod.epc                     as epc,        --so the Etag \n");
        sql.append(" to_char(sod.eff_date, 'yyyy/MM/dd HH24:mi:ss')                   as startDate,       -- thoi gian bat dau hieu luc ve \n");
        sql.append(" to_char(sod.exp_date, 'yyyy/MM/dd HH24:mi:ss')               as endDate \n");
        sql.append(" FROM \n");
        sql.append(" sale_order_detail sod \n");
        sql.append(" WHERE 1=1 \n");
        sql.append(" AND sod.sale_order_id = ? \n");
        sql.append(" AND sod.auto_renew_vtp = '1' ");
        arrParams.add(orderId);
        return getListData(sql, arrParams, 0, null, ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.TicketOrder.class);
    }
}
