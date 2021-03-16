package com.viettel.etc.repositories;

import com.viettel.etc.dto.DocumentDTO;

import java.util.List;

/**
 * Autogen class Repository Interface: Lop lay danh sach ve chung tu
 *
 * @author toolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
public interface DocumentRepository {

    List<DocumentDTO> getDocumentType(DocumentDTO itemParamsEntity);

    List<DocumentDTO> getDocumentByCustTypeId(DocumentDTO itemParamsEntity, Long custTypeId);

    List<DocumentDTO> getDocumentTypes(DocumentDTO itemParamsEntity);
}
