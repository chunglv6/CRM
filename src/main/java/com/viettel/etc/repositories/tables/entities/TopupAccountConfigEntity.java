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
@Table(name = "TOPUP_ACCOUNT_CONFIG")
public class TopupAccountConfigEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "TOPUP_ACCOUNT_CONFIG_SEQ")
    @SequenceGenerator(name = "TOPUP_ACCOUNT_CONFIG_SEQ", sequenceName = "TOPUP_ACCOUNT_CONFIG_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOPUP_ACCOUNT_CONFIG_ID")
    Long topupAccountConfigId;

    @Column(name = "ACCOUNT_USER_ID")
    String accountUserId;

    @Column(name = "ACCOUNT_USER")
    String accountUser;

    @Column(name = "ACCOUNT_FULLNAME")
    String accountFullname;

    @Column(name = "PARTY_CODE")
    String partyCode;

    @Column(name = "PARTY_NAME")
    String partyName;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "PHONE_NUMBER")
    String phoneNumber;

    @Column(name = "DENTITY_NUMBER")
    String dentityNumber;

    @Column(name = "AMOUNT")
    Long amount;

    @Column(name = "BALANCE")
    Long balance;

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
