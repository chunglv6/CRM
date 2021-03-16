package com.viettel.etc.services;

import com.viettel.etc.dto.ActionAuditDetailTempDTO;

import java.util.List;

public interface ActionAuditDetailService {
    void updateActionAuditDetail(Long actionAuditId, Long pkId, List<ActionAuditDetailTempDTO> actionAuditDetailTempDTOList, String tableName);
}
