package com.viettel.etc.services;

import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImportServicePlanService {
    ResponseEntity<?> importServicePlan(MultipartFile fileImport, Authentication authentication) throws EtcException, IOException;

    List<ServicePlanEntity> findByServicePlanCodeAndStatus(String servicePlanCode, Long status);
}
