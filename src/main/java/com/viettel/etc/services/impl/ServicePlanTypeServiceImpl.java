package com.viettel.etc.services.impl;

import com.viettel.etc.repositories.ServicePlanTypeRepository;
import com.viettel.etc.services.ServicePlanTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicePlanTypeServiceImpl implements ServicePlanTypeService {
    @Autowired
    ServicePlanTypeRepository servicePlanTypeRepository;

    @Override
    public Object getTicketType() {
        return servicePlanTypeRepository.getTicketType();
    }
}
