package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import com.viettel.etc.repositories.tables.entities.SaleOrderEntity;
import com.viettel.etc.repositories.tables.SaleOrderRepositoryJPA;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Autogen class: Create Service For Table Name Sale_order
 * 
 * @author ToolGen
 * @date Thu Oct 22 17:41:46 ICT 2020
 */
@Service
public class SaleOrderServiceJPA {

    @Autowired
    SaleOrderRepositoryJPA sale_order;

    public List<SaleOrderEntity>  findAll() {
        return this.sale_order.findAll();
    }

    public SaleOrderEntity save(SaleOrderEntity Sale_order) {
        return this.sale_order.save(Sale_order);
    }

    public Optional<SaleOrderEntity> findById(Long id) {
        return this.sale_order.findById(id);
    }

    public void deleteById(Long id) {
        this.sale_order.deleteById(id);
    }

    public SaleOrderEntity getOne(Long id) {
        return this.sale_order.getOne(id);
    }

    public Boolean existsById(Long id) {
        return this.sale_order.existsById(id);
    }

    public Optional<SaleOrderEntity> findBySaleOrderIdAndAmountAndContractIdAndStatus(Long saleOrderId, Long amount, Long contractId, String saleOrderType, String status) {
        return this.sale_order.findBySaleOrderIdAndAmountAndContractIdAndSaleOrderTypeAndStatus(saleOrderId, amount, contractId, saleOrderType, status);
    }

}