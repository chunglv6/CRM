

package com.viettel.etc.services.impl;

import com.viettel.etc.dto.TopupCashResponseDTO;
import com.viettel.etc.repositories.tables.entities.TopupEtcEntity;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.utils.exceptions.EtcException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TopupServiceImplTest {

    private com.viettel.etc.services.impl.TopupServiceImpl topupServiceImplUnderTest;
    String soapTopupResponse;
    String soapTopupResponseError;
    com.viettel.etc.dto.ocs.OCSResponse ocsResponse;

    @BeforeEach
    void setUp() throws Exception {
        topupServiceImplUnderTest = new TopupServiceImpl();
        topupServiceImplUnderTest.wsAuditRepositoryJPA = mock(com.viettel.etc.repositories.tables.WsAuditRepositoryJPA.class);
        topupServiceImplUnderTest.topupEtcServiceJPA = mock(com.viettel.etc.services.tables.TopupEtcServiceJPA.class);
        topupServiceImplUnderTest.ocsService = mock(com.viettel.etc.services.OCSService.class);
        topupServiceImplUnderTest.contractServiceJPA = mock(com.viettel.etc.services.tables.ContractServiceJPA.class);
        topupServiceImplUnderTest.parseThymeleafTemplate = mock(com.viettel.etc.utils.exports.PdfHtmlTemplateExporter.class);


        soapTopupResponse = "\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <ns2:doReceiveRevenueGeneralOnBCCSFullResponse xmlns:ns2=\"http://process.wsim.viettel.com/\">\n" +
                "            <return>\n" +
                "                <amsSaleTransId>2351173873</amsSaleTransId>\n" +
                "                <responseCode>0</responseCode>\n" +
                "                <saleTransCode>GD00002351173873</saleTransCode>\n" +
                "                <transactionId>2351173873</transactionId>\n" +
                "            </return>\n" +
                "        </ns2:doReceiveRevenueGeneralOnBCCSFullResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";

        soapTopupResponseError = "\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <ns2:doReceiveRevenueGeneralOnBCCSFullResponse xmlns:ns2=\"http://process.wsim.viettel.com/\">\n" +
                "            <return>\n" +
                "                <amsSaleTransId>2351173873</amsSaleTransId>\n" +
                "                <responseCode>12345</responseCode>\n" +
                "                <saleTransCode>GD00002351173873</saleTransCode>\n" +
                "                <transactionId>2351173873</transactionId>\n" +
                "            </return>\n" +
                "        </ns2:doReceiveRevenueGeneralOnBCCSFullResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";

        new MockUp<TopupServiceImpl>() {
            @mockit.Mock
            private String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step) {
                return soapTopupResponse;
            }
        };

        ocsResponse = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");

        when(topupServiceImplUnderTest.ocsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);
    }

    @Ignore
    void testExecuteTopupCashToEtc() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);

        // Configure WsAuditRepositoryJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.WsAuditEntity wsAuditEntity = new com.viettel.etc.repositories.tables.entities.WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new java.sql.Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(new com.viettel.etc.repositories.tables.entities.WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure OCSService.addBalance(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure TopupEtcServiceJPA.findAll(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity.setTopupEtcId(0L);
        topupEtcEntity.setTopupCode("topupCode");
        topupEtcEntity.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity.setContractId(0L);
        topupEtcEntity.setAmount(0L);
        topupEtcEntity.setBalanceBefore(0L);
        topupEtcEntity.setBalanceAfter(0L);
        topupEtcEntity.setTopupType(0L);
        topupEtcEntity.setShopId(0L);
        topupEtcEntity.setShopName("shopName");
        final java.util.List<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntities = java.util.Arrays.asList(topupEtcEntity);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findAll()).thenReturn(topupEtcEntities);

        // Configure TopupEtcServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        when(topupServiceImplUnderTest.topupEtcServiceJPA.save(any())).thenReturn(topupEtcEntity1);

        // Configure OCSService.charge(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse1 = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.charge(any(), any(), any(), any())).thenReturn(ocsResponse1);

        // Run the testV
        final java.lang.Long result = topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO);

        // Verify the results
        assertEquals(0L, result);
    }
    
    @Ignore
    void testExecuteTopupCashToEtc_SaveToupEtcThrowsException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);

        // Configure WsAuditRepositoryJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.WsAuditEntity wsAuditEntity = new com.viettel.etc.repositories.tables.entities.WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new java.sql.Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(new com.viettel.etc.repositories.tables.entities.WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure OCSService.addBalance(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse.resultCode("0");
        ocsResponse.setResultCode("0");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure TopupEtcServiceJPA.findAll(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity.setTopupEtcId(0L);
        topupEtcEntity.setTopupCode("topupCode");
        topupEtcEntity.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity.setContractId(0L);
        topupEtcEntity.setAmount(0L);
        topupEtcEntity.setBalanceBefore(0L);
        topupEtcEntity.setBalanceAfter(0L);
        topupEtcEntity.setTopupType(0L);
        topupEtcEntity.setShopId(0L);
        topupEtcEntity.setShopName("shopName");
        final java.util.List<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntities = java.util.Arrays.asList(topupEtcEntity);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findAll()).thenReturn(topupEtcEntities);

        // Configure TopupEtcServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        when(topupServiceImplUnderTest.topupEtcServiceJPA.save(new TopupEtcEntity())).thenReturn(topupEtcEntity1);

        // Configure OCSService.charge(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse1 = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.charge(any(), any(), any(), any())).thenReturn(ocsResponse1);

        // Run the testV
        // Verify the results
        assertThrows(EtcException.class, () ->
                        topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO),
                "Nạp tiền thất bại");
    }

    @Ignore
    void testExecuteTopupCashToEtc_WsAuditRepositoryJPAReturnsNull() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(new com.viettel.etc.repositories.tables.entities.WsAuditEntity())).thenReturn(null);

        when(topupServiceImplUnderTest.ocsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure TopupEtcServiceJPA.findAll(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity.setTopupEtcId(0L);
        topupEtcEntity.setTopupCode("topupCode");
        topupEtcEntity.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity.setContractId(0L);
        topupEtcEntity.setAmount(0L);
        topupEtcEntity.setBalanceBefore(0L);
        topupEtcEntity.setBalanceAfter(0L);
        topupEtcEntity.setTopupType(0L);
        topupEtcEntity.setShopId(0L);
        topupEtcEntity.setShopName("shopName");
        final java.util.List<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntities = java.util.Arrays.asList(topupEtcEntity);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findAll()).thenReturn(topupEtcEntities);

        // Configure TopupEtcServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        when(topupServiceImplUnderTest.topupEtcServiceJPA.save(any())).thenReturn(topupEtcEntity1);

        when(topupServiceImplUnderTest.ocsService.charge(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Run the test
        final java.lang.Long result = topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO);

        // Verify the results
        assertEquals(0L, result);
    }

    @Ignore
    void testExecuteTopupCashToEtc_OCSServiceChargeThrowsException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);

        // Configure WsAuditRepositoryJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.WsAuditEntity wsAuditEntity = new com.viettel.etc.repositories.tables.entities.WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new java.sql.Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(any())).thenReturn(wsAuditEntity);

        // Configure TopupEtcServiceJPA.findAll(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity.setTopupEtcId(0L);
        topupEtcEntity.setTopupCode("topupCode");
        topupEtcEntity.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity.setContractId(0L);
        topupEtcEntity.setAmount(0L);
        topupEtcEntity.setBalanceBefore(0L);
        topupEtcEntity.setBalanceAfter(0L);
        topupEtcEntity.setTopupType(0L);
        topupEtcEntity.setShopId(0L);
        topupEtcEntity.setShopName("shopName");
        final java.util.List<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntities = java.util.Arrays.asList(topupEtcEntity);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findAll()).thenReturn(topupEtcEntities);

        // Configure TopupEtcServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        when(topupServiceImplUnderTest.topupEtcServiceJPA.save(any())).thenReturn(topupEtcEntity1);

        when(topupServiceImplUnderTest.ocsService.charge(new com.viettel.etc.dto.AddSupOfferRequestDTO(), null, 0L, "partyCode")).thenThrow(java.lang.Exception.class);

        // Run the test
        final java.lang.Long result = topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO);

        // Verify the results
        assertEquals(0L, result);
    }

    @Ignore
    void testExportTopupCashBill() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupExportDTO exportDTO = new com.viettel.etc.dto.TopupExportDTO(0L, "address", "booAddress");

        // Configure TopupEtcServiceJPA.findById(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntity = java.util.Optional.of(topupEtcEntity1);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findById(0L)).thenReturn(topupEtcEntity);

        // Configure ContractServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.ContractEntity(0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L), "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(topupServiceImplUnderTest.contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        when(topupServiceImplUnderTest.parseThymeleafTemplate.parseThymeleafTemplate("templatePath", new java.util.HashMap<>())).thenReturn("result");
        when(topupServiceImplUnderTest.parseThymeleafTemplate.generatePdfFromHtml("html", "outputPath")).thenReturn("result");

        // Run the test
        final java.lang.Object result = topupServiceImplUnderTest.exportTopupCashBill(authentication, exportDTO);

        // Verify the results
    }

    @Ignore
    void testExportTopupCashBill_TopupEtcServiceJPAReturnsAbsent() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupExportDTO exportDTO = new com.viettel.etc.dto.TopupExportDTO(0L, "address", "booAddress");

        // Configure TopupEtcServiceJPA.findById(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntity = java.util.Optional.of(topupEtcEntity1);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findById(any())).thenReturn(topupEtcEntity);

        // Configure ContractServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.ContractEntity(0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L), "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(topupServiceImplUnderTest.contractServiceJPA.findById(any())).thenReturn(contractEntity);

        when(topupServiceImplUnderTest.parseThymeleafTemplate.parseThymeleafTemplate(any(), any())).thenReturn("result");
        when(topupServiceImplUnderTest.parseThymeleafTemplate.generatePdfFromHtml(any(), any())).thenReturn("result");

        // Run the test
        final java.lang.Object result = topupServiceImplUnderTest.exportTopupCashBill(authentication, exportDTO);

        // Verify the results
    }

    @Ignore
    void testExportTopupCashBill_ContractServiceJPAReturnsAbsent() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupExportDTO exportDTO = new com.viettel.etc.dto.TopupExportDTO(0L, "address", "booAddress");

        // Configure TopupEtcServiceJPA.findById(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntity = java.util.Optional.of(topupEtcEntity1);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findById(any())).thenReturn(topupEtcEntity);

        when(topupServiceImplUnderTest.contractServiceJPA.findById(any())).thenReturn(java.util.Optional.empty());
        when(topupServiceImplUnderTest.parseThymeleafTemplate.parseThymeleafTemplate("templatePath", new java.util.HashMap<>())).thenReturn("result");
        when(topupServiceImplUnderTest.parseThymeleafTemplate.generatePdfFromHtml("html", "outputPath")).thenReturn("result");

        // Run the test
        final java.lang.Object result = topupServiceImplUnderTest.exportTopupCashBill(authentication, exportDTO);

        // Verify the results
    }

    @Ignore
    void testExportTopupCashBill_PdfHtmlTemplateExporterGeneratePdfFromHtmlThrowsDocumentException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupExportDTO exportDTO = new com.viettel.etc.dto.TopupExportDTO(0L, "address", "booAddress");

        // Configure TopupEtcServiceJPA.findById(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntity = java.util.Optional.of(topupEtcEntity1);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findById(0L)).thenReturn(topupEtcEntity);

        // Configure ContractServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.ContractEntity(0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L), "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(topupServiceImplUnderTest.contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        when(topupServiceImplUnderTest.parseThymeleafTemplate.parseThymeleafTemplate("templatePath", new java.util.HashMap<>())).thenReturn("result");
        when(topupServiceImplUnderTest.parseThymeleafTemplate.generatePdfFromHtml("html", "outputPath")).thenThrow(com.lowagie.text.DocumentException.class);

        // Run the test
        final java.lang.Object result = topupServiceImplUnderTest.exportTopupCashBill(authentication, exportDTO);

        // Verify the results
    }

    @Ignore
    void testExportTopupCashBill_PdfHtmlTemplateExporterGeneratePdfFromHtmlThrowsSAXException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupExportDTO exportDTO = new com.viettel.etc.dto.TopupExportDTO(0L, "address", "booAddress");

        // Configure TopupEtcServiceJPA.findById(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntity = java.util.Optional.of(topupEtcEntity1);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findById(0L)).thenReturn(topupEtcEntity);

        // Configure ContractServiceJPA.findById(...).
        final java.util.Optional<com.viettel.etc.repositories.tables.entities.ContractEntity> contractEntity = java.util.Optional.of(new com.viettel.etc.repositories.tables.entities.ContractEntity(0L, 0L, "contractNo", new java.sql.Date(0L), new java.sql.Date(0L), new java.sql.Date(0L), "description", "status", "emailNotification", "smsNotification", "pushNotification", "billCycle", "payCharge", 0L, "accountUser", "noticeName", "noticeAreaName", "noticeStreet", "noticeAreaCode", "noticeEmail", "noticePhoneNumber", "profileStatus", "approvedUser", new java.sql.Date(0L), "addfilesUser", new java.sql.Date(0L), "signName", "smsRenew", new java.sql.Date(0L), "createUser", new java.sql.Date(0L), 0L, "shopName", "accountUserId", "note", 0L, "accountAlias", "orderNumber"));
        when(topupServiceImplUnderTest.contractServiceJPA.findById(0L)).thenReturn(contractEntity);

        when(topupServiceImplUnderTest.parseThymeleafTemplate.parseThymeleafTemplate("templatePath", new java.util.HashMap<>())).thenReturn("result");
        when(topupServiceImplUnderTest.parseThymeleafTemplate.generatePdfFromHtml("html", "outputPath")).thenThrow(org.xml.sax.SAXException.class);

        // Run the test
        final java.lang.Object result = topupServiceImplUnderTest.exportTopupCashBill(authentication, exportDTO);

        // Verify the results
    }

    @Test
    void testExecuteTopupCashToEtcThrowsException() {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);

        new MockUp<TopupServiceImpl>() {
            @mockit.Mock
            private String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step) {
                return "";
            }
        };

        new MockUp<TopupServiceImpl>() {
            @mockit.Mock
            private TopupCashResponseDTO readSoapResponse(String response) throws IOException, XMLStreamException, JAXBException {
                return null;
            }
        };

        // Verify the results
        assertThrows(EtcException.class, () ->
                        topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO)
                , "Có lỗi khi giao tiếp với BCCS");
    }

    @Test
    void testExecuteTopupCashToEtc_TopupCashResponseDTOThrowsException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);

        // Configure WsAuditRepositoryJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.WsAuditEntity wsAuditEntity = new com.viettel.etc.repositories.tables.entities.WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new java.sql.Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(new com.viettel.etc.repositories.tables.entities.WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure OCSService.addBalance(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse.resultCode("1");
        ocsResponse.setResultCode("1");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure TopupEtcServiceJPA.findAll(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity.setTopupEtcId(0L);
        topupEtcEntity.setTopupCode("topupCode");
        topupEtcEntity.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity.setContractId(0L);
        topupEtcEntity.setAmount(0L);
        topupEtcEntity.setBalanceBefore(0L);
        topupEtcEntity.setBalanceAfter(0L);
        topupEtcEntity.setTopupType(0L);
        topupEtcEntity.setShopId(0L);
        topupEtcEntity.setShopName("shopName");
        final java.util.List<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntities = java.util.Arrays.asList(topupEtcEntity);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findAll()).thenReturn(topupEtcEntities);

        // Configure TopupEtcServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        when(topupServiceImplUnderTest.topupEtcServiceJPA.save(any())).thenReturn(topupEtcEntity1);

        // Configure OCSService.charge(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse1 = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.charge(any(), any(), any(), any())).thenReturn(ocsResponse1);

        new MockUp<TopupServiceImpl>() {
            @mockit.Mock
            private String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step) {
                return soapTopupResponseError;
            }
        };

        // Verify the results
        assertThrows(EtcException.class, () ->
                        topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO)
                , "Lỗi khi giao tiếp với OCS");
    }

    @Test
    void testExecuteTopupCashToEtc_TopupCashResponseDTO_OCS_ThrowsException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupDTO topupDTO = new com.viettel.etc.dto.TopupDTO("address", "customerName", "stockModelCode", 0L, 0L, 0L, "transType", "saleTransType", 0L, 0L);

        // Configure WsAuditRepositoryJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.WsAuditEntity wsAuditEntity = new com.viettel.etc.repositories.tables.entities.WsAuditEntity();
        wsAuditEntity.setWsAuditId(0L);
        wsAuditEntity.setWsCallType("wsCallType");
        wsAuditEntity.setActTypeId(0L);
        wsAuditEntity.setRequestTime(new java.sql.Date(0L));
        wsAuditEntity.setActionUserName("actionUserName");
        wsAuditEntity.setWsUri("wsUri");
        wsAuditEntity.setSourceAppId("sourceAppId");
        wsAuditEntity.setIpPc("ipPc");
        wsAuditEntity.setDestinationAppId("destinationAppId");
        wsAuditEntity.setStatus("status");
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(new com.viettel.etc.repositories.tables.entities.WsAuditEntity())).thenReturn(wsAuditEntity);

        // Configure OCSService.addBalance(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse.resultCode("1");
        ocsResponse.setResultCode("1");
        ocsResponse.description("description");
        ocsResponse.setDescription("description");
        ocsResponse.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.addBalance(any(), any(), any(), any())).thenReturn(ocsResponse);

        // Configure TopupEtcServiceJPA.findAll(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity.setTopupEtcId(0L);
        topupEtcEntity.setTopupCode("topupCode");
        topupEtcEntity.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity.setContractId(0L);
        topupEtcEntity.setAmount(0L);
        topupEtcEntity.setBalanceBefore(0L);
        topupEtcEntity.setBalanceAfter(0L);
        topupEtcEntity.setTopupType(0L);
        topupEtcEntity.setShopId(0L);
        topupEtcEntity.setShopName("shopName");
        final java.util.List<com.viettel.etc.repositories.tables.entities.TopupEtcEntity> topupEtcEntities = java.util.Arrays.asList(topupEtcEntity);
        when(topupServiceImplUnderTest.topupEtcServiceJPA.findAll()).thenReturn(topupEtcEntities);

        // Configure TopupEtcServiceJPA.save(...).
        final com.viettel.etc.repositories.tables.entities.TopupEtcEntity topupEtcEntity1 = new com.viettel.etc.repositories.tables.entities.TopupEtcEntity();
        topupEtcEntity1.setTopupEtcId(0L);
        topupEtcEntity1.setTopupCode("topupCode");
        topupEtcEntity1.setTopupDate(new java.sql.Date(0L));
        topupEtcEntity1.setContractId(0L);
        topupEtcEntity1.setAmount(0L);
        topupEtcEntity1.setBalanceBefore(0L);
        topupEtcEntity1.setBalanceAfter(0L);
        topupEtcEntity1.setTopupType(0L);
        topupEtcEntity1.setShopId(0L);
        topupEtcEntity1.setShopName("shopName");
        when(topupServiceImplUnderTest.topupEtcServiceJPA.save(any())).thenReturn(topupEtcEntity1);

        // Configure OCSService.charge(...).
        final com.viettel.etc.dto.ocs.OCSResponse ocsResponse1 = new com.viettel.etc.dto.ocs.OCSResponse();
        ocsResponse1.resultCode("resultCode");
        ocsResponse1.setResultCode("resultCode");
        ocsResponse1.description("description");
        ocsResponse1.setDescription("description");
        ocsResponse1.setSubscriptionTicketId("subscriptionTicketId");
        when(topupServiceImplUnderTest.ocsService.charge(any(), any(), any(), any())).thenReturn(ocsResponse1);

        new MockUp<TopupServiceImpl>() {
            @mockit.Mock
            private String requestSOAP(String wsURL, long actTypeId, Authentication authentication, String xmlInput, int step) {
                return soapTopupResponse;
            }
        };

        // Verify the results
        assertThrows(EtcException.class, () ->
                        topupServiceImplUnderTest.executeTopupCashToEtc(authentication, topupDTO)
                , "Lỗi khi giao tiếp với OCS");
    }

    @Test
    void testExportTopupCashBill_TopupEtcEntityThrowsException() throws Exception {
        // Setup
        final org.springframework.security.core.Authentication authentication = null;
        final com.viettel.etc.dto.TopupExportDTO exportDTO = new com.viettel.etc.dto.TopupExportDTO(0L, "address", "booAddress");

        when(topupServiceImplUnderTest.topupEtcServiceJPA.findById(any())).thenReturn(Optional.empty());

        // Verify the results
        assertThrows(EtcException.class, () ->
                        topupServiceImplUnderTest.exportTopupCashBill(authentication, exportDTO),
                "Không có dữ liệu");
    }
    @Test
    void testWriteLog() {
        // Setup
        final Authentication authentication = null;
        WsAuditEntity expectedResult = new WsAuditEntity();
        expectedResult.setWsCallType("wsCallType");
        expectedResult.setActTypeId(0L);
        expectedResult.setRequestTime(new Date(System.currentTimeMillis()));
        expectedResult.setActionUserName("system");
        expectedResult.setWsUri("url");
        expectedResult.setSourceAppId("crm");
        expectedResult.setIpPc("ip");
        expectedResult.setDestinationAppId("OCS");
        expectedResult.setStatus("status");
        expectedResult.setFinishTime(0L);

        expectedResult.setMsgReponse("resp".getBytes());
        expectedResult.setMsgRequest("req".getBytes());
        when(topupServiceImplUnderTest.wsAuditRepositoryJPA.save(any())).thenReturn(expectedResult);
        new MockUp<TopupServiceImpl>() {
            @mockit.Mock
            public WsAuditEntity writeLog(String req, String resp, String url, String ip, long timeCallOCS, long actTypeId,
                                          Authentication authentication, String wsCallType, String status){
                return expectedResult;
            }
        };
        // Run the test
        final WsAuditEntity result = topupServiceImplUnderTest.writeLog("req", "resp", "url", "ip", 0L, 0L, authentication, "wsCallType", "status");
        // Verify the results

        assertEquals(expectedResult,result);
    }
}

