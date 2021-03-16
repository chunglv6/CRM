package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.ContractRepository;
import com.viettel.etc.repositories.CustomerRepository;
import com.viettel.etc.repositories.VehicleRepository;
import com.viettel.etc.repositories.tables.ActReasonRepositoryJPA;
import com.viettel.etc.repositories.tables.ServiceFeeRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.ActionAuditDetailServiceJPA;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.net.InetAddress;
import java.util.*;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerServiceJPA customerServiceJPA;

    @Autowired
    ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private SaleTransService saleTransService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    CategoriesService categoriesService;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    private ActionAuditDetailServiceJPA actionAuditDetailServiceJPA;

    @Autowired
    private ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Autowired
    private SaleTransDetailService saleTransDetailService;

    @Override
    public Object addCustomerEnterprise(CustomerDTO customerDTO, Authentication authentication) throws EtcException, Exception {
        checkExistsTaxCode(customerDTO.getTaxCode());
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        String userLogin = FnCommon.getUserLogin(authentication);
        CustomerEntity customerEntity = customerDTO.toRegisterCustomerEnterprise();
        customerEntity.setCreateDate(currDate);
        customerEntity.setCreateUser(userLogin);
        customerEntity.setStatus(CustomerEntity.Status.ACTIVATED.value);
        CustomerEntity save = customerServiceJPA.save(customerEntity);
        updateLogAuditByRegister(authentication, customerDTO, save);

        return CustomerDTO.builder().custId(save.getCustId()).build();
    }

    @Override
    public Object addCustomer(CustomerDTO customerDTO, Authentication authentication) throws EtcException, Exception {
        checkExistsDocumentNumber(customerDTO.getDocumentNumber());
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        String userLogin = FnCommon.getUserLogin(authentication);
        CustomerEntity customer = customerDTO.toAddCustomerEntity();
        customer.setCreateDate(currDate);
        customer.setCreateUser(userLogin);
        customer.setStatus(CustomerEntity.Status.ACTIVATED.value);
        CustomerEntity save = this.customerServiceJPA.save(customer);
        updateLogAuditByRegister(authentication, customerDTO, save);
        return CustomerDTO.builder().custId(save.getCustId()).build();
    }

    @Override
    public CustomerEntity addCustomerQuick(CustomerDTO customerDTO, Authentication authentication) {
        String userLogin = FnCommon.getUserLogin(authentication);
        CustomerEntity customer = customerDTO.toAddCustomerEntity();
        customer.setCreateUser(userLogin);
        customer.setStatus(CustomerEntity.Status.ACTIVATED.value);
        java.sql.Date currDate = new java.sql.Date(new java.util.Date().getTime());
        customer.setCreateDate(currDate);
        customerServiceJPA.save(customer);
        
        customer.setDocumentNumber(String.valueOf(customer.getCustId()));
        customerServiceJPA.save(customer);
        return customer;
    }

    /**
     * update log register customer
     *
     * @param authentication
     * @param customerDTO
     * @param customerEntity
     * @throws Exception
     */
    private void updateLogAuditByRegister(Authentication authentication, CustomerDTO customerDTO, CustomerEntity customerEntity) throws Exception {
        // update action audit
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, getReasonIdByactTypeId(customerDTO.getActTypeId()), customerDTO.getActTypeId(), customerEntity.getCustId(), null, null, ip));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(customerEntity).toEntity(actionAuditEntity.getActionAuditId(), CustomerEntity.class.getAnnotation(Table.class).name(), customerEntity.getCustId());
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
        //update sale trans
        if (customerDTO.getAmount() != null && customerDTO.getAmount() > Constants.AMOUNT_ZERO) {
            saleTransService.updateSaleTrans(customerDTO.getAmount(), customerEntity.getCustId(), authentication);
        }
    }

    private void checkExistsDocumentNumber(String documentNumber) throws EtcException {
        if (customerServiceJPA.existsByDocumentNumber(documentNumber)) {
            throw new EtcException("crm.duplicate.document.number");
        }
    }


    private void checkExistsTaxCode(String taxCode) throws EtcException {
        if (customerServiceJPA.existsByTaxCode(taxCode)) {
            throw new EtcException("crm.duplicate.tax.code");
        }
    }

    /**
     * update customer personal
     *
     * @param customerDTO
     * @param custId
     * @param authentication
     * @return
     */
    @Override
    public void updateCustomer(CustomerDTO customerDTO, Long custId, Authentication authentication) throws Exception {
        Optional<CustomerEntity> entity = customerServiceJPA.findById(custId);
        CustomerEntity customerUpdate = new CustomerEntity();
        if (entity.isPresent()) {
            customerUpdate = entity.get();
            if (!customerUpdate.getDocumentNumber().equals(customerDTO.getDocumentNumber())) {
                checkExistsDocumentNumber(customerDTO.getDocumentNumber());
            }
        }

        CustomerEntity customerEntity = new Gson().fromJson(new Gson().toJson(customerUpdate), CustomerEntity.class);
        customerDTO.updateValueCustomer(customerUpdate);
        customerServiceJPA.save(customerUpdate);
        updateLogAudit(customerDTO, authentication, custId, customerUpdate, customerEntity);
    }

    @Override
    public void updateCustomer(String phone, String email, Authentication authentication) throws Exception {
        String user = FnCommon.getUserLogin(authentication);
        ContractEntity contractEntity = contractServiceJPA.getByAccountUser(user);
        if (contractEntity == null || contractEntity.getCustId() == null) {
            throw new EtcException("crm.contract.not.exist");
        }
        Optional<CustomerEntity> entity = customerServiceJPA.findById(contractEntity.getCustId());
        CustomerEntity customerUpdate = new CustomerEntity();
        if (entity.isPresent()) {
            customerUpdate = entity.get();
            if (phone != null) {
                customerUpdate.setPhoneNumber(phone);
            }
            if (email != null) {
                customerUpdate.setEmail(email);
            }
        }
        customerServiceJPA.save(customerUpdate);
    }

    /**
     * update customer enterprise
     *
     * @param customerDTO
     * @param custId
     * @param authentication
     * @throws Exception
     */
    @Override
    public void updateCustomerEnterprise(CustomerDTO customerDTO, Long custId, Authentication authentication) throws Exception {
        Optional<CustomerEntity> entity = customerServiceJPA.findById(custId);
        CustomerEntity customerUpdate = new CustomerEntity();
        if (entity.isPresent()) {
            customerUpdate = entity.get();
            if (!customerUpdate.getTaxCode().equals(customerDTO.getTaxCode())) {
                checkExistsTaxCode(customerDTO.getTaxCode());
            }
        }
        CustomerEntity customerEntity = new Gson().fromJson(new Gson().toJson(customerUpdate), CustomerEntity.class);
        customerDTO.updateValueCustomerEnterprise(customerUpdate);
        customerServiceJPA.save(customerUpdate);
        updateLogAudit(customerDTO, authentication, custId, customerUpdate, customerEntity);
    }

    /**
     * update log action audit, action audit detail, sale trans
     *
     * @param customerDTO
     * @param authentication
     * @param custId
     * @param customerEntityUpdate
     * @param customerEntity
     * @throws Exception
     */
    private void updateLogAudit(CustomerDTO customerDTO, Authentication authentication, Long custId, CustomerEntity customerEntityUpdate, CustomerEntity customerEntity) throws Exception {
        // update action audit
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, customerDTO.getReasonId(), customerDTO.getActTypeId(), custId, null, null, ip));
        //update action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(customerEntity, customerEntityUpdate).toEntity(actionAuditEntity.getActionAuditId(), CustomerEntity.class.getAnnotation(Table.class).name(), custId);
        actionAuditDetailServiceJPA.save(actionAuditDetailEntity);
        //update sale trans and sale trans detail
        if (customerDTO.getAmount() != null && customerDTO.getAmount() > Constants.AMOUNT_ZERO) {
            addSaleTransAndSaleTransDetails(custId, authentication, customerDTO.getActTypeId());
        }
    }

    /**
     * Tim kiem khach hang theo : DOCUMENT_NUMBER, PHONE_NUMBER
     *
     * @param requestModel params client
     * @return
     * @author Chucnd
     */
    @Override
    public Object findCustomerByDocumentAndPhone(CustomerSearchDTO requestModel) {
        ResultSelectEntity dataResult = customerRepository.findCustomerByDocumentAndPhone(requestModel);
        List<CustomerSearchDTO> listData = (List<CustomerSearchDTO>) dataResult.getListData();
        for (CustomerSearchDTO customerSearchDTO : listData) {
            customerSearchDTO.setSignName(customerSearchDTO.getCustName());
            if (!FnCommon.isNullOrEmpty(customerSearchDTO.getAuthName())) {
                customerSearchDTO.setSignName(customerSearchDTO.getAuthName());
            }
            if (!FnCommon.isNullOrEmpty(customerSearchDTO.getRepName())) {
                customerSearchDTO.setSignName(customerSearchDTO.getRepName());
            }
        }
        return dataResult;
    }

    /**
     * Tra cuu thong tin khach hang
     *
     * @param dataParams params client
     * @return
     */
    @Override
    public Object searchTreeInfo(SearchInfoDTO dataParams) {
        List<Long> custVehicle = null;
        List<Long> custContract;
        List<String> custListString;
        Map<Long, List<VehicleEntity>> vehicleMap = new HashMap<>();
        Map<Long, List<ContractEntity>> contractMap = new HashMap<>();
        if (dataParams.getPlateNumber() != null || dataParams.getRfidSerial() != null || dataParams.getActiveStatus() != null) {
            custVehicle = new LinkedList<>();
            ResultSelectEntity dataVehicle = vehicleRepository.findVehicleSearchTree(VehicleDTO.builder()
                    .plateNumber(dataParams.getPlateNumber()).rfidSerial(dataParams.getRfidSerial()).activeStatus(dataParams.getActiveStatus()).build());
            List<VehicleDTO> vehicleList = (List<VehicleDTO>) dataVehicle.getListData();
            for (VehicleDTO vehicle : vehicleList) {
                List<VehicleEntity> vehicleEntityList = vehicleMap.get(vehicle.getContractId());
                if (vehicleEntityList == null) {
                    vehicleEntityList = new LinkedList<>();
                }
                vehicleEntityList.add(VehicleEntity.builder().vehicleId(vehicle.getVehicleId()).plateNumber(vehicle.getPlateNumber()).build());
                vehicleMap.put(vehicle.getContractId(), vehicleEntityList);
                if (!custVehicle.contains(vehicle.getCustId())) {
                    custVehicle.add(vehicle.getCustId());
                }
            }
        }
        if (dataParams.getContractNo() != null || dataParams.getStartDate() != null || dataParams.getEndDate() != null || dataParams.getPhoneNumber() != null) {
            custContract = new LinkedList<>();
            ResultSelectEntity dataContract = contractRepository.searchContract(SearchContractDTO.builder()
                    .contractNo(dataParams.getContractNo()).noticePhoneNumber(dataParams.getPhoneNumber())
                    .startDate(dataParams.getStartDate()).endDate(dataParams.getEndDate()).build());
            List<SearchContractDTO> contractList = (List<SearchContractDTO>) dataContract.getListData();
            Set<Long> contractIdList = vehicleMap.keySet();
            for (SearchContractDTO contract : contractList) {
                if (custVehicle == null || custVehicle.contains(contract.getCustId())) {
                    if (custVehicle == null || contractIdList.contains(contract.getContractId())) {
                        List<ContractEntity> contractEntityList = contractMap.get(contract.getCustId());
                        if (contractEntityList == null) {
                            contractEntityList = new LinkedList<>();
                        }
                        contractEntityList.add(ContractEntity.builder().contractId(contract.getContractId()).contractNo(contract.getContractNo()).build());
                        contractMap.put(contract.getCustId(), contractEntityList);
                    }
                    if (!custContract.contains(contract.getCustId())) {
                        custContract.add(contract.getCustId());
                    }
                }
            }
        } else {
            custContract = custVehicle;
            for (Long contractId : vehicleMap.keySet()) {
                ContractEntity contract = contractServiceJPA.getOne(contractId);
                List<ContractEntity> contractEntityList = contractMap.get(contract.getCustId());
                if (contractEntityList == null) {
                    contractEntityList = new LinkedList<>();
                }
                contractEntityList.add(ContractEntity.builder().contractId(contract.getContractId()).contractNo(contract.getContractNo()).build());
                contractMap.put(contract.getCustId(), contractEntityList);
            }
        }
        if (custContract != null) {
            if (custContract.isEmpty()) {
                ResultSelectEntity dataResult = new ResultSelectEntity();
                dataResult.setListData(new LinkedList<>());
                dataResult.setCount(0);
                return dataResult;
            }
            custListString = new LinkedList<>();
            for (Long custIdContract : custContract) {
                custListString.add(custIdContract.toString());
            }
            dataParams.setCustomerIdList(String.join(", ", custListString));
        }
        ResultSelectEntity dataResult = customerRepository.searchTreeInfo(dataParams);
        List<SearchInfoDTO> listObject = processJsonObjectCustomer((List<SearchInfoDTO>) dataResult.getListData(), vehicleMap, contractMap);
        dataResult.setListData(listObject);
        return dataResult;
    }

    /**
     * Tim kiem khach hang theo Id
     *
     * @param custId params client
     * @return
     */
    @Override
    public Object findCustomerById(Long custId, String accessToken) {
        return customerRepository.findCustomerById(custId);
    }

    /**
     * Chuyen du lieu doi tuong sang cay json  bao gom : khach hang, hop dong, doanh nghiep
     * Bao gom cac thong tin : Khach hang , hop dong, phuong tien
     *
     * @param listObject
     * @return
     */
    private List<SearchInfoDTO> processJsonObjectCustomer(List<SearchInfoDTO> listObject, Map<Long, List<VehicleEntity>> vehicleMap, Map<Long, List<ContractEntity>> contractMap) {

        for (SearchInfoDTO object : listObject) {
            List<ContractEntity> contractList = contractMap.get(object.getCustId());
            if (contractList == null) {
                contractList = contractServiceJPA.getByCustId(object.getCustId());
            }
            List<SearchInfoDTO.Contract> contractsResult = new LinkedList<>();
            for (ContractEntity contract : contractList) {
                List<SearchInfoDTO.PlateNumber> plateNumberList = new LinkedList<>();
                List<VehicleEntity> vehicleList = vehicleMap.get(contract.getContractId());
                if (vehicleList == null) {
                    vehicleList = vehicleServiceJPA.getByContractId(contract.getContractId());
                }
                for (VehicleEntity vehicle : vehicleList) {
                    plateNumberList.add(new SearchInfoDTO.PlateNumber(vehicle.getVehicleId(), vehicle.getPlateNumber()));
                }
                contractsResult.add(new SearchInfoDTO.Contract(contract.getContractId(), contract.getContractNo(), plateNumberList));
            }
            object.setContracts(contractsResult);
        }
        return listObject;
    }

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
     * Them thong tin giao dich/chi tiet thong tin giao dich
     *
     * @param custId
     * @param actionTypeId
     * @param authentication
     * @return
     */
    private void addSaleTransAndSaleTransDetails(long custId, Authentication authentication, long actionTypeId) {
        addSaleTransAndSaleTransDetails(custId, null, null, authentication, actionTypeId);
    }

    private void addSaleTransAndSaleTransDetails(long custId, Long contractId, String contractNo, Authentication authentication, long actionTypeId) {
        SaleTransEntity saleTranSaved = saleTransService.addSaleTrans(custId, contractId, contractNo, authentication, actionTypeId);
        if (saleTranSaved != null) {
            saleTransDetailService.addSaleTransDetail(authentication, saleTranSaved.getSaleTransId(), null, actionTypeId);
        }
    }

    /**
     * lay thong tin dang ky dich vu cua khach hang, du lieu duoc dua vao tu phan he cong thong tin
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getCustomerRegister(CustRegisterDTO itemParamsEntity) {
        return customerRepository.getCustomerRegister(itemParamsEntity);
    }
}
