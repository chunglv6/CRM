package com.viettel.etc.repositories;

import com.viettel.etc.dto.TopupAccountDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

public interface TopupAccountConfigRepository {
    ResultSelectEntity findTopupAccount(TopupAccountDTO params);
}
