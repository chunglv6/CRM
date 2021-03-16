package com.viettel.etc.dto;
import com.viettel.etc.dto.AcceptBriefcasesDTO;
import com.viettel.etc.dto.AcceptBriefcasesVehicleDTO;
import com.viettel.etc.dto.ActionAuditDetailDTO;
import com.viettel.etc.dto.ActionAuditDetailTempDTO;
import com.viettel.etc.dto.ActionAuditDTO;
import com.viettel.etc.dto.ActionAuditHistoryDetailDTO;
import com.viettel.etc.dto.ActionAuditHistoryDTO;
import com.viettel.etc.dto.ActionDTO;
import com.viettel.etc.dto.ActiveRFIDResponseDTO;
import com.viettel.etc.dto.ActReasonDTO;
import com.viettel.etc.dto.ActTypeDTO;
import com.viettel.etc.dto.ActTypeMappingDTO;
import com.viettel.etc.dto.AdditionalBriefcasesDTO;
import com.viettel.etc.dto.AddServicePlanDTO;
import com.viettel.etc.dto.AddSupOfferRequestDTO;
import com.viettel.etc.dto.AddVehicleRequestDTO;
import com.viettel.etc.dto.AssignRfidDTO;
import com.viettel.etc.dto.AttachmentFileDTO;
import com.viettel.etc.dto.AuditHistoryDTO;
import com.viettel.etc.dto.BotRevenueShareDTO;
import com.viettel.etc.dto.BriefcasesDocumentsDTO;
import com.viettel.etc.dto.CategoryDTO;
import com.viettel.etc.dto.ContractByCustomerDTO;
import com.viettel.etc.dto.ContractDTO;
import com.viettel.etc.dto.ContractProfileDTO;
import com.viettel.etc.dto.ContractSearchDTO;
import com.viettel.etc.dto.CustomerDTO;
import com.viettel.etc.dto.CustomerFindByIdDTO;
import com.viettel.etc.dto.CustomerSearchDTO;
import com.viettel.etc.dto.CustRegisterDTO;
import com.viettel.etc.dto.CustTypeMappingDTO;
import com.viettel.etc.dto.DataTypeDTO;
import com.viettel.etc.dto.DestroyTicketDTO;
import com.viettel.etc.dto.DocumentDTO;
import com.viettel.etc.dto.ExportVehiclesAssignedDTO;
import com.viettel.etc.dto.FindByFilterResponse;
import com.viettel.etc.dto.GetInfoRegisterDTO;
import com.viettel.etc.dto.ListVehicleAssignRfidDTO;
import com.viettel.etc.dto.MergeContractDTO;
import com.viettel.etc.dto.MobileUserDetailDTO;
import com.viettel.etc.dto.ModifyVehicleDTO;
import com.viettel.etc.dto.OtpDTO;
import com.viettel.etc.dto.PackageOfferDTO;
import com.viettel.etc.dto.PriceListDTO;
import com.viettel.etc.dto.ProdPackProductOfferTypeDTO;
import com.viettel.etc.dto.ProductPackageDTO;
import com.viettel.etc.dto.ProductPackageFeeDTO;
import com.viettel.etc.dto.ProfileDTO;
import com.viettel.etc.dto.PromotionAssignDTO;
import com.viettel.etc.dto.PromotionDTO;
import com.viettel.etc.dto.RequestGetFeeChargeTicketDTO;
import com.viettel.etc.dto.ResponseGetInfoRegisterDTO;
import com.viettel.etc.dto.ResultAssignRfidDTO;
import com.viettel.etc.dto.ResultFileImportDTO;
import com.viettel.etc.dto.RFIDDTO;
import com.viettel.etc.dto.SaleBusinessMesDTO;
import com.viettel.etc.dto.SaleOrderDetailDTO;
import com.viettel.etc.dto.SaleServiceAdvanceDTO;
import com.viettel.etc.dto.SaleServiceModelAdvanceDTO;
import com.viettel.etc.dto.SaleTransDelBoo1DTO;
import com.viettel.etc.dto.SaleTransDetailDTO;
import com.viettel.etc.dto.SaleTransDetailVehicleOwnerAppDTO;
import com.viettel.etc.dto.SaleTransDTO;
import com.viettel.etc.dto.SaleTransVehicleOwnerAppDTO;
import com.viettel.etc.dto.SearchBriefcasesDTO;
import com.viettel.etc.dto.SearchContractByCustomerDTO;
import com.viettel.etc.dto.SearchContractDTO;
import com.viettel.etc.dto.SearchInfoDTO;
import com.viettel.etc.dto.ServiceFeeDTO;
import com.viettel.etc.dto.ServicePlanDTO;
import com.viettel.etc.dto.ServicePlanFeeDTO;
import com.viettel.etc.dto.ServicePlanTypeDTO;
import com.viettel.etc.dto.ServicePlanVehicleDuplicateDTO;
import com.viettel.etc.dto.ServiceRegisterDTO;
import com.viettel.etc.dto.SplitContractDTO;
import com.viettel.etc.dto.StageBooDTO;
import com.viettel.etc.dto.StageDTO;
import com.viettel.etc.dto.StationBooDTO;
import com.viettel.etc.dto.StationDTO;
import com.viettel.etc.dto.SupOfferDTOSuccesDTO;
import com.viettel.etc.dto.TerminateContractDTO;
import com.viettel.etc.dto.TopupCashResponseDTO;
import com.viettel.etc.dto.TopupDTO;
import com.viettel.etc.dto.TopupExportDTO;
import com.viettel.etc.dto.UpdateContractProfileDTO;
import com.viettel.etc.dto.UpdateVehicleProfileDTO;
import com.viettel.etc.dto.UpdateVehicleRequestOCSDTO;
import com.viettel.etc.dto.UsersDTO;
import com.viettel.etc.dto.VehicleAddSuffOfferDTO;
import com.viettel.etc.dto.VehicleByContractDTO;
import com.viettel.etc.dto.VehicleDTO;
import com.viettel.etc.dto.VehicleGroupDTO;
import com.viettel.etc.dto.VehicleGroupIdDTO;
import com.viettel.etc.dto.VehicleGroupIdResponseDTO;
import com.viettel.etc.dto.VehicleGroupResponse;
import com.viettel.etc.dto.VehicleInforRegistryDTO;
import com.viettel.etc.dto.VehicleProfileDTO;
import com.viettel.etc.dto.VehicleProfilePaginationDTO;
import com.viettel.etc.dto.VehicleSearchDTO;
import com.viettel.etc.dto.VehiclesExceptionDTO;
import com.viettel.etc.dto.VehicleSwapPlateNumberDTO;
import com.viettel.etc.dto.VehicleTransferDTO;
import com.viettel.etc.dto.VehicleTypeDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.hamcrest.Matchers;
import static org.hamcrest.CoreMatchers.allOf;
import static com.google.code.beanmatchers.BeanMatchers.*;
import org.junit.jupiter.api.Test;

class DTOTest {

	@BeforeEach
	void setUp() {
	}

	@Test
	void AcceptBriefcasesDTO (){
		assertThat(AcceptBriefcasesDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AcceptBriefcasesVehicleDTO (){
		assertThat(AcceptBriefcasesVehicleDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditDetailDTO (){
		assertThat(ActionAuditDetailDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditDetailTempDTO (){
		assertThat(ActionAuditDetailTempDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditDTO (){
		assertThat(ActionAuditDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditHistoryDetailDTO (){
		assertThat(ActionAuditHistoryDetailDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditHistoryDTO (){
		assertThat(ActionAuditHistoryDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionDTO (){
		assertThat(ActionDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActiveRFIDResponseDTO (){
		assertThat(ActiveRFIDResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ActiveRFIDResponseDTO.ActiveResponses.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActReasonDTO (){
		assertThat(ActReasonDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActTypeDTO (){
		assertThat(ActTypeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActTypeMappingDTO (){
		assertThat(ActTypeMappingDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AdditionalBriefcasesDTO (){
		assertThat(AdditionalBriefcasesDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AddServicePlanDTO (){
		assertThat(AddServicePlanDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AddSupOfferRequestDTO (){
		assertThat(AddSupOfferRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AddVehicleRequestDTO (){
		assertThat(AddVehicleRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AssignRfidDTO (){
		assertThat(AssignRfidDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AttachmentFileDTO (){
		assertThat(AttachmentFileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AuditHistoryDTO (){
		assertThat(AuditHistoryDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void BotRevenueShareDTO (){
		assertThat(BotRevenueShareDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void BriefcasesDocumentsDTO (){
		assertThat(BriefcasesDocumentsDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CategoryDTO (){
		assertThat(CategoryDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(CategoryDTO.CategoryData.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(CategoryDTO.CatagoryMethodRecharge.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(CategoryDTO.Category.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(CategoryDTO.Categories.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ContractByCustomerDTO (){
		assertThat(ContractByCustomerDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSettersExcluding("dataToEntityOnEditContract")));
	}

	@Test
	void ContractDTO (){
		assertThat(ContractDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSettersExcluding("dataToEntityOnEditContract")));
		assertThat(ContractDTO.OCSContractInfo.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ContractProfileDTO (){
		assertThat(ContractProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
//		assertThat(ContractProfileDTO.ProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ContractSearchDTO (){
		assertThat(ContractSearchDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSettersExcluding("dataToEntityOnEditContract")));
	}

	@Test
	void CustomerDTO (){
		assertThat(CustomerDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CustomerFindByIdDTO (){
		assertThat(CustomerFindByIdDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CustomerSearchDTO (){
		assertThat(CustomerSearchDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CustRegisterDTO (){
		assertThat(CustRegisterDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CustTypeMappingDTO (){
		assertThat(CustTypeMappingDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void DataTypeDTO (){
		assertThat(DataTypeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(DataTypeDTO.DataType.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void DestroyTicketDTO (){
		assertThat(DestroyTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void DocumentDTO (){
		assertThat(DocumentDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ExportVehiclesAssignedDTO (){
		assertThat(ExportVehiclesAssignedDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void FindByFilterResponse (){
		assertThat(FindByFilterResponse.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void GetInfoRegisterDTO (){
//		assertThat(GetInfoRegisterDTO.class, allOf(hasValidGettersAndSetters()));
	}

	@Test
	void ListVehicleAssignRfidDTO (){
		assertThat(ListVehicleAssignRfidDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MergeContractDTO (){
		assertThat(MergeContractDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MobileUserDetailDTO (){
		assertThat(MobileUserDetailDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ModifyVehicleDTO (){
		assertThat(ModifyVehicleDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void OtpDTO (){
		assertThat(OtpDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void PackageOfferDTO (){
		assertThat(PackageOfferDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void PriceListDTO (){
		assertThat(PriceListDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ProdPackProductOfferTypeDTO (){
		assertThat(ProdPackProductOfferTypeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ProductPackageDTO (){
		assertThat(ProductPackageDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ProductPackageFeeDTO (){
		assertThat(ProductPackageFeeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ProfileDTO (){
		assertThat(ProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void PromotionAssignDTO (){
		assertThat(PromotionAssignDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void PromotionDTO (){
		assertThat(PromotionDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestGetFeeChargeTicketDTO (){
		assertThat(RequestGetFeeChargeTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResponseGetInfoRegisterDTO (){
		assertThat(ResponseGetInfoRegisterDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResultAssignRfidDTO (){
		assertThat(ResultAssignRfidDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResultFileImportDTO (){
		assertThat(ResultFileImportDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RFIDDTO (){
		assertThat(RFIDDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(RFIDDTO.ActiveResponses.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleBusinessMesDTO (){
//		assertThat(SaleBusinessMesDTO.class, allOf(hasValidGettersAndSettersExcluding("successResult")));
	}

	@Test
	void SaleOrderDetailDTO (){
		assertThat(SaleOrderDetailDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleServiceAdvanceDTO (){
		assertThat(SaleServiceAdvanceDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleServiceModelAdvanceDTO (){
		assertThat(SaleServiceModelAdvanceDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleTransDelBoo1DTO (){
		assertThat(SaleTransDelBoo1DTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleTransDetailDTO (){
		assertThat(SaleTransDetailDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleTransDetailVehicleOwnerAppDTO (){
		assertThat(SaleTransDetailVehicleOwnerAppDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleTransDTO (){
		assertThat(SaleTransDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleTransVehicleOwnerAppDTO (){
		assertThat(SaleTransVehicleOwnerAppDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SearchBriefcasesDTO (){
		assertThat(SearchBriefcasesDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SearchContractByCustomerDTO (){
		assertThat(SearchContractByCustomerDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SearchContractDTO (){
		assertThat(SearchContractDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SearchInfoDTO (){
		assertThat(SearchInfoDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(SearchInfoDTO.PlateNumber.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(SearchInfoDTO.Contract.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServiceFeeDTO (){
		assertThat(ServiceFeeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServicePlanDTO (){
		assertThat(ServicePlanDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServicePlanFeeDTO (){
		assertThat(ServicePlanFeeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServicePlanTypeDTO (){
		assertThat(ServicePlanTypeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServicePlanVehicleDuplicateDTO (){
		assertThat(ServicePlanVehicleDuplicateDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServiceRegisterDTO (){
		assertThat(ServiceRegisterDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SplitContractDTO (){
		assertThat(SplitContractDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSettersExcluding("dataToEntityOnEditContract")));
	}

	@Test
	void StageBooDTO (){
		assertThat(StageBooDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StageBooDTO.Stage.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void StageDTO (){
		assertThat(StageDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StageDTO.StationOut.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StageDTO.StationIn.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StageDTO.Stage.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void StationBooDTO (){
		assertThat(StationBooDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StationBooDTO.Station.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void StationDTO (){
		assertThat(StationDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StationDTO.Station.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SupOfferDTOSuccesDTO (){
		assertThat(SupOfferDTOSuccesDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void TerminateContractDTO (){
		assertThat(TerminateContractDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void TopupCashResponseDTO (){
		assertThat(TopupCashResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void TopupDTO (){
		assertThat(TopupDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void TopupExportDTO (){
		assertThat(TopupExportDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void UpdateContractProfileDTO (){
		assertThat(UpdateContractProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void UpdateVehicleProfileDTO (){
		assertThat(UpdateVehicleProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void UpdateVehicleRequestOCSDTO (){
		assertThat(UpdateVehicleRequestOCSDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(UpdateVehicleRequestOCSDTO.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void UsersDTO (){
		assertThat(UsersDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleAddSuffOfferDTO (){
		assertThat(VehicleAddSuffOfferDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleByContractDTO (){
		assertThat(VehicleByContractDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleDTO (){
		assertThat(VehicleDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleGroupDTO (){
		assertThat(VehicleGroupDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleGroupDTO.VehicleGroup.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleGroupIdDTO (){
		assertThat(VehicleGroupIdDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleGroupIdResponseDTO (){
		assertThat(VehicleGroupIdResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleGroupIdResponseDTO.VehicleGroup.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleGroupResponse (){
		assertThat(VehicleGroupResponse.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleGroupResponse.VehicleGroup.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleInforRegistryDTO (){
		assertThat(VehicleInforRegistryDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleProfileDTO (){
		assertThat(VehicleProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleProfilePaginationDTO (){
		assertThat(VehicleProfilePaginationDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
//		assertThat(VehicleProfilePaginationDTO.ProfileDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleSearchDTO (){
		assertThat(VehicleSearchDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehiclesExceptionDTO (){
		assertThat(VehiclesExceptionDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehiclesExceptionDTO.station.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehiclesExceptionDTO.stage.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleSwapPlateNumberDTO (){
		assertThat(VehicleSwapPlateNumberDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleTransferDTO (){
		assertThat(VehicleTransferDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleTypeDTO (){
		assertThat(VehicleTypeDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleTypeDTO.VehicleType.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}