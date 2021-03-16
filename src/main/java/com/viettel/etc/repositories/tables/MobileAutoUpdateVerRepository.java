package com.viettel.etc.repositories.tables;

import com.viettel.etc.dto.mobile.MobileAutoVerUpdateDTO;
import com.viettel.etc.dto.mobile.ReqDataCheckVersionMobile;
import com.viettel.etc.repositories.tables.entities.MobileAutoUpdateVerEntity;

import java.util.List;

public interface MobileAutoUpdateVerRepository {
    List<MobileAutoUpdateVerEntity> searchVerMobile(MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO);

}
