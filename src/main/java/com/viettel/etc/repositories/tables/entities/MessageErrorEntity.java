package com.viettel.etc.repositories.tables.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MESSAGE_ERROR")
public class MessageErrorEntity implements Serializable {
    @Id
    @Column(name = "CODE")
    Long code;

    @Column(name = "MESSAGE")
    String message;

    @Column(name = "MESSAGE_VI")
    String messageVi;

    @Column(name = "MESSAGE_EN")
    String messageEn;
}
