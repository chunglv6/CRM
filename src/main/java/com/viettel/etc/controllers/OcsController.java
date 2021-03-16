package com.viettel.etc.controllers;

import com.viettel.etc.services.OCSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class OcsController {

    @Autowired
    OCSService ocsService;

    @GetMapping("/ocs/query-vehicle")
    public ResponseEntity<?> queryVehicle(@RequestParam  String epc, @AuthenticationPrincipal Authentication authentication){
        return ResponseEntity.ok(ocsService.queryVehicleOcs(epc,authentication));
    }

    @GetMapping("/ocs/query-contract")
    public ResponseEntity<?> queryOcsContract(@RequestParam  String contractId, @AuthenticationPrincipal Authentication authentication){
        return ResponseEntity.ok(ocsService.queryContractOcs(contractId,authentication));
    }
}
