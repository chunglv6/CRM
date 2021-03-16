package com.viettel.etc.services;

import com.viettel.etc.dto.ServiceFeeDTO;
import org.springframework.security.core.Authentication;

public interface ServiceFeeService {
    Object getServiceFee(ServiceFeeDTO params);

    Object getDetailServiceFee(Long serviceFeeId);

    Object getDetailServiceFeeByReason(Long actReasonId);

    Object addServiceFee(ServiceFeeDTO params, Authentication authentication) throws Exception;

    Object editServiceFee(ServiceFeeDTO params, Authentication authentication, Long serviceFeeId) throws Exception;

    Object deleteServiceFee(Authentication authentication, Long serviceFeeId) throws Exception;

    Object approveServiceFee(String listId, Authentication authentication);

    String exportServiceFee(ServiceFeeDTO itemParamsEntity) throws Exception;
}
