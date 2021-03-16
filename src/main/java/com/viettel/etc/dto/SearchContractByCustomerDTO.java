package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchContractByCustomerDTO {
    Integer custID;
    String contractNo;
    String startDate;
    String endDate;
    String signName;
    String signDate;
    String status;
    Integer startrecord;
    Boolean resultsqlex;
    Integer pagesize;
    Boolean resultSqlEx;
}
