package com.viettel.etc.controllers;

import com.viettel.etc.dto.*;
import com.viettel.etc.services.ActionService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: Lop ve tac dong
 *
 * @author ToolGen
 * @date Mon Jun 29 11:19:51 ICT 2020
 */
@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class ActionController {

    @Autowired
    private ActionService actionService;

    /**
     * lay danh sach loai tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/action-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getActionType(@AuthenticationPrincipal Authentication authentication, ActionDTO dataParams) {
        Object resultObj = actionService.getActionType(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(authentication, resultObj), HttpStatus.OK);
    }

    /**
     * lay danh sach lich su tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/action-histories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getActionHistory(@AuthenticationPrincipal Authentication authentication, ActionAuditHistoryDTO dataParams) {
        Object resultObj = actionService.getActionHistory(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(authentication, resultObj), HttpStatus.OK);
    }

    /**
     * lay danh sach ly do theo action id
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/action-reasons/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getActionReason(@AuthenticationPrincipal Authentication authentication, ActionDTO dataParams, @PathVariable("id") Long id) {

        Object resultObj = actionService.getActionReason(dataParams, id);
        return new ResponseEntity<>(FunctionCommon.responseToClient(authentication, resultObj), HttpStatus.OK);
    }

    /**
     * lay danh sach ly do
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/action-reasons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getActionReason(@AuthenticationPrincipal Authentication authentication, ActionDTO dataParams) {

        Object resultObj = actionService.getActionReason(dataParams, null);
        return new ResponseEntity<>(FunctionCommon.responseToClient(authentication, resultObj), HttpStatus.OK);
    }

    /**
     * lay danh sach lich su tac dong chi tiet
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @param id              action audit id
     * @return
     */
    @RequestMapping(value = "/action-histories/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getActionHistoryDetail(@AuthenticationPrincipal Authentication authentication, ActionAuditHistoryDetailDTO dataParams,
                                                         @PathVariable("id") Long id) {
        Object resultObj = actionService.getActionHistoryDetail(dataParams, id);
        return new ResponseEntity<>(FunctionCommon.responseToClient(authentication, resultObj), HttpStatus.OK);
    }

    /***
     * Lich su tac dong KH
     * @param authentication
     * @param custId
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/customers/{custId}/act-customers-histories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findActionCustomerHistory(@AuthenticationPrincipal Authentication authentication,
                                                            @PathVariable(name = "custId") Long custId,
                                                            AuditHistoryDTO requestModel) {
        requestModel.setScreenType(Constants.CUSTOMER_TYPE_SCREEN);
        requestModel.setCustId(custId);
        Object responseModel = actionService.findActionCustomerHistory(requestModel);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /***
     * Lich su tac dong hop dong
     * @param authentication
     * @param custId
     * @param contractId
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/customers/{custId}/contracts/{contractId}/act-contract-histories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findActionContractHistory(@AuthenticationPrincipal Authentication authentication,
                                                            @PathVariable(name = "custId") Long custId,
                                                            @PathVariable(name = "contractId") Long contractId,
                                                            AuditHistoryDTO requestModel) {
        requestModel.setScreenType(Constants.CONTRACT_TYPE_SCREEN);
        requestModel.setCustId(custId);
        requestModel.setContractId(contractId);
        Object responseModel = actionService.findActionCustomerHistory(requestModel);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * export danh sach lich su tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @param response        response
     * @return
     */
    @RequestMapping(value = "/act-histories/exports", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportActHistory(@AuthenticationPrincipal Authentication authentication,
                                 @RequestBody AuditHistoryDTO dataParams,
                                 HttpServletResponse response) throws Exception {

        String fileName = actionService.exportActHistory(dataParams);
        FnCommon.responseFile(response, fileName);
    }

    /***
     * Lich su tac dong phuong tien
     * @param authentication
     * @param custId
     * @param contractId
     * @param vehicleId
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/customers/{custId}/contracts/{contractId}/vehicles/{vehicleId}/act-vehicle-histories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findActionVehicleHistory(@AuthenticationPrincipal Authentication authentication,
                                                           @PathVariable(name = "custId") Long custId,
                                                           @PathVariable(name = "contractId") Long contractId,
                                                           @PathVariable(name = "vehicleId") Long vehicleId,
                                                           AuditHistoryDTO requestModel) {
        requestModel.setScreenType(Constants.VEHICLE_TYPE_SCREEN);
        requestModel.setCustId(custId);
        requestModel.setContractId(contractId);
        requestModel.setVehicleId(vehicleId);
        Object responseModel = actionService.findActionCustomerHistory(requestModel);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * Lich su tac dong phuong tien
     *
     * @param contractId   params client
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/contracts/{contractId}/act-vehicle-histories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> actionVehicleHistory(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable(name = "contractId") Long contractId,
                                                       AuditHistoryDTO requestModel) {
        requestModel.setContractId(contractId);
        Object responseModel = actionService.actionCustomerHistory(requestModel);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * export danh sach lich su tac dong
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @param response        response
     * @return
     */
    @RequestMapping(value = "/action-histories/exports", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportActionHistory(@AuthenticationPrincipal Authentication authentication, @RequestBody ActionAuditHistoryDTO dataParams, HttpServletResponse response)
            throws Exception {

        String fileName = actionService.exportActionHistory(dataParams);
        FnCommon.responseFile(response, fileName);
    }

    /**
     * Lich su tac dong phuong tien
     *
     * @param contractId   params client
     * @param requestModel
     */
    @RequestMapping(value = "/contracts/{contractId}/act-vehicle-histories/export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void actionVehicleHistoryExport(@AuthenticationPrincipal Authentication authentication,
                                           @PathVariable(name = "contractId") Long contractId,
                                           HttpServletResponse response,
                                           AuditHistoryDTO requestModel) throws IOException {
        requestModel.setContractId(contractId);
        String fileName = actionService.exportActionHistoryVehicle(requestModel);
        FnCommon.responseFile(response, fileName);
    }

    /***
     * Lay danh sach lich su the RFID
     * @param authentication
     * @param vehicleId
     * @param actionDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{id}/rfid-vehicle-histories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> serchRfidVehicleHistories(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable(name = "id") Long vehicleId,
                                                       ActionDTO actionDTO) {
        Object response = actionService.searchRfidVehicleHistories(vehicleId, actionDTO);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Lay tong so ban ghi khach hang, contract, vehicle, contract profile
     *
     * @param authentication
     * @return
     */
    @GetMapping("/count-dashboard")
    public ResponseEntity<?> getCountShowData(@AuthenticationPrincipal Authentication authentication) {
        return new ResponseEntity<>(FunctionCommon.responseToClient(actionService.getCountShowData()), HttpStatus.OK);
    }
}
