package com.viettel.etc.repositories;

import com.viettel.etc.dto.SaleOrderDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

public interface SaleOrderRepository {
    ResultSelectEntity getSaleOrder(SaleOrderDTO params);
}
