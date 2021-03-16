package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.TopupAccountConfigRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.TopupAccountConfigEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TopupAccountConfigServiceJPA {
    
    @Autowired
    TopupAccountConfigRepositoryJPA repository;

    public List<TopupAccountConfigEntity> getByAccountUser(String accountUser) {
        return repository.getByAccountUser(accountUser);
    }

    public TopupAccountConfigEntity get(Long id) {
        return repository.getOne(id);
    }

    public TopupAccountConfigEntity getActiveByAccountUser(String accountUser) {
        return repository.getByAccountUserAndStatus(accountUser, Constants.STATUS.ACTIVE);
    }

    public TopupAccountConfigEntity getByAccountUserAndCreateDate(String accountUser, Date createDate) {
        List<TopupAccountConfigEntity> listAccount = repository.getByAccountUserAndCreateDateBeforeOrderByCreateDateDesc(accountUser, createDate);
        if (FnCommon.isNullOrEmpty(listAccount)) {
            return null;
        }
        return listAccount.get(0);
    }

    public TopupAccountConfigEntity save(TopupAccountConfigEntity entity) {
        return repository.save(entity);
    }
}
