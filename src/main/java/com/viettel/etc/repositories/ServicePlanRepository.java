package com.viettel.etc.repositories;

import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanFeeDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.List;

/**
 * Autogen class Repository Interface: Lop thao tac danh sach ca nhan
 *
 * @author toolGen
 * @date Wed Jul 01 09:00:20 ICT 2020
 */
public interface ServicePlanRepository {
    ResultSelectEntity searchTicketPrices(ServicePlanDTO servicePlanDTO);

    ServicePlanFeeDTO getFee(ServicePlanDTO itemParamsEntity);

    ResultSelectEntity getFeeMobile(ServicePlanDTO servicePlanDTO);

    ResultSelectEntity getMinFee(ServicePlanDTO servicePlanDTO);

    ResultSelectEntity findTicketPricesExits(ServicePlanDTO dataParams);

    ResultSelectEntity findTicketPriceExitsEffDate(ServicePlanDTO servicePlanDTO);

    ServicePlanVehicleDuplicateDTO checkExistsTicketNew(ServicePlanDTO param);

    ResultSelectEntity getFeeNew(ServicePlanDTO servicePlanDTO, Long excVehicleId, Long excTicketId, List<String> vehicleGroupId, Long vehicleGroupIdOld);

    ServicePlanDTO getServicePlan(Long stationId, Long stageId, Long servicePlanTypeId, Long vehicleGroupId, String effDate, String expDate, String offerId);
}
