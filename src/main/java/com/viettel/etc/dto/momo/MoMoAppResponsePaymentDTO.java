package com.viettel.etc.dto.momo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoMoAppResponsePaymentDTO extends MoMoAppRequestPaymentDTO {
    String responseTime;
    String orderId;
    String errorCode;
    String errorMess;
    public MoMoAppResponsePaymentDTO(MoMoAppRequestPaymentDTO baseData){
        this.setRequestId(baseData.getRequestId());
        this.setContractId(baseData.getContractId());
        this.setContractNo(baseData.getContractNo());
        this.setAmount(baseData.getAmount());
        this.setRequestTime(baseData.getRequestTime());
        this.setMsisdn(baseData.getMsisdn());
    }
}
