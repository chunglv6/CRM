package com.viettel.etc.services.impl;

import com.viettel.etc.dto.momo.MoMoNotifyRequestDTO;
import com.viettel.etc.dto.momo.MoMoNotifyResponseDTO;
import com.viettel.etc.dto.momo.MoMoPayAppResponseDTO;
import com.viettel.etc.dto.momo.MoMoRawDataDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.viettelpay.RequestAddSupOfferDTO;
import com.viettel.etc.dto.viettelpay.VehicleAddSupOfferViettelPayDTO;
import com.viettel.etc.repositories.tables.SaleOrderRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.SaleOrderEntity;
import com.viettel.etc.services.OCSService;
import com.viettel.etc.services.tables.SaleOrderServiceJPA;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.encoding.MomoEncoder;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MoMoServiceImplTest {

    private MoMoServiceImpl moMoServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        moMoServiceImplUnderTest = new MoMoServiceImpl();
        moMoServiceImplUnderTest.saleOrderRepositoryJPA = mock(SaleOrderRepositoryJPA.class);
        moMoServiceImplUnderTest.saleOrderServiceJPA = mock(SaleOrderServiceJPA.class);
        moMoServiceImplUnderTest.ocsService = mock(OCSService.class);
    }

    @Ignore
    void testCreateAddMoneyOrder() {
        // Setup
        final RequestAddSupOfferDTO addSupOfferRequest = new RequestAddSupOfferDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        final VehicleAddSupOfferViettelPayDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSupOfferViettelPayDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;

        // Configure SaleOrderRepositoryJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.save(Mockito.any())).thenReturn(saleOrderEntity);

        // Run the test
        final Object result = moMoServiceImplUnderTest.mobileAppChuPTCreateSaleOrder(addSupOfferRequest, authentication, 0L, 0L);

        // Verify the results
        assertNotNull(result);
    }

    /*@Test
    void testCreateAddMoneyOrder_SaleOrderRepositoryJPAReturnsNull() {
        // Setup
        final RequestAddSupOfferDTO addSupOfferRequest = new RequestAddSupOfferDTO();
        addSupOfferRequest.setContractNo("contractNo");
        addSupOfferRequest.setAmount(0L);
        addSupOfferRequest.setQuantity(0L);
        final VehicleAddSupOfferViettelPayDTO vehicleAddSupOfferViettelPayDTO = new VehicleAddSupOfferViettelPayDTO();
        vehicleAddSupOfferViettelPayDTO.setPlateNumber("plateNumber");
        vehicleAddSupOfferViettelPayDTO.setVehicleId(0L);
        vehicleAddSupOfferViettelPayDTO.setVehiclesGroupId(0L);
        vehicleAddSupOfferViettelPayDTO.setQuantity(0L);
        vehicleAddSupOfferViettelPayDTO.setPrice(0L);
        vehicleAddSupOfferViettelPayDTO.setStationId(0L);
        vehicleAddSupOfferViettelPayDTO.setStageId(0L);
        vehicleAddSupOfferViettelPayDTO.setEffDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setExpDate(new Date(0L));
        vehicleAddSupOfferViettelPayDTO.setLaneOut(0L);
        addSupOfferRequest.setList(Arrays.asList(vehicleAddSupOfferViettelPayDTO));

        final Authentication authentication = null;
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.save(new SaleOrderEntity())).thenReturn(null);

        // Run the test
        final Object result = moMoServiceImplUnderTest.mobileAppChuPTCreateSaleOrder(addSupOfferRequest, authentication, 0L, 0L);

        // Verify the results
        assertNotNull(result);
    }*/

    @Ignore
    void testVerifyMoMoRequestData() throws Exception {
        // Setup
        MoMoNotifyRequestDTO data = new MoMoNotifyRequestDTO("accessKey", "0", "transType",
                0, "message", 0L, "storeId", "signature");
        data.setPartnerRefId("0");
        data.setMomoTransId("momoTransId");
        final Authentication authentication = null;
        final MoMoNotifyResponseDTO expectedResult = new MoMoNotifyResponseDTO(0, "Thanh cong", "0", "momoTransId", 0L, "signature");

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(0L, SaleOrderEntity.Status.NEW_ORDER.value)).thenReturn(saleOrderEntity);
        new MockUp<MomoEncoder>() {
            @mockit.Mock
            String signHmacSHA256(String raw, String secretKey) {
                return "signature";
            }
        };
        // Run the test
        final MoMoNotifyResponseDTO result = moMoServiceImplUnderTest.verifyMoMoRequestData(data, authentication);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testVerifyMoMoRequestData_ThrowsException() {
        // Setup
        final MoMoNotifyRequestDTO data = new MoMoNotifyRequestDTO("accessKey", "partnerTransId", "transType", 0, "message", 0L, "storeId", "signature");
        final Authentication authentication = null;

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(0L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(0L, "status")).thenReturn(saleOrderEntity);

        // Run the test
        assertThrows(Exception.class, () -> moMoServiceImplUnderTest.verifyMoMoRequestData(data, authentication));
    }

    @Ignore
    void testVerifyMoMoRequestData_SaleOrderRepositoryJPAReturnsAbsent() throws Exception {
        // Setup
        MoMoNotifyRequestDTO data = new MoMoNotifyRequestDTO("accessKey", "partnerTransId", "transType", 0, "message", 0L, "storeId", "signature");
        data.setPartnerRefId("0");
        data.setAmount(0L);
        data.setMomoTransId("momoTransId");
        final Authentication authentication = null;
        final MoMoNotifyResponseDTO expectedResult = new MoMoNotifyResponseDTO(0, "Thanh cong", "0", "momoTransId", 0L, "signature");
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(0L, "status")).thenReturn(Optional.empty());

        new MockUp<MomoEncoder>() {
            @mockit.Mock
            String signHmacSHA256(String raw, String secretKey) {
                return "signature";
            }
        };
        // Run the test
        final MoMoNotifyResponseDTO result = moMoServiceImplUnderTest.verifyMoMoRequestData(data, authentication);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Ignore
    void testRequestMoMoAppPayment() throws Exception {
        // Setup
        MoMoRawDataDTO data = new MoMoRawDataDTO("customerNumber", "appData");
        data.setAmount(0L);
        data.setMomoTransId("0");
        data.setPartnerRefId("0");
        data.setPartnerCode("PartnerCode");
        final Authentication authentication = null;

        // Configure SaleOrderRepositoryJPA.findBySaleOrderIdAndStatus(...).
        final SaleOrderEntity saleOrderEntity1 = new SaleOrderEntity();
        saleOrderEntity1.setSaleOrderId(1L);
        saleOrderEntity1.setSaleOrderDate(new Date(0L));
        saleOrderEntity1.setSaleOrderType("saleOrderType");
        saleOrderEntity1.setSaleOrderSource("saleOrderSource");
        saleOrderEntity1.setStatus("status");
        saleOrderEntity1.setMethodRechargeId(0L);
        saleOrderEntity1.setPaymentMethodId(0L);
        saleOrderEntity1.setAmount(0L);
        saleOrderEntity1.setQuantity(0L);
        saleOrderEntity1.setDiscount(0L);
        final Optional<SaleOrderEntity> saleOrderEntity = Optional.of(saleOrderEntity1);
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(0L, SaleOrderEntity.Status.NEW_ORDER.value)).thenReturn(saleOrderEntity);

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(moMoServiceImplUnderTest.ocsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity2 = new SaleOrderEntity();
        saleOrderEntity2.setSaleOrderId(0L);
        saleOrderEntity2.setSaleOrderDate(new Date(0L));
        saleOrderEntity2.setSaleOrderType("saleOrderType");
        saleOrderEntity2.setSaleOrderSource("saleOrderSource");
        saleOrderEntity2.setStatus("status");
        saleOrderEntity2.setMethodRechargeId(0L);
        saleOrderEntity2.setPaymentMethodId(0L);
        saleOrderEntity2.setAmount(0L);
        saleOrderEntity2.setQuantity(0L);
        saleOrderEntity2.setDiscount(0L);
        when(moMoServiceImplUnderTest.saleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity2);

        ReflectionTestUtils.setField(moMoServiceImplUnderTest, "version", "2.0");
        ReflectionTestUtils.setField(moMoServiceImplUnderTest, "payType", "2");
        new MockUp<MomoEncoder>() {
            @mockit.Mock
            String generateRSA(Map<String, Object> raw, String publicKey) {
                return "hash";
            }
        };
        new MockUp<FnCommon>() {
            @mockit.Mock
            String doPostRequest(String url, Object obj, String timeOut) {
                String raw = "{'status':0, 'message':'Thành công', 'amount':100000, 'transid':2336945117, 'signature':'61a922132e6ba9ad2974e1f73b0007471028e7047e3ee9c651aed0e9cf665c12'}";
                return raw;
            }
        };
        new MockUp<MomoEncoder>() {
            @mockit.Mock
            String signHmacSHA256(String raw, String secretKey) {
                return "signature";
            }
        };
        // Run the test
        final Object result = moMoServiceImplUnderTest.requestMoMoAppPayment(data, authentication);

        // Verify the results
    }

    @Ignore
    void testRequestMoMoAppPayment_SaleOrderRepositoryJPAReturnsAbsent() throws Exception {
        // Setup
        final MoMoRawDataDTO data = new MoMoRawDataDTO("customerNumber", "appData");
        data.setAmount(0L);
        data.setMomoTransId("0");
        data.setPartnerRefId("0");
        data.setPartnerCode("PartnerCode");
        final Authentication authentication = null;
        when(moMoServiceImplUnderTest.saleOrderRepositoryJPA.findBySaleOrderIdAndStatus(0L, "status")).thenReturn(Optional.empty());

        // Configure OCSService.addBalance(...).
        final OCSResponse ocsResponse = new OCSResponse();
        ocsResponse.resultCode("resultCode");
        ocsResponse.setResultCode("resultCode");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(moMoServiceImplUnderTest.ocsService.addBalance(null, 0L, 0L, 0L)).thenReturn(ocsResponse);

        // Configure SaleOrderServiceJPA.save(...).
        final SaleOrderEntity saleOrderEntity = new SaleOrderEntity();
        saleOrderEntity.setSaleOrderId(0L);
        saleOrderEntity.setSaleOrderDate(new Date(0L));
        saleOrderEntity.setSaleOrderType("saleOrderType");
        saleOrderEntity.setSaleOrderSource("saleOrderSource");
        saleOrderEntity.setStatus("status");
        saleOrderEntity.setMethodRechargeId(0L);
        saleOrderEntity.setPaymentMethodId(0L);
        saleOrderEntity.setAmount(0L);
        saleOrderEntity.setQuantity(0L);
        saleOrderEntity.setDiscount(0L);
        when(moMoServiceImplUnderTest.saleOrderServiceJPA.save(new SaleOrderEntity())).thenReturn(saleOrderEntity);

        // Run the test
        final Object result = moMoServiceImplUnderTest.requestMoMoAppPayment(data, authentication);

        // Verify the results
    }

    @Ignore
    void testResponseNotifyFromMoMo() throws Exception {
        // Setup
        final MoMoNotifyRequestDTO data = new MoMoNotifyRequestDTO("accessKey", "partnerTransId", "transType", 0, "message", 0L, "storeId", "signature");
        data.setAmount(0L);
        data.setMomoTransId("0");
        data.setPartnerRefId("0");
        data.setPartnerCode("PartnerCode");
        final Authentication authentication = null;
        final MoMoNotifyResponseDTO expectedResult = new MoMoNotifyResponseDTO(0, "Thanh cong", "0", "0", 0L, "signature");

        new MockUp<MomoEncoder>() {
            @mockit.Mock
            String signHmacSHA256(String raw, String secretKey) {
                return "signature";
            }
        };
        // Run the test
        final MoMoNotifyResponseDTO result = moMoServiceImplUnderTest.responseNotifyFromMoMo(data, authentication);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
