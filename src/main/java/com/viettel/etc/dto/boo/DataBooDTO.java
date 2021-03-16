package com.viettel.etc.dto.boo;

import lombok.Data;

import java.util.List;

/**
 * Du lieu tim kiem cho BOO
 */
@Data
public class DataBooDTO {
    Long vehicleId;
    String vehicleType;
    Long vehicleTypeId;
    Long custId;
    String custName;
    Long contractId;
    String contractNo;
    String plateType;
    String tId;
    String rfIdSerial;

    public DataBooDTO convertToDataBooDTO(Object object) {
        List<DataBooDTO> dataDTOS = (List<DataBooDTO>) object;
        DataBooDTO result;
        if (!dataDTOS.isEmpty()) {
            result = dataDTOS.get(0);
        } else {
            result = new DataBooDTO();
        }
        return result;
    }
}
