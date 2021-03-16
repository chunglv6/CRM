package com.viettel.etc.repositories.tables.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Attachment_file
 *
 * @author ToolGen
 * @date Fri Jul 03 10:59:25 ICT 2020
 */

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ATTACHMENT_FILE")
public class AttachmentFileEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "ATTACHMENT_FILE_SEQ")
    @SequenceGenerator(name = "ATTACHMENT_FILE_SEQ", sequenceName = "ATTACHMENT_FILE_SEQ", allocationSize = 1)
    @Column(name = "ATTACHMENT_FILE_ID")
    Long attachmentFileId;

    @Column(name = "ATTACHMENT_TYPE")
    Long attachmentType;

    @Column(name = "OBJECT_ID")
    Long objectId;

    @Column(name = "DOCUMENT_NAME")
    String documentName;

    @Column(name = "DOCUMENT_PATH")
    String documentPath;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "CREATE_USER")
    String createUser;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "STATUS")
    String status;

    public enum ATTACH_TYPE {
        SERVICE_PLAN(1L),
        EXCEPTION(2L),
        CONTRACT_PAYMENT(3L),
        PROMOTION(4L),
        OTHER(5L);

        public final Long value;

        ATTACH_TYPE(Long value) {
            this.value = value;
        }
    }

    public enum STATUS {
        ACTIVE("1"),
        NOT_ACTIVE("0");

        public final String value;

        STATUS(String value) {
            this.value = value;
        }
    }
}
