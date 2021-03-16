package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.TopupAccountConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TopupAccountConfigRepositoryJPA extends JpaRepository<TopupAccountConfigEntity, Long> {
    TopupAccountConfigEntity getByAccountUserAndStatus(String accountUser, String status);

    List<TopupAccountConfigEntity> getByAccountUserAndCreateDateBeforeOrderByCreateDateDesc(String accountUser, Date createDate);

    List<TopupAccountConfigEntity> getByAccountUser(String accountUser);
}
