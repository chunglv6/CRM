package com.viettel.etc.dto;

import lombok.Data;

import java.util.List;

@Data
public class BriefcasesDocumentsDTO {
    private Long documentTypeId;
    private boolean exist;
    private boolean fake;
    private String description;
    List<ProfileDTO> listProfile;
}
