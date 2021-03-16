package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Action_audit
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ACTION_AUDIT")
public class ActionAuditEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "ACTION_AUDIT_SEQ")
    @SequenceGenerator(name = "ACTION_AUDIT_SEQ", sequenceName = "ACTION_AUDIT_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "ACTION_AUDIT_ID")
    Long actionAuditId;

    @Column(name = "ACT_REASON_ID")
    Long actReasonId;

    @Column(name = "ACT_TYPE_ID")
    Long actTypeId;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "VEHICLE_ID")
    Long vehicleId;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "ACTION_USER_FULL_NAME")
    String actionUserFullName;

    @Column(name = "ACTION_USER_NAME")
    String actionUserName;

    @Column(name = "APP_ID")
    String appId;

    @Column(name = "IP_PC")
    String ipPc;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    Long status;

    public enum Status {
        NOT_SUCCESS(0L),
        SUCCESS(1L);
        public final Long value;

        Status(Long value) {
            this.value = value;
        }
    }
}
