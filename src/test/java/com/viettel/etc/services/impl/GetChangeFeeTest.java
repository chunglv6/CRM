package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.repositories.tables.ServiceFeeRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ServiceFeeEntity;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class GetChangeFeeTest {
    @Mock
    ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @InjectMocks
    ServicePlanServiceImpl servicePlanService;

    @Test
    public void getChangeFeeTest(){
        ServiceFeeDTO dataParams = new ServiceFeeDTO();
        dataParams.setActionTypeId(1L);
        ServiceFeeEntity dataResult = new ServiceFeeEntity();
        dataResult.setActTypeId(1L);
        dataResult.setServiceFeeId(1L);
        dataResult.setFee(1L);
        List<ServiceFeeEntity> serviceFeeList = new ArrayList<>();
        serviceFeeList.add(dataResult);
        given(serviceFeeRepositoryJPA.getByActTypeId(dataParams.getActionTypeId())).willReturn(serviceFeeList);
        ServiceFeeEntity dataResult1 = serviceFeeRepositoryJPA.getByActTypeId(dataParams.getActionTypeId()).get(0);
        ServiceFeeDTO serviceFeeDTO = new ServiceFeeDTO().toResponse(dataResult1);

        new MockUp<ServiceFeeDTO>(){
          @mockit.Mock
          public ServiceFeeDTO toResponse(ServiceFeeEntity entity){
              return serviceFeeDTO;
          }
        };

        servicePlanService.getChangeFee(dataParams);
        assertNotNull(dataResult1);
        assertNotNull(serviceFeeDTO);
    };
}
