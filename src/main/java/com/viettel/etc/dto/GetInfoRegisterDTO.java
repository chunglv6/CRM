package com.viettel.etc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetInfoRegisterDTO", namespace = "http://vr.org.vn/")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInfoRegisterDTO {
    @XmlElement(name = "LayTT4VIETINFResult", namespace = "http://vr.org.vn/")
    public String LayTT4VIETINFResult;

    public String getLayTT4VIETINFResult() {
        return LayTT4VIETINFResult;
    }
}
