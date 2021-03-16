package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Act_type
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ACT_TYPE")
public class ActTypeEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "ACT_TYPE_SEQ")
    @SequenceGenerator(name = "ACT_TYPE_SEQ", sequenceName = "ACT_TYPE_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "ACT_TYPE_ID")
    Long actTypeId;

    @Column(name = "CODE")
    String code;

    @Column(name = "NAME")
    String name;

    @Column(name = "IS_OCS")
    String isOcs;

    @Column(name = "ACT_OBJECT")
    String actObject;

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

}
