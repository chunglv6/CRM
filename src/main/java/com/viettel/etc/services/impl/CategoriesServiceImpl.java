package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.etc.dto.CategoryDTO;
import com.viettel.etc.services.CategoriesService;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResponseEntity;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Data
    class ConverDataCategories {
        ResultSelectEntity data;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CategoriesServiceImpl.class);

    @Value("${ws.dmdc.categories}")
    private String categoryUrl;

    @Value("${ws.dmdc.categories.method-recharge}")
    private String categoryMethodRechargeUrl;

    /**
     * Lay thong tin categories
     *
     * @param token
     * @return
     * @throws Exception
     * @author Chucnd
     */
    @Override
    public ResultSelectEntity findCategoriesCustomer(String token) {
        String strResp = FnCommon.doGetRequest(categoryUrl, null, token);
        ConverDataCategories converDataCategories = new Gson().fromJson(strResp, ConverDataCategories.class);
        return converDataCategories.getData();
    }

    /**
     * Lay danh sach danh muc voi listId danh muc
     *
     * @param token
     * @param tableName
     * @return
     */
    @Override
    public CategoryDTO.Categories findCategoriesByListId(String token, String tableName) {
        Map<String, String> params = new HashMap<>();
        params.put("table_name", tableName);
        CategoryDTO.Categories result = null;
        String strResp = FnCommon.doGetRequest(categoryUrl, params, token);
        CategoryDTO categoryDTO = new Gson().fromJson(strResp, CategoryDTO.class);
        if (categoryDTO.getData() != null) {
            result = categoryDTO.getData();
        }
        return result;
    }

    @Override
    public CategoryDTO.Categories findCategoriesListIdByCode(String token, String tableName, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("table_name", tableName);
        params.put("code", code);
        CategoryDTO.Categories result = null;
        String strResp = FnCommon.doGetRequest(categoryUrl, params, token);
        CategoryDTO categoryDTO = new Gson().fromJson(strResp, CategoryDTO.class);
        if (categoryDTO.getData() != null) {
            result = categoryDTO.getData();
        }
        return result;
    }

    /**
     * Lay danh sach danh muc bang code
     *
     * @param token
     * @param code
     * @return
     */
    @Override
    public List<LinkedHashMap<?, ?>> findCategoriesByListCode(String token, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        String strResp = FnCommon.doGetRequest(categoryUrl, params, token);
        ObjectMapper jackson = new ObjectMapper();
        ResponseEntity responseEntity = null;
        List<LinkedHashMap<?, ?>> result = null;
        try {
            responseEntity = jackson.readValue(strResp, ResponseEntity.class);
            LinkedHashMap<?, ?> data = (LinkedHashMap<?, ?>) responseEntity.getData();
            if (!FnCommon.isNullObject(data)) {
                result = (List<LinkedHashMap<?, ?>>) data.get("listData");
            }
        } catch (JsonProcessingException e) {
            LOG.error("Loi lay du lieu danh muc: ", e);
        }
        return result;
    }

    @Override
    public CategoryDTO.CategoryData findCategoriesByTableNameAndCode(String token, String tableName, String code) {
        String url = categoryMethodRechargeUrl.replace("{tableName}", tableName);
        String urlMethodRecharge = url.replace("{code}", code);
        String strResp = FnCommon.doGetRequest(urlMethodRecharge, null, token);
        return new Gson().fromJson(strResp, CategoryDTO.CategoryData.class);
    }
}
