package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.TopupAccountDTO;
import com.viettel.etc.repositories.TopupAccountConfigRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

@Service
public class TopupAccountConfigRepositoryImpl extends CommonDataBaseRepository implements TopupAccountConfigRepository {
    @Override
    public ResultSelectEntity findTopupAccount(TopupAccountDTO params) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("SELECT\n")
                .append("     A.TOPUP_ACCOUNT_CONFIG_ID     as      topupAccountConfigId,\n")
                .append("     A.ACCOUNT_USER                as      accountUser,\n")
                .append("     A.ACCOUNT_FULLNAME            as      accountFullname\n")
                .append("FROM TOPUP_ACCOUNT_CONFIG A\n");

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
        sql.append("WHERE A.CREATE_DATE < TO_DATE(:topupDate , :format) \n")
            .append("AND (A.STATUS = :status OR A.UPDATE_DATE > TO_DATE(:topupDate , :format))\n");
        hmapParams.put("status", Constants.STATUS.ACTIVE);
        hmapParams.put("topupDate", formatter.format(FnCommon.addDays(params.getTopupDate(), 1)));
        hmapParams.put("format", Constants.ORACLE_DATE_FORMAT);

        if (!FnCommon.isNullOrBlank(params.getAccountUser())) {
            sql.append("AND UPPER(A.ACCOUNT_USER) like :accountUser\n");
            hmapParams.put("accountUser", params.getAccountUser().trim().toUpperCase() + "%");
        }

        Integer start = 0;
        if (!FnCommon.isNullObject(params.getStartrecord())) {
            start = params.getStartrecord();
        }

        Integer pageSize = 10;
        if (!FnCommon.isNullObject(params.getPagesize())) {
            pageSize = params.getPagesize();
        }

        return getListDataAndCount(sql, hmapParams, start, pageSize, TopupAccountDTO.class);
    }
}
