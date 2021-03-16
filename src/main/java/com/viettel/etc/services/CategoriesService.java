package com.viettel.etc.services;

import com.viettel.etc.dto.CategoryDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.LinkedHashMap;
import java.util.List;

public interface CategoriesService {
    ResultSelectEntity findCategoriesCustomer(String token);

    CategoryDTO.Categories findCategoriesByListId(String token, String tableName);

    CategoryDTO.Categories findCategoriesListIdByCode(String token, String tableName, String code);

    List<LinkedHashMap<?, ?>> findCategoriesByListCode(String token, String code);

    CategoryDTO.CategoryData findCategoriesByTableNameAndCode(String token, String tableName, String code);
}
