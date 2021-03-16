package com.viettel.etc.services.impl;

import com.viettel.etc.dto.SearchCustRegisDTO;
import com.viettel.etc.repositories.SearchDepositTransRepository;
import com.viettel.etc.dto.SearchDepositTransDTO;
import com.viettel.etc.services.SearchDepositTransService;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.ExcellDataEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellHeaderEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellSheet;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.viettel.etc.utils.Constants.COMMON_DATE_TIME_FORMAT;

/**
 * Autogen class: Lop thao tac xem thong tin giao dich nap tien
 * 
 * @author ToolGen
 * @date Mon Feb 08 09:01:09 ICT 2021
 */
@Log4j
@Service
public class SearchDepositTransServiceImpl implements SearchDepositTransService{

    @Autowired 
    private SearchDepositTransRepository searchDepositTransRepository;
    

    /**
     * Lay du lieu giao dich nap tien
     * 
     * @param itemParamsEntity params client
     * @return 
     */
    @Override
    public Object getDepositInfo(SearchDepositTransDTO itemParamsEntity) {
        ResultSelectEntity dataResult = searchDepositTransRepository.getDepositInfo(itemParamsEntity);
        return dataResult;
    }

    @Override
    public String exportDepositTrans(SearchDepositTransDTO itemParamsEntity) {
        try {
            String fileName = "Topup-ETC";

            fileName += System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_TIME_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách giao dịch nạp tiền \nNgày xuất ";
            title += date;
            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();
            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("ID"));
            listHeader.add(new ExcellHeaderEntity("Ngày nạp"));
            listHeader.add(new ExcellHeaderEntity("Người nạp"));
            listHeader.add(new ExcellHeaderEntity("Người hưởng thụ"));
            listHeader.add(new ExcellHeaderEntity("Order ID"));
            listHeader.add(new ExcellHeaderEntity("Số tiền nạp (VND)"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();
            List<SearchDepositTransDTO> listDepositTrans = new ArrayList<>();
            ResultSelectEntity result = searchDepositTransRepository.getDepositInfo(itemParamsEntity);
            if(result != null){
                listDepositTrans= (List<SearchDepositTransDTO>) result.getListData();
            }
            int no = 1;

            //set data
            for (SearchDepositTransDTO searchDepositTransDTO : listDepositTrans) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);
                objectList.add(searchDepositTransDTO.getId());
                objectList.add(formatter.format(searchDepositTransDTO.getTopupDate()));
                objectList.add(searchDepositTransDTO.getStaffName());
                objectList.add(searchDepositTransDTO.getContractId());
                objectList.add(searchDepositTransDTO.getSaleOrderId());
                objectList.add(searchDepositTransDTO.getAmount());
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