package com.viettel.etc.repositories.tables.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import com.viettel.etc.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class Entity: Create Entity For Table Name Customer
 * 
 * @author ToolGen
 * @date Wed Jun 24 11:57:24 ICT 2020
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "CUST_QRCODE")
public class CustQrCode {
	
	@Id
    @Column(name = "CONTRACT_ID")
    Long contractId;

    @Column(name = "QRCODE_PATH")
    String qrCodePath;
    
    @JsonFormat(shape = Shape.STRING,pattern = Constants.COMMON_DATE_TIME_FORMAT)
    @Column(name = "CREATE_DATE")
    Date createDate;

    
}
