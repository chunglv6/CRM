package com.viettel.etc.repositories;

import com.viettel.etc.dto.SaleTransDTO;
import com.viettel.etc.dto.SaleTransVehicleOwnerAppDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.List;

/**
 * Autogen class Repository Interface: Lop thao tac danh sach lich su
 *
 * @author toolGen
 * @date Fri Jul 10 10:48:49 ICT 2020
 */
public interface SaleTransRepository {

    ResultSelectEntity findOtherTransactionHistories(Long contractId, SaleTransDTO saleTransDTO);

    List<SaleTransDTO> getServiceFees(SaleTransDTO itemParamsEntity);

    ResultSelectEntity vehicleOwnerAppOtherTransactionHistories(SaleTransVehicleOwnerAppDTO transVehicleOwnerAppDTO, Long contractId);
}
