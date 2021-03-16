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
public class OrderDetailReqDTO {
    String processId;

    String fromCreateDate;

    String toCreateDate;

    String filters;

    String outputContextKeys;

    String outputOrdExtKeys;

    String outputReportKeys;

    String isdn;

    String orderTypeCode;

    String getHsdt;

    @JsonProperty(value = "Status")
    String status;

    //////////////////////////////
    //////////  ENUM  ////////////
    //////////////////////////////
    public enum STATUS {
        PROCESS("1"),
        DONE("2"),
        CANCEL("4");

        String value;
        String getValue() {
            return value;
        }

        STATUS(String value) {
            this.value = value;
        }
    }

}
