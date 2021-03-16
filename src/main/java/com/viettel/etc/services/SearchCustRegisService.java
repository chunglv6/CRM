package com.viettel.etc.services;

import com.viettel.etc.dto.SearchCustRegisDTO;

import java.io.IOException;

/**
 * Autogen class: Lay thong tin khach hang da dang ki
 * 
 * @author ToolGen
 * @date Mon Feb 01 15:43:14 ICT 2021
 */
public interface SearchCustRegisService {
    

    public Object searchCustRegstered(SearchCustRegisDTO itemParamsEntity);
    String exportCustRegistered(SearchCustRegisDTO params) throws IOException;
}