package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Act_reason
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ACT_REASON")
public class ActReasonEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "ACT_REASON_SEQ")
    @SequenceGenerator(name = "ACT_REASON_SEQ", sequenceName = "ACT_REASON_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "ACT_REASON_ID")
    Long actReasonId;

    @Column(name = "ACT_TYPE_ID")
    Long actTypeId;

    @Column(name = "NAME")
    String name;

    @Column(name = "CODE")
    String code;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    String status;

    public enum Status {
        NOT_ACTIVATED("0"),
        ACTIVATED("1");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
