package com.viettel.etc.services;

import com.viettel.etc.dto.mobile.MobileAutoVerUpdateDTO;
import com.viettel.etc.dto.mobile.ReqDataCheckVersionMobile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MobileAutoUpdateVerService {

    Object createVersionMobile(MultipartFile fileImport, String jsonString, Authentication authentication) throws IOException;

    Object searchVerMobile(MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO, Authentication authentication);

    Object getVersionCurrent(ReqDataCheckVersionMobile reqDataCheckVersionMobile);
}
