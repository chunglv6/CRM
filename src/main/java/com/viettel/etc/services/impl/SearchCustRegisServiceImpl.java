package com.viettel.etc.services.impl;

import com.viettel.etc.repositories.SearchCustRegisRepository;
import com.viettel.etc.dto.SearchCustRegisDTO;
import com.viettel.etc.services.SearchCustRegisService;
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
 * Autogen class: Lay thong tin khach hang da dang ki
 * 
 * @author ToolGen
 * @date Mon Feb 01 15:43:14 ICT 2021
 */
@Log4j
@Service
public class SearchCustRegisServiceImpl implements SearchCustRegisService{

    @Autowired 
    private SearchCustRegisRepository searchCustRegisRepository;
    

    /**
     * Lay du lieu doanh thu cua bot
     * 
     * @param itemParamsEntity params client
     * @return 
     */
    @Override
    public Object searchCustRegstered(SearchCustRegisDTO itemParamsEntity) {
        ResultSelectEntity dataResult = searchCustRegisRepository.searchCustRegstered(itemParamsEntity);
        return dataResult;
    }

    @Override
    public String exportCustRegistered(SearchCustRegisDTO params) throws IOException {
        try {
            String fileName = "CustomerRegistered";

            fileName += System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_TIME_FORMAT);
            String date = formatter.format(new Date());
            String title = "Danh sách khách hàng đã đăng kí \nNgày xuất ";
            title += date;
            ExcellSheet sheetExport = new ExcellSheet();

            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();
            listHeader.add(new ExcellHeaderEntity("STT"));
            listHeader.add(new ExcellHeaderEntity("Họ và tên"));
            listHeader.add(new ExcellHeaderEntity("Số điện thoại"));
            listHeader.add(new ExcellHeaderEntity("Tỉnh/Thành phố"));
            listHeader.add(new ExcellHeaderEntity("Quận/Huyện"));
            listHeader.add(new ExcellHeaderEntity("Phường/Xã"));
            listHeader.add(new ExcellHeaderEntity("Biển số"));
            listHeader.add(new ExcellHeaderEntity("Thời gian đăng kí"));
            listHeader.add(new ExcellHeaderEntity("Mã đăng kí"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();
            List<SearchCustRegisDTO> custRegisList = new ArrayList<>();
            ResultSelectEntity result = searchCustRegisRepository.searchCustRegstered(params);
            if(result != null){
                custRegisList= (List<SearchCustRegisDTO>) result.getListData();
            }
            int no = 1;

            //set data
            for (SearchCustRegisDTO aearchCustRegisDTO : custRegisList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(no);
                objectList.add(aearchCustRegisDTO.getCustName());
                objectList.add(aearchCustRegisDTO.getPhoneNumber());
                objectList.add(aearchCustRegisDTO.getProvinceName());
                objectList.add(aearchCustRegisDTO.getDistricName());
                objectList.add(aearchCustRegisDTO.getCommuneName());
                objectList.add(aearchCustRegisDTO.getPlateNumber());
                objectList.add(formatter.format(aearchCustRegisDTO.getRegDate()));
                objectList.add(aearchCustRegisDTO.getOrderNumber());
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