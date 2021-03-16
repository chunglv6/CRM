package com.viettel.etc.dto.viettelpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.util.List;

/**
 * Lop mo ta du lieu phan hoi cho viettelpay
 */
@Data
public class DataDTO {
    String orderId;
    Long contractId;
    String contractNo;
    String fullName;
    String custType;
    String idType;
    String idCard;
    String contactTel;
    String address;
    String contractStatus;
    String paymentLinking;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    Date signDate;

    @Data
    public static class Plate{
        String plateNumber;
        Long plateType;
    }


    public DataDTO convertToDataDTO(Object object, String orderId) {
        List<DataDTO> dataDTOS = (List<DataDTO>) object;
        DataDTO result = null;
        if (!dataDTOS.isEmpty()) {
            result = dataDTOS.get(0);
        }else{
            result = new DataDTO();
        }
        result.setOrderId(orderId);
        return result;
    }
}
