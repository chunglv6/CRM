package com.viettel.etc.utils.exports;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Itsol_TungPV
 */
@Getter
@Setter
public class ConfigHeaderExport {

    private String headerName;
    private String fieldName;
    private String align;
    private String styleFormat;
    private int width;

    public ConfigHeaderExport(String headerName, String fieldName, String align, String styleFormat, int width) {
        this.headerName = headerName;
        this.fieldName = fieldName;
        this.align = align;
        this.styleFormat = styleFormat;
        this.width = width;
    }
}
