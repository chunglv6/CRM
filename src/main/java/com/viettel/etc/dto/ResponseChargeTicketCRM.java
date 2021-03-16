package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseChargeTicketCRM {
    List<ServicePlanDTO> listSuccess;
    List<ServicePlanDTO> listFail;
}
