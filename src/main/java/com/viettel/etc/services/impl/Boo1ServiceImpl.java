package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.boo.*;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.services.Boo1Service;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class Boo1ServiceImpl implements Boo1Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Boo1ServiceImpl.class);

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    @Value("${ws.boo1.activation-check}")
    String wslActivationCheck;

    @Value("${ws.boo1.cancel.ticket}")
    String wslCancelTicket;

    @Value("${ws.boo1.cancel.result}")
    String wslCancelResult;

    @Value("${ws.boo1.calculator.result}")
    String wslCalculatorTicket;

    @Value("${ws.ocs.time-out}")
    Long timeOut;

    @Value("${ws.ocs.retry}")
    Long numberRetry;

    @Value("${boo1.url.token}")
    String urlTokenBoo;

    @Value("${boo1.grant.type}")
    String grantType;

    @Value("${boo1.username}")
    String username;

    @Value("${boo1.password}")
    String password;

    @Value("${boo1.client_id}")
    String clientId;

    @Value("${boo1.client_secret}")
    String clientSecret;

    @Value("${ws.boo1.charge.ticket}")
    String wslChargeTicket;

    @Value("${ws.boo1.online-event.sync}")
    String wslOnlineEventSync;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResActivationCheckDTO findVehicleByPlateNumber(ReqActivationCheckDTO req, Authentication authentication, Long actTypeId) {
        try {
            ResActivationCheckDTO res = null;
            if (!Objects.isNull(req) && !FnCommon.isNullOrEmpty(req.getPlate())) {
                String params = new Gson().toJson(req);
                String resBoo = doPostBoo1Request(wslActivationCheck, params, 0, "", actTypeId);
                if (!Objects.isNull(resBoo) && resBoo.contains("etag")) {
                    res = new Gson().fromJson(resBoo, ResActivationCheckDTO.class);
                }
            }else{
                throw new EtcException("common.validate.plate.number");
            }
            return res;
        } catch (Exception ex) {
            LOGGER.error("Loi khi thuc hien goi Boo1", ex);
            return null;
        }
    }

    /**
     * Huy ve thang quy
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResCancelTicketDTO destroyTicket(ReqCancelTicketDTO request) {
        try {
            ResCancelTicketDTO resCancelTicketDTO = null;
            if (!Objects.isNull(request)) {
                String params = new Gson().toJson(request);
                String resBoo = doPostBoo1Request(wslCancelTicket, params, 1, "", Constants.ACT_TYPE.ADD_VEHICLE);
                if (!Objects.isNull(resBoo)) {
                    resCancelTicketDTO = new Gson().fromJson(resBoo, ResCancelTicketDTO.class);
                }
            }
            return resCancelTicketDTO;
        } catch (Exception ex) {
            LOGGER.error("Loi khi thuc hien goi Boo1", ex);
            return null;
        }
    }

    /**
     * BOT confirm
     *
     * @param requestCancelResult
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResCancelResultDTO cancelResult(ReqCancelResultDTO requestCancelResult) {
        try {
            ResCancelResultDTO resCancelResultDTO = null;
            if (!Objects.isNull(requestCancelResult)) {
                String params = new Gson().toJson(requestCancelResult);

                String resBoo = doPostBoo1Request(wslCancelResult, params, 1, "", Constants.ACT_TYPE.ADD_VEHICLE);
                if (!Objects.isNull(resBoo)) {
                    resCancelResultDTO = new Gson().fromJson(resBoo, ResCancelResultDTO.class);
                }
            }
            return resCancelResultDTO;
        } catch (Exception ex) {
            LOGGER.error("Loi khi thuc hien goi Boo1", ex);
            return null;
        }
    }

    /**
     * Lay thong tin gia ve BOO1
     *
     * @param reqCalculatorTicketDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResCalculatorTicketDTO calculatorTicketBoo1(ReqCalculatorTicketDTO reqCalculatorTicketDTO, Authentication authentication) throws IOException {
        Map<String, String> params = new HashMap<>();
        if (Objects.nonNull(reqCalculatorTicketDTO.getStation_type())) {
            params.put("station_type", reqCalculatorTicketDTO.getStation_type());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getStation_in_id())) {
            params.put("station_in_id", reqCalculatorTicketDTO.getStation_in_id().toString());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getStation_out_id())) {
            params.put("station_out_id", reqCalculatorTicketDTO.getStation_out_id().toString());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getTicket_type())) {
            params.put("ticket_type", reqCalculatorTicketDTO.getTicket_type());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getStart_date())) {
            params.put("start_date", reqCalculatorTicketDTO.getStart_date());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getEnd_date())) {
            params.put("end_date", reqCalculatorTicketDTO.getEnd_date());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getPlate())) {
            params.put("plate", reqCalculatorTicketDTO.getPlate());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getEtag())) {
            params.put("etag", reqCalculatorTicketDTO.getEtag());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getVehicle_type())) {
            params.put("vehicle_type", reqCalculatorTicketDTO.getVehicle_type().toString());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getRegister_vehicle_type())) {
            params.put("register_vehicle_type", reqCalculatorTicketDTO.getRegister_vehicle_type());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getSeat())) {
            params.put("seat", reqCalculatorTicketDTO.getSeat().toString());
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getWeight_goods())) {
            params.put("weight_goods", String.valueOf(reqCalculatorTicketDTO.getWeight_goods().intValue()));
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getWeight_all())) {
            params.put("weight_all", String.valueOf(reqCalculatorTicketDTO.getWeight_all().intValue()));
        }
        if (Objects.nonNull(reqCalculatorTicketDTO.getRequest_datetime())) {
            params.put("request_datetime", reqCalculatorTicketDTO.getRequest_datetime().toString());
        }
        String strResp = doGetBoo1Request(wslCalculatorTicket, params, 1, "", authentication);
        return new Gson().fromJson(strResp, ResCalculatorTicketDTO.class);
    }

    /**
     * Thuc hien request voi phuong thuc post
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String doPostBoo1Request(String url, String params, int step, String reqBody, Long actTypeId) throws IOException {
        long start = 0L, processTime = 0L;
        String responseString = null;
        Request request = null;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            if (step <= numberRetry) {
                String tokenBoo1 = getTokenBoo1();
                if (!FnCommon.isNullOrEmpty(tokenBoo1)) {
                    ResTokenBoo1DTO resTokenBoo1DTO = new Gson().fromJson(tokenBoo1, ResTokenBoo1DTO.class);
                    //check step == config
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
                    RequestBody body = RequestBody.create(Constants.JSON, params);
                    request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + resTokenBoo1DTO.getAccess_token())
                            .url(httpBuilder.build())
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    LOGGER.info("BOO1 request url={} params={}",url ,params);
                    responseString = response.body().string();
                    LOGGER.info("BOO1 response body={}", responseString);
                    writeLog(buildLogRequest(request, params), responseString, url, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.SUCCESS.value, actTypeId);
                    return responseString;
                }
            }

        } catch (Exception exception) {
            LOGGER.error("Loi khi thuc hien goi BOO1", exception);
            if (request != null) {
                processTime = System.currentTimeMillis() - start;
                updateWriteLogFail(wsAuditEntity);
                writeLog(buildLogRequest(request, reqBody), responseString, url, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.NOT_SUCCESS.value, Constants.ACT_TYPE.ADD_VEHICLE);
                doPostBoo1Request(url, params, ++step, reqBody, actTypeId);
            }
        }
        return responseString;
    }

    /**
     * Thuc hien request voi phuong thuc get BOO1
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String doGetBoo1Request(String url, Map<String, String> params, int step, String reqBody, Authentication authentication) throws IOException {
        String responseString = null;
        long start = 0L, processTime = 0L;
        Request request = null;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            if (step < numberRetry) {
                String tokenBoo1 = getTokenBoo1();
                if (!FnCommon.isNullOrEmpty(tokenBoo1)) {
                    ResTokenBoo1DTO tokenBoo1DTO = new Gson().fromJson(tokenBoo1, ResTokenBoo1DTO.class);
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

                    if (params != null) {
                        for (Map.Entry<String, String> param : params.entrySet()) {
                            httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                        }
                    }
                    request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + tokenBoo1DTO.getAccess_token())
                            .url(httpBuilder.build())
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    LOGGER.info("BOO1 request url={} params={}", url, params);
                    responseString = response.body().string();
                    LOGGER.info("BOO1 response body={}", responseString);
                    writeLog(buildLogRequest(request, ""), responseString, url, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.SUCCESS.value, Constants.ACT_TYPE.ADD_VEHICLE);
                    return responseString;
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Loi khi thuc hien goi BOO1", exception);
            if (request != null) {
                updateWriteLogFail(wsAuditEntity);
                processTime = System.currentTimeMillis() - start;
                writeLog(buildLogRequest(request, reqBody), responseString, url, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.NOT_SUCCESS.value, Constants.ACT_TYPE.ADD_VEHICLE);
                doGetBoo1Request(url, params, ++step, reqBody, authentication);
            }
        }
        return responseString;
    }

    /**
     * Get token boo1
     *
     * @return
     */
    public String getTokenBoo1() throws UnknownHostException {
        String responseString = null;
        long start = 0L, processTime = 0L;
        Request request = null;
        OkHttpClient client = new OkHttpClient();
        try {
            client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
            client.setReadTimeout(30, TimeUnit.SECONDS);
            client.setWriteTimeout(30, TimeUnit.SECONDS);

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("grant_type", grantType)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .addFormDataPart("client_id", clientId)
                    .addFormDataPart("client_secret", clientSecret)
                    .build();
            request = new Request.Builder()
                    .header("Accept", "application/x-www-form-urlencoded")
                    .url(urlTokenBoo)
                    .post(requestBody)
                    .build();
            start = System.currentTimeMillis();
            Response response = client.newCall(request).execute();
            processTime = System.currentTimeMillis() - start;
            responseString = response.body().string();
            writeLog(buildLogRequest(request, ""), responseString, urlTokenBoo, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.SUCCESS.value, Constants.ACT_TYPE.ADD_VEHICLE);
        } catch (Exception exception) {
            LOGGER.error("Loi khi thuc hien goi BOO1", exception);
            if (request != null) {
                processTime = System.currentTimeMillis() - start;
                writeLog(buildLogRequest(request, ""), responseString, urlTokenBoo, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.NOT_SUCCESS.value, Constants.ACT_TYPE.ADD_VEHICLE);
            }
        }
        return responseString;
    }

    /***
     * Hàm ghi log gọi BOO1 vào bảng WS_AUDIT
     * @param req
     * @param resp
     * @param url
     * @param ip
     * @param authentication
     * @param wsCallType
     * @param status
     */
    public WsAuditEntity writeLog(String req, String resp, String url, String ip, long timeCallBOO1,
                                  Authentication authentication, String wsCallType, String status, Long actTypeId) {
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            String userLogin = "system", clientId = "crm";
            if (authentication != null) {
                userLogin = FnCommon.getUserLogin(authentication);
                clientId = FnCommon.getClientId(authentication);
            }
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setSourceAppId(clientId);
            wsAuditEntity.setFinishTime(timeCallBOO1);
            wsAuditEntity.setActionUserName(userLogin);
            wsAuditEntity.setRequestTime(new java.sql.Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            wsAuditEntity.setIpPc(ip);
            if(resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setDestinationAppId(Constants.BOO1);
            wsAuditEntity.setActTypeId(actTypeId);
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

    /**
     * Mua ve Boo1
     *
     * @param reqChargeTicketDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResChargeTicketDTO chargeTicketBoo1(ReqChargeTicketDTO reqChargeTicketDTO, Authentication authentication, Long actTypeId, Long requestOutId) throws IOException {
        String params = new Gson().toJson(reqChargeTicketDTO);
        String strResp = doPostBoo1RequestChargeTicket(wslChargeTicket, params, 1, params, authentication, actTypeId, requestOutId);
        LOGGER.info("Response cua BOO1 tra ve : " + strResp);
        return new Gson().fromJson(strResp, ResChargeTicketDTO.class);
    }

    /***
     * Hàm ghi log mua ve gọi BOO1 vào bảng WS_AUDIT
     * @param req
     * @param resp
     * @param url
     * @param ip
     * @param authentication
     * @param wsCallType
     * @param status
     */
    public WsAuditEntity writeLogChargeTicket(String req, String resp, String url, String ip, long timeCallBOO1,
                                              Authentication authentication,
                                              String wsCallType, String status, Long actTypeId, Long requestOutId) {
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            String userLogin = "system", clientId = "crm";
            if (authentication != null) {
                userLogin = FnCommon.getUserLogin(authentication);
                clientId = FnCommon.getClientId(authentication);
            }
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setSourceAppId(clientId);
            wsAuditEntity.setFinishTime(timeCallBOO1);
            wsAuditEntity.setActionUserName(userLogin);
            wsAuditEntity.setRequestTime(new java.sql.Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            wsAuditEntity.setIpPc(ip);
            if(resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setDestinationAppId(Constants.BOO1);
            wsAuditEntity.setActTypeId(actTypeId);
            ResChargeTicketDTO resChargeTicketDTO = new Gson().fromJson(resp, ResChargeTicketDTO.class);
            wsAuditEntity.setRequestOutId(String.valueOf(requestOutId));
            wsAuditEntity.setRequestInId(String.valueOf(resChargeTicketDTO.getRequest_id()));
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOGGER.error("write log ws_audit failed", e);
        }
        return wsAuditEntity;
    }

    /**
     * Thuc hien request dang ki goi cuoc phu OCS voi phuong thuc post
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String doPostBoo1RequestChargeTicket(String url, String params, int step, String reqBody, Authentication authentication, Long actTypeId, Long requestOutId) throws IOException {
        long start = 0L, processTime = 0L;
        String responseString = null;
        Request request = null;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeOut, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            if (step <= numberRetry) {
                String tokenBoo1 = getTokenBoo1();
                if (!FnCommon.isNullOrEmpty(tokenBoo1)) {
                    ResTokenBoo1DTO resTokenBoo1DTO = new Gson().fromJson(tokenBoo1, ResTokenBoo1DTO.class);
                    //check step == config
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
                    RequestBody body = RequestBody.create(Constants.JSON, params);
                    request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + resTokenBoo1DTO.getAccess_token())
                            .url(httpBuilder.build())
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    LOGGER.info("BOO1 url={}, params={}", url, params);
                    responseString = response.body().string();
                    LOGGER.info("BOO1 response body={}", responseString);
                    writeLogChargeTicket(buildLogRequest(request, reqBody), responseString, url, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.SUCCESS.value, actTypeId, requestOutId);
                    return responseString;
                }
            }

        } catch (Exception exception) {
            LOGGER.error("Loi khi thuc hien goi BOO1", exception);
            if (request != null) {
                processTime = System.currentTimeMillis() - start;
                updateWriteLogFail(wsAuditEntity);
                writeLogChargeTicket(buildLogRequest(request, reqBody), responseString, url, InetAddress.getLocalHost().getHostAddress(), processTime, null, request.method(), WsAuditEntity.Status.NOT_SUCCESS.value, actTypeId, requestOutId);
                doPostBoo1RequestChargeTicket(url, params, ++step, reqBody, authentication, actTypeId, requestOutId);
            }
        }
        return responseString;
    }

    /**
     * Ham goi api Ben boo1 thuc hien chuc nang dong bo du lieu
     *
     * @param reqOnlineEventSyncDTO
     * @param authentication
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResOnlineEventDTO onlineEventSync(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO, Authentication authentication, Long actTypeId) {
        try {
            ResOnlineEventDTO resOnlineEventDTO = null;
            String params = new Gson().toJson(reqOnlineEventSyncDTO);
            String resBoo = doPostBoo1Request(wslOnlineEventSync, params, 1, "", actTypeId);
            if (!Objects.isNull(resBoo)) {
                resOnlineEventDTO = new Gson().fromJson(resBoo, ResOnlineEventDTO.class);
            }
            return resOnlineEventDTO;
        } catch (Exception ex) {
            LOGGER.error("Loi khi thuc hien goi Boo1", ex);
            return null;
        }
    }

    /**
     * Cap nhat status goi ws fail
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
}
