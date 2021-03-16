package com.viettel.etc.controllers;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.services.AttachmentFileService;
import com.viettel.etc.services.ImportServicePlanService;
import com.viettel.etc.services.ServicePlanService;
import com.viettel.etc.services.ServicePlanTypeService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

/**
 * Autogen class: Lop thao tac gia ve bang cuoc
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class ServicePlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePlanController.class);

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private ServicePlanTypeService servicePlanTypeService;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private ImportServicePlanService importServicePlanService;

    @Value("${crm.common.max-file-size}")
    private Long maxFileSize;

    /**
     * Them moi gia ve goi cuoc
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/ticket-prices", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> ticketPrices(@AuthenticationPrincipal Authentication authentication,
                                               @RequestBody @Valid ServicePlanDTO dataParams) throws EtcException, Exception {
        Object resultObj = servicePlanService.addTicketPrices(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Update gia ve bang cuoc
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams
     * @return
     */
    @RequestMapping(value = "/ticket-prices/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                     @RequestBody @Valid ServicePlanDTO dataParams,
                                                     @PathVariable(value = "id") Long servicePlanId) throws EtcException, Exception {

        dataParams.setServicePlanId(servicePlanId);
        Object resultObj = servicePlanService.updateTicketPrices(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Delete gia ve bang cuoc
     *
     * @param authentication: thong tin nguoi dung
     * @param servicePlanId
     * @return
     */
    @RequestMapping(value = "/ticket-prices/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                     @PathVariable(value = "id") Long servicePlanId) {
        servicePlanService.deleteTicketPrices(servicePlanId, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * Api App CPT: search service plan
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams
     * @return
     */
    @RequestMapping(value = Constants.MOBILE + "/service-plans", method = RequestMethod.GET)
    public ResponseEntity<Object> searchServicePlan(@AuthenticationPrincipal Authentication authentication,
                                                    PriceListDTO dataParams) throws IOException {
        Object result = servicePlanService.searchServicePlan(authentication, dataParams);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, result), HttpStatus.OK);
    }

    /**
     * Tim kiem danh sach gia ve
     *
     * @param authentication
     * @param dataParams
     * @return
     */
    @GetMapping(value = "/ticket-prices")
    public ResponseEntity<Object> searchTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                     ServicePlanDTO dataParams) {
        Object response = servicePlanService.searchTicketPrices(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Phe duyet ban ghi gia ve
     *
     * @param authentication
     * @param servicePlanDTO
     * @return
     */
    @PutMapping(value = "/ticket-prices/approval")
    public ResponseEntity<Object> approvalTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                       @RequestBody ServicePlanDTO servicePlanDTO) throws EtcException {
        List<ServicePlanEntity> response = servicePlanService.approvalTicketPrices(servicePlanDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Lay phi dang ky mua ve thang, quy
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "/service-plans/fee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFee(@AuthenticationPrincipal Authentication authentication,
                                         ServicePlanDTO dataParams) throws IOException {
        Object resultObj = servicePlanService.getFee(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Lay phi dang ky mua ve thang, quy cho mobile
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = Constants.MOBILE + "/service-plans/fee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFeeMobile(@AuthenticationPrincipal Authentication authentication,
                                               ServicePlanDTO dataParams) {
        Object resultObj = servicePlanService.getFeeMobile(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Lay danh sach loai ve
     *
     * @param authentication: thong tin nguoi dung
     * @return
     */
    @GetMapping(value = "/ticket-prices/type", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTicketType(@AuthenticationPrincipal Authentication authentication) {
        Object resultObj = servicePlanTypeService.getTicketType();
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Tu choi ban ghi gia ve
     *
     * @param authentication
     * @param servicePlanDTO
     * @return
     */
    @PutMapping(value = "/ticket-prices/reject")
    public ResponseEntity<Object> rejectTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                     @RequestBody ServicePlanDTO servicePlanDTO) {
        List<ServicePlanEntity> response = servicePlanService.rejectTicketPrices(servicePlanDTO, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Huy hieu luc ban ghi gia ve
     *
     * @param authentication
     * @param servicePlanId
     * @return
     */
    @PutMapping(value = "/ticket-prices/{id}/cancel")
    public ResponseEntity<Object> cancelTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                     @PathVariable(value = "id") Long servicePlanId) {
        Object response = servicePlanService.cancelTicketPrice(servicePlanId, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Lay gia tien nho nhat cua doan
     *
     * @param authentication
     * @param servicePlanDTO
     * @return
     */
    @GetMapping(value = "/ticket-prices/min-fee")
    public ResponseEntity<Object> searchMinFeeTicketPrices(@AuthenticationPrincipal Authentication authentication,
                                                           ServicePlanDTO servicePlanDTO) {
        Object response = servicePlanService.getMinFee(servicePlanDTO);
        return new ResponseEntity<>(FunctionCommon.responseToClient(response), HttpStatus.OK);
    }

    /**
     * Export file excel gia ve bang cuoc
     *
     * @param authentication
     * @param dataParams
     * @return
     */
    @PostMapping("/ticket-prices/exports")
    public ResponseEntity<?> exportServicePlan(@AuthenticationPrincipal Authentication authentication,
                                               @RequestBody ServicePlanDTO dataParams) throws IOException {
        String pathFile = servicePlanService.exportServicePlan(dataParams, authentication);
        File file = new File(pathFile);
        HttpHeaders header = new HttpHeaders();
        String fileName = "Danh_sach_bang_cuoc_gia_ve_" + FnCommon.convertDateToString(new Date(System.currentTimeMillis()), false, "_") + ".xlsx";
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
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

        return ResponseEntity.ok().headers(header).contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * Tai file template bang cuoc gia ve
     *
     * @param authentication Ma xac thuc
     * @param dataParams     Du lieu tim kiem
     * @return File template response
     */
    @PostMapping("/ticket-prices/download-template")
    public ResponseEntity<?> downloadServicePlanTemplate(@AuthenticationPrincipal Authentication authentication,
                                                         ServicePlanDTO dataParams) throws IOException {
        String pathFile = servicePlanService.downloadServicePlanTemplate(authentication);
        File file = new File(pathFile);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=serviceplan-template-result.xlsx");
        header.add("Access-Control-Expose-Headers", "Content-Disposition");
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = null;
        try {
            resource = new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException ex) {
            LOGGER.error("Download file error", ex);
            return new ResponseEntity<>(FunctionCommon.responseToClient("FILE NOT FOUND"), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().headers(header).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * Import du lieu bang cuoc gia ve bang file excel
     *
     * @param authentication Ma xac thuc
     * @return File excel
     */
    @PostMapping("/ticket-prices/import")
    public ResponseEntity<?> importServicePlanTemplate(@RequestParam MultipartFile fileImport,
                                                       @AuthenticationPrincipal Authentication authentication) throws IOException, EtcException {
        if (fileImport.getSize() > maxFileSize * 1024 * 1024) {
            return ResponseEntity.ok().body(FunctionCommon.responseToClient("Do lon cua file khong duoc vuot qua 5MB"));
        }
        return importServicePlanService.importServicePlan(fileImport, authentication);
    }


    /**
     * Xem chi tiet ban ghi gia ve
     *
     * @param authentication
     * @param servicePlanId
     * @return
     */
    @RequestMapping(value = "/ticket-prices/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> findTicketPricesById(@AuthenticationPrincipal Authentication authentication,
                                                       @PathVariable(value = "id") Long servicePlanId) {
        Object result = servicePlanService.findTicketPricesById(servicePlanId);
        if (result == null) {
            return new ResponseEntity<>(FunctionCommon.responseToClient(null), HttpStatus.OK);
        }
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }


    /**
     * Download file dinh kem
     *
     * @param authenEntity: thong tin nguoi dung
     * @param authenEntity
     * @param profileId
     * @return
     */
    @RequestMapping(value = "/ticket-prices/profiles/{id}/download", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> downloadProfileByContract(@AuthenticationPrincipal Authentication authenEntity,
                                                       @PathVariable(name = "id") Long profileId) {
        AttachmentFileDTO attachmentFileDTO = attachmentFileService.getFileByAttachId(profileId);
        if (attachmentFileDTO == null) {
            return new ResponseEntity<>(FunctionCommon.responseToClient("FILE NOT FOUND"), HttpStatus.NOT_FOUND);
        }
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + attachmentFileDTO.getDocumentName());
        header.add("Access-Control-Expose-Headers", "Content-Disposition");

        Path path = Paths.get(attachmentFileDTO.getDocumentPath());
        ByteArrayResource resource = null;
        try {
            resource = new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException ex) {
            LOGGER.error("Download file error", ex);
            return new ResponseEntity<>(FunctionCommon.responseToClient("FILE NOT FOUND"), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().headers(header).contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * Xoa ho so phuong tien
     *
     * @param authentication
     * @param profileId
     * @return
     * @throws IOException
     * @throws EtcException
     */
    @PutMapping(value = "/ticket-prices/profiles/{profileId}")
    public ResponseEntity<Object> deleteProfile(@AuthenticationPrincipal Authentication authentication,
                                                @PathVariable Long profileId) throws IOException, EtcException {
        servicePlanService.deleteProfile(profileId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * Lay du lieu giay to ca nhan
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/fees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangeFee(@AuthenticationPrincipal Authentication authentication,
                                               ServiceFeeDTO dataParams) {
        ServiceFeeDTO resultObj = servicePlanService.getChangeFee(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * TInh gia ve thang quy moi
     *
     * @param authentication
     * @param requestGetFeeChargeTicketDTO
     * @return
     */
    @RequestMapping(value = "/service-plans/fee-boo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFeeNew(@AuthenticationPrincipal Authentication authentication,
                                            @RequestBody RequestGetFeeChargeTicketDTO requestGetFeeChargeTicketDTO) throws IOException {
        ServicePlanFeeDTO resultObj = servicePlanService.getFeeNew(authentication, requestGetFeeChargeTicketDTO);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
