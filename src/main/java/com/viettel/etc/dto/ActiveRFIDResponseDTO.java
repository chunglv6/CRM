package com.viettel.etc.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
public class ActiveRFIDResponseDTO {
    List<ActiveRFIDResponseDTO.ActiveResponses> activeResponses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ActiveResponses{
        long vehicleId;
        String  result;
        String plateNumber;
        String reason;
        long code;
        String rfidSerial;
    }
}
