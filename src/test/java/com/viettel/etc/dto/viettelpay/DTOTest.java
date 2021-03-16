package com.viettel.etc.dto.viettelpay;
import com.viettel.etc.dto.viettelpay.AutoRenewVtpDTO;
import com.viettel.etc.dto.viettelpay.DataDTO;
import com.viettel.etc.dto.viettelpay.RequestAddMoneyToAccountDTO;
import com.viettel.etc.dto.viettelpay.RequestAddSupOfferDTO;
import com.viettel.etc.dto.viettelpay.RequestBaseViettelDTO;
import com.viettel.etc.dto.viettelpay.RequestCancelConfirmViettelPayDTO;
import com.viettel.etc.dto.viettelpay.RequestCancelInitViettelPayDTO;
import com.viettel.etc.dto.viettelpay.RequestChargeTicketVTPDTO;
import com.viettel.etc.dto.viettelpay.RequestConfirmChangeMoneySourceDTO;
import com.viettel.etc.dto.viettelpay.RequestConfirmRegisterDTO;
import com.viettel.etc.dto.viettelpay.RequestContractPaymentDTO;
import com.viettel.etc.dto.viettelpay.RequestLinkConfirmViettelPayDTO;
import com.viettel.etc.dto.viettelpay.RequestLinkInitViettelPayDTO;
import com.viettel.etc.dto.viettelpay.RequestLinkViettelPayDTO;
import com.viettel.etc.dto.viettelpay.RequestRenewTicketPricesDTO;
import com.viettel.etc.dto.viettelpay.ResAddSupOfferDTO;
import com.viettel.etc.dto.viettelpay.ResponseChargeTicketDTO;
import com.viettel.etc.dto.viettelpay.ResponseGetInfoTicketPurchaseAndExtendedDTO;
import com.viettel.etc.dto.viettelpay.ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO;
import com.viettel.etc.dto.viettelpay.ResponseViettelPayDTO;
import com.viettel.etc.dto.viettelpay.StatusDTO;
import com.viettel.etc.dto.viettelpay.VehicleAddSupOfferViettelPayDTO;
import com.viettel.etc.dto.viettelpay.ViettelPayRequestDTO;
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
	void AutoRenewVtpDTO (){
		assertThat(AutoRenewVtpDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void DataDTO (){
		assertThat(DataDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(DataDTO.Plate.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestAddMoneyToAccountDTO (){
		assertThat(RequestAddMoneyToAccountDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestAddSupOfferDTO (){
		assertThat(RequestAddSupOfferDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestBaseViettelDTO (){
		assertThat(RequestBaseViettelDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestCancelConfirmViettelPayDTO (){
		assertThat(RequestCancelConfirmViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestCancelInitViettelPayDTO (){
		assertThat(RequestCancelInitViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestChargeTicketVTPDTO (){
		assertThat(RequestChargeTicketVTPDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(RequestChargeTicketVTPDTO.Ticket.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestConfirmChangeMoneySourceDTO (){
		assertThat(RequestConfirmChangeMoneySourceDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestConfirmRegisterDTO (){
		assertThat(RequestConfirmRegisterDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestContractPaymentDTO (){
		assertThat(RequestContractPaymentDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestLinkConfirmViettelPayDTO (){
		assertThat(RequestLinkConfirmViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestLinkInitViettelPayDTO (){
		assertThat(RequestLinkInitViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestLinkViettelPayDTO (){
		assertThat(RequestLinkViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void RequestRenewTicketPricesDTO (){
		assertThat(RequestRenewTicketPricesDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResAddSupOfferDTO (){
		assertThat(ResAddSupOfferDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResponseChargeTicketDTO (){
		assertThat(ResponseChargeTicketDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseChargeTicketDTO.ResponseData.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseChargeTicketDTO.ResponseStatus.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseChargeTicketDTO.SetResponseChargeTicket.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResponseGetInfoTicketPurchaseAndExtendedDTO (){
		assertThat(ResponseGetInfoTicketPurchaseAndExtendedDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseGetInfoTicketPurchaseAndExtendedDTO.TicketOrder.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO (){
		assertThat(ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseGetInfoTicketPurchaseAndExtendedPrivateStreamDTO.TicketOrder.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResponseViettelPayDTO (){
		assertThat(ResponseViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtenedPrivateStream.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseVerifyViettelPayData.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.VerifyViettelPayData.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.VerifyViettelPayData.ErrorCode.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(ResponseViettelPayDTO.ResponseAddMoneyResult.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseAddMoneyResultData.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseAddMoneyResultData.ErrorCode.values().length, Matchers.greaterThanOrEqualTo(1));
		assertThat(ResponseViettelPayDTO.ResponseGetInfoTicketPurchaseAndExtened.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseCancelInit.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseGetSourcesViettelPay.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.DataMoneySources.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.MoneySources.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseCancelInitDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.DataCancelInitDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseLinkConfirmDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.DataLinkConfirmDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseLinkInitDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.DataUnRegisterDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.DataRegisterDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseUnRegisterConfirm.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseRegisterConfirm.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResponseViettelPayDTO.ResponseContract.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void StatusDTO (){
		assertThat(StatusDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(StatusDTO.StatusCode.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void VehicleAddSupOfferViettelPayDTO (){
		assertThat(VehicleAddSupOfferViettelPayDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ViettelPayRequestDTO (){
		assertThat(ViettelPayRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}