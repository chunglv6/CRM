package com.viettel.etc.repositories;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

public interface ServiceFeeRepository {
    ResultSelectEntity getServiceFee(ServiceFeeDTO params);
}
