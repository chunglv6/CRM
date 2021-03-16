package com.viettel.etc.repositories;

import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

public interface BriefcasesRepository {
    ResultSelectEntity searchBriefcases(SearchBriefcasesDTO searchBriefcasesDTO);
}
