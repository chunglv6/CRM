package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.repositories.tables.ContractRepositoryJPA;
import com.viettel.etc.repositories.tables.SaleTransRepositoryJPA;
import com.viettel.etc.repositories.tables.VehicleRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransDetailEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransEntity;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.services.OCSService;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.dto.ocs.OCSResponse;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class ChargeTicketTest {
    @InjectMocks
    TicketServiceImpl ticketService;
    @Mock
    ContractRepositoryJPA contractRepositoryJPA;
    @Mock
    TicketRepository ticketRepository;
    @Mock
    private OCSService ocsService;
    @Mock
    private VehicleRepositoryJPA vehicleRepositoryJPA;

    @Mock
    SaleTransRepositoryJPA saleTransRepositoryJPA;

    AddSupOfferRequestDTO addSupOfferRequestDTO;
    ContractEntity contractEntity;
    ServicePlanDTO servicePlanDTO;
    VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO;
    List<VehicleAddSuffOfferDTO> listVehicle;
    Long contractId = 1000l;
    Long custId = 1000l;
    static ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO;
    SaleTransEntity saleTransEntity;
    List<SupOfferDTOSuccesDTO> listSuccess;


    @BeforeAll
    static void beforeClassMethod() {
        servicePlanVehicleDuplicateDTO = new ServicePlanVehicleDuplicateDTO();
        servicePlanVehicleDuplicateDTO.setPlateNumber("19N109055");
    }

    @BeforeEach
    public void setUp() {
        servicePlanDTO = new ServicePlanDTO();
        listVehicle = new ArrayList<>();
        vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO();
        vehicleAddSuffOfferDTO.setEffDate(new Date());
        vehicleAddSuffOfferDTO.setExpDate(new Date());
        vehicleAddSuffOfferDTO.setPrice(1000l);
        vehicleAddSuffOfferDTO.setQuantity(1l);
        listVehicle.add(vehicleAddSuffOfferDTO);
        saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(1000l);

        addSupOfferRequestDTO = new AddSupOfferRequestDTO();
        addSupOfferRequestDTO.setAmount(null);
        addSupOfferRequestDTO.setActTypeId(24l);
        addSupOfferRequestDTO.setAcountETC(true);

        addSupOfferRequestDTO.setList(listVehicle);

    }


    @Test
    public void chargeTicketTest1() {
        contractEntity = new ContractEntity();
        contractEntity.setShopId(1000l);
        given(contractRepositoryJPA.findByContractId(contractId)).willReturn(contractEntity);
        servicePlanVehicleDuplicateDTO.setPlateNumber("19N109055");
        ReflectionTestUtils.setField(ticketService, "ticketRepository", ticketRepository);
        given(ticketRepository.checkExistsTicket(servicePlanDTO, false)).willReturn(servicePlanVehicleDuplicateDTO);
    }



    public void chargeTicketTest3() {
        chargeTicketTest1();
        servicePlanDTO.setPlateNumber("19N109050000");
        servicePlanDTO.setOcsCode("1000");
        List<ServicePlanDTO> list = new ArrayList<>();
        list.add(servicePlanDTO);
        given(ticketRepository.getFee(servicePlanDTO)).willReturn(list);
        ReflectionTestUtils.setField(ticketService, "ticketRepository", ticketRepository);
        given(ticketRepository.checkExistsTicket(servicePlanDTO, false)).willReturn(servicePlanVehicleDuplicateDTO);

        Long totalCharge = 100000l;
        new MockUp<TicketServiceImpl>() {
            @mockit.Mock
            public long getTotalCharge(List<ServicePlanDTO> list) {
                return totalCharge;
            }
        };
        new MockUp<TicketServiceImpl>() {
            @mockit.Mock
            public ServicePlanDTO toServicePlanDTO(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO) {
                return servicePlanDTO;
            }
        };
    }
}
