package com.viettel.etc.services.impl;

import com.viettel.etc.repositories.tables.ContractPaymentRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractPaymentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ContractPaymentServiceImplTest {

    @Mock
    ContractPaymentRepositoryJPA contractPaymentServiceJPA;

    ContractPaymentEntity contractPaymentEntity;

    @BeforeEach
    void setUp() {
        contractPaymentEntity = new ContractPaymentEntity();
    }

    @Test
    void findByContractIdAndStatus() {
        given(contractPaymentServiceJPA.findByContractIdAndStatus(1l, "1")).willReturn(contractPaymentEntity);
        ContractPaymentEntity contractPaymentEntityTest = contractPaymentServiceJPA.findByContractIdAndStatus(1l, "1");
        assertNotNull(contractPaymentEntityTest);
    }
}