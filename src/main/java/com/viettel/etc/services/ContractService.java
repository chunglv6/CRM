package com.viettel.etc.services;

import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.repositories.tables.entities.ActionAuditEntity;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import org.springframework.security.core.Authentication;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

public interface ContractService {
    Object addContract(ContractDTO dataParams, Authentication authentication, Long customerId) throws Exception, EtcException;

    ContractEntity addContractQuick(ContractDTO dataParams, Authentication authentication, CustomerEntity customerEntity, String password) throws Exception;

    void updateProfile(Authentication authentication, Date createDate, Long custId, Long contractId, Long actTypeId, Long reasonId, List<ContractProfileDTO> contractProfileDTOs) throws Exception;

    void updateProfile(Authentication authentication, Date createDate, Long custId, Long contractId, ActionAuditEntity actionAuditEntity, List<ContractProfileDTO> contractProfileDTOs) throws Exception;

    Object appendContract(AddVehicleRequestDTO dataParams, Authentication authentication, Long customerId, Long contractId, String remoteAddr) throws IOException, EtcException, JAXBException, DataNotFoundException, XMLStreamException;

    Object editContract(ContractDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws EtcException, Exception;

    Object splitContract(SplitContractDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws Exception;

    void terminateContract(TerminateContractDTO dataParams, Authentication authentication, Long customerId) throws Exception, EtcException;

    void mergeContract(MergeContractDTO dataParams, Authentication authentication, Long customerId, Long contractId) throws Exception;

    Object findContractById(Long contractId);

    Object findContractByCustomer(Integer custId, SearchContractByCustomerDTO requestModel);

    Object searchContract(SearchContractDTO searchContractDTO);

    Object getDataUserContract(Authentication authentication);

    Object findProfileByContract(Long contractId, ContractProfileDTO requestModel);

    ContractProfileDTO downloadProfileByContract(Integer contractId);

    Object findByPlateNumberAndContractNo(ContractSearchDTO dataParams);

    void deleteProfile(Authentication authentication, Long customerId, Long contractId, Long profileId) throws EtcException, IOException;

    ContractDTO getOCSInfo(Authentication authentication, Long contractId);

    Object findProfileByContractId(Long contractId, ContractProfileDTO requestModel);

    Object addSuperAppContract(CustomerSuperAppDTO dataParams) throws Exception, EtcException;

    Object updateTopUpContract(Authentication authentication, OCSUpdateContractForm params, Long actType);

    Long minFeeTopUp(Authentication authentication);
}
