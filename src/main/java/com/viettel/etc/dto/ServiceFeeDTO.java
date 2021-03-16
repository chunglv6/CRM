package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettel.etc.repositories.tables.entities.ServiceFeeEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceFeeDTO {
    Long id;

    @NotNull
    String serviceFeeName;

    @NotNull
    String serviceFeeCode;

    Long customerId;

    @NotNull
    Long actionTypeId;

    String actTypeName;

    String actTypeCode;

    @NotNull
    Long actReasonId;

    String actReasonName;

    String actReasonCode;

    @NotNull
    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date startDate;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date endDate;

    @DateTimeFormat(pattern = Constants.COMMON_DATE_FORMAT)
    @JsonFormat(pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;

    String createUser;

    Integer status;

    Boolean isActive;

    String approvedUser;

    Date approvedDate;

    Long serviceFeeId;

    @NotNull
    Long fee;

    @NotNull
    String docRefer;

    String description;

    String listId;

    Integer startrecord;

    Integer pagesize;

    public ServiceFeeDTO toResponse(ServiceFeeEntity entity) {
        ServiceFeeDTO serviceFeeDTO = new ServiceFeeDTO();
        serviceFeeDTO.setActionTypeId(entity.getActTypeId());
        serviceFeeDTO.setActReasonId(entity.getActReasonId());
        serviceFeeDTO.setServiceFeeId(entity.getServiceFeeId());
        serviceFeeDTO.setFee(entity.getFee());
        serviceFeeDTO.setCreateDate(entity.getCreateDate());
        serviceFeeDTO.setStartDate(entity.getStartDate());
        serviceFeeDTO.setEndDate(entity.getEndDate());
        serviceFeeDTO.setStatus(entity.getStatus());
        serviceFeeDTO.setApprovedUser(entity.getApprovedUser());
        serviceFeeDTO.setDescription(entity.getDescription());
        serviceFeeDTO.setDocRefer(entity.getDocRefer());
        serviceFeeDTO.setServiceFeeName(entity.getServiceFeeName());
        serviceFeeDTO.setServiceFeeCode(entity.getServiceFeeCode());
        return serviceFeeDTO;
    }

    public boolean isUnChange(ServiceFeeEntity entity) {
        return FnCommon.equal(actionTypeId, entity.getActTypeId()) &&
                FnCommon.equal(actReasonId, entity.getActReasonId()) &&
                FnCommon.equal(fee, entity.getFee()) &&
                FnCommon.equal(startDate, entity.getStartDate()) &&
                FnCommon.equal(endDate, entity.getEndDate()) &&
                FnCommon.equal(description, entity.getDescription()) &&
                FnCommon.equal(docRefer, entity.getDocRefer());
    }
}
