package com.viettel.etc.dto;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class SaleServiceAdvanceDTO {
    @XmlElement(name = "listSaleServiceModel")
    public List<SaleServiceModelAdvanceDTO> listSaleServiceModel;
    @XmlElement(name = "listSaleServicePrice")
    public List<ProductPackageFeeDTO> listSaleServicePrice;
    @XmlElement(name = "saleService")
    public ProductPackageDTO saleService;
    @XmlElement(name = "success")
    public String success;

    public List<SaleServiceModelAdvanceDTO> getListModel() {
        return listSaleServiceModel;
    }

    public void setListModel(List<SaleServiceModelAdvanceDTO> listSaleServiceModel) {
        this.listSaleServiceModel = listSaleServiceModel;
    }

    public List<ProductPackageFeeDTO> getListPrice() {
        return listSaleServicePrice;
    }

    public void setListPrice(List<ProductPackageFeeDTO> listSaleServicePrice) {
        this.listSaleServicePrice = listSaleServicePrice;
    }

    public ProductPackageDTO getSale() {
        return saleService;
    }

    public void setSale(ProductPackageDTO saleService) {
        this.saleService = saleService;
    }

    public String getSuccessResult() {
        return success;
    }

    public void setSuccessResult(String success) {
        this.success = success;
    }
}
