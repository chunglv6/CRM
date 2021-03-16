package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Vehicle_priority
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:25 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "VEHICLE_PRIORITY")
public class VehiclePriorityEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "VEHICLE_PRIORITY_SEQ")
    @SequenceGenerator(name = "VEHICLE_PRIORITY_SEQ", sequenceName = "VEHICLE_PRIORITY_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "VEHICLE_PRIORITIZE_ID")
    Long vehiclePrioritizeId;

    @Column(name = "PLATE_NUMBER")
    String plateNumber;

    @Column(name = "PLATE_TYPE_ID")
    Long plateTypeId;

    @Column(name = "VEHICLE_TYPE_ID")
    Long vehicleTypeId;

    @Column(name = "EPC")
    String epc;

    @Column(name = "TID")
    String tid;

    @Column(name = "RFID_SERIAL")
    String rfidSerial;

    @Column(name = "CUST_NAME")
    String custName;

    @Column(name = "CONTRACT_NO")
    String contractNo;

    @Column(name = "CONTRACT_ID")
    String contractId;

    @Column(name = "ROUTE_ID")
    Long routeId;

    @Column(name = "STAGE_ID")
    Long stageId;

    @Column(name = "STATION_ID")
    Long stationId;

    @Column(name = "PRIORITIZE_TYPE")
    Long prioritizeType;

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "DOC_NAME")
    String docName;

    @Column(name = "DOC_SIZE")
    String docSize;

    @Column(name = "DOC_FILE_PATH")
    String docFilePath;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "APPROVED_USE")
    String approvedUse;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "DEL_USER")
    String delUser;

    @Column(name = "DEL_DATE")
    Date delDate;
}
