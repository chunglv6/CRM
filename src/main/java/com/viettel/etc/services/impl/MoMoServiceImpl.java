package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.ContractDTO;
import com.viettel.etc.dto.momo.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.viettelpay.RequestAddSupOfferDTO;
import com.viettel.etc.dto.viettelpay.ResAddSupOfferDTO;
import com.viettel.etc.repositories.MoMoRepository;
import com.viettel.etc.repositories.ViettelPayRepository;
import com.viettel.etc.repositories.tables.SaleOrderRepositoryJPA;
import com.viettel.etc.repositories.tables.TopupEtcRepositoryJPA;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.SaleOrderEntity;
import com.viettel.etc.repositories.tables.entities.TopupEtcEntity;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.SaleOrderServiceJPA;
import com.viettel.etc.services.tables.TopupEtcServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.encoding.MomoEncoder;
import com.viettel.etc.utils.exceptions.EtcException;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MoMoServiceImpl implements MoMoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoMoServiceImpl.class);

    @Autowired
    SaleOrderRepositoryJPA saleOrderRepositoryJPA;

    @Autowired
    SaleOrderServiceJPA saleOrderServiceJPA;

    @Autowired
    OCSService ocsService;

    @Autowired
    TopupEtcServiceJPA topupEtcServiceJPA;

    @Autowired
    ContractService contractService;

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    @Autowired
    MoMoRepository moMoRepository;

    @Autowired
    ViettelPayRepository vtPayRepository;

    @Autowired
    TopupEtcRepositoryJPA topupEtcRepositoryJPA;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Value("${momo.version}")
    private String version;

    @Value("${momo.paytype}")
    private String payType;

    @Value("${momo.pub.key}")
    private String publicKey;

    @Value("${momo.secret.key}")
    private String secretKey;

    @Value("${ws.momo.url.pay.app}")
    private String payAppUrl;

    @Value("${ws.momo.url.pay.web}")
    private String payWebUrl;

    @Value("${momo.access.key}")
    private String accessKey;

    @Value("${momo.web.extraData}")
    private String extraDataWeb;

    @Value("${ws.momo.url.pay.confirm}")
    private String payConfirmUrl;

    @Value("${momo.ws.time-out}")
    private String timeOut;

    @Value("${ws.ocs.retry}")
    private String numberRetry;

    @Value("${momo.partner.code}")
    private String parnerCode;

    @Value("${ws.momo.url.api.notify.web}")
    private String urlNotifyWeb;

    @Value("${ws.ocs.query-contract}")
    private String wsQueryContract;

    @Autowired
    JedisCacheService jedisCacheService;

    @Autowired
    LuckyService luckyService;

    /***
     * Ham tra ve thong tin tim kiem phuong tien hoac hop dong tu MOMO APP goi sang
     * @param data
     * @param authentication
     * @return
     */
    @Override
    public Object findByPlateOrContract(MoMoGetContractRequestDTO data, Authentication authentication) {

        // validate input
        if (FnCommon.isNullOrEmpty(data.getRequestId()) || FnCommon.isNullOrEmpty(data.getSearchType())
                || (FnCommon.isNullOrEmpty(data.getContractNo()) && FnCommon.isNullOrEmpty(data.getPlateNumber()))
                || (MoMoGetContractRequestDTO.SearchType.CONTRACT_NO.value.equals(data.getSearchType()) && FnCommon.isNullOrEmpty(data.getContractNo()))
                || (MoMoGetContractRequestDTO.SearchType.PLATE_NUMBER.value.equals(data.getSearchType()) && FnCommon.isNullOrEmpty(data.getPlateNumber()))) {
            MoMoGetContractResponseDTO resultObj = new MoMoGetContractResponseDTO();
            resultObj.setErrorCode(MoMoGetContractResponseDTO.ErrorCode.INVALID_INPUT.value);
            resultObj.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.invalid.input"));
            return resultObj;
        }


        Object queryResult = null;
        if (MoMoGetContractRequestDTO.SearchType.CONTRACT_NO.value.equals(data.getSearchType())) {
            queryResult = moMoRepository.findByPlateOrContract(data.getSearchType(), data.getContractNo(), null, data.getPlateNumber());
        } else if (MoMoGetContractRequestDTO.SearchType.PLATE_NUMBER.value.equals(data.getSearchType())) {
            String plateNumber = data.getPlateNumber().toUpperCase();
            String plateTypeCode = FnCommon.getPlateTypeCodeFromPlateNumber(plateNumber);
            if (plateNumber.endsWith("T") || plateNumber.endsWith("X") || plateNumber.endsWith("V")) {
                plateNumber = plateNumber.substring(0, plateNumber.length() - 1);
            }
            queryResult = moMoRepository.findByPlateOrContract(data.getSearchType(), data.getContractNo(), plateTypeCode, plateNumber);
        }
        MoMoGetContractResponseDTO resultObj = null;
        if (!FnCommon.isNullObject(queryResult)) {
            resultObj = new MoMoGetContractResponseDTO().convertToDataDTO(queryResult);
        }
        if (resultObj != null && resultObj.getContractId() != null) {
            resultObj.setRequestTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
            resultObj.setRequestId(data.getRequestId());
            resultObj.setErrorCode(MoMoGetContractResponseDTO.ErrorCode.SUCCESS.value);
            Object lstVehicle = vtPayRepository.findAllVehicleByContractAndPlate(Long.parseLong(resultObj.getContractId()));
            resultObj.setLstVehicles(lstVehicle);
        } else {
            resultObj = new MoMoGetContractResponseDTO();
            resultObj.setRequestId(data.getRequestId());
            resultObj.setRequestTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
            resultObj.setErrorCode(MoMoGetContractResponseDTO.ErrorCode.CONTRACT_NO_NOT_FOUND.value);
            resultObj.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.contract.no.is.not.found"));
        }
        return resultObj;
    }

    /***
     * Xử lý yêu cầu nạp tiền cho hợp đồng giao thông số từ ví MoMo
     * @param req
     * @param authentication
     * @return
     */
    @Override
    public MoMoAppResponsePaymentDTO handleRequestAddMoneyFromMoMoApp(MoMoAppRequestPaymentDTO req, Authentication authentication) {
        LOGGER.info("BackEnd MoMo call API - /momo-app/add-balance BackEnd CRM, req={}", req);
        // Validate Input
        MoMoAppResponsePaymentDTO response = validateMoMoAppRequestPaymentData(req);
        if (!FnCommon.isNullOrEmpty(response.getErrorCode())) {
            response.setResponseTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
            LOGGER.info("BackEnd MoMo call API - /momo-app/add-balance BackEnd CRM, response={}", response);
            writeLog(req.toString(), response.toString(), "", "127.0.0.1", 0, Constants.ACT_TYPE.NAP_TIEN, authentication, "POST", WsAuditEntity.Status.SUCCESS.value);
            return response;
        }
        try {
            Long contractId = Long.parseLong(req.getContractId());
            List<TopupEtcEntity> lstTopUpEtcEntity = topupEtcRepositoryJPA.findByContractIdAndTransId(contractId, req.getRequestId());
            // Kiem tra giao dich nay da duoc xu ly truoc do hay chua, tranh truong hop cung mot thong tin giao dic nhung duoc request nhieu lan
            if (!FnCommon.isNullOrEmpty(lstTopUpEtcEntity)) {
                for (TopupEtcEntity item : lstTopUpEtcEntity) {
                    String dateCheck = FnCommon.convertDateToStringOther(item.getSaleOrderDate(), Constants.COMMON_DATE_TIME_FORMAT);
                    if (req.getRequestTime().trim().equals(dateCheck)) {
                        response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.PEYMENT_YET);
                        response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.payment.yet"));
                        response.setResponseTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
                        LOGGER.info("BackEnd MoMo call API - /momo-app/add-balance BackEnd CRM, response={}", response);
                        writeLog(req.toString(), response.toString(), "", "127.0.0.1", 0, Constants.ACT_TYPE.NAP_TIEN, authentication, "POST", WsAuditEntity.Status.SUCCESS.value);
                        return response;
                    }
                }
            }
            // Check thong tin hop dong va thuc hien nap tien tren OCS
            try {
                ContractDTO contractDTO = contractService.getOCSInfo(authentication, contractId);
                long balanceBefore = contractDTO.getBalance();
                OCSResponse result = ocsService.addBalance(authentication, Constants.ACT_TYPE.NAP_TIEN,
                        contractDTO.getContractId(), req.getAmount());
                LOGGER.info("BackEnd CRM call API - Add-Balance BackEnd OCS, ocsResponse={}", result);
                if (FnCommon.checkOcsCode(result)) {
                    // Luu them lucky code chuong trinh
                    luckyService.genLuckyCode(contractServiceJPA.getOne(contractId), Constants.LUCKY_CODE.NAPTIEN, authentication);

                    TopupEtcEntity topupEtcEntity = saveTopUpEtc(contractDTO, balanceBefore, req.getAmount(), req.getRequestTime(), req.getRequestId());
                    if (!FnCommon.isNullObject(topupEtcEntity)) {
                        topupEtcEntity.setTopupCode(topupEtcEntity.getTopupCode() + topupEtcEntity.getTopupEtcId());
                        topupEtcServiceJPA.save(topupEtcEntity);
                        response.setOrderId(topupEtcEntity.getTopupEtcId().toString());
                        response.setResponseTime(FnCommon.convertDateToStringOther(topupEtcEntity.getTopupDate(), Constants.COMMON_DATE_TIME_FORMAT));
                        response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.SUCCESS);
                        response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.payment.success"));
                        response.setExtraData("Thanh toan cho don hang " + topupEtcEntity.getTopupEtcId() + " qua MoMo");
                    }else{
                        response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.SERVICE_ERROR);
                        response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.service.error"));
                        response.setResponseTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
                    }
                } else {
                    response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.PAYMENT_FAILED);
                    response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.payment.failed"));
                    response.setResponseTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
                }
            }catch (EtcException e){
                LOGGER.info("BackEnd check contract info failed, contractId={}, e={}", contractId, e);
                response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.CONTRACT_ID_IS_NOT_FOUND);
                response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.contract.no.is.not.found"));
                response.setResponseTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
            }
        }catch (NumberFormatException e){
            response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.INPUT_INVALID);
            response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.invalid.input"));
            response.setResponseTime(FnCommon.convertDateToStringOther(new Date(System.currentTimeMillis()), Constants.COMMON_DATE_TIME_FORMAT));
            LOGGER.info("BackEnd MoMo call API - /momo-app/add-balance BackEnd CRM, response={}", response);
            writeLog(req.toString(), response.toString(), "", "127.0.0.1", 0, Constants.ACT_TYPE.NAP_TIEN, authentication, "POST", WsAuditEntity.Status.SUCCESS.value);
            return response;
        }
        LOGGER.info("BackEnd MoMo call API - /momo-app/add-balance BackEnd CRM, response={}", response);
        writeLog(req.toString(), response.toString(), "", "127.0.0.1", 0, Constants.ACT_TYPE.NAP_TIEN, authentication, "POST", WsAuditEntity.Status.SUCCESS.value);
        return response;
    }

    /***
     * Validate du lieu dau vao cho luong nap tien truc tiep tu Vi MoMo
     * @param data
     * @return
     */
    private MoMoAppResponsePaymentDTO validateMoMoAppRequestPaymentData(MoMoAppRequestPaymentDTO data) {
        MoMoAppResponsePaymentDTO response = new MoMoAppResponsePaymentDTO(data);
        if (FnCommon.isNullOrEmpty(data.getContractId())
                || FnCommon.isNullOrEmpty(data.getContractNo())
                || FnCommon.isNullOrEmpty(data.getRequestId())
                || FnCommon.isNullOrEmpty(data.getRequestTime())
                || FnCommon.isNullObject(data.getAmount())) {
            response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.INPUT_INVALID);
            response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.invalid.input"));
            return response;
        }
        if (!FnCommon.validateCommonFormatDate(data.getRequestTime())) {
            response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.REQUEST_TIME_INVALID);
            response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.request.time.invalid"));
            return response;
        }
        if (data.getAmount() < 0L) {
            response.setErrorCode(Constants.MOMO_APP_ERROR_CODE.AMOUNT_WRONG);
            response.setErrorMess(jedisCacheService.getMessageErrorByKey("crm.momo.app.amount.value.wrong"));
            return response;
        }
        return response;
    }

    /***
     * BackEnd CRM gọi sang BackEnd MoMo bắt đầu thực hiện luồng nạp tiền
     * @param data
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    public Object requestMoMoAppPayment(MoMoRawDataDTO data, Authentication authentication) throws Exception {
        MoMoBaseDTO responseAppDTO = new MoMoBaseDTO();
        responseAppDTO.setAmount(data.getAmount());
        responseAppDTO.setPartnerCode(data.getPartnerCode());
        responseAppDTO.setPartnerRefId(data.getPartnerRefId());
        Optional<SaleOrderEntity> saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(Long.parseLong(data.getPartnerRefId()), SaleOrderEntity.Status.NEW_ORDER.value);
        if (saleOrderEntity.isPresent()) {
            MoMoPayAppRequestDTO momoPayAppRequestDTO = new MoMoPayAppRequestDTO();
            momoPayAppRequestDTO.setPartnerCode(data.getPartnerCode());
            momoPayAppRequestDTO.setPartnerRefId(data.getPartnerRefId());
            momoPayAppRequestDTO.setCustomerNumber(data.getCustomerNumber());
            momoPayAppRequestDTO.setAppData(data.getAppData());
            momoPayAppRequestDTO.setVersion(Double.parseDouble(version));
            momoPayAppRequestDTO.setPayType(Integer.parseInt(payType));
            momoPayAppRequestDTO.setDescription("Thanh toan cho don hang " + data.getPartnerRefId() + " qua MoMo");

            // generate RSA hash value
            MoMoBaseDTO obj = new MoMoBaseDTO(data.getPartnerCode(), data.getPartnerRefId(), data.getAmount());
            String crmHashRawString = new Gson().toJson(obj);
            LOGGER.info("BackEnd CRM call API - pay/app BackEnd MoMo, crmHashRawString={}", crmHashRawString);
            String hash = MomoEncoder.generateMoMoRSA(crmHashRawString, publicKey);
            LOGGER.info("BackEnd CRM call API - pay/app BackEnd MoMo, hash={}", hash);
            momoPayAppRequestDTO.setHash(hash);

            // Gọi API yêu cầu treo tiền bên phía MoMo server và nhận  kết quả trả về
            LOGGER.info("BackEnd CRM call API - pay/app BackEnd MoMo, request={}", momoPayAppRequestDTO);
            String strPayAppResp = doPostRequest(payAppUrl, momoPayAppRequestDTO, timeOut,Constants.ACT_TYPE.CALL_PAY_APP_MOMO_API,4);
            MoMoPayAppResponseDTO moMoPayAppResponseDTO = new Gson().fromJson(strPayAppResp, MoMoPayAppResponseDTO.class);
            if (!FnCommon.isNullObject(moMoPayAppResponseDTO)) {
                responseAppDTO.setMomoTransId(moMoPayAppResponseDTO.getTransid());
                if (moMoPayAppResponseDTO.getStatus() == 0) {
                    // Xử lý nộp tiền trên OCS và gọi tới confirm API bên phía MoMo server
                    Long contractId = saleOrderEntity.get().getContractId();
                    String requestId = String.valueOf(System.currentTimeMillis());
                    String requestType = Constants.MOMO_MESSAGE.CANCEL_APP_TRANSACTION;
                    MoMoConfirmRequestDTO moMoConfirmRequestDTO = new MoMoConfirmRequestDTO();
                    long balanceBefore = 0L;
                    try {
                        ContractDTO contractDTO = contractService.getOCSInfo(authentication, contractId);
                        balanceBefore = contractDTO.getBalance();
                        OCSResponse result = ocsService.addBalance(authentication, Constants.ACT_TYPE.NAP_TIEN,
                                saleOrderEntity.get().getContractId(), saleOrderEntity.get().getAmount());
                        if (FnCommon.checkOcsCode(result)) {
                            requestType = Constants.MOMO_MESSAGE.CONFIRM_APP_TRANSACTION;
                            TopupEtcEntity topupEtcEntity = saveTopUpEtc(saleOrderEntity.get(), balanceBefore, moMoPayAppResponseDTO.getTransid());
                            if (!FnCommon.isNullObject(topupEtcEntity)) {
                                topupEtcEntity.setTopupCode(topupEtcEntity.getTopupCode() + topupEtcEntity.getTopupEtcId());
                                topupEtcServiceJPA.save(topupEtcEntity);
                            }
                            saleOrderEntity.get().setOcsStatus(SaleOrderEntity.OcsStatus.SUCCESS.value);
                            saleOrderEntity.get().setStatus(SaleOrderEntity.Status.SUCCESS_PAYMENT.value);
                            saleOrderServiceJPA.save(saleOrderEntity.get());

                            // them lucky code nap tien tu app momo
                            luckyService.genLuckyCode(contractServiceJPA.getOne(saleOrderEntity.get().getContractId()), Constants.LUCKY_CODE.NAPTIEN, authentication);
                        } else {
                            moMoConfirmRequestDTO.setDescription(jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed"));
                            saleOrderEntity.get().setOcsStatus(SaleOrderEntity.OcsStatus.FAILED.value);
                            saleOrderEntity.get().setStatus(SaleOrderEntity.Status.OCS_FAILED.value);
                            saleOrderServiceJPA.save(saleOrderEntity.get());
                        }
                    } catch (Exception ex) {
                        LOGGER.error(jedisCacheService.getMessageErrorByKey("crm.momo.call.ocs.to.add.balance.failed"), ex);
                        moMoConfirmRequestDTO.setDescription(jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed"));
                    }
                    moMoConfirmRequestDTO.setRequestType(requestType);
                    StringBuilder sb = new StringBuilder();
                    sb.append("partnerCode=").append(data.getPartnerCode())
                            .append("&partnerRefId=").append(data.getPartnerRefId())
                            .append("&requestType=").append(requestType)
                            .append("&requestId=").append(requestId)
                            .append("&momoTransId=").append(moMoPayAppResponseDTO.getTransid());
                    LOGGER.info("BackEnd CRM call API confirm MoMo BackEnd: rawStringSignature={}", sb.toString());
                    String signature = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);
                    moMoConfirmRequestDTO.setSignature(signature);
                    moMoConfirmRequestDTO.setPartnerCode(data.getPartnerCode());
                    moMoConfirmRequestDTO.setPartnerRefId(data.getPartnerRefId());
                    moMoConfirmRequestDTO.setMomoTransId(moMoPayAppResponseDTO.getTransid());
                    moMoConfirmRequestDTO.setRequestId(requestId);
                    moMoConfirmRequestDTO.setCustomerNumber(data.getCustomerNumber());

                    LOGGER.info("BackEnd CRM call API - pay/confirm BackEnd MoMo, request={}", moMoConfirmRequestDTO);
                    String strConfirmResp = doPostRequest(payConfirmUrl, moMoConfirmRequestDTO, timeOut, Constants.ACT_TYPE.CALL_PAY_CONFIRM_MOMO_API,4);
                    MoMoConfirmResponseDTO moMoConfirmResponseDTO = new Gson().fromJson(strConfirmResp, MoMoConfirmResponseDTO.class);
                    LOGGER.info("BackEnd CRM call API - pay/confirm BackEnd MoMo, response={}", moMoConfirmResponseDTO);
                    if (Constants.MOMO_MESSAGE.CANCEL_APP_TRANSACTION.equals(requestType)) {
                        throw new EtcException("crm.ocs.momo.add.balance.failed");
                    }
                } else {
                    LOGGER.info("BackEnd CRM call API - pay/app BackEnd MoMo, response={}", moMoPayAppResponseDTO);
                    throw new EtcException("crm.momo.add.balance.failed");
                }
            } else {
                LOGGER.error("BackEnd CRM call API - pay/app BackEnd MoMo failed");
                throw new EtcException("crm.momo.add.balance.failed");
            }
        } else {
            throw new EtcException("crm.order.is.not.found");
        }
        return responseAppDTO;
    }

    /**
     * Tạo dữ liệu đơn hàng nộp tiền
     *
     * @param req
     * @param authentication
     * @param customerId
     * @param contractId
     * @return
     */
    @Override
    public Object mobileAppChuPTCreateSaleOrder(RequestAddSupOfferDTO req, Authentication authentication, Long customerId, Long contractId) {

        // Neu co truyen desContract -> nap ho, khong phai thi nap tai khoan ca nhan
        Long contractIdDes = req.getDesContractId() != null ? req.getDesContractId() : contractId;

        //Luu lai sale order trong bang SALE_ORDER
        ContractDTO desContractDTO = contractService.getOCSInfo(authentication, contractIdDes);
        Date date = new Date(System.currentTimeMillis());
        SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderDate(date);
        saleOrderEntity.setSaleOrderType(SaleOrderEntity.SaleOrderType.ADD_MONEY.value);
        saleOrderEntity.setSaleOrderSource(SaleOrderEntity.SaleOrderSource.MOMO.value);
        saleOrderEntity.setStatus(SaleOrderEntity.Status.NEW_ORDER.value);
        saleOrderEntity.setAmount(req.getAmount());
        saleOrderEntity.setQuantity(req.getQuantity());
        saleOrderEntity.setCustId(customerId);
        saleOrderEntity.setContractId(contractIdDes); // destination contractId
        saleOrderEntity.setContractNo(desContractDTO.getContractNo()); // destination contractNo
        saleOrderEntity.setSourceContractId(contractId); // source contractId
        saleOrderEntity.setSourceContractNo(req.getContractNo()); // source contractNo
        saleOrderEntity.setCreateDate(date);
        saleOrderEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        saleOrderEntity = saleOrderRepositoryJPA.save(saleOrderEntity);

        ResAddSupOfferDTO result = new ResAddSupOfferDTO();
        if (!FnCommon.isNullObject(saleOrderEntity)) {
            result.setBillingCode(saleOrderEntity.getSaleOrderId());
        } else {
            result.setBillingCode(0L);
        }
        return result;
    }

    @Override
    public Object portalChuPTCreateSaleOrder(RequestAddSupOfferDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        Date date = new Date(System.currentTimeMillis());

        // Luu lai Sale order
        SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setAmount(addSupOfferRequest.getAmount());
        saleOrderEntity.setContractId(contractId);
        saleOrderEntity.setCustId(customerId);
        saleOrderEntity.setContractNo(addSupOfferRequest.getContractNo());
        saleOrderEntity.setCreateDate(date);
        saleOrderEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        saleOrderEntity.setQuantity(1L);
        saleOrderEntity.setSaleOrderDate(date);
        saleOrderEntity.setStatus(SaleOrderEntity.Status.NEW_ORDER.value);
        saleOrderEntity.setSaleOrderType(SaleOrderEntity.SaleOrderType.ADD_MONEY.value);
        saleOrderEntity.setSaleOrderSource(SaleOrderEntity.SaleOrderSource.MOMO.value);
        saleOrderEntity = saleOrderRepositoryJPA.save(saleOrderEntity);

        MoMoPayWebResponseDTO response = new MoMoPayWebResponseDTO();
        if (!FnCommon.isNullObject(saleOrderEntity)) {
            MoMoPayWebRequestDTO req = new MoMoPayWebRequestDTO();
            req.setOrderId(saleOrderEntity.getSaleOrderId().toString());
            req.setOrderInfo("Nạp tiền từ momo vào tài khoản ETC");
            req.setAccessKey(accessKey);
            req.setAmount(addSupOfferRequest.getAmount().toString());
            req.setExtraData(extraDataWeb);
            req.setPartnerCode(parnerCode);
            req.setRequestType(Constants.MOMO_MESSAGE.REQUEST_WEB_TYPE);
            req.setNotifyUrl(urlNotifyWeb);
            req.setRequestId(String.valueOf(System.currentTimeMillis()));
            req.setReturnUrl(addSupOfferRequest.getReturnUrl());
            StringBuilder sb = new StringBuilder();
            sb.append("partnerCode=").append(req.getPartnerCode())
                    .append("&accessKey=").append(req.getAccessKey())
                    .append("&requestId=").append(req.getRequestId())
                    .append("&amount=").append(req.getAmount())
                    .append("&orderId=").append(req.getOrderId())
                    .append("&orderInfo=").append(req.getOrderInfo())
                    .append("&returnUrl=").append(req.getReturnUrl())
                    .append("&notifyUrl=").append(req.getNotifyUrl())
                    .append("&extraData=").append(req.getExtraData());
            String signatureCompare = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);
            req.setSignature(signatureCompare);

            // Gọi API yêu cầu treo tiền bên phía MoMo server và nhận  kết quả trả về
            LOGGER.info("Backend Web CRM: Call to backend momo to get link transaction: req={}", req);
            String strPayAppResp = doPostRequest(payWebUrl, req, timeOut, Constants.ACT_TYPE.CREATE_SALE_ORDER,4);

            response = new Gson().fromJson(strPayAppResp, MoMoPayWebResponseDTO.class);
            LOGGER.info("Backend Web CRM: Call to backend momo to get link transaction: response={}", response);
            if (!FnCommon.isNullObject(response)) {
                StringBuilder sbReponse = new StringBuilder();
                sbReponse.append("requestId=").append(response.getRequestId())
                        .append("&orderId=").append(response.getOrderId())
                        .append("&message=").append(response.getMessage())
                        .append("&localMessage=").append(response.getLocalMessage())
                        .append("&payUrl=").append(response.getPayUrl())
                        .append("&errorCode=").append(response.getErrorCode())
                        .append("&requestType=").append(response.getRequestType());
                signatureCompare = MomoEncoder.signHmacSHA256(sbReponse.toString(), secretKey);
                if (!signatureCompare.equals(response.getSignature())) {
                    response.setErrorCode(1);
                    response.setLocalMessage(jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed"));
                    response.setPayUrl("");
                }
            }
            else{
                LOGGER.info("Backend Web CRM: Call to backend momo to get link transaction: failed");
                response = new MoMoPayWebResponseDTO();
                response.setErrorCode(1);
                response.setLocalMessage(jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed"));
                response.setPayUrl("");
            }
        }
        return response;
    }

    /**
     * Xác thực thông tin từ phia MoMo server gui sang
     *
     * @return thông tin liên quan và mã thông báo thành công hay không
     */
    @Override
    public MoMoNotifyResponseDTO verifyMoMoRequestData(MoMoNotifyRequestDTO data, Authentication authentication) throws Exception {

        String partnerRefId = data.getPartnerRefId();
        int status = 0;
        String message = jedisCacheService.getMessageErrorByKey("crm.momo.payment.success");
        String momoTransId = data.getMomoTransId();
        Long amount = data.getAmount();

        Optional<SaleOrderEntity> saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(Long.parseLong(data.getPartnerRefId()), SaleOrderEntity.Status.NEW_ORDER.value);
        if (saleOrderEntity.isPresent()) {
            StringBuilder sb = new StringBuilder();
            amount = saleOrderEntity.get().getAmount();
            sb.append("accessKey=").append(data.getAccessKey())
                    .append("&amount=").append(amount)
                    .append("&message=").append(data.getMessage())
                    .append("&momoTransId=").append(momoTransId)
                    .append("&partnerCode=").append(data.getPartnerCode())
                    .append("&partnerRefId=").append(partnerRefId)
                    .append("&partnerTransId=").append(data.getPartnerTransId())
                    .append("&responseTime=").append(data.getResponseTime())
                    .append("&status=").append(data.getStatus())
                    .append("&storeId=").append(data.getStoreId())
                    .append("&transType=momo_wallet");
            String signatureCompare = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);
            if (!signatureCompare.equals(data.getSignature())) {
                status = 1;
                message = jedisCacheService.getMessageErrorByKey("crm.momo.order.info.wrong");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append(amount)
                .append("&message=").append(message)
                .append("&momoTransId").append(momoTransId)
                .append("&partnerRefId=").append(partnerRefId)
                .append("&status=").append(status);
        String signature = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);

        return new MoMoNotifyResponseDTO(status, message, partnerRefId, momoTransId, amount, signature);
    }

    /**
     * Xác thực thông tin từ phia MoMo server gui sang (Web)
     *
     * @return thông tin liên quan và mã thông báo thành công hay không
     */
    @Override
    public MoMoWebNotifyResponseDTO verifyMoMoRequestDataWeb(MoMoWebNotifyRequestDTO data) throws Exception {
        int errorCode = Constants.MOMO_PORTAL_CPT_ERROR_CODE.SUCCESS;
        String message = jedisCacheService.getMessageErrorByKey("crm.momo.payment.success");

        Optional<SaleOrderEntity> saleOrderEntity = saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(
                Long.parseLong(data.getOrderId()), SaleOrderEntity.Status.NEW_ORDER.value);
        String extraData = "";
        if (!FnCommon.isNullOrEmpty(data.getExtraData())) {
            extraData = data.getExtraData();
        }
        data.setExtraData(extraData);
        if (saleOrderEntity.isPresent()) {
            StringBuilder sb = new StringBuilder();
            sb.append("partnerCode=").append(data.getPartnerCode())
                    .append("&accessKey=").append(data.getAccessKey())
                    .append("&requestId=").append(data.getRequestId())
                    .append("&amount=").append(data.getAmount())
                    .append("&orderId=").append(data.getOrderId())
                    .append("&orderInfo=").append(data.getOrderInfo())
                    .append("&orderType=").append(data.getOrderType())
                    .append("&transId=").append(data.getTransId())
                    .append("&message=").append(data.getMessage())
                    .append("&localMessage=").append(data.getLocalMessage())
                    .append("&responseTime=").append(data.getResponseTime())
                    .append("&errorCode=").append(data.getErrorCode())
                    .append("&payType=").append(data.getPayType())
                    .append("&extraData=").append(data.getExtraData());
            String signatureCompare = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);
            if (!signatureCompare.equals(data.getSignature())) {
                errorCode = Constants.MOMO_PORTAL_CPT_ERROR_CODE.ORDER_ID_ERROR;
                message = jedisCacheService.getMessageErrorByKey("crm.momo.order.info.wrong");
            }

            if (data.getErrorCode() == 0 && errorCode == 0) {
                // Xử lý nộp tiền trên OCS và gọi tới confirm API bên phía MoMo server
                try {
                    Long contractId = saleOrderEntity.get().getContractId();
                    Map<String, String> params = new HashMap<>();
                    params.put("contractId", contractId.toString());
                    HashMap<String, ?> hsMap;
                    String resp = doPostRequest(wsQueryContract, params, null, 1, null, Constants.ACT_TYPE.QUERY_CONTRACT_INFO);
                    hsMap = new Gson().fromJson(resp, LinkedHashMap.class);
                    LOGGER.info("Method: responseAddMoney - QueryContractData hsMap={}", hsMap);
                    Double balanceTemp = (Double) hsMap.get("balance");
                    long balanceBefore = balanceTemp.longValue();
                    OCSResponse result = ocsService.addBalance(null, Constants.ACT_TYPE.NAP_TIEN,
                            saleOrderEntity.get().getContractId(), saleOrderEntity.get().getAmount());
                    if (FnCommon.checkOcsCode(result)) {
                        // Luu them lucky code chuong trinh
                        luckyService.genLuckyCode(contractServiceJPA.getOne(saleOrderEntity.get().getContractId()), Constants.LUCKY_CODE.NAPTIEN, null);

                        String userLogin = saleOrderEntity.get().getContractNo();
                        saleOrderEntity.get().setStatus(SaleOrderEntity.Status.SUCCESS_PAYMENT.value);
                        saleOrderServiceJPA.save(saleOrderEntity.get());
                        try {
                            TopupEtcEntity topupEtcEntity = saveTopUpEtc(saleOrderEntity.get().getContractId(), saleOrderEntity.get().getAmount(),
                                    balanceBefore, userLogin, userLogin, userLogin, null);
                            if (!FnCommon.isNullObject(topupEtcEntity)) {
                                topupEtcEntity.setTopupCode(topupEtcEntity.getTopupCode() + topupEtcEntity.getTopupEtcId());
                                topupEtcServiceJPA.save(topupEtcEntity);
                            }
                        } catch (Exception ex) {
                            errorCode = Constants.MOMO_PORTAL_CPT_ERROR_CODE.SAVE_TOPUP_ERROR;
                            LOGGER.error("Method: responseAddMoney - Save data to TopUpEtcTable failed ex", ex);
                            throw ex;
                        }
                    } else {
                        errorCode = Constants.MOMO_PORTAL_CPT_ERROR_CODE.ADD_BALANCE_OCS_ERROR;
                        message = jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed");
                        saleOrderEntity.get().setStatus(SaleOrderEntity.Status.CANCELED.value);
                        saleOrderServiceJPA.save(saleOrderEntity.get());
                    }
                } catch (Exception ex) {
                    if(errorCode == Constants.MOMO_PORTAL_CPT_ERROR_CODE.SUCCESS)
                        errorCode = Constants.MOMO_PORTAL_CPT_ERROR_CODE.QUERY_CONTRACT_ERROR;
                    message = jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed");
                    LOGGER.error(jedisCacheService.getMessageErrorByKey("crm.momo.call.ocs.to.add.balance.failed"), ex);
                    saleOrderEntity.get().setStatus(SaleOrderEntity.Status.CANCELED.value);
                    saleOrderServiceJPA.save(saleOrderEntity.get());
                }
            }
        } else {
            errorCode = Constants.MOMO_PORTAL_CPT_ERROR_CODE.MOMO_RESPONSE_ERROR;
            message = jedisCacheService.getMessageErrorByKey("crm.momo.payment.failed");
        }
        StringBuilder sb = new StringBuilder();
        Date responseTime = new Date(System.currentTimeMillis());
        sb.append("partnerCode=").append(data.getPartnerCode())
                .append("&accessKey=").append(data.getAccessKey())
                .append("&requestId=").append(data.getRequestId())
                .append("&orderId=").append(data.getOrderId())
                .append("&errorCode=").append(errorCode)
                .append("&message=").append(message)
                .append("&responseTime=").append(responseTime)
                .append("&extraData=").append(data.getExtraData());
        String signature = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);

        return new MoMoWebNotifyResponseDTO(data.getPartnerCode(), data.getAccessKey(), data.getRequestId(), data.getOrderId(), errorCode, message, FnCommon.convertDateToStringOther(responseTime, Constants.MOMO_DATE_FORMAT), "", signature);
    }

    @Override
    public MoMoNotifyResponseDTO responseNotifyFromMoMo(MoMoNotifyRequestDTO data, Authentication authentication)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        MoMoNotifyResponseDTO notifyResponseDTO = new MoMoNotifyResponseDTO();
        notifyResponseDTO.setAmount(data.getAmount());
        notifyResponseDTO.setMomoTransId(data.getMomoTransId());
        notifyResponseDTO.setPartnerRefId(data.getPartnerRefId());
        notifyResponseDTO.setStatus(0);
        notifyResponseDTO.setMessage(jedisCacheService.getMessageErrorByKey("crm.momo.payment.success"));
        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append(data.getAmount())
                .append("&message=").append(jedisCacheService.getMessageErrorByKey("crm.momo.payment.success"))
                .append("&momoTransId=").append(data.getMomoTransId())
                .append("&partnerRefId=").append(data.getPartnerRefId())
                .append("&status=").append(0);
        String signature = MomoEncoder.signHmacSHA256(sb.toString(), secretKey);
        notifyResponseDTO.setSignature(signature);
        return notifyResponseDTO;
    }

    /***
     * Goi sang API do MoMo Service cung cap
     * @param url
     * @param obj
     * @param url
     * @param timeOut
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
            LOGGER.error("Has error call API from MoMo service", e);
            if (request != null) {
                writeLog(buildLogRequest(request, FnCommon.toStringJson(obj)), strRes, url, "127.0.0.1", 0, actType, null, "POST", WsAuditEntity.Status.NOT_SUCCESS.value);
                doPostRequest(url, obj, timeOut, actType,++step);
            }
        }
        return strRes;
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

    /***
     * Hàm ghi log gọi WS OCS vào bảng WS_AUDIT
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
                wsAuditEntity.setDestinationAppId("MOMO");
            } else {
                wsAuditEntity.setSourceAppId("MOMO");
                wsAuditEntity.setDestinationAppId(clientId);
            }
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setActTypeId(actTypeId);
            wsAuditEntity.setSourceAppId(clientId);
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

    private String buildLogRequest(Request request, String body) {
        if (Strings.isBlank(body)) {
            body = "{}";
        }
        return "Request{method=" + request.method() + ", url=" + request.url() + ", body=" + body + ", tag=" + (request.tag() != request ? request.tag() : null) + '}';
    }

    /***
     * Hàm gọi OCS
     * @param url
     * @param params
     * @param json
     * @param step
     * @param authentication
     * @param actTypeId
     * @return
     */
    String doPostRequest(String url, Map<String, String> params, String json, int step, Authentication authentication, long actTypeId) {
        String strRes = "", ip = "127.0.0.1";
        Request request = null;
        long start = 0L, end = 0L;
        OkHttpClient client = new OkHttpClient();
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            //check step == config
            if (step <= Integer.parseInt(numberRetry)) {
                client.setConnectTimeout(Long.parseLong(timeOut), TimeUnit.SECONDS);
                client.setReadTimeout(30, TimeUnit.SECONDS);
                client.setWriteTimeout(30, TimeUnit.SECONDS);
                HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
                if (params != null) {
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                    }
                }
                RequestBody body;
                if (json == null) {
                    body = RequestBody.create(null, new byte[]{});
                } else body = RequestBody.create(Constants.JSON, json);
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .post(body)
                        .build();
                start = System.currentTimeMillis();
                Response response = client.newCall(request).execute();
                end = System.currentTimeMillis() - start;
                strRes = response.body().string();
                LOGGER.info("doPostRequest OCS url={} params={}", url, params);
                LOGGER.info("doPostRequest OCS response body={}", strRes);
                wsAuditEntity = writeLog(buildLogRequest(request, json), strRes, url, ip, end, actTypeId, authentication, request.method(), WsAuditEntity.Status.SUCCESS.value);
                return strRes.replace("null", "\"\"");
            }
        } catch (Exception e) {
            LOGGER.error("Has error call OCS", e);
            if (request != null) {
                updateWriteLogFail(wsAuditEntity);
                end = System.currentTimeMillis() - start;
                writeLog(buildLogRequest(request, json), strRes, url, ip, end, actTypeId, authentication, request.method(), "0");
                doPostRequest(url, params, json, ++step, authentication, actTypeId);
            }
        }
        return strRes;
    }

    /***
     * Luu thong tin nop tien vao bang topup ETC
     * @param contractId
     * @param amount
     * @param balanceBefore
     * @param topupPayer
     * @param authentication
     */
    private TopupEtcEntity saveTopUpEtc(long contractId, long amount, long balanceBefore, String topupPayer, String staffCode, String staffName, Authentication authentication) {
        TopupEtcEntity etcEntity = new TopupEtcEntity();
        String userLogin = "MoMoApi";
        if (authentication != null) {
            userLogin = FnCommon.getUserLogin(authentication);
        }
        etcEntity.setTopupCode("M");
        etcEntity.setTopupChannel(TopupEtcEntity.TopupChannel.MOMO_CHANNEL.value);
        etcEntity.setTopupDate(new Date(System.currentTimeMillis()));
        etcEntity.setContractId(contractId);
        etcEntity.setAmount(amount);
        etcEntity.setTopupType(TopupEtcEntity.TopupType.TOPUP_MOMO.value);
        etcEntity.setStatus(TopupEtcEntity.Status.SUCCESS.value);
        etcEntity.setCreateUser(userLogin);
        etcEntity.setCreateDate(new Date(System.currentTimeMillis()));
        etcEntity.setBalanceBefore(balanceBefore);
        etcEntity.setBalanceAfter(balanceBefore + amount);
        etcEntity.setStaffName(staffName);
        etcEntity.setStaffCode(staffCode);
        etcEntity.setTopupPayer(topupPayer);
        etcEntity.setVolumeNo(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        etcEntity.setNo((long) topupEtcServiceJPA.findAll().size());
        return topupEtcServiceJPA.save(etcEntity);
    }

    /***
     * Luu thong tin nop tien vao bang topup ETC (luong moi, nap tien cho hop dong khac)
     * @param saleOrder
     * @param balanceBefore
     * @return
     */
    private TopupEtcEntity saveTopUpEtc(SaleOrderEntity saleOrder, long balanceBefore, String transId) {
        TopupEtcEntity etcEntity = new TopupEtcEntity();
        etcEntity.setTopupCode("M");
        etcEntity.setTopupChannel(TopupEtcEntity.TopupChannel.MOMO_CHANNEL.value);
        etcEntity.setTopupDate(new Date(System.currentTimeMillis()));
        etcEntity.setContractId(saleOrder.getContractId());
        etcEntity.setSourceContractId(saleOrder.getSourceContractId());
        etcEntity.setSourceContractNo(saleOrder.getSourceContractNo());
        etcEntity.setAmount(saleOrder.getAmount());
        etcEntity.setTopupType(TopupEtcEntity.TopupType.TOPUP_MOMO.value);
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

    /***
     *
     * @param contractDTO
     * @param balanceBefore
     * @param amount
     * @param transId
     * @return
     */
    private TopupEtcEntity saveTopUpEtc(ContractDTO contractDTO, long balanceBefore, long amount, String requestTime, String transId) {
        TopupEtcEntity etcEntity = new TopupEtcEntity();
        etcEntity.setTopupCode("M");
        etcEntity.setTopupChannel(TopupEtcEntity.TopupChannel.MOMO_CHANNEL.value);
        etcEntity.setTopupDate(new Date(System.currentTimeMillis()));
        etcEntity.setContractId(contractDTO.getContractId());
        etcEntity.setSourceContractId(contractDTO.getContractId());
        etcEntity.setSourceContractNo(contractDTO.getContractNo());
        etcEntity.setAmount(amount);
        etcEntity.setTopupType(TopupEtcEntity.TopupType.TOPUP_MOMO.value);
        etcEntity.setStatus(TopupEtcEntity.Status.SUCCESS.value);
        etcEntity.setCreateUser(contractDTO.getContractNo());
        etcEntity.setCreateDate(new Date(System.currentTimeMillis()));
        etcEntity.setBalanceBefore(balanceBefore);
        etcEntity.setBalanceAfter(balanceBefore + amount);
        etcEntity.setStaffCode(contractDTO.getContractNo());
        etcEntity.setStaffName(contractDTO.getContractNo());
        etcEntity.setTopupPayer(contractDTO.getContractNo());
        etcEntity.setVolumeNo(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        etcEntity.setNo((long) topupEtcServiceJPA.findAll().size());
        etcEntity.setSaleOrderDate(FnCommon.convertStringToDate(requestTime, Constants.COMMON_DATE_TIME_FORMAT));
        etcEntity.setTransId(transId);
        return topupEtcServiceJPA.save(etcEntity);
    }
}
