package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.DocumentDTO;
import com.viettel.etc.repositories.DocumentRepository;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * Autogen class Repository Impl: Lop lay danh sach ve chung tu
 *
 * @author ToolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
@Repository
public class DocumentRepositoryImpl extends CommonDataBaseRepository implements DocumentRepository {

    /**
     * Lay danh sach ve cac loai chung tu theo loai tac dong
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public List<DocumentDTO> getDocumentType(DocumentDTO itemParamsEntity) {
        HashMap<String, Object> hmapParams = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct AT.DOCUMENT_TYPE_ID AS ID, DT.CODE as code, DT.NAME as val, DT.TYPE AS type, DT.CREATE_DATE as createDate \n");
        sql.append("from DOCUMENT_TYPE DT \n");
        sql.append("join ACT_ID_TYPE_MAPPING AT on DT.DOCUMENT_TYPE_ID = AT.DOCUMENT_TYPE_ID \n");
        sql.append("where AT.STATUS = 1 \n");
        if (itemParamsEntity.getId() == null) {
            sql.append("and AT.ACT_TYPE_ID = 2 \n");
        } else {
            sql.append("and AT.ACT_TYPE_ID = :actionTypeId \n");
            hmapParams.put("actionTypeId", itemParamsEntity.getId());
        }
        sql.append(" order by NLSSORT(upper(DT.NAME), 'nls_sort=Vietnamese') \n");
        return getDocumentDTOS(itemParamsEntity, sql, hmapParams);
    }

    /**
     * Lay danh loai giay to theo loai khach hang
     *
     * @param itemParamsEntity: params client truyen len
     * @param custTypeId        loai khach hang
     * @return
     */
    @Override
    public List<DocumentDTO> getDocumentByCustTypeId(DocumentDTO itemParamsEntity, Long custTypeId) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select distinct CT.DOCUMENT_TYPE_ID AS ID, DT.CODE, DT.NAME  AS VAL \n");
        sql.append("from  DOCUMENT_TYPE DT \n");
        sql.append("join CUST_ID_TYPE_MAPPING CT on DT.DOCUMENT_TYPE_ID = CT.DOCUMENT_TYPE_ID \n");
        sql.append("where 1 = 1 \n");
        sql.append("and CT.CUST_TYPE_ID = :custTypeId AND CT.STATUS = 1 ");
        hmapParams.put("custTypeId", custTypeId);
        sql.append(" order by NLSSORT(upper(DT.NAME), 'nls_sort=Vietnamese') \n");
        return getDocumentDTOS(itemParamsEntity, sql, hmapParams);
    }

    /***
     * Excute Query
     * @param itemParamsEntity  : params truyen vao sql
     * @param sql               : cau lenh sql
     * @param hmapParams        : param mapping
     * @return
     */
    private List<DocumentDTO> getDocumentDTOS(DocumentDTO itemParamsEntity, StringBuilder sql, HashMap<String, Object> hmapParams) {
        Integer start = 0;
        if (itemParamsEntity != null && itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pageSize = null;
        if (itemParamsEntity != null && itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        return (List<DocumentDTO>) getListData(sql, hmapParams, start, pageSize, DocumentDTO.class);
    }

    /**
     * Lay danh sach ve cac loai chung tu
     *
     * @return
     */
    @Override
    public List<DocumentDTO> getDocumentTypes(DocumentDTO itemParamsEntity) {
        HashMap<String, Object> hmapParams = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct DOCUMENT_TYPE_ID AS ID, CODE as code, NAME as val, TYPE AS type, CREATE_DATE as createDate \n");
        sql.append("from DOCUMENT_TYPE DT \n");
        sql.append(" order by NLSSORT(upper(DT.NAME), 'nls_sort=Vietnamese') \n");
        return getDocumentDTOS(itemParamsEntity, sql, hmapParams);
    }
}
