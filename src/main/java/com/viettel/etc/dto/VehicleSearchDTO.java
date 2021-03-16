package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleSearchDTO {

    String plateNumber;

    Long plateType;

    @JsonIgnore
    Integer startrecord;

    @JsonIgnore
    Integer pagesize;

    // Biến để check xem có phải từ BOT lấy hay không
    Boolean isPortalBot;
}
