package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.RemoveSupOfferRoaming;
import com.viettel.etc.repositories.SaleTransDelBoo1Repository;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.BooException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Autogen class: Lop thao tac mua ve thang quy
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@Service
public class TicketServiceImpl implements TicketService {
    public static final Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Autowired
    SaleTransRepositoryJPA saleTransRepositoryJPA;

    @Autowired
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Autowired
    OCSService ocsService;

    @Autowired
    ContractRepositoryJPA contractRepositoryJPA;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ActionAuditService actionAuditService;

    @Autowired
    ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Autowired
    Boo1Service boo1Service;

    @Autowired
    ServicePlanService servicePlanService;

    @Autowired
    SaleTransDelBoo1RepositoryJPA saleTransDelBoo1RepositoryJPA;

    @Autowired
    SaleTransDelBoo1Repository saleTransDelBoo1Repository;

    @Autowired
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Autowired
    JedisCacheService jedisCacheService;

    @Value("${ws.boo1.charge.ticket}")
    String wsChargeTicket;

    @Value("${ws.dmdc.ticket.type}")
    private String ticketTypeUrl;

    @Value("${ws.dmdc.stage.get}")
    private String stageUrl;

    @Value("${ws.transactions.history}")
    private String transactionsHistoryUrl;

    @Override
    public List<DataTypeDTO.DataType> getTicketTypes(String token) {
        List<DataTypeDTO.DataType> result = new ArrayList<>();
        String strResp = FnCommon.doGetRequest(ticketTypeUrl, null, token);
        DataTypeDTO dataTypeDTO = new Gson().fromJson(strResp, DataTypeDTO.class);
        if (dataTypeDTO != null) {
            result = dataTypeDTO.getData();
        }
        return result;
    }

    /**
     * Them gia tri vao sale trans
     *
     * @param addSupOfferRequest
     * @param authentication
     * @param custId
     * @param contractId
     * @param shopId
     * @param shopName
     * @return
     */
    private SaleTransEntity addSaleTrans(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, long custId, long contractId, long shopId, String shopName) {
        java.sql.Date currDate = new java.sql.Date(System.currentTimeMillis());
        String userLogin = FnCommon.getUserLogin(authentication);
        SaleTransEntity saleTransEntity = addSupOfferRequest.toAddSaleTranEntity(userLogin, currDate, custId, contractId, shopId, shopName);
        saleTransEntity.setStatus(SaleTransEntity.Status.PAID_NOT_INVOICED.value);
        saleTransEntity.setSaleTransType(SaleTransEntity.SaleTransType.MONTHLY_QUARTERLY_TICKET.value);
        saleTransEntity.setAmount(addSupOfferRequest.getAmount());
        return this.saleTransRepositoryJPA.save(saleTransEntity);
    }

    /**
     * Check validate mua ve
     *
     * @param addSupOfferRequest
     * @param contractEntity
     */
    private void validateChargeTicket(AddSupOfferRequestDTO addSupOfferRequest, ContractEntity contractEntity) {
        List<String> listError = new ArrayList<>();
        if (Objects.isNull(contractEntity)) {
            listError.add(jedisCacheService.getMessageErrorByKey("crm.contract.not.exist"));
        }

        if (addSupOfferRequest.getActTypeId() == null) {
            listError.add(jedisCacheService.getMessageErrorByKey("validate.act.type.require"));
        }
        if (listError.size() > 0) {
            throw new EtcException(listError.get(0));
        }
    }

    /**
     * Them gia tri vao sale trans detail
     *
     * @param vehicleAddSuffOfferDTO
     * @param authentication
     * @param saleTransId
     * @param servicePlanDTO
     * @return
     */
    private Object addSaleTransDetail(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Authentication authentication,
                                      long saleTransId, ServicePlanDTO servicePlanDTO,
                                      Long subscriptionTicketId, Long reasonId,
                                      Long actTypeId, Long contractId, Long custId, Long vehicleId, VehicleEntity vehicleEntity) {
        java.sql.Date currDate = new java.sql.Date(System.currentTimeMillis());
        String userLogin = FnCommon.getUserLogin(authentication);
        long price = vehicleAddSuffOfferDTO.getPrice();
        long quantity = vehicleAddSuffOfferDTO.getQuantity();
        if (Objects.nonNull(servicePlanDTO.getScope())) {
            vehicleAddSuffOfferDTO.setScope(servicePlanDTO.getScope().toString());
        }
        if (Objects.nonNull(servicePlanDTO.getOcsCode())) {
            vehicleAddSuffOfferDTO.setOcsCode(servicePlanDTO.getOcsCode());
        }
        SaleTransDetailEntity saleTransDetailEntity = vehicleAddSuffOfferDTO.toAddSaleTransDetailEntity(vehicleAddSuffOfferDTO,
                currDate, userLogin, price, quantity, saleTransId);
        if (vehicleAddSuffOfferDTO.getScope() != null) {
            saleTransDetailEntity.setScope(vehicleAddSuffOfferDTO.getScope());
        }
        if (vehicleAddSuffOfferDTO.getAutoRenew() != null) {
            saleTransDetailEntity.setAutoRenew(vehicleAddSuffOfferDTO.getAutoRenew());
        }
        saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value);

        if (subscriptionTicketId != null) {
            saleTransDetailEntity.setSubscriptionTicketId(subscriptionTicketId);
        }
        if (Constants.BOO1.equals(vehicleAddSuffOfferDTO.getBooCode())) {
            saleTransDetailEntity.setBooCode(Constants.BOO1);
            saleTransDetailEntity.setBooFlow(Constants.BOO2ChargeBOO1);
        } else if (Constants.BOO2.equals(vehicleAddSuffOfferDTO.getBooCode())) {
            saleTransDetailEntity.setBooCode(Constants.BOO2);
            saleTransDetailEntity.setBooFlow(Constants.BOO2ChargeBOO2);
        }
        if (!FnCommon.isNullOrEmpty(vehicleAddSuffOfferDTO.getRfidSerial())) {
            saleTransDetailEntity.setRfidSerial(vehicleAddSuffOfferDTO.getRfidSerial());
        }
        if (!FnCommon.isNullOrEmpty(servicePlanDTO.getOcsCode())) {
            saleTransDetailEntity.setOcsCode(servicePlanDTO.getOcsCode());
        }
        if (vehicleAddSuffOfferDTO.getLaneOut() != null) {
            saleTransDetailEntity.setLane_out(vehicleAddSuffOfferDTO.getLaneOut());
        }
        saleTransDetailEntity.setAct_type_id(actTypeId);
        saleTransDetailEntity.setAct_reason_id(reasonId);
        saleTransDetailEntity.setOrgPlateNumber(vehicleEntity.getPlateNumber().trim() + FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode().trim()));
        SaleTransDetailEntity save = saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
        writeLogNew(authentication, reasonId, actTypeId, contractId, custId,
                vehicleId, save.getSaleTransDetailId(), save, SaleTransDetailEntity.class.getAnnotation(Table.class).name());
        return save;

    }

    /**
     * gen sale trans code
     *
     * @param contractId
     * @param type
     * @param currDate
     * @return
     */
    private String generateSaleTransCode(Long contractId, int type, Date currDate) {
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
     * Set gia tri vao sale trans
     *
     * @param addSupOfferRequest
     * @param contractId
     * @param authentication
     * @param shopId
     * @param shopName
     * @return
     */
    private AddSupOfferRequestDTO setValueToSaveSaleTran(AddSupOfferRequestDTO addSupOfferRequest, long contractId, Authentication authentication, long shopId, String shopName) {
        String saleTransCode = generateSaleTransCode(contractId, 2, new Date());
        addSupOfferRequest.setPaymentMethodId(Long.parseLong(SaleTransEntity.PaymentMethodId.ETC.value));
        addSupOfferRequest.setSaleTransCode(saleTransCode);
        addSupOfferRequest.setSaleTransDate(new Date());
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        if (Objects.nonNull(map) && map.containsKey(Constants.USER_ATTRIBUTE.STAFF_ID)) {
            Long staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
            addSupOfferRequest.setStaffId(staffId);
        }
        addSupOfferRequest.setShopName(shopName);
        addSupOfferRequest.setShopId(shopId);
        return addSupOfferRequest;
    }

    /**
     * Set gia tri service plan DTO
     *
     * @param vehicleAddSuffOfferDTO
     * @return
     */
    private ServicePlanDTO toServicePlanDTO(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO) {
        ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getStageId())) {
            servicePlanDTO.setStageId(vehicleAddSuffOfferDTO.getStageId());
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getStationId())) {
            servicePlanDTO.setStationId(vehicleAddSuffOfferDTO.getStationId());
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getLaneOut())) {
            servicePlanDTO.setLaneOut(vehicleAddSuffOfferDTO.getLaneOut());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getChargeMethodId())) {
            servicePlanDTO.setChargeMethodId(vehicleAddSuffOfferDTO.getChargeMethodId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getServicePlanTypeId())) {
            servicePlanDTO.setServicePlanTypeId(vehicleAddSuffOfferDTO.getServicePlanTypeId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getVehiclesGroupId())) {
            servicePlanDTO.setVehicleGroupId(vehicleAddSuffOfferDTO.getVehiclesGroupId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getAutoRenew())) {
            servicePlanDTO.setAutoRenew(Long.parseLong(vehicleAddSuffOfferDTO.getAutoRenew()));
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getEffDate())) {
            servicePlanDTO.setCreateDateFrom(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getEffDate(), Constants.COMMON_DATE_FORMAT));
            servicePlanDTO.setEffDate(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getEffDate(), Constants.COMMON_DATE_FORMAT));
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getExpDate())) {
            servicePlanDTO.setCreateDateTo(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getExpDate(), Constants.COMMON_DATE_FORMAT));
            servicePlanDTO.setExpDate(FnCommon.convertDateToStringOther(vehicleAddSuffOfferDTO.getExpDate(), Constants.COMMON_DATE_TIME_FORMAT));
        }
        if (Objects.nonNull(vehicleAddSuffOfferDTO.getPlateNumber())) {
            servicePlanDTO.setPlateNumber(vehicleAddSuffOfferDTO.getPlateNumber());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getBooCode())) {
            servicePlanDTO.setBooCode(vehicleAddSuffOfferDTO.getBooCode());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getStationType())) {
            servicePlanDTO.setStationType(vehicleAddSuffOfferDTO.getStationType());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getVehicleTypeId())) {
            servicePlanDTO.setVehicleTypeId(vehicleAddSuffOfferDTO.getVehicleTypeId());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getNetWeight())) {
            servicePlanDTO.setNetWeight(vehicleAddSuffOfferDTO.getNetWeight());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getCargoWeight())) {
            servicePlanDTO.setCargoWeight(vehicleAddSuffOfferDTO.getCargoWeight());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getSeatNumber())) {
            servicePlanDTO.setSeatNumber(vehicleAddSuffOfferDTO.getSeatNumber());
        }

        if (Objects.nonNull(vehicleAddSuffOfferDTO.getEpc())) {
            servicePlanDTO.setEpc(vehicleAddSuffOfferDTO.getEpc());
        }

        return servicePlanDTO;
    }

    /**
     * Lay tong phi mua ve
     *
     * @param list
     * @return
     */
    private long getTotalCharge(List<ServicePlanDTO> list) {
        Long totalCharge = 0l;
        for (ServicePlanDTO servicePlanDTO : list) {
            totalCharge = servicePlanDTO.getFee() + totalCharge;
        }
        return totalCharge;
    }

    /**
     * Set gia tri mua goi cuoc phu
     *
     * @param contractId
     * @param epc
     * @param offerId
     * @param offerLevel
     * @return
     */
    private SupOfferDTOSuccesDTO setValueToSupOffer(Long contractId, String epc, String offerId, String offerLevel) {
        SupOfferDTOSuccesDTO supOfferDTOSucces = new SupOfferDTOSuccesDTO();
        supOfferDTOSucces.setContractId(contractId);
        supOfferDTOSucces.setEpc(epc);
        supOfferDTOSucces.setOfferId(offerId);
        supOfferDTOSucces.setOfferLevel(offerLevel);
        return supOfferDTOSucces;
    }

    /**
     * Set gia tri cua xe mua ve cuoc phu
     *
     * @param vehicleAddSupOffer
     * @param servicePlanDTO
     * @param vehicleEntity
     */
    private void setValueToVehicleAddSupOffer(VehicleAddSuffOfferDTO vehicleAddSupOffer, ServicePlanDTO servicePlanDTO, VehicleEntity vehicleEntity) {
        vehicleAddSupOffer.setOfferLevel("2");
        vehicleAddSupOffer.setOfferId(servicePlanDTO.getOcsCode());
        vehicleAddSupOffer.setEpc(vehicleEntity.getEpc());
        vehicleAddSupOffer.setTid(vehicleEntity.getTid());
        vehicleAddSupOffer.setRfidSerial(vehicleEntity.getRfidSerial());
        vehicleAddSupOffer.setServicePlanId(servicePlanDTO.getServicePlanId());
        if (Objects.nonNull(servicePlanDTO.getServicePlanName())) {
            vehicleAddSupOffer.setServicePlanName(servicePlanDTO.getServicePlanName());
        }
        if (Objects.nonNull(servicePlanDTO.getServicePlanTypeId())) {
            vehicleAddSupOffer.setServicePlanTypeId(servicePlanDTO.getServicePlanTypeId());
        }
        if (Objects.nonNull(servicePlanDTO.getServicePlanId())) {
            vehicleAddSupOffer.setServicePlanId(servicePlanDTO.getServicePlanId());
        }
    }

    /**
     * Validate gia tri dau vao
     *
     * @param vehicleAddSuffOfferDTO
     */
    private List<String> validateInputChargeTicket(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO) {
        List<String> listError = new ArrayList<>();
        if (vehicleAddSuffOfferDTO.getEffDate() == null) {
            listError.add(jedisCacheService.getMessageErrorByKey("validate.briefcase.from.date.not.empty"));
        }
        if (vehicleAddSuffOfferDTO.getPrice() == null) {
            listError.add(jedisCacheService.getMessageErrorByKey("validate.service.plan.fee"));
        }
        if (vehicleAddSuffOfferDTO.getQuantity() == null) {
            listError.add(jedisCacheService.getMessageErrorByKey("common.validate.quantity.not.empty"));
        }
        return listError;
    }

    /**
     * Thay doi OCS
     *
     * @param addSupOfferRequest
     * @param authentication
     * @param contractId
     * @param partyCode
     * @throws Exception
     */
    private void chargeOCS(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, long contractId, String partyCode) throws Exception {
        LOGGER.info("Start call  charge  OCS");
        long start = System.currentTimeMillis();
        OCSResponse ocsResponse = ocsService.charge(addSupOfferRequest, authentication, contractId, partyCode);
        if (!FnCommon.checkOcsCode(ocsResponse)) {
            throw new EtcException("ocs.charge.error");
        }
        long end = System.currentTimeMillis() - start;
        LOGGER.info("End call  charge OCS in : " + end);
    }

    /**
     * Them ban ghi sale trans
     *
     * @param contractEntity
     * @param addSupOfferRequest
     * @param authentication
     * @param contractId
     * @param custId
     * @return
     */
    public SaleTransEntity insertSaleTrans(ContractEntity contractEntity, AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, Long contractId, Long custId) {
        long shopId = contractEntity.getShopId();
        String shopName = contractEntity.getShopName();
        addSupOfferRequest = setValueToSaveSaleTran(addSupOfferRequest, contractId, authentication, shopId, shopName);
        SaleTransEntity saleTransEntity = addSaleTrans(addSupOfferRequest, authentication, custId, contractId, shopId, shopName);
        return saleTransEntity;
    }

    /**
     * @param authentication
     * @param vehicleAddSuffOfferDTO
     * @param actTypeId
     * @param contractId
     * @param userLogin
     * @param partyCode
     * @return
     * @throws Exception
     */
    private OCSResponse addSupOfferOCS(Authentication authentication, VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Long actTypeId, Long contractId, String userLogin, String partyCode) throws Exception {
        LOGGER.info("Start call addSupOffer OCS");
        long start1 = System.currentTimeMillis();
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        Long staffId = 0L;
        if (Objects.nonNull(map) && map.containsKey(Constants.USER_ATTRIBUTE.STAFF_ID)) {
            staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
        }

        OCSResponse ocsResponse = ocsService.addSupOffer(vehicleAddSuffOfferDTO, authentication, actTypeId, contractId, staffId, userLogin, partyCode);
        long end1 = System.currentTimeMillis() - start1;
        LOGGER.info("End call addSupOffer OCS in : " + end1);
        return ocsResponse;
    }

    /**
     * Huy ve cua he thong BOO2
     *
     * @param authentication
     * @param saleTransDelBoo1Entity
     * @param saleTransDelBoo1DTO
     * @param saleTransDetailEntity
     * @return
     * @throws Exception
     */
    private Object destroyTicketBoo2(Authentication authentication, SaleTransDelBoo1Entity saleTransDelBoo1Entity, SaleTransDelBoo1DTO saleTransDelBoo1DTO, SaleTransDetailEntity saleTransDetailEntity) throws Exception {
        Optional<VehicleEntity> optionalVehicleEntity = vehicleRepositoryJPA.findById(saleTransDetailEntity.getVehicleId());
        if (!optionalVehicleEntity.isPresent()) {
            throw new EtcException("crm.vehicle.not.found");
        }
        Long reasonId = getReasonIdByactTypeId(saleTransDelBoo1DTO.getActTypeId());
        if (SaleTransDelBoo1Entity.Status.SUCCESS.value.equals(saleTransDelBoo1DTO.getBotStatus())) {
            OCSResponse ocsResponse = ocsService.deleteSupOffer(authentication, saleTransDelBoo1DTO.getActTypeId(), saleTransDelBoo1Entity.getContractId(), saleTransDelBoo1Entity.getEpc(), saleTransDelBoo1Entity.getOcsCode(), saleTransDelBoo1Entity.getOfferLevel());
            if (ocsResponse != null && "0".equals(ocsResponse.getResultCode())) {
                ocsService.addBalance(authentication, saleTransDelBoo1DTO.getActTypeId(), saleTransDelBoo1Entity.getContractId(), saleTransDelBoo1Entity.getPrice());
            } else {
                throw new EtcException("ocs.delete.sup.offer.fail");
            }
        }
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        SaleTransDetailEntity saleTransDetailEntityOld = gson.fromJson(gson.toJson(saleTransDetailEntity), SaleTransDetailEntity.class);
        SaleTransDelBoo1Entity saleTransDelBoo1EntityOld = gson.fromJson(gson.toJson(saleTransDelBoo1Entity), SaleTransDelBoo1Entity.class);
        updateSaleTransDetails(saleTransDetailEntity, authentication);
        updateSaleTransDel(saleTransDelBoo1Entity, authentication, saleTransDelBoo1DTO);
        updateLogRefund(saleTransDelBoo1DTO.getActTypeId(), reasonId, authentication, saleTransDelBoo1Entity.getContractId(), saleTransDelBoo1Entity.getCustId(), saleTransDetailEntity, saleTransDetailEntityOld, saleTransDelBoo1Entity, saleTransDelBoo1EntityOld);
        return "SUCCESS";

    }

    /**
     * Update thong tin huy ve thang quy
     *
     * @param saleTransDetailEntity
     * @param authentication
     */
    private void updateSaleTransDetails(SaleTransDetailEntity saleTransDetailEntity, Authentication authentication) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.CANCEL.value);
        saleTransDetailEntity.setDestroy_date(date);
        saleTransDetailEntity.setDestroy_user(FnCommon.getUserLogin(authentication));
        saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
    }

    /**
     * Update log action audit
     *
     * @param destroyTicketDTO
     * @param authentication
     * @param contractId
     * @param saleTransDetailEntity
     * @param saleTransDetailEntityOld
     */
    private void updateLog(DestroyTicketDTO destroyTicketDTO, Authentication authentication, Long contractId, SaleTransDetailEntity saleTransDetailEntity, SaleTransDetailEntity saleTransDetailEntityOld) throws UnknownHostException, JSONException, IllegalAccessException {
        // insert action audit
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, destroyTicketDTO.getReasonId(), destroyTicketDTO.getActTypeId(), destroyTicketDTO.getCustId(), contractId, null, ip));
        //insert action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(saleTransDetailEntityOld, saleTransDetailEntity).toEntity(actionAuditEntity.getActionAuditId(), SaleTransDetailEntity.class.getAnnotation(Table.class).name(), null);
        actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);
    }

    /**
     * Set gia tri cho DTO ReqCancelTicket
     *
     * @param destroyTicketDTO
     * @return
     */
    private ReqCancelTicketDTO setValueToReqCancelTicket(DestroyTicketDTO destroyTicketDTO, Long requestType) {
        ReqCancelTicketDTO request = new ReqCancelTicketDTO();
        request.setSubscription_ticket_id(destroyTicketDTO.getSubscriptionTicketId());
        request.setStation_type(destroyTicketDTO.getStationType());
        request.setTicket_type(destroyTicketDTO.getTicketType());
        request.setStation_in_id(destroyTicketDTO.getStationInId());
        request.setStation_out_id(destroyTicketDTO.getStationOutId());
        request.setStart_date(destroyTicketDTO.getStartDate());
        request.setEnd_date(destroyTicketDTO.getEndDate());
        request.setPlate(destroyTicketDTO.getPlateNumber());
        request.setEtag(destroyTicketDTO.getEpc());
        request.setRequest_id(System.currentTimeMillis());
        request.setRequest_datetime(System.currentTimeMillis());
        request.setRequest_type(requestType);
        return request;
    }


    /**
     * Mua ve moi
     *
     * @param addSupOfferRequest
     * @param authentication
     * @param custId
     * @param contractId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseChargeTicketCRM chargeTicketNew(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, long custId, long contractId, long contractIdBuyTicket) throws Exception {
        ContractEntity contractEntity = contractRepositoryJPA.findByContractId(contractId);

        // Check quyen
        ContractEntity contractBuyTicketEntity = contractRepositoryJPA.findByContractId(contractIdBuyTicket);
        if (contractBuyTicketEntity != null) {
            String contractNoBuyTicket = contractBuyTicketEntity.getContractNo();
            Set<String> userRole = FnCommon.getRoleIdCrm(authentication);
            boolean haveRoleBuyTicket = false;
            if (!FnCommon.isNullOrEmpty(userRole)) { // kiem tra quyen mua vé nếu mua vé qua crm, app dai ly
                haveRoleBuyTicket = userRole.contains(Constants.ROLE_CRM.ROLE_ADMIN_CRM) || userRole.contains(Constants.ROLE_CRM.ROLE_QUANLY_TRACUU_DAUNOI);
            }
            String userLogin = FnCommon.getUserLogin(authentication);
            // check có quyền mua vé hay không
            if (userLogin != null && !userLogin.equals(contractNoBuyTicket) && !haveRoleBuyTicket) {
                throw new EtcException("crm.unauthor.buy.ticket");
            }
        } else {
            throw new EtcException("crm.contract.not.exist");
        }

        validateChargeTicket(addSupOfferRequest, contractEntity);
        List<ServicePlanDTO> listTicket = new ArrayList<>();
        ResponseChargeTicketCRM responseChargeTicketCRM = new ResponseChargeTicketCRM();
        for (VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO : addSupOfferRequest.getList()) {
            ServicePlanDTO ticket = toServicePlanDTO(vehicleAddSuffOfferDTO);
            ticket.setVehicleId(vehicleAddSuffOfferDTO.getVehicleId());
            listTicket.add(ticket);
        }
        RequestGetFeeChargeTicketDTO requestGetFeeChargeTicket = new RequestGetFeeChargeTicketDTO();
        requestGetFeeChargeTicket.setServicePlanDTOList(listTicket);
        ServicePlanFeeDTO infoTickets = servicePlanService.getFeeNew(authentication, requestGetFeeChargeTicket);
        if (!FnCommon.isNullOrEmpty(infoTickets.getServicePlanVehicleDuplicate())) {
            Map<String, String> parameter = new HashMap<>();
            parameter.put("PARAMETER", infoTickets.getServicePlanVehicleDuplicate());
            throw new EtcException("crm.charge.ticket.exist", parameter);
        }
        if (addSupOfferRequest.getList().size() != infoTickets.getListServicePlan().size()) {
            throw new EtcException("crm.vehicle.can.not.charge.ticket");
        }
        if (addSupOfferRequest.isAcountETC()) {
            chargeETCNew(addSupOfferRequest, authentication, contractId, contractEntity, custId, FnCommon.getUserLogin(authentication), infoTickets, responseChargeTicketCRM, contractIdBuyTicket);
        }
        return responseChargeTicketCRM;
    }


    @Override
    public ResponseChargeTicketCRM chargeTicketNewOutContractId(ListAddSupOfferRequestDTO listAddSupOfferRequest, Authentication authentication, long custId, long contractId) throws Exception {
        ResponseChargeTicketCRM responseChargeTicketCRM = new ResponseChargeTicketCRM();

        List<ServicePlanDTO> listSuccess = new ArrayList<>();
        List<ServicePlanDTO> listFail = new ArrayList<>();

        for (AddSupOfferRequestDTO addSupOfferRequest : listAddSupOfferRequest.getListContract()) {
            Long contractIdTicket = addSupOfferRequest.getContractId();
            ContractEntity contractEntity = contractRepositoryJPA.findByContractId(contractIdTicket);
            addSupOfferRequest.setContractNo((contractEntity.getContractNo()));
            ResponseChargeTicketCRM response = chargeTicketNew(addSupOfferRequest, authentication, custId, addSupOfferRequest.getContractId(), contractId);
            if (response.getListSuccess() != null)
                listSuccess.addAll(response.getListSuccess());
            if (response.getListFail() != null)
                listFail.addAll(response.getListFail());
        }

        responseChargeTicketCRM.setListSuccess(listSuccess);
        responseChargeTicketCRM.setListFail(listFail);
        return responseChargeTicketCRM;
    }

    /**
     * Tru tien tai khoan ETC
     *
     * @param addSupOfferRequest
     * @param authentication
     * @param contractId
     * @param contractEntity
     * @param custId
     * @param userLogin
     * @throws Exception
     */
    private void chargeETCNew(AddSupOfferRequestDTO addSupOfferRequest, Authentication authentication, long contractId,
                              ContractEntity contractEntity, long custId, String userLogin, ServicePlanFeeDTO servicePlanFee, ResponseChargeTicketCRM responseChargeTicketCRM, long contractIdBuyTicket) throws Exception {
        Long actTypeId = addSupOfferRequest.getActTypeId();
        Long reasonId = getReasonIdByactTypeId(actTypeId);
        // Luu log vao bang sale_trans
        SaleTransEntity saleTransEntity = insertSaleTrans(contractEntity, addSupOfferRequest, authentication, contractId, custId);
        List<ServicePlanDTO> listSuccess = new ArrayList<>();
        List<ServicePlanDTO> listFail = new ArrayList<>();
        Long totalCharge = getTotalCharge(servicePlanFee.getListServicePlan());
        addSupOfferRequest.setAmount(totalCharge);
        // Goi ham chagre tien OCS
        chargeOCS(addSupOfferRequest, authentication, contractIdBuyTicket, saleTransEntity.getSaleTransId().toString());
        for (int i = 0; i < addSupOfferRequest.getList().size(); i++) {
            // validate start end
            VehicleAddSuffOfferDTO vehicleToChargeTicket = addSupOfferRequest.getList().get(i);
            ServicePlanDTO ticketInfo = servicePlanFee.getListServicePlan().get(i);
            ticketInfo.setEffDate(FnCommon.convertDateToStringOther(vehicleToChargeTicket.getEffDate(), Constants.COMMON_DATE_TIME_FORMAT));
            ticketInfo.setExpDate(FnCommon.convertDateToStringOther(vehicleToChargeTicket.getExpDate(), Constants.COMMON_DATE_TIME_FORMAT));
            if (vehicleToChargeTicket.getStationId() != null) {
                ticketInfo.setStationId(vehicleToChargeTicket.getStationId());
            }
            if (vehicleToChargeTicket.getStageId() != null) {
                ticketInfo.setStageId(vehicleToChargeTicket.getStageId());
            }
            if (!FnCommon.isNullOrEmpty(vehicleToChargeTicket.getStationOrStageName())) {
                ticketInfo.setStationOrStageName(vehicleToChargeTicket.getStationOrStageName());
            }
            if (vehicleToChargeTicket.getChargeMethodId() == 1L) {
                vehicleToChargeTicket.getEffDate().setDate(1);
                FnCommon.setTimeOfDate(FnCommon.convertDateToString(vehicleToChargeTicket.getExpDate()), 23, 59, 59);
            }
            if (!validateBeforeCharge(vehicleToChargeTicket)) {
                ticketInfo.setReasons(jedisCacheService.getMessageErrorByKey("validate.charge.ticket.error.start.end.date"));
                listFail.add(ticketInfo);
                continue;
            }
            // Thuc hien dang ki goi cuoc phu cho tung phuong tien
            VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(vehicleToChargeTicket.getVehicleId());
            addOffer(vehicleToChargeTicket, ticketInfo, authentication, addSupOfferRequest,
                    contractId, userLogin, saleTransEntity, listSuccess, listFail, actTypeId, reasonId, custId, vehicleToChargeTicket.getVehicleId(), vehicleEntity);
        }
        boolean isSaleTrans = updateTicketFail(listFail, saleTransEntity, authentication, addSupOfferRequest.getActTypeId(), contractId);
        if (isSaleTrans) {
            writeLogNew(authentication, reasonId, actTypeId, contractId, custId, null,
                    saleTransEntity.getSaleTransId(), saleTransEntity, SaleTransEntity.class.getAnnotation(Table.class).name());
        }
        responseChargeTicketCRM.setListSuccess(listSuccess);
        responseChargeTicketCRM.setListFail(listFail);
    }

    /**
     * Mua ve tram doan BOO1
     *
     * @param authentication
     * @param ticketInfo
     * @param listSuccess
     * @param listFail
     * @param vehicleToChargeTicket
     * @param userLogin
     * @param saleTransEntity
     * @throws Exception
     */
    public void chargeTicketBoo1(Authentication authentication, ServicePlanDTO ticketInfo,
                                 List<ServicePlanDTO> listSuccess, List<ServicePlanDTO> listFail,
                                 VehicleAddSuffOfferDTO vehicleToChargeTicket, String userLogin,
                                 SaleTransEntity saleTransEntity, Long actTypeId, Long reasonId, Long contractId, Long custId, Long vehicleId, VehicleEntity vehicleEntity) throws Exception {
        ReqChargeTicketDTO reqChargeTicketDTO = setValueToReqChargeTicket(authentication, ticketInfo);
        ResChargeTicketDTO resChargeTicketDTO = boo1Service.chargeTicketBoo1(reqChargeTicketDTO, authentication, actTypeId, reqChargeTicketDTO.getRequest_id());
        LOGGER.info("Ket qua khi goi mua ve BOO1={}", resChargeTicketDTO.toString());
        if (resChargeTicketDTO.getSubscription_ticket_id() == null) {
            ticketInfo.setReasons(jedisCacheService.getMessageErrorByKey("boo1.charge.ticket.error"));
            listFail.add(ticketInfo);
        } else {
            listSuccess.add(ticketInfo);
            addSaleTransDetail(vehicleToChargeTicket, authentication,
                    saleTransEntity.getSaleTransId(), ticketInfo,
                    resChargeTicketDTO.getSubscription_ticket_id(), reasonId, actTypeId, contractId, custId, vehicleId, vehicleEntity);
            Map<String, Object> map = FnCommon.getAttribute(authentication);
            Long staffId = 0L;
            if (Objects.nonNull(map) && map.containsKey(Constants.USER_ATTRIBUTE.STAFF_ID)) {
                staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
            }
            LOGGER.info("Start call addSupOfferBoo1 OCS");
            AddSupOfferRoamingDTO addSupOfferRoamingDTO = setValueAddSupOfferRoamingDTO(vehicleToChargeTicket,
                    saleTransEntity.getSaleTransId(), staffId.toString(), userLogin, reqChargeTicketDTO, resChargeTicketDTO, actTypeId);
            long start1 = System.currentTimeMillis();
            ocsService.addSupOfferRoamingBoo1(addSupOfferRoamingDTO, authentication);
            long end1 = System.currentTimeMillis() - start1;
            LOGGER.info("End call addSupOffer OCS in : " + end1);
        }
    }

    /**
     * Mua ve Boo2
     *
     * @param vehicleToChargeTicket
     * @param ticketInfo
     * @param authentication
     * @param addSupOfferRequest
     * @param contractId
     * @param userLogin
     * @param saleTransEntity
     * @throws Exception
     */
    public void chargeTicketBoo2(VehicleAddSuffOfferDTO vehicleToChargeTicket,
                                 ServicePlanDTO ticketInfo, Authentication authentication,
                                 AddSupOfferRequestDTO addSupOfferRequest, Long contractId,
                                 String userLogin, SaleTransEntity saleTransEntity,
                                 List<ServicePlanDTO> listSuccess, List<ServicePlanDTO> listFail,
                                 Long actTypeId, Long reasonId, Long custId, Long vehicleId, VehicleEntity vehicleEntity) throws Exception {
        if (!VehicleEntity.Status.ACTIVATED.value.equals(vehicleEntity.getStatus()) ||
                VehicleEntity.ActiveStatus.TRANSFERRED.value.equals(vehicleEntity.getActiveStatus())) {
            listFail.add(ticketInfo);
//            throw new EtcException("crm.vehicle.can.not.charge.ticket");
        } else {
            List<String> listErrValid = validateInputChargeTicket(vehicleToChargeTicket);
            if (listErrValid.size() > 0) {
                listFail.add(ticketInfo);
            } else {
                setValueToVehicleAddSupOffer(vehicleToChargeTicket, ticketInfo, vehicleEntity);
                OCSResponse ocsResponse = addSupOfferOCS(authentication, vehicleToChargeTicket, addSupOfferRequest.getActTypeId(), contractId, userLogin, saleTransEntity.getSaleTransId().toString());
                if (!FnCommon.checkOcsCode(ocsResponse)) {
                    LOGGER.error("Loi khi goi OCS", ocsResponse);
                    ticketInfo.setReasons(jedisCacheService.getMessageErrorByKey("crm.exception-list.error.add.sub-offer.ocs"));
                    listFail.add(ticketInfo);
                } else {
                    addSaleTransDetail(vehicleToChargeTicket, authentication, saleTransEntity.getSaleTransId(),
                            ticketInfo, Long.parseLong(ocsResponse.getSubscriptionTicketId()), reasonId, actTypeId, contractId, custId, vehicleId, vehicleEntity);
                    listSuccess.add(ticketInfo);
                }
            }
        }
    }

//    /**
//     * THuc hien sau khi goi dang ki goi cuoc phu that bai
//     *
//     * @param listChargeSuccess
//     * @param authentication
//     * @param addSupOfferRequest
//     * @param contractId
//     * @param plateNumber
//     */
//    private void callAddSubOfferOcsFail(List<SupOfferDTOSucces> listChargeSuccess,
//                                        Authentication authentication, AddSupOfferRequest addSupOfferRequest, Long contractId, String plateNumber) {
//        for (SupOfferDTOSucces supOfferDTOSucces : listChargeSuccess) {
//            ocsService.deleteSupOffer(authentication, addSupOfferRequest.getActTypeId(),
//                    supOfferDTOSucces.getContractId(), supOfferDTOSucces.getEpc(), supOfferDTOSucces.getOfferId(), supOfferDTOSucces.getOfferLevel());
//        }
//        // them lai so du
//        ocsService.addBalance(authentication, addSupOfferRequest.getActTypeId(), contractId, addSupOfferRequest.getAmount());
//        Map<String, String> parameter = new HashMap<>();
//        parameter.put("PARAMETER", String.valueOf(plateNumber));
//        throw new EtcException("ocs.add.sub.offer.error", parameter);
//    }

    /**
     * Set gia tri de gui request sang BOO1
     *
     * @param authentication
     * @param servicePlanDTO
     * @return
     */
    private ReqChargeTicketDTO setValueToReqChargeTicket(Authentication authentication, ServicePlanDTO servicePlanDTO) {
        ReqChargeTicketDTO reqChargeTicketDTO = new ReqChargeTicketDTO();
        reqChargeTicketDTO.setStation_type(FnCommon.convertStationType(servicePlanDTO.getStationType()));
        if (servicePlanDTO.getStageId() == null) {
            reqChargeTicketDTO.setStation_in_id(servicePlanDTO.getStationId());
        } else {
            StageBooDTO stageDTO = this.getStageById(FnCommon.getStringToken(authentication), servicePlanDTO.getStageId().toString());
            if (stageDTO.getData() != null) {
                reqChargeTicketDTO.setStation_in_id(stageDTO.getData().getStation_input_id());
                reqChargeTicketDTO.setStation_out_id(stageDTO.getData().getStation_output_id());
            }
        }
        reqChargeTicketDTO.setTicket_type(FnCommon.convertToTicketType(String.valueOf(servicePlanDTO.getServicePlanTypeId())));
        try {
            reqChargeTicketDTO.setStart_date(FnCommon.formatStringBOO2ToBOO1(servicePlanDTO.getEffDate()));
            reqChargeTicketDTO.setEnd_date(FnCommon.formatStringBOO2ToBOO1(servicePlanDTO.getExpDate()));
        } catch (ParseException e) {
            LOGGER.error("Exception format date validateExistTicket:", e);
        }
        reqChargeTicketDTO.setPlate(servicePlanDTO.getPlateNumber());
        reqChargeTicketDTO.setEtag(servicePlanDTO.getEpc());
        reqChargeTicketDTO.setVehicle_type(servicePlanDTO.getVehicleGroupId());
        reqChargeTicketDTO.setRegister_vehicle_type(String.valueOf(servicePlanDTO.getVehicleTypeId()));
        reqChargeTicketDTO.setSeat(servicePlanDTO.getSeatNumber());
        reqChargeTicketDTO.setWeight_goods(servicePlanDTO.getCargoWeight());
        reqChargeTicketDTO.setWeight_all(servicePlanDTO.getNetWeight());
        reqChargeTicketDTO.setRequest_datetime(System.currentTimeMillis());
        reqChargeTicketDTO.setRequest_id(generateRequestId());
        return reqChargeTicketDTO;
    }

    /***
     * lay thong tin cua doan
     * @param token
     * @return
     */
    private StageBooDTO getStageById(String token, String stageId) {
        String requestUrl = stageUrl.replace("{stageId}", stageId);
        String stageResponse = FnCommon.doGetRequest(requestUrl, null, token);
        StageBooDTO stage = new Gson().fromJson(stageResponse, StageBooDTO.class);
        return stage;
    }

    /**
     * Tao requestId
     *
     * @return
     */
    private Long generateRequestId() {
        Long requestId = System.currentTimeMillis();
        return Long.parseLong(requestId.toString().concat(getRandomValue().toString()));
    }

    /**
     * Tao 1 so nguyen bat ki trong khoang 0 den 9999
     *
     * @return
     */
    private Integer getRandomValue() {
        Random rand = new Random();
        return rand.nextInt(10000);
    }

    /**
     * Search ticket del boo1
     *
     * @param contractId
     * @param saleTransDelBoo1DTO
     * @return
     * @throws Exception
     */
    @Override
    public Object searchTicketDelBoo1(Long contractId, SaleTransDelBoo1DTO saleTransDelBoo1DTO) throws Exception {
        ResultSelectEntity dataResult = saleTransDelBoo1Repository.searchTicketDel(contractId, saleTransDelBoo1DTO);
        return dataResult;
    }

    private AddSupOfferRoamingDTO setValueAddSupOfferRoamingDTO(VehicleAddSuffOfferDTO vehicleToChargeTicket,
                                                                Long saleTransId,
                                                                String staffId, String staffName, ReqChargeTicketDTO reqChargeTicketDTO,
                                                                ResChargeTicketDTO resChargeTicketDTO, Long actTypeId) {
        AddSupOfferRoamingDTO addSupOfferRoamingDTO = new AddSupOfferRoamingDTO();
        addSupOfferRoamingDTO.setEPC(vehicleToChargeTicket.getEpc());
        addSupOfferRoamingDTO.setEff_Date(FnCommon.convertDateToString(vehicleToChargeTicket.getEffDate()));
        addSupOfferRoamingDTO.setExp_Date(FnCommon.convertDateToString(vehicleToChargeTicket.getExpDate()));
        addSupOfferRoamingDTO.setPartyCode(saleTransId.toString());
        addSupOfferRoamingDTO.setStaffId(staffId);
        addSupOfferRoamingDTO.setStaffName(staffName);
        addSupOfferRoamingDTO.setSubscriptionTicketId(resChargeTicketDTO.getSubscription_ticket_id().toString());
        addSupOfferRoamingDTO.setRequestId(reqChargeTicketDTO.getRequest_id().toString());
        addSupOfferRoamingDTO.setRequestDateTime(reqChargeTicketDTO.getRequest_datetime().toString());
        addSupOfferRoamingDTO.setResponseId(resChargeTicketDTO.getRequest_id().toString());
        addSupOfferRoamingDTO.setResponseDateTime(resChargeTicketDTO.getResponse_datetime().toString());
        addSupOfferRoamingDTO.setTicketType(FnCommon.convertTicketType(reqChargeTicketDTO.getTicket_type()));
        addSupOfferRoamingDTO.setCharge(vehicleToChargeTicket.getPrice().toString());
        addSupOfferRoamingDTO.setStationIn(reqChargeTicketDTO.getStation_in_id().toString());
        if (reqChargeTicketDTO.getStation_out_id() != null) {
            addSupOfferRoamingDTO.setStationOut(reqChargeTicketDTO.getStation_out_id().toString());
        }
        addSupOfferRoamingDTO.setActTypeId(actTypeId);
        return addSupOfferRoamingDTO;
    }


    /**
     * confirm destroy ticket
     *
     * @param saleTransDetailId
     * @param saleTransDelBoo1DTO
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    public Object confirmDestroyTicket(Long saleTransDetailId, SaleTransDelBoo1DTO saleTransDelBoo1DTO, Authentication authentication) throws Exception {
        String userLogin = FnCommon.getUserLogin(authentication);
        Optional<SaleTransDetailEntity> saleTransDetailEntityOptional = saleTransDetailRepositoryJPA.findById(saleTransDetailId);
        if (!saleTransDetailEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailEntityOptional.get();
        Optional<SaleTransEntity> saleTransEntityOptional = saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId());
        if (!saleTransEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");

        }
        SaleTransEntity saleTransEntity = saleTransEntityOptional.get();
        if (Constants.BOO2.equals(saleTransDetailEntity.getBooCode())) {
            saveValueToSaleTransDelBoo(saleTransDetailEntity, saleTransEntity, saleTransDelBoo1DTO, userLogin);
        }
        if (Constants.BOO1.equals(saleTransDetailEntity.getBooCode())) {
            destroyTicketBoo1(saleTransDetailEntity, saleTransEntity, saleTransDelBoo1DTO, 1L, userLogin);
        }
        return "Success";
    }

    /**
     * Them ban ghi vao bang sale trans del boo1
     *
     * @param saleTransDetailEntity
     * @param saleTransEntity
     * @param saleTransDelBoo1DTO
     * @param userLogin
     */
    private void saveValueToSaleTransDelBoo(SaleTransDetailEntity saleTransDetailEntity, SaleTransEntity saleTransEntity, SaleTransDelBoo1DTO saleTransDelBoo1DTO, String userLogin) {
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
        saleTransDelBoo1Entity.setContractId(saleTransEntity.getContractId());
        saleTransDelBoo1Entity.setPlateNumber(saleTransDetailEntity.getPlateNumber());
        if (saleTransDelBoo1DTO.getStationInId() != null) {
            saleTransDelBoo1Entity.setStationInId(saleTransDelBoo1DTO.getStationInId());
        }
        if (saleTransDelBoo1DTO.getStationOutId() != null) {
            saleTransDelBoo1Entity.setStationOutId(saleTransDelBoo1DTO.getStationOutId());
        }
        saleTransDelBoo1Entity.setServicePlanTypeId(saleTransDetailEntity.getServicePlanTypeId());
        saleTransDelBoo1Entity.setEpc(saleTransDetailEntity.getEpc());
        if (saleTransDelBoo1DTO.getStationType() != null) {
            saleTransDelBoo1Entity.setStationType(saleTransDelBoo1DTO.getStationType());
        }
        if (saleTransDetailEntity.getStageId() != null) {
            saleTransDelBoo1Entity.setStageId(saleTransDetailEntity.getStageId());
        }
        if (saleTransDetailEntity.getStationId() != null) {
            saleTransDelBoo1Entity.setStationId(saleTransDetailEntity.getStationId());
        }
        saleTransDelBoo1Entity.setEffDate(saleTransDetailEntity.getEffDate());
        saleTransDelBoo1Entity.setExpDate(saleTransDetailEntity.getExpDate());
        saleTransDelBoo1Entity.setPrice(saleTransDetailEntity.getPrice());
        saleTransDelBoo1Entity.setRequestType(SaleTransDelBoo1Entity.RequestType.REFUND_MONEY.value);
        saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.RECEIVED.value);
        saleTransDelBoo1Entity.setBotStatus(SaleTransDelBoo1Entity.Status.RECEIVED.value);
        saleTransDelBoo1Entity.setCreateUser(userLogin);
        saleTransDelBoo1Entity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransDelBoo1Entity.setCustId(saleTransEntity.getCustId());
        saleTransDelBoo1Entity.setContractNo(saleTransEntity.getContractNo());
        saleTransDelBoo1Entity.setBooCode(saleTransDetailEntity.getBooCode());
        saleTransDelBoo1Entity.setOcsCode(saleTransDetailEntity.getOcsCode());
        saleTransDelBoo1Entity.setOfferLevel(saleTransDetailEntity.getOfferLevel());
        saleTransDelBoo1Entity.setSubscriptionTicketId(saleTransDetailEntity.getSubscriptionTicketId());
        saleTransDelBoo1Entity.setRequestId(System.currentTimeMillis());
        saleTransDelBoo1Entity.setRequestDatetime(System.currentTimeMillis());
        saleTransDelBoo1Entity.setMethodChargeId(saleTransDelBoo1DTO.getMethodChargeId());
        saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
    }

    /**
     * Huy ve co hoan tien
     *
     * @param subscriptionTicketId
     * @param saleTransDelBoo1DTO
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object destroyTicketRefund(Long subscriptionTicketId, SaleTransDelBoo1DTO saleTransDelBoo1DTO, Authentication authentication) throws Exception {
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId);
        if (saleTransDelBoo1Entity == null) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailRepositoryJPA.findBySubscriptionTicketId(subscriptionTicketId);
        if (saleTransDetailEntity == null) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        return destroyTicketCRM(saleTransDetailEntity, authentication, saleTransDelBoo1Entity, saleTransDelBoo1DTO);
    }

    /**
     * BOT confirm huy ve
     *
     * @param saleTransDetailEntity
     * @param authentication
     * @param saleTransDelBoo1Entity
     * @param saleTransDelBoo1DTO
     * @return
     * @throws Exception
     */
    private Object destroyTicketCRM(SaleTransDetailEntity saleTransDetailEntity, Authentication authentication, SaleTransDelBoo1Entity saleTransDelBoo1Entity, SaleTransDelBoo1DTO saleTransDelBoo1DTO) throws Exception {
        if (Constants.BOO2ChargeBOO2.equals(saleTransDetailEntity.getBooFlow())) {
            return destroyTicketBoo2(authentication, saleTransDelBoo1Entity, saleTransDelBoo1DTO, saleTransDetailEntity);
        }
        if (Constants.BOO1ChargeBOO2.equals(saleTransDetailEntity.getBooFlow()) || Constants.BOO2ChargeBOO1.equals(saleTransDetailEntity.getBooFlow())) {
            return confirmResultBoo1(authentication, saleTransDelBoo1Entity, saleTransDelBoo1DTO, saleTransDetailEntity);
        }
        throw new EtcException("boo.cancel.result.fail");
    }

    /**
     * Tra ket qua huy ve cho BOO1
     *
     * @param authentication
     * @param saleTransDelBoo1Entity
     * @param saleTransDelBoo1DTO
     * @param saleTransDetailEntity
     * @return
     */
    private Object confirmResultBoo1(Authentication authentication, SaleTransDelBoo1Entity saleTransDelBoo1Entity, SaleTransDelBoo1DTO saleTransDelBoo1DTO, SaleTransDetailEntity saleTransDetailEntity) throws Exception {
        Long reasonId = getReasonIdByactTypeId(saleTransDelBoo1DTO.getActTypeId());
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        SaleTransDetailEntity saleTransDetailEntityOld = gson.fromJson(gson.toJson(saleTransDetailEntity), SaleTransDetailEntity.class);
        SaleTransDelBoo1Entity saleTransDelBoo1EntityOld = gson.fromJson(gson.toJson(saleTransDelBoo1Entity), SaleTransDelBoo1Entity.class);
        Optional<VehicleEntity> optionalVehicleEntity = vehicleRepositoryJPA.findById(saleTransDetailEntity.getVehicleId());
        if (!optionalVehicleEntity.isPresent()) {
            throw new EtcException("crm.vehicle.not.found");
        }
        VehicleEntity vehicleEntity = optionalVehicleEntity.get();
        String userLogin = FnCommon.getUserLogin(authentication);
        ReqCancelResultDTO reqCancelResultDTO = new ReqCancelResultDTO();
        reqCancelResultDTO.setRequest_id(System.currentTimeMillis());
        reqCancelResultDTO.setRef_trans_id(reqCancelResultDTO.getRequest_id());
        reqCancelResultDTO.setBOT_confirm(saleTransDelBoo1DTO.getBotStatus());
        if (!saleTransDelBoo1DTO.getBotStatus().equals(Constants.BOT_CONFIRM.YES)) {
            reqCancelResultDTO.setBOT_confirm(Constants.BOT_CONFIRM.NO);
        }
        if (Constants.BOT_CONFIRM.YES.equals(saleTransDelBoo1DTO.getBotStatus())) {
            reqCancelResultDTO.setStatus(Constants.BOO_STATUS.SUCCESS);
        } else {
            reqCancelResultDTO.setStatus(Constants.BOO_STATUS.REJECT);
        }
        reqCancelResultDTO.setSubscription_ticket_id(saleTransDelBoo1Entity.getSubscriptionTicketId());
        reqCancelResultDTO.setRef_trans_id(saleTransDelBoo1Entity.getRequestId());
        checkTransactionHistories(authentication, saleTransDelBoo1DTO.getPlateNumber(), saleTransDelBoo1Entity.getServicePlanTypeId(), vehicleEntity, saleTransDetailEntity);
        ResCancelResultDTO resCancelResultDTO = boo1Service.cancelResult(reqCancelResultDTO);
        if (resCancelResultDTO.getProcess_datetime() != null) {
            if (Constants.BOT_CONFIRM.YES.equals(saleTransDelBoo1DTO.getBotStatus())) {
                ocsService.deleteSupOffer(authentication, saleTransDelBoo1DTO.getActTypeId(),
                        saleTransDelBoo1Entity.getContractId(), saleTransDelBoo1Entity.getEpc(),
                        saleTransDelBoo1Entity.getOcsCode(), saleTransDelBoo1Entity.getOfferLevel());
                saleTransDelBoo1Entity.setBotStatus(saleTransDelBoo1DTO.getBotStatus());
                saleTransDelBoo1Entity.setBotConfirmDate(saleTransDelBoo1DTO.getBotConfirmDate());
                saleTransDelBoo1Entity.setBotConfirmContent(saleTransDelBoo1DTO.getBotConfirmContent());
                saleTransDelBoo1Entity.setBotConfirmUser(userLogin);
                saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.SUCCESS.value);
                saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
                saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.CANCEL.value);
                saleTransDetailEntity.setDestroy_date(new java.sql.Date(System.currentTimeMillis()));
                saleTransDetailEntity.setDestroy_user(userLogin);
                saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
            }
            if (Constants.BOT_CONFIRM.NO.equals(saleTransDelBoo1DTO.getBotStatus())) {
                saleTransDelBoo1Entity.setBotStatus(saleTransDelBoo1DTO.getBotStatus());
                saleTransDelBoo1Entity.setBotConfirmDate(saleTransDelBoo1DTO.getBotConfirmDate());
                saleTransDelBoo1Entity.setBotConfirmContent(saleTransDelBoo1DTO.getBotConfirmContent());
                saleTransDelBoo1Entity.setBotConfirmUser(userLogin);
                saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.REJECT.value);
                saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
            }
            updateLogRefund(saleTransDelBoo1DTO.getActTypeId(), reasonId, authentication, saleTransDelBoo1Entity.getContractId(), saleTransDelBoo1Entity.getCustId(), saleTransDetailEntity, saleTransDetailEntityOld, saleTransDelBoo1Entity, saleTransDelBoo1EntityOld);
            return "SUCCESS";
        } else {
            throw new EtcException("boo1.cancel.result.fail");
        }
    }

    /**
     * update log destroy ticket
     *
     * @param actTypeId
     * @param reasonId
     * @param authentication
     * @param contractId
     * @param custId
     * @param saleTransDetailEntity
     * @param saleTransDetailEntityOld
     */
    private void updateLog(Long actTypeId, Long reasonId, Authentication authentication, Long contractId, Long custId, SaleTransDetailEntity saleTransDetailEntity, SaleTransDetailEntity saleTransDetailEntityOld) throws Exception {
        // insert action audit
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, custId, contractId, null, ip));
        //insert action audit detail
        ActionAuditDetailEntity actionAuditDetailEntity = new ActionAuditDetailDTO(saleTransDetailEntityOld, saleTransDetailEntity).toEntity(actionAuditEntity.getActionAuditId(), SaleTransDetailEntity.class.getAnnotation(Table.class).name(), saleTransDetailEntity.getSaleTransDetailId());
        actionAuditDetailRepositoryJPA.save(actionAuditDetailEntity);

    }

    /**
     * update log destroy ticket
     *
     * @param actTypeId
     * @param reasonId
     * @param authentication
     * @param contractId
     * @param custId
     * @param saleTransDetailEntity
     * @param saleTransDetailEntityOld
     */
    private void updateLogRefund(Long actTypeId, Long reasonId, Authentication authentication, Long contractId, Long custId, SaleTransDetailEntity saleTransDetailEntity, SaleTransDetailEntity saleTransDetailEntityOld, SaleTransDelBoo1Entity saleTransDelBoo1Entity, SaleTransDelBoo1Entity saleTransDelBoo1EntityOld) throws Exception {
        // insert action audit
        String ip = InetAddress.getLocalHost().getHostAddress();
        ActionAuditEntity actionAuditEntity = actionAuditService.updateLogToActionAudit(new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, custId, contractId, null, ip));
        //insert action audit detail
        List<ActionAuditDetailEntity> actionAuditDetailEntityList = new ArrayList<>();
        ActionAuditDetailEntity actionAuditDetailEntity1 = new ActionAuditDetailDTO(saleTransDetailEntityOld, saleTransDetailEntity).toEntity(actionAuditEntity.getActionAuditId(), SaleTransDetailEntity.class.getAnnotation(Table.class).name(), saleTransDetailEntity.getSaleTransDetailId());
        ActionAuditDetailEntity actionAuditDetailEntity2 = new ActionAuditDetailDTO(saleTransDelBoo1EntityOld, saleTransDelBoo1Entity).toEntity(actionAuditEntity.getActionAuditId(), SaleTransDelBoo1Entity.class.getAnnotation(Table.class).name(), saleTransDelBoo1Entity.getSaleTransDelBoo1Id());
        actionAuditDetailEntityList.add(actionAuditDetailEntity1);
        actionAuditDetailEntityList.add(actionAuditDetailEntity2);
        actionAuditDetailRepositoryJPA.saveAll(actionAuditDetailEntityList);

    }

    /**
     * update ban ghi sale trans del
     *
     * @param saleTransDelBoo1Entity
     * @param authentication
     * @param saleTransDelBoo1DTO
     */
    private void updateSaleTransDel(SaleTransDelBoo1Entity saleTransDelBoo1Entity, Authentication authentication, SaleTransDelBoo1DTO saleTransDelBoo1DTO) {
        if (Constants.BOT_CONFIRM.YES.equals(saleTransDelBoo1DTO.getBotStatus())) {
            saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.SUCCESS.value);
            saleTransDelBoo1Entity.setBotStatus(SaleTransDelBoo1Entity.Status.SUCCESS.value);
        } else {
            saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.REJECT.value);
            saleTransDelBoo1Entity.setBotStatus(SaleTransDelBoo1Entity.Status.REJECT.value);
        }
        saleTransDelBoo1Entity.setBotConfirmContent(saleTransDelBoo1DTO.getBotConfirmContent());
        saleTransDelBoo1Entity.setBotConfirmDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransDelBoo1Entity.setBotConfirmUser(FnCommon.getUserLogin(authentication));
        saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);

    }

    /**
     * Thuc hien roolback cac ve that bai
     *
     * @param listFail
     * @param saleTransEntity
     * @param authentication
     */
    public boolean updateTicketFail(List<ServicePlanDTO> listFail, SaleTransEntity saleTransEntity, Authentication authentication,
                                    Long actTypeId, Long contractId) {
        boolean isSaleTrans = true;
        if (listFail.size() > 0) {
            try {
                long totalPriceFail = listFail.stream().reduce(0, (fee, servicePlanDTO) -> fee + servicePlanDTO.getFee().intValue(), Integer::sum);
                long totalTicketFail = listFail.size();
                if (totalPriceFail == saleTransEntity.getAmount()) {
                    saleTransRepositoryJPA.delete(saleTransEntity);
                    isSaleTrans = false;
                } else {
                    saleTransEntity.setAmount(saleTransEntity.getAmount() - totalPriceFail);
                    saleTransEntity.setQuantity(saleTransEntity.getQuantity() - totalTicketFail);
                    saleTransRepositoryJPA.save(saleTransEntity);
                }
                ocsService.addBalance(authentication, actTypeId, contractId, totalPriceFail);
            } catch (Exception e) {
                LOGGER.error("Co loi khi update thong tin cac ve mua loi", e);
            }
        }
        return isSaleTrans;
    }

    /**
     * Validate effDate va expDate
     *
     * @param vehicleToChargeTicket
     * @return
     */
    private boolean validateBeforeCharge(VehicleAddSuffOfferDTO vehicleToChargeTicket) {
        boolean isAccepted = true;
        if (!FnCommon.validateDateChargeTicket(vehicleToChargeTicket.getChargeMethodId(),
                FnCommon.convertToTicketType(vehicleToChargeTicket.getServicePlanTypeId().toString()),
                FnCommon.convertDateToStringOther(vehicleToChargeTicket.getEffDate(), Constants.COMMON_DATE_FORMAT_BOO),
                FnCommon.convertDateToStringOther(vehicleToChargeTicket.getExpDate(), Constants.COMMON_DATE_FORMAT_BOO), Constants.COMMON_DATE_FORMAT_BOO)
        ) {
            isAccepted = false;
        }
        if (vehicleToChargeTicket.getExpDate().before(new Date())) {
            isAccepted = false;
        }
        if (FnCommon.isNullOrEmpty(vehicleToChargeTicket.getBooCode())) {
            isAccepted = false;
        }
        return isAccepted;
    }

    /**
     * Dang ki goi cuoc phu cho tung phuong tien
     *
     * @param vehicleToChargeTicket
     * @param ticketInfo
     * @param authentication
     * @param addSupOfferRequest
     * @param contractId
     * @param userLogin
     * @param saleTransEntity
     * @param listSuccess
     * @param listFail
     */
    public void addOffer(VehicleAddSuffOfferDTO vehicleToChargeTicket,
                         ServicePlanDTO ticketInfo,
                         Authentication authentication,
                         AddSupOfferRequestDTO addSupOfferRequest, Long contractId, String userLogin, SaleTransEntity saleTransEntity,
                         List<ServicePlanDTO> listSuccess,
                         List<ServicePlanDTO> listFail, Long actTypeId, Long reasonId, Long custId, Long vehicleId, VehicleEntity vehicleEntity) {
        try {
            if (Constants.BOO2.equals(vehicleToChargeTicket.getBooCode())) {
                chargeTicketBoo2(vehicleToChargeTicket, ticketInfo, authentication, addSupOfferRequest,
                        contractId, userLogin, saleTransEntity, listSuccess, listFail, actTypeId, reasonId, custId, vehicleId, vehicleEntity);
            }
            if (Constants.BOO1.equals(vehicleToChargeTicket.getBooCode())) {
                chargeTicketBoo1(authentication, ticketInfo, listSuccess, listFail,
                        vehicleToChargeTicket, userLogin, saleTransEntity, actTypeId, reasonId, contractId, custId, vehicleId, vehicleEntity);
            }
        } catch (Exception e) {
            LOGGER.error("Co loi khi mua ve : ", e);
        }
    }

    /**
     * @param saleTransDetailId
     * @param saleTransDetailDTO
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object destroyTicketNotRefund(Long saleTransDetailId, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception {
        Optional<SaleTransDetailEntity> saleTransDetailEntityOptional = saleTransDetailRepositoryJPA.findById(saleTransDetailId);
        if (!saleTransDetailEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailEntityOptional.get();
        Optional<SaleTransEntity> saleTransEntityOptional = saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId());
        if (!saleTransEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransEntity saleTransEntity = saleTransEntityOptional.get();
        if (Constants.BOO2ChargeBOO1.equals(saleTransDetailEntity.getBooFlow())) {
            return destroyTicketNotRefundBoo1(saleTransDetailEntity, saleTransEntity, saleTransDetailDTO, authentication);
        } else {
            return destroyTicketNotRefundBoo2(saleTransDetailEntity, authentication, saleTransDetailDTO, saleTransEntity);
        }
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
    public ActionAuditEntity writeLogNew(Authentication authentication, long reasonId, long actTypeId, long contractId, long customerId, Long vehicleId, long id, Object entity, String tableName) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId,
                    customerId, contractId, vehicleId, ip);
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
     * Call destroy boo1
     *
     * @param saleTransDetailEntity
     * @param saleTransEntity
     * @param saleTransDelBoo1DTO
     * @param requestType
     */
    private void destroyTicketBoo1(SaleTransDetailEntity saleTransDetailEntity, SaleTransEntity saleTransEntity, SaleTransDelBoo1DTO saleTransDelBoo1DTO, Long requestType, String userLogin) throws Exception {
        ReqCancelTicketDTO request = new ReqCancelTicketDTO();
        request.setSubscription_ticket_id(saleTransDetailEntity.getSubscriptionTicketId());
        request.setStation_type(FnCommon.convertStationType(saleTransDelBoo1DTO.getStationType()));
        request.setStation_in_id(saleTransDelBoo1DTO.getStationInId());
        request.setStation_out_id(saleTransDelBoo1DTO.getStationOutId());
        request.setTicket_type(FnCommon.convertToTicketType(saleTransDetailEntity.getServicePlanTypeId().toString()));
        request.setStart_date(FnCommon.convertDateToStringOther(saleTransDetailEntity.getEffDate(), Constants.COMMON_DATE_FORMAT_BOO));
        request.setEnd_date(FnCommon.convertDateToStringOther(saleTransDetailEntity.getExpDate(), Constants.COMMON_DATE_FORMAT_BOO));
        request.setPlate(saleTransDetailEntity.getPlateNumber());
        request.setEtag(saleTransDetailEntity.getEpc());
        request.setRequest_id(System.currentTimeMillis());
        request.setRequest_datetime(System.currentTimeMillis());
        request.setRequest_type(requestType);
        ResCancelTicketDTO response = boo1Service.destroyTicket(request);
        if (response == null) {
            throw new EtcException("boo.call.destroy.ticket.fail");
        }
        if ("RECEIVED".equals(response.getStatus()) && response.getRequest_type().equals(requestType)) {
            setSaleTransDelBoo1Destroy(saleTransDetailEntity, saleTransEntity, saleTransDelBoo1DTO, userLogin, response.getRequest_id());
        }
    }

    /**
     * Them gia tri sale trans del boo1
     *
     * @param saleTransDetailEntity
     * @param saleTransEntity
     * @param saleTransDelBoo1DTO
     * @param userLogin
     * @param request_id
     */
    private void setSaleTransDelBoo1Destroy(SaleTransDetailEntity saleTransDetailEntity, SaleTransEntity saleTransEntity, SaleTransDelBoo1DTO saleTransDelBoo1DTO, String userLogin, Long request_id) {
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
        saleTransDelBoo1Entity.setContractId(saleTransEntity.getContractId());
        saleTransDelBoo1Entity.setPlateNumber(saleTransDetailEntity.getPlateNumber());
        if (saleTransDelBoo1DTO.getStationInId() != null) {
            saleTransDelBoo1Entity.setStationInId(saleTransDelBoo1DTO.getStationInId());
        }
        if (saleTransDelBoo1DTO.getStationOutId() != null) {
            saleTransDelBoo1Entity.setStationOutId(saleTransDelBoo1DTO.getStationOutId());
        }
        saleTransDelBoo1Entity.setServicePlanTypeId(saleTransDetailEntity.getServicePlanTypeId());
        saleTransDelBoo1Entity.setEpc(saleTransDetailEntity.getEpc());
        if (saleTransDelBoo1DTO.getStationType() != null) {
            saleTransDelBoo1Entity.setStationType(saleTransDelBoo1DTO.getStationType());
        }
        if (saleTransDetailEntity.getStageId() != null) {
            saleTransDelBoo1Entity.setStageId(saleTransDetailEntity.getStageId());
        }
        if (saleTransDetailEntity.getStationId() != null) {
            saleTransDelBoo1Entity.setStationId(saleTransDetailEntity.getStationId());
        }
        saleTransDelBoo1Entity.setEffDate(saleTransDetailEntity.getEffDate());
        saleTransDelBoo1Entity.setExpDate(saleTransDetailEntity.getExpDate());
        saleTransDelBoo1Entity.setPrice(saleTransDetailEntity.getPrice());
        saleTransDelBoo1Entity.setRequestType(SaleTransDelBoo1Entity.RequestType.REFUND_MONEY.value);
        saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.RECEIVED.value);
        saleTransDelBoo1Entity.setBotStatus(SaleTransDelBoo1Entity.Status.RECEIVED.value);
        saleTransDelBoo1Entity.setCreateUser(userLogin);
        saleTransDelBoo1Entity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransDelBoo1Entity.setCustId(saleTransEntity.getCustId());
        saleTransDelBoo1Entity.setContractNo(saleTransEntity.getContractNo());
        saleTransDelBoo1Entity.setBooCode(saleTransDetailEntity.getBooCode());
        saleTransDelBoo1Entity.setOcsCode(saleTransDetailEntity.getOcsCode());
        saleTransDelBoo1Entity.setOfferLevel(saleTransDetailEntity.getOfferLevel());
        saleTransDelBoo1Entity.setSubscriptionTicketId(saleTransDetailEntity.getSubscriptionTicketId());
        saleTransDelBoo1Entity.setRequestId(request_id);
        saleTransDelBoo1Entity.setRequestDatetime(System.currentTimeMillis());
        saleTransDelBoo1Entity.setMethodChargeId(saleTransEntity.getMethodRechargeId());
        saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
    }

    /**
     * Huy ve khong hoan tien cua boo2
     *
     * @param saleTransDetailEntity
     * @param authentication
     * @param saleTransDetailDTO
     * @param saleTransEntity
     */
    private Object destroyTicketNotRefundBoo2(SaleTransDetailEntity saleTransDetailEntity, Authentication authentication, SaleTransDetailDTO saleTransDetailDTO, SaleTransEntity saleTransEntity) throws Exception {
        long reasonId = getReasonIdByactTypeId(saleTransDetailDTO.getActTypeId());
        OCSResponse ocsResponse = ocsService.deleteSupOffer(authentication, saleTransDetailDTO.getActTypeId(), saleTransEntity.getContractId(), saleTransDetailEntity.getEpc(), saleTransDetailEntity.getOcsCode(), saleTransDetailEntity.getOfferLevel());
        if (ocsResponse != null && "0".equals(ocsResponse.getResultCode())) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            SaleTransDetailEntity saleTransDetailEntityOld = gson.fromJson(gson.toJson(saleTransDetailEntity), SaleTransDetailEntity.class);
            updateSaleTransDetails(saleTransDetailEntity, authentication);
            updateLog(saleTransDetailDTO.getActTypeId(), reasonId, authentication, saleTransEntity.getContractId(), saleTransEntity.getCustId(), saleTransDetailEntity, saleTransDetailEntityOld);
            return "SUCCESS";
        } else {
            throw new EtcException("ocs.delete.sup.offer.fail");
        }
    }

    /**
     * Huy ve khong hoan tien boo1
     *
     * @param saleTransDetailEntity
     * @param saleTransEntity
     * @param saleTransDetailDTO
     * @param authentication
     */
    private Object destroyTicketNotRefundBoo1(SaleTransDetailEntity saleTransDetailEntity, SaleTransEntity saleTransEntity, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception {
        Long reasonId = getReasonIdByactTypeId(saleTransDetailDTO.getActTypeId());
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        SaleTransDetailEntity saleTransDetailEntityOld = gson.fromJson(gson.toJson(saleTransDetailEntity), SaleTransDetailEntity.class);
        ReqCancelTicketDTO request = new ReqCancelTicketDTO();
        request.setSubscription_ticket_id(saleTransDetailEntity.getSubscriptionTicketId());
        request.setStation_type(FnCommon.convertStationType(saleTransDetailDTO.getStationType()));
        request.setStation_in_id(saleTransDetailDTO.getStationInId());
        request.setStation_out_id(saleTransDetailDTO.getStationOutId());
        request.setTicket_type(FnCommon.convertToTicketType(saleTransDetailEntity.getServicePlanTypeId().toString()));
        request.setStart_date(FnCommon.convertDateToStringOther(saleTransDetailEntity.getEffDate(), Constants.COMMON_DATE_FORMAT_BOO));
        request.setEnd_date(FnCommon.convertDateToStringOther(saleTransDetailEntity.getExpDate(), Constants.COMMON_DATE_FORMAT_BOO));
        request.setPlate(saleTransDetailEntity.getPlateNumber());
        request.setEtag(saleTransDetailEntity.getEpc());
        request.setRequest_id(System.currentTimeMillis());
        request.setRequest_datetime(System.currentTimeMillis());
        request.setRequest_type(Constants.BOT_REFUND.NO);
        ResCancelTicketDTO response = boo1Service.destroyTicket(request);
        if (response == null) {
            throw new EtcException("boo.call.destroy.ticket.fail");
        }
        if (Constants.BOO_STATUS.SUCCESS.equals(response.getStatus())) {
            RemoveSupOfferRoaming removeSupOfferRoaming = new RemoveSupOfferRoaming();
            removeSupOfferRoaming.setEPC(saleTransDetailEntity.getEpc());
            removeSupOfferRoaming.setSubscriptionTicketId(saleTransDetailEntity.getSubscriptionTicketId().toString());
            removeSupOfferRoaming.setIsRefund(Constants.BOT_REFUND.NO.toString());
            removeSupOfferRoaming.setActTypeId(Constants.ACT_TYPE.DESTROY_TICKET);
            OCSResponse ocsResponse = ocsService.removeSupOfferRoaming(removeSupOfferRoaming, authentication);
            if (ocsResponse != null && "0".equals(ocsResponse.getResultCode())) {
                saleTransDetailEntity.setDestroy_user(FnCommon.getUserLogin(authentication));
                saleTransDetailEntity.setDestroy_date(new java.sql.Date(System.currentTimeMillis()));
                saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.CANCEL.value);
                saleTransDetailEntity.setRefund_type(Constants.BOT_REFUND.NO);
                saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
                updateLog(saleTransDetailDTO.getActTypeId(), reasonId, authentication, saleTransEntity.getContractId(), saleTransEntity.getCustId(), saleTransDetailEntity, saleTransDetailEntityOld);
                return "SUCCESS";
            }
        }
        throw new EtcException("crm.destroy.ticket.fail");
    }

    /**
     * Huy tu dong gia han
     *
     * @param saleTransDetailId
     * @param saleTransDetailDTO
     * @param authentication
     * @return
     * @throws Exception
     */
    @Override
    public Object cancelAutoRenew(Long saleTransDetailId, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception {
        Optional<SaleTransDetailEntity> saleTransDetailEntityOptional = saleTransDetailRepositoryJPA.findById(saleTransDetailId);
        if (!saleTransDetailEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailEntityOptional.get();
        Optional<SaleTransEntity> saleTransEntityOptional = saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId());
        if (!saleTransEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransEntity saleTransEntity = saleTransEntityOptional.get();
        if (Constants.AUTO_RENEW.NO.equals(saleTransDetailEntity.getAutoRenew())) {
            throw new EtcException("crm.ticket.not.auto.renew");
        }
        if (Constants.AUTO_RENEW.YES.equals(saleTransDetailEntity.getAutoRenew())) {
            ChargeSupOfferDTO chargeSupOfferDTO = new ChargeSupOfferDTO();
            chargeSupOfferDTO.setContractId(saleTransEntity.getContractId().toString());
            chargeSupOfferDTO.setEpc(saleTransDetailEntity.getEpc());
            chargeSupOfferDTO.setOfferId(saleTransDetailEntity.getOcsCode());
            chargeSupOfferDTO.setOfferLevel(saleTransDetailEntity.getOfferLevel());
            chargeSupOfferDTO.setIsRecurring(Constants.AUTO_RENEW.NO);
            OCSResponse ocsResponse = ocsService.changeSupOfferTicket(chargeSupOfferDTO, authentication);
            if (ocsResponse != null && "0".equals(ocsResponse.getResultCode())) {
                saleTransDetailEntity.setAutoRenew(Constants.AUTO_RENEW.NO);
                saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
                return "SUCCESS";
            }
        }
        throw new EtcException("crm.cancel.auto.renew.fail.data");
    }

    /**
     * Dang ky tu dong gia han
     *
     * @param saleTransDetailId
     * @param saleTransDetailDTO
     * @param authentication
     * @return
     */
    @Override
    public Object registerAutoRenew(Long saleTransDetailId, SaleTransDetailDTO saleTransDetailDTO, Authentication authentication) throws Exception {
        Optional<SaleTransDetailEntity> saleTransDetailEntityOptional = saleTransDetailRepositoryJPA.findById(saleTransDetailId);
        if (!saleTransDetailEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailEntityOptional.get();
        Optional<SaleTransEntity> saleTransEntityOptional = saleTransRepositoryJPA.findById(saleTransDetailEntity.getSaleTransId());
        if (!saleTransEntityOptional.isPresent()) {
            throw new EtcException("crm.validate.ticket.not.empty");
        }
        SaleTransEntity saleTransEntity = saleTransEntityOptional.get();
        if (Constants.AUTO_RENEW.YES.equals(saleTransDetailEntity.getAutoRenew())) {
            throw new EtcException("crm.ticket.auto.renew");
        }
        if (Constants.AUTO_RENEW.NO.equals(saleTransDetailEntity.getAutoRenew())) {
            ChargeSupOfferDTO chargeSupOfferDTO = new ChargeSupOfferDTO();
            chargeSupOfferDTO.setContractId(saleTransEntity.getContractId().toString());
            chargeSupOfferDTO.setEpc(saleTransDetailEntity.getEpc());
            chargeSupOfferDTO.setOfferId(saleTransDetailEntity.getOcsCode());
            chargeSupOfferDTO.setOfferLevel(saleTransDetailEntity.getOfferLevel());
            chargeSupOfferDTO.setIsRecurring(Constants.AUTO_RENEW.YES);
            OCSResponse ocsResponse = ocsService.changeSupOfferTicket(chargeSupOfferDTO, authentication);
            if (ocsResponse != null && "0".equals(ocsResponse.getResultCode())) {
                saleTransDetailEntity.setAutoRenew(Constants.AUTO_RENEW.YES);
                saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
                return "SUCCESS";
            }
        }
        throw new EtcException("crm.cancel.auto.renew.fail.data");
    }

    /**
     * Lay lich su xe qua tram
     *
     * @param authentication
     * @param plate
     * @param ticketType
     * @param vehicleEntity
     * @param saleTransDetailEntity
     * @return
     */
    private TransactionsHistoryVehicleDTO getTransactionsVehicle(Authentication authentication, String plate, String ticketType,
                                                                 VehicleEntity vehicleEntity, SaleTransDetailEntity saleTransDetailEntity, Date effDate, Date expDate) {

        HashMap<String, String> map = new HashMap<>();
        if (!FnCommon.isNullOrEmpty(plate)) {
            map.put("code", plate);
        }
        if (!FnCommon.isNullOrEmpty(ticketType)) {
            map.put("ticketType", FnCommon.convertTicketType(ticketType));
        }
        if (Objects.nonNull(vehicleEntity.getVehicleGroupId())) {
            map.put("vehicleType", vehicleEntity.getVehicleGroupId().toString());
        }
        map.put("contractId", vehicleEntity.getContractId().toString());
        map.put("timestampInFrom", FnCommon.convertDateToStringOther(saleTransDetailEntity.getEffDate(), Constants.COMMON_DATE_FORMAT));
        map.put("timestampInTo", FnCommon.convertDateToStringOther(saleTransDetailEntity.getExpDate(), Constants.COMMON_DATE_FORMAT));
        map.put("stationType", "1");
        if (saleTransDetailEntity.getStageId() != null) {
            StageBooDTO stageDTO = this.getStageById(FnCommon.getStringToken(authentication), saleTransDetailEntity.getStageId().toString());
            if (stageDTO.getData() != null) {
                map.put("stationInId", stageDTO.getData().getStation_input_id().toString());
                map.put("stationOutId", stageDTO.getData().getStation_output_id().toString());
                map.put("timestampOutFrom", FnCommon.convertDateToStringOther(saleTransDetailEntity.getEffDate(), Constants.COMMON_DATE_FORMAT));
                map.put("timestampOutTo", FnCommon.convertDateToStringOther(saleTransDetailEntity.getExpDate(), Constants.COMMON_DATE_FORMAT));
                map.put("stationType", "0");
            }
        } else {
            map.put("stationInId", saleTransDetailEntity.getStationId().toString());
        }
        map.put("pagesize", "10");
        map.put("startrecord", "0");
        String token = FnCommon.getStringToken(authentication);
        String response = FnCommon.doGetRequest(transactionsHistoryUrl, map, token);
        LOGGER.info("Response lich su xe qua tram : " + response);
        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = new Gson().fromJson(response, TransactionsHistoryVehicleDTO.class);
        return transactionsHistoryVehicleDTO;
    }

    /**
     * Check lich su xe qua tram
     *
     * @param authentication
     * @param plateNumber
     * @param servicePlanTypeId
     * @param vehicleEntity
     * @param saleTransDetailEntity
     */
    private void checkTransactionHistories(Authentication authentication, String plateNumber, Long servicePlanTypeId, VehicleEntity vehicleEntity, SaleTransDetailEntity saleTransDetailEntity) {
        if (Constants.SERVICE_PLAN_TYPE_MONTH.equals(saleTransDetailEntity.getServicePlanTypeId())) {
            TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), saleTransDetailEntity.getExpDate());
            if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                throw new BooException("boo.vehicle.transaction.histories.exist");
            }
        }
        if (Constants.SERVICE_PLAN_TYPE_QUART.equals(saleTransDetailEntity.getServicePlanTypeId())) {
            Long effDate = saleTransDetailEntity.getEffDate().getTime();
            Long expDate = saleTransDetailEntity.getExpDate().getTime();
            Long currentDate = System.currentTimeMillis();
            if (currentDate >= effDate && currentDate <= expDate) {
                Long diffDaysExpDateAndEffDate = FnCommon.diffDays(expDate, effDate);
                if (diffDaysExpDateAndEffDate <= 30) {
                    TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), new Date(currentDate));
                    if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                        throw new BooException("boo.vehicle.transaction.histories.exist");
                    }
                } else {
                    Long diffDaysEffDateAndCurrentDate = FnCommon.diffDays(currentDate, effDate);
                    if (diffDaysEffDateAndCurrentDate <= 30) {
                        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), new Date(currentDate));
                        if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                            throw new BooException("boo.vehicle.transaction.histories.exist");
                        }
                    } else {
                        if (diffDaysEffDateAndCurrentDate <= 60) {
                            TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29));
                            if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                                throw new BooException("boo.vehicle.transaction.histories.exist");
                            }
                            TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO1 = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 58));
                            if (transactionsHistoryVehicleDTO1.getData().getListData().size() > 0) {
                                throw new BooException("boo.vehicle.transaction.histories.exist");
                            }

                        } else {
                            TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO2 = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29));
                            if (transactionsHistoryVehicleDTO2.getData().getListData().size() > 0) {
                                throw new BooException("boo.vehicle.transaction.histories.exist");
                            }
                            TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO3 = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 58));
                            if (transactionsHistoryVehicleDTO3.getData().getListData().size() > 0) {
                                throw new BooException("boo.vehicle.transaction.histories.exist");
                            }
                            TransactionsHistoryVehicleDTO transactionsHistoryVehicleDT4 = getTransactionsVehicle(authentication, plateNumber, servicePlanTypeId.toString(), vehicleEntity, saleTransDetailEntity, FnCommon.addDays(saleTransDetailEntity.getEffDate(), 58), new Date(currentDate));
                            if (transactionsHistoryVehicleDT4.getData().getListData().size() > 0) {
                                throw new BooException("boo.vehicle.transaction.histories.exist");
                            }
                        }
                    }
                }
            }
        }
    }
}
