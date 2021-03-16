package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/***
 * Du lieu truyen vao mua ve thang quy tu BOO1
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqChargeTicketDTO {
    String station_type;
    Long station_in_id;
    Long station_out_id;
    String ticket_type;
    String start_date;
    String end_date;
    String plate;
    String etag;
    Long vehicle_type;
    String register_vehicle_type;
    Long seat;
    Double weight_goods;
    Double weight_all;
    Long request_id;
    Long request_datetime;

    public ReqCalculatorTicketDTO toReqCalculatorTicketBooDTO(ReqChargeTicketDTO requestChargeTicketBooDTO){
        ReqCalculatorTicketDTO reqCalculatorTicketBooDTO = new ReqCalculatorTicketDTO();
        reqCalculatorTicketBooDTO.setStation_type(requestChargeTicketBooDTO.getStation_type());
        reqCalculatorTicketBooDTO.setStation_in_id(requestChargeTicketBooDTO.getStation_in_id());
        reqCalculatorTicketBooDTO.setStation_out_id(requestChargeTicketBooDTO.getStation_out_id());
        reqCalculatorTicketBooDTO.setTicket_type(requestChargeTicketBooDTO.getTicket_type());
        reqCalculatorTicketBooDTO.setStart_date(requestChargeTicketBooDTO.getStart_date());
        reqCalculatorTicketBooDTO.setEnd_date(requestChargeTicketBooDTO.getEnd_date());
        reqCalculatorTicketBooDTO.setPlate(requestChargeTicketBooDTO.getPlate());
        reqCalculatorTicketBooDTO.setEtag(requestChargeTicketBooDTO.getEtag());
        reqCalculatorTicketBooDTO.setVehicle_type(requestChargeTicketBooDTO.getVehicle_type());
        reqCalculatorTicketBooDTO.setRegister_vehicle_type(requestChargeTicketBooDTO.getRegister_vehicle_type());
        reqCalculatorTicketBooDTO.setSeat(requestChargeTicketBooDTO.getSeat());
        reqCalculatorTicketBooDTO.setSeat(requestChargeTicketBooDTO.getSeat());
        reqCalculatorTicketBooDTO.setWeight_goods(requestChargeTicketBooDTO.getWeight_goods());
        reqCalculatorTicketBooDTO.setWeight_all(requestChargeTicketBooDTO.getWeight_all());
        return reqCalculatorTicketBooDTO;
    }
}
