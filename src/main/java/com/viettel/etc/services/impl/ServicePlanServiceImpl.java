package com.viettel.etc.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.viettel.etc.dto.*;
import com.viettel.etc.dto.boo.ReqCalculatorTicketDTO;
import com.viettel.etc.dto.boo.ResCalculatorTicketDTO;
import com.viettel.etc.dto.dmdc.VehicleGroupDTO;
import com.viettel.etc.repositories.ExceptionListRepository;
import com.viettel.etc.repositories.ServicePlanRepository;
import com.viettel.etc.repositories.tables.AttachmentFileRepositoryJPA;
import com.viettel.etc.repositories.tables.SaleTransDetailRepositoryJPA;
import com.viettel.etc.repositories.tables.ServiceFeeRepositoryJPA;
import com.viettel.etc.repositories.tables.VehicleRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.*;
import com.viettel.etc.services.tables.BotRevenueShareServiceJPA;
import com.viettel.etc.services.tables.ExceptionListServiceJPA;
import com.viettel.etc.services.tables.ServicePlanServiceJPA;
import com.viettel.etc.services.tables.ServicePlanTypeServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.BooException;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.entities.CoreErrorApp;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Autogen class: Lop thao tac gia ve bang cuoc
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:20 ICT 2020
 */
@Service
public class ServicePlanServiceImpl implements ServicePlanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePlanServiceImpl.class);
    private final String EXCEPTION_VEHICLE_TYPE = "EXCEPTION_VEHICLE_TYPE";
    private final String EXCEPTION_VEHICLE_TYPE_PROMOTION_ID = "EXCEPTION_VEHICLE_TYPE_PROMOTION_ID";
    private final String EXCEPTION_TICKET_TYPE_PROMOTION_ID = "EXCEPTION_TICKET_TYPE_PROMOTION_ID";
    private final Long STATION_OPEN = 1L;

    @Autowired
    private ServicePlanServiceJPA servicePlanServiceJPA;

    @Autowired
    ServicePlanRepository servicePlanRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ServicePlanTypeServiceJPA servicePlanTypeServiceJPA;

    @Autowired
    ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private StageService stageService;

    @Autowired
    private StationService stationService;

    @Autowired
    private BotService botService;

    @Autowired
    private LaneService laneService;

    @Autowired
    private BotRevenueShareServiceJPA botRevenueShareServiceJPA;

    @Autowired
    private AttachmentFileRepositoryJPA attachmentFileRepositoryJPA;

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    VehicleGroupService vehicleGroupService;

    @Autowired
    private FileService fileService;

    @Autowired
    ExceptionListServiceJPA exceptionListServiceJPA;

    @Autowired
    Boo1Service boo1Service;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Autowired
    ExceptionListRepository exceptionListRepository;

    @Value("${ws.dmdc.vehicle-groups}")
    private String wsVehicleGroups;

    @Value("${ws.dmdc.stage.get}")
    private String stageUrl;

    @Value("${ws.dmdc.stations.boo}")
    private String stationUrl;

    @Value("${crm.common.max-file-size}")
    private Integer briefcaseMaxFileSize;

    /**
     * Them moi gia ve goi cuoc
     *
     * @param itemParamsEntity params client
     * @param authentication
     * @return
     */
    @Override
    public Object addTicketPrices(ServicePlanDTO itemParamsEntity, Authentication authentication) throws EtcException, Exception {
        if (itemParamsEntity.getAutoRenew() != 0 && itemParamsEntity.getUseDay() == null) {
            throw new EtcException("crm.validate.data.service.plan.use.day.not.exits");
        }
        if (itemParamsEntity.getScope().equals(ServicePlanEntity.Scope.STATION.value) && itemParamsEntity.getStationId() == null) {
            throw new EtcException("crm.validate.data.station.id.null");
        }
        if (itemParamsEntity.getScope().equals(ServicePlanEntity.Scope.STAGE.value) && itemParamsEntity.getStageId() == null) {
            throw new EtcException("crm.validate.data.stage.id.null");
        }
        checkServicePlanCode(itemParamsEntity);
        Date date = new Date(System.currentTimeMillis());
        String userLogin = FnCommon.getUserLogin(authentication);
        ModelMapper modelMapper = new ModelMapper();
        ServicePlanEntity servicePlanEntity = modelMapper.map(itemParamsEntity, ServicePlanEntity.class);
        servicePlanEntity.setCreateDate(date);
        servicePlanEntity.setCreateUser(userLogin);
        servicePlanEntity.setStatus(ServicePlanEntity.Status.PENDING.value);
        servicePlanEntity.setOfferlevel(Constants.OFFER_LEVEL_DEFAULT);
        ServicePlanEntity servicePlanEntitySaved = servicePlanServiceJPA.save(servicePlanEntity);
        List<BotRevenueShareDTO> botRevenueShareDTOS = itemParamsEntity.getBotRevenueShareList();
        List<AttachmentFileDTO> attachmentFileDTOS = itemParamsEntity.getAttachmentFileDTOS();
        if (!FnCommon.isNullOrEmpty(botRevenueShareDTOS)) {
            List<BotRevenueShareEntity> botRevenueShareEntities = new ArrayList<>();
            for (BotRevenueShareDTO bot : botRevenueShareDTOS) {
                bot.setServicePlanId(servicePlanEntitySaved.getServicePlanId());
                botRevenueShareEntities.add(modelMapper.map(bot, BotRevenueShareEntity.class));
            }
            try {
                botRevenueShareServiceJPA.saveAll(botRevenueShareEntities);
            } catch (Exception e) {
                LOGGER.error(" ERROR INSERT BOT REVENUE SHARE", e);
            }
        }
        if (!FnCommon.isNullOrEmpty(attachmentFileDTOS)) {
            addAttachedFile(attachmentFileDTOS, date, userLogin, servicePlanEntity.getServicePlanId());
        }

        return servicePlanEntitySaved;
    }

    /**
     * Delete service plan
     *
     * @param servicePlanId
     * @param authentication
     * @throws RuntimeException
     */
    @Override
    public void deleteTicketPrices(Long servicePlanId, Authentication authentication) throws RuntimeException {
        ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.findById(servicePlanId).get();
        if (ServicePlanEntity.Status.APPROVAL.value.equals(servicePlanEntity.getStatus()) ||
                ServicePlanEntity.Status.CANCEL.value.equals(servicePlanEntity.getStatus())) {
            throw new EtcException("crm.err.ticket.price.status.exception");
        }
        servicePlanEntity.setStatus(ServicePlanEntity.Status.DELETED.value);
        servicePlanEntity.setUpdateDate(new Date(System.currentTimeMillis()));
        servicePlanEntity.setUpdateUser(FnCommon.getUserLogin(authentication));
        servicePlanServiceJPA.save(servicePlanEntity);
    }

    /**
     * Api App CPT: search service plan
     *
     * @param authentication: thong tin nguoi dung
     * @param servicePlanDTO: thong tin tim kiem
     * @return
     */
    @Override
    public Object searchServicePlan(Authentication authentication, PriceListDTO servicePlanDTO) throws IOException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServicePlanEntity> query = criteriaBuilder.createQuery(ServicePlanEntity.class);
        Root<ServicePlanEntity> root = query.from(ServicePlanEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(servicePlanDTO.getStationId())) {
            predicates.add(criteriaBuilder.equal(root.get("stationId"), servicePlanDTO.getStationId()));
        } else if (Objects.nonNull(servicePlanDTO.getStageId())) {
            predicates.add(criteriaBuilder.equal(root.get("stageId"), servicePlanDTO.getStageId()));
        }
        predicates.add(criteriaBuilder.equal(root.get("status"), ServicePlanEntity.Status.APPROVAL.value));
        Date now = new Date(System.currentTimeMillis());
        predicates.add(criteriaBuilder.or(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), now),
                criteriaBuilder.isNull(root.get("endDate"))));
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), now));
        predicates.add(criteriaBuilder.isNotNull(root.get("vehicleGroupId")));
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(criteriaBuilder.asc(root.get("servicePlanId")));

        ResultSelectEntity resultSelectEntity = new ResultSelectEntity();

        List<ServicePlanEntity> resultList = entityManager.createQuery(query).getResultList();
        if (resultList == null || resultList.isEmpty()) {
            resultSelectEntity.setCount(0);
            resultSelectEntity.setListData(new LinkedList<>());
            return resultSelectEntity;
        }

        Map<Long, List<ServicePlanEntity>> servicePlanGroupByVehicleGroup = resultList
                .stream().collect(groupingBy(ServicePlanEntity::getVehicleGroupId));

        List<PriceListDTO> priceList = new ArrayList<>();
        for (Map.Entry<Long, List<ServicePlanEntity>> entry : servicePlanGroupByVehicleGroup.entrySet()) {
            priceList.add(new PriceListDTO().collectFromEntity(entry.getKey(), entry.getValue()));
        }

        //map data tu DMDC
        ObjectMapper objectMapper = new ObjectMapper();
        VehicleGroupDTO[] vehicleGroupDTOS = new VehicleGroupDTO[0];
        JsonNode dataOfOtherModule = getDataOfOtherModule(wsVehicleGroups, authentication, JsonNode.class);
        if (dataOfOtherModule.get("mess").get("code").intValue() == CoreErrorApp.SUCCESS.getCode()) {
            vehicleGroupDTOS = objectMapper.readValue(dataOfOtherModule.get("data").get("listData").toString(), VehicleGroupDTO[].class);
        }
        Map<Long, VehicleGroupDTO> collect = Arrays.stream(vehicleGroupDTOS).collect(Collectors.toMap(VehicleGroupDTO::getId, Function.identity()));

        for (PriceListDTO priceListDTO : priceList) {
            if (collect.containsKey(priceListDTO.getVehicleGroupId())) {
                VehicleGroupDTO vehicleGroupDTO = collect.get(priceListDTO.getVehicleGroupId());
                priceListDTO.setVehicleGroupName(vehicleGroupDTO.getName());
                priceListDTO.setVehicleGroupDescription(vehicleGroupDTO.getDescription());
            }
        }

        resultSelectEntity.setCount(priceList.size());
        resultSelectEntity.setListData(priceList);
        if (priceList.size() > servicePlanDTO.getStartrecord()) {
            if (priceList.size() < (servicePlanDTO.getStartrecord() + servicePlanDTO.getPagesize())) {
                resultSelectEntity.setListData(priceList.subList(servicePlanDTO.getStartrecord(), priceList.size()));
            } else {
                resultSelectEntity.setListData(priceList.subList(servicePlanDTO.getStartrecord(),
                        servicePlanDTO.getStartrecord() + servicePlanDTO.getPagesize()));
            }
        }

        return resultSelectEntity;
    }


    /**
     * Tim kiem danh sach gia ve
     *
     * @param servicePlanDTO
     * @return
     */
    @Override
    public ResultSelectEntity searchTicketPrices(ServicePlanDTO servicePlanDTO) {
        return servicePlanRepository.searchTicketPrices(servicePlanDTO);
    }

    /**
     * Phe duyet gia ve
     *
     * @param servicePlanDTO
     * @param authentication
     */
    @Override
    public List<ServicePlanEntity> approvalTicketPrices(ServicePlanDTO servicePlanDTO, Authentication authentication) throws EtcException {
        List<Long> servicePlanIdList = servicePlanDTO.getServicePlanIdList();
        Date date = new Date(System.currentTimeMillis());
        List<ServicePlanEntity> servicePlanEntityListUpdate = new ArrayList<>();
        List<Long> servicePlanEntityListUpdateError = new ArrayList<>();
        for (Long servicePlanId : servicePlanIdList) {
            ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.findById(servicePlanId).get();
            if (!ServicePlanEntity.Status.PENDING.value.equals(servicePlanEntity.getStatus())) {
                servicePlanEntityListUpdateError.add(servicePlanId);
                continue;
            }
            if (checkExistOCS(servicePlanEntity.getOcsCode(), date)) {
                servicePlanEntityListUpdateError.add(servicePlanId);
                continue;
            }
            if (checkEffDate(servicePlanId)) {
                servicePlanEntityListUpdateError.add(servicePlanId);
                continue;
            }
            servicePlanEntity.setStatus(ServicePlanEntity.Status.APPROVAL.value);
            servicePlanEntity.setApproverDate(date);
            servicePlanEntity.setApproverUser(FnCommon.getUserLogin(authentication));
            servicePlanEntityListUpdate.add(servicePlanEntity);
        }
        if (servicePlanEntityListUpdate.size() > Constants.SIZE_LIST_ZERO) {
            servicePlanServiceJPA.saveAll(servicePlanEntityListUpdate);
        }
        if (servicePlanEntityListUpdateError.size() > Constants.SIZE_LIST_ZERO) {
            Map<String, String> parameter = new HashMap<>();
            parameter.put("PARAMETER", String.valueOf(servicePlanEntityListUpdateError.size()));
            throw new EtcException("crm.err.count.approval.ticket.prices", parameter);
        }
        return servicePlanEntityListUpdate;
    }

    /**
     * check ocs exist
     *
     * @param ocsCode
     * @param date
     */
    private boolean checkExistOCS(String ocsCode, Date date) throws EtcException {
        List<ServicePlanEntity> servicePlanEntityList = servicePlanServiceJPA.findAll();
        for (ServicePlanEntity servicePlanEntity : servicePlanEntityList) {
            if (servicePlanEntity.getOcsCode().equals(ocsCode) && servicePlanEntity.getStatus().equals(ServicePlanEntity.Status.APPROVAL.value)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Lay phi dang ky mua ve thang, quy
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getFee(ServicePlanDTO itemParamsEntity, Authentication authentication) throws IOException {
        RequestGetFeeChargeTicketDTO requestGetFeeChargeTicketDTO = new RequestGetFeeChargeTicketDTO();
        List<ServicePlanDTO> list = new ArrayList<>();
        String[] vehicleGroupIds = itemParamsEntity.getVehicleGroupIdArr().split(",");
        String[] plateNumbers = itemParamsEntity.getPlateNumber().split(",");
        for (int i = 0; i < vehicleGroupIds.length; i++) {
            if (!"".equals(vehicleGroupIds[i])) {
                Long vehicleGroupId = Long.parseLong(vehicleGroupIds[i]);
                String plateNumber = plateNumbers[i].replace("'", "");
                Gson gson = new GsonBuilder().create();
                ServicePlanDTO servicePlanDTOOld = gson.fromJson(gson.toJson(itemParamsEntity), ServicePlanDTO.class);
                Optional<VehicleEntity> vehicleEntity = vehicleRepositoryJPA.findByPlateNumberActive(plateNumber);
                if (!vehicleEntity.isPresent()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("PARAMETER", plateNumber);
                    throw new EtcException("crm.service-plan.get-fee.invalid.vehicle", map);
                }
                servicePlanDTOOld.setVehicleId(vehicleEntity.get().getVehicleId());
                servicePlanDTOOld.setVehicleGroupId(vehicleGroupId);
                servicePlanDTOOld.setPlateNumber(plateNumber);
                servicePlanDTOOld.setEffDate(itemParamsEntity.getCreateDateFrom());
                servicePlanDTOOld.setExpDate(itemParamsEntity.getCreateDateTo());
                servicePlanDTOOld.setVehicleTypeId(vehicleEntity.get().getVehicleTypeId());
                servicePlanDTOOld.setEpc(vehicleEntity.get().getEpc());
                servicePlanDTOOld.setSeatNumber(vehicleEntity.get().getSeatNumber());
                servicePlanDTOOld.setCargoWeight(vehicleEntity.get().getCargoWeight());
                servicePlanDTOOld.setNetWeight(vehicleEntity.get().getNetWeight());
                list.add(servicePlanDTOOld);
            }
        }
        requestGetFeeChargeTicketDTO.setServicePlanDTOList(list);
        return getFeeNew(authentication, requestGetFeeChargeTicketDTO);
    }

    /**
     * Lay phi dang ky mua ve thang, quy cho mobile
     *
     * @param dataParam params client
     * @return
     */
    @Override
    public Object getFeeMobile(ServicePlanDTO dataParam) {
        if (dataParam.getStageId() != null && dataParam.getStationId() != null) {
            return null;
        }
        return servicePlanRepository.getFeeMobile(dataParam);
    }

    /**
     * Huy bo hieu luc
     *
     * @param servicePlanDTO
     * @param authentication
     * @return
     */
    @Override
    public List<ServicePlanEntity> rejectTicketPrices(ServicePlanDTO servicePlanDTO, Authentication authentication) {
        List<Long> servicePlanIdList = servicePlanDTO.getServicePlanIdList();
        List<ServicePlanEntity> servicePlanEntityList = new ArrayList<>();
        List<Long> listServicePlanIdError = new ArrayList<>();
        for (Long servicePlanId : servicePlanIdList) {
            ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.findById(servicePlanId).get();
            if (!ServicePlanEntity.Status.PENDING.value.equals(servicePlanEntity.getStatus())) {
                listServicePlanIdError.add(servicePlanId);
                continue;
            }
            servicePlanEntity.setNote(servicePlanDTO.getNote());
            servicePlanEntity.setStatus(ServicePlanEntity.Status.REJECT.value);
            servicePlanEntity.setApproverDate(new Date(System.currentTimeMillis()));
            servicePlanEntity.setApproverUser(FnCommon.getUserLogin(authentication));
            servicePlanEntityList.add(servicePlanEntity);
        }
        if (servicePlanEntityList.size() > Constants.SIZE_LIST_ZERO) {
            servicePlanServiceJPA.saveAll(servicePlanEntityList);
        }
        if (listServicePlanIdError.size() > Constants.SIZE_LIST_ZERO) {
            Map<String, String> parameter = new HashMap<>();
            parameter.put("PARAMETER", String.valueOf(listServicePlanIdError.size()));
            throw new EtcException("crm.err.count.reject.ticket.prices", parameter);
        }
        return servicePlanEntityList;
    }

    /**
     * check hieu luc cua ban ghi
     *
     * @param servicePlanEntity
     * @param date
     * @return
     */
    private boolean checkEfficiency(ServicePlanEntity servicePlanEntity, Date date) {
        if (servicePlanEntity.getEndDate() != null) {
            if (date.after(servicePlanEntity.getStartDate()) && date.before(servicePlanEntity.getEndDate())) {
                return true;
            }
        } else {
            if (date.after(servicePlanEntity.getStartDate())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tim kiem gia ve nho nhat theo station id
     *
     * @param servicePlanDTO
     * @return
     */
    @Override
    public Object getMinFee(ServicePlanDTO servicePlanDTO) {
        return servicePlanRepository.getMinFee(servicePlanDTO);
    }

    /**
     * Xu ly export bang cuoc gia ve
     *
     * @param servicePlanDTO
     * @param authentication
     * @return
     */
    @Override
    public String exportServicePlan(ServicePlanDTO servicePlanDTO, Authentication authentication) throws IOException {
        String token = FnCommon.getStringToken(authentication);
        ResultSelectEntity dataResult = servicePlanRepository.searchTicketPrices(servicePlanDTO);
        List<ServicePlanDTO> servicePlans = (List<ServicePlanDTO>) dataResult.getListData();
        if (!servicePlans.isEmpty()) {
            List<Long> stageIdList = servicePlans.stream().filter(value -> value.getStageId() != null).map(ServicePlanDTO::getStageId)
                    .collect(Collectors.toList());
            List<Long> stationList = servicePlans.stream().filter(value -> value.getStationId() != null).map(ServicePlanDTO::getStationId)
                    .collect(Collectors.toList());
            List<Long> vehicleGroupList = servicePlans.stream().filter(value -> value.getVehicleGroupId() != null).map(ServicePlanDTO::getVehicleGroupId)
                    .collect(Collectors.toList());
            List<StageDTO.Stage> dataStages = stageService.findStagesByListId(token, stageIdList);
            List<StationDTO.Station> dataStations = stationService.findStationsByListId(token, stationList);
            List<com.viettel.etc.dto.VehicleGroupDTO.VehicleGroup> dataVehicleGroups = vehicleGroupService.findVehicleGroupByListId(FnCommon.getStringToken(authentication), vehicleGroupList);
            Map<Long, String> stagesMap = dataStages.stream().collect(Collectors.toMap(StageDTO.Stage::getId, StageDTO.Stage::getName));
            Map<Long, String> stationMap = dataStations.stream().collect(Collectors.toMap(StationDTO.Station::getId, StationDTO.Station::getName));
            Map<Long, String> vehicleGroupMap = dataVehicleGroups.stream().collect(Collectors.toMap(com.viettel.etc.dto.VehicleGroupDTO.VehicleGroup::getId, com.viettel.etc.dto.VehicleGroupDTO.VehicleGroup::getName));

            List<ServicePlanTypeEntity> servicePlanTypes = servicePlanTypeServiceJPA.findAll();
            ResultSelectEntity data = categoriesService.findCategoriesCustomer(token);
            List<LinkedTreeMap<?, ?>> lstCategory = (List<LinkedTreeMap<?, ?>>) data.getListData();
            List<LinkedHashMap<?, ?>> listLane = laneService.findAllLanes(token);
            readAndRewritingExcelFile(lstCategory, servicePlans, stationMap, stagesMap, servicePlanTypes, listLane, vehicleGroupMap);
        } else {
            return "/template/export/ServicePlan.xlsx";
        }
        return System.getProperty("user.dir") + File.separator + "workbook.xlsx";
    }

    /**
     * Xu ly export bang cuoc gia ve
     * Doc va thay doi du lieu tu file template
     *
     * @param lstCategory      danh sach danh muc dung chung
     * @param servicePlans     danh sach bang cuoc gia ve
     * @param stationMap       du lieu tram
     * @param stagesMap        du lieu doan
     * @param servicePlanTypes danh sach bang cuoc gia ve
     * @param listLane         danh sach lan
     * @param vehicleGroupMap  danh sach phuong tin thu phi
     */
    public void readAndRewritingExcelFile(List<LinkedTreeMap<?, ?>> lstCategory, List<ServicePlanDTO> servicePlans,
                                          Map<Long, String> stationMap, Map<Long, String> stagesMap,
                                          List<ServicePlanTypeEntity> servicePlanTypes, List<LinkedHashMap<?, ?>> listLane, Map<Long, String> vehicleGroupMap) throws FileNotFoundException {
//        File file = ResourceUtils.getFile("classpath:template/export/ServicePlan.xlsx");
        try (InputStream inp = getClass().getResourceAsStream("/template/export/ServicePlan.xlsx"); Workbook wb = WorkbookFactory.create(inp)) {
            Sheet sheet = wb.getSheetAt(0);
            int indexRow = 3;
            for (ServicePlanDTO servicePlanDTO : servicePlans) {
                rewritingContentExcel(servicePlanDTO, lstCategory, sheet, indexRow, stationMap, stagesMap, servicePlanTypes, listLane, vehicleGroupMap);
                indexRow++;
            }
            try (OutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + File.separator + "workbook.xlsx")) {
                wb.write(fileOut);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Lay danh sach danh muc loi : %s ", e));
        }
    }

    /**
     * Xu ly export bang cuoc gia ve
     * Kiem tra dieu kien ghi noi dung file
     *
     * @param categories       danh sach danh muc dung chung
     * @param sheet            sheet lua chon thuc hien
     * @param listLane         danh sach lan
     * @param servicePlanDTO   du lieu bang cuoc gia ve
     * @param indexRow         chi muc hang
     * @param stationMap       du lieu tram
     * @param stagesMap        du lieu doan
     * @param servicePlanTypes danh sach danh muc bang cuoc gia ve
     */
    public void rewritingContentExcel(ServicePlanDTO servicePlanDTO, List<LinkedTreeMap<?, ?>> categories,
                                      Sheet sheet, int indexRow, Map<Long, String> stationMap, Map<Long, String> stagesMap,
                                      List<ServicePlanTypeEntity> servicePlanTypes, List<LinkedHashMap<?, ?>> listLane,
                                      Map<Long, String> vehicleGroupMap) {
        Row row = sheet.createRow(indexRow);
        int indexCell = 0;
        Cell cell = row.createCell(indexCell);
        cell.setCellValue(indexRow - 2);
        indexCell++;
        setServicePlanTypeId(servicePlanDTO.getServicePlanTypeId(), servicePlanTypes, row, indexCell++);
        setScope(servicePlanDTO.getScope(), row, indexCell++);
        setStationOrStage(servicePlanDTO.getStationId(), servicePlanDTO.getStageId(), stationMap, stagesMap, row, indexCell++);
        setLane(servicePlanDTO.getLaneIn(), listLane, row, indexCell++);
        setLane(servicePlanDTO.getLaneOut(), listLane, row, indexCell++);
        setVehicleGroup(servicePlanDTO.getVehicleGroupId(), vehicleGroupMap, row, indexCell++);
        setValueCell(servicePlanDTO.getFee(), row, indexCell++);
        setValueCell(servicePlanDTO.getOcsCode(), row, indexCell++);
        setValueCell(servicePlanDTO.getEffDate(), row, indexCell++);
        setValueCell(FnCommon.convertDateToString(servicePlanDTO.getCreateDate(), false), row, indexCell++);
        setValueCell(servicePlanDTO.getCreateUser(), row, indexCell++);
        setStatus(servicePlanDTO.getStatus(), row, indexCell);
    }

    /**
     * Gan gia tri lan vao,lan ra cho file
     *
     * @param laneId    ma dinh danh lan
     * @param listLane  danh sach lan
     * @param row       hang thuc hien
     * @param indexCell chi muc cot
     */
    private void setLane(Long laneId, List<LinkedHashMap<?, ?>> listLane, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        if (laneId != null) {
            for (LinkedHashMap<?, ?> lane : listLane) {
                if (laneId.equals(Long.parseLong(lane.get("id").toString()))) {
                    cell.setCellValue(lane.get("name").toString());
                    break;
                } else {
                    cell.setCellValue("");
                }
            }
        } else {
            cell.setCellValue("");
        }
    }

    /**
     * set pham vi ap dung cho ban ghi
     *
     * @param scope     ma dinh danh pham vi
     * @param row       hang thuc hien
     * @param indexCell chi muc cot
     */
    private void setScope(Long scope, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        switch (scope.toString()) {
            case "1":
                cell.setCellValue("Toàn quốc");
                break;
            case "3":
                cell.setCellValue("Theo trạm");
                break;
            case "4":
                cell.setCellValue("Theo đoạn");
                break;
            default:
                cell.setCellValue("");
                break;
        }
    }


    /**
     * Xu ly export bang cuoc gia ve xe
     * Set loai ve
     *
     * @param servicePlanTypeId ma dinh danh bang cuoc gia ve
     * @param servicePlanTypes  danh sach bang cuoc gia ve
     * @param row               hang thuc hien
     * @param indexCell         chi muc cot
     */
    public void setServicePlanTypeId(Long servicePlanTypeId, List<ServicePlanTypeEntity> servicePlanTypes, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        for (ServicePlanTypeEntity servicePlanType : servicePlanTypes) {
            if (servicePlanType.getServicePlanTypeId().equals(servicePlanTypeId)) {
                cell.setCellValue(servicePlanType.getName());
                break;
            } else {
                cell.setCellValue("");
            }
        }
    }

    /**
     * Xu ly export bang cuoc gia ve xe
     * Set tram hoac doan cho excel
     *
     * @param stationId  ma dinh danh tram
     * @param stageId    ma dinh danh doan
     * @param stationMap du lieu tram
     * @param stagesMap  du lieu doan
     * @param row        hang thuc hien
     * @param indexCell  chi muc cot
     */
    public void setStationOrStage(Long stationId, Long stageId, Map<Long, String> stationMap, Map<Long, String> stagesMap, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        if (stationId != null) {
            cell.setCellValue(stationMap.get(stationId));
        } else if (stageId != null) {
            cell.setCellValue(stagesMap.get(stageId));
        } else {
            cell.setCellValue("");
        }
    }

    /**
     * Xu ly export bang cuoc gia ve xe
     * Set value
     *
     * @param value     gia tri du lieu
     * @param row       hang thuc hien ghi du lieu
     * @param indexCell chi muc cot
     */
    public void setValueCell(Object value, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
    }

    /**
     * Xu ly export bang cuoc gia ve xe
     * <p>
     * Set status cho export file
     *
     * @param statusId  ma trang thai
     * @param row       hang thuc hien ghi du lieu
     * @param indexCell chi muc cua cot
     */
    public void setStatus(Long statusId, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        switch (statusId.toString()) {
            case "1":
                cell.setCellValue("Đang chờ duyệt");
                break;
            case "2":
                cell.setCellValue("Đã duyệt");
                break;
            case "3":
                cell.setCellValue("Từ chối duyệt");
                break;
            case "4":
                cell.setCellValue("Hủy hiệu lực");
                break;
            case "5":
                cell.setCellValue("Đã xóa");
                break;
            default:
                cell.setCellValue("");
                break;
        }
    }

    /**
     * Xu ly export bang cuoc gia ve xe
     * Set gia tri name cho truong danh muc
     *
     * @param categoryId ma dinh danh danh muc dung chung
     * @param categories danh sach danh muc dung chung
     * @param row        Hang thuc hien
     * @param indexCell  chi muc cot
     */
    public void setCategoryName(Long categoryId, List<LinkedTreeMap<?, ?>> categories, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        if (categoryId != null) {
            for (LinkedTreeMap<?, ?> category : categories) {
                if (categoryId == category.get("id")) {
                    cell.setCellValue(category.get("name").toString());
                    break;
                } else {
                    cell.setCellValue("");
                }
            }
        } else {
            cell.setCellValue("");
        }
    }

    /**
     * Tai file template service plan
     *
     * @param authentication Ma xac thuc
     * @return duong dan tai file ket qua
     */
    @Override
    public String downloadServicePlanTemplate(Authentication authentication) throws IOException {
        String result = System.getProperty("user.dir");
        String token = FnCommon.getStringToken(authentication);
        List<LinkedHashMap<?, ?>> stations = stationService.findAllStations(token);
        List<LinkedHashMap<?, ?>> stages = stageService.findAllStages(token);
        List<LinkedHashMap<?, ?>> bots = botService.findAllBots(token);
        List<LinkedHashMap<?, ?>> lanes = laneService.findAllLanes(token);
        List<LinkedHashMap<?, ?>> vehicleGroupIds = vehicleGroupService.findAllVehicleGroup(token);
        List<ServicePlanTypeEntity> servicePlanTypes = servicePlanTypeServiceJPA.findAll();
        if (readAndRewritingExcelFile(stations, stages, bots, lanes, servicePlanTypes, vehicleGroupIds)) {
            result = System.getProperty("user.dir") + File.separator + "serviceplan_template_result.xlsx";
        }
        return result;
    }

    /**
     * Tai file template service plan
     * Thuc hien ghi noi dung file template service plan
     *
     * @param stations                  Danh sach tram
     * @param stages                    danh sach doan
     * @param bots                      danh sach bot
     * @param lanes                     danh sach lan
     * @param servicePlanTypeEntityList danh sach bang cuoc gia ve
     * @return boolean
     */
    public boolean readAndRewritingExcelFile(List<LinkedHashMap<?, ?>> stations, List<LinkedHashMap<?, ?>> stages,
                                             List<LinkedHashMap<?, ?>> bots, List<LinkedHashMap<?, ?>> lanes,
                                             List<ServicePlanTypeEntity> servicePlanTypeEntityList,
                                             List<LinkedHashMap<?, ?>> vehicleGroupIds) throws IOException {
        boolean result = false;

        try (InputStream inp = getClass().getResourceAsStream("/template/import/Template_Service_Plan_Download.xlsx"); Workbook wb = WorkbookFactory.create(inp)) {
            Sheet sheet = wb.getSheetAt(1);
            setServicePlanTypeId(0, 1, servicePlanTypeEntityList, sheet, wb);
            doRewritingContentExcel(3, 4, stations, sheet, wb);
            doRewritingContentExcel(6, 7, stages, sheet, wb);
            doRewritingContentExcel(12, 13, bots, sheet, wb);
            doRewritingContentExcel(9, 10, vehicleGroupIds, sheet, wb);
            doRewritingContentExcel(15, 16, lanes, sheet, wb);
            bindingDataToDropdownListExcel(wb, stations, stages, bots, lanes, vehicleGroupIds);
            try (OutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + File.separator + "serviceplan_template_result.xlsx")) {
                wb.write(fileOut);
                result = true;
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Lay danh sach danh muc loi : %s ", e));
        }
        return result;
    }

    /**
     * Thuc hien ghi đè noi dung file template
     *
     * @param fromCell cot bat dau
     * @param toCell   vi tri cot ket thuc
     * @param sheet    sheet duoc lua chon
     * @param values   Danh sach gia tri can set
     * @param wb       noi dung file
     */
    public void doRewritingContentExcel(int fromCell, int toCell, List<LinkedHashMap<?, ?>> values,
                                        Sheet sheet, Workbook wb) {
        if (values != null) {
            int index = 2;
            for (LinkedHashMap<?, ?> value : values) {
                Row row = sheet.getRow(index) != null ? sheet.getRow(index) : sheet.createRow(index);
                Cell firstCell = row.createCell(fromCell);
                firstCell.setCellStyle(FnCommon.createAndSetStyleCell(wb));
                String valueId = value.get("id") != null ? String.valueOf(value.get("id")) : "";
                firstCell.setCellValue(valueId);
                Cell lastCell = row.createCell(toCell);
                lastCell.setCellStyle(FnCommon.createAndSetStyleCell(wb));
                String valueName = value.get("name") != null ? value.get("name").toString() : "";
                lastCell.setCellValue(valueName + "-" + valueId);
                index++;
            }

        }

    }

    /**
     * Xu ly export bang cuoc gia ve xe
     * Set loai ve
     *
     * @param fromCell         Cot bat dau
     * @param toCell           Cot ket thuc
     * @param servicePlanTypes Danh sach bang cuoc gia ve
     * @param sheet            sheet lua chon
     * @param wb               File excel
     */
    public void setServicePlanTypeId(int fromCell, int toCell, List<ServicePlanTypeEntity> servicePlanTypes, Sheet sheet, Workbook wb) {
        if (servicePlanTypes != null) {
            int index = 2;
            for (ServicePlanTypeEntity servicePlanType : servicePlanTypes) {
                Row row = sheet.getRow(index) != null ? sheet.getRow(index) : sheet.createRow(index);
                Cell firstCell = row.createCell(fromCell);
                firstCell.setCellStyle(FnCommon.createAndSetStyleCell(wb));
                firstCell.setCellValue(servicePlanType.getServicePlanTypeId());
                Cell lastCell = row.createCell(toCell);
                lastCell.setCellStyle(FnCommon.createAndSetStyleCell(wb));
                lastCell.setCellValue(servicePlanType.getName() + "-" + servicePlanType.getServicePlanTypeId());
                index++;
            }
            int lastCell = !servicePlanTypes.isEmpty() ? servicePlanTypes.size() : 2;
            doBindingDataToDropdownListExcel(wb, 3, lastCell, "B", 4, 4, 2, 2);
        }
    }

    /**
     * Thuc hien binding du lieu vao dropdownlist excel
     *
     * @param wb        File excel
     * @param startCell Cot bat dau cua sheet categories
     * @param lastCell  Cot ket thuc cua sheet categories
     * @param cellName  Ten cot
     * @param firstRow  Hang bat dau
     * @param lastRow   Hang ket thuc
     * @param firstCol  cot bat dau fill du lieu cua sheet template
     * @param lastCol   Cot ket thuc fill du lieu cua sheet template
     */
    public void doBindingDataToDropdownListExcel(Workbook wb, int startCell, int lastCell, String cellName, int firstRow, int lastRow, int firstCol, int lastCol) {
        String referFormula = "'Categories'!$" + cellName + "$" + String.valueOf(startCell) + ":$" + cellName + "$" + String.valueOf(lastCell + startCell - 1);
        Sheet sheet = wb.getSheetAt(0);
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint)
                dvHelper.createFormulaListConstraint(referFormula);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(
                dvConstraint, addressList);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    /**
     * binding du lieu vao dropdownlist excel
     *
     * @param wb       File excel
     * @param stations Danh sach danh muc tram
     * @param stages   Danh sach danh muc doan
     * @param bots     danh sach danh muc bot
     * @param lanes    danh sach danh muc lan
     */
    public void bindingDataToDropdownListExcel(Workbook wb, List<LinkedHashMap<?, ?>> stations,
                                               List<LinkedHashMap<?, ?>> stages, List<LinkedHashMap<?, ?>> bots,
                                               List<LinkedHashMap<?, ?>> lanes, List<LinkedHashMap<?, ?>> vehicleGroupIds) {
        if (stations != null) {
            int lastCell = !stations.isEmpty() ? stations.size() : 2;
            doBindingDataToDropdownListExcel(wb, 3, lastCell, "E", 4, 4, 3, 3);
        }
        if (stages != null) {
            int lastCell = !stages.isEmpty() ? stages.size() : 2;
            doBindingDataToDropdownListExcel(wb, 3, lastCell, "H", 4, 4, 4, 4);
        }
        if (bots != null) {
            int lastCell = !bots.isEmpty() ? bots.size() : 2;
            doBindingDataToDropdownListExcel(wb, 3, lastCell, "N", 4, 4, 14, 14);
        }
        if (vehicleGroupIds != null) {
            int lastCell = !vehicleGroupIds.isEmpty() ? vehicleGroupIds.size() : 2;
            doBindingDataToDropdownListExcel(wb, 3, lastCell, "K", 4, 4, 5, 5);
        }


    }

    /**
     * Xem chi tiet ban ghi gia ve
     *
     * @param servicePlanId
     * @return
     */
    @Override
    public Object findTicketPricesById(Long servicePlanId) {
        ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.findById(servicePlanId).get();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        ServicePlanDTO servicePlanDTO = modelMapper.map(servicePlanEntity, ServicePlanDTO.class);
        List<BotRevenueShareEntity> botRevenueShareEntityList = botRevenueShareServiceJPA.findByServicePlanId(servicePlanId);
        List<BotRevenueShareDTO> botRevenueShareDTOList = new ArrayList<>();
        for (BotRevenueShareEntity botRevenueShareEntity : botRevenueShareEntityList) {
            BotRevenueShareDTO botRevenueShareDTO = modelMapper.map(botRevenueShareEntity, BotRevenueShareDTO.class);
            botRevenueShareDTOList.add(botRevenueShareDTO);
        }
        List<AttachmentFileEntity> attachmentFileEntityList = attachmentFileRepositoryJPA.
                findByObjectIdAndAndAttachmentType(servicePlanId, AttachmentFileEntity.ATTACH_TYPE.SERVICE_PLAN.value);
        List<AttachmentFileDTO> attachmentFileDTOList = new ArrayList<>();
        for (AttachmentFileEntity attachmentFileEntity : attachmentFileEntityList) {
            AttachmentFileDTO attachmentFileDTO = modelMapper.map(attachmentFileEntity, AttachmentFileDTO.class);
            attachmentFileDTOList.add(attachmentFileDTO);
        }
        if (botRevenueShareDTOList.size() > Constants.SIZE_LIST_ZERO) {
            servicePlanDTO.setBotRevenueShareList(botRevenueShareDTOList);
        }
        if (attachmentFileDTOList.size() > Constants.SIZE_LIST_ZERO) {
            servicePlanDTO.setAttachmentFileDTOS(attachmentFileDTOList);
        }
        return servicePlanDTO;
    }

    /**
     * Huy hieu luc gia ve
     *
     * @param servicePlanId
     * @param authentication
     * @return
     */
    @Override
    public Object cancelTicketPrice(Long servicePlanId, Authentication authentication) {
        Date date = new Date(System.currentTimeMillis());
        ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.findById(servicePlanId).get();
        servicePlanEntity.setDestroyDate(date);
        servicePlanEntity.setDestroyUser(FnCommon.getUserLogin(authentication));
        servicePlanEntity.setStatus(ServicePlanEntity.Status.CANCEL.value);
        if (checkEfficiency(servicePlanEntity, date)) {
            servicePlanEntity.setEndDate(date);
        }
        servicePlanEntity.setStatus(ServicePlanEntity.Status.CANCEL.value);
        return servicePlanServiceJPA.save(servicePlanEntity);
    }

    /**
     * Update thong tin gia ve
     *
     * @param dataParams
     * @param authentication
     * @return
     */
    @Override
    public Object updateTicketPrices(ServicePlanDTO dataParams, Authentication authentication) throws EtcException, Exception {
        if (dataParams.getAutoRenew() != 0 && dataParams.getUseDay() == null) {
            throw new EtcException("crm.validate.data.service.plan.use.day.not.exits");
        }
        Date date = new Date(System.currentTimeMillis());
        String userLogin = FnCommon.getUserLogin(authentication);
        ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.findByServicePlanId(dataParams.getServicePlanId());
        if (!servicePlanEntity.getServicePlanCode().equals(dataParams.getServicePlanCode())) {
            checkServicePlanCode(dataParams);
        }
        dataParams.updateValueTicketPrices(servicePlanEntity);
        servicePlanEntity.setUpdateDate(date);
        servicePlanEntity.setUpdateUser(userLogin);
        // update bot revenue
        List<BotRevenueShareEntity> botRevenueShareEntityList = botRevenueShareServiceJPA.findByServicePlanId(servicePlanEntity.getServicePlanId());
        for (BotRevenueShareEntity botRevenueShareEntity : botRevenueShareEntityList) {
            for (BotRevenueShareDTO botRevenueShareDTO : dataParams.getBotRevenueShareList()) {
                if (botRevenueShareEntity.getBotId().equals(botRevenueShareDTO.getBotId())) {
                    botRevenueShareEntity.setBotRevenue(botRevenueShareDTO.getBotRevenue());
                    botRevenueShareServiceJPA.save(botRevenueShareEntity);
                }
            }
        }
        if (!FnCommon.isNullOrEmpty(dataParams.getAttachmentFileDTOS())) {
            addAttachedFile(dataParams.getAttachmentFileDTOS(), date, userLogin, servicePlanEntity.getServicePlanId());
        }
        return servicePlanServiceJPA.save(servicePlanEntity);
    }

    /**
     * Tim kiem ban ghi gia ve theo loai PT, tram, doan, lan vao,lan ra
     *
     * @param dataParams
     * @return
     */
    @Override
    public List<ServicePlanDTO> findTicketPricesExits(ServicePlanDTO dataParams) {
        ResultSelectEntity result = servicePlanRepository.findTicketPricesExits(dataParams);
        return (List<ServicePlanDTO>) result.getListData();
    }

    /**
     * Xoa file dinh kem
     *
     * @param profileId
     */
    @Override
    public void deleteProfile(Long profileId) {
        AttachmentFileEntity attachmentFileEntity = attachmentFileRepositoryJPA.findById(profileId).orElseThrow(new EtcException("common.profiles.not-exists"));
        new File(attachmentFileEntity.getDocumentPath()).deleteOnExit();
        attachmentFileRepositoryJPA.deleteById(attachmentFileEntity.getAttachmentFileId());
    }

    /**
     * Check gia ve da ton tai tren tram doan
     *
     * @param itemParamsEntity
     */
    private void checkExitsTicketPrice(ServicePlanDTO itemParamsEntity) {
        List<ServicePlanDTO> result = servicePlanService.findTicketPricesExits(itemParamsEntity);
        if (result.size() > Constants.SIZE_LIST_ZERO) {
            for (ServicePlanDTO servicePlanDTO : result) {
                if (servicePlanDTO.getEndDate() == null && itemParamsEntity.getEndDate() == null) {
                    throw new EtcException("crm.validate.data.service.plan.exist");
                }
                if (servicePlanDTO.getEndDate() == null && itemParamsEntity.getEndDate() != null) {
                    checkDateNotNull(servicePlanDTO, itemParamsEntity.getStartDate(), itemParamsEntity.getEndDate());
                }
                if (servicePlanDTO.getEndDate() != null && itemParamsEntity.getEndDate() == null) {
                    checkEndDateIsNull(servicePlanDTO.getEndDate(), itemParamsEntity.getStartDate());
                }
                if (servicePlanDTO.getEndDate() != null && itemParamsEntity.getEndDate() != null) {
                    checkStartAndEndDateEfficiency(servicePlanDTO, itemParamsEntity.getStartDate(), itemParamsEntity.getEndDate());
                }
            }
        }
    }

    /**
     * Check exits service plan case param end date null
     *
     * @param endDate
     * @param startDate
     */
    private void checkEndDateIsNull(Date endDate, Date startDate) {
        if (!startDate.after(endDate)) {
            throw new EtcException("crm.validate.data.service.plan.exist");
        }
    }

    /**
     * Check exits service plan case param not null
     *
     * @param servicePlanDTO
     * @param startDate
     * @param endDate
     */
    private void checkDateNotNull(ServicePlanDTO servicePlanDTO, Date startDate, Date endDate) {
        if (!(startDate.before(servicePlanDTO.getStartDate()) && endDate.before(servicePlanDTO.getStartDate()))) {
            throw new EtcException("crm.validate.data.service.plan.exist");
        }
    }

    /**
     * Check ngay hieu luc cua ban ghi them moi
     *
     * @param servicePlanDTO
     * @param startDate
     * @param endDate
     */
    private boolean checkStartAndEndDateEfficiency(ServicePlanDTO servicePlanDTO, Date startDate, Date endDate) {
        if ((startDate.before(servicePlanDTO.getStartDate()) && endDate.before(servicePlanDTO.getStartDate()))
                || (startDate.after(servicePlanDTO.getEndDate()) && endDate.after(servicePlanDTO.getEndDate()))) {
            return true;
        }
        return false;
    }

    /**
     * Them attach file
     *
     * @param attachmentFileDTOS
     * @param date
     * @param userLogin
     * @param servicePlanId
     */
    private void addAttachedFile(List<AttachmentFileDTO> attachmentFileDTOS, Date date, String userLogin, Long servicePlanId) throws Exception {
        for (AttachmentFileDTO attachmentFileDTO : attachmentFileDTOS) {
            AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity();
            if (!Base64.isBase64(attachmentFileDTO.getFileBase64())) {
                throw new EtcException("crm.validate.file.format.base64.invalid");
            }
            byte[] file = Base64.decodeBase64(attachmentFileDTO.getFileBase64());
            String filePath = String.format("/%s/%s", servicePlanId, UUID.randomUUID().toString() + "-" + attachmentFileDTO.getDocumentName());
            if (!FnCommon.checkBriefcaseValid(filePath, file, briefcaseMaxFileSize)) {
                throw new EtcException("common.validate.briefcase.invalid");
            }
            attachmentFileEntity.setAttachmentType(AttachmentFileEntity.ATTACH_TYPE.SERVICE_PLAN.value);
            attachmentFileEntity.setStatus(AttachmentFileEntity.STATUS.ACTIVE.value);
            attachmentFileEntity.setObjectId(servicePlanId);
            attachmentFileEntity.setDocumentPath(filePath);
            attachmentFileEntity.setDocumentName(attachmentFileDTO.getDocumentName());
            attachmentFileEntity.setCreateDate(date);
            attachmentFileEntity.setCreateUser(userLogin);
            fileService.uploadFile(filePath, file);
            attachmentFileRepositoryJPA.save(attachmentFileEntity);
        }
    }

    /**
     * Check trung ma gia ve
     *
     * @param itemParamsEntity
     */
    private void checkServicePlanCode(ServicePlanDTO itemParamsEntity) {
        List<ServicePlanEntity> servicePlanEntityList = servicePlanServiceJPA.findByServicePlanCodeAndStatus(itemParamsEntity.getServicePlanCode(), ServicePlanEntity.Status.DELETED.value);
        if (servicePlanEntityList.size() > Constants.SIZE_LIST_ZERO) {
            throw new EtcException("crm.validate.data.service.plan.exist");
        }
    }


    /**
     * Check ngay hieu luc phe duyet ban ghi
     *
     * @param servicePlanId
     * @return
     */
    private boolean checkEffDate(Long servicePlanId) {
        ServicePlanEntity servicePlanEntity = servicePlanServiceJPA.getOne(servicePlanId);
        ServicePlanDTO servicePlanDTO = (ServicePlanDTO) FnCommon.convertObjectToObject(servicePlanEntity, ServicePlanDTO.class);
        ResultSelectEntity result = servicePlanRepository.findTicketPriceExitsEffDate(servicePlanDTO);
        List<ServicePlanDTO> servicePlanDTOList = (List<ServicePlanDTO>) result.getListData();
        if (servicePlanDTOList.size() > Constants.SIZE_LIST_ZERO) {
            return true;
        }
        return false;
    }

    /**
     * Set vehicle group name
     *
     * @param vehicleGroupId
     * @param vehicleGroupMap
     * @param row
     * @param indexCell
     */
    private void setVehicleGroup(Long vehicleGroupId, Map<Long, String> vehicleGroupMap, Row row, int indexCell) {
        Cell cell = row.createCell(indexCell);
        if (vehicleGroupId != null) {
            cell.setCellValue(vehicleGroupMap.get(vehicleGroupId));
        } else {
            cell.setCellValue("");
        }
    }

    /**
     * Lay du lieu giay to ca nhan
     *
     * @param dataParams
     * @return
     */
    @Override
    public ServiceFeeDTO getChangeFee(ServiceFeeDTO dataParams) {
        ServiceFeeEntity dataResult = null;
        if (dataParams.getActReasonId() != null) {
            dataResult = serviceFeeRepositoryJPA.getByActTypeIdAndActReasonId(dataParams.getActionTypeId(), dataParams.getActReasonId()).get(0);
        } else {
            dataResult = serviceFeeRepositoryJPA.getByActTypeId(dataParams.getActionTypeId()).get(0);
        }
        return new ServiceFeeDTO().toResponse(dataResult);
    }

    /**
     * Tinh gia ve theo BOO
     *
     * @param authentication
     * @param requestGetFeeChargeTicketDTO
     * @return
     * @throws IOException
     */
    @Override
    public ServicePlanFeeDTO getFeeNew(Authentication authentication, RequestGetFeeChargeTicketDTO requestGetFeeChargeTicketDTO) throws IOException {
        ServicePlanFeeDTO servicePlanFeeDTO = new ServicePlanFeeDTO();
        servicePlanFeeDTO.setListServicePlan(new ArrayList<>());
        String token = FnCommon.getStringToken(authentication);
        Set<String> plateNumberExist = new HashSet<>();
        for (int i = 0; i < requestGetFeeChargeTicketDTO.getServicePlanDTOList().size(); i++) {
            ServicePlanDTO servicePlanDTO = requestGetFeeChargeTicketDTO.getServicePlanDTOList().get(i);
            java.util.Date dateExp = FnCommon.setTimeOfDate(servicePlanDTO.getExpDate(), 23, 59, 59);
            servicePlanDTO.setExpDate(FnCommon.convertDateToStringOther(dateExp, Constants.COMMON_DATE_TIME_FORMAT));
            if (!FnCommon.validatePlateContainsTVX(servicePlanDTO.getPlateNumber())) {
                VehicleEntity vehicleEntity = vehicleRepositoryJPA.findByVehicleId(servicePlanDTO.getVehicleId());
                String plateTypeCode = FnCommon.mappingPlateTypeBOO2ToBOO1(vehicleEntity.getPlateTypeCode());
                servicePlanDTO.setPlateNumber(servicePlanDTO.getPlateNumber() + plateTypeCode);
            }
            if (Constants.BOO2.equals(servicePlanDTO.getBooCode())) {
                getFeeBoo2(servicePlanDTO, token, servicePlanFeeDTO, plateNumberExist, authentication);
            }
            if (Constants.BOO1.equals(servicePlanDTO.getBooCode())) {
                getFeeBoo1(authentication, servicePlanDTO, servicePlanFeeDTO, plateNumberExist);
            }
        }
        if (plateNumberExist.size() > 0) {
            String plateNumberArr = String.join(",", plateNumberExist);
            servicePlanFeeDTO.setServicePlanVehicleDuplicate(plateNumberArr);
        }
        return servicePlanFeeDTO;
    }

    /**
     * @param servicePlanDTO
     * @param stageId
     * @param stationId
     * @param plateType
     * @param reqCalculatorTicketDTO
     * @return
     */
    private Map<String, String> checkVehicleCouldChargeTicket(ServicePlanDTO servicePlanDTO, Long stageId, Long stationId,
                                                              String plateType, ReqCalculatorTicketDTO reqCalculatorTicketDTO) {
        Map<String, String> mapException = new HashMap<>();
        List<ExceptionListEntity> dataException = exceptionListServiceJPA.findAllByPlateNumberAndEpcAndStatus(reqCalculatorTicketDTO.getPlate(), reqCalculatorTicketDTO.getEtag(), ExceptionListEntity.Status.APPROVAL.value);
        if (dataException != null) {
            for (ExceptionListEntity exceptionListEntity : dataException) {
                if (FnCommon.isNullOrEmpty(plateType) || !String.valueOf(exceptionListEntity.getLicensePlateType()).equalsIgnoreCase(plateType) || (ExceptionListEntity.ExceptionType.VEHICLE_PRIORITY.value.equals(exceptionListEntity.getExceptionType()) && isDateExceptionListEfficiency(exceptionListEntity, servicePlanDTO))) {
                    continue;
                }
                if (stageId != null) {
                    if (ExceptionListEntity.ExceptionType.VEHICLE_FORBIDDEN.value.equals(exceptionListEntity.getExceptionType())
                            && stageId.equals(exceptionListEntity.getStageId()) && isDateExceptionListEfficiency(exceptionListEntity, servicePlanDTO)) {
                        Map<String, String> parameter = new HashMap<>();
                        parameter.put("PARAMETER", reqCalculatorTicketDTO.getPlate());
                        throw new EtcException("crm.ticket.purchase.not.allow", parameter);
                    }
                    if (exceptionListEntity.getStageId() != null && exceptionListEntity.getStageId().equals(stageId) && isDateExceptionListEfficiency(exceptionListEntity, servicePlanDTO)) {
                        setValueToMapException(exceptionListEntity, mapException);
                    }
                }
                if (stationId != null) {
                    if (ExceptionListEntity.ExceptionType.VEHICLE_FORBIDDEN.value.equals(exceptionListEntity.getExceptionType())
                            && stationId.equals(exceptionListEntity.getStationId()) && isDateExceptionListEfficiency(exceptionListEntity, servicePlanDTO)) {
                        Map<String, String> parameter = new HashMap<>();
                        parameter.put("PARAMETER", reqCalculatorTicketDTO.getPlate());
                        throw new EtcException("crm.ticket.purchase.not.allow", parameter);
                    }
                    if (exceptionListEntity.getStationId() != null && exceptionListEntity.getStationId().equals(stationId) && isDateExceptionListEfficiency(exceptionListEntity, servicePlanDTO)) {
                        setValueToMapException(exceptionListEntity, mapException);
                    }
                }
                if (stageId == null && stationId == null && exceptionListEntity.getExceptionVehicleType() != null) {
                    mapException.put(EXCEPTION_VEHICLE_TYPE, String.valueOf(exceptionListEntity.getExceptionVehicleType()));
                }
            }
        }
        return mapException;
    }

    /**
     * Set gia tri de gui request sang BOO1
     *
     * @param authentication
     * @param servicePlanDTO
     * @return
     */
    private ReqCalculatorTicketDTO setValueToGetFeeBOO1(Authentication authentication, ServicePlanDTO servicePlanDTO) {
        ReqCalculatorTicketDTO reqCalculatorTicketDTO = new ReqCalculatorTicketDTO();
        reqCalculatorTicketDTO.setStation_type(FnCommon.convertStationType(servicePlanDTO.getStationType()));
        if (servicePlanDTO.getStageId() == null) {
            reqCalculatorTicketDTO.setStation_in_id(servicePlanDTO.getStationId());
        } else {
            StageBooDTO stageDTO = this.getStageById(FnCommon.getStringToken(authentication), servicePlanDTO.getStageId().toString());
            if (stageDTO.getData() != null) {
                reqCalculatorTicketDTO.setStation_in_id(stageDTO.getData().getStation_input_id());
                reqCalculatorTicketDTO.setStation_out_id(stageDTO.getData().getStation_output_id());
            }
        }
        reqCalculatorTicketDTO.setTicket_type(FnCommon.convertToTicketType(String.valueOf(servicePlanDTO.getServicePlanTypeId())));

        SimpleDateFormat formatBOO1 = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT_BOO);
        reqCalculatorTicketDTO.setStart_date(formatBOO1.format(FnCommon.convertStringToDate(servicePlanDTO.getEffDate(), Constants.COMMON_DATE_FORMAT)));
        reqCalculatorTicketDTO.setEnd_date(formatBOO1.format(FnCommon.convertStringToDate(servicePlanDTO.getExpDate(), Constants.COMMON_DATE_FORMAT)));

        reqCalculatorTicketDTO.setPlate(servicePlanDTO.getPlateNumber());
        reqCalculatorTicketDTO.setEtag(servicePlanDTO.getEpc());
        reqCalculatorTicketDTO.setVehicle_type(servicePlanDTO.getVehicleGroupId());
        reqCalculatorTicketDTO.setRegister_vehicle_type(String.valueOf(servicePlanDTO.getVehicleTypeId()));
        reqCalculatorTicketDTO.setWeight_goods(servicePlanDTO.getCargoWeight());
        reqCalculatorTicketDTO.setWeight_all(servicePlanDTO.getNetWeight());
        reqCalculatorTicketDTO.setRequest_datetime(System.currentTimeMillis());
        reqCalculatorTicketDTO.setSeat(servicePlanDTO.getSeatNumber());
        return reqCalculatorTicketDTO;
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
     * Gan gia tri tinh vehicleGroup
     *
     * @param servicePlanDTO
     * @return
     */
    private AddVehicleRequestDTO setValueGetVehicleGroup(ServicePlanDTO servicePlanDTO) {
        AddVehicleRequestDTO addVehicleRequestDTO = new AddVehicleRequestDTO();
        addVehicleRequestDTO.setCargoWeight(servicePlanDTO.getCargoWeight());
        addVehicleRequestDTO.setNetWeight(servicePlanDTO.getNetWeight());
        addVehicleRequestDTO.setSeatNumber(servicePlanDTO.getSeatNumber());
        addVehicleRequestDTO.setVehicleTypeId(servicePlanDTO.getVehicleTypeId());
        addVehicleRequestDTO.setStationId(servicePlanDTO.getStationId());
        return addVehicleRequestDTO;
    }


    /**
     * Tinh gia ve theo tram, doan BOO2
     *
     * @param servicePlanDTO
     * @param token
     * @param servicePlanFeeDTO
     */
    private void getFeeBoo2(ServicePlanDTO servicePlanDTO, String token, ServicePlanFeeDTO servicePlanFeeDTO, Set<String> plateNumberExist, Authentication authentication) {
        List<String> vehicleGroupId = null;
        Map<String, String> mapException = new HashMap<>();
        Long vehicleGroupIdOld = null, excVehicleId = null, excTicketId = null;
        AddVehicleRequestDTO addVehicleRequestDTO = setValueGetVehicleGroup(servicePlanDTO);
        if (servicePlanDTO.getVehicleGroupId() == null) {
            throw new EtcException("crm.boo.null.data");
        }
        // Cat bien so xe theo hau to  T, V, X
        String plateType = FnCommon.getPlateTypeBOO1(servicePlanDTO.getPlateNumber());
        servicePlanDTO.setPlateNumber(FnCommon.formatPlateBOO1(servicePlanDTO.getPlateNumber()));
        // Kiem tra xe da ton tai ve chua
        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = servicePlanRepository.checkExistsTicketNew(servicePlanDTO);
        if (servicePlanVehicleDuplicateDTO != null && !FnCommon.isNullOrEmpty(servicePlanVehicleDuplicateDTO.getPlateNumber())) {
            plateNumberExist.add(servicePlanRepository.checkExistsTicketNew(servicePlanDTO).getPlateNumber());
        }
        ReqCalculatorTicketDTO reqCalculatorTicketDTO = setValueToGetFeeBOO1(authentication, servicePlanDTO);
        // Neu la loai phuong tien tinh phi loai 1 hoac 3 va la tram rach mieu thi phai tinh lai vehicleGroupI
        if ((servicePlanDTO.getVehicleGroupId() == 1 || servicePlanDTO.getVehicleGroupId() == 3)
                && servicePlanDTO.getVehicleTypeId() != null
                && STATION_OPEN.equals(servicePlanDTO.getStationType())
                && Constants.RACH_MIEU_STATION.equals(servicePlanDTO.getStationId())
        ) {
            mapException = checkVehicleCouldChargeTicket(servicePlanDTO, null, null, plateType, reqCalculatorTicketDTO);
            vehicleGroupId = vehicleGroupService.getVehicleGroupById(token, addVehicleRequestDTO);
        }
        // Kiem tra khoang thoi gian mua ve co trung chom voi thoi gian hieu luc cua mien giam khong
        List<ExceptionListEntity> data = exceptionListRepository.findAllExceptionEffective(reqCalculatorTicketDTO, servicePlanDTO.getStageId(),
                servicePlanDTO.getStationId(), plateType);
        List<ExceptionListEntity> dataException = exceptionListServiceJPA.findByPlateNumberAndEpcAndStationIdAndStageIdAndStatus(reqCalculatorTicketDTO.getPlate(), reqCalculatorTicketDTO.getEtag(), servicePlanDTO.getStationId(), servicePlanDTO.getStageId(), ExceptionListEntity.Status.APPROVAL.value);
        if (!FnCommon.isNullOrEmpty(data) || (dataException.isEmpty() && FnCommon.isNullOrEmpty(data))) {
            if (servicePlanDTO.getStageId() != null) {
                StageBooDTO stageDTO = this.getStageById(FnCommon.getStringToken(authentication), servicePlanDTO.getStageId().toString());
                if (stageDTO != null && stageDTO.getData() != null) {
                    mapException = checkVehicleCouldChargeTicket(servicePlanDTO, servicePlanDTO.getStageId(),
                            servicePlanDTO.getStationId(), plateType, reqCalculatorTicketDTO);

                }
            } else if (servicePlanDTO.getStationId() != null) {
                StationBooDTO station = this.getStationById(FnCommon.getStringToken(authentication), servicePlanDTO.getStationId().toString());
                if (station != null && station.getData() != null) {
                    mapException = checkVehicleCouldChargeTicket(servicePlanDTO, servicePlanDTO.getStageId(),
                            servicePlanDTO.getStationId(), plateType, reqCalculatorTicketDTO);
                }
            }
            if (isStationOrStage(servicePlanDTO)) {
                if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_VEHICLE_TYPE))) {
                    vehicleGroupId = null;
                    if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_TICKET_TYPE_PROMOTION_ID))) {
                        List<String> list = vehicleGroupService.getVehicleGroupById(token, addVehicleRequestDTO);
                        if (!FnCommon.isNullOrEmpty(list)) {
                            vehicleGroupIdOld = Long.parseLong(list.get(0));
                        }
                    }
                    servicePlanDTO.setVehicleGroupId(Long.parseLong(mapException.get(EXCEPTION_VEHICLE_TYPE)));
                }
                if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_TICKET_TYPE_PROMOTION_ID))) {
                    excTicketId = Long.parseLong(mapException.get(EXCEPTION_TICKET_TYPE_PROMOTION_ID));
                }

                if (!FnCommon.isNullOrEmpty(mapException.get(EXCEPTION_VEHICLE_TYPE_PROMOTION_ID))) {
                    excVehicleId = Long.parseLong(mapException.get(EXCEPTION_VEHICLE_TYPE_PROMOTION_ID));
                }
                List<ServicePlanDTO> listFeeResult = (List<ServicePlanDTO>) servicePlanRepository.getFeeNew(servicePlanDTO, excVehicleId, excTicketId, vehicleGroupId, vehicleGroupIdOld).getListData();
                if (listFeeResult.size() > 1) {
                    listFeeResult.sort((servicePlanDTO1, servicePlanDTO2) -> (int) (servicePlanDTO1.getFee() - servicePlanDTO2.getFee()));
                }
                if (!listFeeResult.isEmpty()) {
                    setValueToResult(listFeeResult, servicePlanDTO, servicePlanFeeDTO);
                }
            }
        }
    }

    /**
     * Tinh gia ve theo tram doan cua BOO1
     *
     * @param authentication
     * @param servicePlanDTO
     * @throws IOException
     */
    private void getFeeBoo1(Authentication authentication, ServicePlanDTO servicePlanDTO,
                            ServicePlanFeeDTO servicePlanFeeDTO, Set<String> plateNumberExist) throws IOException {
        ReqCalculatorTicketDTO reqCalculatorTicketDTO = setValueToGetFeeBOO1(authentication, servicePlanDTO);
        String plateNumberExistCode = servicePlanDTO.getPlateNumber();
        // Cat T,X,V de goi ham check trung trong database
        servicePlanDTO.setPlateNumber(FnCommon.formatPlateBOO1(servicePlanDTO.getPlateNumber()));
        ServicePlanVehicleDuplicateDTO servicePlanVehicleDuplicateDTO = servicePlanRepository.checkExistsTicketNew(servicePlanDTO);
        if (servicePlanVehicleDuplicateDTO != null && !FnCommon.isNullOrEmpty(servicePlanVehicleDuplicateDTO.getPlateNumber())) {
            plateNumberExist.add(servicePlanRepository.checkExistsTicketNew(servicePlanDTO).getPlateNumber());
        }
        // Xet lai T,X,V de goi sang tinh gia ve BOO1
        servicePlanDTO.setPlateNumber(plateNumberExistCode);
        ResCalculatorTicketDTO resCalculatorTicketDTO = boo1Service.calculatorTicketBoo1(reqCalculatorTicketDTO, authentication);
        if (Objects.nonNull(resCalculatorTicketDTO) && Objects.nonNull(resCalculatorTicketDTO.getPrice_amount())) {
            servicePlanDTO.setFee(resCalculatorTicketDTO.getPrice_amount());
            servicePlanFeeDTO.getListServicePlan().add(servicePlanDTO);
        }
    }

    /**
     * Them ket qua tra ve cua danh sach mua ve
     *
     * @param listFeeResult
     * @param servicePlanDTO
     * @param servicePlanFeeDTO
     */
    private void setValueToResult(List<ServicePlanDTO> listFeeResult, ServicePlanDTO servicePlanDTO, ServicePlanFeeDTO servicePlanFeeDTO) {
        ServicePlanDTO feeResult = listFeeResult.get(0);
        feeResult.setPlateNumber(servicePlanDTO.getPlateNumber());
        servicePlanFeeDTO.getListServicePlan().add(feeResult);
    }

    /**
     * Kiem tra ngay uu tien, ngoai le con hieu luc khong
     *
     * @param exceptionListEntity
     * @return
     */
    private boolean isDateExceptionListEfficiency(ExceptionListEntity exceptionListEntity, ServicePlanDTO servicePlanDTO) {
        if (Objects.nonNull(exceptionListEntity.getEffDate()) && Objects.nonNull(exceptionListEntity.getExpDate())) {
            Long effDateException = exceptionListEntity.getEffDate().getTime();
            java.util.Date expDateException = new java.util.Date(exceptionListEntity.getExpDate().getTime());
            expDateException.setHours(23);
            expDateException.setMinutes(59);
            expDateException.setSeconds(59);
            Long effDateTicket = FnCommon.convertStringToDate(servicePlanDTO.getEffDate(), Constants.COMMON_DATE_FORMAT).getTime();
            Long expDateTicket = FnCommon.convertStringToDate(servicePlanDTO.getExpDate(), Constants.COMMON_DATE_TIME_FORMAT).getTime();
            return ((effDateTicket - effDateException) >= 0 && (expDateException.getTime() - expDateTicket) >= 0);
        } else {
            return Objects.nonNull(exceptionListEntity.getEffDate()) && Objects.isNull(exceptionListEntity.getExpDate());
        }
    }


    /***
     * lay thong tin cua doan
     * @param token
     * @return
     */
    private StationBooDTO getStationById(String token, String stationId) {
        String requestUrl = stationUrl.replace("{stationId}", stationId);
        String stationResponse = FnCommon.doGetRequest(requestUrl, null, token);
        return new Gson().fromJson(stationResponse, StationBooDTO.class);
    }

    /**
     * Kiem tra phai la tram hoac doan khong
     *
     * @param servicePlanDTO
     * @return
     */
    private boolean isStationOrStage(ServicePlanDTO servicePlanDTO) {
        return (servicePlanDTO.getStationId() != null || servicePlanDTO.getStageId() != null);
    }

    /**
     * Day cac gia tri ngoai le vao mapException
     *
     * @param exceptionListEntity
     * @param mapException
     */
    private void setValueToMapException(ExceptionListEntity exceptionListEntity, Map<String, String> mapException) {
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

    private <T> T getDataOfOtherModule(String url, Authentication authentication, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(FnCommon.getStringToken(authentication));

        return restTemplate.exchange(url,
                HttpMethod.GET, new HttpEntity<>(headers),
                responseType)
                .getBody();
    }

}
