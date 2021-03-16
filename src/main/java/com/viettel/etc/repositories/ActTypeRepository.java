package com.viettel.etc.repositories;

import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import org.springframework.security.core.Authentication;

/**
 * Autogen class Repository Interface: Loai tac dong
 *
 * @author toolGen
 * @date Fri Sep 04 09:41:44 ICT 2020
 */
public interface ActTypeRepository {

    ResultSelectEntity find(ActTypeDTO itemParamsEntity);

    Object insert(ActTypeDTO params, Authentication authentication) throws EtcException, Exception;

    Object update(ActTypeDTO param, Authentication authentication) throws EtcException, Exception;

    ResultSelectEntity findOne(Long actTypeId);
}
