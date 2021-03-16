package com.viettel.etc.repositories.tables.entities;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.hamcrest.Matchers;
import static org.hamcrest.CoreMatchers.allOf;
import static com.google.code.beanmatchers.BeanMatchers.*;
import org.junit.jupiter.api.Test;

class EntityTest {

	@BeforeEach
	void setUp() {
	}

	@Test
	void ActIdTypeMappingEntity (){
		assertThat(ActIdTypeMappingEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditDetailEntity (){
		assertThat(ActionAuditDetailEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ActionAuditEntity (){
		assertThat(ActionAuditEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ActionAuditEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void ActReasonEntity (){
		assertThat(ActReasonEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ActReasonEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void ActTypeEntity (){
		assertThat(ActTypeEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void AttachmentFileEntity (){
		assertThat(AttachmentFileEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(AttachmentFileEntity.STATUS.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(AttachmentFileEntity.ATTACH_TYPE.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void BotRevenueShareEntity (){
		assertThat(BotRevenueShareEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CategoryMappingBooEntity (){
		assertThat(CategoryMappingBooEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ContractEntity (){
		assertThat(ContractEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ContractEntity.ProfilesStatus.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(ContractEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void ContractPaymentEntity (){
		assertThat(ContractPaymentEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ContractPaymentEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void ContractProfileEntity (){
		assertThat(ContractProfileEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ContractProfileEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void CustIdTypeMappingEntity (){
		assertThat(CustIdTypeMappingEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void CustomerEntity (){
		assertThat(CustomerEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(CustomerEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void CustRegisEntity (){
		assertThat(CustRegisEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(CustRegisEntity.RegStatus.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void DocumentTypeEntity (){
		assertThat(DocumentTypeEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ExceptionListEntity (){
		assertThat(ExceptionListEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MessageErrorEntity (){
		assertThat(MessageErrorEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void OtpEntity (){
		assertThat(OtpEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void PromotionAssignEntity (){
		assertThat(PromotionAssignEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void PromotionEntity (){
		assertThat(PromotionEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleOrderDetailEntity (){
		assertThat(SaleOrderDetailEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SaleOrderEntity (){
		assertThat(SaleOrderEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(SaleOrderEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(SaleOrderEntity.SaleOrderSource.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(SaleOrderEntity.SaleOrderType.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void SaleTransDelBoo1Entity (){
		assertThat(SaleTransDelBoo1Entity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(SaleTransDelBoo1Entity.RequestType.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(SaleTransDelBoo1Entity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void SaleTransDetailEntity (){
		assertThat(SaleTransDetailEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(SaleTransDetailEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void SaleTransEntity (){
		assertThat(SaleTransEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(SaleTransEntity.PaymentMethodId.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(SaleTransEntity.SaleTransType.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(SaleTransEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void ServiceFeeEntity (){
		assertThat(ServiceFeeEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServicePlanEntity (){
		assertThat(ServicePlanEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ServicePlanEntity.Scope.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(ServicePlanEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void ServicePlanHisEntity (){
		assertThat(ServicePlanHisEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ServicePlanTypeEntity (){
		assertThat(ServicePlanTypeEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void TopupEtcEntity (){
		assertThat(TopupEtcEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(TopupEtcEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(TopupEtcEntity.TopupType.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void VehicleEntity (){
		assertThat(VehicleEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleEntity.ProfilesStatus.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(VehicleEntity.ActiveStatus.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(VehicleEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void VehiclePriorityEntity (){
		assertThat(VehiclePriorityEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleProfileEntity (){
		assertThat(VehicleProfileEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleProfileEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void WsAuditEntity (){
		assertThat(WsAuditEntity.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(WsAuditEntity.Status.values().length, Matchers.greaterThanOrEqualTo(1));
	}

}
