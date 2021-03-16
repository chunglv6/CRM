package com.viettel.etc.services;

import com.viettel.etc.dto.StageDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface StageService {
    List<StageDTO.Stage> findStagesByListId(String token, List<Long> stageIdList);

    List<LinkedHashMap<?, ?>> findAllStages(String token);

    List<LinkedHashMap<?, ?>> findAllStagesBOO2(String token, Map<String, String> params);

    LinkedHashMap<?, ?> findByStationInAndStationOut(Long stationInId, Long stationOutId, String token);
}
