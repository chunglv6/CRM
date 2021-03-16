package com.viettel.etc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SaleBusinessMesDTO {
    @XmlElement(name = "success")
    private String success;

    public String getSuccessResult() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
