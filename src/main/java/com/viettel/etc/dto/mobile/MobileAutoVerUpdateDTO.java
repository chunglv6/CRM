package com.viettel.etc.dto.mobile;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class MobileAutoVerUpdateDTO {

    private String deviceType;
    private String versionName;
    private String fromVersion;
    private String toVersion;
    private String urlUpdate;
    private String description;
    private Long id;
    private int version;
    private Integer status;
    private String appCode;

    /**
     * kieu phienn ban: FULL - duoc xem het, LIMITED - khong duoc xem so lieu
     * kinh doanh
     */
    private String versionType;
    /**
     * danh sach id cua vai tro tai ung voi phien ban
     */
    private String roleIsUsed;

    // cac serial duoc su dung. Blank neu all duoc dung
    private String serialNumber;

    //030216 bo sung them mota tieng anh
    private String descriptionEn;
    //090617 them thong tin chẹck phien bannat buoc update
    private Integer invalid;
}
