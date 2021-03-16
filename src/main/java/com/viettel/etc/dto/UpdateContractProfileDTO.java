package com.viettel.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateContractProfileDTO {
    @Valid
    List<ContractProfileDTO> contractProfiles = new ArrayList<>();

    @NotNull
    Long actTypeId;

    Long reasonId;
}
