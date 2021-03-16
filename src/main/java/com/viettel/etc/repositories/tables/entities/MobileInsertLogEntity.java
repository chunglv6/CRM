package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Data
@NoArgsConstructor
@Table(name="MOBILE_INSTALL_LOG")
@Entity
public class MobileInsertLogEntity {

    @Id
    @GeneratedValue(generator = "MOBILE_INSTALL_LOG_ID_SEQ")
    @SequenceGenerator(name = "MOBILE_INSTALL_LOG_ID_SEQ", sequenceName = "MOBILE_INSTALL_LOG_ID_SEQ", allocationSize = 1)
    @Column(name ="MOBILE_INSTALL_LOG_ID")
    private Long id;

    @Column(name ="APP_CODE")
    private String appCode;

    @Column(name ="INSTALL_TYPE")
    private String installType;

    @Column(name ="IMAC")
    private String imac;

    @Column(name ="USER_NAME")
    private String username;

    @Column(name ="VERSION_NAME")
    private String versionName;

    @Column(name ="DEVICE_TYPE")
    private String deviceType;

    @Column(name ="VERSION")
    private String version;

    @Column(name ="VERSION_TYPE")
    private String versionType;

    @Column(name ="CREATE_DATE")
    private Date createDate;

    @Column(name ="DEVICE_TYPE1")
    private int deviceType1;

    @Column(name ="UPDATE_DATE")
    private Date updateDate;

    @Column(name ="USER_ID")
    private Long userId;
}
