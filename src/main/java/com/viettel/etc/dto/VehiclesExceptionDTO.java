package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.sql.Date;
import java.util.List;

/**
 * Autogen class DTO: Phuong tien ngoai le, uu tien, cam
 *
 * @author ToolGen
 * @date Mon Jul 20 18:48:46 ICT 2020
 */
@Builder
@AllArgsConstructor
@Validated
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class VehiclesExceptionDTO {
    Long vehicleId;
    Long exceptionListId;
    Long status;
    Long vehicleTypeId;
    Long registerVehicleType;
    Long licensePlateType;
    Long customerId;
    String epc;
    String tid;
    String rfidSerial;
    String plateNumber;
    String plateName;
    String customerName;
    String contractNo;
    String contractId;

    Long laneInId;
    Long laneOutId;
    Long routerId;
    Long stationId;
    Long stationInId;
    Long stationOutId;
    Long stageId;

    String laneInName;
    String laneOutName;
    String routerName;
    String stationName;
    String stageName;

    String documentName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date effDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date expDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date createDate;
    String createUser;
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date processDate;
    String processUser;
    String processCommnents;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date approvedDate;
    String approvedUser;
    String approveCommnents;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.COMMON_DATE_FORMAT, locale = Constants.LOCALE_VN, timezone = Constants.TIMEZONE_VN)
    Date cancelDate;
    String cancelUser;
    String cancelCommnents;

    String exceptionType;
    List<String> exceptionTypes;
    Long exceptionVehicleType;
    Long exceptionTicketType;
    Long whiteListType;
    Long blackListType;

    Long vehicleGroupId;
    Long promotionId;

    String module;
    Long exceptionGroup;            // 1: xe ngoai le ; 2: xe uu tien/cam
    List<Long> attachmentFileIds;   // Danh sach Id dung de xoa file (Api sua phuong tien ngoai le, uu tien, cam )
    List<Long> exceptionListIds;    // Danh sach Id dung de trinh duyet
    List<Long> stageIds;            // Danh sach ma doan
    List<Long> stationIds;          // Danh sach ma tram
    List<Long> vehicleTypeIds;      // Danh sach loai xe
    List<Long> plateTypes;          // Danh sach loai bien
    String createDateTo;
    String createDateFrom;
    String approvalDateTo;
    String approvalDateFrom;
    String effDateTo;
    String effDateFrom;
    String cancelDateTo;
    String cancelDateFrom;
    String expDateFrom;
    String expDateTo;
    String exceptionName;
    String vehicleTypeName;
    String stationStageName;
    String statusName;
    String effTime;
    String content;

    Integer startrecord;

    Integer pagesize;

    Boolean resultSqlEx;

    List<AttachmentFileDTO> attachmentFiles;
    List<stage> stages;
    List<station> stations;
    boolean effect;
    boolean expire;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class stage {
        Long stageId;
        String stageName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class station {
        Long stationId;
        String stationName;
    }
}
