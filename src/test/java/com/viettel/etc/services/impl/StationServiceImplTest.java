package com.viettel.etc.services.impl;

import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.StationDTO;
import com.viettel.etc.utils.FnCommon;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StationServiceImplTest {

    private StationServiceImpl stationServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        stationServiceImplUnderTest = new StationServiceImpl();
    }

    @Test
    void testFindStationsByListId() {
        // Setup
        final List<StationDTO.Station> expectedResult = Arrays.asList(new StationDTO.Station("assdsads", 5527L, 1L, 1L));

        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                return "{\"data\":[{\n" +
                        "    \"name\": \"assdsads\",\n" +
                        "    \"id\": 5527,\n" +
                        "    \"method_charge_id\": \"1\",\n" +
                        "    \"route_id\": \"1\""+
                        "}]}";
            }
        };
        // Run the test
        final List<StationDTO.Station> result = stationServiceImplUnderTest.findStationsByListId("token", Arrays.asList(0L));

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAllStations() {
        // Setup

        // Run the test
        final List<LinkedHashMap<?, ?>> result = stationServiceImplUnderTest.findAllStations("token");

        // Verify the results
    }

    @Ignore
    void testFindById() {
        // Setup
        // Run the test
        final LinkedTreeMap<?, ?> result = stationServiceImplUnderTest.findById("token", 0L);

        // Verify the results
    }
}
