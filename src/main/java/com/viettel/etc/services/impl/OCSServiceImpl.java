package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSCreateContractForm;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.dto.ocs.RemoveSupOfferRoaming;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.services.OCSService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class OCSServiceImpl implements OCSService {
    private static final Logger LOG = LoggerFactory.getLogger(OCSServiceImpl.class);

    @Value("${ws.ocs.add-vehicle}")
    private String wsAddVehicle;

    @Value("${ws.ocs.vehicle-offer-id}")
    private String vehicleOfferId;

    @Value("${ws.ocs.modify-vehicle}")
    private String wsModifyVehicle;

    @Value("${ws.ocs.new-contract}")
    private String wsNewContract;

    @Value("${ws.ocs.modify-contract}")
    private String wsModifyContract;

    @Value("${ws.ocs.delete-vehicle}")
    private String wsDeleteVehicle;

    @Value("${ws.ocs.charge}")
    private String wsCharge;

    @Value("${ws.ocs.add-sup-offer}")
    private String wsAddSupOffer;

    @Value("${ws.ocs.query-contract}")
    private String wsQueryContract;

    @Value("${ws.ocs.retry}")
    private String numberRetry;

    @Value("${ws.ocs.time-out}")
    private String timeOut;

    @Value("${ws.ocs.remove-sup-offer}")
    private String wsRemoveSupOffer;

    @Value("${ws.ocs.add-balance}")
    private String wsAddBalance;

    @Value("${ws.ocs.transfer-vehicle}")
    private String wsTransferVehicle;

    @Value("${ws.ocs.changeSupOffer}")
    String wsChangeSupOffer;

    @Value("${ws.ocs.query-vehicle}")
    String wsQueryVehicle;

    @Value("${ws.ocs.add-sup-offer-roaming}")
    private String wsAddSupOfferRoaming;

    @Value("${ws.ocs.remove-sup-offer-roaming}")
    private String wsRemoveSupOfferRoaming;

    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    public OCSServiceImpl(WsAuditRepositoryJPA wsAuditRepositoryJPA) {
        this.wsAuditRepositoryJPA = wsAuditRepositoryJPA;
    }


    /**
     * goi dau noi phuong tien sang OCS
     *
     * @param addVehicleRequestDTO params client
     * @return
     */
    @Override
    public OCSResponse createVehicleOCS(AddVehicleRequestDTO addVehicleRequestDTO, Authentication authentication, long actTypeId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("contractId", String.valueOf(addVehicleRequestDTO.getContractId()));
        params.put("TID", addVehicleRequestDTO.getTid());
        params.put("EPC", addVehicleRequestDTO.getEpc());
        params.put("plateNumber", addVehicleRequestDTO.getPlateNumber().toUpperCase());
        params.put("vehicleType", String.valueOf(addVehicleRequestDTO.getVehicleGroupId()));
        if (addVehicleRequestDTO.getEffDate() != null) {
            params.put("effDate", FnCommon.convertDateToString(addVehicleRequestDTO.getEffDate()));
        }
        if (addVehicleRequestDTO.getExpDate() != null) {
            params.put("expDate", FnCommon.convertDateToString(addVehicleRequestDTO.getExpDate()));
        }
        // loai the ma phuong tien su dung
        params.put("type", addVehicleRequestDTO.getRfidType());
        // trang thai the dang su dung 1
        params.put("status", addVehicleRequestDTO.getStatus());
        // Goi cuoc chinh ma phuong tien du dung
        params.put("offerExternalId", vehicleOfferId);
        params.put("plateType", String.valueOf(addVehicleRequestDTO.getPlateTypeCode()));
        params.put("serialNumber", addVehicleRequestDTO.getRfidSerial());
        if (Objects.nonNull(addVehicleRequestDTO.getSeatNumber())) {
            params.put("seatNumber", addVehicleRequestDTO.getSeatNumber().toString());
        }
        if (Objects.nonNull(addVehicleRequestDTO.getCargoWeight())) {
            Long payloads = Math.round(addVehicleRequestDTO.getCargoWeight());
            params.put("payloads", payloads.toString());
        }
        if (Objects.nonNull(addVehicleRequestDTO.getVehicleTypeId())) {
            params.put("detailedVehicleType", addVehicleRequestDTO.getVehicleTypeId().toString());
        }
        try {
            String result = doPostRequest(wsAddVehicle, params, "", 1, authentication, actTypeId);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /**
     * cap nhat phuong tien sang OCS
     *
     * @param updateVehicleRequestOCSDTO params client
     * @return
     */
    @Override
    public OCSResponse modifyVehicleOCS(UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO, Authentication authentication, boolean isCardService) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EPC", updateVehicleRequestOCSDTO.getEpc());

        if (updateVehicleRequestOCSDTO.getPlateTypeCode() != null) {
            params.put("plateType", updateVehicleRequestOCSDTO.getPlateTypeCode());
        }
        if (updateVehicleRequestOCSDTO.getSeatNumber() != null) {
            params.put("seatNumber", updateVehicleRequestOCSDTO.getSeatNumber().toString());
        }
        if (updateVehicleRequestOCSDTO.getStatus() != null && isCardService) {
            params.put("status", updateVehicleRequestOCSDTO.getStatus());
        }
        if (updateVehicleRequestOCSDTO.getPlateNumber() != null) {
            params.put("plateNumber", updateVehicleRequestOCSDTO.getPlateNumber().toUpperCase());
        }
        if (updateVehicleRequestOCSDTO.getEffDate() != null) {
            params.put("effDate", updateVehicleRequestOCSDTO.getEffDate());
        }
        if (updateVehicleRequestOCSDTO.getVehicleGroupId() != null) {
            params.put("vehicleType", updateVehicleRequestOCSDTO.getVehicleGroupId().toString());
        }
        if (updateVehicleRequestOCSDTO.getVehicleType() != null) {
            params.put("detailedVehicleType", updateVehicleRequestOCSDTO.getVehicleType().toString());
        }

        if (updateVehicleRequestOCSDTO.getPayloads() != null) {
            params.put("payloads", updateVehicleRequestOCSDTO.getPayloads());
        }
        String result = doPostRequest(wsModifyVehicle, params, "", 1, authentication, updateVehicleRequestOCSDTO.getActTypeId());
        return new Gson().fromJson(result, OCSResponse.class);
    }


    /**
     * Tao hợp đồng gửi sang OCS
     *
     * @param form
     * @return
     */
    @Override
    public OCSResponse createContract(OCSCreateContractForm form, Authentication authentication, int actionTypeId) {
        String jsonReq = new Gson().toJson(form);
        String strResp = doPostRequest(wsNewContract, null, jsonReq, 1, authentication, actionTypeId);
        return new Gson().fromJson(strResp, OCSResponse.class);
    }

    /***
     * Câp nhật hợp đồng sang OCS
     * @param form
     * @param authentication
     * @param actionTypeId
     * @return
     */
    @Override
    public OCSResponse updateContract(OCSUpdateContractForm form, Authentication authentication, int actionTypeId) {
        String jsonReq = new Gson().toJson(form);
        Map<String, String> params = new HashMap<>();
        params.put("action", "0");
        String strResp = doPostRequest(wsModifyContract, params, jsonReq, 1, authentication, actionTypeId);
        return new Gson().fromJson(strResp, OCSResponse.class);
    }

    /***
     * Chấm dứt hợp đồng gửi sang OCS
     * @param form
     * @param authentication
     * @param actionTypeId
     * @return
     */
    @Override
    public OCSResponse terminateContract(OCSUpdateContractForm form, Authentication authentication, int actionTypeId) {
        String jsonReq = new Gson().toJson(form);
        Map<String, String> params = new HashMap<>();
        params.put("action", "1");
        String strResp = doPostRequest(wsModifyContract, params, jsonReq, 1, authentication, actionTypeId);
        return new Gson().fromJson(strResp, OCSResponse.class);
    }

    /***
     * Xóa phương tiện gửi sang OCS
     * @param EPC
     * @param contractId
     * @param authentication
     * @param actionTypeId
     * @return
     */
    @Override
    public OCSResponse deleteVehicle(String EPC, String contractId, Authentication authentication, int actionTypeId) {
        Map<String, String> params = new HashMap<>();
        params.put("EPC", EPC);
        params.put("contractId", contractId);
        String strResp = doPostRequest(wsDeleteVehicle, params, "", 1, authentication, actionTypeId);
        return new Gson().fromJson(strResp, OCSResponse.class);
    }

    /**
     * charge tien  OCS
     *
     * @param addSupOfferRequestDTO params client
     * @return
     */
    @Override
    public OCSResponse charge(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, Long contractId, String partyCode) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (contractId != null) {
            params.put("contractId", contractId.toString());
        } else {
            throw new Exception("common.validate.err.data.contractNo");
        }
        if (addSupOfferRequestDTO.getAmount() != null) {
            params.put("charge", addSupOfferRequestDTO.getAmount().toString());
        } else {
            throw new Exception("common.validate.err.data.charge");
        }
        if (!FnCommon.isNullOrEmpty(partyCode)) {
            params.put("partyCode", partyCode);
        }

        if (addSupOfferRequestDTO.getStaffId() != null) {
            params.put("staffId", addSupOfferRequestDTO.getStaffId().toString());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRequestDTO.getStaffName())) {
            params.put("staffName", addSupOfferRequestDTO.getStaffName());
        }

        try {
            String result = doPostRequest(wsCharge, params, "", 1, authentication, addSupOfferRequestDTO.getActTypeId());
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /**
     * Dang ki goi cuoc phu OCS
     *
     * @param vehicleAddSuffOfferDTO params client
     * @return
     */
    @Override
    public OCSResponse addSupOffer(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Authentication authentication,
                                   long actTypeId, Long contractId, Long staffId, String staffName, String partyCode) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (contractId != null) {
            params.put("contractId", contractId.toString());
        }
        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getEpc())) {
            params.put("EPC", vehicleAddSuffOfferDTO.getEpc());
        }

        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getOfferId())) {
            params.put("offerId", vehicleAddSuffOfferDTO.getOfferId());
        } else {
            throw new Exception("common.validate.err.data.offer.id");
        }
        if (vehicleAddSuffOfferDTO.getEffDate() != null) {
            params.put("eff_Date", FnCommon.convertDateToString(vehicleAddSuffOfferDTO.getEffDate()));
        }

        if (vehicleAddSuffOfferDTO.getExpDate() != null) {
            params.put("exp_Date", FnCommon.convertDateToString(vehicleAddSuffOfferDTO.getExpDate()));
        }

        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getOfferLevel())) {
            params.put("offerLevel", vehicleAddSuffOfferDTO.getOfferLevel());
        } else {
            throw new Exception("common.validate.err.data.offer.level");
        }

        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getStationLevel())) {
            params.put("stationLevel", vehicleAddSuffOfferDTO.getStationLevel());
        }

        if (vehicleAddSuffOfferDTO.getStationId() != null) {
            params.put("stationId", vehicleAddSuffOfferDTO.getStationId().toString());
        }
        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getLaneId())) {
            params.put("laneId", vehicleAddSuffOfferDTO.getLaneId());
        }

        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getIsOCSCharged())) {
            params.put("isOCSCharged", vehicleAddSuffOfferDTO.getIsOCSCharged());
        }
        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getChargedVehicleType())) {
            params.put("chargedVehicleType", vehicleAddSuffOfferDTO.getChargedVehicleType());
        }
        if (staffId != null && staffId != 0) {
            params.put("staffId", String.valueOf(staffId));
        }
        if (!FnCommon.isNullOrEmpty(staffName)) {
            params.put("staffName", staffName);
        }
        if (!FnCommon.isNullOrEmpty(partyCode)) {
            params.put("partyCode", partyCode);
        }

        if (FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getAutoRenew())) {
            vehicleAddSuffOfferDTO.setAutoRenew("0");
        }
        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getIsGenCdr())) {
            params.put("isGenCdr", vehicleAddSuffOfferDTO.getIsGenCdr());
        }
        params.put("isRecurring", vehicleAddSuffOfferDTO.getAutoRenew());
        try {
            String result = doPostRequest(wsAddSupOffer, params, "", 1, authentication, actTypeId);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /***
     * Hàm lấy thông tin hợp đồng từ OCS
     * @param authentication
     * @param contractId
     * @return
     */
    @Override
    public String getContractInfo(Authentication authentication, Long contractId) {
        Map<String, String> params = new HashMap<>();
        params.put("contractId", String.valueOf(contractId));
        String response = doPostRequest(wsQueryContract, params, null, 1, authentication, 1);
        OCSResponse ocsResponse = new Gson().fromJson(response, OCSResponse.class);
        if (!"0".equals(ocsResponse.getResultCode())) {
            return "";
        }
        return response;
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
        String strRes = "", ip = null;
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
                ip = InetAddress.getLocalHost().getHostAddress();
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .post(body)
                        .build();
                start = System.currentTimeMillis();
                Response response = client.newCall(request).execute();
                end = System.currentTimeMillis() - start;
                strRes = response.body().string();
                if(!url.contains("queryContract")) {
                    LOG.info("OCS Request url={} params={} body={}", url, params, json);
                    LOG.info("OCS response body={}", strRes);
                }
                wsAuditEntity = writeLog(buildLogRequest(request, json), strRes, url, ip, end, actTypeId, authentication, request.method(), WsAuditEntity.Status.SUCCESS.value);
                return strRes.replace("null", "\"\"");
            }
        } catch (Exception e) {
            LOG.error("Has error call OCS", e);
            if (request != null) {
                updateWriteLogFail(wsAuditEntity);
                end = System.currentTimeMillis() - start;
                writeLog(buildLogRequest(request, json), strRes, url, ip, end, actTypeId, authentication, request.method(), "0");
                doPostRequest(url, params, json, ++step, authentication, actTypeId);
            }
        }
        return strRes;
    }

    private String buildLogRequest(Request request, String body) {
        if (Strings.isBlank(body)) {
            body = "{}";
        }
        return "Request{method=" + request.method() + ", url=" + request.url() + ", body=" + body + ", tag=" + (request.tag() != request ? request.tag() : null) + '}';
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
            wsAuditEntity.setDestinationAppId("OCS");
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOG.error("write log ws_audit failed", e);
        }
        return wsAuditEntity;
    }

    /***
     * Hàm huy goi cuoc phu OCS
     * @param contractId
     * @param epc
     * @param offerId
     * @param offerLevel
     */
    @Override
    public OCSResponse deleteSupOffer(Authentication authentication, Long actTypeId, Long contractId, String epc, String offerId, String offerLevel) {
        Map<String, String> params = new HashMap<>();

        params.put("contractId", contractId.toString());
        params.put("EPC", epc);
        params.put("offerId", offerId);
        params.put("offerLevel", offerLevel);
        try {
            String result = doPostRequest(wsRemoveSupOffer, params, "", 1, authentication, actTypeId);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /***
     * Hàm cong tien
     * @param contractId
     * @param amount
     */
    @Override
    public OCSResponse addBalance(Authentication authentication, Long actTypeId, Long contractId, Long amount) {
        Map<String, String> params = new HashMap<>();
        params.put("contractId", contractId.toString());
        params.put("amount", amount.toString());
        try {
            String result = doPostRequest(wsAddBalance, params, "", 5, authentication, actTypeId);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    @Override
    public OCSResponse addBalance(Authentication authentication, Long actTypeId, Long contractId, Long amount, String partyCode, String type, String channel) {
        Map<String, String> params = new HashMap<>();
        params.put("contractId", contractId.toString());
        params.put("amount", amount.toString());
        if (!FnCommon.isNullOrBlank(partyCode)) {
            params.put("partyCode", partyCode);
        }
        if (!FnCommon.isNullOrBlank(type)) {
            params.put("type", type);
        }
        if (!FnCommon.isNullOrBlank(channel)) {
            params.put("channel", channel);
        }
        try {
            String result = doPostRequest(wsAddBalance, params, "", 5, authentication, actTypeId);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /***
     * Hàm cong tien danh rieng cho cong thanh toan VTP, MoMo
     * @param contractId
     * @param amount
     */
    @Override
    public OCSResponse addBalanceNew(Authentication authentication, Long actTypeId, Long contractId, Long amount, String channel) {
        Map<String, String> params = new HashMap<>();
        params.put("contractId", contractId.toString());
        params.put("amount", amount.toString());
        params.put("channel", channel);
        try {
            String result = doPostRequest(wsAddBalance, params, "", 5, authentication, actTypeId);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
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
            LOG.error("Update log fail", e);
        }

    }

    /**
     * Chuyen chu quyen phuong tien OCS
     *
     * @param EPC
     * @param TID
     * @param contractId
     * @param newContractId
     * @param authentication
     * @param actionTypeId
     * @return
     */
    @Override
    public OCSResponse transferVehicle(String EPC, String TID, String contractId, String newContractId, Authentication authentication, int actionTypeId) {
        Map<String, String> params = new HashMap<>();
        params.put("EPC", EPC);
        params.put("TID", TID);
        params.put("contractId", contractId);
        params.put("newContractId", newContractId);
        String strResp = doPostRequest(wsTransferVehicle, params, "", 1, authentication, actionTypeId);
        return new Gson().fromJson(strResp, OCSResponse.class);
    }

    /**
     * Cap nhat thong tin goi cuoc phu
     *
     * @param vehicleAddSuffOfferDTO
     * @param authentication
     * @param contractId
     * @param staffId
     * @param staffName
     * @param isRecurring
     * @return
     * @throws Exception
     */
    @Override
    public OCSResponse changeSupOffer(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Authentication authentication, Long contractId, Long staffId, String staffName, String isRecurring) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (contractId != null) {
            params.put("contractId", contractId.toString());
        }
        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getEpc())) {
            params.put("EPC", vehicleAddSuffOfferDTO.getEpc());
        }

        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getOfferId())) {
            params.put("offerId", vehicleAddSuffOfferDTO.getOfferId());
        } else {
            throw new Exception("common.validate.err.data.offer.id");
        }
        if (vehicleAddSuffOfferDTO.getExpDate() != null) {
            params.put("exp_Date", FnCommon.convertDateToString(vehicleAddSuffOfferDTO.getExpDate()));
        }

        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getOfferLevel())) {
            params.put("offerLevel", vehicleAddSuffOfferDTO.getOfferLevel());
        } else {
            throw new Exception("common.validate.err.data.offer.level");
        }
        if (!FnCommon.isNullOrEmpty(isRecurring)) {
            params.put("isRecurring", isRecurring);
        }
        if (staffId != null && staffId != 0) {
            params.put("staffId", String.valueOf(staffId));
        }
        if (!FnCommon.isNullOrEmpty(staffName)) {
            params.put("staffName", staffName);
        }
        try {
            String result = doPostRequest(wsChangeSupOffer, params, "", 1, authentication, Constants.ACT_TYPE.MODIFY_VEHICLE);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    @Override
    public LinkedHashMap<?, ?> queryVehicleOcs(String epc, Authentication authentication) {
        Map<String, String> params = new HashMap<>();
        if (!FnCommon.isNullOrEmpty(epc)) {
            params.put("EPC", epc);
        }
        try {
            String result = doPostRequest(wsQueryVehicle, params, "", 1, authentication, Constants.ACT_TYPE.QUERY_CONTRACT_INFO);
            return new Gson().fromJson(result, LinkedHashMap.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    @Override
    public LinkedHashMap<?, ?> queryContractOcs(String contractId, Authentication authentication) {
        Map<String, String> params = new HashMap<>();
        if (!FnCommon.isNullOrEmpty(contractId)) {
            params.put("contractId", contractId);
        }
        try {
            String result = doPostRequest(wsQueryContract, params, "", 1, authentication, Constants.ACT_TYPE.QUERY_CONTRACT_INFO);
            return new Gson().fromJson(result, LinkedHashMap.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /**
     * Dang ki goi cuoc phu OCS - Boo1
     *
     * @param addSupOfferRoamingDTO params
     * @param authentication        params
     * @return
     */
    @Override
    public OCSResponse addSupOfferRoamingBoo1(AddSupOfferRoamingDTO addSupOfferRoamingDTO, Authentication authentication) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getEPC())) {
            params.put("EPC", addSupOfferRoamingDTO.getEPC());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getEff_Date())) {
            params.put("eff_Date", addSupOfferRoamingDTO.getEff_Date());
        }

        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getExp_Date())) {
            params.put("exp_Date", addSupOfferRoamingDTO.getExp_Date());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getPartyCode())) {
            params.put("partyCode", addSupOfferRoamingDTO.getPartyCode());
        }

        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getAgentId())) {
            params.put("agentId", addSupOfferRoamingDTO.getAgentId());
        }

        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getAgentName())) {
            params.put("agentName", addSupOfferRoamingDTO.getAgentName());
        }

        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getStaffId())) {
            params.put("staffId", addSupOfferRoamingDTO.getStaffId());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getStaffName())) {
            params.put("staffName", addSupOfferRoamingDTO.getStaffName());
        }

        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getSubscriptionTicketId())) {
            params.put("subscriptionTicketId", addSupOfferRoamingDTO.getSubscriptionTicketId());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getRequestId())) {
            params.put("requestId", addSupOfferRoamingDTO.getRequestId());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getRequestDateTime())) {
            params.put("requestDateTime", addSupOfferRoamingDTO.getRequestDateTime());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getResponseId())) {
            params.put("responseId", addSupOfferRoamingDTO.getResponseId());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getResponseDateTime())) {
            params.put("responseDateTime", addSupOfferRoamingDTO.getResponseDateTime());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getTicketType())) {
            params.put("ticketType", addSupOfferRoamingDTO.getTicketType());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getDiscountId())) {
            params.put("discountId", addSupOfferRoamingDTO.getDiscountId());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getCharge())) {
            params.put("charge", addSupOfferRoamingDTO.getCharge());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getStationIn())) {
            params.put("stationIn", addSupOfferRoamingDTO.getStationIn());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getStationOut())) {
            params.put("stationOut", addSupOfferRoamingDTO.getStationOut());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getLaneIn())) {
            params.put("laneIn", addSupOfferRoamingDTO.getLaneIn());
        }
        if (!FnCommon.isNullOrEmpty(addSupOfferRoamingDTO.getLaneOut())) {
            params.put("laneOut", addSupOfferRoamingDTO.getLaneOut());
        }

        try {
            String result = doPostRequest(wsAddSupOfferRoaming, params, "", 1, authentication, addSupOfferRoamingDTO.getActTypeId());
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /**
     * Huy goi cuoc phu OCS - Boo1
     *
     * @param removeSupOfferRoaming
     * @param authentication
     * @return
     */
    @Override
    public OCSResponse removeSupOfferRoaming(RemoveSupOfferRoaming removeSupOfferRoaming, Authentication authentication) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getEPC())) {
            params.put("EPC", removeSupOfferRoaming.getEPC());
        }

        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getAgentId())) {
            params.put("agentId", removeSupOfferRoaming.getAgentId());
        }

        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getAgentName())) {
            params.put("agentName", removeSupOfferRoaming.getAgentName());
        }

        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getStaffId())) {
            params.put("staffId", removeSupOfferRoaming.getStaffId());
        }
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getStaffName())) {
            params.put("staffName", removeSupOfferRoaming.getStaffName());
        }

        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getSubscriptionTicketId())) {
            params.put("subscriptionTicketId", removeSupOfferRoaming.getSubscriptionTicketId());
        }
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getRequestId())) {
            params.put("requestId", removeSupOfferRoaming.getRequestId());
        }
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getRequestDateTime())) {
            params.put("requestDateTime", removeSupOfferRoaming.getRequestDateTime());
        }
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getResponseId())) {
            params.put("responseId", removeSupOfferRoaming.getResponseId());
        }
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getResponseDateTime())) {
            params.put("responseDateTime", removeSupOfferRoaming.getResponseDateTime());
        }
        if (!FnCommon.isNullOrEmpty(removeSupOfferRoaming.getIsRefund())) {
            params.put("isRefund", removeSupOfferRoaming.getIsRefund());
        }

        try {
            String result = doPostRequest(wsRemoveSupOfferRoaming, params, "", 1, authentication, removeSupOfferRoaming.getActTypeId());
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }

    /**
     * Cap nhat gia han tu dong OCS
     *
     * @param chargeSupOfferDTO
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    public OCSResponse changeSupOfferTicket(ChargeSupOfferDTO chargeSupOfferDTO, Authentication authentication) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (chargeSupOfferDTO.getContractId() != null) {
            params.put("contractId", chargeSupOfferDTO.getContractId());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getEpc())) {
            params.put("EPC", chargeSupOfferDTO.getEpc());
        }

        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getOfferId())) {
            params.put("offerId", chargeSupOfferDTO.getOfferId());
        } else {
            throw new Exception("common.validate.err.data.offer.id");
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getExp_Date())) {
            params.put("exp_Date", chargeSupOfferDTO.getExp_Date());
        }

        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getOfferLevel())) {
            params.put("offerLevel", chargeSupOfferDTO.getOfferLevel());
        } else {
            throw new Exception("common.validate.err.data.offer.level");
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getIsRecurring())) {
            params.put("isRecurring", chargeSupOfferDTO.getIsRecurring());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getIsRecurringExtBalance())) {
            params.put("isRecurringExtBalance", chargeSupOfferDTO.getIsRecurringExtBalance());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getAgentId())) {
            params.put("agentId", chargeSupOfferDTO.getAgentId());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getAgentName())) {
            params.put("agentName", chargeSupOfferDTO.getAgentName());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getStaffId())) {
            params.put("staffId", chargeSupOfferDTO.getStaffId());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getStaffName())) {
            params.put("staffName", chargeSupOfferDTO.getStaffName());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getSubscriptionTicketId())) {
            params.put("subscriptionTicketId", chargeSupOfferDTO.getSubscriptionTicketId());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getRequestId())) {
            params.put("requestId", chargeSupOfferDTO.getRequestId());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getRequestDateTime())) {
            params.put("requestDateTime", chargeSupOfferDTO.getRequestDateTime());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getResponseId())) {
            params.put("responseId", chargeSupOfferDTO.getResponseId());
        }
        if (!FnCommon.isNullOrEmpty(chargeSupOfferDTO.getResponseDateTime())) {
            params.put("responseDateTime", chargeSupOfferDTO.getResponseDateTime());
        }
        try {
            String result = doPostRequest(wsChangeSupOffer, params, "", 1, authentication, Constants.ACT_TYPE.MODIFY_VEHICLE);
            return new Gson().fromJson(result, OCSResponse.class);
        } catch (Exception ex) {
            LOG.error("Call OCS fail", ex);
            throw ex;
        }
    }
}
