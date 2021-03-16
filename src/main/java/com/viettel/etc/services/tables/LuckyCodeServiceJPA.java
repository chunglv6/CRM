package com.viettel.etc.services.tables;

import com.viettel.etc.repositories.tables.LuckyCodeRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.LuckyCodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LuckyCodeServiceJPA {
    @Autowired
    LuckyCodeRepositoryJPA repository;

    public List<LuckyCodeEntity> getAll() {
        return repository.findAll();
    }

    public LuckyCodeEntity getById(Long id) {
        return repository.getOne(id);
    }

    public List<LuckyCodeEntity> saveAll(Iterable<LuckyCodeEntity> list) {
        return repository.saveAll(list);
    }

    public LuckyCodeEntity save(LuckyCodeEntity entity) {
        return repository.save(entity);
    }

    public List<LuckyCodeEntity> getByCustomerIdAndLuckyType(Long customerId, Integer luckyType) {
        return repository.getByCustomerIdAndLuckyType(customerId, luckyType);
    }

    public boolean existsByLuckyCode(String luckyCode) {
        return repository.existsByLuckyCode(luckyCode);
    }
}
