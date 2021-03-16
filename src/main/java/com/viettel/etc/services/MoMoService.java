package com.viettel.etc.services;

import com.viettel.etc.dto.momo.*;
import com.viettel.etc.dto.viettelpay.RequestAddSupOfferDTO;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MoMoService {
    Object findByPlateOrContract(MoMoGetContractRequestDTO data, Authentication authentication);

    Object mobileAppChuPTCreateSaleOrder(RequestAddSupOfferDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId);

    Object portalChuPTCreateSaleOrder(RequestAddSupOfferDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId) throws IOException, InvalidKeyException, NoSuchAlgorithmException;

    MoMoNotifyResponseDTO verifyMoMoRequestData(MoMoNotifyRequestDTO data, Authentication authentication) throws Exception;

    MoMoWebNotifyResponseDTO verifyMoMoRequestDataWeb(MoMoWebNotifyRequestDTO data) throws Exception;

    Object requestMoMoAppPayment(MoMoRawDataDTO data, Authentication authentication) throws Exception;

    MoMoNotifyResponseDTO responseNotifyFromMoMo(MoMoNotifyRequestDTO data, Authentication authentication) throws Exception;

    MoMoAppResponsePaymentDTO handleRequestAddMoneyFromMoMoApp(MoMoAppRequestPaymentDTO data, Authentication authentication);
}
