package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActionAuditDetailTempDTO;
import com.viettel.etc.repositories.tables.ActionAuditDetailRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ActionAuditDetailEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ActionAuditDetailServiceImplTest {

    @Mock
    private ActionAuditDetailRepositoryJPA mockActionAuditDetailRepositoryJPA;

    @InjectMocks
    private ActionAuditDetailServiceImpl actionAuditDetailServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testUpdateActionAuditDetail() {
        // Setup
        final List<ActionAuditDetailTempDTO> actionAuditDetailTempDTOList = Arrays.asList(new ActionAuditDetailTempDTO("columnName", "oldValue", "newValue"));

        // Configure ActionAuditDetailRepositoryJPA.saveAll(...).
        final ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(0L);
        actionAuditDetailEntity.setActionAuditId(0L);
        actionAuditDetailEntity.setCreateDate(new Date(0L));
        actionAuditDetailEntity.setTableName("tableName");
        actionAuditDetailEntity.setPkId(0L);
        actionAuditDetailEntity.setColunmName("colunmName");
        actionAuditDetailEntity.setOldValue("oldValue");
        actionAuditDetailEntity.setNewValue("newValue");
        final List<ActionAuditDetailEntity> actionAuditDetailEntities = Arrays.asList(actionAuditDetailEntity);
        when(mockActionAuditDetailRepositoryJPA.saveAll(Arrays.asList(new ActionAuditDetailEntity()))).thenReturn(actionAuditDetailEntities);

        // Run the test
        actionAuditDetailServiceImplUnderTest.updateActionAuditDetail(0L, 0L, actionAuditDetailTempDTOList, "tableName");

        // Verify the results
    }

    @Test
    void testUpdateActionAuditDetail_ActionAuditDetailRepositoryJPAReturnsNull() {
        // Setup
        final List<ActionAuditDetailTempDTO> actionAuditDetailTempDTOList = Arrays.asList(new ActionAuditDetailTempDTO("columnName", "oldValue", "newValue"));
        when(mockActionAuditDetailRepositoryJPA.saveAll(Arrays.asList(new ActionAuditDetailEntity()))).thenReturn(null);

        // Run the test
        actionAuditDetailServiceImplUnderTest.updateActionAuditDetail(0L, 0L, actionAuditDetailTempDTOList, "tableName");

        // Verify the results
    }
}
