package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqChangePassUserKeycloakDTO {

    @NotNull
    Boolean enabled;

    Boolean temporary;

    String type;

    Long actTypeId;

    Long actReasonId;

    String value;
}
