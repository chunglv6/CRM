package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.tables.CustomerRepositoryJPA;
import com.viettel.etc.repositories.tables.VehicleRepositoryJPA;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.services.ProductService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.viettel.etc.utils.Constants.CASE_UPDATE_VEHICLE;
import static com.viettel.etc.utils.Constants.IM_VTT_VDTC_CODE;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Value("${ws.ocs.retry}")
    private String numberRetry;

    @Value("${crm.ws.soap.time-out}")
    private String timeOut;

    @Value("${ws.bccs.im.params.project-type}")
    private String paramsProjectType;

    @Value("${ws.bccs.im.params.sale-trans-type}")
    private String paramsSaleTransType;

    @Value("${ws.bccs.url}")
    private String wsBccsUrl;

    @Value("${ws.bccs.params.username}")
    private String paramsUsername;

    @Value("${ws.bccs.params.password}")
    private String paramsPassword;

    @Value("${ws.bccs.im.url}")
    private String wsIMUrl;

    @Value("${ws.bccs.im.params.username}")
    private String paramsUsernameIM;

    @Value("${ws.bccs.im.params.password}")
    private String paramsPasswordIM;

    @Autowired
    CustomerRepositoryJPA customerRepositoryJPA;

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    /**
     * set request lay danh sach DVBH dang hoat dong
     *
     * @param operator
     * @param property
     * @param valueText
     * @param valueType
     * @param user
     * @param pass
     */
    public String setRequestFindByFilter(String operator, String property, String valueText, String valueType, String user, String pass) {
        String header = setHeader(user, pass);
        String xmlInput =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.product.bccs.viettel.com/\">\n" +
                        header +
                        "   <soapenv:Body>\n" +
                        "      <ser:findByFilter>         \n" +
                        "         <arg0>            \n" +
                        "            <operator>" + operator + "</operator>            \n" +
                        "            <property>" + property + "</property>            \n" +
                        "            <valueText>" + valueText + "</valueText>  \n" +
                        "            <valueType>" + valueType + "</valueType>\n" +
                        "        </arg0>\n" +
                        "      </ser:findByFilter>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>\n";
        return xmlInput;
    }

    /**
     * Set header cho cac request
     *
     * @param user
     * @param pass
     */
    public String setHeader(String user, String pass) {
        String header =
                "<soapenv:Header>\n" +
                        "          <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">                 \n" +
                        "         <wsse:UsernameToken wsu:Id=\"UsernameToken-32ce50e8-4a5d-4040-af71-c3428d92daa7\">                       \n" +
                        "            <wsse:Username>" + user + "</wsse:Username>                       \n" +
                        "            <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">" + pass + "</wsse:Password>                    \n" +
                        "         </wsse:UsernameToken>              \n" +
                        "      </wsse:Security>        \n" +
                        "   </soapenv:Header>  ";
        return header;
    }

    /**
     * Goi soap API
     *
     * @param wsURL
     * @param actTypeId
     * @param authentication
     * @param xmlInput
     * @param step
     */

    public String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step) {
        StringBuilder outputString = new StringBuilder();
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
                    outputString.append(responseString);
                }
                end = System.currentTimeMillis() - start;
                writeLog(req, outputString.toString(), url.toString(), ip, end, actTypeId, authentication, httpConn.getRequestMethod(), WsAuditEntity.Status.SUCCESS.value);
            }
        } catch (Exception e) {
            LOGGER.error("Has error call WS BCCS", e);
            end = System.currentTimeMillis() - start;
            if (httpConn != null && req != null) {
                writeLog(req, outputString.toString(), wsURL, ip, end, actTypeId, authentication, httpConn.getRequestMethod(), WsAuditEntity.Status.ERROR.value);
                requestSOAP(wsURL, actTypeId, authentication, xmlInput, ++step);
            }
        }
        return outputString.toString();
    }

    /**
     * Doc response lay danh sach DVBH dang hoat dong
     *
     * @param response
     */
    public FindByFilterResponse readResponse(String response) throws IOException, XMLStreamException, JAXBException {
        XMLStreamReader xsr = readResponseCommon(response);
        FindByFilterResponse findByFilterResponse = null;
        if (xsr != null) {
            xsr.nextTag(); // Advance to Envelope tag
            xsr.nextTag(); // Advance to Header tag
            xsr.next(); // Advance to Body tag
            xsr.nextTag(); // Advance to Body tag
            xsr.nextTag(); // Advance to ns2 tag
            JAXBContext jc = JAXBContext.newInstance(FindByFilterResponse.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<FindByFilterResponse> je = unmarshaller.unmarshal(xsr, FindByFilterResponse.class);
            findByFilterResponse = je.getValue();
        }
        return findByFilterResponse;
    }

    /**
     * Xu ly respone tra ve
     *
     * @param response
     */
    public XMLStreamReader readResponseCommon(String response) throws IOException, XMLStreamException {
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
     * Ghi log ws vào bảng WS_AUDIT
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
    private void writeLog(String req, String resp, String url, String ip, long timeCallWs, long actTypeId,
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
            wsAuditEntity.setDestinationAppId("IM");
            if (resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOGGER.error("write log ws_audit failed", e);
        }
    }

    /**
     * set request lay cac mat hang thuoc DVBH
     *
     * @param code
     * @param userNameProduct
     * @param passWordProduct
     */
    public String setRequestSaleServicesAdvBOBySSCode(String code, String userNameProduct, String passWordProduct) {
        String header = setHeader(userNameProduct, passWordProduct);
        String xmlInput =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.product.bccs.viettel.com/\">\n" +
                        header +
                        "   <soapenv:Body>\n" +
                        "      <ser:getSaleServicesAdvBOBySSCode>\n" +
                        "         <!--Optional:-->\n" +
                        "         <arg0>" + code + "</arg0>\n" +
                        "      </ser:getSaleServicesAdvBOBySSCode>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
        return xmlInput;
    }

    /**
     * Doc response lay danh sach mat hang thuoc DVBH
     *
     * @param response
     */
    public SaleServiceAdvanceDTO readResponseSale(String response) throws IOException, XMLStreamException, JAXBException {
        XMLStreamReader xsr = readResponseCommon(response);
        SaleServiceAdvanceDTO saleServiceAdvanceDTO = null;
        if (xsr != null) {
            xsr.nextTag(); // Advance to Envelope tag
            xsr.nextTag(); // Advance to Header tag
            xsr.next(); // Advance to Body tag
            xsr.nextTag(); // Advance to Body tag
            xsr.nextTag(); // Advance to ns2 tag
            xsr.nextTag(); // Advance to return tag
            JAXBContext jc = JAXBContext.newInstance(SaleServiceAdvanceDTO.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<SaleServiceAdvanceDTO> je = unmarshaller.unmarshal(xsr, SaleServiceAdvanceDTO.class);
            saleServiceAdvanceDTO = je.getValue();
        }
        return saleServiceAdvanceDTO;
    }

    /**
     * set request len giao dich ETC
     *
     * @param passWordIM
     * @param userIM
     * @param saleServiceAdvanceDTO
     * @param serial
     * @param custId
     */
    public String setRequestSaveSaleTransExternal(String passWordIM, String userIM, List<SaleServiceModelAdvanceDTO> list, SaleServiceAdvanceDTO saleServiceAdvanceDTO, String serial, Long custId, Authentication authentication) {
        String listSaleServiceModel = setListSaleServiceModel(list, serial);
        String listSaleServicePrice = setListSaleServicePrice(saleServiceAdvanceDTO);
        String saleService = setSaleService(saleServiceAdvanceDTO);
        String arg0 = setInfoCustomer(custId, authentication);
        String header = setHeader(userIM, passWordIM);
        String xmlInput =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sal=\"http://saleBusiness.sale.bccs.viettel.com/\">\n" +
                        header +
                        "   <soapenv:Body>\n" +
                        "      <sal:saveSaleTransExternal>\n" +
                        "         <arg0>\n" +
                        arg0 +
                        "         </arg0>\n" +
                        "         <arg1>\n" +
                        saleService +
                        listSaleServiceModel +
                        listSaleServicePrice +
                        "         </arg1>              \n" +
                        "      </sal:saveSaleTransExternal>        \n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>\n";
        return xmlInput;
    }

    /**
     * Len giao dich ETC
     *
     * @param response
     */
    public SaleBusinessMesDTO readResponseIM(String response) throws IOException, XMLStreamException, JAXBException {
        XMLStreamReader xsr = readResponseCommon(response);
        SaleBusinessMesDTO saleBusinessMesDTO = null;
        if (xsr != null) {
            xsr.nextTag(); // Advance to Envelope tag
            xsr.nextTag(); // Advance to Header tag
            xsr.next(); // Advance to Body tag
            xsr.nextTag(); // Advance to Body tag
            JAXBContext jc = JAXBContext.newInstance(SaleBusinessMesDTO.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<SaleBusinessMesDTO> je = unmarshaller.unmarshal(xsr, SaleBusinessMesDTO.class);
            saleBusinessMesDTO = je.getValue();
        }
        return saleBusinessMesDTO;

    }

    /**
     * set listSaleServiceModel
     *
     * @param list
     * @param serial
     */
    public String setListSaleServiceModel(List<SaleServiceModelAdvanceDTO> list, String serial) {
        PackageOfferDTO packageOfferDTO = list.get(0).getListSaleServiceDetail().get(0);
        ProdPackProductOfferTypeDTO prodPackProductOfferTypeDTO = list.get(0).getSaleServiceModel();
        String listSaleServiceModel =
                "            <listSaleServiceModel>   \n" +
                        "               <saleServiceModel>\n" +
                        "                  <checkShopStock>" + prodPackProductOfferTypeDTO.getCheckShopStock() + "</checkShopStock>\n" +
                        "                  <checkStaffStock>" + prodPackProductOfferTypeDTO.getCheckStaffStock() + "</checkStaffStock>\n" +
                        "               </saleServiceModel>\n" +
                        "               <listSaleServiceDetail>\n" +
                        "                  <productOfferingId>" + packageOfferDTO.getProductOfferingId() + "</productOfferingId>\n" +
                        "                  <productOfferPriceId>" + packageOfferDTO.getProductOfferPriceId() + "</productOfferPriceId>\n" +
                        "                  <serial>" + serial + "</serial>\n" +
                        "                  <productOfferTypeId>" + packageOfferDTO.getProductOfferTypeId() + "</productOfferTypeId>  \n" +
                        "                  <prodPackOfferId>" + packageOfferDTO.getProdPackOfferId() + "</prodPackOfferId>\n" +
                        "                  <price>" + packageOfferDTO.getPrice() + "</price>\n" +
                        "                  <vat>" + packageOfferDTO.getVat() + "</vat>\n" +
                        "                  <newOrSold>" + packageOfferDTO.getNewOrSold() + "</newOrSold>\n" +
                        "               </listSaleServiceDetail>\n" +
                        "            </listSaleServiceModel>\n";
        return listSaleServiceModel;
    }

    /**
     * set setListSaleServicePrice
     *
     * @param saleServiceAdvanceDTO
     */
    public String setListSaleServicePrice(SaleServiceAdvanceDTO saleServiceAdvanceDTO) {
        List<ProductPackageFeeDTO> list = saleServiceAdvanceDTO.getListPrice();
        Long pricePHM = 0L;
        Long pricePolicyIdPHM = 0L;
        Long vatPHM = 0L;
        Long pricePolicyIdTB = 0L;
        for (ProductPackageFeeDTO productPackageFeeDTO : list) {
            if ("PHM".equals(productPackageFeeDTO.getCode())) {
                pricePHM = productPackageFeeDTO.getPrice();
                pricePolicyIdPHM = productPackageFeeDTO.getPricePolicyId();
                vatPHM = productPackageFeeDTO.getVat();
            }
            if ("PTB".equals(productPackageFeeDTO.getCode())) {
                pricePolicyIdTB = productPackageFeeDTO.getPricePolicyId();
            }
        }
        String listSaleServicePrice =
                "             <listSaleServicePrice>\n" +
                        "                    <price>" + pricePHM.toString() + "</price>\n" +
                        "                    <pricePolicyId>" + pricePolicyIdPHM.toString() + "</pricePolicyId>\n" +
                        "                    <vat>" + vatPHM.toString() + "</vat>\n" +
                        "             </listSaleServicePrice>\n" +
                        "              <listSaleServicePrice>\n" +
                        "                    <pricePolicyId>" + pricePolicyIdTB.toString() + "</pricePolicyId>\n" +
                        "                </listSaleServicePrice>\t\t\t \n";
        return listSaleServicePrice;
    }

    /**
     * set setSaleService
     *
     * @param saleServiceAdvanceDTO
     */
    public String setSaleService(SaleServiceAdvanceDTO saleServiceAdvanceDTO) {
        ProductPackageDTO productPackageDTO = saleServiceAdvanceDTO.getSale();
        String code = productPackageDTO.getCode();
        String saleService =
                "            <saleService>\n" +
                        "               <code>" + code + "</code>\n" +
                        "            </saleService>\n";
        return saleService;
    }

    /**
     * Lay thong tin khach hang gui sang IM
     *
     * @param custId
     */
    public String setInfoCustomer(Long custId, Authentication authentication) {
        String address = "";
        String company = "";
        String custName = "";
        String email = "";
        String tin = "";
        long projectType = Long.parseLong(paramsProjectType);
        long saleTransType = Long.parseLong(paramsSaleTransType);
        CustomerEntity customerEntity = customerRepositoryJPA.findAllByCustId(custId);
        if (customerEntity.getAuthName() != null) {
            address = customerEntity.getAuthStreet();
            company = customerEntity.getAuthName();
            custName = customerEntity.getAuthName();
            email = customerEntity.getAuthEmail();
            tin = customerEntity.getTaxCode();
        } else if (customerEntity.getRepName() != null) {
            address = customerEntity.getRepStreet();
            company = customerEntity.getRepName();
            custName = customerEntity.getRepName();
            email = customerEntity.getRepEmail();
            tin = customerEntity.getTaxCode();
        } else {
            address = customerEntity.getStreet();
            company = "";
            custName = customerEntity.getCustName();
            tin = "";
        }

        // Len giao dich IM
        // Chua co ham lay, dang set mac dinh
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        if (map == null) {
            throw new EtcException("crm.user.attribute.null");
        }
        long shopId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.SHOP_ID).toString());
        long staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
        String arg0 =
                " <address>" + address + "</address>\n" +
                        "            <company>" + company + "</company>\n" +
                        "            <custName>" + custName + "</custName>\n" +
                        "            <email>" + email + "</email>\n" +
                        "            <tin>" + tin + "</tin>            \n" +
                        "            <shopId>" + shopId + "</shopId>                      \n" +
                        "            <staffId>" + staffId + "</staffId>                      \n" +
                        "            <projectType>" + projectType + "</projectType>                   \n" +
                        "            <saleTransType>" + saleTransType + "</saleTransType>\n";

        return arg0;
    }

    @Override
    public SaleBusinessMesDTO callBCCSIM(Authentication authentication, Long actTypeId, VehicleEntity vehicleEntity, String serial, Long custId, int caseUpdate, String statusVehicleBoo1) throws EtcException, JAXBException, XMLStreamException, IOException {
        String code = "";
        if (VehicleEntity.RfidType.WINDSHIELD.value.equals(vehicleEntity.getRfidType())) {
            code = IM_VTT_VDTC_CODE.ETC_FREE_WINDSHIELD;
            code = getCodeBCCIM(code, statusVehicleBoo1, caseUpdate, vehicleEntity.getRfidType());
        } else if (VehicleEntity.RfidType.LAMP.value.equals(vehicleEntity.getRfidType())) {
            code = IM_VTT_VDTC_CODE.ETC_FEE_120_LAMP;
            code = getCodeBCCIM(code, statusVehicleBoo1, caseUpdate, vehicleEntity.getRfidType());
        }

        String xmlInputSale = setRequestSaleServicesAdvBOBySSCode(code, paramsUsername, paramsPassword);
        String responseSale = requestSOAP(wsBccsUrl, actTypeId, authentication, xmlInputSale, 1);
        LOGGER.info("Lay danh sach mat hang thuoc DVBH : " + responseSale);
        if (FnCommon.isNullOrEmpty(responseSale)) {
            if (caseUpdate == CASE_UPDATE_VEHICLE.REGISTER_VEHICLE) {
                vehicleRepositoryJPA.delete(vehicleEntity);
            } else if (caseUpdate == CASE_UPDATE_VEHICLE.ASSIGN_VEHICLE) {
                returnStatusBefore(vehicleEntity);
            }
            throw new EtcException("common.validate.err.call.product.sale.service");
        }
        SaleServiceAdvanceDTO saleServiceAdvanceDTO = readResponseSale(responseSale);

        // Neu khong tim thay serial trong kho thi thong bao cho nguoi dung
        if (saleServiceAdvanceDTO != null && saleServiceAdvanceDTO.getListModel() != null) {
            String requestSaveSaleTransExternal = setRequestSaveSaleTransExternal(paramsPasswordIM, paramsUsernameIM, saleServiceAdvanceDTO.getListModel(), saleServiceAdvanceDTO, serial, custId, authentication);
            LOGGER.info("requestSaveSaleTransExternal len IM : " + requestSaveSaleTransExternal);
            String responseIM = requestSOAP(wsIMUrl, actTypeId, authentication, requestSaveSaleTransExternal, 1);
            LOGGER.info("Response tra ve IM : " + responseIM);
            if (FnCommon.isNullOrEmpty(responseIM)) {
                if (caseUpdate == CASE_UPDATE_VEHICLE.REGISTER_VEHICLE) {
                    vehicleRepositoryJPA.delete(vehicleEntity);
                } else if (caseUpdate == CASE_UPDATE_VEHICLE.ASSIGN_VEHICLE) {
                    returnStatusBefore(vehicleEntity);
                }
                throw new EtcException("common.validate.err.call.im");
            }
            return readResponseIM(responseIM);
        } else {
            throw new EtcException("crm.im-vtt.serial.not.found");
        }
    }

    private void returnStatusBefore(VehicleEntity vehicleEntity) {
        vehicleEntity.setStatus(VehicleEntity.Status.SIMILAR.value);
        vehicleEntity.setActiveStatus(null);
        vehicleEntity.setRfidSerial(null);
        vehicleEntity.setTid(null);
        vehicleEntity.setEpc(null);
        vehicleEntity.setRfidType(null);
        vehicleRepositoryJPA.save(vehicleEntity);
    }

    private String getCodeBCCIM(String code, String statusVehicleBoo1, int caseUpdate, String etagType) {
        if (caseUpdate == CASE_UPDATE_VEHICLE.REGISTER_VEHICLE && !statusVehicleBoo1.equals(Constants.BOO_STATUS.ACTIVE) && !statusVehicleBoo1.equals(Constants.BOO_STATUS.DESTROY) && Calendar.getInstance().get(Calendar.YEAR) <= 2021) {
            if (etagType.equals(VehicleEntity.RfidType.WINDSHIELD.value)) {
                return IM_VTT_VDTC_CODE.ETC_FREE_WINDSHIELD;
            } else if (etagType.equals(VehicleEntity.RfidType.LAMP.value)) {
                return IM_VTT_VDTC_CODE.ETC_FREE_LAMP;
            } else {
                return code;
            }
        } else {
            return code;
        }
    }
}
