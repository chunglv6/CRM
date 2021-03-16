package com.viettel.etc.services;

import com.viettel.etc.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TicketService {

    List<DataTypeDTO.DataType> getTicketTypes(String token);

    ResponseChargeTicketCRM chargeTicketNew(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, long custId, long contractId, long contractIdBuyTicket) throws Exception;

    ResponseChargeTicketCRM chargeTicketNewOutContractId(ListAddSupOfferRequestDTO listAddSupOfferRequestDTO, Authentication authentication, long custId, long contractId) throws Exception;

    Object searchTicketDelBoo1(Long contractId, SaleTransDelBoo1DTO saleTransDelBoo1DTO) throws Exception;

    Object confirmDestroyTicket(Long saleTransDetailId, SaleTransDelBoo1DTO saleTransDelBoo1DTO, Authentication authentication) throws Exception;

    Object destroyTicketRefund(Long subscriptionTicketId, SaleTransDelBoo1DTO saleTransDelBoo1DTO, Authentication authentication) throws Exception;

    Object destroyTicketNotRefund(Long saleTransDetailId, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception;

    Object cancelAutoRenew(Long saleTransDetailId, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception;

    Object registerAutoRenew(Long saleTransDetailId, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception;
}
