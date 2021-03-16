package com.viettel.etc.repositories;

import com.viettel.etc.repositories.tables.entities.CustQrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Document_type
 *
 * @author ToolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
@Repository
public interface QrCodeRepository extends JpaRepository<CustQrCode, Long> {
    Optional<CustQrCode> findByContractId(Long contractId);
}
