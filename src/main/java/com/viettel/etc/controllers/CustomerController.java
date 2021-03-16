package com.viettel.etc.controllers;

import com.google.zxing.WriterException;
import com.viettel.etc.dto.*;
import com.viettel.etc.services.ContractService;
import com.viettel.etc.services.CustomerService;
import com.viettel.etc.services.QrCodeService;
import com.viettel.etc.utils.*;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static com.viettel.etc.utils.Constants.REQUEST_MAPPING_V1;

@RestController
@RequestMapping(value = REQUEST_MAPPING_V1)
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    ContractService contractService;

    @Autowired
    QrCodeService qrCodeService;

    @Autowired
    VehicleService vehicleService;

    /**
     * ky moi hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param dataParams     form data ky moi hop dong
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/customers/{customerId}/contracts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveContract(@AuthenticationPrincipal Authentication authentication,
                                               @PathVariable Long customerId,
                                               @RequestBody @Valid ContractDTO dataParams) throws Exception {
        Object resultObj = contractService.addContract(dataParams, authentication, customerId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * ky phu luc hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data phu luc hop dong
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/customers/{customerId}/contracts/{contractId}/append-contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> editContract(@AuthenticationPrincipal Authentication authentication,
                                               @PathVariable Long customerId,
                                               @PathVariable Long contractId,
                                               @RequestBody AddVehicleRequestDTO dataParams,
                                               HttpServletRequest request) throws IOException, EtcException, JAXBException, DataNotFoundException, XMLStreamException {
        Object addVehicleRequestDTO = contractService.appendContract(dataParams, authentication, customerId, contractId, request.getRemoteAddr());
        return new ResponseEntity<>(FunctionCommon.responseToClient(CoreErrorApp.SUCCESS, addVehicleRequestDTO), HttpStatus.OK);
    }


    /**
     * sua doi hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data sua hop dong
     * @return
     */
    @PutMapping(value = "/customers/{customerId}/contracts/{contractId}")
    public ResponseEntity<Object> editContract(@AuthenticationPrincipal Authentication authentication,
                                               @PathVariable Long customerId,
                                               @PathVariable Long contractId,
                                               @RequestBody ContractDTO dataParams) throws EtcException, Exception {
        Object resultObj = contractService.editContract(dataParams, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }


    /**
     * dang ky khach hang doanh nghiep
     *
     * @param authentication user dang nhap
     * @param dataParams     form data customer
     * @return
     */
    @PostMapping(value = "/customer-enterprise", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addCustomerEnterprise(@AuthenticationPrincipal Authentication authentication,
                                                        @RequestBody @Valid CustomerDTO dataParams) throws Exception {
        Object resultObj = customerService.addCustomerEnterprise(dataParams, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * dang ky khach hang cac nhan
     *
     * @param authentication user dang nhap
     * @param customer       form data customer
     * @return
     */
    @PostMapping(value = "/customer-personal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveCustomer(@AuthenticationPrincipal Authentication authentication,
                                               @RequestBody @Valid CustomerDTO customer) throws Exception {
        Object resultObj = customerService.addCustomer(customer, authentication);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * tach hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data tach hop dong
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/customers/{customerId}/contracts/{contractId}/split-contracts")
    public ResponseEntity<Object> splitContract(@AuthenticationPrincipal Authentication authentication,
                                                @PathVariable Long customerId,
                                                @PathVariable Long contractId,
                                                @RequestBody @Valid SplitContractDTO dataParams) throws Exception {
        Object resultObj = contractService.splitContract(dataParams, authentication, customerId, contractId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * huy hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param dataParams     form data huy hop dong
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/customers/{customerId}/contracts")
    public ResponseEntity<Object> terminateContract(@AuthenticationPrincipal Authentication authentication,
                                                    @PathVariable Long customerId,
                                                    @RequestBody @Valid TerminateContractDTO dataParams) throws Exception {
        contractService.terminateContract(dataParams, authentication, customerId);
        return new ResponseEntity<>(FunctionCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * gop hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data gop hop dong
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/customers/{customerId}/contracts/{contractId}/merges")
    public ResponseEntity<Object> mergeContract(@AuthenticationPrincipal Authentication authentication,
                                                @PathVariable Long customerId,
                                                @PathVariable Long contractId,
                                                @RequestBody @Valid MergeContractDTO dataParams) throws Exception {
        contractService.mergeContract(dataParams, authentication, customerId, contractId);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }


    /**
     * thay doi thong tin khach hang cac nhan
     *
     * @param authentication user dang nhap
     * @param customer       form data customer
     * @return
     */
    @PutMapping(value = "/customer-personal/{custId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateCustomer(@AuthenticationPrincipal Authentication authentication,
                                                 @PathVariable Long custId,
                                                 @RequestBody CustomerDTO customer) throws Exception {
        customerService.updateCustomer(customer, custId, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * Cap nhat thong tin tai khoan chu phuong tien
     * API App CPT
     *
     * @param authentication: Thong tin nguoi dung
     * @return
     */

    @PutMapping(value = Constants.MOBILE + "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUserData(@AuthenticationPrincipal Authentication authentication,
                                                 @RequestBody UsersDTO usersDTO) throws Exception {
        customerService.updateCustomer(usersDTO.getPhone(), usersDTO.getEmail(), authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }

    /**
     * thay doi thong tin khach hang doanh nghiep
     *
     * @param authentication user dang nhap
     * @param customer       form data customer
     * @return
     */
    @PutMapping(value = "/customer-enterprise/{custId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateCustomerEnterprise(@AuthenticationPrincipal Authentication authentication,
                                                           @PathVariable Long custId,
                                                           @RequestBody CustomerDTO customer) throws Exception {
        customerService.updateCustomerEnterprise(customer, custId, authentication);
        return new ResponseEntity<>(FnCommon.responseToClient(CoreErrorApp.SUCCESS, null), HttpStatus.OK);
    }


    /**
     * Tim kiem khach hang theo : DOCUMENT_NUMBER, PHONE_NUMBER
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     * @author Chucnd
     */
    @RequestMapping(value = "/customer-info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findCustomerByDocumentAndPhone(@AuthenticationPrincipal Authentication authentication,
                                                                 CustomerSearchDTO dataParams) {

        if (dataParams != null && dataParams.getInputSearch() != null) {
            dataParams.setToken(FnCommon.getStringToken(authentication));
            Object resultObj = customerService.findCustomerByDocumentAndPhone(dataParams);
            return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FunctionCommon.responseToClient(""), HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Tra cuu thong tin khach hang
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @RequestMapping(value = "/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchTreeInfo(@AuthenticationPrincipal Authentication authentication,
                                                 SearchInfoDTO dataParams) {
        Object resultObj = customerService.searchTreeInfo(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }

    /**
     * Thong tin khach hang theo id
     *
     * @param authenEntity: thong tin nguoi dung
     * @param custId        params client
     * @return
     */
    @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomerById(@AuthenticationPrincipal Authentication authenEntity,
                                             @PathVariable(name = "id") Long custId) {
        Object result = customerService.findCustomerById(custId, FnCommon.getStringToken(authenEntity));
        return new ResponseEntity<>(FunctionCommon.responseToClient(result), HttpStatus.OK);
    }


    /***
     * Lay qrcode cua khach hang
     * @param contractNo
     * @return
     * @throws WriterException
     * @throws IOException
     */
    @GetMapping(Constants.MOBILE + "/customers/qrcode")
    public ResponseEntity<?> getQrCodeById(@RequestParam String contractNo) throws WriterException, IOException {
        String pathQrCode = qrCodeService.getQrCodeOfCust(contractNo);
        return new ResponseEntity<>(FunctionCommon.responseToClient(pathQrCode), HttpStatus.OK);
    }


    /**
     * lay thong tin dang ky dich vu cua khach hang, du lieu duoc dua vao tu phan he cong thong tin
     *
     * @param authentication: thong tin nguoi dung
     * @param dataParams      params client
     * @return
     */
    @GetMapping(value = "/customers/registry-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCustomerRegister(@AuthenticationPrincipal Authentication authentication,
                                                      CustRegisterDTO dataParams) {
        Object resultObj = customerService.getCustomerRegister(dataParams);
        return new ResponseEntity<>(FunctionCommon.responseToClient(resultObj), HttpStatus.OK);
    }
}
