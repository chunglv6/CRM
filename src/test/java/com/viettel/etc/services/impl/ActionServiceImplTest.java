package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActionAuditHistoryDTO;
import com.viettel.etc.dto.ActionAuditHistoryDetailDTO;
import com.viettel.etc.dto.ActionDTO;
import com.viettel.etc.dto.AuditHistoryDTO;
import com.viettel.etc.repositories.ActionRepository;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class ActionServiceImplTest {

    @InjectMocks
    ActionServiceImpl actionServiceImpl;

    @Mock
    ActionRepository actionRepository;

    @Mock
    ContractServiceJPA contractServiceJPA;

    @Test
    public void testGetActionType() throws Exception {
        // param
        ActionDTO itemParamsEntity = new ActionDTO();
        itemParamsEntity.setActTypeId(3L);
        List<ActionDTO> actionDTOList = new ArrayList<>();
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setActTypeId(3L);
        actionDTOList.add(actionDTO);
        // mock
        given(actionRepository.getActionType(itemParamsEntity)).willReturn(actionDTOList);
        // execute
        assertEquals(actionDTOList, actionServiceImpl.getActionType(itemParamsEntity));
    }

    @Test
    public void testGetActionHistory() throws Exception {
        // param
        ActionAuditHistoryDTO itemParamsEntity = new ActionAuditHistoryDTO();
        itemParamsEntity.setActTypeId(3L);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setCount(10);
        ActionAuditHistoryDTO actionAuditHistoryDTO = new ActionAuditHistoryDTO();
        // mock
        given(actionRepository.getActionHistory(itemParamsEntity)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, actionServiceImpl.getActionHistory(itemParamsEntity));
    }

    @Test
    public void testGetActionReason() throws Exception {
        // param
        ActionDTO itemParamsEntity = new ActionDTO();
        itemParamsEntity.setActTypeId(3L);
        Long id = 1L;
        List<ActionDTO> actionDTOList = new ArrayList<>();
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setActTypeId(3L);
        actionDTOList.add(actionDTO);
        // mock
        given(actionRepository.getActionReason(itemParamsEntity, id)).willReturn(actionDTOList);
        // execute
        assertEquals(actionDTOList, actionServiceImpl.getActionReason(itemParamsEntity, id));
    }

    @Test
    public void testGetActionHistoryDetail() throws Exception {
        // param
        ActionAuditHistoryDetailDTO itemParamsEntity = new ActionAuditHistoryDetailDTO();
        itemParamsEntity.setTableName("table");
        Long id = 1L;
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setCount(10);
        ActionAuditHistoryDTO actionAuditHistoryDTO = new ActionAuditHistoryDTO();
        // mock
        given(actionRepository.getActionHistoryDetail(itemParamsEntity, id)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, actionServiceImpl.getActionHistoryDetail(itemParamsEntity, id));
    }

    @Test
    public void testFindActionCustomerHistory() throws Exception {
        // param
        AuditHistoryDTO dataParams = new AuditHistoryDTO();
        dataParams.setCustId(1L);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setCount(10);
        // mock
        given(actionRepository.findActionCustomerHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, actionServiceImpl.findActionCustomerHistory(dataParams));
    }

    @Test
    public void testExportActHistory() throws Exception {
        // param
        Long customerId = 1L;
        AuditHistoryDTO dataParams = new AuditHistoryDTO();
        dataParams.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<AuditHistoryDTO> listData = new ArrayList<>();
        AuditHistoryDTO auditHistoryDTO = new AuditHistoryDTO();
        auditHistoryDTO.setCustId(customerId);
        listData.add(auditHistoryDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(actionRepository.findActionCustomerHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertNotNull(actionServiceImpl.exportActHistory(dataParams));
    }

    @Test
    public void testActionCustomerHistory() throws Exception {
        // param
        Long contractId = 2L;
        Long customerId = 1L;
        AuditHistoryDTO dataParams = new AuditHistoryDTO();
        dataParams.setCustId(customerId);
        dataParams.setContractId(contractId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setCount(10);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setCustId(customerId);
        // mock
        given(contractServiceJPA.getOne(contractId)).willReturn(contractEntity);
        given(actionRepository.actionCustomerHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, actionServiceImpl.actionCustomerHistory(dataParams));
    }

    @Test
    public void testExportActionHistory() throws Exception {
        // param
        Long customerId = 1L;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        dataParams.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ActionAuditHistoryDTO> listData = new ArrayList<>();
        ActionAuditHistoryDTO auditHistoryDTO = new ActionAuditHistoryDTO();
        auditHistoryDTO.setCustId(customerId);
        auditHistoryDTO.setActObject("1");
        auditHistoryDTO.setStatus(1L);
        listData.add(auditHistoryDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(actionRepository.getActionHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertNotNull(actionServiceImpl.exportActionHistory(dataParams));
    }
    @Test
    public void testExportActionHistoryCase2() throws Exception {
        // param
        Long customerId = 1L;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        dataParams.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ActionAuditHistoryDTO> listData = new ArrayList<>();
        ActionAuditHistoryDTO auditHistoryDTO = new ActionAuditHistoryDTO();
        auditHistoryDTO.setCustId(customerId);
        auditHistoryDTO.setActObject("2");
        auditHistoryDTO.setStatus(1L);
        listData.add(auditHistoryDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(actionRepository.getActionHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertNotNull(actionServiceImpl.exportActionHistory(dataParams));
    }
    @Test
    public void testExportActionHistoryCase3() throws Exception {
        // param
        Long customerId = 1L;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        dataParams.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ActionAuditHistoryDTO> listData = new ArrayList<>();
        ActionAuditHistoryDTO auditHistoryDTO = new ActionAuditHistoryDTO();
        auditHistoryDTO.setCustId(customerId);
        auditHistoryDTO.setActObject("3");
        auditHistoryDTO.setStatus(1L);
        listData.add(auditHistoryDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(actionRepository.getActionHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertNotNull(actionServiceImpl.exportActionHistory(dataParams));
    }
    @Test
    public void testExportActionHistoryCase4() throws Exception {
        // param
        Long customerId = 1L;
        ActionAuditHistoryDTO dataParams = new ActionAuditHistoryDTO();
        dataParams.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<ActionAuditHistoryDTO> listData = new ArrayList<>();
        ActionAuditHistoryDTO auditHistoryDTO = new ActionAuditHistoryDTO();
        auditHistoryDTO.setCustId(customerId);
        auditHistoryDTO.setActObject("4");
        auditHistoryDTO.setStatus(1L);
        listData.add(auditHistoryDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(actionRepository.getActionHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertNotNull(actionServiceImpl.exportActionHistory(dataParams));
    }

    @Test
    public void testExportActionHistoryVehicle() throws Exception {
        // param
        Long customerId = 1L;
        AuditHistoryDTO dataParams = new AuditHistoryDTO();
        dataParams.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<AuditHistoryDTO> listData = new ArrayList<>();
        AuditHistoryDTO auditHistoryDTO = new AuditHistoryDTO();
        auditHistoryDTO.setCustId(customerId);
        listData.add(auditHistoryDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(actionRepository.actionCustomerHistory(dataParams)).willReturn(resultSelectEntity);
        // execute
        assertNotNull(actionServiceImpl.exportActionHistoryVehicle(dataParams));
    }

    @Test
    public void testSearchRfidVehicleHistories() throws Exception {
        // param
        Long vehicleId = 1L;
        Long actTypeId = 2L;
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setActTypeId(actTypeId);
        ResultSelectEntity resultSelectEntity =  new ResultSelectEntity();
        resultSelectEntity.setCount(10);
        // mock
        given(actionRepository.findRfidVehicleHistories(vehicleId, actionDTO)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, actionServiceImpl.searchRfidVehicleHistories(vehicleId, actionDTO));
    }
}
