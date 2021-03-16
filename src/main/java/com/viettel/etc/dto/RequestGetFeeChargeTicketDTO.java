package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestGetFeeChargeTicketDTO {
    List<ServicePlanDTO> servicePlanDTOList;
}
