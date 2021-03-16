package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfirmOrderReqDTO {

    String signed;

    DataReq data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataReq {
        String contract_code;

        String buyer_name;

        String buyer_addr;

        String buyer_phone;

        String buyer_email;

        String contract_name;

        Long contract_price;

        String contract_content;

        Long cod;

        Long commission;

        String vtp_username;

        String vtp_mabuucuc;

        String trans_id;
    }
}
