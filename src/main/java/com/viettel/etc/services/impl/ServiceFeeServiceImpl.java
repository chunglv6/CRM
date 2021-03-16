package com.viettel.etc.services.impl;

import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.repositories.ServiceFeeRepository;
import com.viettel.etc.repositories.tables.entities.ServiceFeeEntity;
import com.viettel.etc.services.ServiceFeeService;
import com.viettel.etc.services.tables.ActReasonServiceJPA;
import com.viettel.etc.services.tables.ServiceFeeServiceJPA;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.ExcellDataEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellHeaderEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellSheet;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;

/**
 * Autogen class: Lop thao tac phi
 *
 * @author ToolGen
 * @date Wed Jul 01 09:00:16 ICT 2020
 */
@Log4j
@Service
public class ServiceFeeServiceImpl implements ServiceFeeService {
    @Autowired
    ServiceFeeServiceJPA serviceFeeServiceJPA;

    @Autowired
    ServiceFeeRepository serviceFeeRepository;

    @Autowired
    ActReasonServiceJPA actReasonServiceJPA;

    /**
     * Lay danh sach phi
     *
     * @param params
     * @return
     */
    @Override
    public Object getServiceFee(ServiceFeeDTO params) {
        Integer start = params.getStartrecord();
        Integer pageSize = params.getPagesize();
        if (params.getIsActive() != null) {
            params.setStartrecord(null);
            params.setPagesize(null);
        }
        ResultSelectEntity result =  serviceFeeRepository.getServiceFee(params);
        List<ServiceFeeDTO> serviceFeeList = (List<ServiceFeeDTO>) result.getListData();
        List<ServiceFeeDTO> serviceFeeListResult = new LinkedList<>();
        Calendar now = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.roll(Calendar.DATE,-1);
        for (ServiceFeeDTO serviceFeeDTO : serviceFeeList) {
            serviceFeeDTO.setIsActive(serviceFeeDTO.getStartDate().getTime() < now.getTimeInMillis() &&
                    serviceFeeDTO.getStatus().equals(Constants.SERVICE_FEE_APPROVED) &&
                    (serviceFeeDTO.getEndDate() == null || serviceFeeDTO.getEndDate().getTime() > yesterday.getTimeInMillis()));
            if (params.getIsActive() != null) {
                if (params.getIsActive() && serviceFeeDTO.getIsActive()) {
                    serviceFeeListResult.add(serviceFeeDTO);
                }
                if (!params.getIsActive() && !serviceFeeDTO.getIsActive()) {
                    serviceFeeListResult.add(serviceFeeDTO);
                }
            }
        }
        if (params.getIsActive() != null) {
            start = start == null ? 0 : start;
            int end = pageSize == null ? serviceFeeListResult.size() : pageSize + start;
            end = Math.min(end, serviceFeeListResult.size());
            result.setListData(serviceFeeListResult.subList(start, end));
            result.setCount(serviceFeeListResult.size());
        }
        return result;
    }

    /**
     * Xem chi tiet ban ghi
     *
     * @param serviceFeeId
     * @return
     */
    @Override
    public Object getDetailServiceFee(Long serviceFeeId) {
        if (serviceFeeServiceJPA.existsById(serviceFeeId)) {
            return new ServiceFeeDTO().toResponse(serviceFeeServiceJPA.getOne(serviceFeeId));
        }
        return null;
    }

    /**
     * Xem chi tiet ban ghi
     *
     * @param actReasonId
     * @return
     */
    @Override
    public Object getDetailServiceFeeByReason(Long actReasonId) {
        Date now = new Date();
        List<ServiceFeeEntity> serviceFeeList = serviceFeeServiceJPA.getByActReasonIdAndStartDateBefore(actReasonId, now);
        for (ServiceFeeEntity serviceFee : serviceFeeList) {
            if (serviceFee.getEndDate() == null || serviceFee.getEndDate().getTime() > new DateTime(now).minusDays(1).getMillis()) {
                return new ServiceFeeDTO().toResponse(serviceFee);
            }
        }
        return null;
    }

    /**
     * Them moi ban ghi phi
     *
     * @param params
     * @param authentication
     * @return
     */
    @Override
    public Object addServiceFee(ServiceFeeDTO params, Authentication authentication) {
        Date now = new Date();
        if (params.getStartDate().getTime() <= now.getTime()) {
            throw new EtcException("crm.start.date.is.past");
        }
        if (params.getEndDate() == null) {
            if (serviceFeeServiceJPA.getByActReasonIdAndEndDateIsNull(params.getActReasonId()).size() > 0 ||
                serviceFeeServiceJPA.getByActReasonIdAndEndDateAfter(params.getActReasonId(), params.getStartDate()).size() > 0) {
                throw new EtcException("crm.date.overlap");
            }
        } else {
            if (params.getStartDate().getTime() > params.getEndDate().getTime()) {
                throw new EtcException("crm.start.date.after.end.date");
            }
            if (serviceFeeServiceJPA.getByActReasonIdAndEndDateBetween(params.getActReasonId(), params.getStartDate(), params.getEndDate()).size() > 0 ||
                serviceFeeServiceJPA.getByActReasonIdAndStartDateBetween(params.getActReasonId(), params.getStartDate(), params.getEndDate()).size() > 0) {
                throw new EtcException("crm.date.overlap");
            }
        }
        ServiceFeeEntity serviceFeeEntity = new ServiceFeeEntity();
        serviceFeeEntity.setActReasonId(params.getActReasonId());
        if (params.getActionTypeId() == null && actReasonServiceJPA.existsById(params.getActReasonId())) {
            serviceFeeEntity.setActTypeId(actReasonServiceJPA.getOne(params.getActReasonId()).getActTypeId());
        } else {
            serviceFeeEntity.setActTypeId(params.getActionTypeId());
        }
        serviceFeeEntity.setServiceFeeName(params.getServiceFeeName());
        serviceFeeEntity.setServiceFeeCode(params.getServiceFeeCode());
        serviceFeeEntity.setFee(params.getFee());
        serviceFeeEntity.setStartDate(params.getStartDate());
        serviceFeeEntity.setEndDate(params.getEndDate());
        serviceFeeEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        serviceFeeEntity.setDocRefer(params.getDocRefer());
        serviceFeeEntity.setDescription(params.getDescription());
        serviceFeeEntity.setStatus(Constants.SERVICE_FEE_NEW);
        return new ServiceFeeDTO().toResponse(serviceFeeServiceJPA.save(serviceFeeEntity));
    }

    /**
     * Sua ban ghi phi
     *
     * @param params
     * @param authentication
     * @param serviceFeeId
     * @return
     */
    @Override
    public Object editServiceFee(ServiceFeeDTO params, Authentication authentication, Long serviceFeeId) {
        if (!serviceFeeServiceJPA.existsById(serviceFeeId)) {
            return null;
        }
        Date now = new Date();
        ServiceFeeEntity serviceFeeEntity = serviceFeeServiceJPA.getOne(serviceFeeId);
        if (params.getEndDate() != null) {
            if (now.getTime() >= params.getEndDate().getTime()) {
                throw new EtcException("crm.end.date.is.past");
            }
            if (serviceFeeServiceJPA.getByActReasonIdAndStartDateBetween(params.getActReasonId(), serviceFeeEntity.getStartDate(), params.getEndDate()).size() > 1) {
                throw new EtcException("crm.date.overlap");
            }
        }

        if (params.isUnChange(serviceFeeEntity)) {
            return params;
        } else {
            serviceFeeEntity.setActReasonId(params.getActReasonId());
            if (params.getActionTypeId() == null && actReasonServiceJPA.existsById(params.getActReasonId())) {
                serviceFeeEntity.setActTypeId(actReasonServiceJPA.getOne(params.getActReasonId()).getActTypeId());
            } else {
                serviceFeeEntity.setActTypeId(params.getActionTypeId());
            }
            serviceFeeEntity.setServiceFeeName(params.getServiceFeeName());
            serviceFeeEntity.setServiceFeeCode(params.getServiceFeeCode());
            serviceFeeEntity.setFee(params.getFee());
            serviceFeeEntity.setEndDate(params.getEndDate());
            serviceFeeEntity.setDocRefer(params.getDocRefer());
            serviceFeeEntity.setDescription(params.getDescription());
            serviceFeeEntity.setUpdateUser(FnCommon.getUserLogin(authentication));
            serviceFeeEntity.setUpdateDate(new Date());
            return new ServiceFeeDTO().toResponse(serviceFeeServiceJPA.save(serviceFeeEntity));
        }
    }

    /**
     * Xoa ban ghi phi
     *
     * @param authentication
     * @param serviceFeeId
     * @return
     */
    @Override
    public Object deleteServiceFee(Authentication authentication, Long serviceFeeId) {
        if (serviceFeeServiceJPA.existsById(serviceFeeId)) {
            ServiceFeeEntity serviceFeeEntity = serviceFeeServiceJPA.getOne(serviceFeeId);
            if (serviceFeeEntity.getStatus().equals(Constants.SERVICE_FEE_NEW)) {
                serviceFeeServiceJPA.deleteById(serviceFeeId);
                return "";
            }
        }
        return null;
    }

    /**
     * Phe duyet
     *
     * @param listId
     * @param authentication
     * @return
     */
    @Override
    public Object approveServiceFee(String listId, Authentication authentication) {
        Date now = new Date();
        String[] stringList = listId.split(",");
        List<Long> serviceFeeIdList = new LinkedList<>();
        for (String id : stringList) {
            Long serviceFeeId = Long.valueOf(id);
            if (!serviceFeeServiceJPA.existsById(serviceFeeId)) {
                return null;
            }
            serviceFeeIdList.add(serviceFeeId);
        }
        List<ServiceFeeEntity> serviceFeeList = serviceFeeServiceJPA.getByIdIn(serviceFeeIdList);
        for (ServiceFeeEntity serviceFeeEntity : serviceFeeList) {
            if (serviceFeeEntity.getStatus().equals(Constants.SERVICE_FEE_NEW)) {
                if (serviceFeeEntity.getStartDate().getTime() <= now.getTime()) {
                    throw new EtcException("crm.start.date.is.past");
                }
                serviceFeeEntity.setStatus(Constants.SERVICE_FEE_APPROVED);
                serviceFeeEntity.setApprovedDate(now);
                serviceFeeEntity.setApprovedUser(FnCommon.getUserLogin(authentication));
            }
        }
        serviceFeeServiceJPA.saveAll(serviceFeeList);
        return "";
    }

    /**
     * Export file excel
     *
     * @param params
     * @return
     */
    @Override
    public String exportServiceFee(ServiceFeeDTO params) {
        try {

            String fileName = "ServiceFee" + System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách phí dịch vụ \nNgày xuất " + date;

            params.setPagesize(null);
            params.setStartrecord(null);
            ResultSelectEntity serviceFeeDTOList = (ResultSelectEntity) getServiceFee(params);
            List<ServiceFeeDTO> serviceFeeList = (List<ServiceFeeDTO>) serviceFeeDTOList.getListData();
            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();

            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("Mã phí dịch vụ"));
            listHeader.add(new ExcellHeaderEntity("Loại tác động"));
            listHeader.add(new ExcellHeaderEntity("Lý do tác động"));
            listHeader.add(new ExcellHeaderEntity("Mức phí"));
            listHeader.add(new ExcellHeaderEntity("Ngày hiệu lực"));
            listHeader.add(new ExcellHeaderEntity("Ngày hết hiệu lực"));
            listHeader.add(new ExcellHeaderEntity("Trạng thái"));
            listHeader.add(new ExcellHeaderEntity("Ngày tạo"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();
            int no = 1;

            //set data
            for (ServiceFeeDTO serviceFeeDTO : serviceFeeList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);
                objectList.add(serviceFeeDTO.getServiceFeeCode());
                objectList.add(serviceFeeDTO.getActTypeName());
                objectList.add(serviceFeeDTO.getActReasonName());
                objectList.add(serviceFeeDTO.getFee());
                objectList.add(formatter.format(serviceFeeDTO.getStartDate()));
                objectList.add(serviceFeeDTO.getEndDate() == null ? "" : formatter.format(serviceFeeDTO.getEndDate()));

                if (serviceFeeDTO.getIsActive() != null) {
                    if (serviceFeeDTO.getIsActive()) {
                        objectList.add("Hiệu lực");
                    } else {
                        objectList.add("Không hiệu lực");
                    }
                } else {
                    objectList.add("");
                }
                objectList.add(formatter.format(serviceFeeDTO.getCreateDate()));
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
}
