package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Autogen class Entity: Create Entity For Table Name Service_fee
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "SERVICE_FEE")
public class ServiceFeeEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "SERVICE_FEE_SEQ")
    @SequenceGenerator(name = "SERVICE_FEE_SEQ", sequenceName = "SERVICE_FEE_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "SERVICE_FEE_ID")
    Long serviceFeeId;

    @Column(name = "FEE")
    Long fee;

    @Column(name = "ACT_TYPE_ID")
    Long actTypeId;

    @Column(name = "ACT_REASON_ID")
    Long actReasonId;

    @Column(name = "START_DATE")
    Date startDate;

    @Column(name = "END_DATE")
    Date endDate;

    @Column(name = "CREATE_USER")
    String createUser;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    Date createDate;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    Integer status;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "APPROVED_USER")
    String approvedUser;

    @Column(name = "DOC_REFER")
    String docRefer;

    @Column(name = "SERVICE_FEE_CODE")
    String serviceFeeCode;

    @Column(name = "SERVICE_FEE_NAME")
    String serviceFeeName;
}
