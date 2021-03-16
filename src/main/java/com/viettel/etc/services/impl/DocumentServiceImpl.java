package com.viettel.etc.services.impl;

import com.viettel.etc.dto.DocumentDTO;
import com.viettel.etc.repositories.DocumentRepository;
import com.viettel.etc.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Autogen class: Lop lay danh sach ve chung tu
 *
 * @author ToolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    /**
     * Lay danh sach ve cac loai chung tu theo loai tac dong
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getDocumentType(DocumentDTO itemParamsEntity) {
        return documentRepository.getDocumentType(itemParamsEntity);
    }

    /**
     * Lay danh loai giay to theo loai khach hang
     *
     * @param itemParamsEntity params client
     * @param custTypeId       loai khach hang
     * @return
     */
    @Override
    public Object getDocumentByCustTypeId(DocumentDTO itemParamsEntity, Long custTypeId) {
        return documentRepository.getDocumentByCustTypeId(itemParamsEntity, custTypeId);
    }

    /**
     * Lay danh sach ve cac loai chung tu
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getDocumentTypes(DocumentDTO itemParamsEntity) {
        return documentRepository.getDocumentTypes(itemParamsEntity);
    }
}
