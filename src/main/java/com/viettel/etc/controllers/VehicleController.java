package com.viettel.etc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viettel.etc.dto.*;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.ImportVehicleService;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: phuong tien
 *
 * @author ToolGen
 * @date Wed Jun 24 14:38:38 ICT 2020
 */
@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class VehicleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImportVehicleService importVehicleService;

    /**
     * register vehicle
     *
     * @param authentication:      thong tin nguoi dung
     * @param addVehicleRequestDTO
     * @return
     */
    @RequestMapping(value = "/customers/{customerId}/contracts/{contractId}/vehicles", method = RequestMethod.POST)
    public ResponseEntity<Object> registerVehicle(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable Long customerId,
                                                  @PathVariable Long contractId,
                                                  @RequestBody AddVehicleRequestDTO addVehicleRequestDTO,
                                                  HttpServletRequest request) throws DataNotFoundException, IOException, XMLStreamException, JAXBException {

        vehicleService.registerVehicle(addVehicleRequestDTO, authentication, customerId, contractId, request.getRemoteAddr());
        return new ResponseEntity<>(FunctionCommon.responseToClient(ErrorApp.SUCCESS), HttpStatus.OK);
    }

    /**
     * Dau noi nhanh khach hang hop dong phuong tien
     *
     * @param authentication:      thong tin nguoi dung
     * @param addVehicleQuickDTO
     * @return
     */
    @RequestMapping(value = "/vehicles", method = RequestMethod.POST)
    public ResponseEntity<Object> registerVehicleQuick(@AuthenticationPrincipal Authentication authentication,
                                                  @RequestBody AddVehicleQuickDTO addVehicleQuickDTO,
                                                  HttpServletRequest request) throws Exception {

        Object result = vehicleService.registerVehicleQuick(addVehicleQuickDTO, authentication, request.getRemoteAddr());
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /**
     * Gan the cho danh sach phuong tien
     *
     * @param authentication: thong tin nguoi dung
     * @param assignRfidDTO:  params client
     * @return
     */
    @RequestMapping(value = "/customers/{customerId}/contracts/{contractId}/vehicles/assign-rfid", method = RequestMethod.POST)
    public ResponseEntity<ResultAssignRfidDTO> vehicle(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable(name = "customerId") Long customerId,
                                                       @PathVariable(name = "contractId") Long contractId,
                                                       @RequestBody ListVehicleAssignRfidDTO assignRfidDTO) throws Exception {
        ResultAssignRfidDTO resultList = vehicleService.vehicleAssign(assignRfidDTO, authentication, customerId, contractId);
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    /**
     * Xuat danh sach xe dan the.
     *
     * @param authenEntity: thong tin nguoi dung
     * @param contractId    params client
     * @return
     */
    @RequestMapping(value = "/contracts/{id}/vehicles-assigned-export", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportVehiclesAssigned(@AuthenticationPrincipal Authentication authenEntity,
                                                    @PathVariable(name = "id") Long contractId) throws IOException {
        String pathFile = vehicleService.exportVehiclesAssigned(contractId, authenEntity);
        File file = new File(pathFile);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicle_assigned.xlsx");
        header.add("Access-Control-Expose-Headers", "Content-Disposition");
        ByteArrayResource resource = null;
        try {
            if (file.exists()) {
                Path path = Paths.get(file.getAbsolutePath());
                resource = new ByteArrayResource(Files.readAllBytes(path));
            } else {
                InputStream inputStream = getClass().getResourceAsStream(pathFile);
                resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
            }
        } catch (IOException ex) {
            LOGGER.error("Download file error", ex);
            return new ResponseEntity<>(FunctionCommon.responseToClient("FILE NOT FOUND"), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().headers(header).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * Kiem tra thong tin dang kiem
     *
     * @param authentication: thong tin nguoi dung
     * @param data          params client
     * @return
     */
    @RequestMapping(value = "/vehicles-registry-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> vehicleRegistryInfo(@AuthenticationPrincipal Authentication authentication,
                                                 @RequestBody VehicleInforRegistryDTO data) throws  Exception{
        Object responseData = vehicleService.vehicleRegistryInfo(data.getData());
        if (responseData == null) {
            return new ResponseEntity<>(FunctionCommon.responseToClient(""), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseData), HttpStatus.OK);
    }

    /**
     * Ho so dinh kem cua phuong tien phe duyet
     *
     * @param authentication: thong tin nguoi dung
     * @param vehicleId       params client
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/customers/contracts/vehicles/{id}/profiles/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileByVehicleAll(@AuthenticationPrincipal Authentication authentication,
                                                     @PathVariable(name = "id") Long vehicleId,
                                                     VehicleProfilePaginationDTO requestModel) {
        requestModel.setVehicleId(vehicleId);
        Object responseData = vehicleService.findProfileByVehicle(requestModel);
        if (responseData == null) {
            return new ResponseEntity<>(FunctionCommon.responseToClient(""), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseData), HttpStatus.OK);
    }

    /**
     * Ho so dinh kem cua phuong tien
     *
     * @param authentication: thong tin nguoi dung
     * @param vehicleId       params client
     * @param requestModel
     * @return
     */
    @RequestMapping(value = "/customers/contracts/vehicles/{id}/profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProfileByVehicle(@AuthenticationPrincipal Authentication authentication,
                                                  @PathVariable(name = "id") Long vehicleId,
                                                  VehicleProfileDTO requestModel) {
        requestModel.setVehicleId(vehicleId);
        Object responseData = vehicleService.findProfileByVehicleId(requestModel);
        if (responseData == null) {
            return new ResponseEntity<>(FunctionCommon.responseToClient(""), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseData), HttpStatus.OK);
    }

    /**
     * Download file dinh kem
     *
     * @param authenEntity: thong tin nguoi dung
     * @param authenEntity
     * @param profileId
     * @return
     */
    @RequestMapping(value = "/customers/contracts/vehicles/profiles/{id}/download", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> downloadProfileByContract(@AuthenticationPrincipal Authentication authenEntity,
                                                       @PathVariable(name = "id") Long profileId) {
        VehicleProfilePaginationDTO vehicleProfilePaginationDTO = vehicleService.downloadProfileByContract(profileId);
        if (vehicleProfilePaginationDTO == null) {
            return new ResponseEntity<>(FunctionCommon.responseToClient("FILE NOT FOUND"), HttpStatus.NOT_FOUND);
        }
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + vehicleProfilePaginationDTO.getFileName());
        header.add("Access-Control-Expose-Headers", "Content-Disposition");
        ByteArrayResource resource = new ByteArrayResource(fileService.getFile(vehicleProfilePaginationDTO.getFilePath()));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


    /**
     * API lay phuong tien dang hoat dong
     *
     * @param authenEntity: thong tin nguoi dung
     * @param contractId    params client
     * @return
     */
    @RequestMapping(value = "/customers/contracts/{id}/vehicles/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findVehicleActived(@AuthenticationPrincipal Authentication authenEntity,
                                                        @PathVariable(name = "id") Long contractId,
                                                        VehicleByContractDTO requestModel) {
        requestModel.setSearchType(Constants.VEHICLE_ACTIVE);
        requestModel.setContractId(contractId);
        Object responseModel = vehicleService.findVehicleByContract(requestModel, authenEntity);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }


    /**
     * API lay phuong tien da gan the
     *
     * @param authenEntity: thong tin nguoi dung
     * @param contractId    params client
     * @return
     */
    @RequestMapping(value = "/customers/contracts/{id}/vehicles/assigned-rfid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findVehicleAssignRFID(@AuthenticationPrincipal Authentication authenEntity,
                                                        @PathVariable(name = "id") Long contractId,
                                                        VehicleByContractDTO requestModel) {
        requestModel.setSearchType(Constants.VEHICLE_RFID);
        requestModel.setContractId(contractId);
        Object responseModel = vehicleService.findVehicleByContract(requestModel, authenEntity);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * API tra cuu thong tin phuong tien chua gan the
     *
     * @param authenEntity: thong tin nguoi dung
     * @param contractId    params client
     * @return
     */
    @RequestMapping(value = "/customers/contracts/{id}/vehicles/not-assign-rfid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findVehicleNotAssignRFID(@AuthenticationPrincipal Authentication authenEntity,
                                                           @PathVariable(name = "id") Long contractId,
                                                           VehicleByContractDTO requestModel) {
        requestModel.setSearchType(Constants.VEHICLE_NOT_RFID);
        requestModel.setContractId(contractId);
        Object responseModel = vehicleService.findVehicleByContract(requestModel, authenEntity);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * tra cuu phuong tien theo id
     *
     * @param authenEntity: thong tin nguoi dung
     * @param vehicleId     params client
     * @return
     */
    @RequestMapping(value = "/customers/contracts/vehicles/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findVehicleById(@AuthenticationPrincipal Authentication authenEntity,
                                                  @PathVariable(name = "id") Long vehicleId) {
        Object responseModel = vehicleService.findVehicleById(vehicleId, authenEntity);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }


    /**
     * API App CPT: search vehicle
     *
     * @param vehicleDTO
     * @param authentication
     * @return
     */
    @RequestMapping(value = Constants.MOBILE + "/customers/contracts/vehicles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchVehicle(@AuthenticationPrincipal Authentication authentication,
                                                VehicleDTO vehicleDTO) throws JsonProcessingException {
        Object responseModel = vehicleService.searchVehicle(vehicleDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * Save File Import
     *
     * @param authentication: thong tin nguoi dung
     * @param file:           file import
     * @return
     */
    @RequestMapping(value = "/customers/{customerId}/contracts/{contractId}/import-vehicles", method = RequestMethod.POST)
    public ResponseEntity<?> saveFileImport(@AuthenticationPrincipal Authentication authentication,
                                            @RequestParam("file") MultipartFile file,
                                            @PathVariable Long customerId,
                                            @PathVariable Long contractId) throws IOException, DataNotFoundException {
        ResultFileImportDTO resultFileImportDTO = vehicleService.saveFileImportVehicles(file, authentication, customerId, contractId);
        byte[] data = resultFileImportDTO.getResult();
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            if (fileName.endsWith(".xlsx")) {
                fileName = fileName.substring(0, fileName.length() - 5);
                fileName = FnCommon.replaceFileName(fileName) + "-result.xlsx";
            }
            if (fileName.endsWith(".xls")) {
                fileName = fileName.substring(0, fileName.length() - 4);
                fileName = FnCommon.replaceFileName(fileName) + "-result.xls";
            }
        }
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .header("Access-Control-Expose-Headers", "Number-Records-Error")
                .header("Number-Records-Error", String.valueOf(resultFileImportDTO.getListError().size()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    /**
     * Active RFID
     *
     * @param authentication
     * @param vehicleId
     * @param rfidDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{id}/active", method = RequestMethod.POST)
    public ResponseEntity activeRFID(@AuthenticationPrincipal Authentication authentication,
                                     @PathVariable("id") Long vehicleId,
                                     @RequestBody RFIDDTO rfidDTO) throws IllegalAccessException, JSONException, UnknownHostException {
        rfidDTO.setVehicleId(vehicleId);
        Object response = vehicleService.activeRFID(rfidDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Active list RFID
     *
     * @param authentication
     * @param vehicleIds
     * @param rfidDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{ids}/actives", method = RequestMethod.POST)
    public ResponseEntity activesRFID(@AuthenticationPrincipal Authentication authentication,
                                      @PathVariable("ids") List<Long> vehicleIds,
                                      @RequestBody RFIDDTO rfidDTO) {
        ActiveRFIDResponseDTO activeRFIDResponseDTO = new ActiveRFIDResponseDTO();
        List<ActiveRFIDResponseDTO.ActiveResponses> activeResponses = new ArrayList<>();
        for (Long vehicleId : vehicleIds) {
            rfidDTO.setVehicleId(vehicleId);
            try {
                ActiveRFIDResponseDTO.ActiveResponses activeRFID = vehicleService.activeRFID(rfidDTO, authentication);
                activeResponses.add(activeRFID);
            } catch (Exception e) {
                LOGGER.error("active RFID failed, vehicleIds: {}", vehicleIds, e);
                activeResponses.add(new ActiveRFIDResponseDTO.ActiveResponses(vehicleId, "FAIL", null, null, 5, null));
            }
        }
        activeRFIDResponseDTO.setActiveResponses(activeResponses);
        return new ResponseEntity<>(FunctionCommon.responseToClient(activeRFIDResponseDTO), HttpStatus.OK);
    }

    /**
     * Khoa the RFID
     *
     * @param authentication
     * @param vehicleId
     * @param rfidDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{id}/lock", method = RequestMethod.PUT)
    public ResponseEntity lockRFID(@AuthenticationPrincipal Authentication authentication,
                                   @PathVariable("id") Long vehicleId,
                                   @RequestBody RFIDDTO rfidDTO) throws IllegalAccessException, JSONException, UnknownHostException {
        rfidDTO.setVehicleId(vehicleId);
        Object response = vehicleService.lockRFID(rfidDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Mo khoa RFID
     *
     * @param authentication
     * @param vehicleId
     * @param rfidDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{id}/unlock", method = RequestMethod.PUT)
    public ResponseEntity unlockRFID(@AuthenticationPrincipal Authentication authentication,
                                     @PathVariable("id") Long vehicleId,
                                     @RequestBody RFIDDTO rfidDTO) throws IllegalAccessException, JSONException, UnknownHostException {
        rfidDTO.setVehicleId(vehicleId);
        Object response = vehicleService.unlockRFID(rfidDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Huy RFID
     *
     * @param authentication
     * @param vehicleId
     * @param rfidDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{id}/destroy", method = RequestMethod.DELETE)
    public ResponseEntity destroyRFID(@AuthenticationPrincipal Authentication authentication,
                                      @PathVariable("id") Long vehicleId,
                                      @RequestBody RFIDDTO rfidDTO) throws IllegalAccessException, JSONException, UnknownHostException {
        rfidDTO.setVehicleId(vehicleId);
        Object response = vehicleService.destroyRFID(rfidDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Doi the
     *
     * @param authentication
     * @param vehicleId
     * @param rfidDTO
     * @return
     */
    @RequestMapping(value = "/vehicles/{id}/swap", method = RequestMethod.PUT)
    public ResponseEntity swapRFID(@AuthenticationPrincipal Authentication authentication,
                                   @PathVariable("id") Long vehicleId,
                                   @RequestBody RFIDDTO rfidDTO) throws Exception {
        rfidDTO.setVehicleId(vehicleId);
        Object response = vehicleService.swapRFID(rfidDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Chuyen chu quyen phuong tien
     *
     * @param authentication
     * @param vehicleTransferDTO
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/transfer-vehicles")
    public ResponseEntity<Object> transferVehicle(@AuthenticationPrincipal Authentication authentication,
                                                  @RequestBody VehicleTransferDTO vehicleTransferDTO) throws Exception {
        vehicleService.transferVehicles(vehicleTransferDTO, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * Tai file template vehicle
     *
     * @param response       Thong tin phan hoi cho client
     * @param authentication Ma xac thuc token
     * @return Noi dung file template
     */
    @PostMapping("/vehicles/download-template")
    public ResponseEntity<Object> downloadVehiclesTemplate(HttpServletResponse response,
                                                           @AuthenticationPrincipal Authentication authentication) {
        String pathFile = vehicleService.downloadVehiclesTemplate(authentication);
        File file = new File(pathFile);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicle-template.xlsx");
        header.add("Access-Control-Expose-Headers", "Content-Disposition");
        ByteArrayResource resource = null;
        try {
            if (file.exists()) {
                Path path = Paths.get(file.getAbsolutePath());
                resource = new ByteArrayResource(Files.readAllBytes(path));
            } else {
                InputStream inputStream = getClass().getResourceAsStream(pathFile);
                resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
            }
        } catch (IOException ex) {
            LOGGER.error("Download file error", ex);
            return new ResponseEntity<>(FunctionCommon.responseToClient("FILE NOT FOUND"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().headers(header).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * Cap nhat ho so phuong tien
     *
     * @param authentication
     * @param vehicleId
     * @param updateVehicleProfileDTO
     * @return
     * @throws IOException
     * @throws EtcException
     */
    @PutMapping(value = "/vehicles/{vehicleId}/profiles")
    public ResponseEntity<Object> update(@AuthenticationPrincipal Authentication authentication,
                                         @PathVariable Long vehicleId,
                                         @RequestBody @Valid UpdateVehicleProfileDTO updateVehicleProfileDTO) throws Exception {
        vehicleService.updateProfile(new Date(System.currentTimeMillis()),
                authentication, updateVehicleProfileDTO.getContractId(), vehicleId,
                updateVehicleProfileDTO.getActTypeId(), updateVehicleProfileDTO.getReasonId(), updateVehicleProfileDTO.getVehicleProfiles());
        return new ResponseEntity<>(FunctionCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * Xoa ho so phuong tien
     *
     * @param authentication
     * @param vehicleId
     * @param profileId
     * @param updateVehicleProfileDTO
     * @return
     * @throws IOException
     * @throws EtcException
     */
    @PutMapping(value = "/vehicles/{vehicleId}/profiles/{profileId}")
    public ResponseEntity<Object> deleteProfile(@AuthenticationPrincipal Authentication authentication,
                                                @PathVariable Long vehicleId,
                                                @PathVariable Long profileId,
                                                @RequestBody UpdateVehicleProfileDTO updateVehicleProfileDTO) throws IOException, EtcException {
        vehicleService.deleteProfile(authentication, profileId, vehicleId, updateVehicleProfileDTO);
        return new ResponseEntity<>(FunctionCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * Lay thong tin dang kiem
     *
     * @param authentication: thong tin nguoi dung
     * @param plateNumber     bien so xe
     * @return
     */
    @GetMapping(value = "/vehicles/registry-info/{plateNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getVehicleRegistry(@AuthenticationPrincipal Authentication authentication,
                                                     @PathVariable("plateNumber") String plateNumber) throws Exception {
        Object result = vehicleService.getVehicleRegistry(plateNumber, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /***
     *
     * @param authentication
     * @param dataParams
     * @return
     */
    @GetMapping(value = "/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getVehiclesByPlateNumber(@AuthenticationPrincipal Authentication authentication,
                                                           VehicleSearchDTO dataParams) {
        Object result = vehicleService.getVehiclesByPlateNumber(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    @GetMapping(value = "/vehicle", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getVehicle(@AuthenticationPrincipal Authentication authentication,
                                             VehicleDTO dataParams) {
        Object result = vehicleService.getVehicle(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }

    /**
     * Tai file template vehicle except
     *
     * @param response
     * @param authentication
     * @return
     */
    @PostMapping("/vehicles/template-except-vehicle")
    public void vehiclesImportTemplateExcept(HttpServletResponse response,
                                             @AuthenticationPrincipal Authentication authentication) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/template/import/template_ngoaile_uutien_vipham.xlsx")) {
            response.setContentType("application/force-download");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment; filename=template_ngoaile_uutien_vipham.xlsx");
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * API tra cuu thong tin phuong tien da gan the
     *
     * @param authenEntity: thong tin nguoi dung
     * @param contractId    params client
     * @return
     */
    @RequestMapping(value = "/customers/contracts/{id}/vehicles/find-assigned-rfid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findVehicleAssignedRFID(@AuthenticationPrincipal Authentication authenEntity,
                                                          @PathVariable(name = "id") Long contractId,
                                                          VehicleByContractDTO requestModel) {
        requestModel.setContractId(contractId);
        Object responseModel = vehicleService.findVehicleAssignedRFID(requestModel, authenEntity);
        return new ResponseEntity<>(FunctionCommon.responseToClient(responseModel), HttpStatus.OK);
    }

    /**
     * Lay thong tin dang kiem
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @param plateNumber     bien so xe
     * @return
     */
    @GetMapping(value = "/vehicles/vehicle-registry-info/{plateNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getVehicleRegistryInfo(@AuthenticationPrincipal Authentication authentication,
                                                         VehicleDTO dataParams, @PathVariable("plateNumber") String plateNumber) {
        Object resultObj = vehicleService.getVehicleRegistryInfo(dataParams, plateNumber);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }


    /**
     * Doi bien so xe
     *
     * @param authentication
     * @param vehicleId
     * @param vehicleSwapPlateNumberDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/vehicles/{vehicleId}/swap-plate-numbers", method = RequestMethod.POST)
    public ResponseEntity swapPlateNumber(@AuthenticationPrincipal Authentication authentication,
                                          @PathVariable("vehicleId") Long vehicleId,
                                          @RequestBody VehicleSwapPlateNumberDTO vehicleSwapPlateNumberDTO) throws Exception {
        vehicleSwapPlateNumberDTO.setVehicleId(vehicleId);
        Object response = vehicleService.swapPlateNumber(vehicleSwapPlateNumberDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * update phuong tien
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/customers/{customerId}/contracts/{contractId}/vehicles/{vehicleId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> modifyVehicle(@AuthenticationPrincipal Authentication authentication,
                                                   @PathVariable Long customerId,
                                                   @PathVariable Long contractId,
                                                   @PathVariable Long vehicleId,
                                                   @RequestBody ModifyVehicleDTO dataParams) throws Exception {
        Object object = vehicleService.modifyVehicle(dataParams, customerId, contractId, vehicleId, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(object), HttpStatus.OK);
    }

    /***
     * Tool import vehicle BL QL T Q
     * @param fileImport
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/import-vehicle",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> importBatchVehicle(@RequestParam MultipartFile fileImport, @AuthenticationPrincipal Authentication authentication) throws Exception {
        return importVehicleService.importBatchVehicle(fileImport, authentication);
    }


    /***
     * Get etag batch vehicle from BOO1
     * @param fileImport
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/etag-batch-vehicle",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> getEtagBatchVehicle(@RequestParam MultipartFile fileImport, @AuthenticationPrincipal Authentication authentication) throws Exception {
        return importVehicleService.getEtagBatchVehicle(fileImport, authentication);
    }


    /***
     * Modify batch vehicle from OCS
     * @param fileImport
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/modify-batch-vehicle",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> modifyPlateBatchVehicle(@RequestParam MultipartFile fileImport, @AuthenticationPrincipal Authentication authentication) throws Exception {
        return importVehicleService.modifyPlateBatchVehicle(fileImport, authentication);
    }

    /***
     * Check batch vehicle from OCS
     * @param fileImport
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/check-batch-vehicle",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> checkPlateBatchVehicle(@RequestParam MultipartFile fileImport, @AuthenticationPrincipal Authentication authentication) throws Exception {
        return importVehicleService.checkPlateBatchVehicle(fileImport, authentication);
    }

    /***
     * Syn etag batch vehicle from BOO1
     * @param fileImport
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/syn-batch-vehicle",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> synEtagBatchVehicle(@RequestParam MultipartFile fileImport, @AuthenticationPrincipal Authentication authentication) throws Exception {
        return importVehicleService.synEtagBatchVehicle(fileImport, authentication);
    }

    /***
     * Kiem tra bien so xe
     * @throws Exception
     */
    @GetMapping(value = "/vehicle/info/{plate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> vehicleInfo(@PathVariable String plate,
                                              @AuthenticationPrincipal Authentication authentication) throws Exception {
        Object resultObj = vehicleService.getInfoByPlateNumber(plate, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
