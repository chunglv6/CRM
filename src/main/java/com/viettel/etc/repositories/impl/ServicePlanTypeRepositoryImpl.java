package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ServicePlanTypeDTO;
import com.viettel.etc.repositories.ServicePlanTypeRepository;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class ServicePlanTypeRepositoryImpl extends CommonDataBaseRepository implements ServicePlanTypeRepository {

    /**
     * Lay danh sach loai ve
     *
     * @return
     */
    @Override
    public ResultSelectEntity getTicketType() {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        //create sql query
        sql.append("select SERVICE_PLAN_TYPE_ID                         as servicePlanTypeId, \n");
        sql.append("NAME                                                as name,                  -- Loai ve\n");
        sql.append("DURATION                                            as duration       -- Loai PT thu phi\n");
        sql.append("from SERVICE_PLAN_TYPE  order by NAME \n");

        Integer start = null;
        Integer pageSize = null;
        return getListDataAndCount(sql, hmapParams, start, pageSize, ServicePlanTypeDTO.class);
    }
}
