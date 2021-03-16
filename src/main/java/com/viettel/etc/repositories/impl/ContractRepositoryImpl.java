package com.viettel.etc.repositories.impl;

import com.google.common.base.Strings;
import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.ContractRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Autogen class Repository Impl: Lop thao tac tra cuu thong tin khach hang
 *
 * @author ToolGen
 * @date Wed Jun 24 16:21:56 ICT 2020
 */
@Repository
public class ContractRepositoryImpl extends CommonDataBaseRepository implements ContractRepository {

    /**
     * Tra cuu thong tin hop dong theo ma hop dong.
     *
     * @param contractId : param lay tu url
     * @return
     */
    @Override
    public ResultSelectEntity findContractById(Long contractId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("    con.CUST_ID          as custId,              -- ma khach hang\n");
        sql.append("    con.CONTRACT_ID      as contractId,          -- ma hop dong\n");
        sql.append("    con.CONTRACT_NO      as contractNo,          -- so hop dong\n");
        sql.append("    con.SIGN_DATE       as signDate,          -- ngay ky\n");
        sql.append("    con.EFF_DATE        as effDate,           -- ngay co hieu luc\n");
        sql.append("    con.EXP_DATE        as expDate,           -- ngay het hieu luc\n");
        sql.append("    con.STATUS           as status,             -- trang thai hop dong\n");
        sql.append("    con.ACCOUNT_USER     accountUser,       -- tai khoan dang nhap\n");
        sql.append("    con.ACCOUNT_USER_ID     accountUserId,       -- tai khoan dang nhap\n");
        sql.append("    con.CREATE_USER         as createUser,       -- Nguoi tao\n");
        sql.append("    con.IS_LOCK             as isLock,           -- Khoa tai khoan\n");
        sql.append("    ---------------------- dia chi thong bao cuoc  \n");
        sql.append("    con.NOTICE_NAME         as noticeName,        -- ho va ten\n");
        sql.append("    con.NOTICE_AREA_NAME    as noticeAreaName,      -- dia chi\n");
        sql.append("    con.NOTICE_AREA_CODE    as noticeAreaCode,      -- dia chi\n");
        sql.append("    con.NOTICE_STREET       as noticeStreet,        -- so nha, duong, pho\n");
        sql.append("    con.NOTICE_EMAIL        as noticeEmail,         --  dia chi Email\n");
        sql.append("    con.NOTICE_PHONE_NUMBER as noticePhoneNumber,   --so dien thoai \n");
        sql.append("    con.SIGN_NAME           as signName,            -- nguoi ky\n");
        sql.append("    ----------------------- tai khoan thanh toan \n");
        sql.append("    con.EMAIL_NOTIFICATION  as emailNotification,  -- thong bao email\n");
        sql.append("    con.SMS_NOTIFICATION    as smsNotification,    \n");
        sql.append("    con.SMS_RENEW           as smsRenew,           \n");
        sql.append("    con.PUSH_NOTIFICATION   as pushNotification,   \n");
        sql.append("    con.BILL_CYCLE          as billCycle,          \n");
        sql.append("    con.PAY_CHARGE          as payCharge,           \n");
        sql.append("    ----------------------- Thong tin nguoi uu quyen \n");
        sql.append("    cu.AUTH_NAME            as authName,                -- Ten nguoi uu quyen\n");
        sql.append("    cu.AUTH_BIRTH_DATE      as authBirthDate,           -- Ngay sinh nguoi uy quyen\n");
        sql.append("    cu.AUTH_GENDER          as authGender,              -- Gioi tinh nguoi uu quyen\n");
        sql.append("    cu.AUTH_IDENTITY_TYPE_ID   as authTypeId,              -- Loai giay to nguoi uy quyen\n");
        sql.append("    cu.AUTH_IDENTITY_NUMBER as authNumber,              -- So giay to nguoi uy quyen\n");
        sql.append("    cu.AUTH_DATE_OF_ISSUE   as authDateIssue,           -- Ngay cap\n");
        sql.append("    cu.AUTH_PLACE_OF_ISSUE  as authPlaceIssue,          -- Noi cap\n");
        sql.append("    ----------------------- Thong tin nguoi dai dien \n");
        sql.append("    cu.REP_NAME             as repName,                 -- Ten nguoi dai dien\n");
        sql.append("    cu.REP_BIRTH_DATE       as repBirthDate,            -- Ngay sinh nguoi dai dien\n");
        sql.append("    cu.REP_GENDER           as repGender,               -- Gioi tinh nguoi dai dien\n");
        sql.append("    cu.REP_IDENTITY_TYPE_ID as repTypeId,               -- Loai giay to nguoi dai dien\n");
        sql.append("    cu.REP_IDENTITY_NUMBER  as repNumber,               -- So giay to nguoi dai dien\n");
        sql.append("    cu.REP_DATE_OF_ISSUE    as repDateOfIssue,          -- Ngay cap\n");
        sql.append("    cu.REP_PLACE_OF_ISSUE   as repPlaceOfIssue,         -- Noi cap\n");
        sql.append("    ----------------------- Thong tin khach hang ca nhan \n");
        sql.append("    cu.CUST_NAME            as custName,                -- Ten khach hang\n");
        sql.append("    cu.BIRTH_DATE           as birthDate,               -- Ngay sinho\n");
        sql.append("    cu.GENDER               as gender,                  -- Gioi tinh\n");
        sql.append("    cu.DOCUMENT_TYPE_ID     as documentTypeId,          -- Loai giay to\n");
        sql.append("    cu.DOCUMENT_NUMBER      as documentNumber,          -- So giay to\n");
        sql.append("    cu.DATE_OF_ISSUE        as dateOfIssue,             -- Ngay cap\n");
        sql.append("    cu.PLACE_OF_ISSUE       as placeOfIssue,            -- Noi cap\n");
        sql.append("    cu.CUST_TYPE_ID         as custTypeId,              -- Loai khach hang\n");
        sql.append("    case\n");
        sql.append("    when cu.AUTH_NAME is not null then 1\n");
        sql.append("    when cu.AUTH_NAME is null and cu.REP_NAME is not null then 2\n");
        sql.append("    else  0\n");
        sql.append("    end as customerType\n");
        sql.append("    from CONTRACT con, CUSTOMER cu\n");
        sql.append("   where con.CUST_ID = cu.CUST_ID (+)");
        List<Object> arrParams = new ArrayList<>();
        if (contractId != null) {
            sql.append(" and CONTRACT_ID=?");
            arrParams.add(contractId);
        }
        return getListDataAndCount(sql, arrParams, 0, 10, ContractByCustomerDTO.class);
    }


    /**
     * Tra cuu danh sach hop dong theo customer
     *
     * @param custId
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findContractByCustomer(Integer custId, SearchContractByCustomerDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("    CONTRACT_NO                         as contractNo,-- so hop dong \n");
        sql.append("    SIGN_NAME                           as signName,  -- nguoi ky (dd/mm/yyyy)\n");
        sql.append("    to_char(SIGN_DATE, 'dd/mm/yyyy')    as signDate,  -- ngay ky  (dd/mm/yyyy)\n");
        sql.append("    STATUS                              as status     -- trang thai hop dong\n");
        sql.append(" from CONTRACT where 1=1\n");
        List<Object> arrParams = new ArrayList<>();
        if (custId != null) {
            sql.append(" and CUST_ID = ?");
            arrParams.add(custId);
        }
        if (!FnCommon.isNullOrEmpty(requestModel.getStartDate())) {
            sql.append(" and SIGN_DATE >= to_date(?,'dd/mm/yyyy')");
            arrParams.add(requestModel.getStartDate());
        }
        if (!FnCommon.isNullOrEmpty(requestModel.getEndDate())) {
            sql.append(" and SIGN_DATE <= to_date(?,'dd/mm/yyyy hh24:mi:ss')");
            arrParams.add(requestModel.getEndDate() + Constants.TIME_ONE_DAY);
        }
        if (!Strings.isNullOrEmpty(requestModel.getContractNo())) {
            sql.append(" and LOWER(CONTRACT_NO) like '%' || LOWER(?) || '%'");
            arrParams.add(requestModel.getContractNo().trim());
        }
        Integer start = 0;
        if (requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, SearchContractByCustomerDTO.class);
    }

    /**
     * Tra cuu thong tin hop dong
     *
     * @param searchContractDTO
     * @return
     */
    @Override
    public ResultSelectEntity searchContract(SearchContractDTO searchContractDTO) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select DISTINCT");
        sql.append("    cus.CUST_ID             as custId,        -- ma khach hang  \n");
        sql.append("    cus.CUST_NAME           as custName,      -- ten khach hang \n");
        sql.append("    con.CONTRACT_NO         as contractNo,    -- so hop dong    \n");
        sql.append("    con.ACCOUNT_USER_ID     as accountUserId, -- ma khach hang  \n");
        sql.append("    con.SIGN_DATE           as signDate,      -- Ngay ky        \n");
        sql.append("    con.SIGN_NAME           as signName,      -- Nguoi ky       \n");
        sql.append("    con.STATUS              as status,        -- status         \n");
        sql.append("    con.CONTRACT_ID         as contractId,    -- Ma hop dong    \n");
        sql.append("    con.IS_LOCK             as isLock,        -- Khoa tai khoan         \n");
        sql.append("    con.ACCOUNT_USER        as accountUser,   -- Tai khoan dang nhap \n");
        sql.append("    cus.BIRTH_DATE          as birthDate,     -- Ngay sinh    \n");
        sql.append("    cus.DOCUMENT_TYPE_ID    as documentTypeId,-- Loai giay to    \n");
        sql.append("    cus.DOCUMENT_NUMBER     as documentNumber,-- So giay to    \n");
        sql.append("    cus.REP_BIRTH_DATE      as repBirthDate,                -- Ngay sinh nguoi dai dien    \n");
        sql.append("    cus.REP_IDENTITY_TYPE_ID      as repIdentityTypeId,     -- Loai giay to nguoi dai dien    \n");
        sql.append("    cus.REP_IDENTITY_NUMBER       as repIdentityNumber,     -- So giay to nguoi dai dien    \n");
        sql.append("    cus.AUTH_BIRTH_DATE           as authBirthDate,         -- Ngay sinh nguoi uu quyen    \n");
        sql.append("    cus.AUTH_IDENTITY_TYPE_ID     as authIdentityTypeId,    -- Loai giay to nguoi uu quyen    \n");
        sql.append("    cus.AUTH_IDENTITY_NUMBER      as authIdentityNumber     -- So giay to nguoi uu quyen    \n");
        sql.append(" from\n");
        sql.append(" customer cus ,contract con, vehicle ve \n");
        sql.append(" where con.cust_id = cus.cust_id and con.contract_id = ve.contract_id (+)\n");
        // So hop dong
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getContractNo())) {
            sql.append(" and LOWER(con.CONTRACT_NO) like '%' || LOWER(?) ||'%'");
            arrParams.add(searchContractDTO.getContractNo().trim());
        }
        // Bien so xe
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getPlateNumber())) {
            sql.append(" and LOWER(ve.PLATE_NUMBER) like '%' || LOWER(?) ||'%'");
            arrParams.add(searchContractDTO.getPlateNumber().trim());
        }
        // So giay to
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getDocumentNumber())) {
            sql.append(" and LOWER(DOCUMENT_NUMBER) like '%' || LOWER(?) ||'%'");
            arrParams.add(searchContractDTO.getDocumentNumber().trim());
        }
        // Dien thoai
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getNoticePhoneNumber())) {
            sql.append(" and NOTICE_PHONE_NUMBER like '%' || ? ||'%'");
            arrParams.add(searchContractDTO.getNoticePhoneNumber().trim());
        }
        // Dien thoai
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getPhoneNumber())) {
            sql.append(" and PHONE_NUMBER like '%' || ? ||'%'");
            arrParams.add(searchContractDTO.getPhoneNumber().trim());
        }
        // Ma khach hang
        if (searchContractDTO.getCustId() != null) {
            sql.append(" and con.CUST_ID = ? ");
            arrParams.add(searchContractDTO.getCustId());
        }
        // Ten khach hang
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getCustName())) {
            sql.append(" and LOWER(CUST_NAME) like '%' || LOWER(?) ||'%'");
            arrParams.add(searchContractDTO.getCustName().trim());
        }
        // Loại khach hang
        if (searchContractDTO.getCustTypeId() != null) {
            sql.append(" and CUST_TYPE_ID = ?");
            arrParams.add(searchContractDTO.getCustTypeId());
        }
        // Tu ngay
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getStartDate())) {
            sql.append(" and con.SIGN_DATE >= to_date(?, 'dd/mm/yyyy')");
            arrParams.add(searchContractDTO.getStartDate().trim());
        }
        // Den ngay
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getEndDate())) {
            sql.append(" and con.SIGN_DATE <= to_date(?, 'dd/mm/yyyy hh24:mi:ss')");
            arrParams.add(searchContractDTO.getEndDate() + Constants.TIME_ONE_DAY);
        }
        //auto complete documentNumber or phone number
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getDocumentNumberOrPhone())) {
            sql.append(" (and cus.DOCUMENT_NUMBER = ? OR cus.PHONE_NUMBER = ?) and con.STATUS = 2");
            arrParams.add(searchContractDTO.getDocumentNumberOrPhone().trim());
            arrParams.add(searchContractDTO.getDocumentNumberOrPhone().trim());
        }
        // account user
        if (!FnCommon.isNullOrEmpty(searchContractDTO.getAccountUser())) {
            sql.append(" and con.ACCOUNT_USER = ?");
            arrParams.add(searchContractDTO.getAccountUser().trim());
        }
        sql.append(" ORDER BY cus.CUST_NAME ASC,con.SIGN_DATE DESC\n");

        Integer start = 0;
        if (searchContractDTO.getStartrecord() != null) {
            start = searchContractDTO.getStartrecord();
        }
        Integer pageSize = null;
        if (searchContractDTO.getPagesize() != null) {
            pageSize = searchContractDTO.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, SearchContractDTO.class);
    }

    /**
     * Tim kiem file dinh kem theo contract_id
     *
     * @param contractId
     * @return
     */
    @Override
    public ResultSelectEntity findProfileByContract(Long contractId, ContractProfileDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append(" select ");
        sql.append("    cp.contract_profile_id     as contractProfileId     -- Ma hop dong\n");
        sql.append("   ,dt.DOCUMENT_TYPE_ID        as documentTypeId        -- Loai giay to\n");
        sql.append("   ,dt.NAME                    as documentTypeName      -- Ten loai giay to\n");
        sql.append("   ,dt.TYPE                    as type                  -- Loai chung tu\n");
        sql.append("   ,cp.createDate              as createDate            -- Ngay tiep nhan\n");
        sql.append("   ,cp.createDate              as scanDay               -- Ngay scan\n");
        sql.append("   ,cp.FILE_NAME               as fileName              -- Ten file\n");
        sql.append("   ,cp.STATUS                  as status                -- Trang thai chung tu\n");
        sql.append("   ,cp.DESCRIPTION             as description           -- ghi chu\n");
        sql.append(" from\n");
        sql.append(" (select contract_profile_id, c.document_type_id, max(c.create_date) as createDate, c.FILE_NAME, c.STATUS, c.DESCRIPTION \n");
        sql.append(" from contract_profile c where 1=1\n");
        if (contractId != null) {
            sql.append(" and c.CONTRACT_ID = ?");
            arrParams.add(contractId);
        }
        sql.append(" GROUP BY c.document_type_id, c.FILE_NAME, c.contract_profile_id, c.STATUS, c.DESCRIPTION ");
        sql.append(" order by c.document_type_id desc) cp\n");
        sql.append(" RIGHT JOIN DOCUMENT_TYPE dt ON cp.DOCUMENT_TYPE_ID = dt.DOCUMENT_TYPE_ID \n");
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
        return getListDataAndCount(sql, arrParams, start, pageSize, ContractProfileDTO.class);
    }

    /**
     * Download file dinh kiem theo id
     *
     * @param profileId
     * @return
     */
    @Override
    public List<ContractProfileDTO> downloadProfileByContract(Integer profileId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append("    CONTRACT_PROFILE_ID     as contractProfileId        -- Loai giay to\n");
        sql.append("    ,FILE_NAME              as fileName                 -- Ten file dinh kem\n");
        sql.append("    ,FILE_PATH              as filePath                 -- Loai giay to\n");
        sql.append(" from CONTRACT_PROFILE where CONTRACT_PROFILE_ID=?\n");
        List<Object> arrParams = Arrays.asList(profileId);
        return (List<ContractProfileDTO>) getListData(sql, arrParams, 0, 10, ContractProfileDTO.class);
    }

    /**
     * Tim kiem hop dong theo : CONTRACT_NO, PlATE_NUMBER
     *
     * @param dataParams params client
     * @return
     * @author Chucnd
     */
    @Override
    public ResultSelectEntity findByPlateNumberAndContractNo(ContractSearchDTO dataParams) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append("    ct.CONTRACT_ID                             as contractId,          -- so hop dong\n");
        sql.append("    ct.CONTRACT_NO                             as contractNo,          -- so hop dong\n");
        sql.append("    ct.SIGN_DATE                               as signDate,            -- ngay ky\n");
        sql.append("    ct.SIGN_NAME                               as signName,            -- nguoi ky\n");
        sql.append("    ct.EFF_DATE                                as effDate,             -- ngay co hieu luc\n");
        sql.append("    ct.EXP_DATE                                as expDate,             -- ngay het hieu luc\n");
        sql.append("    ct.CREATE_USER                             as createUser,          -- Nhan vien\n");
        sql.append("    ct.NOTICE_AREA_NAME                        as noticeAreaName,      -- Dia chi\n");
        sql.append("    ct.NOTICE_STREET                           as noticeStreet,        -- Duong pho\n");
        sql.append("    ct.NOTICE_AREA_CODE                        as noticeAreaCode,      -- ma duong pho\n");
        sql.append("    cu.CUST_ID                                 as custId,              -- ma khach hang\n");
        sql.append("    cu.CUST_NAME                               as custName,            -- ten khach hang\n");
        sql.append("    cu.REP_NAME                                as repName,             -- ten nguoi dai dien\n");
        sql.append("    cu.CUST_TYPE_ID                            as custTypeId,          -- Loai khach hang\n");
        sql.append("    cu.AUTH_NAME                               as authName,            -- ten nguoi uy quyen\n");
        sql.append("    cu.PHONE_NUMBER                            as phoneNumber,         -- So dien thoai\n");
        sql.append("    cu.DOCUMENT_NUMBER                         as documentNumber,      -- So giay to\n");
        sql.append("    cu.DOCUMENT_TYPE_ID                        as documentTypeId       -- Loai giay to\n");
        sql.append(" from CONTRACT ct, CUSTOMER cu  where 1=1 and ct.CUST_ID = cu.CUST_ID (+) \n");

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

        sql.append(" and (UPPER(CONTRACT_NO) = ? or CONTRACT_ID = (select DISTINCT CONTRACT_ID from VEHICLE where UPPER(PLATE_NUMBER) = ? and status = 1 and active_status not in (0,5)))\n");
        List<Object> arrParams = new LinkedList<>();
        arrParams.add(dataParams.getInputSearch().toUpperCase());
        arrParams.add(dataParams.getInputSearch().toUpperCase());
        if (dataParams.getStatus() != null) {
            sql.append("and ct.STATUS = ? ");
            arrParams.add(dataParams.getStatus());
        }
        return getListDataAndCount(sql, arrParams, 0, 1, ContractSearchDTO.class);
    }

    /**
     * Tim kiem hop dong theo contractId
     *
     * @param contractId
     * @param requestModel
     * @return
     */
    @Override
    public ResultSelectEntity findProfileByContractId(Long contractId, ContractProfileDTO requestModel) {
        StringBuilder sql = new StringBuilder();
        List<Object> arrParams = new ArrayList<>();
        sql.append("select DISTINCT\n");
        sql.append(" cp.CONTRACT_PROFILE_ID                          as contractProfileId                -- ID ho so dinh kem \n");
        sql.append(" ,cp.FILE_NAME                                   as fileName                         -- Ten file \n");
        sql.append(" ,dt.NAME                                        as documentTypeName                 -- Loai giay to \n");
        sql.append(" ,cp.CREATE_DATE                                 as createDate                       -- Ngay phe duyet \n");
        sql.append(" ,cp.CREATE_DATE                                 as scanDay                          -- Ngay scan \n");
        sql.append(" ,cp.STATUS                                      as status                           -- Trang thai giay to \n");
        sql.append(" from  CONTRACT_PROFILE cp, DOCUMENT_TYPE dt where cp.DOCUMENT_TYPE_ID = dt.DOCUMENT_TYPE_ID \n");
        if (contractId != null) {
            sql.append(" and cp.CONTRACT_ID = ? \n");
            arrParams.add(contractId);
        }

        Integer start = 0;
        if (requestModel != null && requestModel.getStartrecord() != null) {
            start = requestModel.getStartrecord();
        }
        Integer pageSize = null;
        if (requestModel != null && requestModel.getPagesize() != null) {
            pageSize = requestModel.getPagesize();
        }
        return getListDataAndCount(sql, arrParams, start, pageSize, ContractProfileDTO.class);
    }
}
