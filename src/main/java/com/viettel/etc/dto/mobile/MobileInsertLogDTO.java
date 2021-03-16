package com.viettel.etc.dto.mobile;

import lombok.Data;

import java.util.Date;

@Data
public class MobileInsertLogDTO {

    private Long mobileInstallLogId;
    private String versionName;
    private String deviceType;
    private String version;
    private String versionType;
    private String userName;
    private String imac;
    private String installType;
    private Long userId;
}
