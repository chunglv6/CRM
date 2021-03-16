package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Service_plan_type
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SERVICE_PLAN_TYPE")
public class ServicePlanTypeEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "SERVICE_PLAN_TYPE_SEQ")
    @SequenceGenerator(name = "SERVICE_PLAN_TYPE_SEQ", sequenceName = "SERVICE_PLAN_TYPE_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "SERVICE_PLAN_TYPE_ID")
    Long servicePlanTypeId;

    @Column(name = "NAME")
    String name;

    @Column(name = "DURATION")
    Long duration;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "STATUS")
    Long status;
}
