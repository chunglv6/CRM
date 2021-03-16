package com.viettel.etc.repositories.tables.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Vehicle
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "VEHICLE")
public class VehicleEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "VEHICLE_SEQ")
    @SequenceGenerator(name = "VEHICLE_SEQ", sequenceName = "VEHICLE_SEQ", allocationSize = 1)
    @Column(name = "VEHICLE_ID")
    Long vehicleId;

    @Column(name = "CUST_ID")
    Long custId;

    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "CONTRACT_APPENDIX")
    String contractAppendix;

    @Column(name = "PLATE_NUMBER")
    String plateNumber;

    @Column(name = "VEHICLE_TYPE_ID")
    Long vehicleTypeId;

    @Column(name = "VEHICLE_GROUP_ID")
    Long vehicleGroupId;

    @Column(name = "SEAT_NUMBER")
    Long seatNumber;

    @Column(name = "NET_WEIGHT")
    Double netWeight;

    @Column(name = "GROSS_WEIGHT")
    Double grossWeight;

    @Column(name = "CARGO_WEIGHT")
    Double cargoWeight;

    @Column(name = "PULLING_WEIGHT")
    Double pullingWeight;

    @Column(name = "CHASSIC_NUMBER")
    String chassicNumber;

    @Column(name = "ENGINE_NUMBER")
    String engineNumber;

    @Column(name = "VEHICLE_MARK_ID")
    Long vehicleMarkId;

    @Column(name = "VEHICLE_BRAND_ID")
    Long vehicleBrandId;

    @Column(name = "VEHICLE_COLOUR_ID")
    Long vehicleColourId;

    @Column(name = "PLATE_TYPE")
    Long plateType;

    @Column(name = "STATUS")
    String status;

    @Column(name = "ACTIVE_STATUS")
    String activeStatus;

    @Column(name = "EPC")
    String epc;

    @Column(name = "TID")
    String tid;

    @Column(name = "RFID_SERIAL")
    String rfidSerial;

    @Column(name = "RESERVED_MEMORY")
    String reservedMemory;

    @Column(name = "RFID_TYPE")
    String rfidType;

    @Column(name = "OFFER_ID")
    Long offerId;

    @Column(name = "OFFER_CODE")
    String offerCode;

    @Column(name = "EFF_DATE")
    Date effDate;

    @Column(name = "EXP_DATE")
    Date expDate;

    @Column(name = "DEPT_ID")
    Long deptId;

    @Column(name = "SHOP_ID")
    Long shopId;

    @Column(name = "CREATE_USER")
    String createUser;

    @Column(name = "CREATE_DATE")
    Date createDate;

    @Column(name = "PROFILE_STATUS")
    String profileStatus;

    @Column(name = "APPROVED_USER")
    String approvedUser;

    @Column(name = "APPROVED_DATE")
    Date approvedDate;

    @Column(name = "ADDFILES_USER")
    String addfilesUser;

    @Column(name = "ADDFILES_DATE")
    Date addfilesDate;

    @Column(name = "OWNER")
    String owner;

    @Column(name = "APPENDIX_DATE")
    Date appendixDate;

    @Column(name = "APPENDIX_USERNAME")
    String appendixUsername;

    @Column(name = "NOTE")
    String note;

    @Column(name = "VEHICLE_TYPE_NAME")
    String vehicleTypeName;

    @Column(name = "VEHICLE_TYPE_CODE")
    String vehicleTypeCode;

    @Column(name = "VEHICLE_GROUP_CODE")
    String vehicleGroupCode;

    @Column(name = "PLATE_TYPE_CODE")
    String plateTypeCode;

    @Column(name = "PROMOTION_CODE")
    String promotionCode;


    public enum Status {
        NOT_ACTIVATED("0"),
        ACTIVATED("1"),
        IMPORT("2"),
        SIMILAR("3"),
        NOT_SIMILAR("4");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }

    public enum ActiveStatus {
        NOT_ACTIVATED("0"),
        ACTIVATED("1"),
        CANCEL("2"),
        CLOSED("3"),
        OPEN("4"),
        TRANSFERRED("5");

        public final String value;

        ActiveStatus(String value) {
            this.value = value;
        }
    }

    public enum ProfilesStatus {
        NOT_RECEIVED("1"),
        APPROVED("2"),
        REJECT("3"),
        ADDITIONAL_RECORDS("4");

        public final String value;

        ProfilesStatus(String value) {
            this.value = value;
        }
    }

    public enum RfidType {
        WINDSHIELD("1"),
        LAMP("2");
        public final String value;

        RfidType(String value) {
            this.value = value;
        }
    }

}
