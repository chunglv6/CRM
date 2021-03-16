package com.viettel.etc.dto.boo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Du lieu phan hoi khi Boo mua ve thang quy
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResCalculatorTicketDTO {
    Long vehicle_type;
    Long price_amount;
    Long service_plan_id;
    Long service_plan_type_id;
    Long charge_method_id;
    String ocs_code;
    Long auto_renew;
    Long scope;
    Long stage_id;
    Long process_datetime;
    Long response_datetime;
}
