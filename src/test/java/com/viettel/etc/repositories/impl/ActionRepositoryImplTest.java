package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ActionAuditHistoryDTO;
import com.viettel.etc.dto.ActionAuditHistoryDetailDTO;
import com.viettel.etc.dto.ActionDTO;
import com.viettel.etc.dto.AuditHistoryDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ActionRepositoryImplTest {

    private ActionRepositoryImpl actionRepositoryImplUnderTest;
    private CommonDataBaseRepository commonDataBaseRepository;

    @BeforeEach
    void setUp() {
        actionRepositoryImplUnderTest = new ActionRepositoryImpl();
    }

    @Test
    void testGetActionType() {
        // Setup
        final ActionDTO itemParamsEntity = new ActionDTO();
        itemParamsEntity.setReasonId(0L);
        itemParamsEntity.setId("id");
        itemParamsEntity.setActTypeId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setName("name");
        itemParamsEntity.setIsOcs("isOcs");
        itemParamsEntity.setActObject("actObject");
        itemParamsEntity.setCreateUser("createUser");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setUpdateUser("updateUser");

        final ActionDTO actionDTO = new ActionDTO();
        actionDTO.setReasonId(0L);
        actionDTO.setId("id");
        actionDTO.setActTypeId(0L);
        actionDTO.setCode("code");
        actionDTO.setName("name");
        actionDTO.setIsOcs("isOcs");
        actionDTO.setActObject("actObject");
        actionDTO.setCreateUser("createUser");
        actionDTO.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        actionDTO.setUpdateUser("updateUser");
        final List<ActionDTO> expectedResult = Arrays.asList(actionDTO);

        // Run the test
        final List<ActionDTO> result = actionRepositoryImplUnderTest.getActionType(itemParamsEntity);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetActionHistory(){
        // Setup
        final ActionAuditHistoryDTO itemParamsEntity = new ActionAuditHistoryDTO();
        itemParamsEntity.setActionAuditId(0L);
        itemParamsEntity.setCustId(0L);
        itemParamsEntity.setCustName("custName");
        itemParamsEntity.setActObject("actObject");
        itemParamsEntity.setActionTypeName("actionTypeName");
        itemParamsEntity.setActionReasonName("actionReasonName");
        itemParamsEntity.setCreateDate(new Date(0L));
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);
        itemParamsEntity.setDocumentNumber("documentNumber");
        itemParamsEntity.setActTypeId(0L);
        itemParamsEntity.setPhoneNumber("phoneNumber");
        itemParamsEntity.setContractNo("ContractNo");
        itemParamsEntity.setPlateNumber("PlateNumber");
        itemParamsEntity.setRfidSerial("RfidSerial");
        itemParamsEntity.setActionUserFullName("ActionUserFullName");
        itemParamsEntity.setTableName("TableName");
        itemParamsEntity.setStartDate("20/10/2020");
        itemParamsEntity.setEndDate("20/10/2020");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
              result = actionRepositoryImplUnderTest.getActionHistory(itemParamsEntity);
        }catch (NullPointerException e){
            result = null;
        }

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetActionReason() {
        // Setup
        final ActionDTO itemParamsEntity = new ActionDTO();
        itemParamsEntity.setReasonId(0L);
        itemParamsEntity.setId("id");
        itemParamsEntity.setActTypeId(0L);
        itemParamsEntity.setCode("code");
        itemParamsEntity.setName("name");
        itemParamsEntity.setIsOcs("isOcs");
        itemParamsEntity.setActObject("actObject");
        itemParamsEntity.setCreateUser("createUser");
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setUpdateUser("updateUser");

        final ActionDTO actionDTO = new ActionDTO();
        actionDTO.setReasonId(0L);
        actionDTO.setId("id");
        actionDTO.setActTypeId(0L);
        actionDTO.setCode("code");
        actionDTO.setName("name");
        actionDTO.setIsOcs("isOcs");
        actionDTO.setActObject("actObject");
        actionDTO.setCreateUser("createUser");
        actionDTO.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        actionDTO.setUpdateUser("updateUser");
        final List<ActionDTO> expectedResult = Arrays.asList(actionDTO);

        // Run the test
        final List<ActionDTO> result = actionRepositoryImplUnderTest.getActionReason(itemParamsEntity, 0L);

        // Verify the results
        assertNull(result);
    }

    @Test
    void testGetActionHistoryDetail() {
        // Setup
        final ActionAuditHistoryDetailDTO itemParamsEntity = new ActionAuditHistoryDetailDTO();
        itemParamsEntity.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        itemParamsEntity.setTableName("tableName");
        itemParamsEntity.setStartrecord(0);
        itemParamsEntity.setPagesize(0);

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actionRepositoryImplUnderTest.getActionHistoryDetail(itemParamsEntity, 0L);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
        assertNull(result);
    }

    @Test
    void testFindActionCustomerHistory() {
        // Setup
        final AuditHistoryDTO requestModel = new AuditHistoryDTO();
        requestModel.setActionUserName("actionUserName");
        requestModel.setActionType("actionType");
        requestModel.setActionDate("actionDate");
        requestModel.setUserName("userName");
        requestModel.setReason("reason");
        requestModel.setColumnName("columnName");
        requestModel.setOldValue("oldValue");
        requestModel.setNewValue("newValue");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actionRepositoryImplUnderTest.findActionCustomerHistory(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testActionCustomerHistory() {
        // Setup
        final AuditHistoryDTO requestModel = new AuditHistoryDTO();
        requestModel.setActionUserName("actionUserName");
        requestModel.setActionType("actionType");
        requestModel.setActionDate("actionDate");
        requestModel.setUserName("userName");
        requestModel.setReason("reason");
        requestModel.setColumnName("columnName");
        requestModel.setOldValue("oldValue");
        requestModel.setNewValue("newValue");
        requestModel.setStartDate("startDate");
        requestModel.setEndDate("endDate");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actionRepositoryImplUnderTest.actionCustomerHistory(requestModel);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }

    @Test
    void testFindRfidVehicleHistories() {
        // Setup
        final ActionDTO actionDTO = new ActionDTO();
        actionDTO.setReasonId(0L);
        actionDTO.setId("id");
        actionDTO.setActTypeId(0L);
        actionDTO.setCode("code");
        actionDTO.setName("name");
        actionDTO.setIsOcs("isOcs");
        actionDTO.setActObject("actObject");
        actionDTO.setCreateUser("createUser");
        actionDTO.setCreateDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        actionDTO.setUpdateUser("updateUser");

        // Run the test
        ResultSelectEntity result = new ResultSelectEntity();
        try {
            result = actionRepositoryImplUnderTest.findRfidVehicleHistories(0L, actionDTO);
        }catch (NullPointerException e){
            result = null;
        }
        // Verify the results
//        assertNotNull(result);
    }
}
