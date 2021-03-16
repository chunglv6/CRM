package com.viettel.etc.controllers;

import com.viettel.etc.dto.AcceptBriefcasesDTO;
import com.viettel.etc.dto.AcceptBriefcasesVehicleDTO;
import com.viettel.etc.dto.AdditionalBriefcasesDTO;
import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.services.BriefcasesService;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.UnknownHostException;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: Lop quan ly ho so
 *
 * @author ToolGen
 * @date Fri Jun 26 14:52:58 ICT 2020
 */
@RestController
@RequestMapping(value = REQUEST_MAPPING_V1, produces = MediaType.APPLICATION_JSON_VALUE)
public class BriefcasesController {

    @Autowired
    BriefcasesService briefcasesService;

    /**
     * Bo sung ho so
     *
     * @param authentication          Thong tin nguoi dung
     * @param contractId              id hop dong
     * @param additionalBriefcasesDTO form data bo sung hop dong
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/briefcases/{contractId}/additional-briefcases")
    public ResponseEntity<Object> additionalBriefcases(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable Long contractId,
                                                       @RequestBody @Valid AdditionalBriefcasesDTO additionalBriefcasesDTO) throws Exception, EtcException {
        Object responseModel = briefcasesService.additionalBriefcases(additionalBriefcasesDTO, authentication, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * Tra cuu ho so
     *
     * @param authentication:     thong tin nguoi dung
     * @param searchBriefcasesDTO params client
     * @return
     */

    @RequestMapping(value = "/briefcases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchBriefcases(@AuthenticationPrincipal Authentication authentication, SearchBriefcasesDTO searchBriefcasesDTO) {
        Object result = briefcasesService.searchBriefcases(searchBriefcasesDTO);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }


    /**
     * Phe duyet ho so cua hop dong
     *
     * @param authentication      Thong tin nguoi dung
     * @param acceptBriefcasesDTO form data phe duyet
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/briefcases/approval-contract-profiles")
    public ResponseEntity<Object> approvalContract(@AuthenticationPrincipal Authentication authentication,
                                                   @RequestBody @Valid AcceptBriefcasesDTO acceptBriefcasesDTO) throws UnknownHostException {
        briefcasesService.approvalContract(acceptBriefcasesDTO, authentication, true);
        return new ResponseEntity<>(FunctionCommon.responseToClient(ErrorApp.SUCCESS.getDescription()), HttpStatus.OK);
    }

    /**
     * Tu choi ho so cua hop dong
     *
     * @param authentication      Thong tin nguoi dung
     * @param acceptBriefcasesDTO form data phe duyet
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/briefcases/reject-contract-profiles")
    public ResponseEntity<Object> rejectContract(@AuthenticationPrincipal Authentication authentication,
                                                 @RequestBody @Valid AcceptBriefcasesDTO acceptBriefcasesDTO) throws UnknownHostException {
        briefcasesService.rejectContract(acceptBriefcasesDTO, authentication, false);
        return new ResponseEntity<>(FunctionCommon.responseToClient(ErrorApp.SUCCESS.getDescription()), HttpStatus.OK);
    }

    /**
     * Phe duyet ho so cua phuong tien
     *
     * @param authentication      Thong tin nguoi dung
     * @param acceptBriefcasesDTO form data phe duyet
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/briefcases/approval-vehicles-profiles")
    public ResponseEntity<Object> approvalVehicle(@AuthenticationPrincipal Authentication authentication,
                                                  @RequestBody @Valid AcceptBriefcasesVehicleDTO acceptBriefcasesDTO) {
        briefcasesService.approvalVehicle(acceptBriefcasesDTO, authentication, true);
        return new ResponseEntity<>(FunctionCommon.responseToClient(ErrorApp.SUCCESS.getDescription()), HttpStatus.OK);
    }

    /**
     * Tu choi ho so cua phuong tien
     *
     * @param authentication      Thong tin nguoi dung
     * @param acceptBriefcasesDTO form data phe duyet
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/briefcases/reject-vehicles-profiles")
    public ResponseEntity<Object> rejectVehicle(@AuthenticationPrincipal Authentication authentication,
                                                @RequestBody @Valid AcceptBriefcasesVehicleDTO acceptBriefcasesDTO) {
        briefcasesService.approvalVehicle(acceptBriefcasesDTO, authentication, false);
        return new ResponseEntity<>(FunctionCommon.responseToClient(ErrorApp.SUCCESS.getDescription()), HttpStatus.OK);
    }

}
