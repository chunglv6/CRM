package com.viettel.etc.dto.mobile;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ReqDownloadVersionNew {
    @NotNull
    private String url;

    @NotNull
    private String versionName;
}
