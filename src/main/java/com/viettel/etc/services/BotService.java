package com.viettel.etc.services;

import com.viettel.etc.xlibrary.core.entities.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;

public interface BotService {
    List<LinkedHashMap<?, ?>> findAllBots(String token);

    ResponseEntity existsBotInStageOrStation(String token, String botIds, String stationId, String stageId);
}
