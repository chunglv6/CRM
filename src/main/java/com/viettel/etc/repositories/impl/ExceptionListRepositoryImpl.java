package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.boo.ReqCalculatorTicketDTO;
import com.viettel.etc.repositories.ExceptionListRepository;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class ExceptionListRepositoryImpl extends CommonDataBaseRepository implements ExceptionListRepository {

    /***
     * Tim kiem ve co dc mua hay khong
     * @param reqCalculatorTicketDTO
     * @param licensePlateType
     * @return
     */
    @Override
    public List<ExceptionListEntity> findAllExceptionEffective(ReqCalculatorTicketDTO reqCalculatorTicketDTO, Long  stageId, Long stationId, String licensePlateType) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append(" SELECT \n");
        sql.append(" EXC.* from EXCEPTION_LIST EXC \n");
        sql.append(" where EXC.STATUS = 4 \n");
        sql.append(" and (EXC.STATION_ID =:station_id or EXC.STATION_ID is null) \n");
        sql.append(" and EXC.EXCEPTION_TYPE in (1,2,3,4) \n");
        if (Constants.RACH_MIEU_STATION.equals(reqCalculatorTicketDTO.getStation_in_id())) {
            hmapParams.put("station_id", Constants.RACH_MIEU_STATION);
        }else {
            hmapParams.put("station_id", FnCommon.isNullObject(stationId) ? null : stationId);
        }
        sql.append(" and (EXC.STAGE_ID =:stage_id or EXC.STAGE_ID is null) \n");
        hmapParams.put("stage_id", FnCommon.isNullObject(stageId) ? null : stageId);

        sql.append(" and EXC.plate_number =:plate_number \n");
        hmapParams.put("plate_number", reqCalculatorTicketDTO.getPlate());

        sql.append(" and EXC.epc =:epc \n");
        hmapParams.put("epc", reqCalculatorTicketDTO.getEtag());

        sql.append(" and EXC.license_plate_type =:license_plate_type \n");
        hmapParams.put("license_plate_type", licensePlateType);

        sql.append(" and ((to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "')  >= EXC.EFF_DATE  and to_date(:expDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "') <= EXC.EXP_DATE)");
        sql.append(" or(to_date(:effDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "')  > EXC.EXP_DATE");
        sql.append(" or to_date(:expDate,'" + Constants.COMMON_DATE_FORMAT_BOO + "')  < EXC.EFF_DATE))");
        hmapParams.put("effDate", reqCalculatorTicketDTO.getStart_date());
        hmapParams.put("expDate", reqCalculatorTicketDTO.getEnd_date());
        return (List<ExceptionListEntity>) getListDataAndCount(sql, hmapParams, 0, null, ExceptionListEntity.class).getListData();
    }
}
