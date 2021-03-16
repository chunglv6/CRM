package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServicePlanFeeDTO {
    List<ServicePlanDTO> listServicePlan;
    String servicePlanVehicleDuplicate;
}
