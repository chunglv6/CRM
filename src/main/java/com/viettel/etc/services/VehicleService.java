package com.viettel.etc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viettel.etc.dto.*;
import com.viettel.etc.repositories.tables.entities.ActionAuditEntity;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.repositories.tables.entities.SaleTransDetailEntity;
import com.viettel.etc.utils.exceptions.DataNotFoundException;
import com.viettel.etc.utils.exceptions.EtcException;
import org.json.JSONException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.List;

/**
 * Autogen class: phuong tien
 *
 * @author ToolGen
 * @date Wed Jun 24 14:38:40 ICT 2020
 */
public interface VehicleService {

    Object registerVehicle(AddVehicleRequestDTO addVehicleRequestDTO, Authentication authentication, Long customerId, Long contractId, String remoteAddr) throws JAXBException, IOException, XMLStreamException, DataNotFoundException, EtcException;

    Object registerVehicleQuick(AddVehicleQuickDTO addVehicleQuickDTO, Authentication authentication, String remoteAddr) throws Exception;

    void updateProfile(Date currDate, Authentication authentication, Long contractId, Long vehicleId, Long actTypeId, Long reasonId, List<VehicleProfileDTO> dataParams) throws Exception;

    void updateProfile(Date currDate, Authentication authentication, Long contractId, Long vehicleId, ActionAuditEntity actionAuditEntity, List<VehicleProfileDTO> dataParams) throws IOException, IllegalAccessException, JSONException;

    ResultAssignRfidDTO vehicleAssign(ListVehicleAssignRfidDTO vehicleAssignRfidDTO, Authentication authentication, Long custId, Long contractId) throws DataNotFoundException;

    String exportVehiclesAssigned(Long contractId, Authentication authentication) throws IOException;

    Object vehicleRegistryInfo(List<AddVehicleRequestDTO> listData) throws Exception;

    void transferVehicles(VehicleTransferDTO vehicleTransferDTO, Authentication authentication) throws Exception;

    Object findProfileByVehicle(VehicleProfilePaginationDTO requestModel);

    VehicleProfilePaginationDTO downloadProfileByContract(Long profileId);

    ActiveRFIDResponseDTO.ActiveResponses activeRFID(RFIDDTO rfidDTO, Authentication authenEntity) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException;

    Object lockRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException;

    Object unlockRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException;

    Object destroyRFID(RFIDDTO rfidDTO, Authentication authentication) throws RuntimeException, JSONException, IllegalAccessException, UnknownHostException;

    Object swapRFID(RFIDDTO rfidDTO, Authentication authentication) throws Exception;

    void transferVehicleOnContract(VehicleTransferDTO vehicleTransferDTO, Authentication authentication) throws Exception;

    Object findVehicleByContract(VehicleByContractDTO requestModel, Authentication authentication);

    Object findVehicleById(Long vehicleId, Authentication authentication);

    ResultFileImportDTO saveFileImportVehicles(MultipartFile file, Authentication authentication, Long customerId, Long contractId) throws IOException, DataNotFoundException;

    Object searchVehicle(VehicleDTO vehicleDTO, Authentication authentication) throws JsonProcessingException;

    String downloadVehiclesTemplate(Authentication authentication);

    void deleteProfile(Authentication authentication, Long profileId, Long vehicleId, UpdateVehicleProfileDTO updateVehicleProfileDTO) throws IOException, EtcException;

    ResponseGetInfoRegisterDTO getVehicleRegistry(String plateNumber, Authentication authentication) throws Exception;

    Object findProfileByVehicleId(VehicleProfileDTO requestModel);

    Object getVehiclesByPlateNumber(VehicleSearchDTO itemParamsEntity);

    Object getVehicle(VehicleDTO params);

    Object findVehicleAssignedRFID(VehicleByContractDTO requestModel, Authentication authentication);

    Object getVehicleRegistryInfo(VehicleDTO itemParamsEntity, String plateNumber);

    Object swapPlateNumber(VehicleSwapPlateNumberDTO vehicleSwapPlateNumberDTO, Authentication authentication) throws Exception;

    Object modifyVehicle(ModifyVehicleDTO modifyVehicleDTO, Long customerId, Long contractId, Long vehicleId, Authentication authentication) throws Exception ;

    String checkVehicle(String plateNumber, String plateTypeCode);

    boolean addTicketWhenSwapRFID(Authentication authentication, Long contractId, String etagNew, List<SaleTransDetailEntity> listSaleTransDetail) throws Exception;

    boolean addOfferExceptionListWhenSwapRFID(Authentication authentication, Long contractId, String etagNew, List<ExceptionListEntity> listExceptionList) throws Exception;

    Object getInfoByPlateNumber(String plate, Authentication authentication) throws Exception;
}
