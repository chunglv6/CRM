package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.TopupDailyRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.TopupDailyEntity;
import com.viettel.etc.utils.FnCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TopupDailyServiceJPA {
    @Autowired
    TopupDailyRepositoryJPA repository;

    public boolean isAddBalanced(String accountUser) {
        Date date = FnCommon.round(Calendar.getInstance().getTime());
        return repository.existsByAccountUserAndTopupDateAfter(accountUser, date);
    }

    public TopupDailyEntity save(TopupDailyEntity entity) {
        return repository.save(entity);
    }

    public TopupDailyEntity getByAccountUserAndTopupDateAfter(String accountUser, Date topupDate) {
        List<TopupDailyEntity> list =  repository.getByAccountUserAndTopupDateAfterOrderByTopupDateAsc(accountUser, topupDate);
        if (FnCommon.isNullOrEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public TopupDailyEntity getByAccountUserAndTopupDate(String accountUser, Date topupDate) {
        return repository.getByAccountUserAndTopupDateBetween(accountUser, topupDate, FnCommon.addDays(topupDate, 1));
    }
}
