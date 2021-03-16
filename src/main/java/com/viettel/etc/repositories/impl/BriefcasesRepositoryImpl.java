package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.repositories.BriefcasesRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BriefcasesRepositoryImpl extends CommonDataBaseRepository implements BriefcasesRepository {

    @Value("${crm.briefcase.number-days-expire}")
    private String numberDaysExpire;
    /**
     * Tra cuu ho so
     *
     * @param searchBriefcasesDTO
     */
    public ResultSelectEntity searchBriefcases(SearchBriefcasesDTO searchBriefcasesDTO) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("        select distinct  ct.CUST_ID  as custId,\n");
        sql.append("        c.cust_name       as custName,              -- Ten khach hang\n");
        sql.append("        ct.contract_no    as contractNo,            -- So hop dong \n");
        sql.append("        ct.contract_id    as contractId,            -- Ma hop dong \n");
        sql.append("        c.document_number as documentNumber,        -- So giay to\n");
        sql.append("        c.cust_type_id    as custTypeId,             -- Loai khach hang\n");
        sql.append("        ct.sign_date      as signDate,              -- ngay ki hop dong\n");
        sql.append("        ct.CREATE_USER    as createUser,            -- Nguoi tiep nhan\n");
        sql.append("        ct.PROFILE_STATUS as profileStatus,          -- trang thai phe duyet ho so \n");
        sql.append("        ct.create_date    as createDate,            -- Ngay tiep nhan \n");
        sql.append("        at.name           as actionName ,                -- Ten loai tac dong \n");
        sql.append("        cust.name           as custTypeName ,       -- Ten loai khach hang \n");
        sql.append("        c.document_type_id           as documentTypeId ,      -- Ma loai giay to \n");
        sql.append("        at.act_type_id           as actTypeId,       -- Ma loai tac dong \n");
        sql.append("        ct.APPROVED_DATE           as approvedDate,       -- Ngay phe duyet \n");
        sql.append("        ct.APPROVED_USER           as approvedUser,       -- Nguoi phe duyet \n");
        sql.append("        (ct.APPROVED_DATE +  \n");
        sql.append(numberDaysExpire);
        sql.append(" )          as expDate       -- Ngay het han \n");
        sql.append("        from customer c join CONTRACT ct on ct.CUST_ID = c.cust_id \n");
        sql.append("        left join vehicle v on v.contract_id = ct.contract_id ");
        sql.append("        join action_audit act on act.CONTRACT_ID = ct.CONTRACT_ID ");
        sql.append("        join act_type at on act.act_type_id = at.act_type_id ");
        sql.append("        join cust_type cust on cust.cust_type_id = c.cust_type_id ");
        // Ki moi hop dong = 11 va ki phu luc hop dong = 12
        sql.append("        where act.act_type_id IN (11,12) ");
        // Lay cac hop dong dang hoat dong = 2
        sql.append("        and ct.status = 2 ");
        /*==========================================
            Them param custId de filter bang ACTION_AUDIT
         ===========================================
         */
        // Tu ngay
        sql.append(" and act.create_date >=  to_date(?,'dd/mm/yyyy') ");
        arrParams.add(searchBriefcasesDTO.getFromDate().trim());
        // Den ngay
        sql.append(" and act.create_date <= to_date(?,'dd/mm/yyyy hh24:mi:ss') ");
        arrParams.add(searchBriefcasesDTO.getToDate().trim() + Constants.TIME_ONE_DAY);
        //ShopId
        sql.append(" and ct.SHOP_ID = ? ");
        arrParams.add(searchBriefcasesDTO.getShopId());
        if (!FnCommon.isNullOrEmpty(searchBriefcasesDTO.getContractNo())) {
            sql.append(" and LOWER(ct.contract_no)  like '%' || LOWER(?) ||'%' ");
            arrParams.add(searchBriefcasesDTO.getContractNo().trim());
        }

        if (!FnCommon.isNullOrEmpty(searchBriefcasesDTO.getDocumentNumber())) {
            sql.append(" and LOWER(c.document_number)  like '%' || LOWER(?) ||'%' ");
            arrParams.add(searchBriefcasesDTO.getDocumentNumber().trim());
        }

        if (!FnCommon.isNullOrEmpty(searchBriefcasesDTO.getPlateNumber())) {
            sql.append(" and LOWER(v.plate_number)  like '%' || LOWER(?) ||'%' ");
            arrParams.add(searchBriefcasesDTO.getPlateNumber().trim());
        }

        if (searchBriefcasesDTO.getCustId() != null) {
            sql.append(" and ct.CUST_ID = ?");
            arrParams.add(searchBriefcasesDTO.getCustId());
        }

        if (searchBriefcasesDTO.getCustTypeId() != null) {
            sql.append(" and c.cust_type_id = ? ");
            arrParams.add(searchBriefcasesDTO.getCustTypeId());
        }


        if (!FnCommon.isNullOrEmpty(searchBriefcasesDTO.getProfileStatus())) {
            sql.append(" and ct.PROFILE_STATUS  IN ( ");
            sql.append(searchBriefcasesDTO.getProfileStatus());
            sql.append(" ) ");
        }

        sql.append(" order by ct.PROFILE_STATUS , ct.create_date desc ");

        Integer start = 0;
        if (searchBriefcasesDTO.getStartrecord() != null) {
            start = searchBriefcasesDTO.getStartrecord();
        }
        Integer pageSize = null;
        if (searchBriefcasesDTO.getPagesize() != null) {
            pageSize = searchBriefcasesDTO.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, SearchBriefcasesDTO.class);
    }
}
