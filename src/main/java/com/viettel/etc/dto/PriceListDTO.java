package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceListDTO {

    Long stationId;

    Long stageId;

    Long vehicleGroupId;

    String vehicleGroupName;

    String vehicleGroupDescription;

    Long ticketPrices;

    Long monthlyTicketPrices;

    Long quarterlyTicketPrices;

    @JsonIgnore
    Integer startrecord = 0;

    @JsonIgnore
    Integer pagesize = 10;


    public PriceListDTO collectFromEntity(Long vehicleGroupId,List<ServicePlanEntity> servicePlanEntities) {
        PriceListDTO priceListDTO = new PriceListDTO();
        priceListDTO.setVehicleGroupId(vehicleGroupId);

        for(ServicePlanEntity servicePlan: servicePlanEntities) {
            if (Constants.SERVICE_PLAN_TYPE_TURN.equals(servicePlan.getServicePlanTypeId())) {
                priceListDTO.setTicketPrices(servicePlan.getFee());
            } else if (Constants.SERVICE_PLAN_TYPE_MONTH.equals(servicePlan.getServicePlanTypeId())) {
                priceListDTO.setMonthlyTicketPrices(servicePlan.getFee());
            } else if(Constants.SERVICE_PLAN_TYPE_QUART.equals(servicePlan.getServicePlanTypeId())) {
                priceListDTO.setQuarterlyTicketPrices(servicePlan.getFee());
            }
        }
        return priceListDTO;
    }
}
