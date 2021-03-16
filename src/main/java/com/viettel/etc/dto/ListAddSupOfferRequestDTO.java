package com.viettel.etc.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListAddSupOfferRequestDTO {
    List<AddSupOfferRequestDTO> listContract = new ArrayList<>();
}
