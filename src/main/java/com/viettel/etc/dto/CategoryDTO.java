package com.viettel.etc.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    Categories data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Categories {
        List<Category> listData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Category {
        String name;
        Long code;
        Long id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CatagoryMethodRecharge{
        Long methodRechargeId;
        Long id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryData{
        CatagoryMethodRecharge data;
    }
}
