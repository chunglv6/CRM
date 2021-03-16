package com.viettel.etc.dto.viettelpost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmOrderDTO {

    String orderNumber;

    String vtpUsername;

    String vtpMaBuuCuc;

    Long contractPrice;

    Long vehicleId;
}
