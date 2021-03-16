
package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Act_id_type_mapping
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ACT_ID_TYPE_MAPPING")
public class ActIdTypeMappingEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "ACT_ID_TYPE_MAPPING_SEQ")
    @SequenceGenerator(name = "ACT_ID_TYPE_MAPPING_SEQ", sequenceName = "ACT_ID_TYPE_MAPPING_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "ID")
    Long id;

    @Column(name = "ACT_TYPE_ID")
    Long actTypeId;

    @Column(name = "DOCUMENT_TYPE_ID")
    Long documentTypeId;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "UPDATE_USER")
    String updateUser;

    @Column(name = "UPDATE_DATE")
    Date updateDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    String status;

    @Column(name = "ACT_REASON_ID")
    Long actReasonId;
}
