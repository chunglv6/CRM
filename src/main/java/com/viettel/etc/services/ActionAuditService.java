package com.viettel.etc.services;

import com.viettel.etc.repositories.tables.entities.ActionAuditEntity;
import org.springframework.security.core.Authentication;

public interface ActionAuditService {
    ActionAuditEntity updateLogToActionAudit(ActionAuditEntity actionAuditEntity);

    <T> ActionAuditEntity updateLogAuditAndAuditDetail(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, T oldEntity, T newEntity) throws Exception;
}
