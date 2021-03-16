package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActionAuditDetailDTO;
import com.viettel.etc.repositories.tables.ActReasonRepositoryJPA;
import com.viettel.etc.repositories.tables.ActionAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ActReasonEntity;
import com.viettel.etc.repositories.tables.entities.ActionAuditDetailEntity;
import com.viettel.etc.repositories.tables.entities.ActionAuditEntity;
import com.viettel.etc.services.tables.ActionAuditDetailServiceJPA;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.security.core.Authentication;

import javax.persistence.Table;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActionAuditServiceImplTest {

    private ActionAuditServiceImpl actionAuditServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        actionAuditServiceImplUnderTest = new ActionAuditServiceImpl();
        actionAuditServiceImplUnderTest.actionAuditRepositoryJPA = mock(ActionAuditRepositoryJPA.class);
        actionAuditServiceImplUnderTest.actionAuditDetailServiceJPA = mock(ActionAuditDetailServiceJPA.class);
        actionAuditServiceImplUnderTest.actReasonRepositoryJPA = mock(ActReasonRepositoryJPA.class);
    }

    @Test
    void testUpdateLogToActionAudit() {
        // Setup
        final ActionAuditEntity actionAuditEntity = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        final ActionAuditEntity expectedResult = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);

        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(actionAuditServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(0L)).thenReturn(actReasonEntities);

        // Configure ActionAuditRepositoryJPA.save(...).
        final ActionAuditEntity actionAuditEntity1 = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        when(actionAuditServiceImplUnderTest.actionAuditRepositoryJPA.save(new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(actionAuditEntity1);

        // Run the test
        final ActionAuditEntity result = actionAuditServiceImplUnderTest.updateLogToActionAudit(actionAuditEntity);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testUpdateLogToActionAudit_ActionAuditRepositoryJPAReturnsNull() {
        // Setup
        final ActionAuditEntity actionAuditEntity = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        final ActionAuditEntity expectedResult = null;

        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(actionAuditServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(0L)).thenReturn(actReasonEntities);

        when(actionAuditServiceImplUnderTest.actionAuditRepositoryJPA.save(new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(null);

        // Run the test
        final ActionAuditEntity result = actionAuditServiceImplUnderTest.updateLogToActionAudit(actionAuditEntity);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Ignore
    void testUpdateLogAuditAndAuditDetail() throws Exception {
        // Setup
        final Authentication authentication = null;
        final T oldEntity = null;
        final T newEntity = null;
        final ActionAuditEntity expectedResult = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);

        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(actionAuditServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(1L)).thenReturn(actReasonEntities);

        // Configure ActionAuditRepositoryJPA.save(...).
        final ActionAuditEntity actionAuditEntity = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        when(actionAuditServiceImplUnderTest.actionAuditRepositoryJPA.save(any())).thenReturn(actionAuditEntity);

        // Configure ActionAuditDetailServiceJPA.save(...).
        final ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(0L);
        actionAuditDetailEntity.setActionAuditId(0L);
        actionAuditDetailEntity.setCreateDate(new Date(0L));
        actionAuditDetailEntity.setTableName("tableName");
        actionAuditDetailEntity.setPkId(0L);
        actionAuditDetailEntity.setColunmName("colunmName");
        actionAuditDetailEntity.setOldValue("oldValue");
        actionAuditDetailEntity.setNewValue("newValue");
        when(actionAuditServiceImplUnderTest.actionAuditDetailServiceJPA.save(new ActionAuditDetailEntity())).thenReturn(actionAuditDetailEntity);
        //when(new ActionAuditDetailDTO(null, null).toEntity(0L, "anyString", 0L)).thenReturn(actionAuditDetailEntity);
        // Run the test
        final ActionAuditEntity result = actionAuditServiceImplUnderTest.updateLogAuditAndAuditDetail(authentication, 0L, 0L, 0L, 0L, 0L, oldEntity, newEntity);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testUpdateLogAuditAndAuditDetail_ThrowsException() {
        // Setup
        final Authentication authentication = null;
        final T oldEntity = null;
        final T newEntity = null;

        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(actionAuditServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(0L)).thenReturn(actReasonEntities);

        // Configure ActionAuditRepositoryJPA.save(...).
        final ActionAuditEntity actionAuditEntity = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);
        when(actionAuditServiceImplUnderTest.actionAuditRepositoryJPA.save(new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(actionAuditEntity);

        // Configure ActionAuditDetailServiceJPA.save(...).
        final ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(0L);
        actionAuditDetailEntity.setActionAuditId(0L);
        actionAuditDetailEntity.setCreateDate(new Date(0L));
        actionAuditDetailEntity.setTableName("tableName");
        actionAuditDetailEntity.setPkId(0L);
        actionAuditDetailEntity.setColunmName("colunmName");
        actionAuditDetailEntity.setOldValue("oldValue");
        actionAuditDetailEntity.setNewValue("newValue");
        when(actionAuditServiceImplUnderTest.actionAuditDetailServiceJPA.save(new ActionAuditDetailEntity())).thenReturn(actionAuditDetailEntity);

        // Run the test
        assertThrows(Exception.class, () -> actionAuditServiceImplUnderTest.updateLogAuditAndAuditDetail(authentication, 0L, 0L, 0L, 0L, 0L, oldEntity, newEntity));
    }

    @Ignore
    void testUpdateLogAuditAndAuditDetail_ActionAuditRepositoryJPAReturnsNull() throws Exception {
        // Setup
        final Authentication authentication = null;
        final T oldEntity = null;
        final T newEntity = null;
        final ActionAuditEntity expectedResult = new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L);

        // Configure ActReasonRepositoryJPA.findAllByActTypeId(...).
        final ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(0L);
        actReasonEntity.setActTypeId(0L);
        actReasonEntity.setName("name");
        actReasonEntity.setCode("code");
        actReasonEntity.setCreateUser("createUser");
        actReasonEntity.setCreateDate(new Date(0L));
        actReasonEntity.setUpdateUser("updateUser");
        actReasonEntity.setUpdateDate(new Date(0L));
        actReasonEntity.setDescription("description");
        actReasonEntity.setStatus("status");
        final List<ActReasonEntity> actReasonEntities = Arrays.asList(actReasonEntity);
        when(actionAuditServiceImplUnderTest.actReasonRepositoryJPA.findAllByActTypeId(0L)).thenReturn(actReasonEntities);

        when(actionAuditServiceImplUnderTest.actionAuditRepositoryJPA.save(new ActionAuditEntity(0L, 0L, 0L, 0L, 0L, 0L, new Date(0L), "actionUserFullName", "actionUserName", "appId", "ipPc", "description", 0L))).thenReturn(null);

        // Configure ActionAuditDetailServiceJPA.save(...).
        final ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(0L);
        actionAuditDetailEntity.setActionAuditId(0L);
        actionAuditDetailEntity.setCreateDate(new Date(0L));
        actionAuditDetailEntity.setTableName("tableName");
        actionAuditDetailEntity.setPkId(0L);
        actionAuditDetailEntity.setColunmName("colunmName");
        actionAuditDetailEntity.setOldValue("oldValue");
        actionAuditDetailEntity.setNewValue("newValue");
        when(actionAuditServiceImplUnderTest.actionAuditDetailServiceJPA.save(new ActionAuditDetailEntity())).thenReturn(actionAuditDetailEntity);
        when(new ActionAuditDetailDTO(any(), any()).toEntity(anyLong(), any(), any())).thenReturn(actionAuditDetailEntity);
        // Run the test
        final ActionAuditEntity result = actionAuditServiceImplUnderTest.updateLogAuditAndAuditDetail(authentication, 0L, 0L, 0L, 0L, 0L, oldEntity, newEntity);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
