package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.ResCancelResultDTO;
import com.viettel.etc.dto.boo.ResCancelTicketDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.RemoveSupOfferRoaming;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.ActionAuditService;
import com.viettel.etc.services.Boo1Service;
import com.viettel.etc.services.OCSService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class TicketServiceImplTest {
    @InjectMocks
    TicketServiceImpl ticketServiceImpl;

    @Mock
    SaleTransRepositoryJPA saleTransRepositoryJPA;

    @Mock
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Mock
    Boo1Service boo1Service;

    @Mock
    OCSService ocsService;

    @Mock
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Mock
    ActionAuditService actionAuditService;

    @Mock
    ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Mock
    SaleTransDelBoo1RepositoryJPA saleTransDelBoo1RepositoryJPA;

    @Mock
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Test
    public void testInsertSaleTrans() throws Exception {
        // param
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setShopId(3L);
        contractEntity.setShopName("shop");
        AddSupOfferRequestDTO addSupOfferRequest = new AddSupOfferRequestDTO();
        Long contractId = 1L;
        Long custId = 2L;
        SaleTransEntity expect = new SaleTransEntity();
        // mock
        given(saleTransRepositoryJPA.save(any())).willReturn(expect);
        // execute
        SaleTransEntity actual = ticketServiceImpl.insertSaleTrans(contractEntity, addSupOfferRequest, null, contractId, custId);
        assertEquals(expect, actual);
    }

    @Ignore
    public void testDestroyTicketNotRefundCaseBoo1() throws Exception {
        // param
        Long saleTransDetailId = 1L;
        SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
        String expect = "SUCCESS";
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransId(3L);
        saleTransDetailEntity.setBooFlow(2L);
        saleTransDetailEntity.setServicePlanTypeId(4L);
        saleTransDetailEntity.setSubscriptionTicketId(5L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        ResCancelTicketDTO response = new ResCancelTicketDTO();
        response.setStatus("SUCCESS");
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        RemoveSupOfferRoaming removeSupOfferRoaming = new RemoveSupOfferRoaming();
        removeSupOfferRoaming.setSubscriptionTicketId(saleTransDetailEntity.getSubscriptionTicketId().toString());
        removeSupOfferRoaming.setIsRefund(Constants.BOT_REFUND.NO.toString());
        removeSupOfferRoaming.setActTypeId(Constants.ACT_TYPE.DESTROY_TICKET);
        // mock
        given(saleTransDetailRepositoryJPA.findById(saleTransDetailId)).willReturn(Optional.of(saleTransDetailEntity));
        given(saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId())).willReturn(Optional.of(saleTransEntity));
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String convertStationType(Long stationType) {
                return "user";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String convertToTicketType(String servicePlanTypeId) {
                return "user";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String convertDateToStringOther(Date date, String format) {
                return "user";
            }
        };
        given(boo1Service.destroyTicket(any())).willReturn(response);
        ReflectionTestUtils.setField(ticketServiceImpl,"boo1Service",boo1Service);
        given(ocsService.removeSupOfferRoaming(removeSupOfferRoaming, null)).willReturn(ocsResponse);
        // execute
        Object actual = ticketServiceImpl.destroyTicketNotRefund(saleTransDetailId, saleTransDetailDTO, null);
        assertEquals(expect, actual);
    }

    @Test
    public void testDestroyTicketNotRefundCaseBoo2() throws Exception {
        // param
        Long actTypeId = 1L;
        Long customerId = 2L;
        Long actReasonId = 3L;
        Long saleTransDetailId = 1L;
        SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
        saleTransDetailDTO.setActTypeId(actTypeId);
        String expect = "SUCCESS";
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransId(3L);
        saleTransDetailEntity.setBooFlow(1L);
        saleTransDetailEntity.setServicePlanTypeId(4L);
        saleTransDetailEntity.setSubscriptionTicketId(5L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        ResCancelTicketDTO response = new ResCancelTicketDTO();
        response.setStatus("SUCCESS");
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        RemoveSupOfferRoaming removeSupOfferRoaming = new RemoveSupOfferRoaming();
        removeSupOfferRoaming.setSubscriptionTicketId(saleTransDetailEntity.getSubscriptionTicketId().toString());
        removeSupOfferRoaming.setIsRefund(Constants.BOT_REFUND.NO.toString());
        removeSupOfferRoaming.setActTypeId(Constants.ACT_TYPE.DESTROY_TICKET);
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        // mock
        given(saleTransDetailRepositoryJPA.findById(saleTransDetailId)).willReturn(Optional.of(saleTransDetailEntity));
        given(saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId())).willReturn(Optional.of(saleTransEntity));
        ReflectionTestUtils.setField(ticketServiceImpl,"actReasonRepositoryJPA",actReasonRepositoryJPA);
        given(actReasonRepositoryJPA.findAllByActTypeId(saleTransDetailDTO.getActTypeId())).willReturn(list);
        given(ocsService.deleteSupOffer(null, saleTransDetailDTO.getActTypeId(), saleTransEntity.getContractId(), saleTransDetailEntity.getEpc(), saleTransDetailEntity.getOcsCode(), saleTransDetailEntity.getOfferLevel())).willReturn(ocsResponse);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        ReflectionTestUtils.setField(ticketServiceImpl, "actionAuditService", actionAuditService);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(ticketServiceImpl, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        // execute
        Object actual = ticketServiceImpl.destroyTicketNotRefund(saleTransDetailId, saleTransDetailDTO, null);
        assertEquals(expect, actual);
    }

    @Test
    public void testConfirmDestroyTicketCaseBoo2() throws Exception {
        // param
        Long saleTransDetailId = 1L;
        SaleTransDelBoo1DTO saleTransDelBoo1DTO = new SaleTransDelBoo1DTO();
        saleTransDelBoo1DTO.setStationInId(1L);
        saleTransDelBoo1DTO.setStationOutId(2L);
        saleTransDelBoo1DTO.setStationType(3L);
        saleTransDelBoo1DTO.setStageId(4L);
        saleTransDelBoo1DTO.setStationId(5L);
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setBooCode("BOO2");
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        String expect = "Success";
        // mock
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        given(saleTransDetailRepositoryJPA.findById(saleTransDetailId)).willReturn(Optional.of(saleTransDetailEntity));
        given(saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId())).willReturn(Optional.of(saleTransEntity));
        ReflectionTestUtils.setField(ticketServiceImpl, "saleTransDelBoo1RepositoryJPA", saleTransDelBoo1RepositoryJPA);
        // execute
        Object actual = ticketServiceImpl.confirmDestroyTicket(saleTransDetailId, saleTransDelBoo1DTO, null);
        assertEquals(expect, actual);
    }
    @Test
    public void testConfirmDestroyTicketCaseBoo1() throws Exception {
        // param
        Long saleTransDetailId = 1L;
        SaleTransDelBoo1DTO saleTransDelBoo1DTO = new SaleTransDelBoo1DTO();
        saleTransDelBoo1DTO.setStationInId(1L);
        saleTransDelBoo1DTO.setStationOutId(2L);
        saleTransDelBoo1DTO.setStationType(3L);
        saleTransDelBoo1DTO.setStageId(4L);
        saleTransDelBoo1DTO.setStationId(5L);
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setBooCode("BOO1");
        saleTransDetailEntity.setStageId(4L);
        saleTransDetailEntity.setStationId(5L);
        saleTransDetailEntity.setServicePlanTypeId(6L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        ResCancelTicketDTO response = new ResCancelTicketDTO();
        response.setStatus("RECEIVED");
        response.setRequest_type(1L);
        String expect = "Success";
        // mock
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        given(saleTransDetailRepositoryJPA.findById(saleTransDetailId)).willReturn(Optional.of(saleTransDetailEntity));
        given(saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId())).willReturn(Optional.of(saleTransEntity));
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String convertStationType(Long stationType) {
                return "user";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String convertToTicketType(String servicePlanTypeId) {
                return "user";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String convertDateToStringOther(Date date, String format) {
                return "user";
            }
        };
        given(boo1Service.destroyTicket(any())).willReturn(response);
        ReflectionTestUtils.setField(ticketServiceImpl,"boo1Service",boo1Service);
        ReflectionTestUtils.setField(ticketServiceImpl, "saleTransDelBoo1RepositoryJPA", saleTransDelBoo1RepositoryJPA);
        // execute
        Object actual = ticketServiceImpl.confirmDestroyTicket(saleTransDetailId, saleTransDelBoo1DTO, null);
        assertEquals(expect, actual);
    }

    @Test
    public void testDestroyTicketRefundCaseBoo2() throws Exception {
        // param
        Long subscriptionTicketId = 1L;
        SaleTransDelBoo1DTO saleTransDelBoo1DTO = new SaleTransDelBoo1DTO();
        saleTransDelBoo1DTO.setActTypeId(2L);
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setBooFlow(1L);
        saleTransDetailEntity.setVehicleId(2L);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(2L);
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, 1L, 2L, null, null, null);
        String expect = "SUCCESS";
        // mock
        ReflectionTestUtils.setField(ticketServiceImpl,"saleTransDelBoo1RepositoryJPA",saleTransDelBoo1RepositoryJPA);
        given(saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId)).willReturn(saleTransDelBoo1Entity);
        given(saleTransDetailRepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId)).willReturn(saleTransDetailEntity);
        given(vehicleRepositoryJPA.findById(saleTransDetailEntity.getVehicleId())).willReturn(Optional.of(vehicleEntity));
        ReflectionTestUtils.setField(ticketServiceImpl,"actReasonRepositoryJPA",actReasonRepositoryJPA);
        given(actReasonRepositoryJPA.findAllByActTypeId(saleTransDelBoo1DTO.getActTypeId())).willReturn(list);
        given(ocsService.deleteSupOffer(null, saleTransDelBoo1DTO.getActTypeId(), saleTransDelBoo1Entity.getContractId(), saleTransDetailEntity.getEpc(), saleTransDetailEntity.getOcsCode(), saleTransDetailEntity.getOfferLevel())).willReturn(ocsResponse);
        ReflectionTestUtils.setField(ticketServiceImpl, "actionAuditService", actionAuditService);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        ReflectionTestUtils.setField(ticketServiceImpl, "actionAuditDetailRepositoryJPA", actionAuditDetailRepositoryJPA);
        // execute
        Object actual = ticketServiceImpl.destroyTicketRefund(subscriptionTicketId, saleTransDelBoo1DTO, null);
        assertEquals(expect, actual);
    }

    @Ignore
    public void testDestroyTicketRefundCaseBoo1() throws Exception {
        // param
        Long subscriptionTicketId = 1L;
        SaleTransDelBoo1DTO saleTransDelBoo1DTO = new SaleTransDelBoo1DTO();
        saleTransDelBoo1DTO.setActTypeId(2L);
        saleTransDelBoo1DTO.setBotStatus(1L);
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setBooFlow(3L);
        saleTransDetailEntity.setVehicleId(2L);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(2L);
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ResCancelResultDTO response = new ResCancelResultDTO();
        response.setProcess_datetime(10L);
        String expect = "SUCCESS";
        // mock
        ReflectionTestUtils.setField(ticketServiceImpl,"saleTransDelBoo1RepositoryJPA",saleTransDelBoo1RepositoryJPA);
        given(saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId)).willReturn(saleTransDelBoo1Entity);
        given(saleTransDetailRepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId)).willReturn(saleTransDetailEntity);
        given(vehicleRepositoryJPA.findById(saleTransDetailEntity.getVehicleId())).willReturn(Optional.of(vehicleEntity));
        given(boo1Service.cancelResult(any())).willReturn(response);
        ReflectionTestUtils.setField(ticketServiceImpl,"boo1Service",boo1Service);
        // execute
        Object actual = ticketServiceImpl.destroyTicketRefund(subscriptionTicketId, saleTransDelBoo1DTO, null);
        assertEquals(expect, actual);
    }

    @Ignore
    public void testDestroyTicketRefundCaseBoo1Fail() throws Exception {
        // param
        Long subscriptionTicketId = 1L;
        SaleTransDelBoo1DTO saleTransDelBoo1DTO = new SaleTransDelBoo1DTO();
        saleTransDelBoo1DTO.setActTypeId(2L);
        saleTransDelBoo1DTO.setBotStatus(0L);
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setBooFlow(3L);
        saleTransDetailEntity.setVehicleId(2L);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(2L);
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ResCancelResultDTO response = new ResCancelResultDTO();
        response.setProcess_datetime(10L);
        String expect = "SUCCESS";
        // mock
        ReflectionTestUtils.setField(ticketServiceImpl,"saleTransDelBoo1RepositoryJPA",saleTransDelBoo1RepositoryJPA);
        given(saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId)).willReturn(saleTransDelBoo1Entity);
        given(saleTransDetailRepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId)).willReturn(saleTransDetailEntity);
        given(vehicleRepositoryJPA.findById(saleTransDetailEntity.getVehicleId())).willReturn(Optional.of(vehicleEntity));
        given(boo1Service.cancelResult(any())).willReturn(response);
        ReflectionTestUtils.setField(ticketServiceImpl,"boo1Service",boo1Service);
        // execute
        Object actual = ticketServiceImpl.destroyTicketRefund(subscriptionTicketId, saleTransDelBoo1DTO, null);
        assertEquals(expect, actual);
    }

    @Test
    public void testCancelAutoRenew() throws Exception {
        // param
        Long saleTransDetailId = 1L;
        SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setAutoRenew("1");
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setContractId(2L);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ChargeSupOfferDTO chargeSupOfferDTO = new ChargeSupOfferDTO();
        chargeSupOfferDTO.setContractId("2");
        chargeSupOfferDTO.setIsRecurring("0");
        String expect = "SUCCESS";
        // mock
        given(saleTransDetailRepositoryJPA.findById(saleTransDetailId)).willReturn(Optional.of(saleTransDetailEntity));
        given(saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId())).willReturn(Optional.of(saleTransEntity));
        given(ocsService.changeSupOfferTicket(chargeSupOfferDTO, null)).willReturn(ocsResponse);
        // execute
        Object actual = ticketServiceImpl.cancelAutoRenew(saleTransDetailId, saleTransDetailDTO, null);
        assertEquals(expect, actual);
    }

    @Test
    public void testRegisterAutoRenew() throws Exception {
        // param
        Long saleTransDetailId = 1L;
        SaleTransDetailDTO saleTransDetailDTO = new SaleTransDetailDTO();
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setAutoRenew("0");
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setContractId(2L);
        OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.setResultCode("0");
        ChargeSupOfferDTO chargeSupOfferDTO = new ChargeSupOfferDTO();
        chargeSupOfferDTO.setContractId("2");
        chargeSupOfferDTO.setIsRecurring("1");
        String expect = "SUCCESS";
        // mock
        given(saleTransDetailRepositoryJPA.findById(saleTransDetailId)).willReturn(Optional.of(saleTransDetailEntity));
        given(saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId())).willReturn(Optional.of(saleTransEntity));
        given(ocsService.changeSupOfferTicket(chargeSupOfferDTO, null)).willReturn(ocsResponse);
        // execute
        Object actual = ticketServiceImpl.registerAutoRenew(saleTransDetailId, saleTransDetailDTO, null);
        assertEquals(expect, actual);
    }
}

