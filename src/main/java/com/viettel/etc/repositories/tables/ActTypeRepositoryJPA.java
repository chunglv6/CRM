package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.ActTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Act_type
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Repository
public interface ActTypeRepositoryJPA extends JpaRepository<ActTypeEntity, Long> {
    List<ActTypeEntity> findAllByCodeAndStatus(String code, String status);
    @Query(value = "SELECT ACT_TYPE_ID FROM ACT_TYPE where CODE = :code", nativeQuery = true)
    Long findActTypeIdByCode(String code);
}
