package com.viettel.etc.dto.momo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoMoGetContractRequestDTO {
    String requestId;
    String searchType;
    String plateNumber;
    String contractNo;
    String extraData;

    public enum SearchType {
        CONTRACT_NO("2"),
        PLATE_NUMBER("1");
        public final String value;
        SearchType(String value) {
            this.value = value;
        }
    }
}
