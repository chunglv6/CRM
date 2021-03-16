package com.viettel.etc.utils;

import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;

import java.util.HashMap;
import java.util.Map;

public enum ErrorApp {
    SUCCESS(1, CoreErrorApp.SUCCESS.getDescription()),
    DATA_EMPTY(5, "common.validate.data.empty"),
    ERR_DATA(10, "err.data"),
    ERR_DATA_EXCEL_FILE(2,"crm.vehicles.exception.excel.validate"),
    ERR_DATA_EXCEL_FORMAT(40, "crm.vehicles.exception.excel");

    private int code;
    private String description;

    ErrorApp(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(int code){this.code = code;}

    public int getCode() {
        return code;
    }

    public String toStringJson() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code", code);
        objectMap.put("description", description);
        return FnCommon.convertObjectToStringJson(objectMap);
    }
}
