package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractSearchDTO extends ContractDTO {
    String inputSearch;

    String custName;

    String repName;

    String authName;

    Long custTypeId;

    String phoneNumber;

    String documentNumber;

    Long documentTypeId;
}
