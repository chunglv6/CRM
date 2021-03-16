package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.viettel.etc.repositories.tables.entities.PromotionEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import play.data.format.Formats;

import java.util.Date;
import java.util.List;

/**
 * Autogen class DTO: Lop quan ly chuong trinh khuyen mai/ chiet khau
 *
 * @author ToolGen
 * @date Fri Sep 04 15:40:32 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PromotionDTO {

    Long Id;

    Long promotionId;

    String promotionCode;

    String promotionName;

    String promotionContent;

    String promotionLevel;

    String promotionType;

    Double promotionAmount;

    String status;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date effDate;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date expDate;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;

    String description;

    String docRefer;

    String cancelComment;

    Integer startrecord;

    Integer pagesize;

    Integer approvedStatus;

    Long stationId;

    Long stageId;

    List<Long> stationIds;

    List<Long> stageIds;

    String listId;

    Boolean isActive;

    Boolean resultSqlEx;

    Boolean isEffective;

    Boolean isExpire;

    List<AttachmentFileDTO> fileList;

    public PromotionEntity toAddPromotionEntity() {
        PromotionEntity promotionEntity = new PromotionEntity();

        promotionEntity.setPromotionType(FnCommon.trim(getPromotionType()));
        promotionEntity.setPromotionCode(FnCommon.trim(getPromotionCode()));
        promotionEntity.setPromotionName(FnCommon.trim(getPromotionName()));
        promotionEntity.setPromotionContent(FnCommon.trim(getPromotionContent()));
        promotionEntity.setPromotionType(getPromotionType());
        promotionEntity.setPromotionLevel(getPromotionLevel());
        promotionEntity.setPromotionAmount(getPromotionAmount());
        promotionEntity.setEffDate(getEffDate());
        promotionEntity.setExpDate(getExpDate());
        promotionEntity.setStationId(getStationId());
        promotionEntity.setStageId(getStageId());
        promotionEntity.setDescription(FnCommon.trim(getDescription()));
        promotionEntity.setDocRefer(getDocRefer());

        return promotionEntity;
    }

    public boolean setDataToEntityOnEditPromotion(PromotionEntity promotionEntity) {
        if (promotionName.equals(promotionEntity.getPromotionName()) &&
                FnCommon.equal(promotionLevel, promotionEntity.getPromotionLevel()) &&
                FnCommon.equal(promotionAmount, promotionEntity.getPromotionAmount()) &&
                FnCommon.equal(promotionContent, promotionEntity.getPromotionContent()) &&
                FnCommon.equal(description, promotionEntity.getDescription()) &&
                FnCommon.equal(docRefer, promotionEntity.getDocRefer()) &&
                FnCommon.equal(expDate, promotionEntity.getExpDate())) {
            return false;
        } else {
            promotionEntity.setPromotionName(FnCommon.trim(getPromotionName()));
            promotionEntity.setPromotionContent(FnCommon.trim(getPromotionContent()));
            promotionEntity.setPromotionLevel(getPromotionLevel());
            promotionEntity.setPromotionAmount(getPromotionAmount());
            promotionEntity.setExpDate(getExpDate());
            promotionEntity.setDocRefer(getDocRefer());
            promotionEntity.setDescription(FnCommon.trim(getDescription()));
            return true;
        }
    }

    public PromotionDTO(PromotionEntity promotionEntity) {
        promotionId = promotionEntity.getPromotionId();
        promotionCode = promotionEntity.getPromotionCode();
        promotionType = promotionEntity.getPromotionType();
        promotionName = promotionEntity.getPromotionName();
        promotionContent = promotionEntity.getPromotionContent();
        promotionLevel = promotionEntity.getPromotionLevel();
        promotionAmount = promotionEntity.getPromotionAmount();
        status = promotionEntity.getStatus();
        effDate = promotionEntity.getEffDate();
        expDate = promotionEntity.getExpDate();
        docRefer = promotionEntity.getDocRefer();
        description = promotionEntity.getDescription();
        createUser = promotionEntity.getCreateUser();
        stationId = promotionEntity.getStationId();
        stageId = promotionEntity.getStageId();
    }


    Long promotionAssignId;


    String assignLevel;


    Long custId;


    Long contractId;


    Long vehicleId;


    String createUser;


    Date updateDate;


    String updateUser;


    String plateNumber;


    String epc;

}