package com.viettel.etc.dto.momo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MoMoGetContractResponseDTO {
    String requestId;
    String contractNo;
    String contractId;
    String custName;
    String msisdn;
    String errorCode;
    String errorMess;
    String requestTime;
    String extraData;
    Object lstVehicles;

    public MoMoGetContractResponseDTO convertToDataDTO(Object object) {
        List<MoMoGetContractResponseDTO> dataDTOS = (List<MoMoGetContractResponseDTO>) object;
        MoMoGetContractResponseDTO result = null;
        if (!dataDTOS.isEmpty()) {
            result = dataDTOS.get(0);
        } else {
            result = new MoMoGetContractResponseDTO();
        }
        return result;
    }

    public enum ErrorCode {
        SUCCESS("0"),
        INVALID_INPUT("5"),
        CONTRACT_NO_NOT_FOUND("8");
        public final String value;

        ErrorCode(String value) {
            this.value = value;
        }
    }
}
