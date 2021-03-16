package com.viettel.etc.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface ImportVehicleService {
    ResponseEntity<?> importBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception;

    ResponseEntity<?> getEtagBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception;

    ResponseEntity<?> modifyPlateBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception;

    ResponseEntity<?> checkPlateBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception;

    ResponseEntity<?> synEtagBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception;
}
