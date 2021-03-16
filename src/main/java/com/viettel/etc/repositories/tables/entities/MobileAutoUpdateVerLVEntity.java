package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Table(name = "MOBILE_AUTO_UPDATE_VER_LV")
@Entity
public class MobileAutoUpdateVerLVEntity {

    @Id
    @Column(name = "IOS_AUTO_UPDATE_VER_LV_ID")
    private int iosAutoUpdateVerLVId;

    @Column(name = "IOS_AUTO_UPDATE_VER_ID")
    private int iosAutoUpdateVerId;

    @Column(name = "IOS_AUTO_UPDATE_VER_LANG_ID")
    private int iosAutoUpdateVerLangId;

    @Column(name = "DESCRIPTION")
    private String description;
}
