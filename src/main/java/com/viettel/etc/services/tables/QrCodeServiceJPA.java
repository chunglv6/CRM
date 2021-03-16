package com.viettel.etc.services.tables;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.etc.repositories.QrCodeRepository;
import com.viettel.etc.repositories.tables.entities.CustQrCode;

/**
 * Autogen class: Create Service For Table Name Act_id_type_mapping
 *
 * @author ToolGen
 * @since Wed Jun 24 11:57:23 ICT 2020
 */
@Service
public class QrCodeServiceJPA {

    @Autowired
    QrCodeRepository qrCodeRepository;

    public Optional<CustQrCode> findByCustId(Long id) {
        return qrCodeRepository.findByContractId(id);
    }

    public CustQrCode saveQrCode(CustQrCode custQrCode) {
        return qrCodeRepository.save(custQrCode);
    }
}
