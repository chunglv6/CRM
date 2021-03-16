package com.viettel.etc.dto.mobile;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ReqDataCheckVersionMobile {
    @NotNull
    private String appCode;

    @NotNull
    private String deviceType;
}
