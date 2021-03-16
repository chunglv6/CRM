package com.viettel.etc.services.impl;

import com.viettel.etc.repositories.tables.MessageErrorRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.MessageErrorEntity;
import com.viettel.etc.services.MessageErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageErrorServiceImpl implements MessageErrorService {
    @Autowired
    MessageErrorRepositoryJPA messageErrorRepositoryJPA;

    @Override
    public List<MessageErrorEntity> getAll() {
        return messageErrorRepositoryJPA.findAll();
    }
}
