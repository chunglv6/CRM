package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettel.etc.dto.im.ResStockTransEtagDTO;
import com.viettel.etc.dto.mobile.MobileAutoVerUpdateDTO;
import com.viettel.etc.dto.mobile.ReqDataCheckVersionMobile;
import com.viettel.etc.repositories.impl.MobileAutoUpdateVerRepositoryImpl;
import com.viettel.etc.repositories.tables.MobileAutoUpdateVerJPA;
import com.viettel.etc.repositories.tables.entities.MobileAutoUpdateVerEntity;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.MobileAutoUpdateVerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Optional;

@Service
public class MobileAutoUpdateVerServiceImpl implements MobileAutoUpdateVerService {

    @Autowired
    private MobileAutoUpdateVerJPA mobileAutoUpdateVerJPA;

    @Autowired
    private MobileAutoUpdateVerRepositoryImpl mobileAutoUpdateVerRepository;

    @Autowired
    private FileService fileService;

    /**
     * tao moi ten phien ban cho mobile
     * @param jsonString
     * @param authentication
     * @return
     */
    @Override
    public Object createVersionMobile(MultipartFile fileImport, String jsonString, Authentication authentication) throws IOException {
        MobileAutoUpdateVerEntity mobileAutoUpdateVerEntity;
//        MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO = new Gson().fromJson(jsonString,MobileAutoVerUpdateDTO.class);
        Gson gson = new Gson();
        Type type = new TypeToken<MobileAutoVerUpdateDTO>() {
        }.getType();
        MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO = gson.fromJson(jsonString, type);
        if (mobileAutoVerUpdateDTO.getId() != null ) {
            Optional<MobileAutoUpdateVerEntity> mobileAutoUpdateVerEntity1 = getVersionById(mobileAutoVerUpdateDTO.getId());
            if(mobileAutoUpdateVerEntity1.isPresent()) {
                mobileAutoUpdateVerEntity = mobileAutoUpdateVerEntity1.get();
            }else{
                mobileAutoUpdateVerEntity = new MobileAutoUpdateVerEntity();
            }
        } else {
            mobileAutoUpdateVerEntity = new MobileAutoUpdateVerEntity();
        }
        if (fileImport !=null){
            byte[] byteArr = fileImport.getBytes();
            String filePath = mobileAutoVerUpdateDTO.getVersionName() + "-" + mobileAutoVerUpdateDTO.getUrlUpdate();
            fileService.uploadFile(filePath, byteArr);
        }
        mobileAutoUpdateVerEntity.setAppCode(mobileAutoVerUpdateDTO.getAppCode());
        mobileAutoUpdateVerEntity.setDeviceType(mobileAutoVerUpdateDTO.getDeviceType());
        mobileAutoUpdateVerEntity.setVersionName(mobileAutoVerUpdateDTO.getVersionName());
        mobileAutoUpdateVerEntity.setVersion(mobileAutoVerUpdateDTO.getVersion());
        mobileAutoUpdateVerEntity.setToVersion(mobileAutoVerUpdateDTO.getToVersion());
        mobileAutoUpdateVerEntity.setFromVersion(mobileAutoVerUpdateDTO.getFromVersion());
        mobileAutoUpdateVerEntity.setCreateDate(new Date(System.currentTimeMillis()));
        mobileAutoUpdateVerEntity.setUrlUpdate(mobileAutoVerUpdateDTO.getUrlUpdate());
        mobileAutoUpdateVerEntity.setIsActived(1);
        mobileAutoUpdateVerEntity.setDescription(mobileAutoVerUpdateDTO.getDescription());
        mobileAutoUpdateVerEntity.setRoleIsUsed(mobileAutoVerUpdateDTO.getRoleIsUsed());
        mobileAutoUpdateVerEntity.setVersionType(mobileAutoVerUpdateDTO.getVersionType());
        mobileAutoUpdateVerEntity.setSerialNumber(mobileAutoVerUpdateDTO.getSerialNumber());
        mobileAutoUpdateVerEntity.setInvalid(mobileAutoVerUpdateDTO.getInvalid());
        return mobileAutoUpdateVerJPA.save(mobileAutoUpdateVerEntity);
    }

    /**
     * tim kiem phien ban mobile
     * @param mobileAutoVerUpdateDTO
     * @param authentication
     * @return
     */
    @Override
    public Object searchVerMobile(MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO, Authentication authentication) {
        return mobileAutoUpdateVerRepository.searchVerMobile(mobileAutoVerUpdateDTO);
    }

    /**
     * Lay phien ban moi nhat
     * @param reqDataCheckVersionMobile
     * @return
     */
    @Override
    public Object getVersionCurrent(ReqDataCheckVersionMobile reqDataCheckVersionMobile) {
        return mobileAutoUpdateVerJPA.findFirstByAppCodeAndDeviceTypeOrderByCreateDateDesc(reqDataCheckVersionMobile.getAppCode(), reqDataCheckVersionMobile.getDeviceType());
    }

    /**
     * Lay ra phien ban da ton tai hay khong
     * @param id
     * @return
     */
    private Optional<MobileAutoUpdateVerEntity> getVersionById(Long id) {
        return mobileAutoUpdateVerJPA.findById(id);
    }
}
