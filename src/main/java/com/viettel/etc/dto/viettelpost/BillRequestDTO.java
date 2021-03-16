package com.viettel.etc.dto.viettelpost;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.etc.dto.ServiceRegisterDTO;
import com.viettel.etc.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillRequestDTO {

    @JsonProperty(value = "GROUPADDRESS_ID")
    Long GROUPADDRESS_ID;

    @JsonProperty(value = "CUS_ID")
    Long CUS_ID;

    @JsonProperty(value = "SENDER_FULLNAME")
    String SENDER_FULLNAME;

    @JsonProperty(value = "SENDER_ADDRESS")
    String SENDER_ADDRESS;

    @JsonProperty(value = "SENDER_PHONE")
    String SENDER_PHONE;

    @JsonProperty(value = "SENDER_EMAIL")
    String SENDER_EMAIL;

    @JsonProperty(value = "SENDER_WARD")
    Long SENDER_WARD;

    @JsonProperty(value = "SENDER_DISTRICT")
    Long SENDER_DISTRICT;

    @JsonProperty(value = "SENDER_PROVINCE")
    Long SENDER_PROVINCE;

    @JsonProperty(value = "RECEIVER_FULLNAME")
    String RECEIVER_FULLNAME;

    @JsonProperty(value = "RECEIVER_ADDRESS")
    String RECEIVER_ADDRESS;

    @JsonProperty(value = "RECEIVER_PHONE")
    String RECEIVER_PHONE;

    @JsonProperty(value = "RECEIVER_EMAIL")
    String RECEIVER_EMAIL;

    @JsonProperty(value = "RECEIVER_WARD")
    Long RECEIVER_WARD;

    @JsonProperty(value = "RECEIVER_DISTRICT")
    Long RECEIVER_DISTRICT;

    @JsonProperty(value = "RECEIVER_PROVINCE")
    Long RECEIVER_PROVINCE;

    @JsonProperty(value = "PRODUCT_NAME")
    String PRODUCT_NAME;

    @JsonProperty(value = "PRODUCT_DESCRIPTION")
    String PRODUCT_DESCRIPTION;

    @JsonProperty(value = "PRODUCT_QUANTITY")
    Long PRODUCT_QUANTITY;

    @JsonProperty(value = "PRODUCT_PRICE")
    Long PRODUCT_PRICE;

    @JsonProperty(value = "PRODUCT_TYPE")
    String PRODUCT_TYPE;

    @JsonProperty(value = "ORDER_PAYMENT")
    Long ORDER_PAYMENT;

    @JsonProperty(value = "ORDER_SERVICE")
    String ORDER_SERVICE;

    @JsonProperty(value = "LIST_ITEM")
    List<ItemDTO> LIST_ITEM;

    public BillRequestDTO(ServiceRegisterDTO dto, Long price) {
        if (Constants.STICKY_TYPE.SHOP.equals(dto.getStickType())) {
            GROUPADDRESS_ID = dto.getShopId();
        } else {
            GROUPADDRESS_ID = 0L;
        }
        String produceName;
        if (dto.getPlateNumber() != null) {
            produceName = "Thẻ etag. Biển số: " + dto.getPlateNumber();
        } else {
            produceName = "Thẻ etag";
        }
        CUS_ID = 0L;
        SENDER_FULLNAME = Constants.VDTC_INFO.SENDER_FULLNAME;
        SENDER_ADDRESS = dto.getStreet();
        SENDER_PHONE = Constants.VDTC_INFO.SENDER_PHONE;
        SENDER_EMAIL = Constants.VDTC_INFO.SENDER_EMAIL;
        SENDER_WARD = dto.getWardId();
        SENDER_DISTRICT = dto.getDistrictId();
        SENDER_PROVINCE = dto.getProvinceId();
        RECEIVER_FULLNAME = dto.getFullName();
        RECEIVER_ADDRESS = dto.getStreet();
        RECEIVER_PHONE = dto.getPhoneNumber();
        RECEIVER_EMAIL = dto.getEmail();
        RECEIVER_WARD = dto.getWardId();
        RECEIVER_DISTRICT = dto.getDistrictId();
        RECEIVER_PROVINCE = dto.getProvinceId();
        PRODUCT_NAME = produceName;
        PRODUCT_DESCRIPTION = dto.getPlateNumber();
        PRODUCT_QUANTITY = dto.getNumVehicle() == null ? 1 : dto.getNumVehicle();
        PRODUCT_PRICE = price;
        PRODUCT_TYPE = "HH";
        ORDER_PAYMENT = 2L;
        ORDER_SERVICE = "VCN";
    }
}
