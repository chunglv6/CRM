package com.viettel.etc.services;

import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Autogen class: Lop quan ly chuong trinh khuyen mai/ chiet khau
 *
 * @author ToolGen
 * @date Fri Sep 04 15:40:35 ICT 2020
 */
public interface PromotionService {

    Object getPromotions(PromotionDTO itemParamsEntity);

    Object getDetailPromotion(Long promotionId);

    Object addPromotion(PromotionDTO itemParamsEntity, List<MultipartFile> files, Authentication authentication) throws Exception;

    Object editPromotion(PromotionDTO itemParamsEntity, Authentication authentication, Long promotionId, List<MultipartFile> files) throws Exception;

    Object deletePromotion(Authentication authentication, Long promotionId) throws Exception;

    void deleteAttachment(Long attachmentId);

    Object approvePromotion(String listId, Authentication authentication);

    String exportPromotion(PromotionDTO itemParamsEntity) throws Exception;

    Object getPromotionAssigns(PromotionAssignDTO itemParamsEntity);

    Object addPromotionAssign(PromotionAssignDTO itemParamsEntity, Authentication authentication);

    Object deletePromotionAssign(Authentication authentication, Long promotionAssignId);

    void downloadAttachment(Long attachmentId, HttpServletResponse response) throws IOException;
}