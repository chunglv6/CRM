package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.Na;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Cust_regis
 *
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "CUST_REGIS")
public class CustRegisEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "CUST_REGIS_SEQ")
    @SequenceGenerator(name = "CUST_REGIS_SEQ", sequenceName = "CUST_REGIS_SEQ", allocationSize = 1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "CUST_REG_ID")
    Long custRegId;

    @Column(name = "CUST_TYPE_ID")
    Long custTypeId;

    @Column(name = "DOCUMENT_NUMBER")
    String documentNumber;

    @Column(name = "DOCUMENT_TYPE_ID")
    Long documentTypeId;

    @Column(name = "TAX_CODE")
    String taxCode;

    @Column(name = "DATE_OF_ISSUE")
    Date dateOfIssue;

    @Column(name = "PLACE_OF_ISSUE")
    String placeOfIssue;

    @Column(name = "REG_STATUS")
    String regStatus;

    @Column(name = "CUST_NAME")
    String custName;

    @Column(name = "BIRTH_DATE")
    Date birthDate;

    @Column(name = "GENDER")
    String gender;

    @Column(name = "AREA_NAME")
    String areaName;

    @Column(name = "STREET")
    String street;

    @Column(name = "AREA_CODE")
    String areaCode;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "PHONE_NUMBER")
    String phoneNumber;

    @Column(name = "FAX")
    String fax;

    @Column(name = "WEBSITE")
    String website;

    @Column(name = "REP_IDENTITY_NUMBER")
    String repIdentityNumber;

    @Column(name = "REP_IDENTITY_TYPE_ID")
    Long repIdentityTypeId;

    @Column(name = "REP_DATE_OF_ISSUE")
    Date repDateOfIssue;

    @Column(name = "REP_PLACE_OF_ISSUE")
    String repPlaceOfIssue;

    @Column(name = "REP_NAME")
    String repName;

    @Column(name = "REP_BIRTH_DATE")
    Date repBirthDate;

    @Column(name = "REP_GENDER")
    String repGender;

    @Column(name = "REP_AREA_NAME")
    String repAreaName;

    @Column(name = "REP_STREET")
    String repStreet;

    @Column(name = "REP_AREA_CODE")
    String repAreaCode;

    @Column(name = "REP_EMAIL")
    String repEmail;

    @Column(name = "REP_PHONE_NUMBER")
    String repPhoneNumber;

    @Column(name = "AUTH_IDENTITY_NUMBER")
    String authIdentityNumber;

    @Column(name = "AUTH_IDENTITY_TYPE_ID")
    Long authIdentityTypeId;

    @Column(name = "AUTH_DATE_OF_ISSUE")
    Date authDateOfIssue;

    @Column(name = "AUTH_PLACE_OF_ISSUE")
    String authPlaceOfIssue;

    @Column(name = "AUTH_NAME")
    String authName;

    @Column(name = "AUTH_BIRTH_DATE")
    Date authBirthDate;

    @Column(name = "AUTH_GENDER")
    String authGender;

    @Column(name = "AUTH_AREA_NAME")
    String authAreaName;

    @Column(name = "AUTH_STREET")
    String authStreet;

    @Column(name = "AUTH_AREA_CODE")
    String authAreaCode;

    @Column(name = "AUTH_EMAIL")
    String authEmail;

    @Column(name = "AUTH_PHONE_NUMBER")
    String authPhoneNumber;

    @Column(name = "ACCOUNT_ID")
    Long accountId;

    @Column(name = "ACCOUNT_USER")
    String accountUser;

    @CreationTimestamp
    @Column(name = "REG_DATE")
    Date regDate;

    @Column(name = "NUM_VEHICLE")
    Long numVehicle;

    @Column(name = "OWNER")
    String owner;

    @Column(name = "PLATE_NUMBER")
    String plateNumber;

    @Column(name = "TIME_FRAME")
    String timeFrame;

    @Column(name = "ORDER_NUMBER")
    String orderNumber;

    @Column(name = "PROVINCE_NAME")
    String provinceName;

    @Column(name = "DISTRIC_NAME")
    String districtName;

    @Column(name = "COMMUNE_NAME")
    String communeName;

    public enum RegStatus {
        NEW("1"),
        SIGNED("2"),
        CANCELLED("3");
        public final String value;

        RegStatus(String value) {
            this.value = value;
        }
    }
}
