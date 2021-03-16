package com.viettel.etc.services;

import com.viettel.etc.dto.AcceptBriefcasesDTO;
import com.viettel.etc.dto.AcceptBriefcasesVehicleDTO;
import com.viettel.etc.dto.AdditionalBriefcasesDTO;
import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.security.core.Authentication;

import java.net.UnknownHostException;

public interface BriefcasesService {
    Object additionalBriefcases(AdditionalBriefcasesDTO additionalBriefcasesDTO, Authentication authentication, Long contractId) throws Exception, EtcException;

    Object searchBriefcases(SearchBriefcasesDTO searchBriefcasesDTO);

    void approvalContract(AcceptBriefcasesDTO acceptBriefcasesContractDTO, Authentication authentication, boolean isAccept) throws UnknownHostException;

    void rejectContract(AcceptBriefcasesDTO acceptBriefcasesDTO, Authentication authentication, boolean isAccept);

    void approvalVehicle(AcceptBriefcasesVehicleDTO acceptBriefcasesVehicleDTO, Authentication authentication, boolean isAccept);
}
