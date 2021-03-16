package com.viettel.etc.services.tables;
import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import com.viettel.etc.repositories.tables.SaleOrderDetailRepositoryJPA;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Autogen class: Create Service For Table Name Sale_order_detail
 * 
 * @author ToolGen
 * @date Thu Oct 22 17:42:25 ICT 2020
 */
@Service
public class SaleOrderDetailServiceJPA {
    @Autowired
    SaleOrderDetailRepositoryJPA sale_order_detail;
    public List<SaleOrderDetailEntity>  findAll() {
        return this.sale_order_detail.findAll();
    }
    public SaleOrderDetailEntity save(SaleOrderDetailEntity Sale_order_detail) {
        return this.sale_order_detail.save(Sale_order_detail);
    }
    public Optional<SaleOrderDetailEntity> findById(Long id) {
        return this.sale_order_detail.findById(id);
    }
    public void deleteById(Long id) {
        this.sale_order_detail.deleteById(id);
    }
    public SaleOrderDetailEntity getOne(Long id) {
        return this.sale_order_detail.getOne(id);
    }
    public Boolean existsById(Long id) {
        return this.sale_order_detail.existsById(id);
    }

}