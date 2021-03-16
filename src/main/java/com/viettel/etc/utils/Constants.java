package com.viettel.etc.utils;

import com.squareup.okhttp.MediaType;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String REQUEST_MAPPING_V1 = "/api/v1";
    public static final String MOBILE = "/mobile";
    public static final String COMMON_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String COMMON_DATE_FORMAT = "dd/MM/yyyy";
    public static final String COMMON_DATE_FORMAT_BOO = "yyyyMMdd";
    public static final String COMMON_DATE_FORMAT_BOO_24H = "yyyyMMdd HH:mm:ss";
    public static final String COMMON_DATE_FORMAT_24H = "dd/MM/yyyy hh24:mi:ss";
    public static final String COMMON_DATE_FORMAT_24H_VTP = "yyyyMMddHHmmss";
    public static final String ORACLE_DATE_FORMAT = "DD-MM-YYYY";
    public static final String MOMO_DATE_FORMAT = "YYYY-MM-DD HH:mm:ss";
    public static final String LOCALE_VN = "vi_VN";
    public static final String TIMEZONE_VN = "Asia/Ho_Chi_Minh";
    public static final String EXCEL_DATE_FORMAT = "dd-MMM-yyyy";
    public static final int AMOUNT_ZERO = 0;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_URL_ENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    /*
     * man hinh tim kiem customer, contract, vehicle
     */ 
    public static final String CUSTOMER_TYPE_SCREEN = "CUSTOMER";
    public static final String CONTRACT_TYPE_SCREEN = "CONTRACT";
    public static final String VEHICLE_TYPE_SCREEN = "VEHICLE";
    public static final String TICKET_TYPE_SCREEN = "TICKET_TYPE_SCREEN";
    // ly do tac dong, loai tac dong
    public static final String ACT_REASON_ACTIVE = "1";
    //The vat ly RFID
    public static final String VEHICLE_RFID = "RFID";
    public static final String VEHICLE_NOT_RFID = "NOT_RFID";
    public static final String VEHICLE_ACTIVE = "VEHICLE_ACTIVE";
    // Loai khach hang
    public static final Long AUTH = 1L;
    public static final Long REP = 2L;
    public static final Long CUSTOMER = 0L;
    public static final Long[] CUSTOMER_ENTERPRISE = {2L, 3L, 4L, 5L, 6L};
    public static final Long[] CUSTOMER_PERSONAL = {1L, 7L};
    public static final Long CUS_ENTERPRISE = 2L;
    public static final Long CUS_PERSONAL = 1L;
    // Cach tinh thoi gian hieu luc cua ve (theo tram/doan)
    public static final String METHOD_CHARGE = "METHOD_CHARGE";
    public static final String PLATE_TYPE = "PLATE_TYPE";

    //Chiet khau, khuyen mai
    public static final String PROMOTION_NEW = "0";
    public static final String PROMOTION_ACTIVE = "1";
    public static final String PROMOTION_INACTIVE = "2";
    public static final String PROMOTION_APPROVED = "1";
    public static final String PROMOTION_UNAPPROVED = "0";
    public static final String PROMOTION_KM = "1";
    public static final String PROMOTION_CK = "2";
    public static final String PROMOTION_NL = "3";
    public static final String PROMOTION_LVL_CUSTOMER = "0";
    public static final String PROMOTION_LVL_CONTRACT = "1";
    public static final String PROMOTION_LVL_VEHICLE = "2";

    //ExceptionType
    public static final String EXCEPTION_VEHICLE = "1";
    public static final String EXCEPTION_TICKET = "2";
    public static final String EXCEPTION_PRIORITY = "3";
    public static final String EXCEPTION_BAN = "4";
    public static final Long EXCEPTION_NEW = 1L;
    public static final Long EXCEPTION_APPROVED = 4L;

    //Service plan type
    public static final Long SERVICE_PLAN_TYPE_PRIORITY = 2L;
    public static final Long SERVICE_PLAN_TYPE_BAN = 3L;
    public static final Long SERVICE_PLAN_TYPE_TURN = 1L;
    public static final Long SERVICE_PLAN_TYPE_MONTH = 4L;
    public static final Long SERVICE_PLAN_TYPE_QUART = 5L;

    //Phi dich vu
    public static final Integer SERVICE_FEE_NEW = 0;
    public static final Integer SERVICE_FEE_ACTIVE = 1;
    public static final Integer SERVICE_FEE_INACTIVE = 2;
    public static final Integer SERVICE_FEE_APPROVED = 1;
    public static final Integer SERVICE_FEE_UNAPPROVED = 0;

    //Other
    public static final String TRAM_KIN = "0";
    public static final String MODULE_CRM = "CRM";
    public static final String MODULE_BOT = "BOT";
    public static final String MODULE_MOT = "MOT";
    public static final String TIME_ONE_DAY = " 23:59:59";
    public static final String SLASH = "/";
    public static final Long OFFER_LEVEL_DEFAULT = 2L;
    public static final String MESSAGE_CODE = "MESSAGE_CODE";
    public static final String MESSAGE_ERROR_VI = "MESSAGE_ERROR_VI";
    public static final String MESSAGE_ERROR_EN = "MESSAGE_ERROR_EN";
    public static final String CONTRACT_KEY_GEN = "PWD";
    public static final Long ATTACHMENT_EXCEPTION = 2L;
    public static final String BOO1 = "BOO1";
    public static final String BOO2 = "BOO2";
    public static final String VTP = "Viettel Post";
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    public static String STR_EMPTY = "";
    public static int SIZE_LIST_ZERO = 0;
    public static int SIZE_LIST_ONE = SIZE_LIST_ZERO + 1;
    public static String GROUPS = "ETC - Chủ phương tiện";
    public static String OCS_STATION = "0";
    public static String OCS_STAGE = "2";
    public static Long RACH_MIEU_STATION = 5018L;

    public interface STATUS {
        String ACTIVE = "1";
        String INACTIVE = "0";
    }

    public interface LUCKY_CODE {
        int CUSTNEW = 1;
        int NAPTIEN = 2;
        int LKVTPAY = 3;
    }

    public interface VDTC_INFO {
        String SENDER_FULLNAME = "Công ty giao thông số VN (VDTC)";
        String SENDER_ADDRESS = "Số 1 Trần Hữu Dực";
        String SENDER_PHONE = "0866.623.368";
        String SENDER_EMAIL = "cskh@vdtc.com.vn";
        Long SENDER_WARD = 494L;
        Long SENDER_DISTRICT = 25L;
        Long SENDER_PROVINCE = 1L;
    }

    public interface EXCEPTION_GROUP {
        Long EXCEPTION = 1L;            // 1. Nhom ngoai le xe, ve
        Long PRIORITIZE = 2L;           // 2. Nhom uu tien , cam
    }

    public interface EXCEPTION_TYPE {
        String VEHICLE = "1";              // 1. Ngoai le xe
        String TICKET = "2";               // 2. Ngoai le ve
        String WHITELIST = "3";            // 3. Uu tien
        String BLACKLIST = "4";            // 4. Cam
    }

    public interface ATTACHMENT_TYPE {
        Long REVENUE = 1L;              // 1. File dinh kem cua nghiep vu quan ly bang cuoc, chia se doanh thu
        Long EXCEPTION = 2L;            // 2. File dinh kem của nghiep vu quan ly ngoai le, uu tien, cam.
        Long OTHER = 3L;                // 3. Nghiep vu khac
    }

    public interface ATTACHMENT_FILE_STATUS {
        String ACTIVE = "1";            // 1. Hoat dong
        String IN_ACTIVE = "2";         //2. Khong hoat dong
    }

    public interface EXCEL_SHEET {
        Integer WHITELIST = 0;                  // Sheet danh sach uu tien
        Integer BLACKLIST = 1;                  // Sheet danh sach cam
        Integer EXCEPTION_VEHICLE = 2;          // Sheet danh sach ngoai le xe
        Integer EXCEPTION_TICKET = 3;           // Sheet danh sach ngoai le ve
    }

    public interface ACT_TYPE {
        Long LK_VTPAY = 13L;
        Long ADD_VEHICLE = 3L;
        Long MODIFY_VEHICLE = 5L;
        Long CLOSE_BRIEFCASES = 20L;
        Long CHARGE_TICKET = 24L;
        Long DESTROY_TICKET = 25L;
        Long NAP_TIEN = 32L;
        Long BOO1_SYNC = 260L;
        Long BOO1_CHECK_VEHICLE = 270L;
        Long SEND_SMS = 280L;
        Long SEND_EMAIL = 290L;
        Long QUERY_CONTRACT_INFO = 1L;
        Long CALL_PAY_APP_MOMO_API = 296L;
        Long CALL_PAY_CONFIRM_MOMO_API = 297L;
        Long SAVE_CDR_MERCHANTS_DATA = 298L;
        Long CREATE_SALE_ORDER = 299L;
        Long VERIFY_SALE_ORDER = 300L;
        Long RESULT_SALE_ORDER = 301L;
        Long IMPORT_BL_WL_T_Q = 302L;
        Long SERVICE_REGISTER = 61L;
        Long ADD_CONTRACT = 11L;
    }

    public interface ACT_REASON {
        Long DEFAULT = 0L;
        Long ADD_VEHICLE = 2L;
        Long CLOSE_BRIEFCASES = 39L;
        Long ACTIVE_RFID = 32L;
    }

    public interface BOO_INFO {
        Long CUST_ID = 1L;
        Long CONTRACT_ID = 1L;
        String PROFILE_STATUS = "2";
    }

    public interface CATEGORY_STATUS {
        String ACTIVE = "1"; //hoạt động
        String INACTIVE = "0"; // không hoạt động
    }

    public interface BOO_STATUS {
        String NOT_REGISTRY = "NAN";
        String ACTIVE = "ACT";
        String DESTROY = "DES";
        String SUCCESS = "SUCCESS";
        String RECEIVED = "RECEIVED";
        String REJECT = "REJECT";
        String RESPONSE_CODE_SUCCESS = "0";
    }

    public interface BOT_CONFIRM {
        Long YES = 1L;
        Long NO = 0L;
    }

    public interface SERVICE_PLAN_STATUS {
        Long DELETED = 0L;
    }

    public interface REASON_CHANGE_BOO {
        String CHANGE_PLATE_NUMBER = "DBS";
        String CHANGE_EPC = "DET";
        String CHANGE_INFO_REGISTER = "TDK";
        String OPEN_OR_CLOSE_EPC = "DME";
        String CHANGE_OTHER_INFO = "TKH";
        String CANCEL_EPC = "HTK";
        String INSERT_EXCEPTION = "I";
        String UPDATE_EXCEPTION = "U";
    }

    public interface VIETTEL_PAY_EXCEPTION_FIELD {
        List<String> EXCEPTION_FIELD = Arrays.asList("message", "autoRenew_VTP");
    }

    public interface VIETTEL_PAY_TYPE_REGISTER {
        String CANCEL = "0";
        String REGISTER = "1";
    }

    public interface BOO_ERROR_CODE {
        String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
        String INVALID_PARAMS = "INVALID_PARAMS";
        String SUCCESS = "0";
        String FAIL = "1";
    }

    public interface VALID_TYPE_ONLINE_REG {
        List<String> TYPE = Arrays.asList("LX", "GV", "BL", "WL");
        List<String> ACTION_TYPE = Arrays.asList("I", "U");
        String VEHICLE_TYPE = "LX";
        String SERVICE_PLAN = "GV";
        String BLACK_LIST = "BL";
        String WHITE_lIST = "WL";
    }

    public enum RFIDStatus {
        NEW(0),
        ACTIVE(1),
        DESTROY(2),
        CLOSE(3),
        OPEN(4),
        TRANSFER(5);
        public final int code;

        RFIDStatus(int code) {
            this.code = code;
        }
    }

    public static final Long BOO2ChargeBOO2 = 1L;
    public static final Long BOO2ChargeBOO1 = 2L;
    public static final Long BOO1ChargeBOO2 = 3L;

    public interface MOMO_MESSAGE {
        String PARTNER_REF_ID = "partnerRefId";
        String PARTNER_CODE = "partnerCode";
        String AMOUNT = "amount";
        String CONFIRM_APP_TRANSACTION = "capture";
        String CANCEL_APP_TRANSACTION = "revertAuthorize";
        String REQUEST_WEB_TYPE = "captureMoMoWallet";
    }

    public interface BOT_REFUND {
        Long YES = 1L;
        Long NO = 0L;
    }

    public interface AUTO_RENEW {
        String YES = "1";
        String NO = "0";
    }

    public enum BooEtagStatus {
        OPEN(1),
        CLOSE(2),
        DESTROY(0);
        public final int code;

        BooEtagStatus(int code) {
            this.code = code;
        }
    }

    public interface VIETTEL_PAY_SERVICE {
        String COMMAND = "PAYMENT";
        String CMD = "TRANS_INQUIRY";
    }

    public interface EXCEPTION_LIST_TYPE {
        String LX = "1";
        String GV = "2";
        String WL = "3";
        String BL = "4";
    }

    public interface USER_ATTRIBUTE {
        String SHOP_ID = "shop_id";
        String SHOP_NAME = "shop_name";
        String SHOP_CODE = "shop_code";
        String STAFF_ID = "staff_id";
        String PARTNER_CODE = "partner_code";
        String PARTNER_TYPE = "partner_type";
        String WAREHOUSE_ID = "warehouse_id";
    }

    public interface IM_VDTC_STATUS {
        String SUCCESS = "1";
        String FAILED = "0";
    }

    public interface IM_VTT_VDTC_CODE {
        //RfidType WINDSHIELD dan kinh
        String ETC_FREE_WINDSHIELD = "ETC_FREE";
        String ETC_FEE_120_WINDSHIELD = "ETC_FEE_120";

        //RfidType LAMP dan den
        String ETC_FREE_LAMP = "ETC_FREE_LAMP";
        String ETC_FEE_120_LAMP = "ETC_FEE_120_LAMP";
    }

    public interface STICKY_TYPE {
        Long SHOP = 0L;
        Long HOME = 1L;
    }

    public interface SUPERAPP_CONSTANT {
        Long CUST_TYPE_PERSONAL = 1L;
        Long CUST_TYPE_ORGANIZATION = 2L;
        String ACT_TYPE_CODE = "SUPERAPP_REG";
        String DOCUMENT_TYPE_CODE = "CMND";
    }

    public interface CASE_UPDATE_VEHICLE {
        int REGISTER_VEHICLE = 1;
        int ASSIGN_VEHICLE = 2;
        int SWAP_VEHICLE = 3;
    }

    public interface APP_CLIENT_ID {
        String APP_CHU_PT = "mobile-app-chupt";
        String PORTAL_CHU_PT = "portal-chu-pt";
        String APP_CTV_DAI_LY = "mobile-app-ctv-daily";
        String BOO1 = "boo1";
        String CMS = "cms";
        String VT_PAY = "viettelpay";
        String MOMO = "momo";
    }

    public interface MOMO_APP_ERROR_CODE {
        String SUCCESS = "0";
        String PAYMENT_FAILED = "1";
        String PEYMENT_YET = "2";
        String TRANS_IS_NOT_FOUND = "3";
        String AMOUNT_WRONG = "4";
        String INPUT_INVALID = "5";
        String CONTRACT_ID_IS_NOT_FOUND = "8";
        String CONTRACT_ID_IS_BLOCKED = "9";
        String SERVICE_ERROR = "12";
        String REQUEST_TIME_INVALID = "15";
    }

    public interface KEYCLOAK {
        String VTT = "VTT";
        String VDTC = "VDTC";
        String PW_DEFAULT = "123456a@";
    }

    public interface MOMO_PORTAL_CPT_ERROR_CODE {
        int SUCCESS = 0;
        int ORDER_ID_ERROR = 1;
        int SAVE_TOPUP_ERROR = 2;
        int ADD_BALANCE_OCS_ERROR = 3;
        int QUERY_CONTRACT_ERROR = 4;
        int MOMO_RESPONSE_ERROR = 5;
    }

    public interface ROLE_CRM {
        String ROLE_ADMIN_CRM = "Role_Admin_CRM";
        String ROLE_QUANLY_TRACUU_DAUNOI = "Role_QuanLyTraCuu_DauNoi";
    }
}
