package com.viettel.etc.dto.viettelpay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Lop mo ta reponse cho viettelpay
 */
@Data
@NoArgsConstructor
public class ResponseViettelPayDTO {

    @Data
    @NoArgsConstructor
    public static class ResponseContract {
        Object data;
        StatusDTO status;
        Object vehicles;

        public ResponseContract(Object data, StatusDTO status, Object vehicles) {
            this.data = data;
            this.status = status;
            this.vehicles = vehicles;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ResponseRegisterConfirm {
        DataRegisterDTO data;
        StatusDTO status;

        public ResponseRegisterConfirm(DataRegisterDTO data, StatusDTO status) {
            this.data = data;
            this.status = status;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ResponseUnRegisterConfirm {
        DataUnRegisterDTO data;
        StatusDTO status;

        public ResponseUnRegisterConfirm(DataUnRegisterDTO data, StatusDTO status) {
            this.data = data;
            this.status = status;
        }
    }

    @Data
    @NoArgsConstructor
    public static class DataRegisterDTO {
        String orderId;
        String msisdn;
        String bankCode;
        Long contractId;

        public DataRegisterDTO(String orderId, String msisdn, String bankCode, Long contractId) {
            this.orderId = orderId;
            this.msisdn = msisdn;
            this.bankCode = bankCode;
            this.contractId = contractId;
        }
    }

    @Data
    @NoArgsConstructor
    public static class DataUnRegisterDTO {
        String orderId;
        String msisdn;
        Long contractId;

        public DataUnRegisterDTO(String orderId, String msisdn, Long contractId) {
            this.orderId = orderId;
            this.msisdn = msisdn;
            this.contractId = contractId;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseLinkInitDTO {
        StatusDTO status;
        DataRegisterDTO data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataLinkConfirmDTO {
        String token;
        String originalOrderId;
        String bankCode;
        String orderId;
        String contractId;
        String msisdn;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseLinkConfirmDTO {
        StatusDTO status;
        DataLinkConfirmDTO data;
    }

    @Data
    public static class DataCancelInitDTO {
        String otp;
        String orderId;
        String contractId;
        String msisdn;
    }

    @Data
    public static class ResponseCancelInitDTO {
        StatusDTO status;
        DataCancelInitDTO data;
    }

    @Data
    @NoArgsConstructor
    public static class MoneySources {
        String id;
        String accNo;
        String accType;
        String accName;
        String status;
        String bankCode;
        String govIdType;
    }

    @Data
    @NoArgsConstructor
    public static class DataMoneySources {
        List<MoneySources> moneySources;
        String orderId;
        String msisdn;
    }

    @Data
    @NoArgsConstructor
    public static class ResponseGetSourcesViettelPay {
        StatusDTO status;
        DataMoneySources data;
    }

    @Data
    @NoArgsConstructor
    public static class ResponseCancelInit {
        DataCancelInitDTO data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseGetInfoTicketPurchaseAndExtened {
        StatusDTO status;
        ResponseGetInfoTicketPurchaseAndExtendedDTO data;
    }

    @Data
    @NoArgsConstructor
    public static class ResponseAddMoneyResultData {
        String error_code;
        String merchant_code;
        String order_id;
        String return_url;
        String return_bill_code;
        String return_other_info;
        String check_sum;

        public ResponseAddMoneyResultData(String errorCode, String merchantCode, String orderId, String returnUrl,
                                          String returnBillCode, String extraInfo, String checkSum) {
            this.error_code = errorCode;
            this.merchant_code = merchantCode;
            this.order_id = orderId;
            this.return_url = returnUrl;
            this.return_bill_code = returnBillCode;
            this.return_other_info = extraInfo;
            this.check_sum = checkSum;
        }

        public enum ErrorCode {
            SUCCESS("00"),
            FAIL("01");

            public final String value;

            ErrorCode(String value) {
                this.value = value;
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor

    public static class ResponseAddMoneyResult {
        StatusDTO status;
        ResponseAddMoneyResultData data;

        public ResponseAddMoneyResult(ResponseAddMoneyResultData data, StatusDTO status) {
            this.data = data;
            this.status = status;
        }
    }

    @Data
    @NoArgsConstructor
    public static class VerifyViettelPayData {
        String billcode;
        String merchant_code;
        String order_id;
        String trans_amount;
        String error_code;
        String check_sum;

        public VerifyViettelPayData(String billCode, String merchantCode, String orderId, String transAmount, String errorCode, String checkSum) {
            this.billcode = billCode;
            this.merchant_code = merchantCode;
            this.order_id = orderId;
            this.trans_amount = transAmount;
            this.error_code = errorCode;
            this.check_sum = checkSum;
        }

        public enum ErrorCode {
            SUCCESS("00"),
            INVALID_DATA("01"),
            INVALID_CHECK_SUM("02"),
            FAIL("03");

            public final String value;

            ErrorCode(String value) {
                this.value = value;
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseVerifyViettelPayData {
        StatusDTO status;
        VerifyViettelPayData data;

        public ResponseVerifyViettelPayData(VerifyViettelPayData data, StatusDTO status) {
            this.data = data;
            this.status = status;
        }
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class ResponseGetInfoTicketPurchaseAndExtenedPrivateStream {
        StatusDTO status;
        ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO data;
    }
}
