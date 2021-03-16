package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillResponseDTO {
    Integer status;

    Boolean error;

    String message;

    DataResponse data;

    @Data
    public static class DataResponse {
        String ORDER_NUMBER;

        Long MONEY_COLLECTION;

        Long EXCHANGE_WEIGHT;

        Long MONEY_TOTAL;

        Long MONEY_TOTAL_FEE;

        Long MONEY_FEE;

        Long MONEY_COLLECTION_FEE;

        Long MONEY_OTHER_FEE;

        Long MONEY_VAS;

        Long MONEY_VAT;

        Double KPI_HT;

        Long RECEIVER_PROVINCE;

        Long RECEIVER_DISTRICT;

        Long RECEIVER_WARDS;

    }
}
