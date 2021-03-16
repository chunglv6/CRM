package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileReportDTO {
    String contractNo;

    Long vehicleId;

    String otp;

    String fileName;

    String base64Data;
}
