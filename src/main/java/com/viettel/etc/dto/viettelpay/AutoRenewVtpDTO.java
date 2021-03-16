package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viettel.etc.utils.Constants;
import lombok.Data;

@Data
public class AutoRenewVtpDTO {
    Long ticket_type;
    String plateNumber;
    Long amount;
    Long stationInId;
    Long stationOutId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT_24H_VTP)
    String effDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT_24H_VTP)
    String expDate;
}
