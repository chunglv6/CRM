package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.dto.viettelpay.*;
import com.viettel.etc.repositories.SaleOrderDetailRepository;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.repositories.ViettelPayRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.SaleOrderServiceJPA;
import com.viettel.etc.services.tables.SaleTransDetailServiceJPA;
import com.viettel.etc.services.tables.SaleTransServiceJPA;
import com.viettel.etc.services.tables.TopupEtcServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.encoding.RsaCrypto;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ViettelPayServiceImpl implements ViettelPayService {

    public static final String MESSAGE_ERROR = "Cap nhat du lieu that bai";
    public static final String MESSAGE_SUCCESS = "Cap nhat du lieu thanh cong";
    public static final String MESSAGE_INFO_SUCCESS = "Thanh cong";
    public static final String MESSAGE_ERROR_NOT_EXIST = "Khong ton tai du lieu";
    private static final Logger LOGGER = LoggerFactory.getLogger(ViettelPayServiceImpl.class);
    private static final String POST_REQUEST = "POST";
    private static final String GET_REQUEST = "GET";
    private static final String SUCCESS_CODE = "00";
    private static final String VIETTEL_PAY = "viettelpay_attached_file";
    private static final String E_WALLET = "E_WALLET";
    @Autowired
    ViettelPayRepository vtPayRepository;
    @Autowired
    ContractPaymentService contractPaymentService;
    @Autowired
    OCSService ocsService;
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    AttachmentFileRepositoryJPA attachmentFileRepository;
    @Autowired
    ContractPaymentRepositoryJPA contractPaymentRepositoryJPA;
    @Autowired
    ContractRepositoryJPA contractRepositoryJPA;
    @Autowired
    CustomerRepositoryJPA customerRepositoryJPA;
    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;
    @Autowired
    SaleTransDetailServiceJPA saleTransDetailServiceJPA;
    @Autowired
    SaleTransServiceJPA saleTransServiceJPA;
    @Autowired
    StageService stageService;
    @Autowired
    SaleOrderRepositoryJPA saleOrderRepositoryJPA;
    @Autowired
    SaleOrderServiceJPA saleOrderServiceJPA;
    @Autowired
    SaleOrderDetailRepositoryJPA saleOrderDetailRepositoryJPA;
    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;
    @Autowired
    SaleTransRepositoryJPA saleTransRepositoryJPA;
    @Autowired
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    TopupEtcServiceJPA topupEtcServiceJPA;
    @Autowired
    ServicePlanService servicePlanService;
    @Autowired
    ActReasonRepositoryJPA actReasonRepositoryJPA;
    @Autowired
    TicketServiceImpl ticketServiceImpl;
    @Autowired
    ContractService contractService;

    @Value("${ws.viettelpay.url.link-init}")
    private String linkInitViettelPayUrl;
    @Value("${ws.viettelpay.url.link-confirm}")
    private String linkConfirmViettelPayUrl;
    @Value("${ws.viettelpay.url.cancel-init}")
    private String linkCancelInitViettelPayUrl;
    @Value("${ws.viettelpay.url.cancel-confirm}")
    private String linkCancelConfirmViettelPayUrl;
    @Value("${ws.viettelpay.url.sources-query}")
    private String linkGetSourcesViettelPayUrl;
    @Value("${crm.common.max-file-size}")
    private Integer briefcaseMaxFileSize;
    @Value("${ws.viettelpay.private-key}")
    private String privateKey;
    @Value("${ws.viettelpay.client-secret}")
    private String clientSecret;
    @Value("${ws.viettelpay.client-id}")
    private String clientId;
    @Value("${ws.viettelpay.channel-code}")
    private String channelCode;
    @Value("${ws.viettelpay.accept-language}")
    private String acceptLanguage;
    @Value("${ws.dmdc.plate.type}")
    private String plateTypeUrl;
    @Value("${ws.dmdc.stage.get}")
    private String stageUrl;
    @Value("${ws.dmdc.stations.boo}")
    private String stationUrl;
    @Value("${viettelpay.access.code}")
    private String vtpAccessCode;
    @Value("${viettelpay.hash.key}")
    private String vtpHashKey;
    @Autowired
    private FileService fileService;
    @Autowired
    private TicketService ticketService;
    @Value("${viettelpay.version}")
    private String version;
    @Value("${ws.ocs.retry}")
    private String numberRetry;
    @Value("${viettelpay.merchant.code}")
    private String merchanCodeViettelPay;
    @Value("${ws.viettelpay.url.verify-payment}")
    private String vtpVerifyPaymentUrl;
    @Autowired
    JedisCacheService jedisCacheService;
    @Autowired
    LuckyService luckyService;

    /**
     * Tim kiem hop dong theo: 1. tim kiem theo plateType va plateNumber
     * 2. Tim kiem theo contractNo
     * 3. Tim kiem theo contractId
     *
     * @return Object
     */
    @Override
    public Object findByPlateOrContract(RequestBaseViettelDTO data, Authentication authentication) {
        StatusDTO status = null;
        Object result = null;
        DataDTO value = null;
        Object lstVehicle = null;
        if (!FnCommon.isNullOrEmpty(data.getSearchType()) && "2".equals(data.getSearchType())) {
            if (!FnCommon.isNullOrEmpty(data.getContractNo())) {
                result = vtPayRepository.findByPlateOrContract(data.getOrderId(), data.getSearchType(),
                        data.getContractId(), data.getContractNo(), data.getSearchType(), data.getPlateNumber());
            } else {
                status = new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, jedisCacheService.getMessageErrorByKey("viettelpay.contract-no.not.empty-or-null"));
            }
        } else if (!FnCommon.isNullOrEmpty(data.getSearchType()) && "1".equals(data.getSearchType())) {
            if (!FnCommon.isNullOrEmpty(data.getPlateNumber()) && data.getPlateType() != null) {
                result = vtPayRepository.findByPlateOrContract(data.getOrderId(), data.getSearchType(),
                        data.getContractId(), data.getContractNo(), data.getPlateType(), data.getPlateNumber());
            } else {
                status = new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, jedisCacheService.getMessageErrorByKey("viettelpay.plateType-or-plateNumber.required"));
            }
        } else if (!FnCommon.isNullOrEmpty(data.getSearchType()) && "3".equals(data.getSearchType())) {
            if (data.getContractId() != null) {
                result = vtPayRepository.findByPlateOrContract(data.getOrderId(), data.getSearchType(),
                        data.getContractId(), data.getContractNo(), data.getSearchType(), data.getPlateNumber());
            } else {
                status = new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, jedisCacheService.getMessageErrorByKey("viettelpay.contract-id.not.empty-or-null"));
            }
        } else {
            status = new StatusDTO().responseMessage(StatusDTO.StatusCode.INVALID, jedisCacheService.getMessageErrorByKey("viettelpay.search.contract"));
        }
        if (result != null) {
            if (((List<?>) result).isEmpty()) {
                status = new StatusDTO().responseMessage(StatusDTO.StatusCode.NOT_EXIST, jedisCacheService.getMessageErrorByKey("viettelpay.contract.not.exists"));
            } else {
                status = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, jedisCacheService.getMessageErrorByKey("viettelpay.search.contract.success"));
            }
            value = new DataDTO().convertToDataDTO(result, data.getOrderId());
            if (value.getContractId() != null) {
                if (value.getPaymentLinking() == null) {
                    value.setPaymentLinking("0");
                } else {
                    ContractPaymentEntity contractPaymentEntity = contractPaymentService.findByContractIdAndStatus(value.getContractId(), "1");
                    if (contractPaymentEntity != null) {
                        value.setPaymentLinking("1");
                    } else {
                        value.setPaymentLinking("2");
                    }
                }
                lstVehicle = vtPayRepository.findAllVehicleByContractAndPlate(value.getContractId());
            }
        }
        return new ResponseViettelPayDTO.ResponseContract(value, status, lstVehicle);
    }

    /**
     * Cap nhat contract payment khi lien ket viettelpay
     *
     * @param authentication Ma xac thuc
     * @return Object
     */

    @Override
    public Object updateContractPaymentWhenRegister(RequestConfirmRegisterDTO data, Authentication authentication) {
        StatusDTO statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.FAIL, MESSAGE_ERROR);
        boolean hasSuccess = contractPaymentService.updateContractPaymentWhenRegister(data, authentication);
        if (hasSuccess) {
            Optional<ContractEntity> contractEntity = contractRepositoryJPA.findById(FnCommon.retlong(data.getContractId()));
            if (contractEntity.isPresent()) {
                Optional<CustomerEntity> customerEntity = customerRepositoryJPA.findById(contractEntity.get().getCustId());
                if (customerEntity.isPresent()) {
                    String contractId = String.valueOf(contractEntity.get().getContractId());
                    try {
                        updateContractOnOCS(customerEntity.get().getCustId(), contractId, data.getMsisdn(), data.getToken(),
                                OCSUpdateContractForm.ChargeMethod.VIETTELPAY.value, contractEntity.get(), Constants.ACT_TYPE.LK_VTPAY.intValue(), authentication);
                        statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_SUCCESS);

                        // Luu them lucky code chuong trinh
                        luckyService.genLuckyCode(contractEntity.get(), Constants.LUCKY_CODE.LKVTPAY, authentication);
                    } catch (EtcException ex) {
                        LOGGER.error("Loi khi thuc hien goi OCS", ex);
                        statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_SUCCESS);
                        return new ResponseViettelPayDTO.ResponseRegisterConfirm(new ResponseViettelPayDTO.DataRegisterDTO(data.getOrderId(), data.getMsisdn(),
                                data.getBankCode(), data.getContractId()), statusDTO);
                    }
                }
            }
        }
        return new ResponseViettelPayDTO.ResponseRegisterConfirm(new ResponseViettelPayDTO.DataRegisterDTO(data.getOrderId(), data.getMsisdn(),
                data.getBankCode(), data.getContractId()), statusDTO);
    }

    /**
     * Cap nhat contract payment khi huy lien ket viettelpay
     *
     * @return Object
     */
    @Override
    public Object updateContractPaymentWhenUnRegister(RequestContractPaymentDTO data, Authentication authentication) {
        StatusDTO statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.FAIL, MESSAGE_ERROR);
        boolean hasSuccess = contractPaymentService.updateContractPaymentWhenUnRegister(data);
        if (hasSuccess) {
            Optional<ContractEntity> contractEntity = contractRepositoryJPA.findById(FnCommon.retlong(data.getContractId()));
            if (contractEntity.isPresent()) {
                Optional<CustomerEntity> customerEntity = customerRepositoryJPA.findById(contractEntity.get().getCustId());
                if (customerEntity.isPresent()) {
                    String contractId = String.valueOf(contractEntity.get().getContractId());
                    try {
                        updateContractOnOCS(customerEntity.get().getCustId(), contractId, data.getMsisdn(), data.getToken(),
                                OCSUpdateContractForm.ChargeMethod.NONE.value, contractEntity.get(), Constants.ACT_TYPE.LK_VTPAY.intValue(), authentication);
                        statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_SUCCESS);
                    } catch (EtcException ex) {
                        LOGGER.error("Loi khi thuc hien goi OCS", ex);
                        statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_SUCCESS);
                        return new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO(data.getOrderId(), data.getMsisdn(), data.getContractId()), statusDTO);
                    }
                }
            }
        }
        return new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO(data.getOrderId(), data.getMsisdn(), data.getContractId()), statusDTO);
    }

    /**
     * Tra ket qua thay doi nguon tien
     *
     * @param authentication Token xac thuc
     * @return Object
     */
    @Override
    public Object changeMoneySourceViettelPay(RequestConfirmChangeMoneySourceDTO data, Authentication authentication) {
        StatusDTO statusDTO;
        boolean hasSuccess = contractPaymentService.changeMoneySourceViettelPay(data, authentication);
        if (hasSuccess) {
            statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_SUCCESS);
        } else {
            statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.FAIL, MESSAGE_ERROR);
        }
        return new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO(data.getOrderId(), data.getMsisdn(), data.getContractId()), statusDTO);
    }

    /**
     * Thuc hien goi toi server viettel pay khoi tao lien ket tai khoan
     *
     * @param data du lieu client truyen len
     * @return Object
     */
    @Override
    public Object linkInitViettelPay(RequestLinkInitViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException {
        ContractPaymentEntity contractPaymentEntity = contractPaymentRepositoryJPA.findByContractId(data.getContractId());
        if (!FnCommon.isNullObject(contractPaymentEntity) && contractPaymentEntity.getStatus().equals(ContractPaymentEntity.Status.ACTIVATED.value)) {
            throw new EtcException("viettelpay.init.contract.exist");
        }
        String responseLinkInit = doRequestViettelPay(POST_REQUEST, linkInitViettelPayUrl, FnCommon.toStringJson(data), null, authentication, data.getActionTypeId(), 1);
        LOGGER.info("linkInitViettelPay " + responseLinkInit);
        ResponseViettelPayDTO.ResponseLinkInitDTO responseLink = new Gson().fromJson(responseLinkInit, ResponseViettelPayDTO.ResponseLinkInitDTO.class);
        hasErrorOccurred(responseLink);
        if (SUCCESS_CODE.equals(responseLink.getStatus().getCode())) {
            return responseLink.getData();
        } else {
            throw new DataNotFoundException("Lỗi VIETTELPAY: " + responseLink.getStatus().getMessage());
        }
    }

    /**
     * Thuc hien goi toi server viettel pay xac nhan khoi tao lien ket tai khoan
     *
     * @param data du lieu client truyen len
     * @return Object
     */
    @Override
    public Object linkConfirmViettelPay(RequestLinkConfirmViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException {
        if (!Base64.isBase64(data.getCardFileBase64())) {
            throw new EtcException("viettelpay.validate.file.cmnd.format.base64.invalid");
        }
        if (!Base64.isBase64(data.getDocumentLinkInitFileBase64())) {
            throw new EtcException("viettelpay.validate.file.approve.format.base64.invalid");
        }
        String response = doRequestViettelPay(POST_REQUEST, linkConfirmViettelPayUrl, FnCommon.toStringJson(data), null, authentication, data.getActionTypeId(), 1);
        LOGGER.info("linkConfirmViettelPay " + response);
        ResponseViettelPayDTO.ResponseLinkConfirmDTO responseLinkConfirm = new Gson().fromJson(response, ResponseViettelPayDTO.ResponseLinkConfirmDTO.class);
        hasErrorOccurred(responseLinkConfirm);
        if (SUCCESS_CODE.equals(responseLinkConfirm.getStatus().getCode())) {
            try {
                String userLogin = FnCommon.getUserLogin(authentication);
                Long contractPaymentId = saveOrUpdateContractPayment(FnCommon.retlong(responseLinkConfirm.getData().getContractId()), userLogin, responseLinkConfirm, data, authentication);

                //Call OCS update contract
                ContractEntity contractEntity = contractRepositoryJPA.findById(FnCommon.retlong(responseLinkConfirm.getData().getContractId())).get();
                contractEntity.setPaymentDefaultId(contractPaymentId);
                contractRepositoryJPA.save(contractEntity);
                saveAttachmentFile(data, userLogin, contractPaymentId);
                updateContractOnOCS(contractEntity.getCustId(), responseLinkConfirm.getData().getContractId(), responseLinkConfirm.getData().getMsisdn(),
                        responseLinkConfirm.getData().getToken(), OCSUpdateContractForm.ChargeMethod.VIETTELPAY.value, contractEntity, data.getActionTypeId().intValue(), authentication);
            } catch (Exception e) {
                LOGGER.error("HAS ERROR " + e);
                executelinkCancelViettelPay(responseLinkConfirm, FnCommon.retlong(responseLinkConfirm.getData().getContractId()), authentication);
            }
            return responseLinkConfirm.getData();
        } else {
            throw new DataNotFoundException("Lỗi VIETTELPAY: " + responseLinkConfirm.getStatus().getMessage());
        }
    }

    /**
     * Thuc hien goi toi server viettel pay huy lien ket tai khoan
     *
     * @return Object
     */
    private void executelinkCancelViettelPay(ResponseViettelPayDTO.ResponseLinkConfirmDTO responseLinkConfirm, Long contractId, Authentication authentication) throws IOException, DataNotFoundException {
        RequestCancelInitViettelPayDTO request = new RequestCancelInitViettelPayDTO();
        request.setContractId(contractId);
        request.setOrderId(FnCommon.formatDateTime(new Date(System.currentTimeMillis())));
        request.setMsisdn(responseLinkConfirm.getData().getMsisdn());
        request.setToken(responseLinkConfirm.getData().getToken());
        request.setActionTypeId(13L);

        Object responseCancelInit = linkCancelInitViettelPay(request, authentication);
        ResponseViettelPayDTO.DataCancelInitDTO responseCancel = new Gson().fromJson(FnCommon.toStringJson(responseCancelInit), ResponseViettelPayDTO.DataCancelInitDTO.class);
        if (!FnCommon.isNullObject(responseCancel)) {
            RequestCancelConfirmViettelPayDTO requestConfirm = new RequestCancelConfirmViettelPayDTO();
            requestConfirm.setOrderId(FnCommon.formatDateTime(new Date(System.currentTimeMillis())));
            requestConfirm.setOriginalOrderId(responseCancel.getOrderId());
            requestConfirm.setOtp(responseCancel.getOtp());
            requestConfirm.setToken(responseLinkConfirm.getData().getToken());
            requestConfirm.setContractId(FnCommon.retlong(responseCancel.getContractId()));
            requestConfirm.setMsisdn(responseCancel.getMsisdn());
            requestConfirm.setActionTypeId(13L);
            linkCancelConfirmViettelPay(requestConfirm, authentication);
        }
        throw new EtcException("viettelpay.init.confirm.error");
    }

    /**
     * Thuc hien goi toi server viettel pay khoi tao huy lien ket tai khoan
     *
     * @param data du lieu client truyen len
     * @return Object
     */
    @Override
    public Object linkCancelInitViettelPay(RequestCancelInitViettelPayDTO data, Authentication authentication) throws DataNotFoundException, EtcException, IOException {
        ContractPaymentEntity contractPaymentEntity = contractPaymentService.findByContractId(data.getContractId());
        if (FnCommon.isNullObject(contractPaymentEntity)) {
            throw new EtcException("viettelpay.init.contract.not.exist");
        }
        String cancelInitResponse = doRequestViettelPay(POST_REQUEST, linkCancelInitViettelPayUrl, FnCommon.toStringJson(data), null, authentication, data.getActionTypeId(), 1);
        LOGGER.info("linkCancelInitViettelPay " + cancelInitResponse);
        ResponseViettelPayDTO.ResponseCancelInitDTO responseCancel = new Gson().fromJson(cancelInitResponse, ResponseViettelPayDTO.ResponseCancelInitDTO.class);
        hasErrorOccurred(responseCancel);
        if (!SUCCESS_CODE.equals(responseCancel.getStatus().getCode())) {
            throw new DataNotFoundException("Lỗi VIETTELPAY: " + responseCancel.getStatus().getMessage());
        }
        return responseCancel.getData();
    }

    /**
     * Thuc hien goi toi server viettel pay xac nhan huy lien ket tai khoan
     *
     * @param data du lieu client truyen len
     * @return Object
     */
    @Override
    public Object linkCancelConfirmViettelPay(RequestCancelConfirmViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException {
        String response = doRequestViettelPay(POST_REQUEST, linkCancelConfirmViettelPayUrl, FnCommon.toStringJson(data), null, authentication, data.getActionTypeId(), 1);
        LOGGER.info("linkCancelConfirmViettelPay " + response);
        ResponseViettelPayDTO.ResponseLinkConfirmDTO responseLinkCancelConfirmDTO = new Gson().fromJson(response, ResponseViettelPayDTO.ResponseLinkConfirmDTO.class);
        hasErrorOccurred(responseLinkCancelConfirmDTO);
        if (SUCCESS_CODE.equals(responseLinkCancelConfirmDTO.getStatus().getCode())) {
            saveOrUpdateContractPayment(FnCommon.retlong(responseLinkCancelConfirmDTO.getData().getContractId()), FnCommon.getUserLogin(authentication), null, null, authentication);
            ContractEntity contractEntity = contractRepositoryJPA.findById(FnCommon.retlong(responseLinkCancelConfirmDTO.getData().getContractId())).get();
            contractEntity.setPaymentDefaultId(null);
            contractRepositoryJPA.save(contractEntity);
            updateContractOnOCS(contractEntity.getCustId(), String.valueOf(data.getContractId()), data.getMsisdn(),
                    null, OCSUpdateContractForm.ChargeMethod.NONE.value, contractEntity, data.getActionTypeId().intValue(), authentication);
            return responseLinkCancelConfirmDTO.getData();
        } else {
            throw new DataNotFoundException("Lỗi VIETTELPAY: " + responseLinkCancelConfirmDTO.getStatus().getMessage());
        }
    }

    /**
     * Thuc hien goi toi server viettel pay lay thong tin nguon thanh toan cua tai khoan
     *
     * @param msisdn so dien thoai/tai khoan cua khach hang
     * @return Object
     */
    @Override
    public Object getSourcesViettelPay(String msisdn, Long actTypeId, Authentication authentication) throws DataNotFoundException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("msisdn", msisdn);
        String response = doRequestViettelPay(GET_REQUEST, linkGetSourcesViettelPayUrl, null, params, authentication, actTypeId, 1);
        LOGGER.info("getSourcesViettelPay " + response);
        ResponseViettelPayDTO.ResponseGetSourcesViettelPay getSourcesViettelPay = new Gson().fromJson(response, ResponseViettelPayDTO.ResponseGetSourcesViettelPay.class);
        hasErrorOccurred(getSourcesViettelPay);
        if (SUCCESS_CODE.equals(getSourcesViettelPay.getStatus().getCode())) {
            return getSourcesViettelPay.getData();
        }
        throw new DataNotFoundException("Lỗi VIETTELPAY: " + getSourcesViettelPay.getStatus().getMessage());
    }

    /***
     * Lay thong tin mua ve va dang ky gia han ve qua sdk
     * @param authentication
     * @param billingCode
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public Object getInfoTicketPurchaseAndExtendedViaSDK(Authentication authentication, String billingCode) throws JsonProcessingException {
        StatusDTO statusDTO = null;
        Object infoTicketPurchase = vtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDK(billingCode);
        if (!FnCommon.isNullObject(infoTicketPurchase)) {
            Gson gson = new Gson();
            List<DataTypeDTO.DataType> dataTicketTypes = ticketService.getTicketTypes(FnCommon.getStringToken(authentication));
            List<DataTypeDTO.DataType> dataPlateTypes = this.getPlateTypes(FnCommon.getStringToken(authentication));
            Map<Long, String> mapTicketTypes = dataTicketTypes.stream().collect(Collectors.toMap(DataTypeDTO.DataType::getId, DataTypeDTO.DataType::getVal));
            Map<Long, String> mapPlateTypes = dataPlateTypes.stream().collect(Collectors.toMap(DataTypeDTO.DataType::getId, DataTypeDTO.DataType::getVal));
            ResponseGetInfoTicketPurchaseAndExtendedDTO response = gson.fromJson(gson.toJson(infoTicketPurchase), ResponseGetInfoTicketPurchaseAndExtendedDTO.class);
            Object tickets = vtPayRepository.getTicketExtendedViaSDK(billingCode);
            if (!FnCommon.isNullObject(tickets)) {
                ObjectMapper mapper = new ObjectMapper();
                String json = gson.toJson(tickets);
                List<ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder> responseTickets = mapper.readValue(json, new TypeReference<List<ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder>>() {
                });
                StationBooDTO stationName = null;
                StationBooDTO stationNameOut = null;
                if (responseTickets != null) {
                    for (ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder ticketOrder : responseTickets) {
                        if (ticketOrder.getTicketType() != null) {
                            ticketOrder.setTicketType(mapTicketTypes.get(Long.parseLong(ticketOrder.getTicketType())));
                        }
                        if (ticketOrder.getPlateTypeId() != null) {
                            ticketOrder.setPlateType(mapPlateTypes.get(Long.parseLong(ticketOrder.getPlateTypeId())));
                        }
                        if (ticketOrder.getStageId() != null) {
                            StageBooDTO stageDTO = this.getStageById(FnCommon.getStringToken(authentication), String.valueOf(ticketOrder.getStageId()));

                            if (stageDTO.getData() != null) {
                                ticketOrder.setStationCode(stageDTO.getData().getStation_input_id());
                                ticketOrder.setStationCodeOut(stageDTO.getData().getStation_output_id());
                                stationName = this.getStationById(FnCommon.getStringToken(authentication), String.valueOf(stageDTO.getData().getStation_input_id()));
                                stationNameOut = this.getStationById(FnCommon.getStringToken(authentication), String.valueOf(stageDTO.getData().getStation_output_id()));
                                ticketOrder.setStationName(stationName.getData().getName());
                                ticketOrder.setStationNameOut(stationNameOut.getData().getName());
                            }
                        } else {
                            stationName = this.getStationById(FnCommon.getStringToken(authentication), String.valueOf(ticketOrder.getStationId()));
                            ticketOrder.setStationCode(ticketOrder.getStationId());
                            ticketOrder.setStationName(stationName.getData().getName());
                        }
                    }
                }
                response.setTickes(responseTickets);
                statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_INFO_SUCCESS);
            }
            return new ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtened(statusDTO, response);
        } else {
            statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.NOT_EXIST, MESSAGE_ERROR_NOT_EXIST);
            return new ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtened(statusDTO, null);
        }
    }

    /**
     * Thuc hien luu file dinh kem khi thuc hien lien ket the thanh cong
     *
     * @param data du lieu client truyen len
     * @return Object
     */
    public void saveAttachmentFile(RequestLinkConfirmViettelPayDTO data, String userLogin, Long objectId) throws EtcException, IOException {
        byte[] cardFile = Base64.decodeBase64(data.getCardFileBase64());
        byte[] linkInitFile = Base64.decodeBase64(data.getDocumentLinkInitFileBase64());
        executeSaveAttachmentFile(data.getCardDocumentName(), cardFile, userLogin, objectId);
        executeSaveAttachmentFile(data.getDocumentLinkInitName(), linkInitFile, userLogin, objectId);
    }

    /**
     * Thuc hien luu file dinh kem khi thuc hien lien ket the thanh cong
     *
     * @param documentName du lieu client truyen len
     * @param file         du lieu client truyen len
     * @param userLogin    du lieu client truyen len
     * @param objectId     du lieu client truyen len
     * @return
     */
    private void executeSaveAttachmentFile(String documentName, byte[] file, String userLogin, Long objectId) throws IOException {
        AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity();
        String filePath = String.format("%s/%s/%s", VIETTEL_PAY, UUID.randomUUID().toString(), "-" + documentName);
        if (!FnCommon.checkBriefcaseValid(filePath, file, briefcaseMaxFileSize)) {
            throw new EtcException("common.validate.briefcase.invalid");
        }
        attachmentFileEntity.setAttachmentType(AttachmentFileEntity.ATTACH_TYPE.OTHER.value);
        attachmentFileEntity.setStatus(AttachmentFileEntity.STATUS.ACTIVE.value);
        attachmentFileEntity.setObjectId(objectId);
        attachmentFileEntity.setDocumentName(documentName);
        attachmentFileEntity.setDocumentPath(filePath);
        attachmentFileEntity.setCreateDate(new Date(System.currentTimeMillis()));
        attachmentFileEntity.setCreateUser(userLogin);
        fileService.uploadFile(filePath, file);
        attachmentFileRepository.save(attachmentFileEntity);
    }

    /**
     * Thuc hien luu vao bang contract payment khi thuc hien lien ket the thanh cong
     *
     * @param data du lieu client truyen len
     * @return Object
     */
    private Long saveOrUpdateContractPayment(Long contractId, String loginUser, ResponseViettelPayDTO.ResponseLinkConfirmDTO data, RequestLinkConfirmViettelPayDTO requestLinkToContractPayment, Authentication authentication) {
        try {
            Optional<ContractEntity> contractEntity = contractRepositoryJPA.findById(contractId);
            ContractPaymentEntity contractPaymentEntity = contractPaymentService.findByContractId(contractId);
            String userToken = FnCommon.getStringToken(authentication);
            if (!FnCommon.isNullObject(contractPaymentEntity)) {
                if (contractPaymentEntity.getStatus().equals(ContractPaymentEntity.Status.ACTIVATED.value)) {
                    contractPaymentEntity.setStatus(ContractPaymentEntity.Status.NOT_ACTIVATED.value);
                } else {
                    CategoryDTO.CategoryData methodRecharge = categoriesService.findCategoriesByTableNameAndCode(userToken, E_WALLET, data.getData().getBankCode());
                    contractPaymentEntity = requestLinkToContractPayment.toContractPaymentEntityUpdate(contractPaymentEntity);
                    contractPaymentEntity.setToken(data.getData().getToken());
                    contractPaymentEntity.setOrderId(data.getData().getOrderId());
                    contractPaymentEntity.setMethodRechargeCode(data.getData().getBankCode());
                    contractPaymentEntity.setMethodRechargeId(methodRecharge.getData().getMethodRechargeId());
                    contractPaymentEntity.setAccountBankId(methodRecharge.getData().getId());
                    contractPaymentEntity.setLinkPhone(data.getData().getMsisdn());
                    // Luu them lucky code chuong trinh
                    luckyService.genLuckyCode(contractEntity.get(), Constants.LUCKY_CODE.LKVTPAY, authentication);
                }
            } else {
                CategoryDTO.CategoryData methodRecharge = categoriesService.findCategoriesByTableNameAndCode(userToken, E_WALLET, data.getData().getBankCode());
                if (methodRecharge != null) {
                    contractPaymentEntity = requestLinkToContractPayment.toContractPaymentEntity();
                    if (contractEntity.isPresent()) {
                        contractPaymentEntity.setCustId(contractEntity.get().getCustId());
                    }
                    contractPaymentEntity.setCreateUser(loginUser);
                    contractPaymentEntity.setToken(data.getData().getToken());
                    contractPaymentEntity.setOrderId(data.getData().getOrderId());
                    contractPaymentEntity.setContractId(FnCommon.retlong(data.getData().getContractId()));
                    contractPaymentEntity.setMethodRechargeId(methodRecharge.getData().getMethodRechargeId());
                    contractPaymentEntity.setAccountBankId(methodRecharge.getData().getId());
                    contractPaymentEntity.setMethodRechargeCode(data.getData().getBankCode());
                    contractPaymentEntity.setLinkPhone(data.getData().getMsisdn());
                    // Luu them lucky code chuong trinh
                    luckyService.genLuckyCode(contractEntity.get(), Constants.LUCKY_CODE.LKVTPAY, authentication);
                }
            }
            return contractPaymentRepositoryJPA.save(contractPaymentEntity).getContractPaymentId();
        } catch (Exception e) {
            LOGGER.error(" Save/update contract payment error! " + e);
            throw new EtcException("crm.save.update.contractpayment.error");
        }
    }

    /***
     * Hàm ghi log gọi Viettel Pay vào bảng WS_AUDIT
     * @param req
     * @param resp
     * @param url
     * @param ip
     * @param timeCallOCS
     * @param actTypeId
     * @param authentication
     * @param wsCallType
     * @param status
     */
    public WsAuditEntity writeLog(String req, String resp, String url, String ip, long timeCallOCS, long actTypeId,
                                  Authentication authentication, String wsCallType, String status) {
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            String userLogin = "system", clientId = "crm";
            if (authentication != null) {
                userLogin = FnCommon.getUserLogin(authentication);
                clientId = FnCommon.getClientId(authentication);
                wsAuditEntity.setSourceAppId(clientId);
                wsAuditEntity.setDestinationAppId("VIETTELPAY");
            } else {
                wsAuditEntity.setSourceAppId("VIETTELPAY");
                wsAuditEntity.setDestinationAppId(clientId);
            }
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setActTypeId(actTypeId);
            wsAuditEntity.setFinishTime(timeCallOCS);
            wsAuditEntity.setActionUserName(userLogin);
            wsAuditEntity.setRequestTime(new Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            wsAuditEntity.setIpPc(ip);
            if (resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOGGER.error("write log ws_audit failed", e);
        }
        return wsAuditEntity;
    }

    /**
     * Gui request toi server viettel pay.
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String doRequestViettelPay(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
        String strRes = "", ip = null;
        long start = 0L, end = 0L;
        Request request = null;
        try {
            if (step <= Integer.parseInt(numberRetry)) {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(60, TimeUnit.SECONDS);
                client.setReadTimeout(60, TimeUnit.SECONDS);
                client.setWriteTimeout(60, TimeUnit.SECONDS);
                HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

                if (params != null) {
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                    }
                }

                Map<String, String> headers = new HashMap<>();
                headers.put("Client-Id", clientId);
                if (POST_REQUEST.equalsIgnoreCase(requestMethod)) {
                    headers.put("Signature", RsaCrypto.Sign(jsonData, privateKey, true));
                } else {
                    headers.put("Order-Id", FnCommon.formatDateTime(new Date(System.currentTimeMillis())));
                }
                headers.put("Client-Secret", clientSecret);
                headers.put("Timestamp", FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                headers.put("Channel-Code", channelCode);
                headers.put("Accept-Language", acceptLanguage);

                Headers headerBuilder = Headers.of(headers);
                com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("application/json");
                RequestBody body = FnCommon.isNullOrEmpty(jsonData) ? null : RequestBody.create(mediaType, jsonData);
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method(requestMethod, body)
                        .headers(headerBuilder)
                        .build();
                Response response = client.newCall(request).execute();
                start = System.currentTimeMillis();
                end = System.currentTimeMillis() - start;
                strRes = response.body().string();
                writeLog(request.toString(), strRes, url, "127.0.0.1", end, actTypeId, authentication, requestMethod, WsAuditEntity.Status.SUCCESS.value);
                return strRes;
            }
        } catch (Exception e) {
            if (request != null) {
                writeLog(request.toString(), strRes, url, "127.0.0.1", end, actTypeId, authentication, requestMethod, WsAuditEntity.Status.NOT_SUCCESS.value);
                doRequestViettelPay(requestMethod, url, jsonData, params, authentication, actTypeId, ++step);
            }
            LOGGER.error("Has error", e);
        }
        return null;
    }

    /**
     * Gui request toi server OCS cap nhat hop dong.
     *
     * @param customerId
     * @param contractId
     * @param msisdn
     * @param actTypeId
     * @param authentication
     * @param value
     * @param contractEntity
     * @return
     */
    private void updateContractOnOCS(Long customerId, String contractId, String msisdn, String token, String value, ContractEntity contractEntity,
                                     int actTypeId, Authentication authentication) {
        CustomerEntity customerEntity = customerRepositoryJPA.findById(customerId).get();
        OCSUpdateContractForm osOcsUpdateContractForm = new OCSUpdateContractForm();
        osOcsUpdateContractForm.setContractId(contractId);
//        osOcsUpdateContractForm.setMsisdn(msisdn);
        osOcsUpdateContractForm.setChargeMethod(value);
        String contractStatus = "2".equals(contractEntity.getStatus()) ? "1" : "0";
        osOcsUpdateContractForm.setContractStatus(contractStatus);
        osOcsUpdateContractForm.setEffDate(FnCommon.convertDateToString(contractEntity.getEffDate()));
        osOcsUpdateContractForm.setContractType(String.valueOf(customerEntity.getCustTypeId()));
        if (!FnCommon.isNullOrEmpty(token)) {
            osOcsUpdateContractForm.setToken(token);
        }
        if (!value.equalsIgnoreCase(OCSUpdateContractForm.ChargeMethod.NONE.value)) {
            osOcsUpdateContractForm.setAccountNumber(msisdn);
            osOcsUpdateContractForm.setAccountOwner(customerEntity.getCustName());
        }
        OCSResponse ocsResponse = ocsService.updateContract(osOcsUpdateContractForm, authentication, actTypeId);
        if (!"0".equals(ocsResponse.getResultCode())) {
            LOGGER.info("Update contract fail, customerId: {}, contractId: {}", contractEntity.getCustId(), contractEntity.getContractId());
            throw new EtcException("ocs.communicate.error");
        }
    }

    /***
     * kiem tra co loi khi convert du lieu tra ve tu viettelpay
     * @param o
     * @throws DataNotFoundException
     */
    private void hasErrorOccurred(Object o) throws DataNotFoundException {
        if (FnCommon.isNullObject(o)) {
            throw new DataNotFoundException("viettelpay.communicate.error");
        }
    }


    /***
     * lay thong tin cua loai bien so
     * @param token
     * @return
     */
    private List<DataTypeDTO.DataType> getPlateTypes(String token) {
        List<DataTypeDTO.DataType> result = new ArrayList<>();
        String strResp = FnCommon.doGetRequest(plateTypeUrl, null, token);
        DataTypeDTO dataTypeDTO = new Gson().fromJson(strResp, DataTypeDTO.class);
        if (dataTypeDTO.getData() != null) {
            result = dataTypeDTO.getData();
        }
        return result;
    }

    /***
     * lay thong tin cua doan
     * @param token
     * @return
     */
    private StageBooDTO getStageById(String token, String stageId) {
        String requestUrl = stageUrl.replace("{stageId}", stageId);
        String stageResponse = FnCommon.doGetRequest(requestUrl, null, token);
        return new Gson().fromJson(stageResponse, StageBooDTO.class);
    }

    /**
     * Tra ket qua gia han ve thang quy
     *
     * @param requestRenewTicketPricesDTO
     * @param authentication
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseViettelPayDTO.ResponseUnRegisterConfirm confirmResultTicketPurchaseAutoRenew(RequestRenewTicketPricesDTO requestRenewTicketPricesDTO
            , Authentication authentication) {
        return handleAutoRenew(Constants.VIETTEL_PAY_TYPE_REGISTER.REGISTER, requestRenewTicketPricesDTO, authentication);
    }

    @Override
    public ResponseViettelPayDTO.ResponseUnRegisterConfirm confirmResultCancelTicketPurchaseAutoRenew(RequestRenewTicketPricesDTO requestRenewTicketPricesDTO, Authentication authentication) {
        return handleAutoRenew(Constants.VIETTEL_PAY_TYPE_REGISTER.CANCEL, requestRenewTicketPricesDTO, authentication);
    }

    /**
     * Xu ly du lieu truoc khi xu ly gia han ve thang quy
     *
     * @param requestRenewTicketPricesDTO
     * @param authentication
     * @return
     */
    public ResponseViettelPayDTO.ResponseUnRegisterConfirm handleAutoRenew(String type,
                                                                           RequestRenewTicketPricesDTO requestRenewTicketPricesDTO,
                                                                           Authentication authentication) {
        String isCancel = Constants.VIETTEL_PAY_TYPE_REGISTER.CANCEL;
        StatusDTO statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_SUCCESS);
        List<SaleOrderDetailEntity> result = new ArrayList<>();
        Long contractId = null;
        if (!Objects.isNull(requestRenewTicketPricesDTO.autoRenew_VTP) && !requestRenewTicketPricesDTO.getAutoRenew_VTP().isEmpty()) {
            for (AutoRenewVtpDTO autoRenewVtpDTO : requestRenewTicketPricesDTO.getAutoRenew_VTP()) {
                List<SaleOrderDetailEntity> saleOrderDetailEntities = getSaleOrderDetail(autoRenewVtpDTO, authentication);
                if (!saleOrderDetailEntities.isEmpty()) {
                    result.add(saleOrderDetailEntities.get(0));
                }
                if (!result.isEmpty()) {
                    for (SaleOrderDetailEntity saleOrderDetailEntity : result) {
                        if (Constants.VIETTEL_PAY_TYPE_REGISTER.CANCEL.equals(type)) {
                            saleOrderDetailEntity.setAutoRenew("0");
                            saleOrderDetailEntity.setAutoRenewVtp("0");
                        } else {
                            saleOrderDetailEntity.setAutoRenew("1");
                            saleOrderDetailEntity.setAutoRenewVtp("1");
                            isCancel = Constants.VIETTEL_PAY_TYPE_REGISTER.REGISTER;
                        }
                    }
                    saleOrderDetailRepositoryJPA.saveAll(result);
                    contractId = handleOcsAutoRenewVtp(result, authentication, isCancel);
                }
            }
        }
        return new ResponseViettelPayDTO.ResponseUnRegisterConfirm(new ResponseViettelPayDTO.DataUnRegisterDTO(String.valueOf(requestRenewTicketPricesDTO.getOrderId()), requestRenewTicketPricesDTO.getMsisdn(),
                contractId), statusDTO);
    }

    /**
     * Mua ve thang quy viettelPay
     *
     * @param authentication
     * @param requestChargeTicketVTPDTO
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseChargeTicketDTO chargeTicketVTP(Authentication authentication, RequestChargeTicketVTPDTO requestChargeTicketVTPDTO) {
        ResponseChargeTicketDTO.SetResponseChargeTicket setResponseChargeTicket = new ResponseChargeTicketDTO.SetResponseChargeTicket();
        List<SupOfferDTOSuccesDTO> listSuccess = new ArrayList<>();
        String userLogin = FnCommon.getUserLogin(authentication);
        Long orderId = Long.parseLong(requestChargeTicketVTPDTO.getOrderId());
        Long billingCode = Long.parseLong(requestChargeTicketVTPDTO.getBillingCode());
        SaleOrderEntity saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderId(billingCode);
        setResponseChargeTicket.setOrderId(String.valueOf(orderId));
        setResponseChargeTicket.setBillingCode(String.valueOf(billingCode));
        Long contractId = saleOrderEntity.getContractId();
        setResponseChargeTicket.setContractId(contractId);
        setResponseChargeTicket.setMsisdn(requestChargeTicketVTPDTO.getMsisdn());
        try {
            if (SaleOrderEntity.Status.SUCCESS_PAYMENT.value.equals(saleOrderEntity.getStatus())) {
                setResponseChargeTicket.setCode(String.valueOf(2));
                throw new EtcException("viettelpay.charge.ticket.has.been.paid");
            }
            SaleTransEntity saleTransEntity = setValueSaleOrderToSaleTrans(saleOrderEntity, userLogin);
            saleTransEntity = saleTransRepositoryJPA.save(saleTransEntity);
            AddSupOfferRequestDTO addSupOfferRequestDTO = new AddSupOfferRequestDTO();
            addSupOfferRequestDTO.setAmount(saleTransEntity.getAmount());
            addSupOfferRequestDTO.setActTypeId(Constants.ACT_TYPE.CHARGE_TICKET);
            chargeOCS(addSupOfferRequestDTO, authentication, contractId, String.valueOf(saleTransEntity.getSaleTransId()));
            List<SaleOrderDetailEntity> saleOrderDetailEntityList = saleOrderDetailRepositoryJPA.findAllBySaleOrderId(billingCode);
            // Thuc hien dang ki goi cuoc phu cho tung phuong tien
            for (SaleOrderDetailEntity saleOrderDetailEntity : saleOrderDetailEntityList) {
                ServicePlanDTO servicePlanDTO = setValueServicePlan(saleOrderDetailEntity);
                ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = ticketRepository.checkExistsTicket(servicePlanDTO, false);
                if (servicePlanVehicleDuplicateDTO.getPlateNumber() != null && servicePlanVehicleDuplicateDTO.getPlateNumber().contains(saleOrderDetailEntity.getPlateNumber())) {
                    Map<String, String> parameter = new HashMap<>();
                    parameter.put("PARAMETER", String.valueOf(saleOrderDetailEntity.getPlateNumber()));
                    setResponseChargeTicket.setCode(String.valueOf(2));
                    throw new EtcException("crm.charge.ticket.exist.sub.offer", parameter);
                }
                VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(saleOrderDetailEntity.getVehicleId());
                if (!VehicleEntity.Status.ACTIVATED.value.equals(vehicleEntity.getStatus()) ||
                        VehicleEntity.ActiveStatus.TRANSFERRED.value.equals(vehicleEntity.getActiveStatus())) {
                    setResponseChargeTicket.setCode(String.valueOf(5));
                    throw new EtcException("viettelpay.paramter.input.invalid");
                }
                VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = setValueToVehicleAddSupOffer(saleOrderDetailEntity, vehicleEntity);
                OCSResponse ocsResponse = addSupOfferOCS(authentication, vehicleAddSuffOfferDTO, 1L,
                        contractId, userLogin, saleTransEntity.getSaleTransId().toString());

                if (!FnCommon.checkOcsCode(ocsResponse)) {
                    // neu dang ki goi cuoc phu cho 1 xe that  bai thi huy toan bo goi cuoc phu da dang ki thanh cong truoc do
                    for (SupOfferDTOSuccesDTO supOfferDTOSuccesDTO : listSuccess) {
                        ocsService.deleteSupOffer(authentication, Constants.ACT_TYPE.DESTROY_TICKET,
                                contractId, supOfferDTOSuccesDTO.getEpc(), supOfferDTOSuccesDTO.getOfferId(), supOfferDTOSuccesDTO.getOfferLevel());
                    }
                    // them lai so du
                    ocsService.addBalance(authentication, Constants.ACT_TYPE.NAP_TIEN, contractId, addSupOfferRequestDTO.getAmount());
                    Map<String, String> parameter = new HashMap<>();
                    parameter.put("PARAMETER", String.valueOf(saleOrderDetailEntity.getPlateNumber()));
                    setResponseChargeTicket.setCode(String.valueOf(5));
                    throw new EtcException("viettelpay.paramter.input.invalid");
                }
                if (FnCommon.checkOcsCode(ocsResponse)) {
                    SupOfferDTOSuccesDTO supOfferDTOSuccesDTO = setValueToSupOffer(contractId, vehicleEntity.getEpc(), saleOrderDetailEntity.getOcsCode(), "2");
                    listSuccess.add(supOfferDTOSuccesDTO);
                    SaleTransDetailEntity saleTransDetailEntity = setValueToSaleTransDetail(saleOrderDetailEntity, saleTransEntity, userLogin);
                    saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
                    saleOrderRepositoryJPA.save(saleOrderEntity);
                    setResponseChargeTicket.setMessage("SUCCESS");
                    setResponseChargeTicket.setResponseTime(FnCommon.convertDateToStringOther(new java.util.Date(), "yyyyMMddhh24mmss"));
                    setResponseChargeTicket.setCode(String.valueOf(0));
                    saleOrderEntity.setStatus(String.valueOf(SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Coi loi xay ra khi dang ky goi cuoc phu", e);
            if (!"5".equals(setResponseChargeTicket.getCode()) || !"2".equals(setResponseChargeTicket.getCode())) {
                setResponseChargeTicket.setCode("99");
            }
            setResponseChargeTicket.setMessage("FAIL");
            setResponseChargeTicket.setDescription(e.getMessage());
            setResponseChargeTicket.setResponseTime(FnCommon.convertDateToStringOther(new java.util.Date(), "yyyyMMddhh24mmss"));
        }
        return new ResponseChargeTicketDTO(new ResponseChargeTicketDTO.ResponseStatus(setResponseChargeTicket.getCode(),
                setResponseChargeTicket.getMessage(), setResponseChargeTicket.getDescription(), setResponseChargeTicket.getResponseTime()),
                new ResponseChargeTicketDTO.ResponseData(setResponseChargeTicket.getOrderId(),
                        setResponseChargeTicket.getContractId(), setResponseChargeTicket.getBillingCode(), setResponseChargeTicket.getMsisdn()));
    }


    /**
     * Set value to sale_trans
     *
     * @param saleOrderEntity
     * @param userLogin
     * @return
     */
    private SaleTransEntity setValueSaleOrderToSaleTrans(SaleOrderEntity saleOrderEntity, String userLogin) {
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransCode(String.valueOf(saleOrderEntity.getSaleOrderId()));
        saleTransEntity.setSaleTransDate(saleOrderEntity.getSaleOrderDate());
        saleTransEntity.setSaleTransType(saleOrderEntity.getSaleOrderType());
        saleTransEntity.setStatus(saleOrderEntity.getStatus());
        saleTransEntity.setAmount(saleOrderEntity.getAmount());
        saleTransEntity.setQuantity(saleOrderEntity.getQuantity());
        saleTransEntity.setCreateUser(userLogin);
        saleTransEntity.setCreateDate(new Date(System.currentTimeMillis()));
        saleTransEntity.setCustId(saleOrderEntity.getCustId());
        saleTransEntity.setContractNo(saleOrderEntity.getContractNo());
        saleTransEntity.setContractId(saleOrderEntity.getContractId());
        return saleTransEntity;
    }

    /**
     * Tru tien OCS
     *
     * @param addSupOfferRequestDTO
     * @param authentication
     * @param contractId
     * @param partyCode
     * @throws Exception
     */
    private void chargeOCS(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, long contractId, String partyCode) throws Exception {
        LOGGER.info("Start call  charge  OCS");
        long start = System.currentTimeMillis();
        OCSResponse ocsResponse = ocsService.charge(addSupOfferRequestDTO, authentication, contractId, partyCode);
        if (!FnCommon.checkOcsCode(ocsResponse)) {
            throw new EtcException("ocs.charge.error");
        }
        long end = System.currentTimeMillis() - start;
        LOGGER.info("End call  charge OCS in : " + end);
    }

    /**
     * Set gia tri cua xe mua ve cuoc phu
     *
     * @param saleOrderDetailEntity
     * @param vehicleEntity
     */
    private VehicleAddSuffOfferDTO setValueToVehicleAddSupOffer(SaleOrderDetailEntity saleOrderDetailEntity, VehicleEntity vehicleEntity) {
        VehicleAddSuffOfferDTO vehicleAddSupOffer = new VehicleAddSuffOfferDTO();
        vehicleAddSupOffer.setOfferLevel(String.valueOf(Constants.OFFER_LEVEL_DEFAULT));
        vehicleAddSupOffer.setOfferId(saleOrderDetailEntity.getOcsCode());
        vehicleAddSupOffer.setEpc(vehicleEntity.getEpc());
        vehicleAddSupOffer.setTid(vehicleEntity.getTid());
        vehicleAddSupOffer.setRfidSerial(vehicleEntity.getRfidSerial());
        vehicleAddSupOffer.setServicePlanId(saleOrderDetailEntity.getServicePlanId());
        if (Objects.nonNull(saleOrderDetailEntity.getServicePlanName())) {
            vehicleAddSupOffer.setServicePlanName(String.valueOf(saleOrderDetailEntity.getServicePlanName()));
        }
        if (Objects.nonNull(saleOrderDetailEntity.getServicePlanTypeId())) {
            vehicleAddSupOffer.setServicePlanTypeId(saleOrderDetailEntity.getServicePlanTypeId());
        }
        if (Objects.nonNull(saleOrderDetailEntity.getServicePlanId())) {
            vehicleAddSupOffer.setServicePlanId(saleOrderDetailEntity.getServicePlanId());
        }
        if (Objects.nonNull(saleOrderDetailEntity.getAutoRenew())) {
            vehicleAddSupOffer.setAutoRenew(saleOrderDetailEntity.getAutoRenew());
        }
        return vehicleAddSupOffer;
    }

    /**
     * @param authentication
     * @param vehicleAddSuffOfferDTO
     * @param actTypeId
     * @param contractId
     * @param userLogin
     * @param partyCode
     * @return
     * @throws Exception
     */
    public OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
        LOGGER.info("Start call addSupOffer OCS");
        long start1 = System.currentTimeMillis();
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        Long staffId = 0L;
        if (Objects.nonNull(map) && map.containsKey(Constants.USER_ATTRIBUTE.STAFF_ID)) {
            staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
        }

        OCSResponse ocsResponse = ocsService.addSupOffer(vehicleAddSuffOfferDTO, authentication, actTypeId, contractId, staffId, userLogin, partyCode);
        long end1 = System.currentTimeMillis() - start1;
        LOGGER.info("End call addSupOffer OCS in: {}", end1);
        return ocsResponse;
    }

    /**
     * Set gia tri mua goi cuoc phu
     *
     * @param contractId
     * @param epc
     * @param offerId
     * @param offerLevel
     * @return
     */
    private SupOfferDTOSuccesDTO setValueToSupOffer(Long contractId, String epc, String offerId, String offerLevel) {
        SupOfferDTOSuccesDTO supOfferDTOSuccesDTO = new SupOfferDTOSuccesDTO();
        supOfferDTOSuccesDTO.setContractId(contractId);
        supOfferDTOSuccesDTO.setEpc(epc);
        supOfferDTOSuccesDTO.setOfferId(offerId);
        supOfferDTOSuccesDTO.setOfferLevel(offerLevel);
        return supOfferDTOSuccesDTO;
    }

    /**
     * Luu du lieu vao saleTransDetail
     *
     * @param saleOrderDetailEntity
     * @param saleTransEntity
     * @param userLogin
     * @return
     */
    private SaleTransDetailEntity setValueToSaleTransDetail(SaleOrderDetailEntity saleOrderDetailEntity, SaleTransEntity saleTransEntity, String userLogin) {
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setSaleTransId(saleTransEntity.getSaleTransId());
        saleTransDetailEntity.setSaleTransDate(saleTransEntity.getSaleTransDate());
        saleTransDetailEntity.setPrice(saleOrderDetailEntity.getPrice());
        saleTransDetailEntity.setQuantity(saleOrderDetailEntity.getQuantity());
        saleTransDetailEntity.setCreateUser(userLogin);
        saleTransDetailEntity.setCreateDate(new Date(System.currentTimeMillis()));
        saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value);
        saleTransDetailEntity.setEpc(saleOrderDetailEntity.getEpc());
        saleTransDetailEntity.setTid(saleOrderDetailEntity.getTid());
        saleTransDetailEntity.setPlateNumber(saleOrderDetailEntity.getPlateNumber());
        saleTransDetailEntity.setRfidSerial(saleOrderDetailEntity.getRfidSerial());
        saleTransDetailEntity.setVehicleGroupId(saleOrderDetailEntity.getVehicleGroupId());
        saleTransDetailEntity.setServicePlanTypeId(saleOrderDetailEntity.getServicePlanTypeId());
        saleTransDetailEntity.setOcsCode(saleOrderDetailEntity.getOcsCode());
        saleTransDetailEntity.setScope(saleOrderDetailEntity.getScope());
        saleTransDetailEntity.setOfferLevel(saleOrderDetailEntity.getOfferLevel());
        saleTransDetailEntity.setEffDate(saleOrderDetailEntity.getEffDate());
        saleTransDetailEntity.setExpDate(saleOrderDetailEntity.getExpDate());
        saleTransDetailEntity.setAutoRenew(saleOrderDetailEntity.getAutoRenew());
        saleTransDetailEntity.setStationId(saleOrderDetailEntity.getStationId());
        saleTransDetailEntity.setStageId(saleOrderDetailEntity.getStageId());
        return saleTransDetailEntity;
    }

    private ServicePlanDTO setValueServicePlan(SaleOrderDetailEntity saleOrderDetailEntity) {
        ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        if (Objects.nonNull(saleOrderDetailEntity.getStageId())) {
            servicePlanDTO.setStageId(saleOrderDetailEntity.getStageId());
        }
        if (Objects.nonNull(saleOrderDetailEntity.getStationId())) {
            servicePlanDTO.setStationId(saleOrderDetailEntity.getStationId());
        }

        if (Objects.nonNull(saleOrderDetailEntity.getServicePlanTypeId())) {
            servicePlanDTO.setServicePlanTypeId(saleOrderDetailEntity.getServicePlanTypeId());
        }

        if (Objects.nonNull(saleOrderDetailEntity.getVehicleGroupId())) {
            servicePlanDTO.setVehicleGroupId(saleOrderDetailEntity.getVehicleGroupId());
        }

        if (Objects.nonNull(saleOrderDetailEntity.getAutoRenew())) {
            servicePlanDTO.setAutoRenew(Long.parseLong(saleOrderDetailEntity.getAutoRenew()));
        }
        if (Objects.nonNull(saleOrderDetailEntity.getEffDate())) {
            servicePlanDTO.setCreateDateFrom(FnCommon.convertDateToStringOther(saleOrderDetailEntity.getEffDate(), "dd/MM/yyyy"));
        }
        if (Objects.nonNull(saleOrderDetailEntity.getExpDate())) {
            servicePlanDTO.setCreateDateTo(FnCommon.convertDateToStringOther(saleOrderDetailEntity.getExpDate(), "dd/MM/yyyy"));
        }
        if (Objects.nonNull(saleOrderDetailEntity.getPlateNumber())) {
            servicePlanDTO.setPlateNumber(saleOrderDetailEntity.getPlateNumber());
        }
        return servicePlanDTO;
    }


    /**
     * Mua ve thang quy
     *
     * @param addSupOfferRequest
     * @param authentication
     * @param customerId
     * @param contractId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResAddSupOfferDTO confirmChargeTicket(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication
            , Long customerId, Long contractId) throws IOException {
        ResAddSupOfferDTO result = new ResAddSupOfferDTO();
        if (!addSupOfferRequest.isAcountETC()) {
            throw new EtcException("crm.account.is.wrong");
        }
        ContractEntity contractEntity = contractRepositoryJPA.findByContractId(contractId);
        validateChargeTicket(addSupOfferRequest, contractEntity);
        String userLogin = FnCommon.getUserLogin(authentication);
        List<ServicePlanDTO> listTicket = new ArrayList<>();
        for (int i = 0; i < addSupOfferRequest.getList().size(); i++) {
            VehicleAddSuffOfferDTO vehicleAddSubOfferDTO = addSupOfferRequest.getList().get(i);
            ServicePlanDTO ticket = toServicePlanDTO(vehicleAddSubOfferDTO);
            ticket.setVehicleId(vehicleAddSubOfferDTO.getVehicleId());
            listTicket.add(ticket);
        }
        RequestGetFeeChargeTicketDTO requestGetFeeChargeTicket = new RequestGetFeeChargeTicketDTO();
        requestGetFeeChargeTicket.setServicePlanDTOList(listTicket);
        ServicePlanFeeDTO infoTickets = servicePlanService.getFeeNew(authentication, requestGetFeeChargeTicket);
        if (!FnCommon.isNullOrEmpty(infoTickets.getServicePlanVehicleDuplicate())) {
            String messError = jedisCacheService.getMessageErrorByKey("crm.price.is.exist") + ": " + infoTickets.getServicePlanVehicleDuplicate();
            throw new EtcException(messError);
        }

        Long totalCharge = getTotalCharge(infoTickets.getListServicePlan());
        if (!totalCharge.equals(addSupOfferRequest.getAmount())) {
            String messError = jedisCacheService.getMessageErrorByKey("crm.total.price.is.wrong") + ": " + totalCharge;
            throw new EtcException(messError);
        }
        Date date = new Date(System.currentTimeMillis());
        SaleOrderEntity saleOrderEntity = addSaleOrder(addSupOfferRequest, userLogin, date, customerId, contractId);
        addSaleOrderDetail(saleOrderEntity.getSaleOrderId(), userLogin, date, addSupOfferRequest, infoTickets);
        result.setBillingCode(saleOrderEntity.getSaleOrderId());
        return result;
    }


    /**
     * Them moi sale order
     *
     * @param addSupOfferRequest
     * @param userLogin
     * @param date
     * @param customerId
     * @param contractId
     * @return
     */
    public SaleOrderEntity addSaleOrder(AddSupOfferRequestDTO addSupOfferRequest, String userLogin, Date date, Long customerId, Long contractId) {
        SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setAmount(addSupOfferRequest.getAmount());
        saleOrderEntity.setContractId(contractId);
        saleOrderEntity.setCustId(customerId);
        saleOrderEntity.setContractNo(addSupOfferRequest.getContractNo());
        saleOrderEntity.setCreateDate(date);
        saleOrderEntity.setCreateUser(userLogin);
        saleOrderEntity.setQuantity(addSupOfferRequest.getQuantity());
        saleOrderEntity.setSaleOrderDate(date);
        saleOrderEntity.setStatus(SaleOrderEntity.Status.NEW_ORDER.value);
        saleOrderEntity.setSaleOrderType(SaleOrderEntity.SaleOrderType.CHARGE_TICKET.value);
        saleOrderEntity.setSaleOrderSource(SaleOrderEntity.SaleOrderSource.VTP.value);
        saleOrderEntity.setMethodRechargeId(2L);
        saleOrderEntity.setPaymentMethodId(2L);

        return saleOrderRepositoryJPA.save(saleOrderEntity);
    }

    /**
     * Them moi sale order detail
     *
     * @param saleOrderId
     * @param userLogin
     * @param date
     * @param addSupOfferRequest
     */
    private void addSaleOrderDetail(Long saleOrderId, String userLogin, Date date, AddSupOfferRequestDTO addSupOfferRequest, ServicePlanFeeDTO servicePlanFee) {
        LOGGER.info("Method: addSaleOrderDetail: addSupOfferRequest, body ={} ", addSupOfferRequest);
        LOGGER.info("Method: addSaleOrderDetail: servicePlanFee, body ={} ", servicePlanFee);
        List<SaleOrderDetailEntity> listResult = new ArrayList<>();
        for (int i = 0; i < addSupOfferRequest.getList().size(); i++) {
            VehicleAddSuffOfferDTO vehicleAddSupOfferViettelPayDTO = addSupOfferRequest.getList().get(i);
            SaleOrderDetailEntity saleOrderDetailEntity = new SaleOrderDetailEntity();
            saleOrderDetailEntity.setSaleOrderId(saleOrderId);
            saleOrderDetailEntity.setCreateDate(date);
            saleOrderDetailEntity.setCreateUser(userLogin);
            saleOrderDetailEntity.setQuantity(1L);
            saleOrderDetailEntity.setSaleOrderDate(date);
            saleOrderDetailEntity.setOfferLevel(String.valueOf(Constants.OFFER_LEVEL_DEFAULT));
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getAutoRenew())) {
                saleOrderDetailEntity.setAutoRenew(vehicleAddSupOfferViettelPayDTO.getAutoRenew());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getAutoRenew())) {
                saleOrderDetailEntity.setAutoRenewVtp(vehicleAddSupOfferViettelPayDTO.getAutoRenew());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getEffDate())) {
                String effDate = FnCommon.convertDateToStringOther(vehicleAddSupOfferViettelPayDTO.getEffDate(), Constants.COMMON_DATE_FORMAT);
                saleOrderDetailEntity.setEffDate(FnCommon.convertStringToDate(effDate, Constants.COMMON_DATE_FORMAT));
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getExpDate())) {
                String expDate = FnCommon.convertDateToStringOther(vehicleAddSupOfferViettelPayDTO.getExpDate(), Constants.COMMON_DATE_FORMAT);
                saleOrderDetailEntity.setExpDate(FnCommon.convertStringToDate(expDate, Constants.COMMON_DATE_FORMAT));
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getPlateNumber())) {
                saleOrderDetailEntity.setPlateNumber(vehicleAddSupOfferViettelPayDTO.getPlateNumber());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getStageId())) {
                saleOrderDetailEntity.setStageId(vehicleAddSupOfferViettelPayDTO.getStageId());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getStationId())) {
                saleOrderDetailEntity.setStationId(vehicleAddSupOfferViettelPayDTO.getStationId());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getVehiclesGroupId())) {
                saleOrderDetailEntity.setVehicleGroupId(vehicleAddSupOfferViettelPayDTO.getVehiclesGroupId());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getVehiclesGroupId())) {
                saleOrderDetailEntity.setVehicleId(vehicleAddSupOfferViettelPayDTO.getVehiclesGroupId());
            }
            if (Objects.nonNull(vehicleAddSupOfferViettelPayDTO.getServicePlanTypeId())) {
                saleOrderDetailEntity.setServicePlanTypeId(vehicleAddSupOfferViettelPayDTO.getServicePlanTypeId());
            }
            VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(vehicleAddSupOfferViettelPayDTO.getVehicleId());
            if (!FnCommon.isNullObject(vehicleEntity)) {
                saleOrderDetailEntity.setEpc(vehicleEntity.getEpc());
                saleOrderDetailEntity.setTid(vehicleEntity.getTid());
                saleOrderDetailEntity.setRfidSerial(vehicleEntity.getRfidSerial());
            }
            ServicePlanDTO ticketInfo = servicePlanFee.getListServicePlan().get(i);
            if (!FnCommon.isNullObject(ticketInfo)) {
                saleOrderDetailEntity.setOcsCode(ticketInfo.getOcsCode());
                saleOrderDetailEntity.setPrice(ticketInfo.getFee());
                if (!FnCommon.isNullObject(ticketInfo.getScope())) {
                    saleOrderDetailEntity.setScope(ticketInfo.getScope().toString());
                }
                saleOrderDetailEntity.setServicePlanId(ticketInfo.getServicePlanId());
            }
            listResult.add(saleOrderDetailEntity);
        }
        LOGGER.info("Method: addSaleOrderDetail: lstSaleOrderDetailEntity, body ={} ", listResult);
        saleOrderDetailRepositoryJPA.saveAll(listResult);
    }


    /**
     * API goi dau noi len OCS thuc hien nap tien
     * (App se goi API nay sau khi nhan thong bao tu phia VTP)
     *
     * @return ResponseCallOcsFromApp
     */
    @Override
    @Transactional(rollbackFor = EtcException.class)
    public ResponseCallOcsFromApp callOcsToChargeTicket(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication,
                                                        Long customerId, Long contractId, Long orderId) throws Exception {
        LOGGER.info("VTPApp-callOcsToChargeTicket , addSupOfferRequest: body={}", addSupOfferRequest);
        // call OCS de nap tien
        if (!addSupOfferRequest.isAcountETC()) {
            throw new EtcException("crm.account.is.wrong");
        }
        ContractEntity contractEntity = contractRepositoryJPA.findByContractId(contractId);
        validateChargeTicket(addSupOfferRequest, contractEntity);
        String userLogin = FnCommon.getUserLogin(authentication);
        ResponseCallOcsFromApp responseCallOcsFromApp = new ResponseCallOcsFromApp();
        List<ServicePlanDTO> listTicket = new ArrayList<>();
        for (int i = 0; i < addSupOfferRequest.getList().size(); i++) {
            VehicleAddSuffOfferDTO vehicleAddSubOfferDTO = addSupOfferRequest.getList().get(i);
            ServicePlanDTO ticket = toServicePlanDTO(vehicleAddSubOfferDTO);
            ticket.setVehicleId(vehicleAddSubOfferDTO.getVehicleId());
            listTicket.add(ticket);
        }
        RequestGetFeeChargeTicketDTO requestGetFeeChargeTicket = new RequestGetFeeChargeTicketDTO();
        requestGetFeeChargeTicket.setServicePlanDTOList(listTicket);
        ServicePlanFeeDTO infoTickets = servicePlanService.getFeeNew(authentication, requestGetFeeChargeTicket);
        if (!FnCommon.isNullOrEmpty(infoTickets.getServicePlanVehicleDuplicate())) {
            String messError = jedisCacheService.getMessageErrorByKey("crm.price.is.exist") + ": " + infoTickets.getServicePlanVehicleDuplicate();
            throw new EtcException(messError);
        }
        Long totalCharge = getTotalCharge(infoTickets.getListServicePlan());
        if (!totalCharge.equals(addSupOfferRequest.getAmount())) {
            String messError = jedisCacheService.getMessageErrorByKey("crm.total.price.is.wrong") + ": " + totalCharge;
            throw new EtcException(messError);
        }
        if (addSupOfferRequest.isAcountETC()) {
            chargeETCNew(addSupOfferRequest, authentication, contractId, contractEntity, customerId, userLogin, infoTickets, responseCallOcsFromApp);
        }
        Optional<SaleOrderEntity> saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(orderId, SaleOrderEntity.Status.NEW_ORDER.value);
        if (saleOrderEntity.isPresent()) {
            if (responseCallOcsFromApp.getAmountSuccess() > 0) {
                saleOrderEntity.get().setStatus(SaleOrderEntity.Status.SUCCESS_PAYMENT.value);
                saleOrderServiceJPA.save(saleOrderEntity.get());
            } else {
                saleOrderEntity.get().setStatus(SaleOrderEntity.Status.CANCELED.value);
                saleOrderServiceJPA.save(saleOrderEntity.get());
            }
        }

        return responseCallOcsFromApp;
    }

    /**
     * Luong chinh thuc hien mua ve
     *
     * @param addSupOfferRequest
     * @param authentication
     * @param contractId
     * @param contractEntity
     * @param custId
     * @param userLogin
     * @throws Exception
     */
    private void chargeETCNew(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, long contractId,
                              ContractEntity contractEntity, long custId, String userLogin, ServicePlanFeeDTO servicePlanFee,
                              ResponseCallOcsFromApp responseCallOcsFromApp) throws Exception {
        Long actTypeId = addSupOfferRequest.getActTypeId();
        Long reasonId = getReasonIdByactTypeId(actTypeId);
        // Luu log vao bang sale_trans
        SaleTransEntity saleTransEntity = ticketServiceImpl.insertSaleTrans(contractEntity, addSupOfferRequest, authentication, contractId, custId);
        List<ServicePlanDTO> listSuccess = new ArrayList<>();
        List<ServicePlanDTO> listFail = new ArrayList<>();
        Long totalCharge = getTotalCharge(servicePlanFee.getListServicePlan());
        addSupOfferRequest.setAmount(totalCharge);
        for (int i = 0; i < addSupOfferRequest.getList().size(); i++) {
            // validate start end
            VehicleAddSuffOfferDTO vehicleToChargeTicket = addSupOfferRequest.getList().get(i);
            ServicePlanDTO ticketInfo = servicePlanFee.getListServicePlan().get(i);
            if (vehicleToChargeTicket.getChargeMethodId() == 1L) {
                vehicleToChargeTicket.getEffDate().setDate(1);
            }
            if (!validateBeforeCharge(vehicleToChargeTicket)) {
                listFail.add(ticketInfo);
                continue;
            }
            // Thuc hien dang ki goi cuoc phu cho tung phuong tien
            VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(vehicleToChargeTicket.getVehicleId());
            ticketServiceImpl.addOffer(vehicleToChargeTicket, ticketInfo, authentication, addSupOfferRequest,
                    contractId, userLogin, saleTransEntity, listSuccess, listFail, actTypeId, reasonId, custId,
                    vehicleToChargeTicket.getVehicleId(), vehicleEntity);
            //  Thuc hien roll-back trong truong hop mua ve ben Boo2 that bai
            List<ServicePlanDTO> listBoo2Fail = new ArrayList<>();
            listFail.forEach(item -> {
                if (Constants.BOO2.equals(item.getBooCode())) {
                    listBoo2Fail.add(item);
                }
            });
            List<ServicePlanDTO> listBoo2Success = new ArrayList<>();
            if (!FnCommon.isNullOrEmpty(listBoo2Fail)) {
                listSuccess.forEach(item -> {
                    if (Constants.BOO2.equals(item.getBooCode())) {
                        listBoo2Success.add(item);
                    }
                });
            }
            if (!FnCommon.isNullOrEmpty(listBoo2Success)) {
                listBoo2Success.forEach(item -> {
                    listFail.add(item);
                    ocsService.deleteSupOffer(authentication, actTypeId, contractId, item.getEpc(), item.getOcsCode(), "2");
                });
            }
            listSuccess.removeIf(item -> Constants.BOO2.equals(item.getBooCode()));
        }
        boolean isSaleTrans = ticketServiceImpl.updateTicketFail(listFail, saleTransEntity, authentication,
                addSupOfferRequest.getActTypeId(), contractId);
        if (isSaleTrans) {
            ticketServiceImpl.writeLogNew(authentication, reasonId, actTypeId, contractId, custId, null,
                    saleTransEntity.getSaleTransId(), saleTransEntity, SaleTransEntity.class.getAnnotation(Table.class).name());
        }
        long totalAmountSuccess = 0L;
        long totalAmountFailed = 0L;
        if (!FnCommon.isNullOrEmpty(listSuccess)) {
            for (ServicePlanDTO item : listSuccess) {
                totalAmountSuccess += item.getFee();
            }
        }
        if (!FnCommon.isNullOrEmpty(listFail)) {
            for (ServicePlanDTO item : listSuccess) {
                totalAmountFailed += item.getFee();
            }
        }
        responseCallOcsFromApp.setListSuccess(listSuccess);
        responseCallOcsFromApp.setListFail(listFail);
        responseCallOcsFromApp.setAmountFailed(totalAmountFailed);
        responseCallOcsFromApp.setAmountSuccess(totalAmountSuccess);
    }

    /**
     * Validate effDate va expDate
     *
     * @param vehicleToChargeTicket
     * @return
     */
    private boolean validateBeforeCharge(VehicleAddSuffOfferDTO vehicleToChargeTicket) {
        boolean isAccepted = true;
        if (!FnCommon.validateDateChargeTicket(vehicleToChargeTicket.getChargeMethodId(),
                FnCommon.convertToTicketType(vehicleToChargeTicket.getServicePlanTypeId().toString()),
                FnCommon.convertDateToStringOther(vehicleToChargeTicket.getEffDate(), Constants.COMMON_DATE_FORMAT_BOO),
                FnCommon.convertDateToStringOther(vehicleToChargeTicket.getExpDate(), Constants.COMMON_DATE_FORMAT_BOO), Constants.COMMON_DATE_FORMAT_BOO)
        ) {
            isAccepted = false;
        }
        if (vehicleToChargeTicket.getExpDate().before(new java.util.Date())) {
            isAccepted = false;
        }
        if (FnCommon.isNullOrEmpty(vehicleToChargeTicket.getBooCode())) {
            isAccepted = false;
        }
        return isAccepted;
    }

    /**
     * Lay ma ly do theo ma tac dong
     *
     * @param actTypeId
     * @return
     */
    private Long getReasonIdByactTypeId(Long actTypeId) {
        long reasonId = 0L;
        List<ActReasonEntity> list = actReasonRepositoryJPA.findAllByActTypeId(actTypeId);
        if (list != null && list.size() > 0) {
            ActReasonEntity actReasonEntity = list.get(0);
            reasonId = actReasonEntity.getActReasonId();
        }
        return reasonId;
    }


    /***
     * Lay thong tin mua ve va dang ky gia han ve qua sdk (luong rieng)
     * @param authentication
     * @param billingCode
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public Object getInfoSubscriptionsExtendedViaSDKPrivateStream(Authentication authentication, String billingCode) throws JsonProcessingException {
        StatusDTO statusDTO = null;
        Object infoTicketPurchase = vtPayRepository.getInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream(billingCode);
        if (FnCommon.isNullObject(infoTicketPurchase)) {
            statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.NOT_EXIST, MESSAGE_ERROR_NOT_EXIST);
            return new ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtened(statusDTO, null);
        }
        if (!FnCommon.isNullObject(infoTicketPurchase)) {
            Gson gson = new Gson();
            List<DataTypeDTO.DataType> dataTicketTypes = ticketService.getTicketTypes(FnCommon.getStringToken(authentication));
            List<DataTypeDTO.DataType> dataPlateTypes = this.getPlateTypes(FnCommon.getStringToken(authentication));
            Map<Long, String> mapTicketTypes = dataTicketTypes.stream().collect(Collectors.toMap(DataTypeDTO.DataType::getId, DataTypeDTO.DataType::getVal));
            Map<Long, String> mapPlateTypes = dataPlateTypes.stream().collect(Collectors.toMap(DataTypeDTO.DataType::getId, DataTypeDTO.DataType::getVal));

            ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO response = gson.fromJson(gson.toJson(infoTicketPurchase), ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.class);
            Object tickets = vtPayRepository.getTicketExtendedViaSDKPrivateStream(billingCode);
            if (!FnCommon.isNullObject(tickets)) {
                ObjectMapper mapper = new ObjectMapper();
                String json = gson.toJson(tickets);
                List<ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.TicketOrder> responseTickets = mapper.readValue(json, new TypeReference<List<ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.TicketOrder>>() {
                });
                StationBooDTO stationName = null;
                StationBooDTO stationNameOut = null;
                if (responseTickets != null) {
                    for (ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.TicketOrder ticketOrder : responseTickets) {
                        if (ticketOrder.getTicketType() != null) {
                            ticketOrder.setTicketType(mapTicketTypes.get(Long.parseLong(ticketOrder.getTicketType())));
                        }
                        if (ticketOrder.getPlateTypeId() != null) {
                            ticketOrder.setPlateType(mapPlateTypes.get(Long.parseLong(ticketOrder.getPlateTypeId())));
                        }
                        if (ticketOrder.getStageId() != null) {
                            StageBooDTO stageDTO = this.getStageById(FnCommon.getStringToken(authentication), String.valueOf(ticketOrder.getStageId()));

                            if (stageDTO.getData() != null) {
                                ticketOrder.setStationCode(stageDTO.getData().getStation_input_id());
                                ticketOrder.setStationCodeOut(stageDTO.getData().getStation_output_id());
                                stationName = this.getStationById(FnCommon.getStringToken(authentication), String.valueOf(stageDTO.getData().getStation_input_id()));
                                stationNameOut = this.getStationById(FnCommon.getStringToken(authentication), String.valueOf(stageDTO.getData().getStation_output_id()));
                                ticketOrder.setStationName(stationName.getData().getName());
                                ticketOrder.setStationNameOut(stationNameOut.getData().getName());
                            }
                        } else {
                            stationName = this.getStationById(FnCommon.getStringToken(authentication), String.valueOf(ticketOrder.getStationId()));
                            ticketOrder.setStationCode(ticketOrder.getStationId());
                            ticketOrder.setStationName(stationName.getData().getName());
                        }
                    }
                }
                response.setTickes(responseTickets);
                statusDTO = new StatusDTO().responseMessage(StatusDTO.StatusCode.SUCCESS, MESSAGE_INFO_SUCCESS);
            }
            return new ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtenedPrivateStream(statusDTO, response);
        }
        return new ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtenedPrivateStream(statusDTO, null);
    }


    /***
     * lay thong tin cua tram
     * @param token
     * @return
     */
    private StationBooDTO getStationById(String token, String stationId) {
        String requestUrl = stationUrl.replace("{stationId}", stationId);
        String stationResponse = FnCommon.doGetRequest(requestUrl, null, token);
        StationBooDTO station = new Gson().fromJson(stationResponse, StationBooDTO.class);
        return station;
    }

    /**
     * Lay du lieu trong bang saleOrderDetail
     *
     * @param autoRenewVtpDTO
     * @param authentication
     * @return
     */
    public List<SaleOrderDetailEntity> getSaleOrderDetail(AutoRenewVtpDTO autoRenewVtpDTO, Authentication authentication) {
        List<SaleOrderDetailEntity> saleOrderDetailEntities = new ArrayList<>();
        if (!Objects.isNull(autoRenewVtpDTO.getStationInId()) && Objects.isNull(autoRenewVtpDTO.getStationOutId())) {
            saleOrderDetailEntities = saleOrderDetailRepository.findByServicePlanTypeIdAndPlateNumberAndStationIdAndEffDateAndExpDate(autoRenewVtpDTO.getTicket_type(), autoRenewVtpDTO.getPlateNumber(),
                    autoRenewVtpDTO.getStationInId(), autoRenewVtpDTO.getEffDate(), autoRenewVtpDTO.getExpDate());
        } else if (!Objects.isNull(autoRenewVtpDTO.getStationInId()) && !Objects.isNull(autoRenewVtpDTO.getStationOutId())) {
            String token = FnCommon.getStringToken(authentication);
            LinkedHashMap<?, ?> stage = stageService.findByStationInAndStationOut(autoRenewVtpDTO.getStationInId(),
                    autoRenewVtpDTO.getStationOutId(), token);
            if (!Objects.isNull(stage) && !FnCommon.isNullOrEmpty(stage.get("id").toString())) {
                saleOrderDetailEntities = saleOrderDetailRepository.findAllByServicePlanTypeIdAndPlateNumberAndStageIdAndEffDateAndExpDate(
                        autoRenewVtpDTO.getTicket_type(), autoRenewVtpDTO.getPlateNumber(),
                        Long.parseLong(stage.get("id").toString()), autoRenewVtpDTO.getEffDate(), autoRenewVtpDTO.getExpDate());
            }
        }
        return saleOrderDetailEntities;
    }

    /**
     * Goi ocs xu ly du lieu khi tu dong gia han/ huy gia han tu dong
     */
    public Long handleOcsAutoRenewVtp(List<SaleOrderDetailEntity> result, Authentication authentication, String isCancel) {
        Long contractId = null;
        for (SaleOrderDetailEntity saleOrderDetailEntityOcs : result) {
            Optional<SaleOrderEntity> saleOrderEntity = saleOrderServiceJPA.findById(saleOrderDetailEntityOcs.getSaleOrderId());
            if (saleOrderEntity.isPresent()) {
                contractId = saleOrderEntity.get().getContractId();
                VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO = new VehicleAddSuffOfferDTO().toEntityChangeSupOffer(saleOrderDetailEntityOcs);
                try {
                    ocsService.changeSupOffer(vehicleAddSuffOfferDTO, authentication, saleOrderEntity.get().getContractId(),
                            1L, saleOrderEntity.get().getCreateUser(), isCancel);
                } catch (Exception ex) {
                    LOGGER.error("Loi trong qua trinh thuc hien", ex);
                }
            }
        }
        return contractId;
    }

    /***
     * Luu thong tin nop tien vao bang topup ETC (luong moi, nap tien cho hop dong khac)
     * @param saleOrder
     * @param balanceBefore
     * @param authentication
     * @return
     */
    private TopupEtcEntity saveToupEtc(SaleOrderEntity saleOrder, long balanceBefore, String transId, Authentication authentication) {
        TopupEtcEntity etcEntity = new TopupEtcEntity();
        etcEntity.setTopupCode("V");
        etcEntity.setTopupChannel(TopupEtcEntity.TopupChannel.VTP_CHANNEL.value);
        etcEntity.setTopupDate(new Date(System.currentTimeMillis()));
        etcEntity.setContractId(saleOrder.getContractId());
        etcEntity.setSourceContractId(saleOrder.getSourceContractId());
        etcEntity.setSourceContractNo(saleOrder.getSourceContractNo());
        etcEntity.setAmount(saleOrder.getAmount());
        etcEntity.setTopupType(TopupEtcEntity.TopupType.TOPUP_VIETTELPAY.value);
        etcEntity.setStatus(TopupEtcEntity.Status.SUCCESS.value);
        etcEntity.setCreateUser(saleOrder.getContractNo());
        etcEntity.setCreateDate(new Date(System.currentTimeMillis()));
        etcEntity.setBalanceBefore(balanceBefore);
        etcEntity.setBalanceAfter(balanceBefore + saleOrder.getAmount());
        etcEntity.setStaffCode(saleOrder.getContractNo());
        etcEntity.setStaffName(saleOrder.getContractNo());
        etcEntity.setTopupPayer(saleOrder.getContractNo());
        etcEntity.setVolumeNo(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        etcEntity.setNo((long) topupEtcServiceJPA.findAll().size());
        etcEntity.setSaleOrderDate(new Date(System.currentTimeMillis()));
        etcEntity.setSaleOrderId(saleOrder.getSaleOrderId());
        etcEntity.setTransId(transId);
        return topupEtcServiceJPA.save(etcEntity);
    }

    /**
     * Cap nhat status goi ws fail
     *
     * @param wsAuditEntity
     */
    private void updateWriteLogFail(WsAuditEntity wsAuditEntity) {
        try {
            if (wsAuditEntity.getWsAuditId() != null) {
                wsAuditEntity.setStatus(WsAuditEntity.Status.NOT_SUCCESS.value);
                wsAuditRepositoryJPA.save(wsAuditEntity);
            }
        } catch (Exception e) {
            LOGGER.error("Update log fail", e);
        }

    }

    private String buildLogRequest(Request request, String body) {
        if (Strings.isBlank(body)) {
            body = "{}";
        }
        return "Request{method=" + request.method() + ", url=" + request.url() + ", body=" + body + ", tag=" + (request.tag() != request ? request.tag() : null) + '}';
    }

    /**
     * Check validate mua ve
     *
     * @param addSupOfferRequest
     * @param contractEntity
     */
    private void validateChargeTicket(AddSupOfferRequestDTO addSupOfferRequest, ContractEntity contractEntity) {
        List<String> listError = new ArrayList<>();
        if (Objects.isNull(contractEntity)) {
            listError.add(jedisCacheService.getMessageErrorByKey("crm.contract.not.exist"));
        }

        if (addSupOfferRequest.getActTypeId() == null) {
            listError.add(jedisCacheService.getMessageErrorByKey("validate.act.type.require"));
        }
        if (listError.size() > 0) {
            throw new EtcException(listError.get(0));
        }
    }

    /**
     * Set gia tri service plan DTO
     *
     * @param vehicleAddSuffOfferDTO
     * @return
     */
    private ServicePlanDTO toServicePlanDTO(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO) {
        ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getStageId())) {
            servicePlanDTO.setStageId(vehicleAddSuffOfferDTO.getStageId());
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getStationId())) {
            servicePlanDTO.setStationId(vehicleAddSuffOfferDTO.getStationId());
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getLaneOut())) {
            servicePlanDTO.setLaneOut(vehicleAddSuffOfferDTO.getLaneOut());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getChargeMethodId())) {
            servicePlanDTO.setChargeMethodId(vehicleAddSuffOfferDTO.getChargeMethodId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getServicePlanTypeId())) {
            servicePlanDTO.setServicePlanTypeId(vehicleAddSuffOfferDTO.getServicePlanTypeId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getVehiclesGroupId())) {
            servicePlanDTO.setVehicleGroupId(vehicleAddSuffOfferDTO.getVehiclesGroupId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getAutoRenew())) {
            servicePlanDTO.setAutoRenew(Long.parseLong(vehicleAddSuffOfferDTO.getAutoRenew()));
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getEffDate())) {
            servicePlanDTO.setCreateDateFrom(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getEffDate(), Constants.COMMON_DATE_FORMAT));
            servicePlanDTO.setEffDate(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getEffDate(), Constants.COMMON_DATE_FORMAT));
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getExpDate())) {
            servicePlanDTO.setCreateDateTo(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getExpDate(), Constants.COMMON_DATE_FORMAT));
            servicePlanDTO.setExpDate(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getExpDate(), Constants.COMMON_DATE_FORMAT));
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getPlateNumber())) {
            servicePlanDTO.setPlateNumber(vehicleAddSuffOfferDTO.getPlateNumber());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getBooCode())) {
            servicePlanDTO.setBooCode(vehicleAddSuffOfferDTO.getBooCode());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getStationType())) {
            servicePlanDTO.setStationType(vehicleAddSuffOfferDTO.getStationType());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getVehicleTypeId())) {
            servicePlanDTO.setVehicleTypeId(vehicleAddSuffOfferDTO.getVehicleTypeId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getNetWeight())) {
            servicePlanDTO.setNetWeight(vehicleAddSuffOfferDTO.getNetWeight());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getCargoWeight())) {
            servicePlanDTO.setCargoWeight(vehicleAddSuffOfferDTO.getCargoWeight());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getSeatNumber())) {
            servicePlanDTO.setSeatNumber(vehicleAddSuffOfferDTO.getSeatNumber());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getEpc())) {
            servicePlanDTO.setEpc(vehicleAddSuffOfferDTO.getEpc());
        }

        return servicePlanDTO;
    }

    /**
     * Lay tong phi mua ve
     *
     * @param list
     * @return
     */
    private long getTotalCharge(List<ServicePlanDTO> list) {
        Long totalCharge = 0l;
        for (ServicePlanDTO servicePlanDTO : list) {
            totalCharge = servicePlanDTO.getFee() + totalCharge;
        }
        return totalCharge;
    }

    @Override
    public Object createSaleOrder(RequestAddSupOfferDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId) {
        // Neu co truyen desContract -> nap ho, khong phai thi nap tai khoan ca nhan
        Long contractIdDes = addSupOfferRequest.getDesContractId() != null ? addSupOfferRequest.getDesContractId() : contractId;

        //Luu lai sale order trong bang SALE_ORDER
        ContractDTO desContractDTO = contractService.getOCSInfo(authentication, contractIdDes);
        Date date = new Date(System.currentTimeMillis());
        SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderDate(date);
        saleOrderEntity.setSaleOrderType(SaleOrderEntity.SaleOrderType.ADD_MONEY.value);
        saleOrderEntity.setSaleOrderSource(SaleOrderEntity.SaleOrderSource.VTP.value);
        saleOrderEntity.setStatus(SaleOrderEntity.Status.NEW_ORDER.value);
        saleOrderEntity.setAmount(addSupOfferRequest.getAmount());
        saleOrderEntity.setQuantity(addSupOfferRequest.getQuantity());
        saleOrderEntity.setCustId(customerId);
        saleOrderEntity.setContractId(contractIdDes); // destination contractId
        saleOrderEntity.setContractNo(desContractDTO.getContractNo()); // destination contractNo
        saleOrderEntity.setSourceContractId(contractId); // source contractId
        saleOrderEntity.setSourceContractNo(addSupOfferRequest.getContractNo()); // source contractNo
        saleOrderEntity.setCreateDate(date);
        saleOrderEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        saleOrderEntity = saleOrderRepositoryJPA.save(saleOrderEntity);

        //Response cho APP
        ResAddSupOfferDTO result = new ResAddSupOfferDTO();
        String tempStr = vtpAccessCode + saleOrderEntity.getSaleOrderId() + Constants.VIETTEL_PAY_SERVICE.COMMAND + merchanCodeViettelPay + saleOrderEntity.getSaleOrderId() + addSupOfferRequest.getAmount() + version;
        String checkSum = FnCommon.calculateRFC2104HMAC(tempStr, vtpHashKey);
        result.setBillingCode(saleOrderEntity.getSaleOrderId());
        result.setVtpCheckSum(checkSum);
        return result;
    }

    /***
     *  * Xác thực thông tin từ VTP thỏa mãn điều kiện nộp tiền vào tài khoản giao thông trên App chủ phương tiện
     *  * Xác thực thông tin hóa đơn khi mua ve thang/quy qua cong thanh toan
     * @param req
     * @param authentication
     * @return
     */
    @Override
    public ResponseViettelPayDTO.VerifyViettelPayData verifySaleOrder(ViettelPayRequestDTO req, Authentication authentication) {
        String billCode = !FnCommon.isNullOrEmpty(req.getBillcode()) ? req.getBillcode().trim() : "";
        String merchantCode = !FnCommon.isNullOrEmpty(req.getMerchant_code()) ? req.getMerchant_code().trim() : "";
        String orderId = !FnCommon.isNullOrEmpty(req.getOrder_id()) ? req.getOrder_id().trim() : "";
        String errorCode = "";
        String transAmount = "";

        StringBuilder raw = new StringBuilder();
        raw.append(vtpAccessCode).append(billCode).append(merchantCode).append(orderId);

        Optional<SaleOrderEntity> saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(Long.parseLong(req.getOrder_id()), SaleOrderEntity.Status.NEW_ORDER.value);
        if (saleOrderEntity.isPresent()) {
            transAmount = saleOrderEntity.get().getAmount().toString();
            raw.append(transAmount);

            String checkSumVTPay = FnCommon.urlDecodeCheckSumString(req.getCheck_sum());
            String checkSumCRM = FnCommon.calculateRFC2104HMAC(raw.toString(), vtpHashKey);
            LOGGER.info("Kiem tra checksum VTPAY gui co khop vs checksum cua CRM khong checkSumVTPay={},checkSumCRM={}", checkSumVTPay, checkSumCRM);
            // Khop check sum thi tra ve thanh cong, nguoc lai thi tra ve loi
            if (checkSumVTPay.equals(checkSumCRM)) {
                LOGGER.info("Kiem tra checksum OK");
                errorCode = ResponseViettelPayDTO.VerifyViettelPayData.ErrorCode.SUCCESS.value;
            } else {
                LOGGER.info("Kiem tra checksum FAILED");
                errorCode = ResponseViettelPayDTO.VerifyViettelPayData.ErrorCode.INVALID_CHECK_SUM.value;
            }
        } else {
            errorCode = ResponseViettelPayDTO.VerifyViettelPayData.ErrorCode.INVALID_DATA.value;
        }

        String tempStr = vtpAccessCode + billCode + errorCode + merchantCode + orderId + transAmount;
        String checkSum = FnCommon.calculateRFC2104HMAC(tempStr, vtpHashKey);
        ResponseViettelPayDTO.VerifyViettelPayData data = new ResponseViettelPayDTO.VerifyViettelPayData(billCode, merchantCode, orderId, transAmount, errorCode, checkSum);
        LOGGER.info("Tra ve ket qua verify response={}", data);
        writeLog(req.toString(), data.toString(), "", "127.0.0.1", 0, Constants.ACT_TYPE.RESULT_SALE_ORDER, authentication, "POST", WsAuditEntity.Status.SUCCESS.value);
        return data;
    }


    /***
     * Trả kết quả nộp tiền vào tài khoản giao thông trên App chủ phương tien tu VTPAY
     * @param req
     * @param authen
     * @return
     */
    @Override
    public ResponseViettelPayDTO.ResponseAddMoneyResultData resultSaleOrder(ViettelPayRequestDTO req, Authentication authen) {
        String orderId = req.getOrder_id();
        String merchantCode = !FnCommon.isNullOrEmpty(req.getMerchant_code()) ? req.getMerchant_code().trim() : merchanCodeViettelPay;
        String errorCode = ResponseViettelPayDTO.ResponseAddMoneyResultData.ErrorCode.FAIL.value;

        try {
            Optional<SaleOrderEntity> saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(Long.parseLong(orderId), SaleOrderEntity.Status.NEW_ORDER.value);
            if (saleOrderEntity.isPresent()) {
                // Cap nhat trang thai PAYMENT_STATUS trong bang SALE_ODER cua VTPAY tra ve
                saleOrderEntity.get().setPayGateErrorCode(req.getError_code());
                saleOrderEntity.get().setPayGateStatus(req.getPayment_status());
                saleOrderServiceJPA.save(saleOrderEntity.get());

                if (SaleOrderEntity.PaymentCode.SUCCESS.value.equals(req.getError_code()) && SaleOrderEntity.PaymentStatus.SUCCESS.value.equals(req.getPayment_status())) {
                    // Goi API truy van ket qua giao dich ben phia VTP de dam bao OrderId da duoc thanh toan thanh cong
//                    VtpVerifyPaymentRequestDTO request = new VtpVerifyPaymentRequestDTO();
//                    request.setCmd(Constants.VIETTEL_PAY_SERVICE.CMD);
//                    request.setMerchant_code(merchanCodeViettelPay);
//                    request.setOrder_id(orderId);
//                    request.setVersion(version);

                    //rawString tao check_sum request: access_code + cmd + merchant_code + order_id + version
//                    String tempStr = vtpAccessCode + Constants.VIETTEL_PAY_SERVICE.CMD + merchanCodeViettelPay + orderId + version;
//                    String reqCheckSumCRM = FnCommon.calculateRFC2104HMAC(tempStr, vtpHashKey);
//                    request.setCheck_sum(reqCheckSumCRM);

//                    LOGGER.info("CRM call VTPAY verify SALE ORDER request={}", request);
//                    String response = doPostRequestVTP(Constants.VIETTEL_PAY_SERVICE.POST_REQUEST, vtpVerifyPaymentUrl, FnCommon.toStringJson(request), null, authen, Constants.ACT_TYPE.VERIFY_SALE_ORDER, 1);
//
//                    LOGGER.info("CRM call VTPAY verify SALE ORDER response={}", response);
//                    Type type = new TypeToken<VtpVerifyPaymentResponseDTO>() {
//                    }.getType();
//                    VtpVerifyPaymentResponseDTO resVerifyVTPAY = new Gson().fromJson(response, type);
//                    if (!FnCommon.isNullObject(resVerifyVTPAY)) {

                    //rawString de tao check_sum trong response tra ve: access_code + merchant_code + order_id + payment_status + version
//                        String tempCheckSum = vtpAccessCode + merchanCodeViettelPay + orderId + SaleOrderEntity.PaymentStatus.SUCCESS.value + version;
//                        String checkSumCRM = FnCommon.calculateRFC2104HMAC(tempCheckSum, vtpHashKey);
//                        String checkSumVTPAY = FnCommon.urlDecodeCheckSumString(resVerifyVTPAY.getCheck_sum());

                    // truong hop verify giao dich ben VTP da thanh cong
//                        if (SaleOrderEntity.PaymentCode.SUCCESS.value.equals(resVerifyVTPAY.getError_code()) && checkSumVTPAY.equals(checkSumCRM)) {
//                            saleOrderEntity.get().setVerifyStatus(SaleOrderEntity.VerifyStatus.SUCCESS.value);
//                            saleOrderServiceJPA.save(saleOrderEntity.get());

                    Long desContractId = saleOrderEntity.get().getContractId();
                    long amount = saleOrderEntity.get().getAmount();
                    ContractDTO contractDTO = contractService.getOCSInfo(authen, desContractId);
                    if (!FnCommon.isNullObject(contractDTO)) {
                        // Goi dau noi nap tien tren OCS
                        OCSResponse result = ocsService.addBalanceNew(authen, Constants.ACT_TYPE.NAP_TIEN, desContractId, amount, orderId);
                        if (FnCommon.checkOcsCode(result)) {
                            // Luu them lucky code chuong trinh
                            luckyService.genLuckyCode(contractRepositoryJPA.getOne(desContractId), Constants.LUCKY_CODE.NAPTIEN, authen);

                            saleOrderEntity.get().setOcsStatus(SaleOrderEntity.OcsStatus.SUCCESS.value);
                            saleOrderEntity.get().setStatus(SaleOrderEntity.Status.SUCCESS_PAYMENT.value);
                            saleOrderServiceJPA.save(saleOrderEntity.get());

                            errorCode = ResponseViettelPayDTO.ResponseAddMoneyResultData.ErrorCode.SUCCESS.value;
                            TopupEtcEntity topupEtcEntity = saveToupEtc(saleOrderEntity.get(), contractDTO.getBalance(), req.getVt_transaction_id(), authen);
                            if (!FnCommon.isNullObject(topupEtcEntity)) {
                                topupEtcEntity.setTopupCode(topupEtcEntity.getTopupCode() + topupEtcEntity.getTopupEtcId());
                                topupEtcServiceJPA.save(topupEtcEntity);
                            }
                        } else {
                            saleOrderEntity.get().setOcsStatus(SaleOrderEntity.OcsStatus.FAILED.value);
                            saleOrderEntity.get().setStatus(SaleOrderEntity.Status.OCS_FAILED.value);
                            saleOrderServiceJPA.save(saleOrderEntity.get());
                        }
                    } else {
                        LOGGER.info("resultSaleOrder - Contract is not found, contractId={}", desContractId);
                    }
//                        } else {
//                            saleOrderEntity.get().setVerifyStatus(SaleOrderEntity.VerifyStatus.FAILED.value);
//                            saleOrderServiceJPA.save(saleOrderEntity.get());
//                        }
//                    } else {
//                        LOGGER.info("Call API verify payment VTP failed, response={}", resVerifyVTPAY);
//                        saleOrderEntity.get().setVerifyStatus(SaleOrderEntity.VerifyStatus.FAILED.value);
//                        saleOrderServiceJPA.save(saleOrderEntity.get());
//                    }
                }
            } else {
                LOGGER.debug("resultSaleOrder - Order is not found or status != 1, orderId={}", orderId);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("resultSaleOrder - OrderId is wrong format, orderId ={}", orderId, e);
        }

        String tempStr = vtpAccessCode + errorCode + merchantCode + orderId;
        String checkSum = FnCommon.calculateRFC2104HMAC(tempStr, vtpHashKey);
        ResponseViettelPayDTO.ResponseAddMoneyResultData data = new ResponseViettelPayDTO.ResponseAddMoneyResultData(errorCode, merchantCode, orderId, "", "", "", checkSum);
        LOGGER.info("resultSaleOrder response={}", data);

        writeLog(req.toString(), data.toString(), "", "127.0.0.1", 0, Constants.ACT_TYPE.RESULT_SALE_ORDER, authen, "POST", WsAuditEntity.Status.SUCCESS.value);
        return data;
    }

    /**
     * Gửi request tới cổng thanh toán ViettelPay.
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String doPostRequestVTP(String requestMethod, String url, String jsonData, Map<String, String> params, Authentication authentication, long actTypeId, int step) {
        String strRes = "", ip = null;
        long start = 0L, end = 0L;
        Request request = null;
        try {
            if (step <= Integer.parseInt(numberRetry)) {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(60, TimeUnit.SECONDS);
                client.setReadTimeout(60, TimeUnit.SECONDS);
                client.setWriteTimeout(60, TimeUnit.SECONDS);
                HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

                if (params != null) {
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                    }
                }
                RequestBody body = FnCommon.isNullOrEmpty(jsonData) ? null : RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), jsonData);
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method(requestMethod, body)
                        .build();
                Response response = client.newCall(request).execute();
                start = System.currentTimeMillis();
                end = System.currentTimeMillis() - start;
                strRes = response.body().string();
                writeLog(request.toString(), strRes, url, "127.0.0.1", end, actTypeId, authentication, requestMethod, WsAuditEntity.Status.SUCCESS.value);
                return strRes;
            }
        } catch (Exception e) {
            if (request != null) {
                writeLog(request.toString(), strRes, url, "127.0.0.1", end, actTypeId, authentication, requestMethod, WsAuditEntity.Status.NOT_SUCCESS.value);
                doPostRequestVTP(requestMethod, url, jsonData, params, authentication, actTypeId, ++step);
            }
            LOGGER.error("Has error", e);
        }
        return null;
    }

    /***
     * Do POST request
     * @param url
     * @param obj
     * @param timeOut
     * @param actType
     * @param step
     * @return
     */
    public String doPostRequest(String url, Object obj, String timeOut, Long actType, int step) {
        Request request = null;
        String strRes = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(obj));
        try {
            if (step <= Integer.parseInt(numberRetry)) {
                client.setConnectTimeout(Long.parseLong(timeOut), TimeUnit.SECONDS);
                client.setReadTimeout(Long.parseLong(timeOut), TimeUnit.SECONDS);
                client.setWriteTimeout(Long.parseLong(timeOut), TimeUnit.SECONDS);
                HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
                request = new Request.Builder()
                        .header("Accept", "application/json")
                        .url(httpBuilder.build())
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                strRes = response.body().string();
                writeLog(buildLogRequest(request, FnCommon.toStringJson(obj)), strRes, url, "127.0.0.1", 0, actType, null, "POST", WsAuditEntity.Status.SUCCESS.value);
                return strRes.replace("null", "\"\"");
            }

        } catch (Exception e) {
            LOGGER.error("Has error when calling API from " + url, e);
            if (request != null) {
                writeLog(buildLogRequest(request, FnCommon.toStringJson(obj)), strRes, url, "127.0.0.1", 0, actType, null, "POST", WsAuditEntity.Status.NOT_SUCCESS.value);
                doPostRequest(url, obj, timeOut, actType, ++step);
            }
        }
        return strRes;
    }
}
