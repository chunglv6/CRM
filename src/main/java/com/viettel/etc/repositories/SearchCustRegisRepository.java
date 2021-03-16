package com.viettel.etc.repositories;

import com.viettel.etc.dto.SearchCustRegisDTO;
import java.util.List;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

/**
 * Autogen class Repository Interface: Lay thong tin khach hang da dang ki
 * 
 * @author toolGen
 * @date Mon Feb 01 15:43:14 ICT 2021
 */
public interface SearchCustRegisRepository {


    public ResultSelectEntity searchCustRegstered(SearchCustRegisDTO itemParamsEntity);
}