package com.viettel.etc.services;

import java.util.LinkedHashMap;
import java.util.List;

public interface LaneService {
    List<LinkedHashMap<?, ?>> findAllLanes(String token);
}
