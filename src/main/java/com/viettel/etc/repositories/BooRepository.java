package com.viettel.etc.repositories;

import com.viettel.etc.dto.boo.*;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.List;

public interface BooRepository {
    DataBooDTO findByPlateNumberAndEpc(String plateNumber, String epc);

    ResultSelectEntity queryTicketBoo(ReqQueryTicketDTO requestQueryTicketBooDTO);

    ResultSelectEntity getFeeBoo(ReqCalculatorTicketDTO reqCalculatorTicketDTO, List<String> vehicleGroupId, Long stageId, Long methodChargeId, Long excVehicleId, Long excTicketId, Long vehicleGroupIdOld, boolean isCallFromBOO);

    ResultSelectEntity getListCategoryMappingBoo(ReqMappingDTO categoryMappingBooDTO);
}
