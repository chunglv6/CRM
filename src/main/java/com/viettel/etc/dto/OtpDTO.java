package com.viettel.etc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thong tin OTP
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpDTO {

    Long contractId;

    String phone;

    String user;

    Integer confirmType;
}
