package com.viettel.etc.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.repositories.ContractRepository;
import com.viettel.etc.repositories.tables.ActReasonRepositoryJPA;
import com.viettel.etc.repositories.tables.ContractRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.*;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Table;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Autogen class: Create Service For Table Name Contract
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:23 ICT 2020
 */
@Service
@Transactional
public class ContractServiceImpl implements ContractService {
    private static final Logger LOG = LoggerFactory.getLogger(com.viettel.etc.services.tables.ContractServiceJPA.class);

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    CustomerServiceJPA customerServiceJPA;

    @Autowired
    ContractProfileServiceJPA contractProfileServiceJPA;

    @Autowired
    ContractPaymentServiceJPA contractPaymentServiceJPA;

    @Autowired
    VehicleProfileServiceJPA vehicleProfileServiceJPA;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    DocumentTypeServiceJPA documentTypeServiceJPA;

    @Autowired
    ActTypeServiceJPA actTypeServiceJPA;

    @Autowired
    ActReasonServiceJPA actReasonServiceJPA;

    @Autowired
    OCSServiceImpl ocsService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractRepositoryJPA contractRepositoryJPA;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    private ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Autowired
    private SaleTransService saleTransService;

    @Autowired
    private ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Autowired
    private SaleTransDetailService saleTransDetailService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ServicePlanServiceJPA servicePlanServiceJPA;

    @Autowired
    private JedisCacheService jedisCacheService;

    @Autowired
    private KeycloakService keycloakService;

    @Value("${crm.common.max-file-size}")
    private Integer briefcaseMaxFileSize;

    @Value("${ws.keycloak.register.user}")
    private String wsRegisterUser;

    /**
     * ky moi hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param dataParams     form data ky moi hop dong
     * @return
     * @throws IOException
     */
    @Override
    public Object addContract(ContractDTO dataParams, Authentication authentication, Long customerId) throws Exception, EtcException {
        FnCommon.checkDateIsAfter(dataParams.getEffDate(), dataParams.getExpDate());
        Map<String, Object> attribute = FnCommon.getAttribute(authentication);

        Optional<CustomerEntity> customerEntityOptional = this.customerServiceJPA.findById(customerId);
        if (!customerEntityOptional.isPresent()) {
            LOG.warn("Create contract, customer not found, customerId: {}", dataParams.getCustId());
            throw new EtcException("crm.customer.not.exist");
        }
        Long reasonId = getReasonType(dataParams.getActTypeId());
        dataParams.setReasonId(reasonId);
        CustomerEntity customer = customerEntityOptional.get();
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        String userLogin = FnCommon.getUserLogin(authentication);
        ContractEntity contract = dataParams.toAddContractEntity();
        if (Objects.nonNull(attribute) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_ID) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_NAME)) {
            contract.setShopId(Long.valueOf(attribute.get(Constants.USER_ATTRIBUTE.SHOP_ID).toString()));
            contract.setShopName(attribute.get(Constants.USER_ATTRIBUTE.SHOP_NAME).toString());
        } else {
            throw new EtcException("crm.shop.id.not.found");
        }

        contract.setCustId(customerId);
        contract.setCreateDate(currDate);
        contract.setCreateUser(userLogin);
        contract.setIsLock(0L);
        contract.setStatus(ContractEntity.Status.NOT_ACTIVATED.value);
        contract.setProfileStatus(ContractEntity.ProfilesStatus.NOT_RECEIVED.value);
        String passwordApp = RandomStringUtils.randomNumeric(6);
        this.contractServiceJPA.save(contract);
        String contractNo = generateContractNo(contract.getContractId(), contract.getShopId());
        contract.setAccountUserId(createUserForContract(contractNo, dataParams, authentication, passwordApp));
        contract.setAccountUser(contractNo);
        contract.setContractNo(contractNo);
        contract.setContractId(contract.getContractId());
        updateLogContract(authentication, null, contract, dataParams.getReasonId(), dataParams.getActTypeId());
        updateProfile(authentication, currDate, contract, dataParams.getActTypeId(), null, dataParams.getContractProfileDTOs());
        //call OCS create contract
        OCSResponse ocsResponse = ocsService.createContract(new OCSDTO().createContractFormFromContractEntity(contract, customer, dataParams), authentication, dataParams.getActTypeId().intValue());
        if ("0".equals(ocsResponse.getResultCode())) {
            contract.setStatus(ContractEntity.Status.ACTIVATED.value);
            contractServiceJPA.save(contract);
            jedisCacheService.hset(contractNo, Constants.CONTRACT_KEY_GEN, passwordApp);
        } else {
            throw new EtcException("ocs.error.create.contract");
        }
        return ContractDTO.builder().contractId(contract.getContractId()).contractNo(contractNo).build();
    }

    @Override
    public ContractEntity addContractQuick(ContractDTO dataParams, Authentication authentication, CustomerEntity customerEntity, String password) throws Exception {
        Long reasonId = getReasonType(dataParams.getActTypeId());
        dataParams.setReasonId(reasonId);
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        String userLogin = FnCommon.getUserLogin(authentication);
        ContractEntity contract = dataParams.toAddContractEntity();
        Map<String, Object> attribute = FnCommon.getAttribute(authentication);
        if (Objects.nonNull(attribute) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_ID) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_NAME)) {
            contract.setShopId(Long.valueOf(attribute.get(Constants.USER_ATTRIBUTE.SHOP_ID).toString()));
            contract.setShopName(attribute.get(Constants.USER_ATTRIBUTE.SHOP_NAME).toString());
        } else {
            throw new EtcException("crm.shop.id.not.found");
        }
        contract.setCustId(customerEntity.getCustId());
        contract.setCreateDate(currDate);
        contract.setCreateUser(userLogin);
        contract.setIsLock(0L);
        contract.setStatus(ContractEntity.Status.NOT_ACTIVATED.value);
        contract.setProfileStatus(ContractEntity.ProfilesStatus.NOT_RECEIVED.value);
        contractServiceJPA.save(contract);
        String contractNo = generateContractNo(contract.getContractId(), contract.getShopId());
        contract.setAccountUserId(createUserForContract(contractNo, dataParams, authentication, password));
        contract.setAccountUser(contractNo);
        contract.setContractNo(contractNo);
        contract.setContractId(contract.getContractId());
        contract.setIsAdditional(1L);
        //call OCS create contract
        OCSResponse ocsResponse = ocsService.createContract(new OCSDTO().createContractFormFromContractEntity(contract, customerEntity, dataParams), authentication, dataParams.getActTypeId().intValue());
        if ("0".equals(ocsResponse.getResultCode())) {
            contract.setStatus(ContractEntity.Status.ACTIVATED.value);
            contractServiceJPA.save(contract);
        } else {
            throw new EtcException("ocs.error.create.contract");
        }
        return contract;
    }

    /**
     * update log cho customer
     *
     * @param authentication
     * @param contractEntityOld
     * @param contractEntity
     * @param reasonId
     * @param actTypeId
     */
    protected void updateLogContract(Authentication authentication, ContractEntity contractEntityOld, ContractEntity contractEntity, Long reasonId, Long actTypeId) throws Exception {
        // update action audit
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, contractEntity.getCustId(), contractEntity.getContractId(), null));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity;
        if (Objects.isNull(contractEntityOld)) {
            actionAuditDetailEntity = new ActionAuditDetailDTO(contractEntity).toEntity(actionAuditEntity.getActionAuditId(), ContractEntity.class.getAnnotation(Table.class).name(), contractEntity.getContractId());
        } else {
            actionAuditDetailEntity = new ActionAuditDetailDTO(contractEntityOld, contractEntity).toEntity(actionAuditEntity.getActionAuditId(), ContractEntity.class.getAnnotation(Table.class).name(), contractEntity.getContractId());
        }
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
    }

    private void updateProfile(Authentication authentication, Date createDate, ContractEntity contractEntity, Long actTypeId, Long reasonId, List<ContractProfileDTO> contractProfileDTOs) throws Exception, EtcException {
        updateProfile(authentication, createDate, contractEntity.getCustId(), contractEntity.getContractId(), actTypeId, reasonId, contractProfileDTOs);
    }

    /**
     * them chung tu di kem hop dong
     *
     * @param createDate
     * @param custId
     * @param contractId
     * @param contractProfileDTOs
     * @throws IOException
     */
    @Override
    public void updateProfile(Authentication authentication, Date createDate, Long custId, Long contractId, Long actTypeId, Long reasonId, List<ContractProfileDTO> contractProfileDTOs) throws Exception {
        if (CollectionUtils.isNotEmpty(contractProfileDTOs)) {
            ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, custId, contractId, null));
            updateProfile(authentication, createDate, custId, contractId, actionAuditEntity, contractProfileDTOs);
        }
    }

    @Override
    public void updateProfile(Authentication authentication, Date createDate, Long custId, Long contractId, ActionAuditEntity actionAuditEntity, List<ContractProfileDTO> contractProfileDTOs) throws Exception {
        for (ContractProfileDTO contractProfileDTO : contractProfileDTOs) {
            ContractProfileEntity contractProfileEntity = new ContractProfileEntity();
            if (!Base64.isBase64(contractProfileDTO.getFileBase64())) {
                throw new EtcException("crm.file.is.not.base64");
            }
            byte[] file = Base64.decodeBase64(contractProfileDTO.getFileBase64());
            String filePath = String.format("/%s/%s/%s", custId, contractId, UUID.randomUUID().toString() + "-" + contractProfileDTO.getFileName());
            if (!FnCommon.checkBriefcaseValid(filePath, file, briefcaseMaxFileSize)) {
                throw new EtcException("crm.briefcase.invalid");
            }
            contractProfileEntity.setDocumentTypeId(contractProfileDTO.getDocumentTypeId());
            contractProfileEntity.setStatus(ContractProfileEntity.Status.EXIST.value);
            contractProfileEntity.setContractId(contractId);
            contractProfileEntity.setCustId(custId);
            contractProfileEntity.setFileName(contractProfileDTO.getFileName());
            contractProfileEntity.setFilePath(filePath);
            contractProfileEntity.setCreateDate(createDate);
            contractProfileEntity.setCreateUser(FnCommon.getUserLogin(authentication));
            fileService.uploadFile(filePath, file);
            contractProfileServiceJPA.save(contractProfileEntity);
            // khong luu base64 vao log
            contractProfileEntity.setFileSize(null);
            ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(contractProfileEntity)
                    .toEntity(actionAuditEntity.getActionAuditId(), ContractProfileEntity.class.getAnnotation(Table.class).name(),
                            contractProfileEntity.getContractId());
            actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
        }
    }

    /**
     * ky phu luc hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data phu luc hop dong
     * @return
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Override
    @Transactional(noRollbackFor = EtcException.class)
    public Object appendContract(AddVehicleRequestDTO dataParams, Authentication authentication, Long customerId, Long contractId, String remoteAddr) throws IOException, EtcException, JAXBException, DataNotFoundException, XMLStreamException {
        checkCustomerExits(customerId);
        checkContractBeLongCustomer(customerId, contractId);
        ContractEntity contractEntity = contractServiceJPA.findById(contractId).orElseThrow(new EtcException("crm.contract.not.exist"));
        dataParams.setContractAppendix(generateContractAppendixNo(contractEntity.getContractId(), contractEntity.getContractNo()));
        if (Objects.nonNull(dataParams.getPlateType())) {
            dataParams.setPlateTypeId(dataParams.getPlateType());
        }
        return vehicleService.registerVehicle(dataParams, authentication, customerId, contractId, remoteAddr);
    }

    /**
     * sua doi hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data sua hop dong
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Object editContract(ContractDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws EtcException, Exception {
        FnCommon.checkDateIsAfter(dataParams.getEffDate(), dataParams.getExpDate());
        checkContractActive(Lists.newArrayList(contractId));
        checkCustomerExits(customerId);
        ContractEntity contract = this.contractServiceJPA.findById(contractId).orElseThrow(() -> new EtcException("crm.contract.not.exist"));
        ContractEntity contractEntityOld = new Gson().fromJson(new Gson().toJson(contract), ContractEntity.class);
        checkContractBeLongCustomer(customerId, contract);
        dataParams.setDataToEntityOnEditContract(contract);
        contract = this.contractServiceJPA.save(contract);
        updateLogContract(authentication, contractEntityOld, contract, dataParams.getReasonId(), dataParams.getActTypeId());
        addSaleTransAndSaleTransDetails(customerId, contractId, contract.getContractNo(), authentication, dataParams.getActTypeId());
        Optional<CustomerEntity> customer = customerServiceJPA.findById(contract.getCustId());
        if (!customer.isPresent()) {
            LOG.warn("Update contract, customer not found, customerId: {}, contractId: {}", contract.getCustId(), contract.getContractId());
            return ContractDTO.builder().contractId(contract.getContractId()).build();
        }
//        ContractPaymentEntity payment = null;
//        if (contract.getPaymentDefaultId() != null && contract.getPaymentDefaultId() >= 0) {
//        Optional<ContractPaymentEntity> paymentEntityOptional = contractPaymentServiceJPA.findById(contract.getPaymentDefaultId());
//            if (paymentEntityOptional.isPresent()) {
//                payment = paymentEntityOptional.get();
//            }
//        }
        OCSResponse ocsResponse = ocsService.updateContract(new OCSDTO().updateContractOCSFormFromEntity(contract, customer.get(), dataParams), authentication, dataParams.getActTypeId().intValue());
        if ("0".equals(ocsResponse.getResultCode())) {
            LOG.info("Update contract success, customerId: {}, contractId: {}", contract.getCustId(), contract.getContractId());
        } else {
            throw new EtcException("ocs.error.update.contract");
        }
        return ContractDTO.builder().contractId(contract.getContractId()).build();
    }

    /**
     * tach hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data tach hop dong
     * @return
     * @throws IOException
     */
    @Override
    public Object splitContract(SplitContractDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws Exception {
        ContractDTO contractDTO = (ContractDTO) addContract(dataParams, authentication, customerId);
        Optional<ContractEntity> contractEntityOptional = contractRepositoryJPA.findById(contractDTO.getContractId());
        if (!contractEntityOptional.isPresent()) {
            throw new EtcException("crm.contract.not.exist");
        }
        ContractEntity contractEntity = contractEntityOptional.get();
        List<VehicleEntity> allByContractId = this.vehicleServiceJPA.findAllById(dataParams.getVehicleIds());
        VehicleTransferDTO vehicleTransferDTO = VehicleTransferDTO.builder()
                .transferContractId(contractEntity.getContractId())
                .transferVehicleIds(allByContractId.stream().map(VehicleEntity::getVehicleId).collect(Collectors.toList()))
                .actTypeId(dataParams.getActTypeId())
                .reasonId(dataParams.getReasonId())
                .build();
        vehicleService.transferVehicleOnContract(vehicleTransferDTO, authentication);
        addSaleTransAndSaleTransDetails(customerId, contractEntity.getContractId(), contractEntity.getContractNo(), authentication, dataParams.getActTypeId());
        return ContractDTO.builder().contractId(contractDTO.getContractId()).build();
    }

    /**
     * huy hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param dataParams     form data huy hop dong
     * @return
     * @throws IOException
     */
    @Override
    public void terminateContract(TerminateContractDTO dataParams, Authentication authentication, Long customerId) throws Exception, EtcException {
        checkCustomerExits(customerId);
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        List<ContractEntity> terminateContracts = this.contractServiceJPA.findAllById(dataParams.getContractIds());
        checkContractActive(dataParams.getContractIds());
        for (ContractEntity contractEntity : terminateContracts) {
            ContractEntity contractEntityOld = new Gson().fromJson(new Gson().toJson(contractEntity), ContractEntity.class);
            if (!contractEntity.getCustId().equals(customerId)) {
                throw new EtcException("crm.contract.not.belong.customer");
            }
            contractEntity.setEffDate(currDate);
            //1- Tiếp nhận, chưa hoạt động
            //2- Hoạt động
            //3- Hủy
            //4 -Chấm dứt
            contractEntity.setStatus(ContractEntity.Status.TERMINATE.value);
            updateLogContract(authentication, contractEntityOld, contractEntity, dataParams.getActTypeId(), dataParams.getReasonId());
            addSaleTransAndSaleTransDetails(customerId, contractEntity.getContractId(), contractEntity.getContractNo(), authentication, dataParams.getActTypeId());
            updateProfile(authentication, currDate, contractEntity, dataParams.getReasonId(), dataParams.getActTypeId(), dataParams.getContractProfileDTOs());
        }
        List<String> activeStatus = new ArrayList<>();
        activeStatus.add(VehicleEntity.ActiveStatus.ACTIVATED.value);
        activeStatus.add(VehicleEntity.ActiveStatus.CLOSED.value);
        activeStatus.add(VehicleEntity.ActiveStatus.OPEN.value);
        List<VehicleEntity> terminateVehicles = this.vehicleServiceJPA.findByContractIdInAndStatusAndActiveStatus(dataParams.getContractIds(),
                VehicleEntity.Status.ACTIVATED.value, activeStatus);
        for (VehicleEntity vehicleEntity : terminateVehicles) {
            VehicleEntity vehicleEntityOld = new Gson().fromJson(new Gson().toJson(vehicleEntity), VehicleEntity.class);

            vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.CANCEL.value);
            vehicleEntity.setStatus(VehicleEntity.Status.NOT_ACTIVATED.value);
            vehicleEntity.setExpDate(currDate);
            updateLogVehicle(authentication, vehicleEntityOld, vehicleEntity, dataParams.getReasonId(), dataParams.getActTypeId());
        }
        this.contractServiceJPA.saveAll(terminateContracts);
        this.vehicleServiceJPA.saveAll(terminateVehicles);
        for (ContractEntity contractEntity : terminateContracts) {
            String token = null;
            if (contractEntity.getPaymentDefaultId() != null) {
                Optional<ContractPaymentEntity> paymentEntityOptional = contractPaymentServiceJPA.findById(contractEntity.getPaymentDefaultId());
                if (paymentEntityOptional.isPresent()) {
                    token = paymentEntityOptional.get().getToken();
                }
            }
            OCSResponse ocsResponse = ocsService.terminateContract(OCSUpdateContractForm.builder()
                    .contractId(contractEntity.getContractId().toString()).token(token).build(), authentication, Math.toIntExact(dataParams.getActTypeId()));
            if ("0".equals(ocsResponse.getResultCode())) {
                LOG.info("Terminate contract success, customerId: {}, contractId: {}", customerId, contractEntity.getContractId());
            } else {
                LOG.info("Terminate contract fall, customerId: {}, contractId: {}", customerId, contractEntity.getContractId());
                throw new EtcException("ocs.error.terminate.contract");
            }
        }
    }

    /**
     * Update log cho phuong tien
     *
     * @param authentication
     * @param vehicleEntityOld
     * @param vehicleEntity
     * @param reasonId
     * @param actTypeId
     */
    private void updateLogVehicle(Authentication authentication, VehicleEntity vehicleEntityOld, VehicleEntity vehicleEntity, Long reasonId, Long actTypeId) throws Exception {
        // update action audit
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, vehicleEntityOld.getCustId(), vehicleEntityOld.getContractId(), vehicleEntity.getVehicleId()));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(vehicleEntityOld, vehicleEntity).toEntity(actionAuditEntity.getActionAuditId(), VehicleEntity.class.getAnnotation(Table.class).name(), vehicleEntity.getVehicleId());
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
    }

    /**
     * gop hop dong
     *
     * @param authentication user dang nhap
     * @param customerId     id khach hang
     * @param contractId     id hop dong
     * @param dataParams     form data gop hop dong
     * @return
     * @throws IOException
     */
    @Override
    public void mergeContract(MergeContractDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws Exception {
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        ContractEntity targetContract = contractServiceJPA.findById(contractId).get();
        dataParams.getSecondaryContractId().remove(contractId);
        if (CollectionUtils.isEmpty(dataParams.getSecondaryContractId())) {
            return;
        }
        List<Long> contractIds = Lists.newArrayList(contractId);
        contractIds.addAll(dataParams.getSecondaryContractId());
        checkContractActive(contractIds);
        List<ContractEntity> secondaryContracts = this.contractServiceJPA.findAllById(dataParams.getSecondaryContractId());

        List<VehicleEntity> vehicleEntities = this.vehicleServiceJPA.findAllByContractIdInAndStatusAndActiveStatus(
                secondaryContracts.stream().map(ContractEntity::getContractId).collect(Collectors.toList()),
                VehicleEntity.Status.ACTIVATED.value,
                VehicleEntity.ActiveStatus.ACTIVATED.value);

        for (ContractEntity contractEntity : secondaryContracts) {
            //cap nhat trang thai huy
            ContractEntity contractEntityOld = new Gson().fromJson(new Gson().toJson(contractEntity), ContractEntity.class);
            contractEntity.setStatus(ContractEntity.Status.CANCEL.value);
            updateProfile(authentication, currDate, contractEntity, dataParams.getActTypeId(), dataParams.getReasonId(), dataParams.getContractProfiles());
            updateLogContract(authentication, contractEntityOld, contractEntity, dataParams.getReasonId(), dataParams.getActTypeId());
            VehicleTransferDTO vehicleTransferDTO = VehicleTransferDTO.builder()
                    .transferContractId(contractId)
                    .transferVehicleIds(vehicleEntities.stream().map(VehicleEntity::getVehicleId).collect(Collectors.toList()))
                    .actTypeId(dataParams.getActTypeId())
                    .reasonId(dataParams.getReasonId())
                    .build();
            vehicleService.transferVehicleOnContract(vehicleTransferDTO, authentication);
        }
        for (ContractEntity contractEntity : secondaryContracts) {
            String token = null;
            if (contractEntity.getPaymentDefaultId() != null) {
                Optional<ContractPaymentEntity> paymentEntityOptional = contractPaymentServiceJPA.findById(contractEntity.getPaymentDefaultId());
                if (paymentEntityOptional.isPresent()) {
                    token = paymentEntityOptional.get().getToken();
                }
            }
            OCSResponse ocsResponse = ocsService.terminateContract(OCSUpdateContractForm.builder()
                    .contractId(contractEntity.getContractId().toString()).token(token).build(), authentication, dataParams.getActTypeId().intValue());
            if ("0".equals(ocsResponse.getResultCode())) {
                LOG.info("Terminate contract success, customerId: {}, contractId: {}", customerId, contractEntity.getContractId());
            } else {
                LOG.info("Terminate contract fall, customerId: {}, contractId: {}", customerId, contractEntity.getContractId());
                throw new EtcException("ocs.error.terminate.contract");
            }
        }
        addSaleTransAndSaleTransDetails(customerId, targetContract.getContractId(), targetContract.getContractNo(), authentication, dataParams.getActTypeId());
    }

    /**
     * Tim kiem hop dong by id
     *
     * @param contractId params client
     * @return
     */
    @Override
    public Object findContractById(Long contractId) {
        ResultSelectEntity dataResult = contractRepository.findContractById(contractId);
        try {
            ContractByCustomerDTO result = (ContractByCustomerDTO) dataResult.getListData().get(0);
            // check nguoi ky la nguoi uu quyen
            if (result.getCustomerType().equals(Constants.AUTH)) {
                result.setSignName(result.getAuthName());
                result.setSignBirthDate(result.getAuthBirthDate());
                result.setSignGender(result.getAuthGender());
                result.setSignTypeId(result.getAuthTypeId());
                result.setSignNumber(result.getAuthNumber());
                result.setSignDateIssue(result.getAuthDateIssue());
                result.setSignPlaceIssue(result.getAuthPlaceIssue());
                return dataResult;
            }
            // check nguoi ky la nguoi dai dien
            if (result.getCustomerType().equals(Constants.REP)) {
                result.setSignName(result.getRepName());
                result.setSignBirthDate(result.getRepBirthDate());
                result.setSignGender(result.getRepGender());
                result.setSignTypeId(result.getRepTypeId());
                result.setSignNumber(result.getRepNumber());
                result.setSignDateIssue(result.getRepDateIssue());
                result.setSignPlaceIssue(result.getRepPlaceIssue());
                return dataResult;
            }
            // check nguoi ky la khach hang ca nhan
            result.setSignName(result.getCustName());
            result.setSignBirthDate(result.getBirthDate());
            result.setSignGender(result.getGender());
            result.setSignTypeId(result.getDocumentTypeId());
            result.setSignNumber(result.getDocumentNumber());
            result.setSignDateIssue(result.getDateOfIssue());
            result.setSignPlaceIssue(result.getPlaceOfIssue());

        } catch (EtcException ex) {
            LOG.error("Loi findContractById", ex);
            throw new EtcException("ocs.error.search.contract");
        }
        return dataResult;
    }

    /**
     * Tim kiem danh sach hop dong theo param va customer_id
     *
     * @param custId
     * @param requestModel
     * @return
     */
    @Override
    public Object findContractByCustomer(Integer custId, SearchContractByCustomerDTO requestModel) {
        return contractRepository.findContractByCustomer(custId, requestModel);
    }

    /**
     * Tra cuu thong tin theo param
     *
     * @param searchContractDTO
     * @return
     */
    @Override
    public Object searchContract(SearchContractDTO searchContractDTO) {
        return contractRepository.searchContract(searchContractDTO);
    }

    /**
     * Tra cuu thong tin theo user
     *
     * @param authentication
     * @return
     */
    @Override
    public Object getDataUserContract(Authentication authentication) {
        String user = FnCommon.getUserLogin(authentication);
        ContractEntity contractEntity = contractServiceJPA.getByAccountUser(user);  // thong tin hop dong
        if (contractEntity == null || contractEntity.getCustId() == null) {
            throw new EtcException("crm.contract.not.exist");
        }
        CustomerEntity customerEntity = customerServiceJPA.getOne(contractEntity.getCustId());  // thong tin khach hang
        if (customerEntity == null) {
            throw new EtcException("crm.customer.not.exist");
        }
        DateFormat dateFormat = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
        MobileUserDetailDTO dataResult = new MobileUserDetailDTO();
        dataResult.setCusTypeId(customerEntity.getCustTypeId());
        dataResult.setUserName(customerEntity.getCustName());
        if (customerEntity.getBirthDate() != null) {
            dataResult.setBirth(dateFormat.format(customerEntity.getBirthDate()));
        }
        dataResult.setGender(customerEntity.getGender());
        dataResult.setAddress(customerEntity.getAreaName());
        dataResult.setIdentifier(customerEntity.getDocumentNumber());
        if (customerEntity.getDateOfIssue() != null) {
            dataResult.setDateOfIssue(dateFormat.format(customerEntity.getDateOfIssue()));
        }
        dataResult.setPlaceOfIssue(customerEntity.getPlaceOfIssue());
        dataResult.setContractNo(contractEntity.getContractNo());
        if (contractEntity.getSignDate() != null) {
            dataResult.setSignDate(dateFormat.format(contractEntity.getSignDate()));
        }
        if (contractEntity.getEffDate() != null) {
            dataResult.setEffDate(dateFormat.format(contractEntity.getEffDate()));
        }
        if (contractEntity.getExpDate() != null) {
            dataResult.setExpDate(dateFormat.format(contractEntity.getExpDate()));
        }
        dataResult.setPhone(customerEntity.getPhoneNumber());
        dataResult.setEmail(customerEntity.getEmail());
        if (contractEntity.getCustId() != null) {
            dataResult.setCustomerId(String.valueOf(contractEntity.getCustId()));
        }
        if (contractEntity.getContractId() != null) {
            dataResult.setContractId(String.valueOf(contractEntity.getContractId()));
        }
        dataResult.setCusTypeId(customerEntity.getCustTypeId());
        dataResult.setDocumentType(customerEntity.getDocumentTypeId());
        dataResult.setUserId(contractEntity.getAccountUserId());
        dataResult.setRepIdentifier(customerEntity.getRepIdentityNumber());
        dataResult.setRepIdentifierType(customerEntity.getRepIdentityTypeId());
        return dataResult;
    }

    /**
     * Tim kiem file dinh kem  theo contract_id
     *
     * @param contractId
     * @return
     */
    @Override
    public Object findProfileByContract(Long contractId, ContractProfileDTO requestModel) {
        ResultSelectEntity resultData = contractRepository.findProfileByContract(contractId, requestModel);
        List<ContractProfileDTO> contractProfileDTOList = (List<ContractProfileDTO>) resultData.getListData();
        List<ContractProfileDTO> listData = addListProfileToResult(contractProfileDTOList);
        resultData.setListData(listData);
        resultData.setCount(listData.size());
        return resultData;
    }

    /**
     * add list file name to result
     *
     * @param contractProfileDTOList
     * @return
     */
    private List<ContractProfileDTO> addListProfileToResult(List<ContractProfileDTO> contractProfileDTOList) {
        Map<Long, ContractProfileDTO> map = new HashMap<>();
        for (ContractProfileDTO contractProfileDTO : contractProfileDTOList) {
            Long documentTypeId = contractProfileDTO.getDocumentTypeId();
            if (!map.containsKey(documentTypeId)) {
                map.put(documentTypeId, contractProfileDTO);
            }
            ContractProfileDTO temp = map.get(documentTypeId);
            temp.addProfile(contractProfileDTO.getFileName(), contractProfileDTO.getContractProfileId());
        }

        return map.values().stream().collect(Collectors.toList());
    }

    /**
     * Download file dinh kem
     *
     * @param contractId
     * @return
     */
    @Override
    public ContractProfileDTO downloadProfileByContract(Integer contractId) {
        List<ContractProfileDTO> resultData = contractRepository.downloadProfileByContract(contractId);
        return resultData != null && resultData.size() > 0 ? resultData.get(0) : null;
    }

    /**
     * Tim kiem hop dong theo : CONTRACT_NO, PlATE_NUMBER
     *
     * @param dataParams params client
     * @return
     * @author Chucnd
     */
    @Override
    public Object findByPlateNumberAndContractNo(ContractSearchDTO dataParams) {
        return contractRepository.findByPlateNumberAndContractNo(dataParams);
    }

    /**
     * Xoa thong tin hop dong
     *
     * @param authentication
     * @param customerId
     * @param contractId
     * @param profileId
     * @throws EtcException
     * @throws IOException
     */
    @Override
    public void deleteProfile(Authentication authentication, Long customerId, Long contractId, Long profileId) throws EtcException, IOException {
        ContractProfileEntity contractProfileEntity = this.contractProfileServiceJPA.findById(profileId).orElseThrow(new EtcException("crm.profile.not.exist"));
        fileService.removeFile(contractProfileEntity.getFilePath());
        this.contractProfileServiceJPA.deleteById(contractProfileEntity.getContractProfileId());
    }

    private void checkCustomerExits(Long customerId) throws EtcException {
        if (!customerServiceJPA.existsById(customerId)) {
            throw new EtcException("crm.customer.not.exist");
        }
    }

    private void checkContractBeLongCustomer(Long customerId, Long contractId) throws EtcException {
        ContractEntity contractEntity = contractServiceJPA.findById(contractId).orElseThrow(() -> new EtcException("crm.contract.not.exist"));
        if (!contractEntity.getCustId().equals(customerId)) {
            throw new EtcException("crm.contract.not.belong.customer");
        }
    }

    private void checkContractBeLongCustomer(Long customerId, ContractEntity contractEntity) throws EtcException {
        if (!contractEntity.getCustId().equals(customerId)) {
            throw new EtcException("crm.contract.not.belong.customer");
        }
    }

    protected String createUserForContract(String contractNo, ContractDTO contractDTO, Authentication authentication, String passwordApp) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(FnCommon.getStringToken(authentication)));

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();
        body.put("email", contractDTO.getNoticeEmail());
        body.put("username", contractNo);
        body.put("firstName", contractDTO.getNoticeName());
        body.put("lastName", "");
        body.put("enabled", true);

        ArrayNode groups = objectMapper.createArrayNode();
        groups.add(Constants.GROUPS);
        body.put("groups", groups);

        ArrayNode credentials = objectMapper.createArrayNode();
        ObjectNode password = objectMapper.createObjectNode();
        password.put("type", "password");
        password.put("value", passwordApp);
        credentials.add(password);

        body.put("credentials", credentials);
        ResponseEntity<ObjectNode> exchange;
        try {
            exchange = getExchange(restTemplate, body, headers);
        } catch (Exception e) {
            LOG.error("Has ERROR", e);
            throw new EtcException("keycloak.error.create.user");
        }

        String userId = "";
        for (String location : Objects.requireNonNull(exchange.getHeaders().get("Location"))) {
            String[] split = location.split("/");
            userId = split[split.length - 1];
        }
        return userId;
    }

    /**
     * Lay thong tin hop dong ben OCS
     *
     * @param authentication
     * @param contractId
     * @return
     * @throws DataNotFoundException
     */
    @Override
    public ContractDTO getOCSInfo(Authentication authentication, Long contractId) {
        Optional<ContractEntity> contractEntityOptional = contractServiceJPA.findById(contractId);
        if (!contractEntityOptional.isPresent()) {
            throw new EtcException("crm.contract.not.exist");
        }
        ContractEntity contractEntity = contractEntityOptional.get();
        String ocsResponse = ocsService.getContractInfo(authentication, contractId);
        if (ocsResponse.isEmpty()) {
            throw new EtcException("ocs.error.info.contract");
        }

        return new ContractDTO().fromEntity(contractEntity, ocsResponse);
    }

    /**
     * Tim kiem hop dong theo contractId
     *
     * @param contractId
     * @param requestModel
     * @return
     */
    @Override
    public Object findProfileByContractId(Long contractId, ContractProfileDTO requestModel) {
        return contractRepository.findProfileByContractId(contractId, requestModel);
    }

    public synchronized String generateContractNo(Long contractId, Long shopId) {
        String currentContract = String.valueOf(contractId);
        if (currentContract.length() < 9) {
            currentContract = StringUtils.leftPad(currentContract, 9, "0");
        }
        return StringUtils.leftPad(currentContract, 10, "V");
    }

    public synchronized String generateContractAppendixNo(Long contractId, String contractNo) {
        String currentContract = String.valueOf(contractId);
        if (currentContract.length() < 9) {
            currentContract = StringUtils.leftPad(currentContract, 9, "0");
        }
        return String.format("%s/PL.%s", StringUtils.leftPad(currentContract, 10, "V"), contractNo);
    }

    long getReasonType(Long actTypeId) {
        List<ActReasonEntity> list = actReasonRepositoryJPA.findAllByActTypeId(actTypeId);
        if (list != null && list.size() > 0) {
            return list.get(0).getActReasonId();
        }
        return 0;
    }

    /**
     * Them thong tin giao dich/chi tiet thong tin giao dich
     *
     * @param custId
     * @param actionTypeId
     * @param authentication
     * @return
     */

    private void addSaleTransAndSaleTransDetails(Long custId, Long contractId, String contractNo, Authentication authentication, long actionTypeId) {
        SaleTransEntity saleTranSaved = saleTransService.addSaleTrans(custId, contractId, contractNo, authentication, actionTypeId);
        if (saleTranSaved != null) {
            saleTransDetailService.addSaleTransDetail(authentication, saleTranSaved.getSaleTransId(), null, actionTypeId);
        }
    }

    private void checkContractActive(List<Long> contractIds) {
        if (contractServiceJPA.countContract(contractIds, ContractEntity.Status.ACTIVATED.value) != contractIds.size()) {
            throw new EtcException("crm.contract.not.exist");
        }
    }

    private <T> ResponseEntity<ObjectNode> getExchange(RestTemplate restTemplate, ObjectNode body, HttpHeaders headers) {
        return restTemplate.exchange(wsRegisterUser,
                HttpMethod.POST, new HttpEntity<>(body, headers),
                ObjectNode.class);
    }

    /**
     * Luong dang ky moi doi voi nguoi dung dich vu superApp
     *
     * @param dataParams form data dang ky nguoi dung superApp
     * @return
     * @throws IOException
     */
    @Override
    @Transactional(noRollbackFor = EtcException.class)
    public Object addSuperAppContract(CustomerSuperAppDTO dataParams) throws Exception, EtcException {
        Long documentTypeId = documentTypeServiceJPA.findDocumentTypeIdByCode(Constants.SUPERAPP_CONSTANT.DOCUMENT_TYPE_CODE);
        dataParams.setDocumentTypeId(documentTypeId);
        dataParams.setCustTypeId(Constants.SUPERAPP_CONSTANT.CUST_TYPE_PERSONAL);
        Long actTypeId = actTypeServiceJPA.findActTypeIdByCode(Constants.SUPERAPP_CONSTANT.ACT_TYPE_CODE);
        Long reasonId = actReasonServiceJPA.findActReasonIdByActTypeId(actTypeId);
        dataParams.setActTypeId(actTypeId);
        dataParams.setReasonId(reasonId);

        // Check cmnd
//        if (customerServiceJPA.existsByDocumentNumber(dataParams.getDocumentNumber(), dataParams.getCustTypeId())) {
        if (customerServiceJPA.existsByDocumentNumberAndCustTypeId(dataParams.getDocumentNumber(), dataParams.getCustTypeId())) {
            throw new EtcException("crm.duplicate.document.number");
        }

        // Check sdt
        if (customerServiceJPA.existsByPhoneNumer(dataParams.getPhoneNumber())) {
            throw new EtcException("superapp.err.validate.phone.exist");
        }

        CustomerEntity customer = dataParams.toAddCustomerEntity();

        /**
         * 1. Hoạt động
         * 0. Không hoạt động
         */
        customer.setStatus(CustomerEntity.Status.ACTIVATED.value);
        CustomerEntity save = this.customerServiceJPA.save(customer);

        ContractEntity contract = dataParams.toAddContractEntity();
        contract.setCustId(save.getCustId());
        contractServiceJPA.save(contract);
        // Tạo tài khoản keycloak
        String contractNo = generateContractNo(contract.getContractId(), contract.getShopId());
        contract.setAccountUserId(createSuperAppUserForContract(contractNo, dataParams));
        contract.setAccountUser(contractNo);
        contract.setContractNo(contractNo);
        contract.setContractId(contract.getContractId());
        contractServiceJPA.save(contract);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("Role_Admin_CRM"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("crm", null, authorities);

        // Insert bang ACTION_AUDIT_DETAIL
        updateLogContract(authentication, null, contract, dataParams.getReasonId(), dataParams.getActTypeId());

        // TODO call OCS create contract
        // OCSResponse ocsResponse = ocsService.createContract(OCSDTO.createContractFormFromContractEntity(contract, customer), authentication, 1);//dataParams.getActTypeId().intValue());
        // if ("0".equals(ocsResponse.getResultCode())) {
        //     contract.setStatus(ContractEntity.Status.ACTIVATED.value);
        //     contractServiceJPA.save(contract); // Insert vao bang CONTRACT
        //     LOG.info("Create contract success, customerId: {}, contractId: {}", contract.getCustId(), contract.getContractId());

        // // send sms for user
        // String contentSMS = String.format(smsRegisterUser, contractNo, dataParams.getPassword());
        // smsService.sendSMS(contract.getNoticePhoneNumber(), contentSMS, authentication);
        // } else {
        //     throw new EtcException("Create contract fail: " + ocsResponse.getDescription());
        // }

        return CustomerSuperAppDTO.builder().contractId(contract.getContractId()).contractNo(contractNo).build();
    }

    @Override
    public Object updateTopUpContract(Authentication authentication, OCSUpdateContractForm params, Long actType) {
        Long minFee = minFeeTopUp(authentication);
        if (Long.parseLong(params.getTopUpBal()) < minFee) {
            throw new EtcException("topup.amount.not.enough");
        }
        ContractEntity contract = contractServiceJPA.getByAccountUser(FnCommon.getUserLogin(authentication));
        ContractPaymentEntity contractPaymentEntity = contractPaymentServiceJPA.getOne(contract.getPaymentDefaultId());
        contractPaymentEntity.setTopupAuto(Long.parseLong(params.getAutoTopUpExt()));
        contractPaymentEntity.setTopupAmount(Long.parseLong(params.getTopUpBal()));
        CustomerEntity customerEntity = customerServiceJPA.getOne(contract.getCustId());
        OCSUpdateContractForm form = new OCSDTO().updateContractOCSFormFromEntity(contract, customerEntity, null);
        form.setTopUpBal(params.getTopUpBal());
        form.setAutoTopUpExt(params.getAutoTopUpExt());
        OCSResponse ocsResponse = ocsService.updateContract(form, authentication, actType.intValue());
        if ("0".equals(ocsResponse.getResultCode())) {
            LOG.info("Update contract success, customerId: {}, contractId: {}", contract.getCustId(), contract.getContractId());
        } else {
            throw new EtcException("ocs.error.update.contract");
        }
        contractPaymentServiceJPA.save(contractPaymentEntity);
        return ContractDTO.builder().contractId(contract.getContractId()).build();
    }

    @Override
    public Long minFeeTopUp(Authentication authentication) {
        ContractEntity contract = contractServiceJPA.getByAccountUser(FnCommon.getUserLogin(authentication));
        Long contractId = contract.getContractId();
        Long contractPaymentId = contract.getPaymentDefaultId();
        if (contractPaymentId == null) {
            throw new EtcException("payment.default.not.set.yet");
        }
        Long vehicleGroup = vehicleServiceJPA.getHighestPriceVehicle(contractId);
        if (vehicleGroup == null) {
            return null;
        }
        return servicePlanServiceJPA.getHighestPriceByVehicle(vehicleGroup);
    }

    protected String createSuperAppUserForContract(String contractNo, CustomerSuperAppDTO customerSuperAppDTO) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(keycloakService.getAdminToken()));

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();
        body.put("email", "");
        body.put("username", contractNo);
        body.put("firstName", "");
        body.put("lastName", "");
        body.put("enabled", true);

        ArrayNode groups = objectMapper.createArrayNode();
        groups.add(Constants.GROUPS);
        body.put("groups", groups);

        ArrayNode credentials = objectMapper.createArrayNode();
        ObjectNode password = objectMapper.createObjectNode();
        password.put("type", "password");
        password.put("value", customerSuperAppDTO.getPassword());
        credentials.add(password);

        body.put("credentials", credentials);
        ResponseEntity<ObjectNode> exchange;
        try {
            exchange = getExchange(restTemplate, body, headers);
        } catch (Exception e) {
            LOG.error("Has ERROR", e);
            throw new EtcException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    String.format("Có lỗi khi tạo tài khoản bên keycloak với số hợp đồng %s : %s", contractNo, e.getMessage()));
        }

        String userId = "";
        for (String location : Objects.requireNonNull(exchange.getHeaders().get("Location"))) {
            String[] split = location.split("/");
            userId = split[split.length - 1];
        }
        return userId;
    }

}
