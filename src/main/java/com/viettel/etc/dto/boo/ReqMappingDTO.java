package com.viettel.etc.dto.boo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Du lieu request cho danh muc mapping Boo
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReqMappingDTO {
    String type;
    String code;
    String name;
    String value;
    String description;
}
