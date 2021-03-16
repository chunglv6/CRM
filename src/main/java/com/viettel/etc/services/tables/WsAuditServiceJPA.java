package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Autogen class: Create Service For Table Name Ws_audit
 *
 * @author ToolGen
 * @date Wed Jun 24 15:24:42 ICT 2020
 */
@Service
public class WsAuditServiceJPA {

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    public List<WsAuditEntity> findAll() {
        return this.wsAuditRepositoryJPA.findAll();
    }

    public WsAuditEntity save(WsAuditEntity wsAuditEntity) {
        return this.wsAuditRepositoryJPA.save(wsAuditEntity);
    }

    public Optional<WsAuditEntity> findById(Long id) {
        return this.wsAuditRepositoryJPA.findById(id);
    }

    public void deleteById(Long id) {
        this.wsAuditRepositoryJPA.deleteById(id);
    }

    public WsAuditEntity getOne(Long id) {
        return this.wsAuditRepositoryJPA.getOne(id);
    }

    public Boolean existsById(Long id) {
        return this.wsAuditRepositoryJPA.existsById(id);
    }

}
