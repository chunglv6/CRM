package com.viettel.etc.services;

import com.viettel.etc.dto.SaleTransDTO;
import com.viettel.etc.dto.SaleTransVehicleOwnerAppDTO;
import com.viettel.etc.repositories.tables.entities.SaleTransEntity;
import org.springframework.security.core.Authentication;


public interface SaleTransService {
    void updateSaleTrans(Long amount, Long custId, Authentication authenEntity);

    void updateSaleTrans(Long amount, Long custId, Long contractId, String contractNo, Authentication authenEntity);

    void updateSaleTransService(Long actionTypeId, Long custId, Authentication authenEntity);

    Object findOtherTransactionHistories(Long contractId, SaleTransDTO saleTransDTO);

    String exportOtherTransactionHistories(Long contractId, SaleTransDTO saleTransDTO);

    Object vehicleOwnerAppOtherTransactionHistories(SaleTransVehicleOwnerAppDTO transVehicleOwnerAppDTO, String accountUser);

    Object getServiceFees(SaleTransDTO itemParamsEntity);

    SaleTransEntity addSaleTrans(Long custId, Long contractId, String contractNo, Authentication authenEntity, Long actTypeId);

    SaleTransEntity addSaleTrans(Long custId, Authentication authenEntity, Long actTypeId);
}
