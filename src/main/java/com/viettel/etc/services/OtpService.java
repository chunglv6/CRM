package com.viettel.etc.services;

import com.viettel.etc.dto.OtpDTO;
import org.springframework.security.core.Authentication;

public interface OtpService {
    Object requestOTP(OtpDTO params, Authentication authentication);

    Object requestOtpContract(OtpDTO params, Authentication authentication);

    Object otpContract(OtpDTO params, Authentication authentication);
}
