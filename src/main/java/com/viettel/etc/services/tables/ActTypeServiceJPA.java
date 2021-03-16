package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.entities.ActTypeEntity;
import com.viettel.etc.repositories.tables.ActTypeRepositoryJPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Autogen class: Create Service For Table Name Act_type
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:23 ICT 2020
 */
@Service
public class ActTypeServiceJPA {

    @Autowired
    ActTypeRepositoryJPA actTypeRepositoryJPA;

    public List<ActTypeEntity> findAll() {
        return this.actTypeRepositoryJPA.findAll();
    }

    public ActTypeEntity save(ActTypeEntity actTypeEntity) {
        return this.actTypeRepositoryJPA.save(actTypeEntity);
    }

    public Optional<ActTypeEntity> findById(Long id) {
        return this.actTypeRepositoryJPA.findById(id);
    }

    public void deleteById(Long id) {
        this.actTypeRepositoryJPA.deleteById(id);
    }

    public ActTypeEntity getOne(Long id) {
        return this.actTypeRepositoryJPA.getOne(id);
    }

    public Boolean existsById(Long id) {
        return this.actTypeRepositoryJPA.existsById(id);
    }
    public List<ActTypeEntity> findAllByCodeAndStatus(String code,String status) {
        return this.actTypeRepositoryJPA.findAllByCodeAndStatus(code,status);
    }

    public Long findActTypeIdByCode(String code) {
        return this.actTypeRepositoryJPA.findActTypeIdByCode(code);
    }
}
