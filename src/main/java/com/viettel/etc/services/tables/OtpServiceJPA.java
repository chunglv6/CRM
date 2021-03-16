package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.OtpRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.OtpEntity;
import com.viettel.etc.repositories.tables.entities.OtpIdentify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceJPA {
    @Autowired
    OtpRepositoryJPA otpRepositoryJPA;

    public Boolean existsById(OtpIdentify id) {
        return otpRepositoryJPA.existsById(id);
    }

    public OtpEntity getById(OtpIdentify id) {
        return otpRepositoryJPA.getOne(id);
    }

    public OtpEntity save(OtpEntity otp) {
        return otpRepositoryJPA.save(otp);
    }

    public void delete(OtpEntity otp) {
        otpRepositoryJPA.delete(otp);
    }
}
