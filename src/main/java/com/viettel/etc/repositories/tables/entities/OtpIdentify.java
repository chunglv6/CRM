package com.viettel.etc.repositories.tables.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpIdentify implements Serializable {

    public static final int REGISTER = 0;
    public static final int RESET_PASSWORD = 1;
    public static final int SIGN = 3;

    String phone;

    Integer confirmType;
}
