package com.viettel.etc.services;

import com.viettel.etc.dto.DocumentDTO;

/**
 * Autogen class: Lop lay danh sach ve chung tu
 *
 * @author ToolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
public interface DocumentService {

    Object getDocumentType(DocumentDTO itemParamsEntity);

    Object getDocumentByCustTypeId(DocumentDTO itemParamsEntity, Long custTypeId);

    Object getDocumentTypes(DocumentDTO itemParamsEntity);
}
