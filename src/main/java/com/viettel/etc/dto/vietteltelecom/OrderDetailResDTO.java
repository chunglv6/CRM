package com.viettel.etc.dto.vietteltelecom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResDTO {
    String processInstanceId;

    @JsonProperty(value = "Isdn")
    String isdn;

    @JsonProperty(value = "Customer")
    Customer customer;

    String recipientEmail;

    ProductInfo productInfo;

    IsdnPledgeInfo isdnPledgeInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Customer {
        String name;

        String birthDate;

        String idNo;

        String idType;

        String idIssueDate;

        String idIssuePlace;

        String idExpireDate;

        String gender;

        String nationality;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProductInfo {
        String bundleCode;

        String bundleName;

        String bundleDesc;

        String productCode;

        String regReasonCode;

        Long regReasonId;

        Long price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsdnPledgeInfo {
        String isdn;

        Long price;

        Long posPrice;
    }
}
