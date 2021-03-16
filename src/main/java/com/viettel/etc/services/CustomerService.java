package com.viettel.etc.services;

import com.viettel.etc.dto.CustRegisterDTO;
import com.viettel.etc.dto.CustomerDTO;
import com.viettel.etc.dto.CustomerSearchDTO;
import com.viettel.etc.dto.SearchInfoDTO;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.security.core.Authentication;


public interface CustomerService {
    Object addCustomerEnterprise(CustomerDTO dataParams, Authentication authentication) throws EtcException, Exception;

    Object addCustomer(CustomerDTO customerDTO, Authentication authentication) throws EtcException, Exception;

    CustomerEntity addCustomerQuick(CustomerDTO customerDTO, Authentication authentication);

    void updateCustomer(CustomerDTO customerDTO, Long custId, Authentication authentication) throws Exception;

    void updateCustomer(String phone, String email, Authentication authentication) throws Exception;

    void updateCustomerEnterprise(CustomerDTO customerDTO, Long custId, Authentication authentication) throws Exception;

    Object findCustomerByDocumentAndPhone(CustomerSearchDTO itemParamsEntity);

    Object searchTreeInfo(SearchInfoDTO dataParams);

    Object findCustomerById(Long custId, String accessToken);

    Object getCustomerRegister(CustRegisterDTO itemParamsEntity);
}
