package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopupCtvResDTO {

    List<CtvResDTO> error;
    List<CtvResDTO> success;

    @Data
    public static class CtvResDTO {
        String accountUser;
        String mess;
    }
}
