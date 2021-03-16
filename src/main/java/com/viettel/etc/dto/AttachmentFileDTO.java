package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Autogen class DTO: Thong tin tep dinh kem
 *
 * @author ToolGen
 * @date Fri Jul 03 10:59:20 ICT 2020
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AttachmentFileDTO {

    Long attachmentFileId;

    Long attachmentType;

    Long objectId;

    String documentName;

    String documentPath;

    String description;

    String createUser;

    Date createDate;

    String updateUser;

    Date updateDate;

    String status;

    String id;

    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

    @NotNull
    @NotEmpty
    String fileBase64;
}