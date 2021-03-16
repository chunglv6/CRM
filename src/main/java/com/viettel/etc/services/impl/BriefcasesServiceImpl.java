package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.BriefcasesRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.ActionAuditService;
import com.viettel.etc.services.BriefcasesService;
import com.viettel.etc.services.ContractService;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BriefcasesServiceImpl implements BriefcasesService {
    private final static Logger LOGGER = LoggerFactory.getLogger(BriefcasesServiceImpl.class);

    @Autowired
    VehicleService vehicleService;

    @Autowired
    ContractService contractService;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    CustomerServiceJPA customerServiceJPA;

    @Autowired
    BriefcasesRepository briefcasesRepository;

    @Autowired
    CustomerRepositoryJPA customerRepositoryJPA;

    @Autowired
    ContractProfileRepositoryJPA contractProfileRepositoryJPA;

    @Autowired
    VehicleProfileRepositoryJPA vehicleProfileRepositoryJPA;

    @Autowired
    ContractRepositoryJPA contractRepositoryJPA;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    ActionAuditService actionAuditService;

    @Autowired
    ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Autowired
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    /**
     * Bo sung ho so
     *
     * @param authentication          Thong tin nguoi dung
     * @param contractId              id hop dong
     * @param additionalBriefcasesDTO form data bo sung hop dong
     * @return
     * @throws IOException
     */
    @Override
    public Object additionalBriefcases(AdditionalBriefcasesDTO additionalBriefcasesDTO, Authentication authentication, Long contractId) throws Exception, EtcException {
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        Long actTypeId = additionalBriefcasesDTO.getActTypeId();
        ContractEntity contract = contractServiceJPA.findById(contractId).get();
        if (!(contract.getProfileStatus().equals(ContractEntity.ProfilesStatus.REJECT.value)
                || contract.getProfileStatus().equals(ContractEntity.ProfilesStatus.NOT_RECEIVED.value))) {
            throw new EtcException("crm.briefcase.status.not.accepted");
        }

        CustomerEntity customerEntity = customerServiceJPA.findById(additionalBriefcasesDTO.getCustId()).orElseThrow(RuntimeException::new);
        customerEntity.setCustName(additionalBriefcasesDTO.getCustName());
        customerEntity.setBirthDate(additionalBriefcasesDTO.getBirthDate());
        customerEntity.setGender(additionalBriefcasesDTO.getGender());
        customerEntity.setDateOfIssue(additionalBriefcasesDTO.getDateOfIssue());
        customerEntity.setPlaceOfIssue(additionalBriefcasesDTO.getPlaceOfIssue());
        customerServiceJPA.save(customerEntity);

        ContractEntity contractOld = new Gson().fromJson(new Gson().toJson(contract), ContractEntity.class);
        contract.setProfileStatus(ContractEntity.ProfilesStatus.ADDITIONAL_RECORDS.value);
        contractServiceJPA.save(contract);
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogAuditAndAuditDetail(authentication, null,
                actTypeId, contractOld.getCustId(), contractOld.getContractId(), null, contractOld, contract);

        contractService.updateProfile(authentication, currDate, customerEntity.getCustId(), contractId, actionAuditEntity, additionalBriefcasesDTO.getContractProfiles());
        for (Map.Entry<Long, List<VehicleProfileDTO>> vehicleProfilesById : additionalBriefcasesDTO.getVehicleProfilesById().entrySet()) {
            vehicleService.updateProfile(currDate, authentication, contractId, vehicleProfilesById.getKey(), actionAuditEntity, vehicleProfilesById.getValue());
        }

        return CustomerDTO.builder().custId(customerEntity.getCustId()).build();
    }

    /**
     * Tim kiem thong tin ho so
     *
     * @param searchBriefcasesDTO params client
     * @return
     */
    @Override
    public Object searchBriefcases(SearchBriefcasesDTO searchBriefcasesDTO) {
        if (FnCommon.isNullOrEmpty(searchBriefcasesDTO.getFromDate())) {
            throw new EtcException("validate.briefcase.from.date.not.empty");
        }

        if (FnCommon.isNullOrEmpty(searchBriefcasesDTO.getToDate())) {
            throw new EtcException("validate.briefcase.to.date.not.empty");
        }

        if (searchBriefcasesDTO.getShopId() == null) {
            throw new EtcException("validate.briefcase.shopId.not.empty");
        }

        return briefcasesRepository.searchBriefcases(searchBriefcasesDTO);
    }


    /**
     * cap nhat customer
     *
     * @param acceptBriefcasesDTO
     */
    void updateCustomer(Authentication authentication, AcceptBriefcasesDTO acceptBriefcasesDTO, long reasonId) {
        CustomerEntity customerEntity = customerRepositoryJPA.findAllByCustId(acceptBriefcasesDTO.getCustId());
        if (customerEntity != null) {
            CustomerEntity customerEntityOld = new Gson().fromJson(new Gson().toJson(customerEntity), CustomerEntity.class);
            customerEntity = setValueToCustomer(acceptBriefcasesDTO, customerEntity);
            customerRepositoryJPA.save(customerEntity);
            updateLog(authentication, reasonId, acceptBriefcasesDTO.getActTypeId(), acceptBriefcasesDTO.getContractId(), acceptBriefcasesDTO.getCustId(), customerEntity.getCustId(), customerEntityOld, customerEntity, CustomerEntity.class.getAnnotation(Table.class).name());
        }
    }

    /**
     * Cap nhat contractProfile
     *
     * @param briefcasesDocumentsDTOList
     */
    void updateContractProfile(List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList, Authentication authentication, long contractId, Long customerId, long actTypeId, long reasonId) {
        for (BriefcasesDocumentsDTO briefcasesDocumentsDTO : briefcasesDocumentsDTOList) {
            List<ProfileDTO> contractProfileDTOList = briefcasesDocumentsDTO.getListProfile();
            for (ProfileDTO profileDTO : contractProfileDTOList) {
                ContractProfileEntity contractProfileEntity = contractProfileRepositoryJPA.findAllByContractProfileId(profileDTO.getProfileId());
                if (contractProfileEntity != null) {
                    ContractProfileEntity contractProfileEntityOld = new Gson().fromJson(new Gson().toJson(contractProfileEntity), ContractProfileEntity.class);
                    if (briefcasesDocumentsDTO.isFake()) {
                        contractProfileEntity.setStatus(ContractProfileEntity.Status.FAKE.value);
                    } else if (briefcasesDocumentsDTO.isExist()) {
                        contractProfileEntity.setStatus(ContractProfileEntity.Status.EXIST.value);
                    } else {
                        contractProfileEntity.setStatus(ContractProfileEntity.Status.MISSED.value);
                    }
                    if (!FnCommon.isNullOrEmpty(briefcasesDocumentsDTO.getDescription())) {
                        contractProfileEntity.setDescription(briefcasesDocumentsDTO.getDescription());
                    }
                    contractProfileRepositoryJPA.save(contractProfileEntity);

                    updateLog(authentication, reasonId,
                            actTypeId, contractId, customerId, contractProfileEntity.getContractProfileId(),
                            contractProfileEntityOld, contractProfileEntity, ContractProfileEntity.class.getAnnotation(Table.class).name());
                }
            }
        }
    }

    /**
     * Cap nhat contract
     *
     * @param contractId
     * @param userLogin
     * @param isAccept
     */
    void updateContract(AcceptBriefcasesDTO acceptBriefcasesDTO, Authentication authentication, Long contractId, String userLogin, boolean isAccept, long reasonId) {
        ContractEntity contractEntity = contractRepositoryJPA.findAllByContractId(contractId);
        if (!ContractEntity.ProfilesStatus.NOT_RECEIVED.value.equals(contractEntity.getProfileStatus()) &&
                !ContractEntity.ProfilesStatus.ADDITIONAL_RECORDS.value.equals(contractEntity.getProfileStatus())) {
            throw new EtcException("crm.briefcase.status.not.accepted");
        }
        ContractEntity contractEntityOld = new Gson().fromJson(new Gson().toJson(contractEntity), ContractEntity.class);
        contractEntity.setApprovedUser(userLogin);
        contractEntity.setApprovedDate(new Date(System.currentTimeMillis()));
        if (isAccept) {
            contractEntity.setProfileStatus(ContractEntity.ProfilesStatus.APPROVED.value);
        } else {
            contractEntity.setProfileStatus(ContractEntity.ProfilesStatus.REJECT.value);
        }
        if (acceptBriefcasesDTO.getReason() != null && !"".equals(acceptBriefcasesDTO.getReason())) {
            contractEntity.setNote(acceptBriefcasesDTO.getReason());
        }
        contractRepositoryJPA.save(contractEntity);
        updateLog(authentication, reasonId, acceptBriefcasesDTO.getActTypeId(), acceptBriefcasesDTO.getContractId(), acceptBriefcasesDTO.getCustId(), contractEntity.getContractId(), contractEntityOld, contractEntity, ContractEntity.class.getAnnotation(Table.class).name());

    }

    /**
     * Cap nhat vehicle
     *
     * @param isAccept
     * @param userLogin
     */
    void updateVehicle(Authentication authentication, Long reasonId, Long vehicleId,
                       Long actTypeId,
                       Long contractId, Long custId,
                       boolean isAccept, String userLogin, String reason) {
        VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(vehicleId);
        if (!VehicleEntity.ProfilesStatus.NOT_RECEIVED.value.equals(vehicleEntity.getProfileStatus()) &&
                !VehicleEntity.ProfilesStatus.ADDITIONAL_RECORDS.value.equals(vehicleEntity.getProfileStatus())) {
            throw new EtcException("crm.briefcase.status.not.accepted");
        }
        VehicleEntity vehicleEntityOld = new Gson().fromJson(new Gson().toJson(vehicleEntity), VehicleEntity.class);
        vehicleEntity.setApprovedUser(userLogin);
        vehicleEntity.setApprovedDate(new Date(System.currentTimeMillis()));
        if (isAccept) {
            vehicleEntity.setProfileStatus(VehicleEntity.ProfilesStatus.APPROVED.value);
        } else {
            vehicleEntity.setProfileStatus(VehicleEntity.ProfilesStatus.REJECT.value);
        }
        if (reason != null && !"".equals(reason)) {
            vehicleEntity.setNote(reason);
        }
        vehicleRepositoryJPA.save(vehicleEntity);
        updateLog(authentication, reasonId, actTypeId, contractId, custId,
                vehicleEntity.getVehicleId(), vehicleEntityOld, vehicleEntity, VehicleEntity.class.getAnnotation(Table.class).name());

    }

    /**
     * set value to customer
     *
     * @param acceptBriefcasesDTO
     */
    public CustomerEntity setValueToCustomer(AcceptBriefcasesDTO acceptBriefcasesDTO, CustomerEntity customerEntity) {

        if (acceptBriefcasesDTO.getBirthDay() != null && !FnCommon.isNullOrEmpty(acceptBriefcasesDTO.getBirthDay().toString().trim())) {
            if (customerEntity.getAuthName() != null) {
                customerEntity.setAuthBirthDate(new Date(acceptBriefcasesDTO.getBirthDay().getTime()));
            } else if (customerEntity.getRepName() != null) {
                customerEntity.setRepBirthDate(new Date(acceptBriefcasesDTO.getBirthDay().getTime()));
            } else {
                customerEntity.setBirthDate(new Date(acceptBriefcasesDTO.getBirthDay().getTime()));
            }

        }
        if (!FnCommon.isNullOrEmpty(acceptBriefcasesDTO.getGender())) {
            if (customerEntity.getAuthName() != null) {
                customerEntity.setAuthGender(acceptBriefcasesDTO.getGender().trim());
            } else if (customerEntity.getRepName() != null) {
                customerEntity.setRepGender(acceptBriefcasesDTO.getGender().trim());
            } else {
                customerEntity.setGender(acceptBriefcasesDTO.getGender().trim());
            }
        }

        if (acceptBriefcasesDTO.getAuthDateOfIssue() != null && !FnCommon.isNullOrEmpty(acceptBriefcasesDTO.getAuthDateOfIssue().toString().trim())) {
            if (customerEntity.getAuthName() != null) {
                customerEntity.setAuthDateOfIssue(new Date(acceptBriefcasesDTO.getAuthDateOfIssue().getTime()));
            } else if (customerEntity.getRepName() != null) {
                customerEntity.setRepDateOfIssue(new Date(acceptBriefcasesDTO.getAuthDateOfIssue().getTime()));
            } else {
                customerEntity.setDateOfIssue(new Date(acceptBriefcasesDTO.getAuthDateOfIssue().getTime()));
            }
        }

        if (!FnCommon.isNullOrEmpty(acceptBriefcasesDTO.getAuthPlaceOfIssue())) {
            if (customerEntity.getAuthName() != null) {
                customerEntity.setAuthPlaceOfIssue(acceptBriefcasesDTO.getAuthPlaceOfIssue());
            } else if (customerEntity.getRepName() != null) {
                customerEntity.setRepPlaceOfIssue(acceptBriefcasesDTO.getAuthPlaceOfIssue());
            } else {
                customerEntity.setPlaceOfIssue(acceptBriefcasesDTO.getAuthPlaceOfIssue());
            }
        }

        if (acceptBriefcasesDTO.getSignName() != null && !FnCommon.isNullOrEmpty(acceptBriefcasesDTO.getSignName().trim())) {
            if (customerEntity.getAuthName() != null) {
                customerEntity.setAuthName(acceptBriefcasesDTO.getSignName());
            } else if (customerEntity.getRepName() != null) {
                customerEntity.setRepName(acceptBriefcasesDTO.getSignName());
            } else {
                customerEntity.setCustName(acceptBriefcasesDTO.getSignName());
            }

        }
        return customerEntity;
    }

    /**
     * Phe duyet hop dong
     *
     * @param authentication      Thong tin nguoi dung
     * @param acceptBriefcasesDTO form data bo sung hop dong
     * @param isAccept            check la phe duyet hay tu choi
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalContract(AcceptBriefcasesDTO acceptBriefcasesDTO, Authentication authentication, boolean isAccept) {
        String userLogin = FnCommon.getUserLogin(authentication);
        Long actTypeId = acceptBriefcasesDTO.getActTypeId();
        Long reasonId = getReasonType(actTypeId);
        updateCustomer(authentication, acceptBriefcasesDTO, reasonId);
        updateContract(acceptBriefcasesDTO, authentication, acceptBriefcasesDTO.getContractId(), userLogin, isAccept, reasonId);
        updateContractProfile(acceptBriefcasesDTO.getBriefcasesDocumentsDTOList(), authentication, acceptBriefcasesDTO.getContractId(), acceptBriefcasesDTO.getCustId(), acceptBriefcasesDTO.getActTypeId(), reasonId);
    }


    /**
     * Phe duyet hop dong
     *
     * @param authentication      Thong tin nguoi dung
     * @param acceptBriefcasesDTO form data bo sung hop dong
     * @param isAccept            check la phe duyet hay tu choi
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectContract(AcceptBriefcasesDTO acceptBriefcasesDTO, Authentication authentication, boolean isAccept) {
        String userLogin = FnCommon.getUserLogin(authentication);
        Long actTypeId = acceptBriefcasesDTO.getActTypeId();
        Long reasonId = getReasonType(actTypeId);
        updateContract(acceptBriefcasesDTO, authentication, acceptBriefcasesDTO.getContractId(), userLogin, isAccept, reasonId);
        updateContractProfile(acceptBriefcasesDTO.getBriefcasesDocumentsDTOList(), authentication, acceptBriefcasesDTO.getContractId(), acceptBriefcasesDTO.getCustId(), acceptBriefcasesDTO.getActTypeId(), reasonId);
    }


    private void updateLog(Authentication authentication, long reasonId, long actTypeId, long contractId, long customerId, long id, Object oldEntity, Object newEntity, String tableName) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId,
                    customerId, contractId, id, ip);
            actionAuditEntity = actionAuditService.updateLogToActionAudit(actionAuditEntity);

            ActionAuditDetailDTO actionAuditDetailDTO = new ActionAuditDetailDTO(oldEntity, newEntity);
            ActionAuditDetailEntity actionAuditDetailEntity = actionAuditDetailDTO.toEntity(actionAuditEntity.getActionAuditId(), tableName, id);
            actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
        } catch (Exception e) {
            LOGGER.error("Co loi khi ghi log: ", e);
        }
    }

    /**
     * Phe duyet phuong tien
     *
     * @param authentication             Thong tin nguoi dung
     * @param acceptBriefcasesVehicleDTO form data bo sung hop dong
     * @param isAccept                   check la phe duyet hay tu choi
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalVehicle(AcceptBriefcasesVehicleDTO acceptBriefcasesVehicleDTO, Authentication authentication, boolean isAccept) {
        String userLogin = FnCommon.getUserLogin(authentication);
        Long actTypeId = acceptBriefcasesVehicleDTO.getActTypeId();
        long reasonId = getReasonType(actTypeId);
        updateVehicle(authentication, reasonId, acceptBriefcasesVehicleDTO.getVehicleId(), actTypeId, acceptBriefcasesVehicleDTO.getContractId(),
                acceptBriefcasesVehicleDTO.getCustId(), isAccept, userLogin, acceptBriefcasesVehicleDTO.getReason());
        updateVehicleProfile(acceptBriefcasesVehicleDTO, authentication, reasonId, actTypeId,
                acceptBriefcasesVehicleDTO.getCustId(), acceptBriefcasesVehicleDTO.getContractId());
    }


    /**
     * Cap nhat vehicleProfile
     *
     * @param
     */
    void updateVehicleProfile(AcceptBriefcasesVehicleDTO apAcceptBriefcasesVehicleDTO, Authentication authentication,
                              Long reasonId, Long actTypeId, Long custId, Long contractId) {

        for (BriefcasesDocumentsDTO briefcasesDocumentsDTO : apAcceptBriefcasesVehicleDTO.getListDocument()) {
            List<ProfileDTO> list = briefcasesDocumentsDTO.getListProfile();
            for (ProfileDTO profileDTO : list) {
                VehicleProfileEntity vehicleProfileEntity = vehicleProfileRepositoryJPA.findByVehicleProfileId(profileDTO.getProfileId());
                if (vehicleProfileEntity != null) {
                    VehicleProfileEntity vehicleProfileEntityOld = new Gson().fromJson(new Gson().toJson(vehicleProfileEntity), VehicleProfileEntity.class);
                    if (briefcasesDocumentsDTO.isFake()) {
                        vehicleProfileEntity.setStatus(VehicleProfileEntity.Status.FAKE.value);
                    } else if (briefcasesDocumentsDTO.isExist()) {
                        vehicleProfileEntity.setStatus(VehicleProfileEntity.Status.EXIST.value);
                    } else {
                        vehicleProfileEntity.setStatus(VehicleProfileEntity.Status.MISSED.value);
                    }
                    if (!FnCommon.isNullOrEmpty(briefcasesDocumentsDTO.getDescription())) {
                        vehicleProfileEntity.setDescription(briefcasesDocumentsDTO.getDescription());
                    }
                    vehicleProfileRepositoryJPA.save(vehicleProfileEntity);
                    updateLog(authentication, reasonId, actTypeId, contractId, custId,
                            vehicleProfileEntity.getVehicleProfileId(), vehicleProfileEntityOld, vehicleProfileEntity, VehicleProfileEntity.class.getAnnotation(Table.class).name());
                }
            }
        }
    }

    long getReasonType(Long actTypeId) {
        List<ActReasonEntity> list = actReasonRepositoryJPA.findAllByActTypeId(actTypeId);
        if (list != null && list.size() > Constants.SIZE_LIST_ZERO) {
            return list.get(0).getActReasonId();
        }
        return Constants.ACT_REASON.DEFAULT;
    }
}
