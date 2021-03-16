package com.viettel.etc.services;

import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.StationDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface StationService {
    List<StationDTO.Station> findStationsByListId(String token, List<Long> stationIdList);

    List<LinkedHashMap<?, ?>> findAllStations(String token);

    List<LinkedHashMap<?, ?>> findAllStationsBOO2(String token, Map<String, String> params);

    LinkedTreeMap<?,?> findById(String token, Long id);
}
