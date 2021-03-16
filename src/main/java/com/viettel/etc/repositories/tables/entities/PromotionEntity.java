package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Promotion
 * 
 * @author ToolGen
 * @date Fri Sep 04 15:40:38 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "PROMOTION")
public class PromotionEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "PROMOTION_SEQ")
    @SequenceGenerator(name = "PROMOTION_SEQ", sequenceName = "PROMOTION_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "PROMOTION_ID")
    Long promotionId;

    @Column(name = "PROMOTION_CODE")
    String promotionCode;

    @Column(name = "PROMOTION_NAME")
    String promotionName;

    @Column(name = "PROMOTION_CONTENT")
    String promotionContent;

    @Column(name = "PROMOTION_LEVEL")
    String promotionLevel;

    @Column(name = "PROMOTION_TYPE")
    String promotionType;

    @Column(name = "PROMOTION_AMOUNT")
    Double promotionAmount;

    @Column(name = "STATUS")
    String status;

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "CREATE_USER")
    String createUser;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    Date createDate;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "APPROVED_STATUS")
    Long approvedStatus;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "APPROVED_USER")
    String approvedUser;

    @Column(name = "DOC_REFER")
    String docRefer;

    @Column(name = "DOC_FILE_PATH")
    String docFilePath;

    @Column(name = "STATION_ID")
    Long stationId;

    @Column(name = "STAGE_ID")
    Long stageId;

    public boolean equals(PromotionEntity pro) {
        return promotionName.equals(pro.getPromotionName()) &&
                promotionLevel.equals(pro.getPromotionLevel()) &&
                promotionAmount.equals(pro.getPromotionAmount()) &&
                promotionContent.equals(pro.getPromotionContent()) &&
                description.equals(pro.getDescription()) &&
                docRefer.equals(pro.getDocRefer()) &&
                expDate.equals(pro.getExpDate());
    }
}
