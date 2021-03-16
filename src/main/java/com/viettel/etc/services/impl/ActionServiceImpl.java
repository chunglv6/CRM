package com.viettel.etc.services.impl;

import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.ActionRepository;
import com.viettel.etc.services.ActionService;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.ExcellDataEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellHeaderEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellSheet;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;

/**
 * Autogen class: Lop ve tac dong
 *
 * @author ToolGen
 * @date Mon Jun 29 11:19:53 ICT 2020
 */
@Log4j
@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ContractServiceJPA contractServiceJPA;


    /**
     * lay danh sach loai tac dong
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getActionType(ActionDTO itemParamsEntity) {
        return actionRepository.getActionType(itemParamsEntity);
    }

    /**
     * lay danh sach lich su tac dong
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getActionHistory(ActionAuditHistoryDTO itemParamsEntity) {
        return actionRepository.getActionHistory(itemParamsEntity);
    }

    /**
     * lay danh sach ly do
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public Object getActionReason(ActionDTO itemParamsEntity, Long id) {
        return actionRepository.getActionReason(itemParamsEntity, id);
    }

    /**
     * lay danh sach lich su tac dong chi tiet
     *
     * @param itemParamsEntity params client
     * @param id               action audit id
     * @return
     */
    @Override
    public Object getActionHistoryDetail(ActionAuditHistoryDetailDTO itemParamsEntity, Long id) {
        return actionRepository.getActionHistoryDetail(itemParamsEntity, id);
    }

    /**
     * Lich su tac dong khach hang.
     *
     * @param requestModel
     * @return
     */
    @Override
    public Object findActionCustomerHistory(AuditHistoryDTO requestModel) {
        return actionRepository.findActionCustomerHistory(requestModel);
    }

    /**
     * Export lich su tac dong khach hang ra excel.
     *
     * @param dataParams
     * @return
     */
    @Override
    public String exportActHistory(AuditHistoryDTO dataParams) throws IOException {
        try {
            String fileName = "ActionCustomerHistory";

            fileName += System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách lịch sử tác động khách hàng \nNgày xuất ";
            title += date;
            ResultSelectEntity result = actionRepository.findActionCustomerHistory(dataParams);
            List<AuditHistoryDTO> auditHistoryList = (List<AuditHistoryDTO>) result.getListData();
            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();
            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("Loại tác động"));
            listHeader.add(new ExcellHeaderEntity("Người tác động"));
            listHeader.add(new ExcellHeaderEntity("Ngày tác động"));
            listHeader.add(new ExcellHeaderEntity("Người yêu cầu"));
            listHeader.add(new ExcellHeaderEntity("Lý do"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();

            int no = 1;

            //set data
            for (AuditHistoryDTO auditHistoryDTO : auditHistoryList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);
                objectList.add(auditHistoryDTO.getActionType());
                objectList.add(auditHistoryDTO.getActionUserName());
                objectList.add(auditHistoryDTO.getActionDate());
                objectList.add(auditHistoryDTO.getUserName());
                objectList.add(auditHistoryDTO.getReason());
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
     * Lich su tac dong khach hang.
     *
     * @param requestModel
     * @return
     */
    @Override
    public Object actionCustomerHistory(AuditHistoryDTO requestModel) {
        Long contractId = requestModel.getContractId();
        requestModel.setCustId(contractServiceJPA.getOne(contractId).getCustId());
        return actionRepository.actionCustomerHistory(requestModel);
    }

    /**
     * Export lich su tac dong ra excel.
     *
     * @param dataParams
     * @return
     */
    @Override
    public String exportActionHistory(ActionAuditHistoryDTO dataParams) throws IOException {

        try {

            String fileName = "ActionHistory" + System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách lịch sử tác động \nNgày xuất " + date;

            List<ActionAuditHistoryDTO> auditHistoryDTOSList = (List<ActionAuditHistoryDTO>) actionRepository.getActionHistory(dataParams).getListData();

            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();

            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("Mã khách hàng"));
            listHeader.add(new ExcellHeaderEntity("Tên khách hàng"));
            listHeader.add(new ExcellHeaderEntity("Đối tượng tác động"));
            listHeader.add(new ExcellHeaderEntity("Loại tác động"));
            listHeader.add(new ExcellHeaderEntity("Lý do tác động"));
            listHeader.add(new ExcellHeaderEntity("Người tác động"));
            listHeader.add(new ExcellHeaderEntity("Ngày tác động"));
            listHeader.add(new ExcellHeaderEntity("Trạng thái tác động"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();

            int no = 1;

            //set data
            for (ActionAuditHistoryDTO actionAuditHistoryDTO : auditHistoryDTOSList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);
                objectList.add(actionAuditHistoryDTO.getCustId());
                objectList.add(actionAuditHistoryDTO.getCustName());
                /*
                    1. Khách hàng
                    2. Hợp đồng
                    3. Phương tiện
                    4. Thẻ RFID
                */
                if ("1".equals(actionAuditHistoryDTO.getActObject())) {
                    objectList.add("Khách hàng");
                }
                if ("2".equals(actionAuditHistoryDTO.getActObject())) {
                    objectList.add("Hợp đồng");
                }
                if ("3".equals(actionAuditHistoryDTO.getActObject())) {
                    objectList.add("Phương tiện");
                }
                if ("4".equals(actionAuditHistoryDTO.getActObject())) {
                    objectList.add("Thẻ RFID");
                }
                objectList.add(actionAuditHistoryDTO.getActionTypeName());
                objectList.add(actionAuditHistoryDTO.getActionReasonName());
                objectList.add(actionAuditHistoryDTO.getActionUserFullName());
                objectList.add(actionAuditHistoryDTO.getCreateDate());
                objectList.add(actionAuditHistoryDTO.getStatus() == 1 ? "Thành công" : "Không thành công");
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
     * Export lich su tac dong ra excel.
     *
     * @param dataParams
     * @return
     */
    @Override
    public String exportActionHistoryVehicle(AuditHistoryDTO dataParams) throws IOException {

        try {

            String fileName = "ActionHistoryVehicle" + System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách lịch sử thay đổi thông tin phương tiện \nNgày xuất " + date;

            List<AuditHistoryDTO> auditHistoryList = (List<AuditHistoryDTO>) actionRepository.actionCustomerHistory(dataParams).getListData();

            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();

            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("Loại tác động"));
            listHeader.add(new ExcellHeaderEntity("Biển số xe"));
            listHeader.add(new ExcellHeaderEntity("Người tác động"));
            listHeader.add(new ExcellHeaderEntity("Ngày tác động"));
            listHeader.add(new ExcellHeaderEntity("Người yêu cầu"));
            listHeader.add(new ExcellHeaderEntity("Lý do"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();

            int no = 1;

            //set data
            for (AuditHistoryDTO auditHistoryDTO : auditHistoryList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);
                objectList.add(auditHistoryDTO.getActionType());
                objectList.add(auditHistoryDTO.getPlateNumber());
                objectList.add(auditHistoryDTO.getActionUserName());
                objectList.add(auditHistoryDTO.getActionDate());
                objectList.add(auditHistoryDTO.getUserName());
                objectList.add(auditHistoryDTO.getReason());
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
     * Lay danh sach lich su the RFID
     *
     * @param vehicleId
     * @param actionDTO
     * @return
     */
    @Override
    public Object searchRfidVehicleHistories(Long vehicleId, ActionDTO actionDTO) {
        return actionRepository.findRfidVehicleHistories(vehicleId, actionDTO);
    }

    /**
     * Lay tong so ban ghi khach hang, contract, vehicle, contract profile
     * @return
     */
    @Override
    public Object getCountShowData() {
        return actionRepository.countData();
    }
}
