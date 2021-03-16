package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MergeContractDTO {
    private List<Long> secondaryContractId;

    @Valid
    private List<ContractProfileDTO> contractProfiles = new ArrayList<>();

    @NotNull
    Long reasonId;

    Long price;

    Long amount;

    @NotNull
    Long actTypeId;
}
