package com.viettel.etc.services.impl;

import com.viettel.etc.dto.viettelpay.RequestConfirmChangeMoneySourceDTO;
import com.viettel.etc.dto.viettelpay.RequestConfirmRegisterDTO;
import com.viettel.etc.dto.viettelpay.RequestContractPaymentDTO;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.ContractPaymentEntity;
import com.viettel.etc.services.CategoriesService;
import com.viettel.etc.services.ContractPaymentService;
import com.viettel.etc.services.tables.ContractPaymentServiceJPA;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.utils.FnCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ContractPaymentServiceImpl implements ContractPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractPaymentServiceImpl.class);

    @Autowired
    ContractPaymentServiceJPA contractPaymentServiceJPA;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    CategoriesService categoriesService;

    /**
     * Cap nhat contract payment khi lien ket viettelpay
     *
     * @param authentication Ma token
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateContractPaymentWhenRegister(RequestConfirmRegisterDTO data, Authentication authentication) {
        boolean result = false;
        String userToken = FnCommon.getStringToken(authentication);
        String username = FnCommon.getUserLogin(authentication);
        if (!FnCommon.isNullOrEmpty(userToken)) {
            List<LinkedHashMap<?, ?>> categories = categoriesService.findCategoriesByListCode(userToken, data.getBankCode());
            if (categories != null && !categories.isEmpty()) {
                try {
                    Optional<ContractEntity> contractEntity = contractServiceJPA.findByIdAndStatus(data.getContractId(), ContractEntity.Status.ACTIVATED.value);
                    if (contractEntity.isPresent()) {
                        ContractPaymentEntity contractPaymentEntity = findByContractId(data.getContractId());
                        contractPaymentEntity = contractPaymentEntity != null ? contractPaymentEntity : new ContractPaymentEntity();
                        contractPaymentEntity.setOrderId(data.getOrderId());
                        contractPaymentEntity.setContractId(data.getContractId());
                        contractPaymentEntity.setAccountNumber(data.getMsisdn());
                        contractPaymentEntity.setAccountOwner(FnCommon.urlDecodeString(data.getAccountOwner()));
                        contractPaymentEntity.setAccountBankId(Long.parseLong(categories.get(0).get("id").toString()));
                        contractPaymentEntity.setCreateUser(username);
                        contractPaymentEntity.setDescription("Cap nhat khi dang ky");
                        contractPaymentEntity.setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
                        Long methodRechargeId = categories.get(0).get("id") != null ? Long.parseLong(String.valueOf(categories.get(0).get("id"))) : 1L;
                        contractPaymentEntity.setMethodRechargeId(methodRechargeId);
                        contractPaymentEntity.setMethodRechargeCode(data.getBankCode());
                        contractPaymentEntity.setToken(data.getToken());
                        contractPaymentEntity.setChannel(data.getChannel());
                        contractPaymentEntity.setCreateDate(new Date(System.currentTimeMillis()));
                        contractPaymentEntity.setLinkPhone(data.getMsisdn());
                        ContractPaymentEntity contractPaymentUpdate = contractPaymentServiceJPA.save(contractPaymentEntity);
                        if (contractPaymentUpdate.getContractPaymentId() != null) {
                            contractEntity.get().setPaymentDefaultId(contractPaymentUpdate.getContractPaymentId());
                            contractServiceJPA.save(contractEntity.get());
                            result = true;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Cap nhat du lieu khi lien ket loi ", e);
                }
            }
        }
        return result;
    }

    /**
     * Cap nhat contract payment khi huy lien ket viettelpay
     *
     * @return boolean
     */
    @Override
    public boolean updateContractPaymentWhenUnRegister(RequestContractPaymentDTO data) {
        boolean result = false;
        try {
            Optional<ContractEntity> contractEntity = contractServiceJPA.findById(data.getContractId());
            if (contractEntity.isPresent()) {
                if (contractEntity.get().getPaymentDefaultId() != null) {
                    Optional<ContractPaymentEntity> contractPaymentEntity = contractPaymentServiceJPA.findById(contractEntity.get().getPaymentDefaultId());
                    if (contractPaymentEntity.isPresent()) {
                        contractPaymentEntity.get().setStatus(ContractPaymentEntity.Status.NOT_ACTIVATED.value);
                        contractPaymentEntity.get().setChannel(data.getChannel());
                        contractPaymentServiceJPA.save(contractPaymentEntity.get());
                        result = true;
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("Cap nhat du lieu khi huy lien ket loi ", e);
        }
        return result;
    }

    /**
     * Tra ket qua thay doi nguon tien
     *
     * @param authentication Ma token
     * @return boolean
     */
    @Override
    public boolean changeMoneySourceViettelPay(RequestConfirmChangeMoneySourceDTO data, Authentication authentication) {
        boolean result = false;
        String userToken = FnCommon.getStringToken(authentication);
        if (!FnCommon.isNullOrEmpty(userToken)) {
            List<LinkedHashMap<?, ?>> categories = categoriesService.findCategoriesByListCode(userToken, data.getBankCodeAfter());
            if (categories != null && !categories.isEmpty()) {
                try {
                    Optional<ContractEntity> contractEntity = contractServiceJPA.findById(data.getContractId());
                    if (contractEntity.isPresent()) {
                        if (contractEntity.get().getPaymentDefaultId() != null) {
                            Optional<ContractPaymentEntity> contractPaymentEntity = contractPaymentServiceJPA.findById(contractEntity.get().getPaymentDefaultId());
                            Long bankCodeAfterId = Long.parseLong(categories.get(0).get("id").toString());
                            if (contractPaymentEntity.isPresent() && !contractPaymentEntity.get().getAccountBankId().equals(bankCodeAfterId)) {
                                contractPaymentEntity.get().setStatus(ContractPaymentEntity.Status.ACTIVATED.value);
                                contractPaymentEntity.get().setAccountBankId(Long.parseLong(categories.get(0).get("id").toString()));
                                contractPaymentServiceJPA.save(contractPaymentEntity.get());
                                result = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Cap nhat du lieu khi thay doi nguon tien loi ", e);
                }
            }
        }
        return result;
    }

    /**
     * Tim kiem hop dong voi contractId
     *
     * @param contractId Ma hop dong
     * @return ContractPaymentEntity
     */
    @Override
    public ContractPaymentEntity findByContractId(Long contractId) {
        return contractPaymentServiceJPA.findByContractId(contractId);
    }

    /**
     * Tim kiem contract_payment theo contractId va trang thai hoat dong
     *
     * @param contractId Ma hop dong
     * @param status     Trang thai
     * @return ContractPaymentEntity
     */
    @Override
    public ContractPaymentEntity findByContractIdAndStatus(Long contractId, String status) {
        return this.contractPaymentServiceJPA.findByContractIdAndStatus(contractId, status);
    }
}
