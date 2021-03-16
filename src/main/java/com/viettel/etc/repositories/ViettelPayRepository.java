package com.viettel.etc.repositories;

public interface ViettelPayRepository {
    Object findByPlateOrContract(String orderId, String searchType, Long contractId, String contractNo, String plateType, String plateNumber);

    Object findAllVehicleByContractAndPlate(Long contractId);

    Object getInfoOderTicketPurchaseAndExtendedViaSDK(String billingCode);

    Object getTicketExtendedViaSDK(String orderId);

    Object getInfoOderTicketPurchaseAndExtendedViaSDKPrivateStream(String billingCode);

    Object getTicketExtendedViaSDKPrivateStream(String orderId);
}
