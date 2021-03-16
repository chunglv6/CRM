package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ResponseChargeTicketDTO {

    private ResponseStatus status;
    private ResponseData data;

    public ResponseChargeTicketDTO(ResponseStatus status, ResponseData data) {
        this.status = status;
        this.data = data;
    }

    @Data
    public static class SetResponseChargeTicket {
        private String code;
        private String message;
        private String description;
        private String responseTime;
        private String orderId;
        private Long contractId;
        private String billingCode;
        private String msisdn;
    }

    @Data
    @NoArgsConstructor
    public static class ResponseStatus {
        private String code;
        private String message;
        private String description;
        private String responseTime;

        public ResponseStatus(String code, String message, String description, String responseTime) {
            this.code = code;
            this.message = message;
            this.description = description;
            this.responseTime = responseTime;
        }
    }

    @Data
    @NoArgsConstructor

    public static class ResponseData {
        private String orderId;
        private Long contractId;
        private String billingCode;
        private String msisdn;

        public ResponseData(String orderId, Long contractId, String billingCode, String msisdn) {
            this.orderId = orderId;
            this.contractId = contractId;
            this.billingCode = billingCode;
            this.msisdn = msisdn;
        }
    }
}
