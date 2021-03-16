package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.dto.boo.ReqOnlineEventSyncDTO;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: cap nhat phuong tien sang OCS
 *
 * @author ToolGen
 * @date Thu Jun 11 08:35:38 ICT 2020
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateVehicleRequestOCSDTO {
    private Long seatNumber;
    private Long plateTypeId;
    private String epc;
    private String status;
    private Long actTypeId;
    private String plateNumber;
    private String effDate;
    private Long vehicleGroupId;
    private Long vehicleType;
    private String payloads;
    private String plateTypeCode;

    public enum Status {
        ACTIVE("1"),
        SUSPEND("2");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }

    public UpdateVehicleRequestOCSDTO toEntityModifyVehicle(ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO){
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO();
        updateVehicleRequestOCSDTO.setSeatNumber(requestOnlineEventSyncBooDTO.getSeat_new());
        updateVehicleRequestOCSDTO.setActTypeId(Constants.ACT_TYPE.MODIFY_VEHICLE);
        updateVehicleRequestOCSDTO.setEpc(requestOnlineEventSyncBooDTO.getEtag_old());
        return updateVehicleRequestOCSDTO;
    }
}
