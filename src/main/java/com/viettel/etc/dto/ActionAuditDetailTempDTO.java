package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionAuditDetailTempDTO {
    private String columnName;
    private Object oldValue;
    private Object newValue;
}
