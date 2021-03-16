package com.viettel.etc.repositories.tables.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LUCKY_CODE")
public class LuckyCodeEntity {
    @Id
    @GeneratedValue(generator = "LUCKY_CODE_SEQ")
    @SequenceGenerator(name = "LUCKY_CODE_SEQ", sequenceName = "LUCKY_CODE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    Long id;

    @Column(name = "LUCKY_CODE")
    String luckyCode;

    @Column(name = "CUSTOMER_ID")
    Long customerId;

    @Column(name = "PHONE_NUMBER")
    String phoneNumber;

    @Column(name = "LUCKY_TYPE")
    Integer luckyType;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "CREATE_USER")
    String createUser;
}
