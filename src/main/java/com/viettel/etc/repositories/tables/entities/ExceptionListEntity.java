package com.viettel.etc.repositories.tables.entities;

import com.viettel.etc.dto.boo.DataBooDTO;
import com.viettel.etc.dto.boo.ReqOnlineEventRegDTO;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

/**
 * Autogen class Entity: Create Entity For Table Name Exception_list
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EXCEPTION_LIST")
public class ExceptionListEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "EXCEPTION_LIST_SEQ")
    @SequenceGenerator(name = "EXCEPTION_LIST_SEQ", sequenceName = "EXCEPTION_LIST_SEQ", allocationSize = 1)
    @Column(name = "EXCEPTION_LIST_ID")
    Long exceptionListId;

    @Column(name = "STATION_ID")
    Long stationId;

    @Column(name = "STAGE_ID")
    Long stageId;

    @Column(name = "PLATE_NUMBER")
    String plateNumber;

    @Column(name = "EPC")
    String epc;

    @Column(name = "TID")
    String tid;

    @Column(name = "RFID_SERIAL")
    String rfidSerial;

    @Column(name = "EXCEPTION_TYPE")
    String exceptionType;

    @Column(name = "VEHICLE_TYPE_ID")
    Long vehicleTypeId;

    @Column(name = "REGISTER_VEHICLE_TYPE")
    Long registerVehicleType;

    @Column(name = "EXCEPTION_TICKET_TYPE")
    String exceptionTicketType;

    @Column(name = "STATUS")
    Long status;

    @Column(name = "LICENSE_PLATE_TYPE")
    Long licensePlateType;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;

    @Column(name = "CUSTOMER_NAME")
    String customerName;

    @Column(name = "CONTRACT_NO")
    String contractNo;

    @Column(name = "CONTRACT_ID")
    String contractId;

    @Column(name = "CREATE_USER")
    String createUser;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "APPROVED_USER")
    String approvedUser;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "CANCEL_USER")
    String cancelUser;

    @Column(name = "CANCEL_DATE")
    Date cancelDate;

    @Column(name = "STAGE_NAME")
    String stageName;

    @Column(name = "STATION_NAME")
    String stationName;

    @Column(name = "LANE_IN_ID")
    Long laneInId;

    @Column(name = "LANE_IN_NAME")
    String laneInName;

    @Column(name = "LANE_OUT_ID")
    Long laneOutId;

    @Column(name = "LANE_OUT_NAME")
    String laneOutName;

    @Column(name = "EXCEPTION_VEHICLE_TYPE")
    String exceptionVehicleType;

    @Column(name = "ROUTER_ID")
    Long routerId;

    @Column(name = "ROUTER_NAME")
    String routerName;

    @Column(name = "CUSTOMER_ID")
    Long customerId;

    @Column(name = "PROCESS_USER")
    String processUser;

    @Column(name = "PROCESS_DATE")
    Date processDate;

    @Column(name = "PROCESS_COMMNENTS")
    String processComments;

    @Column(name = "APPROVE_COMMNENTS")
    String approveComments;

    @Column(name = "CANCEL_COMMNENTS")
    String cancelComments;

    @Column(name = "WHITELIST_TYPE")
    Long whiteListType;

    @Column(name = "BLACKLIST_TYPE")
    Long blackListType;

    @Column(name = "OCS_CODE")
    String ocsCode;

    @Column(name = "VEHICLE_ID")
    Long vehicleId;

    @Column(name = "PROMOTION_ID")
    Long promotionId;

    @Column(name = "PLATE_TYPE")
    String plateType;

    @Column(name = "ORG_PLATE_NUMBER")
    String orgPlateNumber;

    @Column(name = "PRICE_TURN")
    Long priceTurn;

    @Column(name = "PRICE_MONTHLY")
    Long priceMonthly;

    @Column(name = "PRICE_QUARTERLY")
    Long priceQuarterly;

    @Column(name = "PLATE_TYPE_CODE")
    String plateTypeCode;

    @Column(name = "IS_TRANSFERRED_BOO1")
    Long isTransferredBoo1;

    public enum ExceptionType {
        VEHICLE_TYPE("1"),
        SERVICE_PLAN_TYPE("2"),
        VEHICLE_PRIORITY("3"),
        VEHICLE_FORBIDDEN("4");

        public final String value;

        ExceptionType(String value) {
            this.value = value;
        }
    }

    public enum Status {
        NEW(1L),
        WAIT_APPROVAL(2L),
        REJECT(3L),
        APPROVAL(4L),
        CANCEL(7L);

        public final Long value;

        Status(Long value) {
            this.value = value;
        }
    }


    public ExceptionListEntity toInsertExceptionListEntity(ReqOnlineEventRegDTO requestOnlineEventRegBooDTO, DataBooDTO dataBooDTO, Authentication authentication, Long stageId) {
        ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
        exceptionListEntity.setPlateNumber(FnCommon.formatPlateBOO1(requestOnlineEventRegBooDTO.getPlate()));
        exceptionListEntity.setOrgPlateNumber(requestOnlineEventRegBooDTO.getPlate());
        exceptionListEntity.setEpc(requestOnlineEventRegBooDTO.getEtag());
        setExceptionType(exceptionListEntity, requestOnlineEventRegBooDTO);
        exceptionListEntity.setRfidSerial(dataBooDTO.getRfIdSerial());
        exceptionListEntity.setTid(dataBooDTO.getTId());
        exceptionListEntity.setCustomerId(dataBooDTO.getCustId());
        exceptionListEntity.setCustomerName(dataBooDTO.getCustName());
        exceptionListEntity.setVehicleId(dataBooDTO.getVehicleId());
        Long vehicleType = dataBooDTO.getVehicleTypeId();
        exceptionListEntity.setVehicleTypeId(vehicleType);
        exceptionListEntity.setContractId(String.valueOf(dataBooDTO.getContractId()));
        exceptionListEntity.setContractNo(dataBooDTO.getContractNo());
        Long plateType = dataBooDTO.getPlateType() != null ? Long.valueOf(dataBooDTO.getPlateType()) : null;
        String plateTypeCode = FnCommon.getPlateTypeBOO1(requestOnlineEventRegBooDTO.getPlate());
        exceptionListEntity.setPlateTypeCode(plateTypeCode);
        exceptionListEntity.setLicensePlateType(plateType);
        exceptionListEntity.setStatus(Status.APPROVAL.value);
        exceptionListEntity.setCreateUser(Constants.BOO1);
        exceptionListEntity.setCreateDate(new Date(System.currentTimeMillis()));
        if (!Objects.isNull(stageId)) {
            exceptionListEntity.setStageId(stageId);
        } else {
            exceptionListEntity.setStationId(requestOnlineEventRegBooDTO.getStation_in_id());
        }
        if (Constants.VALID_TYPE_ONLINE_REG.VEHICLE_TYPE.equals(requestOnlineEventRegBooDTO.getType())) {
            exceptionListEntity.setVehicleTypeId(Long.parseLong(requestOnlineEventRegBooDTO.getVehicle_type()));
        }
        if (Constants.VALID_TYPE_ONLINE_REG.SERVICE_PLAN.equals(requestOnlineEventRegBooDTO.getType())) {
            exceptionListEntity.setPriceTurn(requestOnlineEventRegBooDTO.getPrice_turn());
            exceptionListEntity.setPriceMonthly(requestOnlineEventRegBooDTO.getPrice_monthly());
            exceptionListEntity.setPriceQuarterly(requestOnlineEventRegBooDTO.getPrice_quarterly());
        }
        setEffDateAndExpDate(requestOnlineEventRegBooDTO, exceptionListEntity);
        return exceptionListEntity;
    }

    public List<ExceptionListEntity> toUpdateExceptionListEntity(ReqOnlineEventRegDTO requestOnlineEventRegBooDTO, List<ExceptionListEntity> exceptionListEntities, Long stageId) {
        for (ExceptionListEntity exceptionListEntity : exceptionListEntities) {
            exceptionListEntity.setPlateNumber(requestOnlineEventRegBooDTO.getPlate());
            setExceptionType(exceptionListEntity, requestOnlineEventRegBooDTO);
            setEffDateAndExpDate(requestOnlineEventRegBooDTO, exceptionListEntity);
        }

        return exceptionListEntities;
    }

    public static void setEffDateAndExpDate(ReqOnlineEventRegDTO requestOnlineEventRegBooDTO, ExceptionListEntity exceptionListEntity) {
        java.util.Date startDate = FnCommon.convertStringToDate(requestOnlineEventRegBooDTO.getStart_date(), Constants.COMMON_DATE_FORMAT_BOO_24H);
        java.util.Date endDate = FnCommon.convertStringToDate(requestOnlineEventRegBooDTO.getEnd_date(), Constants.COMMON_DATE_FORMAT_BOO_24H);
        if (startDate != null) {
            exceptionListEntity.setEffDate(new Date(startDate.getTime()));
        }

        if (endDate != null && FnCommon.compareDate(endDate) && endDate.compareTo(startDate) > 0) {
            exceptionListEntity.setExpDate(new java.sql.Date(endDate.getTime()));
        } else {
            exceptionListEntity.setExpDate(null);
        }
    }

    public static void setExceptionType(ExceptionListEntity exceptionListEntity, ReqOnlineEventRegDTO requestOnlineEventRegBooDTO) {
        switch (requestOnlineEventRegBooDTO.getType()) {
            case Constants.VALID_TYPE_ONLINE_REG.BLACK_LIST:
                exceptionListEntity.setExceptionType(ExceptionType.VEHICLE_FORBIDDEN.value);
                break;
            case Constants.VALID_TYPE_ONLINE_REG.WHITE_lIST:
                exceptionListEntity.setExceptionType(ExceptionType.VEHICLE_PRIORITY.value);
                break;
            case Constants.VALID_TYPE_ONLINE_REG.SERVICE_PLAN:
                exceptionListEntity.setExceptionType(ExceptionType.SERVICE_PLAN_TYPE.value);
                break;
            case Constants.VALID_TYPE_ONLINE_REG.VEHICLE_TYPE:
                exceptionListEntity.setExceptionType(ExceptionType.VEHICLE_TYPE.value);
                exceptionListEntity.setExceptionVehicleType(requestOnlineEventRegBooDTO.getException_vehicle_type());
                break;
            default:
                break;
        }
    }

}
