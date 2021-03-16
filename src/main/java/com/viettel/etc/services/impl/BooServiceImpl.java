package com.viettel.etc.services.impl;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.*;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.RemoveSupOfferRoaming;
import com.viettel.etc.repositories.BooRepository;
import com.viettel.etc.repositories.TicketRepository;
import com.viettel.etc.repositories.tables.*;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.ExceptionListServiceJPA;
import com.viettel.etc.services.tables.SaleTransDetailServiceJPA;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.BooException;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.net.util.IPAddressUtil;

import javax.persistence.Table;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.viettel.etc.utils.Constants.*;

/**
 * Chuc nang luong lien thong BOO
 */
@Service
public class BooServiceImpl implements BooService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BooServiceImpl.class);
    private final String EXCEPTION_VEHICLE_TYPE = "EXCEPTION_VEHICLE_TYPE";
    private final String EXCEPTION_VEHICLE_TYPE_PROMOTION_ID = "EXCEPTION_VEHICLE_TYPE_PROMOTION_ID";
    private final String EXCEPTION_TICKET_TYPE_PROMOTION_ID = "EXCEPTION_TICKET_TYPE_PROMOTION_ID";

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    @Autowired
    ExceptionListServiceJPA exceptionListServiceJPA;

    @Autowired
    BooRepository booRepository;

    @Autowired
    SaleTransRepositoryJPA saleTransRepositoryJPA;

    @Autowired
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    VehicleTypeService vehicleTypeService;

    @Autowired
    VehicleGroupService vehicleGroupService;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    SaleTransDetailServiceJPA saleTransDetailServiceJPA;

    @Autowired
    OCSService ocsService;

    @Autowired
    SaleTransDelBoo1RepositoryJPA saleTransDelBoo1RepositoryJPA;

    @Autowired
    ExceptionListRepositoryJPA EtExceptionListRepositoryJPA;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ActionAuditService actionAuditService;

    @Autowired
    ActionAuditDetailService actionAuditDetailService;

    @Autowired
    ActionAuditDetailRepositoryJPA actionAuditDetailRepositoryJPA;

    @Autowired
    ActReasonRepositoryJPA actReasonRepositoryJPA;

    @Autowired
    CategoryMappingRepositoryJPA categoryMappingRepositoryJPA;

    @Autowired
    StageService stageService;

    @Autowired
    StationService stationService;

    @Value("${boo.contract.no}")
    private String booContractNo;

    @Value("${ws.dmdc.stages.boo}")
    String stagesBooUrl;

    @Value("${ws.dmdc.stations.boo}")
    String stationsUrl;

    @Value("${ws.dmdc.stage.get}")
    private String stageUrl;

    @Value("${ws.transactions.history}")
    private String transactionsHistoryUrl;


    /**
     * Tra cuu ve
     *
     * @param reqQueryTicketDTO Du lieu tra cuu
     * @return
     */
    @Override
    public Object queryTicket(Authentication authentication, ReqQueryTicketDTO reqQueryTicketDTO) {
        if (reqQueryTicketDTO.getPlate() == null && reqQueryTicketDTO.getEtag() == null && reqQueryTicketDTO.getSubscription_ticket_id() == null) {
            throw new BooException("boo.error.null.data");
        }
        ResQueryTicketDTO resQueryTicketBooDTO = new ResQueryTicketDTO();
        long start = System.currentTimeMillis();
        ResultSelectEntity result = booRepository.queryTicketBoo(reqQueryTicketDTO);
        long end = System.currentTimeMillis();
        List<ResQueryTicketDTO.ListTicket> resultList = (List<ResQueryTicketDTO.ListTicket>) result.getListData();
        resQueryTicketBooDTO.setData(resultList);
        resQueryTicketBooDTO.setProcess_datetime(end - start);
        resQueryTicketBooDTO.setResponse_datetime(System.currentTimeMillis());
        return resQueryTicketBooDTO;
    }

    /**
     * Tinh gia ve
     *
     * @param reqCalculatorTicketDTO Du lieu gia ve
     * @return
     */
    @Override
    public Object calculatorTicket(Authentication authentication, ReqCalculatorTicketDTO reqCalculatorTicketDTO, boolean isCallFromBoo) {
        String token = FnCommon.getStringToken(authentication);
        List<String> vehicleGroupId = null;
        Long stageId = null, methodChargeId = null, vehicleGroupIdOld = null, excVehicleId = null, excTicketId = null;
        Map<String, String> mapException = new HashMap<>();
        if (FnCommon.isNullOrEmpty(reqCalculatorTicketDTO.getRegister_vehicle_type()) && reqCalculatorTicketDTO.getVehicle_type() == null) {
            throw new BooException("crm.boo.null.data");
        }
        String plateType = FnCommon.getPlateTypeBOO1(reqCalculatorTicketDTO.getPlate());
        reqCalculatorTicketDTO.setPlate(FnCommon.formatPlateBOO1(reqCalculatorTicketDTO.getPlate()));

        if ((reqCalculatorTicketDTO.getVehicle_type() == 1 || reqCalculatorTicketDTO.getVehicle_type() == 3)
                && !FnCommon.isNullOrEmpty(reqCalculatorTicketDTO.getRegister_vehicle_type())
                && "O".equalsIgnoreCase(reqCalculatorTicketDTO.getStation_type())
                && Constants.RACH_MIEU_STATION.equals(reqCalculatorTicketDTO.getStation_in_id())
        ) {
            AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
            addVehicleRequestDTO.setCargoWeight(reqCalculatorTicketDTO.getWeight_goods());
            addVehicleRequestDTO.setNetWeight(reqCalculatorTicketDTO.getWeight_all());
            addVehicleRequestDTO.setSeatNumber(reqCalculatorTicketDTO.getSeat());
            addVehicleRequestDTO.setVehicleTypeId(Long.parseLong(reqCalculatorTicketDTO.getRegister_vehicle_type()));
            addVehicleRequestDTO.setStationId(reqCalculatorTicketDTO.getStation_in_id());
            mapException = checkVehicleCouldChargeTicket(reqCalculatorTicketDTO, null, null, plateType);
            if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_VEHICLE_TYPE))) {
                vehicleGroupId = Arrays.asList(mapException.get(EXCEPTION_VEHICLE_TYPE));
            } else {
                vehicleGroupId = vehicleGroupService.getVehicleGroupById(token, addVehicleRequestDTO);
            }
            if (FnCommon.isNullOrEmpty(vehicleGroupId) && reqCalculatorTicketDTO.getVehicle_type() == null) {
                throw new BooException("crm.boo.null.data");
            }
        }

        if ("C".equalsIgnoreCase(reqCalculatorTicketDTO.getStation_type())) {
            String url = stagesBooUrl.replace("{stationInId}", String.valueOf(reqCalculatorTicketDTO.getStation_in_id()));
            String url2 = url.replace("{stationOutId}", String.valueOf(reqCalculatorTicketDTO.getStation_out_id()));
            String stageResponse = FnCommon.doGetRequest(url2, null, token);
            StageBooDTO stage = new Gson().fromJson(stageResponse, StageBooDTO.class);
            if (stage.getData() != null) {
                if (reqCalculatorTicketDTO.getPlate() != null && reqCalculatorTicketDTO.getEtag() != null) {
                    mapException = checkVehicleCouldChargeTicket(reqCalculatorTicketDTO, stage.getData(), null, plateType);
                }
                stageId = stage.getData().getId();
                methodChargeId = stage.getData().getMethod_charge_id();
            } else {
                throw new BooException("crm.boo.null.data");
            }
        } else if ("O".equalsIgnoreCase(reqCalculatorTicketDTO.getStation_type())) {
            String url = stationsUrl.replace("{stationId}", String.valueOf(reqCalculatorTicketDTO.getStation_in_id()));
            String strResp = FnCommon.doGetRequest(url, null, token);
            StationBooDTO stationDTO = new Gson().fromJson(strResp, StationBooDTO.class);
            if (stationDTO.getData() != null) {
                if (reqCalculatorTicketDTO.getPlate() != null && reqCalculatorTicketDTO.getEtag() != null) {
                    mapException = checkVehicleCouldChargeTicket(reqCalculatorTicketDTO, null, stationDTO.getData(), plateType);
                }
                methodChargeId = stationDTO.getData().getMethod_charge_id();
            } else {
                throw new BooException("crm.boo.null.data");
            }
        } else {
            throw new BooException("crm.boo.null.data");
        }
        if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_VEHICLE_TYPE))) {
            vehicleGroupId = null;
            if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_TICKET_TYPE_PROMOTION_ID))) {
                vehicleGroupIdOld = reqCalculatorTicketDTO.getVehicle_type();
            }
            reqCalculatorTicketDTO.setVehicle_type(Long.parseLong(mapException.get(EXCEPTION_VEHICLE_TYPE)));
        }

        if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_TICKET_TYPE_PROMOTION_ID))) {
            excTicketId = Long.parseLong(mapException.get(EXCEPTION_TICKET_TYPE_PROMOTION_ID));
        }

        if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_VEHICLE_TYPE_PROMOTION_ID))) {
            excVehicleId = Long.parseLong(mapException.get(EXCEPTION_VEHICLE_TYPE_PROMOTION_ID));
        }


        long start = System.currentTimeMillis();
        ResultSelectEntity result = booRepository.getFeeBoo(reqCalculatorTicketDTO, vehicleGroupId, stageId, methodChargeId, excVehicleId, excTicketId, vehicleGroupIdOld, isCallFromBoo);
        List<ResCalculatorTicketDTO> resCalculatorTicketDTOList = (List<ResCalculatorTicketDTO>) result.getListData();

        if (resCalculatorTicketDTOList.size() > 1) {
            resCalculatorTicketDTOList.sort((resCalculatorTicket1, resCalculatorTicket2) -> (int) (resCalculatorTicket1.getPrice_amount() - resCalculatorTicket2.getPrice_amount()));
        }
        long end = System.currentTimeMillis();
        if (result.getListData().size() != 0) {
            ResCalculatorTicketDTO resCalculatorTicketBooDTO = (ResCalculatorTicketDTO) result.getListData().get(0);
            resCalculatorTicketBooDTO.setProcess_datetime(end - start);
            resCalculatorTicketBooDTO.setResponse_datetime(System.currentTimeMillis());
            return resCalculatorTicketBooDTO;
        } else {
            throw new BooException("crm.boo.null.data");
        }

    }

    /***
     * kiem tra xe co du dieu kien mua ve hay k?
     * @param requestCalculatorTicket
     */
    private Map<String, String> checkVehicleCouldChargeTicket(ReqCalculatorTicketDTO requestCalculatorTicket, StageBooDTO.Stage stage, StationBooDTO.Station station, String plateType) {
        Map<String, String> mapException = new HashMap<>();
        List<ExceptionListEntity> dataException = exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus(requestCalculatorTicket.getPlate(), requestCalculatorTicket.getEtag(), ExceptionListEntity.Status.APPROVAL.value);

        if (dataException != null) {
            for (ExceptionListEntity exceptionListEntity : dataException) {
                if (FnCommon.isNullOrEmpty(plateType) || !String.valueOf(exceptionListEntity.getLicensePlateType()).equalsIgnoreCase(plateType)) {
                    continue;
                }
                if (stage != null) {
                    if (Integer.parseInt(exceptionListEntity.getExceptionType()) == 4 && exceptionListEntity.getStageId().equals(stage.getId())) {
                        throw new BooException("boo.ticket.purchase.not.allow");
                    }
                    if (exceptionListEntity.getStageId() != null && exceptionListEntity.getStageId().equals(stage.getId())) {
                        if (exceptionListEntity.getExpDate() == null || exceptionListEntity.getExpDate().after(new Date(System.currentTimeMillis()))
                                && (exceptionListEntity.getEffDate().before(new Date(System.currentTimeMillis())) || exceptionListEntity.getEffDate().equals(new Date(System.currentTimeMillis())))) {
                            if (!FnCommon.isNullOrEmpty(exceptionListEntity.getExceptionVehicleType())) {
                                mapException.put(EXCEPTION_VEHICLE_TYPE, String.valueOf(exceptionListEntity.getExceptionVehicleType()));
                                if (exceptionListEntity.getPromotionId() != null) {
                                    mapException.put(EXCEPTION_VEHICLE_TYPE_PROMOTION_ID, String.valueOf(exceptionListEntity.getPromotionId()));
                                }
                            } else {
                                if (exceptionListEntity.getPromotionId() != null) {
                                    mapException.put(EXCEPTION_TICKET_TYPE_PROMOTION_ID, String.valueOf(exceptionListEntity.getPromotionId()));
                                }
                            }
                        }
                    }
                }
                if (station != null) {
                    if (Integer.parseInt(exceptionListEntity.getExceptionType()) == 4 && exceptionListEntity.getStationId().equals(station.getId())) {
                        throw new BooException("boo.ticket.purchase.not.allow");
                    }
                    if (exceptionListEntity.getStationId() != null && exceptionListEntity.getStationId().equals(station.getId())) {
                        if (exceptionListEntity.getExpDate() == null || exceptionListEntity.getExpDate().after(new Date(System.currentTimeMillis()))
                                && (exceptionListEntity.getEffDate().before(new Date(System.currentTimeMillis())) || exceptionListEntity.getEffDate().equals(new Date(System.currentTimeMillis())))) {
                            if (!FnCommon.isNullOrEmpty(exceptionListEntity.getExceptionVehicleType())) {
                                mapException.put(EXCEPTION_VEHICLE_TYPE, String.valueOf(exceptionListEntity.getExceptionVehicleType()));
                                if (exceptionListEntity.getPromotionId() != null) {
                                    mapException.put(EXCEPTION_VEHICLE_TYPE_PROMOTION_ID, String.valueOf(exceptionListEntity.getPromotionId()));
                                }
                            } else {
                                if (exceptionListEntity.getPromotionId() != null) {
                                    mapException.put(EXCEPTION_TICKET_TYPE_PROMOTION_ID, String.valueOf(exceptionListEntity.getPromotionId()));
                                }
                            }
                        }
                    }
                }
                if (stage == null && station == null && exceptionListEntity.getExceptionVehicleType() != null) {
                    mapException.put(EXCEPTION_VEHICLE_TYPE, String.valueOf(exceptionListEntity.getExceptionVehicleType()));
                }
            }
        }
        return mapException;
    }

    /**
     * Mua ve
     *
     * @param authentication
     * @param req
     * @return
     */
    @Override
    @Transactional(noRollbackFor = BooException.class)
    public ResChargeTicketDTO chargeTicket(Authentication authentication, ReqChargeTicketDTO req) throws Exception {
        long startProcessTime = System.currentTimeMillis();
        validateInputChargeTicket(req);
        String plateTypeCode = FnCommon.getPlateTypeBOO1(req.getPlate());
        String plateNumber = FnCommon.formatPlateBOO1(req.getPlate());
        ResCalculatorTicketDTO resCalculatorTicket = (ResCalculatorTicketDTO) calculatorTicket(authentication, req.toReqCalculatorTicketBooDTO(req), false);
        if (Objects.isNull(resCalculatorTicket.getPrice_amount())) {
            throw new BooException("boo.can.not.get.price.ticket");
        }
        // validate start end
        if (!FnCommon.validateDateChargeTicket(resCalculatorTicket.getCharge_method_id(), req.getTicket_type(), req.getStart_date(), req.getEnd_date(), Constants.COMMON_DATE_FORMAT_BOO)) {
            throw new BooException("Invalidate start_date, end_date");
        }
        validateExistTicket(req, resCalculatorTicket.getStage_id());

        VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByContractIdAndPlateNumberAndPlateTypeCode(BOO_INFO.CONTRACT_ID, plateNumber, plateTypeCode);
        if (Objects.isNull(vehicleEntity)) {
            String rfidSerial = String.valueOf(vehicleRepositoryJPA.getNextValSequenceSerial());
            registerVehicleBoo1ToOCS(authentication, req, resCalculatorTicket, plateNumber, plateTypeCode, rfidSerial);
            vehicleEntity = saveVehicle(authentication, req, resCalculatorTicket.getVehicle_type(), rfidSerial, plateTypeCode, plateNumber);
        }
        Long subscriptionTicketId = addOfferToOCS(authentication, resCalculatorTicket, req, vehicleEntity);
        Long endProcessTime = System.currentTimeMillis() - startProcessTime;
        return ResChargeTicketDTO.builder()
                .subscription_ticket_id(subscriptionTicketId)
                .price_amount(resCalculatorTicket.getPrice_amount())
                .status(String.valueOf(1))
                .station_type(req.getStation_type())
                .station_in_id(req.getStation_in_id())
                .station_out_id(req.getStation_out_id())
                .ticket_type(req.getTicket_type())
                .start_date(req.getStart_date())
                .end_date(req.getEnd_date())
                .plate(req.getPlate())
                .etag(req.getEtag())
                .vehicle_type(req.getVehicle_type())
                .register_vehicle_type(req.getRegister_vehicle_type())
                .seat(req.getSeat())
                .weight_goods(req.getWeight_goods())
                .weight_all(req.getWeight_all())
                .request_id(req.getRequest_id())
                .response_datetime(endProcessTime)
                .build();
    }

    /**
     * Huy ve
     *
     * @param reqCancelTicketDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResCancelTicketDTO cancelTicket(Authentication authentication, ReqCancelTicketDTO reqCancelTicketDTO) {
        Long startProcessTime = System.currentTimeMillis();
        ResCancelTicketDTO resCancelTicketDTO = new ResCancelTicketDTO();
        resCancelTicketDTO.setRequest_type(reqCancelTicketDTO.getRequest_type());
        reqCancelTicketDTO.setSubscription_ticket_id(reqCancelTicketDTO.getSubscription_ticket_id());
        String userLogin = FnCommon.getUserLogin(authentication);

        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailRepositoryJPA.findBySubscriptionTicketId(reqCancelTicketDTO.getSubscription_ticket_id());
        SaleTransEntity saleTransEntity = saleTransRepositoryJPA.findBySaleTransId(saleTransDetailEntity.getSaleTransId());
        VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(saleTransDetailEntity.getVehicleId());

        SaleTransDelBoo1Entity saleTransDelBoo1EntityOld = saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(reqCancelTicketDTO.getSubscription_ticket_id());
        Long effDate = saleTransDetailEntity.getEffDate().getTime();
        Long expDate = saleTransDetailEntity.getExpDate().getTime();
        Long currentDate = System.currentTimeMillis();
        if (currentDate >= effDate && currentDate <= expDate) {
            Long diffDaysExpDateAndEffDate = FnCommon.diffDays(expDate, effDate);
            if (diffDaysExpDateAndEffDate <= 30) {
                TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), new Date(currentDate));
                if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                    throw new BooException("boo.vehicle.transaction.histories.exist");
                }
            } else {
                Long diffDaysEffDateAndCurrentDate = FnCommon.diffDays(currentDate, effDate);
                if (diffDaysEffDateAndCurrentDate <= 30) {
                    TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), new Date(currentDate));
                    if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                        throw new BooException("boo.vehicle.transaction.histories.exist");
                    }
                } else {
                    if (diffDaysEffDateAndCurrentDate <= 60) {
                        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29));
                        if (transactionsHistoryVehicleDTO.getData().getListData().size() > 0) {
                            throw new BooException("boo.vehicle.transaction.histories.exist");
                        }
                        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO1 = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 58));
                        if (transactionsHistoryVehicleDTO1.getData().getListData().size() > 0) {
                            throw new BooException("boo.vehicle.transaction.histories.exist");
                        }

                    } else {
                        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO2 = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, saleTransDetailEntity.getEffDate(), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29));
                        if (transactionsHistoryVehicleDTO2.getData().getListData().size() > 0) {
                            throw new BooException("boo.vehicle.transaction.histories.exist");
                        }
                        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDTO3 = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, FnCommon.addDays(saleTransDetailEntity.getEffDate(), 29), FnCommon.addDays(saleTransDetailEntity.getEffDate(), 58));
                        if (transactionsHistoryVehicleDTO3.getData().getListData().size() > 0) {
                            throw new BooException("boo.vehicle.transaction.histories.exist");
                        }
                        TransactionsHistoryVehicleDTO transactionsHistoryVehicleDT4 = getTransactionsVehicle(authentication, saleTransDetailEntity.getPlateNumber(), reqCancelTicketDTO.getTicket_type(), vehicleEntity, saleTransDetailEntity, FnCommon.addDays(saleTransDetailEntity.getEffDate(), 58), new Date(currentDate));
                        if (transactionsHistoryVehicleDT4.getData().getListData().size() > 0) {
                            throw new BooException("boo.vehicle.transaction.histories.exist");
                        }
                    }
                }

            }
        }
        Long requestId = reqCancelTicketDTO.getRequest_id();

        validateRequestCancelTicket(requestId, reqCancelTicketDTO, saleTransDetailEntity, saleTransDelBoo1EntityOld);
        if (!saleTransDetailEntity.getExpDate().before(new Date())) {
            resCancelTicketDTO.setStatus(Constants.BOO_STATUS.REJECT);
        } else {
            throw new BooException("crm.boo.not-found.ticket");
        }
        if (Constants.BOT_REFUND.NO.equals(reqCancelTicketDTO.getRequest_type())) {
            OCSResponse ocsResponse = ocsService.deleteSupOffer(authentication, Constants.ACT_TYPE.DESTROY_TICKET, BOO_INFO.CONTRACT_ID, saleTransDetailEntity.getEpc(), saleTransDetailEntity.getOcsCode(), saleTransDetailEntity.getOfferLevel());
            if (!FnCommon.checkOcsCode(ocsResponse)) {
                resCancelTicketDTO.setStatus(Constants.BOO_STATUS.REJECT);
            } else {
                long reasonId = getReasonIdByactTypeId(Constants.ACT_TYPE.DESTROY_TICKET);
                saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.CANCEL.value);
                saleTransDetailEntity.setDestroy_user(userLogin);
                saleTransDetailEntity.setDestroy_date(new java.sql.Date(System.currentTimeMillis()));
                saleTransDetailEntity.setRefund_type(Constants.BOT_REFUND.NO);
                saleTransDetailEntity.setAct_type_id(Constants.ACT_TYPE.DESTROY_TICKET);
                saleTransDetailEntity.setAct_reason_id(reasonId);
                resCancelTicketDTO.setStatus(Constants.BOO_STATUS.SUCCESS);
                saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
            }

        } else if (Constants.BOT_REFUND.YES.equals(reqCancelTicketDTO.getRequest_type())) {
            resCancelTicketDTO.setStatus(Constants.BOO_STATUS.RECEIVED);
            SaleTransDelBoo1Entity saleTransDelBoo1Entity = new SaleTransDelBoo1Entity();
            Long endProcessTime = System.currentTimeMillis();
            Long processTime = endProcessTime - startProcessTime;
            setValueSaleTransDetailToSaleTransBOO(saleTransDetailEntity,
                    saleTransDelBoo1Entity, reqCancelTicketDTO, processTime, saleTransEntity, userLogin);
            saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
        }

        Long endProcessTime = System.currentTimeMillis();
        Long processTime = endProcessTime - startProcessTime;
        resCancelTicketDTO.setSubscription_ticket_id(reqCancelTicketDTO.getSubscription_ticket_id());
        resCancelTicketDTO.setProcess_datetime(processTime);
        resCancelTicketDTO.setResponse_datetime(endProcessTime);
        resCancelTicketDTO.setRequest_id(reqCancelTicketDTO.getRequest_id());
        return resCancelTicketDTO;
    }

    /**
     * Dong bo online giua 2 BOO
     *
     * @param reqOnlineEventRegDTO Du lieu can cap nhat
     * @return Ket qua tra ve
     */
    @Override
    public ResOnlineEventDTO onlineEventReg(ReqOnlineEventRegDTO reqOnlineEventRegDTO, Authentication authentication) {
        Long startProcessTime = System.currentTimeMillis();
        List<ExceptionListEntity> exceptionListEntities = null;
        validateInputReg(reqOnlineEventRegDTO);
        Long stageId = null;
        String plateNumberFormat = FnCommon.formatPlateBOO1(reqOnlineEventRegDTO.getPlate());
        String token = FnCommon.getStringToken(authentication);
        String mappingExceptionType = FnCommon.mappingExceptionListType(reqOnlineEventRegDTO.getType());
        if (reqOnlineEventRegDTO.getStation_in_id().equals(reqOnlineEventRegDTO.getStation_out_id())) {
            LinkedTreeMap<?, ?> stations = stationService.findById(token, reqOnlineEventRegDTO.getStation_in_id());
            if (!Objects.isNull(stations) && "BOO1".equals(stations.get("booCode"))) {
                exceptionListEntities = exceptionListServiceJPA.findByPlateNumberAndEpcAndStationIdAndExceptionType(plateNumberFormat, reqOnlineEventRegDTO.getEtag(), reqOnlineEventRegDTO.getStation_in_id(), mappingExceptionType);
            } else {
                throw new BooException("crm.boo.exception.station.boo1");
            }

        } else {
            LinkedHashMap<?, ?> stages = stageService.findByStationInAndStationOut(reqOnlineEventRegDTO.getStation_in_id(), reqOnlineEventRegDTO.getStation_out_id(), token);
            if (Objects.isNull(stages) || "BOO2".equals(stages.get("booCode"))) {
                throw new BooException("crm.boo.exception.station.boo1");
            }
            if ("BOO1".equals(stages.get("booCode")) && !FnCommon.isNullOrEmpty(stages.get("id").toString())) {
                exceptionListEntities = exceptionListServiceJPA.findByPlateNumberAndEpcAndStageIdAndExceptionType(plateNumberFormat, reqOnlineEventRegDTO.getEtag(), Long.parseLong(stages.get("id").toString()), mappingExceptionType);
                stageId = Long.parseLong(stages.get("id").toString());
            }
        }
        if (Constants.REASON_CHANGE_BOO.INSERT_EXCEPTION.equals(reqOnlineEventRegDTO.getAction_type().toUpperCase())) {
            if (!Objects.isNull(exceptionListEntities) && !exceptionListEntities.isEmpty()) {
                throw new BooException("crm.boo.exception.exist");
            }
            DataBooDTO dataBooDTO = booRepository.findByPlateNumberAndEpc(FnCommon.formatPlateBOO1(reqOnlineEventRegDTO.getPlate()), reqOnlineEventRegDTO.getEtag());
            if (dataBooDTO != null) {
                ExceptionListEntity exceptionListEntity = new ExceptionListEntity().toInsertExceptionListEntity(reqOnlineEventRegDTO, dataBooDTO, authentication, stageId);
                exceptionListServiceJPA.save(exceptionListEntity);
            }
        } else if (Constants.REASON_CHANGE_BOO.UPDATE_EXCEPTION.equals(reqOnlineEventRegDTO.getAction_type().toUpperCase())) {
            if (Objects.isNull(exceptionListEntities) || exceptionListEntities.isEmpty()) {
                throw new BooException("crm.boo.exception.not.exist");
            }
            new ExceptionListEntity().toUpdateExceptionListEntity(reqOnlineEventRegDTO, exceptionListEntities, stageId);
            exceptionListServiceJPA.saveAll(exceptionListEntities);
        } else {
            throw new BooException("crm.boo.action-type");
        }
        Long endProcessTime = System.currentTimeMillis();
        Long processTime = endProcessTime - startProcessTime;
        ResOnlineEventDTO res = new ResOnlineEventDTO(processTime, endProcessTime, reqOnlineEventRegDTO.getRequest_id(), "0");
        writeLog(FnCommon.toStringJson(reqOnlineEventRegDTO), FnCommon.toStringJson(res), "api/v1/boo/online-event/reg",
                processTime, authentication, "POST", "1", ACT_TYPE.BOO1_SYNC);
        return res;
    }


    /**
     * Dong bo online giua 2 BOO
     *
     * @param reqOnlineEventSyncDTO Du lieu can cap nhat
     * @return Ket qua tra ve
     */
    @Override
    public ResOnlineEventDTO onlineEventSync(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO, Authentication authentication) throws Exception {
        Long startProcessTime = System.currentTimeMillis();
        boolean result = updateDataSync(reqOnlineEventSyncDTO, authentication);
        Long endProcessTime = System.currentTimeMillis();
        Long processTime = endProcessTime - startProcessTime;
        ResOnlineEventDTO res;
        if (result) {
            res = new ResOnlineEventDTO(processTime, endProcessTime, reqOnlineEventSyncDTO.getRequest_id(), Constants.BOO_ERROR_CODE.SUCCESS);
        } else {
            res = new ResOnlineEventDTO(processTime, endProcessTime, reqOnlineEventSyncDTO.getRequest_id(), Constants.BOO_ERROR_CODE.FAIL);
        }
        writeLog(FnCommon.toStringJson(reqOnlineEventSyncDTO), FnCommon.toStringJson(res), "api/v1/boo/online-event/sync",
                processTime, authentication, "POST", res.getResponse_code(), ACT_TYPE.BOO1_SYNC);
        return res;
    }

    /**
     * Ho tro kich hoat etag
     *
     * @param authentication
     * @param req
     * @return
     */
    @Override
    public ResActivationCheckDTO checkActivation(Authentication authentication, ReqActivationCheckDTO req) {
        Long startTime = System.currentTimeMillis();
        validatePlateNumber(req.getPlate());
        String plateNumber = FnCommon.formatPlateBOO1(req.getPlate());
        ResActivationCheckDTO res = new ResActivationCheckDTO();
        res.setRequest_id(req.getRequest_id());
        res.setPlate(req.getPlate());
        res.setStatus(BOO_STATUS.NOT_REGISTRY);
        List<VehicleEntity> vehicleEntityList = vehicleRepositoryJPA.findAllByPlateNumberNotBOO1(plateNumber);
        if (vehicleEntityList.size() >= SIZE_LIST_ZERO) {
            for (VehicleEntity vehicleEntity : vehicleEntityList) {
                if (vehicleEntity.getStatus().equals(VehicleEntity.Status.ACTIVATED.value)) {
                    res.setStatus(BOO_STATUS.ACTIVE);
                }
                if (vehicleEntity.getStatus().equals(VehicleEntity.Status.ACTIVATED.value) && vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.NOT_ACTIVATED.value)) {
                    res.setStatus(BOO_STATUS.NOT_REGISTRY);
                }
                if (vehicleEntity.getStatus().equals(VehicleEntity.Status.NOT_ACTIVATED.value) && vehicleEntity.getActiveStatus().equals(VehicleEntity.ActiveStatus.CANCEL.value)) {
                    res.setStatus(BOO_STATUS.DESTROY);
                }
                res.setEtag(vehicleEntity.getEpc());
                res.setVehicle_type(vehicleEntity.getVehicleGroupId());
                res.setRegister_vehicle_type(vehicleEntity.getVehicleTypeId().toString());
                res.setSeat(vehicleEntity.getSeatNumber());
                res.setWeight_goods(vehicleEntity.getCargoWeight());
                res.setWeight_all(vehicleEntity.getNetWeight());
            }
        }
        res.setResponse_code(BOO_STATUS.RESPONSE_CODE_SUCCESS);
        res.setResponse_datetime(System.currentTimeMillis());
        Long endTime = System.currentTimeMillis();
        res.setProcess_datetime(endTime - startTime);
        return res;
    }

    /**
     * Check du lieu dau vao
     *
     * @param requestOnlineEventRegBooDTO Du lieu dau vao
     */
    private void validateInputReg(ReqOnlineEventRegDTO requestOnlineEventRegBooDTO) {
        if (Constants.VALID_TYPE_ONLINE_REG.TYPE.stream().noneMatch(requestOnlineEventRegBooDTO.getType().toUpperCase()::equals)) {
            throw new BooException("crm.boo.wrong.online.reg.type");
        } else {
            switch (requestOnlineEventRegBooDTO.getType()) {
                case Constants.VALID_TYPE_ONLINE_REG.SERVICE_PLAN:
                    validateServicePlan(requestOnlineEventRegBooDTO);
                    break;
                case Constants.VALID_TYPE_ONLINE_REG.VEHICLE_TYPE:
                    validateVehicleType(requestOnlineEventRegBooDTO);
                    break;
                default:
                    break;
            }
        }
        if (!FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getEtag())) {
            if (requestOnlineEventRegBooDTO.getEtag().length() > 50) {
                throw new BooException("crm.boo.wrong.online.reg.epc.length");
            }
        } else {
            throw new BooException("crm.boo.wrong.online.reg.epc");
        }
        if (FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getAction_type()) ||
                Constants.VALID_TYPE_ONLINE_REG.ACTION_TYPE.stream().noneMatch(requestOnlineEventRegBooDTO.getAction_type().toUpperCase()::equals)) {
            throw new BooException("crm.boo.wrong.online.reg.action_type");
        }

        if (FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getPlate())) {
            throw new BooException("crm.boo.wrong.online.reg.plate_number");
        } else if (!FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getPlate()) &&
                !FnCommon.validatePlateContainsTVX(requestOnlineEventRegBooDTO.getPlate())) {
            throw new BooException("boo.input-reg.params.plate-number.format");
        }
        if (!FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getStart_date())) {
            Date startDate = FnCommon.convertStringToDate(requestOnlineEventRegBooDTO.getStart_date(), Constants.COMMON_DATE_FORMAT_BOO_24H);
            if (Objects.isNull(startDate)) {
                throw new BooException("crm.boo.wrong.online.reg.start_date.format");
            }
        } else {
            throw new BooException("crm.boo.wrong.online.reg.start_date.required");
        }
        if (Objects.isNull(requestOnlineEventRegBooDTO.getStation_in_id())) {
            throw new BooException("crm.boo.wrong.online.reg.station_id");
        }
        if (!FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getEnd_date())) {
            Date endDate = FnCommon.convertStringToDate(requestOnlineEventRegBooDTO.getEnd_date(), Constants.COMMON_DATE_FORMAT_BOO_24H);
            if (!Objects.isNull(endDate)) {
                if (!FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getStart_date())) {
                    Date startDate = FnCommon.convertStringToDate(requestOnlineEventRegBooDTO.getStart_date(), Constants.COMMON_DATE_FORMAT_BOO_24H);
                    if (endDate.compareTo(startDate) <= 0 || !FnCommon.compareDate(endDate)) {
                        throw new BooException("crm.boo.wrong.online.reg.start_date.compare");
                    }
                } else {
                    throw new BooException("crm.boo.wrong.online.reg.end_date.format");
                }
            }
        }
    }

    /**
     * Kiem tra du lieu khi dong bo gia ve
     *
     * @param requestOnlineEventRegBooDTO
     */
    public void validateServicePlan(ReqOnlineEventRegDTO requestOnlineEventRegBooDTO) {
        if (Objects.isNull(requestOnlineEventRegBooDTO.getPrice_turn()) && Objects.isNull(requestOnlineEventRegBooDTO.getPrice_monthly()) &&
                Objects.isNull(requestOnlineEventRegBooDTO.getPrice_quarterly())) {
            throw new BooException("crm.boo.online.reg.service_plan");
        }
    }

    /**
     * Kiem tra du lieu loai phuong tien
     *
     * @param requestOnlineEventRegBooDTO
     */
    public void validateVehicleType(ReqOnlineEventRegDTO requestOnlineEventRegBooDTO) {
        if (FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getException_vehicle_type()) || FnCommon.isNullOrEmpty(requestOnlineEventRegBooDTO.getVehicle_type())) {
            throw new BooException("crm.boo.online.reg.vehicle.type");
        }
    }

    /**
     * Kiem tra du lieu dau vao
     *
     * @param req du lieu dau vao can kiem tra
     * @return Ket qua tra ve dung hay sai
     */
    public boolean updateDataSync(ReqOnlineEventSyncDTO req,
                                  Authentication authentication) throws Exception {
        validateDataSync(req);
        List<SaleTransDetailEntity> listSaleTransDetail = saleTransDetailRepositoryJPA.findOrgPlateTypeNumberAndEpcAndBooFlow(req.getPlate_old(), req.getEtag_old(), BOO1ChargeBOO2);
        List<ExceptionListEntity> exceptionListEntity = exceptionListServiceJPA.findExceptionListStillEffectiveDate(req.getEtag_old(), FnCommon.formatPlateBOO1(req.getPlate_old()));
        return handleReason(req, authentication, exceptionListEntity, listSaleTransDetail);
    }

    /**
     * Kiem tra du lieu dong bo
     *
     * @param requestOnlineEventSyncBooDTO
     */
    public void validateDataSync(ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO) {
        if (FnCommon.isNullOrEmpty(requestOnlineEventSyncBooDTO.getReason())) {
            throw new BooException("crm.boo.reason.empty");
        }
        if (FnCommon.isNullOrEmpty(requestOnlineEventSyncBooDTO.getEtag_old())) {
            throw new BooException("crm.boo.old_epc.required");
        }
    }

    /**
     * Kiem tra ly do thay doi du lieu online sync
     *
     * @param req Du lieu dau vao can kiem tra
     *            Thong tin danh sach ngoai le
     * @return Tra ve danh sach ngoai le
     */
    private boolean handleReason(ReqOnlineEventSyncDTO req, Authentication authentication, List<ExceptionListEntity> listExceptions, List<SaleTransDetailEntity> listSaleTransDetails) throws Exception {
        if (REASON_CHANGE_BOO.CHANGE_PLATE_NUMBER.equals(req.getReason())) {
            return changePlateNumber(req, authentication, listExceptions, listSaleTransDetails);
        } else if (REASON_CHANGE_BOO.CHANGE_EPC.equals(req.getReason())) {
            return changeEpc(req, authentication, listExceptions, listSaleTransDetails);
        } else if (REASON_CHANGE_BOO.OPEN_OR_CLOSE_EPC.equals(req.getReason())) {
            return openAndCloseEpc(req, authentication);
        } else if (REASON_CHANGE_BOO.CHANGE_OTHER_INFO.equals(req.getReason())) {
            return changeOtherInfo(req, authentication, listExceptions, listSaleTransDetails);
        } else if (REASON_CHANGE_BOO.CHANGE_INFO_REGISTER.equals(req.getReason())) {
            return changeInfoRegister(req, authentication, listExceptions, listSaleTransDetails);
        } else if (REASON_CHANGE_BOO.CANCEL_EPC.equals(req.getReason())) {
            return cancelEpc(req, authentication);
        } else {
            return false;
        }
    }

    /**
     * Thay doi thong tin dang ki
     *
     * @param req
     * @return
     */
    public boolean changeInfoRegister(ReqOnlineEventSyncDTO req, Authentication authentication, List<ExceptionListEntity> listExceptions, List<SaleTransDetailEntity> listSaleTransDetails) {
        String plateNumberOldFormat = FnCommon.formatPlateBOO1(req.getPlate_old());
        boolean result = false;
        validateChangeInfoRegister(req);
        List<VehicleEntity> vehicleEntitiesOld = vehicleServiceJPA.findByEpcAndPlateNumberAndActiveStatusAndStatusAndContractId(
                req.getEtag_old(), plateNumberOldFormat, Constants.CATEGORY_STATUS.ACTIVE,
                Constants.CATEGORY_STATUS.ACTIVE, Constants.BOO_INFO.CONTRACT_ID);
        if (!vehicleEntitiesOld.isEmpty()) {
            UpdateVehicleRequestOCSDTO reqOcs = new UpdateVehicleRequestOCSDTO();
            reqOcs.setEpc(req.getEtag_old());
            reqOcs.setVehicleType(Long.parseLong(req.getRegister_vehicle_type_new()));
            reqOcs.setActTypeId(ACT_TYPE.MODIFY_VEHICLE);
            result = modifyVehicleOcs(reqOcs, authentication);
            if (result) {
                for (VehicleEntity vehicleEntity : vehicleEntitiesOld) {
                    vehicleEntity.setVehicleTypeCode(req.getRegister_vehicle_type_new());
                    vehicleEntity.setVehicleTypeId(Long.parseLong(req.getRegister_vehicle_type_new()));
                }
                vehicleRepositoryJPA.saveAll(vehicleEntitiesOld);
            }
        } else {
            LOGGER.error(String.format("Khong ton tai phuong tien nay voi epc = %s,plate number= %s,active status = %s,status = %s,contractId = %s", req.getEtag_old(), plateNumberOldFormat, Constants.CATEGORY_STATUS.ACTIVE,
                    Constants.CATEGORY_STATUS.ACTIVE, 1L));
        }
        return result;
    }

    /**
     * Check du lieu khi thay doi thong tin dang ki
     *
     * @param req
     */
    public void validateChangeInfoRegister(ReqOnlineEventSyncDTO req) {
        if (FnCommon.isNullOrEmpty(req.getRegister_vehicle_type_new()) || FnCommon.isNullOrEmpty(req.getRegister_vehicle_type_old())) {
            throw new BooException("boo.info.register.params.vehicle-type");
        }
        if (FnCommon.isNullOrEmpty(req.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate");
        } else if (!FnCommon.isNullOrEmpty(req.getPlate_old()) && !FnCommon.validatePlateContainsTVX(req.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate.format");
        }
        if (FnCommon.isNullOrEmpty(req.getEtag_old())) {
            throw new BooException("boo.info.register.params.etag");
        }
    }

    /**
     * Doi bien so
     *
     * @param requestOnlineEventSyncBooDTO
     * @return
     */
    @Transactional(rollbackFor = BooException.class, propagation = Propagation.REQUIRES_NEW)
    public boolean changePlateNumber(ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO,
                                     Authentication authentication, List<ExceptionListEntity> exceptionListEntities, List<SaleTransDetailEntity> saleTransDetailEntities) {
        boolean result = false;
        validateChangePlateNumber(requestOnlineEventSyncBooDTO);
        if (!checkPlateNumber(requestOnlineEventSyncBooDTO.getPlate_new(), requestOnlineEventSyncBooDTO.getPlate_old())) {
            String plateNumberOldFormat = FnCommon.formatPlateBOO1(requestOnlineEventSyncBooDTO.getPlate_old());
            String plateNumberNewFormat = FnCommon.formatPlateBOO1(requestOnlineEventSyncBooDTO.getPlate_new());
            if (!saleTransDetailEntities.isEmpty()) {
                UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO();
                updateVehicleRequestOCSDTO.setEpc(requestOnlineEventSyncBooDTO.getEtag_old());
                updateVehicleRequestOCSDTO.setPlateNumber(plateNumberNewFormat);
                Optional<VehicleEntity> vehicleEntity = vehicleRepositoryJPA.findById(saleTransDetailEntities.get(0).getVehicleId());
                if (vehicleEntity.isPresent()) {
                    updateVehicleRequestOCSDTO.setPlateTypeId(vehicleEntity.get().getPlateType());
                    vehicleEntity.get().setPlateNumber(FnCommon.formatPlateBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                    vehicleEntity.get().setPlateTypeCode(FnCommon.getPlateTypeBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                } else {
                    LOGGER.error(String.format("Khong ton tai phuong tien voi id = %s ", saleTransDetailEntities.get(0).getVehicleId()));
                }

                updateVehicleRequestOCSDTO.setActTypeId(ACT_TYPE.MODIFY_VEHICLE);
                OCSResponse response = ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, authentication, false);
                if (checkOcsCode(response)) {
                    for (SaleTransDetailEntity saleTransDetailEntity : saleTransDetailEntities) {
                        saleTransDetailEntity.setPlateNumber(plateNumberNewFormat);
                        saleTransDetailEntity.setOrgPlateNumber(requestOnlineEventSyncBooDTO.getPlate_new());
                    }
                    saleTransDetailServiceJPA.saveAll(saleTransDetailEntities);
                    if (!exceptionListEntities.isEmpty()) {
                        for (ExceptionListEntity exceptionListEntity : exceptionListEntities) {
                            exceptionListEntity.setPlateNumber(FnCommon.formatPlateBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                            exceptionListEntity.setPlateTypeCode(FnCommon.getPlateTypeBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                        }
                        exceptionListServiceJPA.saveAll(exceptionListEntities);
                    }
                    vehicleRepositoryJPA.save(vehicleEntity.get());
                    result = true;
                } else {
                    LOGGER.error(String.format("Cap nhat phuong tien ocs that bai : %s", response));
                    throw new BooException("crm.boo.plate_number.change.fail");
                }

            } else {
                UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO();
                updateVehicleRequestOCSDTO.setEpc(requestOnlineEventSyncBooDTO.getEtag_old());
                updateVehicleRequestOCSDTO.setPlateNumber(plateNumberNewFormat);
                Optional<ContractEntity> contractEntity = contractServiceJPA.findById(Constants.BOO_INFO.CONTRACT_ID);
                if (contractEntity.isPresent()) {
                    List<VehicleEntity> vehicleEntitiesOld = vehicleServiceJPA.findByEpcAndPlateNumberAndActiveStatusAndStatusAndContractId(
                            requestOnlineEventSyncBooDTO.getEtag_old(), plateNumberOldFormat, Constants.CATEGORY_STATUS.ACTIVE,
                            Constants.CATEGORY_STATUS.ACTIVE, contractEntity.get().getContractId());
                    if (!vehicleEntitiesOld.isEmpty()) {
                        updateVehicleRequestOCSDTO.setPlateTypeId(vehicleEntitiesOld.get(0).getPlateType());
                        updateVehicleRequestOCSDTO.setActTypeId(ACT_TYPE.MODIFY_VEHICLE);
                        OCSResponse response = ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, authentication, false);
                        if (checkOcsCode(response)) {
                            if (!exceptionListEntities.isEmpty()) {
                                for (ExceptionListEntity exceptionListEntity : exceptionListEntities) {
                                    exceptionListEntity.setPlateNumber(FnCommon.formatPlateBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                                    exceptionListEntity.setPlateTypeCode(FnCommon.getPlateTypeBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                                }
                                exceptionListServiceJPA.saveAll(exceptionListEntities);
                            }
                            VehicleEntity vehicleEntity = vehicleEntitiesOld.get(0);
                            vehicleEntity.setPlateNumber(FnCommon.formatPlateBOO1(requestOnlineEventSyncBooDTO.getPlate_new()));
                            vehicleRepositoryJPA.save(vehicleEntity);
                            result = true;
                        } else {
                            LOGGER.error(String.format("Cap nhat phuong tien ocs that bai : %s", response));
                        }
                    } else {
                        LOGGER.error(String.format("Khong ton tai phuong tien nay voi epc = %s,plate number= %s,active status = 1,status = 1,contractId = 1", requestOnlineEventSyncBooDTO.getEtag_old(), plateNumberOldFormat));
                    }
                }
            }

        }
        return result;
    }

    /**
     * Kiem tra du lieu khi thay doi bien so xe
     *
     * @param reqOnlineEventSyncDTO
     */
    public void validateChangePlateNumber(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO) {
        if ((!FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old())
                && !FnCommon.validatePlateContainsTVX(reqOnlineEventSyncDTO.getPlate_old())) ||
                (!FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_new()) && !FnCommon.validatePlateContainsTVX(reqOnlineEventSyncDTO.getPlate_new()))) {
            throw new BooException("boo.info.register.params.plate.format");
        } else if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old()) || FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_new())) {
            throw new BooException("boo.info.register.params.plate");
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_old())) {
            throw new BooException("boo.info.register.params.etag");
        }
    }

    /**
     * Thay doi epc
     *
     * @param req
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BooException.class)
    public boolean changeEpc(ReqOnlineEventSyncDTO req, Authentication authentication, List<ExceptionListEntity> listExceptions, List<SaleTransDetailEntity> listSaleTransDetails) throws Exception {
        validateChangeEpc(req);
        boolean result = false;
        if (!req.getEtag_new().equals(req.getEtag_old())) {
            String plateNumberOldFormat = FnCommon.formatPlateBOO1(req.getPlate_old());
            OCSResponse resDelVehicle = ocsService.deleteVehicle(req.getEtag_old(), BOO_INFO.CONTRACT_ID.toString(), authentication, ACT_TYPE.MODIFY_VEHICLE.intValue());
            if (checkOcsCode(resDelVehicle)) {
                List<VehicleEntity> listVehicle = vehicleServiceJPA.findByEpcAndPlateNumberAndActiveStatusAndStatusAndContractId(req.getEtag_old(), plateNumberOldFormat, VehicleEntity.ActiveStatus.ACTIVATED.value, VehicleEntity.Status.ACTIVATED.value, BOO_INFO.CONTRACT_ID);
                if (!listVehicle.isEmpty()) {
                    VehicleEntity vehicleEntity = listVehicle.get(0);
                    vehicleEntity.setEpc(req.getEtag_new());
                    AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO().entityToAddVehicleRequestDTO(vehicleEntity, BOO_INFO.CONTRACT_ID);
                    OCSResponse resAddVehicle = ocsService.createVehicleOCS(addVehicleRequestDTO, authentication, ACT_TYPE.ADD_VEHICLE.intValue());
                    if (checkOcsCode(resAddVehicle)) {
                        vehicleRepositoryJPA.save(vehicleEntity);
                        result = true;

                        // Cap nhat list sale trans details va add offer
                        if (!listSaleTransDetails.isEmpty()) {
                            result = vehicleService.addTicketWhenSwapRFID(authentication, BOO_INFO.CONTRACT_ID, req.getEtag_new(), listSaleTransDetails);
                        }

                        // Cap nhat list exception list va add offer
                        if (!listExceptions.isEmpty()) {
                            result = vehicleService.addOfferExceptionListWhenSwapRFID(authentication, BOO_INFO.CONTRACT_ID, req.getEtag_new(), listExceptions);
                        }
                    } else {
                        LOGGER.error(String.format("Them moi phuong tien ocs that bai : %s", resAddVehicle));
                    }
                } else {
                    LOGGER.error(String.format("Khong ton tai phuong tien nay voi epc = %s,plate number= %s,active status = 1,status = 1,contractId = 1", req.getEtag_old(), plateNumberOldFormat));
                }
            } else {
                LOGGER.error(String.format("Xoa phuong tien ocs that bai : %s", resDelVehicle));
            }
        } else {
            throw new BooException("crm.boo.epc.same");
        }
        return result;
    }

    /**
     * kiem tra du lieu khi thay doi the epc(etag)
     *
     * @param reqOnlineEventSyncDTO
     */
    public void validateChangeEpc(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO) {
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate");
        } else if (!FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old()) && !FnCommon.validatePlateContainsTVX(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate.format");
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_old()) || FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_new())) {
            throw new BooException("boo.info.register.params.etag");
        }
    }


    /**
     * Dong mo the etag
     *
     * @param req Du lieu dong bo
     * @return boolean
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BooException.class)
    public boolean openAndCloseEpc(ReqOnlineEventSyncDTO req, Authentication authentication) {
        boolean result = false;
        validateOpenAndCloseEpc(req);
        Optional<VehicleEntity> vehicleEntity = vehicleServiceJPA.findByPlateNumberAndActiveStatusNot(FnCommon.formatPlateBOO1(req.getPlate_old()), VehicleEntity.ActiveStatus.CANCEL.value);
        if (vehicleEntity.isPresent()) {
            mapActiveStatus(req, vehicleEntity.get());
            UpdateVehicleRequestOCSDTO reqOcs = new UpdateVehicleRequestOCSDTO();
            reqOcs.setEpc(req.getEtag_old());
            reqOcs.setStatus(req.getEtag_status_new());
            reqOcs.setActTypeId(ACT_TYPE.MODIFY_VEHICLE);
            OCSResponse ocsResponse = ocsService.modifyVehicleOCS(reqOcs, authentication, true);
            if (checkOcsCode(ocsResponse)) {
                vehicleServiceJPA.save(vehicleEntity.get());
                result = true;
            } else {
                LOGGER.error(String.format("Cap nhat phuong tien ocs that bai : %s", ocsResponse));
                throw new BooException("boo.open_and_close.epc.fail");
            }
        } else {
            LOGGER.error(String.format("Khong tim thay phuong tien voi plate number = %s,active status = %s", FnCommon.formatPlateBOO1(req.getPlate_old()), VehicleEntity.ActiveStatus.CANCEL.value));
        }
        return result;
    }

    /**
     * Chuyen doi gia tri voi trang thai request gui sang
     *
     * @param requestOnlineEventSyncBooDTO
     * @return
     */
    public void mapActiveStatus(ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO, VehicleEntity vehicleEntity) {
        if (!FnCommon.isNullOrEmpty(requestOnlineEventSyncBooDTO.getEtag_status_new())) {
            switch (requestOnlineEventSyncBooDTO.getEtag_status_new()) {
                case "1":
                    vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
                    break;
                case "2":
                    vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.CLOSED.value);
                    break;
                default:
                    throw new BooException("boo.open_and_close.epc.status.fail");
            }
        }
    }

    /**
     * Chuyen doi gia tri voi trang thai request gui sang
     *
     * @param requestOnlineEventSyncBooDTO
     * @return
     */
    public void mapStatusAndActiveStatus(ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO, VehicleEntity vehicleEntity) {
        if (!FnCommon.isNullOrEmpty(requestOnlineEventSyncBooDTO.getEtag_status_new())) {
            if ("0".equals(requestOnlineEventSyncBooDTO.getEtag_status_new())) {
                vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.CANCEL.value);
                vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
            } else {
                throw new BooException("boo.open_and_close.epc.status.fail");
            }
        }
    }

    /**
     * Kiem tra du lieu khi dong mo etag
     *
     * @param reqOnlineEventSyncDTO
     */
    public void validateOpenAndCloseEpc(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO) {
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_old())) {
            throw new BooException("boo.info.register.params.etag");
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_status_old()) || FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_status_new())) {
            throw new BooException("boo.open-close.epc.params.etag-status");
        } else {
            if ("0".equals(reqOnlineEventSyncDTO.getEtag_status_old()) ||
                    "0".equals(reqOnlineEventSyncDTO.getEtag_status_new())) {
                throw new BooException("crm.boo.old_epc.locked");
            }
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate");
        } else if (!FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old()) && !FnCommon.validatePlateContainsTVX(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate.format");
        }

    }

    /**
     * Thay doi thong tin dang ki
     *
     * @param req Du lieu dong bo
     * @return boolean
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BooException.class)
    public boolean changeOtherInfo(ReqOnlineEventSyncDTO req, Authentication authentication, List<ExceptionListEntity> listExceptions, List<SaleTransDetailEntity> listSaleTransDetails) {
        boolean result = false;
        validateChangeOtherInfo(req);
        List<VehicleEntity> vehicleEntities = vehicleServiceJPA.findByEpcAndPlateNumberAndActiveStatusAndStatusAndContractId(
                req.getEtag_old(), FnCommon.formatPlateBOO1(req.getPlate_old()), Constants.CATEGORY_STATUS.ACTIVE,
                Constants.CATEGORY_STATUS.ACTIVE, Constants.BOO_INFO.CONTRACT_ID);
        if (!listSaleTransDetails.isEmpty()) {
            if (!vehicleEntities.isEmpty()) {
                String token = FnCommon.getStringToken(authentication);
                AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
                addVehicleRequestDTO.setVehicleTypeId(vehicleEntities.get(0).getVehicleTypeId());
                List<String> vehicleGroupIds = findVehicleGroup(token, addVehicleRequestDTO, req);
                if (!vehicleGroupIds.isEmpty()) {
                    result = handleCallOcsWhenChangeInfoVehicle(vehicleEntities.get(0), vehicleGroupIds, req, authentication, listSaleTransDetails);
                    if (result) {
                        for (SaleTransDetailEntity saleTransDetailEntity : listSaleTransDetails) {
                            saleTransDetailEntity.setVehicleGroupId(Long.valueOf(vehicleGroupIds.get(0)));
                        }
                        saleTransDetailServiceJPA.saveAll(listSaleTransDetails);
                    }
                } else {
                    result = updateVehicleCrmAndOcsWhenChangeOtherInfo(vehicleEntities.get(0), req, authentication, vehicleGroupIds);
                }
            } else {
                LOGGER.error(String.format("Khong ton tai phuong tien nay voi epc = %s,plate number= %s,active status = %s,status = %s,contractId = %s",
                        req.getEtag_old(), FnCommon.formatPlateBOO1(req.getPlate_old()), Constants.CATEGORY_STATUS.ACTIVE,
                        Constants.CATEGORY_STATUS.ACTIVE, 1L));
                throw new BooException("crm.boo.exception_list.vehicle.not_exist");
            }
        } else {
            if (!vehicleEntities.isEmpty()) {
                String token = FnCommon.getStringToken(authentication);
                AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
                addVehicleRequestDTO.setVehicleTypeId(vehicleEntities.get(0).getVehicleTypeId());
                List<String> vehicleGroupIds = findVehicleGroup(token, addVehicleRequestDTO, req);
                if (!vehicleGroupIds.isEmpty() && !listSaleTransDetails.isEmpty()) {
                    for (VehicleEntity vehicleEntity : vehicleEntities) {
                        vehicleEntity.setVehicleGroupId(Long.parseLong(vehicleGroupIds.get(0)));
                    }
                    vehicleRepositoryJPA.saveAll(vehicleEntities);
                }
                result = updateVehicleCrmAndOcsWhenChangeOtherInfo(vehicleEntities.get(0), req, authentication, null);
            }
        }
        return result;
    }

    /**
     * Kiem tra du lieu khi thay doi thong tin khac
     *
     * @param reqOnlineEventSyncDTO
     */
    public void validateChangeOtherInfo(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO) {
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_old())) {
            throw new BooException("boo.info.register.params.etag");
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate");
        } else if (!FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old()) && !FnCommon.validatePlateContainsTVX(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate.format");
        }

    }

    /**
     * Ham goi Ocs khi thay doi thong tin phuong tien
     *
     * @param vehicleEntity                Thong tin phuong tien
     * @param vehicleGroupIds              Danh sach id nhom phuong tien tinh phi
     * @param requestOnlineEventSyncBooDTO Du lieu dong bo
     * @param authentication               Xac thuc
     * @return boolean
     */
    public boolean handleCallOcsWhenChangeInfoVehicle(VehicleEntity vehicleEntity, List<String> vehicleGroupIds,
                                                      ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO,
                                                      Authentication authentication, List<SaleTransDetailEntity> saleTransDetailEntities) {
        boolean result = false;
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO().toEntityModifyVehicle(requestOnlineEventSyncBooDTO);
        boolean isChanged = updateVehicleWhenSyncData(vehicleEntity, requestOnlineEventSyncBooDTO, vehicleGroupIds);
        if (isChanged) {
            Optional<ContractEntity> contractEntity = contractServiceJPA.findById(vehicleEntity.getContractId());
            if (contractEntity.isPresent()) {
                ocsService.deleteSupOffer(authentication, ACT_TYPE.MODIFY_VEHICLE,
                        contractEntity.get().getContractId(), requestOnlineEventSyncBooDTO.getEtag_old(),
                        saleTransDetailEntities.get(0).getOcsCode(), String.valueOf(OFFER_LEVEL_DEFAULT));
                result = modifyVehicleOcs(updateVehicleRequestOCSDTO, authentication);
            } else {
                result = modifyVehicleOcs(updateVehicleRequestOCSDTO, authentication);
            }
        }
        return result;
    }

    /**
     * Goi Ocs thay doi thong tin phuong tien
     *
     * @return
     */
    public boolean modifyVehicleOcs(UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO, Authentication authentication) {
        if (!FnCommon.isNullOrEmpty(updateVehicleRequestOCSDTO.getPlateNumber())) {
            updateVehicleRequestOCSDTO.setPlateNumber(FnCommon.formatPlateBOO1(updateVehicleRequestOCSDTO.getPlateNumber()));
        }
        OCSResponse responseModifyVehicle = ocsService.modifyVehicleOCS(updateVehicleRequestOCSDTO, authentication, true);
        return checkOcsCode(responseModifyVehicle);
    }

    /**
     * Kiem tra bien so co giong nhau
     *
     * @param plateNumberNew Bien so xe moi
     * @param plateNumberOld Bien so xe cu
     * @return boolean
     */
    public boolean checkPlateNumber(String plateNumberNew, String plateNumberOld) {
        if (!Objects.isNull(plateNumberNew) && !Objects.isNull(plateNumberOld)) {
            return plateNumberNew.equals(plateNumberOld);
        }
        return true;

    }

    /**
     * Kiem tra ocs code
     *
     * @param response du lieu phan hoi
     * @return boolean
     */
    public boolean checkOcsCode(OCSResponse response) {
        return response != null && response.getResultCode() != null && "0".equals(response.getResultCode());
    }

    /**
     * TIm kiem loai phuong tien tinh phi
     *
     * @param token                        Ma xac thuc
     * @param addVehicleRequestDTO         Du lieu them moi phuong tien
     * @param requestOnlineEventSyncBooDTO Du lieu dong bo
     * @return Danh sach id nhom phuong tien tinh phi
     */
    public List<String> findVehicleGroup(String token, AddVehicleRequestDTO addVehicleRequestDTO,
                                         ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO) {
        setValueFollowVehicleMappingType(addVehicleRequestDTO, requestOnlineEventSyncBooDTO);
        return vehicleGroupService.getVehicleGroupById(token, addVehicleRequestDTO);
    }

    /**
     * Set gia tri tim kiem theo loai mapping cua phuong tien
     *
     * @param addVehicleRequestDTO         Du lieu them moi phuong tien
     * @param requestOnlineEventSyncBooDTO Du lieu dong bo
     */
    public void setValueFollowVehicleMappingType(AddVehicleRequestDTO addVehicleRequestDTO,
                                                 ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO) {
        addVehicleRequestDTO.setSeatNumber(requestOnlineEventSyncBooDTO.getSeat_old());
        addVehicleRequestDTO.setCargoWeight(requestOnlineEventSyncBooDTO.getWeight_goods_old());
        addVehicleRequestDTO.setNetWeight(requestOnlineEventSyncBooDTO.getWeight_all_old());
        addVehicleRequestDTO.setGrossWeight(requestOnlineEventSyncBooDTO.getWeight_all_old());
        addVehicleRequestDTO.setPullingWeight(requestOnlineEventSyncBooDTO.getWeight_all_old());

    }

    /**
     * Confirm BOT
     *
     * @param authentication
     * @param reqCancelResultDTO
     * @return
     */
    @Override
    public ResCancelResultDTO cancelResult(Authentication authentication, ReqCancelResultDTO reqCancelResultDTO) throws Exception {
        Long startTime = System.currentTimeMillis();
        validateInput(reqCancelResultDTO);
        ResCancelResultDTO resCancelResultDTO = new ResCancelResultDTO();
        resCancelResultDTO.setRequest_id(reqCancelResultDTO.getRequest_id());
        resCancelResultDTO.setResponse_datetime(System.currentTimeMillis());
        SaleTransDelBoo1Entity saleTransDelBoo1Entity = saleTransDelBoo1RepositoryJPA.findBySubscriptionTicketId(reqCancelResultDTO.getSubscription_ticket_id());
        if (saleTransDelBoo1Entity == null) {
            throw new BooException("boo.validate.ticket.not.exists");
        }
        SaleTransDetailEntity saleTransDetailEntity = saleTransDetailRepositoryJPA.findBySubscriptionTicketId(reqCancelResultDTO.getSubscription_ticket_id());
        if (reqCancelResultDTO.getBOT_confirm().equals(Constants.BOT_CONFIRM.NO)) {
            Long endTime = System.currentTimeMillis();
            resCancelResultDTO.setProcess_datetime(endTime - startTime);
            return resCancelResultDTO;
        }
        if (reqCancelResultDTO.getBOT_confirm().equals(Constants.BOT_CONFIRM.YES) && Constants.BOO_STATUS.SUCCESS.equals(reqCancelResultDTO.getStatus())) {
            RemoveSupOfferRoaming removeSupOfferRoaming = new RemoveSupOfferRoaming();
            removeSupOfferRoaming.setEPC(saleTransDelBoo1Entity.getEpc());
            removeSupOfferRoaming.setSubscriptionTicketId(saleTransDelBoo1Entity.getSubscriptionTicketId().toString());
            removeSupOfferRoaming.setIsRefund(reqCancelResultDTO.getBOT_confirm().toString());
            removeSupOfferRoaming.setActTypeId(Constants.ACT_TYPE.DESTROY_TICKET);
            OCSResponse ocsResponse = ocsService.removeSupOfferRoaming(removeSupOfferRoaming, authentication);
            if (checkOcsCode(ocsResponse)) {
                ocsService.addBalance(authentication, Constants.ACT_TYPE.DESTROY_TICKET, saleTransDelBoo1Entity.getContractId(), saleTransDelBoo1Entity.getPrice());
                saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.SUCCESS.value);
                saleTransDelBoo1Entity.setBotConfirmDate(new java.sql.Date(System.currentTimeMillis()));
                saleTransDelBoo1Entity.setRequestType(1L);
                saleTransDelBoo1Entity.setBotStatus(reqCancelResultDTO.getBOT_confirm());
                saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
                saleTransDetailEntity.setDestroy_date(new java.sql.Date(System.currentTimeMillis()));
                saleTransDetailEntity.setDestroy_user(Constants.BOO1);
                saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.CANCEL.value);
                saleTransDetailEntity.setRefund_type(1L);
                saleTransDetailServiceJPA.save(saleTransDetailEntity);
                Long endTime = System.currentTimeMillis();
                resCancelResultDTO.setProcess_datetime(endTime - startTime);
            } else {
                Long endTime = System.currentTimeMillis();
                resCancelResultDTO.setProcess_datetime(endTime - startTime);
            }
        } else {
            saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.REJECT.value);
            saleTransDelBoo1Entity.setBotConfirmDate(new java.sql.Date(System.currentTimeMillis()));
            saleTransDelBoo1RepositoryJPA.save(saleTransDelBoo1Entity);
            Long endTime = System.currentTimeMillis();
            resCancelResultDTO.setProcess_datetime(endTime - startTime);
        }
        LOGGER.info("KET QUA HUY VE : " + reqCancelResultDTO.toString());
        return resCancelResultDTO;
    }

    /***
     * Lay danh sach danh muc mapping boo
     * @param authentication
     * @param reqMappingDTO
     * @return
     */
    @Override
    public List<CategoryMappingBooEntity> getListCategoryMappingBoo(Authentication authentication, ReqMappingDTO reqMappingDTO) {
        ResultSelectEntity result = booRepository.getListCategoryMappingBoo(reqMappingDTO);
        return (List<CategoryMappingBooEntity>) result.getListData();
    }

    /**
     * Dang ki phuong tien cho BOO
     *
     * @param authentication
     * @param reqChargeTicketDTO
     * @param vehicleGroupId
     * @return
     * @throws BooException
     */
    public VehicleEntity saveVehicle(Authentication authentication, ReqChargeTicketDTO reqChargeTicketDTO,
                                     Long vehicleGroupId, String rfidSerial, String plateTypeCode, String plate) throws BooException {
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setCustId(BOO_INFO.CUST_ID);
        vehicleEntity.setContractId(BOO_INFO.CONTRACT_ID);
        vehicleEntity.setPlateNumber(plate);
        vehicleEntity.setVehicleTypeId(Long.parseLong(reqChargeTicketDTO.getRegister_vehicle_type()));
        vehicleEntity.setVehicleGroupId(vehicleGroupId);
        vehicleEntity.setSeatNumber(reqChargeTicketDTO.getSeat());
        vehicleEntity.setCargoWeight(reqChargeTicketDTO.getWeight_goods());
        if (reqChargeTicketDTO.getWeight_all() != null) {
            vehicleEntity.setGrossWeight(reqChargeTicketDTO.getWeight_all());
        }
        vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
        vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
        vehicleEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        vehicleEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleEntity.setProfileStatus(VehicleEntity.ProfilesStatus.APPROVED.value);
        vehicleEntity.setEffDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleEntity.setRfidSerial(rfidSerial);
        vehicleEntity.setEpc(reqChargeTicketDTO.getEtag());
        vehicleEntity.setTid(reqChargeTicketDTO.getEtag());
        vehicleEntity.setRfidType("1");
        vehicleEntity.setPlateTypeCode(plateTypeCode);
        vehicleRepositoryJPA.save(vehicleEntity);
        Long reasonId = getReasonIdByactTypeId(Constants.ACT_TYPE.ADD_VEHICLE);
        writeLogNew(authentication, reasonId, Constants.ACT_TYPE.ADD_VEHICLE, BOO_INFO.CONTRACT_ID, BOO_INFO.CUST_ID, vehicleEntity.getVehicleId(), vehicleEntity, VehicleEntity.class.getAnnotation(Table.class).name());
        return vehicleEntity;
    }

    /**
     * Lay ma ly do theo ma tac dong
     *
     * @param actTypeId
     * @return
     */
    private Long getReasonIdByactTypeId(Long actTypeId) {
        long reasonId = 0L;
        List<ActReasonEntity> list = actReasonRepositoryJPA.findAllByActTypeId(actTypeId);
        if (list != null && list.size() > 0) {
            ActReasonEntity actReasonEntity = list.get(0);
            reasonId = actReasonEntity.getActReasonId();
        }
        return reasonId;
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
    private ActionAuditEntity writeLogNew(Authentication authentication, long reasonId, long actTypeId, long contractId, long customerId, long id, Object entity, String tableName) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ActionAuditEntity actionAuditEntity = new ActionAuditDTO().toEntity(authentication, reasonId, actTypeId, customerId, contractId, id, ip);
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

    private WsAuditEntity writeLog(String req, String resp, String url, long timeCallBOO1,
                                  Authentication authentication, String wsCallType, String status, Long actTypeId) {
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            String userLogin = FnCommon.getUserLogin(authentication);
            String clientId = FnCommon.getClientId(authentication);
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setSourceAppId(clientId);
            wsAuditEntity.setFinishTime(timeCallBOO1);
            wsAuditEntity.setActionUserName(userLogin);
            wsAuditEntity.setRequestTime(new java.sql.Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            if(resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setDestinationAppId(MODULE_CRM);
            wsAuditEntity.setActTypeId(actTypeId);
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOGGER.error("write log ws_audit failed", e);
        }
        return wsAuditEntity;
    }

    /**
     * Them gia tri vao sale trans detail
     *
     * @param vehicleAddSuffOfferDTO
     * @param authentication
     * @param saleTransId
     * @return
     */
    private Object addSaleTransDetail(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO,
                                      Authentication authentication, long saleTransId,
                                      String scope, String ocsCode, Long stageId, String orgPlateNumber,
                                      Long subscriptionTicketId, Long reasonId, Long actTypeId) {
        if (Objects.nonNull(scope)) {
            vehicleAddSuffOfferDTO.setScope(scope);
        }
        if (Objects.nonNull(ocsCode)) {
            vehicleAddSuffOfferDTO.setOcsCode(ocsCode);
        }
        SaleTransDetailEntity saleTransDetailEntity = vehicleAddSuffOfferDTO.toAddSaleTransDetailEntity(vehicleAddSuffOfferDTO,
                new java.sql.Date(System.currentTimeMillis()), FnCommon.getUserLogin(authentication), vehicleAddSuffOfferDTO.getPrice(), vehicleAddSuffOfferDTO.getQuantity(), saleTransId);
        if (vehicleAddSuffOfferDTO.getScope() != null) {
            saleTransDetailEntity.setScope(scope);
        }
        if (vehicleAddSuffOfferDTO.getAutoRenew() != null) {
            saleTransDetailEntity.setAutoRenew(vehicleAddSuffOfferDTO.getAutoRenew());
        }
        saleTransDetailEntity.setVehicleId(vehicleAddSuffOfferDTO.getVehicleId());
        saleTransDetailEntity.setPlateNumber(vehicleAddSuffOfferDTO.getPlateNumber());
        saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.PAID_NOT_INVOICED.value);
        saleTransDetailEntity.setStationId(vehicleAddSuffOfferDTO.getStationId());
        saleTransDetailEntity.setStageId(stageId);
        saleTransDetailEntity.setOrgPlateNumber(orgPlateNumber);
        saleTransDetailEntity.setSubscriptionTicketId(subscriptionTicketId);
        saleTransDetailEntity.setBooCode(Constants.BOO2);
        saleTransDetailEntity.setBooFlow(Constants.BOO1ChargeBOO2);
        saleTransDetailEntity.setTid(vehicleAddSuffOfferDTO.getTid());
        saleTransDetailEntity.setRfidSerial(vehicleAddSuffOfferDTO.getRfidSerial());
        saleTransDetailEntity.setVehicleGroupId(vehicleAddSuffOfferDTO.getVehiclesGroupId());
        Date expDate = FnCommon.convertStringToDate(vehicleAddSuffOfferDTO.getExpDate() + TIME_ONE_DAY, COMMON_DATE_FORMAT_BOO_24H);
        if (expDate != null) {
            saleTransDetailEntity.setExpDate(new java.sql.Date(expDate.getTime()));
        }
        saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
        writeLogNew(authentication, reasonId, actTypeId, BOO_INFO.CONTRACT_ID, BOO_INFO.CUST_ID, saleTransDetailEntity.getSaleTransId(), saleTransDetailEntity, SaleTransDetailEntity.class.getAnnotation(Table.class).name());
        return saleTransDetailEntity;
    }

    private void validateInputChargeTicket(ReqChargeTicketDTO requestChargeTicketBooDTO) {
        List<String> listError = new ArrayList<>();

        if (FnCommon.isNullOrEmpty(requestChargeTicketBooDTO.getPlate())) {
            listError.add("common.validate.plate.number.not.empty");
        }

        if (FnCommon.validateMaxlengthString(requestChargeTicketBooDTO.getPlate(), 16)) {
            listError.add("common.validate.plate.number.max.length");
        }

        // validate khoi luong hang hoa
        if ("21".equals(requestChargeTicketBooDTO.getRegister_vehicle_type()) ||
                "22".equals(requestChargeTicketBooDTO.getRegister_vehicle_type())) {
            if (requestChargeTicketBooDTO.getWeight_goods() == null) {
                listError.add("common.validate.cargo.weight.not.empty");
            } else {
                if (requestChargeTicketBooDTO.getWeight_goods() < 0 || FnCommon.validateMaxlengthDouble(requestChargeTicketBooDTO.getWeight_goods(), 6)) {
                    listError.add("common.validate.cargo.weight.min.max");
                }
            }
        }
        // validate so cho ngoi
        if ("11".equals(requestChargeTicketBooDTO.getRegister_vehicle_type()) ||
                "12".equals(requestChargeTicketBooDTO.getRegister_vehicle_type()) ||
                "13".equals(requestChargeTicketBooDTO.getRegister_vehicle_type())) {
            if (requestChargeTicketBooDTO.getSeat() == null) {
                listError.add("common.validate.seat.number.not.empty");
            }
        }

        if (listError.size() > 0) {
            throw new BooException(listError.get(0));
        }
    }

    /**
     * Cap nhat thong tin khi dong bo thay doi thogn tin dang ki
     *
     * @param vehicleEntity
     * @param reuRequestOnlineEventSyncBooDTO
     * @return
     */
    public boolean updateVehicleWhenSyncData(VehicleEntity vehicleEntity, ReqOnlineEventSyncDTO reuRequestOnlineEventSyncBooDTO, List<String> vehicleGroupIds) {
        vehicleEntity.setSeatNumber(reuRequestOnlineEventSyncBooDTO.getSeat_new());
        vehicleEntity.setGrossWeight(reuRequestOnlineEventSyncBooDTO.getWeight_all_new());
        vehicleEntity.setCargoWeight(reuRequestOnlineEventSyncBooDTO.getWeight_goods_new());
        Long vehicleTypeId = (!FnCommon.isNullOrEmpty(reuRequestOnlineEventSyncBooDTO.getRegister_vehicle_type_new())) ? Long.parseLong(reuRequestOnlineEventSyncBooDTO.getRegister_vehicle_type_new()) : 2L;
        vehicleEntity.setVehicleTypeId(vehicleTypeId);
        if (vehicleGroupIds != null && !vehicleGroupIds.isEmpty()) {
            vehicleEntity.setVehicleGroupId(Long.valueOf(vehicleGroupIds.get(0)));
        }
        VehicleEntity vehicleEntityResult = vehicleServiceJPA.save(vehicleEntity);
        return vehicleEntityResult != null;
    }

    /**
     * validate bien so xe
     *
     * @param plateNumber
     */
    private void validatePlateNumber(String plateNumber) {
        if (FnCommon.isNullOrEmpty(plateNumber)) {
            throw new BooException("common.validate.plate.number.not.empty");
        }

        if (FnCommon.validateMaxlengthString(plateNumber, 16)) {
            throw new BooException("common.validate.plate.number.max.length");
        }
    }

    /**
     * Lu gia tri vao sale trans del boo
     *
     * @param saleTransDetailEntity
     * @param saleTransDelBoo1Entity
     * @param requestCancelTicketBooDTO
     * @param processTime
     * @param saleTransEntity
     * @param userLogin
     * @return
     */
    private SaleTransDelBoo1Entity setValueSaleTransDetailToSaleTransBOO(SaleTransDetailEntity saleTransDetailEntity,
                                                                         SaleTransDelBoo1Entity saleTransDelBoo1Entity,
                                                                         ReqCancelTicketDTO requestCancelTicketBooDTO, Long processTime, SaleTransEntity saleTransEntity, String userLogin) {
        saleTransDelBoo1Entity.setSubscriptionTicketId(requestCancelTicketBooDTO.getSubscription_ticket_id());
        if (Objects.nonNull(requestCancelTicketBooDTO.getStation_in_id())) {
            saleTransDelBoo1Entity.setStationInId(requestCancelTicketBooDTO.getStation_in_id());
        }
        if (Objects.nonNull(requestCancelTicketBooDTO.getStation_out_id())) {
            saleTransDelBoo1Entity.setStationOutId(requestCancelTicketBooDTO.getStation_out_id());
        }
        if (Objects.nonNull(saleTransDetailEntity.getStageId())) {
            saleTransDelBoo1Entity.setStageId(saleTransDetailEntity.getStageId());
        }

        if (Objects.nonNull(saleTransDetailEntity.getServicePlanTypeId())) {
            saleTransDelBoo1Entity.setServicePlanTypeId(saleTransDetailEntity.getServicePlanTypeId());
        }
        if (Objects.nonNull(saleTransDetailEntity.getEffDate())) {
            saleTransDelBoo1Entity.setEffDate(saleTransDetailEntity.getEffDate());
        }
        if (Objects.nonNull(saleTransDetailEntity.getExpDate())) {
            saleTransDelBoo1Entity.setExpDate(saleTransDetailEntity.getExpDate());
        }
        if (Objects.nonNull(saleTransDetailEntity.getPlateNumber())) {
            saleTransDelBoo1Entity.setPlateNumber(saleTransDetailEntity.getPlateNumber());
        }
        if (Objects.nonNull(saleTransDetailEntity.getEpc())) {
            saleTransDelBoo1Entity.setEpc(saleTransDetailEntity.getEpc());
        }
        if (Objects.nonNull(saleTransDetailEntity.getPrice())) {
            saleTransDelBoo1Entity.setPrice(saleTransDetailEntity.getPrice());
        }
        saleTransDelBoo1Entity.setRequestId(requestCancelTicketBooDTO.getRequest_id());
        if ("O".equals(requestCancelTicketBooDTO.getStation_type())) {
            saleTransDelBoo1Entity.setStationType(1L);
        } else {
            saleTransDelBoo1Entity.setStationType(0L);
        }
        saleTransDelBoo1Entity.setContractId(BOO_INFO.CONTRACT_ID);
        saleTransDelBoo1Entity.setContractNo(booContractNo);
        saleTransDelBoo1Entity.setStatus(SaleTransDelBoo1Entity.Status.RECEIVED.value);
        if (Constants.BOT_REFUND.YES.equals(requestCancelTicketBooDTO.getRequest_type())) {
            saleTransDelBoo1Entity.setRequestType(SaleTransDelBoo1Entity.RequestType.REFUND_MONEY.value);
        } else {
            saleTransDelBoo1Entity.setRequestType(SaleTransDelBoo1Entity.RequestType.REFUND_NO_MONEY.value);
        }
        saleTransDelBoo1Entity.setCreateUser(userLogin);
        saleTransDelBoo1Entity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransDelBoo1Entity.setOcsCode(saleTransDetailEntity.getOcsCode());
        saleTransDelBoo1Entity.setOfferLevel(saleTransDetailEntity.getOfferLevel());
        saleTransDelBoo1Entity.setBooCode(saleTransDetailEntity.getBooCode());
        saleTransDelBoo1Entity.setProcessDatetime(processTime);
        saleTransDelBoo1Entity.setRequestDatetime(requestCancelTicketBooDTO.getRequest_datetime());
        saleTransDelBoo1Entity.setCustId(BOO_INFO.CUST_ID);
        saleTransDelBoo1Entity.setRequestId(requestCancelTicketBooDTO.getRequest_id());
        saleTransDelBoo1Entity.setMethodChargeId(saleTransEntity.getMethodRechargeId());
        return saleTransDelBoo1Entity;
    }

    /**
     * Check ve co
     *
     * @param requestChargeTicketBooDTO
     * @param stageId
     */
    private void validateExistTicket(ReqChargeTicketDTO requestChargeTicketBooDTO, Long stageId) {
        ServicePlanDTO servicePlanDTO = new ServicePlanDTO();
        servicePlanDTO.setPlateNumber(requestChargeTicketBooDTO.getPlate());
        servicePlanDTO.setStationId(requestChargeTicketBooDTO.getStation_in_id());
        if (Objects.nonNull(stageId)) {
            servicePlanDTO.setStageId(stageId);
        }
        try {
            SimpleDateFormat formatBOO = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT_BOO);
            formatBOO.parse(requestChargeTicketBooDTO.getStart_date());
            formatBOO.parse(requestChargeTicketBooDTO.getEnd_date());
            SimpleDateFormat formatEtc = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);
            servicePlanDTO.setCreateDateFrom(formatEtc.format(formatBOO.parse(requestChargeTicketBooDTO.getStart_date())));
            servicePlanDTO.setCreateDateTo(formatEtc.format(formatBOO.parse(requestChargeTicketBooDTO.getEnd_date())));
        } catch (ParseException e) {
            LOGGER.error("Exception format date validateExistTicket:", e);
        }
        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = ticketRepository.checkExistsTicket(servicePlanDTO, true);
        if (servicePlanVehicleDuplicateDTO.getPlateNumber() != null && servicePlanVehicleDuplicateDTO.getPlateNumber().contains(servicePlanDTO.getPlateNumber())) {
            throw new BooException("crm.validate.data.service.plan.exist");
        }
    }

    /**
     * validate input BOT confirm
     *
     * @param reqCancelResultDTO
     */
    private void validateInput(ReqCancelResultDTO reqCancelResultDTO) {
        if (reqCancelResultDTO.getSubscription_ticket_id() == null) {
            throw new BooException("boo.validate.subscription.ticket.not.empty");
        }
        if (reqCancelResultDTO.getBOT_confirm() == null) {
            throw new BooException("boo.validate.bot.confirm.not.empty");
        }
    }

    /**
     * Validate request huy ve
     *
     * @param requestId
     */
    private void validateRequestCancelTicket(Long requestId, ReqCancelTicketDTO reqCancelTicketDTO, SaleTransDetailEntity saleTransDetailEntity, SaleTransDelBoo1Entity saleTransDelBoo1Entity) {
        List<String> listError = new ArrayList<>();
        if (Objects.isNull(requestId)) {
            listError.add("boo.request_id.must.be.not.null");
        }

        if (requestId != null && !requestId.toString().matches("\\d+")) {
            listError.add("boo.request_id.not.a.number");
        }
        if (saleTransDelBoo1RepositoryJPA.findById(requestId).isPresent()) {
            listError.add("boo.request_id.exist");
        }

        if (saleTransDetailEntity.getStatus() != null && saleTransDetailEntity.getStatus().equals(SaleTransDetailEntity.Status.CANCEL.value)) {
            listError.add("boo.ticket.status.cancel");
        }

        if (saleTransDelBoo1Entity != null) {
            if (saleTransDelBoo1Entity.getStatus().equals(SaleTransDelBoo1Entity.Status.SUCCESS.value) || saleTransDelBoo1Entity.getStatus().equals(SaleTransDelBoo1Entity.Status.RECEIVED.value)) {
                listError.add("boo.ticket.not.cancel");
            }
        }
        if (listError.size() > 0) {
            throw new BooException(listError.get(0));
        }
    }

    /***
     * Dang ki phuong tien BOO1 sang OCS
     * @param authentication
     * @param req
     * @param resCalculatorTicket
     * @param plateNumber
     * @param plateTypeCode
     */
    private void registerVehicleBoo1ToOCS(Authentication authentication, ReqChargeTicketDTO req, ResCalculatorTicketDTO resCalculatorTicket, String plateNumber, String plateTypeCode, String rfidSerial) {
        AddVehicleRequestDTO reqOcs = new AddVehicleRequestDTO();
        reqOcs.setContractId(BOO_INFO.CONTRACT_ID);
        reqOcs.setEpc(req.getEtag());
        reqOcs.setTid(req.getEtag());
        reqOcs.setEffDate(FnCommon.convertStringToDate(req.getStart_date(), Constants.COMMON_DATE_FORMAT_BOO));
        reqOcs.setExpDate(FnCommon.convertStringToDate(req.getEnd_date(), Constants.COMMON_DATE_FORMAT_BOO));
        reqOcs.setStatus(VehicleEntity.Status.ACTIVATED.value);
        reqOcs.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
        reqOcs.setVehicleTypeId(Long.parseLong(req.getRegister_vehicle_type()));
        reqOcs.setPlateNumber(plateNumber);
        reqOcs.setVehicleGroupId(resCalculatorTicket.getVehicle_type());
        reqOcs.setRfidType("1");
        reqOcs.setRfidSerial(rfidSerial);
        reqOcs.setPlateTypeCode(plateTypeCode);
        if (req.getWeight_goods() != null) {
            reqOcs.setCargoWeight(req.getWeight_goods());
        }
        reqOcs.setSeatNumber(req.getSeat());
        OCSResponse ocsResponse = ocsService.createVehicleOCS(reqOcs, authentication, Constants.ACT_TYPE.ADD_VEHICLE);
        if (!checkOcsCode(ocsResponse)) {
            throw new BooException("crm.vehicle.can.not.charge.ticket");
        }
    }

    /***
     * Dang ki goi cuoc phu
     * @param authentication
     * @param resCalculatorTicket
     * @param reqChargeTicket
     * @param vehicleEntity
     * @return
     * @throws Exception
     */
    private Long addOfferToOCS(Authentication authentication, ResCalculatorTicketDTO resCalculatorTicket, ReqChargeTicketDTO reqChargeTicket, VehicleEntity vehicleEntity) throws Exception {
        long subscriptionTicketId = 0L;
        /**
         * Them du lieu Sale Trans vao CRM
         */
        AddSupOfferRequestDTO addSupOfferRequestDTO = new AddSupOfferRequestDTO();
        addSupOfferRequestDTO.setPaymentMethodId(Long.parseLong(SaleTransEntity.PaymentMethodId.ETC.value));
        addSupOfferRequestDTO.setSaleTransCode("TQ" + BOO_INFO.CONTRACT_ID + FnCommon.formatDateTime(new java.sql.Date(System.currentTimeMillis())));
        addSupOfferRequestDTO.setSaleTransDate(new Date());
        Map<String, Object> map = FnCommon.getAttribute(authentication);
        long staffId = 0L;
        if (Objects.nonNull(map) && map.containsKey(Constants.USER_ATTRIBUTE.STAFF_ID)) {
            staffId = Long.parseLong(map.get(Constants.USER_ATTRIBUTE.STAFF_ID).toString());
            addSupOfferRequestDTO.setStaffId(staffId);
        }
        addSupOfferRequestDTO.setContractNo(booContractNo);
        addSupOfferRequestDTO.setQuantity(1L);

        SaleTransEntity saleTransEntity = addSupOfferRequestDTO.toAddSaleTranEntity(FnCommon.getUserLogin(authentication),
                new java.sql.Date(System.currentTimeMillis()), BOO_INFO.CUST_ID, BOO_INFO.CONTRACT_ID, 0L, null);
        saleTransEntity.setStatus(SaleTransEntity.Status.PAID_NOT_INVOICED.value);
        saleTransEntity.setSaleTransType(SaleTransEntity.SaleTransType.MONTHLY_QUARTERLY_TICKET.value);
        saleTransEntity.setAmount(resCalculatorTicket.getPrice_amount());
        saleTransRepositoryJPA.save(saleTransEntity);

        long saleTransId = saleTransEntity.getSaleTransId();
        long reasonId = getReasonIdByactTypeId(Constants.ACT_TYPE.CHARGE_TICKET);
        long actTypeId = Constants.ACT_TYPE.CHARGE_TICKET;
        writeLogNew(authentication, reasonId, Constants.ACT_TYPE.CHARGE_TICKET, BOO_INFO.CONTRACT_ID, BOO_INFO.CUST_ID, saleTransEntity.getSaleTransId(), saleTransEntity, SaleTransEntity.class.getAnnotation(Table.class).name());


        /**
         * Add offer ticket len OCS
         */
        VehicleAddSuffOfferDTO vehicleAddSupOffer = new VehicleAddSuffOfferDTO();
        vehicleAddSupOffer.setOfferLevel(String.valueOf(Constants.OFFER_LEVEL_DEFAULT));
        vehicleAddSupOffer.setOfferId(resCalculatorTicket.getOcs_code());
        vehicleAddSupOffer.setEpc(reqChargeTicket.getEtag());
        vehicleAddSupOffer.setServicePlanId(resCalculatorTicket.getService_plan_id());
        if (Objects.nonNull(resCalculatorTicket.getService_plan_id())) {
            vehicleAddSupOffer.setServicePlanTypeId(resCalculatorTicket.getService_plan_id());
        }
        vehicleAddSupOffer.setPrice(resCalculatorTicket.getPrice_amount());
        vehicleAddSupOffer.setQuantity(1L);
        vehicleAddSupOffer.setScope(String.valueOf(resCalculatorTicket.getScope()));
        vehicleAddSupOffer.setPlateNumber(vehicleEntity.getPlateNumber());
        vehicleAddSupOffer.setVehicleId(vehicleEntity.getVehicleId());
        vehicleAddSupOffer.setEffDate(FnCommon.convertStringToDate(reqChargeTicket.getStart_date(), Constants.COMMON_DATE_FORMAT_BOO));
        vehicleAddSupOffer.setExpDate(FnCommon.convertStringToDate(reqChargeTicket.getEnd_date() + TIME_ONE_DAY, COMMON_DATE_FORMAT_BOO_24H));
        vehicleAddSupOffer.setStationId(reqChargeTicket.getStation_in_id());
        vehicleAddSupOffer.setStageId(resCalculatorTicket.getStage_id());
        vehicleAddSupOffer.setTid(vehicleEntity.getTid());
        vehicleAddSupOffer.setRfidSerial(vehicleEntity.getRfidSerial());
        vehicleAddSupOffer.setVehiclesGroupId(vehicleEntity.getVehicleGroupId());
        OCSResponse ocsResponse = ocsService.addSupOffer(vehicleAddSupOffer, authentication, actTypeId, BOO_INFO.CONTRACT_ID, staffId, FnCommon.getUserLogin(authentication), String.valueOf(saleTransId));
        if (FnCommon.checkOcsCode(ocsResponse)) {
            if (Objects.nonNull(ocsResponse.getSubscriptionTicketId())) {
                subscriptionTicketId = Long.parseLong(ocsResponse.getSubscriptionTicketId().trim());
            }
            addSaleTransDetail(vehicleAddSupOffer, authentication, saleTransId, String.valueOf(resCalculatorTicket.getScope()), resCalculatorTicket.getOcs_code(), resCalculatorTicket.getStage_id(), reqChargeTicket.getPlate(), subscriptionTicketId, reasonId, actTypeId);
        } else {
            throw new BooException("boo.error.charge.ticket");
        }
        return subscriptionTicketId;
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
        map.put("timestampInFrom", FnCommon.convertDateToStringOther(effDate, Constants.COMMON_DATE_FORMAT));
        map.put("timestampInTo", FnCommon.convertDateToStringOther(expDate, Constants.COMMON_DATE_FORMAT));
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
        return new Gson().fromJson(response, TransactionsHistoryVehicleDTO.class);
    }

    /***
     * lay thong tin cua doan
     * @param token
     * @return
     */
    private StageBooDTO getStageById(String token, String stageId) {
        String requestUrl = stageUrl.replace("{stageId}", stageId);
        String stageResponse = FnCommon.doGetRequest(requestUrl, null, token);
        return new Gson().fromJson(stageResponse, StageBooDTO.class);
    }

    /**
     * Luu danh sach exception list
     *
     * @param exceptionListEntities
     * @return
     */
    public List<ExceptionListEntity> saveAllExceptionList(List<ExceptionListEntity> exceptionListEntities, ReqOnlineEventSyncDTO reqOnlineEventSyncDTO) {
        for (ExceptionListEntity exceptionListEntity : exceptionListEntities) {
            exceptionListEntity.setEpc(reqOnlineEventSyncDTO.getEtag_new());
        }
        return exceptionListServiceJPA.saveAll(exceptionListEntities);
    }

    /**
     * Cap nhat thong tin xe crm va ocs
     *
     * @param vehicleEntity
     * @param requestOnlineEventSyncBooDTO
     * @param authentication
     * @return
     */
    public boolean updateVehicleCrmAndOcsWhenChangeOtherInfo(VehicleEntity vehicleEntity,
                                                             ReqOnlineEventSyncDTO requestOnlineEventSyncBooDTO,
                                                             Authentication authentication, List<String> vehicleGroupIds) {
        boolean result = false;
        UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO = new UpdateVehicleRequestOCSDTO().toEntityModifyVehicle(requestOnlineEventSyncBooDTO);
        boolean isChanged = updateVehicleWhenSyncData(vehicleEntity, requestOnlineEventSyncBooDTO, vehicleGroupIds);
        if (isChanged) {
            result = modifyVehicleOcs(updateVehicleRequestOCSDTO, authentication);
        } else {
            LOGGER.error("Cap nhat phuong tien CRM khong thanh cong");
        }
        return result;
    }

    /**
     * Huy tai khoan
     *
     * @param req
     * @param authentication
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BooException.class)
    public boolean cancelEpc(ReqOnlineEventSyncDTO req,
                             Authentication authentication) {
        boolean result = false;
        validateCancelEpc(req);
        Optional<VehicleEntity> vehicleEntity = vehicleServiceJPA.findByPlateNumberAndActiveStatusNot(FnCommon.formatPlateBOO1(req.getPlate_old()), VehicleEntity.ActiveStatus.CANCEL.value);
        if (vehicleEntity.isPresent()) {
            mapStatusAndActiveStatus(req, vehicleEntity.get());
            UpdateVehicleRequestOCSDTO reqOcs = new UpdateVehicleRequestOCSDTO();
            reqOcs.setEpc(req.getEtag_old());
            reqOcs.setStatus(VehicleEntity.ActiveStatus.CANCEL.value);
            reqOcs.setActTypeId(ACT_TYPE.MODIFY_VEHICLE);
            OCSResponse ocsResponse = ocsService.modifyVehicleOCS(reqOcs, authentication, true);
            if (checkOcsCode(ocsResponse)) {
                vehicleServiceJPA.save(vehicleEntity.get());
                result = true;
            } else {
                LOGGER.error(String.format("Cap nhat phuong tien ocs that bai : %s", ocsResponse));
                throw new BooException("boo.open_and_close.epc.fail");
            }
        } else {
            LOGGER.error(String.format("Khong tim thay phuong tien voi plate number = %s,active status = %s", FnCommon.formatPlateBOO1(req.getPlate_old()), VehicleEntity.ActiveStatus.CANCEL.value));
        }

        return result;
    }

    /**
     * Kiem tra khi guy tai khoan
     *
     * @param reqOnlineEventSyncDTO
     */
    public void validateCancelEpc(ReqOnlineEventSyncDTO reqOnlineEventSyncDTO) {
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_old())) {
            throw new BooException("boo.info.register.params.etag");
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_status_old()) || FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getEtag_status_new())) {
            throw new BooException("boo.open-close.epc.params.etag-status");
        }
        if (FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate");
        } else if (!FnCommon.isNullOrEmpty(reqOnlineEventSyncDTO.getPlate_old()) && !FnCommon.validatePlateContainsTVX(reqOnlineEventSyncDTO.getPlate_old())) {
            throw new BooException("boo.info.register.params.plate.format");
        }

    }
}


