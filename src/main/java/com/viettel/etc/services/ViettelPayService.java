package com.viettel.etc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viettel.etc.dto.AddSupOfferRequestDTO;
import com.viettel.etc.dto.viettelpay.*;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface ViettelPayService {
    Object findByPlateOrContract(RequestBaseViettelDTO data, Authentication authentication);

    Object updateContractPaymentWhenRegister(RequestConfirmRegisterDTO data, Authentication authentication);

    Object updateContractPaymentWhenUnRegister(RequestContractPaymentDTO data, Authentication authentication);

    Object changeMoneySourceViettelPay(RequestConfirmChangeMoneySourceDTO data, Authentication authentication);

    Object linkInitViettelPay(RequestLinkInitViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException;

    Object linkConfirmViettelPay(RequestLinkConfirmViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException;

    Object linkCancelInitViettelPay(RequestCancelInitViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException;

    Object linkCancelConfirmViettelPay(RequestCancelConfirmViettelPayDTO data, Authentication authentication) throws DataNotFoundException, IOException;

    Object getSourcesViettelPay(String msisdn, Long actTypeId, Authentication authentication) throws DataNotFoundException, IOException;

    Object getInfoTicketPurchaseAndExtendedViaSDK(Authentication authentication, String billingCode) throws JsonProcessingException;

    ResponseViettelPayDTO.ResponseUnRegisterConfirm confirmResultTicketPurchaseAutoRenew(RequestRenewTicketPricesDTO requestRenewTicketPricesDTO, Authentication authentication) throws Exception;

    ResponseViettelPayDTO.ResponseUnRegisterConfirm confirmResultCancelTicketPurchaseAutoRenew(RequestRenewTicketPricesDTO requestRenewTicketPricesDTO, Authentication authentication) throws Exception;

    ResponseChargeTicketDTO chargeTicketVTP(Authentication authentication, RequestChargeTicketVTPDTO requestChargeTicketVTPDTO);

    ResAddSupOfferDTO confirmChargeTicket(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId) throws IOException;

    ResponseCallOcsFromApp callOcsToChargeTicket(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId, Long orderId) throws Exception;

    Object getInfoSubscriptionsExtendedViaSDKPrivateStream(Authentication authentication, String billingCode) throws JsonProcessingException;

    ResponseViettelPayDTO.VerifyViettelPayData verifySaleOrder(ViettelPayRequestDTO viettelPayRequestDTO, Authentication authentication);

    ResponseViettelPayDTO.ResponseAddMoneyResultData resultSaleOrder(ViettelPayRequestDTO viettelPayRequestDTO, Authentication authentication);

    Object createSaleOrder(RequestAddSupOfferDTO addSupOfferRequest, Authentication authentication, Long customerId, Long contractId);

}
