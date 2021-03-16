package com.viettel.etc.dto.im;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockEtagDTO {

    Long id;

    Long toOwnerId;

    Long toOwnerType;

    Long etagTypeId;

    String serial;

    String tid;

    String epc;

    String poNumber;

    String boxNumber;

    Long status;

    String createdUser;

    String updateUser;

    String createdDate;

    String updateDate;

    Long warehouseId;
}
