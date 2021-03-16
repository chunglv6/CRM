package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.dto.keycloak.ResUserKeycloakDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.repositories.TopupAccountConfigRepository;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.TopupAccountConfigServiceJPA;
import com.viettel.etc.services.tables.TopupDailyServiceJPA;
import com.viettel.etc.services.tables.TopupEtcServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.exports.PdfHtmlTemplateExporter;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.*;

@Service
public class TopupServiceImpl implements TopupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopupServiceImpl.class);

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    @Autowired
    TopupEtcServiceJPA topupEtcServiceJPA;

    @Autowired
    KeycloakService keycloakService;

    @Autowired
    TopupAccountConfigServiceJPA topupAccountConfigServiceJPA;

    @Autowired
    TopupDailyServiceJPA topupDailyServiceJPA;

    @Autowired
    JedisCacheService jedisCacheService;

    @Autowired
    OCSService ocsService;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    ContractService contractService;

    @Autowired
    PdfHtmlTemplateExporter parseThymeleafTemplate;

    @Autowired
    TopupAccountConfigRepository topupAccountConfigRepository;

    @Value("${ws.bccs.topup.params.username}")
    private String usernameTopUp;

    @Value("${ws.bccs.topup.params.password}")
    private String passwordTopUp;

    @Value("${ws.bccs.topup.destroy.params.username}")
    private String usernameDestroy;

    @Value("${ws.bccs.topup.destroy.params.password}")
    private String passwordDestroy;

    @Value("${ws.bccs.topup.url}")
    private String topupUrl;

    @Value("${group.keycloak.etc.cpt.id}")
    private String groupCptId;

    @Value("${ws.bccs.topup.destroy.url}")
    private String destroySaleTransExternalUrl;

    @Value("${ws.bccs.topup.revenueObjectCode}")
    private String revenueObjectCode;

    @Value("${ws.bccs.topup.destroy.external.code}")
    private String externalCode;

    @Value("${ws.ocs.retry}")
    private String numberRetry;

    @Value("${crm.ws.soap.time-out}")
    private String timeOut;

    /***
     * thuc hien nop tien mat vao tai khoan etc
     * @param authentication
     * @param topupDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = EtcException.class)
    public Long executeTopupCashToEtc(Authentication authentication, TopupDTO topupDTO) throws Exception {
        String inputXml = setRequestDoReceiveRevenueGeneralOnBCCSFull(topupDTO);
        String responseSoap = requestSOAP(topupUrl, 1, authentication, inputXml, 1);
        TopupCashResponseDTO response = readSoapResponse(responseSoap);
        if (response != null) {
            if ("0".equals(response.getResponseCode())) {
                OCSResponse result = ocsService.addBalance(authentication, 32L,
                        topupDTO.getContractId(), topupDTO.getPrice());
                if (result == null || result.getResultCode() == null || !"0".equals(result.getResultCode())) {
                    String xmlRequestDestroy = setRequestDestroySaleTransExternal(usernameDestroy, passwordDestroy, response.getAmsSaleTransId(), externalCode);
                    requestDestroyBccs(destroySaleTransExternalUrl, 1L, authentication, xmlRequestDestroy, 1);
                    throw new EtcException("ocs.communicate.error");
                }
                return saveToupEtc(topupDTO.getContractId(), topupDTO.getPrice(), topupDTO.getBalanceBefore(), topupDTO.getCustomerName(), authentication, response);
            } else {
                throw new EtcException(response.getDescription());
            }
        } else {
            throw new EtcException("crm.bccs.communication.error");
        }
    }

    /***
     * Export file hoa don sau khi nop tien
     * @param authentication
     * @param exportDTO
     * @return
     */
    @Override
    public Object exportTopupCashBill(Authentication authentication, TopupExportDTO exportDTO) {
        Optional<TopupEtcEntity> topupEtcEntity = topupEtcServiceJPA.findById(exportDTO.getTopupEtcId());
        Map<String, Object> objectMap = new HashMap<>();
        if (!topupEtcEntity.isPresent()) {
            throw new EtcException("crm.topup.export.null");
        }
        Optional<ContractEntity> contractEntity = contractServiceJPA.findById(topupEtcEntity.get().getContractId());

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", topupEtcEntity.get());
        dataMap.put("today", new Date(System.currentTimeMillis()));
        dataMap.put("BOO_ADDRESS", exportDTO.getBooAddress());
        dataMap.put("ADDRESS", exportDTO.getAddress());
        if (contractEntity.isPresent()) {
            dataMap.put("CONTRACT_NO", contractEntity.get().getContractNo());
        }
        String outputPath = System.getProperty("user.dir") + File.separator + "export.pdf";
        String temp = "template/topup_cash_pdf_template";
        String html = parseThymeleafTemplate.parseThymeleafTemplate(temp, dataMap);
        try {
            String filePath = parseThymeleafTemplate.generatePdfFromHtml(html, outputPath);
            byte[] bytes = IOUtils.toByteArray(new FileInputStream(new File(filePath)));
            objectMap.put("data", bytes);
            objectMap.put("error", ErrorApp.SUCCESS);
            return objectMap;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Object transferMoney(Authentication authentication, TopupTransferDTO params) {
        String user = FnCommon.getUserLogin(authentication);
        TopupAccountConfigEntity accountConfig = topupAccountConfigServiceJPA.getActiveByAccountUser(user);
        if (accountConfig != null && Constants.STATUS.ACTIVE.equals(accountConfig.getStatus())) {
            if (accountConfig.getBalance() < params.getTopupAmount()) {
                throw new EtcException("balance.not.enough");
            }
            ContractDTO contractDTO = contractService.getOCSInfo(authentication, params.getContractId());
            long balanceBefore = contractDTO.getBalance();
            
            OCSResponse response = ocsService.addBalance(authentication, Constants.ACT_TYPE.NAP_TIEN, params.getContractId(), params.getTopupAmount(), accountConfig.getPartyCode(), null, null);
            if (FnCommon.checkOcsCode(response)) {

                accountConfig.setBalance(accountConfig.getBalance() - params.getTopupAmount());
                topupAccountConfigServiceJPA.save(accountConfig);

                // luu vao topupEtc
                Date date = new Date(System.currentTimeMillis());
                TopupEtcEntity topupEtc = new TopupEtcEntity();
                topupEtc.setTopupCode("CTV" + System.currentTimeMillis());
                topupEtc.setTopupDate(date);
                topupEtc.setContractId(params.getContractId());
                topupEtc.setAmount(params.getTopupAmount());
                topupEtc.setBalanceBefore(balanceBefore);
                topupEtc.setBalanceAfter(balanceBefore + params.getTopupAmount());
                topupEtc.setTopupType(TopupEtcEntity.TopupType.TOPUP_CASH.value);
                topupEtc.setStaffCode(user);
                topupEtc.setStaffName(user);
                topupEtc.setStatus(TopupEtcEntity.Status.SUCCESS.value);
                topupEtc.setCreateUser(user);
                topupEtc.setCreateDate(date);
                topupEtc.setTopupPayer(user);
                topupEtc.setVolumeNo("VDTC/" + Calendar.getInstance().get(Calendar.YEAR));
                topupEtc.setNo((long) topupEtcServiceJPA.findAll().size());
                topupEtcServiceJPA.save(topupEtc);
                return topupEtc;
            }
            throw new EtcException("ocs.add.balance.error");
        }
        throw new EtcException("account.not.add.config.yet");
    }

    @Override
    public TopupCtvResDTO addBalance(Authentication authentication, TopupCtvDTO params) {
        Set<String> roleIds = FnCommon.getRoleId(authentication);
        TopupCtvResDTO res = new TopupCtvResDTO();
        List<TopupCtvResDTO.CtvResDTO> listDataError = new LinkedList<>();
        List<TopupCtvResDTO.CtvResDTO> listDataSuccess = new LinkedList<>();
        if (roleIds != null && roleIds.contains("Role_Admin_CRM")) {
            for (TopupAccountDTO ctvDTO : params.getUsers()) {
                TopupCtvResDTO.CtvResDTO dataRes = new TopupCtvResDTO.CtvResDTO();
                dataRes.setAccountUser(ctvDTO.getAccountUser());
                TopupAccountConfigEntity accountConfig = topupAccountConfigServiceJPA.getActiveByAccountUser(ctvDTO.getAccountUser());
                if (accountConfig == null || Constants.STATUS.INACTIVE.equals(accountConfig.getStatus())) {
                    dataRes.setMess(jedisCacheService.getMessageErrorByKey("account.not.add.config.yet"));
                    listDataError.add(dataRes);
                    continue;
                }
                if (ctvDTO.getTopupAmount() == null || ctvDTO.getTopupAmount() <= 0) {
                    dataRes.setMess(jedisCacheService.getMessageErrorByKey("amount.must.be.greater.than.zero"));
                    listDataError.add(dataRes);
                    continue;
                }
                Long balanceBefore = accountConfig.getBalance() == null ? 0L : accountConfig.getBalance();
                Long balanceStartDay = getStartDayBalance(ctvDTO.getAccountUser(), FnCommon.round(Calendar.getInstance().getTime()));
                balanceStartDay = balanceStartDay == null ? 0L : balanceStartDay;
                long peakAmount = balanceStartDay + ctvDTO.getTopupAmount();
                Long balanceAfter = balanceBefore + ctvDTO.getTopupAmount();
                if (peakAmount > accountConfig.getAmount()) {
                    dataRes.setMess(jedisCacheService.getMessageErrorByKey("amount.over.limit"));
                    listDataError.add(dataRes);
                    continue;
                }
                if (topupDailyServiceJPA.isAddBalanced(accountConfig.getAccountUser())) {
                    dataRes.setMess(jedisCacheService.getMessageErrorByKey("account.already.add.balance"));
                    listDataError.add(dataRes);
                    continue;
                }
                //luu TOPUP_ACCOUNT_CONFIG
                accountConfig.setBalance(balanceAfter);
                topupAccountConfigServiceJPA.save(accountConfig);
                //luuu TOPUP_DAILY
                TopupDailyEntity topupDaily = new TopupDailyEntity();
                topupDaily.setAccountUser(accountConfig.getAccountUser());
                topupDaily.setAccountUserId(accountConfig.getAccountUserId());
                topupDaily.setSourceAccountUserId(FnCommon.getIdUserLogin(authentication));
                topupDaily.setTopupDate(Calendar.getInstance().getTime());
                topupDaily.setBalanceBefore(balanceBefore);
                topupDaily.setTopupAmount(ctvDTO.getTopupAmount());
                topupDaily.setBalanceAfter(balanceAfter);
                topupDaily.setStatus(Constants.STATUS.ACTIVE);
                topupDaily.setDescription(null);
                topupDaily.setCreateUser(FnCommon.getUserLogin(authentication));
                topupDailyServiceJPA.save(topupDaily);
                listDataSuccess.add(dataRes);
            }
        } else {
            throw new EtcException("access.denied");
        }
        res.setError(listDataError);
        res.setSuccess(listDataSuccess);
        return res;
    }

    @Override
    public Object findTopupAccount(TopupAccountDTO params) {
        if (params.getTopupDate() == null) {
            params.setTopupDate(Calendar.getInstance().getTime());
        }
        ResultSelectEntity result = topupAccountConfigRepository.findTopupAccount(params);
        List<TopupAccountDTO> listData = (List<TopupAccountDTO>) result.getListData();
        for (TopupAccountDTO accountDTO : listData) {
            TopupDailyEntity topupDailyEntity = topupDailyServiceJPA.getByAccountUserAndTopupDate(accountDTO.getAccountUser(), params.getTopupDate());
            if (topupDailyEntity != null) {
                accountDTO.setTopupDate(topupDailyEntity.getTopupDate());
                accountDTO.setTopupAmount(topupDailyEntity.getTopupAmount());
            }
            accountDTO.setAmount(getDayPeakAmount(accountDTO.getAccountUser(), params.getTopupDate()));
            accountDTO.setBalance(getStartDayBalance(accountDTO.getAccountUser(), FnCommon.addDays(params.getTopupDate(), 1)));
        }
        return result;
    }

    @Override
    public Object detailCTVAccount(String accountUser, Authentication authentication) {
        return topupAccountConfigServiceJPA.getActiveByAccountUser(accountUser);
    }

    @Override
    public Object deleteCTVAccount(String accountUser) {
        TopupAccountConfigEntity entity = topupAccountConfigServiceJPA.getActiveByAccountUser(accountUser);
        if (entity == null) {
            throw new EtcException(ErrorApp.ERR_DATA);
        }
        entity.setStatus(Constants.STATUS.INACTIVE);
        return topupAccountConfigServiceJPA.save(entity);
    }

    @Override
    public Object addAccount(TopupAccountDTO params, Authentication authentication) {
        TopupAccountConfigEntity oldEntity = topupAccountConfigServiceJPA.getActiveByAccountUser(params.getAccountUser());
        if (oldEntity != null) {
            throw new EtcException("crm.account.already.add");
        }
        Calendar instance = Calendar.getInstance();
        oldEntity = getTopupAccountByDate(params.getAccountUser(), instance.getTime());
        if (oldEntity != null && oldEntity.getCreateDate().getTime() >= FnCommon.round(instance.getTime()).getTime() && !oldEntity.getAmount().equals(params.getAmount())) {
            throw new EtcException("amount.can.change.once.per.day");
        }
        List<ResUserKeycloakDTO.GroupUserKeycloak> listGroup = keycloakService.getUserGroups(params.getAccountUserId(), authentication);
        for (ResUserKeycloakDTO.GroupUserKeycloak group : listGroup) {
            if (groupCptId.equals(group.getId())) {
                throw new EtcException("crm.topup.account.cpt");
            }
        }
        return saveAddAccount(params, authentication);
    }

    @Override
    public Object updateAccount(TopupAccountDTO params, Authentication authentication) {
        TopupAccountConfigEntity oldEntity = topupAccountConfigServiceJPA.getActiveByAccountUser(params.getAccountUser());
        if (oldEntity != null) {
            if (!oldEntity.getAmount().equals(params.getAmount())) {    // thay doi han muc
                if (FnCommon.round(oldEntity.getCreateDate()).equals(FnCommon.round(Calendar.getInstance().getTime()))) {
                    // chi cho phep thay doi han muc 1 lan/ngay
                    throw new EtcException("amount.can.change.once.per.day");
                } else {
                    // inactive ban ghi cu, tao ban ghi voi han muc moi
                    oldEntity.setStatus(Constants.STATUS.INACTIVE);
                    oldEntity.setUpdateUser(FnCommon.getUserLogin(authentication));
                    topupAccountConfigServiceJPA.save(oldEntity);
                    return saveAddAccount(params, authentication);
                }
            } else {    // khong thay doi han muc
                setTopupAccount(params, oldEntity);
                oldEntity.setDescription(FnCommon.trim(params.getDescription()));
                oldEntity.setUpdateUser(FnCommon.getUserLogin(authentication));
                return topupAccountConfigServiceJPA.save(oldEntity);
            }
        } else {
            throw new EtcException("account.not.add.config.yet");
        }
    }

    private TopupAccountConfigEntity saveAddAccount(TopupAccountDTO params, Authentication authentication) {
        TopupAccountConfigEntity entity = new TopupAccountConfigEntity();
        entity.setAccountUserId(FnCommon.trim(params.getAccountUserId()));
        entity.setAccountUser(FnCommon.trim(params.getAccountUser()).toUpperCase());
        setTopupAccount(params, entity);
        entity.setAmount(params.getAmount());
        entity.setBalance(params.getBalance() != null ? params.getBalance() : 0L);
        entity.setStatus(Constants.STATUS.ACTIVE);
        entity.setDescription(FnCommon.trim(params.getDescription()));
        entity.setCreateUser(FnCommon.getUserLogin(authentication));
        entity.setUpdateUser(FnCommon.getUserLogin(authentication));
        return topupAccountConfigServiceJPA.save(entity);
    }

    private void setTopupAccount(TopupAccountDTO params, TopupAccountConfigEntity oldEntity) {
        oldEntity.setAccountFullname(FnCommon.trim(params.getAccountFullname()));
        oldEntity.setPartyCode(FnCommon.trim(params.getPartyCode()));
        oldEntity.setPartyName(FnCommon.trim(params.getPartyName()));
        oldEntity.setEmail(FnCommon.trim(params.getEmail()));
        oldEntity.setPhoneNumber(FnCommon.trim(params.getPhoneNumber()));
        oldEntity.setDentityNumber(FnCommon.trim(params.getDentityNumber()));
    }

    private TopupAccountConfigEntity getTopupAccountByDate(String accountUser, java.util.Date date) {
        date = FnCommon.round(FnCommon.addDays(date, 1));
        return topupAccountConfigServiceJPA.getByAccountUserAndCreateDate(accountUser, date);
    }

    private Long getDayPeakAmount(String accountUser, java.util.Date date) {
        TopupAccountConfigEntity entity = getTopupAccountByDate(accountUser, date);
        if (entity == null) {
            return null;
        }
        return entity.getAmount();
    }

    private Long getStartDayBalance(String accountUser, java.util.Date date) {
        TopupAccountConfigEntity entity = getTopupAccountByDate(accountUser, date);
        date = FnCommon.round(date);
        if (entity == null) {
            return null;
        }
        long balance = entity.getBalance() == null ? 0L : entity.getBalance();
        TopupEtcEntity topupEtc = topupEtcServiceJPA.getByCreateUserAndTopupDateAfter(entity.getAccountUser(), date);
        TopupDailyEntity topupDaily = topupDailyServiceJPA.getByAccountUserAndTopupDateAfter(entity.getAccountUser(), date);
        if (topupEtc == null && topupDaily == null) {
            return balance;
        }
        if (topupEtc == null) {
            return topupDaily.getBalanceBefore();
        }
        if (topupDaily == null) {
            return topupEtc.getBalanceBefore();
        }
        if (topupDaily.getTopupDate().getTime() < topupEtc.getTopupDate().getTime()) {
            return topupDaily.getBalanceBefore();
        } else {
            return topupEtc.getBalanceBefore();
        }
    }

    /***
     * Luu thong tin nop tien vao bang topup ETC
     * @param contractId
     * @param amount
     * @param balanceBefore
     * @param topupPayer
     * @param authentication
     * @param response
     */
    private long saveToupEtc(long contractId, long amount, long balanceBefore, String topupPayer, Authentication authentication, TopupCashResponseDTO response) throws Exception {
        try {
            Map<String, Object> map = FnCommon.getAttribute(authentication);
            long staffId = 0L;
            if (map != null) {
                staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
            }

            TopupEtcEntity etcEntity = new TopupEtcEntity();
            etcEntity.setTopupCode("IM".concat(response.getAmsSaleTransId()));
            etcEntity.setTopupDate(new Date(System.currentTimeMillis()));
            etcEntity.setContractId(contractId);
            etcEntity.setAmount(amount);
            etcEntity.setTopupType(TopupEtcEntity.TopupType.TOPUP_CASH.value);
            etcEntity.setStatus(TopupEtcEntity.Status.SUCCESS.value);
            etcEntity.setCreateUser(FnCommon.getUserLogin(authentication));
            etcEntity.setCreateDate(new Date(System.currentTimeMillis()));
            etcEntity.setBalanceBefore(balanceBefore);
            etcEntity.setBalanceAfter(balanceBefore + amount);
            etcEntity.setStaffName(FnCommon.getUserLogin(authentication));
            etcEntity.setStaffCode(String.valueOf(staffId));
            etcEntity.setTopupPayer(topupPayer);
            etcEntity.setVolumeNo(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            etcEntity.setNo((long) topupEtcServiceJPA.findAll().size());
            return topupEtcServiceJPA.save(etcEntity).getTopupEtcId();
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            AddSupOfferRequestDTO addSupOfferRequestDTO = new AddSupOfferRequestDTO();
            addSupOfferRequestDTO.setAmount(amount);
            addSupOfferRequestDTO.setActTypeId(1L);
            ocsService.charge(addSupOfferRequestDTO, authentication, contractId, null);

            String xmlRequestDestroy = setRequestDestroySaleTransExternal(usernameDestroy, passwordDestroy, response.getAmsSaleTransId(), externalCode);
            requestDestroyBccs(destroySaleTransExternalUrl, 1L, authentication, xmlRequestDestroy, 1);

            throw new EtcException("crm.topup.cash.error");
        }
    }

    /***
     * goi bccs thuc hien huy giao dich
     * @param destroySaleTransExternalUrl
     * @param actTypeId
     * @param authentication
     * @param xmlRequestDestroy
     * @param step
     */
    private void requestDestroyBccs(String destroySaleTransExternalUrl, long actTypeId, Authentication authentication, String xmlRequestDestroy, int step) throws JAXBException, XMLStreamException, IOException {
        String responseSoap = requestSOAP(destroySaleTransExternalUrl, actTypeId, authentication, xmlRequestDestroy, step);
        TopupCashResponseDTO response = readSoapResponse(responseSoap);
        if (FnCommon.isNullObject(response)) {
            LOGGER.error("crm.bccs.communication.error");
        }
        if (response != null && !response.isSuccess()) {
            LOGGER.error("crm.bccs.communication.error");
        }
    }

    /***
     * Tao request soap huy giao dich bccs
     * @param usernameDestroy
     * @param passwordDestroy
     * @param amsSaleTransId
     * @param externalCode
     * @return
     */
    private String setRequestDestroySaleTransExternal(String usernameDestroy, String passwordDestroy, String amsSaleTransId, String externalCode) {
        String destroySaleTransExternalRequest =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sell=\"http://sell.service.sale.bccs.viettel.com/\">\n" +
                        "   <soapenv:Header>\n" +
                        "      <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                        "         <wsse:UsernameToken wsu:Id=\"UsernameToken-32ce50e8-4a5d-4040-af71-c3428d92daa7\">\n" +
                        "            <wsse:Username>" + usernameDestroy + "</wsse:Username>\n" +
                        "            <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">" + passwordDestroy + "</wsse:Password>\n" +
                        "         </wsse:UsernameToken>\n" +
                        "      </wsse:Security>\n" +
                        "   </soapenv:Header>\n" +
                        "   <soapenv:Body>\n" +
                        "      <sell:destroySaleTransExternal>\n" +
                        "       <!--saleTransId trên BCCS, trường này được trả lại trong trường transactionId:-->\n" +
                        "         <saleTransID>" + amsSaleTransId + "</saleTransID>\n" +
                        "         <!--Optional:-->\n" +
                        "         <externalCode>" + externalCode + "</externalCode>\n" +
                        "      </sell:destroySaleTransExternal>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>\n";
        return destroySaleTransExternalRequest;
    }

    /***
     * tao request soap cho method len doanh thu gui bccs
     * @param topupDTO
     * @return
     */
    private String setRequestDoReceiveRevenueGeneralOnBCCSFull(TopupDTO topupDTO) {
        String transCode = FnCommon.formatDateTime(new Date(System.currentTimeMillis()));
        String saleTransDate = new Date(System.currentTimeMillis()).toString();
        String doReceiveRevenueGeneralOnBCCSFull = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:proc=\"http://process.wsim.viettel.com/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <proc:doReceiveRevenueGeneralOnBCCSFull>\n" +
                "         <!--Optional:-->\n" +
                "         <revenueInforDTO>\n" +
                "            <!--Địa chỉ khách hàng:-->\n" +
                "            <address>" + topupDTO.getAddress() + "</address>\n" +
                "            <!--Tên khách hàng:-->\n" +
                "            <custName>" + topupDTO.getCustomerName() + "</custName>\n" +
                "            <!--Mã giao dịch bên phía client gọi vào service của BCCS. Đảm bảo mã này là duy nhất cho mỗi lần lên doanh thu:-->\n" +
                "            <transCode>" + transCode + "</transCode>\n" +
                "         </revenueInforDTO>\n" +
                "         <!--Zero or more repetitions:-->\n" +
                "         <lstRevenueItem>\n" +
                "            <!--Mã mặt hàng trên BCCS tương ứng với doanh thu của client tương ứng: Dịch vụ Camera trên hệ thống test truyền vào BP_A_400000, hệt hống thật thì xin khai báo từ P.QT và P.TC:-->\n" +
                "            <stockModelCode>" + topupDTO.getStockModelCode() + "</stockModelCode>\n" +
                "            <price>\n" +
                "            \t<!--Đơn giá:-->\n" +
                "              <price>" + topupDTO.getPrice() + "</price>\n" +
                "              <!--Thuế suất (Truyền vào thuế suất tương ứng: 0; 5; 10):-->\n" +
                "              <vat>" + topupDTO.getVat() + "</vat>\n" +
                "              </price>\n" +
                "              <!--Số lượng:-->\n" +
                "           <quantity>" + topupDTO.getQuantity() + "</quantity>\n" +
                "         </lstRevenueItem>\n" +
                "         <!--Ngày giao dịch theo định dạng: YYYY-MM-DDTHH24:MI:SSS; ví dụ: 2017-07-14T15:00:10:-->\n" +
                "         <saleTransDate>" + saleTransDate + "</saleTransDate>\n" +
                "         <!--Đối tượng lên doanh thu:-->\n" +
                "         <revenueObjectCode>" + revenueObjectCode + "</revenueObjectCode>\n" +
                "         <!--1: Lên doanh thu Đại lý:-->\n" +
                "         <!--1: 2: Lên doanh thu nhân viên; CTV/Điểm bán:-->\n" +
                "         <transType>" + topupDTO.getTransType() + "</transType>\n" +
                "         <!--Loại giao dịch (dùng để phân biệt loại doanh thu trên BCCS, BCCS sẽ cung cấp tham số này cho hệ thống cung cấp dịch vụ cần đẩy doanh thu sang BCCS, hệ thống cung cấp dịch vụ cần cung cấp lại tên của dịch vụ đang kinh doanh để BCCS khai báo bổ sung trên BCCS):-->\n" +
                "         <saleTransType>" + topupDTO.getSaleTransType() + "</saleTransType>\n" +
                "         <!--Optional:-->\n" +
                "         <username>" + usernameTopUp + "</username>\n" +
                "         <!--Optional:-->\n" +
                "         <password>" + passwordTopUp + "</password>\n" +
                "      </proc:doReceiveRevenueGeneralOnBCCSFull>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return doReceiveRevenueGeneralOnBCCSFull;
    }

    /***
     * Call webservice bccs
     * @param wsURL
     * @param actTypeId
     * @param authentication
     * @param xmlInput
     * @param step
     * @return
     */
    private String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step) {
        String outputString = "";
        String req = null, ip = null;
        long end;
        long start = 0L;
        HttpURLConnection httpConn = null;
        try {
            if (step <= Integer.parseInt(numberRetry)) {
                String soapAction = "\"\"";
                String responseString = "";
                URL url = new URL(wsURL);
                URLConnection connection = url.openConnection();
                httpConn = (HttpURLConnection) connection;
                java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
                byte[] buffer = xmlInput.getBytes();
                bout.write(buffer);
                byte[] b = bout.toByteArray();
                httpConn.setRequestProperty("Content-Length",
                        String.valueOf(b.length));
                httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                httpConn.setRequestProperty("SOAPAction", soapAction);
                httpConn.setRequestMethod("POST");
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                httpConn.setConnectTimeout(Integer.parseInt(timeOut));
                req = xmlInput;
                ip = InetAddress.getLocalHost().getHostAddress();
                start = System.currentTimeMillis();
                try (OutputStream out = httpConn.getOutputStream()) {
                    out.write(b);
                }

                InputStreamReader isr =
                        new InputStreamReader(httpConn.getInputStream());
                BufferedReader in = new BufferedReader(isr);

                while ((responseString = in.readLine()) != null) {
                    outputString = outputString + responseString;
                }
                end = System.currentTimeMillis() - start;
                writeLog(req, outputString, url.toString(), ip, end, actTypeId, authentication, httpConn.getRequestMethod(), "1");
            }
        } catch (Exception e) {
            LOGGER.error("Has error call WSRegister", e);
            end = System.currentTimeMillis() - start;
            if (httpConn != null && req != null) {
                writeLog(req, outputString, wsURL, ip, end, actTypeId, authentication, httpConn.getRequestMethod(), "0");
                requestSOAP(wsURL, actTypeId, authentication, xmlInput, ++step);
            }
        }
        return outputString;
    }

    /***
     * doc du lieu tra ve tu soap webservice
     * @param response
     * @return
     * @throws IOException
     * @throws XMLStreamException
     * @throws JAXBException
     */
    private TopupCashResponseDTO readSoapResponse(String response) throws IOException, XMLStreamException, JAXBException {
        XMLStreamReader xsr = readResponseCommon(response);
        while (xsr.hasNext()) {
            int eventType = xsr.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = xsr.getLocalName();
                    if ("return".equals(elementName)) {
                        return readXmlResult(xsr);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    break;
            }
        }
        return null;
    }

    /***
     * doc data tu element return
     * @param xsr
     * @return
     * @throws JAXBException
     */
    private TopupCashResponseDTO readXmlResult(XMLStreamReader xsr) throws JAXBException {
        TopupCashResponseDTO topupCashResponse = null;
        JAXBContext jc = JAXBContext.newInstance(TopupCashResponseDTO.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<TopupCashResponseDTO> je = unmarshaller.unmarshal(xsr, TopupCashResponseDTO.class);
        topupCashResponse = je.getValue();
        return topupCashResponse;
    }

    /**
     * Xu ly respone tra ve
     *
     * @param response
     */
    private XMLStreamReader readResponseCommon(String response) throws IOException, XMLStreamException {
        File file = new File("input.xml");
        FileWriter writer = null;
        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = null;
        try {
            writer = new FileWriter(file);
            file.createNewFile();
            writer.write(response);
            writer.flush();
            xsr = xif.createXMLStreamReader(new FileReader(file));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return xsr;
    }

    /***
     * ghi log wsAudit
     * @param req
     * @param resp
     * @param url
     * @param ip
     * @param timeCallWs
     * @param actTypeId
     * @param authentication
     * @param wsCallType
     * @param status
     */
    public WsAuditEntity writeLog(String req, String resp, String url, String ip, long timeCallWs, long actTypeId,
                                  Authentication authentication, String wsCallType, String status) {
        try {
            WsAuditEntity wsAuditEntity = new WsAuditEntity();
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setActTypeId(actTypeId);
            wsAuditEntity.setSourceAppId(FnCommon.getClientId(authentication));
            wsAuditEntity.setFinishTime(timeCallWs);
            wsAuditEntity.setActionUserName(FnCommon.getUserLogin(authentication));
            wsAuditEntity.setRequestTime(new Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            wsAuditEntity.setIpPc(ip);
            wsAuditEntity.setDestinationAppId("BCCS");
            if (resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            return wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOGGER.error("write log ws_audit failed", e);
        }
        return null;
    }
}
