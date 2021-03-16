package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Lop thao tac tra cuu thong tin khach hang
 * 
 * @author ToolGen
 * @date Wed Jun 24 16:11:33 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SearchInfoDTO {

    Long custId;

    String custName;

    String contractNo;

    String plateNumber;

    String phoneNumber;

    String documentNumber;

    String rfidSerial;

    String startDate;

    String endDate;

    String status;

    Integer startrecord;

    Boolean resultsqlex;

    Integer pagesize;

    Boolean resultSqlEx;

    String activeStatus;

    Long custTypeId;

    Long documentTypeId;

    Long repIdentityTypeId;

    String repIdentityNumber;

    String repPhoneNumber;

    Long authIdentityTypeId;

    String authIdentityNumber;

    String authPhoneNumber;

    String customerIdList;

    List<Contract> contracts = new ArrayList<>();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contract {
        Long contractId;
        String contractNo;
        List<PlateNumber> plateNumbers = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlateNumber {
        Long vehicleId;
        String plateNumber;
    }
}
