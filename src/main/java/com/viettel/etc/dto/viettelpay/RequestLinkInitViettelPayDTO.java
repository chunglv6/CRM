package com.viettel.etc.dto.viettelpay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLinkInitViettelPayDTO extends RequestLinkViettelPayDTO {
    @NotNull
    String contractNo;
    @NotNull
    String contractFullName;
    @NotNull
    String bankCode;
    @NotNull
    String idNo;
    @NotNull
    String idType;
}
