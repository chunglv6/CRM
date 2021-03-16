package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Contract_profile
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "CONTRACT_PROFILE")
public class ContractProfileEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "CONTRACT_PROFILE_SEQ")
    @SequenceGenerator(name = "CONTRACT_PROFILE_SEQ", sequenceName = "CONTRACT_PROFILE_SEQ", allocationSize = 1)
    @Column(name = "CONTRACT_PROFILE_ID")
    Long contractProfileId;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "DOCUMENT_TYPE_ID")
    Long documentTypeId;

    @Column(name = "FILE_NAME")
    String fileName;

    @Column(name = "FILE_SIZE")
    byte[] fileSize;

    @Column(name = "FILE_PATH")
    String filePath;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "APPROVED_USER")
    String approvedUser;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "DEL_USER")
    String delUser;

    @Column(name = "DEL_DATE")
    Date delDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    String status;

    public enum Status {
        EXIST("1"),
        MISSED("2"),
        FAKE("3");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }
}

