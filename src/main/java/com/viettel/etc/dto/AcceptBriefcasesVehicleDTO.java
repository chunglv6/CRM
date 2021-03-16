package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class AcceptBriefcasesVehicleDTO {
    private Long custId;
    List<BriefcasesDocumentsDTO> listDocument;
    private boolean accept;
    private Long contractId;
    private Long vehicleId;
    private Long actTypeId;
    private String reason;
}
