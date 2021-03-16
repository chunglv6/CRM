package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.LuckyCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LuckyCodeRepositoryJPA extends JpaRepository<LuckyCodeEntity, Long> {
    List<LuckyCodeEntity> getByCustomerIdAndLuckyType(Long customerId, Integer luckyType);

    boolean existsByLuckyCode(String luckyCode);
}
