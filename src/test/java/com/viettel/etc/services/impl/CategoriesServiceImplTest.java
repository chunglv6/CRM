package com.viettel.etc.services.impl;

import com.viettel.etc.dto.CategoryDTO;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoriesServiceImplTest {

    private CategoriesServiceImpl categoriesServieImplUnderTest;

    @BeforeEach
    void setUp() {
        categoriesServieImplUnderTest = new CategoriesServiceImpl();
    }

    @Test
    void testFindCategoriesCustomer() {
        // Setup
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                return "{\"data\":{\n" +
                        "    \"name\": \"assdsads\",\n" +
                        "    \"id\": 5527,\n" +
                        "    \"method_charge_id\": \"1\",\n" +
                        "    \"route_id\": \"1\""+
                        "}}";
            }
        };
        // Run the test
        final ResultSelectEntity result = categoriesServieImplUnderTest.findCategoriesCustomer("token");

        // Verify the results
    }

    @Test
    void testFindCategoriesByListId() {
        // Setup
        final CategoryDTO.Categories expectedResult = new CategoryDTO.Categories(Arrays.asList(new CategoryDTO.Category("assdsads", 5527L, 1L)));
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String doGetRequest(String url, Map<String, String> params, String token) {
                return "{\n" +
                        "    \"data\": {\n" +
                        "        \"listData\": [\n" +
                        "            {\n" +
                        "                \"name\": \"assdsads\",\n" +
                        "                \"code\": 5527,\n" +
                        "                \"id\": 1,\n" +
                        "                \"method_charge_id\": \"1\",\n" +
                        "                \"route_id\": \"1\"\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}";
            }
        };
        // Run the test
        final CategoryDTO.Categories result = categoriesServieImplUnderTest.findCategoriesByListId("token", "tableName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindCategoriesByListCode() {
        // Setup

        // Run the test
        final List<LinkedHashMap<?, ?>> result = categoriesServieImplUnderTest.findCategoriesByListCode("token", "code");

        // Verify the results
    }

    @Ignore
    void testFindCategoriesByTableNameAndCode() {
        // Setup
        final CategoryDTO.CategoryData expectedResult = new CategoryDTO.CategoryData(new CategoryDTO.CatagoryMethodRecharge(0L, 0L));

        // Run the test
        final CategoryDTO.CategoryData result = categoriesServieImplUnderTest.findCategoriesByTableNameAndCode("token", "tableName", "code");

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
