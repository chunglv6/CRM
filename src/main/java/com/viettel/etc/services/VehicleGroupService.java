package com.viettel.etc.services;


import com.viettel.etc.dto.AddVehicleRequestDTO;
import com.viettel.etc.dto.VehicleGroupDTO;
import com.viettel.etc.dto.VehicleGroupIdDTO;
import com.viettel.etc.dto.VehicleGroupIdResponseDTO;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public interface VehicleGroupService {
    List<VehicleGroupDTO.VehicleGroup> findVehicleGroupByListId(String token, List<Long> vehicleGroupIdList);

    List<String> getVehicleGroupById(String token, AddVehicleRequestDTO addVehicleRequestDTO);

    List<VehicleGroupIdResponseDTO.VehicleGroup> findVehicleGroupByListObject(String token, VehicleGroupIdDTO vehicleGroupIdDTO) throws IOException;

    List<LinkedHashMap<?, ?>> findAllVehicleGroup(String token);
}
