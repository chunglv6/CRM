package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.SaleOrderDTO;
import com.viettel.etc.repositories.SaleOrderRepository;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;
import com.viettel.etc.xlibrary.core.repositories.CommonDataBaseRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.HashMap;

@Repository
public class SaleOrderRepositoryImpl extends CommonDataBaseRepository implements SaleOrderRepository {
    @Override
    public ResultSelectEntity getSaleOrder(SaleOrderDTO params) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> mapParams = new HashMap<>();

        sql.append("select SALE_ORDER_ID    as      saleOrderId,        \n").
            append("SALE_ORDER_DATE         as      saleOrderDate,      \n").
            append("SALE_ORDER_TYPE         as      saleOrderType,      \n").
            append("SALE_ORDER_SOURCE       as      saleOrderSource,    \n").
            append("STATUS                  as      status,             \n").
            append("METHOD_RECHARGE_ID      as      methodRechargeId,   \n").
            append("PAYMENT_METHOD_ID       as      paymentMethodId,    \n").
            append("AMOUNT                  as      amount,             \n").
            append("QUANTITY                as      quantity,           \n").
            append("DISCOUNT                as      discount,           \n").
            append("PROMOTION               as      promotion,          \n").
            append("AMOUNT_TAX              as      amountTax,          \n").
            append("AMOUNT_NOT_TAX          as      amountNotTax,       \n").
            append("VAT                     as      vat,                \n").
            append("TAX                     as      tax,                \n").
            append("CUST_ID                 as      custId,             \n").
            append("CONTRACT_ID             as      contractId,         \n").
            append("CONTRACT_NO             as      contractNo,         \n").
            append("DESCRIPTION             as      description,        \n").
            append("SOURCE_CONTRACT_ID      as      sourceContractId,   \n").
            append("SOURCE_CONTRACT_NO      as      sourceContractNo,   \n").
            append("PAY_GATE_STATUS         as      payGateStatus,      \n").
            append("PAY_GATE_ERROR_CODE     as      payGateErrorCode,   \n").
            append("VERIFY_STATUS           as      verifyStatus,       \n").
            append("OCS_STATUS              as      ocsStatus           \n").
            append("from SALE_ORDER where 1 = 1                         \n");

        if (params.getContractNo() != null) {
            sql.append("and UPPER(CONTRACT_NO) = :contractNo \n");
            mapParams.put("contractNo", params.getContractNo().trim().toUpperCase());
        }

        if (params.getPayGateStatus() != null) {
            sql.append("and PAY_GATE_STATUS = :payGateStatus \n");
            mapParams.put("payGateStatus", params.getPayGateStatus());
        }

        if (params.getOcsStatus() != null) {
            sql.append("and OCS_STATUS = :ocsStatus \n");
            mapParams.put("ocsStatus", params.getOcsStatus());
        }

        if (params.getSaleOrderSource() != null) {
            sql.append("and SALE_ORDER_SOURCE = :saleOrderSource \n");
            mapParams.put("saleOrderSource", params.getSaleOrderSource());
        }

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);

        if (params.getSaleOrderBefore() != null) {
            sql.append("and SALE_ORDER_DATE >= to_date(:startDate, :format ) \n");
            mapParams.put("startDate", formatter.format(params.getSaleOrderBefore()));
            mapParams.put("format", Constants.ORACLE_DATE_FORMAT);
        }

        if (params.getSaleOrderAfter() != null) {
            sql.append("and SALE_ORDER_DATE <= to_date(:endDate, :format ) \n");
            mapParams.put("endDate", formatter.format(params.getSaleOrderAfter()));
            mapParams.put("format", Constants.ORACLE_DATE_FORMAT);
        }
        sql.append("order by SALE_ORDER_DATE DESC ");

        Integer start = null;
        if (params.getStartrecord() != null) {
            start = params.getStartrecord();
        }

        Integer pageSize = null;
        if (params.getPagesize() != null) {
            pageSize = params.getPagesize();
        }
        return getListDataAndCount(sql, mapParams, start, pageSize, SaleOrderDTO.class);
    }
}
