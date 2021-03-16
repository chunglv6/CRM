package com.viettel.etc.services;

import com.viettel.etc.dto.*;
import com.viettel.etc.dto.ocs.OCSCreateContractForm;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
import com.viettel.etc.dto.ocs.RemoveSupOfferRoaming;
import org.springframework.security.core.Authentication;

import java.util.LinkedHashMap;

public interface OCSService {

    OCSResponse createVehicleOCS(AddVehicleRequestDTO addVehicleRequestDTO, Authentication authentication, long actTypeId);

    OCSResponse modifyVehicleOCS(UpdateVehicleRequestOCSDTO updateVehicleRequestOCSDTO, Authentication authentication, boolean isCardService);

    OCSResponse createContract(OCSCreateContractForm form, Authentication authentication, int actionTypeId);

    OCSResponse updateContract(OCSUpdateContractForm form, Authentication authentication, int actionTypeId);

    OCSResponse terminateContract(OCSUpdateContractForm form, Authentication authentication, int actionTypeId);

    OCSResponse deleteVehicle(String RFID, String contractId, Authentication authentication, int actionTypeId);

    OCSResponse charge(AddSupOfferRequestDTO addSupOfferRequestDTO, Authentication authentication, Long contractId, String partyCode) throws Exception;

    OCSResponse addSupOffer(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Authentication authentication, long actTypeId, Long contractId,
                            Long staffId, String staffName, String partyCode) throws Exception;

    String getContractInfo(Authentication authentication, Long contractId);

    OCSResponse deleteSupOffer(Authentication authentication, Long actTypeId, Long contractId, String epc, String offerId, String offerLevel);

    OCSResponse addBalance(Authentication authentication, Long actTypeId, Long contractId, Long amount);

    OCSResponse addBalance(Authentication authentication, Long actTypeId, Long contractId, Long amount, String partyCode, String type, String channel);

    OCSResponse transferVehicle(String EPC, String TID, String contractId, String newContractId, Authentication authentication, int actionTypeId);

    OCSResponse changeSupOffer(VehicleAddSuffOfferDTO vehicleAddSuffOfferDTO, Authentication authentication, Long contractId, Long staffId, String staffName, String isRecurring) throws Exception;

    LinkedHashMap<?, ?> queryVehicleOcs(String epc, Authentication authentication);

    LinkedHashMap<?, ?> queryContractOcs(String contractId, Authentication authentication);

    OCSResponse addSupOfferRoamingBoo1(AddSupOfferRoamingDTO addSupOfferRoamingDTO, Authentication authentication) throws Exception;

    OCSResponse removeSupOfferRoaming(RemoveSupOfferRoaming removeSupOfferRoaming, Authentication authentication) throws Exception;

    OCSResponse changeSupOfferTicket(ChargeSupOfferDTO chargeSupOfferDTO, Authentication authentication) throws Exception;

    OCSResponse addBalanceNew(Authentication authentication, Long actTypeId, Long contractId, Long amount, String channel);
}
