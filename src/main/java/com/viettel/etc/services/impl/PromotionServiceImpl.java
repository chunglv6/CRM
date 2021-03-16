package com.viettel.etc.services.impl;

import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.repositories.PromotionRepository;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.PromotionService;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.ExcellDataEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellHeaderEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellSheet;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;

/**
 * Autogen class: Lop quan ly chuong trinh khuyen mai/ chiet khau
 *
 * @author ToolGen
 * @date Fri Sep 04 15:40:35 ICT 2020
 */
@Log4j
@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    PromotionServiceJPA promotionServiceJPA;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionAssignServiceJPA promotionAssignServiceJPA;

    @Autowired
    private VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    private ContractServiceJPA contractServiceJPA;

    @Autowired
    private AttachmentFileServiceJPA attachmentFileServiceJPA;

    @Autowired
    private FileService fileService;

    /**
     * Lay danh sach chuong trinh khuyen mai/ chiet khau
     *
     * @param params params client
     * @return
     */
    @Override
    public Object getPromotions(PromotionDTO params) {

        Integer start = params.getStartrecord();
        Integer pageSize = params.getPagesize();
        if (params.getIsActive() != null) {
            params.setStartrecord(null);
            params.setPagesize(null);
        }
        ResultSelectEntity dataResult = promotionRepository.getPromotions(params);
        List<PromotionDTO> promotionList = (List<PromotionDTO>) dataResult.getListData();
        List<PromotionDTO> promotionListResult = new LinkedList<>();
        Calendar now = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.roll(Calendar.DATE, -1);
        for (PromotionDTO promotionDTO : promotionList) {
            promotionDTO.setIsActive(promotionDTO.getEffDate().getTime() < now.getTimeInMillis() &&
                    promotionDTO.getStatus().equals(Constants.PROMOTION_ACTIVE) &&
                    (promotionDTO.getExpDate() == null || promotionDTO.getExpDate().getTime() > yesterday.getTimeInMillis()));
            if (params.getIsActive() != null) {
                if (params.getIsActive() && promotionDTO.getIsActive()) {
                    promotionListResult.add(promotionDTO);
                }
                if (!params.getIsActive() && !promotionDTO.getIsActive()) {
                    promotionListResult.add(promotionDTO);
                }
            }
        }
        if (params.getIsActive() != null) {
            start = start == null ? 0 : start;
            int end = pageSize == null ? promotionListResult.size() : pageSize + start;
            end = Math.min(end, promotionListResult.size());
            dataResult.setListData(promotionListResult.subList(start, end));
            dataResult.setCount(promotionListResult.size());
        }
        return dataResult;
    }

    /**
     * Lay chi tiet chuong trinh khuyen mai/ chiet khau
     *
     * @param promotionId Id chuong trinh khuyen mai
     * @return
     */
    @Override
    public Object getDetailPromotion(Long promotionId) {
        if (promotionServiceJPA.existsById(promotionId)) {
            PromotionDTO result = new PromotionDTO(promotionServiceJPA.getOne(promotionId));
            List<AttachmentFileDTO> fileList = new LinkedList<>();
            List<AttachmentFileEntity> fileEntityList = attachmentFileServiceJPA.getByAttachmentTypeAndObjectIdAndStatus(4L, promotionId, "1");
            for (AttachmentFileEntity fileEntity : fileEntityList) {
                fileList.add(AttachmentFileDTO.builder()
                        .documentName(fileEntity.getDocumentName())
                        .attachmentFileId(fileEntity.getAttachmentFileId()).build());
            }
            result.setFileList(fileList);
            return result;
        }
        return null;
    }

    /**
     * Them chuong trinh khuyen mai/ chiet khau
     *
     * @param params params client
     * @return
     */
    @Override
    public Object addPromotion(PromotionDTO params, List<MultipartFile> files, Authentication authentication) throws IOException {
        long now = new Date().getTime();
        if (params.getEffDate().getTime() <= now) {
            throw new EtcException("crm.eff.date.is.past");
        }
        if (params.getExpDate() != null && params.getEffDate().getTime() >= params.getExpDate().getTime()) {
            throw new EtcException("crm.eff.date.after.exp.date");
        }
        PromotionEntity promotionEntity = params.toAddPromotionEntity();
        String user = FnCommon.getUserLogin(authentication);
        promotionEntity.setCreateUser(user);
        promotionEntity.setStatus(Constants.PROMOTION_UNAPPROVED);
        PromotionEntity dataResult = this.promotionServiceJPA.save(promotionEntity);
        addAttachmentFile(files, user, dataResult.getPromotionId());
        return new PromotionDTO(dataResult);
    }

    /**
     * Sua chuong trinh khuyen mai/ chiet khau
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object editPromotion(PromotionDTO itemParamsEntity, Authentication authentication, Long promotionId, List<MultipartFile> files) throws IOException {
        Date now = new Date();
        if (itemParamsEntity.getExpDate() != null && itemParamsEntity.getExpDate().getTime() <= now.getTime()) {
            throw new EtcException("crm.exp.date.is.past");
        }
        Optional<PromotionEntity> promotionEntity = promotionServiceJPA.findById(promotionId);
        PromotionEntity promotion;
        if (promotionEntity.isPresent()) {
            promotion = promotionEntity.get();
            if (itemParamsEntity.setDataToEntityOnEditPromotion(promotion)) {
                promotion.setUpdateUser(FnCommon.getUserLogin(authentication));
                promotion.setUpdateDate(now);
            }
            PromotionEntity dataResult = promotionServiceJPA.save(promotion);
            addAttachmentFile(files, FnCommon.getUserLogin(authentication), dataResult.getPromotionId());
            return new PromotionDTO(dataResult);
        }
        return null;
    }

    void addAttachmentFile(List<MultipartFile> files, String userLogin, Long promotionId) throws IOException {
        List<AttachmentFileEntity> fileList = new LinkedList<>();
        for (MultipartFile file : files) {
            AttachmentFileEntity attachmentFileEntity = new AttachmentFileEntity();
            attachmentFileEntity.setAttachmentType(AttachmentFileEntity.ATTACH_TYPE.PROMOTION.value);
            attachmentFileEntity.setStatus(AttachmentFileEntity.STATUS.ACTIVE.value);
            attachmentFileEntity.setObjectId(promotionId);
            attachmentFileEntity.setDocumentPath(file.getOriginalFilename());
            attachmentFileEntity.setDocumentName(file.getOriginalFilename());
            attachmentFileEntity.setCreateUser(userLogin);
            fileList.add(attachmentFileEntity);
            fileService.uploadFile(file.getOriginalFilename(), file.getBytes());
        }
        attachmentFileServiceJPA.saveAll(fileList);
    }

    /**
     * Xoa chuong trinh khuyen mai/ chiet khau
     *
     * @return
     */
    @Override
    public Object deletePromotion(Authentication authentication, Long promotionId) {

        Optional<PromotionEntity> promotion = promotionServiceJPA.findById(promotionId);
        if (promotion.isPresent()) {
            if (Constants.PROMOTION_NEW.equals(promotion.get().getStatus())) {
                promotionServiceJPA.deleteById(promotionId);
                return "";
            }
        }
        return null;
    }

    /**
     * Xoa file dinh kem
     *
     * @return
     */
    @Override
    public void deleteAttachment(Long attachmentId) {
        if (attachmentFileServiceJPA.existsById(attachmentId)) {
            AttachmentFileEntity fileEntity = attachmentFileServiceJPA.getOne(attachmentId);
            if (AttachmentFileEntity.ATTACH_TYPE.PROMOTION.value.equals(fileEntity.getAttachmentType())) {
                fileService.removeFile(fileEntity.getDocumentPath());
                attachmentFileServiceJPA.deleteById(attachmentId);
            } else {
                throw new EtcException("crm.attachment.not.exist");
            }
        } else {
            throw new EtcException("crm.attachment.not.exist");
        }
    }

    @Override
    public Object approvePromotion(String listId, Authentication authentication) {
        String[] stringList = listId.split(",");
        List<Long> listPromotionId = new LinkedList<>();
        for (String id : stringList) {
            Long promotionId = Long.valueOf(id);
            if (!promotionServiceJPA.existsById(promotionId)) {
                return null;
            }
            listPromotionId.add(promotionId);
        }
        Date now = new Date();
        List<PromotionEntity> promotionList = promotionServiceJPA.getByIdIn(listPromotionId);
        for (PromotionEntity promotion : promotionList) {
            if (promotion.getStatus().equals(Constants.PROMOTION_UNAPPROVED)) {
                if (promotion.getEffDate().getTime() <= now.getTime()) {
                    throw new EtcException("crm.eff.date.is.past");
                }
                promotion.setStatus(Constants.PROMOTION_APPROVED);
                promotion.setApprovedDate(now);
                promotion.setApprovedUser(FnCommon.getUserLogin(authentication));
            }
        }
        promotionServiceJPA.saveAll(promotionList);
        return listPromotionId;
    }

    /**
     * Export danh sach chuong trinh khuyen mai/ chiet khau ra excel.
     *
     * @param itemParamsEntity
     * @return
     */
    @Override
    public String exportPromotion(PromotionDTO itemParamsEntity) {

        try {

            String fileName = "Promotion" + System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách chương trình khuyến mại/ chiết khấu \nNgày xuất " + date;

            itemParamsEntity.setPagesize(null);
            itemParamsEntity.setStartrecord(null);
            ResultSelectEntity promotionDTOList = (ResultSelectEntity) getPromotions(itemParamsEntity);
            List<PromotionDTO> promotionDTOSList = (List<PromotionDTO>) promotionDTOList.getListData();

            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();

            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("Loại chương trình"));
            listHeader.add(new ExcellHeaderEntity("Mã chương trình"));
            listHeader.add(new ExcellHeaderEntity("Tên chương trình"));
            listHeader.add(new ExcellHeaderEntity("Mức khuyến mại/ Chiết khấu"));
            listHeader.add(new ExcellHeaderEntity("Ngày hiệu lực"));
            listHeader.add(new ExcellHeaderEntity("Ngày hết hiệu lực"));
            listHeader.add(new ExcellHeaderEntity("Trạng thái"));
            listHeader.add(new ExcellHeaderEntity("Ngày tạo"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();

            int no = 1;

            //set data
            for (PromotionDTO promotionDTO : promotionDTOSList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);

                /*
                    Loại chương trình
                    1. Khuyến mại
                    2. Chiết khấu
                */
                if (promotionDTO.getPromotionLevel() != null) {
                    if (Constants.PROMOTION_KM.equals(promotionDTO.getPromotionLevel())) {
                        objectList.add("Khuyến mại");
                    }
                    if (Constants.PROMOTION_CK.equals(promotionDTO.getPromotionLevel())) {
                        objectList.add("Chiết khấu");
                    }
                    if (Constants.PROMOTION_NL.equals(promotionDTO.getPromotionLevel())) {
                        objectList.add("Ngoại lệ");
                    }
                } else {
                    objectList.add("");
                }

                objectList.add(promotionDTO.getPromotionCode());
                objectList.add(promotionDTO.getPromotionName());
                objectList.add(promotionDTO.getPromotionAmount());
                objectList.add(formatter.format(promotionDTO.getEffDate()));
                objectList.add(promotionDTO.getExpDate() == null ? "" : formatter.format(promotionDTO.getExpDate()));

                if (promotionDTO.getIsActive() != null) {
                    if (promotionDTO.getIsActive()) {
                        objectList.add("Hiệu lực");
                    } else {
                        objectList.add("Không hiệu lực");
                    }
                } else {
                    objectList.add("");
                }

                objectList.add(formatter.format(promotionDTO.getCreateDate()));
                listData.add(objectList);
                no++;
            }
            excellDataEntity.setListData(listData);
            sheetExport.setExcellDataEntity(excellDataEntity);
            //export excel
            if (!FunctionCommon.exportFileExcell(sheetExport, System.getProperty("user.dir") + File.separator + fileName, title)) {
                throw new IOException();
            }
            return fileName;
        } catch (Exception ex) {
            log.error("Export Fail", ex);
        }
        return null;
    }


    /**
     * Lay du lieu doi tuong huong khuyen mai/ chiet khau
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getPromotionAssigns(PromotionAssignDTO itemParamsEntity) {
        return promotionRepository.getPromotionAssignDetail(itemParamsEntity);
    }

    /**
     * Them doi tuong huong chuong trinh khuyen mai/ chiet khau
     *
     * @param params params client
     * @return
     */
    public Object addPromotionAssign(PromotionAssignDTO params, Authentication authentication) {
        if (!promotionServiceJPA.existsById(params.getPromotionId())) {
            throw new EtcException("crm.promotion.exist");
        }
        PromotionEntity promotionEntity = promotionServiceJPA.getOne(params.getPromotionId());
        if (Constants.PROMOTION_NL.equals(promotionEntity.getPromotionLevel())) {
            throw new EtcException("crm.promotion.can.not.assign");
        }
        if (Constants.PROMOTION_LVL_VEHICLE.equals(params.getAssignLevel())) {
            VehicleEntity vehicleEntity = vehicleServiceJPA.getOne(params.getVehicleId());
            if (params.getContractId() == null) {
                params.setContractId(vehicleEntity.getContractId());
            }
            if (params.getCustId() == null) {
                params.setCustId(vehicleEntity.getCustId());
            }
            if (!promotionAssignServiceJPA.getByCustLvl(params.getPromotionId(), params.getCustId()).isEmpty()) {
                throw new EtcException("crm.vehicle.already.assigned");
            }
            if (!promotionAssignServiceJPA.getByContractLvl(params.getPromotionId(), params.getContractId()).isEmpty()) {
                throw new EtcException("crm.vehicle.already.assigned");
            }
            if (!promotionAssignServiceJPA.getByVehicle(params.getPromotionId(), params.getVehicleId()).isEmpty()) {
                throw new EtcException("crm.vehicle.already.assigned");
            }
        } else if (Constants.PROMOTION_LVL_CONTRACT.equals(params.getAssignLevel())) {
            ContractEntity contractEntity = contractServiceJPA.getOne(params.getContractId());
            params.setVehicleId(null);
            if (params.getCustId() == null) {
                params.setCustId(contractEntity.getCustId());
            }
            if (!promotionAssignServiceJPA.getByCustLvl(params.getPromotionId(), params.getCustId()).isEmpty()) {
                throw new EtcException("crm.contract.already.assigned");
            }
            if (!promotionAssignServiceJPA.getByContractLvl(params.getPromotionId(), params.getContractId()).isEmpty()) {
                throw new EtcException("crm.contract.already.assigned");
            }
            List<PromotionAssignEntity> promotionAssignList = promotionAssignServiceJPA.getByContract(params.getPromotionId(), params.getContractId());
            promotionAssignServiceJPA.deleteAll(promotionAssignList);
        } else if (Constants.PROMOTION_LVL_CUSTOMER.equals(params.getAssignLevel())) {
            params.setVehicleId(null);
            params.setContractId(null);
            if (!promotionAssignServiceJPA.getByCustLvl(params.getPromotionId(), params.getCustId()).isEmpty()) {
                throw new EtcException("crm.customer.already.assigned");
            }
            List<PromotionAssignEntity> promotionAssignList = promotionAssignServiceJPA.getByCust(params.getPromotionId(), params.getCustId());
            promotionAssignServiceJPA.deleteAll(promotionAssignList);
        }
        PromotionAssignEntity promotionAssignEntity = new PromotionAssignEntity();
        promotionAssignEntity.setPromotionId(params.getPromotionId());
        promotionAssignEntity.setAssignLevel(params.getAssignLevel());
        promotionAssignEntity.setCustId(params.getCustId());
        promotionAssignEntity.setContractId(params.getContractId());
        promotionAssignEntity.setVehicleId(params.getVehicleId());
        promotionAssignEntity.setPlateNumber(params.getPlateNumber());
        promotionAssignEntity.setEPC(params.getEPC());
        promotionAssignEntity.setEffDate(params.getEffDate());
        promotionAssignEntity.setExpDate(params.getExpDate());
        promotionAssignEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        return promotionAssignServiceJPA.save(promotionAssignEntity);
    }

    /**
     * Xoa doi tuong huong chuong trinh khuyen mai/ chiet khau
     *
     * @return
     */
    @Override
    public Object deletePromotionAssign(Authentication authentication, Long promotionAssignId) {
        Optional<PromotionAssignEntity> promotionAssign = promotionAssignServiceJPA.findById(promotionAssignId);
        if (promotionAssign.isPresent()) {
            promotionAssignServiceJPA.deleteById(promotionAssignId);
            return "";
        }
        return null;
    }

    @Override
    public void downloadAttachment(Long attachmentId, HttpServletResponse response) throws IOException {
        AttachmentFileEntity attachmentFileEntity = attachmentFileServiceJPA.getOne(attachmentId);
        byte[] fileByte = fileService.getFile(attachmentFileEntity.getDocumentPath());
        FnCommon.responseFile(response, fileByte, attachmentFileEntity.getDocumentName());
    }
}
