package com.viettel.etc.repositories;
import com.viettel.etc.dto.SearchDepositTransDTO;
import java.util.List;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
/**
 * Autogen class Repository Interface: Lop thao tac xem thong tin giao dich nap tien
 * 
 * @author toolGen
 * @date Mon Feb 08 09:01:09 ICT 2021
 */
public interface SearchDepositTransRepository {


    public ResultSelectEntity getDepositInfo(SearchDepositTransDTO itemParamsEntity);
}