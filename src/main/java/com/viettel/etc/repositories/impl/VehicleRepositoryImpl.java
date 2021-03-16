package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Autogen class Repository Impl: phuong tien
 *
 * @author ToolGen
 * @date Wed Jun 24 14:38:40 ICT 2020
 */
@Repository
public class VehicleRepositoryImpl extends CommonDataBaseRepository implements VehicleRepository {
    /**
     * Xuat danh sach xe dan the
     *
     * @param contractId: params client truyen len
     * @return
     */
    @Override
    public List<ExportVehiclesAssignedDTO> exportVehiclesAssigned(Long contractId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select \n");
        sql.append("    ve.PLATE_NUMBER         as plateNumber,        -- Bien so cua phuong tien\n");
        sql.append("    ve.CUST_ID              as custId,             -- Ma khach hang\n");
        sql.append("    ve.VEHICLE_TYPE_ID      as vehicleTypeId,      -- Ma nhom phuon tien\n");
        sql.append("    ve.NET_WEIGHT           as netWeight,          -- Khoi luong ban than\n");
        sql.append("    ve.CARGO_WEIGHT         as cargoWeight,        -- Khoi luon hang hoa\n");
        sql.append("    ve.VEHICLE_GROUP_ID     as vehicleGroupId,     -- Ma nhom phuong tien tinh phi\n");
        sql.append("    ve.GROSS_WEIGHT         as crossWeight,        -- Khoi luong toan bo\n");
        sql.append("    ve.PULLING_WEIGHT       as pullingWeight,      -- Khoi luong keo theo\n");
        sql.append("    ve.SEAT_NUMBER          as seatNumber,         -- So ghe ngoi\n");
        sql.append("    ve.ENGINE_NUMBER        as engineNumber,       -- So may phuong tien\n");
        sql.append("    ve.CHASSIC_NUMBER       as chassicNumber,      -- So khung phuong tien\n");
        sql.append("    ve.PLATE_TYPE           as plateType,          -- Mau bien so\n");
        sql.append("    ve.VEHICLE_COLOUR_ID    as vehicleColourId,    -- Mau xe\n");
        sql.append("    ve.VEHICLE_MARK_ID      as vehicleMarkId,      -- Nhan hieu\n");
        sql.append("    ve.VEHICLE_BRAND_ID     as vehicleBrandId,     -- Dong xe\n");
        sql.append("    ve.CREATE_USER          as createUser,         -- Nhan vien dan the\n");
        sql.append("    ve.RFID_SERIAL          as rfidSerial,         -- So serial\n");
        sql.append("    ve.OWNER                as owner,              -- Ten chu phuong tien\n");
        sql.append("    ve.ACTIVE_STATUS        as activeStatus        -- Trang thai hoat dong the\n");
        /*** Mô tả trường STATUS
         * 0. Không hoạt động
         * 1. Hoạt động
         * 2. Import phương tiện từ file
         * 3. Khớp với kết quả đăng kiểm
         * 4. Không khớp kết quả đăng kiểm"
         */
        sql.append("    from VEHICLE ve where ve.STATUS = 3\n");
        if (contractId != null) {
            sql.append(" and ve.CONTRACT_ID = ?\n");
            arrParams.add(contractId);
        }
        return (List<ExportVehiclesAssignedDTO>) getListData(sql, arrParams, 0, null, ExportVehiclesAssignedDTO.class);
    }

    /**
     * Kiem tra thong tin dang kiem
     *
     * @param listPlateNumber
     * @return
     */
    @Override
    public ResultSelectEntity checkSignVehicle(List<String> listPlateNumber) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" SELECT\n");
        sql.append("    ve.PLATE_NUMBER as plateNumber\n");
        sql.append(" FROM\n");
        sql.append("     (select * from VEHICLE_REGIS_DB where 1=1 ");
        addParramToQuery(sql, listPlateNumber, arrParams);
        sql.append("     ) re,\n");
        sql.append("     (select * from VEHICLE where 1=1 ");
        addParramToQuery(sql, listPlateNumber, arrParams);
        sql.append("        ) ve\n");
        sql.append(" where re.PLATE_NUMBER = ve.PLATE_NUMBER\n");
        sql.append("     and re.VEHICLE_TYPE_ID = ve.VEHICLE_TYPE_ID\n");
        sql.append("     and re.SEAT_NUMBER = ve.SEAT_NUMBER\n");
        sql.append("     and re.NET_WEIGHT = ve.NET_WEIGHT\n");
        sql.append("     and re.GROSS_WEIGHT = ve.GROSS_WEIGHT\n");
        sql.append("     and re.CARGO_WEIGHT = ve.CARGO_WEIGHT\n");
        sql.append("     AND re.PULLING_WEIGHT = ve.PULLING_WEIGHT");

        Integer start = 0;
        return getListDataAndCount(sql, arrParams, start, null, AddVehicleRequestDTO.class);
    }

    /**
     * Truyen danh sach tham so
     *
     * @param sql
     * @param listPlateNumber
     * @param arrParams
     */
    private void addParramToQuery(StringBuilder sql, List<String> listPlateNumber, List<Object> arrParams) {
        sql.append("   and (");
        for (int i = 0; i < listPlateNumber.size(); i++) {
            if (i == 0) {
                sql.append("   PlATE_NUMBER = ? ");
            } else {
                sql.append("   or PlATE_NUMBER = ? ");
            }
            arrParams.add(listPlateNumber.get(i));
        }
        sql.append("   )");
    }

    /**
     * Danh sach file dinh kem hop dong
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findProfileByContract(VehicleProfilePaginationDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select ");
        sql.append("    vp.vehicle_profile_id      as vehicleProfileId      -- Ma hop dong\n");
        sql.append("   ,dt.DOCUMENT_TYPE_ID        as documentTypeId        -- Loai giay to\n");
        sql.append("   ,dt.NAME                    as documentTypeName      -- Ten loai giay to\n");
        sql.append("   ,dt.TYPE                    as type                  -- Loai chung tu\n");
        sql.append("   ,vp.createDate              as createDate            -- Ngay tiep nhan\n");
        sql.append("   ,vp.createDate              as scanDay               -- Ngay scan\n");
        sql.append("   ,vp.FILE_NAME               as fileName              -- Ten file\n");
        sql.append("   ,vp.STATUS                  as status                -- Trang thai\n");
        sql.append("   ,vp.DESCRIPTION             as description           -- ghi chu\n");
        sql.append(" from\n");
        sql.append(" (select vehicle_profile_id, v.document_type_id, max(v.create_date) as createDate, v.FILE_NAME, v.STATUS, v.DESCRIPTION \n");
        sql.append(" from vehicle_profile v where 1=1\n");
        if (requestModel.getVehicleId() != null) {
            sql.append(" and v.VEHICLE_ID = ?");
            arrParams.add(requestModel.getVehicleId());
        }
        sql.append(" GROUP BY v.document_type_id, v.vehicle_profile_id, v.FILE_NAME, v.STATUS, v.DESCRIPTION ");
        sql.append(" order by v.document_type_id desc) vp\n");
        sql.append(" RIGHT JOIN DOCUMENT_TYPE dt ON vp.DOCUMENT_TYPE_ID = dt.DOCUMENT_TYPE_ID \n");
        sql.append(" where dt.DOCUMENT_TYPE_ID in\n");
        sql.append(" (select DOCUMENT_TYPE_ID from ACT_ID_TYPE_MAPPING where 1 = 1\n");
        if (requestModel.getActTypeId() != null) {
            sql.append(" and ACT_TYPE_ID = ?)\n");
            arrParams.add(requestModel.getActTypeId());
        }
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, VehicleProfilePaginationDTO.class);
    }

    /**
     * Download file dinh kem hop dong
     *
     * @param profileId
     * @return
     */
    @Override
    public List<VehicleProfilePaginationDTO> downloadProfileByContract(Long profileId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append("    VEHICLE_PROFILE_ID      as vehicleProfileId        -- Loai giay to\n");
        sql.append("    ,FILE_NAME              as fileName                -- Ten file dinh kem\n");
        sql.append("    ,FILE_PATH              as filePath                -- Loai giay to\n");
        sql.append(" from VEHICLE_PROFILE where VEHICLE_PROFILE_ID=?\n");
        List<Object> arrParams = Arrays.asList(profileId);
        return (List<VehicleProfilePaginationDTO>) getListData(sql, arrParams, 0, 1, VehicleProfilePaginationDTO.class);
    }

    /**
     * Tra cuu thong tin phuong tien
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findVehicleByContract(VehicleByContractDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select\n");
        sql.append("    ve.PLATE_NUMBER as plateNumber,               -- Bien so cua phuong tien\n");
        sql.append("    ve.OWNER              as owner,               -- Chu phuong tien\n");
        sql.append("    ve.VEHICLE_TYPE_ID    as vehicleTypeId,       -- Ma loai phuong tien\n");
        sql.append("    ve.SEAT_NUMBER        as seatNumber,          -- So ghe ngoi\n");
        sql.append("    ve.STATUS             as status,              -- Ket qua kiem tra (3 Khop, 4 Khong khop)\n");
        sql.append("    ve.RFID_SERIAL        as rfidSerial,          -- So serial\n");
        sql.append("    ve.VEHICLE_GROUP_ID   as vehicleGroupId,      -- Ma nhom phuong tien tinh phi\n");
        sql.append("    ve.ACTIVE_STATUS      as activeStatus,        -- Trang thai hoat dong the\n");
        sql.append("    ve.VEHICLE_ID         as vehicleId,           -- Ma phuong tien\n");
        sql.append("    ve.NET_WEIGHT         as netWeight,           -- Khoi luong ban than\n");
        sql.append("    ve.GROSS_WEIGHT       as grossWeight,         -- Khoi luong toan bo\n");
        sql.append("    ve.CARGO_WEIGHT       as cargoWeight,         -- Khoi luong hang hoa\n");
        sql.append("    ve.PULLING_WEIGHT     as pullingWeight,       -- Khoi luong keo theo\n");
        sql.append("    ve.PLATE_TYPE         as plateType,           -- Loai bien so\n");
        sql.append("    ve.PLATE_TYPE_CODE    as plateTypeCode,       -- Loai bien so\n");
        sql.append("    ve.EPC         as epc            -- Etag cua xe\n");
        sql.append("from VEHICLE ve where 1=1\n");
        if (requestModel.getContractId() != null) {
            sql.append(" and CONTRACT_ID=?");
            arrParams.add(requestModel.getContractId());
        }

        /***
         * Mô tả trường ACTIVE_STATUS
         * 0. Chưa kích hoạt
         * 1. Hoạt động
         * 2. Hủy
         * 3. Đóng
         * 4. Mở
         * 5. Đã chuyển nhượng"
         */

        /***
         * Mô tả trường STATUS
         * 0. Không hoạt động
         * 1. Hoạt động
         * 2. Import phương tiện từ file
         * 3. Khớp với kết quả đăng kiểm
         * 4. Không khớp kết quả đăng kiểm"
         */
        switch (requestModel.getSearchType()) {
            case Constants.VEHICLE_RFID:
                sql.append(" and ACTIVE_STATUS in (0,1,3,4) \n");
                sql.append(" and STATUS = 1 and RFID_SERIAL is not null \n");
                break;
            case Constants.VEHICLE_NOT_RFID:
                if (requestModel.getVehicleImportType() == null) {
                    sql.append(" and RFID_SERIAL is null \n");
                }
                if (requestModel.getVehicleImportType() != null) {
                    sql.append(" and STATUS in (2,3,4) \n");
                }
                break;
            case Constants.VEHICLE_ACTIVE:
                sql.append(" and ACTIVE_STATUS in (1,4) \n");
                sql.append(" and STATUS = 1 and RFID_SERIAL is not null \n");
                if (!FnCommon.isNullOrEmpty(requestModel.getPlateNumber())) {
                    sql.append(" and LOWER(PLATE_NUMBER) like '%' || LOWER(?) ||'%'");
                    arrParams.add(requestModel.getPlateNumber().trim());
                }
                break;
        }

        // Ngay dang ky
        if (!FnCommon.isNullOrEmpty(requestModel.getStartDate())) {
            sql.append(" and ve.CREATE_DATE >= to_date(?, 'dd/mm/yyyy')");
            arrParams.add(requestModel.getStartDate());
        }
        // Ngay het han
        if (!FnCommon.isNullOrEmpty(requestModel.getEndDate())) {
            sql.append(" and ve.CREATE_DATE <= to_date(?, 'dd/mm/yyyy hh24:mi:ss')");
            arrParams.add(requestModel.getEndDate() + Constants.TIME_ONE_DAY);
        }
        sql.append(" order by VEHICLE_GROUP_ID asc");
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, VehicleByContractDTO.class);
    }

    /**
     * Tra cuu thong tin phuong tien
     *
     * @param vehicleId
     * @return
     */
    @Override
    public ResultSelectEntity findVehicleById(Long vehicleId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select \n");
        sql.append("    ve.PLATE_NUMBER                 as plateNumber,                 -- Bien so cua phuong tien\n");
        sql.append("    ve.CUST_ID                      as custId,                      -- Chu phuong tien\n");
        sql.append("    ve.VEHICLE_TYPE_ID              as vehicleTypeId,               -- Ma loai phuong tien\n");
        sql.append("    ve.VEHICLE_GROUP_ID             as vehicleGroupId,              -- Ma nhom phuong tien tinh phi\n");
        sql.append("    ve.OWNER                        as owner,                       -- Chu phuong tien\n");
        sql.append("    ve.NET_WEIGHT                   as netWeight,                   -- Khoi luong ban than\n");
        sql.append("    ve.CARGO_WEIGHT                 as cargoWeight,                 -- Khoi luon hang hoa\n");
        sql.append("    ve.GROSS_WEIGHT                 as grossWeight,                 -- Khoi luong toan bo\n");
        sql.append("    ve.PULLING_WEIGHT               as pullingWeight,               -- Khoi luong keo theo\n");
        sql.append("    ve.SEAT_NUMBER                  as seatNumber,                  -- So ghe ngoi\n");
        sql.append("    ve.ENGINE_NUMBER                as engineNumber,                -- So may phuong tien\n");
        sql.append("    ve.CHASSIC_NUMBER               as chassicNumber,               -- So khung phuong tien\n");
        sql.append("    ve.VEHICLE_COLOUR_ID            as vehicleColourId,             -- Mau son phuong tien\n");
        sql.append("    ve.VEHICLE_MARK_ID              as vehicleMarkId,               -- Nhan hieu phuong tien\n");
        sql.append("    ve.VEHICLE_BRAND_ID             as vehicleBrandId,              -- Dong xe phuong tien\n");
        sql.append("    ve.PLATE_TYPE                   as plateType,                   -- Mau bien so\n");
        sql.append("    ve.CREATE_USER                  as createUser,                  -- Nhan vien dan the\n");
        sql.append("    ve.RFID_SERIAL                  as rfidSerial,                  -- So serial\n");
        sql.append("    ve.EPC                          as EPC,                         -- So EPC\n");
        sql.append("    ve.CREATE_DATE                  as createDate,                  -- Ngay dau noi\n");
        sql.append("    ve.PROMOTION_CODE               as promotionCode,               -- Ma gioi thieu\n");
        sql.append("    ve.ACTIVE_STATUS                as activeStatus                 -- Trang thai hoat dong the\n");
        sql.append("    from VEHICLE ve where 1=1\n");
        if (vehicleId != null) {
            sql.append(" and ve.VEHICLE_ID = ?");
            arrParams.add(vehicleId);
        }
        return getListDataAndCount(sql, arrParams, 0, 1, VehicleByContractDTO.class);
    }

    /**
     * Lay thong tin dang kiem
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getVehicleRegistry(VehicleDTO itemParamsEntity, String plateNumber) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select VEHICLE_TYPE_ID          as vehicleTypeId,                           ---Loai phuong tien \n");
        sql.append("OWNER                           as owner,                                   ---Ten chu phuong tien \n");
        sql.append("CARGO_WEIGHT                    as cargoWeight,                             ---Khoi luong hang hoa \n");
        sql.append("NET_WEIGHT                      as netWeight,                               ---Khoi luong ban than \n");
        sql.append("GROSS_WEIGHT                    as grossWeight,                             ---Khoi luong toan bo \n");
        sql.append("SEAT_NUMBER                     as seatNumber,                              ---So cho \n");
        sql.append("PULLING_WEIGHT                  as pullingWeight,                           ---Khoi luong keo theo \n");
        sql.append("CHASSIC_NUMBER                  as chassicNumber,                           ---So khung \n");
        sql.append("ENGINE_NUMBER                   as engineNumber,                            ---So may \n");
        sql.append("VEHICLE_MARK_ID                 as vehicleMarkId,                           ---Nhan hieu \n");
        sql.append("VEHICLE_COLOUR_ID               as vehicleColourId,                         ---Mau son \n");
        sql.append("PLATE_TYPE                      as plateType,                               ---Loai bien \n");
        sql.append("VEHICLE_BRAND_ID                as vehicleBrandId                           ---Dong xe \n");
        sql.append("from VEHICLE_REGIS_DB \n");
        sql.append("where UPPER(PLATE_NUMBER) = :plateNumber \n");
        hmapParams.put("plateNumber", plateNumber.toUpperCase());

        Integer start = null;
        if (itemParamsEntity != null && itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if (itemParamsEntity != null && itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, VehicleDTO.class);
    }

    /**
     * Tim kiem ho so dinh kem theo vehicleId
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findProfileByVehicleId(VehicleProfileDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("Select \n");
        sql.append(" vp.VEHICLE_PROFILE_ID                           as vehicleProfileId                 -- ID ho so dinh kem \n");
        sql.append(" ,vp.FILE_NAME                                   as fileName                         -- Ten file \n");
        sql.append(" ,dt.NAME                                        as documentTypeName                 -- Loai giay to \n");
        sql.append(" ,vp.CREATE_DATE                                 as createDate                       -- Ngay tiep nhan \n");
        sql.append(" ,vp.CREATE_DATE                                 as scanDay                          -- Ngay scan \n");
        sql.append(" ,vp.STATUS                                      as status                           -- Trang thai giay to \n");
        sql.append(" from  VEHICLE_PROFILE vp, DOCUMENT_TYPE dt where vp.DOCUMENT_TYPE_ID = dt.DOCUMENT_TYPE_ID \n");
        if (requestModel.getVehicleId() != null) {
            sql.append(" and vp.VEHICLE_ID = ? \n");
            arrParams.add(requestModel.getVehicleId());
        }

        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, VehicleProfileDTO.class);
    }

    /**
     * Tim kiem ho so dinh kem theo vehicleId
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findVehicleSearchTree(VehicleDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("Select \n")
        .append(" VEHICLE_ID                                      as vehicleId,                        -- ID ho so dinh kem \n")
        .append(" CUST_ID                                         as custId,                           -- Ten file \n")
        .append(" CONTRACT_ID                                     as contractId,                       -- Loai giay to \n")
        .append(" PLATE_NUMBER                                    as plateNumber                       -- Ngay tiep nhan \n")
        .append(" from  VEHICLE WHERE ACTIVE_STATUS <> '5' \n");
        if (!FnCommon.isNullOrEmpty(requestModel.getPlateNumber())) {
            sql.append(" and LOWER(PLATE_NUMBER) like LOWER(?) ||'%'");
            arrParams.add(requestModel.getPlateNumber().trim());
        }
        if (requestModel.getRfidSerial() != null) {
            sql.append(" and RFID_SERIAL like ? ||'%'");
            arrParams.add(requestModel.getRfidSerial());
        }
        if (!FnCommon.isNullOrEmpty(requestModel.getActiveStatus())) {
            sql.append(" and ACTIVE_STATUS = ?");
            arrParams.add(requestModel.getActiveStatus());
        }
        return getListDataAndCount(sql, arrParams, 0, null, VehicleDTO.class);
    }

    @Override
    public ResultSelectEntity getVehicle(VehicleDTO params) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();
        sql.append("select \n")
                .append("cust_id                        as custId,                                  ---ID khach hang \n")
                .append("contract_id                    as contractId,                              ---ID hop dong \n")
                .append("owner                          as owner,                                   ---Ten chu pt \n")
                .append("vehicle_id                     as vehicleId,                               ---ID phương tiện \n")
                .append("vehicle_type_id                as vehicleTypeId,                           ---ID loại xe \n")
                .append("plate_number                   as plateNumber,                             ---Biển số xe \n")
                .append("plate_type_code                as plateTypeCode,                           ---Loại biển số \n")
                .append("SEAT_NUMBER                    as seatNumber,                              ---So cho \n")
                .append("NET_WEIGHT                     as netWeight,                               ---netWeight \n")
                .append("GROSS_WEIGHT                   as grossWeight,                             ---grossWeight \n")
                .append("CARGO_WEIGHT                   as cargoWeight,                             ---cargoWeight \n")
                .append("PULLING_WEIGHT                 as pullingWeight,                           ---pullingWeight \n")
                .append("VEHICLE_GROUP_ID               as vehicleGroupId,                          ---vehicleGroupId \n")
                .append("VEHICLE_MARK_ID                as vehicleMarkId,                           ---vehicleMarkId \n")
                .append("VEHICLE_COLOUR_ID              as vehicleColourId,                         ---vehicleColourId \n")
                .append("epc                            as epc                                       \n")
                .append("from VEHICLE \n");
        /**
         * 0. Chưa kích hoạt
         * 1. Hoạt động
         * 2. Hủy
         * 3. Đóng
         * 4. Mở
         * 5. Đã chuyển nhượng"
         */
        sql.append("where status = 1 and active_status in (1,3,4) \n");

        if (!FnCommon.isNullOrBlank(params.getPlateNumber())) {
            sql.append("and UPPER(PLATE_NUMBER) = :plateNumber \n");
            hmapParams.put("plateNumber", params.getPlateNumber().toUpperCase());
        }

        if (!FnCommon.isNullOrBlank(params.getPlateTypeCode())) {
            sql.append("and plate_type_code = :plateTypeCode \n");
            hmapParams.put("plateTypeCode", params.getPlateTypeCode());
        }

        if (!FnCommon.isNullOrBlank(params.getEpc())) {
            sql.append("and epc = :epc \n");
            hmapParams.put("epc", params.getEpc());
        }

        return getListDataAndCount(sql, hmapParams, 0, null, VehicleDTO.class);
    }


    /***
     * Lay thong tin danh sach phuong tien theo plateNumber
     * @param itemParamsEntity
     * @return
     */
    @Override
    public ResultSelectEntity getVehiclesByPlateNumber(VehicleSearchDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();

        sql.append("select \n");
        sql.append("cust_id                         as custId,                                  ---ID khach hang \n");
        sql.append("contract_id                     as contractId,                              ---ID hop dong \n");
        sql.append("owner                           as owner,                                   ---Ten chu pt \n");
        sql.append("vehicle_id                      as vehicleId,                               ---ID phương tiện \n");
        sql.append("vehicle_type_id                 as vehicleTypeId,                           ---ID loại xe \n");
        sql.append("plate_number                    as plateNumber,                             ---Biển số xe \n");
        sql.append("plate_type                      as plateType,                               ---Loại biển số \n");
        sql.append("epc                             as epc                                       \n");
        sql.append("from VEHICLE \n");
        /**
         * Lấy ra danh sách phương tiện có trạng thái active_status khac  (0,2,5) va rfid not null
         * 0. Chưa kích hoạt
         * 1. Hoạt động
         * 2. Hủy
         * 3. Đóng
         * 4. Mở
         * 5. Đã chuyển nhượng"
         */
        sql.append("where status =1 and active_status in (1,3,4) and rfid_serial is not null \n");

        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getPlateNumber())) {
            sql.append("and LOWER(PLATE_NUMBER) like '%' || LOWER(?) ||'%' \n");
            arrParams.add(itemParamsEntity.getPlateNumber());
        }

        if (itemParamsEntity.getPlateType() != null) {
            sql.append("and PLATE_TYPE = ? \n");
            arrParams.add(itemParamsEntity.getPlateType());
        }

        sql.append("order by plateNumber \n");

        Integer start = 0;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize;
        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        } else {
            pageSize = 50;
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, VehicleDTO.class);
    }

    /***
     * Lay thong tin danh sach phuong tien theo plateNumber cho cong bot
     * @param itemParamsEntity
     * @return
     */
    @Override
    public ResultSelectEntity getVehiclesByPlateNumberForPortalBot(VehicleSearchDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();

        sql.append("select \n");
        sql.append("v.cust_id                         As custId,                                  ---ID khach hang \n");
        sql.append("c.contract_id                     As contractId,                              ---ID hop dong \n");
        sql.append("c.contract_no                     As contractNo,                              ---So hop dong \n");
        sql.append("v.owner                           As owner,                                   ---Ten chu pt \n");
        sql.append("v.vehicle_id                      As vehicleId,                               ---ID phuong tien \n");
        sql.append("v.vehicle_group_id                As vehicleGroupId,                          ---ID loai xe \n");
        sql.append("v.vehicle_type_id                 As vehicleTypeId,                           ---ID loai xe dang kiem");
        sql.append("v.plate_number                    As plateNumber,                             ---Bien so xe \n");
        sql.append("v.plate_type                      As plateType,                               ---Loai bien so \n");
        sql.append("v.epc                             As epc,                                     ---epc \n");
        sql.append("v.plate_type_code                 As plateTypeCode,                           ---code_loai_bien_so \n");
        sql.append("v.rfid_serial                     As rfidSerial                               ---thong tin the rfid \n");
        sql.append("from vehicle v join contract c on v.contract_id = c.contract_id \n");
        sql.append("where v.active_status <> 5      --Lay xe khac da chuyen nhuong \n");
        sql.append("and c.status = 2                --Hop dong dang hoat dong \n");
        sql.append("and v.status = 1                --Trang thai the dang hoat dong \n");

        if (!FnCommon.isNullOrEmpty(itemParamsEntity.getPlateNumber())) {
            sql.append("and LOWER(PLATE_NUMBER) = LOWER(?) \n");
            arrParams.add(itemParamsEntity.getPlateNumber());
        }

        if (itemParamsEntity.getPlateType() != null) {
            sql.append("and PLATE_TYPE = ? \n");
            arrParams.add(itemParamsEntity.getPlateType());
        }

        Integer start = 0;
        if (itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }

        Integer pageSize;
        if (itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        } else {
            pageSize = 50;
        }

        return getListDataAndCount(sql, arrParams, start, pageSize, VehicleDTO.class);
    }

    /**
     * Tra cuu thong tin danh sach phuong tien da gan the
     *
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findVehicleAssignedRFID(VehicleByContractDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select\n");
        sql.append("    ve.PLATE_NUMBER as plateNumber,               -- Bien so cua phuong tien\n");
        sql.append("    ve.OWNER              as owner,               -- Chu phuong tien\n");
        sql.append("    ve.VEHICLE_TYPE_ID    as vehicleTypeId,       -- Ma loai phuong tien\n");
        sql.append("    ve.SEAT_NUMBER        as seatNumber,          -- So ghe ngoi\n");
        sql.append("    ve.STATUS             as status,              -- Ket qua kiem tra (3 Khop, 4 Khong khop)\n");
        sql.append("    ve.RFID_SERIAL        as rfidSerial,          -- So serial\n");
        sql.append("    ve.VEHICLE_GROUP_ID   as vehicleGroupId,      -- Ma nhom phuong tien tinh phi\n");
        sql.append("    ve.ACTIVE_STATUS      as activeStatus,        -- Trang thai hoat dong the\n");
        sql.append("    ve.VEHICLE_ID         as vehicleId,           -- Ma phuong tien\n");
        sql.append("    ve.NET_WEIGHT         as netWeight,           -- Khoi luong ban than\n");
        sql.append("    ve.GROSS_WEIGHT       as grossWeight,         -- Khoi luong toan bo\n");
        sql.append("    ve.CARGO_WEIGHT       as cargoWeight,         -- Khoi luong hang hoa\n");
        sql.append("    ve.PULLING_WEIGHT     as pullingWeight        -- Khoi luong keo theo\n");
        sql.append("from VEHICLE ve where 1=1\n");
        if (requestModel.getContractId() != null) {
            sql.append(" and CONTRACT_ID=?");
            arrParams.add(requestModel.getContractId());
        }

        /***
         * Mô tả trường ACTIVE_STATUS
         * 0. Chưa kích hoạt
         * 1. Hoạt động
         * 2. Hủy
         * 3. Đóng
         * 4. Mở
         * 5. Đã chuyển nhượng"
         */

        /***
         * Mô tả trường STATUS
         * 0. Không hoạt động
         * 1. Hoạt động
         * 2. Import phương tiện từ file
         * 3. Khớp với kết quả đăng kiểm
         * 4. Không khớp kết quả đăng kiểm"
         */
        sql.append(" and STATUS = 1 and RFID_SERIAL is not null \n");
        if (requestModel.getPlateNumber() != null) {
            sql.append(" and LOWER(PLATE_NUMBER) like '%' || LOWER(?) || '%'");
            arrParams.add(requestModel.getPlateNumber().trim());
        }

        // Ngay dang ky
        if (!FnCommon.isNullOrEmpty(requestModel.getStartDate())) {
            sql.append(" and ve.CREATE_DATE >= to_date(?, 'dd/mm/yyyy')");
            arrParams.add(requestModel.getStartDate());
        }
        // Ngay het han
        if (!FnCommon.isNullOrEmpty(requestModel.getEndDate())) {
            sql.append(" and ve.CREATE_DATE <= to_date(?, 'dd/mm/yyyy hh24:mi:ss')");
            arrParams.add(requestModel.getEndDate() + Constants.TIME_ONE_DAY);
        }
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, VehicleByContractDTO.class);
    }


    /**
     * Lay thong tin dang kiem
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getVehicleRegistryInfo(VehicleDTO itemParamsEntity, String plateNumber) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append("select \n");
        sql.append("v.VEHICLE_TYPE_ID                 as vehicleTypeId,                           ---Loai phuong tien \n");
        sql.append("v.OWNER                           as owner,                                   ---Ten chu phuong tien \n");
        sql.append("v.CARGO_WEIGHT                    as cargoWeight,                             ---Khoi luong hang hoa \n");
        sql.append("v.NET_WEIGHT                      as netWeight,                               ---Khoi luong ban than \n");
        sql.append("v.GROSS_WEIGHT                    as grossWeight,                             ---Khoi luong toan bo \n");
        sql.append("v.SEAT_NUMBER                     as seatNumber,                              ---So cho \n");
        sql.append("v.PULLING_WEIGHT                  as pullingWeight,                           ---Khoi luong keo theo \n");
        sql.append("v.CHASSIC_NUMBER                  as chassicNumber,                           ---So khung \n");
        sql.append("v.ENGINE_NUMBER                   as engineNumber,                            ---So may \n");
        sql.append("v.VEHICLE_MARK_ID                 as vehicleMarkId,                           ---Nhan hieu \n");
        sql.append("v.VEHICLE_COLOUR_ID               as vehicleColourId,                         ---Mau son \n");
        sql.append("v.PLATE_TYPE                      as plateType,                               ---Loai bien \n");
        sql.append("v.VEHICLE_BRAND_ID                as vehicleBrandId,                          ---Dong xe \n");
        sql.append("v.VEHICLE_GROUP_ID                as vehicleGroupId,                          ---Loai phuong tien tinh phi \n");
        sql.append("vp.FILE_PATH                      as filePath,                                ---Anh phuong tien \n");
        sql.append("vp.FILE_NAME                      as fileName,                                ---Ten file \n");
        sql.append("v.EXP_DATE                        as regisExpDate                             ---Ngay het han dang kiem \n");
        sql.append("from VEHICLE V \n");
        // anh phuong tien
        sql.append(",(select VEHICLE_ID, FILE_PATH, FILE_NAME FILE_NAME from VEHICLE_PROFILE where DOCUMENT_TYPE_ID = 18) vp \n");
        sql.append("where v.VEHICLE_ID  = vp.VEHICLE_ID (+)\n");
        sql.append("and UPPER(v.PLATE_NUMBER) = :plateNumber \n");
        hmapParams.put("plateNumber", plateNumber.toUpperCase());

        Integer start = null;
        if (itemParamsEntity != null && itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if (itemParamsEntity != null && itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        return getListDataAndCount(sql, hmapParams, start, pageSize, VehicleDTO.class);
    }

    /**
     * Lay cac phuong tien da het han
     * @return
     */
    @Override
    public ResultSelectEntity getVehicleExp() {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> hmapParams = new HashMap<>();

        sql.append(" select \n");
        sql.append(" vehicle_id  as vehicleId,                      ---id phuong tien \n");
        sql.append(" epc    as epc,                                  ---EPC phuong tien \n");
        sql.append(" contract_id    as contractId,                   ---Ma hop dong \n");
        sql.append(" cust_id    as custId                            ---Ma khach hang \n");
        sql.append(" from VEHICLE where ");
        sql.append(" status = 1 ");
        sql.append(" and ACTIVE_STATUS in (1,4) ");
        sql.append(" and PROFILE_STATUS = 3 ");
        sql.append(" and APPROVED_DATE < trunc(sysdate) - 15 ");
        return getListDataAndCount(sql, hmapParams, null, null, VehicleEntity.class);
    }
}
