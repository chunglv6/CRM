package com.viettel.etc.services.impl;

import com.viettel.etc.dto.SaleOrderDTO;
import com.viettel.etc.repositories.SaleOrderRepository;
import com.viettel.etc.services.SaleOrderService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Log4j
@Service
public class SaleOrderServiceImpl implements SaleOrderService {

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Override
    public Object getSaleOrder(SaleOrderDTO params) {
        if (params.getSaleOrderAfter() != null) {
            Date after = params.getSaleOrderAfter();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(after);
            calendar.add(Calendar.DATE, 1);
            after = calendar.getTime();
            params.setSaleOrderAfter(after);
        }
        return saleOrderRepository.getSaleOrder(params);
    }
}
