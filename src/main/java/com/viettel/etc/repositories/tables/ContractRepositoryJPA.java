package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Contract
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Repository
public interface ContractRepositoryJPA extends JpaRepository<ContractEntity, Long> {
    ContractEntity findAllByContractId(Long contractId);

    ContractEntity getByAccountUser(String accountUser);

    ContractEntity getByAccountAlias(String accountAlias);

    ContractEntity findByContractId(Long contractId);

    ContractEntity findByContractNo(String contractNo);

    List<ContractEntity> findByAccountUser(String accountUser);

    Optional<ContractEntity> findByContractIdAndStatus(Long contractId, String status);

    ContractEntity getByAccountUserId(String accountUserId);

    List<ContractEntity> getByCustId(Long custId);

    @Query("select count(c.contractId) from ContractEntity c where c.contractId in :contractIds and c.status = :status")
    Long countContractByContractIdAndStatus(List<Long> contractIds, String status);

    @Query("select ct.contractId from ContractEntity  ct inner join VehicleEntity  ve on ve.contractId = ct.contractId and ct.contractNo =:contractNo and" +
            " ve.epc=:epc and ve.plateNumber = :plateNumber")
    Long getContractIdByContractNoAndEpcAndPlateNumber(String contractNo,String epc,String plateNumber);
}
