package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.MobileAutoUpdateVerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileAutoUpdateVerJPA extends JpaRepository<MobileAutoUpdateVerEntity, Long> {
    MobileAutoUpdateVerEntity findFirstByAppCodeAndDeviceTypeOrderByCreateDateDesc(String appCode,String deviceType);

}
