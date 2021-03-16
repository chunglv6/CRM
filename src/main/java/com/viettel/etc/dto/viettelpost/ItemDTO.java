package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTO {
    @JsonProperty(value = "PRODUCT_NAME")
    String PRODUCT_NAME;

    @JsonProperty(value = "PRODUCT_PRICE")
    Long PRODUCT_PRICE;

    @JsonProperty(value = "PRODUCT_WEIGHT")
    Long PRODUCT_WEIGHT;

    @JsonProperty(value = "PRODUCT_QUANTITY")
    Long PRODUCT_QUANTITY;
}
