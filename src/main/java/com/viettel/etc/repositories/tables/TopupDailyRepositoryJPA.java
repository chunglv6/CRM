package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.TopupDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TopupDailyRepositoryJPA extends JpaRepository<TopupDailyEntity, Long> {
    boolean existsByAccountUserAndTopupDateAfter(String accountUser, Date after);

    List<TopupDailyEntity> getByAccountUserAndTopupDateAfterOrderByTopupDateAsc(String accountUser, Date topupDate);

    TopupDailyEntity getByAccountUserAndTopupDateBetween(String accountUser, Date topupDate1, Date topupDate2);
}
