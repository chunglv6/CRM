package com.viettel.etc.services;

import com.viettel.etc.dto.FindByFilterResponse;
import com.viettel.etc.dto.SaleBusinessMesDTO;
import com.viettel.etc.dto.SaleServiceAdvanceDTO;
import com.viettel.etc.dto.SaleServiceModelAdvanceDTO;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.security.core.Authentication;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.List;

public interface ProductService {
    String setRequestFindByFilter(String operator, String property, String valueText, String valueType, String user, String pass);

    String setInfoCustomer(Long custId, Authentication authentication);

    String setHeader(String user, String pass);

    String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step);

    FindByFilterResponse readResponse(String response) throws IOException, XMLStreamException, JAXBException;

    XMLStreamReader readResponseCommon(String response) throws IOException, XMLStreamException;

    String setRequestSaleServicesAdvBOBySSCode(String code, String userNameProduct, String passWordProduct);

    SaleServiceAdvanceDTO readResponseSale(String response) throws IOException, XMLStreamException, JAXBException;

    String setRequestSaveSaleTransExternal(String passWordIM, String userIM, List<SaleServiceModelAdvanceDTO> list, SaleServiceAdvanceDTO saleServiceAdvanceDTO, String serial, Long custId, Authentication authentication);

    SaleBusinessMesDTO readResponseIM(String response) throws IOException, XMLStreamException, JAXBException;

    String setListSaleServiceModel(List<SaleServiceModelAdvanceDTO> list, String serial);

    String setListSaleServicePrice(SaleServiceAdvanceDTO saleServiceAdvanceDTO);

    String setSaleService(SaleServiceAdvanceDTO saleServiceAdvanceDTO);

    SaleBusinessMesDTO callBCCSIM(Authentication authentication, Long actTypeId, VehicleEntity vehicleEntity, String serial, Long custId, int caseUpdate, String statusVehicleBoo1) throws EtcException, JAXBException, XMLStreamException, IOException;

}
