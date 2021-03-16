
package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Action_audit_detail
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ACTION_AUDIT_DETAIL")
public class ActionAuditDetailEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "ACTION_AUDIT_DETAIL_SEQ")
    @SequenceGenerator(name = "ACTION_AUDIT_DETAIL_SEQ", sequenceName = "ACTION_AUDIT_DETAIL_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "ACTION_AUDIT_DETAIL_ID")
    Long actionAuditDetailId;

    @Column(name = "ACTION_AUDIT_ID")
    Long actionAuditId;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "TABLE_NAME")
    String tableName;

    @Column(name = "PK_ID")
    Long pkId;

    @Column(name = "COLUNM_NAME")
    String colunmName;

    @Column(name = "OLD_VALUE")
    String oldValue;

    @Column(name = "NEW_VALUE")
    String newValue;
}
