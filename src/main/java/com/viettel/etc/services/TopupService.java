package com.viettel.etc.services;

import com.viettel.etc.dto.*;
import org.springframework.security.core.Authentication;

public interface TopupService {
    Long executeTopupCashToEtc(Authentication authentication, TopupDTO topupDTO) throws Exception;

    Object exportTopupCashBill(Authentication authentication, TopupExportDTO topup);

    Object transferMoney(Authentication authentication, TopupTransferDTO params);

    TopupCtvResDTO addBalance(Authentication authentication, TopupCtvDTO params);

    Object findTopupAccount(TopupAccountDTO params);

    Object addAccount(TopupAccountDTO params, Authentication authentication);

    Object updateAccount(TopupAccountDTO params, Authentication authentication);

    Object detailCTVAccount(String accountUser, Authentication authentication);

    Object deleteCTVAccount(String accountUser);
}
