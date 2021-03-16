package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.SaleTransDetailRepository;
import com.viettel.etc.repositories.tables.ContractRepositoryJPA;
import com.viettel.etc.repositories.tables.SaleTransDetailRepositoryJPA;
import com.viettel.etc.repositories.tables.ServiceFeeRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.ServiceFeeEntity;
import com.viettel.etc.services.CategoriesService;
import com.viettel.etc.services.StageService;
import com.viettel.etc.services.StationService;
import com.viettel.etc.services.VehicleGroupService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class SaleTransDetailServiceImplTest {
    @InjectMocks
    SaleTransDetailServiceImpl saleTransDetailServiceImpl;

    @Mock
    SaleTransDetailRepository saleTransDetailRepository;

    @Mock
    StageService stageService;

    @Mock
    StationService stationService;

    @Mock
    ContractRepositoryJPA contractRepositoryJPA;

    @Mock
    VehicleGroupService vehicleGroupService;

    @Mock
    CategoriesService categoriesService;

    @Mock
    ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @Mock
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Test
    public void testSearchTicketPurchaseHistories() throws Exception {
        // param
        Long vehicleId = 1L;
        SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<SaleTransDetailDTO> saleTransDetailDTOList = new ArrayList<>();
        SaleTransDetailDTO saleTransDetailDTO1 = new SaleTransDetailDTO();
        saleTransDetailDTO1.setStageId(1L);
        saleTransDetailDTO1.setStationId(2L);
        saleTransDetailDTOList.add(saleTransDetailDTO1);
        resultSelectEntity.setListData(saleTransDetailDTOList);
        // mock
        given(saleTransDetailRepository.findTicketPurchaseHistories(vehicleId, saleTransDetailDTO)).willReturn(resultSelectEntity);
        ReflectionTestUtils.setField(saleTransDetailServiceImpl,"stageService",stageService);
        ReflectionTestUtils.setField(saleTransDetailServiceImpl,"stationService",stationService);
        // execute
        Object actual = saleTransDetailServiceImpl.searchTicketPurchaseHistories(vehicleId, saleTransDetailDTO, null);
        assertNotNull(actual);
    }

    @Ignore
    public void testAddSaleTransDetail() throws Exception {
        // param
        Long saleTransId = 1L;
        Long vehicleId = 2L;
        Long actTypeId = 3L;
        List<ServiceFeeEntity> serviceFeeEntityList = new ArrayList<>();
        ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setActTypeId(actTypeId);
        serviceFeeEntityList.add(serviceFeeEntity);
        // mock
        given(serviceFeeRepositoryJPA.getByActTypeId(actTypeId)).willReturn(serviceFeeEntityList);
        ReflectionTestUtils.setField(saleTransDetailServiceImpl,"saleTransDetailRepositoryJPA",saleTransDetailRepositoryJPA);
        // execute
        saleTransDetailServiceImpl.addSaleTransDetail(null, saleTransId, vehicleId, actTypeId);
    }
}
