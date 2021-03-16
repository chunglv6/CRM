package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Autogen class DTO: Lop lay danh sach ve chung tu
 *
 * @author ToolGen
 * @date Fri Jul 03 10:13:49 ICT 2020
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MobileUserDetailDTO {

    Long cusTypeId;

    String userName;

    String userId;

    String birth;

    String gender;

    String address;

    String identifier;

    String repIdentifier;

    Long repIdentifierType;

    String dateOfIssue;

    String placeOfIssue;

    String contractNo;

    String signDate;

    String effDate;

    String expDate;

    String phone;

    String email;

    String customerId;

    String contractId;

    Long documentType;
}
