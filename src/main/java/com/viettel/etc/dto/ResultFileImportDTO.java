package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultFileImportDTO {
    List<AddVehicleRequestDTO> importVehicleList;
    List<String> listError;
    byte [] result;
}
