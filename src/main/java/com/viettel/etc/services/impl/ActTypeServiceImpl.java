package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.repositories.ActTypeRepository;
import com.viettel.etc.services.ActTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Autogen class: Loai tac dong
 *
 * @author ToolGen
 * @date Fri Sep 04 09:41:44 ICT 2020
 */
@Service
public class ActTypeServiceImpl implements ActTypeService {

    @Autowired
    private ActTypeRepository actTypeRepository;

    /**
     * Lay danh sach loai tac dong
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object find(ActTypeDTO itemParamsEntity) {
        return actTypeRepository.find(itemParamsEntity);
    }

    /**
     * Them thong tin tac dong
     * @param params
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    public Object insert(ActTypeDTO params, Authentication authentication) throws Exception {
        return actTypeRepository.insert(params, authentication);
    }

    /**
     * Cap nhat thong tin tac dong
     * @param params
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    public Object update(ActTypeDTO params, Authentication authentication) throws Exception {
        return actTypeRepository.update(params, authentication);
    }

    /**
     * Tim kiem thong tin tac dong
     * @param actTypeId
     * @return
     */
    @Override
    public Object findOne(Long actTypeId) {
        return actTypeRepository.findOne(actTypeId);
    }
}
