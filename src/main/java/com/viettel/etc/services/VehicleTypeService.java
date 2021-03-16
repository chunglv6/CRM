package com.viettel.etc.services;

import com.viettel.etc.dto.VehicleTypeDTO;
import com.viettel.etc.dto.boo.VehicleTypeBooDTO;

import java.util.LinkedHashMap;
import java.util.List;

public interface VehicleTypeService {
    List<LinkedHashMap<?, ?>> findAllVehicleType(String token);

    List<VehicleTypeDTO.VehicleType> findVehicleTypeByListId(String token, List<Long> vehicleTypeIdList);

    List<LinkedHashMap<?, ?>> findByName(String token, String name);

    LinkedHashMap<?, ?> findById(String token, String id);

    VehicleTypeBooDTO findByCode(String token, String code);
}
