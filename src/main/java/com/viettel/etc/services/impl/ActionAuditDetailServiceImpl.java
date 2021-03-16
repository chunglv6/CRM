package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActionAuditDetailTempDTO;
import com.viettel.etc.repositories.tables.ActionAuditDetailRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ActionAuditDetailEntity;
import com.viettel.etc.services.ActionAuditDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActionAuditDetailServiceImpl implements ActionAuditDetailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(VehicleServiceImpl.class);

    @Autowired
    private ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    /**
     * update log to action audit detail
     *
     * @param actionAuditId
     * @param pkId
     * @param actionAuditDetailTempDTOList
     */
    @Override
    public void updateActionAuditDetail(Long actionAuditId, Long pkId, List<ActionAuditDetailTempDTO> actionAuditDetailTempDTOList, String tableName) {
        List<ActionAuditDetailEntity> actionAuditDetailEntityList = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        for (ActionAuditDetailTempDTO actionAuditDetailTempDTO : actionAuditDetailTempDTOList) {
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
            actionAuditDetailEntity.setActionAuditId(actionAuditId);
            actionAuditDetailEntity.setCreateDate(date);
            actionAuditDetailEntity.setTableName(tableName);
            actionAuditDetailEntity.setPkId(pkId);
            actionAuditDetailEntity.setColunmName(actionAuditDetailTempDTO.getColumnName());
            actionAuditDetailEntity.setOldValue(actionAuditDetailTempDTO.getOldValue().toString());
            actionAuditDetailEntity.setNewValue(actionAuditDetailTempDTO.getNewValue().toString());
            actionAuditDetailEntityList.add(actionAuditDetailEntity);
        }
        try {
            actionAuditDetailRepositoryJPA.saveAll(actionAuditDetailEntityList);
        } catch (Exception e) {
            LOGGER.error("Loi! update audit detail: ", e);
        }
    }
}
