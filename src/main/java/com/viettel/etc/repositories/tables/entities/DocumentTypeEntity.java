package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Document_type
 * 
 * @author ToolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "DOCUMENT_TYPE")
public class DocumentTypeEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "DOCUMENT_TYPE_SEQ")
    @SequenceGenerator(name = "DOCUMENT_TYPE_SEQ", sequenceName = "DOCUMENT_TYPE_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "DOCUMENT_TYPE_ID")
    Long documentTypeId;

    @Column(name = "CODE")
    String code;

    @Column(name = "NAME")
    String name;

    @Column(name = "TYPE")
    String type;

    @Column(name = "ADDITIONAL_DATE")
    Long additionalDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    String status;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "UPDATE_DATE")
    Date updateDate;
}