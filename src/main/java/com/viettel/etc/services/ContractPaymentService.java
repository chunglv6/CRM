package com.viettel.etc.services;

import com.viettel.etc.dto.viettelpay.RequestConfirmChangeMoneySourceDTO;
import com.viettel.etc.dto.viettelpay.RequestConfirmRegisterDTO;
import com.viettel.etc.dto.viettelpay.RequestContractPaymentDTO;
import com.viettel.etc.repositories.tables.entities.ContractPaymentEntity;
import org.springframework.security.core.Authentication;

public interface ContractPaymentService {
    boolean updateContractPaymentWhenRegister(RequestConfirmRegisterDTO data, Authentication authentication);

    boolean updateContractPaymentWhenUnRegister(RequestContractPaymentDTO data);

    boolean changeMoneySourceViettelPay(RequestConfirmChangeMoneySourceDTO data, Authentication authentication);

    ContractPaymentEntity findByContractId(Long contractId);

    ContractPaymentEntity findByContractIdAndStatus(Long contractId, String status);
}
