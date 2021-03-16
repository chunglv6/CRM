package com.viettel.etc.services;

import com.viettel.etc.repositories.tables.entities.ContractEntity;
import org.springframework.security.core.Authentication;

public interface LuckyService {
    String genLuckyCode (ContractEntity contractEntity, int type, Authentication authentication);
}
