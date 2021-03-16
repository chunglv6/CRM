package com.viettel.etc.controllers;

import com.viettel.etc.dto.CustomerSuperAppDTO;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1 + "/superapp/driver")
public class SuperAppController {

    @Autowired
    private ContractService contractService;

    /**
     * dang ky khach hang khong thuoc ETC
     *
     * @param contract       form data customer
     * @return
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addSuperAppContract(@RequestBody @Valid CustomerSuperAppDTO contract) throws Exception {
        Object resultObj = contractService.addSuperAppContract(contract);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
