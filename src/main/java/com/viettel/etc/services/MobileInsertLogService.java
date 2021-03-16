package com.viettel.etc.services;

import com.viettel.etc.dto.mobile.MobileInsertLogDTO;
import org.springframework.security.core.Authentication;

public interface MobileInsertLogService {

    Object saveMobileLog (MobileInsertLogDTO mobileInsertLogDTO, Authentication authentication);
}
