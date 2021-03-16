package com.viettel.etc.services;

import com.viettel.etc.repositories.tables.entities.MessageErrorEntity;

import java.util.List;

public interface MessageErrorService {
    List<MessageErrorEntity> getAll();
}
