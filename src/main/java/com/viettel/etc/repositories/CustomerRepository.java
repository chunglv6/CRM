package com.viettel.etc.repositories;

import com.viettel.etc.dto.CustRegisterDTO;
import com.viettel.etc.dto.CustomerSearchDTO;
import com.viettel.etc.dto.SearchInfoDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

/**
 * Autogen class Repository Interface: Lop thao tac danh sach ca nhan
 *
 * @author toolGen
 * @date Thu Jul 02 14:40:47 ICT 2020
 */
public interface CustomerRepository {

    ResultSelectEntity findCustomerByDocumentAndPhone(CustomerSearchDTO itemParamsEntity);

    ResultSelectEntity searchTreeInfo(SearchInfoDTO requestModel);

    ResultSelectEntity findCustomerById(Long custId);

    ResultSelectEntity getCustomerRegister(CustRegisterDTO itemParamsEntity);
}
