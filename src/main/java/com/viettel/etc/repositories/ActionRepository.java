package com.viettel.etc.repositories;

import com.viettel.etc.dto.*;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.List;

/**
 * Autogen class Repository Interface: Lop ve tac dong
 *
 * @author toolGen
 * @date Mon Jun 29 11:19:53 ICT 2020
 */
public interface ActionRepository {

    List<ActionDTO> getActionType(ActionDTO itemParamsEntity);

    ResultSelectEntity getActionHistory(ActionAuditHistoryDTO itemParamsEntity);

    List<ActionDTO> getActionReason(ActionDTO itemParamsEntity, Long id);

    ResultSelectEntity getActionHistoryDetail(ActionAuditHistoryDetailDTO itemParamsEntity, Long id);

    ResultSelectEntity findActionCustomerHistory(AuditHistoryDTO requestModel);

    ResultSelectEntity actionCustomerHistory(AuditHistoryDTO requestModel);

    ResultSelectEntity findRfidVehicleHistories(Long vehicleId, ActionDTO actionDTO);

    Object countData();
}
