package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TOPUP_DAILY")
public class TopupDailyEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "TOPUP_DAILY_SEQ")
    @SequenceGenerator(name = "TOPUP_DAILY_SEQ", sequenceName = "TOPUP_DAILY_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOPUP_DAILY_ID")
    Long topupDailyId;

    @Column(name = "ACCOUNT_USER")
    String accountUser;

    @Column(name = "ACCOUNT_USER_ID")
    String accountUserId;

    @Column(name = "SOURCE_ACCOUNT_USER_ID")
    String sourceAccountUserId;

    @Column(name = "TOPUP_DATE")
    Date topupDate;

    @Column(name = "BALANCE_BEFORE")
    Long balanceBefore;

    @Column(name = "TOPUP_AMOUNT")
    Long topupAmount;

    @Column(name = "BALANCE_AFTER")
    Long balanceAfter;

    @Column(name = "STATUS")
    String status;

    @Column(name = "DESCRIPTION")
    String description;

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
}
