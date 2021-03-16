package com.viettel.etc.services.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.TopupAccountDTO;
import com.viettel.etc.dto.keycloak.ResUserKeycloakDTO;
import com.viettel.etc.dto.keycloak.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.ActionAuditService;
import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.services.KeycloakService;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.services.tables.OtpServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.utils.exceptions.BooException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class KeycloakServiceImpl implements KeycloakService {
    protected final Logger LOGGER = LoggerFactory.getLogger(KeycloakServiceImpl.class);

    @Value("${ws.keycloak.register.user}")
    private String wsRegisterUser;

    @Value("${ws.keycloak.user.search}")
    private String userKeycloakUrl;

    @Value("${ws.keycloak.user.groups}")
    private String userGroupsUrl;

    @Value("${ws.keycloak.lock.user}")
    private String lockUserUrl;

    @Value("${ws.keycloak.reset-pass.user}")
    private String resetPassUrl;

    @Value("${sms.user.reset-password}")
    private String smsResetPassword;

    @Value("${sms.user.lock-user}")
    private String smsLockUser;

    @Value("${sms.user.unlock-user}")
    private String smsUnLockUser;

    @Value("${ws.keycloak.login}")
    private String wsLogin;

    @Value("${ws.keycloak.logout}")
    private String wsLogout;

    @Value("${user.keycloak.admin.username}")
    private String wsAdminUser;

    @Value("${user.keycloak.admin.password}")
    private String wsAdminPassword;

    @Value("${user.keycloak.admin.client.id}")
    private String wsClientId;

    @Value("${keycloak.credentials.secret}")
    private String wsSecret;

    @Value("${ws.keycloak.get.all.user}")
    private String getAllUser;

    @Value("${ws.keycloak.count.user}")
    private String totalCountUser;

    @Value("${ws.keycloak.group.user}")
    private String groupUser;

    @Value("${ws.keycloak.detail.user}")
    private String detailUser;

    @Autowired
    private CustomerServiceJPA customerServiceJPA;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private SMSServiceImpl smsService;

    @Autowired
    private ContractServiceJPA contractServiceJPA;

    @Autowired
    private OtpServiceJPA otpServiceJPA;

    @Autowired
    JedisCacheService jedisCacheService;
    /**
     * Import du lieu tai khoan vao keycloak
     *
     * @param authentication
     * @param fileImport
     * @return
     * @throws IOException
     */
    @Override
    public ResponseEntity<?> createUserKeycloak(Authentication authentication, MultipartFile fileImport, String group) throws IOException {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                if (hasEmptyAllCellOnRow(sheet.getRow(i), 1, 11)) {
                    if (validateContentData(sheet.getRow(i))) {
                        handleContentRowExcel(sheet.getRow(i), authentication, group);
                    } else {
                        Cell result = sheet.getRow(i).createCell(12);
                        result.setCellType(CellType.STRING);
                        result.setCellValue("Fail");
                        Cell description = sheet.getRow(i).createCell(13);
                        description.setCellType(CellType.STRING);
                        description.setCellValue("Du lieu khong thoa man dieu kien");
                    }
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
        }
        return FnCommon.returnFileExcel(bytes, fileName);
    }

    @Override
    public Object findCTVAccount(TopupAccountDTO params, Authentication authentication) {
        String searchKey = params.getAccountUser() == null ? "" : params.getAccountUser();
        searchKey = searchKey.toUpperCase();
        String token = FnCommon.getStringToken(authentication);
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("briefRepresentation", "true");
        reqParams.put("username", searchKey);
        String responseUser = FnCommon.doGetRequest(userKeycloakUrl, reqParams, token);
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<ResUserKeycloakDTO>>() {
        }.getType();
        ArrayList<ResUserKeycloakDTO> userArray = gson.fromJson(responseUser, userListType);
        for (ResUserKeycloakDTO userKeycloakDTO : userArray) {
            userKeycloakDTO.setUsername(userKeycloakDTO.getUsername().toUpperCase());
            if (userKeycloakDTO.getUsername().equals(searchKey)) {
                return userKeycloakDTO;
            }
        }
        return null;
    }

    @Override
    public List<ResUserKeycloakDTO.GroupUserKeycloak> getUserGroups(String userId, Authentication authentication) {
        String token = FnCommon.getStringToken(authentication);
        String url = userGroupsUrl.replace("{id}", userId);
        String responseUser = FnCommon.doGetRequest(url, null, token);
        Gson gson = new Gson();
        Type groupType = new TypeToken<ArrayList<ResUserKeycloakDTO.GroupUserKeycloak>>() {
        }.getType();
        return gson.fromJson(responseUser, groupType);
    }

    /**
     * @return lay danh sach user
     * @throws IOException
     */
    @Override
    public Object getUser(ReqUserKeycloakDTO reqUserKeycloakDTO, Authentication authentication) throws IOException {
        int pageSize = reqUserKeycloakDTO.getPageSize() == 0 ? 10 : reqUserKeycloakDTO.getPageSize();
        int pageIndex = reqUserKeycloakDTO.getPageIndex() == 0 ? 1 : reqUserKeycloakDTO.getPageIndex();
        String token = FnCommon.getStringToken(authentication);
        int total = getAllUserByKeywork(token, reqUserKeycloakDTO.getKeyword());
        int first = pageIndex * pageSize > total ? 0 : pageIndex * pageSize;
        String url = getAllUser.replace("{first}", String.valueOf(first)).replace("{max}", String.valueOf(pageSize)) + "&search=" + reqUserKeycloakDTO.getKeyword();
        String responseUser = FnCommon.doGetRequest(url, null, token);
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<ResUserKeycloakDTO>>() {
        }.getType();
        ArrayList<ResUserKeycloakDTO> userArray = gson.fromJson(responseUser, userListType);
        if (userArray != null && !userArray.isEmpty()) {
            for (ResUserKeycloakDTO resUserKeycloakDTO : userArray) {
                String url1 = groupUser.replace("{id}", resUserKeycloakDTO.getId());
                String responseGroup = FnCommon.doGetRequest(url1, null, token);
                if (!"".equals(responseGroup)) {
                    Type groupListType = new TypeToken<ArrayList<ResUserKeycloakDTO.GroupUserKeycloak>>() {
                    }.getType();
                    ArrayList<ResUserKeycloakDTO.GroupUserKeycloak> groupArray = gson.fromJson(responseGroup, groupListType);
                    resUserKeycloakDTO.setGroup(groupArray);
                }
                String url2 = detailUser.replace("{id}", resUserKeycloakDTO.getId());
                String resUserDetail = FnCommon.doGetRequest(url2, null, token);
                ResUserKeycloakDTO userDetail = gson.fromJson(resUserDetail, ResUserKeycloakDTO.class);
                resUserKeycloakDTO.setAttributes(userDetail.getAttributes());
            }
        }
        ResultSelectEntity result = new ResultSelectEntity();
        result.setCount(total);
        result.setListData(userArray);
        return result;
    }

    /**
     * khoa tai khoan khach hang
     * API App CPT
     *
     * @param params         params client
     * @param authentication thong tin user
     * @return thanh cong
     */
    @Override
    public Object lockUser(String userId, ReqChangePassUserKeycloakDTO params, Authentication authentication, String ip) throws IOException {
        String token = FnCommon.getStringToken(authentication);
        boolean isLock = params.getEnabled();
        String url = lockUserUrl.replace("{userId}", userId);
        Long acTypeId = params.getActTypeId();
        Long actReasonId = params.getActReasonId();
        if (!params.getEnabled()) {
            if (acTypeId == null) {
                throw new EtcException("validate.act.type.require");
            }
        }
        params.setActTypeId(null);
        params.setActReasonId(null);
        RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(params));
        Response response = FnCommon.doPutRequest(url, token, body);
        ResKeycloakDTO resKeycloakDTO = new Gson().fromJson(response.body().string(), ResKeycloakDTO.class);
        if (resKeycloakDTO == null) {
            resKeycloakDTO = new ResKeycloakDTO();
        }
        resKeycloakDTO.setCode(response.code());
        resKeycloakDTO.setMessage(response.message());
        // reset keycloak thanh cong
        if (resKeycloakDTO.getCode() == HttpStatus.NO_CONTENT.value()) {
            ContractEntity contractEntity = contractServiceJPA.findContractByAccountUserId(userId);
            if (contractEntity != null) {
                if (!isLock) {
                    String msg = String.format(smsLockUser, contractEntity.getAccountUser());
                    smsService.sendSMS(contractEntity.getNoticePhoneNumber(), msg, authentication);
                } else {
                    String msg = String.format(smsUnLockUser, contractEntity.getAccountUser());
                    smsService.sendSMS(contractEntity.getNoticePhoneNumber(), msg, authentication);
                }

                contractEntity.setIsLock(isLock ? 0L : 1L);
                contractServiceJPA.save(contractEntity);
                if (!params.getEnabled()) {
                    ActionAuditEntity actionAudit = new ActionAuditEntity();
                    actionAudit.setActTypeId(acTypeId);
                    actionAudit.setActReasonId(actReasonId);
                    actionAudit.setCustId(contractEntity.getCustId());
                    actionAudit.setContractId(contractEntity.getContractId());
                    actionAudit.setActionUserName(FnCommon.getUserLogin(authentication));
                    actionAudit.setActionUserFullName(FnCommon.getUserLogin(authentication));
                    actionAudit.setAppId(Constants.MODULE_CRM);
                    actionAudit.setIpPc(ip);
                    actionAudit.setStatus(1L);
                    actionAuditService.updateLogToActionAudit(actionAudit);
                }
            }
        }
        return resKeycloakDTO;
    }

    /**
     * reset pass tai khoan khach hang
     * API App CPT
     *
     * @param authentication thong tin user
     * @return thanh cong
     */
    @Override
    public Object resetPassUser(String userId, Authentication authentication) throws IOException {
        String token = FnCommon.getStringToken(authentication);
        String newPassword = RandomStringUtils.randomNumeric(6);
        ReqChangePassUserKeycloakDTO params = new ReqChangePassUserKeycloakDTO();
        params.setTemporary(false);
        params.setType("password");
        params.setValue(newPassword);
        String url = resetPassUrl.replace("{userId}", userId);
        RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(params));
        Response response = FnCommon.doPutRequest(url, token, body);
        ResKeycloakDTO resKeycloakDTO = new Gson().fromJson(response.body().string(), ResKeycloakDTO.class);
        if (resKeycloakDTO == null) {
            resKeycloakDTO = new ResKeycloakDTO();
        }
        resKeycloakDTO.setCode(response.code());
        resKeycloakDTO.setMessage(response.message());

        // reset keycloak thanh cong
        if (resKeycloakDTO.getCode() == HttpStatus.NO_CONTENT.value()) {
            ContractEntity contractEntity = contractServiceJPA.findContractByAccountUserId(userId);
            if (contractEntity != null) {
                String msg = String.format(smsResetPassword, contractEntity.getAccountUser(), newPassword);
                smsService.sendSMS(contractEntity.getNoticePhoneNumber(), msg, authentication);
            }
        }

        return resKeycloakDTO;
    }

    /**
     * reset pass tai khoan khach hang
     * API App CPT
     *
     * @param params params client
     * @return thanh cong
     */
    @Override
    public Object resetPassUserCPT(ReqResetPassDTO params) throws IOException {
        OtpIdentify otpId = new OtpIdentify();
        otpId.setPhone(params.getPhone());
        otpId.setConfirmType(OtpIdentify.RESET_PASSWORD);
        if (!otpServiceJPA.existsById(otpId)) {
            throw new EtcException("validate.otp.not.exist");
        }
        OtpEntity otpEntity = otpServiceJPA.getById(otpId);
        if (!otpEntity.getOtp().equals(params.getOtp())) {
            throw new EtcException("validate.otp.wrong");
        }
        long diff = new Date().getTime() - otpEntity.getSignDate().getTime();
        if (diff > otpEntity.getDuration() * 1000 * 60) {   // diff in minute
            throw new EtcException("validate.opt.expired");
        }
        otpServiceJPA.delete(otpEntity);
        ReqChangePassUserKeycloakDTO reqChangePassUserKeycloakDTO = new ReqChangePassUserKeycloakDTO();
        reqChangePassUserKeycloakDTO.setValue(params.getValue());
        reqChangePassUserKeycloakDTO.setTemporary(false);
        reqChangePassUserKeycloakDTO.setType("password");
        ContractEntity contractByUser = contractServiceJPA.getByAccountUser(params.getUser());
        ContractEntity contractByAlias = contractServiceJPA.getByAccountAlias(params.getUser());
        ContractEntity contractEntity = contractByUser == null ? contractByAlias : contractByUser;
        if (contractEntity == null) {
            throw new EtcException("crm.contract.not.belong.customer");
        }
        CustomerEntity customer = customerServiceJPA.getOne(contractEntity.getCustId());
        if (customer == null) {
            throw new EtcException("crm.customer.not.exist");
        }
        if (!params.getPhone().equals(customer.getPhoneNumber()) &&
                !params.getPhone().equals(customer.getRepPhoneNumber()) &&
                !params.getPhone().equals(customer.getAuthPhoneNumber()) &&
                !params.getPhone().equals(contractEntity.getNoticePhoneNumber())) {
            throw new EtcException("validate.phone.not.belong");
        }
        String userId = contractEntity.getAccountUserId() == null ? "" : contractEntity.getAccountUserId();
        String url = resetPassUrl.replace("{userId}", userId);
        RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(reqChangePassUserKeycloakDTO));
        String token = getAdminToken();
        Response response = FnCommon.doPutRequest(url, token, body);
        ResKeycloakDTO resKeycloakDTO = new ResKeycloakDTO();
        if (response != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ResKeycloakDTO>() {
            }.getType();
            resKeycloakDTO = gson.fromJson(response.body().string(), type);
            resKeycloakDTO = resKeycloakDTO == null ? new ResKeycloakDTO() : resKeycloakDTO;
            resKeycloakDTO.setCode(response.code());
            resKeycloakDTO.setMessage(response.message());
        }
        return resKeycloakDTO;
    }

    /**
     * change pass tai khoan khach hang
     * API App CPT
     *
     * @param params params client
     * @return thanh cong
     */
    @Override
    public Object changePassUser(ReqChangePassDTO params, Authentication authentication) throws IOException {
        String userId = FnCommon.getIdUserLogin(authentication);
        userId = userId == null ? "" : userId;
        ResKeycloakDTO resKeycloakDTO = new ResKeycloakDTO();
        String token = getUserToken(wsLogin, params.getValue(), params.getClientSecret(), authentication);
        if (token == null || "".equals(token)) {
            throw new EtcException("crm.password.wrong");
        }
        ReqChangePassUserKeycloakDTO reqChangePassUserKeycloakDTO = new ReqChangePassUserKeycloakDTO();
        reqChangePassUserKeycloakDTO.setValue(params.getNewValue());
        reqChangePassUserKeycloakDTO.setTemporary(false);
        reqChangePassUserKeycloakDTO.setType("password");
        String url = resetPassUrl.replace("{userId}", userId);
        RequestBody body = RequestBody.create(Constants.JSON, FnCommon.toStringJson(reqChangePassUserKeycloakDTO));
        Response response = FnCommon.doPutRequest(url, getAdminToken(), body);
        if (response != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ResKeycloakDTO>() {
            }.getType();
            resKeycloakDTO = gson.fromJson(response.body().string(), type);
            resKeycloakDTO = resKeycloakDTO == null ? new ResKeycloakDTO() : resKeycloakDTO;
            resKeycloakDTO.setCode(response.code());
            resKeycloakDTO.setMessage(response.message());
        }
        return resKeycloakDTO;
    }

    /**
     * dang nhap
     *
     * @param params params client
     * @return thanh cong
     */
    @Override
    public ResLoginDTO login(ReqLoginDTO params) {
        if (!validateUserLoginApp(params)) {
            ResLoginDTO res = new ResLoginDTO();
            res.setCode(HttpStatus.UNAUTHORIZED.value());
            res.setError(String.valueOf(jedisCacheService.getCodeErrorByKey("common.login.user.not-exists")));
            res.setError_description(jedisCacheService.getMessageErrorByKey("common.login.user.not-exists"));
            return res;
        }

        ContractEntity contract = contractServiceJPA.getByAccountAlias(params.getUsername());
        if (contract != null) {
            params.setUsername(contract.getAccountUser());
        }
        ResLoginDTO result = getUserLoginDetail(wsLogin, params);
        if (result != null && HttpStatus.OK.value() != result.getCode()) {
            result.setCode(result.getCode());
            result.setError(result.getError());
            result.setError_description(jedisCacheService.getMessageErrorByKey(result.getError_description()));
        }

        // Co cho super app
        if (result != null && HttpStatus.OK.value() == result.getCode() && contract != null) {
            result.setIsETC(contract.getIsEtc());
        }
        return result;
    }

    /**
     * dang xuat
     *
     * @param params params client
     * @return thanh cong
     */
    @Override
    public ResLoginDTO logout(ReqLogoutDTO params) throws IOException {
        Response response = logout(wsLogout, params);
        if (response != null && HttpStatus.NO_CONTENT.value() == response.code()) {
            return null;
        }
        ResLoginDTO responseDTO;
        if (response != null) {
            responseDTO = new Gson().fromJson(response.body().string(), ResLoginDTO.class);
            responseDTO.setCode(response.code());
            return responseDTO;
        }
        throw new EtcException("common.validate.data.empty");
    }

    @Override
    public void alias(String aliasName, Authentication authentication) {
        ContractEntity contractAlias = contractServiceJPA.getByAccountUser(FnCommon.getUserLogin(authentication));
        if (aliasName.equals(contractAlias.getAccountAlias())) {
            return;
        }
        ContractEntity contractByAlias = contractServiceJPA.getByAccountAlias(aliasName);
        ContractEntity contractByUser = contractServiceJPA.getByAccountUser(aliasName);
        if (contractByAlias == null && contractByUser == null) {
            contractAlias.setAccountAlias(aliasName);
            contractServiceJPA.save(contractAlias);
            return;
        }
        throw new EtcException("crm.username.already.exist");
    }

    /**
     * dang nhap
     *
     * @param params params client
     * @return thanh cong
     */
    @Override
    public ResLoginDTO loginBoo(ReqLoginDTO params) {
        ResLoginDTO result = getUserLoginDetail(wsLogin, params);
        if (result != null) {
            if (HttpStatus.OK.value() != result.getCode()) {
                throw new BooException(result.getError(), result.getError_description(), result.getCode());
            }
        }
        return result;
    }


    /**
     * Lay token admin
     *
     * @return
     */
    @Override
    public String getAdminToken() {
        OkHttpClient client = new OkHttpClient();
        AdminDTO adminAccount = new AdminDTO(wsAdminUser, wsAdminPassword, wsClientId, wsSecret);
        RequestBody body = RequestBody.create(Constants.FORM_URL_ENCODED, adminAccount.toString());
        try {
            FnCommon.setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(wsLogin).newBuilder();
            Request request = new Request.Builder()
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .url(httpBuilder.build())
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            ResKeycloakDTO resKeycloakDTO;
            if (response != null) {
                resKeycloakDTO = new Gson().fromJson(response.body().string(), ResKeycloakDTO.class);
                return resKeycloakDTO.getAccess_token();
            }
        } catch (Exception e) {
            LOGGER.error("Method getAdminToken error", e);
        }
        return null;
    }

    /**
     * Lay token user
     *
     * @return
     */
    @Override
    public String getUserToken(String wsLogin, String pass, String clientSecret, Authentication authentication) {
        OkHttpClient client = new OkHttpClient();
        UserDTO userAccount = new UserDTO(pass, FnCommon.getClientId(authentication), FnCommon.getUserLogin(authentication), clientSecret);
        RequestBody body = RequestBody.create(Constants.FORM_URL_ENCODED, userAccount.toString());
        try {
            FnCommon.setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(wsLogin).newBuilder();
            Request request = new Request.Builder()
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .url(httpBuilder.build())
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            ResKeycloakDTO resKeycloakDTO;
            if (response != null) {
                resKeycloakDTO = new Gson().fromJson(response.body().string(), ResKeycloakDTO.class);
                return resKeycloakDTO.getAccess_token();
            }
        } catch (Exception e) {
            LOGGER.error("Method getUserToken error", e);
        }
        return null;
    }

    /***
     * Logout session
     * @param wsLogout
     * @param reqBody
     * @return
     */
    public Response logout(String wsLogout, ReqLogoutDTO reqBody) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(Constants.FORM_URL_ENCODED, reqBody.toString());
        try {
            FnCommon.setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(wsLogout).newBuilder();
            Request request = new Request.Builder()
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .url(httpBuilder.build())
                    .post(body)
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            LOGGER.error("Has error", e);
        }
        return null;
    }

    /**
     * Lay thong tin dang nhap
     *
     * @return
     */
    public ResLoginDTO getUserLoginDetail(String wsLogin, ReqLoginDTO userAccount) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(Constants.FORM_URL_ENCODED, userAccount.toString());
        try {
            FnCommon.setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(wsLogin).newBuilder();
            Request request = new Request.Builder()
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .url(httpBuilder.build())
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            ResLoginDTO responseDTO;
            if (response != null) {
                responseDTO = new Gson().fromJson(response.body().string(), ResLoginDTO.class);
                responseDTO.setCode(response.code());
                return responseDTO;
            }
        } catch (Exception e) {
            LOGGER.error("Has error", e);
        }
        return null;
    }

    private boolean validateUserLoginApp(ReqLoginDTO params) {
        String clientId = params.getClient_id();
        ContractEntity contractEntity = contractServiceJPA.getByAccountUser(params.getUsername().toUpperCase());
        if (clientId.equals(Constants.APP_CLIENT_ID.APP_CHU_PT) || clientId.equals(Constants.APP_CLIENT_ID.PORTAL_CHU_PT)) {
            ContractEntity contractByAlias = contractServiceJPA.getByAccountAlias(params.getUsername());
            return contractEntity != null || contractByAlias != null;
        }
        if (clientId.equals(Constants.APP_CLIENT_ID.APP_CTV_DAI_LY)) {
            return contractEntity == null;
        }

        if (clientId.equals(Constants.APP_CLIENT_ID.BOO1) || clientId.equals(Constants.APP_CLIENT_ID.MOMO) || clientId.equals(Constants.APP_CLIENT_ID.CMS) || clientId.equals(Constants.APP_CLIENT_ID.VT_PAY)) {
            return contractEntity == null;
        }
        return false;
    }


    /**
     * Dem so ban ghi r phan trang theo maxRecord
     *
     * @return
     */
    private Integer getAllUserByKeywork(String token, String keyword) {
        String response = FnCommon.doGetRequest(totalCountUser + "&search=" + keyword, null, token);
        int record = 0;
        if (response != null) {
            record = Integer.parseInt(response);
        }
        return record;
    }

    /**
     * check content row has empty or null
     *
     * @param row      Du lieu ma row can kiem tra
     * @param fromCell Cot bat dau thuc hien
     * @param toCell   Cot Ket thuc thuc hien
     * @return Chuoi kiem tra
     */
    private boolean hasEmptyAllCellOnRow(Row row, int fromCell, int toCell) {
        if (row != null) {
            for (int i = fromCell; i <= toCell; i++) {
                if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(i)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Xu ly du lieu file excel
     *
     * @param row
     * @param authentication
     * @param group
     */
    protected void handleContentRowExcel(Row row, Authentication authentication, String group) {
        Response result = callRegisterUserKeycloak(authentication, row, group);
        Cell cell = row.createCell(12);
        cell.setCellType(CellType.STRING);
        if (!Objects.isNull(result)) {
            if (result.code() == HttpStatus.CREATED.value()) {
                cell.setCellValue("Success");
            } else {
                cell.setCellValue("Fail");
                Cell description = row.createCell(13);
                description.setCellType(CellType.STRING);
                description.setCellValue(result.message());
            }
        } else {
            cell.setCellValue("Fail");
            Cell description = row.createCell(13);
            description.setCellType(CellType.STRING);
            description.setCellValue("Lỗi trong khi gọi api keycloak");
        }
    }

    /**
     * Tao tai khoan keycloak
     *
     * @param authentication
     * @param row
     * @param group
     * @return
     */
    protected Response callRegisterUserKeycloak(Authentication authentication, Row row, String group) {
        Map<String, Object> body = new HashMap<>();
        setBody(body, row);
        /**
         * Set group for account
         */

        body.put("groups", Arrays.asList(group));

        /**
         * set attribute cho account
         */
        Map<String, Object> attributes = new HashMap<>();
        setAttributes(attributes, row);
        body.put("attributes", attributes);

        Map<String, Object> password = new HashMap<>();
        password.put("type", "password");
        password.put("value", Constants.KEYCLOAK.PW_DEFAULT);
        body.put("credentials", Arrays.asList(password));
        Response response = null;
        try {
            String reqBody = new Gson().toJson(body);
            response = doPostRequest(authentication, wsRegisterUser, reqBody);
        } catch (Exception e) {
            LOGGER.error("Has ERROR", e);
        }
        return response;
    }

    /**
     * Kiem tra cac truong du lieu bat buoc khi tao tai khoan keycloak
     *
     * @param row
     * @return
     */
    private boolean validateContentData(Row row) {
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(0)))) {
            if (Constants.KEYCLOAK.VTT.equals(FnCommon.getStringValue(row.getCell(0))) &&
                    !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(1))) && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(2)))
                    && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(4))) && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(5)))
            ) {
                return true;
            } else if (Constants.KEYCLOAK.VDTC.equals(FnCommon.getStringValue(row.getCell(0))) &&
                    !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(1)))
                    && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(2))) && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(3)))
                    && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(5))) && !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(4)))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Set attributes keycloak
     *
     * @param attributes
     * @param row
     */
    private void setAttributes(Map<String, Object> attributes, Row row) {
        attributes.put(Constants.USER_ATTRIBUTE.STAFF_ID, FnCommon.getStringValue(row.getCell(1)).trim());
        attributes.put(Constants.USER_ATTRIBUTE.SHOP_ID, FnCommon.getStringValue(row.getCell(4)).trim());
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(7)))) {
            attributes.put(Constants.USER_ATTRIBUTE.PARTNER_CODE, FnCommon.getStringValue(row.getCell(7)));
        }
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(8)))) {
            attributes.put(Constants.USER_ATTRIBUTE.PARTNER_TYPE, FnCommon.getStringValue(row.getCell(8)));
        }
        if (Constants.KEYCLOAK.VDTC.equals(FnCommon.getStringValue(row.getCell(0)))) {
            attributes.put(Constants.USER_ATTRIBUTE.WAREHOUSE_ID, FnCommon.getStringValue(row.getCell(3)).trim());
        }
        attributes.put(Constants.USER_ATTRIBUTE.SHOP_NAME, FnCommon.getStringValue(row.getCell(5)).trim());
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(6)))) {
            attributes.put(Constants.USER_ATTRIBUTE.SHOP_CODE, FnCommon.getStringValue(row.getCell(6)).trim());
        }
    }

    /**
     * Set du lieu cho body
     *
     * @param body
     * @param row
     */
    private void setBody(Map<String, Object> body, Row row) {
        body.put("username", FnCommon.getStringValue(row.getCell(2)).trim());
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(9)))) {
            body.put("email", FnCommon.getStringValue(row.getCell(9)));
        }
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(10)))) {
            body.put("firstName", FnCommon.getStringValue(row.getCell(10)));
        }
        if (!FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(11)))) {
            body.put("lastName", FnCommon.getStringValue(row.getCell(11)));
        }
        body.put("enabled", true);
    }


    /**
     * Thuc hien goi api keycloak voi phuog thuc get
     *
     * @param restTemplate
     * @param body
     * @param headers
     * @param url
     * @param params
     * @param <T>
     * @return
     */
    private <T> ResponseEntity<String> getExchange(RestTemplate restTemplate, ObjectNode body, HttpHeaders headers, String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!Objects.isNull(params)) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                builder.queryParam(param.getKey(), param.getValue());
            }
        }
        HttpEntity httpEntity;
        if (!Objects.isNull(body)) {
            httpEntity = new HttpEntity<>(body, headers);
        } else {
            httpEntity = new HttpEntity<>(headers);
        }

        return restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET, httpEntity,
                String.class);
    }

    /**
     * Thuc hien request voi phuong thuc post
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response doPostRequest(Authentication authentication, String url, String reqBody) {
        Response response = null;
        Request request = null;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        try {
            //check step == config
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            RequestBody body = RequestBody.create(Constants.JSON, reqBody);
            String token = FnCommon.getStringToken(authentication);
            request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .url(httpBuilder.build())
                    .post(body)
                    .build();
            response = client.newCall(request).execute();

        } catch (Exception exception) {
            LOGGER.error("Loi khi thuc hien tao tai khoan keycloak", exception);

        }
        return response;
    }
}
