package com.viettel.etc.dto.viettelpay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCancelInitViettelPayDTO extends RequestLinkViettelPayDTO {
    @NotNull
    String token;
}
