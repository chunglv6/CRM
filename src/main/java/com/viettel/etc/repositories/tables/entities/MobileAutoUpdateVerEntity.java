package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Data
@NoArgsConstructor
@Table(name = "MOBILE_AUTO_UPDATE_VER")
@Entity
public class MobileAutoUpdateVerEntity {
    @Id
    @GeneratedValue(generator = "AUTO_UPDATE_VER_ID_SEQ")
    @SequenceGenerator(name = "AUTO_UPDATE_VER_ID_SEQ", sequenceName = "AUTO_UPDATE_VER_ID_SEQ", allocationSize = 1)
    @Column(name ="AUTO_UPDATE_VER_ID")
    private Long autoUpdateVerId;

    @Column(name ="APP_CODE")
    private String appCode;

    @Column(name ="VERSION_NAME")
    private String versionName;

    @Column(name ="FROM_VERSION")
    private String fromVersion;

    @Column(name ="TO_VERSION")
    private String toVersion;

    @Column(name ="CREATE_DATE")
    private Date createDate;

    @Column(name ="IS_ACTIVED")
    private int isActived;

    @Column(name ="URL")
    private String url;

    @Column(name ="URL_UPDATE")
    private String urlUpdate;

    @Column(name ="DEVICE_TYPE")
    private String deviceType;

    @Column(name ="DESCRIPTION")
    private String description;

    @Column(name ="VERSION")
    private int version;

    @Column(name ="VERSION_TYPE")
    private String versionType;

    @Column(name ="ROLE_IS_USED")
    private String roleIsUsed;

    @Column(name ="SERIAL_NUMBER")
    private String serialNumber;

    @Column(name ="MORE_INFO")
    private String moreInfo;

    @Column(name ="NUMBER_DOWNLOAD")
    private long numberDownload;

    @Column(name ="PUBLISH_BY")
    private String publishBy;

    @Column(name ="INVALID")
    private int invalid;
}
