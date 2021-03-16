package com.viettel.etc.services.impl;

import com.viettel.etc.dto.mobile.MobileInsertLogDTO;
import com.viettel.etc.repositories.tables.MobileInsertLogRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.MobileInsertLogEntity;
import com.viettel.etc.services.MobileInsertLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class MobileInsertLogServiceImpl implements MobileInsertLogService {

    @Autowired
    public MobileInsertLogRepositoryJPA mobileInsertLogRepositoryJPA;

    /**
     * Luu log or update log mobile
     * @param mobileInsertLogDTO
     * @return
     */
    @Override
    public Object saveMobileLog(MobileInsertLogDTO mobileInsertLogDTO, Authentication authentication) {
        MobileInsertLogEntity mobileInsertLogEntityOld = checkLog(mobileInsertLogDTO.getImac());
        if (mobileInsertLogEntityOld == null) {
            MobileInsertLogEntity mobileInsertLogEntityNew = new MobileInsertLogEntity();
            mobileInsertLogEntityNew.setImac(mobileInsertLogDTO.getImac());
            mobileInsertLogEntityNew.setInstallType("INSTALL");
            mobileInsertLogEntityNew.setUserId(mobileInsertLogDTO.getUserId());
            mobileInsertLogEntityNew.setCreateDate(new Date(System.currentTimeMillis()));
            mobileInsertLogEntityNew.setVersion(mobileInsertLogDTO.getVersion());
            mobileInsertLogEntityNew.setVersionType(mobileInsertLogDTO.getVersionType());
            mobileInsertLogEntityNew.setDeviceType(mobileInsertLogDTO.getDeviceType());
            return mobileInsertLogRepositoryJPA.save(mobileInsertLogEntityNew);
        } else {
            mobileInsertLogEntityOld.setInstallType("UPDATE");
            mobileInsertLogEntityOld.setUserId(mobileInsertLogDTO.getUserId());
            mobileInsertLogEntityOld.setCreateDate(new Date(System.currentTimeMillis()));
            mobileInsertLogEntityOld.setVersion(mobileInsertLogDTO.getVersion());
            mobileInsertLogEntityOld.setVersionType(mobileInsertLogDTO.getVersionType());
            mobileInsertLogEntityOld.setDeviceType(mobileInsertLogDTO.getDeviceType());
            return mobileInsertLogRepositoryJPA.save(mobileInsertLogEntityOld);
        }
    }

    /**
     * check xem ban ghi log theo imac da ton tai chua
     * @param imac
     * @return
     */
    private MobileInsertLogEntity checkLog(String imac) {
        return mobileInsertLogRepositoryJPA.getByImac(imac);
    }
}
