package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.ContractRepository;
import com.viettel.etc.repositories.CustomerRepository;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.tables.ActReasonRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.ActionAuditService;
import com.viettel.etc.services.SaleTransService;
import com.viettel.etc.services.tables.ActionAuditDetailServiceJPA;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerServiceImpl;

    @Mock
    CustomerServiceJPA customerServiceJPA;

    @Mock
    ActionAuditService actionAuditService;

    @Mock
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Mock
    ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Mock
    SaleTransService saleTransService;

    @Mock
    ContractServiceJPA contractServiceJPA;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    ContractRepository contractRepository;

    @Test
    public void testAddCustomerEnterprise() throws  Exception {
        // param
        Long actTypeId = 1L;
        String taxCode = "taxCode";
        Long customerId = 2L;
        Long actReasonId = 3L;
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setActTypeId(actTypeId);
        customerDTO.setTaxCode(taxCode);
        customerDTO.setCustId(customerId);
        customerDTO.setAmount(100L);
        CustomerDTO expect = new CustomerDTO();
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setStatus("1");
        customerEntity.setCreateUser("user");
        customerEntity.setCreateDate(new java.sql.Date(new java.util.Date().getTime()));
        customerEntity.setTaxCode(taxCode);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        List<ActReasonEntity> actReasonEntityList = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(actReasonId);
        actReasonEntityList.add(actReasonEntity);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(10L);
        // mock
        given(customerServiceJPA.existsByTaxCode(taxCode)).willReturn(false);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        ReflectionTestUtils.setField(customerServiceImpl,"actionAuditDetailServiceJPA",actionAuditDetailServiceJPA);
        given(customerServiceJPA.save(any())).willReturn(customerEntity);
        ReflectionTestUtils.setField(customerServiceImpl, "customerServiceJPA", customerServiceJPA);
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        ReflectionTestUtils.setField(customerServiceImpl, "actionAuditService", actionAuditService);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        given(actReasonRepositoryJPA.findAllByActTypeId(actTypeId)).willReturn(actReasonEntityList);
        // execute
        assertEquals(expect, customerServiceImpl.addCustomerEnterprise(customerDTO, null));
    }

    @Test
    public void testAddCustomer() throws Exception {
        // param
        Long customerId = 1L;
        Long custTypeId = 2L;
        Long documentTypeId = 3L;
        Long actReasonId = 4L;
        Long actTypeId = 5L;
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustId(customerId);
        customerDTO.setCustTypeId(custTypeId);
        customerDTO.setDocumentTypeId(documentTypeId);
        CustomerDTO expect = new CustomerDTO();
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setStatus("1");
        customerEntity.setCreateUser("user");
        customerEntity.setCreateDate(new java.sql.Date(new java.util.Date().getTime()));
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        List<ActReasonEntity> actReasonEntityList = new ArrayList<>();
        ActReasonEntity actReasonEntity = new ActReasonEntity();
        actReasonEntity.setActReasonId(actReasonId);
        actReasonEntityList.add(actReasonEntity);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setSaleTransId(10L);
        // mock
        given(customerServiceJPA.existsByCustTypeIdAndDocumentTypeId(custTypeId, documentTypeId)).willReturn(false);
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        given(customerServiceJPA.save(any())).willReturn(customerEntity);
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        given(actReasonRepositoryJPA.findAllByActTypeId(actTypeId)).willReturn(actReasonEntityList);
        ReflectionTestUtils.setField(customerServiceImpl,"actionAuditDetailServiceJPA",actionAuditDetailServiceJPA);
        // execute
        assertEquals(expect, customerServiceImpl.addCustomer(customerDTO, null));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        // param
        CustomerDTO customerDTO = new CustomerDTO();
        Long customerId= 1L;
        Long actReasonId = 2L;
        Long actTypeId = 3L;
        customerDTO.setAmount(100L);
        customerDTO.setActTypeId(actTypeId);
        customerDTO.setDocumentNumber("123");
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        customerEntity.setDocumentNumber("123");
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        // mock
        given(customerServiceJPA.findById(customerId)).willReturn(Optional.of(customerEntity));
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        new MockUp<CustomerServiceImpl>(){
            @mockit.Mock
            public void checkExistsDocumentNumber(String document){
                throw new EtcException("crm.duplicate.document.number");
            }
        };
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        //execute
        customerServiceImpl.updateCustomer(customerDTO, customerId , null);
    }

    @Test
    public void testUpdateCustomerPhone() throws Exception {
        // param
        String phone = "phone";
        String email = "email";
        CustomerDTO customerDTO = new CustomerDTO();
        Long customerId= 1L;
        Long actReasonId = 2L;
        Long actTypeId = 3L;
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        ContractEntity contractEntity =  new ContractEntity();
        contractEntity.setCustId(customerId);
        // mock
        given(contractServiceJPA.getByAccountUser("user")).willReturn(contractEntity);
        given(customerServiceJPA.findById(customerId)).willReturn(Optional.of(customerEntity));
        new MockUp<FnCommon>() {
            @mockit.Mock
            public String getUserLogin(Authentication authentication) {
                return "user";
            }
        };
        //execute
        customerServiceImpl.updateCustomer(phone, email , null);
    }

    @Test
    public void testUpdateCustomerEnterprise() throws Exception {
        // param
        Long customerId = 1L;
        Long actReasonId = 2L;
        Long actTypeId = 3L;
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustId(customerId);
        customerDTO.setTaxCode("123");
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustId(customerId);
        customerEntity.setTaxCode("123");
        ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(null, actReasonId, actTypeId, customerId, null, null);
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailEntity();
        actionAuditDetailEntity.setActionAuditDetailId(1L);
        // mock
        given(customerServiceJPA.findById(customerId)).willReturn(Optional.of(customerEntity));
        new MockUp<ActionAuditDetailDTO>(){
            @mockit.Mock
            public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
                return actionAuditDetailEntity;
            }
        };
        new MockUp<ActionAuditDTO>(){
            @mockit.Mock
            public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
                return actionAuditEntity;
            }
        };
        new MockUp<CustomerServiceImpl>(){
            @mockit.Mock
            public void checkExistsTaxCode(String taxCode){
                throw new EtcException("crm.duplicate.tax.code");
            }
        };
        given(actionAuditService.updateLogToActionAudit(actionAuditEntity)).willReturn(actionAuditEntity);
        // execute
        customerServiceImpl.updateCustomerEnterprise(customerDTO, customerId, null);
    }

    @Test
    public void testFindCustomerByDocumentAndPhone() throws Exception {
        // param
        Long customerId = 1L;
        CustomerSearchDTO requestModel = new CustomerSearchDTO();
        requestModel.setCustId(customerId);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<CustomerSearchDTO> listData = new ArrayList<>();
        CustomerSearchDTO customerSearchDTO = new CustomerSearchDTO();
        customerSearchDTO.setCustId(customerId);
        customerSearchDTO.setAuthName("auth");
        customerSearchDTO.setRepName("rep");
        listData.add(customerSearchDTO);
        resultSelectEntity.setListData(listData);
        // mock
        given(customerRepository.findCustomerByDocumentAndPhone(requestModel)).willReturn(resultSelectEntity);
        // execute
        assertEquals(resultSelectEntity, customerServiceImpl.findCustomerByDocumentAndPhone(requestModel));
    }

    @Test
    public void testSearchTreeInfo() throws  Exception {
        // param
        String plateNumber = "plate";
        String contractNo = "contract";
        Long contractId = 2L;
        Long customerId = 4L;
        SearchInfoDTO dataParams = new SearchInfoDTO();
        dataParams.setPlateNumber(plateNumber);
        dataParams.setActiveStatus("1");
        dataParams.setContractNo(contractNo);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<VehicleDTO> vehicleList = new ArrayList<>();
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setPlateNumber(plateNumber);
        vehicleDTO.setActiveStatus("1");
        vehicleList.add(vehicleDTO);
        resultSelectEntity.setListData(vehicleList);
        ResultSelectEntity resultSelectEntityContract = new ResultSelectEntity();
        List<SearchContractDTO> contractList = new ArrayList<>();
        SearchContractDTO searchContractDTO = new SearchContractDTO();
        searchContractDTO.setContractNo(contractNo);
        searchContractDTO.setContractId(contractId);
        searchContractDTO.setCustId(customerId);
        contractList.add(searchContractDTO);
        resultSelectEntityContract.setListData(contractList);

        // mock
        ReflectionTestUtils.setField(customerServiceImpl,"vehicleRepository",vehicleRepository);
        given(vehicleRepository.findVehicleSearchTree(any())).willReturn(resultSelectEntity);
        given(contractRepository.searchContract(any())).willReturn(resultSelectEntityContract);
        // execute
        assertNotNull(customerServiceImpl.searchTreeInfo(dataParams));
    }

    @Test
    public void testSearchTreeInfoCase2() throws  Exception {
        // param
        String plateNumber = "plate";
        String contractNo = "contract";
        Long contractId = 2L;
        Long customerId = 4L;
        SearchInfoDTO dataParams = new SearchInfoDTO();
        dataParams.setPlateNumber(plateNumber);
        dataParams.setActiveStatus("1");
        dataParams.setContractNo(contractNo);
        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();
        List<VehicleDTO> vehicleList = new ArrayList<>();
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setPlateNumber(plateNumber);
        vehicleDTO.setActiveStatus("1");
        vehicleDTO.setCustId(customerId);
        vehicleList.add(vehicleDTO);
        resultSelectEntity.setListData(vehicleList);
        ResultSelectEntity resultSelectEntityContract = new ResultSelectEntity();
        List<SearchContractDTO> contractList = new ArrayList<>();
        SearchContractDTO searchContractDTO = new SearchContractDTO();
        searchContractDTO.setContractNo(contractNo);
        searchContractDTO.setContractId(contractId);
        searchContractDTO.setCustId(customerId);
        contractList.add(searchContractDTO);
        resultSelectEntityContract.setListData(contractList);
        ResultSelectEntity resultSelectEntityTree = new ResultSelectEntity();
        List<SearchInfoDTO> searchInfoDTOS = new ArrayList<>();
        SearchInfoDTO searchInfoDTO = new SearchInfoDTO();
        searchInfoDTO.setContractNo(contractNo);
        searchInfoDTOS.add(searchInfoDTO);
        resultSelectEntityTree.setListData(searchInfoDTOS);
        List<ContractEntity> contractListTree = new ArrayList<>();
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setCustId(customerId);
        contractListTree.add(contractEntity);
        // mock
        ReflectionTestUtils.setField(customerServiceImpl,"vehicleRepository",vehicleRepository);
        given(vehicleRepository.findVehicleSearchTree(any())).willReturn(resultSelectEntity);
        given(contractRepository.searchContract(any())).willReturn(resultSelectEntityContract);
        given(customerRepository.searchTreeInfo(dataParams)).willReturn(resultSelectEntityTree);
        given(contractServiceJPA.getByCustId(any())).willReturn(contractListTree);
        // execute
        assertNotNull(customerServiceImpl.searchTreeInfo(dataParams));
    }
 }
