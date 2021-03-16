package com.viettel.etc.services;

import com.viettel.etc.dto.boo.*;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface Boo1Service {
    ResActivationCheckDTO findVehicleByPlateNumber(ReqActivationCheckDTO reqActivationCheckDTO, Authentication authentication, Long actTypeId) throws IOException;

    ResCancelTicketDTO destroyTicket(ReqCancelTicketDTO request) throws IOException;

    ResCancelResultDTO cancelResult(ReqCancelResultDTO requestCancelResult) throws Exception;

    ResCalculatorTicketDTO calculatorTicketBoo1(ReqCalculatorTicketDTO reqCalculatorTicketDTO, Authentication authentication) throws IOException;

    ResChargeTicketDTO chargeTicketBoo1(ReqChargeTicketDTO reqChargeTicketDTO, Authentication authentication, Long actTypeId, Long requestOutId) throws IOException;

    ResOnlineEventDTO onlineEventSync(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO, Authentication authentication, Long actTypeId);


}
