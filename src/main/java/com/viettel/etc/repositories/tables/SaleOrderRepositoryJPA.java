package com.viettel.etc.repositories.tables;

import com.viettel.etc.repositories.tables.entities.SaleOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Sale_order
 *
 * @author ToolGen
 * @date Thu Oct 22 17:41:46 ICT 2020
 */
@Repository
public interface SaleOrderRepositoryJPA extends JpaRepository<SaleOrderEntity, Long> {
    SaleOrderEntity findBySaleOrderId(Long saleOrderId);

    Optional<SaleOrderEntity> findBySaleOrderIdAndStatus(Long saleOrderId, String status);

    Optional<SaleOrderEntity> findBySaleOrderIdAndSaleOrderTypeAndSaleOrderSourceAndStatus(Long saleOrderId,
                                                                                           String saleOrderType,
                                                                                           String saleOrderSource,
                                                                                           String status);

    Optional<SaleOrderEntity> findBySaleOrderIdAndAmountAndContractIdAndSaleOrderTypeAndStatus(Long saleOrderId,
                                                                                               Long amount,
                                                                                               Long contractId,
                                                                                               String saleOrderType,
                                                                                               String status);
}
