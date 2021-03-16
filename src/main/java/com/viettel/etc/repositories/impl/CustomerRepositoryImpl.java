package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.CustRegisterDTO;
import com.viettel.etc.dto.CustomerFindByIdDTO;
import com.viettel.etc.dto.CustomerSearchDTO;
import com.viettel.etc.dto.SearchInfoDTO;
import com.viettel.etc.repositories.CustomerRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Autogen class Repository Impl: Lop thao tac danh sach ca nhan
 *
 * @author ToolGen
 * @date Thu Jul 02 14:40:47 ICT 2020
 */
@Repository
public class CustomerRepositoryImpl extends CommonDataBaseRepository implements CustomerRepository {

    /**
     * Tim kiem khach hang theo : DOCUMENT_NUMBER, PHONE_NUMBER
     *
     * @param requestModel params client
     * @return
     * @author Chucnd
     */
    @Override
    public ResultSelectEntity findCustomerByDocumentAndPhone(CustomerSearchDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        sql.append("   select ");
        sql.append("         cus.CUST_ID         as custId,              -- Ma khach hang\n");
        sql.append("         cus.CUST_TYPE_ID    as custTypeId,          -- Loai khach hang\n");
        sql.append("         ct.NAME             as custTypeName,        -- Loai khach hang\n");
        sql.append("         cus.CUST_NAME       as custName,            -- Ten khach hang\n");
        sql.append("         cus.BIRTH_DATE      as birthDate,           -- Ngay sinh\n");
        sql.append("         cus.GENDER          as gender,              -- Gioi tinh\n");
        sql.append("         cus.DOCUMENT_NUMBER as documentNumber,      -- CMTND\n");
        sql.append("         cus.DATE_OF_ISSUE   as dateOfIssue,         -- Ngay cap\n");
        sql.append("         cus.PLACE_OF_ISSUE  as placeOfIssue,        -- noi cap\n");
        sql.append("         cus.AREA_NAME       as areaName,            -- Dia chi\n");
        sql.append("         cus.PHONE_NUMBER    as phoneNumber,         -- So dien thoai\n");
        sql.append("         cus.EMAIL           as email,               -- Email\n");
        sql.append("         cus.TAX_CODE        as taxCode,             -- Tax code\n");
        sql.append("         cus.AREA_CODE       as areaCode,            -- dia chi\n");
        sql.append("         cus.AUTH_NAME       as authName,            -- Ten nguoi uu quyen\n");
        sql.append("         cus.REP_NAME        as repName,             -- Ten nguoi dai dien\n");
        sql.append("         cus.STREET          as street,               -- so nha, duong, pho\n");
        sql.append("         cus.DOCUMENT_TYPE_ID          as documentTypeId               -- Loai giay to\n");
        sql.append("    from ( select * from CUSTOMER where PHONE_NUMBER = ? or DOCUMENT_NUMBER = ? or TAX_CODE = ?) cus,\n");
        sql.append("         CUST_TYPE ct\n ");
        sql.append("    where cus.CUST_TYPE_ID = ct.CUST_TYPE_ID\n");
        List<Object> arrParams = Arrays.asList(requestModel.getInputSearch(), requestModel.getInputSearch(), requestModel.getInputSearch());

        Integer start = 0;
        if (requestModel != null && requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel != null && requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        ResultSelectEntity resultData = getListDataAndCount(sql, arrParams, start, pageSize, CustomerSearchDTO.class);
        return resultData;
    }

    /**
     * Tra cuu thong tin khach hang.
     *
     * @param requestModel : params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity searchTreeInfo(SearchInfoDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select \n")
        .append("    cus.CUST_ID                       as custId,                         -- ma khach hang  \n")
        .append("    cus.CUST_NAME                     as custName,                       -- ten khach hang \n")
        .append("    cus.CUST_TYPE_ID                  as custTypeId,                     -- Loai khach hang \n")
        .append("    cus.DOCUMENT_NUMBER               as documentNumber,                 -- So giay to \n")
        .append("    cus.DOCUMENT_TYPE_ID              as documentTypeId,                 -- Loai giay to \n")
        .append("    cus.PHONE_NUMBER                  as phoneNumber,                    -- So dien thoai cua khach hang \n")
        .append("    cus.REP_IDENTITY_NUMBER           as repIdentityNumber,              -- So Giay to nguoi dai dien \n")
        .append("    cus.REP_IDENTITY_TYPE_ID          as repIdentityTypeId,              -- Loai giay to nguoi dai dien \n")
        .append("    cus.REP_PHONE_NUMBER              as repPhoneNumber,                 -- So dien thoai nguoi dai dien \n")
        .append("    cus.AUTH_IDENTITY_NUMBER          as authIdentityNumber,             -- So Giay to nguoi uu quyen \n")
        .append("    cus.AUTH_IDENTITY_TYPE_ID         as authIdentityTypeId,             -- Loai giay to nguoi uu quyen \n")
        .append("    cus.AUTH_PHONE_NUMBER             as authPhoneNumber                 -- So dien thoai nguoi uu quyen \n")
        .append(" from customer cus where 1 = 1 ");
        // So giay to
        if (!FnCommon.isNullOrEmpty(requestModel.getDocumentNumber())) {
            sql.append(" and LOWER(DOCUMENT_NUMBER) like '%' || LOWER(?) ||'%'");
            arrParams.add(requestModel.getDocumentNumber().trim());
        }
        // Ten khach hang
        if (!FnCommon.isNullOrEmpty(requestModel.getCustName())) {
            sql.append(" and LOWER(CUST_NAME) like '%' || LOWER(?) ||'%'");
            arrParams.add(requestModel.getCustName().trim());
        }
        // ID khach hang
        if (!FnCommon.isNullOrEmpty(requestModel.getCustomerIdList())) {
            sql.append(" and CUST_ID IN (").append(requestModel.getCustomerIdList()).append(") ");
        }
        sql.append(" order by cus.create_date desc");
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        ResultSelectEntity resultData = getListDataAndCount(sql, arrParams, start, pageSize, SearchInfoDTO.class);
        return resultData;
    }

    /**
     * Tim kiem khach hang theo Id
     *
     * @param custId: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity findCustomerById(Long custId) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = Arrays.asList(custId);
        sql.append("select \n");
        sql.append("    cus.CUST_TYPE_ID             as custTypeId,                    -- Loai khach hang\n");
        sql.append("    ct.Name                      as custTypeName,                  -- Ten loai doanh nghiep\n");
        sql.append("    ct.TYPE                      as type,                          -- loai khach hang\n");
        sql.append("    cus.TAX_CODE                 as taxCode,                       -- Ma so thue\n");
        sql.append("    cus.DOCUMENT_TYPE_ID         as documentTypeId,                -- Loai giay to\n");
        sql.append("    dt1.NAME                     as documentTypeName,              -- Ten loai giay to\n");
        sql.append("    cus.CUST_ID                  as custId,                        -- Ma khach hang\n");
        sql.append("    cus.STATUS                   as status,                        -- Trang thai\n");
        sql.append("    cus.CUST_NAME                as custName,                      -- Ten khach hang\n");
        sql.append("    cus.BIRTH_DATE               as birthDate,                     -- Ngay sinh\n");
        sql.append("    cus.GENDER                   as gender,                        -- Gioi tinh\n");
        sql.append("    cus.DOCUMENT_NUMBER          as documentNumber,                -- So CMMT\n");
        sql.append("    cus.DATE_OF_ISSUE            as dateOfIssue,                   -- Ngay cap CMT\n");
        sql.append("    cus.PLACE_OF_ISSUE           as placeOfIssue,                  -- Noi cap CMT\n");
        sql.append("    cus.AREA_NAME                as areaName,                      -- Dia chi T/P\n");
        sql.append("    cus.AREA_CODE                as areaCode,                      -- Ma phuong xa\n");
        sql.append("    cus.STREET                   as street,                        -- Thanh pho\n");
        sql.append("    cus.PHONE_NUMBER             as phoneNumber,                   -- So dien thoai\n");
        sql.append("    cus.EMAIL                    as email,                         -- Email khach hang\n");
        sql.append("-----------------Nguoi dai dien-------------------------------------------------\n");
        sql.append("    cus.REP_NAME                 as repName,                       -- ho va ten nguoi dai dien\n");
        sql.append("    cus.REP_GENDER               as repGender,                     -- gioi tinh nguoi dai dien\n");
        sql.append("    cus.REP_BIRTH_DATE           as repBirthDate,                  -- ngay sinh nguoi dai dien\n");
        sql.append("    cus.REP_IDENTITY_NUMBER      as repIdentityNumber,             -- So giay to nguoi dai dien\n");
        sql.append("    cus.REP_IDENTITY_TYPE_ID     as repIdentityTypeId,             -- Loai giay to nguoi dai dien\n");
        sql.append("    dt2.NAME                     as repIdentityTypeName,           -- Ten loai giay to nguoi dai dien\n");
        sql.append("    cus.REP_DATE_OF_ISSUE        as repDateOfIssue,                -- Ngay cap giay to nguoi dai dien\n");
        sql.append("    cus.REP_PLACE_OF_ISSUE       as repPlaceOfIssue,               -- Noi cap giay to nguoi dai dien\n");
        sql.append("    cus.REP_AREA_NAME            as repAreaName,                   -- Dia chi nguoi dai dien\n");
        sql.append("    cus.REP_STREET               as repStreet,                     -- So nha nguoi dai dien\n");
        sql.append("    cus.REP_AREA_CODE            as repAreaCode,                   -- Ma khu vuc nguoi dai dien\n");
        sql.append("    cus.REP_PHONE_NUMBER         as repPhoneNumber,                -- So dien thoai nguoi dai dien\n");
        sql.append("    cus.REP_EMAIL                as repEmail,                      -- Email nguoi dai dien\n");
        sql.append("--------------------- Nguoi uy quyen---------------------------\n");
        sql.append("    cus.AUTH_NAME                as authName,                      -- ho va ten nguoi uy quyen\n");
        sql.append("    cus.AUTH_GENDER              as authGender,                    -- gioi tinh nguoi uy quyen\n");
        sql.append("    cus.AUTH_BIRTH_DATE          as authBirthDate,                 -- ngay sinh nguoi uy quyen\n");
        sql.append("    cus.AUTH_IDENTITY_NUMBER     as authIdentityNumber,            -- So giay to nguoi uy quyen\n");
        sql.append("    cus.AUTH_IDENTITY_TYPE_ID    as authIdentityTypeId,            -- Loai giay to nguoi uy quyen\n");
        sql.append("    dt3.NAME                     as authIdentityTypeName,          -- Ten loai giay to nguoi uy quyen\n");
        sql.append("    cus.AUTH_DATE_OF_ISSUE       as authDateOfIssue,               -- Ngay cap giay to nguoi uy quyen\n");
        sql.append("    cus.AUTH_PLACE_OF_ISSUE      as authPlaceOfIssue,              -- Noi cap giay to nguoi uy quyen\n");
        sql.append("    cus.AUTH_AREA_NAME           as authAreaName,                  -- Dia chi nguoi uy quyen\n");
        sql.append("    cus.AUTH_STREET              as authStreet,                    -- So nha nguoi uy quyen\n");
        sql.append("    cus.AUTH_AREA_CODE           as authAreaCode,                  -- Ma khu vuc nguoi uy quyen\n");
        sql.append("    cus.AUTH_PHONE_NUMBER        as authPhoneNumber,               -- So dien thoai nguoi uy quyen\n");
        sql.append("    cus.AUTH_EMAIL               as authEmail                      -- Mail nguoi uy quyen\n");
        sql.append(" from \n");
        sql.append("    (select * from CUSTOMER  where CUST_ID = ?) cus, CUST_TYPE ct,");
        sql.append("    (select DOCUMENT_TYPE_ID, NAME from DOCUMENT_TYPE) dt1,\n");
        sql.append("    (select DOCUMENT_TYPE_ID, NAME from DOCUMENT_TYPE) dt2,\n");
        sql.append("    (select DOCUMENT_TYPE_ID, NAME from DOCUMENT_TYPE) dt3\n");
        sql.append(" where cus.CUST_TYPE_ID = ct.CUST_TYPE_ID (+)\n");
        sql.append("   and cus.DOCUMENT_TYPE_ID = dt1.DOCUMENT_TYPE_ID (+)\n");
        sql.append("   and cus.REP_IDENTITY_TYPE_ID = dt2.DOCUMENT_TYPE_ID (+)\n");
        sql.append("   and cus.AUTH_IDENTITY_TYPE_ID = dt3.DOCUMENT_TYPE_ID (+)\n");
        ResultSelectEntity resultData = getListDataAndCount(sql, arrParams, 0, 1, CustomerFindByIdDTO.class);
        return resultData;
    }

    /**
     * lay thong tin dang ky dich vu cua khach hang, du lieu duoc dua vao tu phan he cong thong tin
     *
     * @param itemParamsEntity: params client truyen len
     * @return
     */
    @Override
    public ResultSelectEntity getCustomerRegister(CustRegisterDTO itemParamsEntity) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();

        sql.append("select \n");
        sql.append("    ACCOUNT_ID               as accountId,                      --Mã tài khoản khách hàng đăng ký\n");
        sql.append("    ACCOUNT_USER             as accountUser,                    --Tài khoản đăng ký\n");
        sql.append("    AREA_CODE                as areaCode,                       --Mã khu vực\n");
        sql.append("    AREA_NAME                as areaName,                       --Địa chỉ thường trú\n");
        sql.append("    AUTH_AREA_CODE           as authAreaCode,                   --Mã khu vực của người ủy quyền\n");
        sql.append("    AUTH_AREA_NAME           as authAreaName,                   --Địa chỉ người được ủy quyền\n");
        sql.append("    AUTH_BIRTH_DATE          as authBirthDate,                  --Ngày sinh người được ủy quyền\n");
        sql.append("    AUTH_DATE_OF_ISSUE       as authDateOfIssue,                --Ngày cấp giấy tờ người được ủy quyền\n");
        sql.append("    AUTH_EMAIL               as authEmail,                      --Địa chỉ Email người được ủy quyền\n");
        sql.append("    AUTH_GENDER              as authGender,                     --Giới tính người được ủy quyền\n");
        sql.append("    AUTH_IDENTITY_NUMBER     as authIentityNumber,              --Số giấy tờ người được ủy quyền\n");
        sql.append("    AUTH_IDENTITY_TYPE_ID    as authIdentityTypeId,             --Loại giấy tờ người được ủy quyền\n");
        sql.append("    AUTH_NAME                as authName,                       --Họ và tên người được ủy quyền\n");
        sql.append("    AUTH_PHONE_NUMBER        as authPhoneNumber,                --Số điện thoại người được ủy quyền\n");
        sql.append("    AUTH_PLACE_OF_ISSUE      as authPlaceOfIssue,               --Nơi cấp giấy tờ người được ủy quyền\n");
        sql.append("    AUTH_STREET              as authStreet,                     --Số nhà, đường/ phố người được ủy quyền\n");
        sql.append("    BIRTH_DATE               as birthDate,                      --Ngày sinh khách hàng cá nhân/ Ngày thanh lập tổ chức, doanh nghiệp\n");
        sql.append("    CUST_NAME                as custName,                       --Họ và tên khách hàng cá nhân/ Tên tổ chức, doanh nghiệp\n");
        sql.append("    CUST_REG_ID              as custRegId,                      --Khóa chính của bảng (AUTO)\n");
        sql.append("    CUST_TYPE_ID             as custTypeId,                     --Loại khách hàng\n");
        sql.append("    DATE_OF_ISSUE            as dateOfIssue,                    --Ngày cấp CMTND/CCCD/Hộ chiếu/GPKD\n");
        sql.append("    DOCUMENT_NUMBER          as documentNumber,                 --Số giấy tờ (CMTND/CCCD/Hộ chiếu/GPKD)\n");
        sql.append("    DOCUMENT_TYPE_ID         as documentTypeId,                 --Loại giấy tờ\n");
        sql.append("    EMAIL                    as email,                          --Địa chỉ Email của khách hàng\n");
        sql.append("    FAX                      as fax,                            --Số fax của khách hàng\n");
        sql.append("    GENDER                   as gender,                         --Giới tính\n");
        sql.append("    NUM_VEHICLE              as numVehicle,                     --Tổng số lượng phương tiện\n");
        sql.append("    PHONE_NUMBER             as phoneNumber,                    --Số điện thoại của khách hàng\n");
        sql.append("    PLACE_OF_ISSUE           as placeOfIssue,                   --Nơi cấp CMTND/CCCD/Hộ chiếu/GPKD\n");
        sql.append("    REG_DATE                 as regDate,                        --Ngày đăng ký\n");
        sql.append("    REG_STATUS               as regStatus,                      --Trạng thái đăng ký\n");
        sql.append("    REP_AREA_CODE            as repAreaCode,                    --Mã khu vực của người đại diện\n");
        sql.append("    REP_AREA_NAME            as repAreaName,                    --Địa chỉ người đại diện\n");
        sql.append("    REP_BIRTH_DATE           as repBirthDate,                   --Ngày sinh người đại diện\n");
        sql.append("    REP_DATE_OF_ISSUE        as repDateOfIssue,                 --Ngày cấp giấy tờ người đại diện\n");
        sql.append("    REP_EMAIL                as repEmail,                       --Địa chỉ Email người đại diện\n");
        sql.append("    REP_GENDER               as repGender,                      --Giới tính người đại diện\n");
        sql.append("    REP_IDENTITY_NUMBER      as repIdentityNumber,              --Số giấy tờ người đại diện\n");
        sql.append("    REP_IDENTITY_TYPE_ID     as repIdentityTypeId,              --Loại giấy tờ người đại diện\n");
        sql.append("    REP_NAME                 as repName,                        --Họ và tên người đại diện\n");
        sql.append("    REP_PHONE_NUMBER         as repPhoneNumber,                 --Số điện thoại người đại diện\n");
        sql.append("    REP_PLACE_OF_ISSUE       as repPlaceOfIssue,                --Nơi cấp giấy tờ người đại diện\n");
        sql.append("    REP_STREET               as repStreet,                      --Số nhà, đường/ phố người đại diện\n");
        sql.append("    STREET                   as street,                         --Số nhà, đường/ phố\n");
        sql.append("    TAX_CODE                 as taxCode,                        --Mã số thuế của doanh nghiệp\n");
        sql.append("    WEBSITE                  as website                         --Địa chỉ website\n");
        sql.append("from CUST_REGIS \n");
        sql.append("where 1=1 \n");

        if (itemParamsEntity.getPhoneNumber() != null) {
            sql.append("and PHONE_NUMBER = ? \n");
            arrParams.add(itemParamsEntity.getPhoneNumber());

        } else {
            sql.append("and CUST_REG_ID = 0 \n");
        }

        sql.append("order by CUST_REG_ID");

        Integer start = null;
        if (itemParamsEntity != null && itemParamsEntity.getStartrecord() != null) {
            start = itemParamsEntity.getStartrecord();
        }
        Integer pageSize = null;
        if (itemParamsEntity != null && itemParamsEntity.getPagesize() != null) {
            pageSize = itemParamsEntity.getPagesize();
        }
        ResultSelectEntity resultData = getListDataAndCount(sql, arrParams, start, pageSize, CustRegisterDTO.class);
        return resultData;
    }
}
