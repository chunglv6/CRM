package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.BriefcasesRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.ActionAuditService;
import com.viettel.etc.services.ContractService;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

@ExtendWith(SpringExtension.class)
public class BriefcasesServiceImplTest {

    @InjectMocks
    BriefcasesServiceImpl briefcasesServiceImpl;

    @Mock
    ContractServiceJPA contractServiceJPA;

    @Mock
    CustomerServiceJPA customerServiceJPA;

    @Mock
    ActionAuditService actionAuditService;

    @Mock
    ContractService contractService;

    @Mock
    VehicleService vehicleService;

    @Mock
    BriefcasesRepository briefcasesRepository;

    @Mock
    CustomerRepositoryJPA customerRepositoryJPA;

    @Mock
    ContractProfileRepositoryJPA contractProfileRepositoryJPA;

    @Mock
    ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Mock
    ContractRepositoryJPA contractRepositoryJPA;

    @Mock
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Mock
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Mock
    VehicleProfileRepositoryJPA vehicleProfileRepositoryJPA;

    @Test
    public void testAdditionalBriefcases() throws Exception {
        // param
        Long contractId = 1L;
        Long customerId = 2L;
        Map<Long, List<VehicleProfileDTO>> map = new HashMap<>();
        List<VehicleProfileDTO> vehicleProfileDTOList = new ArrayList<>();
        VehicleProfileDTO vehicleProfileDTO = new VehicleProfileDTO();
        vehicleProfileDTO.setContractId(contractId);
        vehicleProfileDTOList.add(vehicleProfileDTO);
        map.put(1L, vehicleProfileDTOList);
        AdditionalBriefcasesDTO additionalBriefcasesDTO = new AdditionalBriefcasesDTO();
        additionalBriefcasesDTO.setCustId(customerId);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setProfileStatus("3");
        additionalBriefcasesDTO.setVehicleProfilesById(map);
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        CustomerDTO expect = new CustomerDTO();
        expect.setCustId(customerId);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        // mock
        given(contractServiceJPA.findById(contractId)).willReturn(Optional.of(contractEntity));
        given(customerServiceJPA.findById(customerId)).willReturn(Optional.of(customerEntity));
        given(actionAuditService.updateLogAuditAndAuditDetail(any(), any(), any(), any(), any(), any(), any(), any())).willReturn(actionAuditEntity);
        // execute
        assertEquals(expect, briefcasesServiceImpl.additionalBriefcases(additionalBriefcasesDTO, null, contractId));
    }

    @Test
    public void testSearchBriefcases() throws Exception {
        // param
        SearchBriefcasesDTO searchBriefcasesDTO = new SearchBriefcasesDTO();
        searchBriefcasesDTO.setFromDate("1234567");
        searchBriefcasesDTO.setToDate("1234567");
        searchBriefcasesDTO.setShopId(1L);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        resultSelectEntity.setCount(10L);
        // mock
        given(briefcasesRepository.searchBriefcases(searchBriefcasesDTO)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, briefcasesServiceImpl.searchBriefcases(searchBriefcasesDTO));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        // param
        Long reasonId = 1L;
        Long customerId = 2L;
        Long contractId = 3L;
        Long actTypeId = 4L;
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setCustId(customerId);
        acceptBriefcasesDTO.setActTypeId(actTypeId);
        acceptBriefcasesDTO.setContractId(contractId);
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        // mock
        given(customerRepositoryJPA.findAllByCustId(customerId)).willReturn(customerEntity);
        // execute
        briefcasesServiceImpl.updateCustomer(null, acceptBriefcasesDTO, reasonId);
    }

    @Test
    public void testUpdateContractProfile() throws Exception {
        // param
        Long documentTypeId = 1L;
        Long contractId = 2L;
        Long customerId = 3L;
        Long actTypeId = 4L;
        Long reasonId = 5L;
        List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList = new ArrayList<>();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setDocumentTypeId(documentTypeId);
        briefcasesDocumentsDTO.setFake(true);
        briefcasesDocumentsDTO.setDescription("des");
        List<ProfileDTO> profileDTOList = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(10L);
        profileDTOList.add(profileDTO);
        briefcasesDocumentsDTO.setListProfile(profileDTOList);
        briefcasesDocumentsDTOList.add(briefcasesDocumentsDTO);
        ContractProfileEntity contractProfileEntity = new ContractProfileEntity();
        contractProfileEntity.setCustId(customerId);
        contractProfileEntity.setContractProfileId(10L);
        // mock
        given(contractProfileRepositoryJPA.findAllByContractProfileId(any())).willReturn(contractProfileEntity);
        // execute
        briefcasesServiceImpl.updateContractProfile(briefcasesDocumentsDTOList, null, contractId, customerId, actTypeId, reasonId);
    }

    @Test
    public void testUpdateContractProfileCase2() throws Exception {
        // param
        Long documentTypeId = 1L;
        Long contractId = 2L;
        Long customerId = 3L;
        Long actTypeId = 4L;
        Long reasonId = 5L;
        List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList = new ArrayList<>();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setDocumentTypeId(documentTypeId);
        briefcasesDocumentsDTO.setExist(true);
        briefcasesDocumentsDTO.setDescription("des");
        List<ProfileDTO> profileDTOList = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(10L);
        profileDTOList.add(profileDTO);
        briefcasesDocumentsDTO.setListProfile(profileDTOList);
        briefcasesDocumentsDTOList.add(briefcasesDocumentsDTO);
        ContractProfileEntity contractProfileEntity = new ContractProfileEntity();
        contractProfileEntity.setCustId(customerId);
        contractProfileEntity.setContractProfileId(10L);
        // mock
        given(contractProfileRepositoryJPA.findAllByContractProfileId(any())).willReturn(contractProfileEntity);
        // execute
        briefcasesServiceImpl.updateContractProfile(briefcasesDocumentsDTOList, null, contractId, customerId, actTypeId, reasonId);
    }

    @Test
    public void testUpdateContractProfileCase3() throws Exception {
        // param
        Long documentTypeId = 1L;
        Long contractId = 2L;
        Long customerId = 3L;
        Long actTypeId = 4L;
        Long reasonId = 5L;
        List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList = new ArrayList<>();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setDocumentTypeId(documentTypeId);
        briefcasesDocumentsDTO.setDescription("des");
        List<ProfileDTO> profileDTOList = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(10L);
        profileDTOList.add(profileDTO);
        briefcasesDocumentsDTO.setListProfile(profileDTOList);
        briefcasesDocumentsDTOList.add(briefcasesDocumentsDTO);
        ContractProfileEntity contractProfileEntity = new ContractProfileEntity();
        contractProfileEntity.setCustId(customerId);
        contractProfileEntity.setContractProfileId(10L);
        // mock
        given(contractProfileRepositoryJPA.findAllByContractProfileId(any())).willReturn(contractProfileEntity);
        // execute
        briefcasesServiceImpl.updateContractProfile(briefcasesDocumentsDTOList, null, contractId, customerId, actTypeId, reasonId);
    }

    @Test
    public void testUpdateContract() throws Exception {
        // param
        Long contractId = 1L;
        String userLogin = "user";
        Boolean isAccept = true;
        Long reasonId = 2L;
        Long actTypeId = 3L;
        Long customerId = 4L;
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setReason(reasonId.toString());
        acceptBriefcasesDTO.setActTypeId(actTypeId);
        acceptBriefcasesDTO.setCustId(customerId);
        acceptBriefcasesDTO.setContractId(contractId);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setProfileStatus("1");
        // mock
        given(contractRepositoryJPA.findAllByContractId(any())).willReturn(contractEntity);
        //execute
        briefcasesServiceImpl.updateContract(acceptBriefcasesDTO, null, contractId, userLogin, isAccept, reasonId);
    }

    @Test
    public void testUpdateContractCase2() throws Exception {
        // param
        Long contractId = 1L;
        String userLogin = "user";
        Boolean isAccept = false;
        Long reasonId = 2L;
        Long actTypeId = 3L;
        Long customerId = 4L;
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setReason(reasonId.toString());
        acceptBriefcasesDTO.setActTypeId(actTypeId);
        acceptBriefcasesDTO.setCustId(customerId);
        acceptBriefcasesDTO.setContractId(contractId);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractId(contractId);
        contractEntity.setProfileStatus("1");
        // mock
        given(contractRepositoryJPA.findAllByContractId(any())).willReturn(contractEntity);
        //execute
        briefcasesServiceImpl.updateContract(acceptBriefcasesDTO, null, contractId, userLogin, isAccept, reasonId);
    }

    @Test
    public void testUpdateVehicle() throws Exception {
        // param
        Long contractId = 1L;
        String userLogin = "user";
        Boolean isAccept = true;
        Long reasonId = 2L;
        Long actTypeId = 3L;
        Long customerId = 4L;
        Long vehicleId = 5L;
        String reason = "reason";
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setCustId(customerId);
        vehicleEntity.setProfileStatus("1");
        vehicleEntity.setVehicleId(vehicleId);
        // mock
        given(vehicleRepositoryJPA.findByVehicleId(any())).willReturn(vehicleEntity);
        // execute
        briefcasesServiceImpl.updateVehicle(null, reasonId, vehicleId, actTypeId, contractId, customerId, isAccept, userLogin, reason);
    }

    @Test
    public void testUpdateVehicleCase2() throws Exception {
        // param
        Long contractId = 1L;
        String userLogin = "user";
        Boolean isAccept = false;
        Long reasonId = 2L;
        Long actTypeId = 3L;
        Long customerId = 4L;
        Long vehicleId = 5L;
        String reason = "reason";
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setCustId(customerId);
        vehicleEntity.setProfileStatus("1");
        vehicleEntity.setVehicleId(vehicleId);
        // mock
        given(vehicleRepositoryJPA.findByVehicleId(any())).willReturn(vehicleEntity);
        // execute
        briefcasesServiceImpl.updateVehicle(null, reasonId, vehicleId, actTypeId, contractId, customerId, isAccept, userLogin, reason);
    }

    @Test
    public void testSetValueToCustomer() throws Exception {
        // param
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setBirthDay(new Date(System.currentTimeMillis()));
        acceptBriefcasesDTO.setGender("1");
        acceptBriefcasesDTO.setAuthDateOfIssue(new Date(System.currentTimeMillis()));
        acceptBriefcasesDTO.setAuthPlaceOfIssue("place");
        acceptBriefcasesDTO.setSignName("name");
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setAuthName("auth");
        CustomerEntity expect = new CustomerEntity();

        // mock
        // execute
        assertNotNull(briefcasesServiceImpl.setValueToCustomer(acceptBriefcasesDTO, customerEntity));
    }

    @Test
    public void testSetValueToCustomerCase2() throws Exception {
        // param
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setBirthDay(new Date(System.currentTimeMillis()));
        acceptBriefcasesDTO.setGender("1");
        acceptBriefcasesDTO.setAuthDateOfIssue(new Date(System.currentTimeMillis()));
        acceptBriefcasesDTO.setAuthPlaceOfIssue("place");
        acceptBriefcasesDTO.setSignName("name");
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setRepName("auth");
        CustomerEntity expect = new CustomerEntity();

        // mock
        // execute
        assertNotNull(briefcasesServiceImpl.setValueToCustomer(acceptBriefcasesDTO, customerEntity));
    }

    @Test
    public void testSetValueToCustomerCase3() throws Exception {
        // param
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setBirthDay(new Date(System.currentTimeMillis()));
        acceptBriefcasesDTO.setGender("1");
        acceptBriefcasesDTO.setAuthDateOfIssue(new Date(System.currentTimeMillis()));
        acceptBriefcasesDTO.setAuthPlaceOfIssue("place");
        acceptBriefcasesDTO.setSignName("name");
        CustomerEntity customerEntity = new CustomerEntity();
        CustomerEntity expect = new CustomerEntity();

        // mock
        // execute
        assertNotNull(briefcasesServiceImpl.setValueToCustomer(acceptBriefcasesDTO, customerEntity));
    }

    @Test
    public void testApprovalContract() throws Exception {
        // param
        List<ProfileDTO> listProfile = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1L);
        listProfile.add(profileDTO);
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setContractId(1l);
        acceptBriefcasesDTO.setActTypeId(2L);
        acceptBriefcasesDTO.setCustId(3L);
        List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList = new ArrayList<>();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setDescription("des");
        briefcasesDocumentsDTO.setListProfile(listProfile);

        briefcasesDocumentsDTOList.add(briefcasesDocumentsDTO);
        acceptBriefcasesDTO.setBriefcasesDocumentsDTOList(briefcasesDocumentsDTOList);
        boolean isAccept = true;
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setProfileStatus("1");
        contractEntity.setContractId(1L);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        // mock
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        given(actReasonRepositoryJPA.findAllByActTypeId(any())).willReturn(list);
        given(contractRepositoryJPA.findAllByContractId(any())).willReturn(contractEntity);
        given(actionAuditService.updateLogToActionAudit(any())).willReturn(actionAuditEntity);
        // execute
        briefcasesServiceImpl.approvalContract(acceptBriefcasesDTO, null, isAccept);
    }

    @Test
    public void testUpdateVehicleProfile() throws Exception {
        // param
        Long reasonId = 1L;
        Long actTypeId = 2L;
        Long contractId = 3L;
        Long customerId = 4L;
        List<ProfileDTO> listProfile = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1L);
        listProfile.add(profileDTO);
        AcceptBriefcasesVehicleDTO apAcceptBriefcasesVehicleDTO = new AcceptBriefcasesVehicleDTO();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setListProfile(listProfile);
        briefcasesDocumentsDTO.setFake(true);
        briefcasesDocumentsDTO.setDescription("des");
        List<BriefcasesDocumentsDTO> listDocument  = new ArrayList<>();
        listDocument.add(briefcasesDocumentsDTO);
        apAcceptBriefcasesVehicleDTO.setListDocument(listDocument);
        VehicleProfileEntity vehicleProfileEntity = new VehicleProfileEntity();
        vehicleProfileEntity.setContractId(contractId);
        vehicleProfileEntity.setVehicleProfileId(1L);

        // mock
        given(vehicleProfileRepositoryJPA.findByVehicleProfileId(any())).willReturn(vehicleProfileEntity);
        // execute
        briefcasesServiceImpl.updateVehicleProfile(apAcceptBriefcasesVehicleDTO, null, reasonId, actTypeId, contractId, customerId);
    }

    @Test
    public void testUpdateVehicleProfileCase2() throws Exception {
        // param
        Long reasonId = 1L;
        Long actTypeId = 2L;
        Long contractId = 3L;
        Long customerId = 4L;
        List<ProfileDTO> listProfile = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1L);
        listProfile.add(profileDTO);
        AcceptBriefcasesVehicleDTO apAcceptBriefcasesVehicleDTO = new AcceptBriefcasesVehicleDTO();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setListProfile(listProfile);
        briefcasesDocumentsDTO.setExist(true);
        briefcasesDocumentsDTO.setDescription("des");
        List<BriefcasesDocumentsDTO> listDocument  = new ArrayList<>();
        listDocument.add(briefcasesDocumentsDTO);
        apAcceptBriefcasesVehicleDTO.setListDocument(listDocument);
        VehicleProfileEntity vehicleProfileEntity = new VehicleProfileEntity();
        vehicleProfileEntity.setContractId(contractId);
        vehicleProfileEntity.setVehicleProfileId(1L);

        // mock
        given(vehicleProfileRepositoryJPA.findByVehicleProfileId(any())).willReturn(vehicleProfileEntity);
        // execute
        briefcasesServiceImpl.updateVehicleProfile(apAcceptBriefcasesVehicleDTO, null, reasonId, actTypeId, contractId, customerId);
    }

    @Test
    public void testUpdateVehicleProfileCase3() throws Exception {
        // param
        Long reasonId = 1L;
        Long actTypeId = 2L;
        Long contractId = 3L;
        Long customerId = 4L;
        List<ProfileDTO> listProfile = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1L);
        listProfile.add(profileDTO);
        AcceptBriefcasesVehicleDTO apAcceptBriefcasesVehicleDTO = new AcceptBriefcasesVehicleDTO();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setListProfile(listProfile);
        briefcasesDocumentsDTO.setDescription("des");
        List<BriefcasesDocumentsDTO> listDocument  = new ArrayList<>();
        listDocument.add(briefcasesDocumentsDTO);
        apAcceptBriefcasesVehicleDTO.setListDocument(listDocument);
        VehicleProfileEntity vehicleProfileEntity = new VehicleProfileEntity();
        vehicleProfileEntity.setContractId(contractId);
        vehicleProfileEntity.setVehicleProfileId(1L);

        // mock
        given(vehicleProfileRepositoryJPA.findByVehicleProfileId(any())).willReturn(vehicleProfileEntity);
        // execute
        briefcasesServiceImpl.updateVehicleProfile(apAcceptBriefcasesVehicleDTO, null, reasonId, actTypeId, contractId, customerId);
    }

    @Test
    public void testRejectContract() throws Exception {
        // param
        List<ProfileDTO> listProfile = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1L);
        listProfile.add(profileDTO);
        AcceptBriefcasesDTO acceptBriefcasesDTO = new AcceptBriefcasesDTO();
        acceptBriefcasesDTO.setContractId(1l);
        acceptBriefcasesDTO.setActTypeId(2L);
        acceptBriefcasesDTO.setCustId(3L);
        List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList = new ArrayList<>();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setDescription("des");
        briefcasesDocumentsDTO.setListProfile(listProfile);

        briefcasesDocumentsDTOList.add(briefcasesDocumentsDTO);
        acceptBriefcasesDTO.setBriefcasesDocumentsDTOList(briefcasesDocumentsDTOList);
        boolean isAccept = true;
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setProfileStatus("1");
        contractEntity.setContractId(1L);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        // mock
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        given(actReasonRepositoryJPA.findAllByActTypeId(any())).willReturn(list);
        given(contractRepositoryJPA.findAllByContractId(any())).willReturn(contractEntity);
        given(actionAuditService.updateLogToActionAudit(any())).willReturn(actionAuditEntity);
        // execute
        briefcasesServiceImpl.rejectContract(acceptBriefcasesDTO, null, isAccept);
    }

    @Test
    public void testApprovalVehicle() throws Exception {
        // param
        List<ProfileDTO> listProfile = new ArrayList<>();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1L);
        listProfile.add(profileDTO);
        AcceptBriefcasesVehicleDTO acceptBriefcasesDTO = new AcceptBriefcasesVehicleDTO();
        acceptBriefcasesDTO.setContractId(1l);
        acceptBriefcasesDTO.setActTypeId(2L);
        acceptBriefcasesDTO.setCustId(3L);
        List<BriefcasesDocumentsDTO> briefcasesDocumentsDTOList = new ArrayList<>();
        BriefcasesDocumentsDTO briefcasesDocumentsDTO = new BriefcasesDocumentsDTO();
        briefcasesDocumentsDTO.setDescription("des");
        briefcasesDocumentsDTO.setListProfile(listProfile);
        acceptBriefcasesDTO.setListDocument(briefcasesDocumentsDTOList);
        briefcasesDocumentsDTOList.add(briefcasesDocumentsDTO);
        boolean isAccept = true;
        List<ActReasonEntity> list = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(1L);
        list.add(actReasonEntity);
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setProfileStatus("1");
        vehicleEntity.setVehicleId(1L);
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActionAuditId(1L);
        // mock
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        given(actReasonRepositoryJPA.findAllByActTypeId(any())).willReturn(list);
        given(vehicleRepositoryJPA.findByVehicleId(any())).willReturn(vehicleEntity);
        given(actionAuditService.updateLogToActionAudit(any())).willReturn(actionAuditEntity);
        // execute
        briefcasesServiceImpl.approvalVehicle(acceptBriefcasesDTO, null, isAccept);
    }
}
