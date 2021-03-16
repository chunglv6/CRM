package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import com.viettel.etc.dto.viettelpost.*;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.services.ViettelPostService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ViettelPostServiceImpl implements ViettelPostService {
    private static final Logger LOG = LoggerFactory.getLogger(ViettelPostServiceImpl.class);

    @Value("${ws.viettel.post.url.token}")
    private String wsVTPLogin;

    @Value("${username.viettel.post}")
    private String userNameVTPost;

    @Value("${password.viettel.post}")
    private String passVTPost;

    @Value("${ws.viettel.post.url.bil-create}")
    private String wsCreateBill;

    @Value("${ws.viettel.post.url.confirm-order}")
    private String wsConfirmOrder;

    @Value("${ws.viettel.post.province}")
    private String wsProvinceVTP;

    @Value("${ws.viettel.post.district}")
    private String wsDistrictVTP;

    @Value("${ws.viettel.post.wards}")
    private String wsWardsVTP;

    @Value("${ws.viettel.post.shops}")
    private String wsShopVTP;

    @Value("${ws.viettel.post.md5-key}")
    private String md5Key;

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    @Autowired
    JedisCacheService jedisCacheService;

    @Override
    public BillResponseDTO createBill(BillRequestDTO requestDTO, String remoteAddr) {
        BillResponseDTO responseDTO;
        try {
            String token = getToken();
            String strBody = FnCommon.toStringJson(requestDTO);
            String strRes = viettelPostPostRequest(wsCreateBill, token, strBody, remoteAddr);
            responseDTO = new Gson().fromJson(strRes, BillResponseDTO.class);
            if (responseDTO == null) {
                responseDTO = new BillResponseDTO();
                responseDTO.setStatus(HttpStatus.OK.value());
                responseDTO.setMessage(jedisCacheService.getMessageErrorByKey("crm.fail.create.order"));
                responseDTO.setError(true);
            }
        } catch (Exception e) {
            LOG.error("Xay ra loi khi goi ViettelPost " + e);
            throw new EtcException("crm.viettel.post.connect.fail");
        }
        return responseDTO;
    }

    @Override
    public String getToken() {
        return viettelPostToken(wsVTPLogin, userNameVTPost, passVTPost);
    }

    @Override
    public ConfirmOrderResDTO confirmOrder(ConfirmOrderDTO params, String remoteAddr, ContractEntity contractEntity, String tranId, Long cod) {
        ConfirmOrderReqDTO request = new ConfirmOrderReqDTO();
        ConfirmOrderReqDTO.DataReq dataReq = new ConfirmOrderReqDTO.DataReq();
        dataReq.setBuyer_name(contractEntity.getNoticeName());
        dataReq.setBuyer_addr(contractEntity.getNoticeAreaName());
        dataReq.setBuyer_email(contractEntity.getNoticeEmail());
        dataReq.setBuyer_phone(contractEntity.getNoticePhoneNumber());
        dataReq.setContract_code(params.getOrderNumber());
        dataReq.setContract_name("Thẻ dán kính");
        dataReq.setContract_price(params.getContractPrice());
        dataReq.setContract_content("Nhập doanh thu");
        dataReq.setCod(cod);
        if (FnCommon.isNullOrBlank(params.getOrderNumber())) {
            dataReq.setCommission(35000L);
        } else {
            dataReq.setCommission(40000L);
        }
        dataReq.setVtp_username(params.getVtpUsername());
        dataReq.setVtp_mabuucuc(params.getVtpMaBuuCuc());
        String email = dataReq.getBuyer_email() == null ? "" : dataReq.getBuyer_email();
        String contractCode = dataReq.getContract_code() == null ? "" : dataReq.getContract_code();
        dataReq.setTrans_id(tranId);
        request.setData(dataReq);
        String md5 = contractCode + dataReq.getTrans_id() + dataReq.getBuyer_name() + dataReq.getBuyer_addr() +
                dataReq.getBuyer_phone() + email + dataReq.getContract_name() + dataReq.getContract_price() +
                dataReq.getContract_content() + md5Key + dataReq.getCod() + dataReq.getCommission() +
                dataReq.getVtp_username() + dataReq.getVtp_mabuucuc();
        request.setSigned(FnCommon.md5Encoder(md5));

        String token = viettelPostToken(wsVTPLogin, userNameVTPost, passVTPost);
        String strBody = FnCommon.toStringJson(request);
        String strRes = viettelPostPostRequest(wsConfirmOrder, token, strBody, remoteAddr);
        ConfirmOrderResDTO responseDTO;
        try {
            responseDTO = new Gson().fromJson(strRes, ConfirmOrderResDTO.class);
            if (responseDTO == null) {
                responseDTO = new ConfirmOrderResDTO();
                responseDTO.setErrorCode(HttpStatus.BAD_REQUEST.value());
                responseDTO.setMessage(jedisCacheService.getMessageErrorByKey("crm.call.confirm-oder.vtpost.failed"));//loi khi goi VTP
            } else {
                return responseDTO;
            }
        } catch (Exception e) {
            LOG.error("Has error", e);
            throw new EtcException(ErrorApp.ERR_DATA);
        }
        return null;
    }

    @Override
    public ProvinceDTO getListProvinceById(Long id) {
        String url = id == null ? wsProvinceVTP : wsProvinceVTP + id;
        String response = FnCommon.doGetRequest(url, new HashMap<>(), null);
        if (response != null) {
            return new Gson().fromJson(response, ProvinceDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public DistrictDTO getListDistrict(Long id) {
        String url = id == null ? wsDistrictVTP : wsDistrictVTP + id;
        String response = FnCommon.doGetRequest(url, new HashMap<>(), null);
        if (response != null) {
            return new Gson().fromJson(response, DistrictDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public WardsDTO getListWards(Long id) {
        String url = id == null ? wsWardsVTP : wsWardsVTP + id;
        String response = FnCommon.doGetRequest(url, new HashMap<>(), null);
        if (response != null) {
            return new Gson().fromJson(response, WardsDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public ShopDTO getListShop() {
        try {
            String token = viettelPostToken(wsVTPLogin, userNameVTPost, passVTPost);
            String resString = viettelPostGetRequest(wsShopVTP, token, null);
            return new Gson().fromJson(resString, ShopDTO.class);
        } catch (Exception e) {
            LOG.error("Has error", e);
        }
        return null;
    }

    /**
     * Gui bill den vt post
     *
     * @param url
     * @param token
     * @param strBody
     * @return
     */
    private String viettelPostPostRequest(String url, String token, String strBody, String remoteAddr) {
        OkHttpClient client = new OkHttpClient();
        String res = "";
        long process = 0, start = System.currentTimeMillis();
        RequestBody requestBody = RequestBody.create(Constants.JSON, strBody);
        try {
            FnCommon.setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            Request request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Token", token)
                    .url(httpBuilder.build())
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            process = System.currentTimeMillis() - start;
            res = response.body().string();
            LOG.info("VTP POST response: " + res);
            writeLog(strBody, res, url, remoteAddr, "POST", WsAuditEntity.Status.SUCCESS.value, process);
            return res;
        } catch (Exception e) {
            process = System.currentTimeMillis() - start;
            writeLog(strBody, res, url, remoteAddr, "POST", WsAuditEntity.Status.ERROR.value, process);
            LOG.error("Xay ra loi khi goi ViettelPost " + e);
            throw new EtcException("crm.viettel.post.connect.fail");
        }
    }

    private String viettelPostGetRequest(String url, String token, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();
        try {
            FnCommon.setOkHtppClient(client);
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                }
            }
            Request request = new Request.Builder()
                    .header("Accept", "application/json")
                    .header("Token", token)
                    .url(httpBuilder.build())
                    .get().build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            LOG.info("VTP GET response: " + res);
            return res;
        } catch (Exception e) {
            LOG.error("Xay ra loi khi goi ViettelPost " + e);
            throw new EtcException("crm.viettel.post.connect.fail");
        }
    }

    /**
     * Lay token vt post
     *
     * @param url
     * @param userName
     * @param pass
     * @return
     */
    private String viettelPostToken(String url, String userName, String pass) {
        OkHttpClient client = new OkHttpClient();
        try {
            FnCommon.setOkHtppClient(client);
            JSONObject body = new JSONObject();
            body.put("USERNAME", userName);
            body.put("PASSWORD", pass);
            RequestBody requestBody = RequestBody.create(Constants.JSON, body.toString());
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

            Request request = new Request.Builder()
                    .header("Accept", "application/json")
                    .url(httpBuilder.build())
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            JSONObject responseBody = new JSONObject(res);
            JSONObject data = responseBody.getJSONObject("data");
            return data.getString("token");
        } catch (Exception e) {
            LOG.error("Xay ra loi khi goi ViettelPost " + e);
            throw new EtcException("crm.viettel.post.connect.fail");
        }
    }

    private void writeLog(String req, String resp, String url, String ip, String wsCallType, String status, long processTime) {
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            String userLogin = "public", clientId = "public";
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setSourceAppId(clientId);
            wsAuditEntity.setFinishTime(processTime);
            wsAuditEntity.setActionUserName(userLogin);
            wsAuditEntity.setRequestTime(new java.sql.Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            wsAuditEntity.setIpPc(ip);
            if (resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setDestinationAppId(Constants.VTP);
            wsAuditEntity.setActTypeId(Constants.ACT_TYPE.SERVICE_REGISTER);
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOG.error("write log ws_audit failed", e);
        }
    }
}
