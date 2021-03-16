package com.viettel.etc.services;

import com.viettel.etc.dto.boo.*;
import com.viettel.etc.repositories.tables.entities.CategoryMappingBooEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;

public interface BooService {
    Object queryTicket(Authentication authentication, ReqQueryTicketDTO reqQueryTicketDTO);

    Object calculatorTicket(Authentication authentication, ReqCalculatorTicketDTO reqCalculatorTicketDTO, boolean isCallFromBoo);

    ResChargeTicketDTO chargeTicket(Authentication authentication, ReqChargeTicketDTO reqChargeTicketDTO) throws Exception;

    ResCancelTicketDTO cancelTicket(Authentication authentication, ReqCancelTicketDTO reqCancelTicketDTO);

    ResOnlineEventDTO onlineEventReg(ReqOnlineEventRegDTO reqOnlineEventRegDTO, Authentication authentication) throws Exception;

    ResOnlineEventDTO onlineEventSync(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO, Authentication authentication) throws Exception;

    ResActivationCheckDTO checkActivation(Authentication authentication, ReqActivationCheckDTO reqActivationCheckDTO);

    ResCancelResultDTO cancelResult(Authentication authentication, ReqCancelResultDTO reqCancelResultDTO) throws Exception;

    List<CategoryMappingBooEntity> getListCategoryMappingBoo(Authentication authentication, ReqMappingDTO reqMappingDTO);

}
