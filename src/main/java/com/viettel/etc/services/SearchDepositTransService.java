package com.viettel.etc.services;

import com.viettel.etc.dto.SearchDepositTransDTO;

/**
 * Autogen class: Lop thao tac xem thong tin giao dich nap tien
 * 
 * @author ToolGen
 * @date Mon Feb 08 09:01:09 ICT 2021
 */
public interface SearchDepositTransService {
    

    public Object getDepositInfo(SearchDepositTransDTO itemParamsEntity);
    String exportDepositTrans(SearchDepositTransDTO itemParamsEntity);
}