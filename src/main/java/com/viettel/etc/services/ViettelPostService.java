package com.viettel.etc.services;

import com.viettel.etc.dto.viettelpost.*;
import com.viettel.etc.repositories.tables.entities.ContractEntity;

public interface ViettelPostService {
    BillResponseDTO createBill(BillRequestDTO requestDTO, String remoteAddr);

    String getToken();

    ConfirmOrderResDTO confirmOrder(ConfirmOrderDTO params, String remoteAddr, ContractEntity contractEntity, String tranId, Long cod);

    ProvinceDTO getListProvinceById(Long id);

    DistrictDTO getListDistrict(Long id);

    WardsDTO getListWards(Long id);

    ShopDTO getListShop();
}
