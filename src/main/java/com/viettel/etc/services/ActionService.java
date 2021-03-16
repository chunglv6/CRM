package com.viettel.etc.services;

import com.viettel.etc.dto.*;

import java.io.IOException;

/**
 * Autogen class: Lop ve tac dong
 *
 * @author ToolGen
 * @date Mon Jun 29 11:19:53 ICT 2020
 */
public interface ActionService {

    Object getActionType(ActionDTO itemParamsEntity);

    Object getActionHistory(ActionAuditHistoryDTO itemParamsEntity);

    Object getActionReason(ActionDTO itemParamsEntity, Long id);

    Object getActionHistoryDetail(ActionAuditHistoryDetailDTO itemParamsEntity, Long id);

    Object findActionCustomerHistory(AuditHistoryDTO requestModel);

    String exportActHistory(AuditHistoryDTO dataParams) throws IOException;

    Object actionCustomerHistory(AuditHistoryDTO requestModel);

    String exportActionHistory(ActionAuditHistoryDTO dataParams) throws IOException;

    String exportActionHistoryVehicle(AuditHistoryDTO dataParams) throws IOException;

    Object searchRfidVehicleHistories(Long vehicleId, ActionDTO actionDTO);

    Object getCountShowData();
}
