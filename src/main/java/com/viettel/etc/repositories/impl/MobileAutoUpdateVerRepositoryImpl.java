package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ActionDTO;
import com.viettel.etc.dto.mobile.MobileAutoVerUpdateDTO;
import com.viettel.etc.dto.mobile.ReqDataCheckVersionMobile;
import com.viettel.etc.repositories.tables.MobileAutoUpdateVerRepository;
import com.viettel.etc.repositories.tables.entities.MobileAutoUpdateVerEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class MobileAutoUpdateVerRepositoryImpl extends CommonDataBaseRepository implements MobileAutoUpdateVerRepository {

    /**
     * tim kiem cac phien ban cho mobile
     * @param mobileAutoVerUpdateDTO
     * @return
     */
    @Override
    public List<MobileAutoUpdateVerEntity> searchVerMobile(MobileAutoVerUpdateDTO mobileAutoVerUpdateDTO) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("SELECT mobile.auto_update_ver_id as autoUpdateVerId, mobile.app_code as appCode, mobile.version_name as versionName,");
        sql.append(" mobile.from_version as fromVersion, mobile.to_version as toVersion,  mobile.create_date as createDate, ");
        sql.append(" mobile.is_actived as isActived,  mobile.url as url, mobile.url_update as urlUpdate, mobile.device_type as deviceType,");
        sql.append(" mobile.description as description, mobile.version as version, mobile.version_type as versionType, ");
        sql.append(" mobile.role_is_used as roleIsUsed, ");
        sql.append(" mobile.serial_number as serialNumber, mobile.more_info as moreInfo, mobile.number_download as numberDownload, ");
        sql.append(" mobile.publish_by as publishBy, mobile.invalid as invalid ");
        sql.append(" FROM mobile_auto_update_ver mobile WHERE 1 = 1");

        if (mobileAutoVerUpdateDTO.getInvalid() == 0) {
            sql.append(" AND (mobile.invalid is null or mobile.invalid = 0) ");
        } else if (mobileAutoVerUpdateDTO.getInvalid() == 1) {
            sql.append(" AND mobile.invalid = 1 ");
        }
        if (mobileAutoVerUpdateDTO.getStatus() == 1) {
            // tim kiem da duyet
            sql.append(" AND mobile.is_actived = 1 ");
        } else if (mobileAutoVerUpdateDTO.getStatus() == 2) {
            // tim kiem da duyet
            sql.append(" AND mobile.is_actived = 0 ");
        }
        if (mobileAutoVerUpdateDTO.getDeviceType() != null) {
            //neu khong phai la tat ca
            sql.append(" AND mobile.device_type =:device_type \n ");
            hmapParams.put("device_type", mobileAutoVerUpdateDTO.getDeviceType());
        }
        if (!"".equals(mobileAutoVerUpdateDTO.getVersionName())) {
            sql.append(" AND mobile.version_name =:version_name \n");
            hmapParams.put("version_name", mobileAutoVerUpdateDTO.getVersionName());
        }

        if (mobileAutoVerUpdateDTO.getVersionType() != null && !"".equals(mobileAutoVerUpdateDTO.getVersionType())
                && !"-1".equals(mobileAutoVerUpdateDTO.getVersionType())) {
            sql.append(" AND mobile.version_type =:version_type \n ");
            hmapParams.put("version_type", mobileAutoVerUpdateDTO.getVersionType().trim());
        }

        //order theo ngay tao
        sql.append(" order by mobile.create_date desc ");
        return (List<MobileAutoUpdateVerEntity>) getListData(sql, hmapParams, null, null, MobileAutoUpdateVerEntity.class);
    }
}
