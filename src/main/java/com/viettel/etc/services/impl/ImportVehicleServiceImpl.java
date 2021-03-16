package com.viettel.etc.services.impl;

import com.viettel.etc.dto.AddVehicleRequestDTO;
import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.UpdateVehicleRequestOCSDTO;
import com.viettel.etc.dto.VehicleAddSuffOfferDTO;
import com.viettel.etc.dto.boo.ReqActivationCheckDTO;
import com.viettel.etc.dto.boo.ReqOnlineEventSyncDTO;
import com.viettel.etc.dto.boo.ResActivationCheckDTO;
import com.viettel.etc.dto.boo.ResOnlineEventDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.repositories.ServicePlanRepository;
import com.viettel.etc.repositories.tables.SaleTransDetailRepositoryJPA;
import com.viettel.etc.repositories.tables.SaleTransRepositoryJPA;
import com.viettel.etc.repositories.tables.VehicleRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransDetailEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransEntity;
import com.viettel.etc.repositories.tables.entities.VehicleEntity;
import com.viettel.etc.services.BooService;
import com.viettel.etc.services.ImportVehicleService;
import com.viettel.etc.services.OCSService;
import com.viettel.etc.services.tables.ExceptionListServiceJPA;
import com.viettel.etc.services.tables.PromotionServiceJPA;
import com.viettel.etc.services.tables.VehicleServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class ImportVehicleServiceImpl implements ImportVehicleService {

    private final Logger LOGGER = LoggerFactory.getLogger(ImportVehicleServiceImpl.class);

    @Autowired
    ExceptionListServiceJPA exceptionListServiceJPA;

    @Autowired
    Boo1ServiceImpl boo1Service;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    ServicePlanRepository servicePlanRepository;

    @Autowired
    OCSService ocsService;

    @Autowired
    VehicleRepositoryJPA vehicleRepositoryJPA;

    @Autowired
    SaleTransRepositoryJPA saleTransRepositoryJPA;

    @Autowired
    SaleTransDetailRepositoryJPA saleTransDetailRepositoryJPA;

    @Autowired
    PromotionServiceJPA promotionServiceJPA;

    @Autowired
    BooService booService;

    private final String BOO1 = "BOO1";
    private final String BOO2 = "BOO2";
    private final String BLACK_LIST = "BL";
    private final String WHILE_LIST = "WL";
    private final String MONTHLY_TICKET = "T";
    private final String QUARTERLY_TICKET = "Q";
    private final String MGV = "MGV";
    private final String MGX = "MGX";

    private final int PLATE_NUMBER = 1;
    private final int ETAG = 2;
    private final int STATION = 3;
    private final int STAGE = 4;
    private final int EFF_DATE = 5;
    private final int EXP_DATE = 6;
    private final int OFFER_ID = 7;
    private final int EXCEPTION_VEHICLE_TYPE = 8;
    private final int BOO = 9;
    private final int RESULT = 10;
    private final int DESC = 11;

    private final int GET_PLATE_NUMBER_RESULT = 3;
    private final int GET_ETAG_RESULT = 4;
    private final int GET_ETAG_DESC = 5;
    private final int BOO1_RES = 6;

    @Override
    public ResponseEntity<?> importBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                if (sheet.getRow(i) != null && hasEmptyAllCellOnRow(sheet.getRow(i), PLATE_NUMBER, BOO)) {
                    Row row = sheet.getRow(i);
                    String plateNumber, rfidSerial, effDateStr = "", expDateStr = "";
                    Long vehicleType = 0L, vehicleGroup = null, seatNumber = 0L, servicePlanType = null, promotionId = null,
                            customerId = Constants.BOO_INFO.CUST_ID, contractId = Constants.BOO_INFO.CONTRACT_ID, station, stage;
                    Double cargoWeight = null;
                    Double grossWeight = null;
                    ServicePlanDTO servicePlanDTO = null;

                    Date effDate, expDate;

                    String plateNumberConvert = row.getCell(PLATE_NUMBER).getStringCellValue().replaceAll("[\\-\\.]", "").trim();
                    if (!FnCommon.validatePlateContainsTVX(plateNumberConvert)) {
                        resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Bien so xe khong dung dinh dang");
                        continue;
                    }

                    plateNumber = FnCommon.formatPlateBOO1(plateNumberConvert);

                    String plateTypeCode = FnCommon.getPlateTypeCodeFromPlateNumber(plateNumberConvert);
                    String boo = row.getCell(BOO).getStringCellValue();
                    String exceptionVehicleType = row.getCell(EXCEPTION_VEHICLE_TYPE).getStringCellValue();
                    String offerId, etag;
                    try {
                        if (!FnCommon.isNullOrEmpty(row.getCell(EFF_DATE).getStringCellValue()) && !FnCommon.isNullOrEmpty(row.getCell(EXP_DATE).getStringCellValue())) {
                            effDate = new SimpleDateFormat(Constants.COMMON_DATE_TIME_FORMAT).parse(row.getCell(EFF_DATE).getStringCellValue());
                            expDate = new SimpleDateFormat(Constants.COMMON_DATE_TIME_FORMAT).parse(row.getCell(EXP_DATE).getStringCellValue());
                            effDateStr = row.getCell(EFF_DATE).getStringCellValue().trim();
                            expDateStr = row.getCell(EXP_DATE).getStringCellValue().trim();
                        } else {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Gia tri effDate va expDate khong hop le");
                            continue;
                        }
                        offerId = String.valueOf((long) row.getCell(OFFER_ID).getNumericCellValue());
                        etag = null;
                        if (row.getCell(ETAG) != null) {
                            etag = row.getCell(ETAG).getStringCellValue().trim();
                        }
                        if (row.getCell(STATION) != null) {
                            station = (long) row.getCell(STATION).getNumericCellValue();
                        } else {
                            station = null;
                        }

                        if (row.getCell(STAGE) != null) {
                            stage = (long) row.getCell(STAGE).getNumericCellValue();
                        } else {
                            stage = null;
                        }

                        rfidSerial = vehicleRepositoryJPA.getNextValSequenceSerial().toString();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Kieu du lieu khong hop le Eception= " + e.toString());
                        continue;
                    }

                    if (BOO1.equals(boo)) {
                        // Neu xe cua BOO1 va etag null thi goi api BOO1 lay thong tin etag
                        ReqActivationCheckDTO req = new ReqActivationCheckDTO();
                        req.setPlate(plateNumberConvert);
                        req.setRequest_id(System.currentTimeMillis());
                        req.setRequest_datetime(System.currentTimeMillis());
                        ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
                        if (res != null && Constants.BOO_STATUS.ACTIVE.equals(res.getStatus())) {
                            etag = res.getEtag();
                            if (!FnCommon.isNullOrEmpty(res.getRegister_vehicle_type())) {
                                vehicleType = Long.parseLong(res.getRegister_vehicle_type());
                            }
                            seatNumber = res.getSeat();
                            cargoWeight = res.getWeight_goods();
                            grossWeight = res.getWeight_all();
                            vehicleGroup = res.getVehicle_type();
                        } else {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Khong tim thay xe ben BOO1");
                            continue;
                        }
                    } else if (BOO2.equals(boo) && FnCommon.isNullOrEmpty(etag)) {
                        // Neu xe cua BOO2 va etag null thi lay thong tin trong bang Vehicle
                        if (vehicleServiceJPA.isBoo2ActVehicle(plateNumber, plateTypeCode)) {
                            VehicleEntity vehicleEntity = vehicleServiceJPA.getActiveByPlateNumberAndPlateTypeCode(plateNumber, plateTypeCode);
                            etag = vehicleEntity.getEpc();
                            vehicleGroup = vehicleEntity.getVehicleGroupId();
                        } else {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Khong tim thay Etag trong bang Vehicle");
                            continue;
                        }
                    }


                    List<VehicleEntity> list = vehicleRepositoryJPA.findByPlateNumberAndPlateTypeCodeAndActiveStatusAndStatus(plateNumber, plateTypeCode, VehicleEntity.Status.ACTIVATED.value, VehicleEntity.ActiveStatus.ACTIVATED.value);
//                    plateNumber = plateNumberConvert + "O";
                    VehicleEntity vehicle = null;
                    if (list.size() > 1) {
                        resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Trung ban ghi vehicle trong bang Vehicle");
                        continue;
                    } else if (list.size() == 1) {
                        vehicle = list.get(0);
                    }
                    if (BOO1.equals(boo) && vehicle == null) {
                        /***
                         * Neu la xe BOO1 thi check trong bang Vehicle
                         * Chua co trong vehicle thi them xe BOO1 va call OC
                         *
                         */
                        if (!vehicleServiceJPA.isAddedContractBoo1(plateNumber, plateTypeCode)) {
                            VehicleEntity vehicleEntity = new VehicleEntity();
                            vehicleEntity.setCustId(Constants.BOO_INFO.CUST_ID);
                            vehicleEntity.setContractId(Constants.BOO_INFO.CONTRACT_ID);
                            vehicleEntity.setPlateNumber(plateNumber);
                            vehicleEntity.setVehicleGroupId(vehicleGroup);
                            vehicleEntity.setStatus(VehicleEntity.Status.ACTIVATED.value);
                            vehicleEntity.setActiveStatus(VehicleEntity.ActiveStatus.ACTIVATED.value);
                            vehicleEntity.setPlateTypeCode(plateTypeCode);
                            vehicleEntity.setEpc(etag);
                            vehicleEntity.setTid(etag);
                            vehicleEntity.setRfidSerial(rfidSerial);
                            vehicleEntity.setRfidType(String.valueOf(Constants.RFIDStatus.ACTIVE.code));
                            vehicleEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
                            vehicleEntity.setEffDate(new java.sql.Date(System.currentTimeMillis()));
                            vehicleEntity.setCreateUser("tungnk");
                            vehicleEntity.setSeatNumber(seatNumber);
                            vehicleEntity.setCargoWeight(cargoWeight);
                            vehicleEntity.setGrossWeight(grossWeight);
                            vehicleEntity.setVehicleTypeId(vehicleType);
                            vehicleEntity.setVehicleGroupId(vehicleGroup);
                            vehicleEntity.setProfileStatus(Constants.BOO_INFO.PROFILE_STATUS);
                            vehicleServiceJPA.save(vehicleEntity);
                        }
                        // Tim xe tren ocs neu khong co thi add vehicle len OCS
                        if (!Objects.equals("Success", ocsService.queryVehicleOcs(etag, authentication).get("description"))) {
                            AddVehicleRequestDTO vehicleRequest = new AddVehicleRequestDTO();
                            vehicleRequest.setContractId(Constants.BOO_INFO.CONTRACT_ID);
                            vehicleRequest.setEpc(etag);
                            vehicleRequest.setPlateNumber(plateNumber);
                            vehicleRequest.setVehicleGroupId(vehicleGroup);
                            vehicleRequest.setEffDate(effDate);
                            vehicleRequest.setExpDate(expDate);
                            vehicleRequest.setPlateTypeCode(plateTypeCode);
                            vehicleRequest.setTid(etag);
                            vehicleRequest.setRfidType(String.valueOf(Constants.RFIDStatus.ACTIVE.code));
                            vehicleRequest.setRfidSerial(rfidSerial);
                            vehicleRequest.setStatus(VehicleEntity.Status.ACTIVATED.value);
                            vehicleRequest.setSeatNumber(seatNumber);
                            vehicleRequest.setCargoWeight(cargoWeight);
                            OCSResponse ocsResponse = ocsService.createVehicleOCS(vehicleRequest, authentication, Constants.ACT_TYPE.BOO1_SYNC);
                            if (!FnCommon.checkOcsCode(ocsResponse)) {
                                resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Them moi xe len OCS loi " + ocsResponse.getDescription());
                                continue;
                            }
                        }
                    } else if (BOO2.equals(boo) && vehicle != null) {
                        customerId = vehicle.getCustId();
                        contractId = vehicle.getContractId();
                    }

                    // MGX MGV
                    if (MGV.equals(exceptionVehicleType) || MGX.equals(exceptionVehicleType)
                            || MONTHLY_TICKET.equals(exceptionVehicleType) || QUARTERLY_TICKET.equals(exceptionVehicleType)) {
                        if (MONTHLY_TICKET.equals(exceptionVehicleType)) {
                            servicePlanType = Constants.SERVICE_PLAN_TYPE_MONTH;
                        } else if (QUARTERLY_TICKET.equals(exceptionVehicleType)) {
                            servicePlanType = Constants.SERVICE_PLAN_TYPE_QUART;
                        } else {
                            servicePlanType = null;
                        }
                        servicePlanDTO = servicePlanRepository.getServicePlan(station, stage, servicePlanType, vehicleGroup, effDateStr, expDateStr, offerId);

                        // khong lay duoc bang gia
                        if (servicePlanDTO == null) {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Khong tim thay gia ve trong ServicePlan");
                            continue;
                        } else {
                            // kiem tra xem promotionId co ton tai khong
                            if (servicePlanDTO.getPromotionId() == null || !promotionServiceJPA.existsById(servicePlanDTO.getPromotionId())) {
                                resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Khong tim thay promotionId voi offerid =" + offerId);
                                continue;
                            } else {
                                promotionId = servicePlanDTO.getPromotionId();
                            }

                        }
                    }
                    if ((MGV.equals(exceptionVehicleType) || MGX.equals(exceptionVehicleType)) &&
                            (Constants.SERVICE_PLAN_TYPE_MONTH.equals(servicePlanDTO.getServicePlanTypeId())
                                    || Constants.SERVICE_PLAN_TYPE_QUART.equals(servicePlanDTO.getServicePlanTypeId()))) {
                        // Thanh cong thi insert vao exception list
                        ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
                        if (station != null) {
                            exceptionListEntity.setStationId(station);
                        }
                        exceptionListEntity.setPlateNumber(plateNumber);
                        exceptionListEntity.setEpc(etag);
                        exceptionListEntity.setTid(etag);
                        exceptionListEntity.setRfidSerial(rfidSerial);
                        exceptionListEntity.setVehicleTypeId(vehicleGroup);
                        exceptionListEntity.setExceptionType(FnCommon.mappingExceptionListType(exceptionVehicleType));
                        exceptionListEntity.setStatus(ExceptionListEntity.Status.APPROVAL.value);
                        if (!FnCommon.isNullOrEmpty(plateTypeCode)) {
                            exceptionListEntity.setLicensePlateType(Long.parseLong(plateTypeCode));
                        }
                        exceptionListEntity.setDescription("Import tu file");
                        exceptionListEntity.setEffDate(new java.sql.Date(effDate.getTime()));
                        exceptionListEntity.setExpDate(new java.sql.Date(expDate.getTime()));
                        exceptionListEntity.setContractId(String.valueOf(Constants.BOO_INFO.CONTRACT_ID));
                        exceptionListEntity.setCreateUser("tungnk");
                        exceptionListEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
                        exceptionListEntity.setApprovedDate(new java.sql.Date(System.currentTimeMillis()));
                        exceptionListEntity.setApprovedUser("tungnk");
                        exceptionListEntity.setOrgPlateNumber(row.getCell(PLATE_NUMBER).getStringCellValue());
                        exceptionListEntity.setOcsCode(offerId);
                        exceptionListEntity.setPromotionId(promotionId);
                        exceptionListServiceJPA.save(exceptionListEntity);
                    } else {
                        VehicleAddSuffOfferDTO suffOffer = new VehicleAddSuffOfferDTO();
                        suffOffer.setEpc(etag);
                        suffOffer.setEffDate(effDate);
                        suffOffer.setExpDate(expDate);
                        suffOffer.setOfferId(offerId);
                        suffOffer.setOfferLevel(Constants.OFFER_LEVEL_DEFAULT.toString());
                        suffOffer.setAutoRenew("0");    //ve khong gia han tu dong
                        OCSResponse ocsResponse = ocsService.addSupOffer(suffOffer, authentication, Constants.ACT_TYPE.IMPORT_BL_WL_T_Q, contractId, null, null, null);
                        if (FnCommon.checkOcsCode(ocsResponse)) {

                            // Neu la xe uu tien hoac cam thi insert vao exception list
                            if (WHILE_LIST.equals(exceptionVehicleType) || BLACK_LIST.equals(exceptionVehicleType)) {
                                // Thanh cong thi insert vao exception list
                                ExceptionListEntity exceptionListEntity = new ExceptionListEntity();
                                if (station != null) {
                                    exceptionListEntity.setStationId(station);
                                }
                                exceptionListEntity.setPlateNumber(plateNumber);
                                exceptionListEntity.setEpc(etag);
                                exceptionListEntity.setTid(etag);
                                exceptionListEntity.setRfidSerial(rfidSerial);
                                exceptionListEntity.setVehicleTypeId(vehicleGroup);
                                exceptionListEntity.setExceptionType(FnCommon.mappingExceptionListType(exceptionVehicleType));
                                exceptionListEntity.setStatus(ExceptionListEntity.Status.APPROVAL.value);
                                if (!FnCommon.isNullOrEmpty(plateTypeCode)) {
                                    exceptionListEntity.setLicensePlateType(Long.parseLong(plateTypeCode));
                                }
                                exceptionListEntity.setDescription("Import tu file");
                                exceptionListEntity.setEffDate(new java.sql.Date(effDate.getTime()));
                                exceptionListEntity.setExpDate(new java.sql.Date(expDate.getTime()));
                                exceptionListEntity.setContractId(String.valueOf(Constants.BOO_INFO.CONTRACT_ID));
                                exceptionListEntity.setCreateUser("tungnk");
                                exceptionListEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
                                exceptionListEntity.setApprovedDate(new java.sql.Date(System.currentTimeMillis()));
                                exceptionListEntity.setApprovedUser("tungnk");
                                exceptionListEntity.setOrgPlateNumber(row.getCell(PLATE_NUMBER).getStringCellValue());
                                exceptionListEntity.setOcsCode(offerId);
                                exceptionListEntity.setPromotionId(promotionId);
                                exceptionListServiceJPA.save(exceptionListEntity);
                            } else if (MONTHLY_TICKET.equals(exceptionVehicleType) || QUARTERLY_TICKET.equals(exceptionVehicleType)) {
                                // neu la ve thang quy thi insert vao saletrans va saletransdetail
                                SaleTransEntity saleTransEntity = new SaleTransEntity();
                                saleTransEntity.setSaleTransCode("TQ" + contractId + FnCommon.formatDateTime(new java.sql.Date(System.currentTimeMillis())));
                                saleTransEntity.setSaleTransDate(new java.sql.Date(effDate.getTime()));
                                saleTransEntity.setSaleTransType(SaleTransEntity.SaleTransType.MONTHLY_QUARTERLY_TICKET.value);
                                saleTransEntity.setStatus(SaleTransEntity.Status.PAID_NOT_INVOICED.value);
                                saleTransEntity.setCustId(customerId);
                                saleTransEntity.setContractId(contractId);
                                saleTransEntity.setCreateUser("tungnk");
                                saleTransEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
                                saleTransRepositoryJPA.save(saleTransEntity);

                                SaleTransDetailEntity saleTransDetailEntity = new SaleTransDetailEntity();
                                saleTransDetailEntity.setSaleTransId(saleTransEntity.getSaleTransId());
                                saleTransDetailEntity.setSaleTransDate(new java.sql.Date(System.currentTimeMillis()));
                                saleTransDetailEntity.setPlateNumber(plateNumber);
                                saleTransDetailEntity.setEpc(etag);
                                saleTransDetailEntity.setTid(etag);
                                saleTransDetailEntity.setRfidSerial(rfidSerial);
                                saleTransDetailEntity.setVehicleGroupId(vehicleGroup);
                                saleTransDetailEntity.setServicePlanId(servicePlanDTO.getServicePlanId());
                                saleTransDetailEntity.setServicePlanName(servicePlanDTO.getServicePlanName());
                                saleTransDetailEntity.setServicePlanTypeId(servicePlanDTO.getServicePlanTypeId());
                                saleTransDetailEntity.setOcsCode(servicePlanDTO.getOcsCode());
                                saleTransDetailEntity.setScope(String.valueOf(servicePlanDTO.getScope()));
                                saleTransDetailEntity.setEffDate(new java.sql.Date(effDate.getTime()));
                                saleTransDetailEntity.setExpDate(new java.sql.Date(expDate.getTime()));
                                saleTransDetailEntity.setAutoRenew("0");
                                saleTransDetailEntity.setQuantity(1L);
                                saleTransDetailEntity.setPrice(servicePlanDTO.getFee());
                                saleTransDetailEntity.setOrgPlateNumber(plateNumberConvert);
                                if (station != null) {
                                    saleTransDetailEntity.setStationId(station);
                                }
                                if (stage != null) {
                                    saleTransDetailEntity.setStageId(stage);
                                }
                                saleTransDetailEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
                                saleTransDetailEntity.setCreateUser("tungnk");
                                saleTransDetailEntity.setOfferLevel(Constants.OFFER_LEVEL_DEFAULT.toString());
                                saleTransDetailEntity.setStatus(SaleTransDetailEntity.Status.UN_PAID.value);
                                saleTransDetailRepositoryJPA.save(saleTransDetailEntity);
                            } else {
                                resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Loai phai thuoc BL,WL,T,Q");
                            }
                        } else {
                            resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Add supOffer len OCS loi " + ocsResponse.getDescription());
                            continue;
                        }
                        resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Success", "Thanh cong");
                    }
                } else {
                    resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Du lieu truyen vao khong hop le");
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
            throw new EtcException("crm.import-batch-vehicle.failed");
        }
        return FnCommon.returnFileExcel(bytes, fileName);
    }


    @Override
    public ResponseEntity<?> getEtagBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String plateNumberConvert = row.getCell(PLATE_NUMBER).getStringCellValue().replaceAll("[\\-\\.]", "");
                ReqActivationCheckDTO req = new ReqActivationCheckDTO();
                req.setPlate(plateNumberConvert);
                req.setRequest_id(System.currentTimeMillis());
                req.setRequest_datetime(System.currentTimeMillis());
                ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
                if (res != null) {
                    resultImport(sheet.getRow(i).createCell(GET_PLATE_NUMBER_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Success", "Thanh cong");
                    resultImport(sheet.getRow(i).createCell(GET_ETAG_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Success", "Thanh cong");
                    Cell etagCell = sheet.getRow(i).createCell(ETAG);
                    etagCell.setCellType(CellType.STRING);
                    etagCell.setCellValue(res.getEtag());

                    Cell booRes = sheet.getRow(i).createCell(BOO1_RES);
                    booRes.setCellType(CellType.STRING);
                    booRes.setCellValue(String.valueOf(res.toString()));
                } else {
                    resultImport(sheet.getRow(i).createCell(GET_ETAG_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Failed", "Khong tim thay xe ben BOO1");
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
            throw new EtcException("crm.import-batch-vehicle.failed");
        }
        return FnCommon.returnFileExcel(bytes, fileName);
    }

    @Override
    public ResponseEntity<?> synEtagBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String plateNumber = row.getCell(0).getStringCellValue();
                String etag = row.getCell(1).getStringCellValue();
                String plateTypeBoo1 = row.getCell(2).getStringCellValue();

                String plateNumberConvert = FnCommon.getPlateNumberBoo1(plateNumber, plateTypeBoo1);
                ReqActivationCheckDTO req = new ReqActivationCheckDTO();
                req.setPlate(plateNumberConvert);
                req.setRequest_id(System.currentTimeMillis());
                req.setRequest_datetime(System.currentTimeMillis());
                ResActivationCheckDTO res = boo1Service.findVehicleByPlateNumber(req, authentication, Constants.ACT_TYPE.BOO1_CHECK_VEHICLE);
                LOGGER.info("========= Process number ========= " + i);
                if (res != null && res.getEtag() != null) {
                    if (!etag.equals(res.getEtag())) {
                        ReqOnlineEventSyncDTO reqOnlineEventSyncDTO = new ReqOnlineEventSyncDTO();
                        reqOnlineEventSyncDTO.setPlate_old(plateNumberConvert);
                        reqOnlineEventSyncDTO.setEtag_old(etag);
                        reqOnlineEventSyncDTO.setEtag_new(res.getEtag());
                        reqOnlineEventSyncDTO.setRequest_datetime(System.currentTimeMillis());
                        reqOnlineEventSyncDTO.setRequest_id(System.currentTimeMillis());
                        reqOnlineEventSyncDTO.setReason(Constants.REASON_CHANGE_BOO.CHANGE_EPC);

                        ResOnlineEventDTO resOnlineEventDTO = booService.onlineEventSync(reqOnlineEventSyncDTO, authentication);
                        if ("0".equals(resOnlineEventDTO.getResponse_code())) {
                            LOGGER.info("========= Process number syn etag suscess  ========= " + i);
                            resultImport(sheet.getRow(i).createCell(GET_ETAG_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Sucess", "Dong bo etag thanh cong");
                        } else {
                            LOGGER.info("========= Process number syn etag  failed  ========= " + i);
                            resultImport(sheet.getRow(i).createCell(GET_ETAG_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Failed", "Dong bo etag that bai");
                        }
                    } else {
                        LOGGER.info("========= Process number khop etag  ========= " + i);
                        resultImport(sheet.getRow(i).createCell(GET_ETAG_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Sucess", "Khop etag");
                    }
                } else {
                    resultImport(sheet.getRow(i).createCell(GET_ETAG_RESULT), sheet.getRow(i).createCell(GET_ETAG_DESC), "Failed", "Khong tim thay xe ben BOO1");
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
            throw new EtcException("crm.import-batch-vehicle.failed");
        }
        return FnCommon.returnFileExcel(bytes, fileName);
    }

    @Override
    public ResponseEntity<?> modifyPlateBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String plateNumber = row.getCell(PLATE_NUMBER).getStringCellValue().replaceAll("[\\-\\.]", "");
                String etag = row.getCell(ETAG).getStringCellValue();

                UpdateVehicleRequestOCSDTO reqModify = new UpdateVehicleRequestOCSDTO();
                reqModify.setEpc(etag);
                reqModify.setPlateNumber(plateNumber);
                reqModify.setActTypeId(Constants.ACT_TYPE.MODIFY_VEHICLE);
                OCSResponse ocsResponse = ocsService.modifyVehicleOCS(reqModify, authentication, false);
                if (FnCommon.checkOcsCode(ocsResponse)) {
                    resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Success", "Thanh cong");
                } else {
                    resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Loi " + ocsResponse.getDescription());
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
            throw new EtcException("crm.import-batch-vehicle.failed");
        }
        return FnCommon.returnFileExcel(bytes, fileName);
    }

    @Override
    public ResponseEntity<?> checkPlateBatchVehicle(MultipartFile fileImport, Authentication authentication) throws Exception {
        byte[] byteArr = fileImport.getBytes();
        String fileName = fileImport.getOriginalFilename();
        assert fileName != null;
        byte[] bytes = new byte[0];
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArr))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String plateNumber = row.getCell(PLATE_NUMBER).getStringCellValue().replaceAll("[\\-\\.]", "");
                String etag = row.getCell(ETAG).getStringCellValue().trim();

                LinkedHashMap<?, ?> ocsResponse = ocsService.queryVehicleOcs(etag, authentication);
                if ("Success".equals(ocsResponse.get("description"))) {
                    resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Success", "Thanh cong");
                } else {
                    resultImport(sheet.getRow(i).createCell(RESULT), sheet.getRow(i).createCell(DESC), "Failed", "Loi " + ocsResponse.get("description"));
                }
            }
            org.apache.commons.io.output.ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Import noi dung khong thanh cong ", e);
            throw new EtcException("crm.import-batch-vehicle.failed");
        }
        return FnCommon.returnFileExcel(bytes, fileName);
    }


    private void resultImport(Cell resultCell, Cell descCell, String result, String desc) {
        if (resultCell != null && descCell != null) {
            resultCell.setCellType(CellType.STRING);
            resultCell.setCellValue(result);
            descCell.setCellType(CellType.STRING);
            descCell.setCellValue(desc);
        }
    }

    /**
     * check content row has empty or null
     *
     * @param row      Du lieu ma row can kiem tra
     * @param fromCell Cot bat dau thuc hien
     * @param toCell   Cot Ket thuc thuc hien
     * @return Chuoi kiem tra
     */
    private boolean hasEmptyAllCellOnRow(Row row, int fromCell, int toCell) {
        if (row != null) {
            for (int i = fromCell; i <= toCell; i++) {
                if (i == STATION || i == OFFER_ID || i == STAGE) {
                    return row.getCell(i).getCellType() == CellType.NUMERIC;
                } else {
                    if (i == ETAG || !FnCommon.isNullOrEmpty(FnCommon.getStringValue(row.getCell(i)))) {
                        return true;
                    }
                    return row.getCell(i).getCellType() == CellType.STRING;
                }

            }
        }
        return false;
    }
}
