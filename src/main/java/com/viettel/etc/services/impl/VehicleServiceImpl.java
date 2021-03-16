package com.viettel.etc.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.ReqActivationCheckDTO;
import com.viettel.etc.dto.boo.ReqOnlineEventSyncDTO;
import com.viettel.etc.dto.boo.ResActivationCheckDTO;
import com.viettel.etc.dto.dmdc.VehicleGroupDTO;
import com.viettel.etc.dto.im.StockEtagDTO;
import com.viettel.etc.dto.im.StockTransEtagDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.RemoveSupOfferRoaming;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.criteria.*;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.viettel.etc.utils.Constants.*;

/**
 * Autogen class: phuong tien
 *
 * @author ToolGen
 * @date Wed Jun 24 14:38:40 ICT 2020
 */
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {
    private final static Logger LOGGER = LoggerFactory.getLogger(VehicleServiceImpl.class);

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    OCSService ocsService;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    VehicleProfileRepositoryJPA vehicleProfileRepositoryJPA;

    @Autowired
    ProductService productService;

    @Autowired
    ContractRepositoryJPA contractRepositoryJPA;

    @Autowired
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Autowired
    VehicleTypeService vehicleTypeService;

    @Autowired
    VehicleGroupService vehicleGroupService;

    @Autowired
    ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @Autowired
    SaleTransRepositoryJPA saleTransRepositoryJPA;

    @Autowired
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Autowired
    SearchXCGService searchXCGService;

    @Autowired
    FileService fileService;

    @Autowired
    ExceptionListRepositoryJPA exceptionListRepositoryJPA;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    ActionAuditService actionAuditService;

    @Autowired
    SaleTransService saleTransService;

    @Autowired
    ServiceFeeService serviceFeeService;

    @Autowired
    CategoriesService categoriesService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    StageService stageService;

    @Autowired
    StationService stationService;

    @Autowired
    ContractProfileRepositoryJPA contractProfileRepositoryJPA;

    @Autowired
    ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Autowired
    ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Autowired
    SaleTransDetailService saleTransDetailService;

    @Autowired
    SaleTransDetailServiceJPA saleTransDetailServiceJPA;

    @Autowired
    SaleTransServiceJPA saleTransServiceJPA;

    @Autowired
    Boo1ServiceImpl boo1Service;

    @Autowired
    ExceptionListServiceJPA exceptionListServiceJPA;

    @Autowired
    ServiceRegistryService serviceRegistryService;

    @Autowired
    JedisCacheService jedisCacheService;

    @Autowired
    private IMService imService;

    @Value("${ws.ocs.vehicle-offer-id}")
    private String vehiclesOfferId;

    @Value("${ws.ocs.vehicle-offer-code}")
    private String vehiclesOfferCode;

    @Value("${ws.dmdc.vehicle-groups}")
    private String wsVehicelGroups;

    @Value("${crm.common.max-file-size}")
    private String maxFileSize;

    @Value("${crm.common.max-file-size}")
    private Integer briefcaseMaxFileSize;

    @Value("${ws.dmdc.table-categories}")
    private String tableNameCategories;

    @Value("${ws.register.strTagID}")
    private String EPC;

    @Value("${boo1.is_check}")
    private boolean isCheckBoo1;

    @Autowired
    private SMSServiceImpl smsService;

    @Value("${sms.user.register}")
    private String smsRegisterUser;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    DocxService docxService;

    @Autowired
    private LuckyService luckyService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private TopupService topupService;


    /***
     * Dang ky phuong tien
     * @param addVehicleRequestDTO
     * @param authentication
     * @param customerId
     * @param contractId
     * @throws JAXBException
     * @throws IOException
     * @throws XMLStreamException
     * @throws DataNotFoundException
     * @throws EtcException
     */
    @Transactional(noRollbackFor = EtcException.class)
    public Object registerVehicle(AddVehicleRequestDTO addVehicleRequestDTO, Authentication authentication, Long customerId, Long contractId, String remoteAddr) throws JAXBException, IOException, XMLStreamException, DataNotFoundException, EtcException {
        validateRegisterVehicle(addVehicleRequestDTO);
        if (checkVehicleRegisterExist(addVehicleRequestDTO.getPlateNumber().trim().toUpperCase())) {
            throw new EtcException("crm.vehicle.exist");
        }
        List<VehicleEntity> listVehicleOld = vehicleRepositoryJPA.findAllByContractIdAndStatusAndActiveStatus(contractId);
        String statusVehicleBoo1 = checkVehicleActivationBoo1(addVehicleRequestDTO.getPlateNumber(), authentication, addVehicleRequestDTO.getPlateTypeCode(), addVehicleRequestDTO.getActTypeId());
        roundWeightVehicle(addVehicleRequestDTO);
        VehicleEntity vehicleEntity = saveVehicle(addVehicleRequestDTO, authentication, customerId, contractId);

        /***
         * * Check User có partner_code khong
         *          * Neu co thi goi sang IM VDTC
         *          * Neu khong co thi chay luong cu goi IM VTT
         */
        StockTransEtagDTO stockTransEtagDTO = checkAndVerifySerialIMVDTC(addVehicleRequestDTO.getRfidSerial(), authentication, vehicleEntity, CASE_UPDATE_VEHICLE.REGISTER_VEHICLE);
        // luong chay sang IM VTT nhu bt
        if (stockTransEtagDTO == null) {
            SaleBusinessMesDTO saleBusinessMesDTO = productService.callBCCSIM(authentication, addVehicleRequestDTO.getActTypeId(), vehicleEntity, addVehicleRequestDTO.getRfidSerial(), customerId, CASE_UPDATE_VEHICLE.REGISTER_VEHICLE, statusVehicleBoo1);
            if (saleBusinessMesDTO.getSuccessResult() == null || !"true".equals(saleBusinessMesDTO.getSuccessResult())) {
                vehicleRepositoryJPA.delete(vehicleEntity);
                throw new EtcException("crm.register.vehicle.im");
            }
        } else {
            updateSerialIMVDTC(stockTransEtagDTO, IM_VDTC_STATUS.SUCCESS, authentication);
        }

        setSerialToVehicle(addVehicleRequestDTO, contractId, vehicleEntity);

        // dinh kem giay gioi thieu vao vehicle
        if (!FnCommon.isNullOrEmpty(addVehicleRequestDTO.getFileNameGiayDeNghi())) {
            FileReportDTO fileReportDTO = new FileReportDTO();
            fileReportDTO.setFileName(addVehicleRequestDTO.getFileNameGiayDeNghi() + "_signed.pdf");
            fileReportDTO.setContractNo(addVehicleRequestDTO.getContractNo());
            fileReportDTO.setVehicleId(vehicleEntity.getVehicleId());
            docxService.saveReportFiles(fileReportDTO, authentication);
        }
        OCSResponse res = ocsService.createVehicleOCS(addVehicleRequestDTO, authentication, addVehicleRequestDTO.getActTypeId());
        if (!FnCommon.checkOcsCode(res)) {
            setStatusCallImSuccess(vehicleEntity);
            throw new EtcException("ocs.register.vehicle");
        }

        addVehicleRequestDTO.setVehicleId(vehicleEntity.getVehicleId());
        if ("ETC_VIETTELPOST".equals(FnCommon.getUserLogin(authentication))) {
            serviceRegistryService.confirmOrder(addVehicleRequestDTO, remoteAddr);
        }
        ContractEntity contractEntity = contractServiceJPA.getOne(vehicleEntity.getContractId());
        String contractNo = contractEntity == null ? null : contractEntity.getContractNo();
        Long price = 0L;
        List<VehicleEntity> vehicleList = vehicleServiceJPA.getByPlateNumberAndPlateTypeCodeAndStatusNot(addVehicleRequestDTO.getPlateNumber(), addVehicleRequestDTO.getPlateTypeCode(), VehicleEntity.Status.ACTIVATED.value);
        if (Calendar.getInstance().get(Calendar.YEAR) > 2021 || Constants.BOO_STATUS.DESTROY.equals(statusVehicleBoo1) || !FnCommon.isNullOrEmpty(vehicleList)) {
            ServiceFeeDTO serviceFee = (ServiceFeeDTO) serviceFeeService.getDetailServiceFeeByReason(Constants.ACT_REASON.ACTIVE_RFID);
            price = serviceFee.getFee();
        }
        addSaleTransAndSaleTransDetailsAddVehicle(vehicleEntity.getCustId(), vehicleEntity.getContractId(), contractNo, vehicleEntity.getVehicleId(), authentication, price);
        AddVehicleRequestDTO result = new AddVehicleRequestDTO();
        result.setVehicleId(vehicleEntity.getVehicleId());
        // send email and sms
        if (listVehicleOld.isEmpty()) {
            sendSmsAndEmail(contractId, authentication);
        }
        return result;
    }

    @Override
    @Transactional(noRollbackFor = EtcException.class)
    public Object registerVehicleQuick(AddVehicleQuickDTO req, Authentication authentication, String remoteAddr) throws Exception {
        String plate = FnCommon.formatPlateBOO1(req.getPlateNumber().trim().toUpperCase());
        if (checkVehicleRegisterExist(plate)) {
            throw new EtcException("crm.vehicle.exist");
        }
        String statusVehicleBoo1 = checkVehicleActivationBoo1(req.getPlateNumber(), authentication, ACT_TYPE.ADD_VEHICLE);

        if (FnCommon.isNullOrEmpty(req.getOwner())) {
            req.setOwner(req.getPhoneNumber());
        }

        if (FnCommon.isNullOrEmpty(req.getAddress())) {
            req.setAddress(req.getPhoneNumber());
        }

        /***
         * Tao request tao phuong tien
         */
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setRfidSerial(req.getSerial());
        addVehicleRequestDTO.setNetWeight(req.getNetWeight());
        addVehicleRequestDTO.setCargoWeight(req.getCargoWeight());
        addVehicleRequestDTO.setGrossWeight(req.getGrossWeight());
        addVehicleRequestDTO.setSeatNumber(req.getSeatNumber());
        addVehicleRequestDTO.setVehicleTypeId(req.getVehicleTypeId());
        addVehicleRequestDTO.setVehicleGroupId(req.getVehicleGroupId());
        addVehicleRequestDTO.setPullingWeight(req.getPullingWeight());
        addVehicleRequestDTO.setEngineNumber(req.getEngineNumber());
        addVehicleRequestDTO.setChassicNumber(req.getChassicNumber());
        addVehicleRequestDTO.setFileNameGiayDeNghi(req.getFileNameGiayDeNghi());
        addVehicleRequestDTO.setPlateNumber(plate);
        addVehicleRequestDTO.setActTypeId(ACT_TYPE.ADD_VEHICLE);
        addVehicleRequestDTO.setPlateTypeCode(FnCommon.getPlateTypeCodeFromPlateNumber(req.getPlateNumber()));
        addVehicleRequestDTO.setOwner(req.getOwner());
        addVehicleRequestDTO.setVehicleGroupCode(req.getVehicleGroupCode());
        roundWeightVehicle(addVehicleRequestDTO);

        /***
         * Tao khach hang ao
         */
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        java.sql.Date fixDate = new java.sql.Date(new java.util.Date(100, Calendar.JANUARY, 1).getTime());
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustTypeId(1L);
        customerDTO.setCustName(req.getOwner());
        customerDTO.setPhoneNumber(req.getPhoneNumber());
        customerDTO.setAreaName(req.getAddress());
        customerDTO.setCreateDate(currDate);
        customerDTO.setBirthDate(fixDate);
        customerDTO.setDocumentTypeId(1L);
        CustomerEntity customerEntity = customerService.addCustomerQuick(customerDTO, authentication);

        /***
         * Tao hop dong ao
         */
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setActTypeId(ACT_TYPE.ADD_CONTRACT);
        contractDTO.setSignDate(currDate);
        contractDTO.setSignName(req.getOwner());
        contractDTO.setEffDate(currDate);
        contractDTO.setEmailNotification("0");
        contractDTO.setSmsNotification("0");
        contractDTO.setPushNotification("0");
        contractDTO.setBillCycle("1");
        contractDTO.setPayCharge("1");
        contractDTO.setNoticeName(req.getOwner());
        contractDTO.setNoticeAreaName(req.getAddress());
        contractDTO.setNoticePhoneNumber(req.getPhoneNumber());
        contractDTO.setProfileStatus("1");
        String passwordApp = RandomStringUtils.randomNumeric(6);
        ContractEntity contractEntity = contractService.addContractQuick(contractDTO, authentication, customerEntity, passwordApp);

        VehicleEntity vehicleEntity = saveVehicle(addVehicleRequestDTO, authentication, customerEntity.getCustId(), contractEntity.getContractId());
        vehicleEntity.setVehicleTypeCode(req.getVehicleTypeCode());
        vehicleEntity.setNote("QUICK_SALE");

        /***
         * * Check User có partner_code khong
         *          * Neu co thi goi sang IM VDTC
         *          * Neu khong co thi chay luong cu goi IM VTT
         */
        StockTransEtagDTO stockTransEtagDTO = checkAndVerifySerialIMVDTC(addVehicleRequestDTO.getRfidSerial(), authentication, vehicleEntity, CASE_UPDATE_VEHICLE.REGISTER_VEHICLE);
        // luong chay sang IM VTT nhu bt
        if (stockTransEtagDTO == null) {
            SaleBusinessMesDTO saleBusinessMesDTO = productService.callBCCSIM(authentication, addVehicleRequestDTO.getActTypeId(), vehicleEntity, addVehicleRequestDTO.getRfidSerial(), customerEntity.getCustId(), CASE_UPDATE_VEHICLE.REGISTER_VEHICLE, statusVehicleBoo1);
            if (saleBusinessMesDTO.getSuccessResult() == null || !"true".equals(saleBusinessMesDTO.getSuccessResult())) {
                vehicleRepositoryJPA.delete(vehicleEntity);
                throw new EtcException("crm.register.vehicle.im");
            }
        } else {
            updateSerialIMVDTC(stockTransEtagDTO, IM_VDTC_STATUS.SUCCESS, authentication);
        }

        setSerialToVehicle(addVehicleRequestDTO, contractEntity.getContractId(), vehicleEntity);


        // dinh kem giay gioi thieu vao vehicle
        if (!FnCommon.isNullOrEmpty(addVehicleRequestDTO.getFileNameGiayDeNghi())) {
            FileReportDTO fileReportDTO = new FileReportDTO();
            fileReportDTO.setFileName(addVehicleRequestDTO.getFileNameGiayDeNghi() + "_signed.pdf");
            fileReportDTO.setContractNo(addVehicleRequestDTO.getContractNo());
            fileReportDTO.setVehicleId(vehicleEntity.getVehicleId());
            docxService.saveReportFiles(fileReportDTO, authentication);
        }
        OCSResponse res = ocsService.createVehicleOCS(addVehicleRequestDTO, authentication, ACT_TYPE.ADD_VEHICLE);

        CategoryDTO.Categories dataCategories = categoriesService.findCategoriesListIdByCode(FnCommon.getStringToken(authentication), Constants.PLATE_TYPE, addVehicleRequestDTO.getPlateTypeCode());
        if(dataCategories != null && !dataCategories.getListData().isEmpty()){
            vehicleEntity.setPlateType(dataCategories.getListData().get(0).getId());
        }

        if (!FnCommon.checkOcsCode(res)) {
            setStatusCallImSuccess(vehicleEntity);
            throw new EtcException(0, "ocs.register.vehicle");
        }

        Long price = 0L;
        List<VehicleEntity> vehicleList = vehicleServiceJPA.getByPlateNumberAndPlateTypeCodeAndStatusNot(req.getPlateNumber(), addVehicleRequestDTO.getPlateTypeCode(), VehicleEntity.Status.ACTIVATED.value);
        if (Calendar.getInstance().get(Calendar.YEAR) > 2021 || Constants.BOO_STATUS.DESTROY.equals(statusVehicleBoo1) || !FnCommon.isNullOrEmpty(vehicleList)) {
            ServiceFeeDTO serviceFee = (ServiceFeeDTO) serviceFeeService.getDetailServiceFeeByReason(Constants.ACT_REASON.ACTIVE_RFID);
            price = serviceFee.getFee();
        }
        addSaleTransAndSaleTransDetailsAddVehicle(vehicleEntity.getCustId(), vehicleEntity.getContractId(), contractEntity.getContractNo(), vehicleEntity.getVehicleId(), authentication, price);
        // send sms for user
        luckyService.genLuckyCode(contractEntity, LUCKY_CODE.CUSTNEW, authentication);
        String contentSMS = String.format(smsRegisterUser, contractEntity.getContractNo(), passwordApp);
        smsService.sendSMS(contractEntity.getNoticePhoneNumber(), contentSMS, authentication);

        try {
            // check xem co nap tien hay khong
            if (req.getAmount() != null && req.getAmount() > 0) {
                TopupTransferDTO topupTransferDTO = new TopupTransferDTO();
                topupTransferDTO.setContractId(contractEntity.getContractId());
                topupTransferDTO.setTopupAmount(req.getAmount());
                topupService.transferMoney(authentication, topupTransferDTO);
            }
        } catch (Exception e) {
            LOGGER.error("Nap tien quick error ", e);
            throw new EtcException("crm.register.vehicle.quick.err-topup");
        }

        return vehicleEntity.getVehicleId();
    }


    /***
     * Luu phuong tien vao database
     * @param dataParams
     * @param authentication
     * @param customerId
     * @param contractId
     * @return
     * @throws IOException
     * @throws DataNotFoundException
     * @throws EtcException
     */
    public VehicleEntity saveVehicle(AddVehicleRequestDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws IOException, DataNotFoundException, EtcException {
        Date currDate = new Date(System.currentTimeMillis());
        String userLogin = FnCommon.getUserLogin(authentication);
        long reasonId = getReasonIdByactTypeId(dataParams.getActTypeId());

        if (validateRfidSerial(dataParams.getRfidSerial())) {
            throw new EtcException("crm.serial.exist.crm");
        }

        /***
         * Goi sang ben IM VDTC de lay chi tiet serial - 29112020
         */
        StockEtagDTO stockEtagDTO = imService.getSerialDetails(dataParams.getRfidSerial(), authentication);
        // END Goi sang ben IM VDTC de lay chi tiet serial

        Map<String, Object> map = FnCommon.getAttribute(authentication);
        Long shopId = Long.parseLong(map.get(USER_ATTRIBUTE.SHOP_ID).toString());
        Long vehicleGroupId = dataParams.getVehicleGroupId();
        String vehicleGroupCode = dataParams.getVehicleGroupCode();
        dataParams.setEpc(stockEtagDTO.getEpc());
        dataParams.setTid(stockEtagDTO.getTid());
        VehicleEntity vehicleEntity = dataParams.toAddVehicleEntity(customerId, contractId, userLogin, stockEtagDTO);
        vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
        vehicleEntity.setProfileStatus(VehicleEntity.ProfilesStatus.NOT_RECEIVED.value);
        vehicleEntity.setRfidType(String.valueOf(stockEtagDTO.getEtagTypeId()));
        vehicleEntity.setOfferId(Long.parseLong(vehiclesOfferId));
        vehicleEntity.setOfferCode(vehiclesOfferCode);
        vehicleEntity.setShopId(shopId);
        vehicleEntity.setVehicleGroupId(vehicleGroupId);
        vehicleEntity.setVehicleGroupCode(vehicleGroupCode);
        VehicleEntity save = vehicleRepositoryJPA.save(vehicleEntity);
        ActionAuditEntity actionAuditEntity = writeLogNew(authentication, reasonId, dataParams.getActTypeId(), contractId, customerId, save.getVehicleId(), save, VehicleEntity.class.getAnnotation(Table.class).name());
        addNewVehicleProfile(authentication, currDate, userLogin, contractId, save.getVehicleId(), dataParams.getVehicleProfileDTOs(), reasonId, dataParams.getActTypeId(), customerId, actionAuditEntity.getActionAuditId(), vehicleEntity);
        dataParams.setVehicleGroupId(vehicleGroupId);
        return save;
    }

    /**
     * them chung tu di kem phuong tien
     *
     * @param currDate
     * @param authentication
     * @param contractId
     * @param vehicleId
     * @param actTypeId
     * @param reasonId
     * @param dataParams
     * @throws Exception
     */
    @Override
    public void updateProfile(Date currDate, Authentication authentication, Long contractId, Long vehicleId, Long actTypeId, Long reasonId, List<VehicleProfileDTO> dataParams) throws Exception {
        if (CollectionUtils.isNotEmpty(dataParams)) {
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, null, contractId, vehicleId));
            updateProfile(currDate, authentication, contractId, vehicleId, actionAuditEntity, dataParams);
        }
    }

    /**
     * Luu vehicle profiles
     *
     * @param currDate
     * @param authentication
     * @param contractId
     * @param vehicleId
     * @param actionAuditEntity
     * @param dataParams
     * @throws IOException
     * @throws IllegalAccessException
     * @throws JSONException
     */
    @Override
    public void updateProfile(Date currDate, Authentication authentication, Long contractId, Long vehicleId, ActionAuditEntity actionAuditEntity, List<VehicleProfileDTO> dataParams) throws IllegalAccessException, JSONException {
        List<VehicleProfileEntity> vehicleProfileEntities = new ArrayList<>();
        String userLogin = FnCommon.getUserLogin(authentication);
        Optional<ContractEntity> optionalContractEntity = this.contractServiceJPA.findById(contractId);
        if (!optionalContractEntity.isPresent()) {
            throw new EtcException("crm.contract.not.exist");
        }
        ContractEntity contractEntity = optionalContractEntity.get();
        if (dataParams != null) {
            for (VehicleProfileDTO vehicleProfile : dataParams) {
                VehicleProfileEntity vehicleProfileEntity = new VehicleProfileEntity();
                if (!Base64.isBase64(vehicleProfile.getFileBase64())) {
                    throw new EtcException("crm.file.is.not.base64");
                }
                byte[] file = Base64.decodeBase64(vehicleProfile.getFileBase64());
                String filePath = String.format("/%s/%s/%s/%s", contractEntity.getCustId(), contractId, vehicleId, UUID.randomUUID().toString() + "-" + vehicleProfile.getFileName());
                if (!FnCommon.checkBriefcaseValid(filePath, file, briefcaseMaxFileSize)) {
                    throw new EtcException("crm.briefcase.not.accepted");
                }
                vehicleProfileEntity.setContractId(contractId);
                vehicleProfileEntity.setVehicleId(vehicleId);
                vehicleProfileEntity.setDocumentTypeId(vehicleProfile.getDocumentTypeId());
                vehicleProfileEntity.setFileName(vehicleProfile.getFileName());
                vehicleProfileEntity.setFilePath(filePath);
                vehicleProfileEntity.setFileSize(file);
                vehicleProfileEntity.setCreateDate(currDate);
                vehicleProfileEntity.setCreateUser(userLogin);

                vehicleProfileEntity.setStatus(VehicleProfileEntity.Status.EXIST.value);
                vehicleProfileEntities.add(vehicleProfileEntity);
                fileService.uploadFile(filePath, file);

                // khong luu base64 vao log
                vehicleProfileEntity.setFileSize(null);
                ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleProfileEntity).toEntity(actionAuditEntity.getActionAuditId(), VehicleProfileEntity.class.getAnnotation(Table.class).name(), vehicleProfileEntity.getContractId());
                actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
            }
            vehicleProfileRepositoryJPA.saveAll(vehicleProfileEntities);
        }
    }

    /***
     * Update phương tiện
     * @param modifyVehicleDTO
     * @param vehicleEntity
     * @return
     */
    public VehicleEntity updateVehicle(ModifyVehicleDTO modifyVehicleDTO, VehicleEntity vehicleEntity,
                                       Authentication authentication) {
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO();
        if (!FnCommon.isNullOrEmpty(modifyVehicleDTO.getOwner())) {
            vehicleEntity.setOwner(modifyVehicleDTO.getOwner());
        }
        if (Objects.nonNull(modifyVehicleDTO.getGrossWeight())) {
            vehicleEntity.setGrossWeight(modifyVehicleDTO.getGrossWeight());
        }
        if (Objects.nonNull(modifyVehicleDTO.getPullingWeight())) {
            vehicleEntity.setPullingWeight(modifyVehicleDTO.getPullingWeight());
        }
        if (!FnCommon.isNullOrEmpty(modifyVehicleDTO.getChassicNumber())) {
            vehicleEntity.setChassicNumber(modifyVehicleDTO.getChassicNumber());
        }

        if (!FnCommon.isNullOrEmpty(modifyVehicleDTO.getEngineNumber())) {
            vehicleEntity.setEngineNumber(modifyVehicleDTO.getEngineNumber());
        }

        if (Objects.nonNull(modifyVehicleDTO.getSeatNumber())) {
            updateVehicleRequestOCSDTO.setSeatNumber(modifyVehicleDTO.getSeatNumber());
            vehicleEntity.setSeatNumber(modifyVehicleDTO.getSeatNumber());
        }

        if (Objects.nonNull(modifyVehicleDTO.getVehicleColourId())) {
            vehicleEntity.setVehicleColourId(modifyVehicleDTO.getVehicleColourId());
        }

        if (Objects.nonNull(modifyVehicleDTO.getVehicleMarkId())) {
            vehicleEntity.setVehicleMarkId(modifyVehicleDTO.getVehicleMarkId());
        }
        if (Objects.nonNull(modifyVehicleDTO.getVehicleBrandId())) {
            vehicleEntity.setVehicleBrandId(modifyVehicleDTO.getVehicleBrandId());
        }
        if (Objects.nonNull(modifyVehicleDTO.getPlateType())) {
            vehicleEntity.setPlateType(modifyVehicleDTO.getPlateType());
            vehicleEntity.setPlateTypeCode(modifyVehicleDTO.getPlateTypeCode());
            updateVehicleRequestOCSDTO.setPlateTypeId(modifyVehicleDTO.getPlateType());
        }

        if (Objects.nonNull(modifyVehicleDTO.getPlateType())) {
            updateVehicleRequestOCSDTO.setPlateTypeCode(modifyVehicleDTO.getPlateTypeCode());
        }
        /***
         * Bo sung truong duoc phep thay doi o giai doan 2
         */
        if (Objects.nonNull(modifyVehicleDTO.getNetWeight())) {
            vehicleEntity.setNetWeight(modifyVehicleDTO.getNetWeight());
        }
        if (Objects.nonNull(modifyVehicleDTO.getCargoWeight())) {
            vehicleEntity.setCargoWeight(modifyVehicleDTO.getCargoWeight());
            Long payloads = Math.round(modifyVehicleDTO.getCargoWeight());
            updateVehicleRequestOCSDTO.setPayloads(payloads.toString());
        }
        if (Objects.nonNull(modifyVehicleDTO.getVehicleTypeId())) {
            vehicleEntity.setVehicleTypeId(modifyVehicleDTO.getVehicleTypeId());
            vehicleEntity.setVehicleTypeCode(modifyVehicleDTO.getVehicleTypeCode());
            vehicleEntity.setVehicleTypeName(modifyVehicleDTO.getVehicleTypeName());
            updateVehicleRequestOCSDTO.setVehicleType(modifyVehicleDTO.getVehicleTypeId());
        }
        if (Objects.nonNull(modifyVehicleDTO.getVehicleGroupId())) {
            updateVehicleRequestOCSDTO.setVehicleGroupId(modifyVehicleDTO.getVehicleGroupId());
            vehicleEntity.setVehicleGroupCode(modifyVehicleDTO.getVehicleGroupCode());
            vehicleEntity.setVehicleGroupId(modifyVehicleDTO.getVehicleGroupId());

        }
        updateVehicleRequestOCSDTO.setActTypeId(modifyVehicleDTO.getActTypeId());
        updateVehicleRequestOCSDTO.setEpc(vehicleEntity.getEpc());
        OCSResponse ocsResponse = ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, authentication, false);
        if (ocsResponse.getDescription() == null || !"Success".equals(ocsResponse.getDescription())) {
            throw new EtcException("ocs.modify.vehicle.error");
        }
        vehicleRepositoryJPA.save(vehicleEntity);
        return vehicleEntity;
    }

    /***
     * Gan the cho phuong tien
     * @param listVehicleAssignRfidDTO
     * @param authentication
     * @param custId
     * @param contractId
     * @return
     * @throws DataNotFoundException
     */
    @Override
    public ResultAssignRfidDTO vehicleAssign(ListVehicleAssignRfidDTO listVehicleAssignRfidDTO, Authentication authentication, Long custId, Long contractId) throws DataNotFoundException {
        ResultAssignRfidDTO resultAssignRfidDTO = new ResultAssignRfidDTO();
        List<AssignRfidDTO> list = new ArrayList<>();
        if (listVehicleAssignRfidDTO.getStartRfidSerial() == null) {
            throw new DataNotFoundException("common.validate.serial.empty");
        }
        Long rfidSerial = Long.parseLong(listVehicleAssignRfidDTO.getStartRfidSerial());
        for (AddVehicleRequestDTO requestDTO : listVehicleAssignRfidDTO.getAddVehicleRequestDTOS()) {
            AssignRfidDTO assignRfidDTO = new AssignRfidDTO();
            assignRfidDTO.setRfidSerial(rfidSerial.toString());
            assignRfidDTO.setVehicleId(requestDTO.getVehicleId());
            assignRfidDTO.setDescriptions("FAIL");
            try {
                if (vehicleAssign(requestDTO, authentication, custId, contractId, rfidSerial, listVehicleAssignRfidDTO.getActTypeId())) {
                    assignRfidDTO.setDescriptions("SUCCESS");
                }
            } catch (Exception ex) {
                LOGGER.error("Gan the cho phuong tien bi loi " + requestDTO.getPlateNumber(), ex);
            } finally {
                list.add(assignRfidDTO);
                resultAssignRfidDTO.setList(list);
                rfidSerial = rfidSerial + 1;
            }
        }
        return resultAssignRfidDTO;
    }

    /***
     * Xuat danh sách xe gan the
     * @param contractId
     * @param authentication
     * @return
     * @throws IOException
     */
    @Override
    public String exportVehiclesAssigned(Long contractId, Authentication authentication) throws IOException {
        List<ExportVehiclesAssignedDTO> listDataExport = vehicleRepository.exportVehiclesAssigned(contractId);
        List<Long> vehicleTypeIdList = new ArrayList<>();
        List<Long> vehicleGroupIdList = new ArrayList<>();
        for (ExportVehiclesAssignedDTO object : listDataExport) {
            vehicleTypeIdList.add(object.getVehicleTypeId());
            vehicleGroupIdList.add(object.getVehicleGroupId());
        }
        // set value from categories
        for (ExportVehiclesAssignedDTO object : listDataExport) {
            Map<String, Map<String, String>> categoryMap = new HashMap<>();
            setValueCategoryMap(authentication, categoryMap);
            if (Objects.nonNull(object.getVehicleColourId())) {
                String vehicleColourKey = buildCategoryKeyByCode("VEHICLE_COLOUR", object.getVehicleColourId().toString());
                if (categoryMap.containsKey(vehicleColourKey)) {
                    object.setColor(categoryMap.get(vehicleColourKey).get("name"));
                }
            }
            if (Objects.nonNull(object.getVehicleMarkId())) {
                String vehicleMarkKey = buildCategoryKeyByCode("VEHICLE_MARK", object.getVehicleMarkId().toString());
                if (categoryMap.containsKey(vehicleMarkKey)) {
                    object.setMark(categoryMap.get(vehicleMarkKey).get("name"));
                }
            }
            if (Objects.nonNull(object.getVehicleBrandId())) {
                String vehicleBrandKey = buildCategoryKeyByCode("VEHICLE_BRAND", object.getVehicleBrandId().toString());
                if (categoryMap.containsKey(vehicleBrandKey)) {
                    object.setBrand(categoryMap.get(vehicleBrandKey).get("name"));
                }
            }
            // set vehicle type name
            List<VehicleTypeDTO.VehicleType> dataVehicleTypes = vehicleTypeService.findVehicleTypeByListId(FnCommon.getStringToken(authentication), vehicleTypeIdList);
            Map<Long, String> vehicleTypeMap = dataVehicleTypes.stream().collect(Collectors.toMap(VehicleTypeDTO.VehicleType::getId, VehicleTypeDTO.VehicleType::getName));
            if (Objects.nonNull(object.getVehicleTypeId())) {
                object.setNameType(vehicleTypeMap.get(object.getVehicleTypeId()));
            }
            //set group name from group id
            List<com.viettel.etc.dto.VehicleGroupDTO.VehicleGroup> dataVehicleGroups = vehicleGroupService.findVehicleGroupByListId(FnCommon.getStringToken(authentication), vehicleGroupIdList);
            Map<Long, String> vehicleGroupMap = dataVehicleGroups.stream().collect(Collectors.toMap(com.viettel.etc.dto.VehicleGroupDTO.VehicleGroup::getId, com.viettel.etc.dto.VehicleGroupDTO.VehicleGroup::getName));
            if (Objects.nonNull(object.getVehicleGroupId())) {
                object.setNameGroup(vehicleGroupMap.get(object.getVehicleGroupId()));
            }
        }
        // set value to file excel
        readAndWritingFileExcel(listDataExport);
        return System.getProperty("user.dir") + File.separator + "workbook.xlsx";
    }

    /**
     * Doc va thay doi du lieu file template
     *
     * @param listData
     * @return
     */
    private void readAndWritingFileExcel(List<ExportVehiclesAssignedDTO> listData) throws IOException {
        try (InputStream inp = getClass().getResourceAsStream("/template/export/vehicle-assigned.xlsx"); Workbook wb = WorkbookFactory.create(inp)) {
            Sheet sheet = wb.getSheetAt(0);
            int indexRow = 3;
            for (ExportVehiclesAssignedDTO data : listData) {
                writingContentToExcel(data, sheet, indexRow, wb);
                indexRow++;
            }
            try (OutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + File.separator + "workbook.xlsx")) {
                wb.write(fileOut);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Lay danh sach danh muc loi : %s ", e));
        }
    }

    /**
     * Kiem tra dieu kien ghi noi dung file
     *
     * @param data
     * @param sheet
     * @param indexRow
     * @param wb
     */
    private void writingContentToExcel(ExportVehiclesAssignedDTO data, Sheet sheet, int indexRow, Workbook wb) {
        Row row = sheet.createRow(indexRow);
        int indexCell = 0;
        createAndSetStyleCell(wb, row, indexCell, String.valueOf(indexRow - 2));
        indexCell++;
        createAndSetStyleCell(wb, row, indexCell++, data.getPlateNumber());
        createAndSetStyleCell(wb, row, indexCell++, data.getNameGroup());
        createAndSetStyleCell(wb, row, indexCell++, data.getOwner());
        createAndSetStyleCell(wb, row, indexCell++, data.getNameType());
        createAndSetStyleCell(wb, row, indexCell++, data.getMark());
        createAndSetStyleCell(wb, row, indexCell++, data.getRfidSerial());
        createAndSetStyleCell(wb, row, indexCell++, data.getCreateUser());
        //To-do set ghi chu cho file excel
        createAndSetStyleCell(wb, row, indexCell, "");
    }

    /**
     * Kiem tra dang kiem cua phuong tiencustomers/contracts
     *
     * @param listDataRequest
     * @return
     */
    @Override
    public Object vehicleRegistryInfo(List<AddVehicleRequestDTO> listDataRequest) throws Exception {
        List<VehicleEntity> result = new ArrayList<>();
        List<Long> listVehicleSimilar = new ArrayList<>();
        List<Long> listVehicleNotSimilar = new ArrayList<>();
        if (listDataRequest.size() > Constants.SIZE_LIST_ZERO) {
            for (AddVehicleRequestDTO obj : listDataRequest) {
                ResponseGetInfoRegisterDTO res = searchXCGService.getInfoRegister(obj.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(obj.getPlateTypeCode()), EPC);
                checkVehicleRegistry(obj, listVehicleSimilar, listVehicleNotSimilar, res);
            }
        }

        // cap nhat trang thai khong khop voi ket qua dang kiem
        if (listVehicleNotSimilar.size() > Constants.SIZE_LIST_ZERO) {
            List<VehicleEntity> vehicleEntityListUpdate = new ArrayList<>();
            for (Long vehicleId : listVehicleNotSimilar) {
                VehicleEntity vehicleEntity = vehicleServiceJPA.findById(vehicleId).get();
                vehicleEntity.setStatus(VehicleEntity.Status.NOT_SIMILAR.value);
                vehicleEntityListUpdate.add(vehicleEntity);
            }
            result = vehicleServiceJPA.saveAll(vehicleEntityListUpdate);
        }

        // cap nhat trang thai khop voi ket qua dang kiem
        if (listVehicleSimilar.size() > Constants.SIZE_LIST_ZERO) {
            List<VehicleEntity> vehicleEntityListUpdate = new ArrayList<>();
            for (Long vehicleId : listVehicleSimilar) {
                VehicleEntity vehicleEntity = vehicleServiceJPA.findById(vehicleId).get();
                vehicleEntity.setStatus(VehicleEntity.Status.SIMILAR.value);
                vehicleEntityListUpdate.add(vehicleEntity);
            }
            result = vehicleServiceJPA.saveAll(vehicleEntityListUpdate);
        }
        return result;
    }


    /**
     * Tim kiem file theo contractId
     *
     * @param requestModel
     * @return
     */
    @Override
    public Object findProfileByVehicle(VehicleProfilePaginationDTO requestModel) {
        ResultSelectEntity resultData = vehicleRepository.findProfileByContract(requestModel);
        List<VehicleProfilePaginationDTO> vehicleProfileList = (List<VehicleProfilePaginationDTO>) resultData.getListData();
        List<VehicleProfilePaginationDTO> listData = addListProfileToResult(vehicleProfileList);
        resultData.setListData(listData);
        resultData.setCount(listData.size());
        return resultData;
    }


    /**
     * add list file name to result
     *
     * @param vehicleProfileList
     * @return
     */
    private List<VehicleProfilePaginationDTO> addListProfileToResult(List<VehicleProfilePaginationDTO> vehicleProfileList) {
        Map<Long, VehicleProfilePaginationDTO> map = new HashMap<>();
        for (VehicleProfilePaginationDTO vehicleProfilePaginationDTO : vehicleProfileList) {
            Long documentTypeId = vehicleProfilePaginationDTO.getDocumentTypeId();
            if (!map.containsKey(documentTypeId)) {
                map.put(documentTypeId, vehicleProfilePaginationDTO);
            }
            VehicleProfilePaginationDTO temp = map.get(documentTypeId);
            temp.addProfile(vehicleProfilePaginationDTO.getFileName(), vehicleProfilePaginationDTO.getVehicleProfileId());
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Lay ho so dinh kem cua hop dong
     *
     * @param profileId
     * @return
     */
    @Override
    public VehicleProfilePaginationDTO downloadProfileByContract(Long profileId) {
        List<VehicleProfilePaginationDTO> resultData = vehicleRepository.downloadProfileByContract(profileId);
        return resultData != null && resultData.size() > 0 ? resultData.get(0) : null;
    }


    /***
     * Thuc hien gan the cho phuong tien va goi OCS
     * @param requestDTO
     * @param authentication
     * @param custId
     * @param contractId
     * @param rfidSerial
     * @param actTypeId
     * @return
     * @throws IOException
     * @throws XMLStreamException
     * @throws EtcException
     * @throws JAXBException
     */
    public boolean vehicleAssign(AddVehicleRequestDTO requestDTO, Authentication authentication, Long custId, Long contractId, Long rfidSerial, Long actTypeId) throws Exception {
        if (validateRfidSerial(rfidSerial.toString())) {
            return false;
        }
        Long reasonId = getReasonIdByactTypeId(actTypeId);
        String statusVehicleBoo1 = checkVehicleActivationBoo1(requestDTO.getPlateNumber(), authentication, requestDTO.getPlateTypeCode(), requestDTO.getActTypeId());
        /***
         * Goi sang ben IM VDTC de lay chi tiet serial - 29112020
         */
        StockEtagDTO stockEtagDTO = imService.getSerialDetails(requestDTO.getRfidSerial(), authentication);
        // END Goi sang ben IM VDTC de lay chi tiet serial

        VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleIdAndStatusAndRfidSerial(requestDTO.getVehicleId(), VehicleEntity.Status.SIMILAR.value, null);
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        VehicleEntity vehicleEntityOld = gson.fromJson(gson.toJson(vehicleEntity), VehicleEntity.class);
        vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
        vehicleEntity.setEpc(stockEtagDTO.getEpc());
        vehicleEntity.setTid(stockEtagDTO.getTid());
        vehicleEntity.setRfidSerial(rfidSerial.toString());
        vehicleEntity.setRfidType(stockEtagDTO.getEtagTypeId().toString());
        vehicleEntity.setApprovedUser(FnCommon.getUserLogin(authentication));
        vehicleRepositoryJPA.save(vehicleEntity);

        /***
         * * Check User có partner_code khong
         *          * Neu co thi goi sang IM VDTC
         *          * Neu khong co thi chay luong cu goi IM VTT
         */
        StockTransEtagDTO stockTransEtagDTO = checkAndVerifySerialIMVDTC(rfidSerial.toString(), authentication, vehicleEntity, CASE_UPDATE_VEHICLE.ASSIGN_VEHICLE);
        if (stockTransEtagDTO == null) {
            // luong chay sang IM VTT nhu bt
            SaleBusinessMesDTO saleBusinessMesDTO = productService.callBCCSIM(authentication, actTypeId, vehicleEntity, rfidSerial.toString(), custId, CASE_UPDATE_VEHICLE.ASSIGN_VEHICLE, statusVehicleBoo1);
            if (saleBusinessMesDTO == null || "false".equals(saleBusinessMesDTO.getSuccessResult())) {
                returnStatusImportBeforeCallIM(vehicleEntity);
                return false;
            }
        } else {
            updateSerialIMVDTC(stockTransEtagDTO, IM_VDTC_STATUS.SUCCESS, authentication);
        }

        setDTOtoOCS(requestDTO, vehicleEntity, contractId, rfidSerial, actTypeId);
        OCSResponse response = ocsService.createVehicleOCS(requestDTO, authentication, requestDTO.getActTypeId());
        if (!FnCommon.checkOcsCode(response)) {
            // Neu OCS tra ve != 0 thi chuyen trang thai sang KHONG HOAT DONG
            returnStatusImportBeforeCallOCS(vehicleEntity);
            vehicleRepositoryJPA.save(vehicleEntity);
            return false;
        }

        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId,
                custId, contractId, vehicleEntity.getVehicleId(), ip);
        actionAuditEntity = actionAuditService.updateLogToActionAudit(actionAuditEntity);

        ActionAuditDetailDTO actionAuditDetailDTO = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity);
        ActionAuditDetailEntity actionAuditDetailEntity = actionAuditDetailDTO.toEntity(actionAuditEntity.getActionAuditId(), VehicleProfileEntity.class.getAnnotation(Table.class).name(), vehicleEntity.getVehicleId());
        actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
        return true;
    }

    /***
     * update vehicle transfer
     * @param vehicleTransferDTO
     * @param authentication
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferVehicles(VehicleTransferDTO vehicleTransferDTO, Authentication authentication) throws Exception {
        List<Long> transferVehicleIds = vehicleTransferDTO.getTransferVehicleIds();
        String ip = InetAddress.getLocalHost().getHostAddress();
        Date currDate = new Date(System.currentTimeMillis());
        String userLogin = FnCommon.getUserLogin(authentication);
        for (Long vehicleId : transferVehicleIds) {
            VehicleEntity vehicleEntityUpdate = vehicleServiceJPA.findById(vehicleId).get();
            VehicleEntity vehicleEntityOld = new Gson().fromJson(new Gson().toJson(vehicleEntityUpdate), VehicleEntity.class);
            if (vehicleEntityUpdate.getContractId().equals(vehicleTransferDTO.getTransferContractId())) {
                throw new EtcException("crm.transfer.vehicle.duplicate.contract");
            }
            VehicleEntity vehicleEntityInsert = new Gson().fromJson(new Gson().toJson(vehicleEntityUpdate), VehicleEntity.class);
            // call API OCS
            OCSResponse ocsResponse = callOCSTransfer(vehicleTransferDTO.getTransferContractId(), vehicleEntityUpdate, authentication, vehicleTransferDTO.getActTypeId());
            if (ocsResponse != null && "0".equals(ocsResponse.getResultCode())) {
                updateVehicle(vehicleEntityUpdate);
                vehicleEntityInsert = insertVehicle(vehicleEntityInsert, vehicleTransferDTO.getTransferContractId());
                checkChargeTicket(vehicleId, currDate, vehicleEntityUpdate.getContractId(), vehicleTransferDTO.getTransferContractId(), userLogin, vehicleEntityInsert.getVehicleId());
                updateLogVehicleOld(authentication, vehicleTransferDTO, vehicleEntityOld, vehicleEntityUpdate, ip);
                updateLogVehicleNew(authentication, vehicleEntityInsert, ip);
                //update sale trans
                String contractNo = contractServiceJPA.findById(vehicleEntityUpdate.getContractId()).get().getContractNo();
                if (vehicleTransferDTO.getAmount() != null && vehicleTransferDTO.getAmount() > Constants.AMOUNT_ZERO) {
                    addSaleTransAndSaleTransDetails(vehicleEntityUpdate.getCustId(), vehicleEntityUpdate.getContractId(), contractNo, authentication, vehicleId, vehicleTransferDTO.getActTypeId());
                }
                if (!Objects.isNull(vehicleTransferDTO.getContractProfileDTOList())) {
                    updateContractProfile(authentication, currDate, userLogin, vehicleTransferDTO, ip);
                }
            } else {
                throw new EtcException("Transfer OCS fail");
            }
        }
    }

    /**
     * Luu lich su tac dong chuyen
     *
     * @param vehicleTransferDTO
     * @param authentication
     * @throws Exception
     */
    @Override
    public void transferVehicleOnContract(VehicleTransferDTO vehicleTransferDTO, Authentication authentication) throws Exception {
        List<Long> transferVehicleIds = vehicleTransferDTO.getTransferVehicleIds();
        String ip = InetAddress.getLocalHost().getHostAddress();
        for (Long vehicleId : transferVehicleIds) {
            VehicleEntity vehicleEntity = vehicleServiceJPA.findById(vehicleId).orElseThrow(new EtcException("Phương tiện không tồn tại"));
            VehicleEntity vehicleEntityOld = new Gson().fromJson(new Gson().toJson(vehicleEntity), VehicleEntity.class);
            vehicleEntity.setContractId(vehicleTransferDTO.getTransferContractId());
            vehicleServiceJPA.save(vehicleEntity);
            // update action audit
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, vehicleTransferDTO.getReasonId(), vehicleTransferDTO.getActTypeId(), vehicleEntityOld.getCustId(), vehicleEntityOld.getContractId(), vehicleId, ip));
            //update action audit detail
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleId);
            actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
            callOCS(vehicleTransferDTO.getTransferContractId(), vehicleEntityOld, vehicleEntity, authentication, vehicleTransferDTO.getActTypeId());
        }
    }

    /***
     * Tra cuu thong tin phuong tien
     * @param requestModel
     * @param authentication
     * @return
     */
    @Override
    public Object findVehicleByContract(VehicleByContractDTO requestModel, Authentication authentication) {
        ResultSelectEntity dataResult = vehicleRepository.findVehicleByContract(requestModel);
        if (dataResult.getListData().size() > 0) {
            List<VehicleByContractDTO> listData = (List<VehicleByContractDTO>) dataResult.getListData();
            for (VehicleByContractDTO vehicleDTO : listData) {
                // Lay thong tin xe tu service category
                try {
                    ResultSelectEntity listResult = categoriesService.findCategoriesCustomer(FnCommon.getStringToken(authentication));
                    List<Map<String, String>> listResultDTO = (List<Map<String, String>>) listResult.getListData();
                    if (listResultDTO != null) {
                        Map<String, Map<String, String>> categoryMap = new HashMap<>();
                        for (Map<String, String> temp : listResultDTO) {
                            categoryMap.put(buildCategoryKeyByCode(temp.get("table_name"), temp.get("code")), temp);
                        }
                        if (Objects.nonNull(vehicleDTO.getVehicleTypeId())) {
                            String vehicleTypeKey = buildCategoryKeyByCode("VEHICLE_TYPE", vehicleDTO.getVehicleTypeId().toString());
                            if (categoryMap.containsKey(vehicleTypeKey)) {
                                vehicleDTO.setNameType(categoryMap.get(vehicleTypeKey).get("name"));
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("Error call categories", ex);
                }
            }
        }
        return dataResult;
    }

    /**
     * tra cuu phuong tien theo id
     *
     * @param vehicleId params client
     * @return
     */
    @Override
    public Object findVehicleById(Long vehicleId, Authentication authentication) {
        ResultSelectEntity dataResult = vehicleRepository.findVehicleById(vehicleId);
        if (dataResult.getListData().size() > 0) {
            VehicleByContractDTO vehicleByContractDTO = (VehicleByContractDTO) dataResult.getListData().get(0);
            // Lay thong tin xe tu service category
            Map<String, Map<String, String>> categoryMap = new HashMap<>();
            setValueCategoryMap(authentication, categoryMap);
            if (Objects.nonNull(vehicleByContractDTO.getVehicleColourId())) {
                String vehicleColourKey = buildCategoryKeyByCode("VEHICLE_COLOUR", vehicleByContractDTO.getVehicleColourId().toString());
                if (categoryMap.containsKey(vehicleColourKey)) {
                    vehicleByContractDTO.setColor(categoryMap.get(vehicleColourKey).get("name"));
                }
            }
            if (Objects.nonNull(vehicleByContractDTO.getVehicleMarkId())) {
                String vehicleMarkKey = buildCategoryKeyByCode("VEHICLE_MARK", vehicleByContractDTO.getVehicleMarkId().toString());
                if (categoryMap.containsKey(vehicleMarkKey)) {
                    vehicleByContractDTO.setMark(categoryMap.get(vehicleMarkKey).get("name"));
                }
            }
            if (Objects.nonNull(vehicleByContractDTO.getVehicleBrandId())) {
                String vehicleBrandKey = buildCategoryKeyByCode("VEHICLE_BRAND", vehicleByContractDTO.getVehicleBrandId().toString());
                if (categoryMap.containsKey(vehicleBrandKey)) {
                    vehicleByContractDTO.setBrand(categoryMap.get(vehicleBrandKey).get("name"));
                }
            }
            if (Objects.nonNull(vehicleByContractDTO.getVehicleTypeId())) {
                String vehicleTypeKey = buildCategoryKeyByCode("VEHICLE_TYPE", vehicleByContractDTO.getVehicleTypeId().toString());
                if (categoryMap.containsKey(vehicleTypeKey)) {
                    vehicleByContractDTO.setNameType(categoryMap.get(vehicleTypeKey).get("name"));
                }
            }
            if (Objects.nonNull(vehicleByContractDTO.getPlateType())) {
                String vehiclePlateTypeKey = buildCategoryKeyByCode("PLATE_TYPE", vehicleByContractDTO.getPlateType().toString());
                if (categoryMap.containsKey(vehiclePlateTypeKey)) {
                    vehicleByContractDTO.setPlateTypeName(categoryMap.get(vehiclePlateTypeKey).get("name"));
                }
            }
            // To-Do get group name from group id
        }
        return dataResult;
    }

    /**
     * Call ocs
     *
     * @param TransferContractId
     * @param vehicleEntityUpdate
     * @param vehicleEntityInsert
     * @param authentication
     * @param actTypeId
     * @throws Exception
     */
    private void callOCS(Long TransferContractId, VehicleEntity vehicleEntityUpdate, VehicleEntity vehicleEntityInsert, Authentication authentication, Long actTypeId) throws Exception {
        // get contract no from contract id
        String contractId = "";
        Optional<ContractEntity> contractEntityOptional = contractServiceJPA.findById(vehicleEntityUpdate.getContractId());
        if (contractEntityOptional.isPresent()) {
            contractId = String.valueOf(contractEntityOptional.get().getContractId());
        }
        OCSResponse resultObjDelete = ocsService.deleteVehicle(vehicleEntityUpdate.getEpc(), contractId, authentication, actTypeId.intValue());
        if (!FnCommon.checkOcsCode(resultObjDelete)) {
            throw new EtcException("Call delete vehicle OCS fall :" + resultObjDelete.getDescription());
        }

        OCSResponse resultObjCreate = ocsService.createVehicleOCS(new AddVehicleRequestDTO().entityToAddVehicleRequestDTO(vehicleEntityInsert, TransferContractId), authentication, actTypeId);
        if (!FnCommon.checkOcsCode(resultObjCreate)) {
            throw new EtcException("Call create vehicle OCS fall :" + resultObjCreate.getDescription());
        }
    }

    /**
     * insert vehicle with contractId new
     *
     * @param vehicleEntityOld
     * @param transferContractId
     */
    private VehicleEntity insertVehicle(VehicleEntity vehicleEntityOld, Long transferContractId) {
        Long custId = contractServiceJPA.findById(transferContractId).get().getCustId();
        vehicleEntityOld.setVehicleId(null);
        vehicleEntityOld.setContractId(transferContractId);
        vehicleEntityOld.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
        vehicleEntityOld.setStatus(VehicleEntity.Status.ACTIVATED.value);
        vehicleEntityOld.setCustId(custId);
        vehicleEntityOld.setEffDate(new Date(System.currentTimeMillis()));
        vehicleEntityOld.setExpDate(null);
        return vehicleServiceJPA.save(vehicleEntityOld);
    }

    /**
     * update vehicle active status transfer
     *
     * @param vehicleEntity
     */
    private void updateVehicle(VehicleEntity vehicleEntity) {
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.TRANSFERRED.value);
        vehicleServiceJPA.save(vehicleEntity);
    }

    /**
     * Download file import, luu du lieu vao vehicle
     *
     * @param file
     * @param authentication
     * @param customerId
     * @param contractId
     */
    public ResultFileImportDTO saveFileImportVehicles(MultipartFile file, Authentication authentication, Long customerId, Long contractId) throws IOException, DataNotFoundException {

        ResultFileImportDTO resultFileImportDTO = new ResultFileImportDTO();
        List<AddVehicleRequestDTO> list = new ArrayList<>();
        List<String> listError = new ArrayList<>();
        byte[] byteArr = file.getBytes();
        boolean isExcel = validateFileTemplate(file);
        if (!isExcel) {
            throw new EtcException("crm.file.not.is.format.excel");
        }
        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr));
        Sheet sheet = workbook.getSheetAt(0);
        boolean isAccepted = validateFileImport(sheet);
        String token = FnCommon.getStringToken(authentication);
        CellStyle styleHeader = workbook.createCellStyle();
        setStyleCell(styleHeader);
        Font fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setCharSet(FontCharset.VIETNAMESE.getValue());
        styleHeader.setFont(fontHeader);

        CellStyle styleCell = workbook.createCellStyle();
        setStyleCell(styleCell);
        Font fontCell = workbook.createFont();
        fontCell.setCharSet(FontCharset.VIETNAMESE.getValue());

        if (isAccepted) {
            if (sheet.getLastRowNum() >= 4) {
                if (FnCommon.validateMaxRow(sheet.getLastRowNum())) {
                    throw new EtcException("common.validate.max.row.num.excel");
                }
                Cell cell16 = sheet.getRow(3).createCell(15);
                cell16.setCellValue(jedisCacheService.getMessageErrorByKey("common.validate.err.data.check.invalid"));
                cell16.setCellStyle(styleHeader);
                Cell cell17 = sheet.getRow(3).createCell(16);
                cell17.setCellValue(jedisCacheService.getMessageErrorByKey("common.validate.reason"));
                cell17.setCellStyle(styleHeader);
                ResultSelectEntity listResult = categoriesService.findCategoriesCustomer(token);
                List<Map<String, String>> listResultDTO = (List<Map<String, String>>) listResult.getListData();
                Map<String, Map<String, String>> categoryMap = new HashMap<>();
                for (Map<String, String> temp : listResultDTO) {
                    categoryMap.put(buildCategoryKey(temp.get("table_name"), temp.get("description")), temp);
                }
                HashMap<Integer, String> hashMap = new HashMap<>();
                for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                    StringBuilder result = new StringBuilder();
                    AddVehicleRequestDTO vehicleDTO = new AddVehicleRequestDTO();
                    vehicleDTO.setId(i);
                    Row row = sheet.getRow(i);
                    Cell cellCheck = row.createCell(15);
                    Cell cellReason = row.createCell(16);
                    cellCheck.setCellStyle(styleCell);
                    cellReason.setCellStyle(styleCell);
                    if (checkNullOrEmptyRowExcel(row)) {
                        cellCheck.setCellValue("");
                        cellReason.setCellValue("");
                        continue;
                    }
                    validateInputExcel(vehicleDTO, row, result, hashMap, i);
                    if (!"".equals(result.toString())) {
                        listError.add(result.toString());
                        cellCheck.setCellValue(jedisCacheService.getMessageErrorByKey("common.validate.not.invalid"));
                        cellReason.setCellValue(result.toString());
                    } else {
                        getIdByType(vehicleDTO, categoryMap);
                        cellCheck.setCellValue(jedisCacheService.getMessageErrorByKey("common.validate.invalid"));
                        list.add(vehicleDTO);
                    }
                }
            }
        } else {
            throw new EtcException("crm.file.not.is.format.excel");
        }
        resultFileImportDTO.setImportVehicleList(list);
        resultFileImportDTO.setListError(listError);
        if (resultFileImportDTO.getListError() == null || resultFileImportDTO.getListError().size() == 0) {
            VehicleGroupIdDTO vehicleGroupIdDTO = new VehicleGroupIdDTO();
            vehicleGroupIdDTO.setVehicle(resultFileImportDTO.getImportVehicleList());
            List<VehicleGroupIdResponseDTO.VehicleGroup> vehicleGroupByListObject = vehicleGroupService.findVehicleGroupByListObject(FnCommon.getStringToken(authentication), vehicleGroupIdDTO);
            int result = mapVehicleGroupId(resultFileImportDTO.getImportVehicleList(), vehicleGroupByListObject, sheet, resultFileImportDTO.getListError());
            if (result == 0) {
                saveListVehicle(resultFileImportDTO.getImportVehicleList(), authentication, customerId, contractId);
            } else {
                for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    Cell cellCheck = row.getCell(15);
                    cellCheck.setCellValue(jedisCacheService.getMessageErrorByKey("common.validate.not.invalid"));
                    cellCheck.setCellStyle(styleCell);
                }
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        workbook.close();
        resultFileImportDTO.setResult(os.toByteArray());
        return resultFileImportDTO;
    }

    /**
     * API App CPT: search vehicle
     *
     * @param vehicleDTO
     * @param authentication
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Object searchVehicle(VehicleDTO vehicleDTO, Authentication authentication) throws JsonProcessingException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VehicleEntity> query = criteriaBuilder.createQuery(VehicleEntity.class);
        Root<VehicleEntity> root = query.from(VehicleEntity.class);
        Expression<String> status = root.get("status");
        Expression<String> activeStatus = root.get("activeStatus");

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<VehicleEntity> countRoot = countQuery.from(VehicleEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        List<ContractEntity> byAccountUser = contractServiceJPA.findByAccountUser(FnCommon.getUserLogin(authentication));
        Long contractId = byAccountUser.get(0).getContractId();
        if (vehicleDTO.getInContract() == null || vehicleDTO.getInContract()) {

            if (CollectionUtils.isEmpty(byAccountUser)) {
                ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
                resultSelectEntity.setCount(0);
                resultSelectEntity.setListData(new ArrayList<VehicleEntity>());
                return resultSelectEntity;
            }
            predicates.add(criteriaBuilder.equal(root.get("contractId"), contractId));
        }

        if (Objects.nonNull(vehicleDTO.getVehicleId())) {
            predicates.add(criteriaBuilder.equal(root.get("vehicleId"), vehicleDTO.getVehicleId()));
        }

        if (Objects.nonNull(vehicleDTO.getStatus())) {
            predicates.add(criteriaBuilder.equal(root.get("status"), vehicleDTO.getStatus()));
        }

        if (Objects.nonNull(vehicleDTO.getActiveStatus())) {
            predicates.add(criteriaBuilder.equal(root.get("activeStatus"), vehicleDTO.getActiveStatus()));
        }

        if (Objects.nonNull(vehicleDTO.getActiveStatusIsNot())) {
            predicates.add(criteriaBuilder.notEqual(root.get("activeStatus"), vehicleDTO.getActiveStatusIsNot()));
        }

        if (vehicleDTO.getStatuses() != null && !vehicleDTO.getStatuses().isEmpty()) {
            Predicate predicate = status.in(vehicleDTO.getStatuses());
            predicates.add(predicate);
        }

        if (vehicleDTO.getActiveStatuses() != null && !vehicleDTO.getActiveStatuses().isEmpty()) {
            Predicate predicate = activeStatus.in(vehicleDTO.getActiveStatuses());
            predicates.add(predicate);
        }

        String plateNumber = Objects.nonNull(vehicleDTO.getPlateNumber()) ? vehicleDTO.getPlateNumber().toLowerCase() : "";
        if (vehicleDTO.getInContract() == null || vehicleDTO.getInContract()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("plateNumber")), "%" + plateNumber + "%"));
        } else {
            predicates.add(criteriaBuilder.notEqual(root.get("contractId"), contractId));
            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("plateNumber")), plateNumber));
        }

        predicates.add(criteriaBuilder.isNotNull(root.get("activeStatus")));

        query.where(predicates.toArray(new Predicate[0]));
        countQuery.where(predicates.toArray(new Predicate[0]));

        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<VehicleDTO> resultList = entityManager.createQuery(query)
                .setMaxResults(vehicleDTO.getPagesize())
                .setFirstResult(vehicleDTO.getStartrecord())
                .getResultList()
                .stream().map(new VehicleDTO()::fromEntityToSearchVehicle).collect(Collectors.toList());

        //map data tu DMDC
        ObjectMapper objectMapper = new ObjectMapper();
        VehicleGroupDTO[] vehicleGroupDTOS = new VehicleGroupDTO[0];
        JsonNode dataOfOtherModule = getDataOfOtherModule(wsVehicelGroups, authentication, JsonNode.class);
        if (dataOfOtherModule.get("mess").get("code").intValue() == CoreErrorApp.SUCCESS.getCode()) {
            vehicleGroupDTOS = objectMapper.readValue(dataOfOtherModule.get("data").get("listData").toString(), VehicleGroupDTO[].class);
        }
        Map<Long, VehicleGroupDTO> collect = Arrays.stream(vehicleGroupDTOS).collect(Collectors.toMap(VehicleGroupDTO::getId, Function.identity()));

        for (VehicleDTO result : resultList) {
            if (collect.containsKey(result.getVehicleGroupId())) {
                VehicleGroupDTO vehicleGroupDTO = collect.get(result.getVehicleGroupId());
                result.setVehicleGroupName(vehicleGroupDTO.getName());
                result.setVehicleGroupDescription(vehicleGroupDTO.getDescription());
            }
        }
        resultSelectEntity.setCount(entityManager.createQuery(countQuery.select(criteriaBuilder.count(countRoot))).getSingleResult());
        resultSelectEntity.setListData(resultList);
        return resultSelectEntity;
    }

    private <T> T getDataOfOtherModule(String url, Authentication authentication, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(FnCommon.getStringToken(authentication));

        return restTemplate.exchange(url,
                HttpMethod.GET, new HttpEntity<>(headers),
                responseType)
                .getBody();
    }

    /**
     * Noi ten table va value
     *
     * @param tableName
     * @param description
     */
    private String buildCategoryKey(String tableName, String description) {
        return tableName + "_" + description;
    }

    /**
     * Validate ten file
     *
     * @param fileName
     */
    private boolean validateFileName(String fileName) {
        boolean isAccepted = true;
        if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
            isAccepted = false;
        }
        return isAccepted;

    }

    /**
     * Validate tieu de
     *
     * @param sheet
     */
    private boolean validateFileImport(Sheet sheet) {
        boolean isAccepted = true;
        Row row2 = sheet.getRow(2);
        int totalRow = row2.getLastCellNum();
        if (totalRow != 15) {
            isAccepted = false;
        }
        StringBuilder template = new StringBuilder();
        template.append("STT").append("plateNumber").append("owner")
                .append("vehicleType").append("netWeight")
                .append("cargoWeight").append("grossWeight")
                .append("pullingWeight").append("seatNumber")
                .append("engineNumber").append("chassicNumber").append("vehicleColor")
                .append("vehicleMark").append("vehicleBrand").append("plateType");

        StringBuilder input = new StringBuilder();
        input.append(row2.getCell(0).getStringCellValue().trim()).append(row2.getCell(1).getStringCellValue().trim())
                .append(row2.getCell(2).getStringCellValue().trim())
                .append(row2.getCell(3).getStringCellValue().trim()).append(row2.getCell(4).getStringCellValue().trim())
                .append(row2.getCell(5).getStringCellValue().trim()).append(row2.getCell(6).getStringCellValue().trim())
                .append(row2.getCell(7).getStringCellValue().trim()).append(row2.getCell(8).getStringCellValue().trim())
                .append(row2.getCell(9).getStringCellValue().trim()).append(row2.getCell(10).getStringCellValue().trim())
                .append(row2.getCell(11).getStringCellValue().trim()).append(row2.getCell(12).getStringCellValue().trim())
                .append(row2.getCell(13).getStringCellValue().trim()).append(row2.getCell(14).getStringCellValue().trim());
        if (template.toString().hashCode() != input.toString().hashCode()) {
            isAccepted = false;
        }
        return isAccepted;
    }

    /**
     * Lay gia tri String cua cell
     *
     * @param cell
     */
    private String getStringValue(Cell cell) {
        try {
            if (cell == null) {
                return "";
            } else if (CellType.BLANK == cell.getCellType()) {
                return "";
            } else if (CellType.BOOLEAN == cell.getCellType()) {
                return cell.getBooleanCellValue() + "";
            } else if (CellType.ERROR == cell.getCellType()) {
                return null;
            } else if (CellType.FORMULA == cell.getCellType()) {
                return cell.getCellFormula();
            } else if (CellType.NUMERIC == cell.getCellType()) {
                return String.valueOf(cell.getNumericCellValue());
            } else if (CellType.STRING == cell.getCellType()) {
                return cell.getStringCellValue();
            }
        } catch (Exception e) {
            LOGGER.error("Error when cast value to String", e);
        }
        return "";
    }

    /**
     * Set style cell
     *
     * @param style
     */
    public void setStyleCell(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);
    }

    /**
     * Validate noi dung file excel
     */
    private void validateInputExcel(AddVehicleRequestDTO vehicleDTO, Row row, StringBuilder result, HashMap<Integer, String> hashMap, int i) {
        // validate bien so xe
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(1)))) {
            String plateNumber = getStringValue(row.getCell(1));
            if (!validatePlateNumber(plateNumber)) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.plate.number.only.number.letter"));
            }
            if (FnCommon.validateMaxlengthString(plateNumber, 16)) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.plate.number.max.length"));
            }
            if (checkVehicleExist(plateNumber.toUpperCase())) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.plate.number.exist"));
            }
            if (hashMap.containsValue(plateNumber)) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.plate.number.exist.excel"));
            }
            vehicleDTO.setPlateNumber(plateNumber);
            hashMap.put(i, plateNumber);
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("common.validate.plate.number.not.empty"));
        }
        // validate chu phuong tien
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(2)))) {
            String owner = getStringValue(row.getCell(2));
            if (FnCommon.validateMaxlengthString(owner, 255)) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.owner.maxlength"));
            } else {
                vehicleDTO.setOwner(owner);
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("common.validate.owner.not.empty"));
        }
        // validate vehicleType
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(3)))) {
            String[] vehicleType = getIdAndCodeByName(getStringValue(row.getCell(3)));
            if (vehicleType == null || vehicleType.length != 3) {
                throw new EtcException("categories.connection.error");
            } else {
                vehicleDTO.setVehicleTypeName(vehicleType[0]);
                vehicleDTO.setVehicleTypeId(Long.parseLong(vehicleType[1]));
                vehicleDTO.setVehicleTypeCode(vehicleType[2]);
            }

        } else {
            result.append(jedisCacheService.getMessageErrorByKey("common.validate.vehicle.type.id.not.empty"));
        }

        // validate khoi luong ban than
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(4)))) {
            if (row.getCell(4).getCellType() != CellType.NUMERIC) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.net.weight.must.be.number"));
            } else {
                Double netWeight = FnCommon.round(Double.parseDouble(getStringValue(row.getCell(4))));
                if (netWeight < 0 || FnCommon.validateMaxlengthDouble(netWeight, 6)) {
                    result.append(jedisCacheService.getMessageErrorByKey("common.validate.net.weight.min.max"));
                } else {
                    vehicleDTO.setNetWeight(netWeight);
                }
            }
        }

        // validate khoi luong hang hoa
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(5)))) {
            if (row.getCell(5).getCellType() != CellType.NUMERIC) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.cargo.must.be.number"));
            } else {
                Double cargoWeight = FnCommon.round(Double.parseDouble(getStringValue(row.getCell(5))));
                if (cargoWeight < 0 || FnCommon.validateMaxlengthDouble(cargoWeight, 6)) {
                    result.append(jedisCacheService.getMessageErrorByKey("common.validate.cargo.weight.min.max"));
                } else {
                    vehicleDTO.setCargoWeight(cargoWeight);
                }
            }
        }

        // validate khoi luong toan bo
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(6)))) {
            if (row.getCell(6).getCellType() != CellType.NUMERIC) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.gross.weight.must.be.number"));
            } else {
                Double grossWeight = FnCommon.round(Double.parseDouble(getStringValue(row.getCell(6))));
                if (grossWeight < 0 || FnCommon.validateMaxlengthDouble(grossWeight, 6)) {
                    result.append(jedisCacheService.getMessageErrorByKey("common.validate.gross.weight.min.max"));
                } else {
                    vehicleDTO.setGrossWeight(grossWeight);
                }
            }
        }

        // validate khoi luong keo theo
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(7)))) {
            if (row.getCell(7).getCellType() != CellType.NUMERIC) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.pulling.weight.must.be.number"));
            } else {
                Double pullingWeight = FnCommon.round(Double.parseDouble(getStringValue(row.getCell(7))));
                if (pullingWeight < 0 || FnCommon.validateMaxlengthDouble(pullingWeight, 6)) {
                    result.append(jedisCacheService.getMessageErrorByKey("common.validate.pulling.weight.min.max"));
                } else {
                    vehicleDTO.setPullingWeight(pullingWeight);
                }
            }
        }
        // validate so cho ngoi
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(8)))) {
            if (row.getCell(8).getCellType() != CellType.NUMERIC) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.seat.number.not.empty"));
            } else {
                Long seatNumber = (new Double(Double.parseDouble(getStringValue(row.getCell(8))))).longValue();
                if (seatNumber < 0 || seatNumber >= 1000) {
                    result.append(jedisCacheService.getMessageErrorByKey("common.validate.seat.number.min.max"));
                } else {
                    vehicleDTO.setSeatNumber(seatNumber);
                }
            }
        } else {
            result.append(jedisCacheService.getMessageErrorByKey("common.validate.seat.number.not.empty"));
        }
        // validate so may
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(9)))) {
            String engineNumber = getStringValue(row.getCell(9));
            if (FnCommon.validateMaxlengthString(engineNumber, 50)) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.engine.number.max.length"));
            } else {
                vehicleDTO.setEngineNumber(engineNumber);
            }
        }
        //validate so khung
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(10)))) {
            String chassicNumber = getStringValue(row.getCell(9));
            if (FnCommon.validateMaxlengthString(chassicNumber, 50)) {
                result.append(jedisCacheService.getMessageErrorByKey("common.validate.chassic.number.max.length"));
            } else {
                vehicleDTO.setChassicNumber(chassicNumber);
            }
        }

        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(11)))) {
            vehicleDTO.setVehicleColor(getStringValue(row.getCell(11)));
        }

        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(12)))) {
            vehicleDTO.setVehicleMark(getStringValue(row.getCell(12)));
        }

        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(13)))) {
            vehicleDTO.setVehicleBrand(getStringValue(row.getCell(13)));
        }
        if (!FnCommon.isNullOrEmpty(getStringValue(row.getCell(14)))) {
            vehicleDTO.setPlateColor(getStringValue(row.getCell(14)));
        }
    }

    /**
     * Get id by value
     *
     * @param vehicleDTO
     * @param categoryMap
     */
    private void getIdByType(AddVehicleRequestDTO vehicleDTO, Map<String, Map<String, String>> categoryMap) {
        if (Objects.nonNull(vehicleDTO.getVehicleMark())) {
            String[] result = getIdAndCodeByName(vehicleDTO.getVehicleMark());
            if (result == null || result.length != 3) {
                throw new EtcException("categories.connection.error");
            }
            vehicleDTO.setVehicleMarkId(Long.parseLong(result[1]));
        }
        if (Objects.nonNull(vehicleDTO.getVehicleBrand())) {
            String[] result = getIdAndCodeByName(vehicleDTO.getVehicleBrand());
            if (result == null || result.length != 3) {
                throw new EtcException("categories.connection.error");
            }
            vehicleDTO.setVehicleBrandId(Long.parseLong(result[1]));
        }
        if (Objects.nonNull(vehicleDTO.getVehicleColor())) {
            String[] result = getIdAndCodeByName(vehicleDTO.getVehicleColor());
            if (result == null || result.length != 3) {
                throw new EtcException("categories.connection.error");
            }
            vehicleDTO.setVehicleColourId(Long.parseLong(result[1]));
        }
        if (Objects.nonNull(vehicleDTO.getPlateColor())) {
            String[] result = getIdAndCodeByName(vehicleDTO.getPlateColor());
            if (result == null || result.length != 3) {
                throw new EtcException("categories.connection.error");
            }
            vehicleDTO.setPlateTypeId(Long.parseLong(result[1]));
            vehicleDTO.setPlateTypeCode(result[2]);
        }
    }

    /**
     * Luu danh sach phuong tien import
     *
     * @param dataParams
     * @param authentication
     * @param customerId
     * @param contractId
     */
    public void saveListVehicle(List<AddVehicleRequestDTO> dataParams, Authentication authentication, Long customerId, Long contractId) throws IOException, DataNotFoundException, EtcException {
        List<VehicleEntity> list = new ArrayList<>();
        VehicleEntity vehicleEntity;
        for (AddVehicleRequestDTO addVehicleRequestDTO : dataParams) {
            vehicleEntity = saveVehicle(addVehicleRequestDTO, authentication, customerId, contractId, true);
            list.add(vehicleEntity);
        }
        Long actTypeId = 3L;
        Long reasonId = getReasonIdByactTypeId(actTypeId);
        List<VehicleEntity> listResult = vehicleRepositoryJPA.saveAll(list);
        for (VehicleEntity vehicleEntity1 : listResult) {
            writeLogNew(authentication, reasonId, actTypeId, contractId, customerId,
                    vehicleEntity1.getVehicleId(), vehicleEntity1, VehicleEntity.class.getAnnotation(Table.class).name());
        }
    }

    /**
     * Kich hoat serial cho phuong tien
     *
     * @param rfidDTO
     * @param authentication
     * @return
     * @throws RuntimeException
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws UnknownHostException
     */
    @Override
    public ActiveRFIDResponseDTO.ActiveResponses activeRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException {
        return activeRFIDOCS(rfidDTO, authentication);
    }

    /**
     * Khoa the serial cua phuong tien
     *
     * @param rfidDTO
     * @param authentication
     * @return
     * @throws RuntimeException
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws UnknownHostException
     */
    @Override
    public Object lockRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException {
        return updateStatusRFID(rfidDTO, Constants.RFIDStatus.CLOSE.code, authentication);
    }

    /**
     * Mo the serial cua phuong tien
     *
     * @param rfidDTO
     * @param authentication
     * @return
     * @throws RuntimeException
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws UnknownHostException
     */
    @Override
    public Object unlockRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException {
        return updateStatusRFID(rfidDTO, Constants.RFIDStatus.OPEN.code, authentication);
    }

    /**
     * Huy the serial cua phuong tien
     *
     * @param rfidDTO
     * @param authentication
     * @return
     * @throws RuntimeException
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws UnknownHostException
     */
    @Override
    public Object destroyRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException {
        long vehicleId = rfidDTO.getVehicleId();
        Optional<VehicleEntity> optionalVehicleEntity = vehicleServiceJPA.findById(vehicleId);
        if (!optionalVehicleEntity.isPresent()) {
            LOGGER.error("active RFID, vehicle not found, vehicleId: {}", vehicleId);
            return false;
        }
        VehicleEntity vehicleEntity = optionalVehicleEntity.get();
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        VehicleEntity vehicleEntityOld = gson.fromJson(gson.toJson(vehicleEntity), VehicleEntity.class);
        String activeStatus = vehicleEntity.getActiveStatus();
        OCSResponse response = ocsService.deleteVehicle(vehicleEntity.getEpc(), String.valueOf(vehicleEntity.getContractId()), authentication, (int) rfidDTO.getActTypeId());
        if ("0".equals(response.getResultCode())) {
            vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.CANCEL.value);
            vehicleEntity.setStatus(VehicleEntity.Status.NOT_ACTIVATED.value);
            vehicleServiceJPA.save(vehicleEntity);
            // update action audit
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, rfidDTO.getReasonId(), rfidDTO.getActTypeId(), rfidDTO.getCustId(), vehicleEntity.getContractId(), vehicleId, ip));
            //update action audit detail
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleId);
            actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
            //update sale trans
            if (rfidDTO.getAmount() != null && rfidDTO.getAmount() > Constants.AMOUNT_ZERO) {
                addSaleTransAndSaleTransDetails(rfidDTO.getCustId(), vehicleEntity.getContractId(), null, authentication, rfidDTO.getVehicleId(), rfidDTO.getActTypeId());
            }

            // Kiem tra xem ton tai trong BL WL MG khong va da co ve hieu luc ben BOO1 chua
            List<SaleTransDetailEntity> listSaleTransDetail = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntity.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode()), vehicleEntity.getEpc(), Constants.BOO1);
            List<ExceptionListEntity> listExceptionList = exceptionListServiceJPA.findExceptionListStillEffectiveDate(vehicleEntity.getEpc(), vehicleEntity.getPlateNumber());
            if (listSaleTransDetail != null && listSaleTransDetail.size() > 0 || listExceptionList != null && listExceptionList.size() > 0) {
                // Dong bo phuong tien sang BOO1 khi dong mo etag
                ReqOnlineEventSyncDTO req = new ReqOnlineEventSyncDTO();
                req.setEtag_old(vehicleEntity.getEpc());
                req.setReason(Constants.REASON_CHANGE_BOO.CANCEL_EPC);
                req.setRequest_id(System.currentTimeMillis());
                req.setRequest_datetime(System.currentTimeMillis());
                if (activeStatus.equals(VehicleEntity.ActiveStatus.ACTIVATED.value) || activeStatus.equals(VehicleEntity.ActiveStatus.OPEN.value)
                        || activeStatus.equals(VehicleEntity.ActiveStatus.TRANSFERRED.value)) {
                    req.setEtag_status_old(String.valueOf(Constants.BooEtagStatus.OPEN.code));
                } else {
                    req.setEtag_status_old(String.valueOf(Constants.BooEtagStatus.CLOSE.code));
                }
                req.setEtag_status_new(String.valueOf(Constants.BooEtagStatus.DESTROY.code));
                boo1Service.onlineEventSync(req, authentication, rfidDTO.getActTypeId());
            }

            return true;
        } else {
            throw new EtcException("Destroy OCS fail: " + response.getDescription());
        }
    }

    /**
     * Doi the serial khac cho phuong tien
     *
     * @param rfidDTO
     * @param authentication
     * @return
     * @throws DataNotFoundException
     * @throws JSONException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized Object swapRFID(RFIDDTO rfidDTO, Authentication authentication) throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        long vehicleId = rfidDTO.getVehicleId();
        Optional<VehicleEntity> optionalVehicleEntity = vehicleServiceJPA.findById(vehicleId);
        if (!optionalVehicleEntity.isPresent()) {
            throw new EtcException("crm.vehicle.not.found");
        }
        VehicleEntity vehicleEntity = optionalVehicleEntity.get();
        ContractEntity contractEntity = contractServiceJPA.findById(vehicleEntity.getContractId()).get();
        if (!vehicleEntity.getStatus().equals(VehicleEntity.Status.ACTIVATED.value)) {
            throw new EtcException("crm.vehicle.not.active");
        }
        if (vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value)
                || vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.CANCEL.value)
                || vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.TRANSFERRED.value)) {
            throw new EtcException("crm.vehicle.not.active");
        }
        VehicleEntity vehicleEntityOld = new Gson().fromJson(new Gson().toJson(vehicleEntity), VehicleEntity.class);

        List<VehicleEntity> vehicles = vehicleServiceJPA.findBySerialRFID(rfidDTO.getSerialRFID());
        if (vehicles.size() > 0) {
            throw new EtcException("crm.serial.was.attached.by.another.vehicle");
        }

        /***
         * Goi sang ben IM VDTC de lay chi tiet serial - 29112020
         */
        StockEtagDTO stockEtagDTO = imService.getSerialDetails(rfidDTO.getSerialRFID(), authentication);
        // END Goi sang ben IM VDTC de lay chi tiet serial


        /***
         * * Check User có partner_code khong
         *          * Neu co thi goi sang IM VDTC
         *          * Neu khong co thi chay luong cu goi IM VTT
         */
        StockTransEtagDTO stockTransEtagDTO = checkAndVerifySerialIMVDTC(rfidDTO.getSerialRFID(), authentication, vehicleEntity, CASE_UPDATE_VEHICLE.SWAP_VEHICLE);
        if (stockTransEtagDTO == null) {
            // luong chay sang IM VTT nhu bt
            SaleBusinessMesDTO saleBusinessMesDTO = productService.callBCCSIM(authentication, rfidDTO.getActTypeId(), vehicleEntity, rfidDTO.getSerialRFID(), vehicleEntity.getCustId(), CASE_UPDATE_VEHICLE.SWAP_VEHICLE, "");
            if (saleBusinessMesDTO == null || "false".equals(saleBusinessMesDTO.getSuccessResult())) {
                throw new EtcException("crm.register.vehicle.im");
            }
        } else {
            updateSerialIMVDTC(stockTransEtagDTO, IM_VDTC_STATUS.SUCCESS, authentication);
        }

        OCSResponse response = ocsService.deleteVehicle(vehicleEntity.getEpc(), String.valueOf(vehicleEntity.getContractId()), authentication, (int) rfidDTO.getActTypeId());
        if (FnCommon.checkOcsCode(response)) {
            vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value);
            vehicleEntity.setRfidSerial(rfidDTO.getSerialRFID());
            vehicleEntity.setTid(stockEtagDTO.getTid());
            vehicleEntity.setEpc(stockEtagDTO.getEpc());
            AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO().entityToAddVehicleRequestDTO(vehicleEntity, vehicleEntity.getContractId());
            addVehicleRequestDTO.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
            OCSResponse resCreateVehicle = ocsService.createVehicleOCS(addVehicleRequestDTO, authentication, rfidDTO.getActTypeId());
            if (FnCommon.checkOcsCode(resCreateVehicle)) {
                vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
            } else {
                updateSerialIMVDTC(stockTransEtagDTO, IM_VDTC_STATUS.FAILED, authentication);
                throw new EtcException("ocs.create.vehicle.fail" + ": " + resCreateVehicle.getDescription());
            }
            vehicleServiceJPA.save(vehicleEntity);
            // update action audit
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, rfidDTO.getReasonId(), rfidDTO.getActTypeId(), rfidDTO.getCustId(), vehicleEntity.getContractId(), vehicleId, ip));
            //update action audit detail
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleId);
            actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
            //update sale trans
            if (rfidDTO.getAmount() != null && rfidDTO.getAmount() > Constants.AMOUNT_ZERO) {
                addSaleTransAndSaleTransDetails(rfidDTO.getCustId(), contractEntity.getContractId(), contractEntity.getContractNo(), vehicleId, authentication, rfidDTO.getActTypeId());
            }
            List<SaleTransDetailEntity> listSaleTransDetail = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntityOld.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityOld.getPlateTypeCode()), vehicleEntityOld.getEpc(), Constants.BOO1);
            if (listSaleTransDetail != null && listSaleTransDetail.size() > 0) {
                ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
                reqOnlineEventSyncDTO.setEtag_old(vehicleEntityOld.getEpc());
                reqOnlineEventSyncDTO.setEtag_new(stockEtagDTO.getEpc());
                reqOnlineEventSyncDTO.setRequest_id(System.currentTimeMillis());
                reqOnlineEventSyncDTO.setRequest_datetime(System.currentTimeMillis());
                reqOnlineEventSyncDTO.setReason(Constants.REASON_CHANGE_BOO.CHANGE_EPC);
                updateExceptionListAndSaleTransDetail(listSaleTransDetail, null, stockEtagDTO, vehicleEntity.getPlateNumber(), vehicleEntity.getPlateTypeCode());
                boo1Service.onlineEventSync(reqOnlineEventSyncDTO, authentication, rfidDTO.getActTypeId());
            }

            List<SaleTransDetailEntity> listSaleTransDetailBoo2 = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntityOld.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityOld.getPlateTypeCode()), vehicleEntityOld.getEpc(), Constants.BOO2);
            List<ExceptionListEntity> listExceptionListBoo2 = exceptionListServiceJPA.findExceptionListStillEffectiveDate(vehicleEntityOld.getEpc(), vehicleEntityOld.getPlateNumber());
            if (listSaleTransDetailBoo2 != null && listSaleTransDetailBoo2.size() > 0 || listExceptionListBoo2 != null && listExceptionListBoo2.size() > 0) {
                addTicketWhenSwapRFID(authentication, contractEntity.getContractId(), stockEtagDTO.getEpc(), listSaleTransDetailBoo2);
                addOfferExceptionListWhenSwapRFID(authentication, contractEntity.getContractId(), stockEtagDTO.getEpc(), listExceptionListBoo2);
            }
            if (rfidDTO.getVehicleProfileIdDelete().size() > Constants.SIZE_LIST_ZERO) {
                deleteProfileVehicle(rfidDTO.getVehicleProfileIdDelete());
            }
            if (!Objects.isNull(rfidDTO.getVehicleProfiles())) {
                updateProfile(currentDate, authentication, vehicleEntity.getContractId(), vehicleEntity.getVehicleId(), rfidDTO.getActTypeId(), rfidDTO.getReasonId(), rfidDTO.getVehicleProfiles());
            }
            return true;
        } else {
            updateSerialIMVDTC(stockTransEtagDTO, IM_VDTC_STATUS.FAILED, authentication);
            throw new DataNotFoundException("ocs.can.not.delete.RFID");
        }
    }

    /**
     * Cap nhat the serial cho phuong tien
     *
     * @param rfidDTO
     * @param activeStatus
     * @param authentication
     * @return
     * @throws RuntimeException
     * @throws UnknownHostException
     * @throws JSONException
     * @throws IllegalAccessException
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusRFID(RFIDDTO rfidDTO, int activeStatus, Authentication authentication) throws RuntimeException, UnknownHostException, JSONException, IllegalAccessException {
        long vehicleId = rfidDTO.getVehicleId();
        Optional<VehicleEntity> optionalVehicleEntity = vehicleServiceJPA.findById(vehicleId);
        if (!optionalVehicleEntity.isPresent()) {
            LOGGER.error("active RFID, vehicle not found, vehicleId: {}", vehicleId);
            return false;
        }
        VehicleEntity vehicleEntity = optionalVehicleEntity.get();
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        VehicleEntity vehicleEntityOld = gson.fromJson(gson.toJson(vehicleEntity), VehicleEntity.class);
        vehicleEntity.setActiveStatus(String.valueOf(activeStatus));
        //call OCS update status;
        String ocsStatus = null;
        if (activeStatus == Constants.RFIDStatus.CLOSE.code) {
            ocsStatus = UpdateVehicleRequestOCSDTO.Status.SUSPEND.value;
        } else if (activeStatus == Constants.RFIDStatus.OPEN.code) {
            ocsStatus = UpdateVehicleRequestOCSDTO.Status.ACTIVE.value;
        }
        OCSResponse response = ocsService.modifyVehicleOCS(UpdateVehicleRequestOCSDTO.builder()
                        .epc(vehicleEntity.getEpc())
                        .status(ocsStatus)
                        .actTypeId(rfidDTO.getActTypeId())
                        .build(),
                authentication, true);
        if ("0".equals(response.getResultCode())) {
            vehicleEntity = vehicleServiceJPA.save(vehicleEntity);
            ContractEntity contractEntity = contractServiceJPA.findById(vehicleEntity.getContractId()).get();
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, rfidDTO.getReasonId(), rfidDTO.getActTypeId(),
                    vehicleEntity.getCustId(), vehicleEntity.getContractId(), vehicleEntity.getVehicleId(), ip);
            actionAuditEntity = actionAuditService.updateLogToActionAudit(actionAuditEntity);

            ActionAuditDetailDTO actionAuditDetailDTO = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity);
            ActionAuditDetailEntity actionAuditDetailEntity = actionAuditDetailDTO.toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleEntity.getVehicleId());
            actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
            String saleTransCode = generateSaleTransCode(vehicleId, Integer.parseInt(SaleTransEntity.SaleTransType.PACKAGE.value), new java.util.Date());
            String userLogin = FnCommon.getUserLogin(authentication);
            List<ServiceFeeEntity> serviceFeeEntityList = serviceFeeRepositoryJPA.getByActTypeId(rfidDTO.getActTypeId());
            if (serviceFeeEntityList.size() == Constants.SIZE_LIST_ZERO) {
                throw new EtcException("crm.can.not.modify.vehicle");
            }
            ServiceFeeEntity serviceFeeEntity = serviceFeeEntityList.get(0);
            Long shopId = contractEntity.getShopId();
            String shopName = contractEntity.getShopName();
            String contractNo = contractEntity.getContractNo();
            //insert sale trans
            SaleTransEntity saleTransEntity = toSaleTrans(saleTransCode, shopId, shopName, serviceFeeEntity.getFee(),
                    vehicleEntity.getContractId(), contractNo, userLogin, rfidDTO.getCustId(), authentication);
            saleTransEntity = saleTransRepositoryJPA.save(saleTransEntity);
            // insert sale trans detail
            SaleTransDetailEntity saleTransDetailEntity = toSaleTransDetail(saleTransEntity.getSaleTransId()
                    , vehicleEntity, serviceFeeEntity, userLogin);
            saleTransDetailRepositoryJPA.save(saleTransDetailEntity);


            // Kiem tra xem ton tai trong BL WL MG khong va da co ve hieu luc ben BOO1 chua
            List<SaleTransDetailEntity> listSaleTransDetail = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntity.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode()), vehicleEntity.getEpc(), Constants.BOO1);
            List<ExceptionListEntity> listExceptionList = exceptionListServiceJPA.findExceptionListStillEffectiveDate(vehicleEntity.getEpc(), vehicleEntity.getPlateNumber());
            if (listSaleTransDetail != null && listSaleTransDetail.size() > 0 || listExceptionList != null && listExceptionList.size() > 0) {
                // Dong bo phuong tien sang BOO1 khi dong mo etag
                ReqOnlineEventSyncDTO req = new ReqOnlineEventSyncDTO();
                req.setEtag_old(vehicleEntity.getEpc());
                req.setReason(Constants.REASON_CHANGE_BOO.OPEN_OR_CLOSE_EPC);
                req.setRequest_id(System.currentTimeMillis());
                req.setRequest_datetime(System.currentTimeMillis());
                if (activeStatus == Constants.RFIDStatus.CLOSE.code) {
                    req.setEtag_status_old(String.valueOf(Constants.BooEtagStatus.OPEN.code));
                    req.setEtag_status_new(String.valueOf(Constants.BooEtagStatus.CLOSE.code));
                } else if (activeStatus == Constants.RFIDStatus.OPEN.code) {
                    req.setEtag_status_old(String.valueOf(Constants.BooEtagStatus.CLOSE.code));
                    req.setEtag_status_new(String.valueOf(Constants.BooEtagStatus.OPEN.code));
                }

                boo1Service.onlineEventSync(req, authentication, rfidDTO.getActTypeId());
            }

            return true;
        } else {
            throw new RuntimeException("Update status ocs fail");
        }
    }

    /**
     * Noi ten table va id
     *
     * @param tableName
     * @param id
     * @return
     */
    private String buildCategoryKeyByCode(String tableName, String id) {
        return tableName + "_" + id;
    }

    /**
     * Lay danh sach id cua DMDC
     *
     * @param authentication
     * @param categoryMap
     */
    private void setValueCategoryMap(Authentication authentication, Map<String, Map<String, String>> categoryMap) {
        try {
            ResultSelectEntity listResult = categoriesService.findCategoriesCustomer(FnCommon.getStringToken(authentication));
            List<Map<String, String>> listResultDTO = (List<Map<String, String>>) listResult.getListData();
            if (listResultDTO != null) {
                for (Map<String, String> temp : listResultDTO) {
                    categoryMap.put(buildCategoryKeyByCode(temp.get("table_name"), temp.get("id")), temp);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error call categories", ex);
        }
    }

    /**
     * Validate input dau vao khi dang ki phuong tien
     *
     * @param addVehicleRequestDTO
     */
    public void validateRegisterVehicle(AddVehicleRequestDTO addVehicleRequestDTO) throws EtcException {
        // validate bien so xe
        List<String> listError = new ArrayList<>();
        if (FnCommon.isNullOrEmpty(addVehicleRequestDTO.getPlateNumber())) {
            listError.add("common.validate.plate.number.not.empty");
        }

        if (!validatePlateNumber(addVehicleRequestDTO.getPlateNumber())) {
            listError.add("common.validate.plate.number.only.number.letter");
        }

        if (FnCommon.validateMaxlengthString(addVehicleRequestDTO.getPlateNumber(), 16)) {
            listError.add("common.validate.plate.number.max.length");
        }

        if (!FnCommon.validatePlateContainsTVX(addVehicleRequestDTO.getPlateNumber())) {
            if (!FnCommon.validatePlateContainsTVX(addVehicleRequestDTO.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(addVehicleRequestDTO.getPlateTypeCode()))) {
                listError.add("common.validate.plate.number");
            }
        }

        // validate chu phuong tien
        if (FnCommon.isNullOrEmpty(addVehicleRequestDTO.getOwner())) {
            listError.add("common.validate.owner.not.empty");
        }

        if (FnCommon.validateMaxlengthString(addVehicleRequestDTO.getOwner(), 255)) {
            listError.add("common.validate.owner.maxlength");
        }

        if (addVehicleRequestDTO.getVehicleTypeId() == null) {
            listError.add("common.validate.vehicle.type.not.empty");
        }

        // validate khoi luong toan ban than
        if (addVehicleRequestDTO.getNetWeight() != null) {
            if (addVehicleRequestDTO.getNetWeight() < 0 || FnCommon.validateMaxlengthDouble(addVehicleRequestDTO.getNetWeight(), 6)) {
                listError.add("common.validate.net.weight.min.max");
            }
        }
        // validate khoi luong hang hoa
        if (addVehicleRequestDTO.getCargoWeight() != null) {
            if (addVehicleRequestDTO.getCargoWeight() < 0 || FnCommon.validateMaxlengthDouble(addVehicleRequestDTO.getCargoWeight(), 6)) {
                listError.add("common.validate.cargo.weight.min.max");
            }
        }

        // validate khoi luong toan bo
        if (!Objects.isNull(addVehicleRequestDTO.getGrossWeight())) {
            if (addVehicleRequestDTO.getGrossWeight() < 0 || FnCommon.validateMaxlengthDouble(addVehicleRequestDTO.getGrossWeight(), 6)) {
                listError.add("common.validate.gross.weight.min.max");
            }
        }
        // validate khoi luong keo theo
        if (!Objects.isNull(addVehicleRequestDTO.getPullingWeight())) {
            if (addVehicleRequestDTO.getPullingWeight() < 0 || FnCommon.validateMaxlengthDouble(addVehicleRequestDTO.getPullingWeight(), 6)) {
                listError.add("common.validate.pulling.weight.min.max");
            }
        }

        // validate so cho ngoi
        if (addVehicleRequestDTO.getSeatNumber() == null) {
            listError.add("common.validate.seat.number.not.empty");
        }
        if (addVehicleRequestDTO.getSeatNumber() < 0 || addVehicleRequestDTO.getSeatNumber() >= 1000) {
            listError.add("common.validate.seat.number.min.max");
        }
        // validate loai bien
        if (addVehicleRequestDTO.getPlateTypeId() == null) {
            listError.add("common.validate.plate.type.not.empty");
        }
        // validate so serial
        if (addVehicleRequestDTO.getRfidSerial() == null) {
            listError.add("common.validate.serial.not.empty");
        }
        // validate so may
        if (!Objects.isNull(addVehicleRequestDTO.getEngineNumber())) {
            if (FnCommon.validateMaxlengthString(addVehicleRequestDTO.getEngineNumber().trim(), 50)) {
                listError.add("common.validate.engine.number.max.length");
            }
        }

        // validate so khung
        if (!Objects.isNull(addVehicleRequestDTO.getChassicNumber())) {
            if (FnCommon.validateMaxlengthString(addVehicleRequestDTO.getChassicNumber().trim(), 50)) {
                listError.add("common.validate.chassic.number.max.length");
            }
        }
        // validate vehicleGroupId
        if (Objects.isNull(addVehicleRequestDTO.getVehicleGroupId())) {
            listError.add("crm.validate.vehicle.group.id");
        }
        // validate vehicleGroupCode
        if (FnCommon.isNullOrEmpty(addVehicleRequestDTO.getVehicleGroupCode())) {
            listError.add("crm.validate.vehicle.group.code");
        }
        if (listError.size() > 0) {
            throw new EtcException(listError.get(0));
        }
    }

    /**
     * update profile contract
     *
     * @param currDate
     * @param userLogin
     * @param vehicleTransferDTO
     * @param ip
     */
    private void updateContractProfile(Authentication authentication, Date currDate, String userLogin, VehicleTransferDTO vehicleTransferDTO, String ip) throws Exception, EtcException {
        if (CollectionUtils.isEmpty(vehicleTransferDTO.getContractProfileDTOList())) {
            return;
        }
        for (ContractProfileDTO contractProfileDTO : vehicleTransferDTO.getContractProfileDTOList()) {
            ContractProfileEntity contractProfileEntity = new ContractProfileEntity();
            if (!Base64.isBase64(contractProfileDTO.getFileBase64())) {
                throw new EtcException("crm.file.is.not.base64");
            }
            byte[] file = Base64.decodeBase64(contractProfileDTO.getFileBase64());
            String filePath = String.format("/%s/%s/%s", contractProfileDTO.getCustId(), contractProfileDTO.getContractId(), UUID.randomUUID().toString() + "-" + contractProfileDTO.getFileName());
            if (!FnCommon.checkBriefcaseValid(filePath, file, briefcaseMaxFileSize)) {
                throw new EtcException("crm.briefcase.not.accepted");
            }
            contractProfileEntity.setCustId(contractProfileDTO.getCustId());
            contractProfileEntity.setContractId(contractProfileDTO.getContractId());
            contractProfileEntity.setDocumentTypeId(contractProfileDTO.getDocumentTypeId());
            contractProfileEntity.setFileName(contractProfileDTO.getFileName());
            contractProfileEntity.setFilePath(filePath);
            contractProfileEntity.setFileSize(file);
            contractProfileEntity.setCreateDate(currDate);
            contractProfileEntity.setCreateUser(userLogin);
            contractProfileEntity.setStatus(ContractProfileEntity.Status.EXIST.value);
            fileService.uploadFile(filePath, file);
            contractProfileRepositoryJPA.save(contractProfileEntity);
            //khong luu base 64 vao trong profiles
            contractProfileEntity.setFileSize(null);
            updateLogProfile(authentication, vehicleTransferDTO, contractProfileEntity, ip);
        }
    }

    /**
     * Ghi log cho phan them moi profile
     *
     * @param vehicleTransferDTO
     * @param contractProfileEntity
     * @param ip
     */
    private void updateLogProfile(Authentication authentication, VehicleTransferDTO vehicleTransferDTO, ContractProfileEntity contractProfileEntity, String ip) throws Exception {
        // update action audit
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, vehicleTransferDTO.getReasonId(), vehicleTransferDTO.getActTypeId(), contractProfileEntity.getCustId(), vehicleTransferDTO.getTransferContractId(), null, ip));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(contractProfileEntity).toEntity(actionAuditEntity.getActionAuditId(), ContractProfileEntity.class.getAnnotation(Table.class).name(), contractProfileEntity.getContractProfileId());
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
    }

    /**
     * Luu  phuong tien import
     *
     * @param dataParams
     * @param authentication
     * @param customerId
     * @param contractId
     */
    public VehicleEntity saveVehicle(AddVehicleRequestDTO dataParams, Authentication authentication, Long customerId, Long contractId, boolean isImport) throws IOException, DataNotFoundException, EtcException {
        String userLogin = FnCommon.getUserLogin(authentication);
        dataParams.setApprovedUser(userLogin);
        Map<String, Object> getAttribute = FnCommon.getAttribute(authentication);
        Object shopId = getAttribute.get(USER_ATTRIBUTE.SHOP_ID);
        dataParams.setShopId(Long.parseLong(shopId.toString()));
        VehicleEntity vehicleEntity = dataParams.toAddVehicleEntity(customerId, contractId, userLogin, null);
        if (isImport) {
            vehicleEntity.setStatus(VehicleEntity.Status.IMPORT.value);
        }
        vehicleEntity.setOfferId(Long.parseLong(vehiclesOfferId));
        vehicleEntity.setOfferCode(vehiclesOfferCode);
        vehicleEntity.setProfileStatus(VehicleEntity.ProfilesStatus.NOT_RECEIVED.value);
        return vehicleEntity;
    }

    /**
     * Download template file excel danh sach phuong tien
     *
     * @param
     * @return file excel
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    @Override
    public String downloadVehiclesTemplate(Authentication authentication) {
        String result = "/template/import/vehicle_template.xlsx";
        try {
            String token = FnCommon.getStringToken(authentication);
            ResultSelectEntity categories = categoriesService.findCategoriesCustomer(token);
            List<LinkedHashMap<?, ?>> vehicleTypes = vehicleTypeService.findAllVehicleType(token);
            if (categories != null && !categories.getListData().isEmpty()) {
                List<LinkedTreeMap<?, ?>> lstCategory = (List<LinkedTreeMap<?, ?>>) categories.getListData();
                readAndRewritingExcelFile(lstCategory, vehicleTypes);
                result = System.getProperty("user.dir") + File.separator + "vehicle-template-result.xlsx";
            }

        } catch (Exception e) {
            LOGGER.error(String.format("Lay danh sach danh muc loi : %s ", e));
        }
        return result;
    }

    /**
     * Doc va thay doi du lieu tu file template
     *
     * @param lstCategory  Danh sach danh muc dung chung
     * @param vehicleTypes Danh sach loai phuong tien
     * @throws IOException Loi khi xu ly file
     */
    public void readAndRewritingExcelFile(List<LinkedTreeMap<?, ?>> lstCategory, List<LinkedHashMap<?, ?>> vehicleTypes) throws IOException {
        try (InputStream inp = getClass().getResourceAsStream("/template/import/vehicle_template.xlsx"); Workbook wb = WorkbookFactory.create(inp)) {
            Sheet sheet = wb.getSheetAt(1);
            Map<String, List<LinkedTreeMap<?, ?>>> mapCategories = lstCategory.stream()
                    .filter(category -> tableNameCategories.contains(category.get("table_name").toString()))
                    .collect(Collectors.groupingBy(category -> category.get("table_name").toString()));
            for (Map.Entry<String, List<LinkedTreeMap<?, ?>>> mapCategory : mapCategories.entrySet()) {
                rewritingContentExcel(mapCategory.getKey(), mapCategories.get(mapCategory.getKey()), sheet, wb);
            }
            setVehicleTypeExcel(vehicleTypes, 0, 2, sheet, wb);
            bindingDataToDropdownListExcel(wb, mapCategories, vehicleTypes);
            try (OutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + File.separator + "vehicle-template-result.xlsx")) {
                wb.write(fileOut);
            }
        } catch (IOException e) {
            LOGGER.error("Tao file template khong thanh cong: ", e);
        }
    }


    /**
     * Set du lieu loai phuong tien cho cot excel
     *
     * @param vehicleTypes Danh sach loai phuong tien
     * @param fromCell     Cot bat dau xu ly
     * @param toCell       Cot ket thuc xu ly
     */
    public void setVehicleTypeExcel(List<LinkedHashMap<?, ?>> vehicleTypes, int fromCell, int toCell, Sheet sheet, Workbook wb) {
        if (vehicleTypes != null) {
            int index = 2;
            for (LinkedHashMap<?, ?> vehicle : vehicleTypes) {
                Row row = sheet.getRow(index) != null ? sheet.getRow(index) : sheet.createRow(index);
                for (int i = fromCell; i <= toCell; i++) {
                    if (i == fromCell) {
                        createAndSetStyleCell(wb, row, i, String.valueOf(index - 1));
                    } else if (i > fromCell && i < toCell) {
                        String vehicleName = vehicle.get("name") != null ? vehicle.get("name").toString() : "";
                        String vehicleId = vehicle.get("id") != null ? vehicle.get("id").toString() : "";
                        String vehicleCode = vehicle.get("code") != null ? vehicle.get("code").toString() : "";
                        createAndSetStyleCell(wb, row, i, vehicleName + "-" + vehicleId + "-" + vehicleCode);
                    } else {
                        String value = vehicle.get("id") != null ? vehicle.get("id").toString() : "";
                        createAndSetStyleCell(wb, row, i, value);
                    }
                }
                index++;
            }
        }
    }


    /**
     * Kiem tra dieu kien ghi noi dung file
     *
     * @param tableName  Ten bang can set gia tri
     * @param categories Danh sach danh muc dung chung
     * @param sheet      Sheet duoc lua chon thao tac
     */
    public void rewritingContentExcel(String tableName, List<LinkedTreeMap<?, ?>> categories,
                                      Sheet sheet, Workbook wb) {
        switch (tableName) {
            case "PLATE_TYPE":
                doRewritingContentExcel(16, 18, categories, sheet, wb);
                break;

            case "VEHICLE_BRAND":
                doRewritingContentExcel(12, 14, categories, sheet, wb);
                break;

            case "VEHICLE_MARK":
                doRewritingContentExcel(8, 10, categories, sheet, wb);
                break;

            case "VEHICLE_COLOUR":
                doRewritingContentExcel(4, 6, categories, sheet, wb);
                break;

            default:
                break;
        }
    }

    /**
     * Thuc hien ghi đè noi dung file template
     *
     * @param fromCell   Cot bat dau xu ly
     * @param toCell     Cot ket thuc xu ly du lieu
     * @param categories Danh sach danh muc dung chung
     * @param sheet      Sheet lua con thuc hien doc ghi du lieu
     */
    public void doRewritingContentExcel(int fromCell, int toCell, List<LinkedTreeMap<?, ?>> categories,
                                        Sheet sheet, Workbook wb) {
        int index = 2;
        for (LinkedTreeMap<?, ?> category : categories) {
            Row row = sheet.getRow(index) != null ? sheet.getRow(index) : sheet.createRow(index);
            for (int i = fromCell; i <= toCell; i++) {
                if (i == fromCell) {
                    createAndSetStyleCell(wb, row, i, String.valueOf(index - 1));
                } else if (i > fromCell && i < toCell) {
                    String name = category.get("name") != null ? category.get("name").toString() : "";
                    String id = category.get("id") != null ? category.get("id").toString() : "";
                    String code = category.get("code") != null ? category.get("code").toString() : "";
                    createAndSetStyleCell(wb, row, i, name + "-" + id + "-" + code);
                } else {
                    String id = category.get("id") != null ? category.get("id").toString() : "";
                    createAndSetStyleCell(wb, row, i, id);
                }
            }
            index++;

        }

    }

    /**
     * Tao va set style cho column cua excel
     *
     * @param wb     Workbook thao tac voi file excel
     * @param row    Hang can set style
     * @param column Cot can set style
     * @param value  Du lieu set cho cot
     */
    private void createAndSetStyleCell(Workbook wb, Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.GREEN.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cell.setCellStyle(cellStyle);
    }

    /**
     * Set trang thai phuong tien khi goi IM thanh cong
     *
     * @param vehicleEntity
     */
    void setStatusCallImSuccess(VehicleEntity vehicleEntity) {
        vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value);
        vehicleRepositoryJPA.save(vehicleEntity);
    }

    /**
     * Set gia tri de goi sang OCS dang ki phuong tien
     *
     * @param requestDTO
     * @param vehicleEntity
     * @param contractId
     * @param rfidSerial
     * @param actTypeId
     * @return
     */
    private AddVehicleRequestDTO setDTOtoOCS(AddVehicleRequestDTO requestDTO, VehicleEntity vehicleEntity, Long contractId, Long rfidSerial, Long actTypeId) {
        requestDTO.setEpc(vehicleEntity.getEpc());
        requestDTO.setTid(vehicleEntity.getTid());
        requestDTO.setContractId(contractId);
        requestDTO.setVehicleTypeId(vehicleEntity.getVehicleTypeId());
        requestDTO.setRfidSerial(rfidSerial.toString());
        requestDTO.setPlateNumber(vehicleEntity.getPlateNumber());
        requestDTO.setPlateType(vehicleEntity.getPlateType());
        requestDTO.setActTypeId(actTypeId);
        requestDTO.setRfidType(vehicleEntity.getRfidType());
        requestDTO.setActiveStatus(vehicleEntity.getActiveStatus());
        requestDTO.setVehicleGroupId(vehicleEntity.getVehicleGroupId());
        requestDTO.setStatus(vehicleEntity.getStatus());
        requestDTO.setSeatNumber(vehicleEntity.getSeatNumber());
        if (vehicleEntity.getCargoWeight() != null) {
            requestDTO.setCargoWeight(vehicleEntity.getCargoWeight());
        }
        if (vehicleEntity.getEffDate() != null) {
            requestDTO.setEffDate(vehicleEntity.getEffDate());
        } else {
            requestDTO.setEffDate(new java.util.Date());
        }
        if (vehicleEntity.getExpDate() != null) {
            requestDTO.setExpDate(vehicleEntity.getExpDate());
        } else {
            requestDTO.setExpDate(FnCommon.addYears(new java.util.Date(), 50));
        }
        return requestDTO;
    }

    /**
     * Xoa ho so phuong tien
     *
     * @param authentication
     * @param profileId
     * @param vehicleId
     * @param updateVehicleProfileDTO
     * @throws IOException
     * @throws EtcException
     */
    @Override
    public void deleteProfile(Authentication authentication, Long profileId, Long vehicleId, UpdateVehicleProfileDTO updateVehicleProfileDTO) throws IOException, EtcException {
        VehicleProfileEntity vehicleProfileEntity = this.vehicleProfileRepositoryJPA.findById(profileId).orElseThrow(new EtcException("common.profiles.not-exists"));
        fileService.removeFile(vehicleProfileEntity.getFilePath());
        this.vehicleProfileRepositoryJPA.deleteById(vehicleProfileEntity.getVehicleProfileId());
    }

    /**
     * Tra lai trang thai phuong tien import truoc khi goi IM
     *
     * @param vehicleEntity
     */
    void returnStatusImportBeforeCallIM(VehicleEntity vehicleEntity) {
        vehicleEntity.setStatus(VehicleEntity.Status.SIMILAR.value);
        vehicleEntity.setActiveStatus(null);
        vehicleEntity.setRfidSerial(null);
        vehicleEntity.setTid(null);
        vehicleEntity.setEpc(null);
        vehicleEntity.setRfidType(null);
        vehicleRepositoryJPA.save(vehicleEntity);
    }

    /**
     * Tra lai trang thai phuong tien  truoc khi goi IM
     *
     * @param vehicleEntity
     */
    void returnStatusImportBeforeCallOCS(VehicleEntity vehicleEntity) {
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value);
        vehicleRepositoryJPA.save(vehicleEntity);
    }

    /**
     * Lay thong tin dang kiem
     *
     * @return
     */
    @Override
    public ResponseGetInfoRegisterDTO getVehicleRegistry(String plateNumber, Authentication authentication) throws Exception {
        plateNumber = plateNumber.replaceAll("[\\-\\.]", "").trim();
        String plateTypeCode = FnCommon.getPlateTypeCodeFromPlateNumber(plateNumber);
        if (FnCommon.checkPlate5Number2Char(plateNumber) && (plateNumber.endsWith("T") || plateNumber.endsWith("X") || plateNumber.endsWith("V"))) {
            plateNumber = plateNumber.substring(0, plateNumber.length() - 1);
        }
        if (!FnCommon.validatePlateContainsTVX(plateNumber)) {
            throw new EtcException("common.validate.plate.number");
        }
        String plate = FnCommon.getPlateNumberBoo1(plateNumber, "");
        VehicleEntity vehicleEntity = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(FnCommon.formatPlateBOO1(plateNumber), plateTypeCode);
        if (vehicleEntity == null) {
            ReqActivationCheckDTO req = new ReqActivationCheckDTO();
            req.setPlate(plate);
            req.setRequest_id(System.currentTimeMillis());
            req.setRequest_datetime(System.currentTimeMillis());
            ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
            if (res != null && Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                return searchXCGService.getInfoRegister(plate, res.getEtag());
            }
        } else {
            return searchXCGService.getInfoRegister(plate, vehicleEntity.getEpc());
        }
        return searchXCGService.getInfoRegister(plate, null);
    }

    /**
     * Tim kiem ho so dinh kem theo vehicleId
     *
     * @param requestModel
     * @return
     */
    @Override
    public Object findProfileByVehicleId(VehicleProfileDTO requestModel) {
        return vehicleRepository.findProfileByVehicleId(requestModel);
    }

    /**
     * Luu lich su tac dong
     *
     * @param authentication
     * @param reasonId
     * @param actTypeId
     * @param contractId
     * @param customerId
     * @param id
     * @param entity
     * @param tableName
     * @return
     */
    private ActionAuditEntity writeLogNew(Authentication authentication, long reasonId, long actTypeId, long contractId, long customerId, long id, Object entity, String tableName) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, customerId, contractId, id, ip);
            actionAuditEntity = actionAuditService.updateLogToActionAudit(actionAuditEntity);

            ActionAuditDetailDTO actionAuditDetailDTO = new ActionAuditDetailDTO(entity);
            ActionAuditDetailEntity actionAuditDetailEntity = actionAuditDetailDTO.toEntity(actionAuditEntity.getActionAuditId(), tableName, id);
            actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
            return actionAuditEntity;
        } catch (Exception e) {
            LOGGER.error("Co loi khi ghi log: ", e);
            return null;
        }
    }

    /**
     * Lay ma ly do theo ma tac dong
     *
     * @param actTypeId
     * @return
     */
    private Long getReasonIdByactTypeId(Long actTypeId) {
        long reasonId = 0l;
        List<ActReasonEntity> list = actReasonRepositoryJPA.findAllByActTypeId(actTypeId);
        if (list != null && list.size() > 0) {
            ActReasonEntity actReasonEntity = list.get(0);
            reasonId = actReasonEntity.getActReasonId();
        }
        return reasonId;
    }

    /**
     * Them ho so dinh kem phuong tien
     *
     * @param authentication
     * @param currDate
     * @param userLogin
     * @param contractId
     * @param vehicleId
     * @param dataParams
     * @param reasonId
     * @param actTypeId
     * @param customerId
     * @param actionAuditId
     * @throws IOException
     * @throws EtcException
     */
    private void addNewVehicleProfile(Authentication authentication, Date currDate, String userLogin, Long contractId, Long vehicleId, List<VehicleProfileDTO> dataParams, long reasonId,
                                      long actTypeId, long customerId, long actionAuditId, VehicleEntity vehicleEntity) throws EtcException {
        List<VehicleProfileEntity> vehicleProfileEntities = new ArrayList<>();
        if (dataParams != null) {
            for (VehicleProfileDTO vehicleProfile : dataParams) {
                VehicleProfileEntity vehicleProfileEntity = new VehicleProfileEntity();
                if (!Base64.isBase64(vehicleProfile.getFileBase64())) {
                    returnStatusBeforeRegister(vehicleEntity);
                    throw new EtcException("crm.file.is.not.base64");
                }
                byte[] file = Base64.decodeBase64(vehicleProfile.getFileBase64());
                String filePath = String.format("/%s/%s/%s/%s", customerId, contractId, vehicleId, UUID.randomUUID().toString() + "-" + vehicleProfile.getFileName());
                if (!FnCommon.checkBriefcaseValid(filePath, file, briefcaseMaxFileSize)) {
                    returnStatusBeforeRegister(vehicleEntity);
                    throw new EtcException("crm.briefcase.invalid");
                }
                vehicleProfileEntity.setContractId(contractId);
                vehicleProfileEntity.setVehicleId(vehicleId);
                vehicleProfileEntity.setDocumentTypeId(vehicleProfile.getDocumentTypeId());
                vehicleProfileEntity.setFileName(vehicleProfile.getFileName());
                vehicleProfileEntity.setFilePath(filePath);
                vehicleProfileEntity.setFileSize(file);
                vehicleProfileEntity.setCreateDate(currDate);
                vehicleProfileEntity.setCreateUser(userLogin);

                vehicleProfileEntity.setStatus(VehicleProfileEntity.Status.EXIST.value);
                vehicleProfileEntities.add(vehicleProfileEntity);
                fileService.uploadFile(filePath, file);
            }
            List<VehicleProfileEntity> list = vehicleProfileRepositoryJPA.saveAll(vehicleProfileEntities);
            for (VehicleProfileEntity vehicleProfileEntity : list) {
                // khong luu base 64 vao trong DB
                vehicleProfileEntity.setFileSize(null);
                writeLogNewToProfiles(vehicleProfileEntity.getVehicleProfileId(), vehicleProfileEntity, VehicleProfileEntity.class.getAnnotation(Table.class).name(), actionAuditId);
            }
        }
    }

    /**
     * Set trang thai phuong tien truoc khi dang ki
     *
     * @param vehicleEntity
     */
    void returnStatusBeforeRegister(VehicleEntity vehicleEntity) {
        vehicleEntity.setStatus(VehicleEntity.Status.NOT_ACTIVATED.value);
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value);
        vehicleEntity.setRfidSerial(null);
        vehicleEntity.setTid(null);
        vehicleEntity.setEpc(null);
        vehicleEntity.setRfidType(null);
        vehicleRepositoryJPA.save(vehicleEntity);
    }

    /**
     * binding du lieu vao dropdownlist excel
     *
     * @param wb           workbook thuc hien xu ly file excel
     * @param categories   Danh sach danh muc dung chung
     * @param vehicleTypes Danh sach loai phuog tien
     */
    public void bindingDataToDropdownListExcel(Workbook wb, Map<String, List<LinkedTreeMap<?, ?>>> categories, List<LinkedHashMap<?, ?>> vehicleTypes) {
        for (Map.Entry<String, List<LinkedTreeMap<?, ?>>> mapCategory : categories.entrySet()) {
            rewritingContentExcel(mapCategory.getKey(), categories.get(mapCategory.getKey()), wb);
        }
        if (vehicleTypes != null) {
            doBindingDataToDropdownListExcel(wb, 3, vehicleTypes.size(), "B", 4, 4, 3, 3);
        }
    }

    /**
     * Kiem tra dieu kien ghi noi dung file
     *
     * @param tableName  Ten bang can set gia tri
     * @param categories Danh sach danh muc dung chung
     */
    public void rewritingContentExcel(String tableName, List<LinkedTreeMap<?, ?>> categories,
                                      Workbook wb) {
        switch (tableName) {
            case "PLATE_TYPE":
                doBindingDataToDropdownListExcel(wb, 3, categories.size(), "R", 4, 4, 14, 14);
                break;
            case "VEHICLE_BRAND":
                doBindingDataToDropdownListExcel(wb, 3, categories.size(), "N", 4, 4, 12, 12);
                break;

            case "VEHICLE_MARK":
                doBindingDataToDropdownListExcel(wb, 3, categories.size(), "J", 4, 4, 13, 13);
                break;

            case "VEHICLE_COLOUR":
                doBindingDataToDropdownListExcel(wb, 3, categories.size(), "F", 4, 4, 11, 11);
                break;

            default:
                break;
        }
    }

    /**
     * Thuc hien binding du lieu vao dropdownlist excel
     *
     * @param wb        Ten bang can set gia tri
     * @param startCell Cot bat dau de lay du lieu
     * @param lastCell  Cot ket thuc lay du lieu
     * @param cellName  Ten cot can set du lieu
     * @param firstRow  Vi tri hang bat dau set du lieu
     * @param lastRow   Vi tri hang ket thuc set du lieu
     * @param firstCol  Vi tri cot bat dau set du lieu
     * @param lastCol   Vi tri cot ket thuc set du lieu
     */
    public void doBindingDataToDropdownListExcel(Workbook wb, int startCell, int lastCell, String cellName, int firstRow, int lastRow, int firstCol, int lastCol) {
        String referFormula = "'Categories'!$" + cellName + "$" + String.valueOf(startCell) + ":$" + cellName + "$" + String.valueOf(lastCell + startCell - 1);
        Sheet sheet = wb.getSheetAt(0);
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint)
                dvHelper.createFormulaListConstraint(referFormula);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(
                dvConstraint, addressList);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }


    /***
     * Lay thong tin chi tiet phuong tien
     * @param itemParamsEntity
     * @return
     */
    @Override
    public Object getVehiclesByPlateNumber(VehicleSearchDTO itemParamsEntity) {
        ResultSelectEntity dataResult = null;
        if (itemParamsEntity.getIsPortalBot() != null && itemParamsEntity.getIsPortalBot()) {
            dataResult = vehicleRepository.getVehiclesByPlateNumberForPortalBot(itemParamsEntity);
        } else {
            dataResult = vehicleRepository.getVehiclesByPlateNumber(itemParamsEntity);
        }
        return dataResult;
    }

    @Override
    public Object getVehicle(VehicleDTO params) {
        if (params.getPlateNumber() != null) {
            if (!FnCommon.validatePlateContainsTVX(params.getPlateNumber())) {
                throw new EtcException("common.validate.plate.number");
            }
            String plateNumber = FnCommon.formatPlateBOO1(params.getPlateNumber());
            String plateTypeCode = FnCommon.getPlateTypeBOO1(params.getPlateNumber());
            params.setPlateNumber(plateNumber);
            params.setPlateTypeCode(plateTypeCode);
        }
        List<?> result = vehicleRepository.getVehicle(params).getListData();
        if (FnCommon.isNullOrEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * Validate bien so xe
     *
     * @param plateNumber
     * @return
     */

    private boolean validatePlateNumber(String plateNumber) {
        String regex = "\\w+";
        return plateNumber.matches(regex);
    }

    /**
     * Lay ma loai phuong tien
     *
     * @param vehicleType
     * @return
     */
    private String[] getIdAndCodeByName(String vehicleType) {
        String[] result = {};
        if (!Objects.isNull(vehicleType) && vehicleType.contains("-")) {
            result = vehicleType.split("-", 3);
        }
        return result;
    }

    /**
     * Check xe ton tai
     *
     * @param plateNumber
     */
    public boolean checkVehicleExist(String plateNumber) {
        boolean check = false;
        List<VehicleEntity> vehicleEntity = vehicleRepositoryJPA.findAllByPlateNumberAndStatusIsNot(plateNumber, VehicleEntity.Status.NOT_ACTIVATED.value);
        if (vehicleEntity != null && vehicleEntity.size() > 0) {
            check = true;
        }
        return check;
    }

    /**
     * Them thong tin giao dich/chi tiet thong tin giao dich
     *
     * @param custId
     * @param actionTypeId
     * @param authentication
     * @return
     */

    private void addSaleTransAndSaleTransDetails(long custId, Long contractId, String contractNo, Authentication authentication, Long vehicleId, long actionTypeId) {
        SaleTransEntity saleTranSaved = saleTransService.addSaleTrans(custId, contractId, contractNo, authentication, actionTypeId);
        if (saleTranSaved != null) {
            saleTransDetailService.addSaleTransDetail(authentication, saleTranSaved.getSaleTransId(), vehicleId, actionTypeId);
        }
    }

    /**
     * Them thong tin giao dich/chi tiet thong tin giao dich
     *
     * @param custId
     * @param actionTypeId
     * @param authentication
     * @return
     */
    private void addSaleTransAndSaleTransDetails(Long custId, Long contractId, String contractNo, Long vehicleId, Authentication authentication, long actionTypeId) {
        SaleTransEntity saleTranSaved = saleTransService.addSaleTrans(custId, contractId, contractNo, authentication, actionTypeId);
        if (saleTranSaved != null) {
            saleTransDetailService.addSaleTransDetail(authentication, saleTranSaved.getSaleTransId(), vehicleId, actionTypeId);
        }
    }

    private void addSaleTransAndSaleTransDetailsAddVehicle(Long custId, Long contractId, String contractNo, Long vehicleId, Authentication authentication, Long fee) {
        ServiceFeeDTO serviceFee = (ServiceFeeDTO) serviceFeeService.getDetailServiceFeeByReason(Constants.ACT_REASON.ACTIVE_RFID);
        SaleTransEntity saleTranSaved = saleTransService.addSaleTrans(custId, contractId, contractNo, authentication, serviceFee.getActionTypeId());
        if (saleTranSaved != null) {
            saleTransDetailService.addSaleTransDetailAddVehicle(authentication, saleTranSaved.getSaleTransId(), vehicleId, fee, serviceFee);
        }
    }

    /**
     * Lam tron cac truong khoi luong
     *
     * @param addVehicleRequestDTO
     */
    private void roundWeightVehicle(AddVehicleRequestDTO addVehicleRequestDTO) {
        if (!Objects.isNull(addVehicleRequestDTO.getNetWeight())) {
            Double netWeight = FnCommon.round(addVehicleRequestDTO.getNetWeight());
            addVehicleRequestDTO.setNetWeight(netWeight);
        }
        if (!Objects.isNull(addVehicleRequestDTO.getCargoWeight())) {
            Double cargoWeight = FnCommon.round(addVehicleRequestDTO.getCargoWeight());
            addVehicleRequestDTO.setCargoWeight(cargoWeight);
        }
        if (!Objects.isNull(addVehicleRequestDTO.getGrossWeight())) {
            Double grossWeight = FnCommon.round(addVehicleRequestDTO.getGrossWeight());
            addVehicleRequestDTO.setGrossWeight(grossWeight);
        }
        if (!Objects.isNull(addVehicleRequestDTO.getPullingWeight())) {
            Double pullingWeight = FnCommon.round(addVehicleRequestDTO.getPullingWeight());
            addVehicleRequestDTO.setPullingWeight(pullingWeight);
        }
    }

    /***
     * Gan thong tin the cho phuong tien sau khi dang ki vơi IM
     * @param addVehicleRequestDTO
     * @param contractId
     * @param vehicleEntity
     * @return
     */
    private void setSerialToVehicle(AddVehicleRequestDTO addVehicleRequestDTO, long contractId, VehicleEntity vehicleEntity) {
        addVehicleRequestDTO.setContractId(contractId);
        addVehicleRequestDTO.setPlateType(addVehicleRequestDTO.getPlateTypeId());
        addVehicleRequestDTO.setActiveStatus(vehicleEntity.getActiveStatus());
        addVehicleRequestDTO.setRfidType(vehicleEntity.getRfidType());
        addVehicleRequestDTO.setEffDate(vehicleEntity.getEffDate());
        addVehicleRequestDTO.setExpDate(vehicleEntity.getExpDate());
        addVehicleRequestDTO.setStatus(vehicleEntity.getStatus());
        addVehicleRequestDTO.setSeatNumber(vehicleEntity.getSeatNumber());
        if (vehicleEntity.getCargoWeight() != null) {
            addVehicleRequestDTO.setCargoWeight(vehicleEntity.getCargoWeight());
        }
    }

    /**
     * Validate serial co activeStatus =  1 ton tai trong bang vehicle
     *
     * @param rfidSerial
     * @return
     */
    private boolean validateRfidSerial(String rfidSerial) {
        boolean exist = false;
        List<VehicleEntity> vehicleEntity = vehicleRepositoryJPA.findAllByStatusAndRfidSerial(VehicleEntity.Status.ACTIVATED.value, rfidSerial);
        if (vehicleEntity != null && vehicleEntity.size() > 0) {
            exist = true;
        }
        return exist;
    }

    /**
     * Lay ma loai phuong tien tinh phi
     *
     * @param addVehicleRequestDTOS
     * @param vehicleGroupIdResponseDTO
     * @param sheet
     * @param listError
     * @return
     */
    private int mapVehicleGroupId(List<AddVehicleRequestDTO> addVehicleRequestDTOS, List<VehicleGroupIdResponseDTO.VehicleGroup> vehicleGroupIdResponseDTO,
                                  Sheet sheet, List<String> listError) {
        Map<Integer, VehicleGroupIdResponseDTO.VehicleGroup> maps = vehicleGroupIdResponseDTO.stream().collect(Collectors.toMap(VehicleGroupIdResponseDTO.VehicleGroup::getId, Function.identity()));
        for (int i = 0; i < addVehicleRequestDTOS.size(); i++) {
            if (maps.containsKey(addVehicleRequestDTOS.get(i).getId())) {
                addVehicleRequestDTOS.get(i).setVehicleGroupId(maps.get(addVehicleRequestDTOS.get(i).getId()).getVehicleGroupId());
                addVehicleRequestDTOS.get(i).setVehicleGroupCode(maps.get(addVehicleRequestDTOS.get(i).getId()).getVehicleGroupCode());
            } else {
                Row row = sheet.getRow(addVehicleRequestDTOS.get(i).getId());
                String result = (getStringValue(row.getCell(16)));
                result = result + jedisCacheService.getMessageErrorByKey("crm.can.not.found.vehicle.group.id");
                row.getCell(16).setCellValue(result);
                listError.add(result);
            }
        }
        return listError.size();
    }

    /**
     * Luu lich su tac dong
     *
     * @param id
     * @param entity
     * @param tableName
     * @param actionAuditId
     */
    private void writeLogNewToProfiles(long id, Object entity, String tableName, long actionAuditId) {
        try {
            ActionAuditDetailDTO actionAuditDetailDTO = new ActionAuditDetailDTO(entity);
            ActionAuditDetailEntity actionAuditDetailEntity = actionAuditDetailDTO.toEntity(actionAuditId, tableName, id);
            actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
        } catch (Exception e) {
            LOGGER.error("Co loi khi ghi log: ", e);
        }
    }

    /**
     * Call OCS cua chuc nang chuyen chu quyen
     *
     * @param transferContractId
     * @param vehicleEntityUpdate
     * @param authentication
     * @param actTypeId
     */
    private OCSResponse callOCSTransfer(Long transferContractId, VehicleEntity vehicleEntityUpdate, Authentication authentication, Long actTypeId) {
        OCSResponse result = ocsService.transferVehicle(vehicleEntityUpdate.getEpc(), vehicleEntityUpdate.getTid(), String.valueOf(vehicleEntityUpdate.getContractId()), String.valueOf(transferContractId), authentication, actTypeId.intValue());
        if (result != null && !"0".equals(result.getResultCode())) {
            throw new EtcException("Call transfer vehicle OCS fall :" + result.getDescription());
        }
        return result;
    }

    /**
     * Kich hoat the
     *
     * @param rfidDTO
     * @param authentication
     * @return
     * @throws UnknownHostException
     * @throws JSONException
     * @throws IllegalAccessException
     */
    @Transactional(rollbackFor = Exception.class)
    public ActiveRFIDResponseDTO.ActiveResponses activeRFIDOCS(RFIDDTO rfidDTO, Authentication authentication) throws UnknownHostException, JSONException, IllegalAccessException {
        long vehicleId = rfidDTO.getVehicleId();
        ActiveRFIDResponseDTO.ActiveResponses activeResponses = new ActiveRFIDResponseDTO.ActiveResponses();
        activeResponses.setResult("FAIL");
        activeResponses.setVehicleId(vehicleId);
        Optional<VehicleEntity> optionalVehicleEntity = vehicleServiceJPA.findById(vehicleId);
        if (!optionalVehicleEntity.isPresent()) {
            LOGGER.error("active RFID, vehicle not found, vehicleId: {}", vehicleId);
            activeResponses.setReason(jedisCacheService.getMessageErrorByKey("crm.active.vehicle.not.found"));
            activeResponses.setCode(2);
        } else {
            VehicleEntity vehicleEntity = optionalVehicleEntity.get();
            activeResponses.setPlateNumber(vehicleEntity.getPlateNumber());
            activeResponses.setRfidSerial(vehicleEntity.getRfidSerial());
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            VehicleEntity vehicleEntityOld = gson.fromJson(gson.toJson(vehicleEntity), VehicleEntity.class);
            AddVehicleRequestDTO addVehicleRequestDTO = setValueToActiveRFID(vehicleEntity);
            OCSResponse response = ocsService.createVehicleOCS(addVehicleRequestDTO, authentication, rfidDTO.getActTypeId());
            if (response.getDescription() == null) {
                activeResponses.setReason(jedisCacheService.getMessageErrorByKey("ocs.register.vehicle"));
                activeResponses.setCode(2);
            }
            if (!"Success".equals(response.getDescription())) {
                if ("WS_RFID_EXIST".equals(response.getDescription())) {
                    activeResponses.setReason(jedisCacheService.getMessageErrorByKey("crm.activeRFID.serial.exist.ocs"));
                    activeResponses.setCode(3);
                } else {
                    activeResponses.setReason(jedisCacheService.getMessageErrorByKey("crm.activeRFID.serial.register.ocs"));
                    activeResponses.setCode(4);
                }
            }
            if ("Success".equals(response.getDescription())) {
                List<VehicleEntity> listVehicleOld = vehicleRepositoryJPA.findAllByContractIdAndStatusAndActiveStatus(vehicleEntity.getContractId());
                if (listVehicleOld.isEmpty()) {
                    sendSmsAndEmail(vehicleEntity.getContractId(), authentication);
                }
                activeResponses.setResult("SUCCESS");
                vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
                vehicleEntity = vehicleServiceJPA.save(vehicleEntity);
                Long reasonId = getReasonIdByactTypeId(rfidDTO.getActTypeId());
                String ip = InetAddress.getLocalHost().getHostAddress();
                ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, reasonId, rfidDTO.getActTypeId(),
                        vehicleEntity.getCustId(), vehicleEntity.getContractId(), vehicleEntity.getVehicleId(), ip);
                actionAuditEntity = actionAuditService.updateLogToActionAudit(actionAuditEntity);

                ActionAuditDetailDTO actionAuditDetailDTO = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity);
                ActionAuditDetailEntity actionAuditDetailEntity = actionAuditDetailDTO.toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleEntity.getVehicleId());
                actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
            }
        }
        return activeResponses;
    }

    /**
     * Set gia tri de kich hoat the
     *
     * @param vehicleEntity
     * @return
     */
    private AddVehicleRequestDTO setValueToActiveRFID(VehicleEntity vehicleEntity) {
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setContractId(vehicleEntity.getContractId());
        addVehicleRequestDTO.setTid(vehicleEntity.getTid());
        addVehicleRequestDTO.setEpc(vehicleEntity.getEpc());
        addVehicleRequestDTO.setVehicleGroupId(vehicleEntity.getVehicleGroupId());
        addVehicleRequestDTO.setPlateNumber(vehicleEntity.getPlateNumber());
        if (Objects.nonNull(vehicleEntity.getEffDate())) {
            addVehicleRequestDTO.setEffDate(vehicleEntity.getEffDate());
        }
        if (Objects.nonNull(vehicleEntity.getExpDate())) {
            addVehicleRequestDTO.setExpDate(vehicleEntity.getExpDate());
        }
        addVehicleRequestDTO.setRfidType(vehicleEntity.getRfidType());
        addVehicleRequestDTO.setStatus(vehicleEntity.getStatus());
        addVehicleRequestDTO.setPlateTypeCode(vehicleEntity.getPlateTypeCode());
        addVehicleRequestDTO.setRfidSerial(vehicleEntity.getRfidSerial());
        addVehicleRequestDTO.setActiveStatus(vehicleEntity.getStatus());
        addVehicleRequestDTO.setSeatNumber(vehicleEntity.getSeatNumber());
        if (vehicleEntity.getCargoWeight() != null) {
            addVehicleRequestDTO.setCargoWeight(vehicleEntity.getCargoWeight());
        }
        return addVehicleRequestDTO;
    }

    /**
     * validate truoc khi cap nhat phuong tien
     *
     * @param modifyVehicleDTO
     */
    private void validateModifyVehicle(ModifyVehicleDTO modifyVehicleDTO) {
        List<String> listError = new ArrayList<>();
        // validate chu phuong tien
        if (!FnCommon.isNullOrEmpty(modifyVehicleDTO.getOwner())) {
            if (FnCommon.validateMaxlengthString(modifyVehicleDTO.getOwner(), 255)) {
                listError.add("common.validate.owner.maxlength");
            }
        }

        // validate khoi luong toan bo
        if (!Objects.isNull(modifyVehicleDTO.getGrossWeight())) {
            if (modifyVehicleDTO.getGrossWeight() < 0 || FnCommon.validateMaxlengthDouble(modifyVehicleDTO.getGrossWeight(), 6)) {
                listError.add("common.validate.gross.weight.min.max");
            }
        }
        // validate khoi luong keo theo
        if (!Objects.isNull(modifyVehicleDTO.getPullingWeight())) {
            if (modifyVehicleDTO.getPullingWeight() < 0 || FnCommon.validateMaxlengthDouble(modifyVehicleDTO.getPullingWeight(), 6)) {
                listError.add("common.validate.pulling.weight.min.max");
            }
        }
        // validate so cho ngoi
        if (Objects.nonNull(modifyVehicleDTO.getSeatNumber())) {
            if (modifyVehicleDTO.getSeatNumber() < 0 || modifyVehicleDTO.getSeatNumber() >= 1000) {
                listError.add("common.validate.seat.number.min.max");
            }
        }
        // validate so may
        if (!Objects.isNull(modifyVehicleDTO.getEngineNumber())) {
            if (FnCommon.validateMaxlengthString(modifyVehicleDTO.getEngineNumber().trim(), 50)) {
                listError.add("common.validate.engine.number.max.length");
            }
        }

        // validate so khung
        if (!Objects.isNull(modifyVehicleDTO.getChassicNumber())) {
            if (FnCommon.validateMaxlengthString(modifyVehicleDTO.getChassicNumber().trim(), 50)) {
                listError.add("common.validate.chassic.number.max.length");
            }
        }
        if (listError.size() > 0) {
            throw new EtcException(listError.get(0));
        }
    }

    /**
     * Tra cuu thong tin danh sach phuong tien da gan the
     *
     * @param requestModel
     * @param authentication
     * @return
     */
    @Override
    public Object findVehicleAssignedRFID(VehicleByContractDTO requestModel, Authentication authentication) {
        ResultSelectEntity dataResult = vehicleRepository.findVehicleAssignedRFID(requestModel);
        if (dataResult.getListData().size() > 0) {
            List<VehicleByContractDTO> listData = (List<VehicleByContractDTO>) dataResult.getListData();
            for (VehicleByContractDTO vehicleDTO : listData) {
                // Lay thong tin xe tu service category
                try {
                    ResultSelectEntity listResult = categoriesService.findCategoriesCustomer(FnCommon.getStringToken(authentication));
                    List<Map<String, String>> listResultDTO = (List<Map<String, String>>) listResult.getListData();
                    Map<String, Map<String, String>> categoryMap = new HashMap<>();
                    for (Map<String, String> temp : listResultDTO) {
                        categoryMap.put(buildCategoryKeyByCode(temp.get("table_name"), temp.get("code")), temp);
                    }
                    if (Objects.nonNull(vehicleDTO.getVehicleTypeId())) {
                        String vehicleTypeKey = buildCategoryKeyByCode("VEHICLE_TYPE", vehicleDTO.getVehicleTypeId().toString());
                        if (categoryMap.containsKey(vehicleTypeKey)) {
                            vehicleDTO.setNameType(categoryMap.get(vehicleTypeKey).get("name"));
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("Error call categories", ex);
                }
            }
        }
        return dataResult;
    }

    /**
     * Check xe mua ve thang quy
     *
     * @param vehicleId
     * @param currDate
     * @param contractId
     * @param transferContractId
     * @param userLogin
     * @param newVehicleId
     */
    private void checkChargeTicket(Long vehicleId, Date currDate, Long contractId, Long transferContractId, String userLogin, Long newVehicleId) {
        List<SaleTransDetailEntity> saleTransDetailEntityList = saleTransDetailServiceJPA.findByVehicleIdAndStatus(vehicleId, SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value);
        List<SaleTransEntity> saleTransEntityList = saleTransServiceJPA.findByContractIdAndSaleTransType(contractId, SaleTransEntity.SaleTransType.MONTHLY_QUARTERLY_TICKET.value);
        if (saleTransDetailEntityList.size() > Constants.SIZE_LIST_ZERO && saleTransEntityList.size() > Constants.SIZE_LIST_ZERO) {
            for (SaleTransEntity saleTransEntity : saleTransEntityList) {
                for (SaleTransDetailEntity saleTransDetailEntity : saleTransDetailEntityList) {
                    if (saleTransDetailEntity.getEffDate() != null && saleTransDetailEntity.getExpDate() != null &&
                            currDate.after(saleTransDetailEntity.getEffDate()) && currDate.before(saleTransDetailEntity.getExpDate())) {
                        SaleTransEntity saleTransEntityInsert = new Gson().fromJson(new Gson().toJson(saleTransEntity), SaleTransEntity.class);
                        saleTransEntityInsert = insertSaleTrans(saleTransEntityInsert, transferContractId, currDate, userLogin);
                        SaleTransDetailEntity saleTransDetailEntityInsert = new Gson().fromJson(new Gson().toJson(saleTransDetailEntity), SaleTransDetailEntity.class);
                        updateSaleTransDetailOld(saleTransDetailEntity, currDate);
                        insertSaleTransDetailNew(saleTransDetailEntityInsert, currDate, saleTransEntityInsert.getSaleTransId(), newVehicleId);
                    }
                }
            }
        }
    }

    /**
     * Them moi du lieu cho ban ghi moi
     *
     * @param saleTransDetailEntityInsert
     * @param currDate
     * @param saleTransId
     */
    private void insertSaleTransDetailNew(SaleTransDetailEntity saleTransDetailEntityInsert, Date currDate, Long saleTransId, Long newVehicleId) {
        saleTransDetailEntityInsert.setSaleTransDetailId(null);
        saleTransDetailEntityInsert.setStatus(SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value);
        saleTransDetailEntityInsert.setEffDate(currDate);
        saleTransDetailEntityInsert.setSaleTransId(saleTransId);
        saleTransDetailEntityInsert.setVehicleId(newVehicleId);
        saleTransDetailServiceJPA.save(saleTransDetailEntityInsert);
    }

    /**
     * Update thong tin ban gi cu
     *
     * @param saleTransDetailEntity
     * @param currDate
     */
    private void updateSaleTransDetailOld(SaleTransDetailEntity saleTransDetailEntity, Date currDate) {
        saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.CANCEL.value);
        saleTransDetailEntity.setExpDate(currDate);
        saleTransDetailServiceJPA.save(saleTransDetailEntity);
    }

    /**
     * Them moi ban ghi sale trans
     *
     * @param saleTransEntity
     * @param transferContractId
     * @param currDate
     * @param userLogin
     */
    private SaleTransEntity insertSaleTrans(SaleTransEntity saleTransEntity, Long transferContractId, Date currDate, String userLogin) {
        ContractEntity contractEntity = contractServiceJPA.findById(transferContractId).get();
        saleTransEntity.setSaleTransId(null);
        saleTransEntity.setContractId(transferContractId);
        saleTransEntity.setContractNo(contractEntity.getContractNo());
        saleTransEntity.setCreateDate(currDate);
        saleTransEntity.setCreateUser(userLogin);
        saleTransEntity.setSaleTransType(SaleTransEntity.SaleTransType.MONTHLY_QUARTERLY_TICKET.value);
        saleTransEntity.setCustId(contractEntity.getCustId());
        return saleTransServiceJPA.save(saleTransEntity);
    }

    /**
     * Validate file mau
     *
     * @param file
     * @return
     */
    private boolean validateFileTemplate(MultipartFile file) {
        boolean isAccepted = true;
        String fileName = file.getOriginalFilename();
        Long maxSize = Long.parseLong(maxFileSize);
        if (file.getSize() > maxSize * 1024 * 1024) {
            isAccepted = false;
        }
        boolean checkExcel = validateFileName(fileName);
        if (!checkExcel) {
            isAccepted = false;
        }
        return isAccepted;
    }

    /**
     * Lay thong tin dang kiem
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getVehicleRegistryInfo(VehicleDTO itemParamsEntity, String plateNumber) {
        return vehicleRepository.getVehicleRegistryInfo(itemParamsEntity, plateNumber);
    }

    /**
     * Doi bien so xe
     *
     * @param req
     * @param authentication
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = EtcException.class)
    public Object swapPlateNumber(VehicleSwapPlateNumberDTO req, Authentication authentication) throws Exception {

        Date date = new Date(System.currentTimeMillis());
        long vehicleId = req.getVehicleId();
        Optional<VehicleEntity> optionalVehicleEntity = vehicleServiceJPA.findById(vehicleId);
        if (!optionalVehicleEntity.isPresent()) {
            LOGGER.error("active RFID, vehicle not found, vehicleId: {}", vehicleId);
            throw new EtcException("crm.vehicle.not.found");
        }

        VehicleEntity vehicleEntity = optionalVehicleEntity.get();
        if (!FnCommon.validatePlateContainsTVX(req.getNewPlateNumber())) {
            if (!FnCommon.validatePlateContainsTVX(req.getNewPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode()))) {
                throw new EtcException("common.validate.plate.number");
            }
        }

        /***
         * Check bien so co ton tai ben boo1 hay khong
         */
        String statusVehicleBoo1 = checkVehicleActivationBoo1(req.getNewPlateNumber(), authentication, vehicleEntity.getPlateTypeCode(), req.getActTypeId());
        if (BOO_STATUS.ACTIVE.equals(statusVehicleBoo1)) {
            Map<String, String> parameter = new HashMap<>();
            parameter.put("PARAMETER", req.getNewPlateNumber());
            throw new EtcException("crm.vehicle.boo1.plate-exists", parameter);
        }


        Optional<ContractEntity> optionalContractEntity = contractServiceJPA.findById(vehicleEntity.getContractId());
        if (!optionalContractEntity.isPresent()) {
            throw new EtcException("crm.contract.not.found");
        }
        ContractEntity contractEntity = optionalContractEntity.get();
        VehicleEntity vehicleEntityInsert = new Gson().fromJson(new Gson().toJson(vehicleEntity), VehicleEntity.class);
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO();
        updateVehicleRequestOCSDTO.setEpc(vehicleEntity.getEpc());
        updateVehicleRequestOCSDTO.setPlateNumber(req.getNewPlateNumber());
        updateVehicleRequestOCSDTO.setEffDate(FnCommon.convertDateToString(date));
        updateVehicleRequestOCSDTO.setActTypeId(req.getActTypeId());
        OCSResponse ocsResponse = ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, authentication, true);
        if ("0".equals(ocsResponse.getResultCode())) {
            updateVehicleOld(vehicleEntity, req.getNewPlateNumber());
            // update action audit
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, req.getReasonId(), req.getActTypeId(), null, vehicleEntity.getContractId(), vehicleId, ip));
            //update action audit detail
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityInsert, vehicleEntity).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleId);
            actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
            //update sale trans
            if (req.getAmount() != null && req.getAmount() > Constants.AMOUNT_ZERO) {
                addSaleTransAndSaleTransDetails(contractEntity.getCustId(), vehicleEntity.getContractId(), vehicleId, authentication, req.getActTypeId());
            }
            /***
             * Truong hop doi bien so: Kiem tra neu xe hoat dong ben BOO1 voi bien so moi(newPlateNumber)  va co ve trong bang sale_trans_detail voi bien so cu(plateNumber)
             */
            List<SaleTransDetailEntity> listSaleTransDetail = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntityInsert.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode()), vehicleEntity.getEpc(), Constants.BOO1);
            if (listSaleTransDetail != null && !listSaleTransDetail.isEmpty()) {
                ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
                String plateNew = req.getNewPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode());
                String plateOld = vehicleEntityInsert.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode());
                reqOnlineEventSyncDTO.setPlate_new(plateNew);
                reqOnlineEventSyncDTO.setPlate_old(plateOld);
                reqOnlineEventSyncDTO.setRequest_id(System.currentTimeMillis());
                reqOnlineEventSyncDTO.setRequest_datetime(System.currentTimeMillis());
                reqOnlineEventSyncDTO.setReason(Constants.REASON_CHANGE_BOO.CHANGE_PLATE_NUMBER);
                updateExceptionListAndSaleTransDetail(listSaleTransDetail, null, null, req.getNewPlateNumber(), vehicleEntity.getPlateTypeCode());
                boo1Service.onlineEventSync(reqOnlineEventSyncDTO, authentication, req.getActTypeId());
            }
            List<SaleTransDetailEntity> listSaleTransDetailBoo2 = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntityInsert.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityInsert.getPlateTypeCode()), vehicleEntityInsert.getEpc(), Constants.BOO2);
            List<ExceptionListEntity> listExceptionListBoo2 = exceptionListServiceJPA.findExceptionListStillEffectiveDate(vehicleEntityInsert.getEpc(), vehicleEntityInsert.getPlateNumber());
            if (listSaleTransDetailBoo2 != null && !listSaleTransDetailBoo2.isEmpty() || listExceptionListBoo2 != null && !listExceptionListBoo2.isEmpty()) {
                updateExceptionListAndSaleTransDetail(listSaleTransDetailBoo2, listExceptionListBoo2, null, vehicleEntity.getPlateNumber(), vehicleEntity.getPlateTypeCode());
            }
            if (req.getVehicleProfileIdDelete().size() > Constants.SIZE_LIST_ZERO) {
                deleteProfileVehicle(req.getVehicleProfileIdDelete());
            }
            updateProfile(date, authentication, contractEntity.getContractId(), vehicleId, req.getActTypeId(), req.getReasonId(), req.getVehicleProfileDTOs());
            return true;
        } else {
            throw new EtcException("ocs.register.vehicle");
        }

    }

    /**
     * Cap nhat xe cu
     *
     * @param vehicleEntity
     * @param newPlateNumber
     */
    private void updateVehicleOld(VehicleEntity vehicleEntity, String newPlateNumber) {
        vehicleEntity.setPlateNumber(newPlateNumber.toUpperCase());
        vehicleServiceJPA.save(vehicleEntity);
    }

    /**
     * Luu log doi bien so xe
     *
     * @param contractId
     * @param vehicleId
     * @param actionTypeId
     * @param authentication
     * @return
     */
    private void addSaleTransAndSaleTransDetails(Long custId, Long contractId, Long vehicleId, Authentication authentication, long actionTypeId) {
        addSaleTransAndSaleTransDetails(custId, contractId, null, vehicleId, authentication, actionTypeId);
    }

    /**
     * Check xe ton tai
     *
     * @param plateNumber
     */
    public boolean checkVehicleRegisterExist(String plateNumber) {
        boolean result = false;
        List<VehicleEntity> list = vehicleRepositoryJPA.findAllByPlateNumberAndStatus(plateNumber, VehicleEntity.Status.ACTIVATED.value);
        if (list != null && !list.isEmpty()) {
            for (VehicleEntity veh : list) {
                Optional<ContractEntity> contractEntity = contractRepositoryJPA.findByContractIdAndStatus(veh.getContractId(), ContractEntity.Status.ACTIVATED.value);
                if (contractEntity.isPresent() && !VehicleEntity.ActiveStatus.TRANSFERRED.value.equals(veh.getActiveStatus())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Update log vehicle old
     *
     * @param authentication
     * @param vehicleTransferDTO
     * @param vehicleEntityOld
     * @param vehicleEntityUpdate
     * @param ip
     */
    private void updateLogVehicleOld(Authentication authentication, VehicleTransferDTO vehicleTransferDTO, VehicleEntity vehicleEntityOld, VehicleEntity vehicleEntityUpdate, String ip) throws JSONException, IllegalAccessException {
        // update action audit
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, vehicleTransferDTO.getReasonId(), vehicleTransferDTO.getActTypeId(), vehicleEntityUpdate.getCustId(), vehicleEntityUpdate.getContractId(), vehicleEntityUpdate.getVehicleId(), ip));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntityUpdate).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleEntityUpdate.getVehicleId());
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
    }

    /**
     * Update log vehicle new
     *
     * @param authentication
     * @param vehicleEntityInsert
     * @param ip
     */
    private void updateLogVehicleNew(Authentication authentication, VehicleEntity vehicleEntityInsert, String ip) throws JSONException, IllegalAccessException {
        // update action audit
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, ACT_REASON.ADD_VEHICLE, ACT_TYPE.ADD_VEHICLE, vehicleEntityInsert.getCustId(), vehicleEntityInsert.getContractId(), vehicleEntityInsert.getVehicleId(), ip));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityInsert).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleEntityInsert.getVehicleId());
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
    }

    /**
     * Ham kiem tra xe co ton tai ben BOO1 hay khong
     *
     * @param plateNumber
     */
    public String checkVehicleActivationBoo1(String plateNumber, Authentication authentication, String plateTypeBoo2, Long actTypeId) {
        ReqActivationCheckDTO req = new ReqActivationCheckDTO();
        req.setPlate(FnCommon.getPlateNumberBoo1(plateNumber, FnCommon.mappingPlateTypeBOO2ToBOO1(plateTypeBoo2)));
        req.setRequest_id(System.currentTimeMillis());
        req.setRequest_datetime(System.currentTimeMillis());
        ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, actTypeId);
        // Co co check phuong tien tu BOO1 hay khong
        if (isCheckBoo1) {
            if (res != null) {
                if (Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                    throw new EtcException("crm.vehicle.boo1.exists");
                } else {
                    return res.getStatus();
                }
            } else {
                throw new EtcException("crm.vehicle.boo1.connect.fail");
            }
        }
        return "";
    }


    /**
     * Ham kiem tra xe co ton tai ben BOO1 hay khong
     *
     * @param plateNumber
     */
    public String checkVehicleActivationBoo1(String plateNumber, Authentication authentication, Long actTypeId) {
        ReqActivationCheckDTO req = new ReqActivationCheckDTO();
        req.setPlate(plateNumber);
        req.setRequest_id(System.currentTimeMillis());
        req.setRequest_datetime(System.currentTimeMillis());
        ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, actTypeId);
        // Co co check phuong tien tu BOO1 hay khong
        if (isCheckBoo1) {
            if (res != null) {
                if (Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                    throw new EtcException("crm.vehicle.boo1.exists");
                } else {
                    return res.getStatus();
                }
            } else {
                throw new EtcException("crm.vehicle.boo1.connect.fail");
            }
        }
        return "";
    }

    /**
     * Chuc nang cap nhat phuong tien moi
     *
     * @param authentication
     * @param modifyVehicleDTO
     * @param customerId
     * @param contractId
     * @param vehicleId
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object modifyVehicle(ModifyVehicleDTO modifyVehicleDTO, Long customerId, Long contractId, Long vehicleId, Authentication authentication) throws Exception {
        if (!contractServiceJPA.findById(contractId).isPresent()) {
            throw new EtcException("crm.contract.not.exist");
        }
        ContractEntity contractEntity = contractServiceJPA.findById(contractId).get();
        VehicleEntity vehicleEntityNew = vehicleServiceJPA.findById(vehicleId).get();
        if (VehicleEntity.Status.ACTIVATED.value.equals(vehicleEntityNew.getStatus()) && VehicleEntity.ActiveStatus.TRANSFERRED.value.equals(vehicleEntityNew.getActiveStatus())) {
            throw new EtcException("crm.can.not.modify.vehicle");
        }

        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        VehicleEntity vehicleEntityOld = gson.fromJson(gson.toJson(vehicleEntityNew), VehicleEntity.class);
        if (!FnCommon.validatePlateContainsTVX(vehicleEntityOld.getPlateNumber())) {
            if (!FnCommon.validatePlateContainsTVX(vehicleEntityOld.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(modifyVehicleDTO.getPlateTypeCode()))) {
                throw new EtcException("common.validate.plate.number");
            }
        }
        validateModifyVehicle(modifyVehicleDTO);
        updateVehicle(modifyVehicleDTO, vehicleEntityNew, authentication);
        String ip = InetAddress.getLocalHost().getHostAddress();
        // insert action_audit, action_audit_detail
        writeLogUpdate(authentication, vehicleEntityOld, vehicleEntityNew, modifyVehicleDTO.getReasonId(), modifyVehicleDTO.getActTypeId(), customerId, contractId, vehicleId, ip, VehicleEntity.class.getAnnotation(Table.class).name());
        // cap nhat lai sale_trans_detail
        insertFeeUpdateVehicle(contractId, authentication, modifyVehicleDTO.getActTypeId(), contractEntity, customerId, vehicleEntityNew);

        List<SaleTransDetailEntity> listSaleTransDetail = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntityOld.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityOld.getPlateTypeCode()), vehicleEntityOld.getEpc(), Constants.BOO1);
        if (listSaleTransDetail != null && !listSaleTransDetail.isEmpty()) {
            ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = setValueToUpdateBoo1(vehicleEntityOld, modifyVehicleDTO);
            boo1Service.onlineEventSync(reqOnlineEventSyncDTO, authentication, modifyVehicleDTO.getActTypeId());
        }
        List<SaleTransDetailEntity> listSaleTransDetailBoo2 = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooCode(vehicleEntityOld.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityOld.getPlateTypeCode()), vehicleEntityOld.getEpc(), Constants.BOO2);
        if (listSaleTransDetailBoo2 != null && !listSaleTransDetailBoo2.isEmpty() || listSaleTransDetail != null && !listSaleTransDetail.isEmpty()) {
            List<SaleTransDetailEntity> combinedList = Stream.of(listSaleTransDetail, listSaleTransDetailBoo2).flatMap(x -> x.stream()).collect(Collectors.toList());
            updateSaleTransDetail(combinedList, modifyVehicleDTO, contractId, vehicleEntityOld, authentication, gson, customerId, vehicleId, ip);
        }

        // cap nhat exception list
        List<ExceptionListEntity> listExceptionList = exceptionListServiceJPA.findExceptionListStillEffectiveDate(vehicleEntityOld.getEpc(), vehicleEntityOld.getPlateNumber());
        updateExceptionList(listExceptionList, vehicleEntityOld, authentication, modifyVehicleDTO, customerId, contractId, vehicleId, ip, gson);
        return vehicleEntityNew;
    }

    @Override
    public String checkVehicle(String plateNumber, String plateTypeCode) {
        String result = Constants.BOO_STATUS.NOT_REGISTRY;
        List<VehicleEntity> vehicleEntityList = vehicleRepositoryJPA.findAllByPlateNumberNotBOO1(plateNumber);
        if (vehicleEntityList.size() >= Constants.SIZE_LIST_ZERO) {
            for (VehicleEntity vehicleEntity : vehicleEntityList) {
                if (vehicleEntity.getStatus().equals(VehicleEntity.Status.ACTIVATED.value)) {
                    return Constants.BOO_STATUS.ACTIVE;
                }
                if (vehicleEntity.getStatus().equals(VehicleEntity.Status.ACTIVATED.value) && vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value)) {
                    result = Constants.BOO_STATUS.NOT_REGISTRY;
                }
                if (vehicleEntity.getStatus().equals(VehicleEntity.Status.NOT_ACTIVATED.value) && vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.CANCEL.value)) {
                    result = Constants.BOO_STATUS.DESTROY;
                }
            }
        }

        ReqActivationCheckDTO req = new ReqActivationCheckDTO();
        req.setPlate(FnCommon.getPlateNumberBoo1(plateNumber, FnCommon.mappingPlateTypeBOO2ToBOO1(plateTypeCode)));
        req.setRequest_id(System.currentTimeMillis());
        req.setRequest_datetime(System.currentTimeMillis());
        ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, null, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);

        if (res != null) {
            if (Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                return res.getStatus();
            } else if (Constants.BOO_STATUS.NOT_REGISTRY.equals(result)) {
                return res.getStatus();
            } else {
                return Constants.BOO_STATUS.DESTROY;
            }
        } else {
            return null;
        }
    }


    /**
     * Set gia tri de thay doi thong tin xe Boo1
     *
     * @param vehicleEntityOld
     * @param modifyVehicleDTO
     * @return
     */
    private ReqOnlineEventSyncDTO setValueToUpdateBoo1(VehicleEntity vehicleEntityOld, ModifyVehicleDTO modifyVehicleDTO) {
        ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
        String plate = vehicleEntityOld.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityOld.getPlateTypeCode());
        reqOnlineEventSyncDTO.setPlate_old(plate);
        reqOnlineEventSyncDTO.setPlate_new(plate);
        reqOnlineEventSyncDTO.setEtag_new(vehicleEntityOld.getEpc());
        reqOnlineEventSyncDTO.setEtag_old(vehicleEntityOld.getEpc());
        reqOnlineEventSyncDTO.setReason(Constants.REASON_CHANGE_BOO.CHANGE_OTHER_INFO);
        if (Objects.nonNull(modifyVehicleDTO.getVehicleTypeId())) {
            reqOnlineEventSyncDTO.setReason(Constants.REASON_CHANGE_BOO.CHANGE_INFO_REGISTER);
            reqOnlineEventSyncDTO.setRegister_vehicle_type_old(vehicleEntityOld.getVehicleTypeId().toString());
            reqOnlineEventSyncDTO.setRegister_vehicle_type_new(modifyVehicleDTO.getVehicleTypeId().toString());
        }
        if (Objects.nonNull(modifyVehicleDTO.getSeatNumber())) {
            reqOnlineEventSyncDTO.setSeat_old(vehicleEntityOld.getSeatNumber());
            reqOnlineEventSyncDTO.setSeat_new(modifyVehicleDTO.getSeatNumber());
        }
        if (Objects.nonNull(modifyVehicleDTO.getCargoWeight())) {
            reqOnlineEventSyncDTO.setWeight_goods_old(vehicleEntityOld.getCargoWeight());
            reqOnlineEventSyncDTO.setWeight_goods_new(modifyVehicleDTO.getCargoWeight());
        }
        if (Objects.nonNull(modifyVehicleDTO.getNetWeight())) {
            reqOnlineEventSyncDTO.setWeight_all_old(vehicleEntityOld.getGrossWeight());
            reqOnlineEventSyncDTO.setWeight_all_new(modifyVehicleDTO.getGrossWeight());
        }
        reqOnlineEventSyncDTO.setRequest_id(System.currentTimeMillis());
        reqOnlineEventSyncDTO.setRequest_datetime(System.currentTimeMillis());
        return reqOnlineEventSyncDTO;
    }

    /**
     * update exception_list khi thay doi thong tin phuong tien
     *
     * @param vehicleEntity
     * @param authentication
     * @param modifyVehicleDTO
     * @param custId
     * @param contractId
     * @param vehicleId
     * @param ip
     * @param gson
     */
    private void updateExceptionList(List<ExceptionListEntity> listEntities, VehicleEntity vehicleEntity,
                                     Authentication authentication,
                                     ModifyVehicleDTO modifyVehicleDTO, Long custId,
                                     Long contractId, Long vehicleId, String ip, Gson gson) throws JSONException, IllegalAccessException {
        for (ExceptionListEntity exceptionListEntity : listEntities) {
            if (modifyVehicleDTO.getVehicleGroupId() != vehicleEntity.getVehicleGroupId()) {
                exceptionListEntity.setVehicleTypeId(modifyVehicleDTO.getVehicleGroupId());
            }
            if (Objects.nonNull(modifyVehicleDTO.getPlateType())) {
                exceptionListEntity.setPlateTypeCode(modifyVehicleDTO.getPlateTypeCode());
                exceptionListEntity.setLicensePlateType(Long.parseLong(modifyVehicleDTO.getPlateTypeCode()));
                exceptionListEntity.setOrgPlateNumber(vehicleEntity.getPlateNumber() + modifyVehicleDTO.getPlateTypeCode());
            }
            ExceptionListEntity exceptionListEntityOld = gson.fromJson(gson.toJson(exceptionListEntity), ExceptionListEntity.class);
            exceptionListRepositoryJPA.save(exceptionListEntity);
            // insert action audit
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, modifyVehicleDTO.getReasonId(), modifyVehicleDTO.getActTypeId(), custId, contractId, vehicleId, ip));
            //insert action audit detail
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(exceptionListEntityOld, exceptionListEntity).toEntity(actionAuditEntity.getActionAuditId(), ExceptionListEntity.class.getAnnotation(Table.class).name(), vehicleId);
            actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
        }
    }

    /**
     * Update cac ve khi thay doi thong tin phuong tien
     *
     * @param listSaleTransDetails
     * @param modifyVehicleDTO
     * @param contractId
     * @param vehicleEntityOld
     * @param authentication
     * @throws Exception
     */
    private void updateSaleTransDetail(List<SaleTransDetailEntity> listSaleTransDetails, ModifyVehicleDTO modifyVehicleDTO,
                                       Long contractId, VehicleEntity vehicleEntityOld,
                                       Authentication authentication, Gson gson, Long custId, Long vehicleId, String ip) throws Exception {
        Long vehicleGroupId = modifyVehicleDTO.getVehicleGroupId();

        for (SaleTransDetailEntity saleTransDetail : listSaleTransDetails) {
            saleTransDetail.setOrgPlateNumber(saleTransDetail.getPlateNumber() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntityOld.getPlateTypeCode()));
            SaleTransDetailEntity saleTransDetailEntityOld = gson.fromJson(gson.toJson(saleTransDetail), SaleTransDetailEntity.class);
            if (!vehicleEntityOld.getVehicleGroupId().equals(vehicleGroupId)) {
                if (Constants.BOO2.equals(saleTransDetail.getBooCode())) {
                    ocsService.deleteSupOffer(authentication, modifyVehicleDTO.getActTypeId(), contractId, saleTransDetail.getEpc(), saleTransDetail.getOcsCode(), saleTransDetail.getOfferLevel());
                    saleTransDetail.setStatus(Long.parseLong(SaleTransEntity.Status.CANCEL.value));
                    saleTransDetailRepositoryJPA.save(saleTransDetail);
                }
                if (Constants.BOO1.equals(saleTransDetail.getBooCode())) {
                    RemoveSupOfferRoaming removeSupOfferRoaming = new RemoveSupOfferRoaming();
                    removeSupOfferRoaming.setEPC(vehicleEntityOld.getEpc());
                    removeSupOfferRoaming.setSubscriptionTicketId(saleTransDetail.getSubscriptionTicketId().toString());
                    removeSupOfferRoaming.setActTypeId(modifyVehicleDTO.getActTypeId());
                    ocsService.removeSupOfferRoaming(removeSupOfferRoaming, authentication);
                    saleTransDetail.setStatus(Long.parseLong(SaleTransEntity.Status.CANCEL.value));
                    saleTransDetailRepositoryJPA.save(saleTransDetail);
                }
            }

            // insert action_audit, action_audit_detail
            writeLogUpdate(authentication, saleTransDetailEntityOld, saleTransDetail, modifyVehicleDTO.getReasonId(), modifyVehicleDTO.getActTypeId(), custId, contractId, vehicleId, ip, SaleTransDetailEntity.class.getAnnotation(Table.class).name());
        }
    }

    /**
     * Ghi log khi update
     *
     * @param authentication
     * @param objectOld
     * @param objectNew
     * @param reasonId
     * @param actTypeId
     * @param custId
     * @param contractId
     * @param vehicleId
     * @param ip
     * @param tableName
     * @throws JSONException
     * @throws IllegalAccessException
     */
    private void writeLogUpdate(Authentication authentication, Object objectOld, Object objectNew, Long reasonId, Long actTypeId,
                                Long custId, Long contractId, Long vehicleId, String ip, String tableName) throws JSONException, IllegalAccessException {
        // insert action audit
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId,
                actTypeId, custId, contractId, vehicleId, ip));
        //insert action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(objectOld, objectNew).toEntity(actionAuditEntity.getActionAuditId(), tableName, vehicleId);
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
    }

    /**
     * Update sale_trans_detail khi cap nhat phuong tien
     *
     * @param contractId
     * @param authentication
     * @param actTypeId
     * @param customerId
     * @param vehicleEntityNew
     */
    private void insertFeeUpdateVehicle(Long contractId, Authentication authentication, Long actTypeId, ContractEntity contractEntity, Long customerId, VehicleEntity vehicleEntityNew) {
        String saleTransCode = generateSaleTransCode(contractId, Integer.parseInt(SaleTransEntity.SaleTransType.PACKAGE.value), new java.util.Date());
        String userLogin = FnCommon.getUserLogin(authentication);
        List<ServiceFeeEntity> serviceFeeEntityList = serviceFeeRepositoryJPA.getByActTypeId(actTypeId);
        if (serviceFeeEntityList.size() == Constants.SIZE_LIST_ZERO) {
            throw new EtcException("crm.can.not.modify.vehicle");
        }
        ServiceFeeEntity serviceFeeEntity = serviceFeeEntityList.get(0);
        SaleTransEntity saleTransEntity = toSaleTrans(saleTransCode, contractEntity.getShopId(), contractEntity.getShopName(), serviceFeeEntity.getFee(), contractId, contractEntity.getContractNo(), userLogin, customerId, authentication);
        saleTransEntity = saleTransRepositoryJPA.save(saleTransEntity);
        // insert sale trans detail
        SaleTransDetailEntity saleTransDetailEntity = toSaleTransDetail(saleTransEntity.getSaleTransId()
                , vehicleEntityNew, serviceFeeEntity, userLogin);
        saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
    }

    /**
     * Cap nhat execption va saletrandetail neu ton tai ben boo1
     *
     * @param listSaleTransDetail
     * @param listExceptionList
     * @param stockEtagDTO
     */
    public void updateExceptionListAndSaleTransDetail(List<SaleTransDetailEntity> listSaleTransDetail, List<ExceptionListEntity> listExceptionList, StockEtagDTO stockEtagDTO, String plateNumber, String plateTypeCode) {
        if (listExceptionList != null && !listExceptionList.isEmpty()) {
            for (ExceptionListEntity exceptionListEntity : listExceptionList) {
                if (!Objects.isNull(stockEtagDTO)) {
                    exceptionListEntity.setEpc(stockEtagDTO.getEpc());
                }
                if (!FnCommon.isNullOrEmpty(plateNumber)) {
                    exceptionListEntity.setPlateNumber(plateNumber);
                    exceptionListEntity.setOrgPlateNumber(plateNumber + FnCommon.mappingPlateTypeBOO2ToBOO1(plateTypeCode));
                    exceptionListEntity.setPlateTypeCode(plateTypeCode);
                    exceptionListEntity.setLicensePlateType(Long.parseLong(plateTypeCode));
                }
            }
            exceptionListServiceJPA.saveAll(listExceptionList);
        }
        if (listSaleTransDetail != null && !listSaleTransDetail.isEmpty()) {
            for (SaleTransDetailEntity saleTransDetailEntity : listSaleTransDetail) {
                if (!Objects.isNull(stockEtagDTO)) {
                    saleTransDetailEntity.setEpc(stockEtagDTO.getEpc());
                }
                if (!FnCommon.isNullOrEmpty(plateNumber)) {
                    saleTransDetailEntity.setPlateNumber(plateNumber);
                    saleTransDetailEntity.setOrgPlateNumber(plateNumber + FnCommon.mappingPlateTypeBOO2ToBOO1(plateTypeCode));
                }
            }
            saleTransDetailServiceJPA.saveAll(listSaleTransDetail);
        }
    }

    /**
     * Kiem tra thong tin dang kiem
     *
     * @param addVehicleRequestDTO
     * @param listVehicleSimilar
     * @param listVehicleNotSimilar
     * @param res
     */
    private void checkVehicleRegistry(AddVehicleRequestDTO addVehicleRequestDTO, List<Long> listVehicleSimilar, List<Long> listVehicleNotSimilar, ResponseGetInfoRegisterDTO res) {
        String plateNumber = res.getPlateNumber().replaceAll("[\\-\\.]", "");
        String cargoWeight = addVehicleRequestDTO.getCargoWeight() != null ? addVehicleRequestDTO.getCargoWeight().toString() : Constants.STR_EMPTY;
        String netWeight = addVehicleRequestDTO.getNetWeight() != null ? addVehicleRequestDTO.getNetWeight().toString() : Constants.STR_EMPTY;
        if (FnCommon.formatPlateBOO1(plateNumber).equals(addVehicleRequestDTO.getPlateNumber()) &&
                res.getVehicleTypeId().equals(addVehicleRequestDTO.getVehicleTypeId().toString()) &&
                res.getSeatNumber().equals(addVehicleRequestDTO.getSeatNumber().toString()) &&
                res.getCargoWeight().equals(cargoWeight) &&
                res.getNetWeight().equals(netWeight)) {
            listVehicleSimilar.add(addVehicleRequestDTO.getVehicleId());
        } else {
            listVehicleNotSimilar.add(addVehicleRequestDTO.getVehicleId());
        }
    }


    /***
     * *    Kiem tra user co partner_code co phai cua VTDC khong ?
     *      * Neu co thi goi IM-VDTC de check lay thong tin serial
     *      * Khong co thi thi goi IM-VTT nhu luong cu
     *      * Sua cap nhat theo yeu cau VDTC 29112020
     * @param serial
     * @param authentication
     * @return
     * @throws IOException
     */
    private StockTransEtagDTO checkAndVerifySerialIMVDTC(String serial, Authentication authentication, VehicleEntity vehicleEntity, int caseUpdate) throws IOException {
        Map<String, Object> attribute = FnCommon.getAttribute(authentication);
        if (Objects.nonNull(attribute) && attribute.containsKey(USER_ATTRIBUTE.WAREHOUSE_ID) && !FnCommon.isNullOrEmpty(attribute.get(USER_ATTRIBUTE.WAREHOUSE_ID).toString())) {
            StockTransEtagDTO stockTransEtag = new StockTransEtagDTO();
            stockTransEtag.setWarehouseId(Long.valueOf(attribute.get(USER_ATTRIBUTE.WAREHOUSE_ID).toString()));
            stockTransEtag.setStaffId(Long.parseLong(attribute.get(USER_ATTRIBUTE.STAFF_ID).toString()));
            stockTransEtag.setSerial(serial);
            return imService.verifySerial(stockTransEtag, authentication, vehicleEntity, caseUpdate);
        }
        return null;
    }

    /***
     * Update lai serial IM VDTC sau khi dau noi phuong tien goi OCS
     * @param stockTransEtagDTO
     * @param status
     * @param authentication
     * @throws IOException
     */
    private void updateSerialIMVDTC(StockTransEtagDTO stockTransEtagDTO, String status, Authentication authentication) throws IOException {
        if (stockTransEtagDTO != null) {
            stockTransEtagDTO.setStatus(status);
            imService.updateSerial(stockTransEtagDTO, authentication);
        }
    }

    /**
     * Check null toan bo cell trong row
     *
     * @param row
     * @return
     */
    private boolean checkNullOrEmptyRowExcel(Row row) {
        StringBuilder builder = new StringBuilder();
        builder.append(getStringValue(row.getCell(0)))
                .append(getStringValue(row.getCell(1)))
                .append(getStringValue(row.getCell(2)))
                .append(getStringValue(row.getCell(3)))
                .append(getStringValue(row.getCell(4)))
                .append(getStringValue(row.getCell(5)))
                .append(getStringValue(row.getCell(6)))
                .append(getStringValue(row.getCell(7)))
                .append(getStringValue(row.getCell(8)))
                .append(getStringValue(row.getCell(9)))
                .append(getStringValue(row.getCell(10)))
                .append(getStringValue(row.getCell(11)))
                .append(getStringValue(row.getCell(12)))
                .append(getStringValue(row.getCell(13)))
                .append(getStringValue(row.getCell(14)));
        return FnCommon.isNullOrEmpty(builder.toString());
    }

    /**
     * goi ocs addSuboffer
     *
     * @param authentication
     * @param contractId
     * @param etagNew
     * @return
     * @throws Exception
     */
    @Override
    public boolean addTicketWhenSwapRFID(Authentication authentication, Long contractId,
                                         String etagNew, List<SaleTransDetailEntity> listSaleTransDetailBoo2) throws Exception {
        boolean result = false;
        if (listSaleTransDetailBoo2 != null && listSaleTransDetailBoo2.size() > 0) {
            for (SaleTransDetailEntity saleTransDetailEntity : listSaleTransDetailBoo2) {
                VehicleAddSuffOfferDTO reqAddOffer = new VehicleAddSuffOfferDTO();
                String offerLevel = saleTransDetailEntity.getOfferLevel() != null ? saleTransDetailEntity.getOfferLevel() : String.valueOf(OFFER_LEVEL_DEFAULT);
                reqAddOffer.setOfferLevel(offerLevel);
                reqAddOffer.setEpc(etagNew);
                reqAddOffer.setOfferId(saleTransDetailEntity.getOcsCode());
                reqAddOffer.setEffDate(saleTransDetailEntity.getEffDate());
                reqAddOffer.setExpDate(saleTransDetailEntity.getExpDate());
                reqAddOffer.setIsGenCdr("0");
                reqAddOffer.setAutoRenew(saleTransDetailEntity.getAutoRenew());
                Optional<SaleTransEntity> saleTransEntity = saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId());
                if (saleTransEntity.isPresent()) {
                    OCSResponse responseAddSupOffer = ocsService.addSupOffer(reqAddOffer, authentication, ACT_TYPE.MODIFY_VEHICLE.intValue(), contractId,
                            saleTransEntity.get().getStaffId(), saleTransEntity.get().getStaffName(), String.valueOf(saleTransEntity.get().getSaleTransId()));
                    if (FnCommon.checkOcsCode(responseAddSupOffer)) {
                        result = true;
                        saleTransDetailEntity.setEpc(etagNew);
                        if (Objects.nonNull(responseAddSupOffer.getSubscriptionTicketId())) {
                            saleTransDetailEntity.setSubscriptionTicketId(Long.parseLong(responseAddSupOffer.getSubscriptionTicketId().trim()));
                        }
                        saleTransDetailServiceJPA.save(saleTransDetailEntity);
                    } else {
                        LOGGER.error(String.format("THem moi goi cuoc OCS that bai : %s", responseAddSupOffer));
                    }
                }
            }
        }
        return result;
    }


    /**
     * goi ocs addSuboffer
     *
     * @param authentication
     * @param contractId
     * @param etagNew
     * @return
     * @throws Exception
     */
    @Override
    public boolean addOfferExceptionListWhenSwapRFID(Authentication authentication, Long contractId,
                                                     String etagNew, List<ExceptionListEntity> listExceptionList) throws Exception {
        boolean result = false;
        if (listExceptionList != null && listExceptionList.size() > 0) {
            for (ExceptionListEntity ex : listExceptionList) {
                VehicleAddSuffOfferDTO reqAddOffer = new VehicleAddSuffOfferDTO();
                reqAddOffer.setOfferLevel(String.valueOf(Constants.OFFER_LEVEL_DEFAULT));
                reqAddOffer.setEpc(etagNew);
                reqAddOffer.setOfferId(ex.getOcsCode());
                reqAddOffer.setEffDate(ex.getEffDate());
                reqAddOffer.setIsGenCdr("0");
                if (ex.getExpDate() != null) {
                    reqAddOffer.setExpDate(ex.getExpDate());
                } else {
                    reqAddOffer.setExpDate(FnCommon.addYears(ex.getEffDate(), 30));
                }

                OCSResponse responseAddSupOffer = ocsService.addSupOffer(reqAddOffer, authentication, ACT_TYPE.MODIFY_VEHICLE, contractId,
                        null, null, null);
                if (FnCommon.checkOcsCode(responseAddSupOffer)) {
                    result = true;
                    ex.setEpc(etagNew);
                    exceptionListRepositoryJPA.save(ex);
                } else {
                    LOGGER.error(String.format("THem moi goi cuoc OCS that bai : %s", responseAddSupOffer));
                }
            }
        }
        return result;
    }

    /**
     * Xoa ho so dinh kem phuong tien
     *
     * @param vehicleProfileIdList
     */
    private void deleteProfileVehicle(List<Long> vehicleProfileIdList) {
        for (Long vehicleProfileId : vehicleProfileIdList) {
            VehicleProfileEntity vehicleProfileEntity = vehicleProfileRepositoryJPA.findByVehicleProfileId(vehicleProfileId);
            fileService.removeFile(vehicleProfileEntity.getFilePath());
            vehicleProfileRepositoryJPA.deleteById(vehicleProfileEntity.getVehicleProfileId());
        }
    }

    private SaleTransEntity toSaleTrans(String saleTransCode, Long shopId, String shopName, Long amount,
                                        Long contractId, String contractNo,
                                        String createUser, Long custId, Authentication authentication) {
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransCode(saleTransCode);
        saleTransEntity.setSaleTransDate(new Date(System.currentTimeMillis()));
        saleTransEntity.setSaleTransType(SaleTransEntity.SaleTransType.PACKAGE.value);
        saleTransEntity.setStatus(SaleTransEntity.Status.PAID_NOT_INVOICED.value);
        saleTransEntity.setShopId(shopId);
        saleTransEntity.setShopName(shopName);
        Map<String, Object> attribute = FnCommon.getAttribute(authentication);
        if (Objects.nonNull(attribute) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_ID) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_NAME)) {
            saleTransEntity.setShopId(Long.valueOf(attribute.get(Constants.USER_ATTRIBUTE.SHOP_ID).toString()));
            saleTransEntity.setShopName(attribute.get(Constants.USER_ATTRIBUTE.SHOP_NAME).toString());
            saleTransEntity.setStaffId(Long.parseLong(attribute.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString()));
        }
        saleTransEntity.setAmount(amount);
        saleTransEntity.setContractId(contractId);
        saleTransEntity.setContractNo(contractNo);
        saleTransEntity.setCreateUser(createUser);
        saleTransEntity.setCreateDate(new Date(System.currentTimeMillis()));
        saleTransEntity.setCustId(custId);
        saleTransEntity.setStaffName(createUser);
        return saleTransEntity;
    }

    private SaleTransDetailEntity toSaleTransDetail(long saleTransId, VehicleEntity vehicleEntity,
                                                    ServiceFeeEntity serviceFeeEntity, String userLogin) {
        SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
        saleTransDetailEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransDetailEntity.setPrice(serviceFeeEntity.getFee());
        saleTransDetailEntity.setServiceFeeId(serviceFeeEntity.getServiceFeeId());
        saleTransDetailEntity.setServiceFeeName(serviceFeeEntity.getServiceFeeName());
        saleTransDetailEntity.setSaleTransId(saleTransId);
        saleTransDetailEntity.setSaleTransDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransDetailEntity.setQuantity(Long.parseLong("1"));
        saleTransDetailEntity.setCreateUser(userLogin);
        saleTransDetailEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        /**
         * 1: Chưa thanh toán
         * 2: Đã thanh toán, chưa lập hóa đơn.
         * 3: Đã lập hóa đơn
         * 4: Hủy
         * */
        saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value);
        saleTransDetailEntity.setVehicleId(vehicleEntity.getVehicleId());
        if (Objects.nonNull(vehicleEntity.getEpc())) {
            saleTransDetailEntity.setEpc(vehicleEntity.getEpc());
        }
        if (Objects.nonNull(vehicleEntity.getTid())) {
            saleTransDetailEntity.setTid(vehicleEntity.getTid());
        }
        if (Objects.nonNull(vehicleEntity.getRfidSerial())) {
            saleTransDetailEntity.setRfidSerial(vehicleEntity.getRfidSerial());
        }
        saleTransDetailEntity.setPlateNumber(vehicleEntity.getPlateNumber());
        saleTransDetailEntity.setVehicleGroupId(vehicleEntity.getVehicleGroupId());
        return saleTransDetailEntity;
    }


    /**
     * Gen sale_trans_code
     *
     * @return
     */
    private String generateSaleTransCode(Long contractId, int type, java.util.Date currDate) {
        String date = FnCommon.convertDate(currDate);
        StringBuilder saleTranCode = new StringBuilder();
        if (type == 1) {
            saleTranCode.append("DV");
            saleTranCode.append(contractId);
            saleTranCode.append(".");
            saleTranCode.append(date);
        }
        if (type == 2) {
            saleTranCode.append("TQ");
            saleTranCode.append(contractId);
            saleTranCode.append(".");
            saleTranCode.append(date);
        }
        return saleTranCode.toString();
    }

    /**
     * Gui tin nhan va email khi dang ki phuong tien lan dau thanh cong
     *
     * @param contractId
     * @param authentication
     */
    private void sendSmsAndEmail(Long contractId, Authentication authentication) {
        ContractEntity contractEntity = contractRepositoryJPA.findByContractId(contractId);
        if (jedisCacheService.hget(contractEntity.getContractNo(), Constants.CONTRACT_KEY_GEN) != null) {
            // Luu them lucky code chuong trinh
            luckyService.genLuckyCode(contractEntity, LUCKY_CODE.CUSTNEW, authentication);

            String passwordApp = (String) jedisCacheService.hget(contractEntity.getContractNo(), Constants.CONTRACT_KEY_GEN);
            // send sms for user
            String contentSMS = String.format(smsRegisterUser, contractEntity.getContractNo(), passwordApp);
            smsService.sendSMS(contractEntity.getNoticePhoneNumber(), contentSMS, authentication);
            // send email for user
            String subject = new String("[ePass] Thông báo thông tin tài khoản".getBytes(), Charset.forName(StandardCharsets.UTF_8.name()));
            String filePath = "template" + File.separator + "template_email.txt";
            Map<String, String> parameter = new HashMap<>();
            parameter.put("PARAM1", contractEntity.getNoticeName());
            parameter.put("PARAM2", contractEntity.getContractNo());
            parameter.put("PARAM3", passwordApp);
            emailService.sendMail(subject, contractEntity.getNoticeEmail(), filePath, parameter, authentication);
            jedisCacheService.hdelete(contractEntity.getContractNo(), Constants.CONTRACT_KEY_GEN);
        }
    }

    public Object getInfoByPlateNumber(String plate, Authentication authentication) throws Exception {
        String plateNumber = FnCommon.getPlateNumberBoo1(plate, "");
        serviceRegistryService.checkVehicle(plate);

        // lay data dang kiem
        ResponseGetInfoRegisterDTO resXCG = searchXCGService.getInfoRegister(plateNumber, null);
        // map loai phuong tien tinh phi tuu dmdc
        AddVehicleRequestDTO dataParams = new AddVehicleRequestDTO();
        if (!FnCommon.isNullOrEmpty(resXCG.getVehicleTypeId())) {
            dataParams.setVehicleTypeId(Long.valueOf(resXCG.getVehicleTypeId()));
        }
        if (!FnCommon.isNullOrEmpty(resXCG.getCargoWeight())) {
            dataParams.setCargoWeight(Double.valueOf(resXCG.getCargoWeight()));
        }
        if (!FnCommon.isNullOrEmpty(resXCG.getNetWeight())) {
            dataParams.setNetWeight(Double.valueOf(resXCG.getNetWeight()));
        }
        if (!FnCommon.isNullOrEmpty(resXCG.getGrossWeight())) {
            dataParams.setGrossWeight(Double.valueOf(resXCG.getGrossWeight()));
        }
        if (!FnCommon.isNullOrEmpty(resXCG.getPullingWeight())) {
            dataParams.setPullingWeight(Double.valueOf(resXCG.getPullingWeight()));
        }
        if (!FnCommon.isNullOrEmpty(resXCG.getSeatNumber())) {
            dataParams.setSeatNumber(Long.valueOf(resXCG.getSeatNumber()));
        }
        List<String> stringList = vehicleGroupService.getVehicleGroupById(FnCommon.getStringToken(authentication), dataParams);
        if (stringList != null && stringList.size() == 2) {
            resXCG.setVehicleGroupId(Long.parseLong(stringList.get(0)));
            resXCG.setVehicleGroupCode(stringList.get(1));
        }
        return resXCG;
    }
}
