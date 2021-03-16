package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.VehiclesExceptionDTO;
import com.viettel.etc.repositories.AttachmentFileRepository;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.VehiclesExceptionRepository;
import com.viettel.etc.repositories.tables.AttachmentFileRepositoryJPA;
import com.viettel.etc.repositories.tables.ExceptionListRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.AttachmentFileEntity;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.repositories.tables.entities.ServicePlanEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Autogen class Repository Impl: Phuong tien ngoai le, uu tien, cam
 *
 * @author Dongnv
 * @since Mon Jul 20 18:48:49 ICT 2020
 */
@Repository
public class VehiclesExceptionRepositoryImpl extends CommonDataBaseRepository implements VehiclesExceptionRepository {
    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    AttachmentFileRepository attachmentFileRepository;

    @Autowired
    ExceptionListRepositoryJPA exceptionListRepositoryJPA;

    @Autowired
    AttachmentFileRepositoryJPA attachmentFileRepositoryJPA;

    /**
     * API xuat file Excel
     *
     * @param dto         : params client truyen len
     * @param partnerCode : partnerCode của userlogin
     * @param isAdmin
     * @return List<VehiclesExceptionDTO>
     */
    @Override
    public List<VehiclesExceptionDTO> getDataExport(VehiclesExceptionDTO dto, String partnerCode, boolean isAdmin) {
        HashMap<String, Object> hmapParams = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select e.EXCEPTION_LIST_ID exceptionListId,");
        sql.append(" CASE WHEN e.EXCEPTION_TYPE = 1 THEN 'Loại xe' ");
        sql.append(" WHEN e.EXCEPTION_TYPE = 2 THEN 'Loại giá vé' ");
        sql.append(" WHEN e.EXCEPTION_TYPE = 3 THEN 'Ưu tiên' ");
        sql.append(" ELSE 'Cấm' END exceptionName, ");
        sql.append(" c2.name vehicleTypeName, ");
        sql.append(" nvl(e.station_Name, e.stage_Name) stationStageName, ");
        sql.append(" to_char(e.eff_Date, 'dd/MM/yyyy') || ' - ' || to_char(e.exp_Date, 'dd/MM/yyyy') effTime, ");
        sql.append(" CASE WHEN e.EXCEPTION_TYPE = 1 THEN  ");
        sql.append(" (SELECT c3.name FROM CATEGORIES.VEHICLE_GROUP c3 WHERE c3.code= e.EXCEPTION_VEHICLE_TYPE)");
        sql.append(" WHEN e.EXCEPTION_TYPE = 2 THEN ");
        sql.append(" (SELECT c4.val FROM CATEGORIES.CATEGORIES c4 WHERE c4.code= e.EXCEPTION_TICKET_TYPE  and c4.table_name = 'TICKET_PRICE_TYPE')");
        sql.append(" ELSE Null END content,");
        sql.append(" CASE WHEN e.status = 1 THEN 'Tạo mới' ");
        sql.append(" WHEN e.status = 2 THEN 'Chờ duyệt' ");
        sql.append(" WHEN e.status = 3 THEN 'Từ chối duyệt' ");
        sql.append(" WHEN e.status = 4 THEN 'Đã duyệt' ");
        sql.append(" WHEN e.status = 5 THEN 'Đang hiệu lực' ");
        sql.append(" WHEN e.status = 6 THEN 'Hết hiệu lực' ");
        sql.append(" ELSE 'Đã hủy hiệu lực' END statusName, ");
        sql.append(" e.PLATE_NUMBER plateNumber, e.license_Plate_Type licensePlateType, c.val PlateName, e.EPC, e.RFID_SERIAL rfidSerial,");
        sql.append(" e.station_Id stationId, e.station_Name stationName, e.stage_Id stageId, e.stage_Name stageName,");
        sql.append(" e.lane_In_Id laneInId, e.lane_In_Name laneInName, e.lane_Out_Id laneOutId, e.lane_Out_Name laneOutName,");
        sql.append(" e.router_Id routerId, e.router_Name routerName, e.contract_Id contractId, e.contract_No contractNo,");
        sql.append(" e.customer_Id customerId, e.customer_Name customerName, ");
        sql.append(" e.exception_Type exceptionType, e.vehicle_Type_Id vehicleTypeId, e.exception_Ticket_Type exceptionTicketType,");
        sql.append(" e.exception_Vehicle_Type exceptionVehicleType, e.WHITELIST_TYPE whiteListType, e.BLACKLIST_TYPE blackListType, e.status, e.description,");
        sql.append(" e.eff_Date effDate, e.exp_Date expDate, e.create_Date createDate, e.create_User createUser,");
        sql.append(" e.approved_User approvedUser, e.approved_Date approvedDate, e.approve_Commnents approveCommnents,");
        sql.append(" e.cancel_User cancelUser, e.cancel_Date cancelDate, e.cancel_Commnents cancelCommnents");
        sql.append(" FROM crm.EXCEPTION_LIST e ");
        sql.append(" INNER JOIN CATEGORIES.CATEGORIES c ON e.LICENSE_PLATE_TYPE = c.code and c.table_name = 'PLATE_TYPE' ");
        sql.append(" LEFT JOIN CATEGORIES.VEHICLE_GROUP c2 on to_char(e.VEHICLE_TYPE_ID) = c2.code");
        if (dto.getModule().equalsIgnoreCase(Constants.MODULE_BOT) && !isAdmin) {
            sql.append(" LEFT JOIN CATEGORIES.bot_states stages ON e.stage_id = stages.stages_id ");
            sql.append(" LEFT JOIN CATEGORIES.bot_station station ON e.station_id = station.station_id ");
            sql.append(" LEFT JOIN CATEGORIES.bot bot ON (stages.bot_id = bot.id OR station.bot_id = bot.id) ");
        }
        sql.append(" WHERE 1=1");

        //region set where condition
        if (dto.getModule().equalsIgnoreCase(Constants.MODULE_BOT) && !isAdmin) {
            sql.append(" and bot.code = :partnerCode");
            hmapParams.put("partnerCode", partnerCode);
        }

        if (dto.getModule().equalsIgnoreCase(Constants.MODULE_MOT)) {
            sql.append(" and e.status = 4 ");
            if (!dto.isEffect()) {
                sql.append(" and trunc(e.eff_date) <= trunc(sysdate) ");
            }
        }

        if (!FnCommon.isNullOrEmpty(dto.getExceptionType())) {
            sql.append(" and e.exception_Type = :exceptionType ");
            hmapParams.put("exceptionType", dto.getExceptionType().trim());
        } else {
            if (dto.getExceptionGroup() == 1L) { // Xe ngoai le
                sql.append(" and e.exception_Type in (1, 2) ");
            }

            if (dto.getExceptionGroup() == 2L) { // xe uu tien & cam
                sql.append(" and e.exception_Type in (3, 4) ");
            }
        }

        if (!FnCommon.isNullOrEmpty(dto.getPlateNumber())) {
            sql.append(" and e.PLATE_NUMBER like :plateNumber ");
            hmapParams.put("plateNumber", "%" + dto.getPlateNumber().trim() + "%");
        }

        if (!FnCommon.isNullOrEmpty(dto.getPlateTypes())) {
            sql.append(" and e.license_Plate_Type in (:plateTypes) ");
            hmapParams.put("plateTypes", dto.getPlateTypes());
        }

        if (!FnCommon.isNullOrEmpty(dto.getVehicleTypeIds())) {
            sql.append(" and e.vehicle_Type_Id in (:vehicleTypeIds) ");
            hmapParams.put("vehicleTypeIds", dto.getVehicleTypeIds());
        }

        if (!FnCommon.isNullOrEmpty(dto.getEpc())) {
            sql.append(" and e.epc like :epc ");
            hmapParams.put("epc", "%" + dto.getEpc().trim() + "%");
        }

        if (!FnCommon.isNullObject(dto.getExceptionVehicleType())) {
            sql.append(" and e.exception_Vehicle_Type = :exceptionVehicleType ");
            hmapParams.put("exceptionVehicleType", dto.getExceptionVehicleType());
        }

        if (!FnCommon.isNullObject(dto.getExceptionTicketType())) {
            sql.append(" and e.exception_Ticket_Type = :exceptionTicketType ");
            hmapParams.put("exceptionTicketType", dto.getExceptionTicketType());
        }

        if (!FnCommon.isNullObject(dto.getWhiteListType())) {
            sql.append(" and e.WHITELIST_TYPE = :whiteListType ");
            hmapParams.put("whiteListType", dto.getWhiteListType());
        }

        if (!FnCommon.isNullObject(dto.getBlackListType())) {
            sql.append(" and e.BLACKLIST_TYPE = :blackListType ");
            hmapParams.put("blackListType", dto.getBlackListType());
        }

        if (!FnCommon.isNullOrEmpty(dto.getStageIds()) && !FnCommon.isNullOrEmpty(dto.getStationIds())) {
            sql.append(" and (e.stage_Id in (:stageIds) OR e.station_Id in (:stationIds) )");
            hmapParams.put("stageIds", dto.getStageIds());
            hmapParams.put("stationIds", dto.getStationIds());
        } else {
            if (!FnCommon.isNullOrEmpty(dto.getStageIds())) {
                sql.append(" and e.stage_Id in (:stateIds) ");
                hmapParams.put("stateIds", dto.getStageIds());
            }

            if (!FnCommon.isNullOrEmpty(dto.getStationIds())) {
                sql.append(" and e.station_Id in (:stationIds) ");
                hmapParams.put("stationIds", dto.getStationIds());
            }
        }

        if (!FnCommon.isNullObject(dto.getStatus())) {
            sql.append(" and e.status = :status");
            hmapParams.put("status", dto.getStatus());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCreateDateFrom())) {
            sql.append(" and trunc(e.create_Date) >= to_date(:createDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("createDateFrom", dto.getCreateDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCreateDateTo())) {
            sql.append(" and trunc(e.create_Date) <= to_date(:createDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("createDateTo", dto.getCreateDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getEffDateFrom())) {
            sql.append(" and trunc(e.eff_date) >= to_date(:effDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("effDateFrom", dto.getEffDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getEffDateTo())) {
            sql.append(" and trunc(e.eff_date) <= to_date(:effDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("effDateTo", dto.getEffDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getApprovalDateFrom())) {
            sql.append(" and trunc(e.approved_Date) >= to_date(:approvalDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("approvalDateFrom", dto.getApprovalDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getApprovalDateTo())) {
            sql.append(" and trunc(e.approved_Date) <= to_date(:approvalDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("approvalDateTo", dto.getApprovalDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCancelDateFrom())) {
            sql.append(" and trunc(e.cancel_Date) >= to_date(:cancelDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("cancelDateFrom", dto.getCancelDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCancelDateTo())) {
            sql.append(" and trunc(e.cancel_Date) <= to_date(:cancelDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("cancelDateTo", dto.getCancelDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getExpDateFrom())) {
            sql.append(" and trunc(e.EXP_DATE) >= to_date(:expDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("expDateFrom", dto.getExpDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getExpDateTo())) {
            sql.append(" and trunc(e.EXP_DATE) <= to_date(:expDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("expDateTo", dto.getExpDateTo());
        }

        if (dto.isEffect()) {
            sql.append(" and trunc(e.eff_date) <= trunc(sysdate) and (trunc(sysdate) <=  trunc(e.EXP_DATE) or e.EXP_DATE IS NULL) ");
        }

        if (dto.isExpire()) {
            sql.append(" and trunc(sysdate) > trunc(e.EXP_DATE) ");
        }

        sql.append(" ORDER BY e.create_Date desc ");
        //endregion

        Integer start = null;
        Integer pageSize = null;
        return (List<VehiclesExceptionDTO>) getListData(sql, hmapParams, start, pageSize, VehiclesExceptionDTO.class);
    }

    /**
     * API tim kiem phuong tien ngoai le/ uu tien & cam
     *
     * @param dto     : params client truyen len
     * @param isAdmin
     * @return ResultSelectEntity
     */
    @Override
    public ResultSelectEntity getDataVehiclesException(VehiclesExceptionDTO dto, String partnerCode, boolean isAdmin) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select e.EXCEPTION_LIST_ID exceptionListId,");
        sql.append(" CASE WHEN e.EXCEPTION_TYPE = 1 THEN 'Loại xe' ");
        sql.append(" WHEN e.EXCEPTION_TYPE = 2 THEN 'Loại giá vé' ");
        sql.append(" WHEN e.EXCEPTION_TYPE = 3 THEN 'Ưu tiên' ");
        sql.append(" ELSE 'Cấm' END exceptionName, ");
        sql.append(" c2.name vehicleTypeName, ");
        sql.append(" nvl(e.station_Name, e.stage_Name) stationStageName, ");
        sql.append(" to_char(e.eff_Date, 'dd/MM/yyyy') || ' - ' || to_char(e.exp_Date, 'dd/MM/yyyy') effTime, ");
        sql.append(" CASE WHEN e.EXCEPTION_TYPE = 1 THEN  ");
        sql.append(" (SELECT c3.name FROM CATEGORIES.VEHICLE_GROUP c3 WHERE c3.code= e.EXCEPTION_VEHICLE_TYPE)");
        sql.append(" WHEN e.EXCEPTION_TYPE = 2 THEN ");
        sql.append(" (SELECT c4.val FROM CATEGORIES.CATEGORIES c4 WHERE c4.code= e.EXCEPTION_TICKET_TYPE  and c4.table_name = 'TICKET_PRICE_TYPE')");
        sql.append(" ELSE Null END content,");
        sql.append(" CASE WHEN e.status = 1 THEN 'Tạo mới' ");
        sql.append(" WHEN e.status = 2 THEN 'Chờ duyệt' ");
        sql.append(" WHEN e.status = 3 THEN 'Từ chối duyệt' ");
        sql.append(" WHEN e.status = 4 THEN 'Đã duyệt' ");
        sql.append(" WHEN e.status = 5 THEN 'Đang hiệu lực' ");
        sql.append(" WHEN e.status = 6 THEN 'Hết hiệu lực' ");
        sql.append(" ELSE 'Đã hủy hiệu lực' END statusName, ");
        sql.append(" e.PLATE_NUMBER plateNumber, e.license_Plate_Type licensePlateType, c.val PlateName, e.EPC, e.RFID_SERIAL rfidSerial,");
        sql.append(" e.station_Id stationId, e.station_Name stationName, e.stage_Id stageId, e.stage_Name stageName,");
        sql.append(" e.lane_In_Id laneInId, e.lane_In_Name laneInName, e.lane_Out_Id laneOutId, e.lane_Out_Name laneOutName,");
        sql.append(" e.router_Id routerId, e.router_Name routerName, e.contract_Id contractId, e.contract_No contractNo,");
        sql.append(" e.customer_Id customerId, e.customer_Name customerName, ");
        sql.append(" e.exception_Type exceptionType, e.vehicle_Type_Id vehicleTypeId, e.exception_Ticket_Type exceptionTicketType,");
        sql.append(" e.exception_Vehicle_Type exceptionVehicleType, e.WHITELIST_TYPE whiteListType, e.BLACKLIST_TYPE blackListType, e.status, e.description,");
        sql.append(" e.eff_Date effDate, e.exp_Date expDate, e.create_Date createDate, e.create_User createUser,");
        sql.append(" e.approved_User approvedUser, e.approved_Date approvedDate, e.approve_Commnents approveCommnents,");
        sql.append(" e.cancel_User cancelUser, e.cancel_Date cancelDate, e.cancel_Commnents cancelCommnents");
        sql.append(" FROM crm.EXCEPTION_LIST e ");
        sql.append(" INNER JOIN CATEGORIES.CATEGORIES c ON e.LICENSE_PLATE_TYPE = c.code and c.table_name = 'PLATE_TYPE' ");
        sql.append(" LEFT JOIN CATEGORIES.VEHICLE_GROUP c2 on to_char(e.VEHICLE_TYPE_ID) = c2.code ");
        if (dto.getModule().equalsIgnoreCase(Constants.MODULE_BOT) && !isAdmin) {
            sql.append(" LEFT JOIN CATEGORIES.bot_states stages ON e.stage_id = stages.stages_id ");
            sql.append(" LEFT JOIN CATEGORIES.bot_station station ON e.station_id = station.station_id ");
            sql.append(" LEFT JOIN CATEGORIES.bot bot ON (stages.bot_id = bot.id OR station.bot_id = bot.id) ");
        }
        sql.append(" WHERE 1=1");

        //region set where condition
        if (dto.getModule().equalsIgnoreCase(Constants.MODULE_BOT) && !isAdmin) {
            sql.append(" and bot.code = :partnerCode");
            hmapParams.put("partnerCode", partnerCode);
        }

        if (dto.getModule().equalsIgnoreCase(Constants.MODULE_MOT)) {
            sql.append(" and e.status = 4 ");
            if (!dto.isEffect()) {
                sql.append(" and trunc(e.eff_date) <= trunc(sysdate) ");
            }
        }

        if (!FnCommon.isNullOrEmpty(dto.getExceptionType())) {
            sql.append(" and e.exception_Type = :exceptionType ");
            hmapParams.put("exceptionType", dto.getExceptionType().trim());
        } else {
            if (dto.getExceptionGroup() == 1L) { // Xe ngoai le
                sql.append(" and e.exception_Type in (1, 2) ");
            }

            if (dto.getExceptionGroup() == 2L) { // xe uu tien & cam
                sql.append(" and e.exception_Type in (3, 4) ");
            }
        }

        if (!FnCommon.isNullOrEmpty(dto.getPlateNumber())) {
            sql.append(" and e.PLATE_NUMBER like :plateNumber ");
            hmapParams.put("plateNumber", "%" + dto.getPlateNumber().trim() + "%");
        }

        if (!FnCommon.isNullOrEmpty(dto.getPlateTypes())) {
            sql.append(" and e.license_Plate_Type in (:plateTypes) ");
            hmapParams.put("plateTypes", dto.getPlateTypes());
        }

        if (!FnCommon.isNullOrEmpty(dto.getVehicleTypeIds())) {
            sql.append(" and e.vehicle_Type_Id in (:vehicleTypeIds) ");
            hmapParams.put("vehicleTypeIds", dto.getVehicleTypeIds());
        }

        if (!FnCommon.isNullOrEmpty(dto.getRfidSerial())) {
            sql.append(" and e.RFID_SERIAL like :rfidSerial ");
            hmapParams.put("rfidSerial", "%" + dto.getRfidSerial().trim() + "%");
        }

        if (!FnCommon.isNullObject(dto.getExceptionVehicleType())) {
            sql.append(" and e.exception_Vehicle_Type = :exceptionVehicleType ");
            hmapParams.put("exceptionVehicleType", dto.getExceptionVehicleType());
        }

        if (!FnCommon.isNullObject(dto.getExceptionTicketType())) {
            sql.append(" and e.exception_Ticket_Type = :exceptionTicketType ");
            hmapParams.put("exceptionTicketType", dto.getExceptionTicketType());
        }

        if (!FnCommon.isNullObject(dto.getWhiteListType())) {
            sql.append(" and e.WHITELIST_TYPE = :whiteListType ");
            hmapParams.put("whiteListType", dto.getWhiteListType());
        }

        if (!FnCommon.isNullObject(dto.getBlackListType())) {
            sql.append(" and e.BLACKLIST_TYPE = :blackListType ");
            hmapParams.put("blackListType", dto.getBlackListType());
        }

        if (!FnCommon.isNullOrEmpty(dto.getStageIds()) && !FnCommon.isNullOrEmpty(dto.getStationIds())) {
            sql.append(" and (e.stage_Id in (:stageIds) OR e.station_Id in (:stationIds) )");
            hmapParams.put("stageIds", dto.getStageIds());
            hmapParams.put("stationIds", dto.getStationIds());
        } else {
            if (!FnCommon.isNullOrEmpty(dto.getStageIds())) {
                sql.append(" and e.stage_Id in (:stateIds) ");
                hmapParams.put("stateIds", dto.getStageIds());
            }

            if (!FnCommon.isNullOrEmpty(dto.getStationIds())) {
                sql.append(" and e.station_Id in (:stationIds) ");
                hmapParams.put("stationIds", dto.getStationIds());
            }
        }

        if (!FnCommon.isNullObject(dto.getStatus())) {
            sql.append(" and e.status = :status");
            hmapParams.put("status", dto.getStatus());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCreateDateFrom())) {
            sql.append(" and trunc(e.create_Date) >= to_date(:createDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("createDateFrom", dto.getCreateDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCreateDateTo())) {
            sql.append(" and trunc(e.create_Date) <= to_date(:createDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("createDateTo", dto.getCreateDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getEffDateFrom())) {
            sql.append(" and trunc(e.eff_date) >= to_date(:effDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("effDateFrom", dto.getEffDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getEffDateTo())) {
            sql.append(" and trunc(e.eff_date) <= to_date(:effDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("effDateTo", dto.getEffDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getApprovalDateFrom())) {
            sql.append(" and trunc(e.approved_Date) >= to_date(:approvalDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("approvalDateFrom", dto.getApprovalDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getApprovalDateTo())) {
            sql.append(" and trunc(e.approved_Date) <= to_date(:approvalDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("approvalDateTo", dto.getApprovalDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCancelDateFrom())) {
            sql.append(" and trunc(e.cancel_Date) >= to_date(:cancelDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("cancelDateFrom", dto.getCancelDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getCancelDateTo())) {
            sql.append(" and trunc(e.cancel_Date) <= to_date(:cancelDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("cancelDateTo", dto.getCancelDateTo());
        }

        if (!FnCommon.isNullOrEmpty(dto.getExpDateFrom())) {
            sql.append(" and trunc(e.EXP_DATE) >= to_date(:expDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("expDateFrom", dto.getExpDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(dto.getExpDateTo())) {
            sql.append(" and trunc(e.EXP_DATE) <= to_date(:expDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("expDateTo", dto.getExpDateTo());
        }

        if (dto.isEffect()) {
            sql.append(" and trunc(e.eff_date) <= trunc(sysdate) and (trunc(sysdate) <=  trunc(e.EXP_DATE) or e.EXP_DATE IS NULL) ");
        }

        if (dto.isExpire()) {
//            sql.append(" and trunc(sysdate) > trunc(e.EXP_DATE) ");
            sql.append("and (SYSDATE not between e.EFF_DATE and e.EXP_DATE) \n");
        }

        sql.append(" ORDER BY e.create_Date desc ");
        //endregion

        Integer start = 0;
        if (!FnCommon.isNullObject(dto.getStartrecord())) {
            start = dto.getStartrecord();
        }

        Integer pageSize = 10;
        if (!FnCommon.isNullObject(dto.getPagesize())) {
            pageSize = dto.getPagesize();
        }

        return getListDataAndCount(sql, hmapParams, start, pageSize, VehiclesExceptionDTO.class);
    }

    @Override
    public ResultSelectEntity getVehiclesException(VehiclesExceptionDTO params) {
        StringBuilder sql = new StringBuilder();
        sql.append("select  \n")
                .append("e.EXCEPTION_LIST_ID          as          exceptionListId,        -- Id \n")
                .append("e.EXCEPTION_TYPE             as          exceptionType,          -- Loai ngoai le \n")
                .append("e.PLATE_NUMBER               as          plateNumber,            -- Bien so xe \n")
                .append("e.LICENSE_PLATE_TYPE         as          licensePlateType,       -- Loai ben so \n")
                .append("e.RFID_SERIAL                as          rfidSerial,             -- So serial \n")
                .append("e.STATION_ID                 as          stationId,              -- Tram \n")
                .append("e.STAGE_ID                   as          stageId,                -- Doan \n")
                .append("e.VEHICLE_TYPE_ID            as          vehicleTypeId,          -- Loai phuong tien thu phi \n")
                .append("e.STATUS                     as          status,                 -- Trang thai \n")
                .append("p.PROMOTION_ID               as          promotionId,            -- ID Noi dung \n")
                .append("p.PROMOTION_NAME             as          content,                -- Noi dung \n")
                .append("e.EFF_DATE                   as          effDate,                -- Ngay hieu luc \n")
                .append("e.EXP_DATE                   as          expDate,                -- Ngay het hieu luc \n")
                .append("e.CREATE_DATE                as          createDate,             -- Ngay tao \n")
                .append("e.CREATE_USER                as          createUser              -- Nguoi tao \n")
                .append("from EXCEPTION_LIST e LEFT JOIN PROMOTION p ON e.PROMOTION_ID = p.PROMOTION_ID \n")
                .append("where 1=1 \n");

        HashMap<String, Object> hmapParams = new HashMap<>();

        //Tim kiem theo Loai ngoai le
        if (params.getExceptionTypes() != null && !params.getExceptionTypes().isEmpty()) {
            sql.append("and e.EXCEPTION_TYPE IN (:exceptionTypes) \n");
            hmapParams.put("exceptionTypes", params.getExceptionTypes());
        }

        if (params.getExceptionType() != null && !params.getExceptionType().isEmpty()) {
            sql.append("and e.EXCEPTION_TYPE = :exceptionType \n");
            hmapParams.put("exceptionType", params.getExceptionType());
        }

        //Tim kiem theo loai uu tien
        if (params.getWhiteListType() != null) {
            sql.append("and e.WHITELIST_TYPE = :whiteListType \n");
            hmapParams.put("whiteListType", params.getWhiteListType());
        }

        //Tim kiem theo loai cam
        if (params.getBlackListType() != null) {
            sql.append("and e.BLACKLIST_TYPE = :blackListType \n");
            hmapParams.put("blackListType", params.getBlackListType());
        }

        //Tim kiem theo Ten chuong trinh
        if (params.getPlateNumber() != null) {
            sql.append("and UPPER(e.PLATE_NUMBER) like :plateNumber \n");
            hmapParams.put("plateNumber", params.getPlateNumber().toUpperCase().trim() + "%");
        }

        // Tim kiem theo loai bien so
        if (params.getLicensePlateType() != null) {
            sql.append("and e.LICENSE_PLATE_TYPE = :licensePlateType \n");
            hmapParams.put("licensePlateType", params.getLicensePlateType());
        }

        // Tim kiem theo epc
        if (params.getEpc() != null) {
            sql.append("and e.epc = :epc \n");
            hmapParams.put("epc", params.getEpc());
        }

        // Tim kiem theo loai phuong tien thu phi
        if (params.getVehicleTypeId() != null) {
            sql.append("and e.VEHICLE_TYPE_ID = :vehicleTypeId \n");
            hmapParams.put("vehicleTypeId", params.getVehicleTypeId());
        }

        // Tim kiem theo doan
        if (params.getStageIds() != null && !params.getStageIds().isEmpty()) {
            sql.append("and e.STAGE_ID IN (:stageIds) \n");
            hmapParams.put("stageIds", params.getStageIds());
        }

        // Tim kiem theo tram
        if (params.getStationIds() != null && !params.getStationIds().isEmpty()) {
            sql.append("and e.STATION_ID IN (:stationIds) \n");
            hmapParams.put("stationIds", params.getStationIds());
        }

        // Tim kiem theo trang thai
        if (params.getStatus() != null) {
            sql.append("and e.STATUS = :status \n");
            hmapParams.put("status", params.getStatus());
        }

        // Tim kiem theo loai mien giam
        if (params.getPromotionId() != null) {
            sql.append("and e.PROMOTION_ID = :promotionId \n");
            hmapParams.put("promotionId", params.getPromotionId());
        }

        if (!FnCommon.isNullObject(params.getExceptionVehicleType())) {
            sql.append(" and e.exception_Vehicle_Type = :exceptionVehicleType ");
            hmapParams.put("exceptionVehicleType", params.getExceptionVehicleType());
        }

        if (!FnCommon.isNullOrEmpty(params.getCreateDateFrom())) {
            sql.append(" and trunc(e.CREATE_DATE) >= to_date(:createDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("createDateFrom", params.getCreateDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(params.getCreateDateTo())) {
            sql.append(" and trunc(e.CREATE_DATE) <= to_date(:createDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("createDateTo", params.getCreateDateTo());
        }

        if (!FnCommon.isNullOrEmpty(params.getEffDateFrom())) {
            sql.append(" and trunc(e.EFF_DATE) >= to_date(:effDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("effDateFrom", params.getEffDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(params.getEffDateTo())) {
            sql.append(" and trunc(e.EFF_DATE) <= to_date(:effDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("effDateTo", params.getEffDateTo());
        }

        if (!FnCommon.isNullOrEmpty(params.getApprovalDateFrom())) {
            sql.append(" and trunc(e.APPROVED_DATE) >= to_date(:approvalDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("approvalDateFrom", params.getApprovalDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(params.getApprovalDateTo())) {
            sql.append(" and trunc(e.APPROVED_DATE) <= to_date(:approvalDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("approvalDateTo", params.getApprovalDateTo());
        }

        if (!FnCommon.isNullOrEmpty(params.getCancelDateFrom())) {
            sql.append(" and trunc(e.CANCEL_DATE) >= to_date(:cancelDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("cancelDateFrom", params.getCancelDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(params.getCancelDateTo())) {
            sql.append(" and trunc(e.CANCEL_DATE) <= to_date(:cancelDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("cancelDateTo", params.getCancelDateTo());
        }

        if (!FnCommon.isNullOrEmpty(params.getExpDateFrom())) {
            sql.append(" and trunc(e.EXP_DATE) >= to_date(:expDateFrom, 'dd/mm/yyyy') ");
            hmapParams.put("expDateFrom", params.getExpDateFrom());
        }

        if (!FnCommon.isNullOrEmpty(params.getExpDateTo())) {
            sql.append(" and trunc(e.EXP_DATE) <= to_date(:expDateTo, 'dd/mm/yyyy') ");
            hmapParams.put("expDateTo", params.getExpDateTo());
        }

        //Lay data khi tich hieu luc
        if (params.isEffect() && !params.isExpire()) {
            sql.append("and (SYSDATE between e.EFF_DATE and e.EXP_DATE) \n");
        }

        //Lay data khi tich het hieu luc
        if (!params.isEffect() && params.isExpire()) {
            sql.append("and (SYSDATE not between e.EFF_DATE and e.EXP_DATE) \n");
        }

        sql.append("order by e.CREATE_DATE \n");

        Integer start = null;

        if (params.getStartrecord() != null) {
            start = params.getStartrecord();
        }

        Integer pageSize = null;

        if (params.getPagesize() != null) {
            pageSize = params.getPagesize();
        }

        return getListDataAndCount(sql, hmapParams, start, pageSize, VehiclesExceptionDTO.class);
    }

    /**
     * API lay thong tin chi tiet xe ngoai le
     *
     * @param exceptionListId: params client truyen len
     * @return VehiclesExceptionDTO
     */
    @Override
    public VehiclesExceptionDTO viewDetail(Long exceptionListId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select EXCEPTION_LIST_ID exceptionListId,");
        sql.append(" PLATE_NUMBER plateNumber, license_Plate_Type licensePlateType, EPC, RFID_SERIAL rfidSerial,");
        sql.append(" station_Id stationId, station_Name stationName, stage_Id stageId, stage_Name stageName,");
        sql.append(" lane_In_Id laneInId, lane_In_Name laneInName, lane_Out_Id laneOutId, lane_Out_Name laneOutName,");
        sql.append(" router_Id routerId, router_Name routerName, contract_Id contractId, contract_No contractNo,");
        sql.append(" customer_Id customerId, customer_Name customerName, ");
        sql.append(" exception_Type exceptionType, vehicle_Type_Id vehicleTypeId, exception_Ticket_Type exceptionTicketType,");
        sql.append(" exception_Vehicle_Type exceptionVehicleType, WHITELIST_TYPE whiteListType, BLACKLIST_TYPE blackListType,  status, description,");
        sql.append(" eff_Date effDate, exp_Date expDate, create_Date createDate, create_User createUser,");
        sql.append(" approved_User approvedUser, approved_Date approvedDate, approve_Commnents approveCommnents,");
        sql.append(" cancel_User cancelUser, cancel_Date cancelDate, cancel_Commnents cancelCommnents, PROMOTION_ID promotionId,");
        sql.append(" REGISTER_VEHICLE_TYPE registerVehicleType");
        sql.append(" FROM EXCEPTION_LIST where EXCEPTION_LIST_ID = :exceptionListId");

        HashMap<String, Object> hmapParams = new HashMap<>();
        hmapParams.put("exceptionListId", exceptionListId);

        VehiclesExceptionDTO data = (VehiclesExceptionDTO) getFirstData(sql, hmapParams, VehiclesExceptionDTO.class);
        if (!FnCommon.isNullObject(data)) {
            List<AttachmentFileEntity> attachmentFileEntities = attachmentFileRepositoryJPA.findByObjectId(exceptionListId);
            HashMap<String, Object> mapEntity = new HashMap<>();
            mapEntity.put("attachmentFiles", attachmentFileEntities);
            VehiclesExceptionDTO dto = (VehiclesExceptionDTO) FnCommon.convertObjectToObject(mapEntity, VehiclesExceptionDTO.class);
            data.setAttachmentFiles(dto.getAttachmentFiles());
        }
        return data;
    }

    /**
     * Update status
     *
     * @param : params client truyen len
     * @return
     */
    @Override
    public List<VehiclesExceptionDTO> postUpdateStatus(Long status, List<Long> listId) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE EXCEPTION_LIST SET STATUS = :status WHERE EXCEPTION_LIST_ID IN (:exceptionListIds)");
        HashMap<String, Object> hmapParams = new HashMap<>();
        hmapParams.put("status", status);
        hmapParams.put("exceptionListIds", listId);
        Boolean result = excuteSqlDatabase(sql, hmapParams);
        VehiclesExceptionDTO itemResult = new VehiclesExceptionDTO();
        itemResult.setResultSqlEx(result);
        List<VehiclesExceptionDTO> listData = new ArrayList<>();
        listData.add(itemResult);
        return listData;
    }

    /**
     * Update status
     *
     * @param exceptionList: list id
     * @return
     */
    @Override
    public List<VehiclesExceptionDTO> postUpdateStatusTransfer(List<Long> exceptionList) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE EXCEPTION_LIST SET IS_TRANSFERRED_BOO1 = 1 WHERE EXCEPTION_LIST_ID IN (:exceptionList)");
        HashMap<String, Object> hmapParams = new HashMap<>();
        hmapParams.put("exceptionList", exceptionList);
        Boolean result = excuteSqlDatabase(sql, hmapParams);
        VehiclesExceptionDTO itemResult = new VehiclesExceptionDTO();
        itemResult.setResultSqlEx(result);
        List<VehiclesExceptionDTO> listData = new ArrayList<>();
        listData.add(itemResult);
        return listData;
    }

    /**
     * Them phuong tien ngoai le/uu tien/cam
     *
     * @param exceptionListEntity params client
     * @return
     */
    @Transactional
    @Override
    public ExceptionListEntity saveExceptionList(ExceptionListEntity exceptionListEntity) {
        return exceptionListRepositoryJPA.save(exceptionListEntity);
    }

    /**
     * Them nhieu phuong tien ngoai le/uu tien/cam
     *
     * @param exceptionListEntities params client
     * @return
     */
    @Transactional
    @Override
    public List<ExceptionListEntity> saveAllExceptionList(List<ExceptionListEntity> exceptionListEntities) {
        return exceptionListRepositoryJPA.saveAll(exceptionListEntities);
    }

    /**
     * Them moi tai lieu dinh kem phuong tien ngoai le/uu tien/cam
     *
     * @param attachmentFileEntitys params client
     * @return
     */
    @Transactional
    @Override
    public List<AttachmentFileEntity> saveAttachmentFiles(List<AttachmentFileEntity> attachmentFileEntitys) {
        return attachmentFileRepositoryJPA.saveAll(attachmentFileEntitys);
    }

    /**
     * Xoa phuong tien ngoai le/uu tien/cam
     *
     * @param id params client
     * @return
     */
    @Transactional
    @Override
    public Boolean deleteExceptionList(Long id) {
        HashMap<String, Object> hmapParams = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM EXCEPTION_LIST WHERE EXCEPTION_LIST_ID = :exceptionListId AND STATUS = 1");
        hmapParams.put("exceptionListId", id);

        return excuteSqlDatabase(sql, hmapParams);
    }

    /**
     * Xoa tai lieu dinh kem phuong tien ngoai le/uu tien/cam
     *
     * @param id params client
     * @return
     */
    @Override
    public Boolean deleteAttachmentFiles(Long id) {
        HashMap<String, Object> hmapParams = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ATTACHMENT_FILE ");
        sql.append("WHERE OBJECT_ID = :objectId ");
        hmapParams.put("objectId", id);
        sql.append("AND ATTACHMENT_TYPE = 2 ");
        sql.append("AND NOT EXISTS(SELECT 1 FROM EXCEPTION_LIST EL WHERE EL.EXCEPTION_LIST_ID = :exceptionListId)");
        hmapParams.put("exceptionListId", id);

        return excuteSqlDatabase(sql, hmapParams);
    }

    /***
     * Lay thong tin danh sach phuong tien theo plateNumber
     * @param itemParamsEntity
     * @return
     */
    @Override
    public ResultSelectEntity getVehiclesByPlateNumber(VehiclesExceptionDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select \n");
        sql.append("v.plate_number                    As plateNumber,                             ---Bien so xe \n");
        sql.append("c.contract_id                     As contractId,                              ---ID hop dong \n");
        sql.append("c.contract_no                     As contractNo,                              ---So hop dong \n");
        sql.append("v.cust_id                         As custId,                                  ---ID khach hang \n");
        sql.append("v.VEHICLE_ID                      As vehicleId,                               ---Ma phuong tien \n");
        sql.append("v.owner                           As customerName,                            ---Ten chu pt \n");
        sql.append("v.vehicle_group_id                As vehicleTypeId,                           ---ID loai xe \n");
        sql.append("v.PLATE_TYPE_CODE                 As licensePlateType,                        ---Loai bien so \n");
        sql.append("v.epc                             As epc,                                     ---epc \n");
        sql.append("v.rfid_serial                     As rfidSerial                               ---thong tin the rfid \n");
        sql.append("from vehicle v join contract c on v.contract_Id = c.contract_id \n");
        sql.append("where v.active_status <> 5 --lay xe khac da chuyen nhuong \n");
        sql.append("and c.status= 2 --hop dong dang hoat dong \n");
        sql.append("and v.status = 1 -- Trang thai the dang hoat dong \n");

        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getPlateNumber())) {
            sql.append("and LOWER(PLATE_NUMBER) = LOWER(?) \n");
            arrParams.add(itemParamsEntity.getPlateNumber());
        }

        Integer start = 0;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pagesize;
        if (itemParamsEntity.getPagesize() != null) {
            pagesize = itemParamsEntity.getPagesize();
        } else {
            pagesize = 50;
        }

        ResultSelectEntity resultData = getListDataAndCount(sql, arrParams, start, pagesize, VehiclesExceptionDTO.class);

        return resultData;
    }

    /***
     * Thay doi documentName file dinh kem theo Id phuong tien ngoai le,uu tien, cam
     * @param itemParamsEntity
     * @return
     */
    @Override
    public Boolean UpdateDocumentNameByExceptionListId(VehiclesExceptionDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ATTACHMENT_FILE ");
        sql.append("SET DOCUMENT_NAME = :documentName ");
        sql.append("WHERE ATTACHMENT_TYPE = 2 ");
        sql.append("AND OBJECT_ID = :exceptionListId ");
        HashMap<String, Object> hmapParams = new HashMap<>();
        hmapParams.put("documentName", itemParamsEntity.getDocumentName());
        hmapParams.put("exceptionListId", itemParamsEntity.getExceptionListId());
        return excuteSqlDatabase(sql, hmapParams);
    }

    /***
     * Xoa file dinh kem theo danh sach id
     * @param ids
     * @return
     */
    @Override
    public Boolean deleteAttachmentFileByIds(List<Long> ids) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ATTACHMENT_FILE ");
        sql.append("WHERE ATTACHMENT_TYPE = 2 ");
        sql.append("AND ATTACHMENT_FILE_ID IN (:ids) ");
        HashMap<String, Object> hmapParams = new HashMap<>();
        hmapParams.put("ids", ids);
        return excuteSqlDatabase(sql, hmapParams);
    }

    /***
     * Dem danh sach File dinh kem theo id cua exception_list_id va khong thuoc danh sach AttachmentFileIds can xoa
     * @param ids
     * @param exceptionListId
     * @return
     */
    @Override
    public Integer getCountDatAttachmentFileAfterDelete(List<Long> ids, Long exceptionListId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ATTACHMENT_FILE ");
        sql.append("WHERE ATTACHMENT_TYPE = 2 ");
        sql.append("AND OBJECT_ID = :exceptionListId ");
        sql.append("AND ATTACHMENT_FILE_ID NOT IN (:ids) ");

        HashMap<String, Object> hmapParams = new HashMap<>();
        hmapParams.put("exceptionListId", exceptionListId);
        hmapParams.put("ids", ids);

        return getCountData(sql, hmapParams);
    }

    /***
     * Dem so phuong tien ngoai le, uu tien, cam
     * @param exceptionListEntity
     * @return
     */
    @Override
    public Integer getCountExceptionList(ExceptionListEntity exceptionListEntity) {
        HashMap<String, Object> hmapParams = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM EXCEPTION_LIST ");
        sql.append("WHERE 1 = 1 ");

        if (!FnCommon.isNullObject(exceptionListEntity.getExceptionListId())) {
            sql.append("AND EXCEPTION_LIST_ID != :exceptionListId ");
            hmapParams.put("exceptionListId", exceptionListEntity.getExceptionListId());
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getEpc())) {
            sql.append("AND EPC = :epc ");
            hmapParams.put("epc", exceptionListEntity.getEpc());
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getRfidSerial())) {
            sql.append("AND RFID_SERIAL = :rfidSerial ");
            hmapParams.put("rfidSerial", exceptionListEntity.getRfidSerial());
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getStageId())) {
            sql.append("AND STAGE_ID = :stageId ");
            hmapParams.put("stageId", exceptionListEntity.getStageId());
        } else {
            sql.append("and STAGE_ID IS NULL ");
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getStationId())) {
            sql.append("AND STATION_ID = :stationId ");
            hmapParams.put("stationId", exceptionListEntity.getStationId());
        } else {
            sql.append("and STATION_ID IS NULL ");
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getLaneInId())) {
            sql.append("AND LANE_IN_ID = :laneInId ");
            hmapParams.put("laneInId", exceptionListEntity.getLaneInId());
        } else {
            sql.append("and LANE_IN_ID IS NULL ");
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getLaneOutId())) {
            sql.append("AND LANE_OUT_ID = :laneOutId ");
            hmapParams.put("laneOutId", exceptionListEntity.getLaneOutId());
        } else {
            sql.append("and LANE_OUT_ID IS NULL ");
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getEffDate())) {
            sql.append("AND EFF_DATE = :effDate ");
            hmapParams.put("effDate", exceptionListEntity.getEffDate());
        }

        if (!FnCommon.isNullObject(exceptionListEntity.getExpDate())) {
            sql.append("AND EXP_DATE = :expDate ");
            hmapParams.put("expDate", exceptionListEntity.getExpDate());
        }

        return getCountData(sql, hmapParams);
    }

    /***
     * lay ma OCS
     * @param itemEntity
     * @return
     */
    public ResultSelectEntity getOCSCode(ServicePlanEntity itemEntity, String exceptionType) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        //create sql query
        sql.append("select ");
        sql.append("OCS_CODE  as ocsCode                -- Ma ocs \n");
        sql.append("from SERVICE_PLAN   \n ");
        sql.append("where STATUS = 2                    -- Da duyet \n");

        //ExceptionType
        if (exceptionType != null) {
            if (exceptionType.equals(Constants.EXCEPTION_PRIORITY)) {
                sql.append("and SERVICE_PLAN_TYPE_ID = ").append(Constants.SERVICE_PLAN_TYPE_PRIORITY).append("\n");
            }
            if (exceptionType.equals(Constants.EXCEPTION_BAN)) {
                sql.append("and SERVICE_PLAN_TYPE_ID = ").append(Constants.SERVICE_PLAN_TYPE_BAN).append("\n");
            }
        }

        //Loai xe
        if (itemEntity.getVehicleGroupId() != null) {
            sql.append("and VEHICLE_GROUP_ID = :vehicleGroupId \n");
            hmapParams.put("vehicleGroupId", itemEntity.getVehicleGroupId());
        }

        //Doan
        if (itemEntity.getStageId() != null) {
            sql.append("and STAGE_ID = :stageId \n ");
            hmapParams.put("stageId", itemEntity.getStageId());
        }

        //Tram
        if (itemEntity.getStationId() != null) {
            sql.append("and STATION_ID = :stationId \n ");
            hmapParams.put("stationId", itemEntity.getStationId());
        }

        //Lan vao
        if (itemEntity.getLaneIn() != null) {
            sql.append("and LANE_IN = :laneIn \n ");
            hmapParams.put("laneIn", itemEntity.getLaneIn());
        } else {
            sql.append("and LANE_IN IS NULL \n ");
        }

        //Lan ra
        if (itemEntity.getLaneOut() != null) {
            sql.append("and LANE_OUT = :laneOut \n ");
            hmapParams.put("laneOut", itemEntity.getLaneOut());
        } else {
            sql.append("and LANE_OUT IS NULL \n ");
        }

        sql.append("AND SYSDATE >= START_DATE AND ( SYSDATE <= END_DATE OR END_DATE IS NULL) \n ");

        Integer start = null;
        Integer pageSize = null;

        return getListDataAndCount(sql, hmapParams, start, pageSize, ServicePlanEntity.class);
    }

    @Override
    public AttachmentFileDTO downloadAttachmentFile(Long attachmentFileId) {
        AttachmentFileEntity attachmentFileEntity = attachmentFileRepositoryJPA.findByAttachmentFileId(attachmentFileId);
        AttachmentFileDTO dto = AttachmentFileDTO.builder().documentPath(attachmentFileEntity.getDocumentPath()).build();
        return dto;
    }

    @Override
    public List<AttachmentFileEntity> getAttachmentFiles(Long objectId, Long attachmentType) {
        return attachmentFileRepositoryJPA.findByObjectIdAndAndAttachmentType(objectId, attachmentType);
    }

}
