package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SearchDepositTransDTO;
import com.viettel.etc.repositories.SearchDepositTransRepository;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.HashMap;

import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;
import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT_24H;

/**
 * Autogen class Repository Impl: Lop thao tac xem thong tin giao dich nap tien
 * 
 * @author ToolGen
 * @date Mon Feb 08 09:01:09 ICT 2021
 */
@Repository
public class SearchDepositTransRepositoryImpl extends CommonDataBaseRepository implements SearchDepositTransRepository{    

    /**
     * Lay du lieu giao dich nap tien
     * 
     * @param itemParamsEntity: params client truyen len
     * @return 
     */
    @Override
    public ResultSelectEntity getDepositInfo(SearchDepositTransDTO itemParamsEntity){
         StringBuilder sql = new StringBuilder();
         HashMap<String, Object> hmapParams = new HashMap<>();
         sql.append("select distinct                              \n");
         sql.append("TE.TOPUP_ETC_ID            as ID,            \n");
         sql.append("TE.TOPUP_DATE              as topupDate,     \n");
         sql.append("TE.STAFF_NAME              as staffName,     \n");
         sql.append("TE.CONTRACT_ID             as contractId,    \n");
         sql.append("TE.SALE_ORDER_ID           as saleOrderId,    \n");
         sql.append("TE.AMOUNT                  as amount         \n");
         sql.append(" from topup_etc TE                           \n");
         sql.append(" where 1 =1                                  \n");


        //Tu ngay
        if (itemParamsEntity.getStartDate() != null && itemParamsEntity.getEndDate() == null) {
            sql.append("and TE.TOPUP_DATE >= to_date(:startDate,'" + COMMON_DATE_FORMAT + "') \n");
            hmapParams.put("startDate", itemParamsEntity.getStartDate());
        }

        //Den ngay
        if (itemParamsEntity.getStartDate() == null && itemParamsEntity.getEndDate() != null) {
            sql.append("and TE.TOPUP_DATE <= to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "') \n");
            hmapParams.put("endDate", itemParamsEntity.getEndDate() + " 23:59:59");
        }

        //Khoang ngay
        if (itemParamsEntity.getStartDate() != null && itemParamsEntity.getEndDate() != null) {
            sql.append("and TE.TOPUP_DATE between to_date(:startDate,'" + COMMON_DATE_FORMAT + "') and to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "') \n");
            hmapParams.put("startDate", itemParamsEntity.getStartDate());
            hmapParams.put("endDate", itemParamsEntity.getEndDate() + " 23:59:59");
        }
        if(itemParamsEntity.getStaffName() != null && !"".equals(itemParamsEntity.getStaffName())){
            sql.append(" and ( TE.STAFF_NAME like :staffNameU \n");
            hmapParams.put("staffNameU","%"+itemParamsEntity.getStaffName().toUpperCase()+"%");
            sql.append(" or  TE.STAFF_NAME like :staffNameL ) \n");
            hmapParams.put("staffNameL","%"+itemParamsEntity.getStaffName().toLowerCase()+"%");

        }
        if(itemParamsEntity.getContractId() != null && !"".equals(itemParamsEntity.getContractId())){
            sql.append(" and TE.CONTRACT_ID  like '%' || :contractId || '%' \n");
            hmapParams.put("contractId",itemParamsEntity.getContractId());
        }
        if(itemParamsEntity.getSaleOrderId() != null && !"".equals(itemParamsEntity.getSaleOrderId())){
            sql.append(" and TE.SALE_ORDER_ID like '%' || :saleOrderId || '%' \n");
            hmapParams.put("saleOrderId",itemParamsEntity.getSaleOrderId());
        }
         sql.append(" order by TE.TOPUP_DATE DESC \n");

         Integer start = null;
         if(itemParamsEntity!=null && itemParamsEntity.getStartrecord()!=null){
             start = itemParamsEntity.getStartrecord();
         }
         Integer pageSize = null;
         if(itemParamsEntity!=null && itemParamsEntity.getPagesize()!=null){
             pageSize = itemParamsEntity.getPagesize();
         }
         ResultSelectEntity resultData = getListDataAndCount(sql, hmapParams, start, pageSize,SearchDepositTransDTO.class);
         return resultData;
    }

}