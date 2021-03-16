package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Lop thao tac xem thong tin giao dich nap tien
 * 
 * @author ToolGen
 * @date Mon Feb 08 09:01:07 ICT 2021
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SearchDepositTransDTO {

    Long id;

    Date topupDate;

    String staffName;

    String contractId;

    String saleOrderId;

    Integer startrecord;
    String amount;

    Integer pagesize;
    String startDate;
    String endDate;

    Boolean resultSqlEx;

}