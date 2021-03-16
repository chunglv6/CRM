package com.viettel.etc.services.impl;

import com.squareup.okhttp.*;
import com.viettel.etc.dto.GetInfoRegisterDTO;
import com.viettel.etc.dto.ResponseGetInfoRegisterDTO;
import com.viettel.etc.services.SearchXCGService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.encoding.TripleDES;
import com.viettel.etc.utils.exceptions.EtcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SearchXCGServiceImpl implements SearchXCGService {
    @Value("${ws.register.url}")
    private String wsRegisterUrl;

    @Value("${ws.ocs.retry}")
    private String numberRetry;

    @Value("${ws.register.strUserID}")
    private String strUserID;

    @Value("${ws.register.strTagID}")
    private String strTagID;


    private static final Logger LOGGER = LoggerFactory.getLogger(SearchXCGServiceImpl.class);

    public ResponseGetInfoRegisterDTO getInfoRegister(String strPT_ID, String epc) throws Exception {
        String request = setRequestGetTT4VIETINF(strPT_ID, epc);
        String responseEncrypt = requestSOAP(wsRegisterUrl, request, 1);
        GetInfoRegisterDTO getInfoRegisterDTO = readResponseWSRegister(responseEncrypt);
        if (getInfoRegisterDTO == null || getInfoRegisterDTO.getLayTT4VIETINFResult() == null
                || "".equals(getInfoRegisterDTO.getLayTT4VIETINFResult())) {
            throw new EtcException("crm.exception-list.error.xcg");
        }
        String responseDecrypt = getInfoRegisterDTO.getLayTT4VIETINFResult();
        if(!FnCommon.isNullOrEmpty(responseDecrypt) && !FnCommon.isNullOrEmpty(TripleDES.decrypt(responseDecrypt))) {
            LOGGER.info(responseDecrypt);
            String[] response = Objects.requireNonNull(TripleDES.decrypt(responseDecrypt)).split("\t");
            if ("No Information".equals(response[0]) || response.length < 9) {
                throw new EtcException("crm.xcg.no-information.vehicle");
            } else {
                return ResponseGetInfoRegisterDTO.builder()
                        .plateNumber(response[0])
                        .vehicleTypeId(response[1])
                        .chassicNumber(response[2])
                        .seatNumber(response[3])
                        .netWeight(response[4])
                        .cargoWeight(response[5])
                        .grossWeight(response[6])
                        .pullingWeight(response[7])
                        .owner(response[8])
                        .address(response[9])
                        .code(1)
                        .build();
            }
        } else if (!FnCommon.isNullOrEmpty(responseDecrypt) && "Not Allowed".equals(responseDecrypt)) {
            throw new EtcException("crm.xcg.not-allow.vehicle");
        } else {
            throw new EtcException("crm.xcg.no-information.vehicle");
        }
    }

    private String setRequestGetTT4VIETINF(String strPT_ID, String epc) {
        Random rnd = new Random();
        int number = rnd.nextInt(8999999);
        number += 1000000;
        String user = TripleDES.encrypt(strUserID + number);
        strPT_ID = TripleDES.encrypt(strPT_ID);
        String tagID;
        if (epc != null && !"".equals(epc)) {
            tagID = TripleDES.encrypt(epc);
        } else {
            tagID = TripleDES.encrypt(strTagID);
        }

        String xmlInput =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:vr=\"http://vr.org.vn/\">\n" +
                        "  <soap:Header/>\n" +
                        "  <soap:Body>\n" +
                        "    <vr:LayTT4VIETINF>\n" +
                        "      <vr:strUserID>" + user + "</vr:strUserID>\n" +
                        "      <vr:strPT_ID>" + strPT_ID + "</vr:strPT_ID>\n" +
                        "      <vr:strTagID>" + tagID + "</vr:strTagID>\n" +
                        "    </vr:LayTT4VIETINF>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";
        return xmlInput;
    }

    private String requestSOAP(String wsURL, String xmlInput, int step) {
        String outputString = "";
        try {
            if (step <= Integer.parseInt(numberRetry)) {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(Long.parseLong("30"), TimeUnit.SECONDS);
                client.setReadTimeout(30, TimeUnit.SECONDS);
                client.setWriteTimeout(30, TimeUnit.SECONDS);
                HttpUrl.Builder httpBuilder = HttpUrl.parse(wsURL).newBuilder();
                RequestBody body = RequestBody.create(MediaType.parse("application/soap+xml; charset=utf-8"), xmlInput);
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.code() != HttpStatus.OK.value()) {
                    throw new Exception("crm.exception-list.error.xcg");
                }
                String strRes = response.body().string();
                outputString = outputString + strRes.replace("null", "\"\"");
            }
        } catch (Exception e) {
            LOGGER.error("Has error call WSRegister", e);
            requestSOAP(wsURL, xmlInput, ++step);
        }
        return outputString;
    }

    /**
     * Doc thong tin tra ve
     *
     * @param response
     */
    private GetInfoRegisterDTO readResponseWSRegister(String response) throws Exception {
        XMLStreamReader xsr = readResponseCommon(response);
        GetInfoRegisterDTO getInfoRegisterDTO = null;
        if (xsr != null) {
            xsr.nextTag(); // Advance to Envelope tag+
            xsr.nextTag(); // Advance to Body tag
            xsr.nextTag();
            JAXBContext jc = JAXBContext.newInstance(GetInfoRegisterDTO.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<GetInfoRegisterDTO> je = unmarshaller.unmarshal(xsr, GetInfoRegisterDTO.class);
            getInfoRegisterDTO = je.getValue();
        }
        return getInfoRegisterDTO;

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
}
