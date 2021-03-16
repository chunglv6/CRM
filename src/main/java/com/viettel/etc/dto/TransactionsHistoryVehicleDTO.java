package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsHistoryVehicleDTO {
    TransactionsHistoryList data;
    StatusTransactionsHistoryVehicleDTO mess;

    @Data
    public static class StatusTransactionsHistoryVehicleDTO {
        Long code;
        String description;
    }

    @Data
    public static class TransactionsHistory{
        Long id;
        String ticketId;
        String plateNumber;
    }

    @Data
    public static class TransactionsHistoryList{
        List<TransactionsHistory> listData;
    }
}
