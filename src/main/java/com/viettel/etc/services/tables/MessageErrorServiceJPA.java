package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.MessageErrorRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.MessageErrorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Autogen class Repository Interface: Create Repository For Table Name Message_error
 *
 * @author ToolGen
 * @date Wed Sep 24 11:57:24 ICT 2020
 */
@Service
public class MessageErrorServiceJPA {
    @Autowired
    MessageErrorRepositoryJPA messageErrorRepositoryJPA;

    public List<MessageErrorEntity> findAll() {
        return messageErrorRepositoryJPA.findAll();
    }
}
