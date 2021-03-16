package com.viettel.etc.controllers;

import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.services.ActTypeService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Autogen class: Loai tac dong
 *
 * @author ToolGen
 * @date Fri Sep 04 09:41:42 ICT 2020
 */
@RestController
@RequestMapping(Constants.REQUEST_MAPPING_V1 + "/act-type")
public class ActTypeController {

    @Autowired
    private ActTypeService actTypeService;

    /**
     * Lay danh sach loai tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<Object> find(@AuthenticationPrincipal Authentication authentication, ActTypeDTO dataParams) {
        Object resultObj = actTypeService.find(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Them thong tin tac dong
     *
     * @param authentication
     * @param dataParams
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insert(@AuthenticationPrincipal Authentication authentication,
                                         @RequestBody @Valid ActTypeDTO dataParams) throws Exception {
        Object resultObj = actTypeService.insert(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Cap nhat loai thong tin tac dong
     *
     * @param authentication
     * @param dataParams
     * @param actTypeId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{actTypeId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@AuthenticationPrincipal Authentication authentication,
                                         @RequestBody @Valid ActTypeDTO dataParams,
                                         @PathVariable(value = "actTypeId") Long actTypeId) throws Exception {
        dataParams.setActTypeId(actTypeId);
        Object resultObj = actTypeService.update(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Tim kiem loai thong tin tac dong
     *
     * @param actTypeId
     * @return
     */
    @RequestMapping(value = "/{actTypeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findOne(@PathVariable(value = "actTypeId") Long actTypeId) {
        Object resultObj = actTypeService.findOne(actTypeId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
