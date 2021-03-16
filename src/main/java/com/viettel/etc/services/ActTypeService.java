package com.viettel.etc.services;

import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.security.core.Authentication;

/**
 * Autogen class: Loai tac dong
 *
 * @author ToolGen
 * @date Fri Sep 04 09:41:44 ICT 2020
 */
public interface ActTypeService {
    Object find(ActTypeDTO itemParamsEntity);

    Object insert(ActTypeDTO params, Authentication authentication) throws EtcException, Exception;

    Object update(ActTypeDTO params, Authentication authentication) throws EtcException, Exception;

    Object findOne(Long actTypeId);
}
