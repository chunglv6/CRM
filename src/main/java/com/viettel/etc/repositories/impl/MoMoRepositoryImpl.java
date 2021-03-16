package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.momo.MoMoGetContractRequestDTO;
import com.viettel.etc.dto.momo.MoMoGetContractResponseDTO;
import com.viettel.etc.repositories.MoMoRepository;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MoMoRepositoryImpl extends CommonDataBaseRepository implements MoMoRepository {
    @Override
    public Object findByPlateOrContract(String searchType, String contractNo, String plateType, String plateNumber) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select\n");
        sql.append("c.contract_id                   as contractId,\n");
        sql.append("c.contract_no                   as contractNo,\n");
        sql.append("cu.cust_name                    as custName,\n");
        sql.append("cu.phone_number                 as msisdn\n");

        if (!FnCommon.isNullOrEmpty(searchType) && MoMoGetContractRequestDTO.SearchType.CONTRACT_NO.value.equals(searchType)) {
            sql.append("from CONTRACT c inner join  CUSTOMER cu on cu.cust_id = c.cust_id\n");
            sql.append("where  c.status = 2 and ");
            sql.append(" c.contract_no = ?");
            arrParams.add(contractNo);
        }
        if (!FnCommon.isNullOrEmpty(searchType) && MoMoGetContractRequestDTO.SearchType.PLATE_NUMBER.value.equals(searchType)) {
            sql.append("from CONTRACT c inner join  CUSTOMER cu on cu.cust_id = c.cust_id\n");
            sql.append("inner join vehicle vh on vh.contract_id = c.contract_id\n");
            sql.append("where vh.plate_type_code = ? and vh.plate_number= ? and vh.status=1 and vh.active_status <>5");
            arrParams.add(plateType);
            arrParams.add(plateNumber);
        }
        return getFirstData(sql, arrParams, MoMoGetContractResponseDTO.class);
    }
}
