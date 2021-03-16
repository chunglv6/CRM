package com.viettel.etc.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.util.List;

@Data
public class ResUserKeycloakDTO {
    String id;
    Long createdTimestamp;
    String username;
    boolean enabled;
    boolean emailVerified;
    String firstName;
    String lastName;
    @JsonSetter(nulls= Nulls.AS_EMPTY)
    String email;
    List<GroupUserKeycloak> group;
    UserAttributeKeycloak attributes;

    @Data
    public static class GroupUserKeycloak {
        String id;
        String name;
        String path;
    }

    @Data
    public static class UserAttributeKeycloak {
        @JsonSetter(nulls= Nulls.AS_EMPTY)
        String[] partner_code;
        @JsonSetter(nulls= Nulls.AS_EMPTY)
        String[] partner_type;
        @JsonSetter(nulls= Nulls.AS_EMPTY)
        String[] phone_number;
        @JsonSetter(nulls= Nulls.AS_EMPTY)
        String[] shop_id;
        @JsonSetter(nulls= Nulls.AS_EMPTY)
        String[] shop_name;
        @JsonSetter(nulls= Nulls.AS_EMPTY)
        String[] staff_id;
    }
}
