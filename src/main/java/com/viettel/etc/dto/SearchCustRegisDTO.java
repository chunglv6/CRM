package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Lay thong tin khach hang da dang ki
 * 
 * @author ToolGen
 * @date Mon Feb 01 15:43:13 ICT 2021
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SearchCustRegisDTO {

    String orderNumber;
    String custName;
    String phoneNumber;
    Date regDate;
    String provinceName;
    String districName;
    String communeName;
    String plateNumber;
    String startDate;
    String endDate;
    Integer startrecord;
    Integer pagesize;


}