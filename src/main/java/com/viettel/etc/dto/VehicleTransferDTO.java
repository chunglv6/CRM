package com.viettel.etc.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleTransferDTO {
    Long transferContractId;

    List<Long> transferVehicleIds;

    Long reasonId;

    Long actTypeId;

    Long price;

    Long amount;

    List<ContractProfileDTO> contractProfileDTOList = new ArrayList<>();
}
