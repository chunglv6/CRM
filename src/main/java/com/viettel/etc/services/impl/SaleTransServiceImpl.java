package com.viettel.etc.services.impl;

import com.viettel.etc.dto.SaleTransDTO;
import com.viettel.etc.dto.SaleTransVehicleOwnerAppDTO;
import com.viettel.etc.repositories.SaleOrderRepository;
import com.viettel.etc.repositories.SaleTransRepository;
import com.viettel.etc.repositories.tables.ContractRepositoryJPA;
import com.viettel.etc.repositories.tables.SaleTransRepositoryJPA;
import com.viettel.etc.repositories.tables.ServiceFeeRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransEntity;
import com.viettel.etc.repositories.tables.entities.ServiceFeeEntity;
import com.viettel.etc.services.SaleTransService;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.xlibrary.core.constants.FunctionCommon;
import com.viettel.etc.xlibrary.core.entities.ExcellDataEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellHeaderEntity;
import com.viettel.etc.xlibrary.core.entities.ExcellSheet;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.viettel.etc.utils.Constants.COMMON_DATE_FORMAT;

@Log4j
@Service
public class SaleTransServiceImpl implements SaleTransService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SaleTransServiceImpl.class);

    @Autowired
    private SaleTransRepositoryJPA saleTransRepositoryJPA;

    @Autowired
    private ServiceFeeRepositoryJPA serviceFeeRepositoryJPA;

    @Autowired
    private SaleTransRepository saleTransRepository;

    @Autowired
    private ContractRepositoryJPA contractRepositoryJPA;


    /**
     * update log to sale trans
     *
     * @param amount
     * @param authenEntity
     */
    @Override
    public void updateSaleTrans(Long amount, Long custId, Authentication authenEntity) {
        updateSaleTrans(amount, custId, null, null, authenEntity);
    }

    /**
     * Cap nhat thong tin sale trans
     *
     * @param amount
     * @param custId       Ma dinh danh nguoi dung
     * @param contractId   Ma dinh danh hop dong
     * @param contractNo   So hop dong
     * @param authenEntity Xac thuc
     */
    @Override
    public void updateSaleTrans(Long amount, Long custId, Long contractId, String contractNo, Authentication authenEntity) {
        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setAmount(amount);
        saleTransEntity.setCreateDate(new Date(System.currentTimeMillis()));
        saleTransEntity.setCreateUser(FnCommon.getUserLogin(authenEntity));
        saleTransEntity.setStatus(SaleTransEntity.Status.PAID_NOT_INVOICED.value);
        saleTransEntity.setContractId(contractId);
        saleTransEntity.setContractNo(contractNo);
        saleTransEntity.setCustId(custId);
        try {
            saleTransRepositoryJPA.save(saleTransEntity);
        } catch (Exception e) {
            LOGGER.error("Loi! update sale trans: ", e);
        }
    }

    /**
     * Thuc hien cap nhat du lieu sale trans
     *
     * @param actionTypeId Ma dinh danh loai hanh dong
     * @param custId       Ma dinh danh nguoi dung
     * @param authenEntity Xac thuc
     */
    @Override
    public void updateSaleTransService(Long actionTypeId, Long custId, Authentication authenEntity) {
        ServiceFeeEntity serviceFeeEntity = serviceFeeRepositoryJPA.getByActTypeId(actionTypeId).get(0);
        if (serviceFeeEntity == null) {
            return;
        }
        updateSaleTrans(serviceFeeEntity.getFee(), custId, authenEntity);
    }

    /**
     * Lay thong tin lich su giao dich khac
     *
     * @param contractId
     * @param saleTransDTO
     * @return
     */
    @Override
    public Object findOtherTransactionHistories(Long contractId, SaleTransDTO saleTransDTO) {
        return saleTransRepository.findOtherTransactionHistories(contractId, saleTransDTO);
    }

    /**
     * Export thong tin lich su giao dich khac
     *
     * @param contractId   Ma dinh danh hop dong
     * @param saleTransDTO Du lieu sale trans
     * @return String
     */
    @Override
    public String exportOtherTransactionHistories(Long contractId, SaleTransDTO saleTransDTO) {
        try {
            String fileName = "OtherTransactionHistories" + System.currentTimeMillis() + ".xlsx";

            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE_FORMAT);
            String date = formatter.format(new java.util.Date());
            String title = "Danh sách lịch sử giao dịch khác \nNgày xuất " + date;

            List<SaleTransDTO> saleTransDTOList = (List<SaleTransDTO>) saleTransRepository.findOtherTransactionHistories(contractId, saleTransDTO).getListData();
            ExcellSheet sheetExport = new ExcellSheet();
            //set header
            List<ExcellHeaderEntity> listHeader = new ArrayList<>();

            listHeader.add(new ExcellHeaderEntity("Mã GD"));
            listHeader.add(new ExcellHeaderEntity("Loại GD"));
            listHeader.add(new ExcellHeaderEntity("Người tác động"));
            listHeader.add(new ExcellHeaderEntity("Thời gian GD"));
            listHeader.add(new ExcellHeaderEntity("Số tiền (VNĐ)"));
            listHeader.add(new ExcellHeaderEntity("Nội dung GD"));
            sheetExport.setListHeader(listHeader);

            ExcellDataEntity excellDataEntity = new ExcellDataEntity();
            List<List<Object>> listData = new ArrayList<>();

            //set data
            for (SaleTransDTO saleTrans : saleTransDTOList) {
                List<Object> objectList = new ArrayList<>();
                objectList.add(saleTrans.getSaleTransCode());
                objectList.add(saleTrans.getServiceFeeName());
                objectList.add(saleTrans.getCreateUser());
                objectList.add(saleTrans.getSaleTransDate());
                objectList.add(saleTrans.getAmount());
                objectList.add(saleTrans.getSaleTransContent());

                listData.add(objectList);
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
     * Lay thong tin lich su giao dich khac - APPCPT
     *
     * @param transVehicleOwnerAppDTO
     * @return
     */
    @Override
    public Object vehicleOwnerAppOtherTransactionHistories(SaleTransVehicleOwnerAppDTO transVehicleOwnerAppDTO, String accountUser) {
        ContractEntity contractEntity = contractRepositoryJPA.getByAccountUser(accountUser);
        if (FnCommon.isNullObject(contractEntity)) {
            throw new EtcException("validate.data.not.invalid");
        }
        ResultSelectEntity resultSelectEntity = saleTransRepository.vehicleOwnerAppOtherTransactionHistories(transVehicleOwnerAppDTO, contractEntity.getContractId());

        return resultSelectEntity;
    }

    /**
     * Lay danh sach loai giao dich
     *
     * @param itemParamsEntity params client
     * @return
     */
    @Override
    public List<SaleTransDTO> getServiceFees(SaleTransDTO itemParamsEntity) {

        return saleTransRepository.getServiceFees(itemParamsEntity);
    }

    /**
     * Them thong tin giao dich
     *
     * @param custId       params client
     * @param authenEntity params client
     * @param actTypeId    params client
     * @return
     */
    @Override
    public SaleTransEntity addSaleTrans(Long custId, Long contractId, String contractNo, Authentication authenEntity, Long actTypeId) {
        ServiceFeeEntity serviceFeeEntity = serviceFeeRepositoryJPA.getByActTypeId(actTypeId).get(0);
        if (serviceFeeEntity == null) {
            return null;
        }
        Map<String, Object> attribute = FnCommon.getAttribute(authenEntity);

        SaleTransEntity saleTransEntity = new SaleTransEntity();
        saleTransEntity.setCreateUser(FnCommon.getUserLogin(authenEntity));
        saleTransEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        saleTransEntity.setCustId(custId);
        saleTransEntity.setContractId(contractId);
        saleTransEntity.setContractNo(contractNo);

        if (Objects.nonNull(attribute) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_ID) && attribute.containsKey(Constants.USER_ATTRIBUTE.SHOP_NAME)) {
            saleTransEntity.setShopId(Long.valueOf(attribute.get(Constants.USER_ATTRIBUTE.SHOP_ID).toString()));
            saleTransEntity.setShopName(attribute.get(Constants.USER_ATTRIBUTE.SHOP_NAME).toString());
        }
        /**
         1: Chua thanh toan
         2: Da thanh toan, chua lap hoa don.
         3: Da lap hoa don
         4: Huy
         5. Gui dang ki khong thanh cong
         */
        saleTransEntity.setStatus(SaleTransEntity.Status.PAID_NOT_INVOICED.value);
        /**
         * 1- Ban goi dich vu
         * 2- Ban ve thang quy
         */
        saleTransEntity.setSaleTransType(SaleTransEntity.SaleTransType.PACKAGE.value);
        saleTransEntity.setAmount(serviceFeeEntity.getFee());
        saleTransEntity.setSaleTransDate(new java.sql.Date(System.currentTimeMillis()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        saleTransEntity.setSaleTransCode(String.format("DV%s.%s", contractId, dateFormat.format(saleTransEntity.getSaleTransDate())));
        try {
            return saleTransRepositoryJPA.save(saleTransEntity);
        } catch (Exception e) {
            LOGGER.error("Loi! add sale trans: ", e);
            return null;
        }
    }

    /**
     * Them moi du lieu saleTrans
     *
     * @param custId       Ma dinh danh khach hang
     * @param authenEntity Du lieu xac thuc
     * @param actTypeId
     * @return
     */
    @Override
    public SaleTransEntity addSaleTrans(Long custId, Authentication authenEntity, Long actTypeId) {
        return addSaleTrans(custId, null, null, authenEntity, actTypeId);
    }
}
