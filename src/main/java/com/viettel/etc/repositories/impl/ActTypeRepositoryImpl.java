package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.repositories.ActTypeRepository;
import com.viettel.etc.repositories.tables.entities.ActTypeEntity;
import com.viettel.etc.services.tables.ActTypeServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Autogen class Repository Impl: Loai tac dong
 *
 * @author ToolGen
 * @date Fri Sep 04 09:41:44 ICT 2020
 */
@Repository
public class ActTypeRepositoryImpl extends CommonDataBaseRepository implements ActTypeRepository {

    @Autowired
    private ActTypeServiceJPA actTypeServiceJPA;

    /**
     * Lay danh sach loai tac dong
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity find(ActTypeDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ACT_TYPE_ID as actTypeId , CODE,NAME,IS_OCS as isOcs,ACT_OBJECT as actObject,DESCRIPTION,STATUS,CREATE_DATE as createDate \n")
                .append("from ACT_TYPE  where 1=1 \n");
        HashMap<String, Object> hmapParams = new HashMap<>();
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getCode())) {
            sql.append("and LOWER(CODE) like :code \n");
            hmapParams.put("code", "%" + itemParamsEntity.getCode().toLowerCase().trim() + "%");
        }
        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getName())) {
            sql.append("and LOWER(NAME) like :name \n");
            hmapParams.put("name", "%" + itemParamsEntity.getName().toLowerCase().trim() + "%");
        }

        if (itemParamsEntity.getStatus() != null) {
            sql.append("and STATUS =:status \n");
            hmapParams.put("status", itemParamsEntity.getStatus());
        }

        if (itemParamsEntity.getIsOcs() != null) {
            sql.append("and IS_OCS =:isOcs \n");
            hmapParams.put("isOcs", itemParamsEntity.getIsOcs());
        }
        if (itemParamsEntity.getActObject() != null) {
            sql.append("and ACT_OBJECT =:actObject \n");
            hmapParams.put("actObject", itemParamsEntity.getActObject());
        }
        sql.append("order by NLSSORT(NAME, 'nls_sort = Vietnamese') \n");
        Integer start = null;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, ActTypeDTO.class);
    }

    // thêm mới acttype
    @Override
    public Object insert(ActTypeDTO params, Authentication authentication) {
        List<ActTypeEntity> listCheck = actTypeServiceJPA.findAllByCodeAndStatus(params.getCode(), Constants.ACT_REASON_ACTIVE);
        if (listCheck.size() > Constants.SIZE_LIST_ZERO && params.getStatus().equals(Constants.ACT_REASON_ACTIVE)) {
            throw new EtcException("crm.validate.act.type.exits");
        }
        ModelMapper modelMapper = new ModelMapper();
        ActTypeEntity obj = modelMapper.map(params, ActTypeEntity.class);
        obj.setCreateDate(new Date(System.currentTimeMillis()));
        obj.setCreateUser(FnCommon.getUserLogin(authentication));
        obj.setCode(params.getCode().trim());
        obj.setName(params.getName().trim());
        return actTypeServiceJPA.save(obj);
    }

    // update acttype
    @Override
    public Object update(ActTypeDTO param, Authentication authentication) {
        if (!actTypeServiceJPA.existsById(param.getActTypeId())) {
            throw new EtcException("crm.validate.act.type.not.exits");
        }
        ActTypeEntity obj = actTypeServiceJPA.findById(param.getActTypeId()).get();
        obj.setUpdateDate(new Date(System.currentTimeMillis()));
        obj.setUpdateUser(FnCommon.getUserLogin(authentication));
        obj.setCode(param.getCode().trim());
        obj.setName(param.getName().trim());
        obj.setActObject(param.getActObject());
        obj.setIsOcs(param.getIsOcs());
        obj.setStatus(param.getStatus());
        obj.setDescription(param.getDescription());
        return actTypeServiceJPA.save(obj);
    }

    // lấy 1
    @Override
    public ResultSelectEntity findOne(Long actTypeId) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select ACT_TYPE_ID as actTypeId , CODE,NAME,IS_OCS as isOcs,ACT_OBJECT as actObject,DESCRIPTION,STATUS,CREATE_DATE as createDate from ACT_TYPE ACT_TYPE where ACT_TYPE_ID =:actTypeId");
        hmapParams.put("actTypeId", actTypeId);
        return getListDataAndCount(sql, hmapParams, 0, 1, ActTypeDTO.class);
    }

}
