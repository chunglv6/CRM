package com.viettel.etc.repositories;

import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

/**
 * Autogen class Repository Interface: Lop quan ly chuong trinh khuyen mai/ chiet khau
 *
 * @author toolGen
 * @date Fri Sep 04 15:40:35 ICT 2020
 */
public interface PromotionRepository {

    ResultSelectEntity getPromotions(PromotionDTO itemParamsEntity);

    ResultSelectEntity getPromotionAssignDetail(PromotionAssignDTO itemParamsEntity);
}
