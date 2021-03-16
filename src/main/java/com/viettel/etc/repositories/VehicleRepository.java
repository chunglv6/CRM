package com.viettel.etc.repositories;

import com.viettel.etc.dto.*;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.List;

/**
 * Autogen class Repository Interface: phuong tien
 *
 * @author toolGen
 * @date Wed Jun 24 14:38:40 ICT 2020
 */
public interface VehicleRepository {

    List<ExportVehiclesAssignedDTO> exportVehiclesAssigned(Long contractId);

    ResultSelectEntity checkSignVehicle(List<String> listPlateNumber);

    ResultSelectEntity findProfileByContract(VehicleProfilePaginationDTO requestModel);

    List<VehicleProfilePaginationDTO> downloadProfileByContract(Long profileId);

    ResultSelectEntity findVehicleByContract(VehicleByContractDTO requestModel);

    ResultSelectEntity findVehicleById(Long vehicleId);

    ResultSelectEntity getVehicleRegistry(VehicleDTO itemParamsEntity, String plateNumber);

    ResultSelectEntity findProfileByVehicleId(VehicleProfileDTO requestModel);

    ResultSelectEntity getVehiclesByPlateNumber(VehicleSearchDTO itemParamsEntity);

    ResultSelectEntity getVehicle(VehicleDTO params);

    ResultSelectEntity getVehiclesByPlateNumberForPortalBot(VehicleSearchDTO itemParamsEntity);

    ResultSelectEntity findVehicleAssignedRFID(VehicleByContractDTO requestModel);

    ResultSelectEntity getVehicleRegistryInfo(VehicleDTO itemParamsEntity, String plateNumber);

    ResultSelectEntity getVehicleExp();

    ResultSelectEntity findVehicleSearchTree(VehicleDTO requestModel);
}
