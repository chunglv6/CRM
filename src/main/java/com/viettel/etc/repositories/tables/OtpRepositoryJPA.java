package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.OtpEntity;
import com.viettel.etc.repositories.tables.entities.OtpIdentify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepositoryJPA extends JpaRepository<OtpEntity, OtpIdentify> {
}
