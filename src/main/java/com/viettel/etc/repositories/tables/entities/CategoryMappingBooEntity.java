package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * entity lay danh mucj mapping boo
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "BOO_LIST_MAPPING")
public class CategoryMappingBooEntity {
    @Id
    @GeneratedValue(generator = "BOO_LIST_MAPPING_SEQ")
    @SequenceGenerator(name = "BOO_LIST_MAPPING_SEQ", sequenceName = "BOO_LIST_MAPPING_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "BOO_LIST_MAPPING_ID")
    Long id;

    @Column(name = "TYPE")
    String type;

    @Column(name = "CODE")
    String code;

    @Column(name = "NAME")
    String name;

    @Column(name = "VALUE")
    String value;

    @Column(name = "DESCRIPTION")
    String description;
}
