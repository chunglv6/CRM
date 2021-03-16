package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.SaleOrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Sale_order_detail
 *
 * @author ToolGen
 * @date Thu Oct 22 17:42:25 ICT 2020
 */
@Repository
public interface SaleOrderDetailRepositoryJPA extends JpaRepository<SaleOrderDetailEntity, Long> {
    List<SaleOrderDetailEntity> findAllBySaleOrderId(Long saleOrderId);
}
