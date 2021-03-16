package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.ContractRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Autogen class: Create Service For Table Name Contract
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:23 ICT 2020
 */
@Service
public class ContractServiceJPA {

    @Autowired
    ContractRepositoryJPA contractRepositoryJPA;

    public List<ContractEntity> findAll() {
        return this.contractRepositoryJPA.findAll();
    }

    public ContractEntity save(ContractEntity Contract) {
        return this.contractRepositoryJPA.save(Contract);
    }

    public List<ContractEntity> saveAll(List<ContractEntity> contracts) {
        return this.contractRepositoryJPA.saveAll(contracts);
    }

    public Optional<ContractEntity> findById(Long id) {
        return this.contractRepositoryJPA.findById(id);
    }

    public void deleteById(Long id) {
        this.contractRepositoryJPA.deleteById(id);
    }

    public ContractEntity getByAccountUser(String accountUser) {
        return this.contractRepositoryJPA.getByAccountUser(accountUser);
    }

    public ContractEntity getByContractNo(String contractNo) {
        return this.contractRepositoryJPA.findByContractNo(contractNo);
    }

    public ContractEntity getByAccountAlias(String accountAlias) {
        return this.contractRepositoryJPA.getByAccountAlias(accountAlias);
    }

    public List<ContractEntity> getByCustId(Long custId) {
        return contractRepositoryJPA.getByCustId(custId);
    }

    public ContractEntity getByAccountUserId(String userId) {
        return contractRepositoryJPA.getByAccountUserId(userId);
    }

    public ContractEntity getOne(Long id) {
        return this.contractRepositoryJPA.getOne(id);
    }

    public Boolean existsById(Long id) {
        return this.contractRepositoryJPA.existsById(id);
    }

    public List<ContractEntity> findAllById(List<Long> contractIds) {
        return contractRepositoryJPA.findAllById(contractIds);
    }

    public List<ContractEntity> findByAccountUser(String accountUser) {
        return contractRepositoryJPA.findByAccountUser(accountUser);
    }

    public Optional<ContractEntity> findByIdAndStatus(Long contractId,String status) {
        return contractRepositoryJPA.findByContractIdAndStatus(contractId,status);
    }

    public ContractEntity findContractByAccountUserId(String accountUserId) {
        return contractRepositoryJPA.getByAccountUserId(accountUserId);
    }

    public Long countContract(List<Long> contractIds, String status) {
        return contractRepositoryJPA.countContractByContractIdAndStatus(contractIds, status);
    }

    public Long getContractIdByContractNoAndEpcAndPlateNumber(String contractNo,String epc,String plateNumber){
        return contractRepositoryJPA.getContractIdByContractNoAndEpcAndPlateNumber(contractNo,epc,plateNumber);
    }
}
