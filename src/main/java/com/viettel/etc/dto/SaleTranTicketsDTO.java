package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleTranTicketsDTO {
    Long contractId;
    Integer startRecord;
    Integer pageSize;
}
