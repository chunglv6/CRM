package com.viettel.etc.services;

import com.viettel.etc.dto.ResponseGetInfoRegisterDTO;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public interface SearchXCGService {
    ResponseGetInfoRegisterDTO getInfoRegister(String strPT_ID, String epc) throws Exception;
}
