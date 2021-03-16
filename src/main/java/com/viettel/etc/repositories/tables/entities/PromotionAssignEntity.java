package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PROMOTION_ASSIGN")
public class PromotionAssignEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "PROMOTION_ASSIGN_SEQ")
    @SequenceGenerator(name = "PROMOTION_ASSIGN_SEQ", sequenceName = "PROMOTION_ASSIGN_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "PROMOTION_ASSIGN_ID")
    Long promotionAssignId;

    @Column(name = "PROMOTION_ID")
    Long promotionId;

    @Column(name = "ASSIGN_LEVEL")
    String assignLevel;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "VEHICLE_ID")
    Long vehicleId;

    @Column(name = "PLATE_NUMBER")
    String plateNumber;

    @Column(name = "EPC")
    String EPC;

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

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;
}
