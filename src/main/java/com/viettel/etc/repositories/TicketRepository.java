package com.viettel.etc.repositories;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;

import java.util.List;

public interface TicketRepository {
    List<ServicePlanDTO> getFee(ServicePlanDTO itemParamsEntity);

    ServicePlanVehicleDuplicateDTO checkExistsTicket(ServicePlanDTO param, boolean isCallFromBoo1);
}
