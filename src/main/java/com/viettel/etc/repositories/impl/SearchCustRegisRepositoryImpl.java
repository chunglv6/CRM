package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SearchCustRegisDTO;
import com.viettel.etc.repositories.SearchCustRegisRepository;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;
import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT_24H;

/**
 * Autogen class Repository Impl: Lay thong tin khach hang da dang ki
 * 
 * @author ToolGen
 * @date Mon Feb 01 15:43:14 ICT 2021
 */
@Repository
public class SearchCustRegisRepositoryImpl extends CommonDataBaseRepository implements SearchCustRegisRepository{    

    /**
     * Lay du lieu doanh thu cua bot
     * 
     * @param itemParamsEntity: params client truyen len
     * @return 
     */
    @Override
    public ResultSelectEntity searchCustRegstered(SearchCustRegisDTO itemParamsEntity){
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select distinct \n");
        sql.append("CR.CUST_NAME             as custName,          \n");
        sql.append("CR.PHONE_NUMBER          as phoneNumber,       \n");
        sql.append("CR.PROVINCE_NAME         as provinceName,       \n");
        sql.append("CR.DISTRIC_NAME          as districName,        \n");
        sql.append("CR.COMMUNE_NAME          as communeName,        \n");
        sql.append("CR.PLATE_NUMBER          as plateNumber,       \n");
        sql.append("CR.REG_DATE              as regDate,           \n");
        sql.append("CR.ORDER_NUMBER           as orderNumber         \n");
        sql.append(" from cust_regis  CR where 1 = 1 \n");
        //Tu ngay
        if (itemParamsEntity.getStartDate() != null && itemParamsEntity.getEndDate() == null) {
            sql.append("and CR.REG_DATE >= to_date(:startDate,'" + COMMON_DATE_FORMAT + "') \n");
            hmapParams.put("startDate", itemParamsEntity.getStartDate());
        }

        //Den ngay
        if (itemParamsEntity.getStartDate() == null && itemParamsEntity.getEndDate() != null) {
            sql.append("and CR.REG_DATE <= to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "') \n");
            hmapParams.put("endDate", itemParamsEntity.getEndDate() + " 23:59:59");
        }

        //Khoang ngay
        if (itemParamsEntity.getStartDate() != null && itemParamsEntity.getEndDate() != null) {
            sql.append("and CR.REG_DATE between to_date(:startDate,'" + COMMON_DATE_FORMAT + "') and to_date(:endDate,'" + COMMON_DATE_FORMAT_24H + "') \n");
            hmapParams.put("startDate", itemParamsEntity.getStartDate());
            hmapParams.put("endDate", itemParamsEntity.getEndDate() + " 23:59:59");
        }

        sql.append(" order by CR.REG_DATE DESC \n");

        Integer start = null;
        if(itemParamsEntity!=null && itemParamsEntity.getStartrecord()!=null){
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if(itemParamsEntity!=null && itemParamsEntity.getPagesize()!=null){
            pageSize = itemParamsEntity.getPagesize();
        }
        ResultSelectEntity resultData = getListDataAndCount(sql, hmapParams, start, pageSize, SearchCustRegisDTO.class);
        return resultData;
    }
}