package com.viettel.etc.dto.vietteltelecom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderReqDTO {
    String isdn;

    Customer customer;

    Address address;

    String transactionPlace;

    PayInfo payInfo;

    Long totalFee;

    List<FeeInfo> feeRecords;

    String recipientName;

    String recipientPhone;

    String recipientEmail;

    Long payStatus;

    String target;

    String systemType;

    //////////////////////////////
    //////////  CLASS  ///////////
    //////////////////////////////
    @Data
    @AllArgsConstructor
    public static class Customer {
        String name;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        String province;

        String district;

        String precinct;
    }
    public static class PayInfo {
        Boolean immediatePay;

        String payMethod;

        List<Object> cardRecords = new LinkedList();
    }

    public static class FeeInfo {
        String feeCode;

        Long feeAmount;
    }

    //////////////////////////////
    //////////  ENUM  ////////////
    //////////////////////////////
    public enum TransactionPlace {
        HOME("HOME"),
        SHOP("SHOP");
        String value;
        String getValue() {
            return value;
        }
        TransactionPlace(String value) {
            this.value = value;
        }
    }

    public enum PayStatus {
        NOT_YET(0L),
        SUCCESS(1L),
        FAIL(2L);
        Long value;
        Long getValue() {
            return value;
        }
        PayStatus(Long value) {
            this.value = value;
        }
    }

    public enum PayMethod {
        BANKPLUS("BANKPLUS"),
        VIETTELPAY("VIETTELPAY"),
        CTT("CTT");
        String value;
        String getValue() {
            return value;
        }
        PayMethod(String value) {
            this.value = value;
        }
    }

    //////////////////////////////
    ////////  CONSTANT  //////////
    //////////////////////////////

    public static final String targetConstant = null;
    public static final String systemTypeConstant = "ETC";
}
