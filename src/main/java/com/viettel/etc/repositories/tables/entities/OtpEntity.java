package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "OTP")
@IdClass(OtpIdentify.class)
public class OtpEntity implements Serializable {
    @Id
    String phone;

    @Id
    Integer confirmType;

    String otp;

    @CreationTimestamp
    Date signDate;

    Integer duration;
}
