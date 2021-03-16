package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActionAuditDTO;
import com.viettel.etc.dto.ActionAuditDetailDTO;
import com.viettel.etc.repositories.tables.ActReasonRepositoryJPA;
import com.viettel.etc.repositories.tables.ActionAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ActReasonEntity;
import com.viettel.etc.repositories.tables.entities.ActionAuditDetailEntity;
import com.viettel.etc.repositories.tables.entities.ActionAuditEntity;
import com.viettel.etc.services.ActionAuditService;
import com.viettel.etc.services.tables.ActionAuditDetailServiceJPA;
import com.viettel.etc.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

@Service
public class ActionAuditServiceImpl implements ActionAuditService {

    @Autowired
    ActionAuditRepositoryJPA actionAuditRepositoryJPA;

    @Autowired
    ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Autowired
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    /**
     * update log to action audit
     *
     * @param actionAuditEntity
     * @return
     * @throws Exception
     */
    @Override
    public ActionAuditEntity updateLogToActionAudit(ActionAuditEntity actionAuditEntity) {
        if (Objects.isNull(actionAuditEntity.getActReasonId())) {
            actionAuditEntity.setActReasonId(getReasonType(actionAuditEntity.getActTypeId()));
        }
        return actionAuditRepositoryJPA.save(actionAuditEntity);
    }

    @Override
    public <T> ActionAuditEntity updateLogAuditAndAuditDetail(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, T oldEntity, T newEntity) throws Exception {
        if (Objects.isNull(reasonId)) {
            reasonId = getReasonType(actTypeId);
        }
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, custId, contractId, vehicleId, ip));
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(oldEntity, newEntity).toEntity(actionAuditEntity.getActionAuditId(), oldEntity.getClass().getAnnotation(Table.class).name(), custId);
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
        return actionAuditEntity;
    }

    private long getReasonType(Long actTypeId) {
        List<ActReasonEntity> list = actReasonRepositoryJPA.findAllByActTypeId(actTypeId);
        if (list != null && list.size() > Constants.SIZE_LIST_ZERO) {
            return list.get(0).getActReasonId();
        }
        return Constants.ACT_REASON.DEFAULT;
    }
}
