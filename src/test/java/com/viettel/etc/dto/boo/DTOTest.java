package com.viettel.etc.dto.boo;
import com.viettel.etc.dto.boo.DataBooDTO;
import com.viettel.etc.dto.boo.ReqActivationCheckDTO;
import com.viettel.etc.dto.boo.ReqCalculatorTicketDTO;
import com.viettel.etc.dto.boo.ReqCancelResultDTO;
import com.viettel.etc.dto.boo.ReqCancelTicketDTO;
import com.viettel.etc.dto.boo.ReqChargeTicketDTO;
import com.viettel.etc.dto.boo.ReqMappingDTO;
import com.viettel.etc.dto.boo.ReqOnlineEventRegDTO;
import com.viettel.etc.dto.boo.ReqOnlineEventSyncDTO;
import com.viettel.etc.dto.boo.ReqQueryTicketDTO;
import com.viettel.etc.dto.boo.ResActivationCheckDTO;
import com.viettel.etc.dto.boo.ResCalculatorTicketDTO;
import com.viettel.etc.dto.boo.ResCancelResultDTO;
import com.viettel.etc.dto.boo.ResCancelTicketDTO;
import com.viettel.etc.dto.boo.ResChargeTicketDTO;
import com.viettel.etc.dto.boo.ResExceptionBooDTO;
import com.viettel.etc.dto.boo.ResOnlineEventDTO;
import com.viettel.etc.dto.boo.ResQueryTicketDTO;
import com.viettel.etc.dto.boo.ResTokenBoo1DTO;
import com.viettel.etc.dto.boo.VehicleTypeBooDTO;
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
	void DataBooDTO (){
		assertThat(DataBooDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqActivationCheckDTO (){
		assertThat(ReqActivationCheckDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqCalculatorTicketDTO (){
		assertThat(ReqCalculatorTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqCancelResultDTO (){
		assertThat(ReqCancelResultDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqCancelTicketDTO (){
		assertThat(ReqCancelTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqChargeTicketDTO (){
		assertThat(ReqChargeTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqMappingDTO (){
		assertThat(ReqMappingDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqOnlineEventRegDTO (){
		assertThat(ReqOnlineEventRegDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqOnlineEventSyncDTO (){
		assertThat(ReqOnlineEventSyncDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqQueryTicketDTO (){
		assertThat(ReqQueryTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResActivationCheckDTO (){
		assertThat(ResActivationCheckDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResCalculatorTicketDTO (){
		assertThat(ResCalculatorTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResCancelResultDTO (){
		assertThat(ResCancelResultDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResCancelTicketDTO (){
		assertThat(ResCancelTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResChargeTicketDTO (){
		assertThat(ResChargeTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResExceptionBooDTO (){
		assertThat(ResExceptionBooDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResOnlineEventDTO (){
		assertThat(ResOnlineEventDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResQueryTicketDTO (){
		assertThat(ResQueryTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResQueryTicketDTO.ListTicket.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResTokenBoo1DTO (){
		assertThat(ResTokenBoo1DTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void VehicleTypeBooDTO (){
		assertThat(VehicleTypeBooDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(VehicleTypeBooDTO.VehicleType.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}