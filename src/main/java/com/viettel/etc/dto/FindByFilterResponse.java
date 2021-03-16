package com.viettel.etc.dto;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;


public class FindByFilterResponse {
    @XmlElement(name = "return")
    public List<ProductPackageDTO> productPackageDTOList;

    public List<ProductPackageDTO> getData() {
        return productPackageDTOList;
    }

    public void setData(List<ProductPackageDTO> productPackageDTOList) {
        this.productPackageDTOList = productPackageDTOList;
    }
}
