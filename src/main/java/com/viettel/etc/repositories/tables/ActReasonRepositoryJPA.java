package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.ActReasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Act_reason
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Repository
public interface ActReasonRepositoryJPA extends JpaRepository<ActReasonEntity, Long> {
    List<ActReasonEntity> findAllByActTypeIdAndStatus(Long actTypeId, String status);

    default List<ActReasonEntity> findAllByActTypeId(Long actTypeId) {
        return findAllByActTypeIdAndStatus(actTypeId, ActReasonEntity.Status.ACTIVATED.value);
    }

    List<ActReasonEntity> findAllByCodeAndStatus(String code, String status);

    @Query(value = "SELECT ACT_REASON_ID FROM ACT_REASON where ACT_TYPE_ID = :actTypeId", nativeQuery = true)
    Long findActReasonIdByActTypeId(Long actTypeId);
}
