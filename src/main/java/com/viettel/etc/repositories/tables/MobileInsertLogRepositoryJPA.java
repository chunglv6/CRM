package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.MobileInsertLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileInsertLogRepositoryJPA extends JpaRepository<MobileInsertLogEntity, Long> {

    MobileInsertLogEntity getByImac(String imac);
}
