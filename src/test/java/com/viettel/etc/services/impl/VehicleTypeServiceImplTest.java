package com.viettel.etc.services.impl;

import com.viettel.etc.dto.VehicleTypeDTO;
import com.viettel.etc.dto.boo.VehicleTypeBooDTO;
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

class VehicleTypeServiceImplTest {

    private VehicleTypeServiceImpl vehicleTypeServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        vehicleTypeServiceImplUnderTest = new VehicleTypeServiceImpl();
        vehicleTypeServiceImplUnderTest.vehicleTypeUrl = "vehicleTypeUrl";
        vehicleTypeServiceImplUnderTest.vehicleTypeByCodeUrl = "vehicleTypeByCodeUrl";
        vehicleTypeServiceImplUnderTest.urlVehicleTypeById = "urlVehicleTypeById";

    }

    @Ignore
    void testFindAllVehicleType() {
        // Setup

        // Run the test
        final List<LinkedHashMap<?, ?>> result = vehicleTypeServiceImplUnderTest.findAllVehicleType("token");

        // Verify the results
    }

    @Test
    void testFindVehicleTypeByListId() {
        // Setup
        final List<VehicleTypeDTO.VehicleType> expectedResult = Arrays.asList(new VehicleTypeDTO.VehicleType("assdsads", 5527L));
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                return "{\"data\":[{\n" +
                        "    \"name\": \"assdsads\",\n" +
                        "    \"id\": 5527,\n" +
                        "    \"code\": \"1\",\n" +
                        "    \"route_id\": \"1\""+
                        "}]}";
            }
        };
        // Run the test
        final List<VehicleTypeDTO.VehicleType> result = vehicleTypeServiceImplUnderTest.findVehicleTypeByListId("token", Arrays.asList(0L));

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindByName() {
        // Setup

        // Run the test
        final List<LinkedHashMap<?, ?>> result = vehicleTypeServiceImplUnderTest.findByName("token", "name");

        // Verify the results
    }

    @Test
    void testFindById() {
        // Setup

        // Run the test
        final LinkedHashMap<?, ?> result = vehicleTypeServiceImplUnderTest.findById("token", "id");

        // Verify the results
    }

    @Test
    void testFindByCode() {
        // Setup
        final VehicleTypeBooDTO expectedResult = new VehicleTypeBooDTO(new VehicleTypeBooDTO.VehicleType("assdsads", "1", 5527L));
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                return "{\"data\":{\n" +
                        "    \"name\": \"assdsads\",\n" +
                        "    \"id\": 5527,\n" +
                        "    \"code\": \"1\",\n" +
                        "    \"route_id\": \"1\""+
                        "}}";
            }
        };
        // Run the test
        final VehicleTypeBooDTO result = vehicleTypeServiceImplUnderTest.findByCode("token", "code");

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
